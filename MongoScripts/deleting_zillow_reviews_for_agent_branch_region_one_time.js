db.getCollection("SURVEY_DETAILS").find({}).snapshot().forEach(
	function(e)	{
            	// check any reviews with source Zillow are present
                // in the collection
		if(e.source != null && e.source == "Zillow"){
                        // remove the reviews with source as Zillow
                        db.getCollection("SURVEY_DETAILS").remove( { _id : e._id });
      		}
         }
);
