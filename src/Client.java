
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This is a Tic Tac Toe Client  
 * @author Benny
 * @version 1.0.0
 */

public class Client {

    private JFrame frame = new JFrame("Tic Tac Toe");
    private JPanel main_panel;
    private JPanel info_bar;
    private JPanel board_GUI;
    private JPanel name_input;
    
    private JMenuBar menu_bar;
    private JMenu control_tab;
    private JMenu help_tab;
    private JMenuItem menu_Exit;
    private JMenuItem menu_Instruction;
    
    private JTextField Name;
    private JButton submit;
    private JLabel info;
    private ImageIcon icon;
    private ImageIcon opponent_icon;

    private box[] board = new box[9];
    private box current;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    
    private boolean game_start_flag = false;    
    /**
     * This is the Client constructor
     * @throws Exception
     */
    public Client() throws Exception {

        socket = new Socket("127.0.0.1", 5000);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
    	
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
    	
    	main_panel = new JPanel();
    	
    	menu_bar = new JMenuBar();
    	control_tab = new JMenu("Control");
    	help_tab = new JMenu("Help");
    	menu_Exit = new JMenuItem("Exit");
    	menu_Exit.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent x) {
    			System.exit(0);
    		}
    	});
    	menu_Instruction = new JMenuItem("Instruction");
    	menu_Instruction.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent x) {
    			View.Pop_up(5);
    		}
    	});
    	
    	control_tab.add(menu_Exit);
    	help_tab.add(menu_Instruction);
    	menu_bar.add(control_tab);
    	menu_bar.add(help_tab);
    	
    	info_bar = new JPanel();
    	info = new JLabel("Enter your player name...");
    	info_bar.add(info);

    	board_GUI = new JPanel();
    	board_GUI.setPreferredSize(new Dimension(400, 400));
    	board_GUI.setLayout(new GridLayout(3,3));
    	for (int i = 0; i < 9; i++) {
    		final int a = i;
    		board[i] = new box();
    		board[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
    		board[i].setBackground(Color.WHITE);
    		board[i].addMouseListener(new MouseAdapter() {
    			public void mousePressed(MouseEvent e) {
    				if (game_start_flag == true) {
    					current = board[a];
    					System.out.println("Send command to server make_move " + a);
    					writer.println("make_move " + a);
    				}
    			}
    		});
    		board_GUI.add(board[i]);
    	}
    	
    	
    	name_input = new JPanel();
    	Name = new JTextField(30);
    	submit = new JButton("Submit");
    	submit.addActionListener(new ActionListener() {
    		public void actionPerformed (ActionEvent e) {
    			info.setText("WELCOME " + Name.getText());
    			frame.setTitle("Tic Tac Toe - Player: " + Name.getText());
    			writer.println("name_typed");
    			submit.setEnabled(false);
    		}
    	});
    	name_input.add(Name);
    	name_input.add(submit);
    	main_panel.setLayout(new BorderLayout());
    	main_panel.add(info, BorderLayout.PAGE_START);
    	main_panel.add(board_GUI, BorderLayout.CENTER);
    	main_panel.add(name_input,BorderLayout.PAGE_END);
    	frame.setJMenuBar(menu_bar);
    	frame.getContentPane().add (main_panel);
    	frame.setSize(500, 500);
    	frame.setVisible(true);
    }

   /**
    * This is the play method which handles commands from 
    * @throws Exception
    */
    public void play() throws Exception {
        String command;
        try {
            command = reader.readLine();
            System.out.println("Command received from server is " + command);
            if (command.startsWith("game_start ")) {
                char name = command.charAt(11);
                if (name == 'X') {
                	icon = new ImageIcon("pic/X.png");
                	opponent_icon = new ImageIcon("pic/O.png");
                }
                else if (name == 'O'){
                	icon = new ImageIcon("pic/O.png");
                	opponent_icon = new ImageIcon("pic/X.png");
                }
                frame.setTitle("Tic Tac Toe - Player: " + name);
            }
            while (true) {
                command = reader.readLine();
                System.out.println("The command received from server is " + command);
                if (command.startsWith("valid")) {
                    current.set_icon(icon);
                    current.repaint();
                    info.setText("Valid move, wait for your opponent.");
                } else if (command.startsWith("opponent_moved")) {
                    int box = Integer.parseInt(command.substring(15));
                    board[box].set_icon(opponent_icon);
                    board[box].repaint();
                    System.out.println("board[" + box + "] is repainted");
                    info.setText("Your opponent has moved, now is your turn");
                } else if (command.startsWith("win")) {
                    View.Pop_up(1);
                    break;
                } else if (command.startsWith("lose")) {
                	View.Pop_up(2);
                    break;
                } else if (command.startsWith("tie")) {
                	View.Pop_up(3);
                    break;
                } else if (command.startsWith("player_left")) {
                    View.Pop_up(4);
                    break;
                } else if (command.startsWith("all_player_ready")){
        			game_start_flag = true;
                }
            }
        }
        finally {
            writer.println("quit");
            socket.close();
        }
    }

    /**
     * This is the box class which is used to represent each box in the board
     * @author Benny
     *
     */
    static class box extends JPanel {
        JLabel label = new JLabel((Icon)null);

        public box() {
            setBackground(Color.white);
            add(label);
        }

        public void set_icon(Icon icon) {
            label.setIcon(icon);
        }
    }

    
    /**
     * The main method
     * @param args
     *
     */
    public static void main(String[] args) { 
    	try {
    		Client client = new Client();
    		client.play();
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    }
}