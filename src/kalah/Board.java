package kalah;

import com.qualitascorpus.testsupport.IO;
import java.util.ArrayList;

/**
 * This class controls the playingBoard and its state. It distributes the seeds for each
 * Player's turn, captures the seeds, and completes the checks for any special turns or
 * whetehr the board is empty after each turn.
 * @author mpie374
 */
public class Board {
    public final int NUMHOUSES = 6;
    public final int BOARDSIZE = (NUMHOUSES * 2) + 2;
    private final int P1STOREINDEX = NUMHOUSES;
    private final int P2STOREINDEX = BOARDSIZE - 1;
    private ArrayList<Pit> playingBoard;
    private Player playerOne;
    private Player playerTwo;

    /**
     * Default constructor, creates both Player's and the playing Board
     */
    Board() {
        playerOne = new Player(NUMHOUSES);
        playerTwo = new Player(NUMHOUSES);
        playingBoard = new ArrayList<>();
        playingBoard.addAll(playerOne.getPlayerPits());
        playingBoard.addAll(playerTwo.getPlayerPits());
    }

    /**
     * This method determines which board to display depending on whether a vertical
     * preference has been indicated or not
     * @param io
     * @param vertical - TRUE if board to be displayed vertically
     */
    public void displayBoard(IO io, boolean vertical) {
        if(vertical)
            displayVerticalBoard(io);
        else
            displayHorizontalBoard(io);
    }

    /**
     * This method displays the board vertically to the users in its most updated state.
     * Player 2's Houses are located on the right, with Player 1's on the left.
     * @param io
     */
    public void displayVerticalBoard(IO io) {
        io.println("+---------------+");
        io.println("|       | P2 " + playingBoard.get(P2STOREINDEX).toString() + " |");
        io.println("+-------+-------+");
        for(int i = 0; i < NUMHOUSES; i++) {
            io.println("| " + (i + 1) + "[" + playingBoard.get(i).toString() + "] | " + (NUMHOUSES - i) +
                    "[" + playingBoard.get(playingBoard.size() - i - 2).toString() + "] |");
        }
        io.println("+-------+-------+");
        io.println("| P1 " + playingBoard.get(P1STOREINDEX).toString() + " |       |");
        io.println("+---------------+");
    }

    /**
     * This method displays the board horizontally to the users in its most updated state.
     * Player 2's Houses are loated along the top row with their Store score in the bottom left.
     * Player 1's Houses are along the bottom row with their Store score in the top right.
     * @param io
     */
    public void displayHorizontalBoard(IO io) {
        io.println("+----+-------+-------+-------+-------+-------+-------+----+");
        io.print("| P2 |");
        for(int i = 0; i < NUMHOUSES; i++) {
            io.print(" " + (NUMHOUSES - i) + "[" + playingBoard.get(playingBoard.size() - i - 2).toString() + "] |");
        }
        io.print(" " + playingBoard.get(P1STOREINDEX).toString() + " |");
        io.println("");
        io.println("|    |-------+-------+-------+-------+-------+-------|    |");
        io.print("| " + playingBoard.get(P2STOREINDEX).toString() + " |");
        for(int i = 0; i < NUMHOUSES; i++) {
            io.print(" " + (i + 1) + "[" + playingBoard.get(i).toString() + "] |");
        }
        io.print(" P1 |");
        io.println("");
        io.print("+----+-------+-------+-------+-------+-------+-------+----+");
        io.println("");
    }

