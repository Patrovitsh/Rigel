package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

/**
 * Interface d'un objet Céleste.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public interface CelestialObjectModel<O> {

    /**
     * Retourne un nouvel objet céleste à une position calculé en fonction de la date à laquelle on veut le voir.
     *
     * @param daysSinceJ2010 nombre de jours depuis la date J2010 (peut être négatif).
     * @param eclipticToEquatorialConversion transformateur de coordonnées écliptiques en coordonnées équatoriales.
     * @return un nouvel objet céleste de type 'O' à une position calculé en fonction de 'daysSinceJ2010'
     */
    public abstract O at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion);
}
