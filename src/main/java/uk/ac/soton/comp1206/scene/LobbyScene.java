package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.LobbyButtons;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.lang.reflect.Array;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Shows available lobbies, allows the user to join an available lobby or host a new one
 * When in lobby shows chat and allows user to send chats and change nickname
 * If the user is hosting they can start the multiplayer game
 */
public class LobbyScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(LobbyScene.class);

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> requestLoopHandle = null;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    Multimedia multimedia;

    Communicator communicator;

    SimpleListProperty<String> lobbies;

    LobbyButtons lobbyButtons;

    TextField inputField = new TextField();

    VBox chatView = new VBox();

    boolean isHost = false;

    Button startButton;

    Label lobbyName = new Label("");
    HBox playerList = new HBox();

    boolean inLobby = false;

    String nickName;

    VBox chatMessages = new VBox();

    Label lobbyDescription = new Label("Welcome to the Lobby \n Type /nick NewName to change your name \n \n");



    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */

    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating multiplayer scene");
    }

    /**
     * Intialise the loop which requests current lobbies
     */
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

    /**
     * Layout the UI and add some functionality to the dialog host new game button
     */
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
        lobbyButtons.setLobbyJoinListener(this::joinLobby);


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
        gamesView.setPrefWidth(350);
        Label playMultiplayer = new Label("Play Multiplayer");
        playMultiplayer.getStyleClass().add("multiplayer");
        Button hostNewGame = new Button("Host New Game");
        hostNewGame.getStyleClass().add("hostGame");
        hostNewGame.setOnAction(this::getInput);
        inputField.setPromptText("Enter a lobby name: ");
        inputField.getStyleClass().add("gameInputField");
        inputField.setVisible(false);
        inputField.setOnAction(event -> {
            hostGame(inputField.getText());
            inputField.clear();
            inputField.setVisible(false);
        });
        hostNewGame.setOnMouseEntered(e -> hostNewGame.getStyleClass().add("hostGame:hover"));
        hostNewGame.setOnMouseExited(e -> hostNewGame.getStyleClass().add("hostGame"));
        Label currentGames = new Label("Current Games");
        currentGames.getStyleClass().add("currentGames");


        gamesView.getChildren().addAll(playMultiplayer, hostNewGame, inputField, currentGames, lobbyButtons);



        chatView.setPadding(new Insets(10,10,10,10));
        chatView.setSpacing(10);
        chatView.setVisible(false);
        chatView.getStyleClass().add("chatView");
        chatView.setPrefWidth(400);
        chatView.setAlignment(Pos.CENTER);
        lobbyName.getStyleClass().add("lobbyName");
        lobbyName.setAlignment(Pos.CENTER);
        playerList.getStyleClass().add("playerList");



        ScrollPane chatWindow = new ScrollPane();
        chatWindow.getStyleClass().add("chatWindow");
        chatWindow.setFitToWidth(true);
        chatWindow.setVvalue(1.0);
        chatWindow.setContent(chatMessages);
        chatWindow.setPrefHeight(300);

        lobbyDescription.getStyleClass().add("chat");
        chatMessages.getStyleClass().add("chatMessages");
        chatMessages.setFillWidth(true);
        chatMessages.getChildren().add(lobbyDescription);

        TextField messageBox = new TextField();
        messageBox.setPromptText("Send a message");
        messageBox.setOnAction((event -> {
            sendMessage(messageBox.getText());
            messageBox.clear();
        }));



        Button leaveButton = new Button("Leave Game");
        leaveButton.getStyleClass().add("hostGame");
        leaveButton.setOnMouseEntered(e -> leaveButton.getStyleClass().add("hostGame:hover"));
        leaveButton.setOnMouseExited(e -> leaveButton.getStyleClass().add("hostGame"));
        leaveButton.setOnAction(e -> leaveLobby());
        leaveButton.setAlignment(Pos.CENTER_LEFT);

        startButton = new Button("Start Game");
        startButton.getStyleClass().add("hostGame");
        startButton.setOnMouseEntered(e -> startButton.getStyleClass().add("hostGame:hover"));
        startButton.setOnMouseExited(e -> startButton.getStyleClass().add("hostGame"));
        startButton.setOnAction(e -> startGame());
        startButton.setAlignment(Pos.CENTER_RIGHT);
        startButton.setVisible(false);

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.BOTTOM_CENTER);
        buttons.getChildren().addAll(leaveButton,startButton);
        chatView.getChildren().addAll(lobbyName,playerList, chatWindow, messageBox, buttons);





        mainPane.setLeft(gamesView);
        mainPane.setRight(chatView);


        multimedia = new Multimedia();
        multimedia.playBackgroundMusic("src/main/resources/music/menu.mp3");

        communicator = gameWindow.getCommunicator();
        communicator.addListener(this::handleNetworkLogs);
    }

    /**
     * When the ESCAPE key is pressed return to the main menu and stop the loop
     * @param keyEvent keyboard input
     */
    private void handleKeyPress(KeyEvent keyEvent){
        switch(keyEvent.getCode()) {
            case ESCAPE:
                stopLoop();
                multimedia.stopMusic();
                gameWindow.startMenu();
                break;
        }
    }

    /**
     * Regularly request available lobbies and if currently in a lobby, request the current USERS
     */
    private void requestLoop(){
        communicator.send("LIST");
        if(inLobby){
            communicator.send("USERS");
        }
    }


    /**
     * When any communication is received from the communicator listener, this method is called
     * @param logs the communication received
     * If it is CHANNELS then we display the current available lobbies
     * If it is USERS then we display the current available users
     * If it is MSG then we add the received messsage to the chat window
     * If it is START then we start the game
     */


    private void handleNetworkLogs(String logs) {
        if(logs.startsWith("CHANNELS")){
            logger.info("Received Channels: " + logs);
            String channels = logs.replace("CHANNELS ", "").trim();
            String[] lines = channels.split("\n");
            this.lobbies.clear();
            for (String line : lines) {
                this.lobbies.add(line);
                logger.info("Lobby names are " + line);
            }
        }
        if(logs.startsWith("USERS")){
            logger.info("Received Users: " + logs);
            String users = logs.replace("USERS ", "").trim();
            String[] lines = users.split("\n");
            Platform.runLater(() -> {
                playerList.getChildren().clear();
            });
            for (String line : lines) {
                final String lineF = line + "   ";
                logger.info("Displaying user "+ line);
                Platform.runLater(() -> {
                    final Label label = new Label(lineF);
                    label.getStyleClass().add("playerNames");
                    playerList.getChildren().add(label);
                });
            }
        }
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
                    final Label label = new Label("["+time+"]"+" <"+name+">"+msg);
                    label.getStyleClass().add("instructions");
                    chatMessages.getChildren().add(label);
                });

            }
        }
        if(logs.startsWith("START")){
            logger.info("Switching scenes");
            loadMultiplayerGame();
        }
        if(logs.startsWith("ERROR")){
            logger.info("ERROR OCCURED");
        }
    }


    /**
     * Attempt to join a given lobby
     * Figure out if we are the host
     * Display the lobby window (chat, buttons)
     * @param gameName name of lobby
     */
    private void joinLobby(String gameName){
        logger.info("Attempting to join "+gameName);
        if(!inLobby) {
            chatMessages.getChildren().clear();
            chatView.setVisible(true);
            lobbyName.setText(gameName);
            chatMessages.getChildren().add(lobbyDescription);
            communicator.send("JOIN " + gameName);
            inLobby = true;
            if(isHost){
                startButton.setVisible(true);
            }
            else{
                startButton.setVisible(false);
            }
        }
    }

    /**
     * Stop the communicator request loop
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
     * Get the name of the lobby entered when trying to create a new one
     * @param event enter button
     */
    private void getInput(ActionEvent event){
        inputField.setVisible(true);
        inputField.requestFocus();
    }

    /**
     * Send a new message to the communicator including the users nickname
     * @param message message entered
     */
    private void sendMessage(String message){
        if(message.startsWith("/nick")){
            nickName = message.replace("/nick ", "").trim();
            communicator.send("NICK "+nickName);
        }
        else {
            communicator.send("MSG " + message);
        }
    }

    /**
     * Request to create and then join a new lobby as the host
     * @param gameName lobby name
     */
    private void hostGame(String gameName){
        if(!inLobby) {
            logger.info("Hosting " + gameName);
            communicator.send("CREATE " + gameName);
            isHost = true;
            joinLobby(gameName);
        }
    }

    /**
     * Leave a lobby if we are currently in one
     */

    private void leaveLobby(){
        chatView.setVisible(false);
        communicator.send("PART");
        inLobby=false;
        isHost=false;
        Platform.runLater(() -> {
            playerList.getChildren().clear();
            chatMessages.getChildren().clear();
        });
    }

    /**
     * Start a game if we are host
     */
    private void startGame(){
        communicator.send("START");
    }

    /**
     * Change the window to the multiplayer game
     */
    private void loadMultiplayerGame(){
        Platform.runLater(() -> {
            stopLoop();
            multimedia.stopMusic();
            gameWindow.startMultiPlayerGame();
        });

    }



}
