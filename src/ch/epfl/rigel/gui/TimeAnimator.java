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
     * Retourne un acc??s ?? la propri??t?? TimeAccelerator.
     *
     * @return un acc??s ?? la propri??t?? TimeAccelerator.
     */
    public ObjectProperty<TimeAccelerator> acceleratorProperty(){
        return accelerator;
    }

    /**
     * Retourne un acc??s au contenu de la propri??t?? TimeAccelerator.
     *
     * @return un acc??s au contenu de la propri??t?? TimeAccelerator.
     */
    public TimeAccelerator getAccelerator() {
        return accelerator.get();
    }

    /**
     * Permet de modifier le contenu de la propri??t?? TimeAccelerator.
     *
     * @param accelerator propri??t?? TimeAccelerator.
     */
    public void setAccelerator(TimeAccelerator accelerator) {
        this.accelerator.set(accelerator);
    }

    /**
     * Retourne la propri??t?? running non modifiable de l'ext??rieur.
     *
     * @return la propri??t?? running non modifiable de l'ext??rieur.
     */
    public ReadOnlyBooleanProperty getRunning() {
        return running;
    }

    /**
     * Retourne un acc??s ?? la propri??t?? accelerationValue qui contient la
     * valeur de l'acc??l??ration personnalis??e ?? appliquer.
     *
     * @return un acc??s ?? la propri??t?? accelerationValue.
     */
    public DoubleProperty accelerationValueProperty() {
        return accelerationValue;
    }

    /**
     * Retourne un acc??s ?? la propri??t?? personalAcceleration qui d??finit
     * si on utilise une acc??l??ration du temps personnalis??e ou non.
     *
     * @return un acc??s ?? la propri??t?? accelerationValue.
     */
    public BooleanProperty personalAccelerationProperty() {
        return personalAcceleration;
    }

    /**
     * Retourne true si on utilise une acc??lration personnalis??, false sinon.
     *
     * @return le contenu de la propr??t?? personalAcceleration.
     */
    public boolean getPersonalAcceleration() {
        return personalAcceleration.get();
    }

}
