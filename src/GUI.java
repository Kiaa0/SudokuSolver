// SudokuSolver exe icon author: Icons made by Icons made by <a href="http://www.freepik.com/" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
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

    //initialize GUI 
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
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j] = new JTextField(1);
                cells[i][j].setEditable(false);
                //check for corners
                if (isCorner(i, j)) {
                    cells[i][j].setBorder(BorderFactory.createMatteBorder(0, 1, 3, 3, Color.black));
                }// top, left, bottom, right, color
                else if (i == 2 || i == 5) {
                    cells[i][j].setBorder(BorderFactory.createMatteBorder(0, 1, 3, 1, Color.black));
                } else if (j == 2 || j == 5) {
                    cells[i][j].setBorder(BorderFactory.createMatteBorder(1, 1, 1, 3, Color.black));
                } else {
                    cells[i][j].setBorder(BorderFactory.createLineBorder(Color.black, 1));
                }
                gridPanel.add(cells[i][j]);
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
    //check for corners in sudoku grid
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
    //fill grid with contents of sudoku[][]
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
    //update cells[][] with solver results (to be called from the Solver class)
    public void updateDisplay(int row, int col, int n) {
        if (n == 0) {
            cells[row][col].setText("");
            return;
        }
        cells[row][col].setText(Integer.toString(n));
        cells[row][col].setForeground(Color.gray);
    }
    //clear board & set editable
    private void clearBoard() {
        for (int i=0;i<9;i++) {
            for (int j=0;j<9;j++) {
                sudoku[i][j] = 0;
                cells[i][j].setText("");
                cells[i][j].setEditable(true);
                cells[i][j].setDocument(new JTextFieldCharLimit(1));
                cells[i][j].setFont(font);
                cells[i][j].setHorizontalAlignment(JTextField.CENTER);
                cells[i][j].setForeground(Color.black);
            }
        }
    }
    //check if sudoku grid is empty
    private static boolean isEmpty (int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                if (array[i][j] == 0) continue;
                return false;
            }
        }
        return true;
    }
    //read user input into sudoku[][]
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
    //validate sudoku[][] (to be called following readInput())
    private boolean validate() {
        boolean result = true;
        int[] temp = new int[3];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells.length; j++) {
                //store index & value of current sudoku cell
                temp[0] = sudoku[i][j];
                temp[1] = i; temp[2] = j;
                //temporarily set to 0 
                sudoku[i][j] = 0;
                //validate
                if (!solver.validate_entry(sudoku, temp[0], i, j)) {
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
    public void invalid_file() {
        title.setText("File not found");
        title.setForeground(Color.red);
    } 

// !! MAIN METHOD ~~~~~~~~~~~~~~~
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

    @Override
    public void actionPerformed(ActionEvent e) {
        // * Solve
        //attempt to solve sudoku[][]. Timeout if unable within ~X~ seconds
        if (e.getSource() == solveButton) {
            if (isEmpty(sudoku)) return;
            // solve
            System.out.println("Solve");
            solver.lock = false;
            solver.solve_grid(sudoku);
            System.out.println("Solved");
        }

        // * show original puzzle
        //show unsolved puzzle (populates the grid with contents of sudoku[][])
        else if (e.getSource() == unsolveButton){
            System.out.println("Show Original");
            populateGridPanel();
        }

        // * Insert Puzzle
        //allows user to input their own puzzle cell by cell
        else if (e.getSource() == insertPuzzleButton){
            title.setText("Sudoku Solver v0.1");
            title.setForeground(Color.black);
            //let user input a sudoku board
            System.out.println("Inset Puzzle");
            //clear gridpanel & set editable to true
            clearBoard();
            //update buttons
            confirmButton.setVisible(true);
            solveButton.setVisible(false);
            unsolveButton.setVisible(false);
            insertPuzzleButton.setVisible(false);
            openPuzzleButton.setVisible(false);
        }

        // * Confirm Input
        //reads & validates user input
        else if (e.getSource() == confirmButton) {
            //read user input
            readInput();
            //check if valid
            if (validate()) {
                title.setText("Sudoku Solver v0.1");
                title.setForeground(Color.black);
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

        // *OpenPuzzleButton 
        //assumes the selected file is in the correct format.
        else if (e.getSource() == openPuzzleButton){
            //in case previously chosen file was not found
            title.setText("Sudoku Solver v0.1");
            title.setForeground(Color.black);

            //open sudoku board file
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
                    sudoku = solver.populate_grid(selectedFile.getAbsolutePath(), sudoku);
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
