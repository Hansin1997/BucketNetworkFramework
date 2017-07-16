package Common;



import java.lang.reflect.Field;

import java.util.ArrayList;

import java.util.Iterator;

import java.util.List;

import java.util.Map.Entry;



import com.google.gson.Gson;

import com.google.gson.GsonBuilder;

import com.google.gson.JsonArray;

import com.google.gson.JsonElement;

import com.google.gson.JsonObject;

import com.google.gson.JsonParseException;

import com.google.gson.JsonSyntaxException;



import network.bucketobject.Data;

import network.bucketobject.Table;



public class Tool {

	static public Table object2Table(Object o) {

		Table table = new Table();

		List<Data> values = new ArrayList<Data>();



		Class<?> clazz = o.getClass();

		Field[] F = clazz.getFields();



		table.setTable_name(clazz.getSimpleName());



		for (int i = 0; i < F.length; i++) {

			Field f = F[i];



			Data d = new Data();

			d.setKey(f.getName());

			d.setValue(typeFormat(f.getType().getSimpleName()));

			values.add(d);

		}



		table.setValues(values);

		return table;

	}



	static public String table2JSON(Table table) {

		Gson gson = new GsonBuilder().create();

		String result = "";

		try {

			result = gson.toJson(table);

		} catch (JsonParseException e) {



		}

		return result;

	}



	static public Table JSON2Table(String json) {

		Gson gson = new GsonBuilder().create();

		Table table = null;

		try {

			table = gson.fromJson(json, Table.class);

		} catch (JsonParseException e) {



		}



		return table;



	}



	static public String table2SQL(Table table) {



		String mid = "";



		for (int i = 0; i < table.getValues().size(); i++) {

			Data d = table.getValues().get(i);

			mid += d.getKey() + " " + d.getValue();

			if (i < table.getValues().size() - 1)

				mid += ",";

		}



		return "CREATE TABLE " + table.getTable_name() + " (" + mid + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";



	}



	public static String arrayInsert2SQL(String table_name, JsonArray array) {



		String head = "INSERT INTO " + table_name + " ";

		String key = "";

		String mid = "";

		String tmp = "";



		for (int i = 0; i < array.size(); i++) {

			JsonObject obj = array.get(i).getAsJsonObject();

			Iterator<Entry<String, JsonElement>> it = obj.entrySet().iterator();

			tmp = "";

			while (it.hasNext()) {



				Entry<String, JsonElement> d = it.next();



				tmp += d.getValue();

				if (i == 0) {

					key += d.getKey();

				}

				if (it.hasNext()) {

					tmp += ",";

					if (i == 0) {

						key += ",";

					}

				}



			}

			mid += "(" + tmp + ")";



			if (i < array.size() - 1) {

				mid += ",";

			}

		}



		return head + "(" + key + ") VALUES " + mid + ";";

	}

	



	static final String[][] TypeFormat = { { "String", "text" }, { "boolean", "tinyint(1)" }, { "Date", "timestamp" } ,{ "IncrementalChange" ,""}};



	static String typeFormat(String in) {



		for (String[] t : TypeFormat) {

			try {

				if (in.equals(t[0])) {

					return t[1];

				}

			} catch (IndexOutOfBoundsException e) {



			}

		}



		return in;

	}



	static public String toJson(Object o) {

		Gson gson = new GsonBuilder().create();

		return gson.toJson(o);

	}



	@SuppressWarnings("unchecked")

	static public <E> E object2E(Object object, Class<?> clazz) {

		Gson gson = new GsonBuilder().create();



		return (E) gson.fromJson(gson.toJson(object), clazz);

	}



	static public Object JSON2Object(String json) {

		Gson gson = new GsonBuilder().create();



		return gson.fromJson(json, Object.class);

	}



	@SuppressWarnings("unchecked")

	static public <E> E JSON2E(String json, Class<?> clazz) {



		Gson gson = new GsonBuilder().create();

		E re = null;

		try{

			re = (E) gson.fromJson(json, clazz);

		}catch(JsonSyntaxException e)

		{

			

		}

		return re;

	}



	@SuppressWarnings("unchecked")

	static public <T> List<T> ObjectList(List<?> object, Class<T> clazz) {



		List<T> result = new ArrayList<T>();

		for (int i = 0; i < object.size(); i++) {

			result.add((T) object2E(object.get(i), clazz));

		}



		return result;

	}



	static public JsonArray List2JsonArray(List<?> list) {

		Gson gson = new GsonBuilder().create();

		return gson.fromJson(gson.toJson(list), JsonArray.class);

	}



}