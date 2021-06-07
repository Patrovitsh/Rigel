package ch.epfl.rigel.coordinates;

import javafx.geometry.Point2D;

import java.util.Locale;

/**
 * Coordonnées cartésiennes.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class CartesianCoordinates {

    private final double x, y;

    /**
     * Construit de nouvelles cordonnées cartésiennes.
     *
     * @param x abscisse.
     * @param y ordonnée.
     */
    private CartesianCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Retourne de nouvelles coordonnées cartésiennes.
     *
     * @param x abscisse.
     * @param y ordonnée.
     * @return de nouvelles coordonnées cartésiennes.
     */
    public static CartesianCoordinates of(double x, double y) {
        return new CartesianCoordinates(x, y);
    }

    /**
     * Retourne de nouvelles coordonnées cartésiennes.
     *
     * @param point2D point en deux dimensions.
     * @return de nouvelles coordonnées cartésiennes.
     */
    public static CartesianCoordinates of(Point2D point2D) {
        return new CartesianCoordinates(point2D.getX(), point2D.getY());
    }

    /**
     * Retourne l'abscisse.
     *
     * @return l'abscisse.
     */
    public double x() {
        return x;
    }

    /**
     * Retourne l'ordonnée.
     *
     * @return l'ordonnée.
     */
    public double y() {
        return y;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(x=%.4f, y=%.4f)", x, y);
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean equals(Object o) {
        throw new UnsupportedOperationException();
    }
}
