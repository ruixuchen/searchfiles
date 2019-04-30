package com.crx.forkjoindemo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;

public class SearchFileTask extends RecursiveTask<List<String>>{
	
	private static final long serialVersionUID = 1L;
	private int start;
	private int end;
	private String fileType;
	private String searchString;
	private List<String> files;
	
	public SearchFileTask(String fileType,String searchString,int start,int end,List<String> files) {
		this.fileType=fileType;
		this.searchString=searchString;
		this.start=start;
		this.end=end;
		this.files=files;
	}
	
	@Override
	protected List<String> compute() {
		List<String> result=new ArrayList<>();
		if(end-start<10){
			for(int i=start;i<end;i++){
				String filepath=files.get(i);
				File file=new File(filepath);
				if(file.getName().split("\\.").length>1&&file.getName().split("\\.")[1].equals(fileType)&&search(file)){
					result.add(filepath);
				}
			}
		}else{
			int middle=(start+end)/2;
			SearchFileTask task1=new SearchFileTask(fileType, searchString, middle, end, files);
			SearchFileTask task2=new SearchFileTask(fileType, searchString, start, middle, files);
			invokeAll(task1,task2);
			try {
				result=groupResults(task1.get(), task2.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			
		}
		return result;
	}
	
	private List<String> groupResults(List<String> list1,List<String> list2){
		list1.addAll(list2);
		return list1;
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
