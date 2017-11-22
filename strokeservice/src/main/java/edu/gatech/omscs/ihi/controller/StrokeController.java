package edu.gatech.omscs.ihi.controller;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.uhn.fhir.context.FhirContext;

import ca.uhn.fhir.model.dstu2.resource.QuestionnaireResponse;
import ca.uhn.fhir.model.dstu2.resource.QuestionnaireResponse.Group;
import ca.uhn.fhir.model.dstu2.resource.QuestionnaireResponse.GroupQuestion;

import ca.uhn.fhir.model.primitive.StringDt;
import edu.gatech.omscs.ihi.domain.Patient;
import edu.gatech.omscs.ihi.domain.PatientQuestionnaire;
import edu.gatech.omscs.ihi.domain.StrokeCode;
import edu.gatech.omscs.ihi.domain.id.PatientId;
import edu.gatech.omscs.ihi.domain.id.PatientQuestionnaireId;
import edu.gatech.omscs.ihi.domain.Questionnaire;
import edu.gatech.omscs.ihi.repository.PatientQuestionnaireRepository;
import edu.gatech.omscs.ihi.repository.PatientRepository;
import edu.gatech.omscs.ihi.repository.QuestionnaireRepository;
import edu.gatech.omscs.ihi.service.FhirResourceService;
import edu.gatech.omscs.ihi.service.PatientService;
import edu.gatech.omscs.ihi.service.ServerConnectionService;
import edu.gatech.omscs.ihi.service.SurveyAdminService;
import edu.gatech.omscs.ihi.util.AnswerPayload;
import edu.gatech.omscs.ihi.util.JsonUtils;

@RestController
@RequestMapping( value = "cdc/api/stroke" )
public class StrokeController
{
	@PersistenceContext
	public EntityManager em;	
	
	@Autowired
	private ServerConnectionService serverConnectionService;
	
	@Autowired 
	private PatientRepository patientRepository;
	
	@Autowired
	private SurveyAdminService surveyAdminService;
	
	@Autowired
	private PatientService patientService;
	
	@Autowired
	private FhirResourceService fhirResource;
	
	@Autowired 
	private QuestionnaireRepository questionnaireRepository;
	
	@Autowired
	private PatientQuestionnaireRepository patientQuestionnaireRepository;
	
	
	private boolean checkCredentials( HttpServletRequest request)
	{
		String username = request.getHeader( "username" );
		String password = request.getHeader( "password" );
		
		return this.surveyAdminService.authenticate( username, password );
	}
	
	// ***********************************
	// ***********************************
	// USER AUTHENTICATION
	// ***********************************
	// ***********************************
	
