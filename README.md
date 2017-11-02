# CDC-Post-Discharge-Stroke-Patient-Data

## CDC POST DISCHARGE STROKE PATIENT DATA REPORTING APP - PHASE 2

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

++++++++++++++++++++++++++++++++++++++++++++++++++++++++  
to add patients to the SMART on FHIR server, the following scripts are executed:

/home/student/CDC-Post-Discharge/generated_data/addStrokePatients.sh 
/home/student/installer/provisioning/examples/tasks/reset-database.sh  
cd /home/student/installer/provisioning  
ansible-playbook  -c local -i 'localhost,'  -t 'reset_db,load_patients' smart-on-fhir-servers.yml 
/home/student/installer/provisioning/smart-on-fhir-servers.yml  

++++++++++++++++++++++++++++++++++++++++++++++++++++++++++  
'Error connecting to the server: FATAL:  Peer authentication failed for user "postgres"' can be fixed by:
1. open the file pg_hba.conf in /etc/postgresql/9.x/main
2. change this line: local   all postgres  **trust**
3. sudo service postgresql restart

	
	
