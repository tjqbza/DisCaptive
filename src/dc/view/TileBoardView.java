package dc.view;

import dc.model.List;

/**
 * Interface for Levelview
 */
public interface TileBoardView {

    /**
     * tells the view to update one tile
     * @param rowNr row
     * @param colNr col
     */
    void updateTile (int rowNr, int colNr);

    /**
     * updates the statusline
     * @param text text for the statusline
     */
    void updateStatusLine (String text);

    /**
     * handles the level in case it is won
     */
    void announceLevelComplete ();

    /**
     * handles the level in case it is lost
     */
    void announceLevelLost();

    /**
     * updates the view
     */
    void updateView();


}
