package com.mrcrayfish.modelcreator.element;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_LINES;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import com.mrcrayfish.modelcreator.ModelCreator;
import com.mrcrayfish.modelcreator.util.FaceDimension;

public class Element
{
	private String name = "Cube";

	// Face Variables
	private int selectedFace = 0;
	private Face[] faces = new Face[6];

	// Element Variables
	private double startX = 0.0, startY = 0.0, startZ = 0.0;
	private double width = 16.0, height = 1.0, depth = 1.0;

	// Rotation Variables
	private double originX = 8, originY = 8, originZ = 8;
	private double rotation;
	private int axis = 0;
	private boolean rescale = false;

	// Extra Variables
	private boolean shade = true;

	// Rotation Point Indicator
	private Sphere sphere = new Sphere();

	public Element(double width, double height, double depth)
	{
		this.width = width;
		this.height = height;
		this.depth = depth;
		initFaces();
	}

	public Element(Element cuboid)
	{
		this.width = cuboid.getWidth();
		this.height = cuboid.getHeight();
		this.depth = cuboid.getDepth();
		this.startX = cuboid.getStartX();
		this.startY = cuboid.getStartY();
		this.startZ = cuboid.getStartZ();
		this.originX = cuboid.getOriginX();
		this.originY = cuboid.getOriginY();
		this.originZ = cuboid.getOriginZ();
		this.rotation = cuboid.getRotation();
		this.axis = cuboid.getPrevAxis();
		this.rescale = cuboid.shouldRescale();
		this.shade = cuboid.isShaded();
		this.selectedFace = cuboid.getSelectedFaceIndex();
		initFaces();
		for (int i = 0; i < faces.length; i++)
		{
			Face oldFace = cuboid.getAllFaces()[i];
			faces[i].fitTexture(oldFace.shouldFitTexture());
			faces[i].setTexture(oldFace.getTextureName());
			faces[i].setTextureLocation(oldFace.getTextureLocation());
			faces[i].setStartU(oldFace.getStartU());
			faces[i].setStartV(oldFace.getStartV());
			faces[i].setEndU(oldFace.getEndU());
			faces[i].setEndV(oldFace.getEndV());
			faces[i].setCullface(oldFace.isCullfaced());
			faces[i].setEnabled(oldFace.isEnabled());
			faces[i].setAutoUVEnabled(oldFace.isAutoUVEnabled());
		}
		updateUV();
	}

	public void initFaces()
	{
		for (int i = 0; i < faces.length; i++)
			faces[i] = new Face(this, i);
	}

	public void setSelectedFace(int face)
	{
		this.selectedFace = face;
	}

	public Face getSelectedFace()
	{
		return faces[selectedFace];
	}

	public int getSelectedFaceIndex()
	{
		return selectedFace;
	}

	public Face[] getAllFaces()
	{
		return faces;
	}

	public int getLastValidFace()
	{
		int id = 0;
		for (Face face : faces)
		{
			if (face.isEnabled())
			{
				id = face.getSide();
			}
		}
		return id;
	}

	public FaceDimension getFaceDimension(int side)
	{
		switch (side)
		{
		case 0:
			return new FaceDimension(getWidth(), getHeight());
		case 1:
			return new FaceDimension(getDepth(), getHeight());
		case 2:
			return new FaceDimension(getWidth(), getHeight());
		case 3:
			return new FaceDimension(getDepth(), getHeight());
		case 4:
			return new FaceDimension(getWidth(), getDepth());
		case 5:
			return new FaceDimension(getWidth(), getDepth());
		}
		return null;
	}

	public void clearAllTextures()
	{
		for (Face face : faces)
		{
			face.setTexture(null);
		}
	}

	public void setAllTextures(String texture, String location)
	{
		for (Face face : faces)
		{
			face.setTexture(texture);
			face.setTextureLocation(location);
		}
	}

	public void draw()
	{
		GL11.glPushMatrix();
		{
			if(ModelCreator.transparent)
				GL11.glEnable(GL11.GL_BLEND);
			GL11.glEnable(GL_CULL_FACE);
			GL11.glTranslated(getOriginX(), getOriginY(), getOriginZ());
			rotateAxis();
			GL11.glTranslated(-getOriginX(), -getOriginY(), -getOriginZ());

			// North
			if (faces[0].isEnabled())
			{
				GL11.glColor3f(0, 1, 0);
				faces[0].renderNorth(false, 0);
			}

			// East
			if (faces[1].isEnabled())
			{
				GL11.glColor3f(0, 0, 1);
				faces[1].renderEast(false, 0);
			}

			// South
			if (faces[2].isEnabled())
			{
				GL11.glColor3f(1, 0, 0);
				faces[2].renderSouth(false, 0);
			}

			// West
			if (faces[3].isEnabled())
			{
				GL11.glColor3f(1, 1, 0);
				faces[3].renderWest(false, 0);
			}

			// Top
			if (faces[4].isEnabled())
			{
				GL11.glColor3f(0, 1, 1);
				faces[4].renderUp(false, 0);
			}

			// Bottom
			if (faces[5].isEnabled())
			{
				GL11.glColor3f(1, 0, 1);
				faces[5].renderDown(false, 0);
			}
		}
		GL11.glPopMatrix();
	}

