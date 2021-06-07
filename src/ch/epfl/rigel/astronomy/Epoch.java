package ch.epfl.rigel.astronomy;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * Définitons des époques.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public enum Epoch {
    J2000 (ZonedDateTime.of(
            LocalDate.of(2000, Month.JANUARY, 1),
            LocalTime.NOON,
            ZoneOffset.UTC)
    ),
    J2010 (ZonedDateTime.of(
            LocalDate.of(2010, Month.JANUARY, 1).minusDays(1),
            LocalTime.MIDNIGHT,
            ZoneOffset.UTC)
    );

    Epoch(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }

    /**
     * Nombre de jour dans un siècle Julien.
     */
    private static final double JULIENS_CENTURY = 36525.0;
    /**
     * Nombre de milli-secondes par jour.
     */
    private static final double MILLIS_PER_DAY = Duration.ofDays(1).toMillis();

    private final ZonedDateTime zonedDateTime;

    /**
     * Retourne le nombre de jour qui s'est écoulé depuis cette époque (this).
     *
     * @param when année, mois, jour, heure, fuseau horaire.
     * @return un double représentant le nombre de jour qui s'est écoulé depuis
     *          cette époque (this).
     */
    public double daysUntil(ZonedDateTime when) {
        return zonedDateTime.until(when, ChronoUnit.MILLIS) / MILLIS_PER_DAY;
    }

    /**
     * Retourne le nombre de siècles Julien qui s'est écoulé depuis cette époque (this).
     *
     * @param when année, mois, jour, heure, fuseau horaire.
     * @return un double représentant le nombre de siècles Julien qui s'est écoulé
     *          depuis cette époque (this).
     */
    public double julianCenturiesUntil(ZonedDateTime when) {
        return daysUntil(when) / JULIENS_CENTURY;
    }
}
