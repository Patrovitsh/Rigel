package ch.epfl.rigel.tiledmap;

import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.OSMCoordinates;
import javafx.scene.image.Image;

import java.util.Objects;

/**
 * Fournisseur de Tuile.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public class OSMTileProvider implements TileProvider {
    private static final String MAPS_PACKAGE_NAME = "/maps/";
    
    @Override
    public Tile tileAt(int zoom, int x, int y) {
        OSMCoordinates tileOSMCor = OSMCoordinates.of(zoom, CartesianCoordinates.of(x,y));
        String imageName = MAPS_PACKAGE_NAME + zoom + '_' + x + '_' + y +".png";
        Image tileImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imageName)));

        return new Tile(tileOSMCor, tileImage);
    }

}
