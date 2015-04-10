package com.mrcrayfish.modelcreator.texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import org.newdawn.slick.opengl.Texture;

public class TextureEntry
{
	private String name;
	private List<Texture> texture = new ArrayList<Texture>();
	private List<ImageIcon> image = new ArrayList<ImageIcon>();
	private List<Integer> frames = new ArrayList<Integer>();
	private Map<Integer, Integer> customTimes = new HashMap<Integer, Integer>();
	private int frametime;

	public TextureEntry(String name, Texture texture, ImageIcon image)
	{
		this.name = name;
		
		this.texture.add(texture);
		this.image.add(image);
		this.frames.add(0);
		
		frametime = 1;
	}
	
	public TextureEntry(String name) {
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Texture getTexture()
	{
		return texture.get(getCurrentAnimationFrame());
	}
	
	public ImageIcon getImage()
	{
		return image.get(getCurrentAnimationFrame());
	}

	public void addTexture(Texture texture, ImageIcon image)
	{
		this.texture.add(texture);
		this.image.add(image);
		this.frames.add(this.texture.size()-1);
	}
	
	public void setFrameTime(int frametime) {
		this.frametime = frametime;
	}
	
	public void setFrames(List<Integer> frameList) {
		frames = new ArrayList<Integer>();
		frames.addAll(frameList);
	}
	public void setCustomTimes(Map<Integer, Integer> times) {
		customTimes = new HashMap<Integer, Integer>();
		customTimes.putAll(times);
	}
	
	public int getCurrentAnimationFrame() {
		long maxTime = 0;
		for(int i=0; i<frames.size(); i++) {
			maxTime += getFrameTime(i);
		}
		
		long animTime = System.currentTimeMillis() % maxTime;
		
		for(int i=0; i<frames.size(); i++) {
			if(animTime<=getFrameTime(i)) {
				return frames.get(i);
			}
			animTime -= getFrameTime(i);
		}
		
		return 0;
	}
	
	public long getFrameTime(int frame) {
		if(customTimes.containsKey(frame)) {
			return customTimes.get(frame);
		}
		return frametime * 50L;
	}
}
