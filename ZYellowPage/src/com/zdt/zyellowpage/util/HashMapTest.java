package com.zdt.zyellowpage.util;

import java.util.ArrayList;
import java.util.List;

public class HashMapTest {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Test> list = new  ArrayList<Test>();
		list.add(new Test("0","0001"));
		list.add(new Test("1","0002"));
		list.add(new Test("8","0009"));
		list.add(new Test("3","0004"));
		list.add(new Test("5","0006"));
		list.add(new Test("6","0007"));
		list.add(new Test("2","0003"));
		list.add(new Test("4","0005"));
		list.add(new Test("7","0008"));
		for(int i=0;i<list.size();i++){
			System.out.print(list.get(i).num);
		}
	}
	
}
class Test{
	public String num;
	public String content;
	public  Test(String m,String c){
		 num = m;
		 content = c;
	}
}
