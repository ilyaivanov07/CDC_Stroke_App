## CDC POST DISCHARGE STROKE PATIENT DATA REPORTING APP - PHASE 2

To run the app in the VM:  
1.   open terminal
2.   ./start-strokeservice.sh

In the browser, navigate to  http://localhost:8888/  
user: admin  
pwd: password  
non-admin user: nurse_jane  
pwd: password

SMART on FHIR sandboxed server runs on http://localhost:9080  

++++++++++++++++++++++++++++++++++++++++++++++++++++++++++  
'Error connecting to the server: FATAL:  Peer authentication failed for user "postgres"' can be fixed by:
1. open the file pg_hba.conf in /etc/postgresql/9.x/main
2. change this line: local   all postgres  **trust**
3. sudo service postgresql restart 

+++++++++++++++++++++++++++++++++++++++++++++++++++++++++  
docker run --net host -p 8888:8007 -t strokeapp
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++

### Open Questions   

+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
### Notes/Observations #

New functions:
1. The app now has ability to host multiple questionnaires.  
2. The list of patients is pulled synchronously from the sandboxed FHIR server. The TriggerService is not used anymore.
3. The questionnaire box with the "Submit" and "Cancel" buttons is located on the right hand side. It is sticky and does not disappear as the page scrolls up/down.

+++++++++++++++++++++++++++++++++++++++++++++++++++++++++  

	
	