	public void drawGetPosition()
	{
		GL11.glPushMatrix();
		{
			GL11.glEnable(GL_CULL_FACE);
			GL11.glTranslated(getOriginX(), getOriginY(), getOriginZ());
			rotateAxis();
			GL11.glTranslated(-getOriginX(), -getOriginY(), -getOriginZ());

			int namePos = 1;
			// North
			if (faces[0].isEnabled())
			{
				GL11.glColor3f(0, 1, 0);
				namePos = faces[0].renderNorth(true, namePos);
			}

			// East
			if (faces[1].isEnabled())
			{
				GL11.glColor3f(0, 0, 1);
				namePos = faces[1].renderEast(true, namePos);
			}

			// South
			if (faces[2].isEnabled())
			{
				GL11.glColor3f(1, 0, 0);
				namePos = faces[2].renderSouth(true, namePos);
			}

			// West
			if (faces[3].isEnabled())
			{
				GL11.glColor3f(1, 1, 0);
				namePos = faces[3].renderWest(true, namePos);
			}

			// Top
			if (faces[4].isEnabled())
			{
				GL11.glColor3f(0, 1, 1);
				namePos = faces[4].renderUp(true, namePos);
			}

			// Bottom
			if (faces[5].isEnabled())
			{
				GL11.glColor3f(1, 0, 1);
				namePos = faces[5].renderDown(true, namePos);
			}
		}
		GL11.glPopMatrix();
	}

	public void drawExtras(ElementManager manager)
	{
		if (manager.getSelectedElement() == this)
		{
			GL11.glPushMatrix();
			{
				GL11.glTranslated(getOriginX(), getOriginY(), getOriginZ());
				GL11.glColor3f(0.25F, 0.25F, 0.25F);
				sphere.draw(0.2F, 16, 16);
				rotateAxis();
				GL11.glLineWidth(2F);
				GL11.glBegin(GL_LINES);
				{
					GL11.glColor3f(1, 0, 0);
					GL11.glVertex3i(-4, 0, 0);
					GL11.glVertex3i(4, 0, 0);
					GL11.glColor3f(0, 1, 0);
					GL11.glVertex3i(0, -4, 0);
					GL11.glVertex3i(0, 4, 0);
					GL11.glColor3f(0, 0, 1);
					GL11.glVertex3i(0, 0, -4);
					GL11.glVertex3i(0, 0, 4);
				}
				GL11.glEnd();
			}
			GL11.glPopMatrix();
		}
	}

