/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.gui.builder;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.treatment.CategoryNode;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AddDosedDrugTreatmentView implements ViewBuilder {
	private PresentationModel<DosedDrugTreatment> d_model;
	private JPanel d_dialogPanel = new JPanel();
	private NotEmptyValidator d_validator;
	private final Domain d_domain;
	private final AddisWindow d_mainWindow;
	private DosedDrugTreatmentPresentation d_pm; 

	public AddDosedDrugTreatmentView(PresentationModel<DosedDrugTreatment> presentationModel, 
			Domain domain, 
			AddisWindow mainWindow, 
			JButton okButton) {
		d_model = presentationModel;
		d_domain = domain;
		d_mainWindow = mainWindow;
		d_validator = new NotEmptyValidator();
		d_pm = new DosedDrugTreatmentPresentation(d_model.getBean());
		d_pm.getCategories().addListDataListener(new ListDataListener() {			
			@Override
			public void intervalRemoved(ListDataEvent e) {
				rebuildPanel();
			}
			
			@Override
			public void intervalAdded(ListDataEvent e) {
				rebuildPanel();
			}
			
			@Override
			public void contentsChanged(ListDataEvent e) {
				rebuildPanel();
			}
		});
		Bindings.bind(okButton, "enabled", d_validator);
	}		
	
	public JComponent buildPanel() {
		JPanel dialog = initialize();
		d_dialogPanel.setLayout(new BorderLayout());
		d_dialogPanel.setPreferredSize(new Dimension(400, 600));
		d_dialogPanel.add(dialog);
		return d_dialogPanel;	
	}	
	
	private void rebuildPanel() {
		d_dialogPanel.setVisible(false);
		d_dialogPanel.removeAll();
		d_dialogPanel.add(initialize());
		d_dialogPanel.setVisible(true);
	}
	
	private JPanel initialize() {
		JTextField name = BasicComponentFactory.createTextField(d_model.getModel(DosedDrugTreatment.PROPERTY_NAME), false);
		name.setColumns(20);
		d_validator.add(name);
		
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref, 3dlu, pref, fill:pref:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, fill:pref:grow, 3dlu, p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		int row = 1;
		int colSpan = 6;

		final AbstractValueModel drugModel = d_model.getModel(DosedDrugTreatment.PROPERTY_DRUG);
		builder.addLabel("Drug:", cc.xy(1, row));
		JComboBox drugSelect = AuxComponentFactory.createBoundComboBox(d_domain.getDrugs(), drugModel, true);
		builder.add(drugSelect, cc.xy(3, row));
		builder.add(createNewDrugButton(drugModel), cc.xy(5, row));
		d_validator.add(drugSelect);

		row += 2;
		
		builder.addLabel("Name:", cc.xy(1, row));
		builder.add(name, cc.xy(3, row));
		
		row +=2;
		builder.addSeparator("Category labels", cc.xyw(1, row, colSpan));
		
		row += 2;
		builder.add(createCategoriesPanel(d_pm), cc.xyw(1, row, colSpan));
		
		
		return builder.getPanel();
	}
	
	private JButton createNewDrugButton(final AbstractValueModel drugModel) {
		JButton btn = GUIFactory.createPlusButton("Create drug");
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				d_mainWindow.showAddDialog(CategoryKnowledgeFactory.getCategoryKnowledge(Drug.class), drugModel);
			}
		});
		return btn;
	}
	
	private JComponent createCategoriesPanel(final DosedDrugTreatmentPresentation model) { 
		FormLayout layout = new FormLayout(
				"left:pref, 3dlu, fill:pref:grow, 3dlu, pref",
				"p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		int row = 1;

		for(final CategoryNode category : model.getCategories()) {
			builder.add(new JLabel("Category name"), cc.xy(1, row));
			JTextField name = BasicComponentFactory.createTextField(category.getNameModel(), false);
			builder.add(name, cc.xy(3, row));
			JButton remove = GUIFactory.createIconButton(FileNames.ICON_DELETE, "delete");
			remove.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					model.getCategories().remove(category);
				}
			});
			builder.add(remove, cc.xy(5, row));
			row = LayoutUtil.addRow(layout, row);
		}
		
		builder.add(createAddCategoryButton(model), cc.xy(1, row));
		
		return builder.getPanel();
	}
	

	private JButton createAddCategoryButton(final DosedDrugTreatmentPresentation model) {
		JButton btn = GUIFactory.createLabeledIconButton("Add category" ,FileNames.ICON_PLUS);
		btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				model.getCategories().add(new CategoryNode());
			}
		});
		return btn;
	}
}