    /**
     * Sows the seeds across each House and applicable Store, depending on whose turn it is.
     * The starting index is dependent on whose turn it is. If an empty house is selected the user
     * is prompted for another input. As the final seed is distributed, checks occur to identify
     * any special turns such as captures or additional turns.
     * @param isPlayOneTurn - TRUE if Player 1's turn
     * @param isBoardEmpty - TRUE if board is empty (indicates the final turn)
     * @param selectedHouse - House user has chosen to move from
     * @param io
     * @return TRUE if Player 1's turn next, FALSE for Player 2
     */
    public boolean sowSeeds(boolean isPlayOneTurn, boolean isBoardEmpty, int selectedHouse, IO io) {
        int startHouseIndex = isPlayOneTurn? (selectedHouse - 1) : (selectedHouse + NUMHOUSES);
        int numSeedsToSow = playingBoard.get(startHouseIndex).getNumSeeds();
        if(numSeedsToSow == 0 && !isBoardEmpty) {
            io.println("House is empty. Move again.");
            return isPlayOneTurn;
        }

        int moves = 1; //moves made this turn
        playingBoard.get(startHouseIndex).emptyPit();
        while(moves <= numSeedsToSow) {
            //skip p1 store when p2 turn
            if(startHouseIndex + moves == P1STOREINDEX && !isPlayOneTurn) {
                moves++;
                numSeedsToSow++; //to account for jumping a move
            } else if (startHouseIndex + moves == P2STOREINDEX && isPlayOneTurn) { //skip p2 store when p1 turn
                moves++;
                numSeedsToSow++; //to account for jumping a move
            }
            //check if end of playingBoard has been reached, then restart at index 0 (p1 House 1)
            if(startHouseIndex + moves == BOARDSIZE) {
                startHouseIndex -= (startHouseIndex + moves);
            }
            playingBoard.get(startHouseIndex + moves).addSeeds(1);
            moves++;
        }

        /**
         * Checks final seed for special turns and outcomes.
         * Scen 1. Player 1's turn and they end in the P1 Store == P1 turn again
         * Scen 2. Player 2's turn and they end in the P2 Store == P2 turn again
         * Scen 3. P1's turn, they end in their own House which was prev. empty
         *          && the opposite P2 house is not empty == P1 captures both lots
         *          of seeds into their own store, P2 turn
         * Scen 4. P2's turn, they end in their own House which was prev. empty
         *          && the opposite P1 house is not empty == P2 captures both lots
         *          of seeds into their own Store, P1 turn
         * Scen 5. Normal end of turn == swaps to other player
         */
        int currentPos = startHouseIndex + moves - 1; //index in the ArrayList
        if(isPlayOneTurn && currentPos == P1STOREINDEX) { //Scen. 1
            isPlayOneTurn = true;
        } else if(!isPlayOneTurn && currentPos == P2STOREINDEX) { //Scen. 2
            isPlayOneTurn = false;
        } else if(isPlayOneTurn && currentPos < P1STOREINDEX && playingBoard.get(currentPos).getNumSeeds() == 1
                && playingBoard.get(getOpposingHouseIndex(currentPos)).getNumSeeds() != 0) { //Scen. 3
            playingBoard.get(P1STOREINDEX).addSeeds(playingBoard.get(currentPos).captureSeeds() +
                    playingBoard.get(getOpposingHouseIndex(currentPos)).captureSeeds());
            isPlayOneTurn = false;
        } else if(!isPlayOneTurn && currentPos > P1STOREINDEX && playingBoard.get(currentPos).getNumSeeds() == 1
                && playingBoard.get(getOpposingHouseIndex(currentPos)).getNumSeeds() != 0) { //Scen. 4
            playingBoard.get(P2STOREINDEX).addSeeds(playingBoard.get(currentPos).captureSeeds() +
                    playingBoard.get(getOpposingHouseIndex(currentPos)).captureSeeds());
            isPlayOneTurn = true;
        } else isPlayOneTurn = !isPlayOneTurn; //Scen. 5
        return isPlayOneTurn; //next Player's turn
    }

    /**
     * Finds the Opposing Players house directly opposite the current position
     * @param currentPos - current position on the board
     * @return - the index of the house opposite
     */
    public int getOpposingHouseIndex(int currentPos) {
        return -currentPos + (NUMHOUSES * 2);
    }

    /**
     * Checks whether or not the Board is empty on at least one side,
     * indicates end of game.
     * @param isPlayOneTurn
     * @return
     */
    public boolean isBoardEmpty(boolean isPlayOneTurn) {
        int p1SeedsRemaining = 0;
        int p2SeedsRemaining = 0;

        //iterate through each House of each Player
        //SKIP the Store's
        for(int i = 0; i < NUMHOUSES; i++) {
            if(i != P1STOREINDEX) {
                p1SeedsRemaining += playerOne.getPlayerPits().get(i).getNumSeeds();
            }
            if(i != P2STOREINDEX) {
                p2SeedsRemaining += playerTwo.getPlayerPits().get(i).getNumSeeds();
            }
        }
        return ((p1SeedsRemaining == 0 && isPlayOneTurn) || (p2SeedsRemaining == 0 && !isPlayOneTurn));
    }

