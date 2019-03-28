package com.realtech.socialsurvey.core.services.hierarchylocationmanagemant.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.HierarchyRelocationTarget;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.HierarchyType;
import com.realtech.socialsurvey.core.services.hierarchylocationmanagement.impl.HierarchyLocationManagementServiceImpl;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public class HierarchyLocationManagementServiceImplTest 
{
    @Spy
    @InjectMocks
    private HierarchyLocationManagementServiceImpl hierarchyLocationManagementServiceImpl;
    
    @Mock
    private GenericDao<LicenseDetail, Long> licenceDetailDao;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks( this );
    }

    @After
    public void tearDown() throws Exception
    {}
    
    @Test (expected = InvalidInputException.class)
    public void testReloacteUserForNullTargetLocation() throws SolrException, InvalidInputException 
    {
    	hierarchyLocationManagementServiceImpl.relocateUser(new User(), null);
    }
    
    @Test (expected = InvalidInputException.class)
    public void testReloacteUserForNullTargetHierarchy() throws SolrException, InvalidInputException
    {
    	hierarchyLocationManagementServiceImpl.relocateUser(new User(), new HierarchyRelocationTarget());
    }
    
    @Test (expected = InvalidInputException.class)
    public void testReloacteUserForNullBranch() throws SolrException, InvalidInputException
    {
    	HierarchyRelocationTarget target = new HierarchyRelocationTarget();
    	target.setHierarchyType( HierarchyType.USER );
    	hierarchyLocationManagementServiceImpl.relocateUser(new User(),target);
    }
    
    @Test (expected = InvalidInputException.class)
    public void testReloacteUserForNullRegion() throws SolrException, InvalidInputException
    {
    	HierarchyRelocationTarget target = new HierarchyRelocationTarget();
    	target.setHierarchyType( HierarchyType.USER );
    	target.setTargetBranch(new Branch());
    	hierarchyLocationManagementServiceImpl.relocateUser(new User(),target);
    }
    
    @Test (expected = InvalidInputException.class)
    public void testReloacteUserForNullCompany() throws SolrException, InvalidInputException
    {
    	HierarchyRelocationTarget target = new HierarchyRelocationTarget();
    	target.setHierarchyType( HierarchyType.USER );
    	target.setTargetBranch(new Branch());
    	target.setTargetRegion(new Region());
    	hierarchyLocationManagementServiceImpl.relocateUser(new User(),target);
    }
    
    @Test (expected = InvalidInputException.class)
    public void testReloacteUserForDefaultCompany() throws SolrException, InvalidInputException
    {
    	HierarchyRelocationTarget target = new HierarchyRelocationTarget();
    	target.setHierarchyType( HierarchyType.USER );
    	target.setTargetBranch(new Branch());
    	target.setTargetRegion(new Region());
    	Company comp = new Company();
    	comp.setCompanyId(1);
    	target.setTargetCompany(comp);
    	hierarchyLocationManagementServiceImpl.relocateUser(new User(),target);
    }
    
    
    @Test ( expected = InvalidInputException.class)
    public void testRelocateUserForSameCompany() throws SolrException, InvalidInputException
    {
    	User user = new User();
    	Company comp = new Company();
    	comp.setCompanyId(7);
    	user.setCompany(comp);
    	HierarchyRelocationTarget target = new HierarchyRelocationTarget();
    	target.setTargetCompany(comp);
    	target.setTargetRegion(new Region());
    	target.setTargetBranch(new Branch());
    	target.setHierarchyType( HierarchyType.USER );
    	hierarchyLocationManagementServiceImpl.relocateUser(user, target);
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testRelocateUserForRegionInSameCompany() throws SolrException, InvalidInputException
    {
    	User user = new User();
    	Company comp = new Company();
    	comp.setCompanyId(7);
    	user.setCompany(comp);
    	Region region = new Region();
    	region.setCompany(comp);
    	HierarchyRelocationTarget target = new HierarchyRelocationTarget();
    	target.setTargetCompany(new Company());
    	target.setTargetRegion(region);
    	target.setTargetBranch(new Branch());
    	target.setHierarchyType( HierarchyType.USER );
    	hierarchyLocationManagementServiceImpl.relocateUser(user, target);
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testRelocateUserForBranchInSameCompany() throws SolrException, InvalidInputException
    {
    	User user = new User();
    	Company comp = new Company();
    	comp.setCompanyId(7);
    	user.setCompany(comp);
    	Company companyForRegion = new Company();
    	companyForRegion.setCompanyId(8);
    	Region region = new Region();
    	region.setCompany(companyForRegion);
    	Branch branch = new Branch();
    	branch.setCompany(comp);
    	HierarchyRelocationTarget target = new HierarchyRelocationTarget();
    	target.setTargetCompany(new Company());
    	target.setTargetRegion(region);
    	target.setTargetBranch(branch);
    	target.setHierarchyType( HierarchyType.USER );
    	hierarchyLocationManagementServiceImpl.relocateUser(user, target);
    }
    
    @Test  ( expected = InvalidInputException.class)
    public void testRelocateUserForRegionNotInCompany() throws SolrException, InvalidInputException
    {
    	User user = new User();
    	Company comp = new Company();
    	comp.setCompanyId(5);
    	user.setCompany(comp);
    	Company company = new Company();
    	company.setCompanyId(7);
    	Company companyForRegion = new Company();
    	companyForRegion.setCompanyId(8);
    	Region region = new Region();
    	region.setCompany(companyForRegion);
    	Branch branch =new Branch();
    	branch.setCompany(companyForRegion);
    	HierarchyRelocationTarget target = new HierarchyRelocationTarget();
    	target.setTargetCompany(company);
    	target.setTargetRegion(region);
    	target.setTargetBranch(branch);
    	target.setHierarchyType( HierarchyType.USER );
    	hierarchyLocationManagementServiceImpl.relocateUser(user, target);
    }
    
    @Test  ( expected = InvalidInputException.class)
    public void testRelocateUserForBranchNotInRegion() throws SolrException, InvalidInputException
    {
    	User user = new User();
    	Company comp = new Company();
    	comp.setCompanyId(5);
    	user.setCompany(comp);
    	Company companyForRegion = new Company();
    	companyForRegion.setCompanyId(7);
    	Company companyOfBranch = new Company();
    	companyOfBranch.setCompanyId(8);
    	Region region = new Region();
    	region.setCompany(companyForRegion);
    	Branch branch = new Branch();
    	branch.setCompany(companyOfBranch);
    	HierarchyRelocationTarget target = new HierarchyRelocationTarget();
    	target.setTargetCompany(companyForRegion);
    	target.setTargetRegion(region);
    	target.setTargetBranch(branch);
    	target.setHierarchyType( HierarchyType.USER );
    	hierarchyLocationManagementServiceImpl.relocateUser(user, target);
    }
}
