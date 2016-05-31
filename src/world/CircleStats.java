package world;

/**
 * Essentially a struct for Circle stats.
 * @author robbie
 */
public class CircleStats
{
	public final float totalAccuracy;
	public final float accuracy;
	public final int totalShotsFired;
	public final int shotsFired;
	public final int totalHits;
	public final int hits;
	public final int deaths;
	public final int totalKills;
	public final int kills;
	public final int ticksAlive;
	public final int totalShieldsAcquired;
	public final int shieldsAcquired;
	public final int totalObstacleCollisions;
	public final int obstaclesCollisions;
	public final int totalWallCollisions;
	public final int wallCollisions;
	
	public CircleStats(Circle c)
	{
		this.totalAccuracy = c.getTotalAccuracy();
		this.accuracy = c.getAccuracy();
		this.totalShotsFired = c.getTotalShotsFired();
		this.shotsFired = c.getShotsFired();
		this.totalHits = c.getTotalHits();
		this.hits = c.getHits();
		this.deaths = c.getDeaths();
		this.totalKills = c.getTotalKills();
		this.kills = c.getKills();
		this.ticksAlive = c.getTicksAlive();
		this.totalShieldsAcquired = c.getTotalShieldsAcquired();
		this.shieldsAcquired = c.getShieldsAcquired();
		this.totalObstacleCollisions = c.getTotalObstacleCollisions();
		this.obstaclesCollisions = c.getObstacleCollisions();
		this.totalWallCollisions = c.getTotalWallCollisions();
		this.wallCollisions = c.getWallCollisions();
	}
}