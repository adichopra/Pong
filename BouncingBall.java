import java.awt.Color;
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
	static int paddle_speed;
	int player_x;
	static double player_y;
	int opponent_x;
	double opponent_y;
	static int player_moving;
	int player_score;
	int opponent_score;
	double multiplier;
	
	public BouncingBall() {
		WIDTH = 1400;
		HEIGHT = 1000;
		ball_x = WIDTH / 2;
		ball_y = HEIGHT / 2;
		int angle = (int) (Math.random() * 121) - 60;
		int direction = (int) (Math.random() * 2) * 2 - 1;
		dx = 2 * direction;
		dy = dx * Math.tan(Math.toRadians(angle));
		x_size = 30;
		y_size = 30;
		paddle_width = x_size;
		paddle_height = 150;
		paddle_speed = 5;
		player_x = WIDTH / paddle_width;
		player_y = HEIGHT / 2;
		opponent_x = WIDTH - WIDTH / paddle_width;
		opponent_y = HEIGHT / 2;
		player_moving = 0;
		player_score = 0;
		opponent_score = 0;
		multiplier = 0.001;
	}
	
	private void moveBall() {
		if (dx > getWidth() / 2) dx = getWidth() / 2;
		if (dy > getHeight() / 2) dy = getHeight() / 2;
		ball_x += dx;
		ball_y += dy;
		if (ball_x < 0) {
			ball_x = WIDTH / 2;
			ball_y = HEIGHT / 2;
			int angle = (int) (Math.random() * 121) - 60;
			int direction = (int) (Math.random() * 2) * 2 - 1;
			dx = 2 * direction;
			dy = dx * Math.tan(Math.toRadians(angle));
			opponent_score++;
			System.out.println("Your score: " + player_score);
			System.out.println("AI score: " + opponent_score);
		}
		if (ball_x > player_x - paddle_width / 2  && ball_x < player_x + paddle_width / 2 && ball_y < player_y + paddle_height && ball_y > player_y) {
			ball_x = player_x + paddle_width / 2;
			dx -= 0.5;
		    dx = -dx;
		    dy = dy < 0 ? dy - multiplier * Math.abs(player_y  + paddle_height / 2 - ball_y): dy + multiplier * Math.abs(player_y  + paddle_height / 2 - ball_y);
		}
		if (ball_x + x_size >= getWidth()) {
			ball_x = WIDTH / 2;
			ball_y = HEIGHT / 2;
			int angle = (int) (Math.random() * 121) - 60;
			int direction = (int) (Math.random() * 2) * 2 - 1;
			dx = 2 * direction;
			dy = dx * Math.tan(Math.toRadians(angle));
			player_score++;
			System.out.println("Your score: " + player_score);
			System.out.println("AI score: " + opponent_score);
	    }
		if (ball_x > opponent_x - paddle_width / 2 && ball_x < opponent_x + paddle_width / 2 && ball_y < opponent_y + paddle_height && ball_y > opponent_y) {
			ball_x = opponent_x - paddle_width / 2;
			dx += 0.5;
		    dx = -dx;
		    dy = dy < 0 ? dy - multiplier * Math.abs(opponent_y  + paddle_height / 2 - ball_y): dy + multiplier * Math.abs(opponent_y  + paddle_height / 2 - ball_y);
		}
		if (ball_y < 0) {
		    ball_y = 0;
		    dy -= 0.5;
		    dy = -dy;
		}
		if (ball_y + y_size >= getHeight()) {
		    ball_y = getHeight() - y_size;
		    dy += 0.5;
		    dy = -dy;
		}
	}
	private void movePlayer() {
		if (player_moving == -1 && player_y > 0) player_y -= paddle_speed;
		else if (player_moving == 1 && player_y + paddle_height < getHeight()) player_y += paddle_speed;
	}
	private void moveAI() {
		if (ball_y > opponent_y && opponent_y + paddle_height < getHeight()) opponent_y += paddle_speed;
		else if (ball_y < opponent_y && opponent_y > 0) opponent_y -= paddle_speed;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g2d.setColor(Color.WHITE);
		g2d.fillRect((int) ball_x, (int) ball_y, x_size, y_size);
		g2d.fillRect(player_x, (int) player_y, paddle_width, paddle_height);
		g2d.fillRect(opponent_x, (int) opponent_y, paddle_width, paddle_height);
	}

	public static void main(String[] args) throws InterruptedException {
		JFrame frame = new JFrame("BouncingBall");
		BouncingBall game = new BouncingBall();
		frame.add(game);
		frame.setSize(WIDTH, HEIGHT);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int ID = e.getKeyCode();
                if (ID == KeyEvent.VK_DOWN) player_moving = 1;
                else if (ID == KeyEvent.VK_UP) player_moving = -1;
            }
            public void keyReleased(KeyEvent e) {
                int ID = e.getKeyCode();
                if (ID == KeyEvent.VK_DOWN || ID == KeyEvent.VK_UP) player_moving = 0;
            }
        });
		while (true) {
			game.movePlayer();
			game.moveAI();
			game.moveBall();
			game.repaint();
			Thread.sleep(5);
		}
	}
}