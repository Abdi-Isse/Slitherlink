
/**
 * AnalyzeSolution methods are used to analyze the state of a Slither Link puzzle, 
 * to determine if the puzzle is finished. 
 * 
 * @author Abdihafith Isse & Abdihasib Isse
 * @version v1.0
 */
 
import java.util.*;

public class AnalyzeSolution
{
    /**
     * We don't need to create any objects of class AnalyzeSolution; all of its methods are static.
     */
    private AnalyzeSolution() {}

    /**
     * Returns the number of line segments surrounding Square r,c in p.
     * Returns 0 if the indices are illegal.
     */
    public static int linesAroundSquare(Puzzle p, int r, int c)
    {
        boolean[][] horizontal = p.getHorizontal();
        boolean[][] vertical = p.getVertical();
        
        // validate square and count lines
        int lines = 0;
        if (r >= 0 && r < p.size() && c >= 0 && c < p.size()) {
            if (horizontal[r][c]) lines++; // top
            if (vertical[r][c]) lines++; // left
            if (horizontal[r + 1][c]) lines++; // bottom
            if (vertical[r][c + 1]) lines++; // right
        }
        
        return lines;
    }
    
    /**
     * Returns the distance a given cell is away from a drawn line.
     * Returns -1 if there are no drawn lines.
     */
    public static int nearestLineDistance(Puzzle p, int inputRow, int inputColumn)
    {
        boolean[][] horizontal = p.getHorizontal();
        boolean[][] vertical = p.getVertical();
        int size = vertical.length;
        
        double cellRowMiddle = inputRow + 0.5;
        double cellColumnMiddle = inputColumn + 0.5;
        
        double distance = -1.0;
        for (int r = 0; r <= size; r++) {
            for (int c = 0; c <= size; c++) {
                
                // horizontal line
                double horizontalDistance = Math.abs((c + 0.5) - cellColumnMiddle) + Math.abs(r - cellRowMiddle);
                if (c < size && horizontal[r][c] && (distance == -1.0 || horizontalDistance < distance)) {
                    distance = horizontalDistance;
                }
                
                // vertical line
                double verticalDistance = Math.abs(c - cellColumnMiddle) + Math.abs((r + 0.5) - cellRowMiddle);
                if (r < size && vertical[r][c] && (distance == -1.0 || verticalDistance < distance)) {
                    distance = verticalDistance;
                }
            }
        }
        
        return (int) distance;
    }
    
    /**
     * Returns all squares in p that are surrounded by the wrong number of line segments.
     * Each item on the result will be an int[2] containing the indices of a square.
     * The order of the items on the result is unimportant.
     */
    public static ArrayList<int[]> badSquares(Puzzle p)
    {
        ArrayList<int[]> badSquares = new ArrayList<int[]>();
        for (int r = 0; r < p.size(); r++) {
            for (int c = 0; c < p.size(); c++) {
                int requiredLines = p.getPuzzle()[r][c];
                int drawnLines = linesAroundSquare(p, r, c);
                
                // incorrect
                if (drawnLines != requiredLines && requiredLines != -1) {
                    badSquares.add(new int[]{r,c});
                }
            }
        }
        
        return badSquares;
    }

    /**
     * Returns all dots connected by a single line segment to Dot r,c in p.
     * Each item on the result will be an int[2] containing the indices of a dot.
     * The order of the items on the result is unimportant.
     * Returns null if the indices are illegal.
     */
    public static ArrayList<int[]> getConnections(Puzzle p, int r, int c)
    {
        boolean[][] horizontal = p.getHorizontal();
        boolean[][] vertical = p.getVertical();
        ArrayList<int[]> connections = new ArrayList<int[]>();
        
        // invalid indices
        if (r < 0 || r > p.size() + 1 || c < 0 || c > p.size() + 1) return null;
        
        // path on the right
        if (c < p.size() && horizontal[r][c]) connections.add(new int[] {r, c + 1});
        
        // path on bottom
        if (r < p.size() && vertical[r][c]) connections.add(new int[] {r + 1, c});
        
        // path on the left
        if (c > 0 && horizontal[r][c - 1]) connections.add(new int[] {r, c - 1});
        
        // path on the top
        if (r > 0 && vertical[r - 1][c]) connections.add(new int[] {r - 1, c});

        return connections;
    }

