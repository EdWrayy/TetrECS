package uk.ac.soton.comp1206.game;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.ScoresScene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * Settings extension
 * This class contains static booleans about whether the music and sound is enabled or disabled
 * Upon loading the game, files are read to load what these booleans were set to last time the game was opened
 */
public class Settings {

    private static final Logger logger = LogManager.getLogger(Settings.class);
    /**
     * The static boolean musicActive which tells the media player whether play music or not
     */
    public static boolean musicActive;

    /**
     * The static boolean soundActive which tells the media player whether to play sound or not
     */

    public static boolean soundActive;

    /**
     * Static initialization block
     * Calls loadSettings method immediately
     */
    static {
    loadSettings();
    }

    /**
     * Reads the musicActive and soundActive files to find out what the boolean values are
     * If there is an IOException then it defaults to true
     */
    private static void loadSettings(){
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/IO/musicActive.txt"))) {
            String firstLine = reader.readLine();
            logger.info("musicActive.txt reads: "+firstLine);
            if( firstLine != null && firstLine.equals("true")){
                musicActive = true;
            }
            else{
                musicActive = false;
            }
        }
        catch(IOException e){
            musicActive = true;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/IO/soundActive.txt"))) {
            String firstLine = reader.readLine();
            logger.info("soundActive.txt reads: "+firstLine);
            if(firstLine != null && firstLine.equals("true")){
                soundActive = true;
            }
            else{
                soundActive = false;
            }
        }
        catch(IOException e){
            soundActive = true;
        }
    }

}