    /**
     * This method identifies the winner of the game based on who has the most seeds
     * held in their Store when the game ends. The Game_Outcome is tied to P1's result.
     * @return an enum of type Game_Outcome.
     * WIN = P1 wins and P2 loses,
     * LOSS = P1 loses and P2 wins,
     * TIE = both players end with same score
     */
    public Game_Outcome getWinner() {
        playerOne.calcScore();
        playerTwo.calcScore();

        Game_Outcome didPlayerOne;

        if(playerOne.getScore() > playerTwo.getScore())
            didPlayerOne = Game_Outcome.WIN;
        else if(playerOne.getScore() < playerTwo.getScore())
            didPlayerOne = Game_Outcome.LOSS;
        else //draw
            didPlayerOne = Game_Outcome.TIE;
        return didPlayerOne;
    }

    /**
     * This method returns Player One
     * @return P1 instance
     */
    public Player getPlayerOne() {
        return playerOne;
    }

    /**
     * This method returns Player Two
     * @return P2 instance
     */
    public Player getPlayerTwo() {
        return playerTwo;
    }

    /**
     * Retrieves a specific Pit on the Board
     * @param index
     * @return Pit object
     */
    public Pit getPit(int index) {
        return playingBoard.get(index);
    }

    /**
     * This method checks to see whether the Robot will end its move in its own store,
     * if it starts its turn at the currentHouseIndex
     * @param currentHouseIndex - the starting house
     * @return - TRUE if move leads to extra turn
     */
    public boolean canRobotEndStore(int currentHouseIndex) {
        return playingBoard.get(currentHouseIndex).getNumSeeds() == -currentHouseIndex + BOARDSIZE - 1;
    }

    /**
     * This method checks to see whether the Robot will end its move with a capture of the opponents seeds.
     * As seeds can be placed around the board during the turn that determine this outcome, a dummy board is
     * created to simulate the move, before returning TRUE for the move to be executed on the real board.
     * @param startHouseIndex - House of start of robot move
     * @param numSeedsToSow - seeds in start house
     * @param currentStateBoard - current playingBoard
     * @return - TRUE if move will end in a capture
     */
    public boolean canRobotCapture(int startHouseIndex, int numSeedsToSow, Board currentStateBoard) {
        if(playingBoard.get(startHouseIndex).getNumSeeds() != 0) {
            Board dummyBoard = new Board();
            for(int i = 0; i < dummyBoard.BOARDSIZE; i++) {
                dummyBoard.getPit(i).clonePit(currentStateBoard.getPit(i).getNumSeeds());
            }

            int moves = 1;
            dummyBoard.playingBoard.get(startHouseIndex).emptyPit();
            while(moves <= numSeedsToSow) {
                //skip p1 store when p2 turn
                if(startHouseIndex + moves == P1STOREINDEX) {
                    moves++;
                    numSeedsToSow++; //to account for jumping a move
                }
                //check if end of playingBoard has been reached, then restart at index 0 (p1 House 1)
                if(startHouseIndex + moves == BOARDSIZE) {
                    startHouseIndex -= (startHouseIndex + moves);
                }
                dummyBoard.playingBoard.get(startHouseIndex + moves).addSeeds(1);
                moves++;
            }

            int indexOfFinalSeed = startHouseIndex + moves - 1;
            boolean landsInOwnEmptyPit = (indexOfFinalSeed > NUMHOUSES && dummyBoard.playingBoard.get(indexOfFinalSeed).getNumSeeds() == 1);
            boolean playerOneOpposingNotEmpty = dummyBoard.playingBoard.get(getOpposingHouseIndex(indexOfFinalSeed)).getNumSeeds() > 0;
            return landsInOwnEmptyPit && playerOneOpposingNotEmpty;
        }
        return false;
    }

    /**
     * This method finds the first legal move that the robot can take.
     * @param currentHouseIndex - starting house of move
     * @return - TRUE if legal move found from that start house
     */
    public boolean doesRobotHaveLegalMove(int currentHouseIndex) {
        return playingBoard.get(currentHouseIndex).getNumSeeds() != 0;
    }
}