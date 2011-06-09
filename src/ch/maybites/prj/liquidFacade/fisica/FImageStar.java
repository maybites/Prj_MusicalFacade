package ch.maybites.prj.liquidFacade.fisica;

import org.jbox2d.collision.shapes.CircleDef;
import org.jbox2d.collision.shapes.ShapeDef;

import processing.core.PGraphics;
import ch.maybites.prj.liquidFacade.gestalt.water.WaterSurface;
import ch.maybites.tools.Debugger;
import fisica.FBlob;
import fisica.FBody;
import fisica.FBox;
import fisica.FLine;
import fisica.FPoly;
import fisica.Fisica;

import geomerative.*;

/**
 * Represents a star body that can be added to a world.
 * 
 * <pre>
 * {
 * 	&#064;code
 * 	FPebble myCircle = new FPebble(40);
 * 	world.add(myCircle);
 * }
 * </pre>
 * 
 * @usage Bodies
 * @see FBox
 * @see FBlob
 * @see FPoly
 * @see FLine
 */
public class FImageStar extends FStar {
	protected float m_size;
	protected int m_type;

	WaterSurface m_water;
	
	RShape m_shape;

	protected String m_address;

	/**
	 * Constructs a circular body that can be added to a world.
	 * 
	 * @param _size
	 *            the size of the star
	 */
	public FImageStar(String _address, int _size, int _type, WaterSurface _water, String filename) {
		super(_address, _size, _type, _water);
		m_size = Fisica.screenToWorld(_size);
		m_water = _water;
		m_address = _address;
		m_type = _type;

		RShape fullSvg = RG.loadShape(filename);
	    m_shape = fullSvg.getChild("object");
	    
	    if (m_shape == null) {
	      Debugger.getInstance().debugMessage(this.getClass(), "ERROR: Couldn't find the shapes called 'object' in the SVG file.");
	      return;
	    }
	    
	    // Make the shapes fit in a rectangle of size (w, h)
	    // that is centered in 0
	    
	    m_shape.scale((float)_size/100);
	    m_shape.translate(-_size/2, -_size/2);

	    this.setNoFill();
	    this.setNoStroke();
	}

	public void hit() {
		m_water.drawPebble(getX(), getY());
	}

	public void draw(PGraphics applet) {
		preDraw(applet);

		if (m_image != null) {
			drawImage(applet);
		} else {
			//applet.ellipse(0, 0, getSize(), getSize());
		    m_shape.draw(applet);
			//applet.line(0, 0, getSize() / 2, 0);
		}

		postDraw(applet);
	}

}
