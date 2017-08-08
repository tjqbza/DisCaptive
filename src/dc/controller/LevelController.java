package dc.controller;

import dc.model.Level;
import dc.view.TileBoardView;

/**
 * Controller for DisCaptive
 */


public class LevelController implements TileBoardController {
    private Level model;
    private boolean active;

    public LevelController(Level model) {
        this.model = model;
        active = true;
    }

    /**
     * handles the click of a specific tile
     * @param rowNr row
     * @param colNr collum
     */
    public void handleClick(int rowNr, int colNr) {
        if (!active) return;
        model.handleClick(rowNr, colNr);
    }

    /**
     * handles a movement command from the keyboard
     * @param view the view it came from
     * @param direction the direction of the command
     */
    public void handleMove(TileBoardView view, Movement direction) {
        if (!active) return;
        model.move(direction);

    }

    /**
     * after the level is done, the controller can't controll the level
     * @param view
     */
    public void handleComplete(TileBoardView view) {
        active = false;
    }

    public void handleRestart(TileBoardView view){
        active = true;
    }

}