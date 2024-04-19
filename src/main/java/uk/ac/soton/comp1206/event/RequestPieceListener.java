package uk.ac.soton.comp1206.event;

/**
 * Called by the multiplayerGame class after a piece is placed to request a new one
 * This is implemented in the multiplayerScene which has a communicator, and can request for a new piece from the server
 * This piece can then be passed back to the multiplayer game from there
 * */
public interface RequestPieceListener {

    /**
     * Handles a new piece request
     * */

    public void requestPiece();
}
