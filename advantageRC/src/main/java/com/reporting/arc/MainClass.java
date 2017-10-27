/**
 * 
 */
package com.reporting.arc;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.reporting.arc.utils.EmailSendingUtil;
import com.reporting.arc.utils.PropertyReader;


/**
 * @author Subhrajit
 *
 */
public class MainClass
{

    /**
     * @param args
     */
    public static void main( String[] args )
    {
        JobDetail jobDetail = JobBuilder.newJob( JobClass.class ).withIdentity( "Job1", "ReportAutomation" ).build();
        String cronExpression = PropertyReader.getValueForKey( "CRON.EXPRESSION.VALUE" );
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity( "trigger1", "ReportAutomation" ).build();
        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob( jobDetail, trigger );
        } catch ( SchedulerException e ) {
            e.printStackTrace();
            EmailSendingUtil.sendBatchExceptionMail( e.getMessage() );
        }
    }
}
