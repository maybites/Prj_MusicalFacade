package ch.maybites.prj.liquidFacade.animation;

import processing.core.PGraphics;

public class StarTwoAnim extends StarAnimator {

	public StarTwoAnim(){
		;
	}
	
	public void hit() {
		;
	}

	public void step(float dt) {
		;
	}

	public void draw(PGraphics applet) {
		applet.fill(50);
		applet.ellipse(0, 0, m_star.getSize(), m_star.getSize());
		applet.fill(150);
		applet.ellipse(0, 0, m_star.getSize()/2, m_star.getSize()/2);
		applet.stroke(150);
		applet.strokeWeight(0.5f);
		applet.line(-m_star.getSize(), 0, m_star.getSize(), 0);
		applet.line(0, -m_star.getSize(), 0, m_star.getSize());
		applet.stroke(250);
		applet.strokeWeight(1f);
		applet.line(-m_star.getSize()*.5f, 0, m_star.getSize()*.5f, 0);
		applet.line(0, -m_star.getSize()*.5f, 0, m_star.getSize()*.5f);
	}

}
