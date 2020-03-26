
/**
* SlitherLink does the user interaction for a square Slither Link puzzle.
*
* @author Abdihafith Isse & Abdihasib Isse
* @version 1.0
*/

import java.awt.*;
import java.awt.event.*;

public class SlitherLink implements MouseListener
{
    // puzzle
    private Puzzle game;
    private boolean complete;
    
    // canvas
    private SimpleCanvas sc;
    
    // sizing
    private int windowSize;
    private int windowPadding;
    
    private int cellSize;
    private int lineSize;
    private int dotSize;
    
    // colours
    private Color backgroundColor;
    private Color gridColor;
    
    private Color numberColor;
    private Color fadedNumberColor;
    
    private Color correctColor;
    private Color semiFadedCorrectColor;
    private Color fadedCorrectColor;
    
    private Color wrongColor;
    private Color fadedWrongColor;
    
    private Color completeColor;
    private Color fadedCompleteColor;
    
    // mouse
    private long lastOutOfBoundsClick = -1;
    private int lastOutOfBoundsX = -1;
    private int lastOutOfBoundsY = -1;
    
    /**
     * Quick start.
     */
    public static void main(String[] args)
    {
        Puzzle puzzle = new Puzzle("eg5_1.txt");
        SlitherLink slitherLink = new SlitherLink(puzzle);
    }
    
    /**
     * Creates a display for playing the puzzle p.
     */
    public SlitherLink(Puzzle p)
    {
        game = p;
        
        // sizing
        initSizing(75, 600);
        
        // colours
        theme("default");
        
        // canvas
        sc = new SimpleCanvas("Slither Link (" + game.size() + "x" + game.size() + ")", windowSize, windowSize, backgroundColor);
        sc.addMouseListener(this);
        
        displayPuzzle();
    }
    
    /**
     * Returns the current state of the game.
     */
    public Puzzle getGame()
    {
        return game;
    }
    
    /**
     * Returns the current state of the canvas.
     */
    public SimpleCanvas getCanvas()
    {
        return sc;
    }
    
    /**
     * Calculates and sets pixel sizing values for the display.
     * Allows the window size to be any size below the set maximum.
     * To have a somewhat fixed window size, set a very high idealCellSize.
     */
    public void initSizing(int idealCellSize, int maxWindowSize)
    {
        cellSize = idealCellSize;
        
        windowPadding = cellSize;
        windowSize = (cellSize * game.size()) + (windowPadding * 2);
        
        lineSize = (int) Math.round((double) cellSize / 6.0);
        dotSize = (int) Math.round((double) cellSize / 6.0);
        
        // recalculate if it's greater than the maximum size
        if (windowSize > maxWindowSize) {
            initSizing(maxWindowSize / (game.size() + 2), maxWindowSize);
        }
    }
    
    /**
     * Select which set of colours is used to draw the game.
     */
    public void theme(String name)
    {
        // default theme
        backgroundColor = Color.white;
        gridColor = new Color(240, 240, 240, (int) (1.0 * 255));
        
        numberColor = Color.black;
        fadedNumberColor = new Color(0, 0, 0, (int) (0.35 * 255));
        
        correctColor = new Color(0, 128, 255, (int) (1.0 * 255));
        semiFadedCorrectColor = new Color(0, 128, 255, (int) (0.32 * 255));
        fadedCorrectColor = new Color(0, 128, 255, (int) (0.15 * 255));
        
        wrongColor = new Color(255, 37, 13, (int) (1.0 * 255));
        fadedWrongColor = new Color(255, 37, 13, (int) (0.15 * 255));
        
        completeColor = new Color(0, 220, 80, (int) (1.0 * 255));
        fadedCompleteColor = new Color(0, 247, 17, (int) (0.18 * 255));
        
        // dark theme
        if (name == "dark") {
            backgroundColor = new Color(25, 25, 25, (int) (1.0 * 255));
            gridColor = new Color(50, 50, 50, (int) (1.0 * 255));
            
            numberColor = Color.white;
            fadedNumberColor = new Color(255, 255, 255, (int) (0.35 * 255));
        }
    }
    
