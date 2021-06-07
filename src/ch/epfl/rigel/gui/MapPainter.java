package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.tiledmap.OSMTileProvider;
import ch.epfl.rigel.tiledmap.Tile;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Peintre de carte.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public class MapPainter {
    //number of pixels per tile
    public static final int NUMBER_OF_PIXELS = 256;

    public static final int DEFAULT_ZOOM = 2;
    public static final int NUMBER_OF_TILES_PER_SIDE = DEFAULT_ZOOM * 2;
    public static final int THUMBTACK_DIAMETER = 9;

    private final Canvas canvas;
    private final GraphicsContext ctx;

    /**
     * Constructeur d'un peintre de carte.
     *
     * @param canvas canvas sur lequel dessiner la carte.
     */
    public MapPainter(Canvas canvas){
        this.canvas = canvas;
        ctx = canvas.getGraphicsContext2D();
    }

    /**
     * Nettoie le canvas.
     */
    public void clear() {
        ctx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Dessine la carte avec une punaise indiquant le point d'observation.
     *
     * @param coordinates coordonnées géographiques du point d'observation.
     */
    public void drawThumbtack(CartesianCoordinates coordinates) {
        double newY = coordinates.y() - 15;

        ctx.setStroke(Color.color(0.2, 0.2, 0.2));
        ctx.beginPath();
        ctx.moveTo(coordinates.x(), coordinates.y());
        ctx.lineTo(coordinates.x(), newY);
        ctx.stroke();

        ctx.setFill(Color.RED);
        double radius = THUMBTACK_DIAMETER / 2.;
        ctx.fillOval(coordinates.x() - radius, newY - radius,
                THUMBTACK_DIAMETER, THUMBTACK_DIAMETER);
    }

    /**
     * Re-dessine la carte avec une nouvelle punaise indiquant le point d'observation.
     *
     * @param coordinates coordonnées géographiques du point d'observation.
     */
    public void drawNewThumbtack(CartesianCoordinates coordinates) {
        clear();
        drawMapTiles();
        drawThumbtack(coordinates);
    }

    /**
     * Dessine la carte sur la canvas.
     */
    private void drawMapTiles() {
        OSMTileProvider tileProvider = new OSMTileProvider();

        for(int i = 0; i <  NUMBER_OF_TILES_PER_SIDE; i++)
        {
            for(int j = 0; j <  NUMBER_OF_TILES_PER_SIDE; j++) {
                Tile tile = tileProvider.tileAt(DEFAULT_ZOOM, i, j) ;
                ctx.drawImage(tile.image(), i * NUMBER_OF_PIXELS , j * NUMBER_OF_PIXELS);
            }
        }
    }

}
