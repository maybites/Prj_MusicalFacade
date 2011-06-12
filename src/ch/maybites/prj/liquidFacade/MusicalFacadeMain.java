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

import ch.maybites.prj.liquidFacade.fisica.FStar;
import ch.maybites.prj.liquidFacade.fisica.FWindow;
import ch.maybites.prj.liquidFacade.gestalt.water.*;
import ch.maybites.tools.*;
import ch.maybites.tools.mathematik.Vector2i;

import gestalt.Gestalt;
import gestalt.p5.*;
import processing.core.*;
import fisica.*;
import geomerative.*;
import oscP5.*;
import netP5.*;

public class MusicalFacadeMain extends PApplet {
	private static final long serialVersionUID = 1L;
	public final static String WINDOW_BODY_NAME = "window_";
	public final static String STAR_BODY_NAME = "star_";
	public final static String CLEANUP_BODY_NAME = "clear";
	private final int BALL_HORIZONT = 260;
	
	//ServerIP 192.168.10.2 ServerPort 12421
	//IPhonePort 12521

	public final static String OSC_MAX_REFRESH = "/max/refresh";

	public final static String OSC_SOUND_WINDOW = "/sound/window";
	public final static String OSC_SOUND_STAR = "/sound/star";
	public final static String OSC_IPHONE_WINDOW = "/iphone_echo"; //<(int)id> <(int)windowid> <posX> <posY>
	public final static String OSC_IPHONE_STAR = "/iphone_star_date"; //<(int)id_eigen> <(int)id_ander> <posX> <posY>
	public final static String OSC_IPHONE_WAIT = "/iphone_wait"; //<(int)seconds>
	public final static String OSC_IPHONE_SERVERACKNOWLEDGE = "/iphone_serverisrunning"; //<(int)frequenz> ??

	public final static String OSC_FISICA_NEWSTAR = "/fisica_t_obj"; //<(int)id> <posX> <posY>
	public final static String OSC_FISICA_SERVERRUNNING = "/fisica_isrunning"; 

	public final static String OSC_FISICA_CREATE = "/fisica/create";
	public final static String OSC_FISICA_SETPOS = "/fisica/setPos";
	public final static String OSC_FISICA_SETADDRESS = "/fisica/setAddress";
	public final static String OSC_FISICA_SETCUSTOM = "/fisica/setCustom";
	public final static String OSC_FISICA_CREATESTAR = "/fisica/createstar";
	public final static String OSC_FISICA_WORLDSET = "/fisica/world/set";
	public final static String OSC_FISICA_STARSET = "/fisica/star/set";
	public final static String OSC_FISICA_STARALL = "/fisica/star/all";
	public final static String OSC_FISICA_IPHONECOORDINATES = "/fisica/iphone/coordinates"; // <(int)dx> <(int)dy> <(float)fx> <(float)fy>

	FWorld world;
	GestaltPlugIn gestalt;
	WaterSurface water;
	PShape schloss;
	OscP5Xtended oscP5;
	StarManager starManager;

	Vector2i startSel, endSel;
	PFont systemfont;

	float angleX, angleY, transX, transY, transZ;
	
	public int SimulationSpeed;
	public int SimulationGravity;
	public float WindowRestitution;

	int OSCsoundPort;
	int OSClistenPort;
	int OSCiPhonePort;
	String OSCsoundAddress;

	int OSCcontrolPort;
	String OSCcontrolAddress;

	static private int iPhoneDeltaX = 30;
	static private int iPhoneDeltaY = 30;
	static private float iPhoneFactorX = 2.4f;
	static private float iPhoneFactorY = 2.7f;

