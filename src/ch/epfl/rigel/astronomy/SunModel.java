package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import static ch.epfl.rigel.astronomy.Sun.*;
import static ch.epfl.rigel.math.Angle.*;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Définiton du Soleil.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public enum SunModel implements CelestialObjectModel<Sun> {

    SUN(
            LONGITUDE_SUN_J2010,
            LONGITUDE_SUN_PERIGEE,
            ECCENTRICITY, THETA_0
    );

    private static final double TROPICAL_YEAR = 365.242191;
    public static final double ANGULAR_VELOCITY = TAU / TROPICAL_YEAR;

    /**
     * Longitude du Soleil à J2010 (Unité : radians).
     */
    private final double epsilon_g;
    /**
     * Longitude du Soleil au périgée (Unité : radians).
     */
    private final double w_g;
    /**
     * L'excentricité.
     */
    private final double e;
    /**
     * La taille angulaire (Unité : radians).
     */
    private final double theta_0;

    /**
     * Caractéristiques du Soleil.
     *
     * @param epsilon_g Longitude du Soleil à J2010 (Unité : radians).
     * @param w_g Longitude du Soleil au périgée (Unité : radians).
     * @param e Excentricité.
     * @param theta_0 Taille angulaire (Unité : radians).
     */
    SunModel(double epsilon_g, double w_g, double e, double theta_0) {
        this.epsilon_g = ofDeg(epsilon_g);
        this.w_g = ofDeg(w_g);
        this.e = e;
        this.theta_0 = ofDeg(theta_0);
    }

    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        double M = meanAnomaly(daysSinceJ2010);
        double V = trueAnomaly(M);

        EclipticCoordinates sunEclipticCoords = EclipticCoordinates.of(eclipticLongitude(V), 0);
        EquatorialCoordinates sunEquaCoords= eclipticToEquatorialConversion.apply(sunEclipticCoords);

        return new Sun(
                sunEclipticCoords,
                sunEquaCoords,
                angularSize(V),
                (float) M
        );
    }

    /**
     * Calcul de l'anomalie moyenne du Soleil à moment donnée.
     *
     * @param daysSinceJ2010 Nombre de jour depuis J2010.
     * @return l'anomalie moyenne du Soleil à un moment donnée (Unité : radians).
     */
    private double meanAnomaly(double daysSinceJ2010) {
        return normalizePositive(
                ANGULAR_VELOCITY * daysSinceJ2010 + epsilon_g - w_g
        );
    }

    /**
     * Calcul de l'anomalie moyenne du Soleil.
     *
     * @param M Anomalie Moyenne (Unité : radians).
     * @return la vraie anomalie du Soleil (Unité : radians).
     */
    private double trueAnomaly(double M) {
        return M + 2 * e * sin(M);
    }

    /**
     * Calcul la longitude écliptique géocentrique (Unité : radians).
     * @param V Vraie anomalie (Unité : radians).
     * @return la longitude écliptique géocentrique (Unité : radians).
     */
    private double eclipticLongitude(double V) {
        return normalizePositive(V + w_g);
    }

    /**
     * Calcul de la taille angulaire du Soleil.
     *
     * @param trueAnomaly Vraie anomalie (Unité : radians).
     * @return la taille angulaire du Soleil (Unité : radians).
     */
    private float angularSize(double trueAnomaly) {
        return (float) ( theta_0 * ((1 + e * cos(trueAnomaly)) / (1 - e*e)) );
    }
}
