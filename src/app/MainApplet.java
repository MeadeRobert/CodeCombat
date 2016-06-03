package app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.NoSuchElementException;

import javax.swing.JApplet;

import app.Main.GameState;
import lib.Vector2;
import world.World;

public class MainApplet extends JApplet implements Runnable, KeyListener
{
	/** serializable id **/
	private static final long serialVersionUID = -2598013920892210921L;
	/** size of Applet **/
	private final Dimension size = new Dimension(Main.worldWidth, Main.worldHeight);
	/** plane of existence help by the applet **/
	private World world;
	/** UI for overlay and other user interaction **/
	private UI ui;
	/** overlay stats? **/
	private boolean displayFullStatsOverlay = false;
	/** color of background **/
	private Color backgroundColor = Color.GRAY;
	/** instance of self **/
	public static MainApplet main;
	/** Thread to run on **/
	private Thread thread;
	/** Image to double buffer with **/
	private BufferedImage bi;
	/** The frame the applet is contained **/
	private MainFrame frame;
	/** frames drawn **/
	private int frames = 0;
	/** key toggles **/
	private HashMap<Character, Boolean> keyToggles = new HashMap<Character, Boolean>();
	
	
	/** GameState */
	private GameState gameState = GameState.MENU;

	// Applet core
	// ------------------------------------

	@Override
	public void init()
	{
		// setup key toggles
		keyToggles.put('r', false);
		keyToggles.put('m', false);
		keyToggles.put('p', false);
		keyToggles.put('d', false);
		
		
		// setup the window
		setSize(size);
		setBackground(backgroundColor);
		addKeyListener(this);
		setFocusTraversalKeysEnabled(false);

		// initialize instance variables
		if (main == null)
			main = this;
		world = new World(this, new Vector2((float) getSize().getWidth(), (float) getSize().getHeight()));
		ui = new UI(this);
	}

