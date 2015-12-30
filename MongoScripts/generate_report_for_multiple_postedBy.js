function contains(a, obj) {
    var i = a.length;
    while (i--) {
       if (a[i] === obj) {
           return true;
       }
    }
    return false;
}
var sources = ["facebook", "twitter", "google"];

//For each social media
sources.forEach(function(source){

    var agentIds = [];
    var branchIds = [];
    var regionIds = [];
    var companyIds = [];
    
    //Get all ids which have facebook posts
    //For agents
    db.getCollection('SOCIAL_POST').find({agentId:{$exists:true},agentId:{$ne:-1},source:source},{agentId:1, postedBy:1, _id:0}).snapshot().forEach(
        function(e){
        
        	var agentId = e.agentId;
		    if(typeof agentId === 'object'){
		            agentId = agentId.toNumber()
		    }
		    if(!(contains(agentIds, agentId)))
		            agentIds.push(agentId);
        }
    );
    
    //For branches
    db.getCollection('SOCIAL_POST').find({branchId:{$exists:true},branchId:{$ne:-1}, agentId:-1,source:source},{branchId:1, postedBy:1, _id:0}).snapshot().forEach(
        function(e){
            var branchId = e.branchId;
		    if(typeof branchId === 'object'){
		            branchId = branchId.toNumber()
		    }
		    if(!(contains(branchIds, branchId)))
		            branchIds.push(branchId);
        }
    );
    
    //For regions
    db.getCollection('SOCIAL_POST').find({regionId:{$exists:true},regionId:{$ne:-1}, branchId:-1, agentId:-1, source:source},{regionId:1, postedBy:1, _id:0}).snapshot().forEach(
        function(e){
            var regionId = e.regionId;
		    if(typeof regionId === 'object'){
		            regionId = regionId.toNumber()
		    }
		    if(!(contains(regionIds, regionId)))
		            regionIds.push(regionId);
        }
    );
    
    //For companies
    db.getCollection('SOCIAL_POST').find({companyId:{$exists:true}, companyId:{$ne:-1}, regionId: -1, branchId:-1, agentId:-1, source:source},{companyId:1, postedBy:1, _id:0}).snapshot().forEach(
        function(e){
            var companyId = e.companyId;
		    if(typeof companyId === 'object'){
		            companyId = companyId.toNumber()
		    }
		    if(!(contains(companyIds, companyId)))
		            companyIds.push(companyId);
        }
    );

	//For each id, check if multiple postedBy exist for each source
	
	//For agent
    agentIds.forEach(function(e){
            var postedBy = [];
                    db.getCollection('SOCIAL_POST').find({agentId:e,source:source},{postedBy:1, _id:0}).snapshot().forEach(
                            function(post){
                                    if(!(contains(postedBy, post.postedBy)))
                                            postedBy.push(post.postedBy);
                            });
            //Add record to report if multiple postedBy values exist
            if(postedBy.length > 1){
            
            	var strToPrint = "agentId," + e + "," + source;
            	//Get first and last modified time
            	var postedBys = postedBy.toString().split(/,/g);
            	postedBys.forEach(function(postOwner){
            		strToPrint +="," + postOwner + ",";
		        	var modifiedTimes = db.getCollection('SOCIAL_POST').aggregate(
						[
							{ 
								$match : {$and : [ { agentId : e }, { source:source }, {postedBy : postOwner} ] }
							},
							{
								$group:
									{
										_id : "$agentId",
										firstModifiedTime : { $min : "$timeInMillis" },
										lastModifiedTime : {$max : "$timeInMillis" }
									}
							 }
						]
					);
					modifiedTimes = modifiedTimes.toArray();
					if(modifiedTimes != undefined && modifiedTimes[0] != undefined){
						var firstModifiedTime = modifiedTimes[0].firstModifiedTime.toNumber();
						var lastModifiedTime = modifiedTimes[0].lastModifiedTime.toNumber();
						strToPrint += new Date(firstModifiedTime) + "," + new Date(lastModifiedTime);
					} else {
						strToPrint += "something went wrong";
                    }
				});
				
                print(strToPrint);
            }
    });
	
	//For branch
    branchIds.forEach(function(e){
            var postedBy = [];
                    db.getCollection('SOCIAL_POST').find({branchId:e,source:source},{postedBy:1, _id:0}).snapshot().forEach(
                            function(post){
                                    if(!(contains(postedBy, post.postedBy)))
                                            postedBy.push(post.postedBy);
                            });
            //Add record to report if multiple postedBy values exist
            if(postedBy.length > 1){
            	var strToPrint = "branchId," + e + "," + source;
                var postedBys = postedBy.toString().split(/,/g);
            	postedBys.forEach(function(postOwner){
            		strToPrint +="," + postOwner + ",";
		        	var modifiedTimes = db.SOCIAL_POST.aggregate(
						[
							{ 
								$match : {$and : [ { branchId : e }, { source:source }, {postedBy : postOwner} ] }
							},
							{
								$group:
									{
										_id : "$branchId",
										firstModifiedTime : { $min : "$timeInMillis" },
										lastModifiedTime : {$max : "$timeInMillis" }
									}
							 }
						]
					);
					
					modifiedTimes = modifiedTimes.toArray();
					if(modifiedTimes != undefined && modifiedTimes[0] != undefined){
						var firstModifiedTime = modifiedTimes[0].firstModifiedTime.toNumber();
						var lastModifiedTime = modifiedTimes[0].lastModifiedTime.toNumber();
						strToPrint += new Date(firstModifiedTime) + "," + new Date(lastModifiedTime);
					} else {
						strToPrint += "something went wrong";
                    }
				});
				
                print(strToPrint);
            }
    });
	
	//For region
    regionIds.forEach(function(e){
            var postedBy = [];
                    db.getCollection('SOCIAL_POST').find({regionId:e,source:source},{postedBy:1, _id:0}).snapshot().forEach(
                            function(post){
                                    if(!(contains(postedBy, post.postedBy)))
                                            postedBy.push(post.postedBy);
                            });
            //Add record to report if multiple postedBy values exist
            if(postedBy.length > 1){
                var strToPrint = "regionId," + e + "," + source;
                var postedBys = postedBy.toString().split(/,/g);
            	postedBys.forEach(function(postOwner){
            		strToPrint +="," + postOwner + ",";
		        	var modifiedTimes = db.SOCIAL_POST.aggregate(
						[
							{ 
								$match : {$and : [ { regionId : e }, { source:source }, {postedBy : postOwner} ] }
							},
							{
								$group:
									{
										_id : "$regionId",
										firstModifiedTime : { $min : "$timeInMillis" },
										lastModifiedTime : {$max : "$timeInMillis" }
									}
							 }
						]
					);
					
					modifiedTimes = modifiedTimes.toArray();
					if(modifiedTimes != undefined && modifiedTimes[0] != undefined){
						var firstModifiedTime = modifiedTimes[0].firstModifiedTime.toNumber();
						var lastModifiedTime = modifiedTimes[0].lastModifiedTime.toNumber();
						strToPrint += new Date(firstModifiedTime) + "," + new Date(lastModifiedTime);
					} else {
						strToPrint += "something went wrong";
                    }
				});
				
                print(strToPrint);
            }
    });
	
	//For company
    companyIds.forEach(function(e){
            var postedBy = [];
                    db.getCollection('SOCIAL_POST').find({companyId:e,regionId:-1,branchId:-1,agentId:-1,source:source},{postedBy:1, _id:0}).snapshot().forEach(
                            function(post){
                                    if(!(contains(postedBy, post.postedBy)))
                                            postedBy.push(post.postedBy);
                            });
            //Add record to report if multiple postedBy values exist
            if(postedBy.length > 1){
                var strToPrint = "companyId," + e + "," + source;
                var postedBys = postedBy.toString().split(/,/g);
            	postedBys.forEach(function(postOwner){
            		strToPrint +="," + postOwner + ",";
		        	var modifiedTimes = db.SOCIAL_POST.aggregate(
						[
							{ 
								$match : {$and : [ { companyId : e }, { source:source }, {postedBy : postOwner} ] }
							},
							{
								$group:
									{
										_id : "$companyId",
										firstModifiedTime : { $min : "$timeInMillis" },
										lastModifiedTime : {$max : "$timeInMillis" }
									}
							 }
						]
					);
					
					modifiedTimes = modifiedTimes.toArray();
					if(modifiedTimes != undefined && modifiedTimes[0] != undefined){
						var firstModifiedTime = modifiedTimes[0].firstModifiedTime.toNumber();
						var lastModifiedTime = modifiedTimes[0].lastModifiedTime.toNumber();
						strToPrint += new Date(firstModifiedTime) + "," + new Date(lastModifiedTime);
					} else {
						strToPrint += "something went wrong";
                    }
				});
				
                print(strToPrint);
            }
    });
    
    
});
