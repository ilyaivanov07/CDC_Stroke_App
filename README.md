# CDC-Post-Discharge-Stroke-Patient-Data
CDC POST DISCHARGE STROKE PATIENT DATA REPORTING APP - PHASE 2

to run the app in the VM:

cd ~/CDC-Post-Discharge/strokeservice && sudo mvn spring-boot:run

cd ~/CDC-Post-Discharge/triggerservice && sudo mvn spring-boot:run -DinitialFetchDays=90

in the browser, navigate to  

http://localhost:8888/

user: admin

pwd: password

sample survey is in

~/CDC-Post-Discharge\strokeservice\src\test\resources\data\strokequestionnaire.json

