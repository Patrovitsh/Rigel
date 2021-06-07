package ch.epfl.rigel.gui;

import static ch.epfl.rigel.gui.MapPainter.NUMBER_OF_PIXELS;
import static ch.epfl.rigel.gui.MapPainter.NUMBER_OF_TILES_PER_SIDE;
import static ch.epfl.rigel.gui.MapPainter.DEFAULT_ZOOM;
import static ch.epfl.rigel.gui.SkyCanvasManager.DEFAULT_MOUSE_POSITION;

import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.MercatorProjection;
import ch.epfl.rigel.coordinates.OSMCoordinates;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Region;

/**
 * Gestionnaire de carte.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public class MapManager {

    private static final int CANVAS_SIDE_LENGTH = NUMBER_OF_PIXELS * NUMBER_OF_TILES_PER_SIDE;

    private final ObjectProperty<CartesianCoordinates> mousePosition;
    private final ObjectBinding<GeographicCoordinates> mouseGeographicPosition;
    private final DoubleBinding mouseLonDeg;
    private final DoubleBinding mouseLatDeg;
    private final DoubleBinding scaleFactor;
    private final DoubleBinding scaleX;
    private final DoubleBinding scaleY;

    private final ObserverLocationBean observerLocationBean;
    private final Canvas canvas;
    private final Canvas forSize;
    private final Region region;
    private final MapPainter mapPainter;
    private final MercatorProjection mercatorProjection;


    /**
     * Constructeur d'un gestionnaire de carte.
     *
     * @param observerLocationBean position de l'observateur.
     */
    public MapManager(ObserverLocationBean observerLocationBean) {
        this.observerLocationBean = observerLocationBean;
        canvas = new Canvas(CANVAS_SIDE_LENGTH, CANVAS_SIDE_LENGTH);

        mapPainter = new MapPainter(canvas);
        mercatorProjection = new MercatorProjection(DEFAULT_ZOOM);

        drawMapWithThumbtack(observerLocationBean.getCoordinates());

        forSize = new Canvas();
        region = new Region() {
            {getChildren().add(forSize); getChildren().add(canvas);}
        };

        forSize.widthProperty().bind(region.widthProperty());
        forSize.heightProperty().bind(region.heightProperty());

        scaleX = Bindings.createDoubleBinding( () ->
                forSize.widthProperty().get() / CANVAS_SIDE_LENGTH,
            forSize.widthProperty());

        scaleY = Bindings.createDoubleBinding( () ->
                forSize.heightProperty().get() / CANVAS_SIDE_LENGTH,
            forSize.heightProperty());

        scaleFactor = Bindings.createDoubleBinding( () ->
                Math.min(scaleX.get(), scaleY.get()),
            scaleX,
            scaleY);

        canvas.scaleXProperty().bind(scaleFactor);
        canvas.scaleYProperty().bind(scaleFactor);

        DoubleBinding translateX = Bindings.createDoubleBinding(() -> {
                    if (scaleX.get() <= scaleY.get())
                        return translate();
                    else
                        return translateMiddle(forSize.widthProperty(), forSize.heightProperty());
                },
                scaleFactor);

        DoubleBinding translateY = Bindings.createDoubleBinding(() -> {
                    if (scaleY.get() <= scaleX.get())
                        return translate();
                    else
                        return translateMiddle(forSize.heightProperty(), forSize.widthProperty());
                },
                scaleFactor);

        canvas.translateXProperty().bind(translateX);
        canvas.translateYProperty().bind(translateY);
      
        mousePosition = new SimpleObjectProperty<>(DEFAULT_MOUSE_POSITION);
        
        mouseGeographicPosition = Bindings.createObjectBinding( () -> {
                OSMCoordinates point =
                        OSMCoordinates.of(DEFAULT_ZOOM, mousePosition.get());
                return MercatorProjection.inverseApply(point);
                } ,
            mousePosition);
        
        mouseLonDeg = Bindings.createDoubleBinding(
                () -> mouseGeographicPosition.get().lonDeg() ,
            mouseGeographicPosition);
        
        mouseLatDeg = Bindings.createDoubleBinding(
                () -> mouseGeographicPosition.get().latDeg() ,
            mouseGeographicPosition);
    
        canvasListeners();
    }

    /**
     * Installe des Listener sur le canvas contenant la carte.
     */
    private void canvasListeners() {
        canvas.setOnMouseMoved(
                e -> mousePosition.set(
                        CartesianCoordinates.of(
                                e.getX(), e.getY()) ));

        canvas.setOnMousePressed(e -> {
            if(e.isPrimaryButtonDown())
                canvas.requestFocus();

            if(canvas.isFocused()) {
                mapPainter.drawNewThumbtack(mousePosition.get());
                observerLocationBean.setCoordinates(mouseGeographicPosition.get());
            }
        });
    }

    /**
     * Retourne la region contenant les deux canvas.
     *
     * @return la Region contenant les deux canvas.
     */
    public Region region() {
        return region;
    }

    /**
     * Re-dessine la carte avec une nouvelle punaise indiquant le point d'observation.
     *
     * @param geographicCoordinates coordonnées géographiques du point d'observation.
     */
    public void drawMapWithThumbtack(GeographicCoordinates geographicCoordinates) {
        CartesianCoordinates coordinates
                =  mercatorProjection.apply(geographicCoordinates).cartesianCordinates();
        mapPainter.drawNewThumbtack(coordinates);
    }

    /**
     * Retourne la valeur de la translation classique à appliquer pour
     * être au bord de la fenêtre.
     *
     * @return la valeur de la translation classique à appliquer pour
     *         être au bord de la fenêtre.
     */
    private double translate() {
        return (CANVAS_SIDE_LENGTH * (scaleFactor.get() - 1)) / 2.;
    }

    /**
     * Retourne la valeur de la translation à appliquer pour être au milieu
     * de la fenêtre.
     *
     * @param prop1 première DoubleProperty.
     * @param prop2 deuxième DoubleProperty.
     * @return la valeur de la translation à appliquer pour être au milieu
     *         de la fenêtre.
     */
    private double translateMiddle(DoubleProperty prop1, DoubleProperty prop2) {
        return (prop1.get() - prop2.get()) / 2. + translate();
    }
    
    /**
     * Retourne un accès à la propriété mouseLonDeg qui est la longitude de la
     * position du curseur de la souris.
     *
     * @return un accès à la propriété mouseLonDeg.
     */
    public DoubleBinding mouseLonDegProperty() {
        return mouseLonDeg;
    }

    /**
     * Retourne un accès au contenu de la propriété mouseLonDeg qui est la longitude
     * de la position du curseur de la souris.
     *
     * @return un accès au contenu la propriété mouseLonDeg.
     */
    public double getMouseLonDeg() {
        return mouseLonDeg.get();
    }

    /**
     * Retourne un accès à la propriété mouseLatDeg qui est la latitude de la
     * position du curseur de la souris.
     *
     * @return un accès à la propriété mouseLatDeg.
     */
    public DoubleBinding mouseLatDegProperty() {
        return mouseLatDeg;
    }

    /**
     * Retourne un accès au contenu de la propriété mouseLatDeg qui est la
     * latitude de la position du curseur de la souris.
     *
     * @return un accès au contenu la propriété mouseLatDeg.
     */
    public double getMouseLatDeg() {
        return mouseLatDeg.get();
    }
    
}
