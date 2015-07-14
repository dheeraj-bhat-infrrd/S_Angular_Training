using EncompassSocialSurvey.Entity;
using MySql.Data.MySqlClient;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EncompassSocialSurvey.DAL
{
    public class LoanRepository
    {
        EncompassSocialSurveryContext _socialSurveryContext = new EncompassSocialSurveryContext();


        // select spi.SURVEY_SOURCE_ID, spi.CUSTOMER_EMAIL_ID, spi.CUSTOMER_FIRST_NAME from survey_pre_initiation as spi
        private const string SELECT_QUERY = @"SELECT spi.SURVEY_PRE_INITIATION_ID, spi.SURVEY_SOURCE_ID FROM SURVEY_PRE_INITIATION as  spi
                                        WHERE spi.SURVEY_SOURCE_ID = ?SURVEY_SOURCE_ID AND spi.CUSTOMER_EMAIL_ID = ?CUSTOMER_EMAIL_ID AND spi.CUSTOMER_FIRST_NAME = ?CUSTOMER_FIRST_NAME ;";


        private const string INSERT_QUERY = @"INSERT INTO SURVEY_PRE_INITIATION(  
                                          SURVEY_SOURCE
                                        , SURVEY_SOURCE_ID
                                        , COMPANY_ID
                                        , AGENT_ID
                                        , AGENT_NAME
                                        , AGENT_EMAILID
                                        , CUSTOMER_FIRST_NAME
                                        , CUSTOMER_LAST_NAME
                                        , CUSTOMER_EMAIL_ID
                                        , CUSTOMER_INTERACTION_DETAILS
                                        , ENGAGEMENT_CLOSED_TIME
                                        , REMINDER_COUNTS
                                        , LAST_REMINDER_TIME
                                        , STATUS
                                        , CREATED_ON
                                        , MODIFIED_ON
                                          )
                                        VALUES(
                                          ?SURVEY_SOURCE
                                        , ?SURVEY_SOURCE_ID
                                        , ?COMPANY_ID
                                        , ?AGENT_ID
                                        , ?AGENT_NAME
                                        , ?AGENT_EMAILID
                                        , ?CUSTOMER_FIRST_NAME
                                        , ?CUSTOMER_LAST_NAME
                                        , ?CUSTOMER_EMAIL_ID
                                        , ?CUSTOMER_INTERACTION_DETAILS
                                        , ?ENGAGEMENT_CLOSED_TIME
                                        , ?REMINDER_COUNTS
                                        , ?LAST_REMINDER_TIME
                                        , ?STATUS
                                        , ?CREATED_ON
                                        , ?MODIFIED_ON
                                        ) ;";

        public bool InsertLoan(LoanEntity loan, MySqlConnection mySqlDbConnection, string emailDomain, string emailPrefix)
        {
            Logger.Info("Entering the method LoanRepository.InsertLoan(): LoanId:" + loan.SurveySourceId + " : CustomerEmailId : " + loan.CustomerEmailId);
            bool returnValue = false;

            string insertQuery = INSERT_QUERY;
            MySqlCommand commandToInsert = null;

            try
            {
                // if loand not present then and only then insert
                if (false == IsSurveySourceIdExists(loan, mySqlDbConnection))
                {
                    //
                    commandToInsert = new MySqlCommand(insertQuery, mySqlDbConnection);

                    // set the parameters
                    commandToInsert.Parameters.Add("?SURVEY_SOURCE", MySqlDbType.VarChar, 100).Value = loan.SurveySource;
                    commandToInsert.Parameters.Add("?SURVEY_SOURCE_ID", MySqlDbType.VarChar, 250).Value = loan.SurveySourceId;
                    commandToInsert.Parameters.Add("?COMPANY_ID", MySqlDbType.Int32).Value = loan.CompanyId;
                    commandToInsert.Parameters.Add("?AGENT_ID", MySqlDbType.VarChar, 36).Value = "0";
                    commandToInsert.Parameters.Add("?AGENT_NAME", MySqlDbType.VarChar, 100).Value = loan.AgentName;
                    commandToInsert.Parameters.Add("?AGENT_EMAILID", MySqlDbType.VarChar, 250).Value = loan.AgentEmailId;

                    commandToInsert.Parameters.Add("?CUSTOMER_FIRST_NAME", MySqlDbType.VarChar, 100).Value = loan.CustomerFirstName;
                    commandToInsert.Parameters.Add("?CUSTOMER_LAST_NAME", MySqlDbType.VarChar, 100).Value = loan.CustomerLastName;
                    if (string.IsNullOrWhiteSpace(emailDomain))
                    {
                        commandToInsert.Parameters.Add("?CUSTOMER_EMAIL_ID", MySqlDbType.VarChar, 250).Value = loan.CustomerEmailId;
                    }
                    else
                    {
                        commandToInsert.Parameters.Add("?CUSTOMER_EMAIL_ID", MySqlDbType.VarChar, 250).Value = replaceEmailAddress(loan.CustomerEmailId, emailDomain, emailPrefix);
                    }
                    commandToInsert.Parameters.Add("?CUSTOMER_INTERACTION_DETAILS", MySqlDbType.VarChar, 500).Value = loan.CustomerInteractionDetails;
                    commandToInsert.Parameters.Add("?ENGAGEMENT_CLOSED_TIME", MySqlDbType.DateTime).Value = loan.EngagementClosedTime;
                    commandToInsert.Parameters.Add("?REMINDER_COUNTS", MySqlDbType.Int32).Value = loan.ReminderCounts;
                    commandToInsert.Parameters.Add("?LAST_REMINDER_TIME", MySqlDbType.DateTime).Value = DateTime.Now;
                    commandToInsert.Parameters.Add("?STATUS", MySqlDbType.Int32).Value = loan.Status;
                    commandToInsert.Parameters.Add("?CREATED_ON", MySqlDbType.DateTime).Value = loan.CreatedOn;
                    commandToInsert.Parameters.Add("?MODIFIED_ON", MySqlDbType.DateTime).Value = DateTime.Now;

                    //
                    commandToInsert.ExecuteNonQuery();
                }
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanRepository.InsertLoan()", ex);
                throw ex;
            }
            finally
            {
                if (null != commandToInsert) { commandToInsert.Dispose(); }
            }

            Logger.Info("Exiting the method LoanRepository.InsertLoan(): LoanId:" + loan.SurveySourceId + " : CustomerEmailId : " + loan.CustomerEmailId);
            return returnValue;
        }

        public bool InserLoan(List<LoanEntity> loans, string emailDomain, string emailPrefix)
        {
            Logger.Info("Entering the method LoanRepository.InsertLoan(List<>)");
            bool returnValue = false;

            MySqlConnection mySqlDbConnnection = null;
            try
            {
                mySqlDbConnnection = _socialSurveryContext.DBConnnection;
                foreach (var loanEntity in loans)
                {
                    InsertLoan(loanEntity, mySqlDbConnnection, emailDomain, emailPrefix);
                }
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanRepository.InsertLoan(List<>)", ex);
                throw;
            }
            finally
            {
                // anyhow close the db connection
                if (mySqlDbConnnection.State == System.Data.ConnectionState.Open)
                {
                    mySqlDbConnnection.Close();
                    mySqlDbConnnection.Dispose();
                }
            }

            //
            Logger.Info("Exiting the method LoanRepository.InsertLoan(List<>)");
            return returnValue;
        }

        public bool IsSurveySourceIdExists(LoanEntity loan, MySqlConnection mySqlDbConnection)
        {
            Logger.Info("Entering the method LoanRepository.IsSurveySourceIdExists(): SURVEY_SOURCE_ID: " + loan.SurveySourceId
                 + " : CUSTOMER_EMAIL_ID :  " + loan.CustomerEmailId + " : CUSTOMER_FIRST_NAME : " + loan.CustomerFirstName);
            bool returnValue = false;

            string sqlQuery = SELECT_QUERY;

            MySqlCommand commandToSelect = null;
            MySqlDataReader dataReader = null;

            try
            {
                // select spi.Survey_source_id, spi.CUSTOMER_EMAIL_ID, spi.CUSTOMER_FIRST_NAME from survey_pre_initiation as spi
                commandToSelect = new MySqlCommand(sqlQuery, mySqlDbConnection);
                commandToSelect.Parameters.Add("?SURVEY_SOURCE_ID", MySqlDbType.VarChar, 250).Value = loan.SurveySourceId;
                commandToSelect.Parameters.Add("?CUSTOMER_EMAIL_ID", MySqlDbType.VarChar, 250).Value = loan.CustomerEmailId;
                commandToSelect.Parameters.Add("?CUSTOMER_FIRST_NAME", MySqlDbType.VarChar, 100).Value = loan.CustomerFirstName;

                dataReader = commandToSelect.ExecuteReader();
                while (dataReader.Read())
                {
                    returnValue = true;
                    break;
                }
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanRepository.IsSurveySourceIdExists(): ", ex);
                throw ex;
            }
            finally
            {
                if (null != dataReader && dataReader.IsClosed == false) { dataReader.Close(); }
                if (null != dataReader) { dataReader.Dispose(); }
                if (null != commandToSelect) { commandToSelect.Dispose(); }
            }

            if (returnValue)
            {
                Logger.Info("Exiting the method LoanRepository.IsSurveySourceIdExists(): Records already present in database: don't insert: SURVEY_SOURCE_ID: " + loan.SurveySourceId
                     + " : CUSTOMER_EMAIL_ID :  " + loan.CustomerEmailId + " : CUSTOMER_FIRST_NAME : " + loan.CustomerFirstName);
            }
            else
            {
                Logger.Info("Exiting the method LoanRepository.IsSurveySourceIdExists(): SURVEY_SOURCE_ID: " + loan.SurveySourceId
                    + " : CUSTOMER_EMAIL_ID :  " + loan.CustomerEmailId + " : CUSTOMER_FIRST_NAME : " + loan.CustomerFirstName);
            }
            return returnValue;
        }

        private string replaceEmailAddress(string email, string emailDomain, string emailPrefix)
        {
            if (string.IsNullOrWhiteSpace(email))
            {
                return "";
            }
            else
            {
                email = email.Replace("@", "+");
                Logger.Debug("Transitional email address: " + email);
                email = emailPrefix + "+" + email + "@" + emailDomain;
                Logger.Debug("Final replaced email address: " + email);
                return email;
            }
        }
    }
}
