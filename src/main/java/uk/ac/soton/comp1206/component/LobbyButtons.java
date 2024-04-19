package uk.ac.soton.comp1206.component;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.LobbyJoinListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 A VBox containing buttons for the available servers in the lobby
 Listens to the list of lobbies returned by the communicator in the lobby scene
 */
public class LobbyButtons extends VBox {

    private static final Logger logger = LogManager.getLogger(LobbyButtons.class);
    SimpleListProperty<String> lobbies;

    LobbyJoinListener lobbyJoinListener;

    /**
     Defines the size and width of the VBox and styling
     Sets the list to call the update display method whenever it is changed
     */
    public LobbyButtons() {
        //800x600
        setPrefHeight(300);
        setPrefWidth(400);
        getStyleClass().add("VBox");
        setPadding(new Insets(1, 1, 1, 1));
        setSpacing(0);


        ObservableList<String> observableList = FXCollections.observableArrayList();
        lobbies = new SimpleListProperty<>(observableList);

        lobbies.addListener((ListChangeListener.Change<? extends String> change) -> {

            Platform.runLater(() -> {
                getChildren().clear();
                updateDisplay();
            });

        });
    }

    /**
     Gets the list of lobbies
     @return SimpleListProperty
     */
    public SimpleListProperty<String> getLobbies(){
        return this.lobbies;
    }

    /**
     * Updates the display of lobbies
     * Clears all current lobbies and adds the new ones from the list, styling them appropriately
     * This is called on a loop from the lobbyScene
     * */

    public void updateDisplay(){
        this.getChildren().clear();
        List<String> copyOfLobbies = new ArrayList<>(lobbies);
        Iterator<String> lobbiesIterator = copyOfLobbies.iterator();
        while(lobbiesIterator.hasNext()){
            String lobby = lobbiesIterator.next();
            Button button = new Button(lobby);
            button.getStyleClass().add("hostGame");
            button.setOnAction(event -> buttonClicked(event, lobby));
            button.setOnMouseEntered(e -> button.getStyleClass().add("hostGame:hover"));
            button.setOnMouseExited(e -> button.getStyleClass().add("hostGame"));
            this.getChildren().add(button);
        }
    }

    /**
     * Sets the listener
     * @param lobbyJoinListener which will be a method reference in the lobbyScene class
     * */

    public void setLobbyJoinListener(LobbyJoinListener lobbyJoinListener){
        this.lobbyJoinListener = lobbyJoinListener;
    }

    /**
     * @param gameName the name of the lobby
     * Calls the lobbyjoin listener to join the lobby
     */

    private void buttonClicked(ActionEvent event, String gameName){
        lobbyJoinListener.lobbyJoined(gameName);
    }

}