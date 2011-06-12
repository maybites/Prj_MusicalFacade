package ch.maybites.prj.liquidFacade.gestalt.water;

import mathematik.Vector3f;
import ch.maybites.prj.liquidFacade.Canvas;
import ch.maybites.prj.liquidFacade.gestalt.water.*;
import gestalt.Gestalt;
import gestalt.candidates.JoglDisposableBin;
import gestalt.candidates.glsl.ShaderManager;
import gestalt.candidates.rendertotexture.JoglFrameBufferObject;
import gestalt.shape.Color;
import gestalt.shape.Plane;

public class WaterSurface {

    private GPGPUCAWater _mySimulation;

    private Plane _myWaterView;

    private JoglFrameBufferObject _myInputEnergyMap;
    
    private JoglDisposableBin myCanvas;
    
    int width, height;

	public WaterSurface(int _width, int _height){
		width = _width;
		height = _height;
        /* setup shader */
		
		myCanvas = new JoglDisposableBin();
		myCanvas.color().set(0.5f);
		myCanvas.material.depthmask = false;
		myCanvas.material.depthtest = false;
		
        final ShaderManager myShaderManager = Canvas.getInstance().getPlugin().drawablefactory().extensions().shadermanager();
        Canvas.getInstance().getPlugin().bin(Gestalt.BIN_FRAME_SETUP).add(myShaderManager);

        /* heightmap */
        _myInputEnergyMap = JoglFrameBufferObject.createRectangular(_width, _height);
        _myInputEnergyMap.add(myCanvas);
        //_myInputEnergyMap.scale().set(width, height);
        Canvas.getInstance().getPlugin().bin(Gestalt.BIN_FRAME_SETUP).add(_myInputEnergyMap);

        //final Plane myHeightfieldView = G.plane();
        /*
        final Plane myHeightfieldView = Canvas.getInstance().getPlugin().drawablefactory().plane();
        myHeightfieldView.material().addTexture(_myInputEnergyMap);
        myHeightfieldView.setPlaneSizeToTextureSize();
        myHeightfieldView.position().x -= myHeightfieldView.scale().x * 0.5f;
        Canvas.getInstance().getPlugin().bin(Gestalt.BIN_3D).add(myHeightfieldView);
        */
        
        /* simulation */
        _mySimulation = new GPGPUCAWater(myShaderManager,
                                         _myInputEnergyMap,
                                         "shader/gpgpu/CAWaterSimulationColor.fs",
        								"shader/gpgpu/CAWaterDrawerColor.fs");
        //"shader/gpgpu/CAWaterSimulation.fs",
        //"shader/gpgpu/CAWaterDrawer.fs");
        Canvas.getInstance().getPlugin().bin(Gestalt.BIN_FRAME_SETUP).add(_mySimulation);

        /* simulation view */
        _myWaterView = Canvas.getInstance().getPlugin().drawablefactory().plane();
        _mySimulation.attachWater(_myWaterView.material());
        _myWaterView.setPlaneSizeToTextureSize();
        _myWaterView.position().x += _width * 0.5f;
        _myWaterView.position().y += _height * 0.5f;
        _myWaterView.position().z = 0;
        Canvas.getInstance().getPlugin().bin(Gestalt.BIN_3D).add(_myWaterView);
      // _myWaterView.rotation(.5f, 0f, 0f);

        _mySimulation.damping = 0.985f;
	}
	
	public void waterviewDistance(int z){
        _myWaterView.position().z = z;
	}
	
	private float world2WaterX(float worldPosX){
		return worldPosX - width / 2;
	}
	
	private float world2WaterY(float worldPosY){
		return worldPosY - height / 2;
	}
	
    public void drawPebble(float _mouseX, float _mouseY) {
    	float mouseX = world2WaterX(_mouseX);
    	float mouseY = world2WaterY(_mouseY);
        myCanvas.line(mouseX + 2, mouseY + 2,
        		mouseX - 2, mouseY - 2);
        myCanvas.line(mouseX - 2, mouseY + 2,
        		mouseX + 2, mouseY - 2);
    }

    public void drawBoxes(float theDeltaTime) {
        _mySimulation.damping = 0.3f;
        
        drawBox(200, 200, 30);
        drawBox(200, -200, 60);
        drawBox(0, -200, 40);
        drawBox(20, 20, 10);
        drawBox(-200, 100, 80);
        drawBox(-200, -200, 60);
        drawBox(-100, 100, 50);

        //addStatistic("FPS", 1.0f / theDeltaTime);
        //addStatistic("damping", _mySimulation.damping);
    }
    
    void drawBox(int xPos, int yPos, int size){
    	myCanvas.line(xPos + size/2, yPos + size/2, xPos - size/2, yPos + size/2);
    	myCanvas.line(xPos + size/2, yPos - size/2, xPos - size/2, yPos - size/2);
    	myCanvas.line(xPos + size/2, yPos + size/2, xPos + size/2, yPos - size/2);
    	myCanvas.line(xPos - size/2, yPos + size/2, xPos - size/2, yPos - size/2);
    }
    
    public void drawCenterBox(Color c, int _xPos, int _yPos, int sizeX, int sizeY){
      	float xPos = world2WaterX(_xPos);
    	float yPos = world2WaterY(_yPos);
    	myCanvas.color(c);
    	myCanvas.line(xPos + sizeX/2, yPos + sizeY/2, xPos - sizeX/2, yPos + sizeY/2);
    	myCanvas.line(xPos + sizeX/2, yPos - sizeY/2, xPos - sizeX/2, yPos - sizeY/2);
    	myCanvas.line(xPos + sizeX/2, yPos + sizeY/2, xPos + sizeX/2, yPos - sizeY/2);
    	myCanvas.line(xPos - sizeX/2, yPos + sizeY/2, xPos - sizeX/2, yPos - sizeY/2);
    }
    
    public void drawCenterClearBox(int _xPos, int _yPos, int sizeX, int sizeY){
      	float xPos = world2WaterX(_xPos);
    	float yPos = world2WaterY(_yPos);
    	myCanvas.color(1, 1, 1, 1);
    	myCanvas.fill = true;
    	myCanvas.box(new Vector3f(xPos - sizeX/2, yPos-sizeY/2, 0f), new Vector3f(sizeX, sizeY, 0f));
    }
    
    public void drawBox(int _xPos, int _yPos, int sizeX, int sizeY){
       	float xPos = world2WaterX(_xPos);
    	float yPos = world2WaterY(_yPos);
       	//float xPos = _xPos;
    	//float yPos = _yPos;
     	myCanvas.line(xPos, yPos, xPos - sizeX, yPos);
    	myCanvas.line(xPos - sizeX, yPos, xPos - sizeX, yPos - sizeY);
    	myCanvas.line(xPos - sizeX, yPos - sizeY, xPos, yPos - sizeY);
    	myCanvas.line(xPos, yPos - sizeY, xPos, yPos);
    }
	
}