    /**
     * Displays the initial puzzle on sc.
     * Have a look at puzzle-loop.com for a basic display, or use your imagination.
     */
    public void displayPuzzle()
    {
        // checks if the game has been completed
        complete = AnalyzeSolution.finished(game) == "Finished" ? true : false;
        
        // plain white background
        sc.drawRectangle(0, 0, windowSize, windowSize, backgroundColor);
        
        // grid lines
        drawGridLines(game.getGridHorizontal(), game.getGridVertical(), gridColor);
        drawGridLines(game.getHorizontal(), game.getVertical(), complete ? completeColor : correctColor);
        
        // cells (numbers as dots, highlighting, line errors etc..)
        for (int r = 0; r < game.size(); r++) {
            for (int c = 0; c < game.size(); c++) {
                int requiredLines = game.getPuzzle()[r][c];
                int drawnLines = AnalyzeSolution.linesAroundSquare(game, r, c);
                int nearestDrawnLineDistance = AnalyzeSolution.nearestLineDistance(game, r, c);
                
                // blank
                if (requiredLines == -1) continue;
                
                // default
                if (drawnLines == 0) {
                    
                    // show the cell number (if they've drawn within 2 blocks of it)
                    if (nearestDrawnLineDistance <= 2 && nearestDrawnLineDistance != -1) {
                        drawCellNumberWithDots(r, c, requiredLines, numberColor, 0);
                    
                    // hide the cell number
                    } else if (requiredLines > 0) {
                        drawCellNumberWithDots(r, c, 1, fadedNumberColor, 0);
                    }
                    
                // correct
                } else if (drawnLines == requiredLines) {
                    highlightCell(r, c, complete ? fadedCompleteColor : fadedCorrectColor);
                    drawCellNumberWithDots(r, c, requiredLines, complete ? completeColor : correctColor, 0);
                    
                // incorrect
                } else if (drawnLines > requiredLines) {
                    highlightCell(r, c, fadedWrongColor);
                    drawCellNumberWithDots(r, c, requiredLines, wrongColor, 0);
                    drawCellLines(r, c, wrongColor);
                    
                // progress
                } else {
                    drawCellNumberWithDots(r, c, requiredLines, semiFadedCorrectColor, 0);
                    drawCellNumberWithDots(r, c, requiredLines, correctColor, drawnLines);
                }
            }
        }
    }
        
    /**
     * Draws grid lines in a certain color.
     */
    public void drawGridLines(boolean[][] horizontal, boolean[][] vertical, Color color)
    {
        for (int r = 0; r <= game.size(); r++) {
            for (int c = 0; c <= game.size(); c++) {
                
                // horizontal line
                if (c < game.size() && horizontal[r][c]) {
                    drawThickLine(
                        lineSize, // thickness
                        (c * cellSize) + windowPadding, // x1
                        (r * cellSize) + windowPadding, // y1
                        ((c + 1) * cellSize) + windowPadding, // x2
                        (r * cellSize) + windowPadding, // y2
                        color // color
                    );
                }
                
                // vertical line
                if (r < game.size() && vertical[r][c]) {
                    drawThickLine(
                        lineSize, // thickness
                        (c * cellSize) + windowPadding, // x1
                        (r * cellSize) + windowPadding, // y1
                        (c * cellSize) + windowPadding, // x2
                        ((r + 1) * cellSize) + windowPadding, // y2
                        color // color
                    );
                }
            }
        }
    }
    
    /**
     * Draws the enabled lines around a cell in a certain color.
     */
    public void drawCellLines(int r, int c, Color color) 
    {
        boolean[][] horizontal = new boolean[game.size() + 1][game.size()];
        boolean[][] vertical = new boolean[game.size()][game.size() + 1];
        
        if (game.getHorizontal()[r][c]) horizontal[r][c] = true;
        if (game.getVertical()[r][c]) vertical[r][c] = true;
        if (game.getHorizontal()[r + 1][c]) horizontal[r + 1][c] = true;
        if (game.getVertical()[r][c + 1]) vertical[r][c + 1] = true;
        
        drawGridLines(horizontal, vertical, color);
    }
    
