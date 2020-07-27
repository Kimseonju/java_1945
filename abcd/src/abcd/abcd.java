package abcd;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.text.Position;

import org.omg.CORBA.portable.ApplicationException;

enum Obj_Option {
	Player, Enemy, Bonus, Bullet, EnemyBullet
}

abstract class GameObject extends JPanel {
	protected int posX;
	protected int posY;
	// protected String image;
	protected Obj_Option eObj;
	protected boolean b_Enable;
	protected Image m_image;
	protected int m_Sizex;
	protected int m_Sizey;

	GameObject(int x, int y, Image image, int Size_x, int Size_y) {
		posX = x;
		posY = y;
		m_image = image;
		b_Enable = true;
		m_Sizex = Size_x;
		m_Sizey = Size_y;
	}

	void Setpos(int x, int y) {
		posX = x;
		posY = y;
	}

	int GetleftSize() {
		return posX;
	}

	int GetRightSize() {
		return posX + m_Sizex;
	}

	int GetUpSize() {
		return posY;
	}

	int GetDownSize() {
		return posY + m_Sizey;
	}

	public abstract void Input();

	void SetEnable(boolean bool) {
		b_Enable = bool;
	}

	boolean GetEnable() {
		return b_Enable;
	}

	Image GetImage() {
		return m_image;
	}

	Obj_Option GetObjOption() {
		return eObj;
	}

	int GetposX() {
		return posX;
	}

	int GetposY() {
		return posY;
	}

	void PlusposX(int x) {
		posX += x;
	}

	void PlusposY(int y) {
		posY += y;
	}

	public int Collision(GameObject Obj2) {
		if (GetleftSize() > Obj2.GetRightSize())
			return 0;
		else if (GetRightSize() < Obj2.GetleftSize())
			return 0;
		else if (GetUpSize() > Obj2.GetDownSize())
			return 0;
		else if (GetDownSize() < Obj2.GetUpSize())
			return 0;
		int sum = 0;
		if (eObj == Obj_Option.Player) {
			if (Obj2.GetObjOption() != Obj_Option.Bullet) {
				sum += 2;

			} else {
				return 0;
			}

		} else if (eObj == Obj_Option.Enemy) {
			if (Obj2.GetObjOption() != Obj_Option.EnemyBullet) {
				sum += 1;
				if (Obj2.GetObjOption() == Obj_Option.Player) {
					sum += 2;
				} else if (Obj2.GetObjOption() == Obj_Option.Bonus) {
					return 0;
				}
			} else {
				return 0;
			}
		} else if (eObj == Obj_Option.Bonus) {
			if (Obj2.GetObjOption() != Obj_Option.Bonus) {
				sum += 4;
				if (Obj2.GetObjOption() == Obj_Option.Player) {
					return 2;
				} else if (Obj2.GetObjOption() == Obj_Option.Enemy) {
					return 0;
				}
			} else {
				return 0;
			}
		} else {
			sum = 0;
		}

		return sum;

	}
}

class PlayerObject extends GameObject {

	private boolean right;
	private boolean left;
	private boolean up;
	private boolean down;

	PlayerObject(int x, int y, Image image, int Size_x, int Size_y) {
		super(x, y, image, Size_x, Size_y);
		right = false;
		left = false;
		up = false;
		down = false;
		eObj = Obj_Option.Player;
		// TODO Auto-generated constructor stub
	}

	public void Input() {
		if (right) {
			PlusposX(10);
			if (posX > 800) {
				posX -= 10;
			}
		}
		if (left) {
			PlusposX(-10);
			if (posX < 1) {
				posX += 10;
			}
		}
		if (up) {
			PlusposY(-10);
			if (posY < 0)
				posY += 10;
		}
		if (down) {
			PlusposY(10);
			if (posY > 700)
				posY -= 10;
		}
	}

	public void Keyreset() {
		left = false;
		right = false;
		up = false;
		down = false;
	}

	public void SetLeft(boolean check) {
		left = check;
	}

	public void SetRight(boolean check) {
		right = check;
	}

	public void SetUp(boolean check) {
		up = check;
	}

	public void SetDown(boolean check) {
		down = check;
	}

	public boolean GetLeft() {
		return left;
	}

	public boolean GetRight() {
		return right;
	}

	public boolean GetUp() {
		return up;
	}

	public boolean GetDown() {
		return down;
	}

}