	public void setup() {
		size(1920, 1080, OPENGL);
		// size(1440, 810, OPENGL);

		GlobalPrefs.getInstance().setDataPath(this.dataPath(""));
		this.frameRate(60f);

		OSCsoundPort = GlobalPrefs.getInstance().getIntProperty("soundport", 12321);
		OSClistenPort = GlobalPrefs.getInstance().getIntProperty("listenport", 12321);
		OSCiPhonePort = GlobalPrefs.getInstance().getIntProperty("iphoneport", 12321);
		OSCsoundAddress = GlobalPrefs.getInstance().getStringProperty("soundaddress", "127.0.0.1");
		SimulationSpeed = GlobalPrefs.getInstance().getIntProperty("simulationspeed", 240);
		SimulationGravity = GlobalPrefs.getInstance().getIntProperty("simulationgravity", 200);
		WindowRestitution = GlobalPrefs.getInstance().getfloatProperty("windowrestitution", 0.9f);
		iPhoneDeltaX = GlobalPrefs.getInstance().getIntProperty("iPhoneDeltaX", 30);
		iPhoneDeltaY = GlobalPrefs.getInstance().getIntProperty("iPhoneDeltaY", 30);
		iPhoneFactorX = GlobalPrefs.getInstance().getfloatProperty("iPhoneFactorX", 2.4f);
		iPhoneFactorY = GlobalPrefs.getInstance().getfloatProperty("iPhoneFactorY", 2.7f);
		OSCcontrolPort = GlobalPrefs.getInstance().getIntProperty("OSCcontrolPort", 12221);
		OSCcontrolAddress = GlobalPrefs.getInstance().getStringProperty("OSCcontrolAddress", "127.0.0.1");

		int winPosX = GlobalPrefs.getInstance().getIntProperty("windowsposx", 1440);
		int winPosY = GlobalPrefs.getInstance().getIntProperty("windowsposy", 0);

		frame.setLocation(winPosX, winPosY);
		systemfont = loadFont("font/SystemFont.vlw");
		textFont(systemfont, 18);

		RG.init(this);
		RG.setPolygonizer(RG.ADAPTATIVE);

		Canvas.setup(this);
		gestalt = Canvas.getInstance().getPlugin();
		gestalt.drawBeforeProcessing(true);

		camera(width / 2.0f, height / 2.0f, 1060, width / 2.0f, height / 2.0f,
				0f, 0f, 1f, 0f);

		water = new WaterSurface(width, height);
		water.waterviewDistance(-162);

		Fisica.init(this);

		world = new FWorld();
		world.setGravity(0, SimulationGravity);

		starManager = new StarManager(water, systemfont);
		starManager.setStarProperties(
				GlobalPrefs.getInstance().getfloatProperty("starrestitution", 0.9f), 
				GlobalPrefs.getInstance().getfloatProperty("stardamping", 0.0f),
				GlobalPrefs.getInstance().getfloatProperty("stardensity", 0.0f));

		oscP5 = new OscP5Xtended(this, OSClistenPort);
		oscP5.plug(this, "createFWindow", OSC_FISICA_CREATE);
		oscP5.plug(this, "setSimulationProperties", OSC_FISICA_WORLDSET);
		oscP5.plug(this, "setiPhoneCoordinateFactors", OSC_FISICA_IPHONECOORDINATES);
		oscP5.plug(starManager, "setStarProperty", OSC_FISICA_STARSET);
		oscP5.plug(starManager, "setStarProperties", OSC_FISICA_STARALL);

		
		schloss = loadShape("vector/SchlossFrontEinfach.svg");
		
		createCleanupBorders();
		background(0);
		
		OscMessage refresh = new OscMessage(OSC_MAX_REFRESH);
		NetAddress controlLocation = new NetAddress(OSCcontrolAddress,OSCcontrolPort);
		oscP5.send(refresh, controlLocation);
	}
	
	public void setiPhoneCoordinateFactors(int _deltaX, int _deltaY, float _factorX, float _factorY){
		iPhoneDeltaX = _deltaX;
		iPhoneDeltaY = _deltaY;
		iPhoneFactorX = _factorX;
		iPhoneFactorY = _factorY;
	}
	
	public void setSimulationProperties(int _simSpeed, int _simGravity, float _simRestitution){
		SimulationSpeed = _simSpeed;
		SimulationGravity = _simGravity;
		WindowRestitution = _simRestitution;
		world.setGravity(0, SimulationGravity);
	}
	
