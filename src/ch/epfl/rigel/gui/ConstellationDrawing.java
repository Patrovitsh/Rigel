package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.Constellation;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Rotate;

import static ch.epfl.rigel.math.Angle.angleWithHorizontal;
import static ch.epfl.rigel.math.TrigoFunctions.distanceSquare;

/**
 * Dessinateur de constellations imagées.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public class ConstellationDrawing {


    private ConstellationDrawing() {}

    /**
     * Dessine la constellation fourni.
     *
     * @param ctx contexte graphique.
     * @param constellation constellation à dessiner.
     * @param point1 position de la première étoile.
     * @param point2 position de la deuxième étoile.
     */
    public static void drawRotatedImage(GraphicsContext ctx, Constellation constellation,
                                        Point2D point1, Point2D point2) {
        ctx.save();

        double angle = angleWithHorizontal(point1, point2);
        rotate(ctx, angle + constellation.getDeltaAngle(), point1.getX(), point1.getY());

        double width = Math.sqrt(distanceSquare(point1, point2) * constellation.getScaleFactor());
        double height = (constellation.getHeight() * width) / constellation.getWidth();

        ctx.drawImage(constellation.getImage(), 0, 0, constellation.getWidth(),
                constellation.getHeight(), point1.getX(), point1.getY(), width, height);
        ctx.restore();
    }

    /**
     * Définit la transformation pour appliquer une rotation de l'image.
     *
     * @param ctx contexte graphique.
     * @param angle angle d'inclinaison de l'image.
     * @param pivotX pivot de coordonnée x pour la rotation.
     * @param pivotY pivot de coordonnée y pour la rotation.
     */
    private static void rotate(GraphicsContext ctx, double angle, double pivotX, double pivotY) {
        Rotate r = new Rotate(angle, pivotX, pivotY);
        ctx.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

}
