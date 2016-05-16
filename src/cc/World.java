package cc;
import java.awt.Graphics;
import java.util.ArrayList;

public class World
{
	private ArrayList<Sprite> sprites;
	
	public World()
	{
		sprites = new ArrayList<Sprite>();
		sprites.add(new Circle(400, 300, this));
	}
	
	public void update()
	{
		for(Sprite s : sprites )
		{
			s.update();
		}
	}
	
	public void paint(Graphics g)
	{
		for(Sprite s : sprites )
		{
			s.paint(g);
		}
	}
}