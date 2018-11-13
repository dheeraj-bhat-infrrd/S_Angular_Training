using EncompassSocialSurvey.Entity;
using MySql.Data.MySqlClient;
using System;
using System.Collections.Generic;

namespace EncompassSocialSurvey.DAL
{
    public class LoanRepository
    {
        EncompassSocialSurveryContext _socialSurveryContext = new EncompassSocialSurveryContext();

        #region Read only props

        static readonly DateTime EPOCH_TIME = new DateTime(1970, 1, 2, 0, 0, 0);

        static readonly int DEFAULT_REGION_ID = 0;

        static readonly int DEFAULT_BRANCH_ID = 0;

        static readonly int DEFAULT_AGENT_ID = 0;

        #endregion

        #region Constants

        // select spi.SURVEY_SOURCE_ID, spi.CUSTOMER_EMAIL_ID, spi.CUSTOMER_FIRST_NAME from survey_pre_initiation as spi
        private const string SELECT_QUERY = @"SELECT spi.SURVEY_PRE_INITIATION_ID, spi.SURVEY_SOURCE_ID FROM SURVEY_PRE_INITIATION as  spi
                                        WHERE spi.SURVEY_SOURCE_ID = ?SURVEY_SOURCE_ID AND spi.CUSTOMER_EMAIL_ID = ?CUSTOMER_EMAIL_ID AND spi.CUSTOMER_FIRST_NAME = ?CUSTOMER_FIRST_NAME AND spi.COMPANY_ID = ?COMPANY_ID;";

        private const string CRM_BATCH_TRACKER_SELECT_QUERY = @"SELECT crmtrck.ID, crmtrck.SOURCE, crmtrck.COMPANY_ID, crmtrck.LAST_RUN_START_DATE, crmtrck.LAST_RUN_END_DATE, crmtrck.RECENT_RECORD_FETCHED_DATE, crmtrck.ERROR, crmtrck.CREATED_ON, crmtrck.MODIFIED_ON FROM CRM_BATCH_TRACKER as  crmtrck WHERE crmtrck.COMPANY_ID = ?COMPANY_ID AND crmtrck.SOURCE = ?SOURCE ;";

        private const string COMPANY_SELECT_QUERY = @"SELECT comp.COMPANY_ID, comp.COMPANY, comp.STATUS FROM COMPANY as  comp WHERE comp.COMPANY_ID = ?COMPANY_ID;";

        private const string CRM_BATCH_TRACKER_UPDATE_QUERY = @"UPDATE CRM_BATCH_TRACKER SET LAST_RUN_START_DATE = ?LAST_RUN_START_DATE, LAST_RUN_END_DATE = ?LAST_RUN_END_DATE, RECENT_RECORD_FETCHED_DATE = ?RECENT_RECORD_FETCHED_DATE, LAST_RUN_RECORD_FETCHED_COUNT=?LAST_RUN_RECORD_FETCHED_COUNT, ERROR = ?ERROR, MODIFIED_ON = ?MODIFIED_ON WHERE ID = ?ID";

        private const string CRM_BATCH_TRACKER_INSERT_QUERY = @"INSERT INTO CRM_BATCH_TRACKER(  
                                         
                                         COMPANY_ID
                                        , REGION_ID
                                        , BRANCH_ID
                                        , AGENT_ID
                                        , SOURCE
                                        , LAST_RUN_START_DATE
                                        , LAST_RUN_END_DATE
                                        , RECENT_RECORD_FETCHED_DATE
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
                                        , ?LAST_RUN_START_DATE
                                        , ?LAST_RUN_END_DATE
                                        , ?RECENT_RECORD_FETCHED_DATE
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
                                        , STATE
                                        , CITY
                                        , PARTICIPANT_TYPE
                                        , PROPERTY_ADDRESS
                                        , LOAN_PROCESSOR_NAME
                                        , LOAN_PROCESSOR_EMAIL
                                        , LOAN_PROCESSOR_EMAIL
                                        , CUSTOM_FIELD_ONE
                                        , CUSTOM_FIELD_TWO
                                        , CUSTOM_FIELD_THREE
                                        , CUSTOM_FIELD_FOUR
                                        , CUSTOM_FIELD_FIVE
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
                                        , ?STATE
                                        , ?CITY
                                        , ?PARTICIPANT_TYPE
                                        , ?PROPERTY_ADDRESS
                                        , ?LOAN_PROCESSOR_NAME
                                        , ?LOAN_PROCESSOR_EMAIL
                                        , ?CUSTOM_FIELD_ONE
                                        , ?CUSTOM_FIELD_TWO
                                        , ?CUSTOM_FIELD_THREE
                                        , ?CUSTOM_FIELD_FOUR
                                        , ?CUSTOM_FIELD_FIVE
                                        ) ;";

        
         private const string CRM_BATCH_TRACKER_HISTORY_INSERT_QUERY = @"INSERT INTO CRM_BATCH_TRACKER_HISTORY(  
                                         
