package ch.maybites.prj.liquidFacade.animation;

import java.awt.Color;

import processing.core.PGraphics;

public class StarThreeAnim extends StarAnimator {

	
	int red = 194;
	int green = 65;
	int blue = 141;
	
	int transP2 = 50;
	
	Color c = new Color(red, green, blue);
	int transparency1 = 255;
	int transparency2 = transP2;
	
	int hit = 0;

	public StarThreeAnim(){
		;
	}
	
	public void hit() {
		transparency2 = 250;
		c = new Color(250, 250, 250);
		hit = 20;
	}

	public void step(float dt) {
		if(hit-- > 0){
			int newred = red + (int)((float)(c.getRed() - red) * .95f);
			int newgreen = green + (int)((float)(c.getGreen() - green) * .95f);
			int newblue = blue + (int)((float)(c.getBlue() - blue) * .95f);
			c = new Color(newred, newgreen, newblue);
			transparency2 = transP2 + (int)((float)(transparency2 - transP2) *.95f);
		}
	}

	public void draw(PGraphics applet) {
		applet.fill(c.getRed(), c.getGreen(), c.getBlue(), transparency2);
		applet.ellipse(0, 0, m_star.getSize(), m_star.getSize());
		applet.fill(c.getRed(), c.getGreen(), c.getBlue(), transparency1);
		applet.ellipse(0, 0, m_star.getSize()/2, m_star.getSize()/2);
		applet.stroke(150);
		applet.strokeWeight(0.5f);
		applet.line(-m_star.getSize()*.7f, 0, m_star.getSize()*.7f, 0);
		applet.line(0, -m_star.getSize()*.7f, 0, m_star.getSize()*.7f);
		applet.line(-m_star.getSize()*.7f, -m_star.getSize()*.7f, m_star.getSize()*.7f, m_star.getSize()*.7f);
		applet.line(-m_star.getSize()*.7f, m_star.getSize()*.7f, m_star.getSize()*.7f, -m_star.getSize()*.7f);
		applet.stroke(250);
		applet.strokeWeight(1f);
		applet.line(-m_star.getSize()*.5f, 0, m_star.getSize()*.5f, 0);
		applet.line(0, -m_star.getSize()*.5f, 0, m_star.getSize()*.5f);
		applet.line(-m_star.getSize()*.5f, -m_star.getSize()*.5f, m_star.getSize()*.5f, m_star.getSize()*.5f);
		applet.line(-m_star.getSize()*.5f, m_star.getSize()*.5f, m_star.getSize()*.5f, -m_star.getSize()*.5f);
	}

}
