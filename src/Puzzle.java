/**
 * Puzzle maintains the internal representation of a square Slither Link puzzle.
 *
 * @author Abdihafith Isse
 * @version 1.0
 */
 
import java.util.ArrayList;

public class Puzzle
{
    private int[][] puzzle;         	// the numbers in the squares, i.e. the puzzle definition
                                    	// -1 if the square is empty, 0-3 otherwise
                                    
    private boolean[][] gridHorizontal; // the horizontal line segments of the grid
                                    	// true if the segment is on, false otherwise
    private boolean[][] gridVertical;   // the vertical line segments of the grid
                                    	// true if the segment is on, false otherwise                           
                                    
    private boolean[][] horizontal; 	// the horizontal line segments in the current solution
                                    	// true if the segment is on, false otherwise
    private boolean[][] vertical;   	// the vertical line segments in the current solution
                                    	// true if the segment is on, false otherwise
    
    
    /**
     * Creates the puzzle from file filename, and an empty solution.
     * filename is assumed to hold a valid puzzle.
     */
    public Puzzle(String filename)
    {
        FileIO file = new FileIO(filename);
        parseFile(file.getLines());
    }

    /**
     * Creates the puzzle from "eg5_1.txt".
     */
    public Puzzle()
    {
        this("eg7_2.txt");
    }

    /**
     * Returns the size of the puzzle.
     */
    public int size()
    {
        return puzzle.length;
    }

    /**
     * Returns the number layout of the puzzle.
     */
    public int[][] getPuzzle()
    {
        return puzzle;
    }

    /**
     * Returns the state of the current solution, horizontally.
     */
    public boolean[][] getHorizontal()
    {
        return horizontal;
    }

    /**
     * Returns the state of the current solution, vertically.
     */
    public boolean[][] getVertical()
    {
        return vertical;
    }
    
    /**
     * Returns the state of the current grid, horizontally.
     */
    public boolean[][] getGridHorizontal()
    {
        return gridHorizontal;
    }

    /**
     * Returns the state of the current grid, vertically.
     */
    public boolean[][] getGridVertical()
    {
        return gridVertical;
    }

    /**
     * Turns lines into a Slither Link puzzle.
     * The first String in the argument goes into puzzle[0],
     * The second String goes into puzzle[1], etc.
     * lines is assumed to hold a valid square puzzle; see eg3_1.txt and eg5_1.txt for examples.
     */
    public void parseFile(ArrayList<String> lines)
    {
        // numbers
        puzzle = new int[lines.size()][lines.size()];
        for (int r = 0; r < lines.size(); r++) {
            String line = lines.get(r);
            String[] rowValues = line.split(" ");
            for (int c = 0; c < lines.size(); c++) {
                puzzle[r][c] = Integer.parseInt(rowValues[c]);
            }
        }
        
        // grid
        gridHorizontal = new boolean[lines.size() + 1][lines.size()];
        gridVertical = new boolean[lines.size()][lines.size() + 1];
        
        gridHorizontal = new boolean[lines.size() + 1][lines.size()];
        for (int r = 0; r < gridHorizontal.length; r++)
            for (int c = 0; c < gridHorizontal[r].length; c++)
                gridHorizontal[r][c] = true;
        gridVertical = new boolean[lines.size()][lines.size() + 1];
        for (int r = 0; r < gridVertical.length; r++)
            for (int c = 0; c < gridVertical[r].length; c++)
                gridVertical[r][c] = true;
        
        disableLinesAroundNumber(puzzle, gridHorizontal, gridVertical, 0);
        disableDeadEndPaths(gridHorizontal, gridVertical);
        
        // lines
        horizontal = new boolean[lines.size() + 1][lines.size()];
        vertical = new boolean[lines.size()][lines.size() + 1];
    }

    /**
     * Toggles the vertical line segment below Dot r,c, if the indices are legal.
     * Otherwise do nothing.
     */
    public void verticalClick(int r, int c)
    {
        if (r >= 0 && r < size() && c >= 0 && c < size() + 1) { // && gridVertical[r][c]
            vertical[r][c] = !vertical[r][c];
        }
    }

    /**
     * Toggles the horizontal line segment to the right of Dot r,c, if the indices are legal.
     * Otherwise do nothing.
     */
    public void horizontalClick(int r, int c)
    {
        if (r >= 0 && r < size() + 1 && c >= 0 && c < size()) { // && gridHorizontal[r][c]
            horizontal[r][c] = !horizontal[r][c];
        }
    }
    
    /**
     * Disables the grid lines around a certain number.
     */
    private void disableLinesAroundNumber(int[][] puzzle, boolean[][] horizontal, boolean[][] vertical, int number)
    {
        for (int r = 0; r < size(); r++) {
            for (int c = 0; c < size(); c++) {
                if (puzzle[r][c] == number) {
                    horizontal[r][c] = false;
                    vertical[r][c] = false;
                    
                    horizontal[r + 1][c] = false;
                    vertical[r][c + 1] = false;
                }
            }
        }
    }
    
    /**
     * Disables any dead end pathways.
     */
    private void disableDeadEndPaths(boolean[][] horizontal, boolean[][] vertical)
    {
        int disabledPaths = 0;
        for (int r = 0; r <= size(); r++) {
            for (int c = 0; c <= size(); c++) {
                int pointPathways = 0;
                
                // path on the right
                if (c < size() && horizontal[r][c]) pointPathways++;
                
                // path on the left
                if (c > 0 && horizontal[r][c - 1]) pointPathways++;
                
                // path on the bottom
                if (r < size() && vertical[r][c]) pointPathways++;
                
                // path on the top
                if (r > 0 && vertical[r - 1][c]) pointPathways++;
                
                // if the intersection only has one pathway.. disable it
                if (pointPathways == 1) {
                    if (c < size()) horizontal[r][c] = false;
                    if (c > 0) horizontal[r][c - 1] = false;
                    if (r < size()) vertical[r][c] = false;
                    if (r > 0) vertical[r - 1][c] = false;
                    disabledPaths++;
                }
            }
        }
        
        // make sure the paths in the newly created intersections aren't alone either
        if (disabledPaths > 0) {
            disableDeadEndPaths(horizontal, vertical);
        }
    }

    /**
     * Clears all line segments out of the current solution.
     */
    public void clear()
    {
        horizontal = new boolean[size() + 1][size()];
        vertical = new boolean[size()][size() + 1];
    }
}
