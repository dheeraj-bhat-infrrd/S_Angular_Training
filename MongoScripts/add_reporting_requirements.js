var companies = db.COMPANY_SETTINGS.find({});


companies.forEach(function(record) {
var contactDetails = record["contact_details"];

var ranking_requirements = {
       "minDaysOfRegistration" : NumberInt(90),
       "minCompletedPercentage" : 40,
       "minNoOfReviews" : NumberInt(25),
       "monthOffset" : NumberInt(3),
       "yearOffset" : NumberInt(-1)
       }

record["ranking_requirements"] = ranking_requirements;
db.COMPANY_SETTINGS.save(record);

});