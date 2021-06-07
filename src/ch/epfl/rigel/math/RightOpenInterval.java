package ch.epfl.rigel.math;

import static ch.epfl.rigel.Preconditions.checkArgument;

import java.util.Locale;

/**
 * Un interval ouvert à droite.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class RightOpenInterval extends Interval {

    /**
     * Construit un nouvel interval ouvert à droite.
     *
     * @param low la borne inférieure.
     * @param high la brone supérieure.
     */
    private RightOpenInterval(double low, double high) {
        super(low, high);
    }

    @Override
    public boolean contains(double v) {
        return v >= low() && v < high();
    }

    /**
     * Retourne un nouvel interval ouvert à droite.
     *
     * @param low le minimum de l'interval.
     * @param high le maximum de l'interval.
     * @throws IllegalArgumentException si le minimum n'est pas strictement
     *          plus petit que le maximum.
     * @return un nouvel interval ouvert à droite.
     */
    public static RightOpenInterval of(double low, double high) {
        checkArgument(low < high);
        return new RightOpenInterval(low, high);
    }

    /**
     * Retourne un nouvel interval ouvert à droite axé en 0.
     *
     * @param size la taille de l'interval.
     * @throws IllegalArgumentException si la taille est inférieur ou égale à 0.
     * @return un nouvel interval ouvert à droite axé en 0.
     */
    public static RightOpenInterval symmetric(double size) {
        checkArgument(size > 0);
        return new RightOpenInterval(-size / 2, size / 2);
    }

    /**
     * Retourne une nouvelle valeur qui correspond à celle-ci
     * projetée sur l'interval.
     *
     * @param v la valeur.
     * @return la valeur modulo sur cet interval.
     */
    public double reduce(double v) {
        double sum = floorMod(v - low(), size());
        if (sum < 0)
            return high() + sum;
        return low() + sum;
    }

    /**
     * Retourne le reste de la division.
     *
     * @param x le numérateur.
     * @param y le dénominateur.
     * @return le reste de la division.
     */
    private double floorMod(double x, double y) {
        return x % y;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%.2f;%.2f[", low(), high());
    }

}