class EnemyObject extends GameObject {
	int Frame = 0;
	int moveFrame = 12;
	int BulletFireCount = 0;
	int moveCount = 0;
	int move = 0; // 짝수는 오른쪽 홀수는 왼쪽

	EnemyObject(int x, int y, Image image, int Size_x, int Size_y) {
		super(x, y, image, Size_x, Size_y);
		eObj = Obj_Option.Enemy;
	}

	public void Input() {
		Frame++;
		if (Frame % moveFrame == 0) {
			if (moveCount == 12) {
				move++;
				posY += 30;
				moveCount = 0;
				if (moveFrame > 1)
					moveFrame--;
			}
			if (move % 2 == 0) {
				posX += 10;
			} else {
				posX -= 10;
			}
			moveCount++;
		}
	}
}

class BonusObject extends GameObject {
	int Frame = 0;
	int moveFrame = 5;
	int BulletFireCount = 0;
	int moveCount = 0;
	int move = 0; // 짝수는 오른쪽 홀수는 왼쪽

	BonusObject(int x, int y, Image image, int Size_x, int Size_y) {
		super(x, y, image, Size_x, Size_y);
		eObj = Obj_Option.Bonus;
	}

	public void Input() {

		if (move % 2 == 0) {
			posX += 10;
		} else {
			posX -= 10;
		}
		if (posX > 800 || posX < 10) {
			move++;
		}
	}
}

class BulletObject extends GameObject {

	BulletObject(int x, int y, Image image, int Size_x, int Size_y) {
		super(x, y, image, Size_x, Size_y);
		eObj = Obj_Option.Bullet;
	}

	public void Input() {

		PlusposY(-5);

	}

}

class EnemyBulletObject extends GameObject {

	EnemyBulletObject(int x, int y, Image image, int Size_x, int Size_y) {
		super(x, y, image, Size_x, Size_y);
		eObj = Obj_Option.EnemyBullet;
	}

	public void Input() {
		PlusposY(5);
	}

}

class GameHandler extends JPanel {
	private JTextArea textArea;
	private boolean isGameOver;
	private ArrayList<GameObject> m_listObj = new ArrayList<GameObject>();
	private ArrayList<GameObject> m_listEnemyObj = new ArrayList<GameObject>();
	private PlayerObject player;
	private int HighScore;
	private int Score;
	private boolean isPlayerWin = false; // ture면 이김 false면 패배
	private int m_GameWidth = 900;
	private int m_GameHeight = 800;
	private int m_ScoreWidth = 300;
	protected Image EnemyImage = new ImageIcon("Image/Enemy.png").getImage();
	protected Image EnemyBulletImage = new ImageIcon("Image/EnemyBullet.png").getImage();
	protected Image PlayerBulletImage = new ImageIcon("Image/PlayerBullet.png").getImage();
	protected Image BonusEnemyImage = new ImageIcon("Image/BonusEnemy.png").getImage();
	protected Image RightImage = new ImageIcon("Image/RightImage.png").getImage();
	private int m_Timer = 0;
	private int bonusCount = 0;
	private boolean isBonus;
	private boolean isGameEnd;

	public boolean isGameOver() {
		return isGameOver;
	}

	public GameHandler(JTextArea ta, PlayerObject Player) {
		textArea = ta;
		initData(Player);
		LoadScoreFile();
	}

