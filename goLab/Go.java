package goLab;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
public class Go {

// Initialize the 2d board to be 19 x 19
public static String[][] board = new String[9][9];

//Empty spot is 0, black piece is 1, white piece is 2
public static int[][] numberBoard = new int[9][9]; 

public static boolean[][] visited = new boolean[9][9];
public static boolean[][] alive = new boolean[9][9];

public static double blackScore = 0;
public static double whiteScore = 6.5;

    public static void main(String[] args) {
        

        String[][] premadeBoard = {
  
                {"|", "-|", "-*", "-*", "-|", "-|", "-|", "-|", "-|"},
                {"|", "-*", "-o", "-o", "-*", "-|", "-|", "-|", "-|"},
                {"|",  "-*", "-o", "-|", "-o", "-*", "-|", "-|", "-|"},
                {"|",  "-*", "-o", "-o", "-o", "-*", "-|", "-|", "-|"},
                {"|",  "-*", "-o", "-|", "-o", "-*", "-|", "-|", "-|"},
                {"|", "-|", "-*", "-o", "-o", "-*", "-|", "-|", "-|"},
                {"|", "-|", "-|", "-*", "-*", "-|", "-|", "-|", "-|"},
                {"|", "-|", "-|", "-|", "-|", "-|", "-|", "-|", "-|"},
                {"|", "-|", "-|", "-|", "-|", "-|", "-|", "-|", "-|"}

        };

        printBoard(board);

        // Board establishedCoords = new Board();
        int passCount = 0;
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

                //System.out.println(x + " " + y);

                //The user entering in (-1,-1) as coordinates represents a "pass" move. 3 pass moves in a row end the game
                if(x == -1 && y == -1)
                {
                    passCount ++;
                    if(passCount == 3)
                    {
                        System.out.println("That marks the end of the game!");
                        calculateScore();
                        //Set<Piece> territory = calculateTerritory(0,0);
                        //int size = territory.size();
                        //System.out.println(size);
                        //System.out.println("Black scored: " + blackScore + " and white scored: " + whiteScore);
                        //Implement a scoring function
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
                    //System.out.println("Else branch was run");
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
                    board = checkCapturedPieces(board);
                    printBoard(board);
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

    public static int[] retrieveAdjacents(int x, int y, int color)
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

    public static Set<Piece> calculateTerritory(int x, int y) {
        Set<Piece> territory = new HashSet<>();
        visited = new boolean[9][9];

        // Perform flood-fill starting from the specified intersection
        floodFill(x, y, territory);

        return territory;
    }

    public static void floodFill(int x, int y, Set<Piece> territory) {
        // Check if the intersection is out of bounds or already visited
        if (x < 0 || x >= 9 || y < 0 || y >= 9 || visited[y][x]) {
            return;
        }

        // Mark the intersection as visited
        visited[y][x] = true;

        // Check if the intersection is empty
        if (numberBoard[y][x] == 0) {
            // Add the intersection to the territory
            territory.add(new Piece(x, y));

            // Recursively flood-fill neighboring intersections
            floodFill(x + 1, y, territory); // Right
            floodFill(x - 1, y, territory); // Left
            floodFill(x, y + 1, territory); // Down
            floodFill(x, y - 1, territory); // Up
        }
    }


    /*
    public static boolean multipleEyes(int x, int y, int color, ArrayList<int[]> eyes, int fStones, boolean[][]checked)
    {

        if(checked[y][x])
        {
            System.out.println("We've already checked: " + x + " " + y);
            return false;
        }
        int[] temp = {x, y};
        int[] sTemp = {y, x};
        //If we are checking a spot that is in our eyes list, that means a piece has been placed on what was once an eye
        if(eyes.contains(temp))
        {
            eyes.remove(temp);
        }
        else if(eyes.contains(sTemp))
        {
            eyes.remove(sTemp);
        }
    
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
        
        Piece u = new Piece(x, y-1);
        Piece d = new Piece(x, y+1);
        Piece l = new Piece(x-1, y);
        Piece r = new Piece(x+1, y);

        int[] adjacents = {up, down, left, right};
        Piece[] pieces = {u, d, l, r};

        boolean inAGroup = false;
        //Check to see if a stone is in a group. The stone is in a group if it has at least one friendly stone adjacent to it
        for(int j = 0; j < adjacents.length; j++ )
        {
            if(adjacents[j] == color)
            {
                inAGroup = true;
                break;
            }
        }
        if(!inAGroup)
        {
            //System.out.println(x + " " + y + " is not in a group");
            return false;
        }

        //keep track of any eyes we find in this grouping. Check to see if the eyes found are surrounded by teammates/wall.
        //also keep track of the number of friendly pieces this grouping has

        //ArrayList<int[]> eyeCoords = new ArrayList<>();

        for(int i = 0; i < adjacents.length; i++)
        {
            
            int currColor = adjacents[i];
            int[]coords = pieces[i].getCoordinates();
            int xc = coords[0];
            
            int yc = coords[1];
            
            if(checked[yc][xc])
            {
                return false;
            }
            
            
            //One of the adjacent spots is empty. If it's not already in our list of eyes, add it.
            if(currColor == 0)
            {
                //eyes++;
                
                int[] toAdd = {xc, yc};
                if(!eyes.contains(toAdd))
                {
                    //System.out.println(xc + " " + yc + " is being added to our list of eyes");
                    eyes.add(toAdd);
                    
                }
                
            }
            else if(currColor == color)
            {
                fStones++;
                checked[y][x] = true;
                System.out.println("Caling multiple eyes: " + x + " " + y);
                if(multipleEyes(coords[0], coords[1], currColor, eyes, fStones,checked))
                {
                    return false;
                }
            }
        }
        
        //Exiting the loop means we've hit a point where we are no longer connected to an eye or a friendly stone
        //If we have less than 4 stones in our chain, we can't possibly have 2 eyes "within" our borders
        if(fStones < 5)
        {
            //System.out.println(x + " " + y + " doesn't have enough stones in the group");
            return false;
        }
        //if we have more than 4 stones, it's possible we have 2 eyes within. Check to see how many eyes are in our list
        else
        {
            //System.out.println("We have more than 4 in our group");
            if(eyes.size() < 2)    //if we don't have at least 2 eyes our chain is connected to, we can return false
            {
                System.out.println(x + " " + y + " not enough eyes");
                return false;
            }
            //Otherwise, we need to check our eyes and see what's around them. If two or more are surrounded by only wall/same color stones, 
            //we successfully have 2 eyes within our borders and the whole chain is alive
            else
            {
                //System.out.println("We have more than 4 in our group. We have more than 2 eyes");
                for(int c = 0; c < eyes.size(); c++)
                {
                    int[] point = eyes.get(c);
                    int xToSearch = point[0];
                    int yToSearch = point[1];
                    int[] adjacentPoints = retrieveAdjacents(xToSearch, yToSearch, color);
                    int eyeTouchingEnemyCount = 0;
                    for(int z = 0; z < adjacentPoints.length; z++)
                    {
                        //if any of the eyes within a grouping are touching the opponent, we can't have them secured
                        
                        if(z == oppositeColor(color))
                        {
                            eyeTouchingEnemyCount++;
                        }
                    }
                    //This means that all of our eyes are touching the enemy
                    //System.out.print("Size of eyes:" + eyes.size());
                    //System.out.println(eyes.size() - eyeTouchingEnemyCount);
                    if(eyes.size() - eyeTouchingEnemyCount < 2)
                    {
                        System.out.println(x + " " + y +" not enough eyes that are not touching an enemy");
                        return false;
                    }


                }
                return true;
            }
        }
        //return false;
    }
    */


    public static boolean hasLiberties(int x, int y, int color, boolean[][]checked)
    {

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
            //check if there are more than 1 open spots



            //System.out.println(x + " , " + y + "can breathe");
            return true;
        }

        int throughX = 0;
        int throughY = 0;
        checked[y][x] = true;
        if(!hasLiberties(x, y + 1, color,checked ))
        {
            checked[y+1][x] = true;
            
            //If the left liberty can't breathe, check the up liberty
            if(!hasLiberties(x -1, y, color, checked))
            {
                checked[y][x-1] = true;
                //Check the up liberty
                
                if(!hasLiberties(x, y -1, color, checked))
                {
                    checked[y-1][x] = true;
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

    public static boolean enclosed(int x, int y)
    {
        if (x < 0 || x >= 9 || y < 0 || y >= 9 || visited[y][x]) {
            return true;
        }

        visited[y][x] = true;

        
         boolean right = enclosed(x + 1, y);
         boolean left = enclosed(x - 1, y);
         boolean down = enclosed(x , y+ 1);
         boolean up = enclosed(x , y-1);

         if(!up || !down || !left || !right)
         {
            return false;
         }
         return true;
        
    }

    public static void calculateScore()
    {
        for(int y = 0; y < 9; y++)
        {
            for(int x = 0; x <9; x++)
            {
                if(enclosed(x, y))
                {
                    Set<Piece> t = calculateTerritory(x, y);
                    int total = t.size();
                    int team = whichTeam(x, y);
                    if(total > 28)
                    {
                        team = 3;
                    }
                    System.out.println("Team "+team+" "+x + " " + y + " is in a territory that contains " + total + "spots");
                    
                }
            }
        }
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

        return 3;
    }

    public static void canBreathe()
    {
        boolean[][] visited = new boolean[9][9];
        boolean[][] secondCheck = new boolean[9][9];

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
                    ArrayList<int[]> eyes = new ArrayList<>();
                    
                    /*
                    System.out.println("Calling multiple eyes with: " + x + " " + y);
                    boolean manyEyes = multipleEyes(x, y, color, eyes, 0, secondCheck);
                    //secondCheck[y][x] = true;
                    System.out.println();
                    //A stone is connected to at least one liberty
                    if(colorCanBreathe)
                    {
                        //if many eyes is true, that means a stone is a part of a group that has 2 or more eyes and is safe
                        if(manyEyes)
                        {
                            System.out.println("Group of stones with 2 eyes");
                        }
                    }
                    */
                    
                   
                    
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
                    System.out.println("Black piece at (" + x + ", " + y + ") has been captured");
                    blackScore += 1;
                    gBoard[y][x] = "|";
                    
                }
                else if(color == 2 && !lives)
                {
                    System.out.println("White piece at (" + x + ", " + y + ") has been captured");
                    whiteScore += 1;
                    gBoard[y][x] = "|";
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