    /**
     * Returns an array of length 3 whose first element is the number of line segments in the puzzle p, 
     * and whose other elements are the indices of a dot on any one of those segments. 
     * Returns {0,0,0} if there are no line segments on the board. 
     */
    public static int[] lineSegments(Puzzle p)
    {
        boolean[][] horizontal = p.getHorizontal();
        boolean[][] vertical = p.getVertical();
        
        int[] lineSegments = new int[] {0, 0, 0};
        for (int r = 0; r < p.size(); r++) {
            for (int c = 0; c < p.size(); c++) {
                
                // horizontal line
                if (horizontal[r][c]) lineSegments[0]++;
                
                // vertical line
                if (vertical[r][c]) lineSegments[0]++;
                
                // vertical line in last column
                if (c == p.size()-1 && vertical[r][c + 1]) lineSegments[0]++;
                
                // horizontal line in last row
                if (r == p.size()-1 && horizontal[r + 1][c]) lineSegments[0]++;
                
                // any point connected to a drawn line
                if (lineSegments[1] == 0 && lineSegments[2] == 0) {
                    if (horizontal[r][c] || vertical[r][c]) lineSegments = new int[] {lineSegments[0], r, c};
                    if (r == p.size()-1 && vertical[r][c + 1]) lineSegments = new int[] {lineSegments[0], r, c + 1};
                    if (c == p.size()-1 && horizontal[r + 1][c]) lineSegments = new int[] {lineSegments[0], r + 1, c};
                }
            }
        }
        
        return lineSegments;
    }
    
    /**
     * Tries to trace a closed loop starting from Dot r,c in p. 
     * Returns either an appropriate error message, or 
     * the number of steps in the closed loop (as a String). 
     * See the project page and the JUnit for a description of the messages expected. 
     */
    public static String tracePath(Puzzle p, int initialRow, int initialColumn)
    {
        int[] point = new int[] {-1, -1};
        int[] nextPoint = new int[] {initialRow, initialColumn};
        
        int pathLinesCount = 0;
        while (pathLinesCount == 0 || !(nextPoint[0] == initialRow && nextPoint[1] == initialColumn)) {
            int[] lastPoint = point;
            point = nextPoint;
            
            ArrayList<int[]> connections = getConnections(p, point[0], point[1]);
            if (connections.size() == 0) return "No path";
            if (connections.size() == 1) return "Dangling end";
            if (connections.size() > 2) return "Branching line";
            pathLinesCount++;
            
            nextPoint = (connections.get(0)[0] == lastPoint[0] && connections.get(0)[1] == lastPoint[1]) ? connections.get(1) : connections.get(0);
        }

        return "" + pathLinesCount + "";
    }
    
    /**
     * Returns a message on whether the puzzle p is finished. 
     * p is finished if all squares are good, and all line segments form a single closed loop. 
     * An algorithm is given on the project page. 
     * See the project page and the JUnit for a description of the messages expected.
     */
    public static String finished(Puzzle p)
    {
        int[] lineSegments = lineSegments(p);
        
        int drawnLinesCount = lineSegments[0];
        int pathLinesCount = 0;
        
        // any cells with the wrong number of lines around them
        if (badSquares(p).size() > 0) return "Wrong number";
        
        // checks that the lines make a loop
        String tracePathResult = tracePath(p, lineSegments[1], lineSegments[2]);
        try {
            pathLinesCount = Integer.parseInt(tracePathResult);
        } catch (NumberFormatException e) {
            return tracePathResult;
        }
        
        // isolated lines (outside of the main loop)
        if (pathLinesCount != drawnLinesCount) return "Disconnected lines";
        
        return "Finished";
    }
}
