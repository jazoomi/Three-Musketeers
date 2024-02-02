package assignment2;

import javafx.animation.PauseTransition;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import assignment2.ThreeMusketeers.GameMode;

public class View {

    ThreeMusketeers model;
    Stage stage;
    BorderPane borderPane;

    ThreeMusketeers.GameMode gameMode;

    Label messageLabel = new Label("");
    Label gameModeLabel = new Label("");
    BoardPanel boardPanel;
    Button undoButton, saveButton, restartButton;
    TextField saveFileNameTextField;
    Label saveFileErrorLabel;

    // must use these strings to update saveFileErrorLabel when saving a board
    static String saveFileSuccess = "Saved board";
    static String saveFileExistsError = "Error: File already exists";
    static String saveFileNotTxtError = "Error: File must end with .txt";

    public View(ThreeMusketeers model, Stage stage) {
        this.model = model;
        this.stage = stage;
        initUI();
        
    }


    /**
     * Initializes the UI and shows the main menu
     *
     * Contains default alignment and styles which can be modified
     */
    private void initUI() {
        borderPane = new BorderPane();

        // DO NOT MODIFY IDs
        borderPane.setId("BorderPane");  // DO NOT MODIFY ID
        gameModeLabel.setId("GameModeLabel");  // DO NOT MODIFY ID
        messageLabel.setId("MessageLabel"); // DO NOT MODIFY ID

        var threeMusketeersLabel = new Label("Three  Musketeers");
        // Default styles which can be modified

        borderPane.setStyle("-fx-background-color: #121212;");

        threeMusketeersLabel.setFont(new Font(30));
        threeMusketeersLabel.setStyle("-fx-text-fill: #e8e6e3");

        gameModeLabel.setText("");
        gameModeLabel.setFont(new Font(20));
        gameModeLabel.setStyle("-fx-text-fill: #e8e6e3");

        messageLabel.setFont(new Font(20));
        messageLabel.setStyle("-fx-text-fill: #e8e6e3");

        VBox labels = new VBox(threeMusketeersLabel, gameModeLabel);
        labels.setAlignment(Pos.TOP_CENTER);
        borderPane.setTop(labels);

        showMainMenu();

        var scene = new Scene(borderPane, 800, 800);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Updates the view to show the Main menu
     */
    private void showMainMenu(){
        ModeInputPanel modeInputPanel = new ModeInputPanel(this);

        VBox vBox = new VBox(20, messageLabel, modeInputPanel);
        vBox.setAlignment(Pos.CENTER);

        borderPane.setCenter(vBox);
        borderPane.setBottom(null);
    }

    /**
     * Updates the view to show the BoardPanel and game controls
     */
    private void showBoard() {
        boardPanel = new BoardPanel(this, model.getBoard());
        undoButton = new Button("Undo move");
        undoButton.setId("UndoButton");   // DO NOT MODIFY ID
        undoButton.setPrefSize(150, 50);
        undoButton.setFont(new Font(12));
        undoButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
        undoButton.setOnAction(e -> undo());
        setUndoButton();

        saveButton = new Button("Save board");
        saveButton.setId("SaveButton");  // DO NOT MODIFY ID
        saveButton.setPrefSize(150, 50);
        saveButton.setFont(new Font(12));
        saveButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
        saveButton.setOnAction(e -> saveBoard());

        String boardName = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + ".txt";
        saveFileNameTextField = new TextField(boardName);
        saveFileNameTextField.setId("SaveFileNameTextField");  // DO NOT MODIFY ID
        saveFileNameTextField.setStyle("-fx-background-color: #181a1b; -fx-text-fill: white;");

        saveFileErrorLabel = new Label("");
        saveFileErrorLabel.setId("SaveFileErrorLabel");  // DO NOT MODIFY ID
        saveFileErrorLabel.setStyle("-fx-text-fill: #e8e6e3;");

        restartButton = new Button("New game");
        restartButton.setId("RestartButton");  // DO NOT MODIFY ID
        restartButton.setPrefSize(150, 50);
        restartButton.setFont(new Font(12));
        restartButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
        restartButton.setOnAction(e -> restart());

        GridPane controls = new GridPane();
        controls.addRow(0, undoButton, restartButton);
        controls.addRow(1, saveFileNameTextField, saveButton);
        controls.add(saveFileErrorLabel, 0, 2, 2, 1);
        controls.setHgap(20);
        controls.setVgap(20);
        controls.setAlignment(Pos.BOTTOM_CENTER);
        GridPane.setHalignment(saveFileErrorLabel, HPos.CENTER);

        VBox vBox = new VBox(20, messageLabel, boardPanel, controls);
        vBox.setAlignment(Pos.CENTER);
        borderPane.setCenter(vBox);
        if (!(model.getCurrentAgent() instanceof HumanAgent)) {
            runMove();
        } else {
            messageLabel.setText(String.format("[%s turn] Select a piece", model.getBoard().getTurn().getType()));
        }
    }

    /**
     * Updates the messageLabel to the given String
     * @param messageLabel String to use for the text of the messageLabel
     */
    protected void setMessageLabel(String messageLabel) {
        this.messageLabel.setText(messageLabel);
    }

    /**
     * Handles running a move for both Human and Computer agents
     * messageLabel must always contain 'MUSKETEER' or 'GUARD'
     * Updates the view after performing the move, the board can be updated by calling BoardPanel.updateCells
     * Checks if the game is over and updates the view accordingly
     * On game over, messageLabel must contain the winner ('MUSKETEER' or 'GUARD') and all Cells must be disabled
     *
     * All Cells and Buttons must be disabled while a computer moves
     *
     */
    protected void runMove() { // TODO

    	
    	if (!this.model.isHumanTurn()) {
    		Move move = this.model.getCurrentAgent().getMove();
    		this.model.move(move);
            Cell Fcell = (Cell) this.boardPanel.getChildren().get(move.fromCell.getCoordinate().row * this.model.getBoard().size + move.fromCell.getCoordinate().col);
        	Fcell.setPiece(null);
            this.setMessageLabel(this.model.getBoard().getTurn().toString());
        	this.boardPanel.updateCells();
        	return;
    	}
    	List<Move> moves = this.model.getBoard().getPossibleMoves();
    	for (Move index: moves) {
            Cell Fcell = (Cell) this.boardPanel.getChildren().get(index.fromCell.getCoordinate().row * this.model.getBoard().size + index.fromCell.getCoordinate().col);
            Cell Tcell = (Cell) this.boardPanel.getChildren().get(index.toCell.getCoordinate().row * this.model.getBoard().size + index.toCell.getCoordinate().col);
    		if (Fcell.getUserData() != null && Tcell.getUserData() != null && Fcell.getUserData().equals("selected") && Tcell.getUserData().equals("chosen")) {
    			this.model.move(index);
    			Fcell.setUserData(null);
    			Fcell.setPiece(null);

    		}

    	}
    	
    	this.setMessageLabel(this.model.getBoard().getTurn().toString());
    	this.setUndoButton();
    	this.boardPanel.updateCells();
    }

    /**
     * Enables or disables the undo button depending on if there are moves to undo
     */
    protected void setUndoButton() { // TODO
        if (this.model.getMovesSize() == 0 || this.model.getBoard().isGameOver()) {
            this.undoButton.setDisable(true);
        }  else if (this.model.getMovesSize() == 1 && !(this.model.getMusketeerAgent() instanceof HumanAgent)){
        	this.undoButton.setDisable(true);
        }
        else {
            this.undoButton.setDisable(false);
        }
    }



    /**
     * Sets the GameMode to the given mode
     * Shows the SideSelector (Not needed for Human vs Human) or the BoardPanel accordingly
     * @param mode the selected GameMode
     */
    protected void setGameMode(ThreeMusketeers.GameMode mode) { // TODO


    	this.gameMode = mode;
    	if (mode == GameMode.Human) {

    		this.setSide(Piece.Type.MUSKETEER);
    		
    	}else {
    		this.showSideSelector();
    	}
    	
    }

    /**
     * Handles setting the correct agents based on the selected GameMode and the player's piece type by
     * calling model.selectMode
     * Shows the BoardPanel once the side and mode are selected
     * @param sideType the selected Piece Type for the human player in Human vs Computer games
     */
    protected void setSide(Piece.Type sideType) { // TODO

    	this.model.selectMode(gameMode, sideType);
        saveButton = new Button("Save board");
        restartButton = new Button("New game");
        this.model.getBoard().setTurn(Piece.Type.MUSKETEER);
    	this.showBoard();
    }


    /**
     * Handler for the Undo button
     * Undoes the latest move
     */
    private void undo() { // TODO

    	if (this.model.isHumansPlaying()) {

    		undoHelper();
        	this.setMessageLabel(this.model.getBoard().getTurn().toString());
    	}
    	else {
    		undoHelper();
    	}
    	this.boardPanel.updateCells();
    	this.setUndoButton();

    }

    private void undoHelper() {
    	this.model.undoMove();
    }
    /**
     * Handler for the Save Board button
     * Saves the current board state to a text file
     * Uses saveFileNameTextField to get user input for the name of the file (must end with ".txt")
     * Contains error handling to make sure the file does not already exist and the input ends with ".txt"
     * Updates saveFileErrorLabel with the appropriate message
     *
     * Must use saveFileSuccess, saveFileExistsError, or saveFileNotTxtError to set as the text of saveFileErrorLabel
     */
    private void saveBoard() { // TODO
    	String Text = this.saveFileNameTextField.getText();


    	if (Text.length() <4 || !(Text.substring(Text.length()-4).equals(".txt"))) {
    		this.saveFileErrorLabel.setText(this.saveFileNotTxtError);
    	}
    	else if (new File("boards/"+Text).exists()){
    		this.saveFileErrorLabel.setText(this.saveFileExistsError);

    	}
    	else {
    		this.model.getBoard().saveBoard(new File("boards/" + Text));
    		this.saveFileErrorLabel.setText(this.saveFileSuccess);
    	}
    	
    }

    /**
     * The handler for the New Game button
     * Restarts the game and updates the view accordingly
     * A new game is created and the main menu must be shown again
     */
    private void restart() { // TODO

    	model.restart();
    	this.showMainMenu();
    }

    /**
     * Updates the view to show the side selector
     */
    private void showSideSelector() {
        VBox vBox = new VBox(20, messageLabel, new SideInputPanel(this));
        vBox.setAlignment(Pos.CENTER);
        borderPane.setCenter(vBox);
    }


}
