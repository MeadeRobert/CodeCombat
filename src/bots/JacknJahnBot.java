package bots;

import java.util.ArrayList;
import lib.Vector2;
import world.Circle;
import world.Mine;
import world.Obstacle;
import world.Shield;
import world.Sprite;

public class JacknJahnBot extends Mind{

	public JacknJahnBot(JacknJahnBot t){
		super(t);
	}

	public JacknJahnBot(Circle c, float variance, float mean){
		super(c, variance, mean);
	}

	public Mind copy(){
		return new JacknJahnBot(this);
	}

	@Override
	public void think(){		
		if(isAlive()){
			ArrayList<Sprite> inView = requestInView();
			Sprite target = null;
			Sprite shield = null;
			int priorityQueue = 0;	

			if(!super.isShielded()){ //checks if should shield
				for (Sprite s : inView){
					if (s instanceof Shield){
						if(s.getPosition().dist(getPosition()) < 300){ 
							priorityQueue = 3;
						}
					}
				}
			}
			for (Sprite s : inView){ //checks if should kill
				if (s instanceof Circle){ 
					for (Sprite t : inView){
						if (t instanceof Obstacle){
							if(s.getPosition().dist(getPosition()) > t.getPosition().dist(getPosition())){
								priorityQueue = 2;
							}
						}
						else{
							priorityQueue = 2;
						}
					}
				}
			}
			for (Sprite s : inView){  //checks if should avoid
				if (s instanceof Obstacle){
					if(s.getPosition().dist(getPosition()) < 75){ //avoid obstacle
						priorityQueue = 1;
					}
				}
				if (s instanceof Mine){
					if(s.getPosition().dist(getPosition()) < 200){ //avoid mine
						priorityQueue = 1;
					}
				}	
			}	
			if (super.getVelocity().mag() < 1){ //stuck on obstacle
				priorityQueue = 1;
			}
			switch (priorityQueue){
			case 1://avoidance
					turn((float) Circle.MAX_TURNING_ANGLE);
				break;
			case 2: //murder
				for (Sprite c : inView){
					if (c instanceof Circle){ 
						if(target != null && c.getPosition().dist(getPosition()) < target.getPosition().dist(getPosition())){
							target = c;
						}
						else if (target == null);{
							target = c;
						}
						if(target != null){
							Vector2 direction = target.getPosition().sub(calcEyePosition(getPosition(), getVelocity()));
							Vector2 left = new Vector2(1, getVelocity().angle() - Circle.FOV / 2, true);
							Vector2 right = new Vector2(1, getVelocity().angle() + Circle.FOV / 2, true);
							if(Math.acos(direction.dot(left) / (direction.mag())) - Math.acos(direction.dot(right) / (direction.mag())) > 0)
							{
								turn((float) Math.acos(direction.dot(getVelocity()) / direction.mag() / getVelocity().mag()));
							}
							else
							{
								turn((float) -Math.acos(direction.dot(getVelocity()) / direction.mag() / getVelocity().mag()));
							}
							shoot();
						}
					}	
				}
				break;
			case 3://get shield
				for (Sprite b : inView){
					if (b instanceof Shield){ 
						if(shield != null && b.getPosition().dist(getPosition()) < shield.getPosition().dist(getPosition())){
							shield = b;
						}
						else if (shield == null);{
							shield = b;
						}
						if(shield != null){
							Vector2 direction = shield.getPosition().sub(calcEyePosition(getPosition(), getVelocity()));
							Vector2 left = new Vector2(1, getVelocity().angle() - Circle.FOV / 2, true);
							Vector2 right = new Vector2(1, getVelocity().angle() + Circle.FOV / 2, true);
							if(Math.acos(direction.dot(left) / (direction.mag())) - Math.acos(direction.dot(right) / (direction.mag())) > 0)
							{
								turn((float) Math.acos(direction.dot(getVelocity()) / direction.mag() / getVelocity().mag()));
							}
							else
							{
								turn((float) -Math.acos(direction.dot(getVelocity()) / direction.mag() / getVelocity().mag()));
							}
						}
					}
				}
				break;
			default: turn((float) Circle.MAX_TURNING_ANGLE);
			}	
		}
		else{
			getStats();
		}
	}

	@Override
	public String toString()
	{
		return "JacknJahnBot";
	}
}

