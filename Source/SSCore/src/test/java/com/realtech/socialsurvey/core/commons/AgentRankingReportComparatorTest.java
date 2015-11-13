package com.realtech.socialsurvey.core.commons;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.realtech.socialsurvey.core.entities.AgentRankingReport;


public class AgentRankingReportComparatorTest
{

    private AgentRankingReportComparator comparator;
    private AgentRankingReport agentRankingReportA;
    private AgentRankingReport agentRankingReportB;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception {}


    @Before
    public void setUp() throws Exception
    {
        comparator = new AgentRankingReportComparator();
        agentRankingReportA = new AgentRankingReport();
        agentRankingReportB = new AgentRankingReport();
    }


    @After
    public void tearDown() throws Exception {}


    @Test
    public void testComparingWithHigherFirstAgentRankingReport()
    {
        agentRankingReportA.setAverageScore( 4 );
        agentRankingReportB.setAverageScore( 2 );
        assertEquals( "Agent Ranking Report A is not higher than B", -1,
            comparator.compare( agentRankingReportA, agentRankingReportB ) );
    }


    @Test
    public void testComparingWithHigherSecondAgentRankingReport()
    {
        agentRankingReportA.setAverageScore( 2 );
        agentRankingReportB.setAverageScore( 4 );
        assertEquals( "Agent Ranking Report B is not higher than A", 1,
            comparator.compare( agentRankingReportA, agentRankingReportB ) );
    }


    @Test
    public void testComparingWithEqualAgentRankingReport()
    {
        agentRankingReportA.setAverageScore( 3 );
        agentRankingReportB.setAverageScore( 3 );
        assertEquals( "Agent Ranking Report A is not equivalant to B", 0,
            comparator.compare( agentRankingReportA, agentRankingReportB ) );
    }

}
