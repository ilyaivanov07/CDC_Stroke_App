package edu.gatech.omscs.ihi.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonRawValue;

@Entity
@Table(name = "patient_questionnaire")
public class PatientQuestionnaire implements Serializable {

	private static final long serialVersionUID = 5169327870526300542L;
	
	@Id
	private String mrn;
	
	@Id
	@Column(name = "encounter_id")
	private String encounterId;
	
	@Id
	@Column(name = "destination_id")
	private String destinationId;
	
	@Id
	private String id;
	
	@Column(name = "questionnaire_response_id")
	private String questionnaireResponseId;
	
	@JsonRawValue
	@Column(name = "questionnaire_response_json", columnDefinition = "TEXT")
	private String questionnaireResponseJson;
	
	@Column(name = "questionnaire_response_csv", columnDefinition = "TEXT")
	private String questionnaireResponseCsv;
	
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
	public String getQuestionnaireResponseId() {
		return questionnaireResponseId;
	}
	public void setQuestionnaireResponseId(String questionnaireResponseId) {
		this.questionnaireResponseId = questionnaireResponseId;
	}
	public String getQuestionnaireResponseJson() {
		return questionnaireResponseJson;
	}
	public void setQuestionnaireResponseJson(String questionnaireResponseJson) {
		this.questionnaireResponseJson = questionnaireResponseJson;
	}
	public String getQuestionnaireResponseCsv() {
		return questionnaireResponseCsv;
	}
	public void setQuestionnaireResponseCsv(String questionnaireResponseCsv) {
		this.questionnaireResponseCsv = questionnaireResponseCsv;
	}
	
	
}
