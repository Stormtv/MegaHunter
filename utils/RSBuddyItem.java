package scripts.MegaHunter.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RSBuddyItem {
	private int overall;
	private int buying;
	private int buyingQuantity;
	private int selling;
	private int sellingQuantity;
	private int id;

	public RSBuddyItem(int myId) {
		JSONObject Item = null;
		try {
			Item = readJsonFromUrl("http://api.rsbuddy.com/grandExchange?a=guidePrice&i="
					+ myId);
			try {
				this.id = myId;
				this.overall = Item.getInt("overall");
				this.buying = Item.getInt("buying");
				this.buyingQuantity = Item.getInt("buyingQuantity");
				this.selling = Item.getInt("selling");
				this.sellingQuantity = Item.getInt("sellingQuantity");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void refreshItem(RSBuddyItem i) {
		JSONObject Item = null;
		try {
			Item = readJsonFromUrl("http://api.rsbuddy.com/grandExchange?a=guidePrice&i="
					+ i.getId());
			try {
				i.setOverall(Item.getInt("overall"));
				i.setBuying(Item.getInt("buying"));
				i.setBuyingQuantity(Item.getInt("buyingQuantity"));
				i.setSelling(Item.getInt("selling"));
				i.setSellingQuantity(Item.getInt("sellingQuantity"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static JSONObject readJsonFromUrl(String url) throws IOException,
			JSONException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url)
				.openConnection();
		connection
				.setRequestProperty(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11");
		InputStreamReader is = new InputStreamReader(
				connection.getInputStream());
		try {
			BufferedReader rd = new BufferedReader(is);
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}
	
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public int getOverall() {
		return overall;
	}

	public void setOverall(int overall) {
		this.overall = overall;
	}

	public int getBuying() {
		return buying;
	}

	public void setBuying(int buying) {
		this.buying = buying;
	}

	public int getBuyingQuantity() {
		return buyingQuantity;
	}

	public void setBuyingQuantity(int buyingQuantity) {
		this.buyingQuantity = buyingQuantity;
	}

	public int getSelling() {
		return selling;
	}

	public void setSelling(int selling) {
		this.selling = selling;
	}

	public int getSellingQuantity() {
		return sellingQuantity;
	}

	public void setSellingQuantity(int sellingQuantity) {
		this.sellingQuantity = sellingQuantity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
