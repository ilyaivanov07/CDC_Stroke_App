CREATE TABLE patient
(
  destination_id character varying(255) NOT NULL,
  encounter_id character varying(255) NOT NULL,
  mrn character varying(255) NOT NULL,
  admit_date date,
  discharge_date date,
  encounter_json text,
  first_name character varying(255),
  last_name character varying(255),
  CONSTRAINT patient_pkey PRIMARY KEY (destination_id, encounter_id, mrn)
);
