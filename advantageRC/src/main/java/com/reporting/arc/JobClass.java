/**
 * 
 */
package com.reporting.arc;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.reporting.arc.db.MongoExportTask;
import com.reporting.arc.db.MySqlImportTask;
import com.reporting.arc.reports.ReportGenerater;
import com.reporting.arc.utils.EmailSendingUtil;


/**
 * @author Subhrajit
 *
 */
public class JobClass implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			MongoExportTask.mongoExport();
			MySqlImportTask.importToMysql();
			ReportGenerater.generateReport();
		} catch (Exception e) {
			e.printStackTrace();
			EmailSendingUtil.sendBatchExceptionMail(e.getMessage());
		}
		
	}

}
