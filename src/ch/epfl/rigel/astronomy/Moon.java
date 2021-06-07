package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

import java.util.Locale;

import static ch.epfl.rigel.Preconditions.checkInInterval;

/**
 * La Lune comme Objet Céleste.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class Moon extends CelestialObject {

    public static final double AVERAGE_LONGITUDE = 91.929336;
    public static final double AVERAGE_LONGITUDE_PERIGEE = 130.143076;
    public static final double LONGITUDE_ASCENDING_NODE = 291.682547;
    public static final double INCLINATION_ORBIT = 5.145396;
    public static final double ECCENTRICITY_ORBIT = 0.0549;

    private static final ClosedInterval INTERVAL_PHASE = ClosedInterval.of(0, 1);
    private final float phase;

    /**
     * Construit une nouvelle Lune à une position fixe.
     *
     * @param equatorialPos sa position en coordonnées équatoriales.
     * @param angularSize sa taille angulaire (Unité : radians).
     * @param magnitude sa magnitude.
     * @param phase sa phase (compris entre 0 et 1).
     */
    public Moon(EquatorialCoordinates equatorialPos, float angularSize, float magnitude, float phase) {
        super("Lune", equatorialPos, angularSize, magnitude);
        this.phase = (float) checkInInterval(INTERVAL_PHASE, phase);
    }

    @Override
    public String info() {
        return String.format(Locale.ROOT, name() + " (%.1f%%)", phase * 100);
    }
}
