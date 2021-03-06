package world;

import java.awt.Graphics;

import app.Main;
import lib.Vector2;


/**
 * The projectile class is responsible for managing a projectile a sprite shoots out designed to "kill" other sprites.
 * 
 * @author Robert
 * @version 0.1
 */
public class Projectile extends Sprite
{
	
	/**  maximum speed of a projectile. */
	public static final float SPEED = Integer.parseInt(Main.CONFIG.get("projectileSpeed"));
	
	/** Radius of the projectile. */
	private static final float RADIUS = Integer.parseInt(Main.CONFIG.get("projectileRadius"));
	
	// Instance variables
	// ---------------------------------
	
	/**  owner circle. */
	private Circle owner;

	// Constructors
	// --------------------------------------------------------------

	/**
	 * Instantiates a new projectile.
	 *
	 * @param p the p
	 */
	public Projectile(Projectile p)
	{
		super(p);
		this.owner = p.owner;
	}

	/**
	 * 4-Argument Projectile Constructor.
	 *
	 * @param x x pos
	 * @param y y pos
	 * @param velocity velocity vector
	 * @param c owner circle
	 * @param world plane of existence
	 */
	public Projectile(Vector2 position, Vector2 velocity, Circle c, World world)
	{
		// @formatter:off
		super(
				new Vector2(RADIUS, RADIUS), //size
				position.copy(), // pos
				velocity.copy().normalize().mult(SPEED), // vel
				new Vector2(0, 0), // acc
				c.getColor(), world);
		this.owner = c; // intentional shallow copy
		// @formatter:on
	}

	// Overridden methods
	// -----------------------------------------------------------------

	/* (non-Javadoc)
	 * @see world.Sprite#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		// @formatter:off
		// paint circle for projectile
		g.setColor(getColor());
		g.fillOval(
				(int) (getPosition().getX() - getSize().getX() / 2), 
				(int) (getPosition().getY() - getSize().getY() / 2),
				(int) getSize().getX(), (int) getSize().getY());
		// @formatter:on

		if (Main.debug)
			super.paint(g);
	}

	/* (non-Javadoc)
	 * @see world.Sprite#update()
	 */
	@Override
	public void update()
	{
		// @formatter:off
		// die if out of bounds
		if (getPosition().getX() - getSize().getX() / 2 < 0 
				|| getPosition().getX() + getSize().getX() / 2 > getWorld().getSize().getX()
				|| getPosition().getY() - getSize().getX() / 2 < 0
				|| getPosition().getY() + getSize().getX() / 2 > getWorld().getSize().getY())
			setExistence(false);
		// @formatter:on

		// update position
		setPosition(getPosition().add(getVelocity()));
	}

	/* (non-Javadoc)
	 * @see world.Sprite#collide(world.Sprite)
	 */
	@Override
	public void collide(Sprite s)
	{
		if(s == null) return;
		// @formatter:off
		if (s.getId() != owner.getId() && !(s instanceof Shield && ((Shield) s).isOwner(owner)))
		{			
				if(s instanceof Circle && !((Circle) s).isShielded()) 
				{
					setExistence(false);
					owner.setKills(owner.getKills() + 1);
					owner.setHits(owner.getHits() + 1);
					owner.setTotalHits(owner.getTotalHits() + 1);
					owner.setTotalKills(owner.getTotalHits() + 1);
				}
				else if (s instanceof Shield || s instanceof Mine)
				{
					setExistence(false);
					owner.setHits(owner.getHits() + 1);
					owner.setTotalHits(owner.getTotalHits() + 1);
				}
				else if (s instanceof Obstacle)
				{
					setExistence(false);
				}
		}
		// @formatter:on
	}

	/* (non-Javadoc)
	 * @see world.Sprite#copy()
	 */
	@Override
	public Sprite copy()
	{
		return new Projectile(this);
	}

	/**
	 * The isOwner method tells whether the given circle is the owner of the
	 * projectile.
	 *
	 * @param c Circle to check
	 * @return is owner circle?
	 */
	public boolean isOwner(Sprite c)
	{
		return c != null && owner.getId() == c.getId();
	}
}
