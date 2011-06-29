/*
  Part of the Fisica library - http://www.ricardmarxer.com/fisica

  Copyright (c) 2009 - 2010 Ricard Marxer

  Fisica is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.maybites.prj.liquidFacade.fisica;

import org.jbox2d.common.*;
import org.jbox2d.collision.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.dynamics.*;

import ch.maybites.prj.liquidFacade.animation.SimpleColorWindowAnimator;
import ch.maybites.prj.liquidFacade.animation.WindowAnimator;
import ch.maybites.prj.liquidFacade.gestalt.water.WaterSurface;

import fisica.FBody;
import fisica.Fisica;
import gestalt.shape.Color;

import processing.core.*;

/**
 * Represents a rectangular body that can be added to a world.
 * 
 * <pre>
 * {
 * 	&#064;code
 * 	FBox myBox = new FBox(40, 20);
 * 	world.add(myBox);
 * }
 * </pre>
 * 
 * @usage Bodies
 * @see FCircle
 * @see FBlob
 * @see FPoly
 * @see FLine
 */
public class FWindow extends FBody {
	protected float m_height = 20f;
	protected float m_width = 20f;
	int m_id;
	public Color color;
	WindowAnimator m_animator;
	
	protected String m_adress;
	public WaterSurface water;
    float factorX, factorY;

	protected ShapeDef getShapeDef() {
		PolygonDef pd = new PolygonDef();
		pd.setAsBox(m_width / 2.0f, m_height / 2.0f);
		pd.density = m_density;
		pd.friction = m_friction;
		pd.restitution = m_restitution;
		pd.isSensor = m_sensor;
		return pd;
	}

	/**
	 * Constructs a rectangular body that can be added to a world.
	 * 
	 * @param width
	 *            the width of the rectangle
	 * @param height
	 *            the height of the rectangle
	 */
	public FWindow(WaterSurface _water, float _width, float _height) {
		super();
		factorX = _width / 1920f;
		factorY = _height / 1080f;
		water = _water;
		color = new Color(0.5f, 0.5f, 1f, 1);
		m_animator = new SimpleColorWindowAnimator();
	}
	
	/**
	 * Sets the type of the window.
	 * 
	 * @usage Bodies
	 * @return nothing
	 */
	public void setType(String _name, String _type) {
		if (m_name.equals(_name)) {
			if(_type.equals("simplePing")){
				m_animator = new SimpleColorWindowAnimator();
			}
			m_animator.register(this);
		}
	}

	/**
	 * Returns the height of the rectangle.
	 * 
	 * @usage Bodies
	 * @see #getWidth()
	 * @return the height of the rectangle
	 */
	public float getHeight() {
		// only for FBox
		return Fisica.worldToScreen(m_height);
	}

	/**
	 * Returns the width of the rectangle.
	 * 
	 * @usage Bodies
	 * @see #getHeight()
	 * @return the width of the rectangle
	 */
	public float getWidth() {
		// only for FBox
		return Fisica.worldToScreen(m_width);
	}

	/**
	 * Sets the soundadress of the window.
	 * 
	 * @usage Bodies
	 * @return nothing
	 */
	public void setAddress(String _name, String _adress) {
		if (m_name.equals(_name)) {
			m_adress = _adress;
		}
	}

	/**
	 * Gets the soundadress of the window.
	 * 
	 * @usage Bodies
	 * @return nothing
	 */
	public String getAddress() {
		return m_adress;
	}

	/**
	 * Sets the position of the window.
	 * 
	 * @usage Bodies
	 * @return nothing
	 */
	public void setPosition(String _name, int posX, int posY, int sizeX,
			int sizeY) {
		if (m_name.equals(_name)) {
			setPosition(posX, posY);
			setHeight(sizeY);
			setWidth(sizeX);
			color = new Color(getX() / 1920, 0.5f, 0.5f, 1);
		}
	}
	
	public void setPosition(int posX, int posY){
		super.setPosition((float)posX * factorX, (float)posY * factorY);
	}

	/**
	 * Sets the height of the rectangle. Under the hood the body is removed and
	 * readded to the world.
	 * 
	 * @usage Bodies
	 * @see #getWidth()
	 * @return the height of the rectangle
	 */
	public void setHeight(float height) {
		m_height = Fisica.screenToWorld(height * factorY);

		this.recreateInWorld();
	}

	/**
	 * Sets the width of the rectangle. Under the hood the body is removed and
	 * readded to the world.
	 * 
	 * @usage Bodies
	 * @see #getWidth()
	 * @return the width of the rectangle
	 */
	public void setWidth(float width) {
		m_width = Fisica.screenToWorld(width * factorX);

		this.recreateInWorld();
	}
	
	public void setCustom(int _id, String _name, int _red, int _green, int _blue){
		if (m_name.equals(_name)) {
			color = new Color((float)_red / 255, (float)_green / 255, (float)_blue / 255, 1);
		}
	}

	public void hit() {
		if(m_animator != null)
			m_animator.hit();
	}
	
	public void setID(int _id) {
		m_id = _id;
	}
	
	public int getID() {
		return m_id;
	}

	public void step(float _dt){
		if(m_animator != null)
			m_animator.step(_dt);
	}
	
	public void draw(PGraphics applet) {

		preDraw(applet);

		if(m_animator != null)
			m_animator.draw(applet);

		/*
		if (m_image != null) {
			drawImage(applet);
		} else {
			water.drawCenterClearBox((int) getX(), (int) getY(),
					(int) getWidth() - 3, (int) getHeight() - 3);
			// m_water.drawCenterBox((int)getX(), (int)getY(),
			// (int)getWidth()-2, (int)getHeight()-2);
			// applet.rect(0, 0, getWidth(), getHeight());
		}
		*/
		
		postDraw(applet);
	}

}
