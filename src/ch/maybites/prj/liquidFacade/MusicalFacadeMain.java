/*
 * Musical Facade
 *
 * Copyright (C) 2011 Martin Froehlich & Others
 * *
 * This class is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This class is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * {@link http://www.gnu.org/licenses/lgpl.html}
 *
 */

package ch.maybites.prj.liquidFacade;

import java.util.ArrayList;

import org.jbox2d.dynamics.Body;

import ch.maybites.prj.liquidFacade.fisica.FPebble;
import ch.maybites.prj.liquidFacade.fisica.FWindow;
import ch.maybites.prj.liquidFacade.gestalt.water.*;
import ch.maybites.tools.*;
import ch.maybites.tools.mathematik.Vector2i;

import gestalt.Gestalt;
import gestalt.p5.*;
import processing.core.*;
import fisica.*;

import oscP5.*;
import netP5.*;


public class MusicalFacadeMain extends PApplet {
	private static final long serialVersionUID = 1L;
	public final static String WINDOW_BODY_NAME = "window_";
	public final static String STAR_BODY_NAME = "star_";
	private final int BALL_HORIZONT = 250;

	FWorld world;
	GestaltPlugIn gestalt;
	WaterSurface water;
	PShape schloss;
	OscP5Xtended oscP5;
	StarManager starManager;
	
	Vector2i startSel, endSel;
	PFont systemfont;
	
	float angleX, angleY, transX, transY, transZ;

	public void setup() {
		size(1920, 1080, OPENGL);
		//size(1440, 810, OPENGL);
		frame.setLocation(1440, 0);
		systemfont = loadFont("font/SystemFont.vlw");
		textFont(systemfont, 18); 

		GlobalPrefs.getInstance().setDataPath(this.dataPath(""));
		this.frameRate(60f);
		
		Canvas.setup(this);
		gestalt = Canvas.getInstance().getPlugin();
		gestalt.drawBeforeProcessing(true);
		
		camera(width/2.0f, height/2.0f, 1060, width/2.0f, height/2.0f, 0f, 0f, 1f, 0f);

		water = new WaterSurface(width, height);
		water.waterviewDistance(-162);

		Fisica.init(this);

		world = new FWorld();
		world.setGravity(0, 200);

		starManager = new StarManager(water, systemfont);

		oscP5 = new OscP5Xtended(this,12321);
		oscP5.plug(this,"createFWindow","/fisica/create");


		schloss = loadShape("vector/SchlossFrontEinfach.svg");
		
	
		background(0);

	}
	
	public void oscEvent(OscMessage theOscMessage) {
		  /* with theOscMessage.isPlugged() you check if the osc message has already been
		   * forwarded to a plugged method. if theOscMessage.isPlugged()==true, it has already 
		   * been forwared to another method in your sketch. theOscMessage.isPlugged() can 
		   * be used for double posting but is not required.
		  */  
		  if(theOscMessage.isPlugged()==false) {
		  /* print the address pattern and the typetag of the received OscMessage */
		  println("### received an osc message.");
		  println("### addrpattern\t"+theOscMessage.addrPattern());
		  println("### typetag\t"+theOscMessage.typetag());
		  }
	}
	
	public void setWindowsPos(String _name, int posX, int posY, int sizeX, int sizeY){
		FWindow window = (FWindow) world.hasBodyWidthName(_name);
		if(window != null){
			world.remove(window);
			window.setPosition(posX, posY);
			window.setHeight(sizeY);
			window.setWidth(sizeY);
			world.add(window);
		}
	}
		
	public void createFWindow(String _name, String _type, String _adress, int posX, int posY, int sizeX, int sizeY){
		  // Must remove from world and read to change the size
		FBody body = world.hasBodyWidthName(_name);
		if(body != null){
			world.remove(body);
			oscP5.unplug(body);
		}
		FWindow window = new FWindow(water);
		window.setName(_name);
		window.setAddress(_name, _adress);
		window.setStaticBody(true);
		window.setRestitution(0.9f);
		window.setPosition(posX, posY);
		window.setWidth(sizeX);
		window.setHeight(sizeY);
		oscP5.plug(window,"setPosition","/fisica/setPos");
		oscP5.plug(window,"setAddress","/fisica/setAddress");
		oscP5.plug(window,"setCustom","/fisica/setCustom");
		world.add(window);
	}

