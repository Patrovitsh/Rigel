package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

import java.util.Locale;

import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * Coordonnées Géographiques.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class GeographicCoordinates extends SphericalCoordinates {

    /**
     * Construit de nouvelles coordonnées géographiques.
     *
     * @param longitude la longitude (Unité : radians).
     * @param latitude la latitude (Unité : radians).
     */
    private GeographicCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }

    /**
     * Construit de nouvelles coordonnées géographiques avec les arguments
     * entrés en paramètre si ils sont valides.
     *
     * @param lonDeg la longitude (Unité : degrees).
     * @param latDeg la latitude (Unité : degrees).
     * @return de nouveaux coordonnées équatoriales.
     */
    public static GeographicCoordinates ofDeg(double lonDeg, double latDeg) {
        checkArgument(isValidLonDeg(lonDeg) && isValidLatDeg(latDeg));
        return new GeographicCoordinates(Angle.ofDeg(lonDeg), Angle.ofDeg(latDeg));
    }

    /**
     * Test si la longitude est valide.
     *
     * @param lonDeg la longitude (Unité : degrés).
     * @return true si la longitude est valide.
     */
    public static boolean isValidLonDeg(double lonDeg) {
        return INTERVAL_LON_SYMMETRIC_DEG.contains(lonDeg);
    }

    /**
     * Test si la latitude est valide.
     *
     * @param latDeg la latitude (Unité : degrés).
     * @return true si la latitude est valide.
     */
    public static boolean isValidLatDeg(double latDeg) {
        return INTERVAL_LAT_SYMMETRIC_DEG.contains(latDeg);
    }
    
    /**
     * Retourne la longitude (Unité : radians).
     *
     * @return la longitude (Unité : radians).
     */
    @Override
    public double lon() {
        return super.lon();
    }

    /**
     * Retourne la longitude (Unité : degrés).
     *
     * @return la longitude (Unité : degrés).
     */
    @Override
    public double lonDeg() {
        return super.lonDeg();
    }

    /**
     * Retourne la latitude (Unité : radians).
     *
     * @return la latitude (Unité : radians).
     */
    @Override
    public double lat() {
        return super.lat();
    }

    /**
     * Retourne la latitude (Unité : degrés).
     *
     * @return la latitude (Unité : degrés).
     */
    @Override
    public double latDeg() {
        return super.latDeg();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(lon=%.4f°, lat=%.4f°)", lonDeg(), latDeg());
    }

}
