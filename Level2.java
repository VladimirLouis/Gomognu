package gomoku;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class Level2 extends GomokuEngine {

	private int DEPTH = 3;

	Point topLeft;
	Point bottomLeft;
	Point topRight;
	Point bottomRight;

	ArrayList<Point> availableIntersections;
	ArrayList<Point> searchArea = new ArrayList<>();
	HashMap<Point, Integer> specialPoints = new HashMap<Point, Integer>();
	ArrayList<Point> searchAreaDepth3 = new ArrayList<>();

	/***********************************************************************************************************************/
	public Level2(GomokuBoard board) {
		super(board);

		init();
	}

	/***********************************************************************************************************************/
	private void init() {
		availableIntersections = new ArrayList<>();
		board.disableListeners();

		for (Point point : board.getListOfIntersections()) {
			if (!board.isOccupied(point))
				availableIntersections.add(point);

		}

	}

	/**********************************************************************************************************************/
	public Point move() {

		// IF WE ONLY HAVE 1 INTERSECTIONS TO MOVE IN
		if (availableIntersections.size() == 1) {
			return availableIntersections.get(0);
		}

		// computer moves first
		if (board.getListOfMorpions().size() == 0) {
			return board.getCenter();
		}

		// randomly move around first move made
		Point move = randomMoveAroundFirstMove();
		if (move != null) {
			return move;
		}

		// SETTING BORDER AROUND SET OF MORPIONS
		setSearchAreaBorders2(2);
		searchArea = initialSearchArea();

		// IF WE ONLY HAVE TWO INTERSECTIONS TO MOVE IN
		if (availableIntersections.size() == 2) {

			Point point2Return = minimax(2, Integer.MIN_VALUE, Integer.MAX_VALUE, Boolean.TRUE).entrySet().iterator()
					.next().getKey();

			return point2Return;
		}

		// map of moves
		HashMap<Point, Integer> move_value = new HashMap<Point, Integer>();
		Point forcedMove = null;

		// GETTING THE BEST POINTS TO CHECK
		ArrayList<Integer> values = getBestPointsForMAX();

		// take winning move
		for (HashMap.Entry<Point, Integer> entry : specialPoints.entrySet()) {

			if (entry.getValue() == Integer.MAX_VALUE) {
				return entry.getKey();
			}

		}

		// adding best points to searchAreaDepth3
		addPoints2SearchAreaDepth3();

		// GETTING BEST POINTS TO CHECK FROM MIN PLAYER'S POINT OF VIEW
		// changing turn
		flipMAXMIN();
		values.clear();
		specialPoints.clear();
		values = getBestPointsForMIN();

		// block winning move
		for (HashMap.Entry<Point, Integer> entry : specialPoints.entrySet()) {

			if (entry.getValue() == Integer.MAX_VALUE) {
				return entry.getKey();
			}

		}

		// resetting morpionturn
		flipMAXMIN();

		// add points to searchAreaDepth3
		addPoints2SearchAreaDepth3();

		// calculate move at depth 3
		move_value = minimax(DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, Boolean.TRUE);

		return move_value.entrySet().iterator().next().getKey();

	}

	/**********************************************************************************************************************/
	private void flipMAXMIN() {
		board.changeMorpionTurn();
		MAX_PLAYER = board.getMorpionTurn();
		MIN_PLAYER = oppositeOfMorpionType(MAX_PLAYER);
	}

	/********************************************************************************************************************/
	public void addPoints2SearchAreaDepth3() {
		for (HashMap.Entry<Point, Integer> entry : specialPoints.entrySet()) {
			searchAreaDepth3.add(entry.getKey());

		}
	}

	/****************************************************************************************************************/
	private ArrayList<Integer> getBestPointsForMAX() {
		ArrayList<Integer> values = new ArrayList<>();

		for (Point point : searchArea) {

			board.addMorpion(new Morpion(MAX_PLAYER, point.getLocation()));
			int value = heuristic2(1);

			if (values.size() < 15) {
				values.add(value);
				specialPoints.put(point, value);

			} else {
				Collections.sort(values);
				if (value > values.get(0)) {

					Iterator<Point> specialPointsIterator = specialPoints.keySet().iterator();
					while (specialPointsIterator.hasNext()) {
						Point point2Check = specialPointsIterator.next();

						int valueOfSpecialPoints = specialPoints.get(point2Check);
						if (valueOfSpecialPoints == values.get(0)) {

							specialPointsIterator.remove();
							specialPoints.put(point, value);
							values.remove(0);
							values.add(value);
							break;
						}
					} // end while

				}
			} // end if-else

			board.undoMove();
		} // end for

		return values;
	}

	/*****************************************************************************************************************/
	private ArrayList<Integer> getBestPointsForMIN() {
		ArrayList<Integer> values = new ArrayList<>();

		for (Point point : searchArea) {

			board.addMorpion(new Morpion(MAX_PLAYER, point.getLocation()));
			int value = heuristic2(1);

			if (values.size() < 5 && !searchAreaDepth3.contains(point)) {
				values.add(value);
				specialPoints.put(point, value);

			} else if (!searchAreaDepth3.contains(point)) {
				Collections.sort(values);
				if (value > values.get(0)) {

					Iterator<Point> specialPointsIterator = specialPoints.keySet().iterator();
					while (specialPointsIterator.hasNext()) {
						Point point2Check = specialPointsIterator.next();

						int valueOfSpecialPoints = specialPoints.get(point2Check);
						if (valueOfSpecialPoints == values.get(0)) {

							specialPointsIterator.remove();
							specialPoints.put(point, value);
							values.remove(0);
							values.add(value);
							break;
						}
					} // end while

				}
			} // end if-else

			board.undoMove();
		} // end for

		return values;

	}

	/*****************************************************************************************************************/
	private Point randomMoveAroundFirstMove() {
		if (board.getListOfMorpions().size() == 1) {
			Morpion lastMorpionPlayed = board.getLastMoveMade();
			ArrayList<Morpion> morpionsInBorder = board.getAllBorderOfIntersection(lastMorpionPlayed.getLocation(), 1);

			if (morpionsInBorder.isEmpty()) {
				ArrayList<Point> border = board.getXBorderOf(lastMorpionPlayed.getLocation(), 1);

				if (border.size() == 8) {
					Random rand = new Random();
					int randNum = rand.nextInt(((border.size() - 1) - 0) + 1) + 0;

					return border.get(randNum);
				}
			}
		}

		return null;
	}

	/********************************************************************************************************************/
	private void setSearchAreaBorders2(int buffer_radius) {

		ArrayList<Integer> x_coords = new ArrayList<Integer>();
		ArrayList<Integer> y_coords = new ArrayList<Integer>();

		Morpion morpion;
		for (HashMap.Entry<Point, Morpion> entry : board.getListOfMorpions().entrySet()) {

			morpion = entry.getValue();
			int x = morpion.getLocation().x;
			int y = morpion.getLocation().y;

			if (!x_coords.contains(x)) {
				x_coords.add(x);
			}
			if (!y_coords.contains(y)) {
				y_coords.add(y);
			}
		}

		Collections.sort(x_coords);
		int leftMost = x_coords.get(0);
		Collections.reverse(x_coords);
		int rightMost = x_coords.get(0);

		Collections.sort(y_coords);
		int topMost = y_coords.get(0);
		Collections.reverse(y_coords);
		int bottomMost = y_coords.get(0);

		topLeft = new Point(leftMost, topMost);
		bottomLeft = new Point(leftMost, bottomMost);
		topRight = new Point(rightMost, topMost);
		bottomRight = new Point(rightMost, bottomMost);

		Point top = new Point();
		Point left = new Point();
		Point down = new Point();
		Point right = new Point();

		// top
		int i = 0;
		do {
			top = board.getIntersectionXUpFrom(topLeft, buffer_radius - i);
			++i;
		} while (top == null);

		// left
		i = 0;
		do {
			left = board.getIntersectionXLeftFrom(topLeft, buffer_radius - i);
			++i;
		} while (left == null);

		// down
		i = 0;
		do {
			down = board.getIntersectionXDownFrom(bottomLeft, buffer_radius - i);
			++i;
		} while (down == null);

		// right
		i = 0;
		do {
			right = board.getIntersectionXRightFrom(topRight, buffer_radius - i);
			++i;
		} while (right == null);

		topLeft = new Point(left.x, top.y);
		bottomLeft = new Point(left.x, down.y);
		topRight = new Point(right.x, top.y);
		bottomRight = new Point(left.x, down.y);

	}

	/************************************************************************************************************/
	private ArrayList<Point> initialSearchArea() {
		ArrayList<Point> searchArea2Return = new ArrayList<>();

		for (Point point : availableIntersections) {
			if ((point.x >= topLeft.x && point.x <= topRight.x) && (point.y >= topLeft.y && point.y <= bottomRight.y)) {

				if (board.getMorpionsXRadiusAround(point, 1).isEmpty()
						&& board.getMorpionsXRadiusAround(point, 2).isEmpty()) {
					continue;
				} else {

					searchArea2Return.add(new Point(point.x, point.y));
				}
			}

		}

		return searchArea2Return;
	}

	/*********************************************************************************************************************/
	private HashMap<Point, Integer> minimax(int depth, int alpha, int beta, boolean MAX_PLAYER) {

		HashMap<Point, Integer> move_value2Return = new HashMap<Point, Integer>();

		if (depth == 0) {

			move_value2Return.put(board.getLastMoveMade().getLocation(), heuristic2(DEPTH));

			return move_value2Return;
		} else if (board.getGameOverStatus() && MAX_PLAYER) {
			move_value2Return.put(board.getLastMoveMade().getLocation(), Integer.MIN_VALUE);
			return move_value2Return;
		} else if (board.getGameOverStatus() && !MAX_PLAYER) {
			move_value2Return.put(board.getLastMoveMade().getLocation(), Integer.MAX_VALUE);
			return move_value2Return;
		}

		if (MAX_PLAYER) {
			int value = Integer.MIN_VALUE;

			Iterator<Point> availablePointsIter;
			if (DEPTH == 1) {
				availablePointsIter = availableIntersections.iterator();

			} else if (DEPTH == depth) {
				ArrayList<Point> Az = getSearchAreaDepth3();

				availablePointsIter = Az.iterator();
			} else {
				ArrayList<Point> Az = getSearchArea();

				availablePointsIter = Az.iterator();
			}

			while (availablePointsIter.hasNext()) {
				Point possibleMove = availablePointsIter.next();

				board.addMorpion(new Morpion(board.getMorpionTurn(), new Point(possibleMove.x, possibleMove.y)));
				searchArea.remove(possibleMove);

				HashMap<Point, Integer> move_value = new HashMap<>();
				move_value = (minimax(depth - 1, alpha, beta, Boolean.FALSE));

				int returned_value = move_value.entrySet().iterator().next().getValue();

				if (returned_value >= value) {
					move_value2Return.clear();
					value = returned_value;
					move_value2Return.put(new Point(possibleMove.x, possibleMove.y), value);
				}
				// setting alpha
				if (alpha <= value) {
					alpha = value;
				}

				if (returned_value == Integer.MAX_VALUE || alpha >= beta) {
					board.undoMove();
					searchArea.add(possibleMove);
					break;
				}

				board.undoMove();
				searchArea.add(possibleMove);

			}

			return move_value2Return;
		}

		else if (!MAX_PLAYER) {
			int value = Integer.MAX_VALUE;

			Iterator<Point> availablePointsIter;
			if (DEPTH == 1) {
				availablePointsIter = availableIntersections.iterator();

			} else {
				ArrayList<Point> Az = getSearchArea();

				availablePointsIter = Az.iterator();
			}

			while (availablePointsIter.hasNext()) {
				Point possibleMove = availablePointsIter.next();

				board.addMorpion(new Morpion(board.getMorpionTurn(), new Point(possibleMove.x, possibleMove.y)));
				searchArea.remove(possibleMove);

				HashMap<Point, Integer> move_value = new HashMap<>();
				move_value = (minimax(depth - 1, alpha, beta, Boolean.TRUE));

				int returned_value = move_value.entrySet().iterator().next().getValue();

				if (returned_value <= value) {
					move_value2Return.clear();
					value = returned_value;
					move_value2Return.put(new Point(possibleMove.x, possibleMove.y), value);
				}

				// setting beta
				if (beta <= value) {
					beta = value;
				}

				if (returned_value == Integer.MIN_VALUE || alpha >= beta) {
					board.undoMove();
					searchArea.add(possibleMove);
					break;
				}

				board.undoMove();
				searchArea.add(possibleMove);
			}

			return move_value2Return;
		}

		return move_value2Return;

	}

	/********************************************************************************************************************************/
	private ArrayList<Point> getSearchArea() {
		ArrayList<Point> searchArea2Return = new ArrayList<>();

		for (Point point : searchArea) {
			searchArea2Return.add(new Point(point.x, point.y));
		}

		return searchArea2Return;
	}

	/*********************************************************************************************************************************/
	private ArrayList<Point> getSearchAreaDepth3() {
		ArrayList<Point> searchArea2Return = new ArrayList<>();

		for (Point point : searchAreaDepth3) {

			if (searchArea.contains(point)) {
				searchArea2Return.add(new Point(point.x, point.y));
			}

		}

		return searchArea2Return;
	}
	/*********************************************************************************************************************************/
}
