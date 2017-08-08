package dc.controller;
import dc.view.TileBoardView;

/**
 * Interface for LevelController
 */


public interface TileBoardController {
    /**
     * handles a click
     * @param rowNr row of click
     * @param colNr collum of click
     */
    void handleClick(int rowNr, int colNr);

    /**
     * handles a keyboard command
     * @param view view it came from
     * @param direction direction of the command
     */
    void handleMove(TileBoardView view, Movement direction);

    /**
     * handles if the level is lost
     */
    void handleComplete(TileBoardView view);

    /**
     * Makes the controller usable for the view
     * @param view view it came from
     */
    void handleRestart(TileBoardView view);
}
