This is a general version of Connect 4/Tic-Tac-Toe where the number of rows, columns, and length of a run are configurable. 

The game server code is contained in the file MLGame.java. A sample player implementation is provided in MLPlayer.java. The game server can be started with the commands:

java MLGame [num rows] [num cols] [num connect]

and the sample player implementation can be started with the commands (which tells the implementation whether it is player 1 or 2):

java MLPlayer [1 or 2]

The rules of the game  (number of rows, columns, length of a winning run) are stored in the class Rules.java. When a player 1 and a player 2 connect to the game server, the server sends a rules object to each player. 
A player can lose automatically by specifying an invalid move. A move is invalide if it specifies a column that is too large (>= Rules.numCols) or too small (<0) or if all of the rows in that column are already full.

During gameplay, the players communicate with the server by sending GameMessage objects back and forth. GameMessage is a class that has two fields, move and win. When a player receives a GameMessage, the field move contains the number of the column (starting at 0, counting from the left) corresonding to the other player's move. A value of -1 indicates that the other player has not moved. The field win contains the value Player.EMPTY if no one has won on that move. It contains Player.ONE if player 1 has won and Player.TWO if player 2 has won. 

To specify a move, a player sets the move field in a GameMessage object and sends it to the server, which will then forward the move to the other player. The game server will also display the current board position in the terminal from which it was launched.

The Player.java file contains code that is used to keep track of players. It defines constants Player.ONE, Player.TWO and Player.EMPTY. It also contains the socket numbers that player 1 and player 2 should use to connect to the game server.

