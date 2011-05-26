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

package ch.maybites.prj.musicalFacade;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import processing.core.*;
import controlP5.*;
import fisica.*;

public class MusicalFacadeMain extends PApplet {
	private static final long serialVersionUID = 1L;

	private int oscID;

	FWorld mundo;
	FBox caja;

	float x, y;

	float angleX, angleY, transX, transY, transZ;

	public void setup() {
		size(1280, 1024, OPENGL);
		// frame.setLocation(1440, 0);

		/**
		 * GraphicsEnvironment ge =
		 * GraphicsEnvironment.getLocalGraphicsEnvironment(); GraphicsDevice[]
		 * gs = ge.getScreenDevices(); // gs[1] gets the *second* screen. gs[0]
		 * would get the primary screen GraphicsDevice gd = gs[1];
		 * GraphicsConfiguration[] gc = gd.getConfigurations(); monitor =
		 * gc[0].getBounds(); println(monitor.x + " " + monitor.y + " " +
		 * monitor.width + " " + monitor.height); size(monitor.width,
		 * monitor.height, OPENGL); //frame.setLocation(monitor.x, monitor.y);
		 */

		Fisica.init(this);

		mundo = new FWorld();
		mundo.setGravity(0, 200);

		frameRate(24);
		background(0);

	}

	public void draw() {
		fill(0, 100);
		noStroke();
		rect(0, 0, width, height);

		if ((frameCount % 24) == 0) {
			FCircle bolita = new FCircle(8);
			bolita.setNoStroke();
			bolita.setFill(255);
			bolita.setPosition(100, 20);
			bolita.setVelocity(0, 400);
			bolita.setRestitution(0.9f);
			bolita.setDamping(0);
			mundo.add(bolita);
		}

		mundo.step();
		mundo.draw(this);
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

	public void contactStarted(FContact contacto) {
		FBody cuerpo1 = contacto.getBody1();
		cuerpo1.setFill(255, 0, 0);

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

	public void readArguments() {
		for (int i = 0; i < super.args.length; i++) {
			if (super.args[i].equals("-simID")) {
				oscID = Integer.parseInt(super.args[++i]);
			}
			if (super.args[i].equals("-listenerPort")) {
				oscID = Integer.parseInt(super.args[++i]);
			}
			if (super.args[i].equals("-sendPort")) {
				oscID = Integer.parseInt(super.args[++i]);
			}
			if (super.args[i].equals("-sendAddress")) {
				oscID = Integer.parseInt(super.args[++i]);
			}
		}
	}

	static public void main(String args[]) {
		PApplet.main(new String[] { "ch.maybites.prj.musicalFacade.MusicalFacadeMain" });
		// PApplet.main( new String[] { "--present",
		// "ch.maybites.prj.musicalFacade" } );
	}

	public void destroy() {
		// myConnector.deconnect();
		super.destroy();
	}
}
