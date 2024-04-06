package goLab;
import java.util.Scanner;
import java.util.ArrayList;
public class Go {

// Initialize the 2d board to be 19 x 19
public static String[][] board = new String[9][9];

//Empty spot is 0, black piece is 1, white piece is 2
public static int[][] numberBoard = new int[9][9]; 

public static boolean[][] visited = new boolean[9][9];
public static boolean[][] alive = new boolean[9][9];

    public static void main(String[] args) {
        

        String[][] premadeBoard = {
  
                {null, null, "-*", "-*", null, null, null, null, null},
                {null, "-*", "-o", "-o", "-*", null, null, null, null},
                {null,  "-*", "-o", null, "-o", "-*", null, null, null},
                {null,  "-*", "-o", "-o", "-o", "-*", null, null, null},
                {null,  "-*", "-o", null, "-o", "-*", null, null, null},
                {null, null, "-*", "-o", "-o", "-*", null, null, null},
                {null, null, null, "-*", "-*", null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}

        };

        printBoard(board);

        // Board establishedCoords = new Board();

        int dimension = board.length - 1;
        Scanner obj = new Scanner(System.in);
        boolean blackTurn = true;
        ArrayList<Piece> pieces = new ArrayList<>();
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

                if (!checkBounds(x, y)) {
                    System.out.println("Please enter a valid coordinate\n");
                    continue;
                }

                // Loop through coordinates and check to see if the selected coordinates already
                // have a piece on them

                for (Piece p : pieces) {
                    int[] establishedCoords = p.getCoordinates();
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
            Piece p = new Piece(x, y, (blackTurn) ? true : false);
            pieces.add(p);
            /*
            System.out.println("X coordinate:");
            System.out.println(x);
            System.out.println("Y coordinate:");
            System.out.println(y);
            
            if((y == 0 && x > 0))
            {
                board[y][x] = (blackTurn) ? "-*" : "-o";
            }
            else
            {
                board[y][x] = (blackTurn) ? "*" : "o";
            }
            */
            board[y][x] = (blackTurn) ? "*" : "o";
            
            printBoard(board);
            System.out.println();
            numberBoard[y][x] = (blackTurn) ? 1 : 2;
            printNumberBoard();
            blackTurn = !blackTurn; // Flip the turn

            canBreathe();
            //printAliveBoard();


            //printNumberBoard();

            //Check to see if any pieces have been captured. Adjust scores accordingly if so
            
            //A piece is captured when all adjacent points are surrounded (by edge or by opponent pieces) unless the pieces form their own territory (requires two enclosed eyes or spaces)
            //within opponent territory

            //Each time a piece is added, a check must be done to see if that piece is now apart of a group of pieces (of that same color)
            
        }

    }


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
                    if (j == 0) {
                        System.out.print(array[i][j]);
                    } else {
                        System.out.print("-" + array[i][j]);
                    }

                }
            }
            System.out.println();
        }
    }

    static boolean checkBounds(int x, int y) {
        if ((x >= 8 || x < 0) || (y >= 8 || y < 0)) {
            return false;
        } else {
            return true;
        }

    }


    private static class Piece {
        private int x;
        private int y;
        private boolean black;

        public Piece(int x, int y, boolean color) {
            this.x = x;
            this.y = y;
            this.black = color;
        }

        public int[] getCoordinates() {
            int[] c = { x, y };
            return c;
        }


        //Implement a method to check if a piece is surrounded by opponents/edges

    }


    public static boolean hasLiberties(int x, int y, int color, int friendlyStone)
    {
        
        //Check up
        boolean upValidity = checkBounds(y-1, x);
        int upLiberty = 3;
        if(upValidity)
        {
            upLiberty = numberBoard[y-1][x];
        }
        boolean downValidity = checkBounds(y+1, x);
        int downLiberty = 3;
        if(downValidity)
        {
            downLiberty = numberBoard[y+1][x];
        }
        boolean leftValidity = checkBounds(y, x-1);
        int leftLiberty = 3;
        if(leftValidity)
        {
            leftLiberty = numberBoard[y][x-1];
        }
        boolean rightValidity = checkBounds(y, x+1);
        int rightLiberty = 3;
        if(rightValidity)
        {
            rightLiberty = numberBoard[y][x+1];
        }

        if(y == 1 && x == 2)
        {
            System.out.println("2,1 Coord: upLiberty = " + upLiberty + " down liberty = " + downLiberty + " left liberty = " + leftLiberty + " right liberty = " + rightLiberty);
        }
        else if(x == 2 && y == 2)
        {
            System.out.println("2,2 coord: upLiberty = " + upLiberty + " down liberty = " + downLiberty + " left liberty = " + leftLiberty + " right liberty = " + rightLiberty);
        }
        
        //This if-else branch are my base case scenarios. If a piece has a 0 around it, it can breathe. If a piece is completely surrounded by
        //enemy pieces or the edge, it has no liberties (and is captured)
        //If any position is = 0, this coordinate has a liberty (and can "breathe")
        if(upLiberty == 0 || downLiberty == 0 || leftLiberty == 0 || rightLiberty == 0)
        {
            return true;
        }
        //This means that this stone is surrounded by wall or the opponents stone since it didn't pass the first check.
        else if(upLiberty != color && downLiberty != color && leftLiberty != color && rightLiberty != color)
        {
            //System.out.println();
            //System.out.println("Color: " + color);
            //System.out.println("upLiberty = " + upLiberty + " down liberty = " + downLiberty + " left liberty = " + leftLiberty + " right liberty = " + rightLiberty);
            //System.out.println("X: " + x + "Y: " + y + "is surrounded by wall or enemy pieces");
            //System.out.println();
            return false;
        }
        //This means that the stone is surrounded but has a friendly stone next to it, we need to check if that stone can breathe

        //System.out.println("One of the adjacent stones is friendly");
        //We only want to check the adjacent stones that are equal to color (friendly stones)
        //System.out.println("Friendly stone value: " + friendlyStone);
        //System.out.println("upLiberty = " + upLiberty + " down liberty = " + downLiberty + " left liberty = " + leftLiberty + " right liberty = " + rightLiberty);
        if(upLiberty == color)
        {
            System.out.println("Upliberty. friendlyStone = " + friendlyStone);
            //If the stone that is adjacent to this current one 
            if(friendlyStone == 1)
            {
                System.out.println("Upliberty, but we were sent from a down liberty. Return False");
                return false;
            }
            else
            {
                //System.out.println("Friendly stone is 0");
                return hasLiberties(x, y + 1, color, 0);
                
                
            }
            
        }
        else if(downLiberty == color)

        {
            System.out.println("Down. friendlyStone = " + friendlyStone);
            if(friendlyStone == 0)
            {
                System.out.println("Downliberty, but we were sent from a Upliberty. Return False");
                return false;
            }
            else
            {
                //System.out.println("Friendly stone is 1");
                return hasLiberties(x, y - 1, color, 1);
            }
            
        }
        else if(leftLiberty == color)
        {
            System.out.println("leftliberty. friendlyStone = " + friendlyStone);
            if(friendlyStone == 3)
            {
                System.out.println("Leftliberty, but we were sent from a right liberty. Return False");
                return false;
            }
            else
            {
                //System.out.println("Friendly stone is 2");
                return hasLiberties(x -1, y, color, 2);
            }
            
        }
        else
        {
            System.out.println("Rightliberty. friendlyStone = " + friendlyStone);
            if(friendlyStone == 2)
            {
                System.out.println("Rightliberty, but we were sent from a Leftliberty. Return False");
                return false;
            }
            else
            {
                //System.out.println("Friendly stone is 3");
                return hasLiberties(x -1, y, color, 3);
            }
        }
        
    }

    public static void canBreathe()
    {
        boolean[][] visited = new boolean[9][9];

        // Iterate over the entire board
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int color = numberBoard[i][j];
                //if the current piece is 0, its empty, no need to check if it has liberties. OR, if we've already visited this piece, don't check
                if(color == 0)
                {
                    visited[i][j] = true;
                    alive[i][j] = true;
                    //System.out.println("X coordinate:" + i + "Y coordinate:" + j + "is an empty spot");
                    continue;
                }
                else if(visited[i][j])
                {
                    continue;
                }
                //Otherwise, check if this piece has liberties
                else
                {
                    //System.out.println("These coords were checked for liberties: " + j + ", " + i);
                    boolean colorCanBreathe = hasLiberties(j, i, color, -1);
                    
                    if(colorCanBreathe)
                    {
                        System.out.println("X coordinate: " + j + "Y coordinate: " + i + "has a liberty" );
                    }
                    else
                    {
                        System.out.println("X coordinate: " + j + "Y coordinate: " + i + "has no liberties" );
                    }
                    
                    
                    visited[i][j] = true;
                    alive[i][j] = colorCanBreathe;

                }

                
                }
            }
        }
    

    static void printNumberBoard()
    {
        for(int i = 0; i < 9; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                System.out.print(numberBoard[i][j] + " ");
            }
            System.out.println();
        }
    }

    static void printAliveBoard()
    {
        for(int i = 0; i < 9; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                System.out.print(alive[i][j] + " ");
            }
            System.out.println();
        }
    }

    



    // ◯ ●
    
}
