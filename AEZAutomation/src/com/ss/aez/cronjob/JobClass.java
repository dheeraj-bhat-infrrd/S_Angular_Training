/**
 * 
 */
package com.ss.aez.cronjob;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ss.aez.main.MongoExportTask;
import com.ss.aez.main.MySqlImportTask;
import com.ss.aez.report.ReportGenerater;
import com.ss.aez.util.EmailSendingUtil;

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
