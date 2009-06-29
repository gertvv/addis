package nl.rug.escher.addis.gui;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import nl.rug.escher.addis.entities.BasicPatientGroup;
import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.MetaStudy;
import nl.rug.escher.addis.entities.Study;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.list.SelectionInList;

import fi.smaa.common.ImageLoader;

public class GUIFactory {

	public static JComponent createEndpointLabelWithIcon(ImageLoader loader, Study s, Endpoint e) {
		String fname = FileNames.ICON_STUDY;
		if (s instanceof MetaStudy) {
			MetaStudy ms = (MetaStudy) s;
			if (ms.getAnalysis().getEndpoint().equals(e)) {
				fname = FileNames.ICON_METASTUDY;
			}
		}
		JLabel textLabel = null;
		try {
			Icon icon = loader.getIcon(fname);
			textLabel = new JLabel(e.getName(), icon, JLabel.CENTER);			
		} catch (FileNotFoundException ex) {
			textLabel = new JLabel(e.getName());			
			ex.printStackTrace();
		}
		Bindings.bind(textLabel, "text", 
				new PresentationModel<Endpoint>(e).getModel(Endpoint.PROPERTY_NAME));
		return textLabel;
	}

	public static JComboBox createDrugSelector(PresentationModel<BasicPatientGroup> model, Domain domain) {
		SelectionInList<Drug> drugSelectionInList =
			new SelectionInList<Drug>(
					new ArrayList<Drug>(domain.getDrugs()),
					model.getModel(BasicPatientGroup.PROPERTY_DRUG));
		return BasicComponentFactory.createComboBox(drugSelectionInList);
	}

}
