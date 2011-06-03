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

import gestalt.Gestalt;
import gestalt.p5.*;
import processing.core.*;
import fisica.*;

import oscP5.*;
import netP5.*;


public class MusicalFacadeMain extends PApplet {
	private static final long serialVersionUID = 1L;

	private int oscID;

	FWorld world;
	FBox ping;
	FPoly poly;
	
	float x, y;
	GestaltPlugIn gestalt;
	private final String WINDOW_BODY_NAME = "window_";

	WaterSurface water;
	float angleX, angleY, transX, transY, transZ;
	PShape schloss;
	OscP5Xtended oscP5;
	
	public void setup() {
		size(1920, 1080, OPENGL);
		//size(1440, 810, OPENGL);
		frame.setLocation(1440, 0);
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
		/*
		fill(0, 100);
		noStroke();
		rect(0, 0, width, height);
		*/
		//background(0);
		shape(schloss, 0, 0, 1920, 1080);
		
		//println(mouseY - height/2 + " " + mouseX);
		//water.waterviewDistance(mouseY - height/2);
		//gestalt.camera().position().z = mouseX - width/2;
		//gestalt.camera().fovy = mouseX;

		if ((frameCount % 24) == 0) {
			FPebble bolita = new FPebble(8, water);
			bolita.setName("BALL");
			bolita.setNoStroke();
			bolita.setFill(255);
			bolita.setPosition(400, 20);
			bolita.setVelocity(100, 200);
			bolita.setRestitution(0.9f);
			bolita.setDamping(0);
			world.add(bolita);
		}

		world.step(1f / 240f);
		world.draw(this);
		//water.drawBoxes(0f);
	}

	public void keyPressed() {
		switch (keyCode) {
		case UP:
			break;
		case DOWN:
			break;
		}
		switch (key) {
		case 's':
			break;
		case 'a':
			break;
		}
	}
	
	public void mousePressed() {
		  if (world.getBody(mouseX, mouseY) != null) {
		    return;
		  }

		  poly = new FPoly();
		  poly.setName("BOX");
		  poly.setStrokeWeight(3);
		  poly.setFill(120, 30, 90);
		  poly.setDensity(0);
		  poly.setRestitution(0.5f);
		  poly.vertex(mouseX, mouseY);
		}

	public void mouseDragged() {
		  if (poly!=null) {
		    poly.vertex(mouseX, mouseY);
		  }
		}

	public void mouseReleased() {
		  if (poly!=null) {
		    world.add(poly);
		    poly = null;
		  }
		}

		/**
	public void mousePressed() {
		caja = new FBox(4, 4);
		caja.setStaticBody(true);
		caja.setRestitution(0.9f);
		mundo.add(caja);
		

		x = mouseX;
		y = mouseY;
	}

	public void mouseDragged() {
		if (caja == null) {
			return;
		}

		// Must remove from world and readd to change the size
		mundo.remove(caja);
		float ang = atan2(y - mouseY, x - mouseX);
		caja.setRotation(ang);
		caja.setPosition(x + (mouseX - x) / 2.0f, y + (mouseY - y) / 2.0f);
		caja.setWidth(dist(mouseX, mouseY, x, y));
		mundo.add(caja);

	}
**/

	public void contactStarted(FContact contacto) {
		FCircle ball;
		FPoly box;
		FBody cuerpo1 = contacto.getBody1();
		if(cuerpo1.getName().equals("BALL")){
			ball = (FCircle)cuerpo1;
			ball.setFill(0, 0, 255);
		}else if(cuerpo1.getName().equals("BOX")){
			box = (FPoly)cuerpo1;
			box.setFill(255, 0, 0);
		}
		
		cuerpo1 = contacto.getBody2();
		if(cuerpo1.getName().equals("BALL")){
			ball = (FCircle)cuerpo1;
			ball.setFill(0, 0, 255);
		}else if(cuerpo1.getName().equals("BOX")){
			box = (FPoly)cuerpo1;
			box.setFill(255, 0, 0);
		}
		
		//cuerpo1.setFill(255, 0, 0);

		noFill();
		stroke(255);
		ellipse(contacto.getX(), contacto.getY(), 30, 30);
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
