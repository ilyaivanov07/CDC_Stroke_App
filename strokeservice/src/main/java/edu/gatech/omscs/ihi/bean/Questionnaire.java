package edu.gatech.omscs.ihi.bean;

public class Questionnaire implements java.io.Serializable {

	private static final long serialVersionUID = 4273123751814474083L;

	private String id;
	private String json;
	private String csv;
	private Boolean answered;
	private String title;
	private String days;
	private Boolean isActive;
	
	public String getCsv() {
		return csv;
	}
	public void setCsv(String csv) {
		this.csv = csv;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public Boolean getAnswered() {
		return answered;
	}
	public void setAnswered(Boolean answered) {
		this.answered = answered;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDays() {
		return days;
	}
	public void setDays(String days) {
		this.days = days;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

}
