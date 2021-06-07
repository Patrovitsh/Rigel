package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

import java.util.Locale;

import static ch.epfl.rigel.Preconditions.checkInInterval;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Coordonnées Horizontales.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class HorizontalCoordinates extends SphericalCoordinates {

    /**
     * Construit de nouvelles coordonnées horizontales.
     *
     * @param az l'azimut (Unité : radians).
     * @param alt la hauteur (Unité : radians).
     */
    private HorizontalCoordinates(double az, double alt) {
        super(az, alt);
    }

    /**
     * Construit de nouvelles coordonnées horizontales avec les arguments
     * entrés en paramètre si ils sont valides.
     *
     * @param az l'azimut (Unité : radians).
     * @param alt la hauteur (Unité : radians).
     * @return de nouveaux coordonnées horizontales.
     */
    public static HorizontalCoordinates of(double az, double alt) {
        return new HorizontalCoordinates(
                checkInInterval(INTERVAL_LON_RAD, az),
                checkInInterval(INTERVAL_LAT_SYMMETRIC_RAD, alt)
        );
    }

    /**
     * Construit de nouvelles coordonnées horizontales avec les arguments
     * entrés en paramètre si ils sont valides.
     *
     * @param azDeg l'azimut (Unité : radians).
     * @param altDeg la hauteur (Unité : radians).
     * @return de nouveaux coordonnées horizontales.
     */
    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg) {
        return new HorizontalCoordinates(
                checkInInterval(INTERVAL_LON_RAD, Angle.ofDeg(azDeg)),
                checkInInterval(INTERVAL_LAT_SYMMETRIC_RAD, Angle.ofDeg(altDeg))
        );
    }

    /**
     * Retourne l'azimut (Unité : radians).
     *
     * @return l'azimut (Unité : radians).
     */
    public double az() {
        return lon();
    }

    /**
     * Retourne l'azimut (Unité : degrés).
     *
     * @return l'azimut (Unité : degrés).
     */
    public double azDeg() {
        return lonDeg();
    }

    /**
     * Retourne la hauteur (Unité : radians).
     *
     * @return la hauteur (Unité : radians).
     */
    public double alt() {
        return lat();
    }

    /**
     * Retourne la hauteur (Unité : degrés).
     *
     * @return la hauteur (Unité : degrés).
     */
    public double altDeg() {
        return latDeg();
    }

    /**
     * Retourne une chaîne correspondant à l'octant dans lequel se trouve
     * l'azimut du récepteur.
     *
     * @param n chaîne de caractères correspondant au Nord.
     * @param e chaîne de caractères correspondant à l'Est.
     * @param s chaîne de caractères correspondant au Sud.
     * @param w chaîne de caractères correspondant à l'Ouest.
     * @return une chaîne correspondant à l'octant dans lequel se trouve
     *          l'azimut du récepteur.
     */
    public String azOctantName(String n, String e, String s, String w) {
        float azDegReduce = (float) azDeg() / 45.f;
        int azDegReduceToInt = Math.round(azDegReduce);

        switch (azDegReduceToInt) {
            case 1 : return n+e;
            case 2 : return e;
            case 3 : return s+e;
            case 4 : return s;
            case 5 : return s+w;
            case 6 : return w;
            case 7 : return n+w;
            default: return n;
        }
    }

    /**
     * Calcule la distance angulaire entre le point à ces coordonnées (this)
     * et celui aux coordonnées fournies en arguments.
     *
     * @param that coordonnées horizontales du point dont on veut calculer
     *          la distance angulaire avec celui-ci.
     * @return un nombre de type double correspondant à la distance angulaire
     *          entre 'this' et 'that' (Unité : radians).
     */
    public double angularDistanceTo(HorizontalCoordinates that) {
        double sinPhi1 = sin(this.alt());
        double sinPhi2 = sin(that.alt());
        double cosPhi1 = cos(this.alt());
        double cosPhi2 = cos(that.alt());
        double cosLambda1_2 = cos(this.az() - that.az());

        return Math.acos(sinPhi1 * sinPhi2 + cosPhi1 * cosPhi2 * cosLambda1_2);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(az=%.4f°, alt=%.4f°)", azDeg(), altDeg());
    }
}
