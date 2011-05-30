package ch.maybites.prj.liquidFacade;

import gestalt.p5.*;
import processing.core.*;

public class Canvas {

	static private GestaltPlugIn _plugin = null;
	static private Canvas _instance = null;
	static private PApplet _canvas = null;

	private Canvas(PApplet ref){
		_plugin = new GestaltPlugIn(ref, true);
		_canvas = ref;	
	}
	
	public GestaltPlugIn getPlugin() {
		return _plugin;
	}
	
	public int width(){
		return _canvas.width;
	}

	public int height(){
		return _canvas.width;
	}

	static public Canvas getInstance(){
		return _instance;
	}
	
	static public void setup(PApplet ref) {
		if (_instance == null) {
			synchronized(Canvas.class) {
				if (_instance == null){
					_instance = new Canvas(ref);
					_canvas = ref;
				}
			}
		}
	}

}
