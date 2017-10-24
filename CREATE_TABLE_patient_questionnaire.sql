CREATE TABLE patient_questionnaire
(
  mrn character varying(255) NOT NULL,
  encounter_id character varying(255) NOT NULL,
  destination_id character varying(255) NOT NULL,
  id character varying(255) NOT NULL,
  questionnaire_response_id character varying(255),
  questionnaire_response_json text,
  questionnaire_response_csv text,
  CONSTRAINT primary_key PRIMARY KEY (mrn, encounter_id, destination_id, id)
);
