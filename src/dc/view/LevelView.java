package dc.view;

import dc.controller.LevelController;
import dc.controller.Movement;
import dc.controller.TileBoardController;
import dc.model.List;
import dc.model.Tile;
import dc.model.TileBoardModel;
import dc.util.In;
import dc.util.Type;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Handles the visual part of DisCaptive
 */
public class LevelView extends Region implements TileBoardView {
    protected TileBoardModel<Tile> model;
    protected int rowCount, colCount;
    private Group[][] tileGraphic;
    private Text statusLine;
    private TileBoardController controller;
    private Button restart;
    private Button play;
    private ChoiceBox<String> dropDown;

    public LevelView(TileBoardModel<Tile> model, LevelController controller) {
        this.model = model;
        rowCount = model.getHeight();
        colCount = model.getWidth();
        updateView();
        this.controller = controller;
        setOnKeyPressed(new KeyPressHandler());
        setOnMouseClicked(new MouseClickHandler());
        model.registerView(this);
    }

    /**
     * handles a pressed key
     */
    private class KeyPressHandler implements EventHandler<KeyEvent> {
        public void handle(KeyEvent event) {
            KeyCode key = event.getCode();
            Movement dir = null;
            switch (key) {
                case UP:
                case KP_UP:
                case W:
                    dir = Movement.UP;
                    break;
                case DOWN:
                case KP_DOWN:
                case S:
                    dir = Movement.DOWN;
                    break;
                case LEFT:
                case KP_LEFT:
                case A:
                    dir = Movement.LEFT;
                    break;
                case RIGHT:
                case KP_RIGHT:
                case D:
                    dir = Movement.RIGHT;
                    break;
                default:
                    updateStatusLine("Not a valid key.");
            }
            if (dir != null) {
                controller.handleMove(LevelView.this, dir);
                event.consume();
            }
        }
    }

    /**
     * handles a mouseclick and calculates which tile was clicked
     */
    private class MouseClickHandler implements EventHandler<MouseEvent> {
        public void handle (MouseEvent event) {
            int rowNr = ((int) event.getY()) / 50;
            int colNr = ((int) event.getX()) / 50;
            if (0 <= rowNr && rowNr < rowCount &&
                    0 <= colNr && colNr < colCount) {
                controller.handleClick(rowNr, colNr);
                event.consume();
            }
        }
    }

    /**
     * Checks what kind of tile it is dealing with. Then checks the type and returns the graphic of the specific tile
     * @param tile the object that needs to be drawn.
     * @return returns the graphic of tile
     */
    protected Node makeTileGraphic(Tile tile) {
        switch (tile.getTileType()) {
            case "Player":
                if (tile.getType() == Type.NORTH)
                    return makePlayerGraphic(-90);
                else if (tile.getType() == Type.EAST)
                    return makePlayerGraphic(0);
                else if (tile.getType() == Type.SOUTH)
                    return makePlayerGraphic(90);
                else
                    return makePlayerGraphic(180);
            case "Box":
                return makeBoxGraphic();
            case "EmptyPassage":
                return makeEmptyPassageGraphic();
            case "Wall":
                return makeWallGraphic();
            case "PlayerGoal":
                return makeGoalGraphic();
            case "Key":
                return makeKeyGraphic();
            case "OpenLock":
                return makeLockGraphic(true);
            case "SwitchDirection":
                if (tile.getType() == Type.LEFT)
                    return makeTurnLeftGraphic();
                else
                    return makeTurnRightGraphic();
            case "ForceFieldOpener":
                return makeChipCardGraphic(tile.getType());
            case "Lock":
                return makeLockGraphic(false);
            case "BoxOverGoal":
                return makeBoxGraphic();
            case "Guard":
                if (tile.getType() == Type.EAST)
                    return makeGuardGraphic(0);
                else if (tile.getType() == Type.SOUTH)
                    return makeGuardGraphic(90);
                else if (tile.getType() == Type.WEST)
                    return (makeGuardGraphic(180));
                else if (tile.getType() == Type.NORTH)
                    return makeGuardGraphic(270);
                return makeEmptyPassageGraphic();
            case "Gap":
                return makeTrapGraphic(true);
            case "ForceField":
                if (tile.getType() == Type.BLUE)
                    return makeForceFieldGraphic(Color.BLUE, false);
                else if (tile.getType() == Type.RED)
                    return makeForceFieldGraphic(Color.RED, false);
                else if (tile.getType() == Type.BLUEOPEN) {
                    return makeForceFieldGraphic(Color.BLUE, true);
                } else
                    return makeForceFieldGraphic(Color.RED, true);
            case "BoxInGap":
                return makeTrapGraphic(false);

            default:
                return makeEmptyPassageGraphic();
        }

    }

