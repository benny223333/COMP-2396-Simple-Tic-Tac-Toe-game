import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * This is the Tic Tac Toe Server
 * @author Benny
 *
 */
public class Server {

	/**
	 * This is the main method which creates a server
	 * @param args
	 * @throws Exception
	 */
    public static void main(String[] args) throws Exception {
        ServerSocket socket = new ServerSocket(5000);
        System.out.println("Server is Running...");
        try {
            while (true) {
                game game = new game();
                game.player player_X = game.new player(socket.accept(), 'X');
                game.player player_O = game.new player(socket.accept(), 'O');
                player_X.opponent(player_O);
                player_O.opponent(player_X);
                game.current = player_X;
                player_X.start();
                player_O.start();
            }
        } finally {
            socket.close();
        }
    }
}

/**
 * 
 * this is the actual Tic Tac Toe part, which records the board for determining win, lose or tie
 * @author benny
 *
 */
class game {
    private player[] board = {null, null, null, null, null, null, null, null, null};

    player current;
    
    private int player_ready_count = 0;
    
    /**
     * This checks all winning condition, excluding null situation
     * @return
     */

    public boolean check_win() {
        return
            (board[0] != null && board[0] == board[1] && board[0] == board[2])
          ||(board[3] != null && board[3] == board[4] && board[3] == board[5])
          ||(board[6] != null && board[6] == board[7] && board[6] == board[8])
          ||(board[0] != null && board[0] == board[3] && board[0] == board[6])
          ||(board[1] != null && board[1] == board[4] && board[1] == board[7])
          ||(board[2] != null && board[2] == board[5] && board[2] == board[8])
          ||(board[0] != null && board[0] == board[4] && board[0] == board[8])
          ||(board[2] != null && board[2] == board[4] && board[2] == board[6]);
    }

    /**
     * Determine of the board is filled up or not
     * @return
     */
    public boolean tie() {
        for (int i = 0; i < 9; i++) {
            if (board[i] == null) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Determine if the move made is valid or not, if it is, mark the move on the board and call opponent_move
     * @param box
     * @param player
     * @return
     */
    public synchronized boolean valid_move(int box, player player) {
        if (player == current && board[box] == null) {
            board[box] = current;
            current = current.opponent;
            current.opponent_move(box);
            return true;
        }
        return false;
    }
    
    /**
     * This is the player class, which represent a player in the game
     * @author Benny
     *
     */
    class player extends Thread {
    	char name;
        player opponent;
        Socket socket;
        BufferedReader reader;
        PrintWriter writer;
        
        /**
         * This is the player constructor
         * @param socket
         * @param name
         */
        public player(Socket socket, char name) {
            this.socket = socket;
            this.name = name;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);
                System.out.println("game_start " + name);
                writer.println("game_start " + name);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        /**
         * This sets the opponent of this player to the param opponent
         * @param opponent
         */
        public void opponent(player opponent) {
            this.opponent = opponent;
        }
        
        /**
         * This mark the opponent's move on the player board, also checks for lose and tie situation
         * @param box
         */
        public void opponent_move(int box) {
        	writer.println("opponent_moved " + box);
        	if (check_win()) {
        		System.out.println("lose");
        		writer.println("lose");
        	}
        	else if (tie()) {
        		System.out.println("tie");
        		writer.println("tie");
        	}
        }
        
        /**
         * This is the run method which handles command from clients
         */
        public void run() {
            try {
                // Server message when every player is connected.
            	writer.println("All players connected");
            	System.out.println("All players connected");
                String command;
                while ((command = reader.readLine()) != null) {
                	System.out.println("start reading command from client");
                    if (command.startsWith("make_move")) {
                        int box = Integer.parseInt(command.substring(10));
                        if (valid_move(box, this)) {
                        	System.out.println("valid");
                            writer.println("valid");
                            if (check_win()) {
                            	System.out.println("win");
                            	writer.println("win");
                            }
                            else if (tie()) {
                            	System.out.println("tie");
                                writer.println("tie");
                            }
                        } else {
                        	System.out.println(command);
                            writer.println("What is this message?");
                        }
                    } else if (command.startsWith("quit")) {
                    	System.out.println("Command to client: player_left");
                    	opponent.writer.println("player_left");
                        return;
                    } else if (command.startsWith("name_typed")) {
                    	player_ready_count += 1;
                    	if (player_ready_count == 2) {
                    		System.out.println("player_ready_count is 2");
                    		writer.println("all_player_ready");
                    		opponent.writer.println("all_player_ready");
                    	}
                    }
                }
            } catch (SocketException sx) {
            	System.out.println("Command to client: player_left");
            	opponent.writer.println("player_left");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            finally {
                try {
                	socket.close();
                	} catch (IOException ex) {
                		ex.printStackTrace();
                	}
            }
        }
    }
}