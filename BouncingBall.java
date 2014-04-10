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
	int ball_size;
	int paddle_width;
	int paddle_height;
	int paddle_speed;
	int player1_x;
	double player1_y;
	int player2_x;
	double player2_y;
	static int player1_moving;
	static int player2_moving;
	int player_score;
	int opponent_score;
	double increment;
	boolean calculated;
	
	public BouncingBall() {
		WIDTH = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		HEIGHT = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		ball_size = 30;
		ball_x = WIDTH / 2 - ball_size;
		ball_y = HEIGHT / 2;
		int direction = (int) (Math.random() * 2) * 2 - 1;
		dx = 4 * direction;
		dy = 0;
		paddle_width = ball_size;
		paddle_height = 150;
		paddle_speed = 5;
		player1_x = 0;
		player1_y = HEIGHT / 2 - paddle_height / 2;
		player2_x = WIDTH - player1_x - paddle_width;
		player2_y = HEIGHT / 2 - paddle_height / 2;
		player1_moving = 0;
		player2_moving = 0;
		player_score = 0;
		opponent_score = 0;
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
			System.out.println("Player 1 Score: " + player_score);
			System.out.println("Player 2 Score: " + opponent_score);
		}
		if (interval_overlap(new double[] {ball_x, ball_x + ball_size}, new double[] {player1_x, player1_x + paddle_width}) && interval_overlap(new double[] {ball_y, (int) (ball_y + ball_size)}, new double[] {player1_y, player1_y + paddle_height})) { //ball is not a point
			ball_x = player1_x + paddle_width;
			dx *= increment;
			double rebound_angle = 45 * ((ball_y - (player1_y  + paddle_height / 2)) / (paddle_height / 2));
			double speed = Math.pow(Math.pow(dy, 2) + Math.pow(dx, 2), 0.5);
			dx = speed * Math.cos(Math.toRadians(rebound_angle));
			dy = speed * Math.sin(Math.toRadians(rebound_angle));
		}
		if (ball_x + ball_size >= WIDTH) {
			reset();
			player_score++;
			System.out.println("Your score: " + player_score);
			System.out.println("AI score: " + opponent_score);
	    }
		if (interval_overlap(new double[] {ball_x, ball_x + ball_size}, new double[] {player2_x, player2_x + paddle_width}) && interval_overlap(new double[] {ball_y, (int) (ball_y + ball_size)}, new double[] {player2_y, player2_y + paddle_height})) { //ball is not a point
			ball_x = player2_x - paddle_width;
			dx *= increment;
			double rebound_angle = 45 * ((ball_y - (player2_y  + paddle_height / 2)) / (paddle_height / 2));
			double speed = Math.pow(Math.pow(dy, 2) + Math.pow(dx, 2), 0.5);
			dx = -speed * Math.cos(Math.toRadians(rebound_angle));
			dy = speed * Math.sin(Math.toRadians(rebound_angle));
		}
		if (ball_y < 0) {
		    ball_y = 0;
		    dy = -dy;
		}
		if (ball_y + ball_size >= HEIGHT) {
		    ball_y = HEIGHT - ball_size;
		    dy = -dy;
		}
	}
	private boolean interval_overlap(double[] x1, double[] x2) {
		return x1[0] <= x2[1] && x2[0] <= x1[1];
	}
	private void reset() { 
		ball_x = WIDTH / 2;
		ball_y = HEIGHT / 2;
		int direction = (int) (Math.random() * 2) * 2 - 1;
		dx = 2 * direction;
		dy = 0;
		player1_x = 0;
		player1_y = HEIGHT / 2 - paddle_height / 2;
		player2_x = WIDTH - player1_x - paddle_width;
		player2_y = HEIGHT / 2 - paddle_height / 2;
		calculated = false;
	}
	private void movePlayer1() {
		if (player1_moving == -1) player1_y -= paddle_speed;
		else if (player1_moving == 1) player1_y += paddle_speed;
	}
	private void movePlayer2() {
		if (player2_moving == -1 && player2_y > 0) player2_y -= paddle_speed;
		else if (player2_moving == 1 && player2_y + paddle_height < HEIGHT) player2_y += paddle_speed;
	}
	private void moveAI1() {
		if (Math.abs(AI1()) > paddle_speed) player1_y += AI1() < 0 ? -paddle_speed: paddle_speed; 
		else player1_y += AI1();
	}
	private int AI1() {
		return predictedHitter(true);
	}
	private int topHitter(boolean player1) {
		if (player1) {
			return (int) (ball_y + ball_size - (player1_y)) - 2;
		}
		return (int) (ball_y + ball_size - (player2_y)) - 2;
	}
	private int botHitter(boolean player1) {
		if (player1) {
			return (int) ((ball_y) - (player1_y + paddle_height)) + 2;
		}
		return (int) ((ball_y) - (player2_y + paddle_height)) + 2;
	}
	private int topBotHitter(boolean player1) {
		if (ball_y >= HEIGHT / 2) return botHitter(player1);
		return topHitter(player1);
	}
	private int betterTopBotHitter(boolean player1) {
		if (player1 && dx > 0) return (int) ((HEIGHT / 2) - (player1_y + paddle_height / 2));
		if (!player1 && dx < 0) return (int) ((HEIGHT / 2) - (player2_y + paddle_height / 2));
		return topBotHitter(player1);
	}
	private int predictedHitter(boolean player1) {
		int predicted_y = (int) ((player1 ? -ball_x: (WIDTH - ball_x)) * (dy/dx) + ball_y);
		while (predicted_y < 0 || predicted_y > HEIGHT) {
			if (predicted_y > HEIGHT) predicted_y -= 2 * HEIGHT; 
			predicted_y *= -1;
		}
		if (dy/dx == 0) predicted_y = (int) ball_y;
		if (dx < 0 != player1)	return (int)((HEIGHT / 2) - ((player1 ? player1_y: player2_y) + paddle_height));
		return (int) ((predicted_y) - ((player1 ? player1_y: player2_y) + paddle_height / 2));
	}
	private int AI2() {
		return betterTopBotHitter(false);
	}
	private void moveAI2() {
		if (Math.abs(AI2()) > paddle_speed) player2_y += AI2() < 0 ? -paddle_speed: paddle_speed; 
		else player2_y += AI2();
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g2d.setColor(Color.WHITE);
		g2d.fillRect((int) ball_x, (int) ball_y, ball_size, ball_size);
		g2d.fillRect(player1_x, (int) player1_y, paddle_width, paddle_height);
		g2d.fillRect(player2_x, (int) player2_y, paddle_width, paddle_height);
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
			Thread.sleep(0);
		}
	}
}