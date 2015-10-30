package com.realtech.socialsurvey.version1.testcase;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;


public class TestSetup implements IMethodInterceptor
{
    private Log log = LogFactory.getLog( "UI" );

    @Override
    public List<IMethodInstance> intercept( List<IMethodInstance> originalList, ITestContext context )
    {
        List<IMethodInstance> newList = new ArrayList<IMethodInstance>();

        log.debug( "No. of Tests: " + originalList.size() );

        for ( int i = 0; i < originalList.size(); i++ ) {
            IMethodInstance method = originalList.get( i );

            String methodName = method.getMethod().getTestClass().getName() + "." + method.getMethod().getMethodName();

            log.debug( "Test Method:\t" + methodName + " is added" );
            newList.add( method );
        }

        return newList;
    }
}
