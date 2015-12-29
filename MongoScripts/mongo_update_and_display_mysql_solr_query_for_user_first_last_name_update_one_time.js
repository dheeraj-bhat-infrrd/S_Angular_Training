var mysqlList = [];
var solrList = [];

// change this according to target solr server url and port
var serverUrl = "localhost";
var serverPort = "8983";
db.getCollection("AGENT_SETTINGS").find({}).snapshot().forEach(
	function(e)	{
            if( e.contact_details != undefined && e.contact_details.name != undefined){
                //print(e.contact_details.name + " - " + e.contact_details.firstName + " " + e.contact_details.lastName);
                var nameArr = e.contact_details.name.split(' ');
                var firstName = nameArr[0];
                var lastName = "";
                if( nameArr.length > 1){
                    for(var i = 1; i<=nameArr.length-1;i++){
                        lastName += nameArr[i] + " ";
                    }
                } 
            }
        lastName = lastName.trim();
        e.contact_details.firstName = firstName;
        e.contact_details.lastName = lastName;
        db.getCollection('SURVEY_DETAILS').save(e);
            
        // mysql update query
        var sqlQueryPrefix = "UPDATE ss_user.USERS SET FIRST_NAME = '";
        var sqlQueryColumn = "', LAST_NAME = '";
        var sqlQuerySuffix = "' where USER_ID = ";
        mysqlList.push( sqlQueryPrefix + firstName + sqlQueryColumn + lastName + sqlQuerySuffix + e.iden + ";");
        
        // solr update query
        var solrQueryPrefix = "curl http://"+serverUrl+":"+serverPort+"/solr/ss-users/update -H 'Content-type:application/json' -d '[{\"userId\": ";
        var solrQueryColumn1 = ", \"firstName\":{\"set\":\"";
        var solrQueryColumn2 = "\"}, \"lastName\":{\"set\":\"";
        var solrQuerySuffix = "\"}}]'";
        solrList.push( solrQueryPrefix + e.iden + solrQueryColumn1 + firstName + solrQueryColumn2 + lastName + solrQuerySuffix );
    }
);
print("=============================MySQL update queries=====================================");
mysqlList.forEach(
  function(e){
        print(e);
  }
);
print("=============================SOLR update queries=====================================");
solrList.forEach(
  function(e){
        print(e);
  }
);
print("curl http://"+serverUrl+":"+serverPort+"/solr/ss-users/update?commit=true");
