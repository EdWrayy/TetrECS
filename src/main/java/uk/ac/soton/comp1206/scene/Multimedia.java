package uk.ac.soton.comp1206.scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.URL;


public class Multimedia {

    private MediaPlayer audioPlayer;
    private MediaPlayer musicPlayer;


    /** playAudio Method
     * @param audioFilePath
     * Checks if audio is already playing and stops it if so
     * Plays specified audio file
     * */
    public void playAudio(String audioFilePath) {
        if (audioPlayer != null) {
            audioPlayer.stop();
        }

        Media audioFile = new Media(new File(audioFilePath).toURI().toString());
        audioPlayer = new MediaPlayer(audioFile);
        audioPlayer.play();
    }


    /** playBackGroundMusic Method
     * @param musicFilePath
     * Checks if music is already playing and stops it if so
     * Plays specified music file on a continuous loop
     * */
    public void playBackgroundMusic(String musicFilePath) {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }

        Media musicFile = new Media(new File(musicFilePath).toURI().toString());
        musicPlayer = new MediaPlayer(musicFile);
        musicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loops
        musicPlayer.play();
    }
}