	@Override
	public void start()
	{
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void stop()
	{
	}

	@Override
	public void destroy()
	{
	}

	@Override
	public void paint(Graphics g)
	{
		try
		{

			// setup canvas
			bi = new BufferedImage((int) world.getSize().getX(), (int) world.getSize().getY(),
					BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g2 = bi.createGraphics();
			g2.setColor(getBackground());
			g2.fillRect(0, 0, (int) world.getSize().getX(), (int) world.getSize().getY());

			if (gameState.equals(GameState.MENU))
			{
				ui.drawMenuScreen(g2);
			}
			else
			{
				// paint world
				world.paint(g2);
				if (gameState.equals(GameState.PAUSED))
					ui.drawPauseScreen(g2);
				if (displayFullStatsOverlay)
					ui.drawFullStatsOverlay(g2);

				else ui.drawLeaderboard(g2);
			}

			g.drawImage(bi, 0, 0, (int) getSize().getWidth(), (int) getSize().getHeight(), null);
		}
		catch (ConcurrentModificationException e)
		{
			// skip the rest of the update if paint conflicts with updates
			if (Main.debug)
				System.err.println("Skip rest of frame: Concurrent Modification");
		}
		catch (NoSuchElementException e)
		{
			// ditto here (caused by trying to access world's arraylist of
			// circles whilst updating: bah humbug
			if (Main.debug)
				System.err.println("Skip rest of frame: No Such Element");
		}
	}

	@Override
	public void run()
	{
		while (!gameState.equals(GameState.OVER))
		{
			long start = System.currentTimeMillis();

			try
			{
				if (gameState.equals(GameState.PLAY))
				{
					world.update();
				}
				repaint();
			}
			catch (ConcurrentModificationException e)
			{
				// skip the rest of the update if paint conflicts with updates
				if (Main.debug)
					System.err.println("Skip rest of frame: Concurrent Modification");
			}

			// sleep for rest of frame time
			while (System.currentTimeMillis() - start < Main.FRAME_RATE)
			{
				try
				{
					Thread.sleep(1);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			frames++;
		}
	}

	// Game Actions
	// -------------------------------------------------------

	/**
	 * The restart method resets the world and restarts the game.
	 */
	public void restart()
	{
		world = new World(main, new Vector2((float) size.getWidth(), (float) size.getHeight()));
	}

	/**
	 * The pause method sets the gameState enum to PAUSED and opens the pause
	 * menu.
	 */
	public void pause()
	{
		gameState = GameState.PAUSED;
		// Open menu
	}

	/**
	 * The resume method sets the gameState enum to PLAY and resumes the game
	 */
	public void resume()
	{
		gameState = GameState.PLAY;
	}

	/**
	 * The end method sets the gameState enum to MENU and returns the game to
	 * the main menu.
	 */
	public void end()
	{
		gameState = GameState.MENU;
		// Do menu stuff
	}

	public void togglePause()
	{
		if (gameState.equals(GameState.PLAY))
			gameState = GameState.PAUSED;
		else if (gameState.equals(GameState.PAUSED))
			gameState = GameState.PLAY;
	}

	// Event Listeners
	// -------------------------------------------------------------------------------------

	@Override
	public void keyTyped(KeyEvent e)
	{
		

		
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyChar() == '\t')
			displayFullStatsOverlay = true;	
		if (e.getKeyChar() == 'p' || e.getKeyChar() == 'P')
			if(!keyToggles.get('p')) { togglePause(); keyToggles.put('p', true); }
		if (e.getKeyChar() == 'd' || e.getKeyChar() == 'D')
			if(!keyToggles.get('d')) { Main.debug = !Main.debug; keyToggles.put('d', true); }
		if (e.getKeyChar() == 'r' || e.getKeyChar() == 'R')
			if(!keyToggles.get('r')) { world.restart(); keyToggles.put('r', true); }
		if (e.getKeyChar() == ' ' && gameState.equals(GameState.MENU))
		{
			gameState = GameState.PLAY;
			world.restart();
		}
		if (e.getKeyChar() == 'm' || e.getKeyChar() == 'M' &&
				!gameState.equals(GameState.MENU))
		{
			if(!keyToggles.get('m')) { gameState = GameState.MENU; keyToggles.put('m', true); }
		}
		frame.updateMenu();
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyChar() == '\t')
			displayFullStatsOverlay = false;
		if(keyToggles.containsKey(e.getKeyChar()))
		{
			keyToggles.put(e.getKeyChar(), false);
		}
	}

	// Getters and Setters
	// --------------------------------------------------------------------------------------------

	/**
	 * The getWidth method returns the width of the applet.
	 * @return width of applet
	 */
	public int getWidth()
	{
		return (int) getSize().getWidth();// frame.getWidth();
	}

	/**
	 * The setWidth method sets the width of the applet.
	 * @param width width of applet
	 */
	public void setWidth(int width)
	{
		setSize(width, (int) getSize().getHeight());
	}

	/**
	 * The getHeight method returns the height of the applet.
	 * @return height of applet
	 */
	public int getHeight()
	{
		return (int) getSize().getHeight();// frame.getHeight();
	}

	/**
	 * The setHeight method sets the height of the applet.
	 * @param height height of applet
	 */
	public void setHeight(int height)
	{
		setSize((int) getSize().getWidth(), height);
	}

	/**
	 * The getGameState method returns the current game state.
	 * @return The current game state
	 */
	public GameState getGameState()
	{
		return this.gameState;
	}

	/**
	 * The setGameState method sets gameState to the desired state
	 * @param gameState game state
	 */
	public void setGameState(GameState gameState)
	{
		this.gameState = gameState;
	}

	protected World getWorld()
	{
		return world;
	}

	protected void setWorld(World world)
	{
		this.world = world;
	}

	public MainFrame getFrame()
	{
		return frame;
	}

	public void setFrame(MainFrame frame)
	{
		this.frame = frame;
	}

	public int getFrames()
	{
		return frames;
	}

	public void setFrames(int frames)
	{
		this.frames = frames;
	}
}
