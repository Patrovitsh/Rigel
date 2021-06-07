package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import static ch.epfl.rigel.math.Angle.normalizePositive;
import static java.lang.Double.parseDouble;

public enum HygDatabaseLoader implements StarCatalogue.Loader {
    INSTANCE();

    private enum HygColumns{
        ID, HIP, HD, HR, GL, BF, PROPER, RA, DEC, DIST, PMRA, PMDEC,
        RV, MAG, ABSMAG, SPECT, CI, X, Y, Z, VX, VY, VZ,
        RARAD, DECRAD, PMRARAD, PMDECRAD, BAYER, FLAM, CON,
        COMP, COMP_PRIMARY, BASE, LUM, VAR, VAR_MIN, VAR_MAX;
    }

    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.US_ASCII));
             inputStream) {
            br.readLine();
            String line = br.readLine();

            String[] parts;
            Star star;
            String name;
            double rarad, decrad;

            while (line != null){
                parts = line.split(",");

                if (parts[HygColumns.PROPER.ordinal()].equals("")) {
                    name = parts[HygColumns.CON.ordinal()];
                    if (parts[HygColumns.BAYER.ordinal()].equals(""))
                        name = "? " + name;
                    else
                        name = parts[HygColumns.BAYER.ordinal()] + " " + name;
                } else
                    name = parts[HygColumns.PROPER.ordinal()];

                rarad = normalizePositive(
                        parseDouble(parts[HygColumns.RARAD.ordinal()])
                );
                decrad = parseDouble(parts[HygColumns.DECRAD.ordinal()]);

                star = new Star(
                        (int) conversionToNumbers(parts[HygColumns.HIP.ordinal()]),
                        name,
                        EquatorialCoordinates.of(rarad, decrad),
                        conversionToNumbers(parts[HygColumns.MAG.ordinal()]),
                        conversionToNumbers(parts[HygColumns.CI.ordinal()])
                );
                builder.addStar(star);

                line = br.readLine();
            }

        }

    }

    private float conversionToNumbers(String string) {
        if(string.equals(""))
            return 0;
        else
            return (float) parseDouble(string);
    }

}
