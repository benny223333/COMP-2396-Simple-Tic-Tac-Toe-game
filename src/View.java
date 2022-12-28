
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This is the code for pop up message window
 * @author benny
 *
 */
public class View {
	/**
	 * This will pop different message window depend on the param mode
	 * @param mode
	 */
	public static void Pop_up(int mode) {
		JFrame Pop_up = new JFrame();
		JPanel MainPanel = new JPanel();
		MainPanel.setLayout(new GridLayout(2,1));
		JPanel Button = new JPanel();
		JLabel Words;
		switch(mode) {
		case (1):
			Words = new JLabel("Congratulations. You win.", JLabel.CENTER);	
			Pop_up.setSize(300, 100);
			break;
		case (2):
			Words = new JLabel("You lose.", JLabel.CENTER);
			Pop_up.setSize(300, 100);
			break;
		case (3):
			Words = new JLabel("Draw.", JLabel.CENTER);
			Pop_up.setSize(300, 100);
			break;
		case (4):
			Words = new JLabel("Game Ends. One of the players left.", JLabel.CENTER);
			Pop_up.setSize(300, 100);
			break;
		case (5):
			Words = new JLabel("<html>Some information about the game:<br/>"
					+ "Criteria for a valid move:<br/>"
					+ "-The move is not occupied by any mark.<br/>"
					+ "-The move is made in the player's turn.<br/>"
					+ "-The move is made within the 3x3 board.<br/>"
					+ "The game will continue and switch among the opposite player until it reaches either one of the following conditions:<br/>"
					+ "-Player 1 wins.<br/>"
					+ "-Player 2 wins.<br/>"
					+ "-Draw.<br/><html>");
			Pop_up.setSize(700, 400);
			break;
			default:
				Words = new JLabel("Invalid situation");
				break;
		}
		JButton OK = new JButton("OK");
		OK.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				Pop_up.dispose();
			}
		});
		
		MainPanel.add(Words);
		MainPanel.add(Button);
		Button.add(OK);
		Pop_up.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Pop_up.getContentPane().add(MainPanel);
		Pop_up.setTitle("Message");
		Pop_up.setVisible(true);
	}
}