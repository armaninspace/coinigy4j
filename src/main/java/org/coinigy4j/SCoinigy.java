package org.coinigy4j;

import java.io.IOException;

import org.json.JSONObject;

import com.m3.curly.*;

public class SCoinigy {
	
	public JSONObject joPriceTicker(String exchange_code, 
			String exchange_market) throws IOException {
		Response response = hardPost("https://api.coinigy.com/api/v1/ticker", exchange_code, exchange_market, null, attempts,timeOutMS,rethrow);
		String res=response.getTextBody(); 
		JSONObject jo = new JSONObject(res);
		return jo;
	}	
	
	public JSONObject joListExchanges() throws IOException {
		Response response = hardPost("https://api.coinigy.com/api/v1/exchanges", null, null, null,attempts,timeOutMS,rethrow);
		String res=response.getTextBody(); 
		JSONObject jo = new JSONObject(res);
		return jo;
	}
	
	public JSONObject joListMarkets(String exchange_code) throws IOException {
		Response response = hardPost("https://api.coinigy.com/api/v1/markets", exchange_code, null , null, attempts,timeOutMS,rethrow);
		String res=response.getTextBody(); 
		JSONObject jo = new JSONObject(res);
		return jo;
	}
	
	public JSONObject joListMarketData(String exchange_code, 
			String exchange_market, String type) throws IOException {
		Response response = hardPost("https://api.coinigy.com/api/v1/data", exchange_code, exchange_market , type, attempts,timeOutMS,rethrow);
		String res=response.getTextBody(); 
		//System.out.println(res);
		JSONObject jo = new JSONObject(res);
		return jo;
	}	
	
	public Response hardPost(String url, String exchange_code, String exchange_market, String type,
			int attempts, long timeOutMS,boolean rethrow) throws IOException {
		Request request=new Request(url); // https://api.coinigy.com/api/v1/markets"
		if (exchange_code != null || exchange_market != null || type != null) {
			JSONObject joReq = new JSONObject();
			if (exchange_code != null) {
				joReq.put("exchange_code", exchange_code);
			}
			if (exchange_market != null) {
				joReq.put("exchange_market", exchange_market);
			}
			if (type != null) {
				joReq.put("type", type);
			}
			request.setBody(joReq.toString().getBytes(), "application/json");
		} else {
			request.setBody(new byte[0], "application/json");
		}
		request.setHeader("X-API-KEY", X_API_KEY);
		request.setHeader("X-API-SECRET", X_API_SECRET);
		request.setHeader("Content-Type", "application/json");
		Response response = null;
		IOException returnIOE = null;
		for (int attempt=0; response ==null && attempt<attempts; attempt++) {
			long startTime = System.currentTimeMillis();
			try {
				returnIOE=null;
				response = HTTP.post(request);			
			} catch (IOException ioe) {
				returnIOE=ioe;
				System.err.println(ioe);
			}
			long endTime = System.currentTimeMillis();
			if (pl!=null)
				pl.writePerf(url, exchange_code, exchange_market, type, attempt, returnIOE, response, startTime, endTime);
			try {
				Thread.sleep(timeOutMS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (returnIOE!=null) throw(returnIOE);
		return response;			
	}
	
	public SCoinigy(String X_API_KEY, String X_API_SECRET, CoinigyPerfLogger pl) {
		this(X_API_KEY, X_API_SECRET, pl, 500, true, 3);
	}
	
	public SCoinigy(String X_API_KEY, String X_API_SECRET, CoinigyPerfLogger pl,
			long timeOutMS, boolean rethrow, int attempts) {
		this.X_API_KEY=X_API_KEY;
		this.X_API_SECRET=X_API_SECRET;		
		this.timeOutMS = timeOutMS;
		this.rethrow = rethrow;
		this.attempts = attempts;
		this.X_API_KEY=X_API_KEY;
		this.X_API_SECRET=X_API_SECRET;	
		this.pl=pl;
	}

	long timeOutMS;
	boolean rethrow;
	int attempts;
	CoinigyPerfLogger pl;

	String X_API_KEY;
	String X_API_SECRET;	
	
}
