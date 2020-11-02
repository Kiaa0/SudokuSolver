import java.util.HashMap;
import java.io.*;
import java.lang.String;
import java.lang.System;

/* 
 * Author: Kia Pezesh
 * Description: GUI.java uses Swing to draw a visual Sudoku solving application. 
 *              This application allows the user to insert a Sudoku puzzle (by hand or by uploading a formated .txt file)
 *              and solves the puzzle using backtracking, which is contained in Solver.java.
 */
public class Solver {
    //global variable used to 'signal' when the first solution has been found.
    public boolean lock = false;
    //initialize GUI object
    private GUI gui = new GUI();

    /*
     * Name:        solve_grid()
     * Description: Finds the first solution for a given Sudoku puzzle using backtracking. This method is recursively called
     *              after each new value is validated as part of the solution. When no value can be validated, the method returns
     *              the grid[][] that it started with (backtrack) until a new value can be validated as part of the solution. 
     *              Since this backtracking algorith will not halt on its own after finding the first solution to the given puzzle (improperly formulated Sudoku puzzles have >1 solution),
     *              a gloval boolean variable 'lock' is used to stop any further solving and instead makes the algorithm return grid[][] until the stack is cleared.
     * @params:     int[][] grid - a 9x9 Sudoku puzzle.
     * @returns:    int[][] grid - Each return is one step back in the backtracking process       .
     */
    public int[][] solveGrid (int[][] grid) {
        //return after finding the first possible solution. Base case.
        if (lock) return grid;
        //parse each cell in the Sudoku board
        for (int row=0;row<9;row++) {
            for (int col=0;col<9;col++) {
                //find an empty cell
                if (grid[row][col] > 0) continue;
                //find a possible entry for the empty cell
                for (int n=1;n<10;n++) {
                    //check if n is a valid entry to the current Sudoku board
                    if (validateEntry(grid, n, row, col)) {
                        //enter n into the grid (if valid)
                        grid[row][col] = n;
                        //Update sudoku board here
                        if (lock == false) {
                            gui.updateDisplay(row, col, n);
                        }
                        //recurse (continue to next cell in the grid)
                        solveGrid(grid); 
                        //if backtracking, set current cell back to 0
                        grid[row][col] = 0;
                    }
                }
                //un-update sudoku board (step back)
                if (lock == false) {
                    gui.updateDisplay(row, col, 0);
                }
                //Backtrack. If this is is reached, all n (0-9) not possible. 
                return grid; 
            }
        }
        //If this is reached, a solution has been found: lock any further solving from occuring
        if (!lock) {
            lock = true; 
        }
        //begin rewind
        return grid;
    }
    /*
     * Name:        validateEntry()
     * Description: This method takes a given value n at location row,col and checks whether it is already present
     *              in it's row, column, or subgrid. 
     *              This is done by using a Hashmap, and filling it first with all of the values present in the row, then in the columd, and finally the subgrid.
     *              For each case, the containsKey() method is used to check if n is present in the current Hashmap. The Hashmap is cleared between checking the row, column, and subgrid.
     *              This method is additionally used to validate user-inputted Sudoku puzzles from the validate() method in GUI.java. 
     * @params:     int[][] grid    - a 9x9 Sudoku puzzle.
     *              int n           - the value we are validating as part of the Sudoku solution.
     *              int row, col    - the index of the value we are validating.
     * @returns:    boolean true    - if the value n is a valid entry at location row,col in the current Sudoku board.
     *              boolean false   - if the value n is not a valid entry at location row,col in the current Sudoku board.
     */
    public boolean validateEntry (int[][] grid, int n, int row, int col){
        //initialize Hashmap with a capacity of 9
        HashMap<Integer, Integer> h = new HashMap<>(9);
        int val;
        // * check row
        for (int i=0;i<9;i++){
            val = grid[row][i];
            //place all row entries into hashmap
            if (val > 0) {
                h.put(val, val);
            }
        }
        //check if n is a valid entry in the row
        if (h.containsKey(n)) return false;
        //clear hashmap before checking column
        h.clear();

        // * check column
        for (int i=0;i<9;i++) {
            val = grid[i][col];
            if (val > 0) {
                h.put(val, val);
            }
        }
        //check if n is a valid entry in the column
        if (h.containsKey(n)) return false;

        // * check subgrid & return the result
        return checkSubgrid(grid, getSubgrid(row, col), n);
    }
    /*
     * Name:        getSubgrid()
     * Description: This method uses the row,col index provided by the parameters to identify which subgrid
     *              the indexed cell belongs in. This is done in the context of a 9x9 Sudoku grid containing nine 3x3 subgrids.
     * @params:     int row, col   - the index of the cell for which we are identifying the subgrid it belongs to.
     * @returns:    int            - an integer value from 0-8 representing the 9 Sudoku board subgrids
     *                               ordered from top left to bottom right (top left is 0, bottom right is 8).
     */
    private int getSubgrid (int row, int col) {
        //Top row (subgrids 0,1,2)
        if (row < 3) {
            if (col < 3) return 0;
            if (col < 6) return 1;
            return 2;
        }
        //Middle row (subgrids 3, 4, 5)
        if (row < 6) {
            if (col < 3) return 3;
            if (col < 6) return 4;
            return 5;
        }
        //Bottom row (subgrids 6,7,9)
        if (col < 3) return 6;
        if (col < 6) return 7;
        return 8;
    }
    /*
     * Name:        checkSubgrid()
     * Description: This method first uses the subgrid number obtained from the getSubgrid() method to find the index ranges of the current subgrid
     *              Then, a hashmap is used and every non-zero value existant in the subgrid is added to the hashmap. Once finished, we check if the 
     *              hashmap contains the value n. If n is not already in the hashmap, we know n is a valid entry for this subgrid.
     * @params:     int [][] grid   - the 9x9 Sudoku board.
     *              int subgrid     - the subgrid number (0-8, 0 is top left subgrid, 8 is bottom right).
     *              int n           - the value we are validating for the given subgrid.
     * @returns:    boolean true    - n is a valid entry to the subgrid.
     *              boolean false   - n is not a valid entry to the subgrid.
     */
    private boolean checkSubgrid (int[][] grid, int subgrid, int n) {
        //Subgrid index range vars: row bottom, row top, column bottom, column top
        int rb = 0, rt = 0, cb = 0, ct = 0;
        HashMap<Integer, Integer> h = new HashMap<>(9);

        switch (subgrid) {
        //Top row subgrids
            case 0: //top left
                rb = 0; rt = 3; cb = 0; ct = 3;
                break;
            case 1: //top center
                rb = 0; rt = 3; cb = 3; ct = 6;
                break;
            case 2: //top right
                rb = 0; rt = 3; cb = 6; ct = 9;
                break;
        //Middle row subgrids
            case 3: //middle left
                rb = 3; rt = 6; cb = 0; ct = 3;
                break;
            case 4: //middle center
                rb = 3; rt = 6; cb = 3; ct = 6;
                break;
            case 5: //middle right
                rb = 3; rt = 6; cb = 6; ct = 9;
                break;
        //Bottom row subgrids
            case 6: //bottom right
                rb = 6; rt = 9; cb = 0; ct = 3;
                break;
            case 7: //bottom center
                rb = 6; rt = 9; cb = 3; ct = 6;
                break;
            case 8: //bottom right
                rb = 6; rt = 9; cb = 6; ct = 9;
                break;
        }
        for (int row = rb ; row < rt ; row++) {
            for (int col = cb ; col < ct ; col++) {
                if (grid[row][col] > 0) h.put(grid[row][col], grid[row][col]);
            }
        }
        //return false if n is already in the subgrid
        if (h.containsKey(n)) return false;
        //else n is a valid entry into the subgrid
        return true;
    }  
    /*
     * Name:        populateGrid()
     * Description: This method opens a .txt file selected by the user, then populates array[][] with the Sudoku puzzle contained in the file. 
     *              This method assumes the file selected by the user is in the correct format (9x9 integers from 0-9, 0 indicating a blank cell in the Sudoku board).
     * @params:     String filename - the name of the file the user has selected to open.
     *              int[][] array   - the 2D array in which we will populate the contents of filename into.
     * @returns:    int[][] array   - the populated 2D array containing the Sudoku puzzle.
     */
    public int[][] populateGrid (String filename, int[][] array) throws IOException {
        //initialize FileReader
        FileReader f = null;
        // open the file
        final File file = new File(filename);
        try {
            f = new FileReader(file);
        } catch (FileNotFoundException e) {
            //System.out.println("File not found.");
            //Show file not found message on GUI
            gui.invalid_file();
            return array;
        }
        //System.out.println();
        //populate Sudoku board into 2D array
        int x, row=0, col=0;
        while((x=f.read()) != -1) {
            //ASCII 32 is " ", 10 is "\n", 13 is "\r"
            if (x==32 || x==10 || x==13) continue;
            //convert ASCII to int and place in 2D array
            array[row][col++] = x - 48;
            //move 2D array indices
            if (col==9) {
                row++;
                col=0;
                continue;
            }
        }
        //close the file
        f.close();
        return array;
    }
}