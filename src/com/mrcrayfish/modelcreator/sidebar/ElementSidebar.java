package com.mrcrayfish.modelcreator.sidebar;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3d;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL11.glVertex2i;
import static org.lwjgl.opengl.GL11.glViewport;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.ElementManager;
import com.mrcrayfish.modelcreator.element.Face;

public class ElementSidebar implements Sidebar
{
	private double[] sideViewX = { 0, 0, 0 };
	private double[] sideViewY = { 0, 0, 0 };
	private double[] sideViewSizes = { 210, 210, 210 };
	
	private Element sidebarClickedElement = null;
	private int sidebarClickField = 0;
	private int sidebarClickButton = 0;
	private double sidebarMXStart = 0;
	private double sidebarMYStart = 0;
	
	private ElementManager manager;
	private int width = 0;
	private int height = 0;
	private int sidebar = 4;
	
	@Override
	public void updateValues(ElementManager manager, int width, int height, int sidebarSize)
	{
		this.manager = manager;
		this.width = width;
		this.height = height;
		this.sidebar = sidebarSize;
	}
	
	@Override
	public void draw(int w, Font font)
	{
		GL11.glPushMatrix();
		{
			int h = height / 3;
			int ystart = 0;

			for(int i=0; i<3; i++) {
				GL11.glPushMatrix();
				{
					glViewport(0, ystart, w, h);
					glMatrixMode(GL_PROJECTION);
					glLoadIdentity();
					GLU.gluOrtho2D(0, w, ystart+h, ystart);
					glMatrixMode(GL_MODELVIEW);
					glLoadIdentity();
					
					glTranslatef(0, ystart, 0);
					
					sideViewX[i] = (width/sidebar/2.0 - sideViewSizes[i]/2.0);
					sideViewY[i] = (height/3.0/2.0 - sideViewSizes[i]/2.0);
					drawSideView(sideViewX[i], sideViewY[i], sideViewSizes[i], i, font);
					
					glLineWidth(2F);
					glBegin(GL_LINES);
					{
						glVertex2i(0, h);
						glVertex2i(w, h);
					}
					glEnd();
				}
				GL11.glPopMatrix();
				ystart += height / 3;
			}
		}
		GL11.glPopMatrix();
	}
	
	public void drawSideView(double x, double y, double size, int side, Font font) {
		GL11.glPushMatrix();
		{
			glTranslated(x, y, 0);
			glLineWidth(2F);
			
			//outside lines
			glBegin(GL_LINES);
			{
				glVertex2d(0, 0);
				glVertex2d(0, size);
				
				glVertex2d(size, 0);
				glVertex2d(size, size);
				
				glVertex2d(0, 0);
				glVertex2d(size, 0);
				
				glVertex2d(0, size);
				glVertex2d(size, size);
			}
			glEnd();
			
			//inside lines
			glLineWidth(1F);
			for(int i=1; i<16; i++) {
				glBegin(GL_LINES);
				{
					glVertex2d(size/16*i, 0);
					glVertex2d(size/16*i, size);
					
					glVertex2d(0, size/16*i);
					glVertex2d(size, size/16*i);
				}
				glEnd();
			}
			
			//Cubes
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			for (int i = 0; i < manager.getCuboidCount(); i++)
			{
				Element cube = manager.getCuboid(i);
				if(!(cube).equals(manager.getSelectedCuboid()))
					continue;
					
				Face face = null;
				double xstart = 0;
				double ystart = 0;
				double width = 0;
				double height = 0;
				
				switch(side) {
				//top
				case 0: {
					face = cube.getAllFaces()[4];
					xstart = cube.getStartX() * size/16.0;
					ystart = cube.getStartZ() * size/16.0;
					width = cube.getWidth() * size/16.0;
					height = cube.getDepth() * size/16.0;
					GL11.glColor3f(0, 1, 1);
					break;
				}
				//south
				case 1: {
					face = cube.getAllFaces()[2];
					xstart = cube.getStartX() * size/16.0;
					ystart = (16 - cube.getStartY() - cube.getHeight()) * size/16.0;
					width = cube.getWidth() * size/16.0;
					height = cube.getHeight() * size/16.0;
					GL11.glColor3f(1, 0, 0);
					break;
				}
				//west
				case 2: {
					face = cube.getAllFaces()[3];
					xstart = cube.getStartZ() * size/16.0;
					ystart = (16 - cube.getStartY() - cube.getHeight()) * size/16.0;
					width = cube.getDepth() * size/16.0;
					height = cube.getHeight() * size/16.0;
					GL11.glColor3f(1, 1, 0);
					break;
				}
				}
				
				//Draw face
				if(face!=null) {
					GL11.glPushMatrix();
					{
						face.startRender();
	
						GL11.glBegin(GL11.GL_QUADS);
						{
							if (face.isBinded())
								GL11.glTexCoord2d(face.shouldFitTexture() ? 0 : (face.getStartU() / 16), face.shouldFitTexture() ? 1 : (face.getEndV() / 16));
							GL11.glVertex2d(xstart, ystart + height);
	
							if (face.isBinded())
								GL11.glTexCoord2d(face.shouldFitTexture() ? 1 : (face.getEndU() / 16), face.shouldFitTexture() ? 1 : (face.getEndV() / 16));
							GL11.glVertex2d(xstart + width, ystart + height);
	
							if (face.isBinded())
								GL11.glTexCoord2d(face.shouldFitTexture() ? 1 : (face.getEndU() / 16), face.shouldFitTexture() ? 0 : (face.getStartV() / 16));
							GL11.glVertex2d(xstart + width, ystart);
	
							if (face.isBinded())
								GL11.glTexCoord2d(face.shouldFitTexture() ? 0 : (face.getStartU() / 16), face.shouldFitTexture() ? 0 : (face.getStartV() / 16));
							GL11.glVertex2d(xstart, ystart);
						}
						GL11.glEnd();
	
						face.finishRender();
					}
					GL11.glPopMatrix();
					
					glColor3d(0.2, 0.2, 0.2);
					glLineWidth(2F);
					glBegin(GL_LINES);
					{
						glVertex2d(xstart, ystart);
						glVertex2d(xstart, ystart+height);
						
						glVertex2d(xstart+width, ystart);
						glVertex2d(xstart+width, ystart+height);
						
						glVertex2d(xstart, ystart);
						glVertex2d(xstart+width, ystart);
						
						glVertex2d(xstart, ystart+height);
						glVertex2d(xstart+width, ystart+height);
					}
					glEnd();
				}
			}
			GL11.glDisable(GL11.GL_BLEND);
		}
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		if(side==0) {
			font.drawString(0, 0, "Top", new Color(1, 1, 1));
		} else if(side==1) {
			font.drawString(0, 0, "South", new Color(1, 1, 1));
		} else if(side==2) {
			font.drawString(0, 0, "West", new Color(1, 1, 1));
		}
		GL11.glDisable(GL11.GL_BLEND);
		
		glColor3d(0.6, 0.6, 0.6);
	}

