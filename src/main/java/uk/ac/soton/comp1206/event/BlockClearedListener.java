package uk.ac.soton.comp1206.event;

/**
 * The BlockCleared Listener Called by the game class when a block is cleared to alert the ChallengeScene to play the clearing animation.
 */

public interface BlockClearedListener {

    /**
     * Handle blockcleared
     * @param x coordinate of block
     * @param y coordinate of block
     */

    public void blockCleared(int x, int y);

}
