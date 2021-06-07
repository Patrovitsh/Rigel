package ch.epfl.rigel.gui;

import javafx.animation.AnimationTimer;
import javafx.beans.property.*;

import java.time.ZonedDateTime;

/**
 * Animateur de temps.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class TimeAnimator extends AnimationTimer {

    private long timeStart;
    private final ObjectProperty<TimeAccelerator> accelerator;
    private final SimpleBooleanProperty running;
    private final DateTimeBean dateTimeBean;
    private ZonedDateTime simulatedStart;
    private final DoubleProperty accelerationValue;
    private final BooleanProperty personalAcceleration;

    /**
     * Construit un nouvel animateur de temps.
     *
     * @param dateTimeBean instant d'observation.
     */
    public TimeAnimator(DateTimeBean dateTimeBean) {
        this.dateTimeBean = dateTimeBean;
        accelerator = new SimpleObjectProperty<>();
        running = new SimpleBooleanProperty(false);
        timeStart = 0;
        accelerationValue = new SimpleDoubleProperty(300);
        personalAcceleration = new SimpleBooleanProperty(false);
    }

    @Override
    public void handle(long now) {
        if(timeStart == 0) timeStart = now;
        long timePassed = now - timeStart;

        ZonedDateTime adjustDateTime;

        if(personalAcceleration.getValue()) {
            adjustDateTime = TimeAccelerator
                    .continuous((int) accelerationValue.doubleValue())
                    .adjust(simulatedStart, timePassed);
        } else
            adjustDateTime = accelerator.get().adjust(simulatedStart, timePassed);

        dateTimeBean.setZonedDateTime(adjustDateTime);
    }

    @Override
    public void start() {
        simulatedStart = dateTimeBean.getZonedDateTime();
        running.setValue(true);
        super.start();
    }

    @Override 
    public void stop() {
        running.setValue(false);
        timeStart = 0;
        super.stop();
    }

    /**
     *
     */
    public void resetSimulationTime() {
        simulatedStart = dateTimeBean.getZonedDateTime();
        timeStart = 0;
    }

    /**
     * Retourne un accès à la propriété TimeAccelerator.
     *
     * @return un accès à la propriété TimeAccelerator.
     */
    public ObjectProperty<TimeAccelerator> acceleratorProperty(){
        return accelerator;
    }

    /**
     * Retourne un accès au contenu de la propriété TimeAccelerator.
     *
     * @return un accès au contenu de la propriété TimeAccelerator.
     */
    public TimeAccelerator getAccelerator() {
        return accelerator.get();
    }

    /**
     * Permet de modifier le contenu de la propriété TimeAccelerator.
     *
     * @param accelerator propriété TimeAccelerator.
     */
    public void setAccelerator(TimeAccelerator accelerator) {
        this.accelerator.set(accelerator);
    }

    /**
     * Retourne la propriété running non modifiable de l'extérieur.
     *
     * @return la propriété running non modifiable de l'extérieur.
     */
    public ReadOnlyBooleanProperty getRunning() {
        return running;
    }

    /**
     * Retourne un accès à la propriété accelerationValue qui contient la
     * valeur de l'accélération personnalisée à appliquer.
     *
     * @return un accès à la propriété accelerationValue.
     */
    public DoubleProperty accelerationValueProperty() {
        return accelerationValue;
    }

    /**
     * Retourne un accès à la propriété personalAcceleration qui définit
     * si on utilise une accélération du temps personnalisée ou non.
     *
     * @return un accès à la propriété accelerationValue.
     */
    public BooleanProperty personalAccelerationProperty() {
        return personalAcceleration;
    }

    /**
     * Retourne true si on utilise une accélration personnalisé, false sinon.
     *
     * @return le contenu de la proprété personalAcceleration.
     */
    public boolean getPersonalAcceleration() {
        return personalAcceleration.get();
    }

}
