package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import static ch.epfl.rigel.astronomy.Moon.*;
import static ch.epfl.rigel.math.Angle.normalizePositive;
import static ch.epfl.rigel.math.Angle.ofDeg;
import static java.lang.Math.*;

/**
 * Définiton de la Lune.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public enum MoonModel implements CelestialObjectModel<Moon> {

    MOON(
            AVERAGE_LONGITUDE,
            AVERAGE_LONGITUDE_PERIGEE,
            LONGITUDE_ASCENDING_NODE,
            INCLINATION_ORBIT,
            ECCENTRICITY_ORBIT
    );

    private static final double THETA_0 = ofDeg(0.5181);

    /**
     * Longitude moyenne de la Lune (Unité : radians).
     */
    private final double l0;
    /**
     * Longitude moyenne de la Lune au périgée (Unité : radians).
     */
    private final double P0;
    /**
     * Longitude du noeud ascendant de la Lune (Unité : radians).
     */
    private final double N0;
    /**
     * Cosinus de l'inclinaison de l'orbite de la Lune (Unité : radians).
     */
    private final double cos_i;
    /**
     * Sinus de l'inclinaison de l'orbite de la Lune (Unité : radians).
     */
    private final double sin_i;
    /**
     * Excentricité de l'orbite de la Lune.
     */
    private final double e;

    /**
     * Caractéristiques de la Lune.
     *
     * @param l0 Longitude moyenne de la Lune (Unité : radians).
     * @param P0 Longitude moyenne de la Lune au périgée (Unité : radians).
     * @param N0 Longitude du noeud ascendant de la Lune (Unité : radians).
     * @param i Inclinaison de l'orbite de la Lune (Unité : radians).
     * @param e Excentricité de l'orbite de la Lune.
     */
    MoonModel(double l0, double P0, double N0, double i, double e) {
        this.l0 = ofDeg(l0);
        this.P0 = ofDeg(P0);
        this.N0 = ofDeg(N0);
        double rad_i = ofDeg(i);
        this.cos_i = cos(rad_i);
        this.sin_i = sin(rad_i);
        this.e = e;
    }

    @Override
    public Moon at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        double l = averageOrbitalLongitude(daysSinceJ2010);
        double Mm = meanAnomaly(l, daysSinceJ2010);

        Sun sun = SunModel.SUN.at(daysSinceJ2010, eclipticToEquatorialConversion);
        double lambdaO = sun.eclipticPos().lon();

        double MO = sun.meanAnomaly();
        double sinMO = sin(MO);

        double Ev = evection(l, lambdaO, Mm);
        double Ae = correctionOfTheAnnualEquation(sinMO);

        double Mm_prime = correctedAnomaly(Mm, Ev, Ae, correction3(sinMO));
        double Ec = correctionOfTheCenterEquation(Mm_prime);

        double l_prime = correctedOrbitalLongitude(l, Ev, Ec, Ae, correction4(Mm_prime));
        double l_sec = trueOrbitalLongitude(
                l_prime,
                variation(l_prime, lambdaO)
        );

        double N_prime = correctedLongitudeOfascendingNode(
                averageLongitudeOfascendingNode(daysSinceJ2010),
                sinMO
        );

        EclipticCoordinates eclipticPos = eclipticLongitude(l_sec, N_prime);
        EquatorialCoordinates equatorialPos = eclipticToEquatorialConversion.apply(eclipticPos);

        return new Moon(
                equatorialPos,
                angularSize(Mm_prime, Ec),
                0,
                phase(l_sec, lambdaO)
        );
    }

    /**
     * Retourne la longitude orbitale moyenne (Unité : radians).
     *
     * @param daysSinceJ2010 nombre de jours depuis J2010 (Unité : jour).
     * @return la longitude orbitale moyenne (Unité : radians).
     */
    private double averageOrbitalLongitude(double daysSinceJ2010) {
        return ofDeg(13.1763966) * daysSinceJ2010 + l0;
    }

    /**
     * Retourne l'anomalie moyenne (Unité : radians).
     *
     * @param l longitude orbitale moyenne (Unité : radians).
     * @param daysSinceJ2010 nombre de jours depuis J2010 (Unité : jour).
     * @return l'anomalie moyenne (Unité : radians).
     */
    private double meanAnomaly(double l, double daysSinceJ2010) {
        return l - ofDeg(0.1114041) * daysSinceJ2010 - P0;
    }

    /**
     * Retourne l'évection (Unité : radians).
     *
     * @param l longitude orbitale moyenne (Unité : radians).
     * @param lambdaO longitude écliptique géocentrique du Soleil (Unité : radians).
     * @param Mm anomalie moyenne (Unité : radians).
     * @return l'évection (Unité : radians).
     */
    private double evection(double l, double lambdaO, double Mm) {
        return ofDeg(1.2739) * sin(2 * (l - lambdaO) - Mm);
    }

    /**
     * Retourne la correction de l'équation annuelle (Unité : radians).
     *
     * @param sinMO sinus de l'anomalie moyenne du Soleil (Unité : radians).
     * @return la correction de l'équation annuelle (Unité : radians).
     */
    private double correctionOfTheAnnualEquation(double sinMO) {
        return ofDeg(0.1858) * sinMO;
    }

    /**
     * Retourne la correction 3 (Unité : radians).
     *
     * @param sinMO sinus de l'anomalie moyenne du Soleil (Unité : radians).
     * @return la correction 3 (Unité : radians).
     */
    private double correction3(double sinMO) {
        return ofDeg(0.37) * sinMO;
    }

    /**
     * Retourne l'anomalie corrigée (Unité : radians).
     *
     * @param Mm anomalie moyenne (Unité : radians).
     * @param Ev évection (Unité : radians).
     * @param Ae correction de l'équation annuelle (Unité : radians).
     * @param A3 correction 3 (Unité : radians).
     * @return l'anomalie corrigée (Unité : radians).
     */
    private double correctedAnomaly(double Mm, double Ev, double Ae, double A3) {
        return Mm + Ev - Ae - A3;
    }

    /**
     * Retourne la correction de l'équation du centre (Unité : radians).
     *
     * @param Mm_prime anomalie corrigée (Unité : radians).
     * @return la correction de l'équation du centre (Unité : radians).
     */
    private double correctionOfTheCenterEquation(double Mm_prime) {
        return ofDeg(6.2886) * sin(Mm_prime);
    }

    /**
     * Retourne la correction 4 (Unité : radians).
     *
     * @param Mm_prime anomalie moyenne (Unité : radians).
     * @return la correction 4 (Unité : radians).
     */
    private double correction4(double Mm_prime) {
        return ofDeg(0.214) * sin(2 * Mm_prime);
    }

    /**
     * Retourne la longitude orbitale corrigée (Unité : radians).
     *
     * @param l longitude orbitale moyenne (Unité : radians).
     * @param Ev évection (Unité : radians).
     * @param Ec correction de l'équation du centre (Unité : radians).
     * @param Ae correction de l'équation annuelle (Unité : radians).
     * @param A4 correction 4 (Unité : radians).
     * @return la longitude orbitale corrigée (Unité : radians).
     */
    private double correctedOrbitalLongitude(double l, double Ev, double Ec, double Ae, double A4) {
        return l + Ev + Ec - Ae + A4;
    }

    /**
     * Retourne la variation (Unité : radians).
     *
     * @param l_prime longitude orbitale corrigée (Unité : radians).
     * @param lambdaO longitude écliptique géocentrique du Soleil (Unité : radians).
     * @return la variation (Unité : radians).
     */
    private double variation(double l_prime, double lambdaO) {
        return ofDeg(0.6583) * sin(2 * (l_prime - lambdaO));
    }

    /**
     * Retourne la longitude orbitale vraie (Unité : radians).
     *
     * @param l_prime longitude orbitale corrigée (Unité : radians).
     * @param V variation (Unité : radians).
     * @return la longitude orbitale vraie (Unité : radians).
     */
    private double trueOrbitalLongitude(double l_prime, double V) {
        return l_prime + V;
    }

    /**
     * Retourne la longitude moyenne du nœud ascendant (Unité : radians).
     *
     * @param daysSinceJ2010 nombre de jours depuis J2010 (Unité : jour).
     * @return la longitude moyenne du nœud ascendant (Unité : radians).
     */
    private double averageLongitudeOfascendingNode(double daysSinceJ2010) {
        return N0 - ofDeg(0.0529539) * daysSinceJ2010;
    }

    /**
     * Retourne la longitude corrigée du nœud ascendant (Unité : radians).
     *
     * @param N longitude moyenne du nœud ascendant (Unité : radians).
     * @param sinMO sinus de l'anomalie moyenne du Soleil (Unité : radians).
     * @return la longitude corrigée du nœud ascendant (Unité : radians).
     */
    private double correctedLongitudeOfascendingNode(double N, double sinMO) {
        return N - ofDeg(0.16) * sinMO;
    }

    /**
     * Retourne la longitude écliptique (Unité : radians).
     *
     * @param l_sec longitude orbitale vraie (Unité : radians).
     * @param N_prime longitude corrigée du nœud ascendant (Unité : radians).
     * @return la longitude écliptique (Unité : radians).
     */
    private EclipticCoordinates eclipticLongitude(double l_sec, double N_prime) {
        double numerator = sin(l_sec - N_prime) * cos_i;
        double denominator = cos(l_sec - N_prime);

        double lambdaM = atan2(numerator, denominator) + N_prime;
        lambdaM = normalizePositive(lambdaM);
        double BetaM = asin(sin(l_sec - N_prime) * sin_i);

        return EclipticCoordinates.of(lambdaM, BetaM);
    }

    /**
     * Retourne la phase de la Lune.
     *
     * @param l_sec longitude orbitale vraie (Unité : radians).
     * @param lambdaO longitude écliptique géocentrique du Soleil (Unité : radians).
     * @return la phase de la Lune.
     */
    private float phase(double l_sec, double lambdaO) {
        return (float) (1 - cos(l_sec - lambdaO)) / 2.f;
    }

    /**
     * Retourne la taille angulaire (Unité : radians).
     *
     * @param Mm_prime anomalie corrigée (Unité : radians).
     * @param Ec correction de l'équation du centre (Unité : radians).
     * @return la taille angulaire (Unité : radians).
     */
    private float angularSize(double Mm_prime, double Ec) {
        double p = (1 - e*e) / (1 + e * cos(Mm_prime + Ec));
        return (float) (THETA_0 / p);
    }

}
