/*Neven Marinkovic
 * 4/8/2024
 * CSCI 152 - Go Lab2
 * The following is an attempt at recreating the go Board game. Many features/rules are not implemented. My isInTerritory() function has a weird
 * bug where it'll count territory as belonging to a team in games in which pieces are not played in the middle of the board, so keep that in mind
 * as you test/play the game. You can uncomment out the printTerritory() call within the exit block in the main function to see this. You can 
 * play along the edges as long as you put a piece in the middle somewhere (like 4,4), the scoring calculation will be correct. The program works
 * correctly for most common situations - per my testing.
 */


package goLab;
import java.util.*;
public class Go {

// Initialize the 2d board to be 19 x 19
public static String[][] board = new String[9][9];                                

//Empty spot is 0, black piece is 1, white piece is 2
public static int[][] numberBoard = new int[9][9]; 

public static boolean[][] visited = new boolean[9][9];
public static boolean[][] alive = new boolean[9][9];
public static boolean[][] territory = new boolean[9][9];

public static double blackScore = 0;
public static double whiteScore = 6.5;

    public static void main(String[] args) {
        

        printBoard(board);

        // Board establishedCoords = new Board();
        int passCount = 0;
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

                //System.out.println(x + " " + y);

                //The user entering in (-1,-1) as coordinates represents a "pass" move. 3 pass moves in a row end the game
                if(x == -1 && y == -1)
                {
                    passCount ++;
                    if(passCount == 2)
                    {
                        System.out.println("That marks the end of the game!");
                        board = checkCapturedPieces(board);
                        findTerritory();
                        
                        //printTerritoryBoard();
                        
                        System.out.println("Black scored: " + blackScore + " and white scored: " + whiteScore);
                        obj.close();
                        System.exit(0);
                    }
                    else
                    {
                        blackTurn = !blackTurn; // Flip the turn
                        break;
                    }
                    
                }
                else if (!checkBounds(x, y)) {
                    System.out.println("Please enter a valid coordinate\n");
                    continue;
                }
                else
                {
                    for (Piece p : pieces) {
                        int[] establishedCoords = p.getCoordinates();
                        if (x == establishedCoords[0] && y == establishedCoords[1]) {
                            System.out.println("A stone has already been placed there, please enter new coordinates");
                            alreadyPlaced = true;
                            break;
                        }
                    }

                    // We made it through the for loop and no matches were made. Exit out of the input while loop
                    if (alreadyPlaced) {
                        break;
                    }

                    // Add the new coordinate to our list of coordinates, print out the board, and
                    // flip the turn
                    Piece p = new Piece(x, y);
                    pieces.add(p);

                    board[y][x] = (blackTurn) ? "*" : "o";
                    
                    
                    System.out.println();
                    numberBoard[y][x] = (blackTurn) ? 1 : 2;
                    

                    boolean[][] v = new boolean[9][9];
                    //If the piece we just placed has no liberties (and is automatically dead), set the numberBoard[y][x] back to 0 and set the 
                    //actual board back to | or -|
                    if(!hasLiberties(x, y, numberBoard[y][x], v))
                    {
                        //If a stone has no liberties, check the size of the territory it is in. If the territory size is greater than the min
                        //number of spaces needed to form 2 eyes within a territory
                        System.out.println("You cannot have a stone commit suicide!");
                        numberBoard[y][x] = 0;
                        if(x == 0)
                        {
                            board[y][x] = "|";
                        }
                        else
                        {
                            board[y][x] = "-|";
                        }
                        continue;

                    }

                  
                    //printNumberBoard();
                    blackTurn = !blackTurn; // Flip the turn

                    canBreathe();
                    
                    printBoard(board);
                }
    
            }
        }

    }

    static void printTerritoryBoard()
    {
        for(int i = 0; i < 9; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                System.out.print(territory[i][j] + " ");
            }
            System.out.println();
        }
    }

    static boolean isInTerritory(int x, int y, boolean[][] v, boolean touchedBlack, boolean touchedWhite)
    {

        int[] adjacentPieces = retrieveAdjacents(x, y);

        //loop through those pieces, check values. If a 0 is ever touching both a white and a black piece, it cannot be within a territory.
        for(int i = 0; i <adjacentPieces.length; i++)
        {
            if(adjacentPieces[i] == 1)
            {
                touchedBlack = true;
            }
            else if(adjacentPieces[i] == 2)
            {
                touchedWhite = true;
            }
        }

        //If our piece is indirectly (or directly) connected to both a white and a black piece, it cannot be within a territory
        if(touchedBlack && touchedWhite)
        {
            return false;
        }
        //If we haven't hit a black and a white piece yet AND this point has already been visited, return true
        /*
        if(v[y][x])
        {
            return true;
        }
        */

        v[y][x] = true;
        //Otherwise, we need to check our adjacent pieces and see if we need to try any of those paths
        //Check the above spot
        if(checkBounds(x, y-1) && numberBoard[y-1][x] == 0 && !v[y-1][x])
        {
            
            if(!isInTerritory(x, y-1, v, touchedBlack, touchedWhite))
            {
                return false;
            }
        }
        //Check the spot below
        if(checkBounds(x, y+1) && numberBoard[y+1][x] == 0 && !v[y+1][x])
        {
            if(!isInTerritory(x, y+1, v, touchedBlack, touchedWhite))
            {
                return false;
            } 
        }
        //Check the spot to the left
        if(checkBounds(x-1, y) && numberBoard[y][x-1] == 0 && !v[y][x-1])
        {
            if(!isInTerritory(x-1, y, v, touchedBlack, touchedWhite))
            {
                return false;
            }
        }
        //Check the spot to the right
        if(checkBounds(x+1, y) && numberBoard[y][x+1] == 0 && !v[y][x+1])
        {
            if(!isInTerritory(x+1, y, v, touchedBlack, touchedWhite))
            {
                return false;
            }
        }

        return true;
        

    }

    static void findTerritory()
    {
        
        for(int y = 0; y < 9; y++)
        {
            for(int x = 0; x < 9; x++)
            {
                if(numberBoard[y][x] == 0)
                {
                    boolean [][] haveVisited = new boolean[9][9];
                    //call the isInTerritory function
                    boolean inTerritory = false;
                    
                    inTerritory = isInTerritory(x, y, haveVisited, false, false);
                    if(inTerritory)
                    {
                        territory[y][x] = true;
                        int team = whichTeam(x, y);
                        if(team == 1)
                        {
                            blackScore ++;
                        }
                        else if(team == 2)
                        {
                            whiteScore++;
                        }
                    }

                }
            }
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
        if ((x > 8 || x < 0) || (y > 8 || y < 0)) {
            return false;
        } else {
            return true;
        }

    }


    private static class Piece {
        private int x;
        private int y;
        //private boolean black;

        public Piece(int x, int y) {
            this.x = x;
            this.y = y;
            //this.black = color;
        }

        public int[] getCoordinates() {
            int[] c = { x, y };
            return c;
        }


        //Implement a method to check if a piece is surrounded by opponents/edges

    }

    public static int oppositeColor(int c)
    {
        if(c == 1)
        {
            return 2;
        }
        else
        {
            return 1;
        }
    }

    public static int[] retrieveAdjacents(int x, int y)
    {
        boolean upValidity = checkBounds(x, y-1);
        int up = 3;
        if(upValidity)
        {
            up = numberBoard[y-1][x];
        }
        boolean downValidity = checkBounds(x, y+1);
        int down = 3;
        if(downValidity)
        {
            down = numberBoard[y+1][x];
        }
        boolean leftValidity = checkBounds(x-1, y);
        int left = 3;
        if(leftValidity)
        {
            left = numberBoard[y][x-1];
        }
        boolean rightValidity = checkBounds(x+1, y);
        int right = 3;
        if(rightValidity)
        {
            right = numberBoard[y][x+1];
        }
        
         int[] adjacents = {up, down, left, right};
    
        return adjacents;
    }




    public static boolean hasLiberties(int x, int y, int color, boolean[][]checked)
    {
        if(!checkBounds(x, y))
        {
            return false;
        }
        if(numberBoard[y][x] == oppositeColor(color) || numberBoard[y][x] == 3)
        {
            return false;
        }
        
        //Check up
        boolean upValidity = checkBounds(x, y-1);
        int upLiberty = 3;
        if(upValidity)
        {
            upLiberty = numberBoard[y-1][x];
        }
        boolean downValidity = checkBounds(x, y+1);
        int downLiberty = 3;
        if(downValidity)
        {
            downLiberty = numberBoard[y+1][x];
        }
        boolean leftValidity = checkBounds(x-1, y);
        int leftLiberty = 3;
        if(leftValidity)
        {
            leftLiberty = numberBoard[y][x-1];
        }
        boolean rightValidity = checkBounds(x+1, y);
        int rightLiberty = 3;
        if(rightValidity)
        {
            rightLiberty = numberBoard[y][x+1];
        }

        int oColor = oppositeColor(color);


        if((upLiberty == oColor || upLiberty == 3) && (downLiberty == oColor || downLiberty == 3) &&(leftLiberty == oColor || leftLiberty == 3)&&(rightLiberty == oColor || rightLiberty == 3))
        {
            //System.out.println(x + " " + y + " is surrounded by edge or enemy pieces");
            return false;
        }
        
        if(checked[y][x])
        {
            //System.out.println(x + " " + y + " has already been visited");
            return false;
        }

        
        
        if(upLiberty == 0 || downLiberty == 0 || leftLiberty == 0 || rightLiberty == 0)
        {
            return true;
        }

        int throughX = 0;
        int throughY = 0;
        checked[y][x] = true;
        if(!hasLiberties(x, y + 1, color,checked ))
        {
            if(checkBounds(x, y+1))
            {
                checked[y+1][x] = true;
            }
            
            
            //If the left liberty can't breathe, check the up liberty
            if(!hasLiberties(x -1, y, color, checked))
            {
                if(checkBounds(x-1, y))
                {
                    checked[y][x-1] = true;
                }
                
                //Check the up liberty
                
                if(!hasLiberties(x, y -1, color, checked))
                {
                    if(checkBounds(x, y-1))
                    {
                        checked[y-1][x] = true;
                    }
                    
                    //Finally, check the right liberty
                    //checked[y][x+1] = true;
                    if(!hasLiberties(x + 1, y, color, checked))
                    {
                        //System.out.println("None of " + x + " " + y +" surrounding pieces can breathe");
                        return false;
                    }
                    else
                    {
                        throughX = x+1;
                        throughY = y;
                    }
                }
                else
                {
                    throughX = x;
                    throughY = y-1;
                }
                
            }
            else
            {
                throughX = x-1;
                throughY = y;
            }
            
        }
        else
        {
            throughX = x;
            throughY = y+1;
        }

        //System.out.println(x + " , "+y+ " can breathe via: (" + throughX + " , " + throughY + ")");
        return true;
        
    }


    public static int whichTeam(int x, int y)
    {
        //Check pieces to the right
        for(int i = x; i < 9 - x; i++)
        {
            int piece = numberBoard[y][i];
            if(numberBoard[y][i] == 1)
            {
                return 1;
            }
            else if(numberBoard[y][i] == 2)
            {
                return 2;
            }
        }

        //Check pieces to the left
        for(int i = x; i >= 0; i--)
        {
            int piece = numberBoard[y][i];
            if(numberBoard[y][i] == 1)
            {
                return 1;
            }
            else if(numberBoard[y][i] == 2)
            {
                return 2;
            }
        }

        //Check pieces above
        for(int i = y; i >= 0; i--)
        {
            int piece = numberBoard[i][x];
            if(numberBoard[i][x] == 1)
            {
                return 1;
            }
            else if(numberBoard[i][x] == 2)
            {
                return 2;
            }
        }

        for(int i = y; i < 9 - y; i++)
        {
            int piece = numberBoard[y][i];
            if(numberBoard[i][x] == 1)
            {
                return 1;
            }
            else if(numberBoard[i][x] == 2)
            {
                return 2;
            }
        }

        return 0;
    }

    public static void canBreathe()
    {
        boolean[][] visited = new boolean[9][9];

        // Iterate over the entire board
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                int color = numberBoard[y][x];
                //if the current piece is 0, its empty, no need to check if it has liberties. OR, if we've already visited this piece, don't check
                if(color == 0)
                {
                    visited[y][x] = true;
                    alive[y][x] = true;
                    //System.out.println("X coordinate:" + i + "Y coordinate:" + j + "is an empty spot");
                    continue;
                }
                else if(visited[y][x])
                {
                    continue;
                }
                //Otherwise, check if this piece has liberties
                else
                {
                    //System.out.println();
                    boolean[][] c = new boolean[9][9];
                    //System.out.println("These coords were checked for liberties: " + j + ", " + i);
                    boolean colorCanBreathe = hasLiberties(x, y, color, c);    
                    
                    visited[y][x] = true;
                    alive[y][x] = colorCanBreathe;

                }

                
                }
            }
        }

    static String[][] checkCapturedPieces(String[][] gBoard)
    {
        for(int y = 0; y < 9; y++)
        {
            for(int x = 0; x < 9; x++)
            {
                int color = numberBoard[y][x];
                boolean lives = alive[y][x];

                if(color == 1 && !lives)
                {
                    System.out.println("Black piece at (" + x + ", " + y + ") has been captured by White!");
                    whiteScore += 1;
                    gBoard[y][x] = "|";
                    numberBoard[y][x] = 0;
                    //Set the piece that was dead back to life, so that it doesn't get counted each iteration 
                    alive[y][x] = true;
                    
                }
                else if(color == 2 && !lives)
                {
                    System.out.println("White piece at (" + x + ", " + y + ") has been captured by Black!");
                    blackScore += 1;
                    gBoard[y][x] = "|";
                    numberBoard[y][x] = 0;
                    //Set the piece that was dead back to life, so that it doesn't get counted each iteration 
                    alive[y][x] = true;
                }
            }
            
        }
        System.out.println();
        return gBoard;
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
    
}
