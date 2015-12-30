package com.realtech.socialsurvey.core.handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.starter.PrepareBillingReport;


@Component
public class BillingReportHandler
{
    private ExecutorService executor;

    @Autowired
    private PrepareBillingReport prepareBillingReport;


    public void startReportGeneration()
    {
        executor = Executors.newFixedThreadPool( 1 );
        executor.execute( prepareBillingReport );
    }
}
