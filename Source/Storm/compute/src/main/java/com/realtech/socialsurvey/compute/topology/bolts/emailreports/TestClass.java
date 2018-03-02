package com.realtech.socialsurvey.compute.topology.bolts.emailreports;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TestClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long startDate = 1514764800000L;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String startDateStr = sdf.format(new Date(startDate));
		System.out.println(startDateStr);
	}

}
