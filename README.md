## CDC POST DISCHARGE STROKE PATIENT DATA REPORTING APP - PHASE 2

To run the app using Docker:
1. open terminal
2. navigate to CDC-Post-Discharge-Stroke-Patient-Data
3. $ sudo docker-compose up

In the browser, navigate to  http://localhost:8888/  

user: admin  
pwd: password  

non-admin user: nurse_jane  
pwd: password  

NOTE: We use external FHIR server at http://fhirtest.uhn.ca/  


New functions:
1. The app now has ability to host multiple questionnaires.  
2. The list of patients is pulled synchronously from the sandboxed FHIR server. The TriggerService is not used anymore.
3. The questionnaire box with the "Submit" and "Cancel" buttons is located on the right hand side. It is sticky and does not disappear as the page scrolls up/down.
4. A survey can be made active/inactive
5. If multiple surveys are available, only one survey that is most appropriate is displayed.


	
	
