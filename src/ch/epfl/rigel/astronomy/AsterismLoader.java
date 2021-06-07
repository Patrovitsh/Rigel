package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chargeur d'astérismes.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public enum AsterismLoader implements StarCatalogue.Loader {
    INSTANCE();
    
    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.US_ASCII));
             inputStream) {

            Map<Integer, Star> hippOfStars = new HashMap<>();
            List<Star> starsBuilder = builder.stars();

            for(Star star : starsBuilder) {
                hippOfStars.put(star.hipparcosId(), star);
            }

            String line = br.readLine();
            String[] parts;

            List<Star> starsInAsterisms = new ArrayList<>();
            int hipparcos;

            while(line != null) {
                starsInAsterisms.clear();

                parts = line.split(",");

                for (String hipp : parts) {
                    hipparcos = Integer.parseInt(hipp);
                    starsInAsterisms.add(hippOfStars.get(hipparcos));
                }

                builder.addAsterism(new Asterism(starsInAsterisms));

                line = br.readLine();
            }
        }
    }

}