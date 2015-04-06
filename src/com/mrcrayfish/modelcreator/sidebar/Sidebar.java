package com.mrcrayfish.modelcreator.sidebar;

import org.newdawn.slick.Font;

import com.mrcrayfish.modelcreator.element.ElementManager;

public interface Sidebar
{
	public void updateValues(ElementManager manager, int width, int height, int sidebarSize);
	
	public void draw(int w, Font font);
	
	public void handleInput(boolean isMouseOver);
}
