// change this according to target solr server url and port
var serverUrl = "localhost";
var serverPort = "8983";
db.getCollection("REGION_SETTINGS").find({}).snapshot().forEach(
    function(e)	{
            if( e.contact_details != undefined && e.contact_details.name != undefined){
            var solrQueryPrefix = "curl http://"+serverUrl+":"+serverPort+"/solr/ss-regions/update -H 'Content-type:application/json' -d '[{\"regionId\":";
            var solrQueryColumnSet = ", \"regionName\":{\"set\":\"";
            var solrQuerySuffix = "\"}}]\'";
            print(solrQueryPrefix+e.iden+solrQueryColumnSet+e.contact_details.name+solrQuerySuffix);
        }
    }
);
print("curl http://"+serverUrl+":"+serverPort+"/solr/ss-regions/update?commit=true");