	public void deleteFWindow(String _name){
		  // Must remove from world and readd to change the size
		FBody del = world.hasBodyWidthName(_name);
		if(del != null)
			world.remove(del);
	}

	public void draw() {
		oscP5.releaseMessages();
		stroke(255, 0, 0);
		line(0, BALL_HORIZONT, 1920, BALL_HORIZONT);

		if(startSel != null){
			noFill();
			this.rect(startSel.x, startSel.y, endSel.x - startSel.x, endSel.y - startSel.y);
		}
		
		shape(schloss, 0, 0, 1920, 1080);
				
		starManager.step(world);
		starManager.draw(this);

		world.step(1f / 240f);
		world.draw(this);
	}

	public void keyPressed() {
		switch (keyCode) {
		case UP:
			break;
		case DOWN:
			break;
		}
		switch (key) {
		case 'D':
			starManager.delAll();
			break;
		case 'r':
			createRandomStarGenerators(5);
			break;
		}
	}
	
	private void createRandomStarGenerators(int _number){
		for(int i = 0; i < _number; i++){
			starManager.addStarCreator((int)random(0, 1920), (int)random(0, BALL_HORIZONT), (int)random(0, 10), (int)random(1000, 5000));
		}
	}
	
	public void mousePressed() {
		if(this.keyPressed){
			int pressedKey = (int) key - 48;
			if(pressedKey >= 0 && pressedKey < 10){
				starManager.addStarCreator(mouseX, mouseY, pressedKey, abs(BALL_HORIZONT- mouseY) + 20 * 100);
			}else{
				switch (key) {
				case 'd':
					startSel = new Vector2i(mouseX, mouseY);
					endSel = new Vector2i(mouseX, mouseY);
					break;
				}
			}
		}
	}

	public void mouseDragged() {
		if(startSel != null){
			endSel = new Vector2i(mouseX, mouseY);
		}
	}

	public void mouseReleased() {
		if(startSel != null){
			starManager.delStar(min(startSel.x, endSel.x), min(startSel.y, endSel.y), max(startSel.x, endSel.x), max(startSel.y, endSel.y));
			startSel = null;
			endSel = null;
		}
	}

	public void contactStarted(FContact contacto) {
		FBody body1 = contacto.getBody1();
		FBody body2 = contacto.getBody2();
		if(body1.getName() != null && body2.getName() != null){
			if(body1.getName().startsWith(WINDOW_BODY_NAME))
				print("body1 is window - ");
			if(body1.getName().startsWith(STAR_BODY_NAME))
				((FPebble)body1).hit();
			if(body2.getName().startsWith(WINDOW_BODY_NAME))
				println("body2 is window");
			if(body2.getName().startsWith(STAR_BODY_NAME))
				((FPebble)body2).hit();
		} else {
			println("body without name!!?");
		}
		
	}

	void contactPersisted(FContact contact) {
		   // Draw in blue an ellipse where the contact took place
		   fill(0, 0, 170);
		   ellipse(contact.getX(), contact.getY(), 10, 10);
		 }

	public void contactEnded(FContact contacto) {
		FBody cuerpo1 = contacto.getBody1();
		cuerpo1.setFill(255);
	}

	static public void main(String args[]) {
		//PApplet.main(new String[] { "ch.maybites.prj.liquidFacade.MusicalFacadeMain" });
		PApplet.main( new String[] { "--display=2", "--present", "ch.maybites.prj.liquidFacade.MusicalFacadeMain" } );
	}

	public void destroy() {
		// myConnector.deconnect();
		super.destroy();
	}
}
