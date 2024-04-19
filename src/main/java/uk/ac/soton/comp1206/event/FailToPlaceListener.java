package uk.ac.soton.comp1206.event;


/**
 * Called by the game class when attempting to place a block at a location
 * Tells the challenge scene whether the block was successfully placed or not
 * This is used to choose whether to play the block placed sound or failed to place buzzer sound
 */
public interface FailToPlaceListener {


    /**
     * Handles whether a block actually placed or not after the grid is clicked
     * @param failedToPlace Can be true or false
     */
    public void failedToPlace(boolean failedToPlace);
}
