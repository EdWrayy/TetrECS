package uk.ac.soton.comp1206.scene;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.LobbyButtons;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LobbyScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(LobbyScene.class);

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> requestLoopHandle = null;

    Multimedia multimedia;

    Communicator communicator;

    SimpleListProperty<String> lobbies;

    LobbyButtons lobbyButtons;



    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */

    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating multiplayer scene");
    }

    @Override
    public void initialise() {
        final Runnable requestLoop = new Runnable()
        {
            public void run() {
                requestLoop();
                logger.info("requestLoop has been called");
            }
        };
        requestLoopHandle = scheduler.scheduleAtFixedRate(requestLoop, 1000, 1000, TimeUnit.MILLISECONDS);

        if (scene != null) {
            scene.setOnKeyPressed(this::handleKeyPress);
        }
    }

    @Override
    public void build() {
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        var lobbyPane = new StackPane();
        lobbyPane.setMaxWidth(gameWindow.getWidth());
        lobbyPane.setMaxHeight(gameWindow.getHeight());
        lobbyPane.getStyleClass().add("menu-background");
        root.getChildren().add(lobbyPane);

        //The lobbyPane is basically just for the background, the mainPane is for the content.
        lobbies = new SimpleListProperty<>(FXCollections.observableArrayList());
        lobbyButtons = new LobbyButtons();
        lobbyButtons.getLobbies().bindBidirectional(this.lobbies);
        lobbyButtons.setLobbyJoinListener(this::lobbyJoined);


        var mainPane = new BorderPane();
        lobbyPane.getChildren().add(mainPane);

        HBox titleWrapper = new HBox();
        titleWrapper.setAlignment(Pos.CENTER);
        titleWrapper.setPadding(new Insets(20,20,20,20));
        URL imageUrl = getClass().getResource("/images/TetrECS.png");
        Image titleImage = new Image(imageUrl.toString());
        ImageView title = new ImageView(titleImage);
        title.setFitHeight(200);
        title.setFitWidth(300);
        title.setPreserveRatio(true);
        titleWrapper.getChildren().add(title);
        mainPane.setTop(titleWrapper);

        VBox gamesView = new VBox();
        gamesView.setPadding(new Insets(10,10,10,10));
        gamesView.setSpacing(20);
        Label playMultiplayer = new Label("Play Multiplayer");
        playMultiplayer.getStyleClass().add("multiplayer");
        Button hostNewGame = new Button("Host New Game");
        hostNewGame.getStyleClass().add("hostGame");
        hostNewGame.setOnAction(this::hostGame);
        hostNewGame.setOnMouseEntered(e -> hostNewGame.getStyleClass().add("hostGame:hover"));
        hostNewGame.setOnMouseExited(e -> hostNewGame.getStyleClass().add("hostGame"));
        Label currentGames = new Label("Current Games");
        currentGames.getStyleClass().add("currentGames");


        gamesView.getChildren().addAll(playMultiplayer, hostNewGame, currentGames, lobbyButtons);


        mainPane.setLeft(gamesView);




        multimedia = new Multimedia();
        multimedia.playBackgroundMusic("src/main/resources/music/menu.mp3");

        communicator = gameWindow.getCommunicator();
        communicator.addListener(this::loadLobbies);
    }

    private void handleKeyPress(KeyEvent keyEvent){
        switch(keyEvent.getCode()) {
            case ESCAPE:
                stopLoop();
                multimedia.stopMusic();
                gameWindow.startMenu();
                break;
        }
    }
    private void requestLoop(){
        communicator.send("LIST");
    }



    private void loadLobbies(String lobbies) {
        String channels = lobbies.replace("CHANNELS ", "").trim();
        String[] lines = channels.split("\n");
        this.lobbies.clear();
        for (String line : lines) {
            this.lobbies.add(line);
            logger.info("Lobby names are "+ line);
        }
    }



    private void lobbyJoined(String gameName){
        logger.info("Attempting to join "+gameName);
    }

    private void stopLoop() {
        scheduler.shutdown(); // Shut down the scheduler
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow(); // Force shutdown if not terminated
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        logger.info("Game loop has been stopped");
    }

    private void hostGame(ActionEvent event){

    }



}
