var companies = db.COMPANY_SETTINGS.find({"contact_details.countryCode":{$exists : true}, "contact_details.contact_numbers":{$exists : true}});
companies.forEach(function(record) {
 var contactDetails = record["contact_details"];
 var contactNumbers = contactDetails["contact_numbers"];
 var phone1 = {"countryAbbr" : contactDetails["countryCode"].toLowerCase()};
 contactNumbers["phone1"] = phone1;
 contactDetails["contact_numbers"] = contactNumbers;
 record["contact_details"] = contactDetails;
 db.COMPANY_SETTINGS.save(record);
});

var branches = db.BRANCH_SETTINGS.find({"contact_details.countryCode":{$exists : true}, "contact_details.contact_numbers":{$exists : true}, "contact_details.name":{$ne:"Default Branch"}});
branches.forEach(function(record) {
 var contactDetails = record["contact_details"];
 var contactNumbers = contactDetails["contact_numbers"];
 var phone1 = {"countryAbbr" : contactDetails["countryCode"].toLowerCase()};
 contactNumbers["phone1"] = phone1;
 contactDetails["contact_numbers"] = contactNumbers;
 record["contact_details"] = contactDetails;
 db.BRANCH_SETTINGS.save(record);
});

var regions = db.REGION_SETTINGS.find({"contact_details.countryCode":{$exists : true}, "contact_details.contact_numbers":{$exists : true}, "contact_details.name":{$ne:"Default Region"}});
regions.forEach(function(record) {
 var contactDetails = record["contact_details"];
 var contactNumbers = contactDetails["contact_numbers"];
 var phone1 = {"countryAbbr" : contactDetails["countryCode"].toLowerCase()};
 contactNumbers["phone1"] = phone1;
 contactDetails["contact_numbers"] = contactNumbers;
 record["contact_details"] = contactDetails;
 db.REGION_SETTINGS.save(record);
});

var agents = db.AGENT_SETTINGS.find({"contact_details.countryCode":{$exists : true}, "contact_details.contact_numbers":{$exists : true}});
agents.forEach(function(record) {
 var contactDetails = record["contact_details"];
 var contactNumbers = contactDetails["contact_numbers"];
 var phone1 = {"countryAbbr" : contactDetails["countryCode"].toLowerCase()};
 contactNumbers["phone1"] = phone1;
 contactDetails["contact_numbers"] = contactNumbers;
 record["contact_details"] = contactDetails;
 db.AGENT_SETTINGS.save(record);
});

