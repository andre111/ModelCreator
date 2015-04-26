package com.mrcrayfish.modelcreator.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.mrcrayfish.modelcreator.Icons;
import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.ElementManager;
import com.mrcrayfish.modelcreator.texture.TextureManager;
import com.mrcrayfish.modelcreator.util.ComponentUtil;

public class GlobalPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private ElementManager manager;

	private JRadioButton ambientOcc;
	//private JLabel particleImage;
	private JButton btnParticle;

	public GlobalPanel(ElementManager manager)
	{
		this.manager = manager;
		setLayout(new GridLayout(2, 1, 0, 5));
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(221, 221, 228), 5), "<html><b>Global Properties</b></html>"));
		setMaximumSize(new Dimension(186, 80));
		initComponents();
		addComponents();
	}

	public void initComponents()
	{
		ambientOcc = ComponentUtil.createRadioButton("Ambient Occulusion", "Determine the light for each element");
		ambientOcc.setSelected(true);
		ambientOcc.addActionListener(a -> manager.setAmbientOcc(ambientOcc.isSelected()));
		
		//particleImage = new JLabel("Particle: missing");

		btnParticle = new JButton("Particle");
		btnParticle.setIcon(Icons.texture);
		btnParticle.addActionListener(a ->
		{
			String texture = TextureManager.display(manager);
			if (texture != null)
			{
				manager.setParticle(texture);
				btnParticle.setText(texture);
			}
		});
	}

	public void addComponents()
	{
		add(ambientOcc);
		add(btnParticle);
	}

	@Override
	public void updateValues(Element cube)
	{
		ambientOcc.setSelected(manager.getAmbientOcc());
	}
}