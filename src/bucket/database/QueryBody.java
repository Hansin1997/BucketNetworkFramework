package bucket.database;

/**
 * 条件类
 * 
 * @author Hansin
 *
 */
public class QueryBody {

	/**
	 * 匹配条件
	 */
	protected QueryBodyType queryType;
	/**
	 * 键
	 */
	protected String key;
	/**
	 * 值
	 */
	protected Object value;

	/**
	 * 链表头
	 */
	protected Query head;

	public QueryBody(Query head) {
		this.head = head;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	public void setQueryType(QueryBodyType queryType) {
		this.queryType = queryType;
	}

	public QueryBodyType getQueryType() {
		return queryType;
	}

	/**
	 * 匹配条件类型
	 * 
	 * @author Hansin
	 *
	 */
	public enum QueryBodyType {
		/**
		 * 等于
		 */
		EQU,
		/**
		 * 大于
		 */
		GRE,
		/**
		 * 小于
		 */
		LES,
		/**
		 * 近似
		 */
		LIKE
	};

	/**
	 * 等于
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Query equ(String key, Object value) {
		this.setKey(key);
		this.setValue(value);
		this.setQueryType(QueryBodyType.EQU);

		return this.head;
	}

	/**
	 * 小于
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Query les(String key, Object value) {
		this.setKey(key);
		this.setValue(value);
		this.setQueryType(QueryBodyType.LES);

		return this.head;
	}

	/**
	 * 大于
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Query gre(String key, Object value) {
		this.setKey(key);
		this.setValue(value);
		this.setQueryType(QueryBodyType.GRE);

		return this.head;
	}

	/**
	 * 近似
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Query like(String key, Object value) {
		this.setKey(key);
		this.setValue(value);
		this.setQueryType(QueryBodyType.LIKE);

		return this.head;
	}

	/**
	 * 获取条件符号
	 * 
	 * @return
	 */
	public String getSymbol() {
		switch (queryType) {
		case GRE:
			return ">";
		case LES:
			return "<";
		case EQU:
			return "=";
		case LIKE:
			return " LIKE ";
		}
		return "???";
	}
}