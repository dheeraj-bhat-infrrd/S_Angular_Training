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

        static readonly DateTime EPOCH_TIME = new DateTime(1970, 1, 2, 0, 0, 0);

        static readonly int DEFAULT_REGION_ID = 0;

        static readonly int DEFAULT_BRANCH_ID = 0;

        static readonly int DEFAULT_AGENT_ID = 0;

        // select spi.SURVEY_SOURCE_ID, spi.CUSTOMER_EMAIL_ID, spi.CUSTOMER_FIRST_NAME from survey_pre_initiation as spi
        private const string SELECT_QUERY = @"SELECT spi.SURVEY_PRE_INITIATION_ID, spi.SURVEY_SOURCE_ID FROM SURVEY_PRE_INITIATION as  spi
                                        WHERE spi.SURVEY_SOURCE_ID = ?SURVEY_SOURCE_ID AND spi.CUSTOMER_EMAIL_ID = ?CUSTOMER_EMAIL_ID AND spi.CUSTOMER_FIRST_NAME = ?CUSTOMER_FIRST_NAME ;";

        private const string CRM_BATCH_TRACKER_SELECT_QUERY = @"SELECT crmtrck.ID, crmtrck.SOURCE, crmtrck.COMPANY_ID, crmtrck.RECENT_RECORD_FETCHED_START_DATE, crmtrck.RECENT_RECORD_FETCHED_END_DATE, crmtrck.ERROR, crmtrck.CREATED_ON, crmtrck.MODIFIED_ON FROM CRM_BATCH_TRACKER as  crmtrck WHERE crmtrck.COMPANY_ID = ?COMPANY_ID AND crmtrck.SOURCE = ?SOURCE ;";

        private const string COMPANY_SELECT_QUERY = @"SELECT comp.COMPANY_ID, comp.COMPANY, comp.STATUS FROM COMPANY as  comp WHERE comp.COMPANY_ID = ?COMPANY_ID;";

        private const string CRM_BATCH_TRACKER_UPDATE_QUERY = @"UPDATE CRM_BATCH_TRACKER SET RECENT_RECORD_FETCHED_START_DATE = ?RECENT_RECORD_FETCHED_START_DATE, RECENT_RECORD_FETCHED_END_DATE = ?RECENT_RECORD_FETCHED_END_DATE, ERROR = ?ERROR, MODIFIED_ON = ?MODIFIED_ON WHERE ID = ?ID";

        private const string CRM_BATCH_TRACKER_INSERT_QUERY = @"INSERT INTO CRM_BATCH_TRACKER(  
                                         
                                         COMPANY_ID
                                        , REGION_ID
                                        , BRANCH_ID
                                        , AGENT_ID
                                        , SOURCE
                                        , RECENT_RECORD_FETCHED_START_DATE
                                        , RECENT_RECORD_FETCHED_END_DATE
                                        , ERROR
                                        , CREATED_ON
                                        , MODIFIED_ON
                                          )
                                        VALUES(
                                          ?COMPANY_ID
                                        , ?REGION_ID
                                        , ?BRANCH_ID
                                        , ?AGENT_ID
                                        , ?SOURCE
                                        , ?RECENT_RECORD_FETCHED_START_DATE
                                        , ?RECENT_RECORD_FETCHED_END_DATE
                                        , ?ERROR
                                        , ?CREATED_ON
                                        , ?MODIFIED_ON
                                        ) ;";

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
										, COLLECTION_NAME
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
										, ?COLLECTION_NAME
                                        ) ;";

        public bool UpdateCrmBatchTracker(CRMBatchTrackerEntity entity)
        {
            Logger.Info("Inside method UpdateCrmBatchTracker");
            bool returnValue = false;
            String updateQuery = CRM_BATCH_TRACKER_UPDATE_QUERY;
            MySqlConnection mySqlDbConnection = null;
            MySqlCommand commandToUpdate = null;
            try
            {
                mySqlDbConnection = _socialSurveryContext.DBConnnection;
                commandToUpdate = new MySqlCommand(updateQuery, mySqlDbConnection);

                // set the parameters
                commandToUpdate.Parameters.Add("?ID", MySqlDbType.Int32).Value = entity.Id;
                commandToUpdate.Parameters.Add("?MODIFIED_ON", MySqlDbType.DateTime).Value = entity.ModifiedOn;
                commandToUpdate.Parameters.Add("?RECENT_RECORD_FETCHED_START_DATE", MySqlDbType.DateTime).Value = entity.RecentRecordFetchedStartDate;
                commandToUpdate.Parameters.Add("?RECENT_RECORD_FETCHED_END_DATE", MySqlDbType.DateTime).Value = entity.RecentRecordFetchedEndDate;
                commandToUpdate.Parameters.Add("?ERROR", MySqlDbType.String).Value = entity.error;
                commandToUpdate.ExecuteNonQuery();
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanRepository.UpdateCrmBatchTracker()", ex);
                throw ex;
            }
            finally
            {
                if (null != commandToUpdate) { commandToUpdate.Dispose(); }
                if (null != mySqlDbConnection) { mySqlDbConnection.Close(); }
            }

            return returnValue;

        }

        public bool InsertCRMBatchTracker(CRMBatchTrackerEntity entity)
        {
            Logger.Info("Inside method InsertCRMBatchTracker");
            bool returnValue = false;
            string insertQuery = CRM_BATCH_TRACKER_INSERT_QUERY;
            MySqlConnection mySqlDbConnection = null;
            MySqlCommand commandToInsert = null;
            try
            {
                mySqlDbConnection = _socialSurveryContext.DBConnnection;
                commandToInsert = new MySqlCommand(insertQuery, mySqlDbConnection);

                // set the parameters
                commandToInsert.Parameters.Add("?SOURCE", MySqlDbType.VarChar, 100).Value = entity.Source;
                commandToInsert.Parameters.Add("?COMPANY_ID", MySqlDbType.Int32).Value = entity.CompanyId;
                commandToInsert.Parameters.Add("?REGION_ID", MySqlDbType.Int32).Value = DEFAULT_REGION_ID;
                commandToInsert.Parameters.Add("?BRANCH_ID", MySqlDbType.Int32).Value = DEFAULT_BRANCH_ID;
                commandToInsert.Parameters.Add("?AGENT_ID", MySqlDbType.Int32).Value = DEFAULT_AGENT_ID;
                commandToInsert.Parameters.Add("?RECENT_RECORD_FETCHED_START_DATE", MySqlDbType.DateTime).Value = entity.RecentRecordFetchedStartDate;
                commandToInsert.Parameters.Add("?RECENT_RECORD_FETCHED_END_DATE", MySqlDbType.DateTime).Value = entity.RecentRecordFetchedEndDate;
                commandToInsert.Parameters.Add("?ERROR", MySqlDbType.DateTime).Value = entity.error;

                commandToInsert.Parameters.Add("?CREATED_ON", MySqlDbType.DateTime).Value = entity.CreatedOn;
                commandToInsert.Parameters.Add("?MODIFIED_ON", MySqlDbType.DateTime).Value = DateTime.Now;

                //
                commandToInsert.ExecuteNonQuery();
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanRepository.InsertLoan()", ex);
                throw ex;
            }
            finally
            {
                if (null != commandToInsert) { commandToInsert.Dispose(); }
                if (null != mySqlDbConnection) { mySqlDbConnection.Close(); }
            }
            return returnValue;
        }

        public bool InsertLoan(LoanEntity loan, MySqlConnection mySqlDbConnection)
        {
            Logger.Info("Entering the method LoanRepository.InsertLoan(): LoanId:" + loan.SurveySourceId + " : CustomerEmailId : " + loan.CustomerEmailId);
            bool returnValue = false;

            string insertQuery = INSERT_QUERY;
            MySqlCommand commandToInsert = null;

            try
            {

                if (false == IsSurveySourceIdExists(loan, mySqlDbConnection))
                {
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
                    commandToInsert.Parameters.Add("?CUSTOMER_EMAIL_ID", MySqlDbType.VarChar, 250).Value = loan.CustomerEmailId;
                    commandToInsert.Parameters.Add("?CUSTOMER_INTERACTION_DETAILS", MySqlDbType.VarChar, 500).Value = loan.CustomerInteractionDetails;
                    commandToInsert.Parameters.Add("?ENGAGEMENT_CLOSED_TIME", MySqlDbType.DateTime).Value = loan.EngagementClosedTime;
                    commandToInsert.Parameters.Add("?REMINDER_COUNTS", MySqlDbType.Int32).Value = loan.ReminderCounts;
                    commandToInsert.Parameters.Add("?LAST_REMINDER_TIME", MySqlDbType.DateTime).Value = EPOCH_TIME;
                    commandToInsert.Parameters.Add("?STATUS", MySqlDbType.Int32).Value = loan.Status;
                    commandToInsert.Parameters.Add("?CREATED_ON", MySqlDbType.DateTime).Value = loan.CreatedOn;
                    commandToInsert.Parameters.Add("?MODIFIED_ON", MySqlDbType.DateTime).Value = DateTime.Now;
                    commandToInsert.Parameters.Add("?COLLECTION_NAME", MySqlDbType.VarChar, 250).Value = "COMPANY_SETTINGS";

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


        public bool InserLoan(List<LoanEntity> loans)
        {
            Logger.Info("Entering the method LoanRepository.InsertLoan(List<>)");
            bool returnValue = false;

            MySqlConnection mySqlDbConnnection = null;
            try
            {
                mySqlDbConnnection = _socialSurveryContext.DBConnnection;
                foreach (var loanEntity in loans)
                {
                    Logger.Debug("Insert loan entity  with agent email address " + loanEntity.AgentEmailId + " into database ");
                    InsertLoan(loanEntity, mySqlDbConnnection);
                }
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanRepository.InsertLoan(List<>)", ex);
                throw;
            }
            finally
            {

                if (null != mySqlDbConnnection && mySqlDbConnnection.State == System.Data.ConnectionState.Open)
                {
                    Logger.Debug("Closing database connection ");
                    mySqlDbConnnection.Close();
                    mySqlDbConnnection.Dispose();
                }
            }

            //
            Logger.Info("Exiting the method LoanRepository.InsertLoan(List<>)");
            return returnValue;
        }

        public CRMBatchTrackerEntity getCrmBatchTrackerByCompanyAndSource(long companyId, string source)
        {
            Logger.Debug("Inside method getCrmBatchTrackerByCompanyAndSource");
            String sqlQuery = CRM_BATCH_TRACKER_SELECT_QUERY;
            MySqlCommand commandToSelect = null;
            MySqlDataReader dataReader = null;
            CRMBatchTrackerEntity crmBatchTracker = null;
            MySqlConnection mySqlDbConnection = null;
            try
            {
                mySqlDbConnection = _socialSurveryContext.DBConnnection;
                commandToSelect = new MySqlCommand(sqlQuery, mySqlDbConnection);
                commandToSelect.Parameters.Add("?COMPANY_ID", MySqlDbType.Int32).Value = companyId;
                commandToSelect.Parameters.Add("?SOURCE", MySqlDbType.VarChar, 250).Value = source;
                dataReader = commandToSelect.ExecuteReader();
                while (dataReader.Read())
                {
                    crmBatchTracker = new CRMBatchTrackerEntity();
                    crmBatchTracker.Id = dataReader.GetInt32("ID");
                    crmBatchTracker.CompanyId = companyId;
                    crmBatchTracker.RecentRecordFetchedStartDate = dataReader.GetDateTime("RECENT_RECORD_FETCHED_START_DATE");
                    crmBatchTracker.RecentRecordFetchedEndDate = dataReader.GetDateTime("RECENT_RECORD_FETCHED_END_DATE");
                    crmBatchTracker.Source = source;
                    crmBatchTracker.CreatedOn = dataReader.GetDateTime("CREATED_ON");
                    crmBatchTracker.ModifiedOn = dataReader.GetDateTime("MODIFIED_ON");

                    var errorColunnIndex = dataReader.GetOrdinal("ERROR");
                    if (!dataReader.IsDBNull(errorColunnIndex))
                        crmBatchTracker.error = dataReader.GetString("ERROR");
                    

                }
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanRepository.getCrmBatchTrackerByCompanyAndSource(): ", ex);
                throw ex;
            }
            finally
            {
                if (null != dataReader && dataReader.IsClosed == false) { dataReader.Close(); }
                if (null != dataReader) { dataReader.Dispose(); }
                if (null != commandToSelect) { commandToSelect.Dispose(); }
                if (null != mySqlDbConnection) { mySqlDbConnection.Close(); }
            }
            return crmBatchTracker;
        }

        public Company getCompanyById(long companyId)
        {
            Logger.Debug("Inside method getCompanyById");
            String sqlQuery = COMPANY_SELECT_QUERY;
            MySqlCommand commandToSelect = null;
            MySqlDataReader dataReader = null;
            Company company = null;
            MySqlConnection mySqlDbConnection = null;
            try
            {
                mySqlDbConnection = _socialSurveryContext.DBConnnection;
                commandToSelect = new MySqlCommand(sqlQuery, mySqlDbConnection);
                commandToSelect.Parameters.Add("?COMPANY_ID", MySqlDbType.Int32).Value = companyId;

                dataReader = commandToSelect.ExecuteReader();
                while (dataReader.Read())
                {
                    company = new Company();
                    company.companyId = companyId;
                    company.company = dataReader.GetString("COMPANY");
                    company.status = dataReader.GetString("STATUS");


                }
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanRepository.getCrmBatchTrackerByCompanyAndSource(): ", ex);
                throw ex;
            }
            finally
            {
                if (null != dataReader && dataReader.IsClosed == false) { dataReader.Close(); }
                if (null != dataReader) { dataReader.Dispose(); }
                if (null != commandToSelect) { commandToSelect.Dispose(); }
                if (null != mySqlDbConnection) { mySqlDbConnection.Close(); }
            }
            return company;
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
    }
}
