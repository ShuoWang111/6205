package edu.neu.coe.info6205.union_find;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import tool.CSVUtils;

public class Step2_3{
	
	static UF_HWQUPC uf;
    
	public Step2_3(int n) {
		this.uf = new UF_HWQUPC(n);
		
	}
	
	//generates one random pair of integers between 0 and n-1 (task2)
    private static int[] Random_pairs(int n) {
    	int[] pair = new int[2];
    	pair[0] = (int)(Math.random()*n);
    	pair[1] = (int)(Math.random()*n);
//    	System.out.println("Random pairs of integers between 0 and "+n+" : "+Arrays.toString(pair));
    	return pair;
    }
	

	// if all sites are connected (task2)
    private static boolean Allconnected(int n) {
    	boolean c = true;		// c  connected
    	for(int i = 0; i<n-1; i++) {
    		if(uf.find(i)!=uf.find(i+1)) {
    			c = false;
    			return c;
    		}
    	}
    	return c;
    	
    }
    
    
  
    

	//takes n as the argument and returns the number of connections (task2)
    private static int count(int n) {
    	int number = 0;			//the number of connections
    	int[] pair = new int[2];
    	while(!Allconnected(n)) {
    		pair = Random_pairs(n);
    		if(!uf.connected(pair[0],pair[1])) {
    			uf.union(pair[0],pair[1]);
    		}
    		number++;
    	}
//    	System.out.println("The number of connections: "+ number);
    	return number;
    }
    
    // average number of pairs/connection(m) when runs t times (task3)
    private static int AverageCount(int n, int t) {
    	int sum = 0;
    	for(int i = 0; i < t; i++) {
    		Step2_3 step = new Step2_3(n);
    		sum += count(n);
    		
    	}
//    	System.out.println("sum: "+ sum);
    	return sum/t;
    }

	public static void main(String[] args) {
	    	Scanner scan = new Scanner(System.in);
	    	List<String> dataList=new ArrayList<String>();
	    	
	    	int n = 500;					//the number of "sites"
			int[] connection = new int[n]; 	//the number of connections
			int t = 100;					//the times of each site tests
			
//			System.out.println("Input n (must be integer):");
//			while(!scan.hasNextInt()) {
//				System.out.println("Inputed a wrong type! Input n (must be integer):");
//			}
//			n = scan.nextInt();
			for(int i = n-1; i>0; i--) {
				
//				connection[i] = count(i);
				connection[i] = AverageCount(i,t);
			}
			for(int i=0;i<connection.length;i++)
			{	
				int site = i+1;
				String c = String.valueOf(connection[i]);	
				dataList.add(i,c);
			    System.out.println("The number of sites is "+site+", the number of connections is "+connection[i]+".");
			}
			
			// export data
	        boolean isSuccess=CSVUtils.exportCsv(new File("/Users/wangshuo/Desktop/test.csv"), dataList);
	        System.out.println(isSuccess);


	}
			
}
