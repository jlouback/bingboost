package bingboost;

/*
 * A Result is an individual item returned by a query. It will contain information
 * deemed important enough to include in our algorithms to find more relevant keywords.
 */

public class Result {
	
	String title;
	String description;
	int relevant;
	
	/*
	 * Store all text as lowercase for comparisons
	 */
	public Result(String title, String description, int relevant) {
		this.title = title.toLowerCase();
		this.description = description.toLowerCase();
		this.relevant = relevant;
	}
}
