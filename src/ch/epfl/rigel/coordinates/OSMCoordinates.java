package ch.epfl.rigel.coordinates;

import java.util.Locale;

import ch.epfl.rigel.Preconditions;

/**
 * Coordonnés OSM.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class OSMCoordinates {
    
    private final int zoom;
    private final CartesianCoordinates cartesianCoordinates;

    /**
     * Constructeur de coordonnées OSM.
     *
     * @param zoom niveau de zoom.
     * @param cartesianCoordinates coordonnées cartésiennes.
     */
    private OSMCoordinates (int zoom, CartesianCoordinates cartesianCoordinates) {
        this.zoom = zoom;
        this.cartesianCoordinates = cartesianCoordinates;
    }
    
    
    /**
     * Retourne de nouvelles coordonnées OSM.
     *
     * @param zoom le niveau de zoom.
     * @param cartesianCoordinates les cordonnées cartésiennes.
     * @return de nouvelles coordonnées OSM.
     */
    public static OSMCoordinates of(int zoom, CartesianCoordinates cartesianCoordinates) {
        Preconditions.checkArgument(isValidZoom(zoom));
        return new OSMCoordinates(zoom, cartesianCoordinates);
    }
    
    /**
     * Retourne de nouvelles coordonnées OSM avec les mêmes
     * cordonnées cartésiennes avec un niveau de zoom different.
     *
     * @param zoom le niveau de zoom.
     * @return de nouvelles coordonnées OSM.
     */
    public OSMCoordinates atZoom(int zoom) {
        Preconditions.checkArgument(isValidZoom(zoom));
        return OSMCoordinates.of(zoom, cartesianCoordinates);
    }
    
    /**
     * Test si le niveau de zoom est valide.
     *
     * @param zoom le niveau de zoom.
     * @return true si le niveau de zoom est valide.
     */
    public static boolean isValidZoom(int zoom) {
        return (zoom >= 0);
    }
    
    /**
     * Retourne le niveau de zoom .
     *
     * @return zoom.
     */
    public int zoom() {
        return zoom;
    }
    
    /**
     * Retourne les cordonnées cartésiennes.
     *
     * @return les cordonnées cartésiennes.
     */
    public CartesianCoordinates cartesianCordinates() {
        return cartesianCoordinates;
    }

    
    /**
     * Retourne l'abscisse .
     *
     * @return l'abscisse.
     */
    public double x() {
        return cartesianCoordinates.x();
    }

    /**
     * Retourne l'ordonnée.
     *
     * @return l'ordonnée.
     */
    public double y() {
        return cartesianCoordinates.y();
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(zoom=%d ,x=%.4f, y=%.4f)", zoom, x(), y());
    }
    
}
