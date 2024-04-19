package uk.ac.soton.comp1206.scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import uk.ac.soton.comp1206.game.Settings;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.File;
import java.net.URL;

/**
 * Handles playing music and sounds
 */
public class Multimedia {

    private MediaPlayer audioPlayer;
    private MediaPlayer musicPlayer;

    /**
     * Checks if audio is already playing and stops it if so
     * Plays specified audio file
     * @param audioFilePath filePath
     */
    public void playAudio(String audioFilePath) {
        if (audioPlayer != null) {
            audioPlayer.stop();
        }

        Media audioFile = new Media(new File(audioFilePath).toURI().toString());
        audioPlayer = new MediaPlayer(audioFile);
        if(Settings.soundActive) {
            audioPlayer.play();
        }
    }


    /**
     * Checks if music is already playing and stops it if so
     * Plays specified music file on a continuous loop
     * @param musicFilePath filePath
     */
    public void playBackgroundMusic(String musicFilePath) {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
        Media musicFile = new Media(new File(musicFilePath).toURI().toString());
        musicPlayer = new MediaPlayer(musicFile);
        musicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loops
        if(Settings.musicActive) {
            musicPlayer.play();
        }
    }

    /**
     * Forcibly stop current music if any is playing
     */
    public void stopMusic(){
        musicPlayer.stop();
    }
}
