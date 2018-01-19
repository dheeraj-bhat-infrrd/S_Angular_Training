var companies = db.COMPANY_SETTINGS.find({});


companies.forEach(function(record) {
var contactDetailsName = record.contact_details.name;
if(!(contactDetailsName === 'Advantage Rent A Car' || contactDetailsName === 'E-Z Rent-a-Car' || contactDetailsName === 'Car Rental Company')){
var ranking_requirements = {
       "minDaysOfRegistration" : NumberInt(90),
       "minCompletedPercentage" : 40,
       "minNoOfReviews" : NumberInt(25),
       "monthOffset" : -0.5,
       "yearOffset" : NumberInt(-6)
       }
var modifiedOn = new Date().getTime();
record["ranking_requirements"] = ranking_requirements;
record["modifiedOn"] = modifiedOn;
db.COMPANY_SETTINGS.save(record);
}
});