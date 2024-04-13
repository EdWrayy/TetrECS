package uk.ac.soton.comp1206.scene;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrintQuality;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ScoresScene extends BaseScene {

    Multimedia multimedia;

    SimpleListProperty<Pair<String, Integer>> localScores;

    Game game;

    ScoresList scoresList;
    private static final Logger logger = LogManager.getLogger(ScoresScene.class);

    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
        this.game = game;
    }

    @Override
    public void initialise() {
        Scene scene = root.getScene();
        if (scene != null) {
            scene.setOnKeyPressed(this::handleKeyPress);
        }
    }

    @Override
    public void build() {
        multimedia = new Multimedia();
        multimedia.playBackgroundMusic("src/main/resources/music/menu.mp3");
        scoresList = new ScoresList();
        localScores = new SimpleListProperty<>(FXCollections.observableArrayList());
        scoresList.getScoresProperty().bindBidirectional(localScores);


        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        StackPane scoresPane = new StackPane();
        scoresPane.setMaxWidth(gameWindow.getWidth());
        scoresPane.setMaxHeight(gameWindow.getHeight());
        scoresPane.getStyleClass().add("menu-background");
        root.getChildren().add(scoresPane);
        var mainPane = new BorderPane();
        scoresPane.getChildren().add(mainPane);

        mainPane.setBottom(scoresList);



        loadScores("src/main/resources/IO/scores.txt");
        checkScore();
        writeScores("src/main/resources/IO/scores.txt");

        VBox titles = new VBox();
        titles.setPadding(new Insets(10, 10, 10, 10));
        titles.setSpacing(10);
        titles.setAlignment(Pos.CENTER);
        mainPane.setTop(titles);

        URL imageUrl = getClass().getResource("/images/TetrECS.png");
        Image titleImage = new Image(imageUrl.toString());
        ImageView title = new ImageView(titleImage);
        title.setFitHeight(200);
        title.setFitWidth(500);
        title.setPreserveRatio(true);

        Label gameOver = new Label("Game Over");
        gameOver.getStyleClass().add("gameOver");
        gameOver.setAlignment(Pos.CENTER);
        Label yourScore = new Label("Your Score: " +game.getScoreValue());
        yourScore.getStyleClass().add("yourScore");
        yourScore.setAlignment(Pos.CENTER);
        Label highScores = new Label("High Scores:");
        highScores.getStyleClass().add("highScores");
        highScores.setAlignment(Pos.CENTER);

        titles.getChildren().add(title);
        titles.getChildren().add(gameOver);
        titles.getChildren().add(yourScore);
        titles.getChildren().add(highScores);


    }

    public void updateScores(SimpleListProperty<Pair<String, Integer>> newScores) {
        this.localScores = newScores;
    }

    // Getters for external access
    public SimpleListProperty<Pair<String, Integer>> getLocalScoresProperty() {
        return localScores;
    }


    public void handleKeyPress(KeyEvent keyEvent) {
        logger.info("key pressed");
        switch(keyEvent.getCode()) {
            case ESCAPE:
                logger.info("ESC pressed");
                showMenu(keyEvent);
                break;
            case P:
                logger.info("P pressed");
                showMenu(keyEvent);
                break;
        }
    }

    private void showMenu(KeyEvent event) {
        multimedia.stopMusic();
        gameWindow.startMenu();
        logger.info("Instructions Music Stopped");
    }

    public void loadScores(String filePath) {
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    Integer score = Integer.parseInt(parts[1].trim());
                    localScores.add(new Pair<>(name, score));
                }
            }
        } catch (IOException e) {
            localScores.add(new Pair<>("unknown", 1000));
            localScores.add(new Pair<>("unknown", 2000));
            localScores.add(new Pair<>("unknown", 3000));
            localScores.add(new Pair<>("unknown", 4000));
            localScores.add(new Pair<>("unknown", 5000));
        }
    if(localScores.isEmpty()){
        localScores.add(new Pair<>("unknown", 1000));
        localScores.add(new Pair<>("unknown", 2000));
        localScores.add(new Pair<>("unknown", 3000));
        localScores.add(new Pair<>("unknown", 4000));
        localScores.add(new Pair<>("unknown", 5000));
    }
    }

    public void writeScores(String filePath) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            for (Pair<String, Integer> score : localScores.get()) {
                writer.write(score.getKey() + ":" + score.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            logger.info("Nothing to write");
        }
    }

    public void checkScore(){
        int score = game.getScoreValue();
        Iterator<Pair<String,Integer>> scoresIterator = localScores.iterator();
        int lowestHighScore = 999999999;
        if(localScores.size() >= 10) {
            while (scoresIterator.hasNext()) {
                Pair<String, Integer> pair = scoresIterator.next();
                Integer currentScore = pair.getValue();
                if (currentScore < lowestHighScore) {
                    lowestHighScore = currentScore;
                }
            }
            if (score > lowestHighScore) {
                boolean valueRemoved = false;
                Iterator<Pair<String,Integer>> scoresIterator2 = localScores.iterator();
                while (scoresIterator2.hasNext() && !valueRemoved) {
                    Pair<String, Integer> pair = scoresIterator2.next();
                    if(pair.getValue() == lowestHighScore){
                        localScores.remove(pair);
                        valueRemoved = true;
                    }
                }
                localScores.add(new Pair<>("You", score));
            }
        }
        else {
            localScores.add(new Pair<>("You", score));
        }
        sortScores();
    }

    public void sortScores(){
        Comparator<Pair<String, Integer>> comparator = (pair1, pair2) -> pair2.getValue().compareTo(pair1.getValue());
        Collections.sort(localScores, comparator);
    }





}