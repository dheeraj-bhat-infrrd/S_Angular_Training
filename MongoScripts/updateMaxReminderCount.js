print("strat");
var i = 1;
var companies = db.COMPANY_SETTINGS.find({});


companies.forEach(function(record) {
var survey_settings = record["survey_settings"];

survey_settings.max_number_of_survey_reminders = 3;
record["survey_settings"] = survey_settings;
db.COMPANY_SETTINGS.save(record);

print(i + " updated for " + record.iden );
i++;
});

print("end");
