package com.crx.forkjoindemo;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class Main {

	public static void main(String[] args) {
//		String type="java";
//		String searchString="SWZJ.DZSWJ.LOGIN.QUERYDJXH";
//		System.out.println("start searching------------------------------------------------------------------------------------");
//		long startTime=System.currentTimeMillis();
//		List<String> files=getAllFiles("/Users/chenruixu/Desktop/电子税务局/WWXT/DZSWJ/business/trunk",type,searchString);
//		getAllFiles(files,type,searchString);
//		long endTime=System.currentTimeMillis();
//		System.out.println("using "+(endTime-startTime)+" milliseconds---------------------------------------------------------");
		getAllFiles();
	}
	
	private static void getAllFiles() {
		ForkJoinPool pool=new ForkJoinPool(10);
		ProcessFolder processFolder=new ProcessFolder("/Users/chenruixu/Desktop/电子税务局/WWXT/DZSWJ/business/trunk/hdxt/gy/service/src/gov/gt3/hdxt", "java");
		pool.execute(processFolder);
		do {
			System.out.printf("Main: Active Threads: %d\n",pool.getActiveThreadCount());
			System.out.printf("Main: Task Count: %d\n",pool.getQueuedTaskCount());
			System.out.printf("Main: Steal Count: %d\n",pool.getStealCount());
			try {
				TimeUnit.SECONDS.sleep(1);
			}catch (Exception e) {
				e.printStackTrace();
			}
		} while (!processFolder.isDone());
		pool.shutdown();
		List<String> result=processFolder.join();
		System.out.println(result);
		//System.out.println(result.size());
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
	 * traversal a file dictionary recursively based on depth-first algorithm and get all the files' absolute path
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
			System.out.println("there are "+result.size()+" files need to be scanned");
//			for (String string : result) {
//				System.out.println(string);
//			}
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
			if(searchResults.size()>0) {
				System.out.println("the file list as follows contain the string you input---------------------------------");
				for (String string : searchResults) {
					System.out.println(string);
				}
				System.out.println("--------------------------------------------------------------------------------------");
			}else {
				System.out.println("there is no file contain the string you input");
			}
			
		}catch (Exception e) {
			
		}
	}
}