	@RequestMapping( value = "authenticate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE )
	public JsonNode authenticate( HttpServletRequest request )
	{
		String value = checkCredentials( request ) ? "Success" : "Failure";
		
		return JsonUtils.convertKeyValueToJson( "AuthenticationSuccess", value );
	}
	
	// ***********************************
	@SuppressWarnings("unchecked")
	// ***********************************
	// PATIENT
	// ***********************************
	// ***********************************
	// get patients
	@RequestMapping( value = "patient", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
	public JsonNode getPatients( HttpServletRequest request )
	{
		if ( checkCredentials( request ) )
		{
			// get patients from FHIR server
			try {
				patientService.getPatientsFromFHIR();
			}
			catch(Exception ex) { 
				ex.printStackTrace(System.out);
			}
			
			int destId = this.surveyAdminService.getDestinationForSurveyAdmin( request.getHeader( "username" ) );
			String strDestId = new Integer(destId).toString();
			
			// get all available questionnaires
			List<Questionnaire> questionnaires = (List<Questionnaire>)this.questionnaireRepository.findAll();

			List<Patient> patients = (List<Patient>)this.patientRepository.findAllPatientsByDestinationId( strDestId );
			
			String queryString = "SELECT p FROM PatientQuestionnaire p WHERE p.patientQuestionnaireId.id = :id AND p.patientQuestionnaireId.mrn = :mrn AND p.patientQuestionnaireId.encounterId = :encounterId AND p.patientQuestionnaireId.destinationId = :destinationId";
			TypedQuery<PatientQuestionnaire> p = em.createQuery(queryString, PatientQuestionnaire.class);
			
			// loop through patients
			for (Patient patient: patients) {
				
				 
				Set<Questionnaire> answeredQuestionnaires = patient.getAnsweredQuestionnaires();
				
//				answeredQuestionnaires = (List<PatientQuestionnaire>)em.createNamedQuery("findPatientQuestionnaires")
//						.setParameter("mrn", patient.getPatientId().getMrn())
//						.setParameter("encounterId", patient.getPatientId().getEncounterId())
//						.setParameter("destinationId", patient.getPatientId().getDestinationId())
//						.getResultList();
				
//				System.out.println("answeredQuestionnaires.size(): " + answeredQuestionnaires.size());
//				System.out.println("answeredQuestionnaires.get(0): " + answeredQuestionnaires.get(0).getQuestionnaireResponseCsv());
				
				
				Set<edu.gatech.omscs.ihi.bean.Questionnaire> patientQuestionnaires = new HashSet<edu.gatech.omscs.ihi.bean.Questionnaire>();

				// loop through all available questionnaires
				for (Questionnaire questionnaire : questionnaires) {
					edu.gatech.omscs.ihi.bean.Questionnaire q = new edu.gatech.omscs.ihi.bean.Questionnaire();
					String id = questionnaire.getId();
					String json = questionnaire.getJson();
					q.setId(id);
					q.setAnswered(false);
					q.setIsActive(questionnaire.getIsActive());

					JsonNode node = JsonUtils.converStringToJsonNode(json);
					q.setTitle(node.get("group").get("title").asText());
					q.setDays(node.get("days").asText());
					
					if (answeredQuestionnaires != null) {
						for (Questionnaire answered: answeredQuestionnaires) {
							//System.out.println("answered questionnaire: " + answered.getPatientQuestionnaireId().getId());
							// if questionnaire was submitted
							if (answered.getId().equals(id)) {
								
								List<PatientQuestionnaire> ques = p
								.setParameter("id", id)
								.setParameter("mrn", patient.getPatientId().getMrn())
								.setParameter("encounterId", patient.getPatientId().getEncounterId())
								.setParameter("destinationId", patient.getPatientId().getDestinationId())
								.getResultList();

								q.setAnswered(true);
								q.setJson(ques.get(0).getQuestionnaireResponseJson());
								q.setCsv(ques.get(0).getQuestionnaireResponseCsv());
								//System.out.println("===DEBUG: ques.get(0).getQuestionnaireResponseCsv() " + ques.get(0).getQuestionnaireResponseCsv());
								break;
							}
						}
					}
					
					patientQuestionnaires.add(q);
				}
				patient.setQuestionnaires(patientQuestionnaires);
			}
			
			return JsonUtils.convertBeanToJsonNode( patients );
		}
		
		return null;
	}
	
	// save Patient
	@RequestMapping( value = "patient", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE )
	public void setPatient( HttpServletRequest request, @RequestBody JsonNode dischargedPatients )
	{
		Patient toPublish = ( Patient ) JsonUtils.convertJsonToBean( dischargedPatients.toString(), Patient.class );				
		this.patientRepository.save( toPublish );
	}
	
	// ***********************************
	// ***********************************
	// STROKE CODES
	// ***********************************
	// ***********************************
	
	@RequestMapping( value = "codes/icd", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
	public JsonNode getStrokeCodes( HttpServletRequest request)
	{		
		Iterable< StrokeCode > codes = surveyAdminService.getStrokeCodes();
		
		return JsonUtils.convertBeanToJsonNode( codes );	
	}
	
	// ***********************************
	// ***********************************
	// QUESTIONNAIRE
	// ***********************************
	// ***********************************
	
	// create questionnaire
	@RequestMapping( value = "questionnaire", method = RequestMethod.POST )
	public void uploadQuestionnaire( HttpServletRequest request, @RequestParam( "file" ) MultipartFile file ) {
		if ( checkCredentials( request) ) {
			try {
				String testId = file.getOriginalFilename();
				testId = file.getOriginalFilename().substring( 0, testId.lastIndexOf( "." ) );
				ByteArrayInputStream stream = new ByteArrayInputStream( file.getBytes() );
				
				String json_resource = IOUtils.toString( stream, "UTF-8" );
				
				String fhirId = "";
				
				try {
					// Save Questionnaire to FHIR
					fhirId = this.fhirResource.updateResource("Questionnaire", json_resource);
				}
				catch(Exception ex) {
					fhirId = java.util.UUID.randomUUID().toString();
					ex.printStackTrace(System.out);
				}
				
				// Save Questionnaire to localdb
				this.questionnaireRepository.save(new Questionnaire(fhirId, json_resource, true));
			}
			catch ( Exception exp ) {
				exp.printStackTrace();
			}
		}
	}

	
	// get a specific questionnaire
	@RequestMapping( value = "questionnaire", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
	public JsonNode getQuestionnaireJson( HttpServletRequest request, @RequestParam( "questionnaireId" ) String questionnaireId) {
		if ( checkCredentials( request) )
		{			
			return JsonUtils.convertBeanToJsonNode( this.questionnaireRepository.findOne(questionnaireId));
		}
		return null;
	}
	

	// update questionnaire's isActive field
	@RequestMapping( value = "updatequestionnaire/{id}/{isActive}", method = {RequestMethod.PUT, RequestMethod.GET})
	public void updateQuestionnaire(HttpServletRequest request, @PathVariable("id") String questionnaireId, @PathVariable("isActive") Boolean isActive) {
		//System.out.println("update questionnaire called with id: " + questionnaireId + " and isActive = " + isActive.toString());
		if (checkCredentials(request)) {
			Questionnaire q = this.questionnaireRepository.findOne(questionnaireId);
			q.setIsActive(isActive);
			this.questionnaireRepository.save(q);
		}
	}
	
	
	// delete questionnaire
	@RequestMapping( value = "questionnaire/{id}", method = RequestMethod.DELETE )
	public void deleteQuestionnaire( HttpServletRequest request, @PathVariable( "id" ) String questionnaireId ) {
		//System.out.println("delete questionnaire called with id: " + questionnaireId);
		if ( checkCredentials( request) ) {
			// delete from StrokeApp DB
			this.questionnaireRepository.delete( questionnaireId );

			try {
				// delete from FHIR
				this.fhirResource.deleteFhirResource("Questionnaire", questionnaireId);
			}
			catch(Exception ex) {
				ex.printStackTrace(System.out);
			}
			
		}
	}

	
	// get all questionnaires
	@RequestMapping( value = "questionnaires", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
	public JsonNode getQuestionnaireJson( HttpServletRequest request) {
		if ( checkCredentials( request) ) {			
			return JsonUtils.convertBeanToJsonNode( this.questionnaireRepository.findAll(new Sort(Sort.Direction.DESC, "id")) );
		}
		
		return null;
	}
	
	
	// ***********************************
	// ***********************************
	// QUESTIONNAIRE RESPONSE
	// ***********************************
	// ***********************************
//	@RequestMapping( value = "questionnaire-response", method = RequestMethod.GET )
//	public JsonNode getQuestionnaireResponse( HttpServletRequest request, @PathVariable( "id" ) String questionnaireId )
//	{
//		return null;
//	}
	
	
	
	@RequestMapping( value = "questionnaire-response", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE )
	public void uploadQuestionnaireResponse( HttpServletRequest request, 
			@RequestParam( "mrn" ) String mrn,
			@RequestParam( "encounterid" ) String encounterId,
			@RequestParam( "destinationid" ) String destinationId,
			@RequestParam( "questionnaireId" ) String questionnaireId,
			@RequestBody JsonNode answers)
	{
		if ( checkCredentials( request) )
		{
			try
			{				
				//System.out.println(answers.toString());
				
				Map<String,AnswerPayload> answerMap = new LinkedHashMap<String,AnswerPayload>(); 				
				
				// Serialize json to Map
				ObjectMapper mapperObj = new ObjectMapper();
				
				answerMap = mapperObj.readValue(answers.toString(),
                        new TypeReference<LinkedHashMap<String,AnswerPayload>>(){});
				
				// Set up the QuestionnaireResponse by first getting the Questionnaire 
				// FHIR ID
				Iterable<Questionnaire> surveys = this.questionnaireRepository.findAll();
				Iterator<Questionnaire> itr = surveys.iterator();
				Questionnaire survey =  itr.next();
			
				// Set the id on the FHIR QuestionnaireResponse
				QuestionnaireResponse qr = new QuestionnaireResponse();
				qr.getQuestionnaire().setReference("Questionnaire/" + survey.getId());
				qr.getGroup().setLinkId("root");
				qr.getSubject().setReference("Patient/" + mrn); 					
				
				// Iterate through the AnswerPayload and construct the QuestionnaireResponse
				ArrayList<AnswerPayload> vals = new ArrayList<AnswerPayload>(answerMap.values());
				
				Group toAdd = null;
				HashMap<String,Group> groupCache = new HashMap<String,Group>();
				StringBuffer csv = new StringBuffer();
				
				// csv header
				csv.append("Group name,LinkId,Question,Answer\n");
				
				// construct the QuestionnaireResponse, maybe generate CSV here				
				for (int i = 0 ; i < vals.size() ; i++)
				{
					AnswerPayload current = vals.get(i);
					String linkId = current.getId();
					String text = current.getText().trim();
					String field = current.getField();
					
					if (field.equals("group") && !linkId.contains("."))
					{
						toAdd = qr.getGroup().addGroup();
						toAdd.setLinkId(linkId);
						toAdd.setTitle(text);
						groupCache.put(linkId, toAdd);
						
					}
					else if (!field.equals("group"))
					{
						// First char of question represents its group
						int dot = linkId.indexOf(".");
						String groupKey = linkId.substring(0, dot);

						Group toUpdate = groupCache.get(groupKey);
						GroupQuestion question = toUpdate.addQuestion();
						question.setLinkId(linkId);
						question.setText(text);
						
						question.addAnswer().setValue(new StringDt(current.getValue()));
						csv.append("\"" + toUpdate.getTitle() +
								   "\"," + linkId + 
								   ",\"" + text + 
								   "\",\"" + 
								   current.getValue() + "\"\n");
						
					}					
				}	
								
				FhirContext ctx = this.serverConnectionService.getFhirContext();
				String encoded = ctx.newJsonParser().encodeResourceToString(qr);

				try {
				    // Push QuestionnaireResponse to FHIR
				    this.fhirResource.updateResource("QuestionnaireResponse", encoded);
				}
				catch(Exception ex) {
					ex.printStackTrace(System.out);
				}
			    
			    // save PatientQuestionnaire in DB
			    PatientQuestionnaireId patientQuestionnaireId = new PatientQuestionnaireId(mrn, encounterId, destinationId, questionnaireId);
			    PatientQuestionnaire patientQuestionnaire = new PatientQuestionnaire(patientQuestionnaireId, 
			    		questionnaireId, 
			    		encoded, 
			    		csv.toString());
			    this.patientQuestionnaireRepository.save(patientQuestionnaire);
			    
				
			}
			catch ( Exception exp )
			{
				exp.printStackTrace();
			}
		}
	}
	
	// ***********************************
	// ***********************************
	// FHIR
	// ***********************************
	// ***********************************
	
	@RequestMapping( value = "fhir", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
	public JsonNode getFhirResource( HttpServletRequest request, @PathVariable( "resourceType" ) String resourceType, @PathVariable( "resourceId" ) String resourceId )
	{
		if ( checkCredentials( request) )
		{
			try {
				this.fhirResource.getFhirResource( resourceType, resourceId );
			}
			catch(Exception ex) {
				ex.printStackTrace(System.out);
			}
		}
		
		return null;
	}
	
	@RequestMapping( value = "fhir", method = RequestMethod.POST )
	public void updateFhirResource( HttpServletRequest request, @PathVariable( "resourceType" ) String resourceType, @RequestBody JsonNode resource )
	{
		if ( checkCredentials( request) )
		{
			try {
				this.fhirResource.updateResource( resourceType, resource.toString() );
			}
			catch(Exception ex) {
				ex.printStackTrace(System.out);
			}
		}
	}
	
	@RequestMapping( value = "fhir", method = RequestMethod.DELETE )
	public void deleteFhirResource( HttpServletRequest request, @PathVariable( "resourceType" ) String resourceType, @PathVariable( "resourceId" ) String resourceId )
	{
		if ( checkCredentials( request) )
		{
			
		}
	}
	
	// ***********************************
	// ***********************************
	// ADMIN
	// ***********************************
	// ***********************************
	
	@RequestMapping( value = "admin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
	public JsonNode getAdminJson( HttpServletRequest request )
	{
		if ( checkCredentials( request) )
		{
			
		}
		
		return null;
	}
	
	@RequestMapping( value = "admin", method = RequestMethod.POST )
	public void uploadAdminJson( HttpServletRequest request, @RequestParam( "file" ) MultipartFile file )
	{
		if ( checkCredentials( request) )
		{
			
		}
	}
	
	@RequestMapping( value = "admin", method = RequestMethod.DELETE )
	public void deleteAdminJson( HttpServletRequest request, @PathVariable( "id" ) String testId )
	{
		if ( checkCredentials( request) )
		{
			
		}
	}
	
	// ***********************************
	// ***********************************
	// TEST
	// ***********************************
	// ***********************************
/*	
	@RequestMapping( value = "test", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
	public JsonNode getTestJson( HttpServletRequest request )
	{
		getQR();
		System.out.println("Called QR");
		if ( checkCredentials( request) )
		{
			return JsonUtils.convertBeanToJsonNode( this.testRepository.findAll() );
		}
		
		
		
		return null;
	}
	
	@RequestMapping( value = "test", method = RequestMethod.POST )
	public void uploadTestJson( HttpServletRequest request, @RequestParam( "file" ) MultipartFile file )
	{
		if ( checkCredentials( request) )
		{
			try
			{
				String testId = file.getOriginalFilename();
				testId = file.getOriginalFilename().substring( 0, testId.lastIndexOf( "." ) );
				
				ByteArrayInputStream stream = new ByteArrayInputStream( file.getBytes() );
				this.testRepository.save( new Questionnaire( testId, IOUtils.toString( stream, "UTF-8" ) ) );
			}
			catch ( Exception exp )
			{
				exp.printStackTrace();
			}
		}
	}
	
	@RequestMapping( value = "test/{id}", method = RequestMethod.DELETE )
	public void deleteTestJson( HttpServletRequest request, @PathVariable( "id" ) String testId )
	{
		if ( checkCredentials( request) )
		{
			this.testRepository.delete( testId );
		}
	}
	
	
	public void getQR()
	{
			System.out.println("Enter QR)");
			
			Test test =  this.testRepository.findOne("questionnaire-response");
			JsonNode newnode= JsonUtils.convertBeanToJsonNode( test.getJson() );
			System.out.println(newnode.toString());
			QuestionnaireResponse qr = FhirContext.forDstu2().newJsonParser().parseResource( QuestionnaireResponse.class, newnode.toString());
			System.out.println("Created QR, calling convert");
			
			convertJsontoCSV(qr);
	}
	
	public void convertJsontoCSV(QuestionnaireResponse response) 
	{
		
		//response.getGroup().isEmpty();
		System.out.println("entered convert");
		
		Iterator<?> it = (Iterator<?>) response.getGroup();

		while(it.hasNext())
		{
			Group group = (Group) it;
			if (group.isEmpty())
			{
				 String groupTitle = group.getTitle();
				 System.out.println("Getting group.questions");
				 process_questions(group.getQuestion());
				
				
			}
			
			
		}
	}

	public void process_questions(List<GroupQuestion> questions)
	{
		System.out.println("entered questions");
				Iterator<?> it = (Iterator<?>) questions;
				
				while(it.hasNext())
				{
					GroupQuestion question = (GroupQuestion) it.next();
					String answers = question.getText();
					System.out.println(answers);
					
				}
				
	}
	*/
		
}

