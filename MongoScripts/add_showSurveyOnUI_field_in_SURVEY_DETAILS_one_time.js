db.getCollection("SURVEY_DETAILS").update({},{$set : {"showSurveyOnUI":true}},false,true)
