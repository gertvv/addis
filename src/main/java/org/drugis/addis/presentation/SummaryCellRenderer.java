/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

package org.drugis.addis.presentation;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.drugis.mtc.summary.NodeSplitPValueSummary;
import org.drugis.mtc.summary.NormalSummary;
import org.drugis.mtc.summary.QuantileSummary;

public class SummaryCellRenderer implements TableCellRenderer {

	private static final DecimalFormat s_format = new DecimalFormat("#.##");

	public Component getTableCellRendererComponent(JTable table, Object cellContents,
			boolean isSelected, boolean hasFocus, int row, int column) {
		return (new DefaultTableCellRenderer()).getTableCellRendererComponent(table, getCellText(cellContents), isSelected, hasFocus, row, column);
	}

	private String getCellText(Object cellContents) {
		String str = "N/A";
		if (cellContents instanceof NormalSummary) {
			str = getNormalSummaryString(cellContents);
		} else if (cellContents instanceof QuantileSummary) {
			str = getQuantileSummaryString(cellContents);
		} else if (cellContents instanceof NodeSplitPValueSummary) {
			str = getNodeSplitPvalueString(cellContents);
		}
		return str;
	}

	private String getNodeSplitPvalueString(Object cellContents) {
		NodeSplitPValueSummary re = (NodeSplitPValueSummary)cellContents;
		
		String str = "N/A";
		if (re != null && re.getDefined()) {
			str = format(re.getPvalue());
		}
		return str;
	}

	private String getQuantileSummaryString(Object cellContents) {
		QuantileSummary re = (QuantileSummary)cellContents;
		
		String str = "N/A";
		if (re != null && re.getDefined()) {
			str = format(re.getQuantile(1)) + " (" + 
				format(re.getQuantile(0)) + ", " + format(re.getQuantile(2)) + ")";
		}
		return str;
	}
	
	private String getNormalSummaryString(Object cellContents) {
		NormalSummary re = (NormalSummary)cellContents;
		
		String str = "N/A";
		if (re != null && re.getDefined()) {
			String mu = format(re.getMean());
			String sigma = format(re.getStandardDeviation());
			
			str = mu + " \u00B1 " + sigma;
		}
		return str;
	}
	
	 String format(double d) {
    	return s_format.format(d);
	 }
}