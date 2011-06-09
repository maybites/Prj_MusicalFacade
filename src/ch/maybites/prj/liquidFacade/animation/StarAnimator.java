package ch.maybites.prj.liquidFacade.animation;

import ch.maybites.prj.liquidFacade.fisica.FStar;

public abstract class StarAnimator extends Animator {
	FStar m_star;
	
	public void register(FStar _star){
		m_star = _star;
	}

}