    protected Node makeEmptyPassageGraphic() {
        return scaleDown(makeEmptyPassage());
    }

    protected Node makeWallGraphic() {
        return scaleDown(makeWall());
    }

    protected Node makePlayerGraphic(int i) {
        Group player = makePlayer();
        player.rotateProperty().setValue(i);
        return scaleDown(player);
    }

    protected Node makeChipCardGraphic(Type color) {
        if(color == Type.BLUE)
            return scaleDown(makeChipCard(Color.BLUE));
        else
            return scaleDown(makeChipCard(Color.RED));
    }

    protected Node makeKeyGraphic() {
        return scaleDown(makeKey());
    }

    protected Node makeTrapGraphic(boolean bol) {
        return scaleDown(makeTrap(bol));
    }

    protected Node makeGoalGraphic() {
        return scaleDown(makeGoal());
    }

    protected Node makeBoxGraphic() {
        return scaleDown(makeBox());
    }

    protected Node makeLockGraphic(boolean bol) {
        return scaleDown(makeLock(bol));
    }

    protected Node makeTurnRightGraphic() {
        return scaleDown(makeTurnRight());
    }

    protected Node makeTurnLeftGraphic() {
        return scaleDown(makeTurnLeft());
    }

    protected Node makeForceFieldGraphic(Color color, boolean open) {
        Group g = makeForceField(color, open);
        return scaleDown(g);
    }

    protected Node makeGuardGraphic(int i) {
        Group guard = makeGuard();
        guard.rotateProperty().setValue(i);
        return scaleDown(guard);
    }




    private Group makeGuard() {
        Color c = new Color(1f, 0f, 0f, 0f);
        Rectangle background = new Rectangle(150, 150);
        background.fillProperty().setValue(c);

        Polygon head = new Polygon(45, 35, 100, 35, 100, 55, 140, 75, 100, 95, 100, 115, 45, 115);
        head.fillProperty().set(Color.PINK);
        head.setStroke(Color.BLACK);
        head.setStrokeWidth(1);

        Rectangle shoulders = new Rectangle(55, 0, 55, 150);
        shoulders.fillProperty().set(Color.BLUE);
        shoulders.setArcWidth(15);
        shoulders.setArcHeight(15);
        return new Group(background, shoulders, head);
    }

    private Group makeForceField(Color color, boolean open) {
        Rectangle forceField = new Rectangle(150, 150, color);
        forceField.setStrokeWidth(3);
        forceField.setStrokeType(StrokeType.CENTERED);
        forceField.setStroke(Color.BLACK);

        Rectangle space = new Rectangle(100, 100, Color.BEIGE);
        space.translateXProperty().setValue(25);
        space.translateYProperty().setValue(25);
        space.setStrokeWidth(2);
        space.setStrokeType(StrokeType.CENTERED);
        space.setStroke(Color.BLACK);
        if (!open) {
            Line x1 = new Line(27, 27, 123, 123);
            x1.setStrokeWidth(4);

            Line x2 = new Line(123, 27, 27, 123);
            x2.setStrokeWidth(4);
            return new Group(forceField, space, x1, x2);
        } else {
            return new Group(forceField, space);
        }
    }

    private Group makeTurnRight() {
        Rectangle eSpace = new Rectangle(150, 150, Color.BEIGE);
        eSpace.setStrokeWidth(3);
        eSpace.setStrokeType(StrokeType.CENTERED);
        eSpace.setStroke(Color.BLACK);

        Line top = new Line(20, 20, 130, 20);
        Line right = new Line(130, 20, 130, 100);
        Line bottom = new Line(20, 130, 130, 130);
        Line left = new Line(20, 130, 20, 50);

        Polygon arrow1 = new Polygon(20, 30, 10, 50, 30, 50);
        Polygon arrow2 = new Polygon(130, 120, 120, 100, 140, 100);
        return new Group(eSpace, top, right, bottom, left, arrow1, arrow2);
    }

