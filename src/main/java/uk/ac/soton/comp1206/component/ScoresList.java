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

public class ScoresList extends VBox {

    private static final Logger logger = LogManager.getLogger(ScoresList.class);
    SimpleListProperty<Pair<String,Integer>> scores;
    //Stores the data of the scores

    public ScoresList(){
        setPrefHeight(400);
        setPrefWidth(100);
        getStyleClass().add("VBox");
        setPadding(new Insets(10, 10, 10, 10));
        setSpacing(5);


        ObservableList<Pair<String, Integer>> observableList = FXCollections.observableArrayList();
        scores = new SimpleListProperty<>(observableList);
        ListView<Pair<String, Integer>> listView = new ListView<>(scores);
        //ListView is the visual component showing the scores
        //Customize how the listview appears
        listView.setCellFactory(lv -> new ListCell<Pair<String, Integer>>() {
            final FadeTransition fade = new FadeTransition(Duration.millis(1000), this);
            @Override
            public void updateItem(Pair<String, Integer> item, boolean empty) {
                super.updateItem(item, empty);
                // Customize how each score is displayed
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setOpacity(0);
                    setText(empty ? null : item.getKey() + ": " + item.getValue());
                    getStyleClass().add("listcell");
                    switch(getIndex()){
                        case 0: setStyle("-fx-text-color: pink;");
                        break;
                        case 1: setStyle("-fx-text-color: purple;");
                        break;
                        case 2: setStyle("-fx-text-color: red;");
                        break;
                        case 3: setStyle("-fx-text-color: orange;");
                        break;
                        case 4: setStyle("-fx-text-color: yellow;");
                        break;
                        case 5: setStyle("-fx-text-color: green;");
                        break;
                        case 6: setStyle("-fx-text-color: lime;");
                        break;
                        case 7: setStyle("-fx-text-color: olive;");
                        break;
                        case 8: setStyle("-fx-text-color: blue;");
                        break;
                        case 9: setStyle("-fx-text-color: aqua;");
                        break;
                        default: setStyle("fx-text-color: white");
                        break;
                    }

                    fade.setFromValue(0.0);
                    fade.setToValue(1.0);
                    fade.setDelay(Duration.millis(getIndex()* 200)); // Delay based on index
                    fade.play();
                }
            }
        });
        listView.getStyleClass().add("scorelist");
        Label localScores = new Label("Local Scores");
        localScores.getStyleClass().add("heading");
        this.getChildren().add(localScores);
        this.getChildren().add(listView);
    }

    public SimpleListProperty<Pair<String,Integer>> getScoresProperty(){
        return this.scores;
    }
}