	private void createCleanupBorders(){
		FLine bottom = new FLine(0, 1060, 1920, 1060);
		bottom.setName(CLEANUP_BODY_NAME);
		bottom.setSensor(true);
		bottom.setStatic(true);
		bottom.setFill(1f);
		bottom.setDrawable(true);
		world.add(bottom);
		FLine right = new FLine(1918, 0, 1918, 1059);
		right.setName(CLEANUP_BODY_NAME);
		right.setSensor(true);
		right.setStatic(true);
		right.setFill(1f);
		world.add(right);
		FLine left = new FLine(2, 0, 2, 1059);
		left.setName(CLEANUP_BODY_NAME);
		left.setSensor(true);
		left.setStatic(true);
		left.setFill(1f);
		world.add(left);
	}

	public void oscEvent(OscMessage theOscMessage) {
		if (theOscMessage.isPlugged() == false) {
			/*
			 * print the address pattern and the typetag of the received
			 * OscMessage
			 */
			println("### received an osc message.");
			println("### addrpattern\t>" + theOscMessage.addrPattern()+ "<");
			println("### address\t" + theOscMessage.address().substring(1));
			println("### typetag\t" + theOscMessage.typetag());
			if (theOscMessage.addrPattern().equals(OSC_FISICA_NEWSTAR)) {
				int type =  ((Integer)theOscMessage.arguments()[0]).intValue();
				int posX =  iphone2fisicaX(((Integer)theOscMessage.arguments()[1]).intValue());
				int posY =  iphone2fisicaY(((Integer)theOscMessage.arguments()[2]).intValue());
				world.add(starManager.createStar(theOscMessage.address().substring(1), type, posX, posY));
			}else if (theOscMessage.addrPattern().equals(OSC_FISICA_SERVERRUNNING)) {
				OscMessage iPhone = new OscMessage(OSC_IPHONE_SERVERACKNOWLEDGE);
				NetAddress iPhoneLocation = new NetAddress(theOscMessage.address().substring(1),
						OSCiPhonePort);
				oscP5.send(iPhone, iPhoneLocation);
			}		
		}
	}

	public void setWindowsPos(String _name, int posX, int posY, int sizeX,
			int sizeY) {
		FWindow window = (FWindow) world.hasBodyWidthName(_name);
		if (window != null) {
			world.remove(window);
			window.setPosition(posX, posY);
			window.setHeight(sizeY);
			window.setWidth(sizeY);
			world.add(window);
		}
	}

	public void createFWindow(String _name, String _type, String _adress,
			int posX, int posY, int sizeX, int sizeY) {
		// Must remove from world and read to change the size
		FBody body = world.hasBodyWidthName(_name);
		if (body != null) {
			world.remove(body);
			oscP5.unplug(body);
		}
		FWindow window = new FWindow(water);
		window.setName(_name);
		window.setID(Integer.parseInt(_name.substring(WINDOW_BODY_NAME.length())));
		window.setAddress(_name, _adress);
		window.setStaticBody(true);
		window.setRestitution(WindowRestitution);
		window.setPosition(posX, posY);
		window.setRotation(0.05f);
		window.setWidth(sizeX);
		window.setHeight(sizeY);
		oscP5.plug(window, "setPosition", "/fisica/setPos");
		oscP5.plug(window, "setAddress", "/fisica/setAddress");
		oscP5.plug(window, "setCustom", "/fisica/setCustom");
		world.add(window);
	}

	public void deleteFWindow(String _name) {
		// Must remove from world and readd to change the size
		FBody del = world.hasBodyWidthName(_name);
		if (del != null)
			world.remove(del);
	}

