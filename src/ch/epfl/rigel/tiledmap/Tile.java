package ch.epfl.rigel.tiledmap;

import ch.epfl.rigel.coordinates.OSMCoordinates;
import javafx.scene.image.Image;

/**
 * Tuile (Carte zoomé).
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public final class Tile {
   
    private final OSMCoordinates osmCor;
    private final Image image;

    /**
     * Constuit une nouvelle Tuile.
     *
     * @param osmCor coordonnées OSM.
     * @param image image que contient la tuile.
     */
    public Tile (OSMCoordinates osmCor, Image image) {
        this.osmCor = osmCor;
        this.image = image;
    }
   
    /**
     * Retourne l'image du tile.
     *
     * @return l'image du tile.
     */
    public Image image() {
        return image;
    }
    
    /**
     * Retourne le niveau de zoom.
     *
     * @return le niveau de zoom.
     */
    public double zoom() {
        return osmCor.zoom();
    }

    
    /**
     * Retourne l'abscisse .
     *
     * @return l'abscisse.
     */
    public double x() {
        return osmCor.x();
    }

    /**
     * Retourne l'ordonnée.
     *
     * @return l'ordonnée.
     */
    public double y() {
        return osmCor.y();
    }
}
