package org.coinigy4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;

import com.m3.curly.Response;

import au.com.bytecode.opencsv.CSVWriter;

public class CoinigyPerfLogger {
	
	private CSVWriter operf = null;
	public  void writePerf(String url, String exchange_code, String exchange_market, String type, int attempt, Exception exception, Response resp, long startTime, long endTime) throws IOException {
		if (operf == null) return;
		String line[] = new String [11];
		line[0]= (new Date(startTime)).toString();
		line[1]= "" + startTime; // "datems";
		line[2]= "-1"; // "lastknownping";
		line[3]= url; // "requestURL";
		line[4]= exchange_code; // "exchange_code";
		line[5]= exchange_market; // "exchange_market";
		line[6]= type ; // "type";
		line[7]= "" + (resp == null ? "" : resp.getStatus()) ; // "httpResponsCode";
		line[8]= "" + (endTime-startTime); // "responseTimeMS";		
		line[9]= "" + attempt; // "attempt"
		line[10]=  (exception == null ? "" : exception.toString()); // "exception"
		operf.writeNext(line);
		operf.flush();
	}
	
	public CoinigyPerfLogger(String logfpath) {
		String header[] = new String [11];
		header[0]="date";
		header[1]="datems";
		header[2]="lastknownping";
		header[3]="requestURL";
		header[4]="exchange_code";
		header[5]="exchange_market";
		header[6]="type";
		header[7]="httpResponsCode";
		header[8]="responseTimeMS";		
		header[9]= "attempt"; // "attempt"
		header[10]= "exception"; // "exception"
		try {
			operf = new CSVWriter(new FileWriter(new File(logfpath + "/perf_" + System.currentTimeMillis() + ".csv")));
			operf.writeNext(header);
			operf.flush();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void byebye() {
		try {
			if (operf==null) {
				operf.flush();
				operf.close();				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


}
