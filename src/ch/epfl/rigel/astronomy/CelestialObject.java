package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.util.Objects;

import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * Objets Célestes.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public abstract class CelestialObject {

    private final String name;
    private final EquatorialCoordinates equatorialPos;
    private final float angularSize, magnitude;

    /**
     * Construit un nouvel objet céleste à une position fixe.
     *
     * @param name son nom.
     * @param equatorialPos sa position en coordonnées équatoriales.
     * @param angularSize sa taille angulaire (Unité : radians).
     * @param magnitude sa magnitude.
     */
    CelestialObject(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        checkArgument(angularSize >= 0);

        this.name = Objects.requireNonNull(name);
        this.equatorialPos = Objects.requireNonNull(equatorialPos);
        this.angularSize = angularSize;
        this.magnitude = magnitude;
    }

    /**
     * Retourne le nom de l'objet céleste.
     *
     * @return le nom de l'objet céleste.
     */
    public String name() {
        return name;
    }

    /**
     * Retourne la position en coordonnées équatoriales de l'objet céleste.
     *
     * @return la position en coordonnées équatoriales de l'objet céleste.
     */
    public EquatorialCoordinates equatorialPos() {
        return equatorialPos;
    }

    /**
     * Retourne la taille angulaire de l'objet celeste (Unité : radians).
     *
     * @return la taille angulaire de l'objet celeste (Unité : radians).
     */
    public double angularSize() {
        return angularSize;
    }

    /**
     * Retourne la magnitude de l'objet céleste.
     *
     * @return la magnitude de l'objet céleste.
     */
    public double magnitude() {
        return magnitude;
    }

    /**
     * Retourne par défaut le nom de l'objet céleste mais peut être redéfini
     * pour donner plus d'informations.
     *
     * @return le nom de l'objet céleste.
     */
    public String info() {
        return name;
    }

    @Override
    public final String toString() {
        return info();
    }
}
