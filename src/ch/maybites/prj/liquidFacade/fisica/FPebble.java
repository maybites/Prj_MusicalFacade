package ch.maybites.prj.liquidFacade.fisica;

import processing.core.PGraphics;
import ch.maybites.prj.liquidFacade.gestalt.water.WaterSurface;
import fisica.FCircle;

public class FPebble extends FCircle {

	WaterSurface water;

	public FPebble(int size, WaterSurface _water) {
		super(size);
		water = _water;
	}

	public void hit(){
		water.drawPebble(getX(), getY());
	}

	public void draw(PGraphics applet) {
		preDraw(applet);

		if (m_image != null) {
			drawImage(applet);
		} else {
			applet.ellipse(0, 0, getSize(), getSize());
			applet.line(0, 0, getSize() / 2, 0);
		}
		
		postDraw(applet);
	}

}
