package ch.epfl.rigel.astronomy;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Polynomial;

import static ch.epfl.rigel.math.Angle.normalizePositive;
import static ch.epfl.rigel.math.Angle.ofHr;

/**
 * Temps Sidéral.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class SiderealTime {

    private SiderealTime() {}

    public final static double MILLIS_PER_HOURS = Duration.ofHours(1).toMillis();
    public final static Duration SIDEREAL_DAY_DURATION
            = Duration.ofSeconds((long) Polynomial.of(23,56, 4).at(60));
    public final static Duration DAY_DURATION = Duration.ofHours(24);
    public final static int FREQUENCY_SEC_PER_MIN = 60;

    private final static double FACTOR_FOR_S1 = 1.002737909;
    private final static Polynomial EQUATION =
            Polynomial.of(0.000025862, 2400.051336, 6.697374558);

    /**
     * Méthode statique retournant le temps sidéral de Greenwich en radians.
     *
     * @param when année, mois, jour, heure, fuseau horaire.
     * @return un double représentant le temps sidéral de Greenwich (Unité : radians).
     */
    public static double greenwich(ZonedDateTime when) {
        ZonedDateTime zonedDateTime = when.withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime zonedDateTimeTruncated = zonedDateTime.truncatedTo(ChronoUnit.DAYS);

        double T = Epoch.J2000.julianCenturiesUntil(zonedDateTimeTruncated);
        double t = zonedDateTimeTruncated.until(zonedDateTime, ChronoUnit.MILLIS);
        t = t / MILLIS_PER_HOURS;

        double S0 = EQUATION.at(T);
        double S1 = FACTOR_FOR_S1 * t;

        double Sg = ofHr(S0 + S1);

        return normalizePositive(Sg);
    }

    /**
     * Méthode statique retournant le temps sidéral local (Unité : radians).
     *
     * @param when année, mois, jour, heure, fuseau horaire.
     * @param where l'endroit où se trouve le point considéré.
     * @return un double représentant le temps sidéral local (Unité : radians).
     */
    public static double local(ZonedDateTime when, GeographicCoordinates where) {
        double Sl = greenwich(when) + where.lon();
        return normalizePositive(Sl);
    }
}
