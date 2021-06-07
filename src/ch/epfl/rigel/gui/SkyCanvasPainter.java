package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Transform;

import java.util.List;

import static ch.epfl.rigel.math.Angle.ofDeg;
import static javafx.scene.paint.Color.*;

/**
 * Peintre de ciel.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public class SkyCanvasPainter {

    private static final ClosedInterval MAGNITUDE_INTERVAL = ClosedInterval.of(-2, 5);
    private static final double HALF_DEGREE = 0.5;
    private static final double OPACITY_SUN_HALO = 0.25;
    private static final int DEFAULT_VALUE = 1;
    private static final int TAU_DEG = 360;
    private static final int POLE_POSITION = 45;
    private static final String NORTH = "N";
    private static final String EAST = "E";
    private static final String SOUTH = "S";
    private static final String WEST = "O";
    private static final HorizontalCoordinates CENTER = HorizontalCoordinates.of(0, 0);
    private static final ClosedInterval BLUE_COLOR_INTERVAL = ClosedInterval.of(0, 1);
    private static final ClosedInterval GREEN_COLOR_INTERVAL = ClosedInterval.of(0, 0.75);

    private final Canvas canvas;
    private final GraphicsContext ctx;
    private final BooleanProperty dayNightCycle;

    /**
     * Constructeur d'un nouveau peintre de ciel.
     *
     * @param canvas dessin.
     */
    public SkyCanvasPainter(Canvas canvas){
        this.canvas = canvas;
        ctx = canvas.getGraphicsContext2D();
        dayNightCycle = new SimpleBooleanProperty(false);

    }

    /**
     * Efface le canvas et définie la couleur de fond en fonction de la position du soleil.
     *
     * @param sunPos coordonnées horizontales du Soleil.
     */
    public void clear(HorizontalCoordinates sunPos) {
        ctx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        Color color;
        if(dayNightCycle.getValue())
            color = colorBasedOnSunPosition(sunPos);
        else
            color = BLACK;

        ctx.setFill(color);
        ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Retourne une couleur qui est défini en fonction de la latitude du Soleil.
     *
     * @param sunPos coordonnées horizontales du Soleil.
     * @return une Color qui est défini en fonction de la latitude du Soleil.
     */
    private Color colorBasedOnSunPosition(HorizontalCoordinates sunPos) {
        double lonSun = sunPos.altDeg();

        double colorBlue = BLUE_COLOR_INTERVAL.clip(lonSun * 0.2);
        double colorGreen = GREEN_COLOR_INTERVAL.clip(lonSun * 0.15);

        return Color.color(0, colorGreen, colorBlue);
    }

    /**
     * Dessine les constellations sur le canvas.
     *
     * @param sky ciel observé à dessiner.
     * @param transformer transformation entre le repère de la projection et celui du canevas.
     */
    public void drawConstellations(ObservedSky sky, Transform transformer) {
        List<Constellation> constellations = sky.constellations();

        CartesianCoordinates coords1, coords2;
        Point2D point1, point2;

        for(Constellation constellation : constellations) {
            coords1 = sky.getCartesiansCoordsCelestial(constellation.getStar1());
            coords2 = sky.getCartesiansCoordsCelestial(constellation.getStar2());

            point1 = transformer.transform(coords1.x(), coords1.y());
            point2 = transformer.transform(coords2.x(), coords2.y());

            if(!noPointInCanvas(point1, point2))
                ConstellationDrawing.drawRotatedImage(ctx, constellation, point1, point2);
        }

    }

    /**
     * Dessine les astérismes sur le canvas.
     *
     * @param sky ciel observé à dessiner.
     * @param transformer transformation entre le repère de la projection et celui du canevas.
     */
    public void drawAsterisms(ObservedSky sky, Transform transformer) {
        List<Integer> starsIndexInCatalogue;
        double[] starsPosition = sky.starsPositions();
        Point2D pos1, pos2;

        ctx.setStroke(BLUE);
        ctx.beginPath();

        for(Asterism asterism : sky.asterisms()) {
            starsIndexInCatalogue = sky.starsIndex(asterism);

            pos2 = position(0, starsIndexInCatalogue, starsPosition, transformer);
            ctx.moveTo(pos2.getX(), pos2.getY());

            for(int i = 1; i < asterism.stars().size(); ++i) {
                pos1 = pos2;
                pos2 = position(i, starsIndexInCatalogue, starsPosition, transformer);

                if(noPointInCanvas(pos1, pos2))
                    ctx.moveTo(pos2.getX(), pos2.getY());
                else
                    ctx.lineTo(pos2.getX(), pos2.getY());
            }
        }
        ctx.stroke();
    }

    /**
     * Dessine la trajectoire de l'objet céleste qu'on suit.
     *
     * @param horCoords liste de coordonnées horizontales indiquant les anciennes position
     *                  de l'objet céleste à dessiner.
     * @param projection projection stéréographique à utiliser pour dessiner le trajet de
     *                   l'objet céleste.
     * @param transformer transformation entre le repère de la projection et celui du canevas.
     */
    public void drawPath(List<HorizontalCoordinates> horCoords, StereographicProjection projection,
                         Transform transformer) {
        ctx.setStroke(PURPLE);
        ctx.beginPath();
        CartesianCoordinates xyCoords;
        Point2D pos;
        boolean initialPosNotFind = true;

        for(HorizontalCoordinates coord : horCoords) {
            xyCoords = projection.apply(coord);
            pos = transformer.transform(xyCoords.x(), xyCoords.y());

            if(initialPosNotFind) {
                initialPosNotFind = false;
                ctx.moveTo(pos.getX(), pos.getY());
            } else
                ctx.lineTo(pos.getX(), pos.getY());
        }
        ctx.stroke();
    }

    /**
     * Dessine les étoiles et les astérismes sur le canvas.
     *
     * @param sky ciel observé à dessiner.
     * @param projection projection stéréographique à utiliser pour dessiner les étoiles.
     * @param transformer transformation entre le repère de la projection et celui du canevas.
     */
    public void drawStars(ObservedSky sky, StereographicProjection projection, Transform transformer) {
        Star star ;
        double xStar, yStar;
        Color color;
        List<Star> stars = sky.stars();
        double[] starsPositions = sky.starsPositions();
        
        for(int i = 0; i < stars.size(); i++)
        {
            star = stars.get(i);
            xStar = starsPositions[i*2];
            yStar = starsPositions[i*2+1];

            color = BlackBodyColor.colorForTemperature(star.colorTemperature());
            drawCelestialObject(star, xStar, yStar, color, projection, transformer);
        }
    }

    /**
     * Dessine les planètes sur le canvas.
     *
     * @param sky ciel observé à dessiner.
     * @param projection projection stéréographique à utiliser pour dessiner les étoiles.
     * @param transformer transformation entre le repère de la projection et celui du canevas.
     */
    public void drawPlanets(ObservedSky sky, StereographicProjection projection, Transform transformer) {
        Planet planet;
        double xPlanet, yPlanet;

        List<Planet> planets = sky.planets();
        double[] planetsPositions = sky.planetsPositions();

        for(int i = 0; i < planets.size(); i++)
        {
            planet = planets.get(i);
            xPlanet = planetsPositions[i*2];
            yPlanet = planetsPositions[i*2+1];

            drawCelestialObject(planet, xPlanet, yPlanet, LIGHTGREY, projection, transformer);
        }
    }

    /**
     * Dessine le soleil sur le canvas.
     *
     * @param sky ciel observé à dessiner.
     * @param projection projection stéréographique à utiliser pour dessiner les étoiles.
     * @param transformer transformation entre le repère de la projection et celui du canevas.
     */
    public void drawSun(ObservedSky sky, StereographicProjection projection, Transform transformer) {
        Sun sun = sky.sun();
        double xSun = sky.sunPositon().x();
        double ySun = sky.sunPositon().y();

        Point2D pos = transformer.transform(xSun, ySun);
        double size = projection.applyToAngle(sun.angularSize());
        double diameter = transformer.deltaTransform(size, 0).magnitude();

        Color color = YELLOW.deriveColor(
                DEFAULT_VALUE, DEFAULT_VALUE, DEFAULT_VALUE, OPACITY_SUN_HALO
        );

        fillCircleWithColor(pos.getX(), pos.getY(), diameter * 2.2, color);
        fillCircleWithColor(pos.getX(), pos.getY(), diameter + 2, YELLOW);
        fillCircleWithColor(pos.getX(), pos.getY(), diameter, WHITE);
    }

    /**
     * Dessine la Lune sur le canvas.
     *
     * @param sky ciel observé à dessiner.
     * @param projection projection stéréographique à utiliser pour dessiner les étoiles.
     * @param transformer transformation entre le repère de la projection et celui du canevas.
     */
    public void drawMoon(ObservedSky sky, StereographicProjection projection, Transform transformer) {
        double xMoon = sky.moonPositon().x();
        double yMoon = sky.moonPositon().y();

        Moon moon = sky.moon();
        double size = projection.applyToAngle(moon.angularSize());

        drawCelestialObject(xMoon, yMoon, size, WHITE, transformer);
    }

    /**
     * Dessine l'horizon sur le canvas.
     *
     * @param projection projection stéréographique à utiliser pour dessiner les étoiles.
     * @param transformer transformation entre le repère de la projection et celui du canevas.
     */
    public void drawHorizon(StereographicProjection projection, Transform transformer) {
        CartesianCoordinates coords = projection.circleCenterForParallel(CENTER);

        Point2D pos = transformer.transform(coords.x(), coords.y());
        double size = 2 * projection.circleRadiusForParallel(CENTER);
        double diameter = transformer.deltaTransform(size, 0).magnitude();

        if(diameter < 0)
            diameter = - diameter;

        ctx.setStroke(Color.RED);
        ctx.setLineWidth(2);
        ctx.setTextBaseline(VPos.TOP);
        ctx.setTextAlign(TextAlignment.CENTER);
        strokeCircle(pos.getX(), pos.getY(), diameter);
        ctx.setFill(Color.RED);

        HorizontalCoordinates hor;
        for(int lonDeg = 0; lonDeg < TAU_DEG; lonDeg += POLE_POSITION) {
            
            hor = HorizontalCoordinates.ofDeg(lonDeg, - HALF_DEGREE);
            coords = projection.apply(hor);

            pos = transformer.transform(coords.x(), coords.y());
            ctx.fillText(hor.azOctantName(NORTH, EAST, SOUTH, WEST), pos.getX(), pos.getY());
        }
    }

    /**
     * Dessine l'objet céleste fourni sur le canvas.
     *
     * @param celestialObject objet céleste.
     * @param xCoord coordonnée horizontale x.
     * @param yCoord coordonnée verticale y.
     * @param color couleur de l'objet céleste.
     * @param projection projection utilisé.
     * @param transformer transformer utilisé.
     */
    private void drawCelestialObject(CelestialObject celestialObject, double xCoord, double yCoord,
                           Color color, StereographicProjection projection, Transform transformer) {
        double size = sizeBasedOnMagnitude(celestialObject.magnitude(), projection);
        drawCelestialObject(xCoord, yCoord, size, color, transformer);
    }

    /**
     * Dessine l'objet céleste fourni sur le canvas.
     *
     * @param xCoord coordonnée horizontale x.
     * @param yCoord coordonnée verticale y.
     * @param size taille de l'objet céleste.
     * @param color couleur de l'objet céleste.
     * @param transformer transformer utilisé.
     */
    private void drawCelestialObject(double xCoord, double yCoord, double size, Color color, Transform transformer) {
        Point2D pos = transformer.transform(xCoord, yCoord);
        double diameter = transformer.deltaTransform(size, 0).magnitude();

        fillCircleWithColor(pos.getX(), pos.getY(), diameter, color);
    }

    /**
     * Dessine un rond sur le canvas.
     *
     * @param x coordonnée x où dessiner le rond.
     * @param y coordonnée y où dessiner le rond.
     * @param diameter diamètre du cercle à dessiner.
     */
    private void fillCircleWithColor(double x, double y, double diameter, Color color) {
        ctx.setFill(color);
        double radius = diameter / 2.0;
        ctx.fillOval(x - radius, y - radius, diameter, diameter);
    }
    /**
     * Dessine un rond vide sur le canvas.
     *
     * @param x coordonnée x où dessiner le rond.
     * @param y coordonnée y où dessiner le rond.
     * @param diameter diamètre du cercle à dessiner.
     */
    private void strokeCircle(double x, double y, double diameter) {
        double radius = diameter / 2.0;
        ctx.strokeOval(x - radius, y - radius, diameter, diameter);
    }

    /**
     * Retourne la taille de l'objet céleste en fonction de sa magnitude.
     *
     * @param magnitude magnitude de l'objet céleste.
     * @param projection projection stéréographique à utiliser pour projeter la taille de l'objet céleste.
     * @return la taille de l'objet céleste en fonction de sa magnitude.
     */
    private double sizeBasedOnMagnitude(double magnitude, StereographicProjection projection) {
        double mPrime = MAGNITUDE_INTERVAL.clip(magnitude);
        double f = (99 - 17 * mPrime) / 140.0;
        return f * projection.applyToAngle(ofDeg(HALF_DEGREE));
    }

    /**
     * Retourne True si aucun des deux points n'appartient au canvas,
     * si au moins un point est sur le canvas, retourne False.
     *
     * @param pos1 premier point.
     * @param pos2 deuxième point.
     * @return True si aucun point n'appartient au canvas, False sinon.
     */
    private boolean noPointInCanvas(Point2D pos1, Point2D pos2) {
        Bounds canvasBounds = canvas.getBoundsInLocal();
        return !canvasBounds.contains(pos1) && !canvasBounds.contains(pos2);
    }

    /**
     * Retourne la position.
     *
     * @param index indice de l'étoile dans l'astérisme.
     * @param indexInCatalogue liste d'indices des étoiles de l'astérisme dans le catalogue.
     * @param positions coordonnées cartésiennes des étoiles dans un tableau de double.
     * @param transformer transformation entre le repère de la projection et celui du canevas.
     * @return la position sous forme d'un Point2D.
     */
    private Point2D position(int index, List<Integer> indexInCatalogue, double[] positions, Transform transformer) {
        int starIndex = 2 * indexInCatalogue.get(index);
        return transformer.transform(positions[starIndex], positions[starIndex + 1]);
    }

    /**
     * Retourne un accès à la propriété dayNightCycle qui définit si on doit
     * dessiner ou non le cycle jour/nuit.
     *
     * @return un accès à la propriété dayNightCycle.
     */
    public BooleanProperty dayNightCycleProperty() {
        return dayNightCycle;
    }
}
