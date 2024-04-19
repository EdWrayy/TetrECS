package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Displays the scores after a game ends
 * In single player it displays the local scores and the online high scores
 * In multiplayer it replaces the local scores with the scores of all players in that game
 */

public class ScoresScene extends BaseScene {

    Multimedia multimedia;

    SimpleListProperty<Pair<String, Integer>> localScores;

    SimpleListProperty<Pair<String, Integer>> remoteScores;

    Communicator communicator;

    String name = "User";
    Game game;

    ScoresList scoresList;

    ScoresList onlineScoresList;

    TextInputDialog dialog;
    private static final Logger logger = LogManager.getLogger(ScoresScene.class);

    /**
     * Saves data from the game
     * @param gameWindow the current gameWindow
     * @param game the game which just ended
     */
    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
        this.game = game;
    }

    /**
     * Sets up handling user input
     */
    @Override
    public void initialise() {
        scene.setOnKeyPressed(this::handleKeyPress);
    }

    /**
     * Sets up UI which contains a ScoresList custom component
     * Loads the saved scores from the files
     * Checks whether our scores beat any
     * Add our score if necessary
     * Sort the new list
     * Override the old list with the new one, write it to the file
     */
    @Override
    public void build() {
        multimedia = new Multimedia();
        multimedia.playBackgroundMusic("src/main/resources/music/menu.mp3");

        scoresList = new ScoresList();
        localScores = new SimpleListProperty<>(FXCollections.observableArrayList());
        scoresList.getScoresProperty().bindBidirectional(localScores);

        onlineScoresList = new ScoresList();
        remoteScores = new SimpleListProperty<>(FXCollections.observableArrayList());
        onlineScoresList.getScoresProperty().bindBidirectional(remoteScores);




        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        StackPane scoresPane = new StackPane();




        scoresPane.setMaxWidth(gameWindow.getWidth());
        scoresPane.setMaxHeight(gameWindow.getHeight());
        scoresPane.getStyleClass().add("menu-background");
        root.getChildren().add(scoresPane);
        var mainPane = new BorderPane();
        scoresPane.getChildren().add(mainPane);

        HBox highScoresInfo = new HBox();
        highScoresInfo.setPrefWidth(800);
        highScoresInfo.setPrefWidth(800);
        highScoresInfo.setPrefHeight(300);
        highScoresInfo.setAlignment(Pos.CENTER);

        VBox localScores = new VBox();
        localScores.setPrefWidth(400);
        Label localScoresLabel = new Label("Local Scores");
        localScoresLabel.setAlignment(Pos.CENTER);
        localScoresLabel.getStyleClass().add("scores");
        localScores.getChildren().add(localScoresLabel);
        localScores.getChildren().add(scoresList);
        scoresList.setAlignment(Pos.CENTER);


        VBox onlineScores = new VBox();
        onlineScores.setPrefWidth(400);
        Label onlineScoresLabel = new Label("Online Scores");
        onlineScoresLabel.getStyleClass().add("onlinescores");
        onlineScoresLabel.setAlignment(Pos.CENTER);
        onlineScores.getChildren().add(onlineScoresLabel);
        onlineScores.getChildren().add(onlineScoresList);
        onlineScoresList.setAlignment(Pos.CENTER);

        highScoresInfo.getChildren().add(localScores);
        highScoresInfo.getChildren().add(onlineScores);

        localScores.setAlignment(Pos.TOP_LEFT);
        localScores.setPadding(new Insets(10, 10, 10, 10));
        localScores.setSpacing(1);
        localScores.setPrefWidth(350);
        onlineScores.setAlignment(Pos.TOP_LEFT);
        onlineScores.setPadding(new Insets(10, 10, 10, 10));
        onlineScores.setSpacing(1);
        onlineScores.setPrefWidth(350);

        mainPane.setCenter(highScoresInfo);



        VBox titles = new VBox();
        titles.setPadding(new Insets(5, 5, 5, 5));
        titles.setSpacing(5);
        titles.setAlignment(Pos.CENTER);
        mainPane.setTop(titles);

        URL imageUrl = getClass().getResource("/images/TetrECS.png");
        Image titleImage = new Image(imageUrl.toString());
        ImageView title = new ImageView(titleImage);
        title.setFitHeight(150);
        title.setFitWidth(300);
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

        dialog = new TextInputDialog("User");
        dialog.setTitle(null);
        dialog.setHeaderText("Choose Your Name");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/style/game.css").toExternalForm());
        dialog.getDialogPane().setGraphic(null);
        dialog.getDialogPane().getStyleClass().add("dialog");
        dialog.getDialogPane().lookupButton(ButtonType.OK).getStyleClass().add("dialog-button");
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).getStyleClass().add("dialog-button");

        //Add hover
        dialog.getDialogPane().lookupButton(ButtonType.OK).setOnMouseEntered(e -> dialog.getDialogPane().lookupButton(ButtonType.OK).getStyleClass().add("button-ok:hover"));
        dialog.getDialogPane().lookupButton(ButtonType.OK).setOnMouseExited(e -> dialog.getDialogPane().lookupButton(ButtonType.OK).getStyleClass().add("button-ok"));

        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setOnMouseEntered(e -> dialog.getDialogPane().lookupButton(ButtonType.CANCEL).getStyleClass().add("button-ok:hover"));
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setOnMouseExited(e -> dialog.getDialogPane().lookupButton(ButtonType.CANCEL).getStyleClass().add("button-ok"));


        loadScores("src/main/resources/IO/scores.txt");
        checkScore();
        if(!(game instanceof MultiplayerGame)){
            writeScores("src/main/resources/IO/scores.txt");
        }
        communicator = gameWindow.getCommunicator();
        communicator.addListener(this::loadOnlineScores);
        communicator.send("HISCORES");
        try{
            Thread.sleep(100);
        }catch(Exception ignored){}
        checkOnlineScores();


        sortOnlineScores();

        if(game instanceof MultiplayerGame){
            localScoresLabel.setText("This Game");
        }



    }


    /**
     * If Escape is pressed we return to the main menu
     * @param keyEvent event
     */

    public void handleKeyPress(KeyEvent keyEvent) {
        logger.info("Key Pressed: " + keyEvent);
        if (Objects.requireNonNull(keyEvent.getCode()) == KeyCode.ESCAPE) {
            showMenu(keyEvent);
        }
    }

    /**
     * Switch to main menu
     * @param event ESCAPE
     */

    private void showMenu(KeyEvent event) {
        multimedia.stopMusic();
        gameWindow.startMenu();
        logger.info("Instructions Music Stopped");
    }

    /**
     * Reads the saved scores files and loads them into the SimpleList as pairs of String names and integer scores
     * @param filePath path of saved local scores
     */

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
            localScores.add(new Pair<>("User", 1000));
            localScores.add(new Pair<>("User", 2000));
            localScores.add(new Pair<>("User", 3000));
            localScores.add(new Pair<>("User", 4000));
            localScores.add(new Pair<>("User", 5000));
        }
        if (localScores.isEmpty()) {
            localScores.add(new Pair<>("User", 1000));
            localScores.add(new Pair<>("User", 2000));
            localScores.add(new Pair<>("User", 3000));
            localScores.add(new Pair<>("User", 4000));
            localScores.add(new Pair<>("User", 5000));
        }
        if (game instanceof MultiplayerGame) {
            ArrayList<Pair<String, Integer>> arrayList = (((MultiplayerGame) game).playersData);
            Iterator<Pair<String, Integer>> iterator = arrayList.iterator();
            localScores.clear();
            while (iterator.hasNext()) {
                Pair<String, Integer> pair = iterator.next();
                localScores.add(pair);
            }
        }
    }

    /**
     * Writes updated list to the file to save any changes
     * @param filePath path of local scores list
     */

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

    /**
     * If there are more than 10 scores already, it checks if our scores beats any in the list
     * If there are less than 10 scores our score is immediately added provided it isn't 0
     * It will ask the user to input a username in single player, if nothing is entered it defaults to User
     * In multiplayer their nickname is automatically used
     */
    public void checkScore(){
        if(game instanceof MultiplayerGame){}
        else {
            int score = game.getScoreValue();
            Iterator<Pair<String, Integer>> scoresIterator = localScores.iterator();
            int lowestHighScore = 999999999;
            if (localScores.size() >= 10) {
                while (scoresIterator.hasNext()) {
                    Pair<String, Integer> pair = scoresIterator.next();
                    Integer currentScore = pair.getValue();
                    if (currentScore < lowestHighScore) {
                        lowestHighScore = currentScore;
                    }
                }
                if (score > lowestHighScore && score != 0) {
                    Optional<String> result = dialog.showAndWait();
                    name = result.orElse("User");
                    if (name.length() > 15) {
                        name = name.substring(0, 15);
                    }
                    boolean valueRemoved = false;
                    Iterator<Pair<String, Integer>> scoresIterator2 = localScores.iterator();
                    while (scoresIterator2.hasNext() && !valueRemoved) {
                        Pair<String, Integer> pair = scoresIterator2.next();
                        if (pair.getValue() == lowestHighScore) {
                            localScores.remove(pair);
                            valueRemoved = true;
                        }
                    }
                    localScores.add(new Pair<>(name, score));
                }
            } else if (score != 0) {
                Optional<String> result = dialog.showAndWait();
                name = result.orElse("User");
                if (name.length() > 15) {
                    name = name.substring(0, 15);
                }
                localScores.add(new Pair<>(name, score));
            }
        }
        sortScores();
    }

    /**
     * Sorts the scores, putting the highest at index 0 and decending from there
     */
    public void sortScores(){
        Comparator<Pair<String, Integer>> comparator = (pair1, pair2) -> pair2.getValue().compareTo(pair1.getValue());
        Collections.sort(localScores, comparator);
    }

    /**
     * Gets the scores from the server and creates a list of them
     * @param onlineScore the scores received from the communicator
     */
    public void loadOnlineScores(String onlineScore) {
        onlineScore = onlineScore.replaceFirst("HISCORES ", "");
        logger.info("online scores called on "+onlineScore);
        String[] lines = onlineScore.split("\n");
        for(String line : lines){
            String[] parts = line.split(":");
            if (parts.length == 2) {
                String name = parts[0].trim();
                Integer score = Integer.parseInt(parts[1].trim());
                remoteScores.add(new Pair<>(name, score));
            }
        }
    }

    /**
     * Checks if any of our scores beat the online scores
     * If it does it will send our score to the server and update the list on our machine to contain our name
     */

    public void checkOnlineScores(){
        int score = game.getScoreValue();
        Iterator<Pair<String,Integer>> onlineScoresIterator = remoteScores.iterator();
        int lowestHighScore = 999999999;
        while (onlineScoresIterator.hasNext()) {
            Pair<String, Integer> pair = onlineScoresIterator.next();
            Integer currentScore = pair.getValue();
            logger.info("This pair has score " + currentScore);
            if (currentScore < lowestHighScore) {
                lowestHighScore = currentScore;
            }
        }
        logger.info("Lowest high score is " + lowestHighScore);
        if(score > lowestHighScore && score!=0){
            boolean valueRemoved = false;
            Iterator<Pair<String,Integer>> onlineScoresIterator2 = remoteScores.iterator();
            while (onlineScoresIterator2.hasNext() && !valueRemoved) {
                Pair<String, Integer> pair = onlineScoresIterator2.next();
                if(pair.getValue() == lowestHighScore){
                    remoteScores.remove(pair);
                    valueRemoved = true;
                    logger.info("Removed the value "+pair.getValue());
                }
            }
            remoteScores.add(new Pair<>(name, score));
            logger.info("Added new pair name: "+name+ " score: " +score);
            communicator.send("HISCORE "+name+":"+score);
        }


    }


    /**
     * Sorts the online scores highest to lowest, used so that if our score beats an online score it will properly be sorted on our display.
     */
    public void sortOnlineScores(){
        Comparator<Pair<String, Integer>> comparator = (pair1, pair2) -> pair2.getValue().compareTo(pair1.getValue());
        Collections.sort(remoteScores, comparator);
        logger.info("Sorted online scores");
    }






}