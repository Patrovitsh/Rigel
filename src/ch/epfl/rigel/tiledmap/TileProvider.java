package ch.epfl.rigel.tiledmap;

/**
 * Interface représentant le moyen d'obtenir une tuile.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public interface TileProvider {
    
    /**
     * Retourne la tuile correspondante au niveau de zoom , 
     * l'abcsisse et l'ordonnée d'un Point OSM.
     *
     * @param zoom le niveau de zoom.
     * @param x l'abscisse du point.
     * @param y l'ordonnée du point.
    * @return une tuile correspondante au cordonnées OSM données.
     */
    public abstract Tile tileAt(int zoom,int x ,int y);
}
