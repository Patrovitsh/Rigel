package ch.epfl.rigel;

import ch.epfl.rigel.math.Interval;

/**
 * Les préconditions du projet.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class Preconditions {

    private Preconditions() {}

    /**
     * Check le booléen entré en paramètre.
     *
     * @param isTrue le booléen à évaluer.
     * @throws IllegalArgumentException si le paramètre isTrue est faux.
     */
    public static void checkArgument(boolean isTrue) {
        if (!isTrue)
            throw new IllegalArgumentException();
    }

    /**
     * Retourne la valeur entrée en paramètre si elle est contenue dans
     * l'interval.
     *
     * @param interval l'interval.
     * @param value la valeur dont on doit tester l'appartenance à l'interval.
     * @throws IllegalArgumentException si value n'appartient pas à l'interval.
     * @return 'value'.
     */
    public static double checkInInterval(Interval interval, double value) {
        if(interval.contains(value))
            return value;
        throw new IllegalArgumentException("La valeur " + value + " n'appartient pas à " + interval.toString());
    }

}
