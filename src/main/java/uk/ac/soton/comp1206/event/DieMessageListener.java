package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.MultiplayerGame;

/**
 * Called by the multiplayergame class when lives reach 0
 * Used to alert the communicator to send the DIE message and end the game
 */

public interface DieMessageListener {


    /**
     * Handle lives reaching 0 in multiplayer
     */
    public void sendDieMessage();
}
