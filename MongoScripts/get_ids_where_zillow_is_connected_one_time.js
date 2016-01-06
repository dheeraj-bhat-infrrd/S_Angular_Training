var collections = ["AGENT_SETTINGS", "BRANCH_SETTINGS", "REGION_SETTINGS", "COMPANY_SETTINGS"];

collections.forEach(function(collection) {
	var columnName;
	var tableName;
	switch(collection){
		case "AGENT_SETTINGS":
			columnName = "USER_ID";
			tableName = "USERS";
			break;
		
		case "REGION_SETTINGS":
			columnName = "REGION_ID";
			tableName = "REGION";
			break;
		
		case "BRANCH_SETTINGS":
			columnName = "BRANCH_ID";
			tableName = "BRANCH";
			break;
		
		case "COMPANY_SETTINGS":
			columnName = "COMPANY_ID";
			tableName = "COMPANY";
			break;
	}
	db.getCollection(collection).find({"socialMediaTokens.zillowToken" : {$exists : true}}).snapshot().forEach(
		function(e)	{
			var set_statement = "UPDATE ss_user." + tableName + " SET IS_ZILLOW_CONNECTED=1 ";
			var where_statement = " WHERE " + columnName + "=";
			var iden = e.iden;
			where_statement	+= iden + ";";
			if(e.zillowReviewCount != undefined)
				set_statement += ", ZILLOW_REVIEW_COUNT=" + e.zillowReviewCount;
			if(e.zillowReviewAverage != undefined)
			set_statement += ", ZILLOW_AVERAGE_SCORE=" + e.zillowReviewAverage;
			
			print(set_statement + where_statement);
		}
	); 
});

