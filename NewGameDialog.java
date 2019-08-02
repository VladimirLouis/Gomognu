package gomoku;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author vladimirlouis
 */
public class NewGameDialog extends JDialog implements ActionListener {
	JPanel panel;
	String[] gameChoices = new String[3];
	// private static final int GAMEMODE=0;
	private static final int PLAYAS = 0;
	private static final int PLAYAGAINST = 1;

	// BUTTONGROUPS
	// ButtonGroup gameMode;
	ButtonGroup playAgainst;
	ButtonGroup playAs;

	// PLAY AS JRADIOBUTTONS
	JRadioButton playAsSquares;
	JRadioButton playAsDisks;

	// PLAY AGAINST
	JRadioButton two_player;
	JRadioButton playAgainstComp;

	NewGameDialogListener newGameListener;

	/*************************************************************************************************/
	NewGameDialog(Frame frame) {
		super(frame, "New Game");

		MigLayout panelLayout = new MigLayout();
		panel = new JPanel(panelLayout);
		panel.setBackground(new Color(139, 69, 19));

		// BUTTONGROUPS
		playAs = new ButtonGroup();
		playAgainst = new ButtonGroup();
		// gameMode=new ButtonGroup();

		// PLAY AS
		playAsSquares = new JRadioButton("Black");
		playAsSquares.setActionCommand("Squares");
		playAsDisks = new JRadioButton("White");
		playAsDisks.setActionCommand("Disks");

		playAs.add(playAsSquares);
		playAs.add(playAsDisks);

		// PLAY AGAINST
		two_player = new JRadioButton("2 Player");
		two_player.setActionCommand("2 Player");
		playAgainstComp = new JRadioButton("Computer");
		playAgainstComp.setActionCommand("Comp");

		playAgainst.add(two_player);
		playAgainst.add(playAgainstComp);

		// adding play as to panel
		JLabel playAsLabel = new JLabel("Play As");
		panel.add(playAsLabel);
		panel.add(playAsSquares);
		panel.add(playAsDisks, "wrap");

		// adding play against
		JLabel playAgainstLabel = new JLabel("Play Against");
		panel.add(playAgainstLabel);
		panel.add(two_player);
		panel.add(playAgainstComp, "wrap");

		// ADDING OK AND CANCEL BUTTONS
		JButton ok = new JButton("OK");
		ok.addActionListener(this);
		ok.setActionCommand("OK");

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		cancel.setActionCommand("Cancel");

		panel.add(ok, "split 2, cell 2 3");
		panel.add(cancel);

		add(panel);

		pack();
		setVisible(true);
	}

	/******************************************************************************/
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton) e.getSource();

		if (source.getActionCommand().equals("OK")) {

			// Play as CHOICE
			if (playAsDisks.isSelected()) {
				gameChoices[PLAYAS] = playAsDisks.getActionCommand();
			} else {
				gameChoices[PLAYAS] = playAsSquares.getActionCommand();
			}

			// Play against CHOICE
			if (two_player.isSelected()) {
				gameChoices[PLAYAGAINST] = two_player.getActionCommand();
			} else {
				gameChoices[PLAYAGAINST] = playAgainstComp.getActionCommand();
			}

			newGameListener.newGame(gameChoices);

		}

		// EXITS THE DIALOG
		dispose();
	}

	/*****************************************************************************/
	public void setNewGameDialogListener(NewGameDialogListener newGameListener) {
		this.newGameListener = newGameListener;
	}
}