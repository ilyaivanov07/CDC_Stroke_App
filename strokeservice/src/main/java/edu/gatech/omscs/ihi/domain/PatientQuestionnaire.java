package edu.gatech.omscs.ihi.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.NamedQuery;

import com.fasterxml.jackson.annotation.JsonRawValue;
import edu.gatech.omscs.ihi.domain.id.PatientQuestionnaireId;

@Entity
@Table(name = "patient_questionnaire")
@NamedQuery(
	name = "findPatientQuestionnaires",
	query = "SELECT questionnaireResponseJson, questionnaireResponseCsv "
	+ " FROM PatientQuestionnaire WHERE mrn=:mrn AND encounter_id=:encounterId AND destination_id=:destinationId")

public class PatientQuestionnaire implements Serializable {

	private static final long serialVersionUID = 5169327870526300542L;
	
	@EmbeddedId
	private PatientQuestionnaireId patientQuestionnaireId;
	
	@Column(name = "questionnaire_response_id")
	private String questionnaireResponseId;
	
	@JsonRawValue
	@Column(name = "questionnaire_response_json", columnDefinition = "TEXT")
	private String questionnaireResponseJson;
	
	@Column(name = "questionnaire_response_csv", columnDefinition = "TEXT")
	private String questionnaireResponseCsv;
	
	public PatientQuestionnaire() { }

	
	public PatientQuestionnaire(PatientQuestionnaireId patientQuestionnaireId, String questionnaireResponseId,
			String questionnaireResponseJson, String questionnaireResponseCsv) {
		this.patientQuestionnaireId = patientQuestionnaireId;
		this.questionnaireResponseId = questionnaireResponseId;
		this.questionnaireResponseJson = questionnaireResponseJson;
		this.questionnaireResponseCsv = questionnaireResponseCsv;
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
	public PatientQuestionnaireId getPatientQuestionnaireId() {
		return patientQuestionnaireId;
	}
	public void setPatientQuestionnaireId(PatientQuestionnaireId patientQuestionnaireId) {
		this.patientQuestionnaireId = patientQuestionnaireId;
	}
	
	
}
