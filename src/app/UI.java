package app;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Collections;
import java.util.LinkedList;

import bots.Mind;
import world.Circle;
import world.Sprite;

/**
 * The UI class is responsible for rendering the UI elements of the program
 * @author Robert
 * @version 0.1
 */
public class UI
{
	/**
	 * the applet everything is drawn in
	 */
	private MainApplet mainApplet;

	/**
	 * Constructor to initialize the UI class
	 * @param main the applet eveything will be drawn in
	 */
	public UI(MainApplet main)
	{
		this.mainApplet = main;
	}

	public void drawMenuScreen(Graphics g)
	{
		String s = "Code Combat";
		int width = (int) mainApplet.getWorld().getSize().getX();
		int height = (int) mainApplet.getWorld().getSize().getY();

		g.setFont(new Font("Serif", Font.PLAIN, (int) Math.min(getScaledFontSizeHorizontal(s, width / 2, g),
				getScaledFontSizeVertical(height / 4, g))));

		// draw title
		int x = width / 2 - g.getFontMetrics().stringWidth(s) / 2;
		int y = height / 2 - g.getFontMetrics().getHeight();
		g.setColor(Color.CYAN);
		g.drawString(s, x - 1, y - 1);
		g.setColor(Color.BLUE);
		g.drawString(s, x, y);

		// draw instructions blinking
		s = "Press Space to Start";
		g.setFont(new Font("Serif", Font.PLAIN, (int) Math.min(getScaledFontSizeHorizontal(s, width / 4, g),
				getScaledFontSizeVertical(height / 8, g))));
		x = width / 2 - g.getFontMetrics().stringWidth(s) / 2;
		y += height / 4 - g.getFontMetrics().getHeight();

		if (mainApplet.getFrames() % (Main.FRAME_RATE * 3) > Main.FRAME_RATE * 3 / 2)
		{
			g.setColor(Color.CYAN);
			g.drawString(s, x - 1, y - 1);
			g.setColor(Color.BLUE);
			g.drawString(s, x, y);
		}
		else
		{
			g.setColor(mainApplet.getBackground());
			g.drawRect(x, y - g.getFontMetrics().getHeight(), g.getFontMetrics().stringWidth(s),
					g.getFontMetrics().getHeight());
		}
	}

	/**
	 * draws the paused menu
	 * @param g graphics
	 */
	public void drawPauseScreen(Graphics g)
	{
		String s = "Paused";

		// setup font
		Font font = new Font("Serif", Font.PLAIN, 44);
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();

		// setup placement
		int width = (int) mainApplet.getWorld().getSize().getX();
		int height = (int) mainApplet.getWorld().getSize().getY();
		int x = width / 2 - fm.stringWidth(s) / 2;
		int y = height / 2 - fm.getHeight() / 2;

		// draw paused message
		g.setColor(Color.CYAN);
		g.drawString(s, x - 1, y - 1);
		g.setColor(Color.BLUE);
		g.drawString(s, x, y);
	}
	
	/**
	 * draws the leaderboard in the applet
	 * @param g graphics
	 */
	public void drawLeaderboard(Graphics g)
	{
		// get circles
		LinkedList<Circle> circles = new LinkedList<Circle>();
		for (Sprite s : mainApplet.getWorld().getSprites())
			if (s instanceof Circle)
				circles.add((Circle) s);
		Collections.sort(circles);

		// setup font and offsets
		int width = (int) mainApplet.getWorld().getSize().getX();
		int height = (int) mainApplet.getWorld().getSize().getY();
		int x = width / 8 * 7;
		int y = height / 64 * 3;
		int xInset = width / 32;
		int rowHeight = height / 32;
		int colWidth = width / 16;

		// draw box behind leaderboard and title
		g.setColor(new Color(55, 55, 55, 230));
		g.fillRect(x - width / 64, y - height / 32, width / 8, rowHeight * (circles.size() + 2));
		g.setColor(Color.RED);
		g.setFont(new Font("Serif", Font.PLAIN,
				(int) Math.min(Math.min(getScaledFontSizeHorizontal("LeaderBoard", width / 8 - xInset, g),
						getScaledFontSizeVertical(rowHeight, g)), g.getFont().getSize())));
		g.drawString("LeaderBoard", x + (colWidth - g.getFontMetrics().stringWidth("LeaderBoard") + xInset) / 2, y);
		g.drawLine(x - width / 128, y + height / 128, x + width / 32 * 3, y + height / 128);
		y += rowHeight;

		// size font for circles in list
		for (Circle c : circles)
		{
			String s = ("" + c.getMind()).substring(0, Math.min(("" + c.getMind()).length(), Mind.MAX_NAME_CHARS))
					+ ": " + c.getId();
			g.setFont(new Font("Serif", Font.PLAIN, (int) Math.min(
					Math.min(getScaledFontSizeHorizontal(s, width / 8, g), getScaledFontSizeVertical(rowHeight, g)),
					g.getFont().getSize())));
		}

		// paint list of circles
		g.setColor(Color.RED);
		for (Circle c : circles)
		{
			g.setColor(c.getColor());
			String s = ("" + c.getMind()).substring(0, Math.min(("" + c.getMind()).length(), Mind.MAX_NAME_CHARS))
					+ ": " + c.getId();
			g.drawString(s, x + (colWidth - g.getFontMetrics().stringWidth(s) + xInset) / 2, y);
			y += rowHeight;
		}
	}

