package com.mrcrayfish.modelcreator;

import static org.lwjgl.opengl.GL11.*;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.ResourceLoader;

import com.mrcrayfish.modelcreator.dialog.WelcomeDialog;
import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.ElementManager;
import com.mrcrayfish.modelcreator.element.Face;
import com.mrcrayfish.modelcreator.panels.SidebarPanel;
import com.mrcrayfish.modelcreator.texture.PendingTexture;
import com.mrcrayfish.modelcreator.texture.TextureManager;

public class ModelCreator extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	//TODO remove static instance
	public static String texturePath = ".";

	// Canvas Variables
	private final static AtomicReference<Dimension> newCanvasSize = new AtomicReference<Dimension>();
	private final Canvas canvas;
	private int width = 990, height = 800;

	private Camera camera;
	public Texture texture;

	public boolean closeRequested = false;

	// Swing Components
	private JMenuBar menuBar = new JMenuBar();
	private ElementManager manager;

	// Texture Loading Cache
	public List<PendingTexture> pendingTextures = new ArrayList<PendingTexture>();

	private TrueTypeFont font;
	private TrueTypeFont fontBebasNeue;

	public ModelCreator(String title)
	{
		super(title);
		
		setPreferredSize(new Dimension(1493, 840));
		setMinimumSize(new Dimension(1200, 840));
		setLayout(new BorderLayout(10, 0));

		canvas = new Canvas();

		initComponents();

		canvas.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				newCanvasSize.set(canvas.getSize());
			}
		});

		addWindowFocusListener(new WindowAdapter()
		{
			@Override
			public void windowGainedFocus(WindowEvent e)
			{
				canvas.requestFocusInWindow();
			}
		});

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				closeRequested = true;
			}
		});

		manager.updateValues();

		pack();
		setVisible(true);
		setLocationRelativeTo(null);

		Thread loopThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				initDisplay();

				try
				{
					Display.create();
					
					WelcomeDialog.show(ModelCreator.this);

					initFonts();
					TextureManager.init();

					loop();

					Display.destroy();
					dispose();
					System.exit(0);
				}
				catch (LWJGLException e1)
				{
					e1.printStackTrace();
				}
			}
		});
		loopThread.start();
	}

	public void initComponents()
	{
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);

		JMenuItem menuItemNew = new JMenuItem("New");
		menuItemNew.setMnemonic(KeyEvent.VK_N);
		menuItemNew.setToolTipText("New Model");
		menuItemNew.addActionListener(a ->
		{
			int returnVal = JOptionPane.showConfirmDialog(this, "You current work will be cleared, are you sure?", "Note", JOptionPane.YES_NO_OPTION);
			if (returnVal == JOptionPane.YES_OPTION)
			{
				manager.clearElements();
				manager.updateValues();
			}
		});

		JMenuItem menuItemExport = new JMenuItem("Export");
		menuItemExport.setMnemonic(KeyEvent.VK_E);
		menuItemExport.setToolTipText("Export model to JSON");
		menuItemExport.addActionListener(e ->
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Output Directory");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				Exporter exporter = new Exporter(manager, chooser.getSelectedFile().getAbsolutePath(), "test");
				exporter.export();
			}
		});

		JMenuItem menuItemExit = new JMenuItem("Exit");
		menuItemExit.setMnemonic(KeyEvent.VK_E);
		menuItemExit.setToolTipText("Exit application");
		menuItemExit.addActionListener(e ->
		{
			System.exit(0);
		});
		
		JMenuItem menuItemImport = new JMenuItem("Import");
		menuItemImport.setMnemonic(KeyEvent.VK_I);
		menuItemImport.setToolTipText("Import model from JSON");
		menuItemImport.addActionListener(e ->
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Input File");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				Importer importer = new Importer(manager, chooser.getSelectedFile().getAbsolutePath());
				importer.importFromJSON();
			}
		});
		
		JMenuItem menuItemTexturePath = new JMenuItem("Set Texture path");
		menuItemTexturePath.setMnemonic(KeyEvent.VK_S);
		menuItemTexturePath.setToolTipText("Set the base path from where to look for textures");
		menuItemTexturePath.addActionListener(e ->
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Texture path");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				texturePath = chooser.getSelectedFile().getAbsolutePath();
			}
		});

		file.add(menuItemNew);
		file.add(menuItemExport);
		file.add(menuItemImport);
		file.add(menuItemTexturePath);
		file.add(menuItemExit);
		menuBar.add(file);
		setJMenuBar(menuBar);

		canvas.setSize(new Dimension(990, 790));
		add(canvas, BorderLayout.CENTER);

		canvas.setFocusable(true);
		canvas.setVisible(true);
		canvas.requestFocus();

		manager = new SidebarPanel(this);
		add((JPanel) manager, BorderLayout.EAST);
	}

	public void initDisplay()
	{
		try
		{
			Display.setParent(canvas);
			Display.setVSyncEnabled(true);
			Display.setInitialBackground(0.92F, 0.92F, 0.93F);
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
		}
	}

	public void initFonts()
	{
		Font awtFont = new Font("Times New Roman", Font.BOLD, 20);
		font = new TrueTypeFont(awtFont, false);

		try
		{
			InputStream inputStream = ModelCreator.class.getClassLoader().getResourceAsStream("bebas_neue.otf");
			Font customFont = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(50f);
			fontBebasNeue = new TrueTypeFont(customFont, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void loop() throws LWJGLException
	{
		camera = new Camera(60F, (float) Display.getWidth() / (float) Display.getHeight(), 0.3F, 1000F);

		Dimension newDim;

		while (!Display.isCloseRequested() && !getCloseRequested())
		{
			synchronized (this)
			{
				for (PendingTexture texture : pendingTextures)
				{
					texture.load();
				}
				pendingTextures.clear();
			}

			newDim = newCanvasSize.getAndSet(null);

			if (newDim != null)
			{
				width = newDim.width;
				height = newDim.height;
			}
			
			int offset = width / sidebar;

			handleInput();

			glViewport(offset, 0, width-offset, height);
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			GLU.gluPerspective(60F, (float) (width-offset) / (float) height, 0.3F, 1000F);
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			glEnable(GL_DEPTH_TEST);

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glLoadIdentity();
			camera.useView();

			drawPerspective();

			glDisable(GL_DEPTH_TEST);
			glDisable(GL_CULL_FACE);
			glDisable(GL_TEXTURE_2D);
			glDisable(GL_LIGHTING);

			glViewport(0, 0, width, height);
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			GLU.gluOrtho2D(0, width, height, 0);
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();

			drawOverlay();
			drawSideView(offset);

			Display.update();
		}
	}

	public void drawPerspective()
	{
		glClearColor(0.92F, 0.92F, 0.93F, 1.0F);
		drawGrid();

		glTranslatef(-8, 0, 8);
		for (int i = 0; i < manager.getCuboidCount(); i++)
		{
			Element cube = manager.getCuboid(i);
			cube.draw();
			cube.drawExtras(manager);
		}

		GL11.glPushMatrix();
		{
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			GL11.glScaled(0.018, 0.018, 0.018);
			GL11.glRotated(90, 1, 0, 0);
			fontBebasNeue.drawString(8, 0, "Model Creator by MrCrayfish", new Color(0.5F, 0.5F, 0.6F));

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glDisable(GL11.GL_BLEND);
		}
		GL11.glPopMatrix();
	}

	public void drawOverlay()
	{
		GL11.glPushMatrix();
		{
			glTranslatef(width - 80, height - 80, 0);
			glLineWidth(2F);
			glRotated(-camera.getRY(), 0, 0, 1);
	
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			font.drawString(-7, -75, "N", new Color(1, 1, 1));
			GL11.glDisable(GL11.GL_BLEND);
	
			glColor3d(0.6, 0.6, 0.6);
			glBegin(GL_LINES);
			{
				glVertex2i(0, -50);
				glVertex2i(0, 50);
				glVertex2i(-50, 0);
				glVertex2i(50, 0);
			}
			glEnd();
	
			glColor3d(0.3, 0.3, 0.6);
			glBegin(GL_TRIANGLES);
			{
				glVertex2i(-5, -45);
				glVertex2i(0, -50);
				glVertex2i(5, -45);
	
				glVertex2i(-5, 45);
				glVertex2i(0, 50);
				glVertex2i(5, 45);
	
				glVertex2i(-45, -5);
				glVertex2i(-50, 0);
				glVertex2i(-45, 5);
	
				glVertex2i(45, -5);
				glVertex2i(50, 0);
				glVertex2i(45, 5);
			}
			glEnd();
		}
		GL11.glPopMatrix();
	}
	
	private int sidebar = 4;
	private float[] sideViewX = { 30, 30, 30 };
	private float[] sideViewY = { 6, 6, 6 };
	private float[] sideViewSizes = { 250, 250, 250 };
	
	private Element sidebarClickedElement = null;
	private int sidebarClickField = 0;
	private int sidebarClickButton = 0;
	private double sidebarMXStart = 0;
	private double sidebarMYStart = 0;
	public void drawSideView(int w)
	{
		GL11.glPushMatrix();
		{
			glTranslatef(w, 0, 0);
			glLineWidth(2F);
	
			//GL11.glEnable(GL11.GL_BLEND);
			//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			//font.drawString(-7, -75, "N", new Color(1, 1, 1));
			//GL11.glDisable(GL11.GL_BLEND);
	
			glColor3d(0.6, 0.6, 0.6);
			glBegin(GL_LINES);
			{
				glVertex2i(0, 0);
				glVertex2i(0, height);
			}
			glEnd();
			glTranslatef(-w, 0, 0);
			
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
					
					drawSideView(sideViewX[i], sideViewY[i], sideViewSizes[i], i);
					
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
	
	public void drawSideView(float x, float y, float size, int side) {
		GL11.glPushMatrix();
		{
			glTranslatef(x, y, 0);
			glLineWidth(2F);
			
			//outside lines
			glBegin(GL_LINES);
			{
				glVertex2f(0, 0);
				glVertex2f(0, size);
				
				glVertex2f(size, 0);
				glVertex2f(size, size);
				
				glVertex2f(0, 0);
				glVertex2f(size, 0);
				
				glVertex2f(0, size);
				glVertex2f(size, size);
			}
			glEnd();
			
			//inside lines
			glLineWidth(1F);
			for(int i=1; i<16; i++) {
				glBegin(GL_LINES);
				{
					glVertex2f(size/16*i, 0);
					glVertex2f(size/16*i, size);
					
					glVertex2f(0, size/16*i);
					glVertex2f(size, size/16*i);
				}
				glEnd();
			}
			
			//Cubes
			for (int i = 0; i < manager.getCuboidCount(); i++)
			{
				Element cube = manager.getCuboid(i);
				if(!manager.getSelectedCuboid().equals(cube))
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
								GL11.glTexCoord2d(face.shouldFitTexture() ? 0 : (face.getStartU() / 16), face.shouldFitTexture() ? 0 : (face.getEndV() / 16));
							GL11.glVertex2d(xstart, ystart + height);
	
							if (face.isBinded())
								GL11.glTexCoord2d(face.shouldFitTexture() ? 1 : (face.getEndU() / 16), face.shouldFitTexture() ? 0 : (face.getEndV() / 16));
							GL11.glVertex2d(xstart + width, ystart + height);
	
							if (face.isBinded())
								GL11.glTexCoord2d(face.shouldFitTexture() ? 1 : (face.getEndU() / 16), face.shouldFitTexture() ? 1 : (face.getStartV() / 16));
							GL11.glVertex2d(xstart + width, ystart);
	
							if (face.isBinded())
								GL11.glTexCoord2d(face.shouldFitTexture() ? 1 : (face.getStartU() / 16), face.shouldFitTexture() ? 1 : (face.getStartV() / 16));
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

	public void handleInput()
	{
		final float cameraMod = Math.abs(camera.getZ());

		if(Mouse.getX()<width/sidebar) {
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
						}
					} else {
						sidebarClickedElement = null;
						sidebarMXStart = 0;
						sidebarMYStart = 0;
					}
				}
			}
		} else {
			sidebarClickedElement = null;
			sidebarMXStart = 0;
			sidebarMYStart = 0;
			
			if (Mouse.isButtonDown(0))
			{
				final float modifier = (cameraMod * 0.05f);
				camera.addX((float) (Mouse.getDX() * 0.01F) * modifier);
				camera.addY((float) (Mouse.getDY() * 0.01F) * modifier);
			}
			else if (Mouse.isButtonDown(1))
			{
				final float modifier = applyLimit(cameraMod * 0.1f);
				camera.rotateX(-(float) (Mouse.getDY() * 0.5F) * modifier);
				final float rxAbs = Math.abs(camera.getRX());
				camera.rotateY((rxAbs >= 90 && rxAbs < 270 ? -1 : 1) * (float) (Mouse.getDX() * 0.5F) * modifier);
			}
	
			final float wheel = Mouse.getDWheel();
			if (wheel != 0)
			{
				camera.addZ(wheel * (cameraMod / 5000F));
			}
		}
	}

	public float applyLimit(float value)
	{
		if (value > 0.4F)
		{
			value = 0.4F;
		}
		else if (value < 0.15F)
		{
			value = 0.15F;
		}
		return value;
	}

	public void drawGrid()
	{
		glPushMatrix();
		{
			glColor3f(0.2F, 0.2F, 0.27F);

			// Bold outside lines
			glLineWidth(2F);
			glBegin(GL_LINES);
			{
				glVertex3i(-8, 0, -8);
				glVertex3i(-8, 0, 8);
				glVertex3i(8, 0, -8);
				glVertex3i(8, 0, 8);
				glVertex3i(-8, 0, 8);
				glVertex3i(8, 0, 8);
				glVertex3i(-8, 0, -8);
				glVertex3i(8, 0, -8);
			}
			glEnd();

			// Thin inside lines
			glLineWidth(1F);
			glBegin(GL_LINES);
			{
				for (int i = -7; i <= 7; i++)
				{
					glVertex3i(i, 0, -8);
					glVertex3i(i, 0, 8);
				}

				for (int i = -7; i <= 7; i++)
				{
					glVertex3i(-8, 0, i);
					glVertex3i(8, 0, i);
				}
			}
			glEnd();
		}
		glPopMatrix();
	}

	public synchronized boolean getCloseRequested()
	{
		return closeRequested;
	}
}
