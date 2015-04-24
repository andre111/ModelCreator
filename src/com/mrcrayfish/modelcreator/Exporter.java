package com.mrcrayfish.modelcreator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.stream.JsonWriter;
import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.ElementManager;
import com.mrcrayfish.modelcreator.element.Face;

public class Exporter
{
	private List<String> textureList = new ArrayList<String>();

	// Model Variables
	private ElementManager manager;

	public Exporter(ElementManager manager)
	{
		this.manager = manager;
		compileTextureList();
	}

	public File export(File file)
	{
		File path = file.getParentFile();
		if (path.exists() && path.isDirectory()) {
			writeJSONFile(file);
		}
		return file;
	}
	
	public File writeJSONFile(File file) {
		FileWriter fw;
		JsonWriter writer;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file);
			writer = new JsonWriter(fw);
			writer.setIndent("\t");
			writeComponents(writer, manager);
			writer.close();
			fw.close();
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void compileTextureList()
	{
		for (Element cuboid : manager.getAllElements())
		{
			for (Face face : cuboid.getAllFaces())
			{
				if (!textureList.contains(face.getTextureLocation() + face.getTextureName()))
				{
					textureList.add(face.getTextureLocation() + face.getTextureName());
				}
			}
		}
	}

	private void writeComponents(JsonWriter writer, ElementManager manager) throws IOException
	{
		writer.beginObject();
		
		writer.name("__comment").value("Model generated using MrCrayfish's Model Creator (http://mrcrayfish.com/modelcreator/)");
		if (!manager.getAmbientOcc())
		{
			writer.name("ambientocclusion").value(manager.getAmbientOcc());
		}
		writeTextures(writer);

		writer.name("elements");
		writer.beginArray();
		for (int i = 0; i < manager.getElementCount(); i++)
		{
			writer.beginObject();
			writeElement(writer, manager.getElement(i));
			writer.endObject();
		}
		writer.endArray();
		
		writer.endObject();
	}

	private void writeTextures(JsonWriter writer) throws IOException
	{
		writer.name("textures");
		writer.beginObject();
		for (String texture : textureList)
		{
			writer.name(""+textureList.indexOf(texture)).value(texture);
		}
		writer.endObject();
	}

	private void writeElement(JsonWriter writer, Element cuboid) throws IOException
	{
		writer.name("name").value(cuboid.toString());
		writeBounds(writer, cuboid);

		if (!cuboid.isShaded())
		{
			writer.name("shade").value(cuboid.isShaded());
		}
		if (cuboid.getRotation() != 0)
		{
			writeRotation(writer, cuboid);
		}
		writeFaces(writer, cuboid);

	}

	private void writeBounds(JsonWriter writer, Element cuboid) throws IOException
	{
		writer.name("from");
		writer.beginArray();
		writer.value(cuboid.getStartX());
		writer.value(cuboid.getStartY());
		writer.value(cuboid.getStartZ());
		writer.endArray();
		
		writer.name("to");
		writer.beginArray();
		writer.value(cuboid.getStartX() + cuboid.getWidth());
		writer.value(cuboid.getStartY() + cuboid.getHeight());
		writer.value(cuboid.getStartZ() + cuboid.getDepth());
		writer.endArray();
	}
	
	private void writeRotation(JsonWriter writer, Element cuboid) throws IOException
	{
		writer.name("rotation");
		writer.beginObject();
		
		writer.name("origin");
		writer.beginArray();
		writer.value(cuboid.getOriginX());
		writer.value(cuboid.getOriginY());
		writer.value(cuboid.getOriginZ());
		writer.endArray();
		
		writer.name("axis").value(Element.parseAxis(cuboid.getPrevAxis()));
		writer.name("angle").value(cuboid.getRotation());
		if (cuboid.shouldRescale())
		{
			writer.name("rescale").value(cuboid.shouldRescale());
		}
		writer.endObject();
	}

	private void writeFaces(JsonWriter writer, Element cuboid) throws IOException
	{
		writer.name("faces");
		writer.beginObject();
		for (Face face : cuboid.getAllFaces())
		{
			if(face.isEnabled()) {
				writer.name(Face.getFaceName(face.getSide()));
				writer.beginObject();
				
				writer.name("texture").value("#" + textureList.indexOf(face.getTextureLocation() + face.getTextureName()));
				
				writer.name("uv");
				writer.beginArray();
				writer.value(face.getStartU());
				writer.value(face.getStartV());
				writer.value(face.getEndU());
				writer.value(face.getEndV());
				writer.endArray();
				
				if (face.getRotation() > 0)
					writer.name("rotation").value((int) face.getRotation() * 90);
				if (face.isCullfaced())
					writer.name("cullface").value(Face.getFaceName(face.getSide()));
				
				writer.endObject();
			}
		}
		writer.endObject();
	}
}
