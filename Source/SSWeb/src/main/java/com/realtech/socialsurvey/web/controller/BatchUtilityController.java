package com.realtech.socialsurvey.web.controller;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.realtech.socialsurvey.core.batch.utility.starter.LoneWolfReviewProcessorUtility;
import com.realtech.socialsurvey.web.common.JspResolver;


@Controller
public class BatchUtilityController
{

    @Autowired
    private LoneWolfReviewProcessorUtility loneWolfReviewProcessorUtility;


//    @RequestMapping ( value = "/loadBatchUtility")
    public String loadBatchUtility()
    {
        return JspResolver.BATCH_UTILITY;

    }


//    @RequestMapping ( value = "/startLoneWolfJobDetail")
//    @ResponseBody
    public String startLoneWolfJobDetail()
    {
        loneWolfReviewProcessorUtility.execute();
        return Response.ok().toString();
    }
}
