package finalproject;

import java.util.HashMap;
import java.util.ArrayList;

public class SearchEngine {
	public HashMap<String, ArrayList<String> > wordIndex;   // this will contain a set of pairs (String, LinkedList of Strings)	
	public MyWebGraph internet;
	public XmlParser parser;

	public SearchEngine(String filename) throws Exception{
		this.wordIndex = new HashMap<String, ArrayList<String>>();
		this.internet = new MyWebGraph();
		this.parser = new XmlParser(filename);
	}
	
	/* 
	 * This does a graph traversal of the web, starting at the given url.
	 * For each new page seen, it updates the wordIndex, the web graph,
	 * and the set of visited vertices.
	 * 
	 * 	This method will fit in about 30-50 lines (or less)
	 */
	public void crawlAndIndex(String url) throws Exception {
		resetVisited();
		//if url isn't in the graph, add to graph and then traverse
		if(!internet.vertexList.containsKey(url)) internet.addVertex(url);
		ArrayList<String> Q = new ArrayList<String>();
		Q.add(url);
		while(!Q.isEmpty()) {
			String curr = Q.remove(0);
			if(internet.getVisited(curr)==true) break;
			internet.setVisited(curr, true);
			ArrayList<String> words = parser.getContent(curr);
			while(!words.isEmpty()) {
				String curr_word = words.remove(0).toLowerCase();
				if(!wordIndex.containsKey(curr_word)) {
					ArrayList<String> values = new ArrayList<String>();
					values.add(curr);
					wordIndex.put(curr_word, values);		
				}else {
					if(!wordIndex.get(curr_word).contains(curr)) wordIndex.get(curr_word).add(curr);
					
				}
			}
			ArrayList <String> children = parser.getLinks(curr);
			for(int i=0; i<children.size(); i++) {
				if(!internet.vertexList.containsKey(children.get(i))) internet.addVertex(children.get(i));
				if(internet.getVisited(children.get(i))==false) {
						internet.addEdge(curr, children.get(i));
						Q.add(children.get(i));
				}else {
					internet.addEdge(curr, children.get(i));
				}
			}	
				
		}
		
	}
	
	public void resetVisited() {
		ArrayList<String> list = internet.getVertices();
		while(!list.isEmpty()) {
			String cur = list.remove(0);
			internet.setVisited(cur, false);
		}
	}
	
	
	
	/* 
	 * This computes the pageRanks for every vertex in the web graph.
	 * It will only be called after the graph has been constructed using
	 * crawlAndIndex(). 
	 * To implement this method, refer to the algorithm described in the 
	 * assignment pdf. 
	 * 
	 * This method will probably fit in about 30 lines.
	 */
	public void assignPageRanks(double epsilon) {
		ArrayList<String> vertices = internet.getVertices();
		
		//set ranks for all them bois
		for(int i=0; i<vertices.size();i++) {
			internet.setPageRank(vertices.get(i), 1);
		}
		//get first iteration of ranks to compare with
		ArrayList<Double> prevranks = computeRanks(vertices);
		for(int i=0; i<vertices.size();i++) {
			internet.setPageRank(vertices.get(i), prevranks.get(i));
		}
		
		
		int target = vertices.size();
		int x=0;
		ArrayList<Double> currRanks = new ArrayList<Double>();
		while(true) {
			currRanks = computeRanks(vertices);
			for(int i =0; i<target; i++) {
				double prevrank = Math.abs(prevranks.get(i));
				double currRank = Math.abs(currRanks.get(i));
				double idk = Math.abs(prevrank-currRank);
				if(idk<epsilon) x++;
			}
			
			if(x==target) {
				for(int i=0; i<target;i++) {
					internet.setPageRank(vertices.get(i), currRanks.get(i));
				}
				break;
			}else {
				prevranks = currRanks;
				x=0;
				for(int i=0; i<target;i++) {
					internet.setPageRank(vertices.get(i), currRanks.get(i));
				}
			}
		}
		
	}

	/*
	 * The method takes as input an ArrayList<String> representing the urls in the web graph 
	 * and returns an ArrayList<double> representing the newly computed ranks for those urls. 
	 * Note that the double in the output list is matched to the url in the input list using 
	 * their position in the list.
	 */
	public ArrayList<Double> computeRanks(ArrayList<String> vertices) {
		ArrayList<Double> out  = new ArrayList<Double>();
		
		//set rank of all vertices to 1 if initial rank is 0
//		for(int i=0;i<vertices.size();i++) {
//			if(internet.getPageRank(vertices.get(i))==0) {
//				String curr = vertices.get(i);
//				internet.setPageRank(curr, 1);
//			}
//		}
		
		for(int i=0; i<vertices.size();i++) {
			String currVertex = vertices.get(i);
			ArrayList<String> inDegreeList = new ArrayList<String>();
			ArrayList<String> checkinDegree = new ArrayList<String>();
			checkinDegree.addAll(vertices);
			checkinDegree.remove(currVertex);
			for(int j=0;j<checkinDegree.size();j++) {
				String currChild = checkinDegree.get(j);
				ArrayList<String> grandchildren = parser.getLinks(currChild);
				if(grandchildren.contains(currVertex))inDegreeList.add(currChild);
			}
			//if no vertices have edge coming into currVertex then rank for currVertex is just 0.5
			//because stuff inside brackets is 0
			
			double StuffInsideBrackets = 0;
			for(int k=0; k<inDegreeList.size();k++) {
				String currW = inDegreeList.get(k);
				double outW = parser.getLinks(currW).size();
				double rankW = internet.getPageRank(currW);
				StuffInsideBrackets = StuffInsideBrackets + (rankW/outW);
			}
			double rank = 0.5 + 0.5*StuffInsideBrackets;
			out.add(i, rank);
			
		}
		return out;
	}

	
	/* Returns a list of urls containing the query, ordered by rank
	 * Returns an empty list if no web site contains the query.
	 * 
	 * This method should take about 25 lines of code.
	 */
	public ArrayList<String> getResults(String query) {
		query = query.toLowerCase();
		ArrayList<String> unsortedout = new ArrayList<String>();
		if(wordIndex.get(query)!=null) unsortedout = wordIndex.get(query);
		
		
		HashMap<String, Double> outHashmap = new HashMap<String, Double>();
		if(unsortedout == null) {
			return unsortedout;
		}
		else {
			for(int i=0; i<unsortedout.size();i++) {
				outHashmap.put(unsortedout.get(i), internet.getPageRank(unsortedout.get(i)));
			}
			ArrayList<String> out = Sorting.fastSort(outHashmap);
			return out;
		}
	}
}
