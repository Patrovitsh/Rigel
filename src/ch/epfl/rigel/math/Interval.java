package ch.epfl.rigel.math;

/**
 * Un interval.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public abstract class Interval {

    private final double low;
    private final double high;

    /**
     * Construit un nouvel interval.
     *
     * @param low la borne inférieur de l'interval.
     * @param high la borne supérieur de l'interval.
     */
    protected Interval(double low, double high) {
        this.low = low;
        this.high = high;
    }

    /**
     * Retourne la borne inférieur de l'interval.
     *
     * @return la borne inférieur de l'interval.
     */
    public double low() {
        return low;
    }

    /**
     * Retourne la borne supérieur de l'interval.
     *
     * @return la borne supérieur de l'interval.
     */
    public double high() {
        return high;
    }

    /**
     * Retourne la taille de l'interval.
     *
     * @return la taille de l'interval.
     */
    public double size() {
        return high - low;
    }

    /**
     * Test si cette valeur est contenue dans l'interval.
     *
     * @param v la valeur à tester.
     * @return true si cette valeur est contenue dans l'interval.
     */
    public abstract boolean contains(double v);

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean equals(Object o) {
        throw new UnsupportedOperationException();
    }
}
