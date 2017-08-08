package dc.model;

import dc.controller.Movement;
import dc.view.TileBoardView;

/**
 * Created by Timo on 14.07.2016.
 */
public interface TileBoardModel<T> {
    void registerView(TileBoardView view);

    void unregisterView(TileBoardView view);

    int getWidth();

    int getHeight();


    T getBottomTileAt(int rowNr, int colNr);

    T getTopTileAt(int rowNr, int colNr);

    /**
     * Tells the model to move the player and all guards.
     * @param direction direction of the player
     */
    void move(Movement direction);

    /**
     * tells the model to handle a click on a tile and to check if the player can move there
     * @param row row
     * @param col collum
     */
    void handleClick(int row, int col);

    /**
     * Reloads the level
     * Clears all lists of guards, boxes and forcefields and reloads the entire map from scratch. Then tells all views to update themselves.
     */
    void resetLevel();

    /**
     * Starts a new level.
     * Clears all lists of guards, boxes und forcefields, checks if the level number is available and then calculates heigth and width to finally read the map.
     * @param no the number of the level
     */
    void startNewLevel(int no);
}

