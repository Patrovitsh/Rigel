package ch.epfl.rigel.astronomy;

import static ch.epfl.rigel.astronomy.SunModel.ANGULAR_VELOCITY;
import static ch.epfl.rigel.math.Angle.*;
import static java.lang.Math.*;

import java.util.List;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * Définitons des Planètes.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public enum PlanetModel implements CelestialObjectModel<Planet> {
    MERCURY("Mercure", 0.24085, 75.5671, 77.612, 0.205627,
            0.387098, 7.0051, 48.449, 6.74, -0.42),
    VENUS("Vénus", 0.615207, 272.30044, 131.54, 0.006812,
            0.723329, 3.3947, 76.769, 16.92, -4.40),
    EARTH("Terre", 0.999996, 99.556772, 103.2055, 0.016671,
            0.999985, 0, 0, 0, 0),
    MARS("Mars", 1.880765, 109.09646, 336.217, 0.093348,
            1.523689, 1.8497, 49.632, 9.36, -1.52),
    JUPITER("Jupiter", 11.857911, 337.917132, 14.6633, 0.048907,
            5.20278, 1.3035, 100.595, 196.74, -9.40),
    SATURN("Saturne", 29.310579, 172.398316, 89.567, 0.053853,
            9.51134, 2.4873, 113.752, 165.60, -8.88),
    URANUS("Uranus", 84.039492, 356.135400, 172.884833, 0.046321,
            19.21814, 0.773059, 73.926961, 65.80, -7.19),
    NEPTUNE("Neptune", 165.84539, 326.895127, 23.07, 0.010483,
            30.1985, 1.7673, 131.879, 62.20, -6.87);

    public static List<PlanetModel> ALL = List.of(values());
    
    /**
     * Le nom de la planète.
     */
    private final String name;
    /**
     * Période de révolution [année Tropique].
     */
    private final double T;
    /**
     * Longitude à J2010 (Unité : radians).
     */
    private final double epsilon;
    /**
     * Longitude au périgée (Unité : radians).
     */
    private final double w;
    /**
     * Excentricité de l'orbite.
     */
    private final double e;
    /**
     * Demi grand-axe de l'orbite (Unité : UA).
     */
    private final double a;
    /**
     * Cosinus de l'inclinaison de l'orbite à l'écliptique (Unité : radians).
     */
    private final double cos_i;
    /**
     * Sinus de l'inclinaison de l'orbite à l'écliptique (Unité : radians).
     */
    private final double sin_i;
    /**
     * Longitude du nœud ascendant (Unité : radians).
     */
    private final double omega;
    /**
     * Taille Angulaire (Unité : radians).
     */
    private final double theta_0;
    /**
     * Magnitude.
     */
    private final double v_0;

    /**
     * Caractéristiques des planètes.
     *
     * @param name Le nom de la planète.
     * @param T Période de révolution [année Tropique].
     * @param epsilon Longitude à J2010 (Unité : radians).
     * @param w Longitude au périgée (Unité : radians).
     * @param e Excentricité de l'orbite.
     * @param a Demi grand-axe de l'orbite (Unité : UA).
     * @param i Inclinaison de l'orbite à l'écliptique (Unité : radians).
     * @param Omega Longitude du nœud ascendant (Unité : radians).
     * @param theta_0 Taille Angulaire (Unité : radians).
     * @param V_0 Magnitude.
     */
    PlanetModel(String name, double T, double epsilon, double w, double e, double a, double i,
                double Omega, double theta_0, double V_0) {
        this.name = name;
        this.T = T;
        this.epsilon = ofDeg(epsilon);
        this.w = ofDeg(w);
        this.e = e;
        this.a = a;
        double rad_i = ofDeg(i);
        this.cos_i = cos(rad_i);
        this.sin_i = sin(rad_i);
        this.omega = ofDeg(Omega);
        this.theta_0 = ofArcsec(theta_0);
        this.v_0 = V_0;
    }

    @Override
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        double V = trueAnomaly(
                meanAnomaly(daysSinceJ2010)
        );

        double r = radius(V);
        double l = longitudeHeliocentric(V);

        double sin_l_omega = sin(l - omega);
        double phi = latEclipticHeliocentric(sin_l_omega);
        
        double r_proj = radiusProjectedOnEcliptic(r, phi);
        double l_proj = longitudeProjectedOnEcliptic(l, sin_l_omega);

        double V_earth = trueAnomaly(
                meanAnomaly(daysSinceJ2010, EARTH),
                EARTH
        );
        
        double R = radius(V_earth, EARTH);
        double L = longitudeHeliocentric(V_earth, EARTH);

        double lambda = lonEclipticGeocentric(R, L, r_proj, l_proj);

        EclipticCoordinates eclipticCoords = EclipticCoordinates.of(
                lambda,
                latitude(R, L, r_proj, l_proj, lambda, phi)
        );
        EquatorialCoordinates equaCoords = eclipticToEquatorialConversion.apply(eclipticCoords);

        double p = distancePlanetEarth(R, r, L, l, phi);
        double F = phase(l, lambda);

        return new Planet(
                name,
                equaCoords,
                angularSize(p),
                magnitude(r, p, F)
        );
    }

    /**
     * Retourne le nom de la planète.
     *
     * @return le nom de la planète.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Calcul de l'anomalie moyenne.
     *
     * @param D Nombre de jours depuis J2010.
     * @param planet Planète en question.
     * @return l'anomalie moyenne de la planète 'planet' (Unité : radians).
     */
    private static double meanAnomaly(double D, PlanetModel planet) {
      return ANGULAR_VELOCITY * (D / planet.T) + planet.epsilon - planet.w;
    }

    /**
     * Calcul de l'anomalie moyenne.
     *
     * @param D Nombre de jours depuis J2010.
     * @return l'anomalie moyenne de cette planète (this) (Unité : radians).
     */
    private double meanAnomaly(double D) {
        return meanAnomaly(D, this);
    }

    /**
     * Calcul de la vraie anomalie.
     *
     * @param M Anomalie moyenne (Unité : radians).
     * @param planet Planète en question.
     * @return la vraie anomalie de la planète 'planet' (Unité : radians).
     */
    private static double trueAnomaly(double M, PlanetModel planet) {
        return M + 2 * planet.e * sin(M);
    }

    /**
     * Calcul de la vraie anomalie.
     *
     * @param M Anomalie moyenne (Unité : radians).
     * @return la vraie anomalie de cette planète (this) (Unité : radians).
     */
    private double trueAnomaly(double M) {
        return trueAnomaly(M, this);
    }

    /**
     * Calcul de la distance planète-Soleil.
     *
     * @param V Vraie Anomalie (Unité : radians).
     * @param planet Planète en question.
     * @return la distance entre la planète 'planet' et le Soleil (Unité : UA).
     */
    private static double radius(double V, PlanetModel planet) {
        return planet.a * (1 - planet.e*planet.e) / (1 + planet.e * cos(V));
    }

    /**
     * Calcul de la distance planète-Soleil.
     *
     * @param V Vraie Anomalie (Unité : radians).
     * @return la distance entre cette planète (this) et le Soleil (Unité : UA).
     */
    private double radius(double V) {
        return radius(V, this);
    }

    /**
     * Calcul de la longitude héliocentrique.
     *
     * @param V Vraie Anomalie (Unité : radians).
     * @param planet Planète en question.
     * @return la longitude héliocentrique de la planète 'planet' (Unité : radians)
     */
    private static double longitudeHeliocentric(double V, PlanetModel planet) {
        return V + planet.w;
    }

    /**
     * Calcul de la longitude héliocentrique.
     *
     * @param V Vraie Anomalie (Unité : radians).
     * @return la longitude héliocentrique de cette planète (this) (Unité : radians)
     */
    private double longitudeHeliocentric(double V) {
        return longitudeHeliocentric(V, this);
    }

    /**
     * Calcul de la projection d'une distance sur l'écliptique.
     *
     * @param r Distance planète Soleil (Unité : UA).
     * @param phi Latitude écliptique héliocentrique (Unité : radians).
     * @return la rojection de 'r' sur l'écliptique (Unité : UA).
     */
    private static double radiusProjectedOnEcliptic(double r, double phi) {
        return r * cos(phi);
    }

    /**
     * Calcul de la longitude écliptique héliocentrique.
     *
     * @param l Longitude héliocentrique (Unité : radians).
     * @return la longitude écliptique héliocentrique (Unité : radians).
     */
    private double longitudeProjectedOnEcliptic(double l, double sin_l_omega) {
        return atan2(sin_l_omega * cos_i, cos(l - omega)) + omega;
    }

    /**
     * Calcul de la latitude écliptique héliocentrique.
     *
     * @return la latitude écliptique héliocentrique (Unité : radians).
     */
    private double latEclipticHeliocentric(double sin_l_omega) {
        return asin(sin_l_omega * sin_i);
    }

    /**
     * Définie la bonne longitude à attribuer en fonction de si la planète est une planète
     * inférieure ou supérieure.
     *
     * @param R Distance Terre Soleil (Unité : UA).
     * @param L Longitude héliocentrique de la Terre (Unité : radians).
     * @param r_proj Projection du rayon sur l'écliptique (Unité : UA).
     * @param l_proj Longitude écliptique héliocentrique (Unité : radians).
     * @return la longitude écliptique géocentrique (Unité : radians).
     */
    private double lonEclipticGeocentric(double R, double L, double r_proj, double l_proj) {
        if(this.a < EARTH.a)
            return lonPlanetInf(R, L, r_proj, l_proj);
        else if(this.a > EARTH.a)
            return lonPlanetSup(R, L, r_proj, l_proj);

        throw new UnsupportedOperationException();
    }

    /**
     * Calcul de la longitude écliptique géocentrique pour les planètes inférieures.
     *
     * @param R Distance Terre Soleil (Unité : UA).
     * @param L Longitude héliocentrique de la Terre (Unité : radians).
     * @param r_proj Projection du rayon sur l'écliptique (Unité : UA).
     * @param l_proj Longitude écliptique héliocentrique (Unité : radians).
     * @return la longitude écliptique géocentrique (Unité : radians).
     */
    private static double lonPlanetInf(double R, double L, double r_proj, double l_proj) {
        return normalizePositive(
                PI + L + atan2(r_proj * sin(L - l_proj), R - r_proj * cos(L - l_proj))
        );
    }

    /**
     * Calcul de la longitude écliptique géocentrique pour les planètes Supérieures.
     *
     * @param R Distance Terre Soleil (Unité : UA).
     * @param L Longitude héliocentrique de la Terre (Unité : radians).
     * @param r_proj Projection du rayon sur l'écliptique (Unité : UA).
     * @param l_proj Longitude écliptique héliocentrique (Unité : radians).
     * @return la longitude écliptique géocentrique (Unité : radians).
     */
    private static double lonPlanetSup(double R, double L, double r_proj, double l_proj) {
        return normalizePositive(
                l_proj + atan2(R * sin(l_proj - L), r_proj - R * cos(l_proj - L))
        );
    }

    /**
     * Calcul de la latitude écliptique géocentrique.
     *
     * @param R Distance Terre Soleil (Unité : UA).
     * @param L Longitude héliocentrique de la Terre (Unité : radians).
     * @param r_proj Projection du rayon sur l'écliptique (Unité : UA).
     * @param l_proj Longitude écliptique héliocentrique (Unité : radians).
     * @param lambda Longitude écliptique (géocentrique) (Unité : radians).
     * @param phi Latitude écliptique héliocentrique (Unité : radians).
     * @return la latitude écliptique géocentrique (Unité : radians).
     */
    private static double latitude(double R, double L, double r_proj, double l_proj, double lambda, double phi) {
        double numerator = r_proj * tan(phi) * sin(lambda - l_proj);
        double denominator = R * sin(l_proj - L);

        return atan(numerator/denominator);
    }

    /**
     * Calcul de la distance à la Terre.
     *
     * @param R Distance Terre Soleil en (Unité : UA).
     * @param r Distance planète Soleil (Unité : UA).
     * @param L Longitude héliocentrique de la Terre (Unité : radians).
     * @param l Longitude héliocentrique (Unité : radians).
     * @param phi Latitude écliptique héliocentrique (Unité : radians).
     * @return la distance entre cette planète (this) et la Terre (Unité : UA).
     */
    private static double distancePlanetEarth(double R, double r, double L, double l, double phi) {
        return sqrt(R*R + r*r - 2 * R * r * cos(l-L) * cos(phi));
    }

    /**
     * Calcul de la taille angulaire (Unité : radians).
     *
     * @param p Distance planète Terre (Unité : UA).
     * @return la taille angulaire.
     */
    private float angularSize(double p) {
        return (float) (theta_0 / p);
    }

    /**
     * Calcul de la phase.
     *
     * @param l Longitude héliocentrique (Unité : radians).
     * @param lambda Longitude écliptique (géocentrique) (Unité : radians).
     * @return la phase.
     */
    private static double phase(double l, double lambda) {
        return (1 + cos(lambda - l)) / 2;
    }

    /**
     * Calcul de la magnitude.
     *
     * @param r Distance planète Soleil (Unité : UA).
     * @param p Distance planète Terre (Unité : UA).
     * @param F Phase.
     * @return la magnitude.
     */
    private float magnitude(double r, double p, double F) {
        return (float) (v_0 + 5 * log10(r * p / sqrt(F)));
    }

}
