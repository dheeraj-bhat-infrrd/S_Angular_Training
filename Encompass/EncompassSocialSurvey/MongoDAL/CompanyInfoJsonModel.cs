using MongoDB.Bson;
using System.Collections.Generic;

namespace EncompassSocialSurvey.MongoDAL
{
    public class MailIds
    {
        public string work { get; set; }
        public bool isWorkEmailVerified { get; set; }
        public bool isPersonalEmailVerified { get; set; }
    }

    public class ContactNumbers
    {
        public string work { get; set; }
    }

    public class ContactDetails
    {
        public string name { get; set; }
        public string address { get; set; }
        public string address1 { get; set; }
        public string address2 { get; set; }
        public string country { get; set; }
        public string countryCode { get; set; }
        public string zipcode { get; set; }
        public MailIds mail_ids { get; set; }
        public ContactNumbers contact_numbers { get; set; }
    }
    public class CrmInfo
    {
        public long companyId { get; set; }
        public string crm_username { get; set; }
        public string crm_password { get; set; }
        public string crm_fieldId { get; set; }
        public string url { get; set; }
        public string crm_source { get; set; }
        public bool connection_successful { get; set; }
        public int numberOfDays { get; set; }
        public string emailAddressForReport { get; set; }
    }

    public class LockSettings
    {
        public bool isLogoLocked { get; set; }
        public bool isDisplayNameLocked { get; set; }
        public bool isWebAddressLocked { get; set; }
        public bool isBlogAddressLocked { get; set; }
        public bool isWorkPhoneLocked { get; set; }
        public bool isPersonalPhoneLocked { get; set; }
        public bool isFaxPhoneLocked { get; set; }
        public bool isAboutMeLocked { get; set; }
        public bool isAddressLocked { get; set; }
    }
    public class TakeSurveyMail
    {
        public string mail_body { get; set; }
        public List<string> param_order { get; set; }
    }

    public class TakeSurveyMailCustomer
    {
        public string mail_body { get; set; }
        public List<string> param_order { get; set; }
    }

    public class TakeSurveyReminderMail
    {
        public string mail_body { get; set; }
        public List<string> param_order { get; set; }
    }

    public class MailContent
    {
        public TakeSurveyMail take_survey_mail { get; set; }
        public TakeSurveyMailCustomer take_survey_mail_customer { get; set; }
        public TakeSurveyReminderMail take_survey_reminder_mail { get; set; }
    }
    public class ProfileStage
    {
        public string profileStageKey { get; set; }
        public int order { get; set; }
        public int status { get; set; }
    }

    public class SurveySettings
    {
        public int auto_post_score { get; set; }
        public double show_survey_above_score { get; set; }
        public int survey_reminder_interval_in_days { get; set; }
        public int max_number_of_survey_reminders { get; set; }
        public bool isReminderDisabled { get; set; }
        public bool autoPostEnabled { get; set; }
    }

    public class SoicalSurveryCompanyInfo
    {
        public string _class { get; set; }
        public ObjectId _id { get; set; }
        //public ContactDetails contact_details { get; set; }
        //public string createdBy { get; set; }
        //public string createdOn { get; set; }
        public CrmInfo crm_info { get; set; }
        //public int iden { get; set; }
        //public bool isAccountDisabled { get; set; }
        //public bool isDefaultBySystem { get; set; }
        //public bool isLocationEnabled { get; set; }
        //public bool isSeoContentModified { get; set; }
        //public LockSettings lockSettings { get; set; }
        //public MailContent mail_content { get; set; }
        //public string modifiedBy { get; set; }
        //public string modifiedOn { get; set; }
        //public string profileName { get; set; }
        //public List<ProfileStage> profileStages { get; set; }
        //public string profileUrl { get; set; }
        //public int profile_completion { get; set; }
        //public SurveySettings survey_settings { get; set; }
        //public string vertical { get; set; }
    }
}
