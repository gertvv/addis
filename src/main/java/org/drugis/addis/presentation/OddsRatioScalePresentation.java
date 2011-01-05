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

import org.drugis.addis.entities.analysis.OddsRatioToClinicalConverter;
import org.drugis.common.Interval;

import com.jgoodies.binding.value.AbstractValueModel;

import fi.smaa.jsmaa.model.ScaleCriterion;


@SuppressWarnings("serial")
public class OddsRatioScalePresentation extends RiskScalePresentation {
	public final class OddsRatioValueModel extends ScaleConvertingValueModel {
		public Object getValue() {
			return getOddsRatio();
		}
	}
	
	public static final String PROPERTY_ODDS_RATIO = "oddsRatio";
	
	private final OddsRatioToClinicalConverter d_converter;

	public OddsRatioScalePresentation(ScaleCriterion criterion, OddsRatioToClinicalConverter converter) {
		super(criterion);
		d_converter = converter;
	}
	
	@Override
	public AbstractValueModel getModel(String property) {
		if (property.equals(PROPERTY_ODDS_RATIO)) {	
			return new OddsRatioValueModel();
		}
		return super.getModel(property);
	}
	
	private Interval<Double> getOddsRatio(){
		return d_converter.getOddsRatio(convertInterval(getBean().getScale()));
	}
	
	@Override
	protected Interval<Double> getRisk(){
		return d_converter.getRisk(convertInterval(getBean().getScale()));
	}
}
