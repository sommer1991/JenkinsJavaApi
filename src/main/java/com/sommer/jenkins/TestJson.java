package com.sommer.jenkins;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class TestJson {
	public static void main(String[] args) {
		String str1 = "{\"p1\":\"aa\",\"pa\":\"bb\"}";
		String str2 = "{\"p1\":\"cc\",\"pa\":\"dd\"}";
		JSONObject obj1=new JSONObject(str1);
		JSONObject obj2=new JSONObject(str2);
		JSONArray arr = new JSONArray();
		arr.put(obj1);
		arr.put(obj2);
		JSONObject res = new JSONObject();
		res.append("ddd", "sss");
		res.append("sss", obj1);
		res.append("sss", obj2);
		System.out.println(arr);
		System.out.println(res);
		Set<String> set = new HashSet<String>();
		set.add("sss");
		set.add("yyy");
		System.out.println(set.toString());
	}

}
