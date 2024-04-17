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

public class MultiplayerScene extends ChallengeScene{
    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     * */
    Label latestChat = new Label();
    Label chatExplanation = new Label("In game chat. Press T to send a message");

    VBox versusDisplay = new VBox();


    TextField messageBox = new TextField();

    Communicator communicator = gameWindow.getCommunicator();

    MultiplayerGame game;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    boolean chatOpen = false;


    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> requestLoopHandle = null;

    private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);

    public MultiplayerScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("multiPlayerScene created");
    }



    @Override
    public void build(){
        this.game = new MultiplayerGame(5,5);
        communicator.send("PIECE");
        communicator.send("PIECE");
        game.setRequestPieceListener(this::requestPiece);
        game.getPieceMultiplayer();
        game.getPieceMultiplayer();
        game.getPieceMultiplayer();
        game.getPieceMultiplayer();
        super.setupGame(game);
        super.build();





        mainPane.setLeft(versusDisplay);

        VBox bottomScreen = new VBox();
        mainPane.setBottom(bottomScreen);
        StackPane chat = new StackPane();
        //bottomScreen.getChildren().add(chat);
        bottomScreen.getChildren().add(timerBar);
        chatExplanation.getStyleClass().add("instructions");
        chat.getChildren().add(chatExplanation);
        chat.getChildren().add(latestChat);
        chat.getChildren().add(messageBox);

        messageBox = new TextField();
        messageBox.setVisible(false);
        chatExplanation.setVisible(true);
        latestChat.setVisible(true);

        messageBox.setPromptText("Send a message");
        messageBox.setOnAction((event -> {
            sendMessage(messageBox.getText());
        }));

        mainPane.getChildren().remove(labels);
        HBox newLabels = new HBox();
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



    private void requestLoop(){

        //All updates TO the server
        communicator.send("SCORE "+game.getScoreValue());
        communicator.send("BOARD "+game.getGrid().get(0,0) + " "+game.getGrid().get(0,1) + " "+game.getGrid().get(0,2)+ " "
                                  +game.getGrid().get(1,0) + " "+game.getGrid().get(1,1) + " "+game.getGrid().get(1,1) + " "
                                  +game.getGrid().get(2,2) + " "+game.getGrid().get(2,2) + " "+game.getGrid().get(2,2) + " ");
        communicator.send("LIVES "+game.getLivesValue());
        communicator.send("SCORE "+game.getScoreValue());

        //All updates FROM the server
        communicator.send("SCORES");
    }

    @Override
    public void initialise(){
        if (scene != null) {
        scene.setOnKeyPressed(this::handleKeyPress);
        }

        game.start();

        super.updateCurrentPieceDisplay(game.getCurrentPiece());
        super.updateNextPieceDisplay(game.getNextPiece());
        currentAimX = 0;
        currentAimY = 0;

        final Runnable requestLoop = new Runnable()
        {
            public void run() {
                requestLoop();
                logger.info("requestLoop has been called");
            }
        };
        requestLoopHandle = scheduler.scheduleAtFixedRate(requestLoop, 1000, 1000, TimeUnit.MILLISECONDS);

        logger.info("Initialised");
    }

    @Override
    public void handleKeyPress(KeyEvent keyEvent) {
        if(Objects.requireNonNull(keyEvent.getCode()) == KeyCode.T){
            openChat();
        }
        if(Objects.requireNonNull(keyEvent.getCode()) == KeyCode.ENTER){
            if(chatOpen){
                sendMessage(messageBox.getText());
            }
        }
        else{
            super.handleKeyPress(keyEvent);
        }

    }
    private void openChat(){
        Platform.runLater(() -> {
            messageBox.setVisible(true);
            latestChat.setVisible(false);
            chatExplanation.setVisible(false);
            chatOpen = true;
        });
    }

    private void requestPiece(){
        logger.info("piece request sent to server");
        communicator.send("PIECE");
    }

    private void sendMessage(String message){
    logger.info("Sending message " + message);
    communicator.send("MSG"+message);
        Platform.runLater(() -> {
            messageBox.clear();
            messageBox.setVisible(false);
            latestChat.setVisible(true);
            chatOpen = false;
        });
    }

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
                        label.getStyleClass().add("heading");
                        versusDisplay.getChildren().add(label);
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