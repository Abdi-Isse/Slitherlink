# Running The Game
Simply clone this repository and run the SlitherLink.java file.

# Changing Boards
By default this java file will use the eg_5_2.txt file to initialise the game board.
To choose a different board simply choose one of the other eg txt files as a parameter in line 57 of SlitherLink.java

# Changing Themes
There are two themes to choose from for your board, default (light theme) or dark.
To change the theme simply supply either default or dark as a string argument in line 72 of SlitherLink.java

# Game States

The intial puzzle will look like this (Using the 5x5 puzzle as an example here):
<img src="./images/initial-board.png" />

If you make an illegal move the board position at that illegal move will look like this:
<img src="./images/invalid-move.png" />

If you solve the slitherlink puzzle the final game state will look like this:
<img src="./images/win.png" />
