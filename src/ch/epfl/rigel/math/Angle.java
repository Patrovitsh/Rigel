package ch.epfl.rigel.math;

import javafx.geometry.Point2D;

import static ch.epfl.rigel.Preconditions.checkArgument;
import static java.lang.Math.*;

/**
 * Un angle.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class Angle {

    private Angle() {}

    public static final double TAU = 2 * Math.PI;

    private static final double RAD_PER_HR = TAU / 24;
    private static final double DEG_PER_MIN = 60;
    private static final double DEG_PER_HR = DEG_PER_MIN * DEG_PER_MIN;
    private static final RightOpenInterval INTERVAL_RAD = RightOpenInterval.of(0, TAU);
    private static final RightOpenInterval INTERVAL_MIN_AND_SEC = RightOpenInterval.of(0, 60);

    /**
     * Retourne l'angle normalisé sur [0;2pi[.
     *
     * @param rad angle (Unité : radians).
     * @return l'angle normalisé (Unité : radians).
     */
    public static double normalizePositive(double rad) {
        return INTERVAL_RAD.reduce(rad);
    }

    /**
     * Retourne l'angle en radians obtenu depuis des arcs secondes. (Unité : radians)
     *
     * @param sec angle (Unité : secondes d'arc).
     * @return un angle. (Unité : radians)
     */
    public static double ofArcsec(double sec) {
        return ofDeg(sec / DEG_PER_HR);
    }

    /**
     * Retourne l'angle en radians correspondant à la notation sexagésimale
     * de l'angle en degrés. (Unité : radians)
     *
     * @param deg angle (Unité : degrés).
     * @param min minutes d'arc
     * @param sec secondes d'arc
     * @return un angle (Unité : radians).
     */
    public static double ofDMS(int deg, int min, double sec) {
        checkArgument(INTERVAL_MIN_AND_SEC.contains(min));
        checkArgument(INTERVAL_MIN_AND_SEC.contains(sec));
        checkArgument(deg >= 0);

        return ofDeg(deg + min / DEG_PER_MIN + sec / DEG_PER_HR);
    }

    /**
     * Retourne l'angle en radians correspondant à l'angle en degrés. (Unité : radians)
     *
     * @param deg l'angle. (Unité : degrés)
     * @return un angle. (Unité : radians)
     */
    public static double ofDeg(double deg) {
        return toRadians(deg);
    }

    /**
     * Retourne l'angle en degrés correspondant à l'angle en radians. (Unité : degrés)
     *
     * @param rad l'angle (Unité : radians).
     * @return un angle (Unité : degrés).
     */
    public static double toDeg(double rad) {
        return toDegrees(rad);
    }

    /**
     * Retourne l'angle en radians correspondant à l'angle en heures. (Unité : radians)
     *
     * @param hr l'angle (Unité : heures).
     * @return un angle (Unité : radians).
     */
    public static double ofHr(double hr) {
        return hr * RAD_PER_HR;
    }

    /**
     * Retourne un angle en heures correspondant à l'angle en radians. (Unité : heures)
     *
     * @param rad l'angle (Unité : radians).
     * @return un angle (Unité : heures).
     */
    public static double toHr(double rad) {
        return rad / RAD_PER_HR;
    }

    /**
     * Retourne l'angle que fait la droite passante par deux points données
     * avec l'horizontale
     *
     * @param p1,p2 les données cartésiennes des points données
     * @return l'angle (Unité : degrées).
     */
    public static double angleWithHorizontal(Point2D p1, Point2D p2) {
        return toDeg(atan2(p2.getY() - p1.getY(),p2.getX() - p1.getX()));
    }
}
