package com.crx.forkjoindemo;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class Main {

	public static void main(String[] args) {
		String type=args[0];
		String searchString=args[1];
		System.out.println("开始查询-----------------------------------------------------------------------------------");
		long startTime=System.currentTimeMillis();
		List<String> files=getAllFiles("E:\\中国软件\\核心征管svn代码\\HXZG\\business\\trunk",type,searchString);
		getAllFiles(files,type,searchString);
		long endTIme=System.currentTimeMillis();
		System.out.println("查询用时"+(endTIme-startTime)+"毫秒---------------------------------------------------------");
	}
	private static void wordCount(){
		String string="ahjkhaaaasadkjfkhjweilsadfhkjlsahdfhklsakjfhlkasdhf";
		char[] chars=string.toCharArray();
		ForkJoinPool commonPool=ForkJoinPool.commonPool();
		CountTask task=new CountTask(chars, 0, chars.length);
		commonPool.execute(task);
		commonPool.shutdown();
		try {
			 commonPool.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			 e.printStackTrace();
		}
		try {
			Map<Character, Integer> result=task.get();
			Iterator<Character> iterator=result.keySet().iterator();
			while (iterator.hasNext()) {
				Character key=iterator.next();
				System.out.println(key+":"+result.get(key));	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 递归遍历一个文件夹的所有文件,获取所有的绝对路径,深度优先遍历的思想
	 * @param dic
	 * @param files
	 */
	private static void getAllFiles(File dic,List<String> files){
		if(!dic.isDirectory()){
			files.add(dic.getAbsolutePath());
		}else{
			File[] lists=dic.listFiles();
			for (File file : lists) {
				getAllFiles(file, files);
			}
		}
	}
	
	private static List<String> getAllFiles(String filepath,String type,String searchString){
		File path=new File(filepath);
		ForkJoinPool commonPool=new ForkJoinPool(10);
		SearchTask task=new SearchTask(path, type, searchString);
		commonPool.execute(task);
		commonPool.shutdown();
		try {
			 commonPool.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			 e.printStackTrace();
		}
		try {
			List<String> result=task.get();
			System.out.println("共有"+result.size()+"个待扫描的文件");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static void getAllFiles(List<String> files,String type,String searchString){
		ForkJoinPool commonPool=new ForkJoinPool(10);
		SearchFileTask searchFileTask=new SearchFileTask(type, searchString, 0, files.size(),files);
		commonPool.execute(searchFileTask);
		commonPool.shutdown();
		try {
			 commonPool.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			 e.printStackTrace();
		}
		try{
			List<String> searchResults=searchFileTask.get();
			System.out.println("在如何文件中包含所查询的内容-----------------------------------------------------------------");
			for (String string : searchResults) {
				System.out.println(string);
			}
			System.out.println("--------------------------------------------------------------------------------------");
		}catch (Exception e) {
			
		}
	}
}
