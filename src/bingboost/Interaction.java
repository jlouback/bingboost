package bingboost;

import java.io.*;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Interaction {

	private BingBoost boost;
	private String key;
	private float precision;
	private String query;
	private int k;
	private Result[] results;

	public Interaction(String key, float precision, String query) throws FileNotFoundException, IOException {
		this.key = key;
		this.boost = new BingBoost();
		this.precision = precision;
		this.query = query;
		this.k = 10;
		results = new Result[k];
	}

	public float currentPrecision() {
		int relevant = 0;
		for (int i=0; i< results.length; i++) {
			relevant += results[i].relevant;
		}
		return (float)relevant / (float)k;
	}

	public String exitMessage() {
		float current = currentPrecision();
		if (current == 0) {
			return "Search engine has failed to meet desired precision. Boooo.";
		}
		return "Search engine reached desired precision level. Yay.";
	}

	/*
	 * Show the results to the user and collect relevant/irrelevant feedback for each.
	 */
	public void queryAndCollectFeedback() {
		Scanner in = new Scanner(System.in);
		try {
			JSONArray jsonResults = Utils.queryBing(key, query, k);
			int n = (jsonResults.length() < k) ? jsonResults.length() : k;
			System.out.println("Total no of results : " + n);
			System.out.println("Bing Search Results:");
			System.out.println("======================");
			for (int i=0; i<k; i++) {
				JSONObject json = jsonResults.getJSONObject(i);

				System.out.println("Result " + (i+1));
				System.out.println("[");
				System.out.println(" " + json.get("Url"));
				System.out.println(" " + json.get("Title"));
				System.out.println(" " + json.get("Description"));
				System.out.println("]\n");
				System.out.print("Relevant (Y/N)? ");

				int relevant = Utils.relevanceValueForString(in.nextLine());
				results[i] = new Result(json.getString("Title"), json.getString("Description"), relevant);
			}
		} catch (Exception e) {
			if (e instanceof IOException | e instanceof JSONException)
				e.printStackTrace();
		}
		
		System.out.println("======================");
		System.out.println("FEEDBACK SUMMARY");
		System.out.println("Query " + query);
		System.out.println("Precision " + currentPrecision());
	}

	/*
	 * Continue to enhance the query while the designed precision level has not been reached and
	 * the current precision is greater than 0. If current precision equals 0, stop the process 
	 * because we don't have relevant results to work with.
	 */
	public void runBingboost() {
		do {
			printParameters();
			queryAndCollectFeedback();
			if (currentPrecision() < precision && currentPrecision() > 0) {
				System.out.println("Still below the desired precision of " + precision);
				query = boost.updatedQueryForFeedback(query, results);
			}
		} while (currentPrecision() < precision && currentPrecision() > 0);
		System.out.println(exitMessage());
	}

	private void printParameters() {
		System.out.println("Parameters:");
		System.out.println("Client key = " + key);
		System.out.println("Query      = " + query);
		System.out.println("Precision  = " + precision);
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String key = args[0];
		float precision = Float.valueOf(args[1]);
		String query = args[2];
		
		Interaction interaction = new Interaction(key, precision, query);
		interaction.runBingboost();	
	}
}
