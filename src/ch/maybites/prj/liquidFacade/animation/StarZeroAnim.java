package ch.maybites.prj.liquidFacade.animation;

import gestalt.shape.Color;
import processing.core.PGraphics;

public class StarZeroAnim extends StarAnimator {
	Color color;
	
	public StarZeroAnim(){
		color = new Color(1f, 0, 0, 1f);
		;
	}
	
	public void hit() {
		m_star.water.drawPebble(color, m_star.getX(), m_star.getY());
	}

	public void step(float dt) {
		;
	}

	public void draw(PGraphics applet) {
		;
	}

}
