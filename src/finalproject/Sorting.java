package finalproject;

import java.util.*;
import java.util.Map.Entry; // You may need it to implement fastSort

public class Sorting {

	/*
	 * This method takes as input an HashMap with values that are Comparable. 
	 * It returns an ArrayList containing all the keys from the map, ordered 
	 * in descending order based on the values they mapped to. 
	 * 
	 * The time complexity for this method is O(n^2) as it uses bubble sort, where n is the number 
	 * of pairs in the map. 
	 */
    public static <K, V extends Comparable> ArrayList<K> slowSort (HashMap<K, V> results) {
        ArrayList<K> sortedUrls = new ArrayList<K>();
        sortedUrls.addAll(results.keySet());	//Start with unsorted list of urls

        int N = sortedUrls.size();
        for(int i=0; i<N-1; i++){
			for(int j=0; j<N-i-1; j++){
				if(results.get(sortedUrls.get(j)).compareTo(results.get(sortedUrls.get(j+1))) <0){
					K temp = sortedUrls.get(j);
					sortedUrls.set(j, sortedUrls.get(j+1));
					sortedUrls.set(j+1, temp);					
				}
			}
        }
        return sortedUrls;                    
    }
    
    
	/*
	 * This method takes as input an HashMap with values that are Comparable. 
	 * It returns an ArrayList containing all the keys from the map, ordered 
	 * in descending order based on the values they mapped to. 
	 * 
	 * The time complexity for this method is O(n*log(n)), where n is the number 
	 * of pairs in the map. 
	 */
    public static <K, V extends Comparable> ArrayList<K> fastSort(HashMap<K, V> results) {
    	// ADD YOUR CODE HERE
    	ArrayList<Object> out = new ArrayList<Object>();
    	out.addAll(results.keySet());
    	int size=out.size();
    	Object[] outArray = new Object[size]; 
    	int i = 0;
    	while(!out.isEmpty()) {
    		outArray[i] = out.remove(0);
    		i++;
    	}
    	outArray = mergeSort(outArray, 0, size-1, results);
    	i=0;
    	while(out.size()!=size) {
    		out.add(outArray[i]);
    		i++;
    	}
    	return (ArrayList<K>) out;
    }
    
    
    
    public static <K, V extends Comparable> Object[] merge(Object[] in, int x, int mid, int y, HashMap<K,V> map){
    	
    	int size1 = mid-x+1;
    	int size2 = y-mid;
    	Object[] array1 = new Object[size1];
    	Object[] array2 = new Object[size2];
    	for(int i = 0; i<size1; i++) array1[i] = in[x+i];
    	for(int i =0; i<size2; i++) array2[i] = in[mid+1+i];
    	int i = 0;
    	int j = 0;
    	int start = x;
    	
    	while(i<size1 && j<size2) {
    		if(map.get(array1[i]).compareTo(map.get(array2[j]))>=0) {
    			in[start] = array1[i];
    			i++;
    		}else {
    			in[start] = array2[j];
    			j++;
    		}
    		start++;
    	}
    	
    	
    	while(i<size1) {
    		in[start] = array1[i];
    		start++;
    		i++;
    	}
    	
    	while(j<size2) {
    		in[start] = array2[j];
    		start++;
    		j++;
    	}
    	return in;
    }
    
    
    
    
    public static <K, V extends Comparable> Object[] mergeSort(Object[] in, int x, int y, HashMap<K,V> map){
    	if(x<y) {
    		int mid = (x+y)/2;
    		mergeSort(in, x, mid, map);
    		mergeSort(in, mid+1, y, map);
    		return merge(in, x, mid, y, map);
    	}
    	else {
    		return in;
    	}
    }
    
    

}