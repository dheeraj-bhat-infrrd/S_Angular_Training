package com.realtech.socialsurvey.solr.Transformers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class UserTransformer
{
    public Object transformRow( Map<String, Object> row )
    {
        System.out.println( "Entered UserTransformer" );
        System.out.println( "Row : " + row.toString() );
        //Set isAgent, isRegionAdmin and isBranchAdmin values based profiles master ids
        String profileMasterIdStr = (String) row.get( "profiles_master_ids" );
        if ( profileMasterIdStr == null || profileMasterIdStr.isEmpty() ) {
            System.out.println( "profileMasterIdStr is empty" );
        } else {
            System.out.println( "profileMasterIdStr : " + profileMasterIdStr );
            String[] pmiArray = profileMasterIdStr.split( "," );
            int profileMasterIds[] = new int[pmiArray.length];
            boolean isAgent = false, isBranchAdmin = false, isRegionAdmin = false;
            for ( int i = 0; i < pmiArray.length; i++ ) {
                profileMasterIds[i] = Integer.parseInt( pmiArray[i] );
                System.out.println( "profileMasterIds[" + i + "] : " + profileMasterIds[i] );
                switch ( profileMasterIds[i] ) {
                    case 2:
                        isRegionAdmin = true;
                        break;
                    case 3:
                        isBranchAdmin = true;
                        break;
                    case 4:
                        isAgent = true;
                        break;
                }
                row.put( "isAgent", isAgent );
                row.put( "isBranchAdmin", isBranchAdmin );
                row.put( "isRegionAdmin", isRegionAdmin );
            }
        }
        //Change regions and branches to an array of long
        //For regions
        String regionsStr = (String) row.get( "regions" );
        System.out.println( "Regions : " + regionsStr );
        if ( regionsStr == null || regionsStr.isEmpty() ) {
            System.out.println( "regions is empty" );
        } else {
            String[] regionsStrArray = regionsStr.split( "," );
            List<Long> regions = new ArrayList<Long>();
            for ( int i = 0; i < regionsStrArray.length; i++ ) {
                regions.add( Long.parseLong( regionsStrArray[i] ) );
                System.out.println( "regions[" + i + "] = " + regions.get( i ) );
            }
            System.out.println( "regions : " + regions );
            row.put( "regions", regions );
        }

        //For branches
        String branchesStr = (String) row.get( "branches" );
        System.out.println( "Branches : " + branchesStr );
        if ( branchesStr == null || branchesStr.isEmpty() ) {
            System.out.println( "branches is empty" );
        } else {
            String[] branchesStrArray = branchesStr.split( "," );
            List<Long> branches = new ArrayList<Long>();
            for ( int i = 0; i < branchesStrArray.length; i++ ) {
                branches.add( Long.parseLong( branchesStrArray[i] ) );
                System.out.println( "branches[" + i + "] = " + branches.get( i ) );
            }
            row.put( "branches", branches );
        }

        //Set Display Name
        String firstName = (String) row.get( "FIRST_NAME" );
        if ( firstName != null && !firstName.isEmpty() ) {
            String displayName = firstName;
            String lastName = (String) row.get( "LAST_NAME" );
            if ( lastName != null && !lastName.isEmpty() ) {
                displayName += " " + lastName;
            }
            row.put( "displayName", displayName );
            System.out.println( "displayName : " + displayName );
        } else {
            System.out.println( "displayName is empty" );
        }
        return row;
    }
}