	/**
	 * draws the status overlay of the window
	 * @param g graphics
	 */
	public void drawFullStatsOverlay(Graphics g)
	{
		// "struct" for stats data
		class StatData
		{
			public Circle c;
			public String[] data;

			public StatData(Circle c, String[] data)
			{
				this.c = c;
				this.data = data;
			}
		}

		// get circles
		LinkedList<Circle> circles = new LinkedList<Circle>();
		for (Sprite s : mainApplet.getWorld().getSprites())
			if (s instanceof Circle)
				circles.add((Circle) s);
		Collections.sort(circles);

		// parse stats
		// @formatter:off
		String[] statLayout = new String[] { "Mind", "Id", "Kills", "Deaths", "Accuracy", "Status" };
		LinkedList<StatData> stats = new LinkedList<StatData>();
		for (Circle c : circles)
			stats.add(new StatData(c,
					new String[] {
							("" + c.getMind()).substring(0, Math.min(("" + c.getMind()).length(), Mind.MAX_NAME_CHARS)),
							"" + c.getId(),
							"" + c.getTotalKills(),
							"" + c.getDeaths(),
							"" + String.format("%.2f", c.getTotalAccuracy()),
							"" + (c.isAlive() ? "alive" : "dead") // pro ternary here -rjm
							}));
		// @formatter:on

		// setup font and offsets
		int width = (int) mainApplet.getWorld().getSize().getX();
		int height = (int) mainApplet.getWorld().getSize().getY();
		int x = width / 8;
		int y = width / 8;
		int xInset = width / 64;
		int yInset = width / 64;
		int colWidth = (width - 2 * x - xInset) / statLayout.length;
		// guard agains divide by 0
		int rowHeight = stats.size() > 0 ? (height - 2 * y - yInset) / 2 / stats.size() : (height - 2 * y - yInset) / 2;

		// draw background and title
		g.setColor(new Color(55, 55, 55, 230));
		g.fillRect(width / 8, height / 8, width * 3 / 4, height * 3 / 4);
		g.setColor(Color.RED);
		g.setFont(new Font("Serif", Font.PLAIN, 22));
		g.drawString("Full Stats", x + xInset, y + yInset / 2);

		// config fonts and size params
		for (String s : statLayout)
		{
			g.setFont(new Font("Serif", Font.PLAIN,
					(int) Math.min(Math.min(getScaledFontSizeHorizontal(s, colWidth - xInset * 2, g),
							getScaledFontSizeVertical(rowHeight, g)), g.getFont().getSize())));
		}
		for (StatData entry : stats)
		{
			for (String s : entry.data)
			{
				g.setFont(new Font("Serif", Font.PLAIN,
						(int) Math.min(Math.min(getScaledFontSizeHorizontal(s, colWidth - xInset * 2, g),
								getScaledFontSizeVertical(rowHeight, g)), g.getFont().getSize())));
			}
		}

		// overlay circle data
		x += xInset;
		y += yInset * 4;

		for (String s : statLayout)
		{
			g.drawString(s, x + (colWidth - g.getFontMetrics().stringWidth(s)) / 2, y);
			x += colWidth;
		}
		g.drawLine(width / 8 + xInset, y + height / 128, width / 8 * 7 - xInset, y + height / 128);
		y += rowHeight;

		for (StatData entry : stats)
		{
			x = width / 8 + xInset;
			for (String s : entry.data)
			{
				g.setColor(entry.c.getColor());
				g.drawString(s, x + (colWidth - g.getFontMetrics().stringWidth(s)) / 2, y);
				x += colWidth;
			}
			y += rowHeight;
		}

		// overlay other game data
		String timeElapsed = "Time Elapsed: " + mainApplet.getWorld().getTicks() / (1000 / Main.FRAME_RATE);
		g.setColor(Color.RED);
		x = width / 8 * 7 - xInset - g.getFontMetrics().stringWidth(timeElapsed);
		y = height / 8 * 7 - yInset;
		g.drawString(timeElapsed, x, y);
	}

	/**
	 * Scales the font in the x direction
	 * @param text the text to scale
	 * @param width of the window
	 * @param g graphics
	 * @return a float with the scaled size
	 */
	public float getScaledFontSizeHorizontal(String text, int width, Graphics g)
	{
		return (float) width / g.getFontMetrics().stringWidth(text) * g.getFont().getSize();
	}

	/**
	 * scales the font vertically
	 * @param height the height of the window
	 * @param g graphics
	 * @return a float with the scaled size
	 */
	public float getScaledFontSizeVertical(int height, Graphics g)
	{
		return (float) height / g.getFontMetrics().getHeight() * g.getFont().getSize();
	}
}