    /**
     * Draws a cell's number using dots.
     */
    public void drawCellNumberWithDots(int r, int c, int requiredLines, Color color, int dotDrawLimit)
    {
        // percent positions (within a cell) of 1, 2, 3 and 4, as sets of dots
        double[][][] dotsAsNumbersPositions = new double[][][] {{{0.50,0.50}},{{0.36,0.50},{0.64,0.50}},{{0.36,0.61},{0.50,0.37},{0.64,0.61}},{{0.36,0.64},{0.36,0.36},{0.64,0.64},{0.64,0.36}}};
        
        // zero means don't limit the amount of dots drawn
        if (dotDrawLimit == 0) dotDrawLimit = 4;
        
        // draw the dots
        for (int i = 0; i < requiredLines && i < dotDrawLimit; i++) {
            sc.drawDisc(
                windowPadding + (c * cellSize) + (int) Math.round(dotsAsNumbersPositions[requiredLines - 1][i][0] * (double) cellSize), // x
                windowPadding + (r * cellSize) + (int) Math.round(dotsAsNumbersPositions[requiredLines - 1][i][1] * (double) cellSize), // y
                dotSize / 2, // radius
                color // color
            );
        }
    }
    
    /**
     * Highlights a cell in a certain color.
     */
    public void highlightCell(int r, int c, Color color)
    {
        sc.drawRectangle(
            (c * cellSize) + windowPadding - (lineSize / 2), // x1
            (r * cellSize) + windowPadding - (lineSize / 2), // y1
            ((c + 1) * cellSize) + windowPadding + (lineSize / 2), // x2
            ((r + 1) * cellSize) + windowPadding + (lineSize / 2), // y2
            color // color
        );
    }
    
    /**
     * Draws a line with a certain thickness
     */
    public void drawThickLine(int thickness, int x1, int y1, int x2, int y2, Color color)
    {
        sc.drawRectangle(
            x1 - (thickness / 2), // x1
            y1 - (thickness / 2), // y1
            x2 + (thickness / 2), // x2
            y2 + (thickness / 2), // y2
            color // color
        );
    }

    /**
     * Makes a horizontal click to the right of Dot r,c.
     * Update game and the display, if the indices are legal; otherwise do nothing.
     */
    public void horizontalClick(int r, int c)
    {
        game.horizontalClick(r, c);
        displayPuzzle();
    }
    
    /**
     * Makes a vertical click below Dot r,c.
     * Update game and the display, if the indices are legal; otherwise do nothing.
     */
    public void verticalClick(int r, int c)
    {
        game.verticalClick(r, c);
        displayPuzzle();
    }
    
    /**
     * Nearest line (x or y) to a given position.
     */
    public int nearestGridLineDistance(int position)
    {
        return Math.abs((Math.abs(((position - windowPadding) % cellSize) - (cellSize / 2)) - (cellSize / 2)));
    }
    
    /**
     * Nearest line to a given position.
     */
    public int nearestGridLine(int position, boolean floor)
    {
        float number = (position - windowPadding) / (float) cellSize;
        
        if (floor) return (int) Math.floor(number);
                   return (int) Math.round(number);
    }
    
    /**
     * Checks if there could be a line at certain indexes.
     */
    public boolean validGridLine(boolean verticalLine, int r, int c) {
        return r >= 0 && c >= 0 && (verticalLine && r < game.size() && c <= game.size() || !verticalLine && r <= game.size() && c < game.size());
    }
    
    /**
     * Clears all line segments out of the current solution.
     */
    public void clear()
    {
        game.clear();
        displayPuzzle();
    }
    
    /**
     * Actions for a mouse press.
     */
    public void mousePressed(MouseEvent e)
    {
        boolean verticalLine = nearestGridLineDistance(e.getX()) < nearestGridLineDistance(e.getY());
        int r = nearestGridLine(e.getY(), verticalLine);
        int c = nearestGridLine(e.getX(), !verticalLine);
        
        // out of bounds (and after completion) clicks
        if (!validGridLine(verticalLine, r, c) || complete) {
            
            // double click to reset the game
            if (lastOutOfBoundsY == e.getY() && lastOutOfBoundsX == e.getX() && System.currentTimeMillis() - lastOutOfBoundsClick < 500) {
                lastOutOfBoundsClick = 0;
                clear();
            } else {
                lastOutOfBoundsClick = System.currentTimeMillis();
                lastOutOfBoundsY = e.getY();
                lastOutOfBoundsX = e.getX();
            }
            
            return;
        }
        
        // enable nearest line
        if (verticalLine && game.getGridVertical()[r][c]) verticalClick(r, c);
        if (!verticalLine && game.getGridHorizontal()[r][c]) horizontalClick(r, c);
    }
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