	@Override
	public void handleInput(boolean isMouseOver)
	{
		if(isMouseOver) {
			for(int i=0; i<3; i++) {
				if(Mouse.getY()>height/3*i && Mouse.getY()<height/3*(i+1)) {
					double my = Mouse.getY() - height/3*i;
					double mx = Mouse.getX();
					double size = sideViewSizes[i];
					
					if(sidebarClickField!=i || !Mouse.isButtonDown(sidebarClickButton)) {
						sidebarClickedElement = null;
						sidebarMXStart = 0;
						sidebarMYStart = 0;
					}
					
					my -= sideViewY[i];
					mx -= sideViewX[i];
					my = my/size * 16;
					mx = mx/size * 16;
					
					my = (16 - my);
					
					if(Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
						if(sidebarClickedElement==null) {
							//Cubes
							for (int c = 0; c < manager.getCuboidCount(); c++)
							{
								Element cube = manager.getCuboid(c);
								if(!manager.getSelectedCuboid().equals(cube))
									continue;
									
								double xstart = 0;
								double ystart = 0;
								double width = 0;
								double height = 0;
								
								switch(i) {
								//top
								case 0: {
									xstart = cube.getStartX();
									ystart = cube.getStartZ();
									width = cube.getWidth();
									height = cube.getDepth();
									GL11.glColor3f(0, 1, 1);
									break;
								}
								//south
								case 1: {
									xstart = cube.getStartX();
									ystart = (16 - cube.getStartY() - cube.getHeight());
									width = cube.getWidth();
									height = cube.getHeight();
									GL11.glColor3f(1, 0, 0);
									break;
								}
								//west
								case 2: {
									xstart = cube.getStartZ();
									ystart = (16 - cube.getStartY() - cube.getHeight());
									width = cube.getDepth();
									height = cube.getHeight();
									GL11.glColor3f(1, 1, 0);
									break;
								}
								}
								
								if(width<0) {
									xstart += width;
									width = -width;
								}
								if(height<0) {
									ystart += height;
									height = -height;
								}
								
								if(mx>=xstart && mx<=xstart+width) {
									if(my>=ystart && my<=ystart+height) {
										sidebarClickedElement = cube;
										sidebarMXStart = mx;
										sidebarMYStart = my;
										sidebarClickField = i;
										if(Mouse.isButtonDown(0)) {
											sidebarClickButton = 0;
										} else {
											sidebarClickButton = 1;
										}
									}
								}
							}
						} else {
							int offx = 0;
							int offy = 0;

							if(mx-sidebarMXStart>=1) {
								offx = 1;
								sidebarMXStart += 1;
							} else if(mx-sidebarMXStart<=-1) {
								offx = -1;
								sidebarMXStart -= 1;
							}

							if(my-sidebarMYStart>=1) {
								offy = 1;
								sidebarMYStart += 1;
							} else if(my-sidebarMYStart<=-1) {
								offy = -1;
								sidebarMYStart -= 1;
							}

							switch(i) {
							//top
							case 0: {
								if(sidebarClickButton==0) {
									sidebarClickedElement.addStartX(offx);
									sidebarClickedElement.addStartZ(offy);
								} else {
									sidebarClickedElement.addWidth(offx);
									sidebarClickedElement.addDepth(offy);
								}
								break;
							}
							//south
							case 1: {
								if(sidebarClickButton==0) {
									sidebarClickedElement.addStartX(offx);
									sidebarClickedElement.addStartY(-offy);
								} else {
									sidebarClickedElement.addWidth(offx);
									sidebarClickedElement.addHeight(-offy);
								}
								break;
							}
							//west
							case 2: {
								if(sidebarClickButton==0) {
									sidebarClickedElement.addStartZ(offx);
									sidebarClickedElement.addStartY(-offy);
								} else {
									sidebarClickedElement.addDepth(offx);
									sidebarClickedElement.addHeight(-offy);
								}
								break;
							}
							}
							sidebarClickedElement.updateUV();
							manager.updateValues();
						}
					} else {
						sidebarClickedElement = null;
						sidebarMXStart = 0;
						sidebarMYStart = 0;
					}
					
					final float wheel = Mouse.getDWheel();
					if (wheel != 0)
					{
						sideViewSizes[i] += wheel * (sideViewSizes[i] / 5000F);
					}
				}
			}
		} else {
			sidebarClickedElement = null;
			sidebarMXStart = 0;
			sidebarMYStart = 0;
		}
	}
}
