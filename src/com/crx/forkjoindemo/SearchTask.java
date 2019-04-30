package com.crx.forkjoindemo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;

public class SearchTask extends RecursiveTask<List<String>>{

	private static final long serialVersionUID = 1L;
	private String fileType;
	private String searchString;
	private File path;
	
	public SearchTask(File path,String fileType,String searchString){
		this.fileType=fileType;
		this.searchString=searchString;
		this.path=path;
	}
	
	@Override
	protected List<String> compute() {
		List<String> result=new ArrayList<>();
		if(!path.isDirectory()){
//			System.out.println("开始从"+path.getAbsolutePath()+"中查询！");
//			if(path.getName().split("\\.").length>1&&path.getName().split("\\.")[1].equals(fileType)){
//				boolean flag=search(path);
//				if(flag){
//					result.add(path.getAbsolutePath());
//				}
//			}
			result.add(path.getAbsolutePath());
		}else{
			File[] files=path.listFiles();
			for (File file : files) {
				SearchTask task=new SearchTask(file, fileType, searchString);
				invokeAll(task);
				try {
					result.addAll(task.get());
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}
		return result;
	}
	
	private boolean search(File file){
		boolean flag=false;
		try{
			BufferedReader reader=new BufferedReader(new FileReader(file));
			String string;
			while((string=reader.readLine())!=null){
				if(string.contains(searchString)){
					reader.close();
					flag=true;
					break;
				}
			}	
		}catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
}