	public void gameTiming() {
		try {
			Thread.sleep(10);
			m_Timer++;
			if (!isBonus)
				bonusCount++;
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	public void DrawAll(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, m_GameWidth, m_GameHeight);
		g2d.setColor(Color.blue);
		g2d.fillRect(m_GameWidth, 0, m_ScoreWidth, m_GameHeight);
		for (int i = 0; i < m_listObj.size(); ++i) {
			g2d.drawImage(m_listObj.get(i).GetImage(), m_listObj.get(i).GetposX(), m_listObj.get(i).GetposY(), this);
		}
		FontMetrics metrics = g2d.getFontMetrics(g2d.getFont()); // 글자수해서 앞에다가 숫자붙일예정
		g2d.setFont(g2d.getFont().deriveFont(40f));
		metrics = g2d.getFontMetrics(g2d.getFont());
		g2d.setColor(Color.white);
		g2d.drawString("Score :" + Score, (m_GameWidth + (m_ScoreWidth - metrics.stringWidth("Score :" + Score)) / 2),
				100);

		g2d.setColor(Color.red);
		g2d.drawString("" + Score, (m_GameWidth + (m_ScoreWidth - metrics.stringWidth("Score :" + Score)) / 2) + 126,
				100);
		g2d.setFont(g2d.getFont().deriveFont(25f));
		metrics = g2d.getFontMetrics(g2d.getFont());
		// g2d.drawString(str, x, y);
		g2d.setColor(Color.white);
		g2d.drawString("TOP SCORE: " + HighScore,
				(m_GameWidth + (m_ScoreWidth - metrics.stringWidth("TOP SCORE: " + HighScore)) / 2), 300);
		g2d.setColor(Color.red);
		g2d.drawString("" + HighScore, (m_GameWidth + "TOP SCORE: ".length()
				+ (m_ScoreWidth - metrics.stringWidth("TOP SCORE: " + HighScore)) / 2) + 149, 300);
		g2d.drawImage(RightImage, 900, 400, this);

	}

	public void initData(PlayerObject Player) {
		m_listObj.clear();
		m_listEnemyObj.clear();
		player = Player;
		player.Keyreset();
		Player.Setpos(300, 600);
		m_listObj.add(Player);
		isBonus = false;
		isGameEnd = false;
		Score = 0;
		isPlayerWin = false;
		for (int j = 0; j < 2; ++j) {
			for (int i = 0; i < 4; ++i) {
				EnemyObject Enemy = new EnemyObject(i * 100 + 4 + (j * 100), 100 * j + 2, EnemyImage, 54, 57);
				m_listObj.add(Enemy);
				m_listEnemyObj.add(Enemy);
			}
		}
	}

	public boolean isGameEnd() {
		return isGameEnd;
	}

	public void BulletFire(GameObject Obj) {
		BulletObject bullet = new BulletObject(Obj.GetposX() + (Obj.m_Sizex / 2), Obj.GetposY(), PlayerBulletImage, 4,
				24);
		// bullet.image.
		m_listObj.add(bullet);
	}

	public void EnemyBulletFire(GameObject Obj) {
		EnemyBulletObject bullet = new EnemyBulletObject(Obj.GetposX() + (Obj.m_Sizex / 2), Obj.GetposY() + Obj.m_Sizey,
				EnemyBulletImage, 4, 24);
		m_listObj.add(bullet);
	}

	public void SaveHighScore() {
		if (Score > HighScore)
			HighScore = Score;
	}

	public void SaveScoreFile() {
		SaveHighScore();
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("Score.aaa"))) {
			out.write(HighScore);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void LoadScoreFile() {
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("Score.aaa"))) {
			HighScore = in.read();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void drawGameOver() {
		String str;
		if (isPlayerWin) {
			str = "You Win!";

		} else {
			str = "You Lose!";
		}

		int result = JOptionPane.showConfirmDialog(null, "Play Again?", str, JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.CLOSED_OPTION) {
			SaveScoreFile();
			isGameEnd = true;

		} else if (result == JOptionPane.YES_OPTION) {
			SaveHighScore();
			initData(player);
			isGameOver = false;
		}

		else {
			SaveScoreFile();
			isGameEnd = true;
		}

	}

	public void gameInput() {
		SaveHighScore();
		if (bonusCount % 100 == 0 && isBonus == false) {
			BonusObject Bonus = new BonusObject(0, 0 + 2, BonusEnemyImage, 58, 37);
			m_listObj.add(Bonus);
			isBonus = true;
		}
		if (m_listEnemyObj.size() <= 0) {
			isGameOver = true;
			isPlayerWin = true;
		}
		try {
			for (int i = 0; i < m_listObj.size(); ++i) {
				m_listObj.get(i).Input();
				if (m_listObj.get(i).GetposY() < 0 || m_listObj.get(i).GetposY() > 700) {
					if (m_listObj.get(i).GetObjOption() == Obj_Option.Enemy)
						isGameOver = true;

					m_listObj.remove(i);
					i--;
				}
				for (int j = i + 1; j < m_listObj.size(); ++j) {

					int a = m_listObj.get(i).Collision(m_listObj.get(j)); // a가 1이면 적만 2면 플레이어 3이면 적과 플레이어
					if (a > 0) {
						if (a == 1) {
							// 점수증가
							Score += 10;
						} else if (a == 2) {
							// 플레이어 죽음
							isGameOver = true;
							// i=m_listObj.size()-1;
							break;
						} else if (a == 3) {
							// 적과 플레이어 충돌
							Score += 10;
							// i=m_listObj.size()-1;
							isGameOver = true;
							break;
						} else if (a == 4) {
							// 보너스잡음
							isBonus = false;
							Score += 100;
						}
						m_listObj.get(i).SetEnable(false);
						m_listObj.get(j).SetEnable(false);
						m_listObj.remove(j);
						m_listObj.remove(i);
						if (i > 0)
							i--;

					}

				}
			}
			if (bonusCount % 5 == 0) {
				Random random = new Random();
				for (int i = 0; i < m_listEnemyObj.size(); ++i) {
					if (!m_listEnemyObj.get(i).GetEnable()) {
						m_listEnemyObj.remove(i);
						i--;
					}
					if (0 == 1000 % (int) (1 + Math.random() * 1000)) {
						EnemyBulletFire(m_listEnemyObj.get(i));
					}
				}
			}
		} catch (IndexOutOfBoundsException e) {

		}
	}

}

public class abcd extends JPanel implements KeyListener {
	private GameHandler handler;
	private JTextArea textArea = new JTextArea();
	private static abcd abc;
	protected Image PlayerImage = new ImageIcon("Image/Player.png").getImage();
	PlayerObject m_Player = new PlayerObject(300, 600, PlayerImage, 66, 66);

