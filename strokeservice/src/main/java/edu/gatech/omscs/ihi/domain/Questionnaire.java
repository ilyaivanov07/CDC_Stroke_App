package edu.gatech.omscs.ihi.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonRawValue;

@Entity( name = "Questionnaire" )
@Table( name = "Questionnaire" )
public class Questionnaire implements Serializable
{
	private static final long serialVersionUID = -3343602165281072984L;
	
	@Id
	private String id;

	@JsonRawValue
	@Column(columnDefinition = "TEXT")
	private String json;

	@Column(columnDefinition = "BOOLEAN")
	private Boolean isActive;


	public Questionnaire() { } // default constructor must be present
	
	public Questionnaire(String id, String json, Boolean isActive) {
		this.id = id;
		this.json = json;
		this.isActive = isActive;
	}
	
	public String getId() 
	{
		return this.id;
	}

	public void setId( String id ) 
	{
		this.id = id;
	}
	
	public String getJson() 
	{
		return this.json;
	}
	
	public void setJson( String json ) 
	{
		this.json = json;
	}
	
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	public Boolean getIsActive() {
		return isActive;
	}
	
	
}
