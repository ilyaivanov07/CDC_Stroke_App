package edu.gatech.omscs.ihi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import edu.gatech.omscs.ihi.domain.Questionnaire;

@Repository( value = "QuestionnaireRepository" )
public interface QuestionnaireRepository extends PagingAndSortingRepository< Questionnaire, String >
{
}
