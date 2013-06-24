import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryOccurCounter {
	
	/**
	 * 计算某一查询在某一文档内的出现次数，term不得大于maxSpan
	 * 
	 * @param doc
	 *            文档字符串
	 * @param query
	 *            由一个个term组成的查询字符串
	 * @param maxSpan
	 *            term直接最大间隔的单词数
	 * @param isCaseInsensitive
	 *            true为大小写不敏感
	 * @return 查询按照规定在文档中的出现次数
	 */
	public static int getCount(String doc, String query, int maxSpan,
			boolean isCaseInsensitive) {
		String[] terms = query.split(" ");

		// 构建匹配的正则
		StringBuilder regexBuilder = new StringBuilder();
		for (int i = 0; i < terms.length; i++) {
			regexBuilder.append(terms[i]);
			regexBuilder.append(" ");
			if (maxSpan > 0 && i != terms.length - 1)
				regexBuilder.append(String.format("([^.?!\\s]+\\s){0,%d}",
						maxSpan));
		}
		// 构建pattern
		Pattern pattern = null;
		if (isCaseInsensitive)
			pattern = Pattern.compile(regexBuilder.toString().trim(),
					Pattern.CASE_INSENSITIVE);
		else
			pattern = Pattern.compile(regexBuilder.toString().trim());

		// 计数
		Matcher m = pattern.matcher(doc);
		int count = 0;
		while (m.find()) {
			count++;
			// System.out.println("Matcher found: "+m.group());
		}

		return count;
	}

	public static void main(String[] args) {
		String doc = "this is a test . a good test . a bad test !!!! an test? such a goood baddddd test .";
		String query = "a tEst";
		int i = QueryOccurCounter.getCount(doc, query, 1, true);
		System.out.println(i);

	}
}