	public List<Element> getElementsWithRemovedPixel(int pixel) {
		int side = 0;
		for(int i=0; i<6; i++) {
			FaceDimension dim = getFaceDimension(i);
			if(pixel>=dim.getWidth()*dim.getHeight()) {
				pixel -= dim.getWidth()*dim.getHeight();
				side = i+1;
			} else {
				break;
			}
		}

		List<Element> list = new ArrayList<Element>();
		
		switch(side) {
		//north
		case 0: {
			if(depth>1) {
				Element e = new Element(width, height, depth-1);
				e.setStartX(startX);
				e.setStartY(startY);
				e.setStartZ(startZ+1);
				list.add(e);
			}
			int right = pixel/(int) Math.ceil(height);
			int below = pixel - (int) Math.floor(right * height);
			double top = height - below - 1;
			double left = width - right - 1;
			
			if(right>0) {
				Element e = new Element(right, height, 1);
				e.setStartX(startX);
				e.setStartY(startY);
				e.setStartZ(startZ);
				list.add(e);
			}
			
			if(below>0) {
				Element e = new Element(1, below, 1);
				e.setStartX(startX+right);
				e.setStartY(startY);
				e.setStartZ(startZ);
				list.add(e);
			}
			
			if(top>0) {
				Element e = new Element(1, top, 1);
				e.setStartX(startX+right);
				e.setStartY(startY+below+1);
				e.setStartZ(startZ);
				list.add(e);
			}
			
			if(left>0) {
				Element e = new Element(left, height, 1);
				e.setStartX(startX+right+1);
				e.setStartY(startY);
				e.setStartZ(startZ);
				list.add(e);
			}
			
			break;
		}
		//east
		case 1: {
			if(width>1) {
				Element e = new Element(width-1, height, depth);
				e.setStartX(startX);
				e.setStartY(startY);
				e.setStartZ(startZ);
				list.add(e);
			}
			int right = pixel/(int) Math.ceil(height);
			int below = pixel - (int) Math.floor(right * height);
			double top = height - below - 1;
			double left = depth - right - 1;
			
			if(right>0) {
				Element e = new Element(1, height, right);
				e.setStartX(startX+width-1);
				e.setStartY(startY);
				e.setStartZ(startZ);
				list.add(e);
			}
			
			if(below>0) {
				Element e = new Element(1, below, 1);
				e.setStartX(startX+width-1);
				e.setStartY(startY);
				e.setStartZ(startZ+right);
				list.add(e);
			}
			
			if(top>0) {
				Element e = new Element(1, top, 1);
				e.setStartX(startX+width-1);
				e.setStartY(startY+below+1);
				e.setStartZ(startZ+right);
				list.add(e);
			}
			
			if(left>0) {
				Element e = new Element(1, height, left);
				e.setStartX(startX+width-1);
				e.setStartY(startY);
				e.setStartZ(startZ+right+1);
				list.add(e);
			}
			
			break;
		}
		//south
		case 2: {
			if(depth>1) {
				Element e = new Element(width, height, depth-1);
				e.setStartX(startX);
				e.setStartY(startY);
				e.setStartZ(startZ);
				list.add(e);
			}
			int left = pixel/(int) Math.ceil(height);
			int below = pixel - (int) Math.floor(left * height);
			double top = height - below - 1;
			double right = width - left - 1;
			
			if(right>0) {
				Element e = new Element(right, height, 1);
				e.setStartX(startX+left+1);
				e.setStartY(startY);
				e.setStartZ(startZ+depth-1);
				list.add(e);
			}
			
			if(below>0) {
				Element e = new Element(1, below, 1);
				e.setStartX(startX+left);
				e.setStartY(startY);
				e.setStartZ(startZ+depth-1);
				list.add(e);
			}
			
			if(top>0) {
				Element e = new Element(1, top, 1);
				e.setStartX(startX+left);
				e.setStartY(startY+below+1);
				e.setStartZ(startZ+depth-1);
				list.add(e);
			}
			
			if(left>0) {
				Element e = new Element(left, height, 1);
				e.setStartX(startX);
				e.setStartY(startY);
				e.setStartZ(startZ+depth-1);
				list.add(e);
			}
			
			break;
		}
		//west
		case 3: {
			if(width>1) {
				Element e = new Element(width-1, height, depth);
				e.setStartX(startX+1);
				e.setStartY(startY);
				e.setStartZ(startZ);
				list.add(e);
			}
			int left = pixel/(int) Math.ceil(height);
			int below = pixel - (int) Math.floor(left * height);
			double top = height - below - 1;
			double right = depth - left - 1;
			
			if(right>0) {
				Element e = new Element(1, height, right);
				e.setStartX(startX);
				e.setStartY(startY);
				e.setStartZ(startZ+left+1);
				list.add(e);
			}
			
			if(below>0) {
				Element e = new Element(1, below, 1);
				e.setStartX(startX);
				e.setStartY(startY);
				e.setStartZ(startZ+left);
				list.add(e);
			}
			
			if(top>0) {
				Element e = new Element(1, top, 1);
				e.setStartX(startX);
				e.setStartY(startY+below+1);
				e.setStartZ(startZ+left);
				list.add(e);
			}
			
			if(left>0) {
				Element e = new Element(1, height, left);
				e.setStartX(startX);
				e.setStartY(startY);
				e.setStartZ(startZ);
				list.add(e);
			}
			
			break;
		}
		//up
		case 4: {
			if(height>1) {
				Element e = new Element(width, height-1, depth);
				e.setStartX(startX);
				e.setStartY(startY);
				e.setStartZ(startZ);
				list.add(e);
			}
			int right = pixel/(int) Math.ceil(depth);
			int below = pixel - (int) Math.floor(right * depth);
			double top = depth - below - 1;
			double left = width - right - 1;
			
			if(right>0) {
				Element e = new Element(right, 1, depth);
				e.setStartX(startX);
				e.setStartY(startY+height-1);
				e.setStartZ(startZ);
				list.add(e);
			}
			
			if(below>0) {
				Element e = new Element(1, 1, below);
				e.setStartX(startX+right);
				e.setStartY(startY+height-1);
				e.setStartZ(startZ);
				list.add(e);
			}
			
			if(top>0) {
				Element e = new Element(1, 1, top);
				e.setStartX(startX+right);
				e.setStartY(startY+height-1);
				e.setStartZ(startZ+below+1);
				list.add(e);
			}
			
			if(left>0) {
				Element e = new Element(left, 1, depth);
				e.setStartX(startX+right+1);
				e.setStartY(startY+height-1);
				e.setStartZ(startZ);
				list.add(e);
			}
			
			System.out.println(pixel);
			//list.add(this);
			
			break;
		}
		//down
		case 5: {
			if(height>1) {
				Element e = new Element(width, height-1, depth);
				e.setStartX(startX);
				e.setStartY(startY+1);
				e.setStartZ(startZ);
				list.add(e);
			}
			int right = pixel/(int) Math.ceil(depth);
			int below = pixel - (int) Math.floor(right * depth);
			double top = depth - below - 1;
			double left = width - right - 1;
			
			if(right>0) {
				Element e = new Element(right, 1, depth);
				e.setStartX(startX);
				e.setStartY(startY);
				e.setStartZ(startZ);
				list.add(e);
			}
			
			if(below>0) {
				Element e = new Element(1, 1, below);
				e.setStartX(startX+right);
				e.setStartY(startY);
				e.setStartZ(startZ);
				list.add(e);
			}
			
			if(top>0) {
				Element e = new Element(1, 1, top);
				e.setStartX(startX+right);
				e.setStartY(startY);
				e.setStartZ(startZ+below+1);
				list.add(e);
			}
			
			if(left>0) {
				Element e = new Element(left, 1, depth);
				e.setStartX(startX+right+1);
				e.setStartY(startY);
				e.setStartZ(startZ);
				list.add(e);
			}
			
			System.out.println(pixel);
			//list.add(this);
			
			break;
		}
		}
		
		return list;
	}

