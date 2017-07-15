package org.coinigy4j.examples;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.coinigy4j.SCoinigy;
import org.json.JSONArray;
import org.json.JSONObject;

import au.com.bytecode.opencsv.CSVWriter;

public class GetMarketData {
	public static void main (String args[]) throws Exception{
		openBidsFile();
		openAsksFile();
		SCoinigy sc = new SCoinigy("put-your-X_API_KEY-here", "put-your-X_API_SECRET-here", null);
		JSONObject exchanges=sc.joListExchanges();
		//String exchangecodes[]=getExchangeCodes(exchanges);
		String exchangecodes[]= {"BTER", "OK", "BITS", "BTCE"};
		for (int j=0; j<exchangecodes.length ; j++) {
			String exchange_code=exchangecodes[j];
			JSONObject markets = sc.joListMarkets(exchange_code);
			String marketNames[]=getMarketNames(markets);
			Thread.sleep(300);
			for (int i=0; i<marketNames.length ; i++) {
				Thread.sleep(300);
				String market=marketNames[i];
				if (!market.contains("BTC")) continue;
				JSONObject jo = sc.joListMarketData(exchange_code, market, "all");
				processMarketData(jo, exchange_code, market);
				System.out.println(j + " processed " + exchange_code + " || " + market);
			}
		}
		closeAsksFile();
		closeBidsFile();
//		sc.byebye();
	}
	
	public static String[] getExchangeCodes(JSONObject jo) {
		JSONArray jaData=jo.getJSONArray("data");
		String res[] = new String[jaData.length()];
		for (int i=0; i<jaData.length(); i++) {
			res[i]=jaData.getJSONObject(i).getString("exch_code");
		}
		return res;
	}
	public static String[] getMarketNames(JSONObject jo) {
		JSONArray jaData=jo.getJSONArray("data");
		String res[] = new String[jaData.length()];
		for (int i=0; i<jaData.length(); i++) {
			res[i]=jaData.getJSONObject(i).getString("mkt_name");
		}
		return res;
	}
	
	public static void processMarketData(JSONObject jo, String exchange_code, 
			String exchange_market) throws IOException {
		JSONArray asks=jo.getJSONObject("data").getJSONArray("asks");
		JSONArray bids=jo.getJSONObject("data").getJSONArray("bids");
		JSONArray history=jo.getJSONObject("data").getJSONArray("history");
		processAsks(asks, exchange_code, exchange_market);
		processBids(bids, exchange_code, exchange_market);
//		processHistory(history, exchange_code, exchange_market);
	}
	
	public static void processAsks(JSONArray ja, String exchange_code, 
			String exchange_market) throws IOException {
		for (int i=0; i<ja.length(); i++) {
			JSONObject jo = ja.getJSONObject(i);
			double total = jo.getDouble("total");
			double quantity = jo.getDouble("quantity");
			double price = jo.getDouble("price");
			String hash=mkSig(total+"|" + quantity+ "|" + price);
			String row[]= {exchange_code,exchange_market,
					""+total,""+quantity,""+price,hash};
			cwasks.writeNext(row);
		}
		cwasks.flush();
	}

	public static void processBids(JSONArray ja, String exchange_code, 
			String exchange_market) throws IOException {
		for (int i=0; i<ja.length(); i++) {
			JSONObject jo = ja.getJSONObject(i);
			double total = jo.getDouble("total");
			double quantity = jo.getDouble("quantity");
			double price = jo.getDouble("price");
			String hash=mkSig(total+"|" + quantity+ "|" + price);
			String row[]= {exchange_code,exchange_market,
					""+total,""+quantity,""+price,hash};
			cwbids.writeNext(row);
		}
		cwbids.flush();
	}

	public static String mkSig(String txt) {
		return DigestUtils.md5Hex(txt);
	}
	
	static CSVWriter cwbids = null;
	public static void openBidsFile() throws IOException {
		String header[]= {"exchange_code","exchange_market",
				"total","quantity","price","hash"};
		cwbids = new CSVWriter(new FileWriter(new File("data/bids_" + System.currentTimeMillis() + ".csv")));
		cwbids.writeNext(header);
	}
	public static void closeBidsFile() throws IOException {
		cwbids.flush();
		cwbids.close();
		cwbids=null;
	}

	static CSVWriter cwasks = null;
	public static void openAsksFile() throws IOException {
		String header[]= {"exchange_code","exchange_market",
				"total","quantity","price","hash"};
		cwasks = new CSVWriter(new FileWriter(new File("data/asks_" + System.currentTimeMillis() + ".csv")));
		cwasks.writeNext(header);
	}
	public static void closeAsksFile() throws IOException {
		cwasks.flush();
		cwasks.close();
		cwasks=null;
	}
}

// DigestUtils.md5Hex(str);

