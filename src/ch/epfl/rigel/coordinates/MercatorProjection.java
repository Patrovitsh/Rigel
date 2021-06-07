package ch.epfl.rigel.coordinates;

import java.util.function.Function;

import ch.epfl.rigel.math.TrigoFunctions;

import static ch.epfl.rigel.coordinates.SphericalCoordinates.INTERVAL_LAT_SYMMETRIC_DEG;
import static ch.epfl.rigel.coordinates.SphericalCoordinates.INTERVAL_LON_SYMMETRIC_DEG;
import static ch.epfl.rigel.math.Angle.toDeg;
import static ch.epfl.rigel.math.Angle.TAU;
import static java.lang.Math.*;

/**
 * Projection Mercator.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class MercatorProjection implements Function<GeographicCoordinates, OSMCoordinates> {

    private final double f;
    private final int zoom;

    /**
     * Constructeur d'un nouveau système de projection mercator.
     *
     * @param zoom niveau de zoom.
     */
    public MercatorProjection(int zoom) {
        this.zoom = zoom;
        f = powerOfTwo(zoom + 8) / TAU;
    }
    
    @Override
    public OSMCoordinates apply(GeographicCoordinates geoCor) {
        double x = f * (geoCor.lon() + PI);
        double y = f * (PI - TrigoFunctions.asinh(tan(geoCor.lat())));
        CartesianCoordinates carCor = CartesianCoordinates.of(x, y);

        return OSMCoordinates.of(zoom, carCor);
    }

    /**
     * Retourne la projection des coordonnées OSM en coordonnées géographiques.
     *
     * @param osmCor coordonnées OSM.
     * @return la projection des coordonnées OSM en coordonnées géographiques.
     */
    public static GeographicCoordinates inverseApply(OSMCoordinates osmCor) {
        int zoom = osmCor.zoom();
        int s = powerOfTwo(zoom + 8);

        double f = (TAU / s);
        double lon = f * osmCor.x() - PI;
        double lat = atan(sinh(PI - f * osmCor.y()));

        double lonDeg = INTERVAL_LON_SYMMETRIC_DEG.reduce(toDeg(lon));
        double latDeg = INTERVAL_LAT_SYMMETRIC_DEG.clip(toDeg(lat));

        return GeographicCoordinates.ofDeg(lonDeg, latDeg);
    }

    /**
     * Retourne la puissance de deux de power.
     *
     * @param power puissance de deux.
     * @return la puissance de deux de power.
     */
    private static int powerOfTwo(int power) {
       int s = 1;
       for (int i = 0; i < power; i++) {
           s = s*2;
       }
       return s;   
    }
    
}
