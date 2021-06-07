package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.util.Objects;

/**
 * Le Soleil comme Objet Céleste.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class Sun extends CelestialObject {

    protected static final double LONGITUDE_SUN_J2010 = 279.557208;
    protected static final double LONGITUDE_SUN_PERIGEE = 283.112438;
    protected static final double ECCENTRICITY = 0.016705;
    protected static final double THETA_0 = 0.533128;

    private final EclipticCoordinates eclipticPos;
    private final float meanAnomaly;

    /**
     * Construit un nouveau Soleil à une position fixe.
     *
     * @param eclipticPos sa position en coordonnées écliptiques.
     * @param equatorialPos sa position en coordonnées équatoriales.
     * @param angularSize sa taille angulaire (Unité : radians).
     * @param meanAnomaly l'anomalie moyenne (Unité : radians).
     */
    public Sun(EclipticCoordinates eclipticPos, EquatorialCoordinates equatorialPos,
               float angularSize, float meanAnomaly) {

        super("Soleil", equatorialPos, angularSize, -26.7f);
        this.eclipticPos = Objects.requireNonNull(eclipticPos);
        this.meanAnomaly = meanAnomaly;
    }

    /**
     * Retourne la position du Soleil en coordonnées écliptiques.
     *
     * @return la position du Soleil en coordonnées écliptiques.
     */
    public EclipticCoordinates eclipticPos() {
        return eclipticPos;
    }

    /**
     * Retourne l'anomalie moyenne du Soleil (Unité : radians).
     *
     * @return l'anomalie moyenne du Soleil (Unité : radians).
     */
    public double meanAnomaly() {
        return meanAnomaly;
    }
}
