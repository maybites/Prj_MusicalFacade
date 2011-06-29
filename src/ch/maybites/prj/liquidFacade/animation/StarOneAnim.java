package ch.maybites.prj.liquidFacade.animation;

import fisica.FCircle;
import processing.core.PGraphics;

public class StarOneAnim extends StarAnimator {

	public StarOneAnim(){
		;
	}
	
	public void hit() {
		for(int i = 0; i < 10; i++){
			FCircle bang = new FCircle(4f);
			bang.setDamping(.5f);
			bang.setName("Star01Parts");
			bang.setDensity(1f);
			bang.setSensor(false);
			bang.setGroupIndex(-1);
			bang.setPosition(m_star.getX(), m_star.getY());
			bang.setVelocity(-m_star.getVelocityX() * 4f * (float)(Math.random() - .5f), -m_star.getVelocityY() * 2f * (float)(Math.random() - .5f));
			m_star.getWorld().add(bang);
		}
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