	JPanel StartPanel;
	JPanel GamePanel;
	private static JButton btn;
	private JLabel imageLabel = new JLabel();
	private JFrame frame = new JFrame("Let's play Space Invaders");
	boolean Fire = false;

	public static abcd Getabcd() {
		return abc;
	}

	public abcd() {

		handler = new GameHandler(textArea, m_Player);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 1200, 800);
		btn = new JButton("Game Start");
		btn.addActionListener(new MyActionListener());
		btn.setPreferredSize(new Dimension(1200, 100));

		imageLabel.setIcon(new ImageIcon("Image/StartImage.png"));
		imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		StartPanel = new JPanel();
		StartPanel.setLayout(new BorderLayout());
		StartPanel.add(imageLabel, BorderLayout.CENTER);
		StartPanel.add(btn, BorderLayout.SOUTH);
		frame.addKeyListener(this);
		frame.add(StartPanel);
		frame.setFocusable(true); // 키입력관련 초점맞추기
		frame.setVisible(true);
		// handler.repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		handler.DrawAll(g2d);
	}

	public static void main(String[] args) {
		/*
		 * JFrame frame = new JFrame("Java 2D API Example2");
		 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); frame.setBounds(100,
		 * 100, 1000, 800); frame.setBackground(Color.black);
		 */
		abc = new abcd();

	}

	class MyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton button = (JButton) e.getSource();
			if (button == btn) {
				StartPanel.removeAll();
				new Thread(new GameThread()).start();
				frame.add(abc);
				frame.setVisible(true);
			}
		}
	}

	class GameThread implements Runnable {
		@Override
		public void run() {
			for (;;) {
				if (!handler.isGameOver()) {
					handler.gameTiming();
					handler.gameInput();
					repaint();
				} else {
					handler.drawGameOver();
					if (handler.isGameEnd())
						break;
				}

			}
			System.exit(0);

		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		switch (e.getKeyCode()) {
		case KeyEvent.VK_RIGHT:
			m_Player.SetRight(true);
			break;
		case KeyEvent.VK_LEFT:
			m_Player.SetLeft(true);
			break;
		case KeyEvent.VK_DOWN:
			m_Player.SetDown(true);
			break;
		case KeyEvent.VK_UP:
			m_Player.SetUp(true);
			break;
		case KeyEvent.VK_SPACE:
			if (Fire == false) {
				handler.BulletFire(m_Player);
				Fire = true;
				break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		switch (e.getKeyCode()) {
		case KeyEvent.VK_RIGHT:
			m_Player.SetRight(false);
			break;
		case KeyEvent.VK_LEFT:
			m_Player.SetLeft(false);
			break;
		case KeyEvent.VK_DOWN:
			m_Player.SetDown(false);
			break;
		case KeyEvent.VK_UP:
			m_Player.SetUp(false);
			break;
		case KeyEvent.VK_SPACE:
			if (Fire == true) {
				Fire = false;

				
			}
			break;
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
