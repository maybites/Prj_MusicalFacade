package ch.maybites.prj.liquidFacade;

import java.util.ArrayList;

import ch.maybites.prj.liquidFacade.animation.StarAnimator;
import ch.maybites.prj.liquidFacade.animation.StarFourAnim;
import ch.maybites.prj.liquidFacade.animation.StarOneAnim;
import ch.maybites.prj.liquidFacade.animation.StarThreeAnim;
import ch.maybites.prj.liquidFacade.animation.StarTwoAnim;
import ch.maybites.prj.liquidFacade.animation.StarZeroAnim;
import ch.maybites.prj.liquidFacade.fisica.FImageStar;
import ch.maybites.prj.liquidFacade.fisica.FStar;
import ch.maybites.prj.liquidFacade.gestalt.water.WaterSurface;
import ch.maybites.tools.Debugger;
import ch.maybites.tools.GlobalPrefs;

import fisica.FBody;
import fisica.FWorld;

import processing.core.PApplet;
import processing.core.PFont;


public class StarManager {
	
	private ArrayList<StarCreator> autoStars;
	private ArrayList<FBody> starQueue;
	WaterSurface water;
	PFont font;
	Float[] restitution;
	Float[] damping;
	Float[] density;
	
	int maxStarTypes = 10;
	
	public StarManager(WaterSurface _water, PFont _font){
		autoStars = new ArrayList<StarCreator>();
		starQueue = new ArrayList<FBody>();
		water = _water;
		font = _font;
		
		restitution = new Float[maxStarTypes];
		damping = new Float[maxStarTypes];
		density = new Float[maxStarTypes];
		for(int i = 0; i < maxStarTypes; i++){
			restitution[i] = 0.9f;
			damping[i] = 0f;
			density[i] = 0f;
		}
	}
	
	public void setStarProperty(int _type, float _rest, float _damp, float _dens){
		if(_type >= 0 && _type < maxStarTypes){
			restitution[_type] = _rest;
			damping[_type] = _damp;
			density[_type] = _dens;
		}
	}
	
	public void setStarProperties(float _rest, float _damp, float _dens){
		for(int i = 0; i < maxStarTypes; i++){
			restitution[i] = _rest;
			damping[i] = _damp;
			density[i] = _dens;
		}
	}
	
	public void addStarCreator(int _posX, int _posY, int _star, int _freq){
		StarCreator thread = new StarCreator(_posX, _posY, _star, _freq);
		thread.start();
		thread.createNextStar();
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
	
	public synchronized FBody createStar(String _address, int _type, int _posX, int _posY){
		if(_type >= 0 && _type < maxStarTypes){
			FStar star;
			StarAnimator animator;
			if(_type == 0){
				//star = new FImageStar(_address, 20, _type, GlobalPrefs.getInstance().getAbsDataPath("vector/stars/mlove.svg"), water);
				star = new FImageStar(_address, 25, _type, GlobalPrefs.getInstance().getAbsDataPath("vector/stars/mlove_logo_hearticon.svg"), water);
				animator = new StarZeroAnim();
			}else{
				star = new FStar(_address, 13, _type, water);
				if(_type == 1){
					animator = new StarOneAnim();
				}else if(_type == 2){
					animator = new StarTwoAnim();
				}else if(_type == 3){
					animator = new StarThreeAnim();
				}else if(_type == 4){
					animator = new StarFourAnim();
				}else{
					animator = new StarZeroAnim();
				}
			}
			star.registerAnimator(animator);
			star.setName(MusicalFacadeMain.STAR_BODY_NAME + _type);
			star.setNoStroke();
			star.setFill(255);
			star.setGroupIndex(-1);
			star.setPosition(_posX, _posY);
			//star.setVelocity((910 - _posX) / 50, 200);
			star.setVelocity(0, 200);
			star.setRotatable(true);
			star.setAngularVelocity((float)Math.random() - 1f);
			star.setRestitution(restitution[_type]);
			star.setDensity(density[_type]);
			star.setDamping(damping[_type]);
			return star;
		}
		return new FStar(_address, 8, 1, water);
	}
	
	public class StarCreator extends Thread implements Runnable {
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
		
		public void starRemoved(){
			if(type != 1 && alive){
				createNextStar();
			}
		}
		
		private void createNextStar(){
			FStar next = (FStar) createStar("127.0.0.1", type, posX, posY);
			next.registerCreator(this);
			addStar2Queue(next);
		}
		
		public void run(){

			while(alive && type == 1){
				if(waitTime > 0){
					try {
						sleep(waitTime);
					} catch (InterruptedException e){;}
				} else {
					alive = false;
				}
				createNextStar();
			}
		}
	}
}
