package gomoku;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import gomoku.Morpion;
import gomoku.MorpionType;
import gomoku.MovesMadeListener;

import net.miginfocom.swing.MigLayout;

/*********************************************************************************************************/
public class Gomoku extends JFrame implements ActionListener {
	JPanel boardPanel;
	JMenuBar menuBar;
	JMenu file;
	JMenu edit;
	JMenu game;
	JMenu info;

//File menu item
	JMenuItem exit;

//GAME MENU ITEMS
	JMenuItem newGame;
	JCheckBoxMenuItem playAsSquares;
	JCheckBoxMenuItem playAsDisks;
	JCheckBoxMenuItem two_player;
	JCheckBoxMenuItem playAgainstComp;

	NewGameDialog dialog;
//private static final int GAMEMODE = 0;
	private static final int PLAYAS = 0;
	private static final int PLAYAGAINST = 1;

	GomokuBoard board;
	SwingWorker worker;

	/***********************************************************************************************************/
	public Gomoku() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {

		super("Gomognu");
		init();

	}

	/*****************************************************************************************************/
	private void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {

// SETTING JFRAME BACKGROUND color
		getContentPane().setBackground(new Color(139, 69, 19));

//creating miglayout and setting JFrame's manager to miglayout
		MigLayout jFrameLayout = new MigLayout();
		setLayout(jFrameLayout);

//INITIALIZING THE BOARDPANEL
		boardPanel = new JPanel();
		boardPanel.setBackground(new Color(139, 69, 19));

// CREATING THE BOARD
		board = new GomokuBoard();
//board.setEnabled(false);//REMEMBER TO DISABLE BOARD BOARD BEFORE GAME HAS BEGUN

//SETLISTENERS
		setListeners();

// ADDING THE BOARD TO BOARDPANEL
		boardPanel.add(board);

//ADDING BOARDPANEL TO FRAME
		add(boardPanel, "wrap, cell 0 1, gapLeft 5");

//CREATING MENUBAR AND ITEM
		createMenuBarAndItem();

//ADDING MENUBAR TO FRAME
		this.setJMenuBar(menuBar);

		UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		SwingUtilities.updateComponentTreeUI(this);
		pack();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);

	}

	/*******************************************************************************************************/
	private void createMenuBarAndItem() {
		// INITIALIZE THE MENUBAR
		menuBar = new JMenuBar();

		// CREATING THE FILE MENU
		createFileItem();
		createEditItem();
		createGameItem();
		// createInfoItem();

		menuBar.add(file);
		menuBar.add(edit);
		menuBar.add(game);
		// menuBar.add(info);

	}

	/***************************************************************************/
	private void createFileItem() {
		file = new JMenu("File");

		// SUBMENUS FOR FILE
		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(this);
		open.setActionCommand("Open");

		// save
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(this);
		save.setActionCommand("Save");

		// exit
		exit = new JMenuItem("Exit");
		exit.addActionListener(this);
		exit.setActionCommand("Exit");

		// adding submenus to file
		file.add(open);
		file.add(save);
		file.add(exit);
	}

	/***************************************************************************/
	private void createEditItem() {
		edit = new JMenu("Edit");

		// SUBMENUS FOR EDIT
		// undo submenu
		JMenuItem undo = new JMenuItem("Undo");
		undo.addActionListener(this);
		undo.setActionCommand("Undo");

		// redo submenu
		JMenuItem redo = new JMenuItem("Redo");
		redo.addActionListener(this);
		redo.setActionCommand("Redo");

		// setEditMenuListeners();

		// adding submenus
		edit.add(undo);
		edit.add(redo);

	}

	/***************************************************************************/
	private void createGameItem() {
		game = new JMenu("Game");

		// SUBMENUS FOR GAME DETAILS

		// NEWGAME
		newGame = new JMenuItem("New Game");

		// PLAYASSQUARES
		playAsSquares = new JCheckBoxMenuItem("Play as Black");
		playAsDisks = new JCheckBoxMenuItem("Play as White");
		two_player = new JCheckBoxMenuItem("2 Player");
		playAgainstComp = new JCheckBoxMenuItem("Computer");

		setGameItemsListeners();

		game.add(newGame);
		game.add(playAsSquares);
		game.add(playAsDisks);
		game.add(two_player);
		game.add(playAgainstComp);

	}

	/*******************************************************************************************************/
	private void setListeners() {
		// GETTING THE POINTS CLICKED BY THE PLAYER
		board.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent event) {

				int x = event.getX();
				int y = event.getY();

				Point pos = new Point(x, y);

				board.coordsClicked(pos);

			}
		});

		// MOVESMADELISTENER
		board.setMovesMadeListener(new MovesMadeListener() {
			@Override
			public void movesMade() {

				// playMoveSound();

				if (!board.getGameoVerStatus() && playAgainstComp.isSelected() && playAsSquares.isSelected()
						&& board.getMorpionTurn() == MorpionType.DISK) {

					board.setEnabled(false);
					worker = new SwingWorker<Point, Void>() {
						@Override
						protected Point doInBackground() throws Exception {

							return engineMove();
						}

						@Override
						public void done() {
							if(worker!=null && !worker.isCancelled() ) {
							try {
								if (board.getMorpionTurn() == MorpionType.DISK) {
									board.setEnabled(true);
									board.addMorpion(new Morpion(MorpionType.DISK, get()));
									board.repaint();
								} else {
									board.setEnabled(true);
									board.addMorpion(new Morpion(MorpionType.SQUARE, get()));
									board.repaint();
								}
							} catch (InterruptedException | ExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
							
						}

					};

					worker.execute();

				}

				else if (!board.getGameoVerStatus() && playAgainstComp.isSelected() && playAsDisks.isSelected()
						&& board.getMorpionTurn() == MorpionType.SQUARE) {

					board.setEnabled(false);
					worker = new SwingWorker<Point, Void>() {
						@Override
						protected Point doInBackground() throws Exception {

							return engineMove();
						}

						@Override
						public void done() {
							if(worker!=null && !worker.isCancelled() ) {
							try {
								if (board.getMorpionTurn() == MorpionType.DISK) {
									board.setEnabled(true);
									board.addMorpion(new Morpion(MorpionType.DISK, get()));
									board.repaint();
								} else {
									board.setEnabled(true);
									board.addMorpion(new Morpion(MorpionType.SQUARE, get()));
									board.repaint();
								}
							} catch (InterruptedException | ExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
						
						}

					};
					worker.execute();

				}

				else if (two_player.isSelected()) {
					board.repaint();
				}
			}

		});

		// GAMEOVERLISTENER
		board.setGameOverListener(new GameOverListener() {
			@Override
			public void gameOver() {
				// castOverSound();

				;
				board.setEnabled(false);
				JOptionPane.showMessageDialog(Gomoku.this, "Game Over! \n");
			}

		});

	}

	/********************************************************************************************************/
	private void setGameItemsListeners() {
		// NEWGAME
		newGame.addActionListener(this);
		newGame.setActionCommand("NewGame");

		// PLAYASSQUARES
		playAsSquares.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {

				if (playAsSquares.isSelected()) {
					playAsDisks.setSelected(false);
					board.setPlayAsDisks(false);
					board.setPlayAsSquares(true);

					if (!board.getGameOverStatus() && playAgainstComp.isSelected()
							&& board.getMorpionTurn() == MorpionType.DISK) {

						initializeEngineWorker(MorpionType.DISK);

					}

				}

			}

		});

		// PLAYASDISKS
		playAsDisks.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (playAsDisks.isSelected()) {
					playAsSquares.setSelected(false);
					board.setPlayAsDisks(true);
					board.setPlayAsSquares(false);

					if (!board.getGameOverStatus() && playAgainstComp.isSelected()
							&& board.getMorpionTurn() == MorpionType.SQUARE) {

						initializeEngineWorker(MorpionType.SQUARE);

					}

				}
			}

		});

		// 2PLAYER
		two_player.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {

				if (two_player.isSelected()) {
					playAgainstComp.setSelected(false);
					board.setTwo_player(true);
					board.setPlayAgainstComp(false);

				} else {
					playAgainstComp.setSelected(true);
					board.setTwo_player(false);
					board.setPlayAgainstComp(true);
				}

			}

		});

		// PLAYAGAINSTCOMP
		playAgainstComp.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (playAgainstComp.isSelected()) {
					two_player.setSelected(false);
					board.setTwo_player(false);
					board.setPlayAgainstComp(true);

					if (!board.getGameOverStatus() && playAsSquares.isSelected()
							&& board.getMorpionTurn() == MorpionType.DISK) {

						initializeEngineWorker(MorpionType.DISK);

					} else if (!board.getGameOverStatus() && playAsDisks.isSelected()
							&& board.getMorpionTurn() == MorpionType.SQUARE) {

						initializeEngineWorker(MorpionType.SQUARE);
					}

				} else {
					two_player.setSelected(true);
					board.setTwo_player(true);
					board.setPlayAgainstComp(false);
				}
			}

		});

	}

	/*******************************************************************************************************/
	private Point engineMove() {

		GomokuBoard copyBoard = board.copyBoard();
		GomokuEngine level2 = new Level2(copyBoard);

		return level2.move();

	}

	/**************************************************************************************************/
	private void cancelEngine() {
		if (worker != null && !worker.isCancelled() && !worker.isDone()) {
			worker.cancel(true);
		}
	}
	/**************************************************************************************************/
	private void initializeEngineWorker(MorpionType morpionType) {
		worker = new SwingWorker<Point, Void>() {
			@Override
			protected Point doInBackground() throws Exception {

				return engineMove();
			}

			@Override
			public void done() {
				if( worker!=null && !worker.isCancelled()) {
				try {
					
					board.setEnabled(true);
					if (morpionType == MorpionType.DISK) {
						board.addMorpion(new Morpion(MorpionType.DISK, get()));
					} else {
						board.addMorpion(new Morpion(MorpionType.SQUARE, get()));
					}

					board.repaint();
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				}

			}
			

		};

		worker.execute();
	}
/*************************************************************************************************/
	// TODO code application logic here
	public static void main(String args[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new Gomoku().setVisible(true);
				} catch (ClassNotFoundException ex) {
					Logger.getLogger(Gomoku.class.getName()).log(Level.SEVERE, null, ex);
				} catch (InstantiationException ex) {
					Logger.getLogger(Gomoku.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IllegalAccessException ex) {
					Logger.getLogger(Gomoku.class.getName()).log(Level.SEVERE, null, ex);
				} catch (UnsupportedLookAndFeelException ex) {
					Logger.getLogger(Gomoku.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});
	}

	/********************************************************************************************************/
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JMenuItem source = (JMenuItem) (e.getSource());

		// UNDO
		if (source.getActionCommand().equals("Undo")) {

			cancelEngine();
			
			board.undoMove();
			board.setEnabled(true);
			board.repaint();
		}

		// REDO
		if (source.getActionCommand().equals("Redo")) {
			board.redo();
			board.repaint();
		}

		// NEW GAME
		if (source.getActionCommand().equals("NewGame")) {
			
			//cancel engine
			cancelEngine();

			dialog = new NewGameDialog(this);
			dialog.setLocationRelativeTo(this);
			dialog.setNewGameDialogListener(new NewGameDialogListener() {

				@Override
				public void newGame(String[] choices) {

					board.newGame();

					if (choices[PLAYAGAINST].equals("2 Player")) {
						two_player.setSelected(true);
						playAgainstComp.setSelected(false);
						board.setPlayAgainstComp(false);
						board.setTwo_player(true);
					} else {
						two_player.setSelected(false);
						playAgainstComp.setSelected(true);
						board.setPlayAgainstComp(true);
						board.setTwo_player(false);
					}

					if (choices[PLAYAS].equals("Squares")) {
						playAsSquares.setSelected(true);
						playAsDisks.setSelected(false);
						board.setPlayAsSquares(true);
						board.setPlayAsDisks(false);

					} else {
						playAsSquares.setSelected(false);
						playAsDisks.setSelected(true);
						board.setPlayAsSquares(false);
						board.setPlayAsDisks(true);
					}

					board.repaint();
				}
			});

		}

		// SAVE
		if (source.getActionCommand().equals("Save")) {
			JFileChooser fileChosen = new JFileChooser(".");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Serialized files", "Ser");
			fileChosen.setFileFilter(filter);

			fileChosen.showSaveDialog(this);

			File fileSelected = fileChosen.getSelectedFile();

			FileOutputStream fos = null;
			ObjectOutputStream out = null;

			try {
				// saving board state
				fos = new FileOutputStream(fileSelected);
				out = new ObjectOutputStream(fos);

				out.writeObject(board);
				out.close();
				fos.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}

		// OPEN
		if (source.getActionCommand().equals("Open")) {
			JFileChooser fileChosen = new JFileChooser(".");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Serialized files", "ser");
			fileChosen.setFileFilter(filter);

			int returnVal = fileChosen.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File fileSelected = fileChosen.getSelectedFile();

				FileInputStream fis = null;
				ObjectInputStream in = null;

				try {
					fis = new FileInputStream(fileSelected);
					in = new ObjectInputStream(fis);

					GomokuBoard board2 = (GomokuBoard) in.readObject();

					in.close();

					board.loadGame(board2.getListOfMorpions(), board2.getRedoList(), board2.getGameOverStatus(),
							board2.getMorpionTurn());

					board.setPlayAsSquares(board2.getPlayAsSquares());
					playAsSquares.setSelected(board2.getPlayAsSquares());
					board.setPlayAsDisks(board2.getPlayAsDisks());
					playAsDisks.setSelected(board2.getPlayAsDisks());

					board.setPlayAgainstComp(board2.getPlayAgainstComp());
					playAgainstComp.setSelected(board2.getPlayAgainstComp());
					board.setTwo_player(board2.getTwo_player());
					two_player.setSelected(board2.getTwo_player());

					board.repaint();

				} catch (FileNotFoundException ex) {
					Logger.getLogger(Gomoku.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IOException ex) {
					Logger.getLogger(Gomoku.class.getName()).log(Level.SEVERE, null, ex);
				} catch (ClassNotFoundException ex) {
					Logger.getLogger(Gomoku.class.getName()).log(Level.SEVERE, null, ex);
				}

			}

		}

		// EXIT
		if (source.getActionCommand().equals("Exit")) {
			processEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}

	}
	/***********************************************************************************************************/
}
