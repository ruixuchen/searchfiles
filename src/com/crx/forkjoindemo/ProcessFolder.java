package com.crx.forkjoindemo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountedCompleter;

public class ProcessFolder extends CountedCompleter<List<String>>{

	private static final long serialVersionUID = 1L;
	private String path;
	private String fileType;
	private List<ProcessFolder> tasks;
	private List<String> resultList;
	
	public List<String> getResultList() {
		return resultList;
	}

	public void setResultList(List<String> resultList) {
		this.resultList = resultList;
	}

	public ProcessFolder(CountedCompleter<?> completer,String path,String fileType) {
		super(completer); 
		this.path=path;
		this.fileType=fileType;
	}
	public ProcessFolder(String path,String fileType) {
		this.path=path;
		this.fileType=fileType;
	}
	
	@Override
	public void compute() {
		resultList=new CopyOnWriteArrayList<>();
		tasks=new CopyOnWriteArrayList<>();
		File[] files=new File(path).listFiles();
		if(files!=null) {
			for (File file : files) {
				if(file.isDirectory()) {
					//addToPendingCount(1);
					setPendingCount(1);
					ProcessFolder task=new ProcessFolder(this, file.getAbsolutePath(), fileType);
										
					tasks.add(task);
					task.fork();
				}else {
					if(file.getName().endsWith(fileType)) {
						resultList.add(file.getAbsolutePath());
					}
				}
			}
		}
		this.tryComplete();
	}
	
	@Override
	public void onCompletion(CountedCompleter<?> completer) {
		for (ProcessFolder childTask : tasks) {
			resultList.addAll(childTask.getResultList());
		}
	}

}
