package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.Set;

/**
 * The Visual User Interface component representing a single block in the grid.
 *
 * Extends Canvas and is responsible for drawing itself.
 *
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 *
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    private static final Logger logger = LogManager.getLogger(GameBlock.class);

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE
    };

    private final GameBoard gameBoard;


    private final double width;
    private final double height;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    /**
     * Create a new single Game Block
     * @param gameBoard the board this block belongs to
     * @param x the column the block exists in
     * @param y the row the block exists in
     * @param width the width of the canvas to render
     * @param height the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;


        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);
    }

    /**
     * When the value of this block is updated,
     * @param observable what was updated
     * @param oldValue the old value
     * @param newValue the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Set a hovering animation when a block is highlighted, reducing opacity.
     */
    public void setHovering(){
        Color color = COLOURS[getValue()];
        double red = color.getRed();
        double blue = color.getBlue();
        double green = color.getGreen();
        double opacity = color.getOpacity();


        var gc = getGraphicsContext2D();
        //Clear
        gc.clearRect(0,0,width,height);

        gc.beginPath();
        gc.moveTo(0, 0); // Top left corner
        gc.lineTo(width, 0); // Top right corner
        gc.lineTo(0, height); // Bottom left corner
        gc.closePath();
        gc.setFill(new Color(red,blue,green,opacity/2));
        gc.fill();

        gc.beginPath();
        gc.moveTo(width, height); // Bottom right corner
        gc.lineTo(width, 0); // Top right corner
        gc.lineTo(0, height); // Bottom left corner
        gc.closePath();
        gc.setFill(new Color(red,blue,green,opacity*0.9/2));
        gc.fill();


        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);
    }


    /**
     * Plays a fade out animation on a block
     */
    public void fadeOutAnimation(){
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(new Color(1, 1, 1, 1));
        gc.fillRect(0, 0, width, height);
        gc.setStroke(new Color(1, 1, 1, 1));
        gc.strokeRect(0,0,width,height);
        //Draw a new square on top of the current block

        new AnimationTimer() {
            private long lastUpdate = 0;
            private double opacity = 1.0; // Start with full opacity

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                // Calculate elapsed time since last frame in seconds
                double elapsedTime = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;

                // Update opacity
                opacity -= elapsedTime * 0.9; // Decrease opacity at a rate of 0.5 per second
                if (opacity < 0) {
                    opacity = 0;
                    this.stop(); // Stop the animation when fully transparent
                    paintEmpty();
                    return;
                }

                // Clear the area and redraw the shape with new opacity
                gc.clearRect(0, 0, width, height); // Clear the area to avoid ghosting
                gc.setFill(new Color(1, 1, 1, opacity));
                gc.fillRect(0, 0, width, height);
                gc.setStroke(new Color(1, 1, 1, opacity));
                gc.strokeRect(0, 0, width, height);
            }
        }.start();
        gc.clearRect(0,0,width,height);
    }






    /**
     * Handle painting of the block canvas
     */
    public void paint() {
        //If the block is empty, paint as empty
        if(value.get() == 0) {
            paintEmpty();
        } else {
            //If the block is not empty, paint with the colour represented by the value
            paintColor(COLOURS[value.get()]);
        }
    }

    /**
     * Paint this canvas empty
     */
    private void paintEmpty() {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Fill
        gc.setFill(new Color(1.0, 1.0, 1.0, 0.1));
        gc.fillRect(0,0, width, height);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);
    }

    /**
     * Paint this canvas with the given colour
     * @param colour the colour to paint
     */
    private void paintColor(Paint colour) {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        Color color = (Color) colour;
        double red = color.getRed();
        double blue = color.getBlue();
        double green = color.getGreen();
        double opacity = color.getOpacity();

        gc.beginPath();
        gc.moveTo(0, 0); // Top left corner
        gc.lineTo(width, 0); // Top right corner
        gc.lineTo(0, height); // Bottom left corner
        gc.closePath();
        gc.setFill(new Color(red,blue,green,opacity));
        gc.fill();


        gc.beginPath();
        gc.moveTo(width, height); // Bottom right corner
        gc.lineTo(width, 0); // Top right corner
        gc.lineTo(0, height); // Bottom left corner
        gc.closePath();
        gc.setFill(new Color(red,blue,green,opacity*0.9));
        gc.fill();


        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);
    }


    /**
     * Draws a ring on a block
     */
    public void addCenterRing(){
        var gc = getGraphicsContext2D();
        double diameter = Math.min(width, height) * 0.6;
        double centerX = (width - diameter) / 2;
        double centerY = (height - diameter) / 2;
        gc.setFill(new Color(0.5, 0.5, 0.5, 0.5));
        gc.fillOval(centerX, centerY, diameter, diameter);
    }

    /**
     * Get the column of this block
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }

}