    private Group makeTurnLeft() {
        Rectangle eSpace = new Rectangle(150, 150, Color.BEIGE);
        eSpace.setStrokeWidth(3);
        eSpace.setStrokeType(StrokeType.CENTERED);
        eSpace.setStroke(Color.BLACK);

        Line top = new Line(20, 20, 130, 20);
        Line right = new Line(130, 50, 130, 130);
        Line bottom = new Line(20, 130, 130, 130);
        Line left = new Line(20, 100, 20, 20);

        Polygon arrow1 = new Polygon(20, 120, 10, 100, 30, 100);
        Polygon arrow2 = new Polygon(130, 30, 120, 50, 140, 50);
        return new Group(eSpace, top, right, left, bottom, arrow1, arrow2);
    }

    private Group makeLock(boolean open) {
        Rectangle box = new Rectangle(150, 150, Color.BEIGE);
        box.setStrokeWidth(3);
        box.setStrokeType(StrokeType.CENTERED);
        box.setStroke(Color.BLACK);

        Polygon poly = new Polygon(5, 50, 5, 100, 50, 145, 100, 145, 145, 100, 145, 50, 100, 5, 50, 5);
        poly.setStrokeWidth(8);
        poly.setStrokeType(StrokeType.CENTERED);
        poly.setStroke(Color.BLACK);
        poly.setFill(Color.BEIGE);

        Circle c = new Circle(75, 60, 10);
        Polygon triangle = new Polygon(75, 60, 60, 90, 90, 90);
        if (open) {
            poly.setStroke(Color.BURLYWOOD);
            c.setFill(Color.BURLYWOOD);
            triangle.setFill(Color.BURLYWOOD);
        }

        return new Group(box, poly, c, triangle);
    }

    private Group makeBox() {
        Rectangle box = new Rectangle(130, 130, Color.BROWN);
        box.setTranslateX(10);
        box.setTranslateY(10);

        box.setArcWidth(5);
        box.setArcHeight(5);
        box.setStroke(Color.BLACK);
        box.setStrokeWidth(2);
        box.setStrokeType(StrokeType.CENTERED);

        Rectangle wood = new Rectangle(20, 120, Color.BURLYWOOD);
        wood.setTranslateX(12);
        wood.setTranslateY(15);

        Rectangle wood2 = new Rectangle(20, 120, Color.BURLYWOOD);
        wood2.setTranslateX(117);
        wood2.setTranslateY(15);

        Rectangle crossover1 = new Rectangle(115, 20, Color.PERU);
        crossover1.setTranslateX(13);
        crossover1.setTranslateY(25);

        Rectangle crossover2 = new Rectangle(115, 20, Color.PERU);
        crossover2.setTranslateX(13);
        crossover2.setTranslateY(65);

        Rectangle crossover3 = new Rectangle(115, 20, Color.PERU);
        crossover3.setTranslateX(13);
        crossover3.setTranslateY(105);


        return new Group(box, crossover1, crossover2, crossover3, wood, wood2);
    }

    private Group makeGoal() {
        Rectangle eSpace = new Rectangle(150, 150, Color.BEIGE);
        eSpace.setStrokeWidth(3);
        eSpace.setStrokeType(StrokeType.CENTERED);
        eSpace.setStroke(Color.BLACK);

        Circle outter = new Circle(75, 75, 70, Color.BEIGE);
        outter.setStroke(Color.BLACK);
        outter.setStrokeWidth(2);

        Circle middle = new Circle(75, 75, 50, Color.BEIGE);
        middle.setStroke(Color.BLACK);
        middle.setStrokeWidth(2);

        Circle inner = new Circle(75, 75, 30, Color.BEIGE);
        inner.setStroke(Color.BLACK);
        inner.setStrokeWidth(2);

        return new Group(eSpace, outter, middle, inner);
    }

    private Group makeTrap(boolean active) {
        Rectangle rec = new Rectangle(150, 150, Color.BROWN);

        rec.setStrokeWidth(3);
        rec.setStrokeType(StrokeType.CENTERED);
        rec.setStroke(Color.BLACK);

        Rectangle trap = new Rectangle(120, 120);
        trap.setTranslateX(15);
        trap.setTranslateY(15);

        if (!active) {
            trap.setFill(Color.BEIGE);
        }

        return new Group(rec, trap);
    }

