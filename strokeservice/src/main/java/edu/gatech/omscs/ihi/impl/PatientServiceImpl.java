package edu.gatech.omscs.ihi.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Bundle.Entry;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.IGenericClient;
import edu.gatech.omscs.ihi.domain.id.PatientId;
import edu.gatech.omscs.ihi.repository.PatientRepository;
import edu.gatech.omscs.ihi.service.PatientService;
import edu.gatech.omscs.ihi.service.ServerConnectionService;
import edu.gatech.omscs.ihi.util.JsonUtils;

@Service( value = "PatientService" )
public class PatientServiceImpl implements PatientService {

	@Autowired
	private ServerConnectionService serverConnectionService;
	
	@Autowired 
	private PatientRepository patientRepository;
	
	private ArrayList<String> _strokeCodes = new ArrayList<String>();

	@Value("${initialFetchDays}")
	private int initialFetchDays;

	
	@Override
	public void getPatientsFromFHIR() {
		// Get a client connection to the FHIR server
		IGenericClient client  = this.serverConnectionService.getConnectionClient("");

		// Get first page of finished Encounters, 'date' attribute does not mean discharge,
		// just that the date is in the period, so filter on date in processEncounter().
		Bundle results = null;
		
		
		try {
		
			results = client.search()
		      .forResource(Encounter.class)
		      .where(Encounter.STATUS.exactly().code("finished"))
		      .returnBundle(Bundle.class)
		      .execute();
		}
		catch(Exception ex) {
			System.out.println("Entry with code 'finished' not found: "  + ex.getMessage());
			return;
		}
		
		
		// Query the StrokeCodes table and save the codes locally
		JsonNode strokeCodes = serverConnectionService.getStrokeCodesFromStrokeAppServer();
		Iterator<JsonNode> itrCodes = strokeCodes.elements();
		while (itrCodes.hasNext())
		{
			JsonNode current = itrCodes.next().get("icd10StrokeCode");	    			
			_strokeCodes.add(current.asText());
		}	    		
		
		// Process each finished Encounter
		for ( Entry current : results.getEntry() )
		{
			IResource encounter = current.getResource();
			if ( encounter instanceof Encounter )
			{
				processEncounter( ( Encounter ) encounter, initialFetchDays);
			}
		}
		
		// Iterate through the remaining pages
		while ( results.getLink( Bundle.LINK_NEXT ) != null ) 
		{
			// load next page
			results = client.loadPage().next( results ).execute();
			
			// Process each finished Encounter
			for ( Entry current : results.getEntry() )
    		{
    			IResource encounter = current.getResource();
    			if ( encounter instanceof Encounter )
    			{
    				processEncounter( ( Encounter ) encounter, initialFetchDays);
    			}
    		}
		}
		
	}
	

    private void processEncounter( Encounter encounter, int days )
    {
    	Calendar cal = new GregorianCalendar();
    	cal.add( Calendar.DAY_OF_MONTH, - days );
		Date daysAgo = cal.getTime();

		//System.out.println("===DEBUG: processEncounter() ");
		
		try
		{
			// Skip any Encounters that are not inpatient visits.
//			String encounterClass = encounter.getClassElement();
//			if ( !encounterClass.equals( "inpatient" ) )
//			{
//				System.out.println("===DEBUG: !inpatient ");
//				return;
//			}
			
			// Skip if the discharge happened before the search window
			Date startPeriod = encounter.getPeriod().getStart();
			Date endPeriod = encounter.getPeriod().getEnd();
			if ( endPeriod.before( daysAgo ) )
			{
				//System.out.println("===DEBUG: endPeriod.before( daysAgo ), endPeriod: " + endPeriod.toString() + ", daysAgo: " + daysAgo.toString());
				return;
			}
			
			// check icd code
			String encounterReasonCode = encounter.getReason().get(0).getCoding().get(0).getCode();
			if (!_strokeCodes.contains(encounterReasonCode))
			{
				System.out.println("===DEBUG Skipping encounter = " + encounter.toString());
				return;
			}
		
			IdDt destinationReference  = encounter.getHospitalization().getDestination().getReference();
			IdDt patientReference = encounter.getPatient().getReference();
			
			// Get a client connection to the FHIR server
    		IGenericClient client  = this.serverConnectionService.getConnectionClient( "" );
    		
    		// Get first page of finished Encounters, 'date' attribute does not mean discharge,
    		// just that the date is in the period, so filter on date in processEncounter().
    		IBaseResource results = client.read( patientReference ); 
    		if ( results instanceof Patient )
    		{
    			Patient dischargedPatient = (Patient)results;
    			//System.out.println("Discharged patient:= " + dischargedPatient.getName().get(0).getFamilyAsSingleString());
    			//IParser parser = this.serverConnectionService.getFhirContext().newJsonParser().setPrettyPrint(true); // setPrettyPrint is optional
    			
    			String jsonEncounter = null;  //parser.encodeResourceToString(encounter);
    			
    			//System.out.println("Patients encounter: " + jsonEncounter);
    			
    			PatientId patientId = new PatientId(  dischargedPatient.getId().getIdPart(),
						encounter.getId().getIdPart(),
						destinationReference.getIdPart());

    			// if patient already exists, skip 	
    			//if (this.patientRepository.exists(patientId)) {
    				//System.out.println("===DEBUG patient exists");
    			//	return;
    			//}
    			
    			
    			edu.gatech.omscs.ihi.domain.Patient patient =  
    					new edu.gatech.omscs.ihi.domain.Patient(
    							patientId, 
    							dischargedPatient.getName().get(0).getGivenAsSingleString(), 
    							dischargedPatient.getName().get(0).getFamilyAsSingleString(), 
    							new java.sql.Date(endPeriod.getTime()),
    							new java.sql.Date(startPeriod.getTime()),
    							jsonEncounter); 
    			
    			// save patient in strokeapp DB
    			this.patientRepository.save(patient);
    			
    			//System.out.print("===DEBUG dischargedPatients: ");
    			//JsonNode patientPayload = JsonUtils.convertBeanToJsonNode(newPatient);
    			//System.out.print(JsonUtils.convertBeanToJsonString(newPatient));

    			
    			//this.serverConnectionService.pushDataToStrokeAppServer(patientPayload);				
    		}
		}
		catch ( Exception exception )
		{
			//exception.printStackTrace();
			return;
		}
    }
	
	

}
