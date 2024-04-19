package uk.ac.soton.comp1206.event;

/**
 * Called by the LobbyButtons class when a button is clicked.
 * This alerts the lobbyScene to get the communicator to send JOIN and to display the lobby chat
 */
public interface LobbyJoinListener {

    /**
     * Handle a request to join a lobby
     * @param lobbyName the name of the lobby we are trying to join
     */
    public void lobbyJoined(String lobbyName);

}
