package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.SiderealTime;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static ch.epfl.rigel.math.Angle.normalizePositive;
import static java.lang.Math.*;

/**
 * Transformateur de coordonnées équatoriales vers des coordonnées horizontales.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class EquatorialToHorizontalConversion
        implements Function<EquatorialCoordinates, HorizontalCoordinates> {

    private final double cosPhi, sinPhi;
    private final double Sl;

    /**
     * Construit un transformateur de coordonnées, équatoriales vers horizontales.
     *
     * @param when année, mois, jour, heure, fuseau horaire.
     * @param where l'endroit où se trouve le point considéré.
     */
    public EquatorialToHorizontalConversion(ZonedDateTime when, GeographicCoordinates where) {
        double phi = where.lat();
        cosPhi = cos(phi);
        sinPhi = sin(phi);

        Sl = SiderealTime.local(when, where);
    }

    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates equ) {

        double H = Sl - equ.ra();
        double cosH = cos(H);
        double sinH = sin(H);

        double delta = equ.dec();
        double cosDelta = cos(delta);
        double sinDelta = sin(delta);

        double h = height(sinDelta, cosDelta, cosH);
        double A = azimut(cosDelta, sinDelta, sin(h), sinH);

        return HorizontalCoordinates.of(A, h);
    }

    /**
     * Calcule la hauteur (Unité : radians).
     *
     * @return la hauteur (Unité : radians).
     */
    private double height(double sinDelta, double cosDelta, double cosH) {
        return asin(sinDelta * sinPhi + cosDelta * cosPhi * cosH);
    }

    /**
     * Calcule l'azimut (Unité : radians).
     *
     * @return l'azimut (Unité : radians).
     */
    private double azimut(double cosDelta, double sinDelta, double sinh, double sinH) {
        return normalizePositive(
                atan2(-cosDelta * cosPhi * sinH, sinDelta - sinPhi * sinh)
        );
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean equals(Object o) {
        throw new UnsupportedOperationException();
    }

}
