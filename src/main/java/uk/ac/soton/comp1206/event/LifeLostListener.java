package uk.ac.soton.comp1206.event;

/**
 * Called by the game class when a life is lost to play the life lost sound effect
 */
public interface LifeLostListener{

    /**
     * Handles a life lost
     */
    public void lifeLost();
}
