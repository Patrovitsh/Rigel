package ch.epfl.rigel.astronomy;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * Catalogue d'étoiles.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class StarCatalogue {

    public static final String SUN_NAME = "Soleil";
    public static final String MOON_NAME = "Lune";

    private final List<Star> listStars;
    private final Set<Asterism> setAsterisms;
    private final List<Constellation> constellations;
    private final Map<Asterism, List<Integer>> mapAsterismsAndIndex;
    private final Map<Star, Integer> starsIndexInCatalogue;
    private final Map<String, Star> nameOfStars;
    private final Map<String, PlanetModel> nameOfPlanets;

    /**
     * Construit un catalogue d'étoiles.
     *
     * @param stars liste d'étoiles.
     * @param asterisms liste d'astérismes.
     */
    public StarCatalogue(List<Star> stars, List<Asterism> asterisms, List<Constellation> constellations) {
        for(Asterism asterism : asterisms)
            checkArgument(stars.containsAll(asterism.stars()));

        this.listStars = List.copyOf(stars);
        this.setAsterisms = Set.copyOf(asterisms);
        this.constellations = List.copyOf(constellations);
        starsIndexInCatalogue = starsIndexInCatalogue();
        mapAsterismsAndIndex = mapAsterismsAndIndex();
        nameOfStars = nameOfStars();
        nameOfPlanets = nameOfPlanets();
    }

    /**
     * Retourne une map qui lie étoiles et index des étoiles dans le catalogue.
     *
     * @return une map qui lie étoiles et index des étoiles dans le catalogue.
     */
    private Map<Star, Integer> starsIndexInCatalogue() {
        Map<Star, Integer> starsIndexInCatalogue = new HashMap<>();
        for(int i = 0; i < listStars.size(); ++i)
            starsIndexInCatalogue.put(listStars.get(i), i);
        return starsIndexInCatalogue;
    }

    /**
     * Retourne une map qui lie les étoiles contenues dans les astérismes à
     * leur index dans le catalogue d'étoiles.
     *
     * @return une map qui lie les étoiles contenues dans les astérismes à
     *         leur index dans le catalogue d'étoiles.
     */
    private Map<Asterism, List<Integer>> mapAsterismsAndIndex() {
        Map<Asterism, List<Integer>> mapAsterismInteger = new HashMap<>();
        List<Integer> asterismIndex = new ArrayList<>();

        for(Asterism asterism : setAsterisms) {

            for(Star star : asterism.stars())
                asterismIndex.add(starsIndexInCatalogue.get(star));

            mapAsterismInteger.put(asterism, List.copyOf(asterismIndex));
            asterismIndex.clear();
        }

        return mapAsterismInteger;
    }

    /**
     * Retourne une map liant le nom des étoiles aux étoiles.
     *
     * @return une map liant le nom des étoiles aux étoiles.
     */
    private Map<String, Star> nameOfStars() {
        Map<String, Star> nameOfStars = new HashMap<>();

        for(Star star : listStars)
            if(star.name().charAt(0) != '?')
                nameOfStars.put(star.name(), star);

        return nameOfStars;
    }

    /**
     * Retourne une map liant le nom des planètes aux planètes.
     *
     * @return une map liant le nom des planètes aux planètes.
     */
    private Map<String, PlanetModel> nameOfPlanets() {
        Map<String, PlanetModel> nameOfPlanets = new HashMap<>();

        for(PlanetModel planet : PlanetModel.values())
            if(planet != PlanetModel.EARTH)
                nameOfPlanets.put(planet.getName(), planet);

        return nameOfPlanets;
    }

    /**
     * Retourne true si la planète est présente, false sinon.
     *
     * @param name nom de l'étoile.
     * @return true si l'étoile est présente, false sinon.
     */
    public boolean isStarPresent(String name) {
        return nameOfStars.containsKey(name);
    }

    /**
     * Retourne l'étoile correspondant au nom.
     *
     * @param name nom de l'étoile.
     * @return l'étoile correspondant au nom.
     */
    public Star getStar(String name) {
        return nameOfStars.get(name);
    }

    /**
     * Retourne true si la planète est présente, false sinon.
     *
     * @param name nom de la planète.
     * @return true si la planète est présente, false sinon.
     */
    public boolean isPlanetPresent(String name) {
        return nameOfPlanets.containsKey(name);
    }

    /**
     * Retourne la planète correspondant au nom.
     *
     * @param name nom de la planète.
     * @return la planète correspondant au nom.
     */
    public PlanetModel getPlanet(String name) {
        return nameOfPlanets.get(name);
    }

    /**
     * Retourne une liste contenant le nom de chaque objet céleste.
     *
     * @return une liste contenant le nom de chaque objet céleste.
     */
    public List<String> getNameOfCelestialObjects() {
        List<String> names = new ArrayList<>();
        names.add(SUN_NAME);
        names.add(MOON_NAME);
        names.addAll(nameOfPlanets.keySet());

        List<String> starsName = new ArrayList<>(nameOfStars.keySet());
        Collections.sort(starsName);
        names.addAll(starsName);

        return names;
    }

    /**
     * Retourne la liste des Étoiles.
     *
     * @return la liste des Étoiles.
     */
    public List<Star> stars() {
        return List.copyOf(listStars);
    }

    /**
     * Retourne le set des Astérismes.
     *
     * @return le set des Astérismes.
     */
    public Set<Asterism> asterisms() {
        return Set.copyOf(setAsterisms);
    }

    /**
     * Retourne la liste des index des étoiles constituant l'astérisme donné.
     *
     * @param asterism astérisme dont on veut les index.
     * @return la liste des index des étoiles constituant l'astérisme donné.
     */
    public List<Integer> asterismIndices(Asterism asterism) {
        checkArgument(mapAsterismsAndIndex.get(asterism) != null);
        return mapAsterismsAndIndex.get(asterism);
    }

    /**
     * Retourne la liste des constellations.
     *
     * @return la liste des constellations.
     */
    public List<Constellation> constellations() {
        return List.copyOf(constellations);
    }

    /**
     * Bâtisseur de catalogue d'étoiles.
     */
    public static final class Builder {
       
        private final List<Star> stars;
        private final List<Asterism> asterisms;
        private final List<Constellation> constellations;

        public Builder() {
            stars = new ArrayList<>();
            asterisms = new ArrayList<>();
            constellations = new ArrayList<>();
        }
        
        /**
         * Ajoute l'étoile donnée au catalogue en cours de construction,
         * et retourne le bâtisseur.
         *
         * @param star l'étoile à ajouter.
         * @return le bâtisseur.
         */
        public Builder addStar(Star star) {
            stars.add(star);
            return this;
        }

        /**
         * Retourne une vue non modifiable sur les étoiles du catalogue
         * en cours de construction.
         *
         * @return une liste d'étoiles.
         */
        public List<Star> stars() {
            return Collections.unmodifiableList(stars);
        }

        /**
         * Ajoute l'astérisme donné au catalogue en cours de construction,
         * et retourne le bâtisseur.
         *
         * @param asterism astérisme à ajouter.
         * @return le bâtisseur.
         */
        public Builder addAsterism(Asterism asterism) {
            asterisms.add(asterism);
            return this;
        }

        /**
         * Retourne une vue non modifiable sur les astérismes du catalogue
         * en cours de construction.
         *
         * @return une liste d'astérismes.
         */
        public List<Asterism> asterisms() {
            return Collections.unmodifiableList(asterisms);
        }

        /**
         * Ajoute la constellation donnée au catalogue en cours de construction,
         * et retourne le bâtisseur.
         *
         * @param constellation constellation à ajouter.
         * @return le bâtisseur.
         */
        public Builder addConstellation(Constellation constellation) {
            constellations.add(constellation);
            return this;
        }

        /**
         * Retourne une vue non modifiable sur les constellations du catalogue
         * en cours de construction.
         *
         * @return une liste de constellations.
         */
        public List<Constellation> constellations() {
            return Collections.unmodifiableList(constellations);
        }

        /**
         * Demande au chargeur 'loader' d'ajouter au catalogue les étoiles
         * et/ou astérismes qu'il obtient depuis le flot d'entrée inputStream,
         * et retourne le bâtisseur, ou lève IOException en cas d'erreur
         * d'entrée/sortie.
         *
         * @param inputStream flot d'entrée.
         * @param loader chargeur de catalogue.
         * @throws IOException en cas d'erreur d'entrée/sortie.
         * @return le bâtisseur.
         */
        public Builder loadFrom(InputStream inputStream, Loader loader) throws IOException {
            loader.load(inputStream, this);
            return this;
        }

        /**
         * Retourne le catalogue contenant les étoiles et astérismes ajoutés
         * jusqu'alors au bâtisseur.
         *
         * @return le catalogue.
         */
        public StarCatalogue build() {
            return new StarCatalogue(stars, asterisms, constellations);
        }

    }

    /**
     * Chargeur de catalogue d'étoiles et d'astérismes.
     */
    public interface Loader {

        /**
         * Charge les étoiles et/ou astérismes du flot d'entrée inputStream et les
         * ajoute au catalogue en cours de construction du bâtisseur builder, ou lève
         * IOException en cas d'erreur d'entrée/sortie.
         *
         * @param inputStream flot d'entrée.
         * @param builder bâtisseur de catalogue d'étoiles.
         * @throws IOException en cas d'erreur d'entrée/sortie.
         */
        public abstract void load(InputStream inputStream, Builder builder) throws IOException;

    }

}
