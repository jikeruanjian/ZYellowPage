package com.zdt.zyellowpage.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zdt.zyellowpage.jsonEntity.QueryRequest;
import com.zdt.zyellowpage.jsonEntity.ResultData;
import com.zdt.zyellowpage.model.*;

public class JsonTest {

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		//QueryEnterpriseRequest q = new QueryEnterpriseRequest("query-enterprise",0,10,"123","src","电影");
		//1.对象转化为  json 字符串
		QueryEnterprise qq = new QueryEnterprise(0,10,"123","src","电影");
		 //String str =gson.toJson(qq).toString();
		String str = QueryRequest.getQueryRequestJson("xxxxxx", qq);
		 System.out.print( str);
		
		 //2.json 字符串转化为对象
		 QueryRequest xq = gson.fromJson(str, QueryRequest.class);
		 
		// System.out.print(xq.getData().getArea_id());
		
		 //3.列表转化为json字符串
		List<Enterprise> list = new ArrayList<Enterprise>();
		for(int i = 0 ;i<5;i++){
			list.add(new Enterprise("id"+i,"name"+i,"s"+1,"img"+1));
		}
		String str1 =gson.toJson(list);
		
		//对象转化为json字符串
		String str2 = gson.toJson(new ResultData("query-enterprise ",str1));
		//System.out.print( str1);
		ResultData resultData = new Gson().fromJson(str2, ResultData.class);
		
		//System.out.println(resultData.getResult());
		//System.out.println(resultData.getData());
		
		//4.json字符串转化为对象列表
		List<Enterprise> ps = gson.fromJson(resultData.getData(), new TypeToken<List<Enterprise>>(){}.getType());
		//List<Enterprise> ps = (List<Enterprise>) ResultData.getResultList(str1, new TypeToken<List<Enterprise>>(){}.getType());
		for(int i = 0; i < ps.size() ; i++)
		{     
			Enterprise p = ps.get(i);     
			System.out.println(p.getEnterprise_name());
		}
	}
}