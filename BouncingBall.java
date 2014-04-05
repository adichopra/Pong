import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class BouncingBall extends JPanel {
	
	static int WIDTH;
	static int HEIGHT;
	double ball_x;
	double ball_y;
	double dx;
	double dy;
	int x_size;
	int y_size;
	int paddle_width;
	int paddle_height;
	int paddle_speed;
	int player_x;
	double player_y;
	int opponent_x;
	double opponent_y;
	static int player1_moving;
	static int player2_moving;
	int player_score;
	int opponent_score;
	double multiplier;
	double increment;
	
	public BouncingBall() {
		WIDTH = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		HEIGHT = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		ball_x = WIDTH / 2;
		ball_y = HEIGHT / 2;
//		int angle = (int) (Math.random() * 121) - 60;
		int direction = (int) (Math.random() * 2) * 2 - 1;
		dx = 4 * direction;
		dy = 0;//dx * Math.tan(Math.toRadians(angle));
		x_size = 30;
		y_size = 30;
		paddle_width = x_size;
		paddle_height = 150;
		paddle_speed = 5;
		player_x = 0;
		player_y = HEIGHT / 2 - paddle_height / 2;
		opponent_x = WIDTH - player_x - paddle_width;
		opponent_y = HEIGHT / 2 - paddle_height / 2;
		player1_moving = 0;
		player2_moving = 0;
		player_score = 0;
		opponent_score = 0;
		multiplier = 0.001;
		increment = 1.105;
	}
	
	private void moveBall() {
		if (dx > WIDTH / 2) dx = WIDTH / 2;
		if (dy > HEIGHT / 2) dy = HEIGHT / 2;
		ball_x += dx;
		ball_y += dy;
		if (ball_x < 0) {
			reset();
			opponent_score++;
			System.out.println("Your score: " + player_score);
			System.out.println("AI score: " + opponent_score);
		}
		if (interval_overlap(new double[] {ball_x, ball_x + x_size}, new double[] {player_x, player_x + paddle_width}) && interval_overlap(new double[] {ball_y, (int) (ball_y + y_size)}, new double[] {player_y, player_y + paddle_height})) { //ball is not a point
			ball_x = player_x + paddle_width;
			dx *= increment;
			double rebound_angle = 45 * ((ball_y - (player_y  + paddle_height / 2)) / (paddle_height / 2));
			double speed = Math.pow(Math.pow(dy, 2) + Math.pow(dx, 2), 0.5);
			dx = speed * Math.cos(Math.toRadians(rebound_angle));
			dy = speed * Math.sin(Math.toRadians(rebound_angle));
		}
		if (ball_x + x_size >= WIDTH) {
			reset();
			player_score++;
			System.out.println("Your score: " + player_score);
			System.out.println("AI score: " + opponent_score);
	    }
		if (interval_overlap(new double[] {ball_x, ball_x + x_size}, new double[] {opponent_x, opponent_x + paddle_width}) && interval_overlap(new double[] {ball_y, (int) (ball_y + y_size)}, new double[] {opponent_y, opponent_y + paddle_height})) { //ball is not a point
			ball_x = opponent_x - paddle_width;
			dx *= increment;
			double rebound_angle = 45 * ((ball_y - (opponent_y  + paddle_height / 2)) / (paddle_height / 2));
			double speed = Math.pow(Math.pow(dy, 2) + Math.pow(dx, 2), 0.5);
			dx = -speed * Math.cos(Math.toRadians(rebound_angle));
			dy = -speed * Math.sin(Math.toRadians(rebound_angle));
		}
		if (ball_y < 0) {
		    ball_y = 0;
//		    dy -= increment;
		    dy = -dy;
		}
		if (ball_y + y_size >= HEIGHT) {
		    ball_y = HEIGHT - y_size;
//		    dy += increment;
		    dy = -dy;
		}
	}
	private boolean interval_overlap(double[] x1, double[] x2) {
		return x1[0] <= x2[1] && x2[0] <= x1[1];
	}
	private void reset() { 
		ball_x = WIDTH / 2;
		ball_y = HEIGHT / 2;
//		int angle = (int) (Math.random() * 121) - 60;
		int direction = (int) (Math.random() * 2) * 2 - 1;
		dx = 2 * direction;
		dy = 0;//dx * Math.tan(Math.toRadians(angle));
		player_x = 0;
		player_y = HEIGHT / 2 - paddle_height / 2;
		opponent_x = WIDTH - player_x - paddle_width;
		opponent_y = HEIGHT / 2 - paddle_height / 2;
	}
	private void movePlayer1() {
		if (player1_moving == -1 /*&& player_y > 0*/) player_y -= paddle_speed;
		else if (player1_moving == 1 /*&& player_y + paddle_height < HEIGHT*/) player_y += paddle_speed;
	}
	private void movePlayer2() {
		if (player2_moving == -1 && opponent_y > 0) opponent_y -= paddle_speed;
		else if (player2_moving == 1 && opponent_y + paddle_height < HEIGHT) opponent_y += paddle_speed;
	}
	private void moveAI1() {
		if (Math.abs(AI1()) > paddle_speed) player_y += AI1() < 0 ? -paddle_speed: paddle_speed; 
		else player_y += AI1();
	}
	private int AI1() {
		return (int) (ball_y + y_size - (player_y /*+ paddle_height / 2*/)) - 1;
	}
	private int AI2() {
		return (int) (ball_y - (opponent_y + paddle_height / 2));
	}
	private void moveAI2() {
		if (Math.abs(AI2()) > paddle_speed) opponent_y += AI2() < 0 ? -paddle_speed: paddle_speed; 
		else opponent_y += AI2();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g2d.setColor(Color.WHITE);
		g2d.fillRect((int) ball_x, (int) ball_y, x_size, y_size);
		g2d.fillRect(player_x, (int) player_y, paddle_width, paddle_height);
		g2d.fillRect(opponent_x, (int) opponent_y, paddle_width, paddle_height);
	}

	public static void main(String[] args) throws InterruptedException {
		JFrame frame = new JFrame("Pong in Java by Aditya Chopra");
		BouncingBall game = new BouncingBall();
		frame.add(game);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setUndecorated(true); 
		frame.setSize(WIDTH, HEIGHT);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int ID = e.getKeyCode();
                if (ID == KeyEvent.VK_DOWN) player1_moving = 1;
                if (ID == KeyEvent.VK_UP) player1_moving = -1;
                if (ID == KeyEvent.VK_A) player2_moving = 1;
                if (ID == KeyEvent.VK_Q) player2_moving = -1;
                if (ID == KeyEvent.VK_ESCAPE) System.exit(0);
            }
            public void keyReleased(KeyEvent e) {
                int ID = e.getKeyCode();
                if (ID == KeyEvent.VK_DOWN || ID == KeyEvent.VK_UP) player1_moving = 0;
                if (ID == KeyEvent.VK_A || ID == KeyEvent.VK_Q) player2_moving = 0;
            }
        });
		while (true) {
//			game.movePlayer1();
//			game.movePlayer2();
			game.moveAI1();
			game.moveAI2();
			game.moveBall();
			game.repaint();
			Thread.sleep(5);
		}
	}
}
