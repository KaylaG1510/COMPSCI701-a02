package kalah;

import com.qualitascorpus.testsupport.IO;
import com.qualitascorpus.testsupport.MockIO;

/**
 * This class is the starting point for a Kalah implementation using
 * the test infrastructure. This class holds functionality for starting the game,
 * handling player turns and ending the game.
 * @author mpie374
 */
public class Kalah {
	public static void main(String[] args) {
		new Kalah().play(new MockIO(), false, true);
	}

	/**
	 * Handles the initial game state and creates the playing board to start the game.
	 * @param io - handles the standard input and output
	 */
	public void play(IO io, boolean vertical, boolean bmf) {
		boolean playing = true;
		boolean isPlayOneTurn = true;
		boolean isBoardEmpty = false;

		Board playingBoard = new Board();
		playingBoard.displayBoard(io, vertical);
		playingBoard = playTurn(playingBoard, io, isPlayOneTurn, playing, isBoardEmpty, vertical, bmf);
	}

	/**
	 * This method handles the player and robot turn by determining whose turn it is (player or robot),
	 * then retrieving what house that player will choose, before calling the method to sow the seeds.
	 * @param playingBoard - the current state of the board
	 * @param io - handles the input and output
	 * @param isPlayOneTurn - whose turn it is
	 * @param playing - TRUE whilst the game is still active
	 * @param isBoardEmpty - FALSE until at least one side of the board is empty, indicating game over
	 * @param vertical - TRUE if the board is to be displayed vertically
	 * @param bmf - TRUE if PLayer 2 is the bmf robot
	 * @return - the new state of the playing board
	 */
	public static Board playTurn(Board playingBoard, IO io, boolean isPlayOneTurn, boolean playing, boolean isBoardEmpty, boolean vertical, boolean bmf) {
		int selectedHouse = -1;
		while(playing) {
			//Either no robot player or it is PlayerOne's turn
			if(!bmf || (bmf && isPlayOneTurn)) {
				selectedHouse = io.readInteger("Player P" + (isPlayOneTurn ? "1" : "2") + "'s turn - Specify house number or 'q' to quit: ", 1, 6, -1, "q");
				if(!(selectedHouse == -1)) { //valid house input (1-6)
					isPlayOneTurn = playingBoard.sowSeeds(isPlayOneTurn, isBoardEmpty, selectedHouse, io);
				} else { //user entered 'q' to end game
					io.println("Game over");
					playing = false;
				}
			}else { //Robot turn
				selectedHouse = robotTurn(playingBoard, io);
				isPlayOneTurn = playingBoard.sowSeeds(isPlayOneTurn, isBoardEmpty, selectedHouse, io);
			}
			playingBoard.displayBoard(io, vertical);

			//check if a Player's playing board is empty
			isBoardEmpty = playingBoard.isBoardEmpty(isPlayOneTurn);
			if(isBoardEmpty) { //game over
				playingBoard.sowSeeds(isPlayOneTurn, isBoardEmpty, selectedHouse, io);
				gameOver(io, playingBoard, vertical);
				playing = false;
			}
		}
		return playingBoard;
	}

	/**
	 * Searches for the first valid 'best first move' that the robot player can make
	 * by iterating through each house in order of prioritised rules as follows:
	 * Priority 1: Robot searches for lowest house number that leads to an additional turn (ends in store)
	 * Priority 2: Robot searches for lowest house number that leads to a capture of opposition seeds
	 * Priority 3: Robot does first legal move, if there is one
	 * @param playingBoard, the current state of the playing board
	 * @param io
	 * @return the selectedHouse number of robot's move
	 */
	public static int robotTurn(Board playingBoard, IO io) {

		int currentHouseIndex;
		int numSeedsToSow;
		int selectedHouse = -1;
		boolean searchingForBMF = true;
		String reasonForMove = "";

		//Check 1. Lowest #ed house that leads to an additional move
		for(int i = 1; i <= playingBoard.NUMHOUSES; i++) {
			currentHouseIndex = playingBoard.NUMHOUSES + i;
			if(playingBoard.canRobotEndStore(currentHouseIndex)) {
				selectedHouse = currentHouseIndex - playingBoard.NUMHOUSES;
				reasonForMove = "it leads to an extra move";
				searchingForBMF = false;
				break;
			}
		}

		//Check 2. Lowest #ed house that leads to a capture
		if(searchingForBMF) {
			for(int i = 1; i <= playingBoard.NUMHOUSES; i++) {
				currentHouseIndex = playingBoard.NUMHOUSES + i;
				numSeedsToSow = playingBoard.getPit(currentHouseIndex).getNumSeeds();
				if(playingBoard.canRobotCapture(currentHouseIndex, numSeedsToSow, playingBoard)) {
					selectedHouse = currentHouseIndex - playingBoard.NUMHOUSES;
					reasonForMove = "it leads to a capture";
					searchingForBMF = false;
					break;
				}
			}
		}

		//Check 3. Lowest #ed house that has a legal move
		if(searchingForBMF) {
			for(int i = 1; i <= playingBoard.NUMHOUSES; i++) {
				currentHouseIndex = playingBoard.NUMHOUSES + i;
				if(playingBoard.doesRobotHaveLegalMove(currentHouseIndex)) {
					selectedHouse = currentHouseIndex - playingBoard.NUMHOUSES;
					reasonForMove = "it is the first legal move";
					searchingForBMF = false;
					break;
				}
			}
		}

		io.println("Player P2 (Robot) chooses house #" + selectedHouse + " because " + reasonForMove);
		return selectedHouse;
	}

	/**
	 * This method handles the game over functionality. It lets the players know that the
	 * game is over, identifies who won the game and calculates each players score so this
	 * information can all be relayed to the players.
	 * @param io - handles the standard input and output
	 * @param board - the playing board whose state is used to calculate the scores
	 */
	public static void gameOver(IO io, Board board, boolean vertical) {
		io.println("Game over");
		board.displayBoard(io, vertical);

		//find winner and scores
		Game_Outcome winner = board.getWinner();
		io.println("\tplayer 1:" + board.getPlayerOne().getScore());
		io.println("\tplayer 2:" + board.getPlayerTwo().getScore());

		//winner variable is based on playerOne's game outcome.
		switch(winner) {
			case WIN:
				io.println("Player 1 wins!");
				break;
			case LOSS:
				io.println("Player 2 wins!");
				break;
			case TIE:
				io.println("A tie!");
				break;
		}
	}
}
