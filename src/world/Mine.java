package world;

import java.awt.Color;
import java.awt.Graphics;

import app.Main;
import lib.Vector2;

/**
 * The Shield class provides a layer of protection for Circles in game.
 * @author Brian, Robbie, Josh
 * @version 0.1
 */
public class Mine extends Sprite
{
	/** time for duck to respawn **/
	public static final int RESPAWN_TIME = Integer.parseInt(Main.CONFIG.get("mineRespawnTime"));
	/** radius of duck **/
	public static final int RADIUS = Integer.parseInt(Main.CONFIG.get("mineRadius"));
	/** time for 's respawn **/
	private int respawnTimer = RESPAWN_TIME;

	public Mine(Mine m)
	{
		super(m);
	}

	public Mine(World world)
	{
		// @formatter:off
		super(
				new Vector2(RADIUS, RADIUS), // size
				new Vector2((int) (Math.random() * world.getSize().getX()), (int) (Math.random() * world.getSize().getY())), // pos
				new Vector2(1, (float) (Math.random() * Math.PI * 2), true), // vel
				new Vector2(0, 0), // acc
				Color.RED, // random color
				world);
		// @formatter:on
	}

	@Override
	public void update()
	{
		if (isAlive())
		{
			// look for mama duck to kill for kidnapping
			// update position (move in circle)
			setAcceleration(new Vector2(1f / 60f, (float) (getVelocity().angle() + Math.PI / 2), true));
			Vector2 previousVelocity = getVelocity();
			setVelocity(getVelocity().add(getAcceleration()).normalize());
			setPosition(getPosition().add(getVelocity().add(previousVelocity).div(2)));
		}
		else
		{
			respawnTimer--;
			respawn();
		}
	}

	public void paint(Graphics g)
	{	
		if (isAlive())
		{
			super.paint(g);
			// paint the sad little goose looking to kill its false mama duck
			// @formatter:off
			// paint circle
			g.setColor(getColor());
			g.drawOval(
					(int) (getPosition().getX() - getSize().getX() / 2),
					(int) (getPosition().getY() - getSize().getX() / 2),
					(int) getSize().getX(), (int) getSize().getY());
		}
	}

	@Override
	public Sprite copy()
	{
		return new Mine(this);
	}

	@Override
	public void collide(Sprite s)
	{
		if(s == null) return;
		// die once mission complete (allahu akbar!)
		if(s instanceof Circle || s instanceof Shield)
		{
			this.setAlive(false);
		}
		else if (s instanceof Projectile)
		{
			this.setAlive(false);
		}
	}

	private final void respawn()
	{
		if (respawnTimer <= 0)
		{
			getWorld().respawn(this);
			respawnTimer = RESPAWN_TIME;
		}
	}
}