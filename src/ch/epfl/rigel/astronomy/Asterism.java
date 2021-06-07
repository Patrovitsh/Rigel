package ch.epfl.rigel.astronomy;

import java.util.List;

import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * Représentation d'un Astérisme.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */


public final class Asterism {

    private final List<Star> stars;

    /**
     * Construit un nouvel astérisme.
     *
     * @param stars liste des Étoiles qui appartiennent à l'astérisme.
     */
    public Asterism(List<Star> stars) {
        checkArgument(!stars.isEmpty());
        this.stars = List.copyOf(stars);
    }

    /**
     * Retourne la liste des Étoiles qui appartiennent à l'astérisme.
     *
     * @return la liste des Étoiles qui appartiennent à l'astérisme.
     */
    public List<Star> stars() {
        return stars;
    }
}
