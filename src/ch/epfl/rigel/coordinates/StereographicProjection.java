package ch.epfl.rigel.coordinates;

import java.util.Locale;
import java.util.function.Function;

import static ch.epfl.rigel.math.Angle.normalizePositive;
import static java.lang.Math.*;

/**
 * Projection stéréographique.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class StereographicProjection
        implements Function<HorizontalCoordinates, CartesianCoordinates> {

    private final double cosPhi1, sinPhi1, lambda0,phi1;

    /**
     * Construit un nouveau système de projection stéréographique.
     *
     * @param center le centre de la projection stéréographique.
     */
    public StereographicProjection(HorizontalCoordinates center) {
        phi1 = center.alt();
        cosPhi1 = cos(phi1);
        sinPhi1 = sin(phi1);
        lambda0 = center.az();
    }

    /**
     * Retourne les coordonnées du centre du cercle correspondant à la projection du parallèle
     * passant par le point hor.
     *
     * @param hor les coordonnées horizontales d'un point dont passe le parallèle.
     * @return les coordonnées cartésiennes du centre du cercle de la projection d'un parallèle.
     */
    public CartesianCoordinates circleCenterForParallel(HorizontalCoordinates hor) {
        double y = cosPhi1 / (sinPhi1 + sin(hor.alt()));
        return CartesianCoordinates.of(0, y);
    }

    /**
     * Retourne le rayon du cercle correspondant à la projection du parallèle passant
     * par le point de coordonnées parallel.
     *
     * @param parallel les coordonnées horizontales d'un point dont passe le parallèle.
     * @return la longueur du rayon du cercle de la projection d'un parallèle.
     */
    public double circleRadiusForParallel(HorizontalCoordinates parallel) {
        double phi = parallel.alt();
        double cosPhi = cos(phi);
        double sinPhi = sin(phi);

        return cosPhi / (sinPhi + sinPhi1);
    }

    /**
     * Retourne le diamètre projeté d'une sphère centrée au centre de projection.
     *
     * @param rad la taille angulaire (Unité : radians).
     * @return le diamètre projeté d'une sphère de taille angulaire rad centrée au centre de projection.
     */
    public double applyToAngle(double rad) {
        return 2 * tan(rad / 4);
    }

    @Override
    public CartesianCoordinates apply(HorizontalCoordinates azAlt) {
        double lambdaDelta = azAlt.az() - lambda0;
        double cosLambdaDelta = cos(lambdaDelta);
        double sinLambdaDelta = sin(lambdaDelta);

        double phi = azAlt.alt();
        double cosPhi = cos(phi);
        double sinPhi = sin(phi);

        double d = 1 / (1 + sinPhi * sinPhi1 + cosPhi * cosPhi1 * cosLambdaDelta);
        double x = d * cosPhi * sinLambdaDelta;
        double y = d * (sinPhi * cosPhi1 - cosPhi * sinPhi1 * cosLambdaDelta);

        return CartesianCoordinates.of(x, y);
    }

    /**
     * Retourne les coordonnées horizontales du point dont la projection est le point de coordonnées
     * cartésiennes xy.
     *
     * @param xy les coordonnées cartésiennes de la projection d'un point.
     * @return les coordonnées horizontales du point dont la projection est le point de coordonnées
     *              cartésiennes xy.
     */
    public HorizontalCoordinates inverseApply(CartesianCoordinates xy) {
        double x = xy.x(), y = xy.y();
       
        if ((x == 0) && (y == 0))
            return HorizontalCoordinates.of(lambda0, phi1);
        
        double rho = sqrt(x*x + y*y);
        double rho2 = rho*rho;

        double sinC = 2 * rho / (rho2 + 1);
        double cosC = (1 - rho2) / (rho2 + 1);

        double azimut = normalizePositive(
                atan2(x * sinC, rho * cosPhi1 * cosC - y * sinPhi1 * sinC) + lambda0
        );
        double height = asin(cosC * sinPhi1 + (y * sinC * cosPhi1) / rho);

        return HorizontalCoordinates.of(azimut, height);
    }

    @Override
    public String toString() {
        return String.format(
                Locale.ROOT, "StereographicProjection (az=%.4f°, alt=%.4f°)", lambda0, phi1
        );
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
