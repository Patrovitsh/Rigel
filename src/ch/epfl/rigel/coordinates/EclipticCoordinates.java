package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;

import java.util.Locale;

import static ch.epfl.rigel.Preconditions.checkInInterval;

/**
 * Coordonnées Écliptiques.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class EclipticCoordinates extends SphericalCoordinates {

    /**
     * Construit de nouvelles coordonnées écliptiques.
     *
     * @param lon la longitude (Unité : radians).
     * @param lat la latitude (Unité : radians).
     */
    private EclipticCoordinates(double lon, double lat) {
        super(lon, lat);
    }

    /**
     * Construit de nouvelles coordonnées écliptiques avec les arguments
     * entrés en paramètre si ils sont valides.
     *
     * @param lon la longitude (Unité : radians).
     * @param lat la latitude (Unité : radians).
     * @return de nouveaux coordonnées écliptiaues.
     */
    public static EclipticCoordinates of(double lon, double lat) {
        return new EclipticCoordinates(
                checkInInterval(INTERVAL_LON_RAD, lon),
                checkInInterval(INTERVAL_LAT_SYMMETRIC_RAD, lat)
        );
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
        return String.format(Locale.ROOT, "(λ=%.4f°, β=%.4f°)", lonDeg(), latDeg());
    }
}