                                         CRM_BATCH_TRACKER_ID
                                        , STATUS
                                        , COUNT_OF_RECORDS_FETCHED
                                        , CREATED_ON
                                        , CREATED_BY
                                        , MODIFIED_ON
                                        , MODIFIED_BY
                                          )
                                        VALUES(
                                          ?CRM_BATCH_TRACKER_ID
                                        , ?STATUS
                                        , ?COUNT_OF_RECORDS_FETCHED
                                        , ?CREATED_ON
                                        , ?CREATED_BY
                                        , ?MODIFIED_ON
                                        , ?MODIFIED_BY
                                        ) ;";

        #endregion

        public int noOfRecordsInserted = 0;

        #region public methods

        /// <summary>
        /// Updates crm batch tracker table in db
        /// </summary>
        /// <param name="entity"></param>
        /// <returns></returns>
        public bool UpdateCrmBatchTracker(CRMBatchTrackerEntity entity)
        {
            Logger.Info("Inside method UpdateCrmBatchTracker");
            bool returnValue = false;
            String updateQuery = CRM_BATCH_TRACKER_UPDATE_QUERY;
           
            try
            {
                using (MySqlConnection mySqlDbConnection = _socialSurveryContext.DBConnnection)
                {
                    using (MySqlCommand commandToUpdate = new MySqlCommand(updateQuery, mySqlDbConnection))
                    {
                        
                        // set the parameters
                        commandToUpdate.Parameters.Add("?ID", MySqlDbType.Int32).Value = entity.Id;
                        commandToUpdate.Parameters.Add("?MODIFIED_ON", MySqlDbType.DateTime).Value = entity.ModifiedOn;
                        commandToUpdate.Parameters.Add("?LAST_RUN_START_DATE", MySqlDbType.DateTime).Value = entity.LastRunStartDate;
                        commandToUpdate.Parameters.Add("?LAST_RUN_END_DATE", MySqlDbType.DateTime).Value = entity.LastRunEndDate;
                        commandToUpdate.Parameters.Add("?RECENT_RECORD_FETCHED_DATE", MySqlDbType.DateTime).Value = entity.RecentRecordFetchedDate;
                        commandToUpdate.Parameters.Add("?LAST_RUN_RECORD_FETCHED_COUNT", MySqlDbType.Int32).Value = entity.LastRunRecordFetchedCount;
                        commandToUpdate.Parameters.Add("?ERROR", MySqlDbType.String).Value = entity.error;
                        commandToUpdate.ExecuteNonQuery();
                    }
                }
            }

            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanRepository.UpdateCrmBatchTracker()", ex);
                throw ex;
            }
            

            return returnValue;

        }

        /// <summary>
        /// insert in crm batch tracker table in db if any new data if found
        /// </summary>
        /// <param name="entity"></param>
        /// <returns></returns>
        public bool InsertCRMBatchTracker(CRMBatchTrackerEntity entity)
        {
            Logger.Info("Inside method InsertCRMBatchTracker");
            bool returnValue = false;
            string insertQuery = CRM_BATCH_TRACKER_INSERT_QUERY;
      
            try
            {
                using (MySqlConnection mySqlDbConnection = _socialSurveryContext.DBConnnection)
                {
                    using (MySqlCommand commandToInsert = new MySqlCommand(insertQuery, mySqlDbConnection))
                    {

                        // set the parameters
                        commandToInsert.Parameters.Add("?SOURCE", MySqlDbType.VarChar, 100).Value = entity.Source;
                        commandToInsert.Parameters.Add("?COMPANY_ID", MySqlDbType.Int32).Value = entity.CompanyId;
                        commandToInsert.Parameters.Add("?REGION_ID", MySqlDbType.Int32).Value = DEFAULT_REGION_ID;
                        commandToInsert.Parameters.Add("?BRANCH_ID", MySqlDbType.Int32).Value = DEFAULT_BRANCH_ID;
                        commandToInsert.Parameters.Add("?AGENT_ID", MySqlDbType.Int32).Value = DEFAULT_AGENT_ID;
                        commandToInsert.Parameters.Add("?LAST_RUN_START_DATE", MySqlDbType.DateTime).Value = entity.LastRunStartDate;
                        commandToInsert.Parameters.Add("?LAST_RUN_END_DATE", MySqlDbType.DateTime).Value = entity.LastRunEndDate;
                        commandToInsert.Parameters.Add("?RECENT_RECORD_FETCHED_DATE", MySqlDbType.DateTime).Value = entity.RecentRecordFetchedDate;
                        commandToInsert.Parameters.Add("?ERROR", MySqlDbType.DateTime).Value = entity.error;

                        commandToInsert.Parameters.Add("?CREATED_ON", MySqlDbType.DateTime).Value = entity.CreatedOn;
                        commandToInsert.Parameters.Add("?MODIFIED_ON", MySqlDbType.DateTime).Value = DateTime.Now;

                        //
                        commandToInsert.ExecuteNonQuery();
                    }
                }
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanRepository.InsertLoan()", ex);
                throw ex;
            }
          
            return returnValue;
        }

        /// <summary>
        /// Insert loan in db
        /// </summary>
        /// <param name="loan"></param>
        /// <param name="mySqlDbConnection"></param>
        /// <returns></returns>
        public bool InsertLoan(LoanEntity loan, MySqlConnection mySqlDbConnection)
        {
            Logger.Info("Entering the method LoanRepository.InsertLoan(): LoanId:" + loan.SurveySourceId + " : CustomerEmailId : " + loan.CustomerEmailId);
            bool returnValue = false;
            string insertQuery = INSERT_QUERY;
            
            try
            {

                if (false == IsSurveySourceIdExists(loan, mySqlDbConnection))
                {
                    using (MySqlCommand commandToInsert = new MySqlCommand(insertQuery, mySqlDbConnection))
                    {
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
                        commandToInsert.Parameters.Add("?STATE", MySqlDbType.VarChar, 100).Value = loan.State;
                        commandToInsert.Parameters.Add("?CITY", MySqlDbType.VarChar, 100).Value = loan.City;
                        commandToInsert.Parameters.Add("?PARTICIPANT_TYPE", MySqlDbType.Int32).Value = loan.ParticipantType;
                        commandToInsert.Parameters.Add("?PROPERTY_ADDRESS", MySqlDbType.VarChar, 250).Value = loan.PropertyAddress;
                        commandToInsert.Parameters.Add("?LOAN_PROCESSOR_NAME", MySqlDbType.VarChar, 250).Value = loan.LoanProcessorName;
                        commandToInsert.Parameters.Add("?LOAN_PROCESSOR_EMAIL", MySqlDbType.VarChar, 250).Value = loan.LoanProcessorEmail;
                        commandToInsert.Parameters.Add("?CUSTOM_FIELD_ONE", MySqlDbType.VarChar, 250).Value = loan.CustomFieldOne;
                        commandToInsert.Parameters.Add("?CUSTOM_FIELD_TWO", MySqlDbType.VarChar, 250).Value = loan.CustomFieldTwo;
                        commandToInsert.Parameters.Add("?CUSTOM_FIELD_THREE", MySqlDbType.VarChar, 250).Value = loan.CustomFieldThree;
                        commandToInsert.Parameters.Add("?CUSTOM_FIELD_FOUR", MySqlDbType.VarChar, 250).Value = loan.CustomFieldFour;
                        commandToInsert.Parameters.Add("?CUSTOM_FIELD_FIVE", MySqlDbType.VarChar, 250).Value = loan.CustomFieldFive;
                        //
                        commandToInsert.ExecuteNonQuery();
                        noOfRecordsInserted++;
                    }
                }
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanRepository.InsertLoan()", ex);
                throw ex;
            }
            

            Logger.Info("Exiting the method LoanRepository.InsertLoan(): LoanId:" + loan.SurveySourceId + " : CustomerEmailId : " + loan.CustomerEmailId);
            return returnValue;
        }

        /// <summary>
        /// Insert in crm batch tracker history table in db with no of records fetched
        /// </summary>
        /// <param name="entity"></param>
        public void InsertCRMBatchTrackerHistory(CrmBatchTrackerHistory entity)
        {
            Logger.Info("Inside method InsertCRMBatchTrackerHistory");
            #region insert in db   
                    
            string insertQuery = CRM_BATCH_TRACKER_HISTORY_INSERT_QUERY;
            
            try
            {
                using (MySqlConnection mySqlDbConnection = _socialSurveryContext.DBConnnection)
                {
                    using (MySqlCommand commandToInsert = new MySqlCommand(insertQuery, mySqlDbConnection))
                    {

                        #region set the command parameters

                        commandToInsert.Parameters.Add("?CRM_BATCH_TRACKER_ID", MySqlDbType.Int32, 100).Value = entity.CrmBatchTrackerID;
                        commandToInsert.Parameters.Add("?STATUS", MySqlDbType.Int32).Value = entity.Status;
                        commandToInsert.Parameters.Add("?COUNT_OF_RECORDS_FETCHED", MySqlDbType.Int32).Value = entity.CountOfRecordsFetched;
                        commandToInsert.Parameters.Add("?CREATED_ON", MySqlDbType.DateTime).Value = entity.CreatedOn;
                        commandToInsert.Parameters.Add("?CREATED_BY", MySqlDbType.VarChar).Value = entity.CreatedBy;
                        commandToInsert.Parameters.Add("?MODIFIED_ON", MySqlDbType.DateTime).Value = entity.ModifiedOn;
                        commandToInsert.Parameters.Add("?MODIFIED_BY", MySqlDbType.VarChar).Value = entity.ModifiedBy;

                        #endregion
                        commandToInsert.ExecuteNonQuery();
                    }
                }
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanRepository.InsertCRMBatchTrackerHistory()", ex);
                throw ex;
            }
         
            #endregion
            Logger.Info("Exit method LoanRepository.InsertCRMBatchTrackerHistory");
        }

        /// <summary>
        /// insert loan in db
        /// </summary>
        /// <param name="loans"></param>
        /// <returns></returns>
        public int InserLoan(List<LoanEntity> loans)
        {
            Logger.Info("Entering the method LoanRepository.InsertLoan(List<>)");
           
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
            return noOfRecordsInserted;
        }

        /// <summary>
        /// Retrieve crm batch tracker from db by company id and source
        /// </summary>
        /// <param name="companyId"></param>
        /// <param name="source"></param>
        /// <returns></returns>
        public CRMBatchTrackerEntity GetCrmBatchTrackerByCompanyAndSource(long companyId, string source)
        {
            Logger.Debug("Inside method getCrmBatchTrackerByCompanyAndSource");
            String sqlQuery = CRM_BATCH_TRACKER_SELECT_QUERY;
            
            MySqlDataReader dataReader = null;
            CRMBatchTrackerEntity crmBatchTracker = null;

            try
            {
                using (MySqlConnection mySqlDbConnection = _socialSurveryContext.DBConnnection)
                {
                    using (MySqlCommand commandToSelect = new MySqlCommand(sqlQuery, mySqlDbConnection))
                    {
                        commandToSelect.Parameters.Add("?COMPANY_ID", MySqlDbType.Int32).Value = companyId;
                        commandToSelect.Parameters.Add("?SOURCE", MySqlDbType.VarChar, 250).Value = source;
                        using (dataReader = commandToSelect.ExecuteReader())
                        {
                            while (dataReader.Read())
                            {
                                crmBatchTracker = new CRMBatchTrackerEntity();
                                crmBatchTracker.Id = dataReader.GetInt32("ID");
                                crmBatchTracker.CompanyId = companyId;
                                crmBatchTracker.LastRunStartDate = dataReader.GetDateTime("LAST_RUN_START_DATE");
                                crmBatchTracker.LastRunEndDate = dataReader.GetDateTime("LAST_RUN_END_DATE");
                                crmBatchTracker.RecentRecordFetchedDate = dataReader.GetDateTime("RECENT_RECORD_FETCHED_DATE");
                                crmBatchTracker.Source = source;
                                crmBatchTracker.CreatedOn = dataReader.GetDateTime("CREATED_ON");
                                crmBatchTracker.ModifiedOn = dataReader.GetDateTime("MODIFIED_ON");

                                var errorColunnIndex = dataReader.GetOrdinal("ERROR");
                                if (!dataReader.IsDBNull(errorColunnIndex))
                                    crmBatchTracker.error = dataReader.GetString("ERROR");

                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanRepository.getCrmBatchTrackerByCompanyAndSource(): ", ex);
                throw ex;
            }
            
            return crmBatchTracker;
        }

        /// <summary>
        /// retrieve company data from db by company id
        /// </summary>
        /// <param name="companyId"></param>
        /// <returns></returns>
        public Company GetCompanyById(long companyId)
        {
            Logger.Debug("Inside method getCompanyById");
            String sqlQuery = COMPANY_SELECT_QUERY;
            Company company = null;
            
            try
            {
                using (MySqlConnection mySqlDbConnection = _socialSurveryContext.DBConnnection)
                {
                    using (MySqlCommand commandToSelect = new MySqlCommand(sqlQuery, mySqlDbConnection))
                    {
                        commandToSelect.Parameters.Add("?COMPANY_ID", MySqlDbType.Int32).Value = companyId;

                        using (MySqlDataReader dataReader = commandToSelect.ExecuteReader())
                        {
                            while (dataReader.Read())
                            {
                                company = new Company();
                                company.companyId = companyId;
                                company.company = dataReader.GetString("COMPANY");
                                company.status = dataReader.GetString("STATUS");
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanRepository.getCrmBatchTrackerByCompanyAndSource(): ", ex);
                throw ex;
            }
            
            return company;
        }

        /// <summary>
        /// Checks if survey exist
        /// </summary>
        /// <param name="loan"></param>
        /// <param name="mySqlDbConnection"></param>
        /// <returns></returns>
        public bool IsSurveySourceIdExists(LoanEntity loan, MySqlConnection mySqlDbConnection)
        {
            Logger.Info("Entering the method LoanRepository.IsSurveySourceIdExists(): SURVEY_SOURCE_ID: " + loan.SurveySourceId
                 + " : CUSTOMER_EMAIL_ID :  " + loan.CustomerEmailId + " : CUSTOMER_FIRST_NAME : " + loan.CustomerFirstName
                 + " : COMPANY_ID : " + loan.CompanyId );
            bool returnValue = false;

            string sqlQuery = SELECT_QUERY;
            
            try
            {

                // select spi.Survey_source_id, spi.CUSTOMER_EMAIL_ID, spi.CUSTOMER_FIRST_NAME from survey_pre_initiation as spi
                using (MySqlCommand commandToSelect = new MySqlCommand(sqlQuery, mySqlDbConnection))
                {
                    commandToSelect.Parameters.Add("?SURVEY_SOURCE_ID", MySqlDbType.VarChar, 250).Value = loan.SurveySourceId;
                    commandToSelect.Parameters.Add("?CUSTOMER_EMAIL_ID", MySqlDbType.VarChar, 250).Value = loan.CustomerEmailId;
                    commandToSelect.Parameters.Add("?CUSTOMER_FIRST_NAME", MySqlDbType.VarChar, 100).Value = loan.CustomerFirstName;
                    commandToSelect.Parameters.Add("?COMPANY_ID", MySqlDbType.Int32).Value = loan.CompanyId;

                    using (MySqlDataReader dataReader = commandToSelect.ExecuteReader())
                    {
                        while (dataReader.Read())
                        {
                            returnValue = true;
                            break;
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                Logger.Error("Caught an exception: LoanRepository.IsSurveySourceIdExists(): ", ex);
                throw ex;
            }
          

            if (returnValue)
            {
                Logger.Info("Exiting the method LoanRepository.IsSurveySourceIdExists(): Records already present in database: don't insert: SURVEY_SOURCE_ID: " + loan.SurveySourceId
                     + " : CUSTOMER_EMAIL_ID :  " + loan.CustomerEmailId + " : CUSTOMER_FIRST_NAME : " + loan.CustomerFirstName + " : COMPANY_ID : " + loan.CompanyId);
            }
            else
            {
                Logger.Info("Exiting the method LoanRepository.IsSurveySourceIdExists(): SURVEY_SOURCE_ID: " + loan.SurveySourceId
                    + " : CUSTOMER_EMAIL_ID :  " + loan.CustomerEmailId + " : CUSTOMER_FIRST_NAME : " + loan.CustomerFirstName + " : COMPANY_ID : " + loan.CompanyId);
            }
            return returnValue;
        }

        #endregion
    }
}
