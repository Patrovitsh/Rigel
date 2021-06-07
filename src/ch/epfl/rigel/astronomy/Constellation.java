package ch.epfl.rigel.astronomy;

import javafx.scene.image.Image;

import java.util.Objects;

/**
 * Constellation.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public class Constellation {

    private final Image image;
    private final String star1;
    private final String star2;
    private final double width;
    private final double height;
    private final double scaleFactor;
    private final double deltaAngle;

    /**
     * Constructeur d'une constellation.
     *
     * @param imageName nom de l'image.
     * @param star1 étoile de position une.
     * @param star2 étoile de position deux.
     * @param deltaAngle variation d'angle à appliquer.
     * @param scaleFactor facteur arbitraire.
     */
    public Constellation(String imageName, String star1, String star2, double deltaAngle, double scaleFactor) {
        image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imageName)));
        this.star1 = star1;
        this.star2 = star2;
        this.deltaAngle = deltaAngle;
        this.scaleFactor = scaleFactor;
        width = image.getWidth();
        height = image.getHeight();
    }

    /**
     * Retourne l'image de la constellation.
     *
     * @return l'image de la constellation.
     */
    public Image getImage() {
        return image;
    }

    /**
     * Retourne le nom de l'étoile une.
     *
     * @return le nom de l'étoile une.
     */
    public String getStar1() {
        return star1;
    }

    /**
     * Retourne le nom de l'étoile deux.
     *
     * @return le nom de l'étoile deux.
     */
    public String getStar2() {
        return star2;
    }

    /**
     * Retourne la variation d'angle.
     *
     * @return la variation d'angle.
     */
    public double getDeltaAngle() {
        return deltaAngle;
    }

    /**
     * Retourne le tableau de paramètre de la constellation.
     *
     * @return le tableau de paramètres de la constellation.
     */
    public double getScaleFactor() {
        return scaleFactor;
    }

    /**
     * Retourne la largeur de l'image.
     *
     * @return la largeur de l'image.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Retourne la hauteur de l'image.
     *
     * @return la hauteur de l'image.
     */
    public double getHeight() {
        return height;
    }
}
