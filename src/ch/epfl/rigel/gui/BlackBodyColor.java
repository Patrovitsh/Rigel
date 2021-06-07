package ch.epfl.rigel.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.scene.paint.Color;

import static javafx.scene.paint.Color.web;

/**
 * Couleur des étoiles.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public class BlackBodyColor {

    private static final String BBR_COLOR_NAME = "/bbr_color.txt";
    private static final ClosedInterval VALID_TEMPERATURE = ClosedInterval.of(1000, 40000);

    private static Map<Integer, Color> colorsLinkToTemperature;

    private BlackBodyColor() {}

    /**
     * Retourne la couleur correspondant à la température fournie.
     *
     * @param temperature température dont on souhaite obtenir la couleur.
     * @return la couleur correspondant à la température fournie.
     * @throws UncheckedIOException si l'inputStream n'est pas valide.
     */
    public static Color colorForTemperature(double temperature) {
        if(colorsLinkToTemperature == null) {
            try (InputStream bbrColorStream = BlackBodyColor.class.getResourceAsStream(BBR_COLOR_NAME)) {
                initMap(bbrColorStream);
            }
            catch(IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        int roundedTemp = rounded100K(
                Preconditions.checkInInterval(VALID_TEMPERATURE, temperature)
        );
        return colorsLinkToTemperature.get(roundedTemp);
    }

    /**
     * Retourne la température arrondie à 100 Kalvin.
     *
     * @param temperature température à arrondir.
     * @return la température arrondie à 100 Kalvin.
     */
    private static int rounded100K(double temperature) {
        if(temperature % 100 >= 50)
            return truncated100K(temperature) + 100;
        return truncated100K(temperature);
    }

    /**
     * Retourne la température tronquée à 100 Kalvin.
     *
     * @param temperature température à tronquer.
     * @return la température tronquée à 100 Kalvin.
     */
    private static int truncated100K(double temperature) {
        int temp = (int) temperature;
        return ((temp / 100 ) * 100);
    }

    /**
     * Initialise la map qui lie les températures à leur couleur.
     *
     * @param inputStream liste des températures avec la couleur coresspondante.
     * @throws IOException si l'inputStream n'est pas valide.
     */
    private static void initMap(InputStream inputStream) throws IOException {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            inputStream)
        {
            colorsLinkToTemperature = new HashMap<>();

            String line = br.readLine();
            String stringTemp;
            int currentTemp;

            while(line != null) {

                if(interistingLine(line)) {
                    stringTemp = line.substring(1, 6).stripLeading();
                    currentTemp = Integer.parseInt(stringTemp);
                    colorsLinkToTemperature.put(currentTemp, web(line.substring(80, 87)));
                }
                line = br.readLine();
            }
        }
    }

    /**
     * Retourne True si la ligne 'line' contient une couleur qui nous intéresse
     * pour ce projet, et retourne False sinon.
     *
     * @param line ligne de type String.
     * @return True si line contient une couleur intéressante, False sinon.
     */
    private static boolean interistingLine(String line) {
        boolean noHtag = line.charAt(0) != '#';
        return noHtag && line.substring(10, 15).equals("10deg");
    }

}
