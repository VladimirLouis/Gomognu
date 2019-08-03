package gomoku;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class GomokuEngine {
	GomokuBoard board;

	MorpionType MAX_PLAYER;
	MorpionType MIN_PLAYER;

//MAX Pillars
	int MAX_B2P;
	int MAX_SB2P;
	int MAX_UB2P;

	int MAX_B3P;
	int MAX_SB3P;
	int MAX_UB3P;

	int MAX_B4P;
	int MAX_SB4P;
	int MAX_UB4P;

	int MAX_BP3;
	int MAX_SBP3;
	int MAX_UBP3;

	int MAX_BP4;
	int MAX_SBP4;
	int MAX_UBP4;

	int MAX_BP5;
	int MAX_SBP5;
	int MAX_UBP5;

//MIN Pillars
	int MIN_B2P;
	int MIN_SB2P;
	int MIN_UB2P;

	int MIN_B3P;
	int MIN_SB3P;
	int MIN_UB3P;

	int MIN_B4P;
	int MIN_SB4P;
	int MIN_UB4P;

	int MIN_BP3;
	int MIN_SBP3;
	int MIN_UBP3;

	int MIN_BP4;
	int MIN_SBP4;
	int MIN_UBP4;

	int MIN_BP5;
	int MIN_SBP5;
	int MIN_UBP5;

	// MAX PS
	int MAX_PS = 0;

	// MIN PS
	int MIN_PS = 0;

	// int passCounter=0;

	/*****************************************************************************************************/
	public GomokuEngine(GomokuBoard board) {
		this.board = board;
		this.MAX_PLAYER = board.getMorpionTurn();
		this.MIN_PLAYER = oppositeOfMorpionType(MAX_PLAYER);

	}

	/*******************************************
	 * abstract methods
	 ***************************************/
	public abstract Point move();

	/**************************************************************************************************/
	public MorpionType oppositeOfMorpionType(MorpionType morpionType) {
		if (morpionType == MorpionType.SQUARE) {
			return MorpionType.DISK;
		} else {
			return MorpionType.SQUARE;
		}
	}

	/**************************************************************************************************/
	public void evaluateBoard() {

		for (HashMap.Entry<Point, Morpion> entry : board.getListOfMorpions().entrySet()) {
			// ++passCounter;
			Morpion morpion = entry.getValue();

			calculatePillars(morpion, PillarDirection.HORIZONTAL);
			calculatePillars(morpion, PillarDirection.SLANTED_DOWN);
			calculatePillars(morpion, PillarDirection.SLANTED_UP);
			calculatePillars(morpion, PillarDirection.VERTICAL);

		}

		fixPillarDiscrepancies();
		// if(MAX_B2P==1)
		// System.out.println("MAX_B2P "+MAX_B2P);

		//
	}

	/********************************************************************************************************************************/
	private void fixPillarDiscrepancies() {

		MAX_B2P = MAX_B2P / 2;
		MAX_SB2P = MAX_SB2P / 2;
		MAX_UB2P = MAX_UB2P / 2;

		MAX_B3P = MAX_B3P / 3;

		MAX_SB3P = MAX_SB3P / 3;
		MAX_UB3P = MAX_UB3P / 3;

		MAX_B4P = MAX_B4P / 4;
		MAX_SB4P = MAX_SB4P / 4;
		MAX_UB4P = MAX_UB4P / 4;

		MAX_BP3 = MAX_BP3 / 2;
		MAX_SBP3 = MAX_SBP3 / 2;
		MAX_UBP3 = MAX_UBP3 / 2;

		MAX_BP4 = MAX_BP4 / 3;
		MAX_SBP4 = MAX_SBP4 / 3;
		MAX_UBP4 = MAX_UBP4 / 3;

		int dx_bp5 = MAX_BP5 % 4;
		if (dx_bp5 == 0) {
			MAX_BP5 = MAX_BP5 / 4;
		} else {
			MAX_BP5 = MAX_BP5 - dx_bp5;
			MAX_BP5 = MAX_BP5 / 4;
		}

		// MAX_SBP5= MAX_SBP5/4;
		int dx_sbp5 = MAX_SBP5 % 4;
		if (dx_sbp5 == 0) {
			MAX_SBP5 = MAX_SBP5 / 4;
		} else {
			MAX_SBP5 = MAX_SBP5 - dx_sbp5;
			MAX_SBP5 = MAX_SBP5 / 4;
		}

		int dx_ubp5 = MAX_UBP5 % 4;
		if (dx_ubp5 == 0) {
			MAX_UBP5 = MAX_UBP5 / 4;
		} else {
			// System.out.println("We are here!!!");
			MAX_UBP5 = MAX_UBP5 - dx_ubp5;
			MAX_UBP5 = MAX_UBP5 / 4;
		}

		MIN_B2P = MIN_B2P / 2;
		MIN_SB2P = MIN_SB2P / 2;
		MIN_UB2P = MIN_UB2P / 2;

		MIN_B3P = MIN_B3P / 3;
		MIN_SB3P = MIN_SB3P / 3;
		MIN_UB3P = MIN_UB3P / 3;

		MIN_B4P = MIN_B4P / 4;
		MIN_SB4P = MIN_SB4P / 4;
		MIN_UB4P = MIN_UB4P / 4;

		MIN_BP3 = MIN_BP3 / 2;
		MIN_SBP3 = MIN_SBP3 / 2;
		MIN_UBP3 = MIN_UBP3 / 2;

		MIN_BP4 = MIN_BP4 / 3;
		MIN_SBP4 = MIN_SBP4 / 3;
		MIN_UBP4 = MIN_UBP4 / 3;

		dx_bp5 = MIN_BP5 % 4;
		if (dx_bp5 == 0) {
			MIN_BP5 = MIN_BP5 / 4;
		} else {
			MIN_BP5 = MIN_BP5 - dx_bp5;
			MIN_BP5 = MIN_BP5 / 4;
		}

		dx_sbp5 = MIN_SBP5 % 4;
		if (dx_sbp5 == 0) {
			MIN_SBP5 = MIN_SBP5 / 4;
		} else {
			MIN_SBP5 = MIN_SBP5 - dx_sbp5;
			MIN_SBP5 = MIN_SBP5 / 4;
		}

		dx_ubp5 = MIN_UBP5 % 4;
		if (dx_ubp5 == 0) {
			MIN_UBP5 = MIN_UBP5 / 4;
		} else {
			MIN_UBP5 = MIN_UBP5 - dx_ubp5;
			MIN_UBP5 = MIN_UBP5 / 4;
		}

	}

	/******************************************************************************************************************************/
	public void calculatePillars(Morpion morpion, PillarDirection direction) {

		MorpionType morpionType = morpion.getMorpionType();
		Point morpionLocation = morpion.getLocation();

		int first = -1;
		int second = -1;
		Point firstBorder = new Point();
		Point firstBorder1 = new Point();
		Point secondBorder = new Point();
		Point secondBorder1 = new Point();
		boolean firstIsABorder = true;
		boolean secondIsABorder = true;

		switch (direction) {

		case HORIZONTAL:
			// ++passCounter;
			first = board.getMorpionsLeft(morpion);
			second = board.getMorpionsRight(morpion);
			firstBorder = board.getIntersectionXLeftFrom(morpionLocation, first + 1);
			firstBorder1 = board.getIntersectionXLeftFrom(morpionLocation, first + 2);
			secondBorder = board.getIntersectionXRightFrom(morpionLocation, second + 1);
			secondBorder1 = board.getIntersectionXRightFrom(morpionLocation, second + 2);

			firstIsABorder = board.isABorder(firstBorder);
			secondIsABorder = board.isABorder(secondBorder);

			break;

		case VERTICAL:
			first = board.getMorpionsUp(morpion);
			second = board.getMorpionsDown(morpion);
			firstBorder = board.getIntersectionXUpFrom(morpionLocation, first + 1);
			firstBorder1 = board.getIntersectionXUpFrom(morpionLocation, first + 2);
			secondBorder = board.getIntersectionXDownFrom(morpionLocation, second + 1);
			secondBorder1 = board.getIntersectionXDownFrom(morpionLocation, second + 2);

			firstIsABorder = board.isABorder(firstBorder);
			secondIsABorder = board.isABorder(secondBorder);

			break;

		case SLANTED_UP:
			first = board.getMorpionsLeft_Down(morpion);
			second = board.getMorpionsUp_Right(morpion);
			firstBorder = board.getIntersectionXDownLeftFrom(morpionLocation, first + 1);
			firstBorder1 = board.getIntersectionXDownLeftFrom(morpionLocation, first + 2);
			secondBorder = board.getIntersectionXUpRightFrom(morpionLocation, second + 1);
			secondBorder1 = board.getIntersectionXUpRightFrom(morpionLocation, second + 2);

			firstIsABorder = board.isABorder(firstBorder);
			secondIsABorder = board.isABorder(secondBorder);

			break;

		case SLANTED_DOWN:
			first = board.getMorpionsUp_Left(morpion);
			second = board.getMorpionsDown_Right(morpion);
			firstBorder = board.getIntersectionXUpLeftFrom(morpionLocation, first + 1);
			firstBorder1 = board.getIntersectionXUpLeftFrom(morpionLocation, first + 2);
			secondBorder = board.getIntersectionXDownRightFrom(morpionLocation, second + 1);
			secondBorder1 = board.getIntersectionXDownRightFrom(morpionLocation, second + 2);

			firstIsABorder = board.isABorder(firstBorder);
			secondIsABorder = board.isABorder(secondBorder);

			break;

		}

		// B2P
		if (((first == 1 && second == 0) || (first == 0 && second == 1)) && (firstIsABorder && secondIsABorder)) {

			// System.out.println("We are here!!!");

			if (morpionType == MAX_PLAYER) {
				++MAX_B2P;
			} else {
				// ++passCounter;
				++MIN_B2P;
			}

		}

		// SB2P
		else if (((first == 1 && second == 0) || (first == 0 && second == 1))
				&& (firstIsABorder && !secondIsABorder || (!firstIsABorder && secondIsABorder))) {

			if (morpionType == MAX_PLAYER) {
				++MAX_SB2P;
			} else {
				++MIN_SB2P;
			}

			calculatePotentialPillars(PillarType.SB2P, firstIsABorder, secondIsABorder, firstBorder1, secondBorder1,
					direction, morpionType);

		}

		// UB2P
		else if (((first == 1 && second == 0) || (first == 0 && second == 1))
				&& (!firstIsABorder && !secondIsABorder)) {

			if (morpionType == MAX_PLAYER) {
				++MAX_UB2P;
			} else {
				++MIN_UB2P;
			}
			// System.out.println("?????????");
			calculatePotentialPillars(PillarType.UB2P, firstIsABorder, secondIsABorder, firstBorder1, secondBorder1,
					direction, morpionType);

		}

		// B3P
		else if (((first == 1 && second == 1) || (first == 2 && second == 0) || (first == 0 && second == 2))
				&& (firstIsABorder && secondIsABorder)) {

			if (morpionType == MAX_PLAYER) {
				++MAX_B3P;
			} else {
				++MIN_B3P;
			}

		}

		// SB3P
		else if (((first == 1 && second == 1) || (first == 2 && second == 0) || (first == 0 && second == 2))
				&& ((firstIsABorder && !secondIsABorder) || (!firstIsABorder && secondIsABorder))) {

			if (morpionType == MAX_PLAYER) {
				++MAX_SB3P;
			} else {
				++MIN_SB3P;
			}
			calculatePotentialPillars(PillarType.SB3P, firstIsABorder, secondIsABorder, firstBorder1, secondBorder1,
					direction, morpionType);

		}

		// UB3P
		else if (((first == 1 && second == 1) || (first == 2 && second == 0) || (first == 0 && second == 2))
				&& (!firstIsABorder && !secondIsABorder)) {

			if (morpionType == MAX_PLAYER) {

				++MAX_UB3P;
			} else {

				++MIN_UB3P;
			}
			calculatePotentialPillars(PillarType.UB3P, firstIsABorder, secondIsABorder, firstBorder1, secondBorder1,
					direction, morpionType);

		}

		// B4P
		else if (((first == 1 && second == 2) || (first == 2 && second == 1) || (first == 3 && second == 0)
				|| (first == 0 && second == 3)) && (firstIsABorder && secondIsABorder)) {

			if (morpionType == MAX_PLAYER) {
				++MAX_B4P;
			} else {
				++MIN_B4P;
			}

		}

		// SB4P
		else if (((first == 1 && second == 2) || (first == 2 && second == 1) || (first == 3 && second == 0)
				|| (first == 0 && second == 3))
				&& ((firstIsABorder && !secondIsABorder) || (!firstIsABorder && secondIsABorder))) {

			if (morpionType == MAX_PLAYER) {
				++MAX_SB4P;
			} else {
				++MIN_SB4P;
			}

			calculatePotentialPillars(PillarType.SB4P, firstIsABorder, secondIsABorder, firstBorder1, secondBorder1,
					direction, morpionType);
		}

		// UB4P
		else if (((first == 1 && second == 2) || (first == 2 && second == 1) || (first == 3 && second == 0)
				|| (first == 0 && second == 3)) && ((!firstIsABorder && !secondIsABorder))) {

			if (morpionType == MAX_PLAYER) {
				++MAX_UB4P;
			} else {
				++MIN_UB4P;
			}

			calculatePotentialPillars(PillarType.UB4P, firstIsABorder, secondIsABorder, firstBorder1, secondBorder1,
					direction, morpionType);
		}

		// B1P
		else if ((first == 0 && second == 0) && (firstIsABorder && secondIsABorder)) {

		}

		// SB1P
		else if ((first == 0 && second == 0) && (firstIsABorder && !secondIsABorder)
				|| (!firstIsABorder && secondIsABorder)) {

			calculatePotentialPillars(PillarType.SB1P, firstIsABorder, secondIsABorder, firstBorder1, secondBorder1,
					direction, morpionType);
		}

		// UB1P
		else if ((first == 0 && second == 0) && (!firstIsABorder && !secondIsABorder)) {
			calculatePotentialPillars(PillarType.UB1P, firstIsABorder, secondIsABorder, firstBorder1, secondBorder1,
					direction, morpionType);
		}

	}

	/********************************************************************************************************************************/
	private void calculatePotentialPillars(PillarType pillarType, boolean firstIsABorder, boolean secondIsABorder,
			Point firstOuterBorder, Point secondOuterBorder, PillarDirection direction, MorpionType morpionType) {

		Morpion morpion1 = board.getMorpionAt(firstOuterBorder);
		Morpion morpion2 = board.getMorpionAt(secondOuterBorder);
		int first = -1;
		int second = -1;
		boolean p_pillar1_is_a_border = true;
		boolean p_pillar2_is_a_border = true;

		switch (direction) {

		case HORIZONTAL:

			if (morpion1 != null && morpion1.getMorpionType() == morpionType) {
				first = board.getMorpionsLeft(morpion1);
				p_pillar1_is_a_border = board.isABorder(board.getIntersectionXLeftFrom(firstOuterBorder, first + 1));
			}

			if (morpion2 != null && morpion2.getMorpionType() == morpionType) {
				second = board.getMorpionsRight(morpion2);
				p_pillar2_is_a_border = board.isABorder(board.getIntersectionXRightFrom(secondOuterBorder, second + 1));
			}

			break;

		case VERTICAL:

			if (morpion1 != null && morpion1.getMorpionType() == morpionType) {
				first = board.getMorpionsUp(morpion1);
				p_pillar1_is_a_border = board.isABorder(board.getIntersectionXUpFrom(firstOuterBorder, first + 1));
			}

			if (morpion2 != null && morpion2.getMorpionType() == morpionType) {
				second = board.getMorpionsDown(morpion2);
				p_pillar2_is_a_border = board.isABorder(board.getIntersectionXDownFrom(secondOuterBorder, second + 1));
			}

			break;

		case SLANTED_DOWN:

			if (morpion1 != null && morpion1.getMorpionType() == morpionType) {
				first = board.getMorpionsUp_Left(morpion1);
				p_pillar1_is_a_border = board.isABorder(board.getIntersectionXUpLeftFrom(firstOuterBorder, first + 1));
			}

			if (morpion2 != null && morpion2.getMorpionType() == morpionType) {
				second = board.getMorpionsDown_Right(morpion2);
				p_pillar2_is_a_border = board
						.isABorder(board.getIntersectionXDownRightFrom(secondOuterBorder, second + 1));
			}

			break;

		case SLANTED_UP:

			if (morpion1 != null && morpion1.getMorpionType() == morpionType) {
				first = board.getMorpionsLeft_Down(morpion1);
				p_pillar1_is_a_border = board
						.isABorder(board.getIntersectionXDownLeftFrom(firstOuterBorder, first + 1));
			}

			if (morpion2 != null && morpion2.getMorpionType() == morpionType) {
				second = board.getMorpionsUp_Right(morpion2);
				p_pillar2_is_a_border = board
						.isABorder(board.getIntersectionXUpRightFrom(secondOuterBorder, second + 1));
			}

			break;
		}

		if (!firstIsABorder && morpion1 != null && morpion1.getMorpionType() == morpionType) {

			if (PillarType.UB1P == pillarType && p_pillar1_is_a_border) {

				if (first == 0) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP3;
					} else {

						++MIN_SBP3;
					}
				} else if (first == 1) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP4;
					} else {
						++MIN_SBP4;
					}
				}

				else if (first >= 2 && first < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP5;
					} else {
						++MIN_SBP5;
					}
				}
			}

			else if (PillarType.UB1P == pillarType && !p_pillar1_is_a_border) {

				if (first == 0) {
					if (morpionType == MAX_PLAYER) {
						++MAX_UBP3;
					} else {

						++MIN_UBP3;
					}
				} else if (first == 1) {
					if (morpionType == MAX_PLAYER) {
						++MAX_UBP4;
					} else {
						++MIN_UBP4;
					}
				}

				else if (first >= 2 && first < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_UBP5;
					} else {
						++MIN_UBP5;
					}
				}
			}

			else if (PillarType.SB1P == pillarType && p_pillar1_is_a_border) {

				if (first == 0) {
					if (morpionType == MAX_PLAYER) {
						++MAX_BP3;
					} else {

						++MIN_BP3;
					}
				} else if (first == 1) {
					if (morpionType == MAX_PLAYER) {
						++MAX_BP4;
					} else {
						++MIN_BP4;
					}
				} else if (first >= 2 && first < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_BP5;
					} else {
						++MIN_BP5;
					}
				}

			}

			else if (PillarType.SB1P == pillarType && !p_pillar1_is_a_border) {

				if (first == 0) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP3;
					} else {

						++MIN_SBP3;
					}
				} else if (first == 1) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP4;
					} else {
						++MIN_SBP4;
					}
				} else if (first >= 2 && first < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP5;
					} else {
						++MIN_SBP5;
					}
				}
			}

			else if (PillarType.SB2P == pillarType && p_pillar1_is_a_border) {
				if (first == 0) {
					if (morpionType == MAX_PLAYER) {
						++MAX_BP4;
					} else {
						++MIN_BP4;
					}
				} else if (first >= 1 && first < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_BP5;
					} else {
						++MIN_BP5;
					}
				}
			}

			else if (PillarType.SB2P == pillarType && !p_pillar1_is_a_border) {
				if (first == 0) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP4;
					} else {
						++MIN_SBP4;
					}
				} else if (first >= 1 && first < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP5;
					} else {
						++MIN_SBP5;
					}
				}
			}

			else if ((PillarType.SB3P == pillarType || PillarType.SB4P == pillarType) && p_pillar1_is_a_border) {

				if (first >= 0 && first < 3) {
					if (morpionType == MAX_PLAYER) {
						// System.out.println("WE are here");
						++MAX_BP5;
					} else {
						++MIN_BP5;
					}
				}
			}

			else if ((PillarType.SB3P == pillarType || PillarType.SB4P == pillarType) && !p_pillar1_is_a_border) {

				if (first >= 0 && first < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP5;
					} else {
						++MIN_SBP5;
					}
				}
			}

			else if (PillarType.UB2P == pillarType && p_pillar1_is_a_border) {
				if (first == 0) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP4;
					} else {
						++MIN_SBP4;
					}
				} else if (first >= 1 && first < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP5;
					} else {
						++MIN_SBP5;
					}
				}
			}

			else if (PillarType.UB2P == pillarType && !p_pillar1_is_a_border) {
				if (first == 0) {
					if (morpionType == MAX_PLAYER) {
						++MAX_UBP4;
					} else {
						++MIN_UBP4;
					}
				} else if (first >= 1 && first < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_UBP5;
					} else {
						++MIN_UBP5;
					}
				}
			}

			else if ((PillarType.UB3P == pillarType || PillarType.UB4P == pillarType) && p_pillar1_is_a_border) {

				if (first >= 0 && first < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP5;
					} else {
						++MIN_SBP5;
					}
				}
			}

			else if ((PillarType.UB3P == pillarType || PillarType.UB4P == pillarType) && !p_pillar1_is_a_border) {

				if (first >= 0 && first < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_UBP5;
					} else {
						++MIN_UBP5;
					}
				}
			}

		}

		if (!secondIsABorder && morpion2 != null && morpion2.getMorpionType() == morpionType) {

			if (PillarType.UB1P == pillarType && p_pillar2_is_a_border) {

				if (second == 0) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP3;
					} else {

						++MIN_SBP3;
					}
				} else if (second == 1) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP4;
					} else {
						++MIN_SBP4;
					}
				}

				else if (second >= 2 && second < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP5;
					} else {
						++MIN_SBP5;
					}
				}
			}

			else if (PillarType.UB1P == pillarType && !p_pillar2_is_a_border) {

				if (second == 0) {
					if (morpionType == MAX_PLAYER) {
						++MAX_UBP3;
					} else {

						++MIN_UBP3;
					}
				} else if (second == 1) {
					if (morpionType == MAX_PLAYER) {
						++MAX_UBP4;
					} else {
						++MIN_UBP4;
					}
				}

				else if (second >= 2 && second < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_UBP5;
					} else {
						++MIN_UBP5;
					}
				}
			}

			else if (PillarType.SB1P == pillarType && p_pillar2_is_a_border) {
				if (second == 0) {
					if (morpionType == MAX_PLAYER) {
						++MAX_BP3;
					} else {

						++MIN_BP3;
					}
				} else if (second == 1) {
					if (morpionType == MAX_PLAYER) {
						++MAX_BP4;
					} else {
						++MIN_BP4;
					}
				}

				else if (second >= 2 && second < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_BP5;
					} else {
						++MIN_BP5;
					}
				}
			}

			else if (PillarType.SB1P == pillarType && !p_pillar2_is_a_border) {
				if (second == 0) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP3;
					} else {

						++MIN_SBP3;
					}
				} else if (second == 1) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP4;
					} else {
						++MIN_SBP5;
					}
				} else if (second >= 2 && second < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP5;
					} else {
						++MIN_SBP5;
					}
				}
			}

			else if (PillarType.SB2P == pillarType && p_pillar2_is_a_border) {
				if (second == 0) {
					if (morpionType == MAX_PLAYER) {
						++MAX_BP4;
					} else {
						++MIN_BP4;
					}
				} else if (second >= 1 && second < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_BP5;
					} else {
						++MIN_BP5;
					}
				}
			}

			else if (PillarType.SB2P == pillarType && !p_pillar2_is_a_border) {
				if (second == 0) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP4;
					} else {
						++MIN_SBP4;
					}
				} else if (second >= 1 && second < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP5;
					} else {
						++MIN_SBP5;
					}
				}
			}

			else if ((PillarType.SB3P == pillarType || PillarType.SB4P == pillarType) && p_pillar2_is_a_border) {

				if (second >= 0 && second < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_BP5;
					} else {
						++MIN_BP5;
					}
				}
			}

			else if ((PillarType.SB3P == pillarType || PillarType.SB4P == pillarType) && !p_pillar2_is_a_border) {

				if (second >= 0 && second < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP5;
					} else {
						++MIN_SBP5;
					}
				}
			}

			else if (PillarType.UB2P == pillarType && p_pillar2_is_a_border) {
				if (second == 0) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP4;
					} else {
						++MIN_SBP4;
					}
				} else if (second >= 1 && second < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP5;
					} else {
						++MIN_SBP5;
					}
				}
			}

			else if (PillarType.UB2P == pillarType && !p_pillar2_is_a_border) {

				if (second == 0) {
					// System.out.println("Second");
					if (morpionType == MAX_PLAYER) {
						++MAX_UBP4;
					} else {
						++MIN_UBP4;
					}
				} else if (second >= 1 && second < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_UBP5;
					} else {

						++MIN_UBP5;
					}
				}
			}

			else if ((PillarType.UB3P == pillarType || PillarType.UB4P == pillarType) && p_pillar2_is_a_border) {

				if (second > 0 && second < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_SBP5;
					} else {
						++MIN_SBP5;
					}
				}
			}

			else if ((PillarType.UB3P == pillarType || PillarType.UB4P == pillarType) && !p_pillar2_is_a_border) {

				if (second > 0 && second < 3) {
					if (morpionType == MAX_PLAYER) {
						++MAX_UBP5;
					} else {
						++MIN_UBP5;
					}
				}
			}

		}

	}

	/*********************************************************************************************************************************/
	public void clearPillars() {
		MAX_B2P = 0;
		MAX_SB2P = 0;
		MAX_UB2P = 0;

		MAX_B3P = 0;
		MAX_SB3P = 0;
		MAX_UB3P = 0;

		MAX_B4P = 0;
		MAX_SB4P = 0;
		MAX_UB4P = 0;

		MAX_BP3 = 0;
		MAX_SBP3 = 0;
		MAX_UBP3 = 0;

		MAX_BP4 = 0;
		MAX_SBP4 = 0;
		MAX_UBP4 = 0;

		MAX_BP5 = 0;
		MAX_SBP5 = 0;
		MAX_UBP5 = 0;
		MIN_B2P = 0;
		MIN_SB2P = 0;
		MIN_UB2P = 0;

		MIN_B3P = 0;
		MIN_SB3P = 0;
		MIN_UB3P = 0;

		MIN_B4P = 0;
		MIN_SB4P = 0;
		MIN_UB4P = 0;

		MIN_BP3 = 0;
		MIN_SBP3 = 0;
		MIN_UBP3 = 0;

		MIN_BP4 = 0;
		MIN_SBP4 = 0;
		MIN_UBP4 = 0;

		MIN_BP5 = 0;
		MIN_SBP5 = 0;
		MIN_UBP5 = 0;

	}

	/********************************************************************************************************************************/
	public void calculateCloseness() {
		MAX_PS = (MAX_B2P + MAX_SB2P + MAX_UB2P) + (MAX_B3P + MAX_SB3P + MAX_UB3P) * 2
				+ (MAX_B4P + MAX_SB4P + MAX_UB4P) * 3;

		MIN_PS = (MIN_B2P + MAX_SB2P + MIN_UB2P) + (MIN_B3P + MIN_SB3P + MIN_UB3P) * 2
				+ (MIN_B4P + MIN_SB4P + MIN_UB4P) * 3;
	}

	/********************************************************************************************************************************/
	public int heuristic2(int depth) {

		// MAX_PLAYER PLAYS AFTER
		if (depth % 2 == 0) {

			if (board.getGameOverStatus()) {

				return Integer.MIN_VALUE;
			}

			clearPillars();
			evaluateBoard();
			// calculateCloseness();

			if (MAX_UB4P > 0 || MAX_SB4P > 0 || MAX_UBP5 > 0 || MAX_SBP5 > 0 || MAX_BP5 > 0) {// MIN GUARANTEED TO
				// LOSE IN 1

				return Integer.MAX_VALUE - 30000000 + 1000000;

			}

			else if (MIN_UB4P > 0 || MIN_SB4P > 1 || MIN_UBP5 > 1 || MIN_SBP5 > 1 || MIN_BP5 > 1
					|| (MIN_SB4P == 1 && MIN_UBP4 == 1) || (MIN_SB4P == 1 && MIN_UB3P == 1)
					|| (MIN_UBP5 == 1 && MIN_UBP4 == 1) || (MIN_UBP5 == 1 && MIN_UB3P == 1)
					|| (MIN_SBP5 == 1 && MIN_UBP4 == 1) || (MIN_SBP5 == 1 && MIN_UB3P == 1)
					|| (MIN_BP5 == 1 && MIN_UBP4 == 1) || (MIN_BP5 == 1 && MIN_UB3P == 1)
					|| (MIN_SB4P == 1 && MIN_UBP5 == 1) || (MIN_SB4P == 1 && MIN_SBP5 == 1)
					|| (MIN_SB4P == 1 && MIN_BP5 == 1)) {// MAX GUARANTEED TO LOSE IN 1

				return Integer.MAX_VALUE - 2000000000;
			}

			else if (((MAX_UB3P > 0 || MAX_UBP4 > 0) && (MIN_UBP5 <= 1 && MIN_SBP5 == 0 && MIN_BP5 == 0))
					|| ((MAX_UB3P > 0 || MAX_UBP4 > 0) && (MIN_UBP5 == 0 && MIN_SBP5 <= 1 && MIN_BP5 == 0))
					|| ((MAX_UB3P > 0 || MAX_UBP4 > 0) && (MIN_UBP5 == 0 && MIN_SBP5 == 0 && MIN_BP5 <= 1))) {

				return Integer.MAX_VALUE - 40000000 + 1000000;

			}

			else if ((MIN_UB3P > 1 || MIN_UBP4 > 1 || (MIN_UB3P == 1 && MIN_UBP4 == 1))) {

				return Integer.MAX_VALUE - 1500000000 + 1000000;

			}

			else {

				return Integer.MAX_VALUE - 1000000000
						+ (300 * MAX_B4P + 50000 * MAX_SB3P + 128 * MAX_B3P + 49000 * MAX_SBP4 + 100 * MAX_BP4
								+ 48000 * MAX_UB2P + 1300 * MAX_SB2P + 4 * MAX_B2P + 43000 * MAX_UBP3 + 12000 * MAX_SBP3
								+ 2 * MAX_BP3)

						- (120000 * MIN_SB4P + 300 * MIN_B4P + 110000 * MIN_UBP5 + 11000 * MIN_SBP5 + 100000 * MIN_BP5
								+ 110000 * MIN_UB3P + 40000 * MIN_SB3P + 128 * MIN_B3P + 110000 * MIN_UBP4
								+ 30000 * MIN_SBP4 + 100 * MIN_BP4 + 45000 * MIN_UB2P + 1200 * MIN_SB2P + 4 * MIN_B2P
								+ 44000 * MIN_UBP3 + 1100 * MIN_SBP3 + 2 * MIN_BP3)

						+ 1000000;

			}

		}
		/*******************************************************/

		// MIN_PLAYER PLAYS AFTER
		else if (depth % 2 != 0) {

			if (board.getGameOverStatus()) {

				return Integer.MAX_VALUE;

			}

			clearPillars();
			evaluateBoard();
			// calculateCloseness();

			if (MIN_UB4P > 0 || MIN_SB4P > 0 || MIN_UBP5 > 0 || MIN_SBP5 > 0 || MIN_BP5 > 0) {// MAX GUARANTEED
																								// TO LOSE
				return Integer.MAX_VALUE - 2000000000;
			}

			else if (MAX_UB4P > 0 || MAX_SB4P > 1 || MAX_UBP5 > 1 || MAX_SBP5 > 1 || MAX_BP5 > 1
					|| (MAX_SB4P == 1 && MAX_UBP4 == 1) || (MAX_SB4P == 1 && MAX_UB3P == 1)
					|| (MAX_UBP5 == 1 && MAX_UBP4 == 1) || (MAX_UBP5 == 1 && MAX_UB3P == 1)
					|| (MAX_SBP5 == 1 && MAX_UBP4 == 1) || (MAX_SBP5 == 1 && MAX_UB3P == 1)
					|| (MAX_BP5 == 1 && MAX_UBP4 == 1) || (MAX_BP5 == 1 && MAX_UB3P == 1)
					|| (MAX_SB4P == 1 && MAX_UBP5 == 1) || (MAX_SB4P == 1 && MAX_SBP5 == 1)
					|| (MAX_SB4P == 1 && MAX_BP5 == 1)) {// MIN GUARANTEED TO LOSE

				return Integer.MAX_VALUE - 30000000;

			}

			else if (((MIN_UB3P > 0 || MIN_UBP4 > 0) && (MAX_UBP5 <= 1 && MAX_SBP5 == 0 && MAX_BP5 == 0))
					|| ((MIN_UB3P > 0 || MIN_UBP4 > 0) && (MAX_UBP5 == 0 && MAX_SBP5 <= 1 && MAX_BP5 == 0))
					|| ((MIN_UB3P > 0 || MIN_UBP4 > 0) && (MAX_UBP5 == 0 && MAX_SBP5 == 0 && MAX_BP5 <= 1))) {

				return Integer.MAX_VALUE - 1500000000;

			}

			else if ((MAX_UB3P > 1 || MAX_UBP4 > 1 || (MAX_UB3P == 1 && MAX_UBP4 == 1))) {

				return Integer.MAX_VALUE - 40000000;
			}

			else {

				return Integer.MAX_VALUE - 1000000000
						+ (120000 * MAX_SB4P + 162 * MAX_B4P + 1400000 * MAX_UBP5 + 1100000 * MAX_SBP5
								+ 100000 * MAX_BP5 + 110000 * MAX_UB3P + 6184 * MAX_SB3P + 54 * MAX_B3P
								+ 100000 * MAX_UBP4 + 6000 * MAX_SBP4 + 18 * MAX_BP4 + 3200 * MAX_UB2P + 1500 * MAX_SB2P
								+ 6 * MAX_B2P + 3000 * MAX_UBP3 + 1400 * MAX_SBP3 + 2 * MAX_BP3)

						- (162 * MIN_B4P + 5184 * MIN_SB3P + 54 * MIN_B3P + 5000 * MIN_SBP4 + 18 * MIN_BP4
								+ 6000 * MIN_UB2P + 1100 * MIN_SB2P + 6 * MIN_B2P + 5000 * MIN_UBP3 + 1000 * MIN_SBP3
								+ 2 * MIN_BP3);

			}

		}

		return 0;

	}

}
