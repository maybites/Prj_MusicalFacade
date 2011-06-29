package ch.maybites.prj.liquidFacade.animation;

import ch.maybites.prj.liquidFacade.fisica.FWindow;

public abstract class WindowAnimator extends Animator {
	FWindow m_window;
	
	public void register(FWindow _window){
		m_window = _window;
	}

}
