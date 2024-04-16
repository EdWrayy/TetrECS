package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.net.URL;

public class IntroScene extends BaseScene{
    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    Multimedia multimedia;
    public IntroScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void initialise() {
        Scene scene = root.getScene();
    }

    @Override
    public void build() {
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        var introPane = new StackPane();
        introPane.setMaxWidth(gameWindow.getWidth());
        introPane.setMaxHeight(gameWindow.getHeight());
        introPane.getStyleClass().add("introScene");
        root.getChildren().add(introPane);

        URL imageUrl = getClass().getResource("/images/ECSGames.png");
        Image introImage = new Image(imageUrl.toString());
        ImageView introImageView = new ImageView(introImage);
        introImageView.setFitHeight(300);
        introImageView.setFitWidth(400);
        introImageView.setPreserveRatio(true);

        var introWrapper = new HBox();
        introWrapper.setAlignment(Pos.CENTER);
        introWrapper.getChildren().add(introImageView);
        introPane.getChildren().add(introWrapper);

        multimedia = new Multimedia();
        //multimedia.playAudio("src/main/resources/sounds/intro.mp3");

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), introWrapper);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Set up the Fade Out Transition
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), introWrapper);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        SequentialTransition fade = new SequentialTransition(fadeIn, fadeOut);
        fade.setAutoReverse(false);
        fade.setOnFinished(event -> gameWindow.startMenu());
        fade.play();



    }


}
