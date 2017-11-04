package edu.gatech.omscs.ihi.domain;

import java.io.Serializable;
import java.sql.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NamedQuery;

import javax.persistence.JoinColumn;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRawValue;

import edu.gatech.omscs.ihi.domain.id.PatientId;

@Entity( name = "Patient" )
@Table( name = "Patient" )
public class Patient implements Serializable
{
	private static final long serialVersionUID = -1693238231132711914L;

	@EmbeddedId
	private PatientId patientId;
	
	private String firstName;
	private String lastName;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="EST")
	private Date admitDate;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="EST")
	private Date dischargeDate;
	
	@JsonRawValue
	@Column( columnDefinition = "TEXT" )
	private String encounterJson;
	
	@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="patient_questionnaire",
    joinColumns= {
				@JoinColumn(name="destination_id"),
				@JoinColumn(name="encounter_id"), 
				@JoinColumn(name="mrn"),
    			},
    inverseJoinColumns={@JoinColumn(name="id")})
	private Set<Questionnaire> answeredQuestionnaires;

	
	
	@Transient
	private Set<edu.gatech.omscs.ihi.bean.Questionnaire> questionnaires;
	
	
	public Patient() {}

	public Patient(PatientId patientId) {
		this.patientId = patientId;
	}
	
	
	public Patient( PatientId patientId, 
			String firstName, 
			String lastName, 
			Date dischargeDate, 
			Date admitDate, 
			String encounterJson) {
		this.patientId = patientId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dischargeDate = dischargeDate;
		this.admitDate = admitDate;
		this.encounterJson = encounterJson;		
	}
	
	
	public PatientId getPatientId() 
	{
		return this.patientId;
	}

	public void setPatientId( PatientId patientId ) 
	{
		this.patientId = patientId;
	}

	public String getEncounterJson() 
	{
		return this.encounterJson;
	}

	public void setEncounterJson( String encounterJson ) 
	{
		this.encounterJson = encounterJson;
	}

	public String getFirstName() 
	{
		return this.firstName;
	}

	public void setFirstName( String firstName ) 
	{
		this.firstName = firstName;
	}

	public String getLastName() 
	{
		return this.lastName;
	}
	
	public void setLastName( String lastName )
	{
		this.lastName = lastName;
	}
	
	public Date getAdmitDate() 
	{
		return this.admitDate;
	}
	
	public void setAdmitDate( Date admitDate ) 
	{
		this.admitDate = admitDate;
	}
	
	public Date getDischargeDate() 
	{
		return this.dischargeDate;
	}
	
	public void setDischargeDate( Date dischargeDate ) 
	{
		this.dischargeDate = dischargeDate;
	}

	
	public Set<Questionnaire> getAnsweredQuestionnaires() {
		return answeredQuestionnaires;
	}

	
	public Set<edu.gatech.omscs.ihi.bean.Questionnaire> getQuestionnaires() {
		return questionnaires;
	}

	public void setQuestionnaires(Set<edu.gatech.omscs.ihi.bean.Questionnaire> questionnaires) {
		this.questionnaires = questionnaires;
	}

}