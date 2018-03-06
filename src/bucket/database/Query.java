package bucket.database;

/**
 * 查询条件类
 * 
 * @author Hansin
 *
 */
public class Query {

	/**
	 * 顺序，真为正序，假为反序
	 */
	protected boolean sort;

	/**
	 * 条件体
	 */
	protected QueryBody body;

	/**
	 * 条件类型
	 * 
	 * @author Hansin
	 *
	 */
	protected enum QueryType {
		/**
		 * 且
		 */
		AND,
		/**
		 * 或
		 */
		OR
	}

	/**
	 * 条件类型
	 */
	protected QueryType queryType;

	/**
	 * 下一个查询条件目标
	 */
	protected Query target;

	public Query() {
		sort(true);
	}

	public static QueryBody build() {
		Query head = new Query();
		head.body = new QueryBody(head);
		return head.body;
	}

	/**
	 * 获取条件体
	 * 
	 * @return
	 */
	public QueryBody getBody() {
		return body;
	}

	/**
	 * 是否正序
	 * 
	 * @return 是否正序
	 */
	public boolean isSort() {
		return sort;
	}

	/**
	 * 设置正序
	 * 
	 * @param sort
	 *            是否正序
	 */
	public Query sort(boolean sort) {
		this.sort = sort;
		return this;
	}

	/**
	 * 设置条件体(这将重置条件体的头指针)
	 * 
	 * @param body
	 *            条件体
	 */
	public Query setBody(QueryBody body) {
		this.body = body;
		body.head = this;
		return this;
	}

	/**
	 * 且
	 * 
	 * @return
	 */
	public QueryBody and() {
		Query last = findLast(this);
		last.queryType = QueryType.AND;
		last.target = new Query();
		last.target.body = new QueryBody(this.body.head);
		return last.target.body;
	}

	/**
	 * 或
	 * 
	 * @return
	 */
	public QueryBody or() {
		Query last = findLast(this);
		last.queryType = QueryType.OR;
		last.target = new Query();
		last.target.body = new QueryBody(this.body.head);
		return last.target.body;
	}

	/**
	 * 输出调试信息
	 */
	public void print() {
		System.out.println("sort:\t" + isSort());
		System.out.print("query:\t");
		
		Query it = this;
		while (true) {

			if (it.body == null)
				break;

			System.out.print(it.body.getKey());
			switch (it.body.getQueryType()) {
			case EQU:
				System.out.print("=");
				break;
			case LES:
				System.out.print("<");
				break;
			case GRE:
				System.out.print(">");
				break;
			case LIKE:
				System.out.print("~");
				break;
			}
			System.out.print(it.body.getValue());

			
			if (it.target != null)
				System.out.print(it.queryType == QueryType.AND ? " AND " : " OR ");
			else
				break;
			it = it.target;
		}
		System.out.println();
	}

	public static Query findLast(Query head) {
		Query it = head;
		while (it.target != null)
			it = it.target;
		return it;
	}
}
