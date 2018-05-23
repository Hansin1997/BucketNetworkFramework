package bucket.database.query;

/**
 * 条件队列类
 * 
 * @author Hansin
 *
 */
public class QueryQueue {

	/**
	 * 节点体
	 */
	protected QueryBody body;

	/**
	 * 条件类型
	 * 
	 * @author Hansin
	 *
	 */
	public enum QueryType {
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
	 * 节点条件类型
	 */
	protected QueryType queryType;

	/**
	 * 下一个节点
	 */
	protected QueryQueue next;

	public QueryQueue() {

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
	 * 获取下一个查询条件
	 * 
	 * @return
	 */
	public QueryQueue getNext() {
		return next;
	}

	/**
	 * 设置下一个节点
	 * 
	 * @param next
	 */
	public void setNext(QueryQueue next) {
		this.next = next;
	}

	/**
	 * 设置条件体
	 * 
	 * @param body
	 *            条件体
	 */
	public QueryQueue setBody(QueryBody body) {
		this.body = body;
		return this;
	}

	/**
	 * 获取查询条件类型
	 * 
	 * @return
	 */
	public QueryType getQueryType() {
		return queryType;
	}

	/**
	 * 且
	 * 
	 * @return
	 */
	public QueryBody and() {
		QueryQueue last = findLast(this);
		last.queryType = QueryType.AND;
		last.next = new QueryQueue();
		last.next.body = new QueryBody(this.body.head);
		return last.next.body;
	}

	/**
	 * 或
	 * 
	 * @return
	 */
	public QueryBody or() {
		QueryQueue last = findLast(this);
		last.queryType = QueryType.OR;
		last.next = new QueryQueue();
		last.next.body = new QueryBody(this.body.head);
		return last.next.body;
	}

	/**
	 * 寻找链表最后一个节点
	 * 
	 * @param head
	 *            头节点
	 * @return
	 */
	public static QueryQueue findLast(QueryQueue head) {
		QueryQueue it = head;
		while (it.next != null)
			it = it.next;
		return it;
	}

	/**
	 * 获取条件符号
	 * 
	 * @return
	 */
	public String getSymbol() {
		switch (queryType) {
		case AND:
			return "AND";
		case OR:
			return "OR";
		}
		return "";
	}

}
