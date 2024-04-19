package uk.ac.soton.comp1206.component;

import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.ScoresScene;

/**
 The visual component for displaying scores
 This is used to display local scores stored on this device, online scores and also multiplayer game scores
 Subclass of VBox
 */

public class ScoresList extends VBox {

    private static final Logger logger = LogManager.getLogger(ScoresList.class);
    SimpleListProperty<Pair<String,Integer>> scores;
    //Stores the data of the scores

    /**
     Sets visual styling and size
     Creates a new observable list which is bound to the list of scores in the scoresScene, storing names and score.
     Creates a listview to display these scores and adds it to the VBox
     Sets focus as false on the ListView so that it doesn't interfere with action events on the scoresScene.
     */

    public ScoresList(){
        //800x600
        setPrefHeight(300);
        setPrefWidth(400);
        getStyleClass().add("VBox");
        setPadding(new Insets(1, 1, 1, 1));
        setSpacing(0);


        ObservableList<Pair<String, Integer>> observableList = FXCollections.observableArrayList();
        scores = new SimpleListProperty<>(observableList);
        ListView<Pair<String, Integer>> listView = new ListView<>(scores);
        listView.setFocusTraversable(false);
        //ListView is the visual component showing the scores
        //Customize how the listview appears
        listView.setCellFactory(lv -> new ListCell<Pair<String, Integer>>() {
            final FadeTransition fade = new FadeTransition(Duration.millis(1000), this);
            /**
             * @param item which is added to the list, a pair value of a string name and integer score
             * When items are added to the list, they are given a style and a unique color depending on their index
             * A fade animation is then applied to them
             * */
            @Override
            public void updateItem(Pair<String, Integer> item, boolean empty) {
                super.updateItem(item, empty);
                // Customize how each score is displayed
                if (empty) {
                    setText(null);
                    getStyleClass().add("listcell");
                } else {
                    setOpacity(0);
                    setText(empty ? null : item.getKey() + ": " + item.getValue());
                    getStyleClass().add("listcell");
                    switch(getIndex()){
                        case 0: setStyle("-fx-text-fill: #E75480;");
                        break;
                        case 1: setStyle("-fx-text-fill: purple;");
                        break;
                        case 2: setStyle("-fx-text-fill: red;");
                        break;
                        case 3: setStyle("-fx-text-fill: orange;");
                        break;
                        case 4: setStyle("-fx-text-fill: yellow;");
                        break;
                        case 5: setStyle("-fx-text-fill: green;");
                        break;
                        case 6: setStyle("-fx-text-fill: lime;");
                        break;
                        case 7: setStyle("-fx-text-fill: olive;");
                        break;
                        case 8: setStyle("-fx-text-fill: blue;");
                        break;
                        case 9: setStyle("-fx-text-fill: aqua;");
                        break;
                        default: setStyle("fx-text-fill: pink");
                        break;
                    }

                    fade.setFromValue(0.0);
                    fade.setToValue(1.0);
                    fade.setDelay(Duration.millis(getIndex()* 300)); // Delay based on index
                    fade.play();
                }
            }
        });
        listView.getStyleClass().add("scorelist");
        this.getChildren().add(listView);
    }

    /**
     * Returns the scores list, used for binding the list to the one held in the scoresScene
     * @return SimpleListProperty
     */

    public SimpleListProperty<Pair<String,Integer>> getScoresProperty(){
        return this.scores;
    }
}
