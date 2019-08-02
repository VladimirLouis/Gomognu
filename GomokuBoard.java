package gomoku;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import javax.swing.JComponent;
import gomoku.MovesMadeListener;

import gomoku.Morpion;

public class GomokuBoard extends JComponent implements Externalizable {

	private static int SQUAREDIM = 30;
	private final Dimension dimPrefSize;
	private static int BOARDDIM = 20 * SQUAREDIM;
	private int morpionRadius = (int) (SQUAREDIM * .30F);

	private MorpionType morpionTurn;

	private ArrayList<Point> listOfIntersections;
	private ArrayList<Point> occupiedIntersections;
	private Point center;
	private HashMap<Point, Morpion> listOfMorpions;
	private Stack<Morpion> redoList;

	// private Point lastMoveMadeLocation;

	private MovesMadeListener movesMadeListener;
	private GameOverListener gameOverListener;

	private boolean silence;
	private boolean gameOver;

	// PlayAs
	private boolean playAsSquare;
	private boolean playAsDisks;
	private boolean two_player;
	private boolean playAgainstComp;

	/******************************************************************************/
	public GomokuBoard() {
		dimPrefSize = new Dimension(BOARDDIM, BOARDDIM);
		init();
	}

	/**************************************************************************/
	private void init() {

		listOfIntersections = new ArrayList<>();
		occupiedIntersections = new ArrayList<>();
		center = new Point(10 * SQUAREDIM, 10 * SQUAREDIM);
		listOfMorpions = new HashMap<>();

		redoList = new Stack<>();

		morpionTurn = MorpionType.SQUARE;

		addIntersections();
	}

	/**********************************************************************************************/
	@Override
	public Dimension getPreferredSize() {
		return dimPrefSize;
	}

