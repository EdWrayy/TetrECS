package uk.ac.soton.comp1206.game;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.ScoresScene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Settings {

    private static final Logger logger = LogManager.getLogger(Settings.class);
    public static boolean musicActive;

    public static boolean soundActive;

    static {
    loadSettings();
    }

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
