# Gomognu
Java Swing  <a href="https://en.wikipedia.org/wiki/Gomoku"> Connect five/Gomoku</a> game with a very intuitive GUI and a decent engine. 

![](Gomokupic.png)

<h1><b>Features </b></h1>

<ul>
  <li>Only the 19x19 board game is currently available</li>
  <li>Two player mode</li>
  <li>Play against AI(only one level)</li>
  <li>Saving and loading games</li>
  <li>Switch side while game is in session</li>
  <li>Undo/Redo</li>
</ul>

<h1>AI Implementation</h1>
The AI is implemented using the Minimax Algorithm with Alpha Beta pruning. An area enclosing the pieces is first calculated. The available intersections within that area is used to quickly get a set of best moves for the AI at depth 1. Then we calculate the best move for the engine at depth 3 using the intersections from the previous calculations. As of now no time is set for said calculations. <br>
The heuristics function is somewhat experimental; I am attempting to capture the way I see the board.  



