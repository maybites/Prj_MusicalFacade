package ch.maybites.prj.liquidFacade.fisica;

import org.jbox2d.collision.shapes.CircleDef;
import org.jbox2d.collision.shapes.ShapeDef;

import processing.core.PGraphics;
import ch.maybites.prj.liquidFacade.animation.Animator;
import ch.maybites.prj.liquidFacade.animation.StarAnimator;
import ch.maybites.prj.liquidFacade.gestalt.water.WaterSurface;
import fisica.FBlob;
import fisica.FBody;
import fisica.FBox;
import fisica.FLine;
import fisica.FPoly;
import fisica.Fisica;

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
public class FStar extends FBody {
	protected float m_size;
	protected int m_type;
	StarAnimator m_animator;
	WaterSurface m_water;
	
	protected String m_address;

	/**
	 * Constructs a circular body that can be added to a world.
	 * 
	 * @param _size
	 *            the size of the star
	 */
	public FStar(String _address, int _size, int _type, WaterSurface _water) {
		super();
		m_size = Fisica.screenToWorld(_size);
		m_water = _water;
		m_address = _address;
		m_type = _type;
	}

	protected ShapeDef getShapeDef() {
		CircleDef pd = new CircleDef();
		pd.radius = m_size / 2.0f;
		pd.density = m_density;
		pd.friction = m_friction;
		pd.restitution = m_restitution;
		pd.isSensor = m_sensor;
		return pd;
	}
	
	public void registerAniator(StarAnimator _animator){
		m_animator = _animator;
		m_animator.register(this);
	}

	/**
	 * Returns the size of the circle.
	 * 
	 * @usage Bodies
	 * @return the size of the circle
	 */
	public float getSize() {
		return Fisica.worldToScreen(m_size);
	}

	/**
	 * Sets the size of the circle. Under the hood the body is removed and
	 * readded to the world.
	 * 
	 * @usage Bodies
	 * @param size
	 *            the size of the circle
	 */
	public void setSize(float size) {
		m_size = Fisica.screenToWorld(size);

		this.recreateInWorld();
	}

	/**
	 * Sets the type of the star. 
	 * 
	 * @usage Bodies
	 * @param type
	 *            the type of the star
	 */
	public void setType(int _type) {
		m_type = _type;
	}

	/**
	 * Gets the type of the star. 
	 * 
	 * @usage Bodies
	 * @returns type
	 *            the type of the star
	 */
	public int getType() {
		return m_type;
	}

	/**
	 * Gets the address of the stars sender. 
	 * 
	 * @usage Bodies
	 * @returns address
	 *            the address of the sender of the star
	 */
	public String getAddress() {
		return m_address;
	}

	public void hit() {
		m_water.drawPebble(getX(), getY());
	}

	public void draw(PGraphics applet) {
		preDraw(applet);

		if (m_image != null) {
			drawImage(applet);
		} else {
			applet.ellipse(0, 0, getSize(), getSize());
			applet.line(-getSize(), 0, getSize(), 0);
		}

		postDraw(applet);
	}

}
