package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.math.Angle;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Bean JavaFX sur la portion du ciel visible sur l'image.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public class ViewingParametersBean {

    private final DoubleProperty fieldOfViewDeg = new SimpleDoubleProperty();
    private final ObjectProperty<HorizontalCoordinates> center = new SimpleObjectProperty<>();

    /**
     * Retourne un accès à la propriété champ de vue.
     *
     * @return un accès à la propriété champ de vue.
     */
    public DoubleProperty fieldOfViewDegProperty(){
        return fieldOfViewDeg;
    }

    /**
     * Retourne un accès au contenu de la propriété champ de vue (Unité : degré).
     *
     * @return un accès au contenu de la propriété champ de vue (Unité : degré).
     */
    public double getFieldOfViewDeg() {
        return fieldOfViewDeg.get();
    }

    /**
     * Retourne un accès au contenu de la propriété champ de vue (Unité : radian).
     *
     * @return un accès au contenu de la propriété champ de vue (Unité : radian).
     */
    public double getFieldOfView() {
        return Angle.ofDeg(fieldOfViewDeg.get());
    }

    /**
     * Permet de modifier le contenu de la propriété champ de vue.
     *
     * @param fieldOfViewDeg champ de vue en degrés.
     */
    public void setFieldOfViewDeg(double fieldOfViewDeg) {
        this.fieldOfViewDeg.set(fieldOfViewDeg);
    }

    /**
     * Retourne un accès à la propriété centre de projection.
     *
     * @return un accès à la propriété centre de projection.
     */
    public ObjectProperty<HorizontalCoordinates> centerProperty(){
        return center;
    }

    /**
     * Retourne un accès au contenu de la propriété centre de projection.
     *
     * @return un accès au contenu de la propriété centre de projection.
     */
    public HorizontalCoordinates getCenter() {
        return center.get();
    }

    /**
     * Permet de modifier le contenu de la propriété centre de projection.
     *
     * @param center centre de projection.
     */
    public void setCenter(HorizontalCoordinates center) {
        this.center.set(center);
    }

}
