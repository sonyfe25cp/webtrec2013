package edu.bit.dlde.feature;

/**
 * 已经由rankmodel替代
 * 保存查询以及处理过后的一些结果,里面的各种方法最好实现cache机制，以便多次调用的速度
 * 
 * @author lins
 * @date 2012-7-30
 **/
@Deprecated
public class ProcessedQuery {
	private String rawQuery;

	public ProcessedQuery(String rawQuery) {
		this.rawQuery = rawQuery;
	}

	/**
	 * @return 返回原始的查询
	 */
	public String getRawQuery() {
		return rawQuery;
	}

	public String[] getTerms() {
		return rawQuery.split(" ");
	}

	/**
	 * 3级查询扩展。0等价；1近似；2相关
	 */
	String[][] expandQuery = new String[3][5];

	/**
	 * @param prior
	 *            扩展后查询的优先级。0等价；1近似；2相关
	 * @param eQuery
	 *            扩展查询的字符串数组
	 */
	public void setExpanded(int prior, String... eQuery) {
		int min = Math.min(eQuery.length, expandQuery[0].length);
		for (int i = 0; i < min; i++) {
			expandQuery[prior][i] = eQuery[i];
		}
	}

}