	public void draw() {
		oscP5.releaseMessages();
		stroke(255, 0, 0);
		line(0, BALL_HORIZONT, 1920, BALL_HORIZONT);
		line(18, 10, 1902, 10);
		line(1902, BALL_HORIZONT, 1902, BALL_HORIZONT + 50);
		line(18, BALL_HORIZONT, 18, BALL_HORIZONT + 50);

		if (startSel != null) {
			noFill();
			this.rect(startSel.x, startSel.y, endSel.x - startSel.x, endSel.y
					- startSel.y);
		}

		shape(schloss, 0, 0, 1920, 1080);

		starManager.step(world);
		starManager.draw(this);

		world.step(1f / SimulationSpeed);
		world.draw(this);
		
		  if (key == 's') {
			    saveFrame("screenshot-####.tif");
		  }
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
		case 'B':
			for(int i = world.getBodies().size() -1; i >= 0; i--){
				FBody star = (FBody)world.getBodies().get(i);
				if(star != null && star.getName() != null && star.getName().startsWith(STAR_BODY_NAME))
					world.remove(star);
			}
			break;
		case 'r':
			createRandomStarGenerators(5);
			break;
		case 'j':
			OscMessage iPhoneStar = new OscMessage(OSC_IPHONE_STAR);
			iPhoneStar.add(1); // star type
			iPhoneStar.add(1); // star type
			iPhoneStar.add(fisica2iphoneX(800));
			iPhoneStar.add(fisica2iphoneY(800));
			NetAddress iPhoneLocation = new NetAddress("127.0.0.1",
					OSCiPhonePort);
			oscP5.send(iPhoneStar, iPhoneLocation);
			break;
		}
	}

	private void createRandomStarGenerators(int _number) {
		for (int i = 0; i < _number; i++) {
			starManager.addStarCreator((int) random(0, 1920),
					(int) random(0, BALL_HORIZONT), (int) random(0, 10),
					(int) random(1000, 5000));
		}
	}

	public void mousePressed() {
		if (this.keyPressed) {
			int pressedKey = (int) key - 48;
			if (pressedKey >= 0 && pressedKey < 10) {
				starManager.addStarCreator(mouseX, mouseY, pressedKey,
						abs(BALL_HORIZONT - mouseY) + 20 * 100);
			} else {
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
		if (startSel != null) {
			endSel = new Vector2i(mouseX, mouseY);
		}
	}

	public void mouseReleased() {
		if (startSel != null) {
			starManager.delStar(min(startSel.x, endSel.x),
					min(startSel.y, endSel.y), max(startSel.x, endSel.x),
					max(startSel.y, endSel.y));
			startSel = null;
			endSel = null;
		}
	}

	public void contactStarted(FContact contacto) {
		FBody body1 = contacto.getBody1();
		FBody body2 = contacto.getBody2();
		if (body1.getName() != null && body2.getName() != null) {
			if (body1.getName().startsWith(WINDOW_BODY_NAME)) {
				if (body2.getName().startsWith(STAR_BODY_NAME))
					star2windowContact((FStar) body2, (FWindow) body1);
			} else if (body1.getName().startsWith(STAR_BODY_NAME)) {
				if (body2.getName().startsWith(WINDOW_BODY_NAME))
					star2windowContact((FStar) body1, (FWindow) body2);
				else if (body2.getName().startsWith(STAR_BODY_NAME))
					star2starContact((FStar) body1, (FStar) body2);
			}else if (body1.getName().startsWith(CLEANUP_BODY_NAME)) {
				//println("cleanup body2!!");
				star2Cleanup(body2);
			}else if (body2.getName().startsWith(CLEANUP_BODY_NAME)) {
				//println("cleanup body1!!");
				star2Cleanup(body1);
			}
		} else {
			println("body without name!!?");
		}
	}
	
	private void star2Cleanup(FBody _starBody){
		FStar star = (FStar)_starBody;
		star.remove();
		world.remove(_starBody);
	}

	private void star2windowContact(FStar _star, FWindow _window) {
		if(_star.isReactive()){
			if(_star.getType() != 2 || 
					_star.getType() == 2 && _star.lastWindowHit != _window.getID()){
				_star.hit(_window.getID());
				_window.hit();
				OscMessage soundwindow = new OscMessage(OSC_SOUND_WINDOW);
				soundwindow.add(_window.getAddress()); // sound address
				soundwindow.add(_star.getType()); // star type
				soundwindow.add(_star.getVelocityX());
				soundwindow.add(_star.getVelocityY());
				soundwindow.add(_star.getX());
				NetAddress soundLocation = new NetAddress(OSCsoundAddress, OSCsoundPort);
				oscP5.send(soundwindow, soundLocation);
	
				OscMessage iPhonewindow = new OscMessage(OSC_IPHONE_WINDOW);
				iPhonewindow.add(_star.getType()); // star type
				iPhonewindow.add(Integer.parseInt(_window.getName().substring(WINDOW_BODY_NAME.length()))); // window id
				iPhonewindow.add(fisica2iphoneX(_star.getX()));
				iPhonewindow.add(fisica2iphoneY(_star.getY()));
				NetAddress iPhoneLocation = new NetAddress(_star.getAddress(),
						OSCiPhonePort);
				oscP5.send(iPhonewindow, iPhoneLocation);
			}
		}
	}

	private void star2starContact(FStar _star1, FStar _star2) {
		if(_star1.isReactive() && _star2.isReactive()){
			_star1.hit(-1);
			OscMessage soundStar = new OscMessage(OSC_SOUND_STAR);
			soundStar.add(_star1.getType()); // star type
			soundStar.add(_star2.getType()); // star type
			soundStar.add(abs(_star1.getVelocityX()) + abs(_star2.getVelocityX())
					+ abs(_star1.getVelocityY()) + abs(_star2.getVelocityY()));
			soundStar.add(_star1.getX()); // star pos
			NetAddress soundLocation = new NetAddress(OSCsoundAddress, OSCsoundPort);
			oscP5.send(soundStar, soundLocation);
	
			if(_star1.getAddress() != _star2.getAddress()){
				OscMessage iPhoneStar = new OscMessage(OSC_IPHONE_STAR);
				iPhoneStar.add(_star1.getType()); // star type
				iPhoneStar.add(_star2.getType()); // star type
				iPhoneStar.add(fisica2iphoneX(_star1.getX()));
				iPhoneStar.add(fisica2iphoneY(_star1.getY()));
				NetAddress iPhoneLocation = new NetAddress(_star1.getAddress(),
						OSCiPhonePort);
				oscP5.send(iPhoneStar, iPhoneLocation);
		
				iPhoneStar = new OscMessage(OSC_IPHONE_STAR);
				iPhoneStar.add(_star2.getType()); // star type
				iPhoneStar.add(_star1.getType()); // star type
				iPhoneStar.add(fisica2iphoneX(_star2.getX()));
				iPhoneStar.add(fisica2iphoneY(_star2.getY()));
				iPhoneLocation = new NetAddress(_star2.getAddress(), OSCiPhonePort);
				oscP5.send(iPhoneStar, iPhoneLocation);
			}
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
		 //PApplet.main(new String[] {"--present",
		 //"ch.maybites.prj.liquidFacade.MusicalFacadeMain" });
		//PApplet.main(new String[] { "--display=1", "--present",
		//"ch.maybites.prj.liquidFacade.MusicalFacadeMain" });
		PApplet.main(new String[] { "--display=1",
		"ch.maybites.prj.liquidFacade.MusicalFacadeMain" });
	}

	static public int iphone2fisicaX(int posX){
		return (int) ((float)(posX - iPhoneDeltaX) * iPhoneFactorX);
	}
	
	static public int iphone2fisicaY(int posY){
		return (int) ((float)(posY * iPhoneFactorY + iPhoneDeltaY));
	}
	
	static public int fisica2iphoneX(float posX){
		return (int) (((posX) / iPhoneFactorX)  + iPhoneDeltaX);
	}
	
	static public int fisica2iphoneY(float posY){
		return (int) (((posY) - iPhoneDeltaY) / iPhoneFactorY);
	}
	
	
	public void destroy() {
		// myConnector.deconnect();
		super.destroy();
	}
}
