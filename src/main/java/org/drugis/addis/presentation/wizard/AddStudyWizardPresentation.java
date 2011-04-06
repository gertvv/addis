/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.TableModel;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.DependentEntitiesException;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainEvent;
import org.drugis.addis.entities.DomainListener;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.ObjectWithNotes;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.imports.ClinicaltrialsImporter;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.BasicArmPresentation;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.MutableCharacteristicHolder;
import org.drugis.addis.presentation.PopulationCharTableModel;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectAdverseEventsPresentation;
import org.drugis.addis.presentation.SelectEndpointPresentation;
import org.drugis.addis.presentation.SelectFromFiniteListPresentation;
import org.drugis.addis.presentation.SelectPopulationCharsPresentation;
import org.drugis.addis.presentation.StudyMeasurementTableModel;
import org.drugis.addis.presentation.StudyPresentation;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class AddStudyWizardPresentation {
	
	public abstract class OutcomeMeasurementsModel {
		abstract public TableModel getMeasurementTableModel();
	}
	
	@SuppressWarnings("serial")
	private class DrugListHolder extends AbstractListHolder<Drug> implements PropertyChangeListener, DomainListener {
		public DrugListHolder() {
			d_domain.addListener(this);
		}
		
		@Override
		public List<Drug> getValue() {
			return new ArrayList<Drug>(d_domain.getDrugs());
		}

		public void propertyChange(PropertyChangeEvent evt) {
			fireValueChange(null, getValue());
		}

		public void domainChanged(DomainEvent evt) {
			fireValueChange(null, getValue());
		}
	}
	
	@SuppressWarnings("serial")
	private abstract static class OutcomeListHolder<T extends OutcomeMeasure>
	extends AbstractListHolder<T> 
	implements PropertyChangeListener, DomainListener {
		protected Domain d_domain;
		
		public OutcomeListHolder(Domain domain) {
			domain.addListener(this);
			d_domain = domain;
		}

		public void propertyChange(PropertyChangeEvent arg0) {
			fireValueChange(null, getValue());			
		}

		public void domainChanged(DomainEvent evt) {
			fireValueChange(null, getValue());			
		}
	}
	
	@SuppressWarnings("serial")
	private static class EndpointListHolder extends OutcomeListHolder<Endpoint> {
		public EndpointListHolder(Domain domain) {
			super(domain);
		}
		
		@Override
		public List<Endpoint> getValue() {
			return new ArrayList<Endpoint>(d_domain.getEndpoints());
		}
	}

	@SuppressWarnings("serial")
	private static class AdverseEventListHolder extends OutcomeListHolder<AdverseEvent> {
		public AdverseEventListHolder(Domain domain) {
			super(domain);
		}
		
		@Override
		public List<AdverseEvent> getValue() {
			return new ArrayList<AdverseEvent>(d_domain.getAdverseEvents());
		}
	}
	
	@SuppressWarnings("serial")
	private class IndicationListHolder extends AbstractListHolder<Indication> {
		public IndicationListHolder() {
			d_domain.addListener(new DomainListener() {
				public void domainChanged(DomainEvent evt) {
					fireValueChange(null, getValue());
				}
			});
		}
		
		@Override
		public List<Indication> getValue() {
			return new ArrayList<Indication>(d_domain.getIndications());
		}
	}
	
	private Domain d_domain;
	private PresentationModelFactory d_pmf;
	private StudyPresentation d_newStudyPM;
	
	private ListHolder<Endpoint> d_endpointListHolder;
	private ListHolder<AdverseEvent> d_adverseEventListHolder;
	private ListHolder<PopulationCharacteristic> d_populationCharsListHolder;
	private SelectAdverseEventsPresentation d_adverseEventSelect;
	private SelectPopulationCharsPresentation d_populationCharSelect;
	private SelectEndpointPresentation d_endpointSelect;
	
	private Study d_origStudy = null;
	private AddisWindow d_mainWindow;
	
	public AddStudyWizardPresentation(Domain d, PresentationModelFactory pmf, AddisWindow mainWindow) {
		d_domain = d;
		d_pmf = pmf;
		d_mainWindow = mainWindow;
		d_endpointListHolder = new EndpointListHolder(d_domain);
		d_adverseEventListHolder = new AdverseEventListHolder(d_domain);
		d_populationCharsListHolder = d_domain.getPopulationCharacteristicsHolder();
		d_endpointSelect = new SelectEndpointPresentation(d_endpointListHolder, d_mainWindow);
		d_adverseEventSelect = new SelectAdverseEventsPresentation(d_adverseEventListHolder, d_mainWindow);
		d_populationCharSelect = new SelectPopulationCharsPresentation(d_populationCharsListHolder,d_mainWindow);
		clearStudies();
	}

	private void updateSelectionHolders() {
		d_endpointSelect.setSlots(getNewStudy().getStudyEndpoints());
		d_adverseEventSelect.setSlots(getNewStudy().getStudyAdverseEvents());
		d_populationCharSelect.setSlots(getNewStudy().getStudyPopulationCharacteristics());
	}
	
	public AddStudyWizardPresentation(Domain d, PresentationModelFactory pmf, AddisWindow mainWindow, Study origStudy) {
		this(d, pmf, mainWindow);
		d_origStudy = origStudy;
		setNewStudy(origStudy.clone());
	}
	
	public ValueModel getSourceModel() {
		return getCharacteristicModel(BasicStudyCharacteristic.SOURCE);
	}
	
	public ValueModel getSourceNoteModel() {
		return getCharacteristicNoteModel(BasicStudyCharacteristic.SOURCE);
	}
	
	public ValueModel getIdModel() {
		return d_newStudyPM.getModel(Study.PROPERTY_ID);
	}
	
	public ValueModel getIdNoteModel() {
		return new NoteModel(getNewStudy().getStudyIdWithNotes());
	}
	
	public ValueModel getTitleModel() {
		return new MutableCharacteristicHolder(getNewStudy(), BasicStudyCharacteristic.TITLE);
	}
	
	public Domain getDomain() {
		return d_domain;
	}

	public void importCT() throws IOException {
		if(getIdModel().getValue().toString().length() != 0) {
			String studyID = getIdModel().getValue().toString().trim().replace(" ", "%20");
			String url = "http://clinicaltrials.gov/show/"+studyID+"?displayxml=true";
			Study clinicaltrialsData = ClinicaltrialsImporter.getClinicaltrialsData(url);
			setNewStudy(clinicaltrialsData);
		}
	}
	
	public void setNewStudy(Study study) {
		d_newStudyPM = new StudyPresentation(study, d_pmf);
		updateSelectionHolders();
	}

	public ListHolder<Indication> getIndicationListModel() {
		return new IndicationListHolder();
	}
	
	public ValueModel getIndicationModel() {
		return d_newStudyPM.getModel(Study.PROPERTY_INDICATION);
	}

	public ValueModel getIndicationNoteModel() {
		return new NoteModel(getNewStudy().getIndicationWithNotes());
	}
	
	public void clearStudies() {
		d_newStudyPM = (StudyPresentation) new StudyPresentation(new Study(), d_pmf);
		getSourceModel().setValue(Source.MANUAL);

		addArms(2); // by default have 2 arm slots.
		updateSelectionHolders();
		d_endpointSelect.addSlot(); // by default have 1 endpoint slot.
	}
	
	
	public MutableCharacteristicHolder getCharacteristicModel(BasicStudyCharacteristic c) {
		return new MutableCharacteristicHolder(getNewStudy(),c);
	}

	public ValueModel getCharacteristicNoteModel(BasicStudyCharacteristic c) {
		return new NoteModel(getNewStudy().getCharacteristicWithNotes(c));
	}

	public void addArms(int numArms) {
		for(int i = 0; i < numArms; ++i){
			Arm arm = new Arm("Arm " + (i + 1), 0,new Drug("", ""), new FixedDose(0l, SIUnit.MILLIGRAMS_A_DAY));
			getNewStudy().getArms().add(arm);
		}
	}
	
	public int getNumberArms(){
		return getNewStudy().getArms().size();
	}
	
	public void removeArm(int armNum){
		getNewStudy().getArms().remove(armNum);
	}
	
	public DrugListHolder getDrugsModel(){
		return new DrugListHolder();
	}
	
	public BasicArmPresentation getArmModel(int armNumber){
		return new BasicArmPresentation(getNewStudy().getArms().get(armNumber), d_pmf);
	}
	
	public ValueModel getArmNoteModel(int idx) {
		if(getNewStudy().getArms().size() <= idx)
			return null;
		return new ArmNoteModel(getNewStudy().getArms().get(idx));
	}
	
	@SuppressWarnings("serial")
	static class ArmNoteModel extends AbstractValueModel {
		private final Arm d_arm;

		public ArmNoteModel(Arm arm) {
			d_arm = arm;
		}
		
		public String getValue() {
			return d_arm.getNotes().size() > 0 ? d_arm.getNotes().get(0).getText() : null;
		}

		public void setValue(Object newValue) {
		}
	}
	
	@SuppressWarnings("serial")
	static class NoteModel extends AbstractValueModel {
		private final ObjectWithNotes<?> d_obj;

		public NoteModel(ObjectWithNotes<?> obj) {
			d_obj = obj;
		}
		
		public String getValue() {
			return (d_obj != null && d_obj.getNotes().size() > 0) ? d_obj.getNotes().get(0).getText() : null;
		}

		public void setValue(Object newValue) {
		}
	}

	public StudyMeasurementTableModel getEndpointMeasurementTableModel() {
		d_newStudyPM.initializeDefaultMeasurements();
		return new StudyMeasurementTableModel(getNewStudy(), d_pmf, Endpoint.class);
	}
	
	
	public Study saveStudy() {
		if (getNewStudy().getArms().isEmpty()) 
			throw new IllegalStateException("No arms selected in study.");
		if (getNewStudy().getEndpoints().isEmpty()) 
			throw new IllegalStateException("No endpoints selected in study.");
		if (!isIdAvailable())
			throw new IllegalStateException("Study with this ID already exists in domain");

		if (isEditing()) {
			try {
				d_domain.deleteEntity(d_origStudy);
			} catch (DependentEntitiesException e) {
				e.printStackTrace();
			}
		}
		
		// Add the study to the domain.
		d_domain.addStudy(getNewStudy());
		
		return getNewStudy();
	}

	public boolean isIdAvailable() {
		if(getNewStudy().getStudyId() == null) return true;
		if (!d_domain.getStudies().contains(getNewStudy())) {
			return true;
		}
		
		if (isEditing()) {
			return getNewStudy().equals(d_origStudy);
		}
		
		return false;
	}
	
	Study getStudy() {
		return getNewStudy();
	}

	private Study getNewStudy() {
		return d_newStudyPM.getBean();
	}
	
	public StudyPresentation getNewStudyPM() {
		return d_newStudyPM;
	}

	private StudyMeasurementTableModel getAdverseEventMeasurementTableModel() {
		d_newStudyPM.initializeDefaultMeasurements();
		return new StudyMeasurementTableModel(getNewStudy(),d_pmf, AdverseEvent.class);
	}
	
	private PopulationCharTableModel getPopulationCharMeasurementTableModel() {
		d_newStudyPM.initializeDefaultMeasurements();
		return d_newStudyPM.getPopulationCharTableModel();
	}

	public OutcomeMeasurementsModel getAdverseEventsModel() {
		return new OutcomeMeasurementsModel() {
			public StudyMeasurementTableModel getMeasurementTableModel() {
				return getAdverseEventMeasurementTableModel();
			}
		};
	} 
	
	public OutcomeMeasurementsModel getEndpointsModel() {
		return new OutcomeMeasurementsModel() {
			public StudyMeasurementTableModel getMeasurementTableModel() {
				return getEndpointMeasurementTableModel();
			}
		};
	}
	
	public OutcomeMeasurementsModel getPopulationCharsModel() {
		return new OutcomeMeasurementsModel() {
			public TableModel getMeasurementTableModel() {
				return getPopulationCharMeasurementTableModel();
			}
		};
	} 

	public SelectFromFiniteListPresentation<AdverseEvent> getAdverseEventSelectModel() {
		return d_adverseEventSelect;
	}
	
	public SelectFromFiniteListPresentation<Endpoint> getEndpointSelectModel() {
		return d_endpointSelect;
	} 

	public SelectFromFiniteListPresentation<PopulationCharacteristic> getPopulationCharSelectModel() {
		return d_populationCharSelect;
	}

	public boolean isEditing() {
		return (d_origStudy != null);
	}

	public Study getOldStudy() {
		return d_origStudy;
	}
}