	public void addStartX(double amt)
	{
		this.startX += amt;
	}

	public void addStartY(double amt)
	{
		this.startY += amt;
	}

	public void addStartZ(double amt)
	{
		this.startZ += amt;
	}

	public double getStartX()
	{
		return startX;
	}

	public double getStartY()
	{
		return startY;
	}

	public double getStartZ()
	{
		return startZ;
	}

	public void setStartX(double amt)
	{
		this.startX = amt;
	}

	public void setStartY(double amt)
	{
		this.startY = amt;
	}

	public void setStartZ(double amt)
	{
		this.startZ = amt;
	}

	public double getWidth()
	{
		return width;
	}

	public double getHeight()
	{
		return height;
	}

	public double getDepth()
	{
		return depth;
	}

	public void addWidth(double amt)
	{
		this.width += amt;
	}

	public void addHeight(double amt)
	{
		this.height += amt;
	}

	public void addDepth(double amt)
	{
		this.depth += amt;
	}
	
	public void setWidth(double width)
	{
		this.width = width;
	}

	public void setHeight(double height)
	{
		this.height = height;
	}

	public void setDepth(double depth)
	{
		this.depth = depth;
	}

	public double getOriginX()
	{
		return originX;
	}

	public double getOriginY()
	{
		return originY;
	}

	public double getOriginZ()
	{
		return originZ;
	}

	public void addOriginX(double amt)
	{
		this.originX += amt;
	}

	public void addOriginY(double amt)
	{
		this.originY += amt;
	}

	public void addOriginZ(double amt)
	{
		this.originZ += amt;
	}

	public void setOriginX(double amt)
	{
		this.originX = amt;
	}

	public void setOriginY(double amt)
	{
		this.originY = amt;
	}

	public void setOriginZ(double amt)
	{
		this.originZ = amt;
	}

	public double getRotation()
	{
		return rotation;
	}

	public void setRotation(double rotation)
	{
		this.rotation = rotation;
	}

	public int getPrevAxis()
	{
		return axis;
	}

	public void setPrevAxis(int prevAxis)
	{
		this.axis = prevAxis;
	}

	public void setRescale(boolean rescale)
	{
		this.rescale = rescale;
	}

	public boolean shouldRescale()
	{
		return rescale;
	}

	public boolean isShaded()
	{
		return shade;
	}

	public void setShade(boolean shade)
	{
		this.shade = shade;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public void updateUV() {
		for(Face face : faces) {
			face.updateUV();
		}
	}

	public void rotateAxis()
	{
		switch (axis)
		{
		case 0:
			GL11.glRotated(getRotation(), 1, 0, 0);
			break;
		case 1:
			GL11.glRotated(getRotation(), 0, 1, 0);
			break;
		case 2:
			GL11.glRotated(getRotation(), 0, 0, 1);
			break;
		}
	}

	public static String parseAxis(int axis)
	{
		switch (axis)
		{
		case 0:
			return "x";
		case 1:
			return "y";
		case 2:
			return "z";
		}
		return "x";
	}

	public static int parseAxisString(String axis)
	{
		switch (axis)
		{
		case "x":
			return 0;
		case "y":
			return 1;
		case "z":
			return 2;
		}
		return 0;
	}

	public Element copy()
	{
		return new Element(getWidth(), getHeight(), getDepth());
	}
}
