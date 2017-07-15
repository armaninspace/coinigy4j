package org.coinigy4j;

public final class Entry {
	public String type;
	public String exchange;
	public String market;
	public double total;
	public double price;
	public double quantity;
	public String ours="";

	public Entry(String type, String exchange, String market, double total, double price, double quantity) {
		this.type = type;
		this.exchange = exchange;
		this.market = market;
		this.total = total;
		this.quantity = quantity;
		this.price = price;
	}

	static public void printhln() {
		String fstr = "%1$-10s %2$-10s %3$-10s %4$-10s %5$-10s %6$-10s \n";
		System.out.printf(fstr, "type", "exchange", "market", "total", "quantity", "price");
	}

	public void println() {
		String fstr = "%1$-10s %2$-10s %3$-10s %4$-10.2f %5$-10.2f %6$-10.2f %7$-10s\n";
		System.out.printf(fstr, this.type, this.exchange, this.market, this.total, this.quantity, this.price, ours);
	}
}

