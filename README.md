## PROJECT OVERVIEW
Slither Link is a modern puzzle game developed by Nikoli. The player is required to find a path around a grid of dots while satisfying various constraints imposed by the puzzle.

A solution to a Slither Link puzzle satisfies two requirements:
<ol>
    <li>
        All numbers in the puzzle have the right number of line segments surrounding them.
    </li>
    <li>
        All line segments combine to form a single closed loop.
    </li>
</ol>


In this project my project partner and I wrote an implementation of Slither Link to allow someone to play the game.


### THE GAME OF SLITHERLINK
During a game of Slither Link, the player has only one action available: to click the mouse between two dots in the grid, causing a line segment between those two dots to toggle on and off. Try it out at: puzzle-loop.com

The player's goal is to generate a set of line segments that satisfy two requirements: collectively they must form a single closed loop, with no extraneous segments; and every dotted square in the original puzzle must be surrounded by the correct number of segments. Squares that have no dots can have any number of line segments surrounding them.


### TESTING FOR A SOLUTION
Testing to see whether the current board situation constitutes a solution to a puzzle is quite tricky. We need to check for all four failure modes and to return an error message if any of them applies. This is basically what the class AnalyzeSolution does, with several helper methods as described in the code skeleton.

Use the following algorithm to test a solution. Remember that the puzzle is finished if all squares are individually correct, and all line segments form a single closed loop.

Check the number of line segments around each square. If any of them are wrong, return Wrong number.
Find any line segment on the board, and record the dot where it is located. Trace that line around the board, counting the number of steps as you go. If at some point the line ends, return Dangling end. If at some point the line branches, return Branching line. Otherwise eventually the line must go back to the starting dot, and thus it forms a closed loop. Let the number of line segments in the loop be n.
Count the total number of line segments on the board; let this number be t. If t > n, the loop does not contain all of the line segments on the board; return Disconnected lines.
Otherwise t = n, i.e. all of the line segments are in the loop, and the puzzle is finished! return Finished.
The methods in AnalyzeSolution collectively implement this algorithm.
