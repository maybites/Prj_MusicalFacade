package ch.maybites.prj.liquidFacade.fisica;

import org.jbox2d.collision.shapes.CircleDef;
import org.jbox2d.collision.shapes.ShapeDef;

import processing.core.PGraphics;
import ch.maybites.prj.liquidFacade.StarManager.StarCreator;
import ch.maybites.prj.liquidFacade.animation.Animator;
import ch.maybites.prj.liquidFacade.animation.StarAnimator;
import ch.maybites.prj.liquidFacade.gestalt.water.WaterSurface;
import ch.maybites.tools.Debugger;
import fisica.FBlob;
import fisica.FBody;
import fisica.FBox;
import fisica.FLine;
import fisica.FPoly;
import fisica.FWorld;
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
	public float size;
	protected int m_type;
	StarAnimator m_animator;
	StarCreator m_creator;
	int m_hitCounter;
	public WaterSurface water;
	boolean m_isReactive;
	public int lastWindowHit;
	
	protected String m_address;

	/**
	 * Constructs a circular body that can be added to a world.
	 * 
	 * @param _size
	 *            the size of the star
	 */
	public FStar(String _address, int _size, int _type, WaterSurface _water) {
		super();
		size = Fisica.screenToWorld(_size);
		m_address = _address;
		m_type = _type;
		water = _water;
		m_isReactive = true;
		lastWindowHit = -1;
		m_hitCounter = 10;
		if(_type == 1)
			m_hitCounter = 1;
		else if(_type == 2)
			setSensor(true);
		else if(_type == 3)
			m_hitCounter = 3;
	}

	protected ShapeDef getShapeDef() {
		CircleDef pd = new CircleDef();
		pd.radius = size / 2.0f;
		pd.density = m_density;
		pd.friction = m_friction;
		pd.restitution = m_restitution;
		pd.isSensor = m_sensor;
		return pd;
	}
	
	public FWorld getWorld(){
		return this.m_world;
	}
	
	public void registerAnimator(StarAnimator _animator){
		m_animator = _animator;
		m_animator.register(this);
	}
	
	public void registerCreator(StarCreator _creator){
		m_creator = _creator;
	}

	public boolean isReactive(){
		return m_isReactive;
	}

	public boolean isReactive(int _windowID){
		if(m_type != 2 || 
				m_type == 2 && lastWindowHit != _windowID){
			return m_isReactive;
		}
		return false;
	}

	/**
	 * Returns the size of the circle.
	 * 
	 * @usage Bodies
	 * @return the size of the circle
	 */
	public float getSize() {
		return Fisica.worldToScreen(size);
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
		size = Fisica.screenToWorld(size);

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

	public void step(float _dt) {
		//_water.drawPebble(getX(), getY());
		if(m_animator != null){
			m_animator.step(_dt);
		}
	}

	public void hit(int _windowID) {
		//_water.drawPebble(getX(), getY());
		if(m_animator != null){
			m_animator.hit();
		}
		if(--m_hitCounter == 0){
			remove();
		}
		lastWindowHit = _windowID;
	}

	public void remove() {
		if(m_isReactive){
			if(m_creator != null){
				m_creator.starRemoved();
			}
			setSensor(true);
			m_isReactive = false;
		//Debugger.getInstance().debugMessage(this.getClass(), "removed star");
		}
	}

	public void draw(PGraphics applet) {
		preDraw(applet);
		
		if(m_animator != null){
			m_animator.draw(applet);
		}

		postDraw(applet);
	}

}
