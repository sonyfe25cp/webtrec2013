package lucene;

/**
 * 查询的模型
 * 
 * @author ChenJie
 * 
 */
public class QueryModel {

	private int id;
	private String query;
	private String queryExpan;

	public QueryModel(int id, String query, String queryExpan) {
		super();
		this.id = id;
		this.query = query;
		this.queryExpan = queryExpan;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQueryExpan() {
		return queryExpan;
	}

	public void setQueryExpan(String queryExpan) {
		this.queryExpan = queryExpan;
	}

	public String[] getTerms() {
		return query.split(" ");
	}
}
