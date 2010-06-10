/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

package org.drugis.addis.entities.relativeeffect;


import org.drugis.addis.entities.RateMeasurement;

public abstract class BasicRatio extends AbstractBasicRelativeEffect<RateMeasurement> {
	protected BasicRatio(RateMeasurement baseline, RateMeasurement subject) throws IllegalArgumentException {
		super(baseline, subject);
	}

	public TransformedLogStudentT getDistribution() {
		return new TransformedLogStudentT(getMu(), getSigma(), getDegreesOfFreedom());
	}
	
	protected abstract double getMu();
	protected abstract double getSigma();
}