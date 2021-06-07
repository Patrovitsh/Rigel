package ch.epfl.rigel.gui;

import java.time.Duration;
import java.time.ZonedDateTime;


/**
 * Accélération du temps.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
@FunctionalInterface
public interface TimeAccelerator {

   int SPEED_ONE = 1;
   int SPEED_THIRTY = 30;
   int SPEED_THREE_HUNDRED = 300;
   int SPEED_THREE_THOUSAND = 3000;

   /**
    * Retourne le temps simulé.
    *
    * @param T0 temps simulé intial.
    * @param deltaT temps réel écoulé depuis le début de l'animation (Unité : nano-secondes).
    * @return le temps simulé.
    */
   ZonedDateTime adjust(ZonedDateTime T0, long deltaT);

   /**
    * Retourne un accélérateur continu.
    *
    * @param alpha facteur d'accélération.
    * @return un accélérateur continu.
    */
   static TimeAccelerator continuous(int alpha) {
      return (T0, deltaT) ->  T0.plusNanos(alpha * deltaT);
   }

   /**
    * Retourne un accélérateur discret.
    *
    * @param v fréquence d'avancement.
    * @param S pas.
    * @return un accélérateur discret.
    */
   static TimeAccelerator discrete(int v, Duration S) {
      return (T0, deltaT) -> T0.plusNanos((int)(v * deltaT * 1e-9) * S.toNanos());
   }
}
