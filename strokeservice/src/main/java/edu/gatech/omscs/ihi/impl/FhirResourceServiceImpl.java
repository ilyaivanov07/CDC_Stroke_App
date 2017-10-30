package edu.gatech.omscs.ihi.impl;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Questionnaire;
import ca.uhn.fhir.model.dstu2.resource.QuestionnaireResponse;
import ca.uhn.fhir.rest.api.MethodOutcome;

import com.fasterxml.jackson.databind.JsonNode;

import edu.gatech.omscs.ihi.service.FhirResourceService;
import edu.gatech.omscs.ihi.service.ServerConnectionService;

@Service( value = "FhirResourceService" )
public class FhirResourceServiceImpl implements FhirResourceService 
{
	@Autowired
	private ServerConnectionService serverConnectionService;

	@Value( "${strokeService.baseUrl}" )
	private String strokeServiceUrl;
	
	@Override
	public JsonNode getFhirResource( String resourceType, String resourceId ) 
	{
		return null;
	}

	@Override
	public String updateResource( String resourceType, String resource ) 
	{
		IBaseResource baseResource = null;
		FhirContext ctx = serverConnectionService.getFhirContext(); 
		
		switch ( resourceType )
		{
			case "Questionnaire": 
				
				baseResource = ctx.newJsonParser().parseResource( Questionnaire.class, resource );
				
				break;
			
			case "QuestionnaireResponse": 
				
				baseResource = ctx.newJsonParser().parseResource( QuestionnaireResponse.class, resource );
				
				break;
		}
		
		if ( baseResource != null )
		{
			MethodOutcome outcome = this.serverConnectionService.getConnectionClient("")
					.create()
					.resource( baseResource )
					.prettyPrint()
					.encodedJson()
					.execute();
			
			return outcome.getId().getIdPart();
		}
		
		return null;
	}
	
	@Override
	public void deleteFhirResource( String resourceType, String resourceId ) 
	{
		
	}
	
	
	public JsonNode getStrokeCodesFromStrokeAppServer()
	{
		JsonNode values = null;
		try
		{	
			RestTemplate restTemplate = new RestTemplate();
			values = restTemplate.getForObject(strokeServiceUrl + "/codes/icd", JsonNode.class);
			
		}
		catch ( Exception exception )
		{
			exception.printStackTrace();
		}
		
		return values;
	}
	
	
}