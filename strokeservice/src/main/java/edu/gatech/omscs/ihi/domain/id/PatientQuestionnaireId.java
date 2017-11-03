package edu.gatech.omscs.ihi.domain.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;

@Embeddable
public class PatientQuestionnaireId implements Serializable{

	private static final long serialVersionUID = 6767889592230903808L;

	@Column(name = "mrn")
	private String mrn;
	@Column(name = "encounter_id")
	private String encounterId;
	@Column(name = "destination_id")
	private String destinationId;
	@Column(name = "id")
	private String id;
	

	public PatientQuestionnaireId() { }
	
	
	public PatientQuestionnaireId( String mrn, String encounterId, String destinationId, String id)
	{
		this.mrn = mrn;
		this.encounterId = encounterId;
		this.destinationId = destinationId;
		this.id = id;
	}


	public String getMrn() {
		return mrn;
	}


	public void setMrn(String mrn) {
		this.mrn = mrn;
	}


	public String getEncounterId() {
		return encounterId;
	}


	public void setEncounterId(String encounterId) {
		this.encounterId = encounterId;
	}


	public String getDestinationId() {
		return destinationId;
	}


	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}

	
	
}
