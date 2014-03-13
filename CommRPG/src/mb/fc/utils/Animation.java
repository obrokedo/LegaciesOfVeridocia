package mb.fc.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class Animation implements Serializable
{	
	private static final long serialVersionUID = 1L;
	
	public String name;	
	public ArrayList<AnimFrame> frames;	
	
	public Animation(String name) {
		super();
		this.name = name;
		this.frames = new ArrayList<AnimFrame>();		
	}
}
