package ch.epfl.rigel.math;

import ch.epfl.rigel.coordinates.CartesianCoordinates;
import javafx.geometry.Point2D;

import static java.lang.Math.log;
import static java.lang.Math.sqrt;
import static ch.epfl.rigel.coordinates.CartesianCoordinates.of;

/**
 * Fonction trigonométrique.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public class TrigoFunctions {

    private TrigoFunctions() {}

    /**
     * Retourne l'inverse du sinus hyperbolique en x.
     *
     * @param x point où on doit évaluer la valeur.
     * @return l'inverse du sinus hyperbolique en x.
     */
    public static double asinh(double x) {
        return log(x+ sqrt(1+x*x));
    }

    /**
     * Retourne la distance au carré entre deux points d'un plan.
     *
     * @param xy coordonnées cartésiennes du premier point.
     * @param x coordonnée x du second point.
     * @param y coordonnée y du second point.
     * @return la distance au carré entre deux points d'un plan.
     */
    public static double distanceSquare(CartesianCoordinates xy, double x, double y) {
        double deltaX = xy.x() - x;
        double deltaY = xy.y() - y;
        return deltaX * deltaX + deltaY * deltaY;
    }

    /**
     * Retourne la distance au carré entre deux points d'un plan.
     *
     * @param xy1 coordonnées cartésiennes du premier point.
     * @param xy2 coordonnées cartésiennes du second point.
     * @return la distance au carré entre deux points d'un plan.
     */
    public static double distanceSquare(CartesianCoordinates xy1, CartesianCoordinates xy2) {
        return distanceSquare(xy1, xy2.x(), xy2.y());
    }

    /**
     * Retourne la distance au carré entre deux points d'un plan.
     *
     * @param point1 coordonnées du premier point.
     * @param point2 coordonnées du second point.
     * @return la distance au carré entre deux points d'un plan.
     */
    public static double distanceSquare(Point2D point1, Point2D point2) {
        return distanceSquare(of(point1), of(point2));
    }

}

