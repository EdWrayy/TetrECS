package uk.ac.soton.comp1206.event;


/**
 * Called by the game class to update the progress of the timer bar for the visual representation in the challenge scene
 */
public interface GameLoopListener {



    /**
     *Handle the game progress being updated
     *  @param progress shows show close the timer is to 0, determining the length of the timer bar
     */
    public void progress(double progress);
}
