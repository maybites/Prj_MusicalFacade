package ch.maybites.prj.liquidFacade.animation;

import java.awt.Color;

import fisica.FCircle;
import processing.core.PGraphics;

public class StarOneAnim extends StarAnimator {
	
	int transparency1 = 255;
	int transparency2 = 100;
	
	int red = 242;
	int green = 149;
	int blue = 28;
	
	Color c = new Color(red, green, blue);
	
	boolean hit = false;
	
	public StarOneAnim(){
		;
	}
	
	public void hit() {
		transparency2 = 250;
		c = new Color(250, 250, 250);
		hit = true;
	}

	public void step(float dt) {
		if(hit){
			int newred = red + (int)((float)(c.getRed() - red) * .95f);
			int newgreen = green + (int)((float)(c.getGreen() - green) * .95f);
			int newblue = blue + (int)((float)(c.getBlue() - blue) * .95f);
			c = new Color(newred, newgreen, newblue);
			transparency1 = transparency1 - 1;
			transparency2 = transparency2 - 1;
		}
	}

	public void draw(PGraphics applet) {
		applet.fill(c.getRed(), c.getGreen(), c.getBlue(), transparency2);
		applet.ellipse(0, 0, m_star.getSize(), m_star.getSize());
		applet.fill(c.getRed(), c.getGreen(), c.getBlue(), transparency1);
		applet.ellipse(0, 0, m_star.getSize()/2, m_star.getSize()/2);
	}

}
