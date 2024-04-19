package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.PerformanceSensitive;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The extension to the challenge scene, UI for the multiplayer mode
 * Has a simpler UI with less information, but shows the states and scores of all players in the lobby
 */

public class MultiplayerScene extends ChallengeScene{
    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     * */
    Label latestChat = new Label();
    Label chatExplanation = new Label("<In game chat. Press T to send a message>");

    VBox versusDisplay = new VBox();

    TextField messageBox = new TextField();

    Communicator communicator = gameWindow.getCommunicator();

    MultiplayerGame game;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    boolean chatOpen = false;


    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> requestLoopHandle = null;

    private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);

    /**
     * Constructs super class
     * Requests a few pieces immediately to populate the queue in multiplayer game, as this cannot be empty when the game is initialized.
     * @param gameWindow the gameWindow
     */
    public MultiplayerScene(GameWindow gameWindow) {
        super(gameWindow);
        communicator.send("PIECE");
        communicator.send("PIECE");
        communicator.send("PIECE");
        logger.info("multiPlayerScene created");
    }


    /**
     * Create and start a new game
     * Request a few more pieces so there is a buffer in the queue
     * This means if we place many pieces very quickly, we will not empty the queue before new ones are added
     * Setup the UI, removing some things from the previous one and adding the new players list lable
     * Add the chat window
     */
    @Override
    public void build(){
        communicator.send("PIECE");
        communicator.send("PIECE");
        communicator.send("PIECE");
        communicator.send("PIECE");
        communicator.send("PIECE");
        communicator.send("PIECE");
        this.game = new MultiplayerGame(5,5);
        game.setRequestPieceListener(this::requestPiece);
        game.setDieMessageListener(this::sendDieMessage);
        super.setupGame(game);
        super.build();



        mainPane.setLeft(versusDisplay);
        versusDisplay.setAlignment(Pos.CENTER_LEFT);

        VBox bottomScreen = new VBox();
        mainPane.setBottom(bottomScreen);
        bottomScreen.setAlignment(Pos.TOP_CENTER);
        VBox chat = new VBox();
        chat.setAlignment(Pos.TOP_CENTER);
        bottomScreen.getChildren().add(chat);
        bottomScreen.getChildren().add(timerBar);
        chatExplanation.getStyleClass().add("chat");
        messageBox.getStyleClass().add("inGameChatBox");
        latestChat.getStyleClass().add("chat");
        chat.getChildren().add(chatExplanation);
        chat.getChildren().add(latestChat);
        chat.getChildren().add(messageBox);


        messageBox.setVisible(false);
        chatExplanation.setVisible(true);
        latestChat.setVisible(true);
        messageBox.requestFocus();

        messageBox.setPromptText("Send a message");
        messageBox.setOnAction((event -> {
            sendMessage(messageBox.getText());
            event.consume();
        }));

        mainPane.getChildren().remove(labels);
        HBox newLabels = new HBox();
        newLabels.setAlignment(Pos.TOP_CENTER);
        Label multiPlayerMatch = new Label("Multiplayer Match");
        multiPlayerMatch.setAlignment(Pos.CENTER);
        livesLabel.getStyleClass().add("heading");
        livesLabel.setAlignment(Pos.CENTER_RIGHT);
        newLabels.getChildren().add(multiPlayerMatch);
        newLabels.getChildren().add(livesLabel);
        multiPlayerMatch.getStyleClass().add("title");
        mainPane.setTop(newLabels);
        newLabels.setAlignment(Pos.CENTER);
        newLabels.setPrefWidth(800);
        pieceDisplays.getChildren().remove(highScoreLabel);



        communicator.addListener(this::handleNetworkLogs);


    }


    /**
     * Regularly called
     * Requests the current players with their scores and lives
     * Updates the current board status, our lives and score
     */
    private void requestLoop(){

        //All updates TO the server
        communicator.send("SCORE "+game.getScoreValue());
        communicator.send("BOARD "+game.getGrid().get(0,0) + " "+game.getGrid().get(0,1) + " "+game.getGrid().get(0,2)+ " "
                                  +game.getGrid().get(1,0) + " "+game.getGrid().get(1,1) + " "+game.getGrid().get(1,1) + " "
                                  +game.getGrid().get(2,2) + " "+game.getGrid().get(2,2) + " "+game.getGrid().get(2,2) + " ");
        communicator.send("LIVES "+game.getLivesValue());


        //All updates FROM the server
        communicator.send("SCORES");
    }

    /**
     * Starts the game and sets our current aim to 0,0 for keyboard use
     * Starts the request loop
     */
    @Override
    public void initialise(){
        if (scene != null) {
        scene.setOnKeyPressed(this::handleKeyPress);
        }

        game.start();




        currentAimX = 0;
        currentAimY = 0;


        final Runnable requestLoop = new Runnable()
        {
            public void run() {
                requestLoop();
                logger.info("requestLoop has been called");
            }
        };
        requestLoopHandle = scheduler.scheduleAtFixedRate(requestLoop, 0, 400, TimeUnit.MILLISECONDS);

        logger.info("Initialised");
    }

    /**
     * @param event the key/button clicked
     * Handles what event should occur for each button pressed
     */
    @Override
    public void handleKeyPress(KeyEvent event) {
        if (Objects.requireNonNull(event.getCode()) == KeyCode.ESCAPE) {
            stopLoop();
            communicator.send("DIE");
            multimedia.stopMusic();
            gameWindow.startMenu();
        }
        else{
            super.handleKeyPress(event);
        }
    }

    /**
     * Opens the chat allowing us to type and send a message
     */
    @Override
    protected void openChat(){
        logger.info("Attempting to open chat");
        Platform.runLater(() -> {
            messageBox.setVisible(true);
            messageBox.requestFocus();
            latestChat.setVisible(false);
            chatExplanation.setVisible(false);
            chatOpen = true;
        });
    }


    /**
     * When the game ends it sends a DIE message to the server and loads the scoresScene
     * @param game the current game for scores
     */
    @Override
    public void gameOver(Game game){
        stopLoop();
        communicator.send("DIE");
        multimedia.stopMusic();
        gameWindow.startScores(game);
        scene.setOnKeyPressed(null);
    }

    /**
     * Requests a new piece from the server
     */
    private void requestPiece(){
        logger.info("piece request sent to server");
        communicator.send("PIECE");
    }

    /**
     * Sends a new message to the server and updates chat
     * @param message message sent
     */
    private void sendMessage(String message){
    logger.info("Sending message " + message);
    communicator.send("MSG "+message);
        Platform.runLater(() -> {
            messageBox.clear();
            messageBox.setVisible(false);
            latestChat.setVisible(true);
            chatOpen = false;
        });
    }

    /**
     * Called from the multiplayerGame class when lives run out
     * Calls the game over method
     */
    private void sendDieMessage(){
        gameOver(game);
    }

    /**
     * Forcibly stops the request loop
     */
    private void stopLoop() {
        scheduler.shutdown(); // Shut down the scheduler
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow(); // Force shutdown if not terminated
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        logger.info("Request loop has been stopped");
    }


    /**
     * Handles all possible messages received from the communicator
     * @param logs received from the communicator
     * If MSG, update chat with the received message
     * If SCORES, update the displayed players scores
     * If PIECE, enque the received piece number to the queue in the multiplayer Game class
     */
    private void handleNetworkLogs(String logs){
        if(logs.startsWith("MSG")){
            logger.info("Received Messages: " + logs);
            final String message = logs.replace("MSG ", "").trim();
            final String time = LocalTime.now().format(formatter);
            String[] parts = message.split(":");
            if (parts.length >= 2) {
                final String name = parts[0];
                final String msg = parts[1];
                logger.info("Displaying message "+ "["+time+"]"+" <"+name+">"+message);
                Platform.runLater(() -> {
                    final String finalMessage ="["+time+"]"+" <"+name+">"+msg;
                    latestChat.setText(finalMessage);
                });
            }
        }
        if(logs.startsWith("SCORES")){
            String playerdata = logs.replace("SCORES ", "").trim();
            String[] lines = playerdata.split("\n");
            Platform.runLater(() -> {versusDisplay.getChildren().clear();});
            game.playersData.clear();
            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length >= 3) {
                final String playerName = parts[0];
                final Integer score = Integer.parseInt(parts[1]);
                final String lives = parts[2];
                Pair<String, Integer> pair = new Pair<String,Integer>(playerName,score);
                game.playersData.add(pair);
                    Platform.runLater(() -> {
                        Label label = new Label(playerName+"\n"+score);
                        if(lives.equals("DEAD")){
                            label.getStyleClass().add("headingDEAD");
                            logger.info("DEAD HEADING ADDED");
                        }
                        else{label.getStyleClass().add("heading");}
                        versusDisplay.getChildren().add(label);
                        label.setAlignment(Pos.CENTER);
                    });

                }
            }
        }
        if(logs.startsWith("PIECE")){
            String piece = logs.replace("PIECE ", "").trim();
            logger.info("Received piece number: "+piece);
            int pieceValue = Integer.parseInt(piece);
            game.enquePiece(pieceValue);
        }
        if(logs.startsWith("ERROR")){}

    }



}
