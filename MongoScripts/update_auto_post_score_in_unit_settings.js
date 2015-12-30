var defaultAutPpostScore = 3.500000000000000;

db.getCollection('COMPANY_SETTINGS').find({}).snapshot().forEach(function(e) {

    var survey_settings = e.survey_settings;
    if (survey_settings == undefined) {
        survey_settings = {};
    }

    //get auto post score and  minPostScore
    var auto_post_score = survey_settings.auto_post_score;
    var show_survey_above_score = survey_settings.show_survey_above_score;

    if (show_survey_above_score == undefined || show_survey_above_score == 0.0000000000000000) {
        show_survey_above_score = defaultAutPpostScore;
    }
    //update survey settings and survey above score
    survey_settings.auto_post_score = show_survey_above_score;
    survey_settings.show_survey_above_score = show_survey_above_score;

    //update survey settings
    e.survey_settings = survey_settings;
    db.getCollection('COMPANY_SETTINGS').save(e);
})

//for region
db.getCollection('REGION_SETTINGS').find({}).snapshot().forEach(function(e) {

    var survey_settings = e.survey_settings;
    if (survey_settings == undefined) {
        survey_settings = {};
    }

    //get auto post score and  minPostScore
    var auto_post_score = survey_settings.auto_post_score;
    var show_survey_above_score = survey_settings.show_survey_above_score;

    if (show_survey_above_score == undefined || show_survey_above_score == 0.0000000000000000) {
        show_survey_above_score = defaultAutPpostScore;
    }
    //update survey settings and survey above score
    survey_settings.auto_post_score = show_survey_above_score;
    survey_settings.show_survey_above_score = show_survey_above_score;

    //update survey settings
    e.survey_settings = survey_settings;
    db.getCollection('REGION_SETTINGS').save(e);
})

//for branch
db.getCollection('BRANCH_SETTINGS').find({}).snapshot().forEach(function(e) {

    var survey_settings = e.survey_settings;
    if (survey_settings == undefined) {
        survey_settings = {};
    }

    //get auto post score and  minPostScore
    var auto_post_score = survey_settings.auto_post_score;
    var show_survey_above_score = survey_settings.show_survey_above_score;

    if (show_survey_above_score == undefined || show_survey_above_score == 0.0000000000000000) {
        show_survey_above_score = defaultAutPpostScore;
    }
    //update survey settings and survey above score
    survey_settings.auto_post_score = show_survey_above_score;
    survey_settings.show_survey_above_score = show_survey_above_score;

    //update survey settings
    e.survey_settings = survey_settings;
    db.getCollection('BRANCH_SETTINGS').save(e);
})

//for agent
db.getCollection('AGENT_SETTINGS').find({}).snapshot().forEach(function(e) {

    var survey_settings = e.survey_settings;
    if (survey_settings == undefined) {
        survey_settings = {};
    }

    //get auto post score and  minPostScore
    var auto_post_score = survey_settings.auto_post_score;
    var show_survey_above_score = survey_settings.show_survey_above_score;

    if (show_survey_above_score == undefined || show_survey_above_score == 0.0000000000000000) {
        show_survey_above_score = defaultAutPpostScore;
    }
    //update survey settings and survey above score
    survey_settings.auto_post_score = show_survey_above_score;
    survey_settings.show_survey_above_score = show_survey_above_score;

    //update survey settings
    e.survey_settings = survey_settings;
    db.getCollection('AGENT_SETTINGS').save(e);
})



