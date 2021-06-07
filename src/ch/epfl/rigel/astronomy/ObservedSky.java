package ch.epfl.rigel.astronomy;

import java.time.ZonedDateTime;
import java.util.*;

import ch.epfl.rigel.coordinates.*;

import static ch.epfl.rigel.astronomy.StarCatalogue.SUN_NAME;
import static ch.epfl.rigel.math.TrigoFunctions.distanceSquare;

/**
 * Représentation du ciel observé.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public class ObservedSky {

    private final StarCatalogue catalogue;
    private final StereographicProjection projection;
    private final Sun sun;
    private final Moon moon;
    private final ArrayList<Planet> planets;
    private final ArrayList<Star> stars;
    private final CartesianCoordinates sunPosition, moonPosition;
    private final HorizontalCoordinates sunPosHorCoords;
    private final double[] planetsPositions, starsPositions;
    private final EclipticToEquatorialConversion eclipticToEquatorial;
    private final EquatorialToHorizontalConversion equatorialToHorizontal;
    private final double daysSinceJ2010;

    /**
     * Construit une nouvelle observation du ciel.
     *
     * @param when année, mois, jour, heure, fuseau horaire.
     * @param where position d'observation.
     * @param projection projection stéréographique à utiliser.
     * @param catalogue catalogue contenant les étoiles et les astérismes.
     */
    public ObservedSky(ZonedDateTime when, GeographicCoordinates where,
                       StereographicProjection projection, StarCatalogue catalogue)
    {
        this.catalogue = catalogue;
        this.projection = projection;

        eclipticToEquatorial = new EclipticToEquatorialConversion(when);
        equatorialToHorizontal = new EquatorialToHorizontalConversion(when, where);
        daysSinceJ2010 = Epoch.J2010.daysUntil(when);

        // Pour le Soleil
        sun = SunModel.SUN.at(daysSinceJ2010, eclipticToEquatorial);
        sunPosHorCoords = equatorialToHorizontal.apply(sun.equatorialPos());
        sunPosition = projection.apply(sunPosHorCoords);

        // Pour la Lune
        moon = MoonModel.MOON.at(daysSinceJ2010, eclipticToEquatorial);
        moonPosition = projection.apply(equatorialToHorizontal.apply(moon.equatorialPos()));

        // Pour les planètes
        int size = (PlanetModel.values().length - 1) * 2;
        planetsPositions = new double[size];

        Planet currentPlanet;
        CartesianCoordinates currentPlanetPosition;

        planets = new ArrayList<>();
        int count = 0;

        for(PlanetModel planet : PlanetModel.values())
        {
            if (planet == PlanetModel.EARTH)
                continue;

            currentPlanet = planet.at(daysSinceJ2010, eclipticToEquatorial);
            planets.add(currentPlanet);
            currentPlanetPosition = projection
                    .apply(equatorialToHorizontal.apply(currentPlanet.equatorialPos()));
            planetsPositions[count] = currentPlanetPosition.x();
            planetsPositions[count+1] = currentPlanetPosition.y();
            count += 2;
        }

        // Pour les étoiles
        List<Star> starsCatalogue = catalogue.stars();
        size = starsCatalogue.size() * 2;
        starsPositions = new double[size];

        stars = new ArrayList<>();
        CartesianCoordinates currentStarPosition;
        count = 0;

        for(Star currentStar : starsCatalogue)
        {
            stars.add(currentStar);
            currentStarPosition = projection
                    .apply(equatorialToHorizontal.apply(currentStar.equatorialPos()));
            starsPositions[count] = currentStarPosition.x();
            starsPositions[count + 1] = currentStarPosition.y();
            count += 2;
        }

    }

    /**
     * Retourne les coordonnées horizontales de l'objet céleste.
     *
     * @param name nom de l'objet céleste.
     * @return les coordonnées horizontales de l'objet céleste.
     */
    public HorizontalCoordinates getHorCoordsCelestialObject(String name) {
        HorizontalCoordinates horizontalCoordinates;

        if(catalogue.isStarPresent(name)) {
            Star star = catalogue.getStar(name);
            horizontalCoordinates = equatorialToHorizontal.apply(star.equatorialPos());

        } else if(catalogue.isPlanetPresent(name)) {
            Planet planet = catalogue.getPlanet(name)
                    .at(daysSinceJ2010, eclipticToEquatorial);
            horizontalCoordinates = equatorialToHorizontal.apply(planet.equatorialPos());

        } else if(name.equals(SUN_NAME)) {
            Sun sun = SunModel.SUN.at(daysSinceJ2010, eclipticToEquatorial);
            horizontalCoordinates = equatorialToHorizontal.apply(sun.equatorialPos());

        } else  {
            Moon moon = MoonModel.MOON.at(daysSinceJ2010, eclipticToEquatorial);
            horizontalCoordinates = equatorialToHorizontal.apply(moon.equatorialPos());
        }
        return horizontalCoordinates;
    }

    /**
     * Retourne les coordonnées cartésiennes de l'objet céleste.
     *
     * @param name nom de l'objet céleste.
     * @return les coordonnées cartésiennes de l'objet céleste.
     */
    public CartesianCoordinates getCartesiansCoordsCelestial(String name) {
        return projection.apply(getHorCoordsCelestialObject(name));
    }

    /**
     * Retourne le Soleil.
     *
     * @return le Soleil sous la forme d'une instance de Sun.
     */
    public Sun sun() {
        return sun;
    }

    /**
     * Retourne la position du Soleil dans le plan.
     *
     * @return la position du Soleil dans le plan, sous la forme d'une
     *          instance de CartesianCoordinates.
     */
    public CartesianCoordinates sunPositon() {
        return sunPosition;
    }

    /**
     * Retourne la position du Soleil en coordonnées horizontales.
     *
     * @return la positions du Soleil en coordonnées horizontales.
     */
    public HorizontalCoordinates sunPosHorizontalCoords() {
        return sunPosHorCoords;
    }

    /**
     * Retourne la Lune.
     *
     * @return la Lune sous la forme d'une instance de Moon.
     */
    public Moon moon() {
        return moon;
    }

    /**
     * Retourne la position due la Lune dans le plan.
     *
     * @return la position de la Lune dans le plan, sous la forme d'une
     *          instance de CartesianCoordinates.
     */
    public CartesianCoordinates moonPositon() {
        return moonPosition;
    }

    /**
     * Retourne la liste des sept planètes extraterrestres du système solaire.
     *
     * @return la liste des sept planètes extraterrestres du système solaire,
     *          sous la forme d'une List de Planet.
     */
    public List<Planet> planets(){
        return List.copyOf(planets);
    }

    /**
     * Retourne les coordonnées cartésiennes des sept planètes extraterrestres.
     *
     * @return les coordonnées cartésiennes des sept planètes extraterrestres dans
     *          un tableau de double.
     */
    public double[] planetsPositions(){
        return planetsPositions.clone();
    }

    /**
     * Retourne la liste des étoiles.
     *
     * @return la liste des étoiles, sous la forme d'une List de Star.
     */
    public List<Star> stars(){
        return List.copyOf(stars);
    }

    /**
     * Retourne les coordonnées cartésiennes des étoiles.
     *
     * @return les coordonnées cartésiennes des étoiles dans un tableau de double.
     */
    public double[] starsPositions(){
        return starsPositions.clone();
    }

    /**
     * Retourne la liste des astérismes.
     *
     * @return la liste des astérismes, sous la forme d'une List d'Asterism.
     */
    public List<Asterism> asterisms(){
        return List.copyOf(catalogue.asterisms());
    }

    /**
     * Retourne la liste des indices des étoiles de l'astérismes.
     *
     * @param asterism astérisme dont on veut l'indice de ses étoiles.
     * @return la liste des indices des étoiles de l'astérismes, sous la forme
     *          d'une List d'Integer.
     */
    public List<Integer> starsIndex(Asterism asterism) {
        return catalogue.asterismIndices(asterism);
    }

    /**
     * Retourne la liste des constellations.
     *
     * @return la liste des constellations.
     */
    public List<Constellation> constellations() {
        return catalogue.constellations();
    }

    /**
     * Retourne l'objet céleste le plus proche du point.
     *
     * @param point point du plan dont on cherche l'objet céleste le plus proche.
     * @param maxDistance distance maximale de la recherche.
     * @return l'objet céleste le plus proche du point.
     */
    public Optional<CelestialObject> objectClosestTo(CartesianCoordinates point, double maxDistance) {

        // Nous travaillons sur des distances au carré pour éviter d'appliquer une racine
        // sur toutes les distances entre le point et les objets célestes.
        double closestDistance = maxDistance * maxDistance;

        double stepDistance;
        Optional<CelestialObject> closestObject = Optional.empty();

        if(point == null) return closestObject;

        for (int i = 0; i < starsPositions.length; i += 2)
        {
            stepDistance = distanceSquare(point, starsPositions[i], starsPositions[i + 1]);
            if(stepDistance <= closestDistance) {
                closestObject = Optional.of(stars.get(i / 2));
                closestDistance = stepDistance;
            }
        }

        for(int i = 0; i < planetsPositions.length; i += 2)
        {
            stepDistance = distanceSquare(point, planetsPositions[i], planetsPositions[i + 1]);
            if(stepDistance <= closestDistance) {
                closestObject = Optional.of(planets.get(i / 2));
                closestDistance = stepDistance;
            }
        }

        stepDistance = distanceSquare(point, sunPosition);
        if(stepDistance <= closestDistance) {
            closestObject = Optional.of(sun);
            closestDistance = stepDistance;
        }

        stepDistance = distanceSquare(point, moonPosition);
        if(stepDistance <= closestDistance) {
            closestObject = Optional.of(moon);
        }

        return closestObject;
    }

}
