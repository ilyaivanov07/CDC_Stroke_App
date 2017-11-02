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
### Open Questions   

+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
### Notes/Observations #

+++++++++++++++++++++++++++++++++++++++++++++++++++++++++  

	
	
