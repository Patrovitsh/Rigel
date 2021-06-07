package ch.epfl.rigel.gui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Bean JavaFX contenant l'instant d'observation.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class DateTimeBean {

    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> time = new SimpleObjectProperty<>();
    private final ObjectProperty<ZoneId> zone = new SimpleObjectProperty<>();

    /**
     * Retourne un accès à la propriété date.
     *
     * @return un accès à la propriété date.
     */
    public ObjectProperty<LocalDate> dateProperty(){
        return date;
    }

    /**
     * Retourne un accès au contenu de la propriété date.
     *
     * @return un accès au contenu de la propriété date.
     */
    public LocalDate getDate() {
        return date.get();
    }

    /**
     * Permet de modifier le contenu de la propriété date.
     *
     * @param date date.
     */
    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    /**
     * Retourne un accès à la propriété temps.
     *
     * @return un accès à la propriété temps.
     */
    public ObjectProperty<LocalTime> timeProperty(){
        return time;
    }

    /**
     * Retourne un accès au contenu de la propriété temps.
     *
     * @return un accès au contenu de la propriété temps.
     */
    public LocalTime getTime() {
        return time.get();
    }

    /**
     * Permet de modifier le contenu de la propriété temps.
     *
     * @param time temps.
     */
    public void setTime(LocalTime time) {
        this.time.set(time);
    }

    /**
     * Retourne un accès à la propriété zone.
     *
     * @return un accès à la propriété zone.
     */
    public ObjectProperty<ZoneId> zoneProperty(){
        return zone;
    }

    /**
     * Retourne un accès au contenu de la propriété zone.
     *
     * @return un accès au contenu de la propriété zone.
     */
    public ZoneId getZone() {
        return zone.get();
    }

    /**
     * Permet de modifier le contenu de la propriété zone.
     *
     * @param zone zone.
     */
    public void setZone(ZoneId zone) {
        this.zone.set(zone);
    }

    /**
     * Retourne l'instant d'observation.
     *
     * @return l'instant d'observation sous la forme d'une valeur de type ZonedDateTime.
     */
    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.of(getDate(), getTime(), getZone());
    }

    /**
     * Modifie l'instant d'observation pour qu'il soit égal à la valeur de type
     * ZonedDateTime qu'on lui passe en argument.
     *
     * @param observationInstant instant d'observation.
     */
    public void setZonedDateTime(ZonedDateTime observationInstant) {
        setDate(observationInstant.toLocalDate());
        setTime(observationInstant.toLocalTime());
        setZone(observationInstant.getZone());
    }
}
