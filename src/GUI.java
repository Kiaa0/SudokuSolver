// SudokuSolver exe icon author: Icons made by <a href="http://www.freepik.com/" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;


/* 
 * Author: Kia Pezesh
 * Description: GUI.java uses Swing to draw a visual Sudoku solving application. 
 *              This application allows the user to insert a Sudoku puzzle (by hand or by uploading a formated .txt file)
 *              and solves the puzzle using backtracking, which is contained in Solver.java.
 */
public class GUI implements ActionListener {
    private static JFrame frame;
    private static JPanel gridPanel;
    private static JPanel menuPanel;
    private static JLabel title;
    private static Font font;
    private static JButton confirmButton;
    private static JButton solveButton;
    private static JButton unsolveButton;
    private static JButton insertPuzzleButton;
    private static JButton openPuzzleButton;
    private static JTextField[][] cells;
    private static int[][] sudoku;
    private static Solver solver;

    /*
     * Name:        initialize()
     * Description: The initialize() method sets up all Swing components
     *              and draws the GUI for the application and fills the visible
     *              Sudoku board with the contents of 2D array sudoku[][].
     *              This method is only to be called once at application startup.
     * @params:     null
     * @returns:    void
     */
    private static void initialize() {
        GUI gooey = new GUI();
        // * frame setup
        frame.setSize(600, 500); // x, y
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // add title and panels
        title = new JLabel("Sudoku Solver v0.1", SwingConstants.CENTER);
        title.setPreferredSize(new Dimension(600, 16));
        frame.add(title, BorderLayout.PAGE_START);
        frame.add(gridPanel, BorderLayout.CENTER);
        frame.add(menuPanel, BorderLayout.LINE_END);
        frame.setResizable(false);

        // * gridPanel setup
        gridPanel.setBackground(Color.lightGray);
        gridPanel.setLayout(new GridLayout(9, 9));
            //draw grid (using borders)
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col] = new JTextField(1);
                cells[row][col].setEditable(false);
                cells[row][col].setFont(font);
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                //check for "corners" and draw appropriate Sudoku board lines
                if (isCorner(row, col)) {
                    cells[row][col].setBorder(BorderFactory.createMatteBorder(0, 1, 3, 3, Color.black));
                } else if (row == 2 || row == 5) {
                    cells[row][col].setBorder(BorderFactory.createMatteBorder(0, 1, 3, 1, Color.black));
                } else if (col == 2 || col == 5) {
                    cells[row][col].setBorder(BorderFactory.createMatteBorder(1, 1, 1, 3, Color.black));
                } else {
                    cells[row][col].setBorder(BorderFactory.createLineBorder(Color.black, 1));
                }
                gridPanel.add(cells[row][col]);
            }
        }
        // * menuPanel setup
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Color.lightGray);
        menuPanel.setPreferredSize(new Dimension(116, 500));
        menuPanel.add(Box.createVerticalGlue());
            // spacing
        Dimension spacing = new Dimension(0, 30);
            //menuPanel.add(Box.createRigidArea(spacing));
        confirmButton = new JButton("Confirm Input");
        confirmButton.addActionListener(gooey);
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmButton.setVisible(false); //initially invisible
        menuPanel.add(confirmButton);
        menuPanel.add(Box.createVerticalGlue());

        // * solveButton setup
        solveButton = new JButton("Solve");
        solveButton.addActionListener(gooey);
        solveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(solveButton);
        menuPanel.add(Box.createVerticalGlue());

        // * unsolveButton setup
        unsolveButton = new JButton("Show Original");
        unsolveButton.addActionListener(gooey);
        unsolveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(unsolveButton);
        menuPanel.add(Box.createVerticalGlue());

        // * insertPuzzleButton setup
        insertPuzzleButton = new JButton("Insert Puzzle");
        insertPuzzleButton.addActionListener(gooey);
        insertPuzzleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(insertPuzzleButton);
        menuPanel.add(Box.createVerticalGlue());

        // * openPuzzleButton setup
        openPuzzleButton = new JButton("Open Puzzle");
        openPuzzleButton.addActionListener(gooey);
        openPuzzleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(openPuzzleButton);
        menuPanel.add(Box.createVerticalGlue());

        // spacing
        menuPanel.add(Box.createRigidArea(spacing)); // x, y?
        menuPanel.add(Box.createVerticalGlue());
    }
    /* 
     * Name:        isCorner() 
     * Description: This method checks the current row, col position in the sudoku board to check if a darkened border line is needed.
     *              This method aids in the drawing of the two verical and horizontal lines in a Sudoku board which separate the 9 3x3 subgrids.
     * @params:     int row - the horizontal position of the current cell of the Sudoku board.
     *              int col - the vertical position of the current cell of the Sudoku board.
     * @returns:    boolean true - if the current cell is a subgrid "corner".
     *              boolean false - if the current cell is not a subgrid "corner".
     */
    private static boolean isCorner(int row, int col) {
        if (row == 2) {
            if (col == 2 || col == 5)
                return true;
        }
        if (row == 5) {
            if (col == 2 || col == 5)
                return true;
        }
        return false;
    }
    /*
     * Name:        populateGridPanel()
     * Description: This method populates the visible Suoku board with the 
     *              contents of sudoku[][], which is filled by the end-user by 
     *              either manually insertion or by file upload. 
     * @params:     null
     * @returns:    void
     */
    private static void populateGridPanel() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudoku[i][j] == 0)
                    cells[i][j].setText("");
                else {
                    cells[i][j].setText(Integer.toString(sudoku[i][j]));
                }
                // set font and alignment
                cells[i][j].setFont(font);
                cells[i][j].setHorizontalAlignment(JTextField.CENTER);
                cells[i][j].setForeground(Color.black);
                cells[i][j].setEditable(false);
            }
        }
    }
    /*
     * Name:        updateDisplay()
     * Description: This method inserts a value 'n' given by Solver.java into cells[row][col] and is made visible in the gridpanel.
     *              This method is public and is meant to be called from Solver.java.
     * @params:     int row - the vertical alignment of the current grid cell.
     *              int col - the horizontal alignment of the current cell.
     *              int n   - the integer value to be placed into the current cell.
     * @returns:    void
     */
    public void updateDisplay(int row, int col, int n) {
        if (n == 0) {
            cells[row][col].setText("");
            return;
        }
        cells[row][col].setText(Integer.toString(n));
        cells[row][col].setForeground(Color.gray);
    }
    /*
     * Name:        clearBoard()
     * Description: This method clears both sudoku[][] and cells[][]. The Sudoku gridpanel visible to the user is cleared and set to be editable.
     *              This prepares the application to receive a user-inputted Sudoku puzzle. 
     * @params:     null
     * @returns:    void
     */
    private void clearBoard() {
        for (int i=0;i<9;i++) {
            for (int j=0;j<9;j++) {
                sudoku[i][j] = 0;
                cells[i][j].setText("");
                cells[i][j].setEditable(true);
                // limit user input per-cell to one integer between 1-9
                cells[i][j].setDocument(new JTextFieldCharLimit(1));
                cells[i][j].setFont(font);
                cells[i][j].setHorizontalAlignment(JTextField.CENTER);
                cells[i][j].setForeground(Color.black);
            }
        }
    }
    /*
     * Name:        isEmpty()
     * Description: This method checks if a given 2D array is empty.
     * @params:     int[][] array - The 2D array to be checked.
     * @returns:    boolean true - if array[][] is empty.
     *              boolean false - if a non-zero value is contained in array[][].
     */
    private static boolean isEmpty (int[][] array) {
        if (array == null) return true;
        return false;
    }

    /*
     * Name:        readInput()
     * Description: Reads the contents of cells[][] and writes it into sudoku[][]. 
     *              This method is meant to be used to read user-inputted Sudoku board.
     * @params:     null
     * @returns:    void     
     */
    private static void readInput() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells.length; j++) {
                try {
                    sudoku[i][j] = Integer.parseInt(cells[i][j].getText());
                } catch (Exception e) {
                    sudoku[i][j] = 0;
                }
                System.out.print(sudoku[i][j]+ " ");
            }
            System.out.println();
        }
    }
    /*
     * Name:        validate()
     * Description: This method takes the user-inputted Sudoku puzzle and checks if it is solvable by calling validiateEntry() in Solver.java. (eg. does not break Sudoku puzzle rules)
     *              The validation checking is done on 2D array sudoku[][], so this method is meant to be called after readInput() has been called.
     *              Invalid entries are highlited red unit corrected.
     * @params:     null
     * @returns:    boolean true - if Sudoku puzzle is valid.
     *              boolean false - if Sudoku puzzle is not valid.
     */
    private boolean validate() {
        boolean result = true;
        int[] temp = new int[3];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells.length; j++) {
                //store value & index of current sudoku cell
                temp[0] = sudoku[i][j];
                temp[1] = i; temp[2] = j;
                //temporarily set to 0 
                sudoku[i][j] = 0;
                //validate
                if (!solver.validateEntry(sudoku, temp[0], i, j)) {
                    result = false;
                    System.out.println("entry "+temp[0]+" at "+temp[1]+", "+temp[2]+" invalid");
                    cells[i][j].setForeground(Color.red);
                }else {
                    cells[i][j].setForeground(Color.black);
                }
                sudoku[temp[1]][temp[2]] = temp[0];
            }
        }
        return result;
    }
    //show invalid file message in the title JLabel. To be called from Solver.java
    /*
     * Name:        invalid_file()
     * Description: Updates GUI title JLabel to communicate that the user's chosen file was not found.
     * @params:     null
     * @returns:    void
     */
    public void invalid_file() {
        title.setText("File not found");
        title.setForeground(Color.red);
    } 

