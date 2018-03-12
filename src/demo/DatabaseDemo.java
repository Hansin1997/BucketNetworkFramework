package demo;

import java.util.List;

import bucket.database.Database;
import bucket.database.Mongo;
import bucket.database.Query;

/**
 * 数据库使用示例
 * 
 * @author Hansin
 *
 */
public class DatabaseDemo {

	public static void main(String[] args) throws Exception {

		Database db = new Mongo("localhost", 27017); // 创建Mongo数据库实例
		db.connect(); // 连接数据库

		db.useDb("myDB"); // 选择数据库，Mongo数据库不需事先创建数据库

		PhoneBook pb1, pb2, pb3, pb4;

		// 通过数据库对象创建PhoneBook实例
		pb1 = db.instantiate(PhoneBook.class);
		pb2 = db.instantiate(PhoneBook.class);
		pb3 = db.instantiate(PhoneBook.class);
		pb4 = db.instantiate(PhoneBook.class);

		pb1.name = "王思聪";
		pb1.nickname = "隔壁老王";
		pb1.phone = "123456";
		pb1.address = "隔壁";
		pb1.bytes = "asdasd".getBytes();
		pb1.save(); // 储存pb1
		
		System.out.println(pb1.toJSON());

		pb2.name = "马化腾";
		pb2.nickname = "马老西";
		pb2.phone = "55221";
		pb2.address = "深圳";
		pb2.save(); // 储存pb2

		pb3.name = "马云";
		pb3.nickname = "丑马";
		pb3.phone = "987456";
		pb3.address = "不知道在哪";
		pb3.save(); // 储存pb3

		pb4.name = "陈某某";
		pb4.phone = "0011200";
		pb4.QQ = "123456789";
		pb4.save(); // 储存pb4

		List<PhoneBook> pbs = db.find(PhoneBook.class, Query.build()); // 查找PhoneBook中所有记录

		for (PhoneBook pb : pbs) {
			pb.print(); // 输出信息
		}

		System.err.println("----------------------------------------------------");

		// 构造查询条件
		Query query = Query.build().like("name", "王思聪");

		pbs = db.find(PhoneBook.class, query); // 通过条件查询

		for (PhoneBook pb : pbs)
			pb.print(); // 输出信息

		db.close(); // 关闭数据库

	}

}
