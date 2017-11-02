# CDC-Post-Discharge-Stroke-Patient-Data

## CDC POST DISCHARGE STROKE PATIENT DATA REPORTING APP - PHASE 2

++++++++++++++++++++++++++++++++++++++++++++++++++++++++  
to add synthetic patients to the SMART on FHIR server, the following script should be run:

/home/student/CDC-Post-Discharge/generated_data/addStrokePatients.sh 

to run the app in the VM:  
1.   open terminal
2.   ./start-strokeservice.sh

in the browser, navigate to  http://localhost:8888/  
user: admin  
pwd: password  
non-admin user: nurse_jane  
pwd: password

+++++++++++++++++++++++++++++++++++++++++++++++++++++++++  
### Open Questions   

+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
### Notes/Observations #

+++++++++++++++++++++++++++++++++++++++++++++++++++++++++  
SMART on FHIR server runs on   
1. http://localhost:3000/
2. http://localhost:9080

++++++++++++++++++++++++++++++++++++++++++++++++++++++++++  
'Error connecting to the server: FATAL:  Peer authentication failed for user "postgres"' can be fixed by:
1. open the file pg_hba.conf in /etc/postgresql/9.x/main
2. change this line: local   all postgres  **trust**
3. sudo service postgresql restart

	
	
