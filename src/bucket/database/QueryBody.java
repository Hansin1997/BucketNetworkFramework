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
	protected QueryQueueBodyType queueType;
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

	public QueryQueueBodyType getQueueType() {
		return queueType;
	}

	public void setQueueType(QueryQueueBodyType queueType) {
		this.queueType = queueType;
	}

	/**
	 * 匹配条件类型
	 * 
	 * @author Hansin
	 *
	 */
	public enum QueryQueueBodyType {
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
		this.setQueueType(QueryQueueBodyType.EQU);

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
		this.setQueueType(QueryQueueBodyType.LES);

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
		this.setQueueType(QueryQueueBodyType.GRE);

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
		this.setQueueType(QueryQueueBodyType.LIKE);

		return this.head;
	}

	/**
	 * 获取条件符号
	 * 
	 * @return
	 */
	public String getSymbol() {
		switch (queueType) {
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

	/**
	 * 是否正序
	 * 
	 * @return 是否正序
	 */
	public boolean isSort() {
		return head.sort;
	}

	/**
	 * 设置正序
	 * 
	 * @param sort
	 *            是否正序
	 */
	public Query sort(boolean sort) {
		head.sort = sort;
		return head;
	}

	/**
	 * 设置表名
	 * 
	 * @param tableName
	 * @return
	 */
	public Query table(String tableName) {
		this.head.tableName = tableName;
		return head;
	}

}