    private Group makeKey() {

        Rectangle background = new Rectangle(150, 150);
        background.fillProperty().setValue(Color.BEIGE);
        background.setStrokeWidth(3);
        background.setStrokeType(StrokeType.CENTERED);
        background.setStroke(Color.BLACK);

        Polygon key = new Polygon(30, 60, 30, 85, 45, 75, 60, 85, 75, 75, 85, 90, 100, 90, 100, 105, 150, 105, 150, 45, 100, 45, 100, 60);
        key.setStroke(Color.SILVER.darker());
        key.setFill(Color.SILVER);

        key.translateXProperty().setValue(-15);
        Circle c = new Circle(140, 55, 5);
        c.setFill(Color.BEIGE);
        c.translateXProperty().setValue(-15);

        return new Group(background, key, c);
    }

    private Group makeChipCard(Color color) {

        Rectangle background = new Rectangle(150, 150);
        background.fillProperty().setValue(Color.BEIGE);
        background.setStrokeWidth(3);
        background.setStrokeType(StrokeType.CENTERED);
        background.setStroke(Color.BLACK);

        Rectangle chipCard = new Rectangle(100, 50);
        chipCard.setTranslateY(50);
        chipCard.setTranslateX(25);
        chipCard.setArcHeight(25);
        chipCard.setArcWidth(25);
        chipCard.setFill(color);
        chipCard.setStroke(color.darker());
        chipCard.setStrokeWidth(4);

        return new Group(background, chipCard);
    }

    private Group makePlayer() {
        Color c = new Color(1f, 0f, 0f, 0f);
        Rectangle background = new Rectangle(150, 150);
        background.fillProperty().setValue(c);

        Circle head = new Circle(22, 75, 30);
        head.setFill(Color.web("#840"));
        head.setStroke(Color.BLACK);
        head.setStrokeWidth(1);


        Rectangle shoulders = new Rectangle(44, 150);
        shoulders.setArcWidth(44);
        shoulders.setArcHeight(44);
        shoulders.setFill(Color.web("#c00"));
        shoulders.setStroke(Color.BLACK);
        shoulders.setStrokeWidth(1);

        Rectangle cap = new Rectangle(22, 50, 40, 50);
        cap.setArcWidth(24);
        cap.setArcHeight(36);
        cap.setFill(Color.web("#00c"));
        cap.setStroke(Color.BLACK);
        cap.setStrokeWidth(1);

        head.translateXProperty().setValue(53);
        shoulders.translateXProperty().setValue(53);
        cap.translateXProperty().setValue(53);
        return new Group(background, shoulders, cap, head);
    }

    private Group makeWall() {
        Rectangle wall = new Rectangle(150, 150, Color.GRAY);

        wall.setStrokeWidth(3);
        wall.setStrokeType(StrokeType.CENTERED);
        wall.setStroke(Color.BLACK);
        return new Group(wall);
    }

    private Group makeEmptyPassage() {
        Rectangle eSpace = new Rectangle(150, 150, Color.BEIGE);
        eSpace.setStrokeWidth(3);
        eSpace.setStrokeType(StrokeType.CENTERED);
        eSpace.setStroke(Color.BLACK);
        return new Group(eSpace);
    }

    /**
     * All groups are made 150x150, so this method scales it down to a third of that
     * @param group the graphic
     * @return returns the scaled down graphic
     */
    private Group scaleDown(Group group) {
        group.scaleXProperty().setValue(1.0 / 3.0);
        group.scaleYProperty().setValue(1.0 / 3.0);
        return group;
    }

    /**
     * Updates the entire view.
     */
    public void updateView(){
        getChildren().clear();
        tileGraphic = new Group[rowCount][colCount];
        ObservableList<Node> myChildren = getChildren();
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                Tile mt = model.getBottomTileAt(row, col);
                Group g = new Group(makeTileGraphic(mt));
                mt = model.getTopTileAt(row, col);
                if (mt == (null)) {
                } else {
                    g.getChildren().add(makeTileGraphic(mt));
                }

                tileGraphic[row][col] = g;
                myChildren.add(g);
            }
            statusLine = new Text("Welcome to DisCaptive!");
            myChildren.add(statusLine);

            restart = new Button("restart");
            restart.relocate(20, rowCount * 50 + 55);
            myChildren.add(restart);
            restart.setOnAction( event -> restartMap());

            play = new Button("Play");
            play.setLayoutX(225);
            play.setLayoutY(rowCount * 50 + 55);
            myChildren.add(play);
            play.setOnAction(event -> changeLevel());

