db.getCollection('COMPANY_SETTINGS').update({"isAccountDisabled" : false},{$set:{"isIncompleteSurveyDeleteEnabled": false}},{ multi : true});
