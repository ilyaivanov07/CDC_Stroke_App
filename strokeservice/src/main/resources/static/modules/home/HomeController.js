'use strict';
 
angular.module('Home')

.controller('HomeController', ['$scope','$http','$rootScope','$location','$cookies', function ($scope, $http, $rootScope, $location, $cookies) 
{
	$scope.orderByField = 'days';
	$scope.reverseSort = true;
	$scope.showing = false;
	$scope.loading = true;
	
	$scope.init = function()
	{
		if ( !($cookies.get( 'username' ) === undefined ) )
		{
			if ($cookies.get( 'username' ) === '')
			{
				$location.path('/login');
				return;
			}
			else if ($cookies.get( 'username' ) === 'admin')
			{
				$location.path('/admin');
				return;
			}
		}
		
		$scope.dataLoading = true;		
		
		$http({
			method: 'GET',
			url : '/cdc/api/stroke/patient',
			headers : 
			{
				'username' : $cookies.get( 'username' ), 
				'password' : $cookies.get( 'password' )
			}
		})
		.success(function (response) {	
			angular.forEach( response, function( patient, key ) 
			{
				processDaysSinceDischarge(patient);
				setQRavailable(patient);
				
				
				
			});
			
			$scope.patients = response;
		})
		.error(function(data, status) {
			alert('ERROR: ' + status);
		})		
		.finally(function() {
			$scope.loading = false;
		});
	};
	
	var setQRavailable =  function (patient){
		if ((patient.questionnaireResponseId == null || patient.questionnaireResponseId.length < 1) && patient.days > 30){
			patient.displayQuestionnaire = '';
		}
	}
	
	
	$scope.viewQuestionnaire = function(patient, questionnaire)
	{
		$rootScope.selectedPatient = patient;
		$rootScope.questionnaire = questionnaire;
		$location.path('/questionnaire');
	};
	

	$scope.viewQuestionnaireResponse =  function(patient, json)
	{
		patient.qresponse = json;
	}
	
	
	$scope.downloadQuestionnaireResponseJSON =  function(patient, json)
	{
		var dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(json));
		var dlAnchorElem = document.getElementById('downloadAnchorElem');
		dlAnchorElem.setAttribute("href",     dataStr     );
		dlAnchorElem.setAttribute("download", patient.firstName + "_" + patient.lastName +  ".json");
		dlAnchorElem.click();
	}
	
	$scope.downloadQuestionnaireResponseCSV =  function(patient, csv)
	{		
		var anchor = angular.element('<a/>');
		anchor.css({display: 'none'}); // Make sure it's not visible
		angular.element(document.body).append(anchor); // Attach to document

		anchor.attr({
		    href: 'data:attachment/csv;charset=utf-8,' + encodeURI(csv),
		    target: '_blank',
		    download: patient.firstName + '_' + patient.lastName + '.csv'
		})[0].click();

		anchor.remove();		
	}
	
	var processDaysSinceDischarge =  function( patient )
	{
		var dis_date = new Date( patient.dischargeDate );
		var today = new Date();
		var timeDiff = Math.abs( today.getTime() - dis_date.getTime() );
		var diffDays = parseInt(Math.ceil( timeDiff / ( 1000 * 3600 * 24 ) ),10) + 1;
		patient.days = diffDays;
	};


    $scope.questionChecker = function (patient, questionaires, questionaire, days) {
        var questionLength =[0] ;

        var i;
        // for (i = 0; i < questionaires.length; i++) {
        	// console.log("questionaire length: " + questionaires[i].day);
        	// questionLength.push(questionaires[i].day);
        // }

        angular.forEach(questionaires, function (question,val) {
            questionLength.push(question.days);
        });
        questionLength.sort(function(a, b) {
            return a - b;
        });
        var close = closest(questionaire,questionLength);

        // console.log("patient is " + patient.firstName + " closest is " + close + ", questionnaire.days is " + days + " question length is " + questionLength.length);

        return (close == days);

    };
    
    var closest = function  (num, arr) {
        var mid;
        var lo = 0;
        var hi = arr.length - 1;
        while (hi - lo > 1) {
            mid = Math.ceil ((lo + hi) / 2);
            if (arr[mid] < num) {
                lo = mid;
            } else {
                hi = mid;
            }
        }
        if (num - arr[lo] <= arr[hi] - num) {
            return arr[lo];
        }
        return arr[hi];
    };




}]);