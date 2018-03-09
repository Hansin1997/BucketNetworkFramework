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
	 * 条件队列
	 */
	protected QueryQueue queue;

	/**
	 * 要查询的表名
	 */
	protected String tableName;

	public Query(QueryQueue queue) {
		setQueue(queue);
	}

	public static QueryBody build() {
		QueryQueue head = new QueryQueue();
		head.body = new QueryBody(new Query(head));
		return head.body;
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
	 * 设置表名
	 * 
	 * @param tableName
	 * @return
	 */
	public Query table(String tableName) {
		this.tableName = tableName;
		return this;
	}

	/**
	 * 获取表名
	 * 
	 * @return
	 */
	public String table() {
		return tableName;
	}

	public void setQueue(QueryQueue queue) {
		this.queue = queue;
	}

	public QueryQueue getQueue() {
		return queue;
	}

	/**
	 * 且
	 * 
	 * @return
	 */
	public QueryBody and() {
		return this.queue.and();
	}

	/**
	 * 或
	 * 
	 * @return
	 */
	public QueryBody or() {
		return this.queue.or();
	}

	/**
	 * 输出调试信息
	 */
	public void print() {
		System.out.println("sort:\t" + isSort());
		System.out.print("query:\t");

		QueryQueue it = this.queue;
		while (true) {

			if (it.body == null)
				break;

			System.out.print(it.body.getKey());
			System.out.print(it.body.getSymbol());
			System.out.print(it.body.getValue());

			if (it.next != null)
				System.out.print(" " + it.getSymbol() + " ");
			else
				break;
			it = it.next;
		}
		System.out.println();
	}

}
