package assignment2;

import java.util.List;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

public class BoardPanel extends GridPane implements EventHandler<ActionEvent> {

    private final View view;
    private final Board board;

    /**
     * Constructs a new GridPane that contains a Cell for each position in the board
     *
     * Contains default alignment and styles which can be modified
     * @param view
     * @param board
     */
    public BoardPanel(View view, Board board) {
        this.view = view;
        this.board = board;

        // Can modify styling
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: #181a1b;");
        int size = 550;
        this.setPrefSize(size, size);
        this.setMinSize(size, size);
        this.setMaxSize(size, size);

        setupBoard();
        updateCells();
    }


    /**
     * Setup the BoardPanel with Cells
     */
    private void setupBoard(){ // TODO
    	
       for (int row = 0; row < board.size; row++) {
            for (int col = 0; col < board.size; col++) {
                Cell cell = new Cell(new Coordinate(row, col));
                cell.setOnAction(this);
               this.add(cell, col, row);

            }
        }
       
    	
    }

    /**
     * Updates the BoardPanel to represent the board with the latest information
     *
     * If it's a computer move: disable all cells and disable all game controls in view
     *
     * If it's a human player turn and they are picking a piece to move:
     *      - disable all cells
     *      - enable cells containing valid pieces that the player can move
     * If it's a human player turn and they have picked a piece to move:
     *      - disable all cells
     *      - enable cells containing other valid pieces the player can move
     *      - enable cells containing the possible destinations for the currently selected piece
     *
     * If the game is over:
     *      - update view.messageLabel with the winner ('MUSKETEER' or 'GUARD')
     *      - disable all cells
     */
    protected void updateCells(){ // TODO
    	
 
    	String text = null;
    	
       for (int row = 0; row < board.size; row++) {
            for (int col = 0; col < board.size; col++) {
                Cell cell = (Cell) this.getChildren().get(row * board.size + col);
                
                cell.update(board.getCell(new Coordinate(row, col)));
                

                if (this.view.model.getCurrentAgent() instanceof HumanAgent) { // if human turn
                	if (this.view.undoButton != null) {
                		this.view.setUndoButton();
                		this.view.restartButton.setDisable(false);
                		this.view.saveButton.setDisable(false);
                	}
                	if (this.board.getTurn() == Piece.Type.MUSKETEER) { // if human is musketeer
                		if ( cell.getPiece() != null && cell.getPiece().getType() == Piece.Type.GUARD) { // and if current selected cell is guard
                			if (cell.getUserData() != null &&  cell.getUserData().equals("options")) { // and if current cell is a destination for the musketeers
                				cell.setDisable(false);
                				cell.setAgentToColor();

                			}else { //means its not a piece that should be enabled
                				cell.setDisable(true);

                			}
                		}
                		else { // this is a musketeer cell with a musketeer humanAgent
                			highlight(cell);

                		}
                	}
                	else {  //human is a guard
                		if (cell.getPiece() == null) { // is the current cell type a empty

                			if (cell.getUserData() != null && cell.getUserData().equals("options")) { // is the current cell a option for movment
                				cell.setDisable(false);
                				cell.setAgentToColor();

                			}else {

                				cell.setDisable(true);

                			}
                		}
                		else { // the cell is a guard or musketeer; hightlight it
                			highlightGuard(cell);
                			
                		}
               	}
                	
                }else { // comptuer movment
                	cell.setDisable(true);
                	if (this.view.undoButton != null) {
                		this.view.undoButton.setDisable(true);
                		this.view.restartButton.setDisable(true);
                		this.view.saveButton.setDisable(true);
                	}

                }
            }
        }
       
       if (this.board.isGameOver()) {
    	   win();
    	   
       }
       
       
    }
    


