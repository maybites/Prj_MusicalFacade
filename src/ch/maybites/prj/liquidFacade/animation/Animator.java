package ch.maybites.prj.liquidFacade.animation;

import processing.core.*;
import ch.maybites.prj.liquidFacade.fisica.*;

public abstract class Animator {

	abstract public void hit();
	abstract public void step(float dt);
	abstract public void draw(PGraphics canvas);
}