	/******************************************************************************************/
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

//PAINT THE BOARD LINES AND COLOR... 
		paintVMorpionBoard(g2);

//DRAW CENTERS
		drawCenter(g2);

//DRAWS THE MORPIONS FOR ALL CASTS PLAYED. DRAWS THE CURRENT STATE OF THE BOARD.
		drawMorpions(g2);

//CONNECTING MORPIONS FROM FINISHED CASTS
		// drawCastOverLines(g2);

//SETSHOW MORPIONS AND SIEGED
		// setShowMorpionsAndSieges(g2);

//DRAWING LAST MOVE
		if (listOfMorpions.size() > 0) {
			drawLastMove(g2);
		}

	}

	/**************************************************************************************/
	private void paintVMorpionBoard(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(new Color(255, 222, 173));
		g.fillRect(0, 0, BOARDDIM, BOARDDIM);

		g.setFont(new Font("TimesRoman", Font.PLAIN, 10));
		g.setColor(Color.BLACK);
		for (int row = 1; row < 20; row++) {
			g.drawString(Integer.toString(row), row * SQUAREDIM - 3, SQUAREDIM * 20 - 12);
			g.drawString(Integer.toString(row), SQUAREDIM - 12, row * SQUAREDIM + 3);
			g.drawLine(row * SQUAREDIM, SQUAREDIM, row * SQUAREDIM, BOARDDIM - SQUAREDIM);
			g.drawLine(SQUAREDIM, row * SQUAREDIM, BOARDDIM - SQUAREDIM, row * SQUAREDIM);
		}
	}

	/******************************************************************************************/
	private void drawCenter(Graphics2D g2) {
		// DRAW CENTER
		g2.setColor(new Color(0, 153, 0));
		g2.fill(new Ellipse2D.Double(300 - 7, 300 - 7, 15, 15));

	}

	/******************************************************************************************/
	private void drawMorpions(Graphics2D g) {

		for (HashMap.Entry<Point, Morpion> entry : listOfMorpions.entrySet()) {
			Morpion morpion = entry.getValue();

			switch (morpion.getMorpionType()) {

			case DISK:
				g.setColor(Color.WHITE);
				g.fillOval(morpion.getLocation().x - morpionRadius, morpion.getLocation().y - morpionRadius,
						2 * morpionRadius, 2 * morpionRadius);
				break;
			case SQUARE:
				g.setColor(Color.BLACK);
				g.fillOval(morpion.getLocation().x - morpionRadius, morpion.getLocation().y - morpionRadius,
						2 * morpionRadius, 2 * morpionRadius);
				break;

			}
		}
	}

	/***********************************************************************************************/
	public void drawLastMove(Graphics2D g) {
		g.setColor(Color.BLUE);

		Point point = occupiedIntersections.get(occupiedIntersections.size() - 1);

		g.drawOval(point.x - (morpionRadius + (int) (.5f * morpionRadius)),
				point.y - (morpionRadius + (int) (.5f * morpionRadius)),
				2 * (morpionRadius + (int) (.5f * morpionRadius)), 2 * (morpionRadius + (int) (.5f * morpionRadius)));

	}

	/******************************************************************************/
	private void addIntersections() {
		for (int row = 1; row < 20; row++) {
			for (int col = 1; col < 20; col++) {
				listOfIntersections.add(new Point(row * SQUAREDIM, col * SQUAREDIM));

			}
		}

	}

	/*************************************************************************************/
	public void coordsClicked(Point coords) {

		for (Point point : listOfIntersections) {
			if (near(coords, point) && !occupiedIntersections.contains(point)) {

				if (morpionTurn == MorpionType.DISK) {
					addMorpion(new Morpion(MorpionType.DISK, new Point(point.x, point.y)));

				} else {
					addMorpion(new Morpion(MorpionType.SQUARE, new Point(point.x, point.y)));

				}

			}
		}
	}

	/*****************************************************************************/
	private Boolean near(Point clickedCoord, Point intersection) {
		return (intersection.x - clickedCoord.x) * (intersection.x - clickedCoord.x) + (intersection.y - clickedCoord.y)
				* (intersection.y - clickedCoord.y) < (SQUAREDIM / 2) * (SQUAREDIM / 2);

	}

	/*********************************************************************************************************************/
	public void addMorpion(Morpion morpion2Add) {

		if (isEnabled()) {

			// UPDATING ALL MORPIONS LIST
			if (!occupiedIntersections.contains(morpion2Add.getLocation())
					&& morpionTurn == morpion2Add.getMorpionType()) {
				updateListsAfterMove(morpion2Add);

				if (isGameOver(morpion2Add)) {

					gameOver = true;

					if (!silence) {
						gameOverListener.gameOver();
					}
				}

				if (!silence) {

					movesMadeListener.movesMade();

				}

			}

		}

	}// end addMorpion

	/**********************************************************************************************/
	private void updateListsAfterMove(Morpion morpion2Add) {
		listOfMorpions.put(morpion2Add.getLocation(),
				new Morpion(morpion2Add.getMorpionType(), morpion2Add.getLocation()));

		if (!redoList.isEmpty() && redoList.peek().equals(morpion2Add)) {
			redoList.pop();
		} else {
			redoList.clear();
		}

		occupiedIntersections.add(morpion2Add.getLocation());
		changeMorpionTurn();
	}

	/***********************************************************************************************************/
	public void changeMorpionTurn() {
		if (morpionTurn == MorpionType.SQUARE) {
			morpionTurn = MorpionType.DISK;
		} else {
			morpionTurn = MorpionType.SQUARE;
		}
	}

	/***********************************************************************************************************/
	public MorpionType getMorpionTurn() {

		return morpionTurn;
	}

	/**********************************************************************************************************/
	public void setMovesMadeListener(MovesMadeListener movesMadeListener) {

		this.movesMadeListener = movesMadeListener;
	}

	/************************************************************************************************************/
	public void setGameOverListener(GameOverListener gameOverListener) {

		this.gameOverListener = gameOverListener;
	}

	/************************************************************************************************************/
	public void disableListeners() {
		silence = true;
	}

	/***************************************************************************************************/
	public void enableListeners() {
		silence = false;
	}

	/******************************************************************************************************/
	public boolean isGameOver(Morpion morpion) {

		// VERTICAL
		if (getMorpionsUp(morpion) + getMorpionsDown(morpion) >= 4) {

			return true;
		}

		// HORIZONTAL
		if (getMorpionsLeft(morpion) + getMorpionsRight(morpion) >= 4) {
			return true;
		}

		// SLANTED_DOWN
		if (getMorpionsUp_Left(morpion) + getMorpionsDown_Right(morpion) >= 4) {

			return true;
		}

		// SLANTED-UP
		if (getMorpionsUp_Right(morpion) + getMorpionsLeft_Down(morpion) >= 4) {

			return true;
		}

		return false;
	}

	/***********************************************************************************************************/
