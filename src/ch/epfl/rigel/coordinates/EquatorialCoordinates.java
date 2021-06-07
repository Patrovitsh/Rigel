package ch.epfl.rigel.coordinates;

import java.util.Locale;

import static ch.epfl.rigel.Preconditions.checkInInterval;
import static ch.epfl.rigel.math.Angle.toHr;

/**
 * Coordonnées Équatoriales.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class EquatorialCoordinates extends SphericalCoordinates {

    /**
     * Construit de nouvelles coordonnées équqtoriales
     *
     * @param ra l'ascension droite (Unité : radians).
     * @param dec la déclinaison (Unité : radians).
     */
    private EquatorialCoordinates(double ra, double dec) {
        super(ra, dec);
    }

    /**
     * Construit de nouvelles coordonnées équatoriales avec les arguments
     * entrés en paramètre si ils sont valides.
     *
     * @param ra l'ascension droite (Unité : radians).
     * @param dec la déclinaison (Unité : radians).
     * @return de nouveaux coordonnées équatoriales.
     */
    public static EquatorialCoordinates of(double ra, double dec) {
        return new EquatorialCoordinates(
                checkInInterval(INTERVAL_LON_RAD, ra),
                checkInInterval(INTERVAL_LAT_SYMMETRIC_RAD, dec)
        );
    }

    /**
     * Retourne l'ascension droite (Unité : radians).
     *
     * @return l'ascension droite (Unité : radians).
     */
    public double ra() {
        return lon();
    }

    /**
     * Retourne l'ascension droite (Unité : degrés).
     *
     * @return l'ascension droite (Unité : degrés).
     */
    public double raDeg() {
        return lonDeg();
    }

    /**
     * Retourne l'ascension droite (Unité : heures).
     *
     * @return l'ascension droite (Unité : heures).
     */
    public double raHr() {
        return toHr(ra());
    }

    /**
     * Retourne la déclinaison (Unité : radians).
     *
     * @return la déclinaison (Unité : radians).
     */
    public double dec() {
        return lat();
    }

    /**
     * Retourne la déclinaison (Unité : degrés).
     *
     * @return la déclinaison (Unité : degrés).
     */
    public double decDeg() {
        return latDeg();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(ra=%.4fh, dec=%.4f°)", raHr(), decDeg());
    }
}
