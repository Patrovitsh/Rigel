package ch.epfl.rigel.math;

import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * Un polynôme.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class Polynomial {

    private final double[] coefficients;

    /**
     * Construit un polynôme à partir des coefficients fournis en paramètres.
     *
     * @param coeffs les coefficients du polynôme dans l'ordre décroissant.
     */
    private Polynomial(double[] coeffs) {
        coefficients = coeffs;
    }

    /**
     * Retourne un polynôme construit avec les coefficients fournis.
     *
     * @param coefficientN le coefficient de puissance N.
     * @param coefficients les coefficients dans l'ordre décroissant.
     * @return le polynôme obtenus avec ces coefficients.
     */
    public static Polynomial of(double coefficientN, double... coefficients) {
        checkArgument(coefficientN != 0);

        int nbCoefficients = coefficients.length + 1;
        double[] listOfCoeffs = new double[nbCoefficients];

        listOfCoeffs[0] = coefficientN;
        for(int i = 1; i < nbCoefficients; ++i)
            listOfCoeffs[i] = coefficients[i-1];

        return new Polynomial(listOfCoeffs);
    }

    /**
     * Retourne un double représentant la valeur du polynôme évalué
     * en un nombre.
     *
     * @param x le nombre à évaluer.
     * @return un double représentant la valeur du polynôme évalué en x.
     */
    public double at(double x) {
        double summation = coefficients[0];

        for (int i = 1; i < coefficients.length; ++i)
            summation = summation * x + coefficients[i];

        return summation;
    }

    @Override
    public String toString() {
        StringBuilder polynomialToString = new StringBuilder();

        int nbCoeffs = coefficients.length, power = 0;
        double coeff = 0.0;

        for (int i = 0; i < nbCoeffs ; ++i) {
            coeff = coefficients[i];
            if (coeff == 0) continue;

            if (coeff != 1) {
                if (coeff == -1)
                    polynomialToString.append('-');
                else {
                    if(coeff > 0 && i != 0)
                        polynomialToString.append('+');
                    polynomialToString.append(coeff);
                }
            }

            if(i != nbCoeffs - 1)
                polynomialToString.append('x');

            power = nbCoeffs - i - 1;
            if(power > 1)
                polynomialToString.append("^").append(power);
        }

        return polynomialToString.substring(0);
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
