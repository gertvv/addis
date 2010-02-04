/**
 * 
 */
package org.drugis.addis.presentation;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;

public class MeasurementTableModel extends AbstractTableModel {		
	private static final long serialVersionUID = 5331596469882184969L;

	private Study d_study;
	private PresentationModelFactory d_pmf;
	
	public MeasurementTableModel(Study study, PresentationModelFactory pmf) {
		d_study = study;
		d_pmf = pmf;
	}

	public int getColumnCount() {
		return d_study.getArms().size();
	}

	public int getRowCount() {
		return d_study.getOutcomeMeasures().size();
	}
	
	public boolean isCellEditable(int row, int col) {
		return true;
	}
	
	@Override
	public String getColumnName(int index) {
		return d_pmf.getLabeledModel(d_study.getArms().get(index)).getLabelModel().getString();	
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		OutcomeMeasure om = new ArrayList<OutcomeMeasure>(d_study.getOutcomeMeasures()).get(rowIndex);
		Arm arm = d_study.getArms().get(columnIndex);
		return d_pmf.getLabeledModel(d_study.getMeasurement(om, arm));
	}
}