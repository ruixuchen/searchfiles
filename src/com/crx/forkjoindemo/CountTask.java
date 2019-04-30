package com.crx.forkjoindemo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

/**
 * @author chenruixu
 * @desctiption this is a simple demo for fork/join and it can statistics the
 * frequency of the character in a specified string.
 *
 */
public class CountTask extends RecursiveTask<Map<Character, Integer>>{

	private static final long serialVersionUID = 1L;
	
	private char[] chars;
	private int start;
	private int end;

	public CountTask(char[] chars,int start,int end) {
		this.chars=chars;
		this.start=start;
		this.end=end;
	}
	@Override
	protected Map<Character, Integer> compute() {
		Map<Character, Integer> result=null;
		if(end-start<5){
			result=countWords(chars,start,end);
		}else{
			int mid=(start+end)/2;
			CountTask task1=new CountTask(chars, start, mid);
			CountTask task2=new CountTask(chars, mid, end);
			invokeAll(task1,task2);
			try{
				result=groupResults(task1.get(),task2.get());
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	private Map<Character, Integer> groupResults(Map<Character, Integer> res1,Map<Character, Integer> res2){
		Iterator<Character> iterator=res2.keySet().iterator();
		while (iterator.hasNext()) {
			Character key=iterator.next();
			if(res1.containsKey(key)){
				res1.put(key, res1.get(key)+res2.get(key));
			}else{
				res1.put(key, res2.get(key));
			}	
		}
		return res1;	
	}
	
	private Map<Character, Integer> countWords(char[] chars,int start,int end){
		Map<Character, Integer> result=new HashMap<>();
		for(int i=start;i<end;i++){
			if(result.containsKey(chars[i])){
				result.put(chars[i], result.get(chars[i])+1);
			}else{
				result.put(chars[i], 1);
			}
		}
		return result;
	}

}
