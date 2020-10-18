/* 
* Name: Kia Pezesh
* Date: 
* Classname: SudokuSolver.java
* Details: Simple Sudoku puzzle solver using Backtracking algorithm
*/ 
//import java.util.Arrays;
import java.util.Scanner;
import java.util.HashMap;
import java.io.*;
import java.lang.String;
import java.lang.System;


public class SudokuSolver {

    private static String get_input(String[] args) throws IOException {
        //initialize in scanner    
        Scanner in = new Scanner(System.in);
        // prompt user & obtain filename
        System.out.print("Enter file name: ");
        final String filename = in.nextLine();

        // validation check for filename
        if (filename.isBlank()) {
            System.out.println("Invalid file name.");
            System.exit(0);
        }
        //close scanner
        in.close();
        return filename;
    }

    public static int[][] populate_grid (String filename, int[][] array) throws IOException {
        //initialize FileReader
        FileReader f = null;
        // open the file
        final File file = new File(filename);
        try {
            f = new FileReader(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            System.exit(0);
        }
        System.out.println();

        //populate Sudoku board into 2D array
        int x, row=0, col=0;
        while((x=f.read()) != -1) {
            //ASCII 32 is " ", 10 is "\n", 13 is "\r"
            if (x==32 || x==10 || x==13){
                continue;
            }
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

    //n to specify if it's the full board (9x9) or a sub-grid (3x3)
    private static void print_grid (int[][] array, int n) {
        for (int y=0;y<n;y++) {
            if (y == 3 || y == 6) {
                System.out.print("----+-----+----");
                System.out.println();
            }
            for (int x=0;x<n;x++) {
                if (x == 3 || x == 6) {
                    System.out.print(" | ");
                }
                System.out.print(array[y][x]);
            }
            System.out.println();
        }
    }
    //subgrids are numbered from 0-9 from top-left to bottom-right (eg. 4 is center grid)
    private static int get_subgrid (int row, int col) {
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
    private static boolean check_subgrid (int[][] grid, int subgrid, int n) {

        HashMap<Integer, Integer> h = new HashMap<>(9);

        switch (subgrid) {
            //Top row subgrids
            case 0: //top left
                for (int row=0;row<3;row++) {
                    for (int col=0;col<3;col++) {
                        if (grid[row][col] > 0) h.put(grid[row][col], grid[row][col]);
                    }
                }
                break;
            case 1: //top center
                for (int row=0;row<3;row++) {
                    for (int col=3;col<6;col++){
                        if (grid[row][col] > 0) h.put(grid[row][col], grid[row][col]);
                    }
                }
                break;

            case 2: //top right
                for (int row=0;row<3;row++) {
                    for (int col=6;col<9;col++){
                        if (grid[row][col] > 0) h.put(grid[row][col], grid[row][col]);
                    }
                }
                break;
            //Middle row subgrids
            case 3: //middle left
                for (int row=3;row<6;row++) {
                    for (int col=0;col<3;col++){
                        if (grid[row][col] > 0) h.put(grid[row][col], grid[row][col]);
                    }
                }
                break;

            case 4: //middle center
                for (int row=3;row<6;row++) {
                    for (int col=3;col<6;col++){
                        if (grid[row][col] > 0) h.put(grid[row][col], grid[row][col]);
                    }
                }
                break;

            case 5: //middle right
                for (int row=3;row<6;row++) {
                    for (int col=6;col<9;col++){
                        if (grid[row][col] > 0) h.put(grid[row][col], grid[row][col]);
                    }
                }
                break;
            //Bottom row subgrids
            case 6: //bottom right
                for (int row=6;row<9;row++) {
                    for (int col=0;col<3;col++){
                        if (grid[row][col] > 0) h.put(grid[row][col], grid[row][col]);
                    }
                }
                break;
            case 7: //bottom center
                for (int row=6;row<9;row++) {
                    for (int col=3;col<6;col++){
                        if (grid[row][col] > 0) h.put(grid[row][col], grid[row][col]);
                    }
                }
                break;
            case 8: //bottom right
                for (int row=6;row<9;row++) {
                    for (int col=6;col<9;col++){
                        if (grid[row][col] > 0) h.put(grid[row][col], grid[row][col]);
                    }
                }
                break;
        }

        //return false if n is already in the subgrid
        if (h.containsKey(n)) return false;
        //else val is a valid entry into the subgrid
        return true;
    }
    //https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/HashMap.html#%3Cinit%3E()
    private static boolean validate_entry (int[][] grid, int n, int row, int col){
        //initialize Hashmap with a capacity of 9
        HashMap<Integer, Integer> h = new HashMap<>(9);
        int val;

        //check row
        for (int i=0;i<9;i++){
            val = grid[row][i];
            //place all row entries into hashmap
            if (val > 0) {
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
            if (val> 0) {
                h.put(val, val);
            }
        }
        //check if n is a valid entry in the column
        if (h.containsKey(n)) return false;

        //if this return statement is reached, row & col checks have passed. 
        //return the result of the check_subgrid method
        return check_subgrid(grid, get_subgrid(row, col), n);
    }

    private static int[][] solve_grid (int[][] grid) {
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
                        //recurse (continue to next cell in the grid)
                        solve_grid(grid); 
                        //if backtracking, set current cell back to 0
                        grid[row][col] = 0;
                    }
                }
                //Backtrack. If this is is reached, all n (0-9) not possible. 
                return grid; 
            }
        }

        //IDEA: can add global var here and check it after the first solution is printed for user confirmation to show more boards.

        //have to print here since this method will backtrack to the original grid upon reaching this point (the return below)
        print_grid(grid, 9);
        //rewind
        return grid;
    }

    public static void main (final String[] args) throws IOException, NullPointerException {
        //get input file name
        final String filename = get_input(args);
        //initialize empty Sudoku grid
        int[][] grid = new int[9][9];
        //open file & pupulate main grid
        grid = populate_grid(filename, grid);
        //print unsolved board
        System.out.println("Unsolved Sudoku Table: \n");
        print_grid(grid, 9);
        System.out.println();
        System.out.println("Solved Sudoku Table(s): \n");
        solve_grid(grid);

        return;
    }
}
