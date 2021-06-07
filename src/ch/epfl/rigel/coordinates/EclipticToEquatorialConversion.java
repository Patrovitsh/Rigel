package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static ch.epfl.rigel.math.Angle.*;
import static java.lang.Math.*;

/**
 * Transformateur de coordonnées écliptiques vers des coordonnées équatoriales.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {

    private final static Polynomial EQUATION = Polynomial.of(
            ofArcsec(0.00181),
            ofArcsec(-0.0006),
            ofArcsec(-46.815),
            ofDMS(23, 26, 21.45)
    );

    private final double cosEpsilon, sinEpsilon;

    /**
     * Construit un transformateur de coordonnées, écliptiques vers équatoriales.
     *
     * @param when année, mois, jour, heure, fuseau horaire.
     */
    public EclipticToEquatorialConversion(ZonedDateTime when) {
        double T = Epoch.J2000.julianCenturiesUntil(when);

        double epsilon = EQUATION.at(T);

        cosEpsilon = cos(epsilon);
        sinEpsilon = sin(epsilon);
    }

    @Override
    public EquatorialCoordinates apply(EclipticCoordinates ecl) {

        double lambda = ecl.lon();
        double beta = ecl.lat();

        double cosLambda = cos(lambda);
        double sinLambda = sin(lambda);

        double cosBeta = cos(beta);
        double sinBeta = sin(beta);
        double tanBeta = tan(beta);

        return EquatorialCoordinates.of(
                rightAscension(sinLambda, cosLambda, tanBeta),
                declination(sinBeta, cosBeta, sinLambda)
        );
    }

    /**
     * Calcule l'ascencion droite (Unité : radians).
     *
     * @return l'ascension droite (Unité : radians).
     */
    private double rightAscension(double sinLambda, double cosLambda, double tanBeta) {
        return normalizePositive(
                atan2(sinLambda * cosEpsilon - tanBeta * sinEpsilon, cosLambda)
        );
    }

    /**
     * Calcule la déclinaison (Unité : radians).
     *
     * @return la déclinaison (Unité : radians).
     */
    private double declination(double sinBeta, double cosBeta, double sinLambda) {
        return asin(sinBeta * cosEpsilon + cosBeta * sinEpsilon * sinLambda);
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
