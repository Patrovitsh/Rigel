package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

import static ch.epfl.rigel.Preconditions.*;

/**
 * Représentation d'une Étoile.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class Star extends CelestialObject {

    private static final ClosedInterval INTERVAL_COLOR_INDEX = ClosedInterval.of(-0.5, 5.5);

    private final int hipparcosId;
    private final int colorTemperature;

    /**
     * Construit une nouvelle Étoile.
     *
     * @param hipparcosId le numéro Hipparcos de l'Étoile.
     * @param name le nom de l'Étoile.
     * @param equatorialPos la position en coordonnées équatoriales de l'Étoile.
     * @param magnitude la magnitude de l'Étoile.
     * @param colorIndex l'index de la couleur de l'Étoile.
     */
    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos, float magnitude, float colorIndex) {
        super(name, equatorialPos, 0, magnitude);

        checkArgument(hipparcosId >= 0);
        this.hipparcosId = hipparcosId;

        checkInInterval(INTERVAL_COLOR_INDEX, colorIndex);
        double stepMultiplication = 0.92 * colorIndex;
        this.colorTemperature = (int) (4600 * (1 / (stepMultiplication + 1.7) + 1 / (stepMultiplication + 0.62)));
    }

    /**
     * Retourne le numéro Hipparcos de l'Étoile.
     *
     * @return le numéro Hipparcos de l'Étoile.
     */
    public int hipparcosId() {
        return hipparcosId;
    }

    /**
     * Calcul la température de l'Étoile à partir de son code couleur (Unité : Kelvin).
     *
     * @return la température de l'Étoile (Unité : Kelvin).
     */
    public int colorTemperature() {
        return colorTemperature;
    }
}
