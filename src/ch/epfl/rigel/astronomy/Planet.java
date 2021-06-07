package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * Une Planète comme Objet Céleste.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class Planet extends CelestialObject {

    /**
     * Construit une nouvelle planète à une position fixe.
     *
     * @param name son nom.
     * @param equatorialPos sa position en coordonnées équatoriales.
     * @param angularSize sa taille angulaire (Unité : radians).
     * @param magnitude sa magnitude.
     */
    public Planet(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        super(name, equatorialPos, angularSize, magnitude);
    }
}
