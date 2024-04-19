package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.Game;


/**
 * Called by the game class when the game ends to alert the challenge scene to switch to the scores scene
 */
public interface GameOverListener {

    /**
     * Handle game ending
     * @param game The game class passes itself as a parameter which is used to get the final score from
     */
    public void gameOver(Game game);
}
