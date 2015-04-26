package com.mrcrayfish.modelcreator.element;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;

import com.mrcrayfish.modelcreator.texture.TextureEntry;
import com.mrcrayfish.modelcreator.texture.TextureManager;

public class Face
{
	private String texture = null;
	private String textureLocation = "blocks/";
	private double textureU = 0;
	private double textureV = 0;
	private double textureUEnd = 1;
	private double textureVEnd = 1;
	private boolean fitTexture = false;
	private boolean binded = false;
	private boolean cullface = false;
	private boolean enabled = true;
	private boolean autoUV = true;
	private int rotation;

	private Element cuboid;
	private int side;

	public Face(Element cuboid, int side)
	{
		this.cuboid = cuboid;
		this.side = side;
	}
	
	public int renderNorth(boolean split, int namePos)
	{
		if(TextureManager.getTextureEntry(texture)!=null && TextureManager.getTextureEntry(texture).isInterpolated() && TextureManager.getTextureEntry(texture).getFrameCount()>1) {
			renderNorth(0, split, namePos);
			return renderNorth(1, split, namePos);
		} else {
			return renderNorth(0, split, namePos);
		}
	}

	private int renderNorth(int pos, boolean split, int namePos)
	{
		GL11.glPushMatrix();
		{
			startRender(pos);

			double splitX = 1;
			double splitY = 1;
			if(split) {
				splitX = cuboid.getWidth();
				splitY = cuboid.getHeight();
			}
			
			for(int i=0; i<splitX; i++) {
				for(int j=0; j<splitY; j++) {
					double uStart = 0;
					double uEnd = 1;
					double vStart = 0;
					double vEnd = 1;
					
					if(!fitTexture) {
						uStart = textureU / 16;
						uEnd = textureUEnd / 16;
						vStart = textureV / 16;
						vEnd = textureVEnd / 16;
					}
					
					double uSize = (uEnd - uStart);
					double vSize = (vEnd - vStart);
					
					uStart += (splitX-(i+1)) * uSize/splitX;
					uEnd = uStart + (uSize / splitX);
					vStart += (splitY-(j+1)) * vSize/splitY;
					vEnd = vStart + (vSize / splitY);
					
					double xStart = cuboid.getStartX();
					double xEnd = cuboid.getStartX() + cuboid.getWidth();
					double yStart = cuboid.getStartY();
					double yEnd = cuboid.getStartY() + cuboid.getHeight();
					//double zStart = cuboid.getStartZ();
					//double zEnd = cuboid.getStartZ() + cuboid.getDepth();
					
					double xSize = xEnd - xStart;
					double ySize = yEnd - yStart;
					//double zSize = zEnd - zStart;
					
					xStart += i * xSize/splitX;
					xEnd = xStart + (xSize / splitX);
					yStart += j * ySize/splitY;
					yEnd = yStart + (ySize / splitY);
					
					if(split) {
						GL11.glLoadName(namePos++);
					}
					GL11.glBegin(GL11.GL_QUADS);
					{
						if (binded)
							setTexCoord(0, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(xEnd, yStart, cuboid.getStartZ());
		
						if (binded)
							setTexCoord(1, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(xStart, yStart, cuboid.getStartZ());
		
						if (binded)
							setTexCoord(2, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(xStart, yEnd, cuboid.getStartZ());
		
						if (binded)
							setTexCoord(3, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(xEnd, yEnd, cuboid.getStartZ());
					}
					GL11.glEnd();
				}
			}

			finishRender();
		}
		GL11.glPopMatrix();
		
		return namePos;
	}
	
	public int renderEast(boolean split, int namePos)
	{
		if(TextureManager.getTextureEntry(texture)!=null && TextureManager.getTextureEntry(texture).isInterpolated() && TextureManager.getTextureEntry(texture).getFrameCount()>1) {
			renderEast(0, split, namePos);
			return renderEast(1, split, namePos);
		} else {
			return renderEast(0, split, namePos);
		}
	}

	private int renderEast(int pos, boolean split, int namePos)
	{
		GL11.glPushMatrix();
		{
			startRender(pos);
			
			double splitZ = 1;
			double splitY = 1;
			if(split) {
				splitZ = cuboid.getDepth();
				splitY = cuboid.getHeight();
			}
			
			for(int i=0; i<splitZ; i++) {
				for(int j=0; j<splitY; j++) {
					double uStart = 0;
					double uEnd = 1;
					double vStart = 0;
					double vEnd = 1;
					
					if(!fitTexture) {
						uStart = textureU / 16;
						uEnd = textureUEnd / 16;
						vStart = textureV / 16;
						vEnd = textureVEnd / 16;
					}
					
					double uSize = (uEnd - uStart);
					double vSize = (vEnd - vStart);
					
					uStart += (splitZ-(i+1)) * uSize/splitZ;
					uEnd = uStart + (uSize / splitZ);
					vStart += (splitY-(j+1)) * vSize/splitY;
					vEnd = vStart + (vSize / splitY);
					
					double yStart = cuboid.getStartY();
					double yEnd = cuboid.getStartY() + cuboid.getHeight();
					double zStart = cuboid.getStartZ();
					double zEnd = cuboid.getStartZ() + cuboid.getDepth();
					
					double ySize = yEnd - yStart;
					double zSize = zEnd - zStart;
					
					zStart += i * zSize/splitZ;
					zEnd = zStart + (zSize / splitZ);
					yStart += j * ySize/splitY;
					yEnd = yStart + (ySize / splitY);
					
					if(split) {
						GL11.glLoadName(namePos++);
					}
					GL11.glBegin(GL11.GL_QUADS);
					{
						if (binded)
							setTexCoord(0, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(cuboid.getStartX() + cuboid.getWidth(), yStart, zEnd);
		
						if (binded)
							setTexCoord(1, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(cuboid.getStartX() + cuboid.getWidth(), yStart, zStart);
		
						if (binded)
							setTexCoord(2, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(cuboid.getStartX() + cuboid.getWidth(), yEnd, zStart);
		
						if (binded)
							setTexCoord(3, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(cuboid.getStartX() + cuboid.getWidth(), yEnd, zEnd);
					}
					GL11.glEnd();
				}
			}

			finishRender();
		}
		GL11.glPopMatrix();
		
		return namePos;
	}

	public int renderSouth(boolean split, int namePos)
	{
		if(TextureManager.getTextureEntry(texture)!=null && TextureManager.getTextureEntry(texture).isInterpolated() && TextureManager.getTextureEntry(texture).getFrameCount()>1) {
			renderSouth(0, split, namePos);
			return renderSouth(1, split, namePos);
		} else {
			return renderSouth(0, split, namePos);
		}
	}

	private int renderSouth(int pos, boolean split, int namePos)
	{
		GL11.glPushMatrix();
		{
			startRender(pos);

			double splitX = 1;
			double splitY = 1;
			if(split) {
				splitX = cuboid.getWidth();
				splitY = cuboid.getHeight();
			}
			
			for(int i=0; i<splitX; i++) {
				for(int j=0; j<splitY; j++) {
					double uStart = 0;
					double uEnd = 1;
					double vStart = 0;
					double vEnd = 1;
					
					if(!fitTexture) {
						uStart = textureU / 16;
						uEnd = textureUEnd / 16;
						vStart = textureV / 16;
						vEnd = textureVEnd / 16;
					}
					
					double uSize = (uEnd - uStart);
					double vSize = (vEnd - vStart);
					
					uStart += i * uSize/splitX;
					uEnd = uStart + (uSize / splitX);
					vStart += (splitY-(j+1)) * vSize/splitY;
					vEnd = vStart + (vSize / splitY);
					
					double xStart = cuboid.getStartX();
					double xEnd = cuboid.getStartX() + cuboid.getWidth();
					double yStart = cuboid.getStartY();
					double yEnd = cuboid.getStartY() + cuboid.getHeight();
					//double zStart = cuboid.getStartZ();
					//double zEnd = cuboid.getStartZ() + cuboid.getDepth();
					
					double xSize = xEnd - xStart;
					double ySize = yEnd - yStart;
					//double zSize = zEnd - zStart;
					
					xStart += i * xSize/splitX;
					xEnd = xStart + (xSize / splitX);
					yStart += j * ySize/splitY;
					yEnd = yStart + (ySize / splitY);
					
					if(split) {
						GL11.glLoadName(namePos++);
					}
					GL11.glBegin(GL11.GL_QUADS);
					{
						if (binded)
							setTexCoord(0, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(xStart, yStart, cuboid.getStartZ() + cuboid.getDepth());
		
						if (binded)
							setTexCoord(1, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(xEnd, yStart, cuboid.getStartZ() + cuboid.getDepth());
		
						if (binded)
							setTexCoord(2, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(xEnd, yEnd, cuboid.getStartZ() + cuboid.getDepth());
		
						if (binded)
							setTexCoord(3, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(xStart, yEnd, cuboid.getStartZ() + cuboid.getDepth());
					}
					GL11.glEnd();
				}
			}

			finishRender();
		}
		GL11.glPopMatrix();
		
		return namePos;
	}

	public int renderWest(boolean split, int namePos)
	{
		if(TextureManager.getTextureEntry(texture)!=null && TextureManager.getTextureEntry(texture).isInterpolated() && TextureManager.getTextureEntry(texture).getFrameCount()>1) {
			renderWest(0, split, namePos);
			return renderWest(1, split, namePos);
		} else {
			return renderWest(0, split, namePos);
		}
	}

	private int renderWest(int pos, boolean split, int namePos)
	{
		GL11.glPushMatrix();
		{
			startRender(pos);

			double splitZ = 1;
			double splitY = 1;
			if(split) {
				splitZ = cuboid.getDepth();
				splitY = cuboid.getHeight();
			}
			
			for(int i=0; i<splitZ; i++) {
				for(int j=0; j<splitY; j++) {
					double uStart = 0;
					double uEnd = 1;
					double vStart = 0;
					double vEnd = 1;
					
					if(!fitTexture) {
						uStart = textureU / 16;
						uEnd = textureUEnd / 16;
						vStart = textureV / 16;
						vEnd = textureVEnd / 16;
					}
					
					double uSize = (uEnd - uStart);
					double vSize = (vEnd - vStart);
					
					uStart += i * uSize/splitZ;
					uEnd = uStart + (uSize / splitZ);
					vStart += (splitY-(j+1)) * vSize/splitY;
					vEnd = vStart + (vSize / splitY);
					
					double yStart = cuboid.getStartY();
					double yEnd = cuboid.getStartY() + cuboid.getHeight();
					double zStart = cuboid.getStartZ();
					double zEnd = cuboid.getStartZ() + cuboid.getDepth();
					
					double ySize = yEnd - yStart;
					double zSize = zEnd - zStart;
					
					zStart += i * zSize/splitZ;
					zEnd = zStart + (zSize / splitZ);
					yStart += j * ySize/splitY;
					yEnd = yStart + (ySize / splitY);
					
					if(split) {
						GL11.glLoadName(namePos++);
					}
					GL11.glBegin(GL11.GL_QUADS);
					{
						if (binded)
							setTexCoord(0, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(cuboid.getStartX(), yStart, zStart);
		
						if (binded)
							setTexCoord(1, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(cuboid.getStartX(), yStart, zEnd);
		
						if (binded)
							setTexCoord(2, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(cuboid.getStartX(), yEnd, zEnd);
		
						if (binded)
							setTexCoord(3, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(cuboid.getStartX(), yEnd, zStart);
					}
					GL11.glEnd();
				}
			}

			finishRender();
		}
		GL11.glPopMatrix();
		
		return namePos;
	}

	public int renderUp(boolean split, int namePos)
	{
		if(TextureManager.getTextureEntry(texture)!=null && TextureManager.getTextureEntry(texture).isInterpolated() && TextureManager.getTextureEntry(texture).getFrameCount()>1) {
			renderUp(0, split, namePos);
			return renderUp(1, split, namePos);
		} else {
			return renderUp(0, split, namePos);
		}
	}

	private int renderUp(int pos, boolean split, int namePos)
	{
		GL11.glPushMatrix();
		{
			startRender(pos);
			
			double splitX = 1;
			double splitZ = 1;
			if(split) {
				splitX = cuboid.getWidth();
				splitZ = cuboid.getDepth();
			}
			
			for(int i=0; i<splitX; i++) {
				for(int j=0; j<splitZ; j++) {
					double uStart = 0;
					double uEnd = 1;
					double vStart = 0;
					double vEnd = 1;
					
					if(!fitTexture) {
						uStart = textureU / 16;
						uEnd = textureUEnd / 16;
						vStart = textureV / 16;
						vEnd = textureVEnd / 16;
					}
					
					double uSize = (uEnd - uStart);
					double vSize = (vEnd - vStart);
					
					uStart += i * uSize/splitX;
					uEnd = uStart + (uSize / splitX);
					vStart += j * vSize/splitZ;
					vEnd = vStart + (vSize / splitZ);
					
					double xStart = cuboid.getStartX();
					double xEnd = cuboid.getStartX() + cuboid.getWidth();
					double zStart = cuboid.getStartZ();
					double zEnd = cuboid.getStartZ() + cuboid.getDepth();
					
					double xSize = xEnd - xStart;
					double zSize = zEnd - zStart;
					
					xStart += i * xSize/splitX;
					xEnd = xStart + (xSize / splitX);
					zStart += j * zSize/splitZ;
					zEnd = zStart + (zSize / splitZ);
					
					if(split) {
						GL11.glLoadName(namePos++);
					}
					GL11.glBegin(GL11.GL_QUADS);
					{
						if (binded)
							setTexCoord(0, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(xStart, cuboid.getStartY() + cuboid.getHeight(), zEnd);
		
						if (binded)
							setTexCoord(1, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(xEnd, cuboid.getStartY() + cuboid.getHeight(), zEnd);
		
						if (binded)
							setTexCoord(2, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(xEnd, cuboid.getStartY() + cuboid.getHeight(), zStart);
		
						if (binded)
							setTexCoord(3, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(xStart, cuboid.getStartY() + cuboid.getHeight(), zStart);
					}
					GL11.glEnd();
				}
			}

			finishRender();
		}
		GL11.glPopMatrix();
		
		return namePos;
	}

	public int renderDown(boolean split, int namePos)
	{
		if(TextureManager.getTextureEntry(texture)!=null && TextureManager.getTextureEntry(texture).isInterpolated() && TextureManager.getTextureEntry(texture).getFrameCount()>1) {
			renderDown(0, split, namePos);
			return renderDown(1, split, namePos);
		} else {
			return renderDown(0, split, namePos);
		}
	}

	private int renderDown(int pos, boolean split, int namePos)
	{
		GL11.glPushMatrix();
		{
			startRender(pos);
			
			double splitX = 1;
			double splitZ = 1;
			if(split) {
				splitX = cuboid.getWidth();
				splitZ = cuboid.getDepth();
			}
			
			for(int i=0; i<splitX; i++) {
				for(int j=0; j<splitZ; j++) {
					double uStart = 0;
					double uEnd = 1;
					double vStart = 0;
					double vEnd = 1;
					
					if(!fitTexture) {
						uStart = textureU / 16;
						uEnd = textureUEnd / 16;
						vStart = textureV / 16;
						vEnd = textureVEnd / 16;
					}
					
					double uSize = (uEnd - uStart);
					double vSize = (vEnd - vStart);
					
					uStart += i * uSize/splitX;
					uEnd = uStart + (uSize / splitX);
					vStart += (splitZ-(j+1)) * vSize/splitZ;
					vEnd = vStart + (vSize / splitZ);
					
					double xStart = cuboid.getStartX();
					double xEnd = cuboid.getStartX() + cuboid.getWidth();
					double zStart = cuboid.getStartZ();
					double zEnd = cuboid.getStartZ() + cuboid.getDepth();
					
					double xSize = xEnd - xStart;
					double zSize = zEnd - zStart;
					
					xStart += i * xSize/splitX;
					xEnd = xStart + (xSize / splitX);
					zStart += j * zSize/splitZ;
					zEnd = zStart + (zSize / splitZ);
					
					if(split) {
						GL11.glLoadName(namePos++);
					}
					GL11.glBegin(GL11.GL_QUADS);
					{
						if (binded)
							setTexCoord(0, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(xStart, cuboid.getStartY(), zStart);
		
						if (binded)
							setTexCoord(1, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(xEnd, cuboid.getStartY(), zStart);
		
						if (binded)
							setTexCoord(2, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(xEnd, cuboid.getStartY(), zEnd);
		
						if (binded)
							setTexCoord(3, uStart, uEnd, vStart, vEnd);
						GL11.glVertex3d(xStart, cuboid.getStartY(), zEnd);
					}
					GL11.glEnd();
				}
			}

			finishRender();
		}
		GL11.glPopMatrix();
		
		return namePos;
	}

	public void setTexCoord(int corner)
	{
		setTexCoord(corner, textureU/16, textureUEnd/16, textureV/16, textureVEnd/16);
	}
	
	public void setTexCoord(int corner, double uStart, double uEnd, double vStart, double vEnd)
	{
		int coord = corner + rotation;
		if(coord == 0 | coord == 4)
			GL11.glTexCoord2d(uStart, vEnd);
		if(coord == 1 | coord == 5)
			GL11.glTexCoord2d(uEnd, vEnd);
		if(coord == 2 | coord == 6)
			GL11.glTexCoord2d(uEnd, vStart);
		if(coord == 3)
			GL11.glTexCoord2d(uStart, vStart);
	}

	public void startRender(int pos)
	{
		GL11.glEnable(GL_TEXTURE_2D);
		
		GL11.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		GL11.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT); 

		bindTexture(pos);
	}

	public void finishRender()
	{

		GL11.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		GL11.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT); 
	    
		GL11.glDisable(GL_TEXTURE_2D);
		GL11.glDepthFunc(GL11.GL_LESS);
	}

	public void setTexture(String texture)
	{
		this.texture = texture;
	}

	public void bindTexture(int pos)
	{
		TextureImpl.bindNone();
		if (texture != null)
		{
			TextureEntry entry = TextureManager.getTextureEntry(texture);
			if(entry!=null) {
				if(pos==0) {
					if(entry.getTexture()!=null) {
						GL11.glColor3f(1.0F, 1.0F, 1.0F);
						entry.getTexture().bind();
					}
				} else if(pos==1) {
					if(entry.getNextTexture()!=null) {
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						GL11.glDepthFunc(GL11.GL_EQUAL);
						
						entry.getNextTexture().bind();
						GL11.glColor4d(1.0D, 1.0D, 1.0D, entry.getFrameInterpolation());
					}
				}
				
				if(TextureManager.getTextureEntry(texture).isBlurred()) {
					GL11.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
					GL11.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				}
				if(TextureManager.getTextureEntry(texture).isClamped()) {
					GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
				    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE); 
				}
				
				binded = true;
			}
		}
	}

	public void addTextureX(double amt)
	{
		this.textureU += amt;
	}

	public void addTextureY(double amt)
	{
		this.textureV += amt;
	}
	
	public void addTextureXEnd(double amt)
	{
		this.textureUEnd += amt;
	}

	public void addTextureYEnd(double amt)
	{
		this.textureVEnd += amt;
	}

	public double getStartU()
	{
		return textureU;
	}

	public double getStartV()
	{
		return textureV;
	}

	public double getEndU()
	{
		return textureUEnd;
	}

	public double getEndV()
	{
		return textureVEnd;
	}
	
	public void setStartU(double u)
	{
		textureU = u;
	}

	public void setStartV(double v)
	{
		textureV = v;
	}
	
	public void setEndU(double ue)
	{
		textureUEnd = ue;
	}

	public void setEndV(double ve)
	{
		textureVEnd = ve;
	}

	public String getTextureName()
	{
		return texture;
	}

	public Texture getTexture()
	{
		return TextureManager.getTexture(texture);
	}

	public String getTextureLocation()
	{
		return textureLocation;
	}

	public void setTextureLocation(String textureLocation)
	{
		this.textureLocation = textureLocation;
	}

	public void fitTexture(boolean fitTexture)
	{
		this.fitTexture = fitTexture;
	}

	public boolean shouldFitTexture()
	{
		return fitTexture;
	}

	public int getSide()
	{
		return side;
	}

	public boolean isCullfaced()
	{
		return cullface;
	}

	public void setCullface(boolean cullface)
	{
		this.cullface = cullface;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public boolean isAutoUVEnabled()
	{
		return autoUV;
	}

	public void setAutoUVEnabled(boolean enabled)
	{
		this.autoUV = enabled;
	}
	
	public void updateUV() {
		if(autoUV) {
			textureUEnd = textureU + cuboid.getFaceDimension(side).getWidth();
			textureVEnd = textureV + cuboid.getFaceDimension(side).getHeight();
		}
	}
	
	public boolean isBinded() {
		return binded;
	}

	public static String getFaceName(int face)
	{
		switch (face)
		{
		case 0:
			return "north";
		case 1:
			return "east";
		case 2:
			return "south";
		case 3:
			return "west";
		case 4:
			return "up";
		case 5:
			return "down";
		}
		return null;
	}
	
	public static int getFaceSide(String name)
	{
		switch (name)
		{
		case "north":
			return 0;
		case "east":
			return 1;
		case "south":
			return 2;
		case "west":
			return 3;
		case "up":
			return 4;
		case "down":
			return 5;
		}
		return -1;
	}

	public int getRotation()
	{
		return rotation;
	}

	public void setRotation(int rotation)
	{
		this.rotation = rotation;
	}
}
