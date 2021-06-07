package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Chargeur de constellation.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public enum ConstellationDrawLoader implements StarCatalogue.Loader {
    INSTANCE();

    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.US_ASCII));
             inputStream) {

            String line = br.readLine();
            String[] parts;
            Constellation constellation;
            double deltaAngle, scaleFactor;

            while(line != null) {
                parts = line.split(",");

                deltaAngle = Double.parseDouble(parts[3]);
                scaleFactor = Double.parseDouble(parts[4]);

                constellation = new Constellation("/" + parts[0] + ".png", parts[1],
                        parts[2], deltaAngle, scaleFactor);
                builder.addConstellation(constellation);

                line = br.readLine();
            }
        }
    }
}
