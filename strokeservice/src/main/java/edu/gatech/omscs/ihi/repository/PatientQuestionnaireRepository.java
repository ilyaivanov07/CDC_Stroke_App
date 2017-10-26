package edu.gatech.omscs.ihi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import edu.gatech.omscs.ihi.domain.PatientQuestionnaire;
import edu.gatech.omscs.ihi.domain.id.PatientQuestionnaireId;

@Repository( value = "PatientQuestionnaireRepository" )
public interface PatientQuestionnaireRepository extends CrudRepository<PatientQuestionnaire, PatientQuestionnaireId>{

}
