package ch.epfl.rigel.gui;

import static ch.epfl.rigel.astronomy.SiderealTime.*;
import static ch.epfl.rigel.gui.TimeAccelerator.*;

/**
 * Représentation d'un accélérateur de temps nommé.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public enum NamedTimeAccelerator {
    TIMES_1 ("1x" , TimeAccelerator.continuous(SPEED_ONE) ),
    TIMES_30 ("30x" , TimeAccelerator.continuous(SPEED_THIRTY) ),
    TIMES_300 ("300x" , TimeAccelerator.continuous(SPEED_THREE_HUNDRED) ),
    TIMES_3000 ("3000x" , TimeAccelerator.continuous(SPEED_THREE_THOUSAND) ),
    DAY("jour" , TimeAccelerator.discrete(FREQUENCY_SEC_PER_MIN, DAY_DURATION)),
    SIDEREAL_DAY("jour sideral", TimeAccelerator.discrete(FREQUENCY_SEC_PER_MIN, SIDEREAL_DAY_DURATION));

    private final String name;
    private final TimeAccelerator accelerator;

    /**
     * Caractéristiques des accélarateurs de temps.
     *
     * @param name nom de l'accélérateur.
     * @param accelerator accélérateur associé au nom.
     */
    NamedTimeAccelerator(String name, TimeAccelerator accelerator) {
        this.name = name;
        this.accelerator = accelerator;
    }

    /**
     * Retourne le nom de la paire.
     *
     * @return le nom de la paire.
     */
    public String getName() {
        return name;
    }

    /**
     * Retourne l'accélérateur de la paire.
     *
     * @return l'accélérateur de la paire
     */
    public TimeAccelerator getAccelerator() {
        return accelerator;
    }

    @Override
    public String toString(){
         return name;
     }
     
}