    /**
     * Handles Cell clicks and updates the board accordingly
     * When a Cell gets clicked the following must be handled:
     *  - If it's a valid piece that the player can move, select the piece and update the board
     *  - If it's a destination for a selected piece to move, perform the move and update the board
     * @param actionEvent
     */
    @Override
    public void handle(ActionEvent actionEvent) {
        Cell clickedCell = (Cell) actionEvent.getSource();

        if(clickedCell.getUserData() == null ) { // case where user changes choice
        	unhighlight();
        }
        
        if (clickedCell.getUserData() != null && clickedCell.getUserData().equals("options")) { // case where user moves onto a location
        	clickedCell.setUserData("chosen");

        	this.view.runMove();


            	clickedCell.setUserData(null);
        		unhighlight();
                // Add PauseTransition
                PauseTransition pause = new PauseTransition(Duration.seconds(1)); // 1 second pause 
                pause.setOnFinished(event -> {
                    if (!this.view.model.isHumanTurn()) { // for computer move
                        this.view.runMove();
                    }
                });
                pause.play();
                return;
            }
        
        //labeling currentlly selected cell
        clickedCell.setUserData("selected");
        

		//labeling all possible toCells
		List <Cell> list = this.board.getPossibleDestinations(clickedCell);
		for (Cell index: list) {
            Cell current = (Cell) this.getChildren().get(index.getCoordinate().row * board.size + index.getCoordinate().col);
			current.setUserData("options");
			
		}
		
		
        // Check if the clicked cell is a valid piece to move
        List<Cell> options = this.board.getPossibleCells();
        for (Cell index: options) {
            Cell current = (Cell) this.getChildren().get(index.getCoordinate().row * board.size + index.getCoordinate().col);
	        	
        	current.setOptionsColor();
	        
        }
        this.updateCells();
   
    }
    
    
    //helpers
    private void highlight(Cell cell) {
    	
    	if (cell.getUserData() != null && cell.getUserData().equals("selected")) {
			cell.setAgentFromColor();
    	}
		else {
			cell.setDisable(false);

		}
    }
    
    private void highlightGuard(Cell cell) {
    	
    	if (cell.getPiece().getType() == Piece.Type.MUSKETEER) {
    		cell.setDisable(true);
    		return;
    	}
        List<Cell> options = this.board.getPossibleCells();
        for (Cell index: options) {
            Cell current = (Cell) this.getChildren().get(index.getCoordinate().row * board.size + index.getCoordinate().col);
	        	
            if (current.getCoordinate().row == cell.getCoordinate().row && cell.getCoordinate().col == current.getCoordinate().col) {
             	if (cell.getUserData() != null && cell.getUserData().equals("selected")) {
             		cell.setAgentFromColor();
        			
            	}
        		else {
        			cell.setOptionsColor();
        			cell.setDisable(false);
        		}
        }

        }
        }
    
    
	private void unhighlight() {
	      for (int row = 0; row < board.size; row++) {
	            for (int col = 0; col < board.size; col++) {
	                Cell cell = (Cell) this.getChildren().get(row * board.size + col);
	                cell.setUserData(null);
	
	            }
	            
	      }
	      
	}
	
	private void disableAllCells() {
	      for (int row = 0; row < board.size; row++) {
	            for (int col = 0; col < board.size; col++) {
	                Cell cell = (Cell) this.getChildren().get(row * board.size + col);
	                cell.setDisable(true);
	
	            }
	            
	      }
	}
	private void win() {
 	   this.view.setMessageLabel( "WINNER" + this.board.getWinner().toString());
 	   this.disableAllCells();
 	   List<Cell> musketeer = this.board.getMusketeerCells();
 	   List <Cell> guards = this.board.getGuardCells();
 	   for (Cell index: musketeer) {
           Cell current = (Cell) this.getChildren().get(index.getCoordinate().row * board.size + index.getCoordinate().col);

 		   if (this.board.getWinner() == Piece.Type.MUSKETEER) {
 			   current.setWinColor();
 		   }
 		   else {
 			   current.setLossColor();
 		   }
 	   }
 	   for (Cell index: guards) {
           Cell current = (Cell) this.getChildren().get(index.getCoordinate().row * board.size + index.getCoordinate().col);

 		   if (this.board.getWinner() == Piece.Type.GUARD) {
 			   current.setWinColor();
 		   }
 		   else {
 			   current.setLossColor();
 		   }
 	   }
 	   this.view.restartButton.setDisable(false);
	}
}




	

