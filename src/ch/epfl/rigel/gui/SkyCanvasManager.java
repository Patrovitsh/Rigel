package ch.epfl.rigel.gui;

import static ch.epfl.rigel.gui.Main.CANVAS_HEIGHT;
import static ch.epfl.rigel.gui.Main.CANVAS_WIDTH;
import static ch.epfl.rigel.gui.NamedTimeAccelerator.DAY;
import static ch.epfl.rigel.gui.NamedTimeAccelerator.SIDEREAL_DAY;
import static java.lang.Math.abs;
import static java.lang.Math.tan;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.*;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire de canvas.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public class SkyCanvasManager {

    public final static CartesianCoordinates DEFAULT_MOUSE_POSITION = CartesianCoordinates.of(0, 0);

    private final static RightOpenInterval INTERVAL_AZ_DEG = RightOpenInterval.of(0, 360);
    public final static ClosedInterval INTERVAL_ALT_DEG = ClosedInterval.of(5, 90);
    private final static ClosedInterval INTERVAL_FIELD_OF_VIEW = ClosedInterval.of(30, 150);
    private final static int DEFAULT_EXPANSION_FACTOR = 1300;
    private final static int LON_STEP_DISPLACEMENT = 10;
    private final static int LAT_STEP_DISPLACEMENT = 5;
    private final static int MAX_DISTANCE = 10;

    private final DoubleBinding mouseAzDeg;
    private final DoubleBinding mouseAltDeg;
    private final ObjectBinding<CelestialObject> objectUnderMouse;

    private final ObjectBinding<StereographicProjection> projection;
    private final ObjectBinding<Transform> planeToCanvas;
    private final ObjectBinding<ObservedSky> observedSky;
    private final ObjectProperty<CartesianCoordinates> mousePosition;
    private final ObjectBinding<Point2D> mousePointTransform;
    private final ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition;

    private final BooleanBinding timeAnimatorNotDayOrSideral;

    private final Canvas canvas;
    private final SkyCanvasPainter painter;
    private final ViewingParametersBean viewingParametersBean;
    private final DateTimeBean dateTimeBean;
    private final ObserverLocationBean observerLocationBean;
    private final TimeAnimator timeAnimator;

    private final DoubleProperty horFactor;
    private final DoubleProperty verFactor;
    private final DoubleProperty scrollFactor;

    private final BooleanProperty enDrawStars;
    private final BooleanProperty enDrawPlanets;
    private final BooleanProperty enDrawSun;
    private final BooleanProperty enDrawMoon;
    private final BooleanProperty enDrawHorizon;
    private final BooleanProperty enDrawAsterisms;
    private final BooleanProperty enDrawConstellations;
    private final BooleanProperty isTrackingCelestial;

    private String nameObjectTracked;
    private final List<HorizontalCoordinates> horCoords;

    /**
     * Constructeur d'un gestionnaire de canvas.
     *
     * @param catalogue catalogues des ??toiles et des ast??rismes.
     * @param dateTimeBean instant d'observation.
     * @param timeAnimator animateur de temps.
     * @param observerLocationBean position de l'observateur.
     * @param viewParaBean portion du ciel visible sur l'image.
     */
    public SkyCanvasManager(StarCatalogue catalogue, DateTimeBean dateTimeBean, TimeAnimator timeAnimator,
                            ObserverLocationBean observerLocationBean, ViewingParametersBean viewParaBean) {

        viewingParametersBean = viewParaBean;
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        painter = new SkyCanvasPainter(canvas);
        this.observerLocationBean = observerLocationBean;
        this.dateTimeBean = dateTimeBean;
        this.timeAnimator = timeAnimator;

        nameObjectTracked = "";
        horCoords = new ArrayList<>();

        horFactor = new SimpleDoubleProperty(100);
        verFactor = new SimpleDoubleProperty(100);
        scrollFactor = new SimpleDoubleProperty(100);

        enDrawStars = new SimpleBooleanProperty(true);
        enDrawPlanets = new SimpleBooleanProperty(true);
        enDrawSun = new SimpleBooleanProperty(true);
        enDrawMoon = new SimpleBooleanProperty(true);
        enDrawHorizon = new SimpleBooleanProperty(true);
        enDrawAsterisms = new SimpleBooleanProperty(true);
        enDrawConstellations = new SimpleBooleanProperty(true);
        isTrackingCelestial = new SimpleBooleanProperty(false);

        projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(viewingParametersBean.getCenter()),
            viewingParametersBean.centerProperty());

        observedSky = Bindings.createObjectBinding(
                () -> new ObservedSky(dateTimeBean.getZonedDateTime(),
                        observerLocationBean.getCoordinates(), projection.get(), catalogue),
            this.dateTimeBean.dateProperty(),
            this.dateTimeBean.timeProperty(),
            this.dateTimeBean.zoneProperty(),
            this.observerLocationBean.coordinatesProperty(),
            projection);

        planeToCanvas = Bindings.createObjectBinding( () -> {
                double expansionFactor =
                        canvas.getWidth() / (2 * tan(viewingParametersBean.getFieldOfView() / 4));

                if(expansionFactor == 0)                        // ??vite des erreurs lors
                    expansionFactor = DEFAULT_EXPANSION_FACTOR; // de l'initialisation

                return Transform.affine(expansionFactor, 0, 0, -expansionFactor,
                            canvas.getWidth() / 2, canvas.getHeight() / 2);
                },
            viewingParametersBean.fieldOfViewDegProperty(),
            canvas.widthProperty(),
            canvas.heightProperty(),
            projection);

        mousePosition = new SimpleObjectProperty<>(DEFAULT_MOUSE_POSITION);

        mousePointTransform = Bindings.createObjectBinding( () -> {
                double mouseX = mousePosition.get().x();
                double mouseY = mousePosition.get().y();
                return planeToCanvas.get().inverseTransform(mouseX, mouseY);
                },
            mousePosition,
            planeToCanvas);

        mouseHorizontalPosition = Bindings.createObjectBinding( () -> {
                Point2D point = mousePointTransform.get();
                return projection.get()
                            .inverseApply(CartesianCoordinates.of(point.getX(), point.getY()));
                } ,
            mousePosition,
            projection,
            planeToCanvas);

        mouseAzDeg = Bindings.createDoubleBinding(
                () -> mouseHorizontalPosition.get().azDeg() ,
            mouseHorizontalPosition);

        mouseAltDeg = Bindings.createDoubleBinding(
                () -> mouseHorizontalPosition.get().altDeg() ,
            mouseHorizontalPosition);

        objectUnderMouse = Bindings.createObjectBinding( () -> {
                Point2D point = mousePointTransform.get();
                double distance = planeToCanvas.get().inverseDeltaTransform(MAX_DISTANCE, 0).magnitude();
                return observedSky.get()
                            .objectClosestTo(CartesianCoordinates.of(point.getX(), point.getY()), distance)
                            .orElse(null);
                },
            observedSky,
            mousePointTransform,
            planeToCanvas);

        timeAnimatorNotDayOrSideral = Bindings.createBooleanBinding( () ->
                timeAnimator.getPersonalAcceleration() ||
                !(timeAnimator.acceleratorProperty().get().equals(DAY.getAccelerator()) ||
                timeAnimator.acceleratorProperty().get().equals(SIDEREAL_DAY.getAccelerator())),
            timeAnimator.acceleratorProperty(),
            timeAnimator.personalAccelerationProperty());

        canvasListeners();
        othersListeners();
    }

    /**
     * Installe les listeners du canvas.
     */
    private void canvasListeners() {
        canvas.setOnMouseMoved(
                e -> mousePosition.set(
                        CartesianCoordinates.of(
                                e.getX(), e.getY()) ));

        canvas.setOnMousePressed(e -> {
            if(e.isPrimaryButtonDown())
                canvas.requestFocus();
        });

        canvas.setOnScroll(e -> {
            double scroll_factor = scrollFactor.getValue() / 100;
            double x = e.getDeltaX() * scroll_factor;
            double y = e.getDeltaY() * scroll_factor;

            if(abs(x) > abs(y))
                changeFieldOfView(x);
            else
                changeFieldOfView(y);
        });

        canvas.setOnKeyPressed(e -> {
            double hor_factor = horFactor.getValue() / 100;
            double ver_factor = verFactor.getValue() / 100;

            if(!isTrackingCelestial.get()) {
                switch (e.getCode()) {
                    case RIGHT:
                        changeAzimut(LON_STEP_DISPLACEMENT * hor_factor);
                        break;
                    case LEFT:
                        changeAzimut(-LON_STEP_DISPLACEMENT * hor_factor);
                        break;
                    case UP:
                        changeAltitude(LAT_STEP_DISPLACEMENT * ver_factor);
                        break;
                    case DOWN:
                        changeAltitude(-LAT_STEP_DISPLACEMENT * ver_factor);
                }
            }
            e.consume();
        });

        canvas.widthProperty().addListener(e -> drawSky());
        canvas.heightProperty().addListener(e -> drawSky());
    }

    /**
     * Installe les autres listeners utiles.
     */
    private void othersListeners() {
        viewingParametersBean.centerProperty().addListener(e -> drawSky());
        viewingParametersBean.fieldOfViewDegProperty().addListener(e -> drawSky());

        dateTimeBean.dateProperty().addListener(e -> drawSky());
        dateTimeBean.timeProperty().addListener(e -> drawSky());
        dateTimeBean.zoneProperty().addListener(e -> drawSky());

        observerLocationBean.coordinatesProperty().addListener(e -> drawSky());

        enDrawStars.addListener(e -> drawSky());
        enDrawPlanets.addListener(e -> drawSky());
        enDrawSun.addListener(e -> drawSky());
        enDrawMoon.addListener(e -> drawSky());
        enDrawHorizon.addListener(e -> drawSky());
        enDrawAsterisms.addListener(e -> drawSky());
        enDrawConstellations.addListener(e -> drawSky());
        painter.dayNightCycleProperty().addListener(e -> drawSky());
    }

    /**
     * Modifie le champ de vue de la valeur delta.
     *
     * @param delta nombre de degr??s ?? ajouter au champ de vue.
     */
    private void changeFieldOfView(double delta) {
        double oldFieldOfView =
                viewingParametersBean.getFieldOfViewDeg();
        double newFieldOfView =
                INTERVAL_FIELD_OF_VIEW.clip(oldFieldOfView + delta);

        viewingParametersBean.setFieldOfViewDeg(newFieldOfView);
    }

    /**
     * Modifie l'azimut du centre de projection de la valeur deg.
     *
     * @param deg nombre de degr??s ?? ajouter ?? l'azimut.
     */
    private void changeAzimut(double deg) {
        HorizontalCoordinates oldCoordinates =
                viewingParametersBean.getCenter();
        double newAzDeg =
                INTERVAL_AZ_DEG.reduce(oldCoordinates.azDeg() + deg);
        HorizontalCoordinates newCoordinates =
                HorizontalCoordinates.ofDeg(newAzDeg, oldCoordinates.altDeg());

        viewingParametersBean.setCenter(newCoordinates);
    }

    /**
     * Modifie l'altitude du centre de projection de la valeur deg.
     *
     * @param deg nombre de degr??s ?? ajouter ?? l'altitude.
     */
    private void changeAltitude(double deg) {
        HorizontalCoordinates oldCoordinates =
                viewingParametersBean.getCenter();
        double newAltDeg =
                INTERVAL_ALT_DEG.clip(oldCoordinates.altDeg() + deg);
        HorizontalCoordinates newCoordinates =
                HorizontalCoordinates.ofDeg(oldCoordinates.azDeg(), newAltDeg);

        viewingParametersBean.setCenter(newCoordinates);
    }

    /**
     * Nettoie le canvas puis appelle toutes les m??thodes pour dessiner tout le ciel.
     */
    private void drawSky() {
        painter.clear(observedSky.get().sunPosHorizontalCoords());

        if(enDrawConstellations.getValue())
            painter.drawConstellations(observedSky.get(), planeToCanvas.get());

        if(enDrawAsterisms.getValue())
            painter.drawAsterisms(observedSky.get(), planeToCanvas.get());

        if(isTrackingCelestialProperty().get() && timeAnimatorNotDayOrSideral.get()
                && timeAnimator.getRunning().get())
            painter.drawPath(horCoords, projection.get(), planeToCanvas.get());
        else horCoords.clear();

        if(enDrawStars.getValue())
            painter.drawStars(observedSky.get(), projection.get(), planeToCanvas.get());

        if(enDrawPlanets.getValue())
            painter.drawPlanets(observedSky.get(), projection.get(), planeToCanvas.get());

        if(enDrawSun.getValue())
            painter.drawSun(observedSky.get(), projection.get(), planeToCanvas.get());

        if(enDrawMoon.getValue())
            painter.drawMoon(observedSky.get(), projection.get(), planeToCanvas.get());

        if(enDrawHorizon.getValue())
            painter.drawHorizon(projection.get(), planeToCanvas.get());
    }

    /**
     * Retourne les coordonn??es horizontales de l'objet c??leste.
     *
     * @param name nom de l'objet c??leste.
     * @return les coordonn??es horizontales de l'objet c??leste.
     */
    public HorizontalCoordinates getCoordsCelestialObject(String name) {
        HorizontalCoordinates coord = observedSky.get().getHorCoordsCelestialObject(name);
        if(!timeAnimatorNotDayOrSideral.get())
            return coord;

        if(!name.equals(nameObjectTracked)) {
            horCoords.clear();
            nameObjectTracked = name;
        }

        int size = horCoords.size();
        if(size > 0) {
            HorizontalCoordinates pastCoord = horCoords.get(size-1);
            double fieldOfViewFactor = viewingParametersBean.getFieldOfViewDeg() / 300;
            if(abs(coord.azDeg()-pastCoord.azDeg()) > fieldOfViewFactor
                    || abs(coord.altDeg()-pastCoord.altDeg()) > fieldOfViewFactor)
                horCoords.add(coord);
        } else
            horCoords.add(coord);


        if(horCoords.size() > 200) {
            horCoords.remove(0);
        }

        return coord;
    }

    /**
     * Enl??ve tous les ??l??ments de la liste horCoords.
     */
    public void clearHorCoordsList() {
        horCoords.clear();
    }

    /**
     * Retourne un acc??s ?? la propri??t?? mouseAzDeg qui est l'azimut de la
     * position du curseur de la souris.
     *
     * @return un acc??s ?? la propri??t?? mouseAzDeg.
     */
    public DoubleBinding mouseAzDegProperty() {
        return mouseAzDeg;
    }

    /**
     * Retourne un acc??s au contenu de la propri??t?? mouseAzDeg qui est l'azimut
     * de la position du curseur de la souris.
     *
     * @return un acc??s au contenu la propri??t?? mouseAzDeg.
     */
    public double getMouseAzDeg() {
        return mouseAzDeg.get();
    }

    /**
     * Retourne un acc??s ?? la propri??t?? mouseAltDeg qui est la hauteur de la
     * position du curseur de la souris.
     *
     * @return un acc??s ?? la propri??t?? mouseAltDeg.
     */
    public DoubleBinding mouseAltDegProperty() {
        return mouseAltDeg;
    }

    /**
     * Retourne un acc??s au contenu de la propri??t?? mouseAltDeg qui est la
     * hauteur de la position du curseur de la souris.
     *
     * @return un acc??s au contenu la propri??t?? mouseAltDeg.
     */
    public double getMouseAltDeg() {
        return mouseAltDeg.get();
    }

    /**
     * Retourne un acc??s ?? la propri??t?? objectUnderMouse qui est l'objet c??leste
     * le plus proche du curseur de la souris.
     *
     * @return un acc??s ?? la propri??t?? objectUnderMouse.
     */
    public ObjectBinding<CelestialObject> objectUnderMouseProperty() {
        return objectUnderMouse;
    }

    /**
     * Retourne un acc??s au contenu de la propri??t?? objectUnderMouse qui est
     * l'objet c??leste le plus proche du curseur de la souris.
     *
     * @return un acc??s au contenu de la propri??t?? objectUnderMouse.
     */
    public CelestialObject getObjectUnderMouse() {
        return objectUnderMouse.get();
    }

    /**
     * Retourne le canvas desinn??.
     *
     * @return le canvas dessin??.
     */
    public Canvas canvas() {
        return canvas;
    }

    /**
     * Retourne un acc??s ?? la propri??t?? horFactor qui est le facteur
     * de d??placement horizontal.
     *
     * @return un acc??s ?? la propri??t?? horFactor.
     */
    public DoubleProperty horFactorProperty() {
        return horFactor;
    }

    /**
     * Retourne un acc??s ?? la propri??t?? verFactor qui est le facteur
     * de d??placement vertical.
     *
     * @return un acc??s ?? la propri??t?? verFactor.
     */
    public DoubleProperty verFactorProperty() {
        return verFactor;
    }

    /**
     * Retourne un acc??s ?? la propri??t?? scrollFactor qui est le facteur
     * de d??placement du zoom.
     *
     * @return un acc??s ?? la propri??t?? scrollFactor.
     */
    public DoubleProperty scrollFactorProperty() {
        return scrollFactor;
    }

    /**
     * Retourne un acc??s ?? la propri??t?? enDrawStars qui d??fini si on doit
     * dessiner les ??toiles ou non.
     *
     * @return un acc??s ?? la propri??t?? enDrawStars.
     */
    public BooleanProperty enDrawStarsProperty() {
        return enDrawStars;
    }

    /**
     * Retourne un acc??s ?? la propri??t?? enDrawPlanets qui d??fini si on doit
     * dessiner les plan??tes ou non.
     *
     * @return un acc??s ?? la propri??t?? enDrawPlanets.
     */
    public BooleanProperty enDrawPlanetsProperty() {
        return enDrawPlanets;
    }

    /**
     * Retourne un acc??s ?? la propri??t?? enDrawSun qui d??fini si on doit
     * dessiner le Soleil ou non.
     *
     * @return un acc??s ?? la propri??t?? enDrawSun.
     */
    public BooleanProperty enDrawSunProperty() {
        return enDrawSun;
    }

    /**
     * Retourne un acc??s ?? la propri??t?? enDrawMoon qui d??fini si on doit
     * dessiner la Lune ou non.
     *
     * @return un acc??s ?? la propri??t?? enDrawMoon.
     */
    public BooleanProperty enDrawMoonProperty() {
        return enDrawMoon;
    }

    /**
     * Retourne un acc??s ?? la propri??t?? enDrawHorizon qui d??fini si on doit
     * dessiner l'Horizon ou non.
     *
     * @return un acc??s ?? la propri??t?? enDrawHorizon.
     */
    public BooleanProperty enDrawHorizonProperty() {
        return enDrawHorizon;
    }

    /**
     * Retourne un acc??s ?? la propri??t?? enDrawAsterisms qui d??fini si on doit
     * dessiner les constellations ou non.
     *
     * @return un acc??s ?? la propri??t?? enDrawAsterisms.
     */
    public BooleanProperty enDrawAsterismsProperty() {
        return enDrawAsterisms;
    }

    /**
     * Retourne un acc??s ?? la propri??t?? enDrawConstellations qui d??fini si on doit
     * dessiner les constellations imag??es ou non.
     *
     * @return un acc??s ?? la propri??t?? enDrawConstellations.
     */
    public BooleanProperty enDrawConstellationsProperty() {
        return enDrawConstellations;
    }

    /**
     * Retourne un acc??s ?? la propri??t?? dayNightCycle qui d??finit si on doit
     * dessiner le cycle jour/nuit.
     *
     * @return un acc??s ?? la propri??t?? dayNightCycle.
     */
    public BooleanProperty dayNightCycleProperty() {
        return painter.dayNightCycleProperty();
    }

    /**
     * Retourne un acc??s ?? la propri??t?? isTrackingCelestial qui d??finit si on
     * est entrain de suivre un objet c??leste ou non.
     *
     * @return un acc??s ?? la propri??t?? isTrackingCelestial.
     */
    public BooleanProperty isTrackingCelestialProperty() {
        return isTrackingCelestial;
    }

}
