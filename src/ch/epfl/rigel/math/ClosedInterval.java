package ch.epfl.rigel.math;

import static ch.epfl.rigel.Preconditions.checkArgument;

import java.util.Locale;

/**
 * Un interval fermé.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class ClosedInterval extends Interval {

    /**
     * Construit un nouvel interval fermé.
     * @param low la borne inférieure.
     * @param high la brone supérieure.
     */
    private ClosedInterval(double low, double high) {
        super(low, high);
    }

    @Override
    public boolean contains(double v) {
        return v >= low() && v <= high();
    }

    /**
     * Retourne un nouvel interval fermé.
     *
     * @param low le minimum de l'interval.
     * @param high le maximum de l'interval.
     * @throws IllegalArgumentException si le minimum n'est pas strictement
     *          plus petit que le maximum.
     * @return un nouvel interval fermé.
     */
    public static ClosedInterval of(double low, double high) {
        checkArgument(low < high);
        return new ClosedInterval(low, high);
    }

    /**
     * Retourne un nouvel interval fermé axé en 0.
     *
     * @param size la taille de l'interval.
     * @throws IllegalArgumentException si la taille est inférieur ou égale à 0.
     * @return un nouvel interval fermé axé en 0.
     */
    public static ClosedInterval symmetric(double size) {
        checkArgument(size > 0);
        return new ClosedInterval(-size/2, size/2);
    }

    /**
     * Retourne la valeur encrêté.
     *
     * @param v la valeur.
     * @return la valeur encrêté.
     */
    public double clip(double v) {
        if(v < low())
            return low();
        else if (v > high())
            return high();
        else
            return v;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%.2f;%.2f]", low(), high());
    }

}
