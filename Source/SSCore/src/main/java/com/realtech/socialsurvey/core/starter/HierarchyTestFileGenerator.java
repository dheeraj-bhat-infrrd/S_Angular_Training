package com.realtech.socialsurvey.core.starter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HierarchyTestFileGenerator {

	private static Logger LOG = LoggerFactory.getLogger(HierarchyTestFileGenerator.class);
	private File file;
	private String path = "/Users/nishit/work/Social_Survey/";
	
	private static int NUM_OF_REGIONS = 10;
	private static int REGIONS_START_NUM = 1;
	private static int NUM_OF_BRANCHES_PER_ORGANIZATION_UNIT = 10;
	private static int BRANCHES_START_NUM = 1;
	private static int NUM_OF_USERS_PER_ORGANIZATION_UNIT = 500;
	private static int USERS_START_NUM = 18153;
	private static int NUM_OF_ADMIN_PER_ORGANIZATION_UNIT = 1;
	private List<String> regionNames = new ArrayList<String>();
	private List<String> branchNames = new ArrayList<String>();
	private List<String> userNames = new ArrayList<String>();
	
	public void createFile() throws IOException{
		file = new File(path+"testhierarchy.txt");
		if(!file.exists()){
			file.createNewFile();
			LOG.info("Created file "+file.getAbsolutePath());
		}
	}
	
	public void writeHierarchy() throws IOException{
		createFile();
		LOG.debug("Writing into file now.");
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(file);
			addRegions(printWriter);
			addBranches(printWriter);
			addUsers(printWriter);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			if(printWriter != null){
				printWriter.close();
			}
		}
	}
	
	private void addRegions(PrintWriter printWriter){
		LOG.debug("Adding regions header");
		printWriter.println("##Regions");
		LOG.debug("Adding regions");
		for(int counter = REGIONS_START_NUM; counter < (REGIONS_START_NUM+NUM_OF_REGIONS);counter++){
			printWriter.println("Region"+counter+"\tAdd1");
			regionNames.add("Region"+counter);
		}
	}
	
	private void addBranches(PrintWriter printWriter){
		LOG.debug("Adding branches header.");
		printWriter.println("##Branches");
		LOG.debug("Adding branches under company");
		addBranchesUnderCompany(printWriter);
		for(String region: regionNames){
			addBranchesUnderRegion(printWriter, region);
		}
	}
	
	private void addBranchesUnderCompany(PrintWriter printWriter){
		LOG.debug("Adding branches under the company");
		for(int counter = BRANCHES_START_NUM; counter < (BRANCHES_START_NUM+NUM_OF_BRANCHES_PER_ORGANIZATION_UNIT); counter++){
			printWriter.println("Branch"+counter+"\tAdd1\t#Company");
			branchNames.add("Branch"+counter);
		}
	}
	
	private void addBranchesUnderRegion(PrintWriter printWriter, String regionName){
		LOG.debug("Adding branches under Region: "+regionName);
		int startIndex = BRANCHES_START_NUM+branchNames.size();
		int endIndex = BRANCHES_START_NUM+branchNames.size()+NUM_OF_BRANCHES_PER_ORGANIZATION_UNIT;
		for(int counter=startIndex; counter < endIndex; counter++){
			printWriter.println("Branch"+counter+"\tAdd1\t#"+regionName);
			branchNames.add("Branch"+counter);
		}
	}
	
	private void addUsers(PrintWriter printWriter){
		LOG.debug("Adding users header");
		printWriter.println("##Users");
		addUsersUnderCompany(printWriter);
		for(String region : regionNames){
			addUsersUnderOrganizationUnit(printWriter, "region", region);
		}
		for(String branch : branchNames){
			addUsersUnderOrganizationUnit(printWriter, "branch", branch);
		}
	}
	
	private void addUsersUnderCompany(PrintWriter printWriter){
		LOG.debug("Adding users under company");
		for(int counter = USERS_START_NUM; counter < (USERS_START_NUM+NUM_OF_USERS_PER_ORGANIZATION_UNIT); counter++){
			printWriter.println("User"+counter+"\tnishit+"+counter+"@raremile.com\t#Company");
			userNames.add("User"+counter);
		}
	}
	
	private void addUsersUnderOrganizationUnit(PrintWriter printWriter, String organizationUnit, String organizationUnitName){
		LOG.debug("Adding users under "+organizationUnit+": "+organizationUnitName);
		int numOfAdmins = 0;
		int startIndex = USERS_START_NUM + userNames.size();
		int endIndex = USERS_START_NUM+userNames.size()+NUM_OF_USERS_PER_ORGANIZATION_UNIT;
		for(int counter = startIndex; counter < endIndex; counter++){
			if(numOfAdmins < NUM_OF_ADMIN_PER_ORGANIZATION_UNIT){
				printWriter.println("User"+counter+"\tnishit+"+counter+"@raremile.com\t#"+organizationUnit+"\t#"+organizationUnitName+"\t#admin");
				numOfAdmins++;
			}else{
				printWriter.println("User"+counter+"\tnishit+"+counter+"@raremile.com\t#"+organizationUnit+"\t#"+organizationUnitName);
			}
			userNames.add("User"+counter);
		}
	}
	
	public static void main(String[] args){
		HierarchyTestFileGenerator generator = new HierarchyTestFileGenerator();
		try {
			generator.writeHierarchy();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