            dropDown = new ChoiceBox<>();
            dropDown.relocate(130, rowCount * 50 + 55);
            updateDropDown();
            myChildren.add(dropDown);
            play.getScene();
        }
    }

    /**
     * Changes the selected level.
     * It first checks if there is a level selected yet, if not it tells the statusline to update.
     * If a level is selected it takes the level-number from the string and tells the model to start another level.
     * Once the model has changed the level, the view will clear everything off the screen and draws the new level.
     * Then it resizes the window and tells the controller to become active again.
     */
    private void changeLevel(){
        String p = dropDown.getValue();
        if(p == null)
            updateStatusLine("You have not selected a level yet.");
        else{
            p = p.substring(6);
            int a = Integer.parseInt(p);
            model.startNewLevel(a);
            rowCount = model.getHeight();
            colCount = model.getWidth();
            getChildren().clear();
            updateView();
            play.getScene().getWindow().sizeToScene();
            controller.handleRestart(this);
        }
    }

    /**
     * Restarts the map.
     * Tells the model that the map shall be restarted. The model will then restart the map and tell all views associated with it to update their window.
     * Then tells the controller to become active again, incase it was deactivated, because the level was won/lost.
     */
    private void restartMap(){
        model.resetLevel();
        controller.handleRestart(this);

    }

    /**
     * Updates the dropdown-menu for the levels.
     * Checks for all levels that are saved as "Level" int ".txt" between 1 and 1000 and adds them to the dropdown menu.
     */
    private void updateDropDown(){
        for(int i = 0; i<1000; i++){
            In.open("Level" + i + ".txt");
            if(In.done())
                dropDown.getItems().add("Level " + i);
        }
    }

    /**
     * Updates the statusline
     * @param text the text for the statusline
     */
    public void updateStatusLine(String text) {
        statusLine.setText(text);
    }

    /**
     * Handles a won Level
     * Updates the statusline and disconnects the controller from the view.
     */
    public void announceLevelComplete() {
        statusLine.setFill(Color.GREEN);
        statusLine.setFont(Font.font("System", FontWeight.BOLD, 15));
        statusLine.setText("You escaped prison. Don't come back!");
        controller.handleComplete(this);

    }

    /**
     * handles a lost level
     * Updates the statusline and disconnects the controller from the view.
     */
    public void announceLevelLost() {
        statusLine.setFill(Color.RED);
        statusLine.setFont(Font.font("System", FontWeight.BOLD, 15));
        statusLine.setText("You ain't going nowhere!");
        controller.handleComplete(this);
    }

    /**
     * Updates a specific tile that has been changed in the model
     * @param row row
     * @param col collum
     */
    public void updateTile(int row, int col) {
        ObservableList<Node> myChildren = getChildren();
        Tile mt = model.getBottomTileAt(row, col);
        Group g = new Group(makeTileGraphic(mt));
        mt = model.getTopTileAt(row, col);
        if (mt == (null)) {
            //myChildren.remove(1);
        } else {
            g.getChildren().add(makeTileGraphic(mt));
        }

        tileGraphic[row][col] = g;
        myChildren.add(g);
    }

    /**
     * compute the prefered Width
     * @param height amout of collums
     * @return width in pixel
     */
    protected double computePrefWidth(double height) {
        return colCount * 50;
    }

    /**
     * compute the prefered heigth
     * @param width amout of rows
     * @return height in pixel
     */
    protected double computePrefHeight(double width) {
        return rowCount * 50 + 100;
    }

    /**
     * Lays out the children, statusline, choicebox and buttons.
     * Lays out the children and relocates, resizes and restyles the statusline, restart-button and play-button.
     */
    protected void layoutChildren() {
        super.layoutChildren();
        for (int row = 0; row < rowCount; row++)
            for (int col = 0; col < colCount; col++) {
                Group g = tileGraphic[row][col];
                g.relocate(col * 50, row * 50);
            }
        statusLine.relocate(10, rowCount * 50 + 20);
        statusLine.resize(colCount * 50, 30);
        statusLine.setWrappingWidth(colCount * 50);
        statusLine.setTextAlignment(TextAlignment.CENTER);
        statusLine.setStyle("-fx-font-size: 20");

        restart.resize(100, 40);
        restart.setTextAlignment(TextAlignment.CENTER);
        restart.setStyle("-fx-font-size: 20");

        play.resize(100, 40);
        play.setTextAlignment(TextAlignment.CENTER);
        play.setStyle("-fx-font-size: 20");

    }


}

