db.getCollection('SURVEY_DETAILS').find({}).snapshot().forEach(function(e){

print("Starting"); 

if(e.source != "Zillow" && e.source != "3rd Party Review"){
	print("Not a Zillow Or 3rd Party Review");
         var isUserRankingQuestion = true;


         var surveyResponse = e.surveyResponse;
	var newSurveyResponse = [];
          if(surveyResponse != undefined && surveyResponse != null && surveyResponse.length >0){
		 for( var i in  surveyResponse){
			var surveyResponseObj = surveyResponse[i];
			print(surveyResponseObj);
			if(surveyResponseObj != undefined && surveyResponseObj.questionType != undefined ){
			if(surveyResponseObj.questionType.indexOf("range") !== -1){
				surveyResponseObj.isUserRankingQuestion = true;
			}else{
				surveyResponseObj.isUserRankingQuestion = false;
			}
			newSurveyResponse.push(surveyResponseObj);
			}

		}
             e.surveyResponse = newSurveyResponse;

	db.getCollection('SURVEY_DETAILS').save(e);	
	}
}
print("Ended"); 
	
})

