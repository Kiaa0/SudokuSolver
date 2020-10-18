import java.util.HashMap;
import java.io.*;
import java.lang.String;
import java.lang.System;


public class Solver {
    public boolean lock = false;
    GUI gui = new GUI();
    public int[][] solve_grid (int[][] grid) {
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
                    if (validate_entry(grid, n, row, col)) {
                        //enter n into the grid (if valid)
                        grid[row][col] = n;

                        // ! Update sudoku board here
                        if (lock == false) {
                            gui.updateDisplay(row, col, n);
                        }
                        //recurse (continue to next cell in the grid)
                        solve_grid(grid); 
                        //if backtracking, set current cell back to 0
                        grid[row][col] = 0;
                    }
                }
                // ! un-Update sudoku board (backtrack)
              
                if (lock == false) {
                    gui.updateDisplay(row, col, 0);
                }
                
                //Backtrack. If this is is reached, all n (0-9) not possible. 
                return grid; 
            }
        }
        //send signal to GUI object to display solved board 
        if (!lock) {
            lock = true; 
            //gui.updateDisplay(grid);
        }
        //rewind
        return grid;
    }

    public boolean validate_entry (int[][] grid, int n, int row, int col){
        //initialize Hashmap with a capacity of 9
        HashMap<Integer, Integer> h = new HashMap<>(9);
        int val;

        //check row
        for (int i=0;i<9;i++){
            val = grid[row][i];
            //place all row entries into hashmap
            if (val > 0) { // ! TESTING && (i != COL)
                h.put(val, val);
            }
        }
        //check if n is a valid entry in the row
        if (h.containsKey(n)) return false;
        //clear map before checking column
        h.clear();

        //check column
        for (int i=0;i<9;i++) {
            val = grid[i][col];
            if (val > 0) { // ! TESTING && (i != row)
                h.put(val, val);
            }
        }
        //check if n is a valid entry in the column
        if (h.containsKey(n)) return false;

        //if this return statement is reached, row & col checks have passed. 
        //return the result of the check_subgrid method
        return check_subgrid(grid, get_subgrid(row, col), n);
    }

    //subgrids are numbered from 0-9 from top-left to bottom-right (eg. 4 is center grid)
    private int get_subgrid (int row, int col) {
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

    //subgrid == subgrid #, n == possible entry
    private boolean check_subgrid (int[][] grid, int subgrid, int n) {
        //row bottom, row top, column bottom, column top
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
        //else val is a valid entry into the subgrid
        return true;
    }


    //populate 2D array with contents of filename 
    public int[][] populate_grid (String filename, int[][] array) throws IOException {
        //initialize FileReader
        FileReader f = null;
        // open the file
        final File file = new File(filename);
        try {
            f = new FileReader(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            // ! Show file not found on GUI instead of system exit
            gui.invalid_file();
            return array;
        }
        System.out.println();

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