package ch.maybites.prj.liquidFacade.animation;

import processing.core.PApplet;
import processing.core.PGraphics;

public class SimpleColorWindowAnimator extends WindowAnimator{

	public void hit() {
		this.m_window.water.drawCenterBox(m_window.color, 20, (int) m_window.getX(), (int) m_window.getY(),
				(int) m_window.getWidth(), (int) m_window.getHeight());
	}

	public void step(float dt) {
		// TODO Auto-generated method stub
		
	}

	public void draw(PGraphics canvas) {
		this.m_window.water.drawCenterClearBox((int) m_window.getX(), (int) m_window.getY(),
				(int) m_window.getWidth() - 3, (int) m_window.getHeight() - 3);
	}

}
