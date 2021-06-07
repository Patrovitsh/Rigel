package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import static ch.epfl.rigel.math.Angle.TAU;
import static ch.epfl.rigel.math.Angle.toDeg;

/**
 * Coordonnées Sphériques.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
abstract class SphericalCoordinates {

    /**
     * Interval [0, 2Pi[ pour certaines longitudes.
     */
    protected static final RightOpenInterval INTERVAL_LON_RAD = RightOpenInterval.of(0, TAU);
    /**
     * Interval [-180, 180[ pour certaines longitudes.
     */
    protected static final RightOpenInterval INTERVAL_LON_SYMMETRIC_DEG = RightOpenInterval.symmetric(360);
    /**
     * Interval [-Pi, Pi] pour certaines latitudes.
     */
    protected static final ClosedInterval INTERVAL_LAT_SYMMETRIC_RAD = ClosedInterval.symmetric(TAU / 2);
    /**
     * Interval [-90, 90] pour certaines latitudes.
     */
    protected static final ClosedInterval INTERVAL_LAT_SYMMETRIC_DEG = ClosedInterval.symmetric(180);

    private final double longitude, latitude;

    /**
     * Construit de nouvelles coordonnées sphériques.
     *
     * @param longitude la longitude (Unité : radians).
     * @param latitude la latitude (Unité : radians).
     */
    SphericalCoordinates(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Retourne la longitude (Unité : radians).
     *
     * @return la longitude (Unité : radians).
     */
    double lon() {
        return longitude;
    }

    /**
     * Retourne la longitude (Unité : degrés).
     *
     * @return la longitude (Unité : degrés).
     */
    double lonDeg() {
        return toDeg(longitude);
    }

    /**
     * Retourne la latitude (Unité : radians).
     *
     * @return la latitude (Unité : radians).
     */
    double lat() {
        return latitude;
    }

    /**
     * Retourne la latitude (Unité : radians).
     *
     * @return la latitude (Unité : radians).
     */
    double latDeg() {
        return toDeg(latitude);
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    /*
    Nous avons du mettre cette redéfinition en commentaire car dans la classe
    SkyCanvasManager on utilise la méthode setCenter(...), défini dans la
    classe ViewingParametersBean, qui malgré nous, fini par appeler la méthode
    equals(...) ci-dessous. D'après les assistants, cela pourrait venir de
    windows et ils nous ont suggérer de mettre cette redéfinition en commentaire.

    @Override
    public final boolean equals(Object o) {
        throw new UnsupportedOperationException();
    }
     */
}