// THIS RETURNS MORPIONS IN THE CURRENT GAME
	public Morpion getMorpionAt(Point intersection) {

		if (listOfMorpions.containsKey(intersection)) {
			return listOfMorpions.get(intersection);
		}

		return null;
	}

	/**********************************************************************************************************/
	public ArrayList<Point> getListOfIntersections() {
		ArrayList<Point> intersections2Return = new ArrayList<>();

		for (Point point : listOfIntersections) {
			intersections2Return.add(new Point(point.x, point.y));
		}

		return intersections2Return;
	}

	/**********************************************************************************************/
	public Boolean isOccupied(Point intersection) {
		if (occupiedIntersections.contains(intersection)) {

			return true;
		}
		return false;
	}

	/**********************************************************************************************/
	public int getMorpionsUp(Morpion morpion) {
		int x = morpion.getLocation().x;
		int y = morpion.getLocation().y;
		int count = 0;
		Morpion tempMorpion;

		tempMorpion = getMorpionAt(new Point(x, y - (count + 1) * SQUAREDIM));

		if (tempMorpion != null) {
			do {
				// CASE THE MORPION IS A DISK
				if (morpion.getMorpionType() == MorpionType.DISK) {
					if (tempMorpion != null && tempMorpion.getMorpionType() == MorpionType.DISK) {
						++count;
						tempMorpion = getMorpionAt(new Point(x, y - (count + 1) * SQUAREDIM));
					} else {
						return count;
					}
				}
				// END MORPION IS DISK

				// CASE THE MORPION IS A SQUARE
				else if (tempMorpion != null && tempMorpion.getMorpionType() == MorpionType.SQUARE) {
					++count;
					tempMorpion = getMorpionAt(new Point(x, y - (count + 1) * SQUAREDIM));

				} else {
					return count;
				} // END MORPION SQUARE

			} while (count != 4 && tempMorpion != null);
		}

		return count;

	}

	/**********************************************************************************************/
	public int getMorpionsDown(Morpion morpion) {
		int x = morpion.getLocation().x;
		int y = morpion.getLocation().y;
		int count = 0;
		Morpion tempMorpion;

		tempMorpion = getMorpionAt(new Point(x, y + (count + 1) * SQUAREDIM));

		if (tempMorpion != null) {
			do {
				// CASE THE MORPION IS A DISK

				if (morpion.getMorpionType() == MorpionType.DISK) {
					if (tempMorpion != null && tempMorpion.getMorpionType() == MorpionType.DISK) {
						++count;
						tempMorpion = getMorpionAt(new Point(x, y + (count + 1) * SQUAREDIM));
					} else {
						return count;
					}
				}
				// END MORPION IS DISK

				// CASE THE MORPION IS A SQUARE
				else if (tempMorpion != null && tempMorpion.getMorpionType() == MorpionType.SQUARE) {
					++count;
					tempMorpion = getMorpionAt(new Point(x, y + (count + 1) * SQUAREDIM));

				} else {
					return count;
				} // END MORPION SQUARE

			} while (count != 4 && tempMorpion != null);
		}

		return count;
	}

	/**************************************************************************************************/
	public int getMorpionsDown_Right(Morpion morpion) {
		int x = morpion.getLocation().x;
		int y = morpion.getLocation().y;
		int count = 0;
		Morpion tempMorpion;

		tempMorpion = getMorpionAt(new Point(x + (count + 1) * SQUAREDIM, y + (count + 1) * SQUAREDIM));

		if (tempMorpion != null) {
			do {
				// CASE THE MORPION IS A DISK

				if (morpion.getMorpionType() == MorpionType.DISK) {
					if (tempMorpion != null && tempMorpion.getMorpionType() == MorpionType.DISK) {
						++count;
						tempMorpion = getMorpionAt(new Point(x + (count + 1) * SQUAREDIM, y + (count + 1) * SQUAREDIM));
					} else {
						return count;
					}
				}
				// END MORPION IS DISK

				// CASE THE MORPION IS A SQUARE
				else if (tempMorpion != null && tempMorpion.getMorpionType() == MorpionType.SQUARE) {
					++count;
					tempMorpion = getMorpionAt(new Point(x + (count + 1) * SQUAREDIM, y + (count + 1) * SQUAREDIM));

				} else {
					return count;
				} // END MORPION SQUARE

			} while (count != 4 && tempMorpion != null);
		}

		return count;
	}

	/**********************************************************************************************/
	public int getMorpionsLeft(Morpion morpion) {
		int x = morpion.getLocation().x;
		int y = morpion.getLocation().y;
		int count = 0;
		Morpion tempMorpion;

		tempMorpion = getMorpionAt(new Point(x - (count + 1) * SQUAREDIM, y));

		if (tempMorpion != null) {
			do {
				// CASE THE MORPION IS A

				if (morpion.getMorpionType() == MorpionType.DISK) {
					if (tempMorpion != null && tempMorpion.getMorpionType() == MorpionType.DISK) {
						++count;
						tempMorpion = getMorpionAt(new Point(x - (count + 1) * SQUAREDIM, y));
					} else {
						return count;
					}
				}
				// END MORPION IS DISK

				// CASE THE MORPION IS A SQUARE
				else if (tempMorpion != null && tempMorpion.getMorpionType() == MorpionType.SQUARE) {
					++count;
					tempMorpion = getMorpionAt(new Point(x - (count + 1) * SQUAREDIM, y));

				} else {
					return count;
				} // END MORPION SQUARE

			} while (count != 4 && tempMorpion != null);
		}

		return count;
	}

	/**********************************************************************************************/
	public int getMorpionsLeft_Down(Morpion morpion) {
		int x = morpion.getLocation().x;
		int y = morpion.getLocation().y;
		int count = 0;
		Morpion tempMorpion;

		tempMorpion = getMorpionAt(new Point(x - (count + 1) * SQUAREDIM, y + (count + 1) * SQUAREDIM));

		if (tempMorpion != null) {
			do {
				// CASE THE MORPION IS A DISK

				if (morpion.getMorpionType() == MorpionType.DISK) {
					if (tempMorpion != null && tempMorpion.getMorpionType() == MorpionType.DISK) {
						++count;
						tempMorpion = getMorpionAt(new Point(x - (count + 1) * SQUAREDIM, y + (count + 1) * SQUAREDIM));
					} else {
						return count;
					}
				}
				// END MORPION IS DISK

				// CASE THE MORPION IS A SQUARE
				else if (tempMorpion != null && tempMorpion.getMorpionType() == MorpionType.SQUARE) {
					++count;
					tempMorpion = getMorpionAt(new Point(x - (count + 1) * SQUAREDIM, y + (count + 1) * SQUAREDIM));

				} else {
					return count;
				} // END MORPION SQUARE

			} while (count != 4 && tempMorpion != null);
		}

		return count;
	}

	/**********************************************************************************************/
	public int getMorpionsRight(Morpion morpion) {
		int x = morpion.getLocation().x;
		int y = morpion.getLocation().y;
		int count = 0;
		Morpion tempMorpion;

		tempMorpion = getMorpionAt(new Point(x + (count + 1) * SQUAREDIM, y));

		if (tempMorpion != null) {
			do {
				// CASE THE MORPION IS A DISK

				if (morpion.getMorpionType() == MorpionType.DISK) {
					if (tempMorpion != null && tempMorpion.getMorpionType() == MorpionType.DISK) {
						++count;
						tempMorpion = getMorpionAt(new Point(x + (count + 1) * SQUAREDIM, y));
					} else {
						return count;
					}
				}
				// END MORPION IS DISK

				// CASE THE MORPION IS A SQUARE
				else if (tempMorpion != null && tempMorpion.getMorpionType() == MorpionType.SQUARE) {
					++count;
					tempMorpion = getMorpionAt(new Point(x + (count + 1) * SQUAREDIM, y));

				} else {
					return count;
				} // END MORPION SQUARE

			} while (count != 4 && tempMorpion != null);
		}

		return count;
	}

	/******************************************************************************/
	public int getMorpionsUp_Left(Morpion morpion) {
		int x = morpion.getLocation().x;
		int y = morpion.getLocation().y;
		int count = 0;
		Morpion tempMorpion;

		tempMorpion = getMorpionAt(new Point(x - (count + 1) * SQUAREDIM, y - (count + 1) * SQUAREDIM));

		if (tempMorpion != null) {
			do {
				// CASE THE MORPION IS A DISK

				if (morpion.getMorpionType() == MorpionType.DISK) {
					if (tempMorpion != null && tempMorpion.getMorpionType() == MorpionType.DISK) {
						++count;
						tempMorpion = getMorpionAt(new Point(x - (count + 1) * SQUAREDIM, y - (count + 1) * SQUAREDIM));
					} else {
						return count;
					}
				}
				// END MORPION IS DISK

				// CASE THE MORPION IS A SQUARE
				else if (tempMorpion != null && tempMorpion.getMorpionType() == MorpionType.SQUARE) {
					++count;
					tempMorpion = getMorpionAt(new Point(x - (count + 1) * SQUAREDIM, y - (count + 1) * SQUAREDIM));

				} else {
					return count;
				} // END MORPION SQUARE

			} while (count != 4 && tempMorpion != null);
		}

		return count;
	}

	/******************************************************************************/
	public int getMorpionsUp_Right(Morpion morpion) {
		int x = morpion.getLocation().x;
		int y = morpion.getLocation().y;
		int count = 0;
		Morpion tempMorpion;

		tempMorpion = getMorpionAt(new Point(x + (count + 1) * SQUAREDIM, y - (count + 1) * SQUAREDIM));

		if (tempMorpion != null) {
			do {
				// CASE THE MORPION IS A DISK

				if (morpion.getMorpionType() == MorpionType.DISK) {
					if (tempMorpion != null && tempMorpion.getMorpionType() == MorpionType.DISK) {
						++count;
						tempMorpion = getMorpionAt(new Point(x + (count + 1) * SQUAREDIM, y - (count + 1) * SQUAREDIM));
					} else {
						return count;
					}
				}
				// END MORPION IS DISK

				// CASE THE MORPION IS A SQUARE
				else if (tempMorpion != null && tempMorpion.getMorpionType() == MorpionType.SQUARE) {
					++count;
					tempMorpion = getMorpionAt(new Point(x + (count + 1) * SQUAREDIM, y - (count + 1) * SQUAREDIM));

				} else {
					return count;
				} // END MORPION SQUARE

			} while (count != 4 && tempMorpion != null);
		}

		return count;
	}

	/**********************************************************************************************/
	public Point getCenter() {
		return new Point(center.x, center.y);
	}

	/**********************************************************************************************/
	// RETURNS ALL MORPIONS BORDERING INTERSECTION IN RADIUS
	public ArrayList<Morpion> getAllBorderOfIntersection(Point intersection, int radius) {
		ArrayList<Morpion> border = new ArrayList<>();

		// up
		if (getMorpionAt(new Point(intersection.x, intersection.y - radius * SQUAREDIM)) != null) {
			border.add(getMorpionAt(new Point(intersection.x, intersection.y - radius * SQUAREDIM)));

		}

		// right
		if (getMorpionAt(new Point(intersection.x + radius * SQUAREDIM, intersection.y)) != null)
			border.add(getMorpionAt(new Point(intersection.x + radius * SQUAREDIM, intersection.y)));

		// down
		if (getMorpionAt(new Point(intersection.x, intersection.y + radius * SQUAREDIM)) != null)
			border.add(getMorpionAt(new Point(intersection.x, intersection.y + radius * SQUAREDIM)));

		// left
		if (getMorpionAt(new Point(intersection.x - radius * SQUAREDIM, intersection.y)) != null)
			border.add(getMorpionAt(new Point(intersection.x - radius * SQUAREDIM, intersection.y)));

		// up left
		if (getMorpionAt(new Point(intersection.x - radius * SQUAREDIM, intersection.y - radius * SQUAREDIM)) != null)
			border.add(
					getMorpionAt(new Point(intersection.x - radius * SQUAREDIM, intersection.y - radius * SQUAREDIM)));

		// up right
		if (getMorpionAt(new Point(intersection.x + radius * SQUAREDIM, intersection.y - radius * SQUAREDIM)) != null)
			border.add(
					getMorpionAt(new Point(intersection.x + radius * SQUAREDIM, intersection.y - radius * SQUAREDIM)));

		// down left
		if (getMorpionAt(new Point(intersection.x - radius * SQUAREDIM, intersection.y + radius * SQUAREDIM)) != null)
			border.add(
					getMorpionAt(new Point(intersection.x - radius * SQUAREDIM, intersection.y + radius * SQUAREDIM)));

		// down right
		if (getMorpionAt(new Point(intersection.x + radius * SQUAREDIM, intersection.y + radius * SQUAREDIM)) != null)
			border.add(
					getMorpionAt(new Point(intersection.x + radius * SQUAREDIM, intersection.y + radius * SQUAREDIM)));

		return border;

	}

	/**********************************************************************************************/
	// RETURNS ALL FREE INTERSECTIONS AROUND POINT IN A GIVEN RADIUS
	public ArrayList<Point> getXBorderOf(Point intersection, int radius) {
		ArrayList<Point> border = new ArrayList<>();

		if (getIntersectionXUpRightFrom(intersection, radius) != null)
			border.add(getIntersectionXUpRightFrom(intersection, radius));

		if (getIntersectionXRightFrom(intersection, radius) != null)
			border.add(getIntersectionXRightFrom(intersection, radius));

		if (getIntersectionXUpFrom(intersection, radius) != null)
			border.add(getIntersectionXUpFrom(intersection, radius));

		if (getIntersectionXUpLeftFrom(intersection, radius) != null)
			border.add(getIntersectionXUpLeftFrom(intersection, radius));

		if (getIntersectionXLeftFrom(intersection, radius) != null)
			border.add(getIntersectionXLeftFrom(intersection, radius));

		if (getIntersectionXDownFrom(intersection, radius) != null)
			border.add(getIntersectionXDownFrom(intersection, radius));

		if (getIntersectionXDownRightFrom(intersection, radius) != null)
			border.add(getIntersectionXDownRightFrom(intersection, radius));

		if (getIntersectionXDownLeftFrom(intersection, radius) != null)
			border.add(getIntersectionXDownLeftFrom(intersection, radius));

		return border;
	}

	/***********************************************************************************************/
	Point getIntersectionXLeftFrom(Point intersection, int numberLeft) {
		Point point2Return = new Point(intersection.x - numberLeft * SQUAREDIM, intersection.y);

		if (listOfIntersections.contains(point2Return))
			return point2Return;

		return null;
	}

	/**********************************************************************************************/
	public Point getIntersectionXRightFrom(Point intersection, int numberRight) {
		Point point2Return = new Point(intersection.x + numberRight * SQUAREDIM, intersection.y);

		if (listOfIntersections.contains(point2Return))
			return point2Return;

		return null;
	}

	/**********************************************************************************************/
	public Point getIntersectionXUpFrom(Point intersection, int numberUp) {
		Point point2Return = new Point(intersection.x, intersection.y - numberUp * SQUAREDIM);

		if (listOfIntersections.contains(point2Return))
			return point2Return;

		return null;
	}

	/**********************************************************************************************/
	public Point getIntersectionXDownFrom(Point intersection, int numberDown) {
		Point point2Return = new Point(intersection.x, intersection.y + numberDown * SQUAREDIM);

		if (listOfIntersections.contains(point2Return))
			return point2Return;
		return null;
	}

	/**********************************************************************************************/
	public Point getIntersectionXUpRightFrom(Point intersection, int numberUpRight) {
		Point point2Return = new Point(intersection.x + numberUpRight * SQUAREDIM,
				intersection.y - numberUpRight * SQUAREDIM);

		if (listOfIntersections.contains(point2Return))
			return point2Return;
		return null;
	}

	/**********************************************************************************************/
	public Point getIntersectionXDownLeftFrom(Point intersection, int numberDownLeft) {
		Point point2Return = new Point(intersection.x - numberDownLeft * SQUAREDIM,
				intersection.y + numberDownLeft * SQUAREDIM);

		if (listOfIntersections.contains(point2Return))
			return point2Return;

		return null;
	}

	/**********************************************************************************************/
	public Point getIntersectionXDownRightFrom(Point intersection, int numberDownRight) {
		if (listOfIntersections.contains(
				new Point(intersection.x + numberDownRight * SQUAREDIM, intersection.y + numberDownRight * SQUAREDIM)))
			return new Point(intersection.x + numberDownRight * SQUAREDIM,
					intersection.y + numberDownRight * SQUAREDIM);
		return null;
	}

	/**********************************************************************************************/
	Point getIntersectionXUpLeftFrom(Point intersection, int numberUpLeft) {
		if (listOfIntersections.contains(
				new Point(intersection.x - numberUpLeft * SQUAREDIM, intersection.y - numberUpLeft * SQUAREDIM)))
			return new Point(intersection.x - numberUpLeft * SQUAREDIM, intersection.y - numberUpLeft * SQUAREDIM);
		return null;
	}

	/**********************************************************************************************/
	public ArrayList<Morpion> getMorpionsXRadiusAround(Point intersection, int radius) {
		ArrayList<Morpion> border = new ArrayList<>();

		// up
		Morpion morpionUp = getMorpionAt(new Point(intersection.x, intersection.y - radius * SQUAREDIM));
		if (morpionUp != null) {
			border.add(morpionUp);

		}

//right
		Morpion morpionRight = getMorpionAt(new Point(intersection.x + radius * SQUAREDIM, intersection.y));
		if (morpionRight != null)
			border.add(morpionRight);

//down
		Morpion morpionDown = getMorpionAt(new Point(intersection.x, intersection.y + radius * SQUAREDIM));
		if (morpionDown != null)
			border.add(morpionDown);

//left
		Morpion morpionLeft = getMorpionAt(new Point(intersection.x - radius * SQUAREDIM, intersection.y));
		if (morpionLeft != null)
			border.add(morpionLeft);

//up left
		Morpion morpionUpLeft = getMorpionAt(
				new Point(intersection.x - radius * SQUAREDIM, intersection.y - radius * SQUAREDIM));
		if (morpionUpLeft != null)
			border.add(morpionLeft);

//up right
		Morpion morpionUpRight = getMorpionAt(
				new Point(intersection.x + radius * SQUAREDIM, intersection.y - radius * SQUAREDIM));
		if (morpionUpRight != null)
			border.add(morpionUpRight);

//down left
		Morpion morpionDownLeft = getMorpionAt(
				new Point(intersection.x - radius * SQUAREDIM, intersection.y + radius * SQUAREDIM));
		if (morpionDownLeft != null)
			border.add(morpionDownLeft);

//down right
		Morpion morpionDownRight = getMorpionAt(
				new Point(intersection.x + radius * SQUAREDIM, intersection.y + radius * SQUAREDIM));
		if (morpionDownRight != null)
			border.add(morpionDownRight);

		return border;

	}

	/**********************************************************************************************/
	public Morpion getLastMoveMade() {

		return listOfMorpions.get(occupiedIntersections.get(occupiedIntersections.size() - 1));
	}

	/**********************************************************************************************/
	public HashMap<Point, Morpion> getListOfMorpions() {
		HashMap<Point, Morpion> morpions2Return = new HashMap<>();

		for (HashMap.Entry<Point, Morpion> entry : listOfMorpions.entrySet()) {
			Morpion morpion = entry.getValue();
			Point location = morpion.getLocation();
			morpions2Return.put(location, new Morpion(morpion.getMorpionType(), location));

		}
		return morpions2Return;
	}

	/**********************************************************************************************/
	public ArrayList<Point> getOccupiedIntersections() {
		ArrayList<Point> intersections2Return = new ArrayList<>();

		for (Point point : occupiedIntersections) {
			intersections2Return.add(new Point(point.x, point.y));
		}

		return intersections2Return;
	}

	/**********************************************************************************************/
	public boolean isABorder(Point intersection) {
		if (!listOfIntersections.contains(intersection))
			return true;
		else if (occupiedIntersections.contains(intersection))
			return true;

		return false;
	}

	/*********************************************************************************************/
	public void newGame() {
		setEnabled(true);

		listOfMorpions.clear();
		occupiedIntersections.clear();
		redoList.clear();

		morpionTurn = MorpionType.SQUARE;
		gameOver = false;

		invalidate();

	}

	/**********************************************************************************************/
	public void undoMove() {
		if (!listOfMorpions.isEmpty()) {

			Point lastIntersection = occupiedIntersections.get(occupiedIntersections.size() - 1);

			occupiedIntersections.remove(lastIntersection);

			redoList.push(listOfMorpions.get(lastIntersection));
			listOfMorpions.remove(lastIntersection);

			gameOver = false;

			changeMorpionTurn();

		}
	}

	/*********************************************************************************************/
	public void redo() {

		if (redoList.size() > 0) {
			Morpion morpion2ReAdd = redoList.peek();

			addMorpion(morpion2ReAdd);

		}
	}

	/***************************************************************************************************/
	public Stack<Morpion> getRedoList() {
		Stack<Morpion> list2Return = new Stack<>();

		for (Morpion morpion : redoList) {
			list2Return.add(new Morpion(morpion.getMorpionType(), morpion.getLocation()));
		}
		return list2Return;
	}

	/**********************************************************************************************/
	public boolean getGameoVerStatus() {
		return gameOver;
	}

	/**********************************************************************************************/
	public void setPlayAsSquares(boolean playAs) {
		playAsSquare = playAs;
	}

	/**************************************************************************************************/
	public boolean getPlayAsSquares() {
		return playAsSquare;
	}

	/*************************************************************************************************/
	public void setPlayAsDisks(boolean playAs) {
		playAsDisks = playAs;
	}

	/*************************************************************************************************/
	public boolean getPlayAsDisks() {
		return playAsDisks;
	}

	/*************************************************************************************************/
	public void setTwo_player(boolean playAgainst) {
		two_player = playAgainst;
	}

	/**************************************************************************************************/
	public boolean getTwo_player() {
		return two_player;
	}

	/************************************************************************************************/
	public boolean getPlayAgainstComp() {
		return playAgainstComp;
	}

	/*******************************************************************************************************/
	public void setPlayAgainstComp(boolean playAgainst) {
		playAgainstComp = playAgainst;
	}

	/*******************************************************************************************************/
	public boolean getGameOverStatus() {

		return gameOver;
	}

	/********************************************************************************************************/
	public GomokuBoard copyBoard() {
		GomokuBoard copyBoard = new GomokuBoard();

		// SQUAREDIM
		copyBoard.SQUAREDIM = SQUAREDIM;

		// MORPIONTURN
		if (morpionTurn == MorpionType.SQUARE) {
			copyBoard.morpionTurn = MorpionType.SQUARE;
		} else {
			copyBoard.morpionTurn = MorpionType.DISK;
		}

		// Intersections
		copyBoard.listOfIntersections = new ArrayList<>();
		for (Point point : listOfIntersections) {
			copyBoard.listOfIntersections.add(new Point(point.x, point.y));
		}

		// OCCUPIED INTERSECTIONS
		copyBoard.occupiedIntersections = new ArrayList<>();
		for (Point point : occupiedIntersections) {
			copyBoard.occupiedIntersections.add(new Point(point.x, point.y));
		}

		// LIST OF MORPIONS
		copyBoard.listOfMorpions = new HashMap<>();
		for (HashMap.Entry<Point, Morpion> entry : listOfMorpions.entrySet()) {
			Morpion morpion = entry.getValue();
			Point location = morpion.getLocation();
			copyBoard.listOfMorpions.put(new Point(location.x, location.y),
					new Morpion(morpion.getMorpionType(), location));
		}

		return copyBoard;
	}

	/***************************************************************************************************************/
	public void loadGame(HashMap<Point, Morpion> morpionsToLoad, Stack<Morpion> redoList, boolean gameOver,
			MorpionType morpionTurn) {

		newGame();

		this.listOfMorpions = morpionsToLoad;

		this.gameOver = gameOver;

		// UPDATING REDOLIST
		// this.redoList.clear();
		for (Morpion morpion : redoList) {
			this.redoList.push(new Morpion(morpion.getMorpionType(), morpion.getLocation()));
		}

		// load occupied intersections
		setOccupiedIntersections();

		// load MorpionTurn
		this.morpionTurn = morpionTurn;

	}

	/**********************************************************************************************/
	private void setOccupiedIntersections() {
		for (HashMap.Entry<Point, Morpion> entry : listOfMorpions.entrySet()) {
			occupiedIntersections.add(new Point(entry.getValue().getLocation().x, entry.getValue().getLocation().y));
		}
	}

	/************************************************************************************************/
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeObject(listOfMorpions);
		out.writeObject(redoList);
		out.writeBoolean(gameOver);
		out.writeBoolean(playAsSquare);
		out.writeBoolean(playAsDisks);
		out.writeBoolean(playAgainstComp);
		out.writeBoolean(two_player);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		listOfMorpions = (HashMap<Point, Morpion>) in.readObject();
		redoList = (Stack<Morpion>) in.readObject();
		gameOver = in.readBoolean();
		playAsSquare = in.readBoolean();
		playAsDisks = in.readBoolean();
		playAgainstComp = in.readBoolean();
		two_player = in.readBoolean();

	}

	/**********************************************************************************************/
}
