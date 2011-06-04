package ch.maybites.prj.liquidFacade;

import java.util.ArrayList;

import ch.maybites.prj.liquidFacade.fisica.FPebble;
import ch.maybites.prj.liquidFacade.gestalt.water.WaterSurface;

import fisica.FBody;
import fisica.FWorld;

import processing.core.PApplet;
import processing.core.PFont;


public class StarManager {
	
	private ArrayList<StarCreator> autoStars;
	private ArrayList<FBody> starQueue;
	WaterSurface water;
	PFont font;

	public StarManager(WaterSurface _water, PFont _font){
		autoStars = new ArrayList<StarCreator>();
		starQueue = new ArrayList<FBody>();
		water = _water;
		font = _font;
	}
	
	public void addStarCreator(int _posX, int _posY, int _star, int _freq){
		StarCreator thread = new StarCreator(_posX, _posY, _star, _freq);
		thread.start();
		autoStars.add(thread);
	}
	
	public void delStar(int _posX1, int _posY1, int _posX2, int _posY2){
		StarCreator thread;
		for(int i = autoStars.size() - 1; i >= 0; i--){
			thread = autoStars.get(i);
			if(thread.posX > _posX1 && 
					thread.posX < _posX2 &&
					thread.posY > _posY1 && 
					thread.posY < _posY2){
				thread.alive = false;
			}
		}
	}
	
	public void delAll(){
		StarCreator thread;
		for(int i = autoStars.size() - 1; i >= 0; i--){
			thread = autoStars.get(i);
			thread.alive = false;
		}
	}
	
	public void step(FWorld _world){
		synchronized(starQueue){
			for(int i = starQueue.size() - 1; i >= 0; i--){
				_world.add(starQueue.remove(i));
			}
		}
	}
	
	public void draw(PApplet canvas){
		StarCreator thread;
		for(int i = autoStars.size() - 1; i >= 0; i--){
			thread = autoStars.get(i);
			if(thread.alive){
				thread.draw(canvas);
			}else{
				autoStars.remove(i);
			}
		}
	}

	private void addStar2Queue(FBody _body){
		synchronized(starQueue){
			starQueue.add(_body);
		}		
	}
	
	private synchronized FBody createStar(int _type, int _posX, int _posY){
		FPebble star = new FPebble(8, water);
		switch(_type){
		case 1:
			break;
		default:
			star = new FPebble(8, water);
			star.setName(MusicalFacadeMain.STAR_BODY_NAME + _type);
			star.setNoStroke();
			star.setFill(255);
			star.setPosition(_posX, _posY);
			star.setVelocity((910 - _posX) / 50, 200);
			star.setRestitution(0.9f);
			star.setDamping(0);
		}
		return star;
	}
	
	private class StarCreator extends Thread implements Runnable {
		public boolean alive = true;
		public int waitTime, posX, posY, type;
		
		StarCreator(int _posX, int _posY, int _type, int _wait){
			waitTime = _wait;
			posX = _posX;
			posY = _posY;
			type = _type;
		}
		
		public void draw(PApplet canvas){
			canvas.fill(255);
			canvas.ellipse(posX, posY, 5, 5);
			canvas.text("" + type, posX - 10, posY - 10);
		}
		
		public void run(){

			while(alive){
				addStar2Queue(createStar(type, posX, posY));
				
				if(waitTime > 0){
					try {
						sleep(waitTime);
					} catch (InterruptedException e){;}
				} else {
					alive = false;
				}
			}
		}
	}
}