// !! MAIN METHOD 
    public static void main(String[] args) throws Exception {
        //initialize objects & arrays
        frame = new JFrame();
        gridPanel = new JPanel();
        menuPanel = new JPanel();
        solver = new Solver();
        font = new Font("Courier", Font.BOLD, 20);
        cells = new JTextField[9][9];
        sudoku = new int[9][9];
        //initialize gui 
        initialize();
        //make visible
        frame.setVisible(true);
    }
// ! Buttons
    @Override
    public void actionPerformed(ActionEvent e) {
        // * Solve
        //attempt to solve sudoku[][].
        if (e.getSource() == solveButton) {
            if (isEmpty(sudoku)) return;   
            //System.out.println("Solve");
            solver.lock = false;
            solver.solveGrid(sudoku);
            //System.out.println("Solved");
        }

        // * Show Original Puzzle
        //show unsolved puzzle (populates the grid with contents of sudoku[][])
        else if (e.getSource() == unsolveButton){
            System.out.println("Show Original");
            populateGridPanel();
        }

        // * Insert Puzzle
        //allows user to input their own puzzle cell by cell
        else if (e.getSource() == insertPuzzleButton){
            //clear error message if there is one; display application title
            if (title.getText() != "Sudoku Solver v0.1") {
                title.setText("Sudoku Solver v0.1");
                title.setForeground(Color.black);
            }
            //let user input a sudoku board: clear gridpanel & set editable to true
            clearBoard();
            //update buttons
            confirmButton.setVisible(true);
            solveButton.setVisible(false);
            unsolveButton.setVisible(false);
            insertPuzzleButton.setVisible(false);
            openPuzzleButton.setVisible(false);
        }

        // * Confirm Input
        //reads & validates user inputted Sudoku Puzzle
        else if (e.getSource() == confirmButton) {
            //copy user input from cells[][] to sudoku[][]
            readInput();
            //check if valid
            if (validate()) {
                //clear error message if there is one; display application title
                if (title.getText() != "Sudoku Solver v0.1") {
                    title.setText("Sudoku Solver v0.1");
                    title.setForeground(Color.black);
                }
                //update buttons
                confirmButton.setVisible(false);
                solveButton.setVisible(true);
                unsolveButton.setVisible(true);
                insertPuzzleButton.setVisible(true);
                openPuzzleButton.setVisible(true);
                //make cells uneditable by user
                for (int i=0;i<9;i++) {
                    for (int j=0;j<9;j++) {
                        cells[i][j].setEditable(false);
                    }
                }
            } else {
                //invalid input
                title.setText("Invalid Sudoku Puzzle");
                title.setForeground(Color.red);
            }
        }

        // *Open Puzzle
        //Let user open a .txt file containing the desired Sudoku Puzzle. Assumes the selected file is in the correct format.
        else if (e.getSource() == openPuzzleButton){
            //clear error message if there is one; display application title
            if (title.getText() != "Sudoku Solver v0.1") {
                title.setText("Sudoku Solver v0.1");
                title.setForeground(Color.black);
            }

            //open file containing sudoku puzzle 
            System.out.println("Open Puzzle");
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
            chooser.setFileFilter(filter);
            chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = chooser.showOpenDialog(openPuzzleButton);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                System.out.println("Selected file: "+selectedFile.getAbsolutePath());
                try {
                    sudoku = solver.populateGrid(selectedFile.getAbsolutePath(), sudoku);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return;
                }
                //load sudoku board onto gridpanel
                populateGridPanel();
            }
        }
    }
}
