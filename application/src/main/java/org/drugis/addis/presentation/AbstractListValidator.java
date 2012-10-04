/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;

public abstract class AbstractListValidator<E> extends AbstractValueModel implements ValueHolder<Boolean> {
	private static final long serialVersionUID = 391045513777444696L;
	protected final ObservableList<E> d_list;
	private boolean d_value;
	
	public AbstractListValidator(ObservableList<E> list) {
		d_list = list;
		d_list.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				update();
			}
			public void intervalAdded(ListDataEvent e) {
				update();
			}
			public void contentsChanged(ListDataEvent e) {
				update();
			}
		});
		update();
	}
	
	private void update() {
		boolean oldValue = d_value;
		d_value = validate();
		fireValueChange(oldValue, d_value);
	}
	
	public abstract boolean validate();
	
	@Override
	public Boolean getValue() {
		return d_value;
	}
	
	@Override
	public void setValue(Object newValue) {
		throw new IllegalAccessError("ListValidator is read-only");
	}
}