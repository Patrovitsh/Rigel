package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Bean JavaFX contenant la position de l'observateur en degrés.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public class ObserverLocationBean {

    private final DoubleProperty lonDeg;
    private final DoubleProperty latDeg;
    private final ObjectBinding<GeographicCoordinates> coordinates;

    /**
     * Constructeur d'un nouveau bean contenant la position de l'observateur.
     */
    public ObserverLocationBean() {
        lonDeg = new SimpleDoubleProperty();
        latDeg = new SimpleDoubleProperty();

        coordinates = Bindings.createObjectBinding(
                () -> GeographicCoordinates.ofDeg(getLonDeg(), getLatDeg() ),
                lonDeg,
                latDeg);
    }

    /**
     * Retourne un accès à la propriété longitude de la position
     * de l'observateur.
     *
     * @return un accès à la propriété longitude de la position de
     *         l'observateur.
     */
    public DoubleProperty lonDegProperty(){
        return lonDeg;
    }

    /**
     * Retourne un accès au contenu de la propriété longitude de
     * la position de l'observateur.
     *
     * @return un accès au contenu de la propriété longitude de
     *         la position de l'observateur.
     */
    public double getLonDeg() {
        return lonDeg.get();
    }

    /**
     * Permet de modifier le contenu de la propriété longitude de
     * la position de l'observateur.
     *
     * @param lonDeg longitude de la position de l'observateur.
     */
    public void setLonDeg(Double lonDeg) {
        this.lonDeg.set(lonDeg);
    }

    /**
     * Retourne un accès à la propriété latitude de la position
     * de l'observateur.
     *
     * @return un accès à la propriété latitude de la position de
     *         l'observateur.
     */
    public DoubleProperty latDegProperty(){
        return latDeg;
    }

    /**
     * Retourne un accès au contenu de la propriété latitude de
     * la position de l'observateur.
     *
     * @return un accès au contenu de la propriété latitude de
     *         la position de l'observateur.
     */
    public double getLatDeg() {
        return latDeg.get();
    }

    /**
     * Permet de modifier le contenu de la propriété latitude de
     * la position de l'observateur.
     *
     * @param latDeg latitude de la position de l'observateur.
     */
    public void setLatDeg(Double latDeg) {
        this.latDeg.set(latDeg);
    }

    /**
     * Retourne le lien contenant les coordonnées géographiques de la
     * position de l'observateur.
     *
     * @return le lien contenant les coordonnées géographiques de la
     *         position de l'observateur.
     */
    public ObjectBinding<GeographicCoordinates> coordinatesProperty(){
        return coordinates;
    }

    /**
     * Retourne les coordonnées géographiques de la position de l'observateur.
     *
     * @return les coordonnées géographiques de la position de l'observateur.
     */
    public GeographicCoordinates getCoordinates() {
        return coordinates.get();
    }

    /**
     * Définit la position de l'observateur.
     *
     * @param coordinates coordonnées géographiques
     */
    public void setCoordinates(GeographicCoordinates coordinates) {
        setLonDeg(coordinates.lonDeg());
        setLatDeg(coordinates.latDeg());
    }

}
