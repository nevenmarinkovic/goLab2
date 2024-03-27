import java.util.Scanner;

import java.util.ArrayList;

public class App {

    /*
     * This is looking really great, I just made it a 9x9 so it would
     * be a bit easier to read. You should go ahead and run it and see if you
     * like it otherwise you can set it be ack to 19x19 in all your loops
     * and 19x19 in the String array on line 17.
     */

    static void printBoard(String[][] array) {

        System.out.println("  0 1 2 3 4 5 6 7 8"); // Print out the x coordinates above the board
        for (int i = 0; i < array[0].length; i++) {
            System.out.print(i + " "); // Print out the y coordinate on the left side of the board
            for (int j = 0; j < array.length; j++) {
                if (array[i][j] == null) // if the current spot on the board doesn't have a piece on it...
                {
                    if (j == 0) // Check if it's the first column, if it is, print | as the edge of the board
                    {
                        System.out.print("|");

                    } else {
                        System.out.print("-|"); // Otherwise, print -|
                    }
                } else // Print the peice at [i][j]
                {
                    if (i == 0) {
                        System.out.print(array[i][j]);
                    } else {
                        System.out.print("-" + array[i][j]);
                    }

                }
            }
            System.out.println();
        }
    }

    static boolean checkBounds(int x, int y, int bound) {
        if ((x >= bound || x < 0) || (y >= bound || y < 0)) {
            return false;
        } else {
            return true;
        }

    }

    private static class Coordinate {
        private int x;
        private int y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int[] getCoordinates() {
            int[] c = { x, y };
            return c;
        }

    }

    // ◯ ●
    public static void main(String[] args) {
        // Initialize the 2d board to be 19 x 19
        String[][] board = new String[9][9];
        printBoard(board);

        // Board establishedCoords = new Board();

        int dimension = board.length - 1;
        Scanner obj = new Scanner(System.in);
        boolean blackTurn = true;
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        boolean keepPlaying = true;
        while (keepPlaying) {
            // Prompt the user to enter coordinates for whoever's turn it is
            System.out.println((blackTurn) ? "Black's turn" : "White's turn");
            // boolean validCoor = false;
            int x = 0;
            int y = 0;
            Boolean alreadyPlaced = false;
            // inner while loop that will continue to run until the user selects coordinates
            // that haven't already been selected
            while (true) {
                alreadyPlaced = false;
                System.out
                        .println("Please enter a x coordinate for your " + ((blackTurn) ? "black" : "white") + "stone");
                x = obj.nextInt();
                System.out
                        .println("Please enter a y coordinate for your " + ((blackTurn) ? "black" : "white") + "stone");
                y = obj.nextInt();
                System.out.println();

                if (!checkBounds(x, y, dimension)) {
                    System.out.println("Please enter a valid coordinate\n");
                    continue;
                }

                // Loop through coordinates and check to see if the selected coordinates already
                // have a piece on them

                for (Coordinate c : coordinates) {
                    int[] establishedCoords = c.getCoordinates();
                    if (x == establishedCoords[0] && y == establishedCoords[1]) {
                        System.out.println("A stone has already been placed there, please enter new coordinates");
                        alreadyPlaced = true;
                        break;
                    }
                }

                // We made it through the for loop and no matches were made. Exit out of the
                // "input" while loop

                if (!alreadyPlaced) {
                    break;
                }

            }

            // Add the new coordinate to our list of coordinates, print out the board, and
            // flip the turn
            Coordinate c = new Coordinate(x, y);
            coordinates.add(c);
            board[y][x] = (blackTurn) ? "*" : "o";
            printBoard(board);
            System.out.println();
            blackTurn = !blackTurn; // Flip the turn

            //Check to see if any pieces have been captured. Adjust scores accordingly if so
            

        }

    }
}