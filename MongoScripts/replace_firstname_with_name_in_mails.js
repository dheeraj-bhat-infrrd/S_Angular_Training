//Function to replace all occurences of FirstName with Name
function replaceParams(paramOrder){
    while(paramOrder.indexOf("FirstName") >= 0){
        var index = paramOrder.indexOf("FirstName");
        paramOrder[index] = "Name";
    }
    return paramOrder;
}

db.getCollection('COMPANY_SETTINGS').find({mail_content:{$exists:true,$ne:{}}}).snapshot().forEach(function(record){
    //Replace FirstName with Name for all mails
    if(record.mail_content.take_survey_mail != undefined && record.mail_content.take_survey_mail.param_order != undefined)
        record.mail_content.take_survey_mail.param_order = replaceParams(record.mail_content.take_survey_mail.param_order);
    
    if(record.mail_content.take_survey_reminder_mail != undefined && record.mail_content.take_survey_reminder_mail.param_order != undefined)
        record.mail_content.take_survey_reminder_mail.param_order = replaceParams(record.mail_content.take_survey_reminder_mail.param_order);
    
    if(record.mail_content.restart_survey_mail != undefined && record.mail_content.restart_survey_mail.param_order != undefined)
        record.mail_content.restart_survey_mail.param_order = replaceParams(record.mail_content.restart_survey_mail.param_order);
    
    if(record.mail_content.take_survey_mail != undefined && record.mail_content.take_survey_mail.param_order != undefined)
        record.mail_content.take_survey_mail.param_order = replaceParams(record.mail_content.take_survey_mail.param_order);
    
    if(record.mail_content.survey_completion_mail != undefined && record.mail_content.survey_completion_mail.param_order != undefined)
        record.mail_content.survey_completion_mail.param_order = replaceParams(record.mail_content.survey_completion_mail.param_order);
    
    if(record.mail_content.social_post_reminder_mail != undefined && record.mail_content.social_post_reminder_mail.param_order != undefined)
        record.mail_content.social_post_reminder_mail.param_order = replaceParams(record.mail_content.social_post_reminder_mail.param_order);
    
    if(record.mail_content.survey_completion_unpleasant_mail != undefined && record.mail_content.survey_completion_unpleasant_mail.param_order != undefined)
        record.mail_content.survey_completion_unpleasant_mail.param_order = replaceParams(record.mail_content.survey_completion_unpleasant_mail.param_order);
    //Save changes into DB
    db.getCollection('COMPANY_SETTINGS').save(record);
});