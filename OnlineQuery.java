/*************************************************************************
 *  Compilation:  javac OnlineQuery.java 
 *  Execution:    java OnlineQuery -uURL -qqueryString [-mM]
 *  Dependencies: stdlib.jar Queue.java Page.java MergeSort.java
 *  
 *  Downloads web pages, starting from the URL, and subsequently through 
 *  hyperlinks found in the pages.  Computes the similarity of each page 
 *  with the queryString and prints a list of ranked result.
 *
 *************************************************************************/
import java.util.ArrayList;

public class OnlineQuery { 
    public static void main(String[] args) { 
		boolean DEBUG = false;
		if (args.length < 3) {
	    	System.out.println("Usage: java OnlineQuery -uURL -qqueryString [-mM] (Maximum)");
	    	System.exit(0);
		}
        // timeout connection after 1000 miliseconds
        System.setProperty("sun.net.client.defaultConnectTimeout", "1000");
        System.setProperty("sun.net.client.defaultReadTimeout",    "1000");
        // parsing the commandline arguments                                                                                                                  
		int maxPages = 25;         // maximum number of pages to collect
		Page query = new Page();   // the query page
        Page start = null;         // the start page
		for (int i = 0; i < args.length; i++) {
            if (args[i].substring(0,2).equals("-u"))
                start = new Page(args[i].substring(2));
            else if (args[i].substring(0,2).equals("-q")){
                query.setContent(args[i].substring(2));
            }
            else if (args[i].substring(0,2).equals("-m"))
                maxPages = Integer.parseInt(args[i].substring(2));
            else {
                System.out.println("invalid flag:" + args[i].substring(0,2));
                System.exit(0);
            }
        }
		if (start == null || query.getContent().equals("")){ // no seed page or query provided
	   		System.exit(0);
	   	}
		int pageCount = 0; // the number of pages that we visit
		Page[] results = new Page[maxPages]; 	// resulting pages

		DoublingQueue frontier = new DoublingQueue();   //frontier is a FIFO Queue of urls
		frontier.enqueue(start.getURL());				// the start url is the first in the frontier

		HistoryTree history = new HistoryTree();		//store all of the urls that have been searched
		history.insert(start.getURL());

        // do a crawl of web
		System.out.println("+++++++ Searching ");
		int x = 0;
        while (! frontier.isEmpty()) {
            Page v = new Page(frontier.dequeue());  // the next page in line
	    	if (! v.response()){
	    		continue; // no luck with this page 
	    	}
	    	v.findSimilarity(query);      // compute similarity
	   		if (v.getSimilarity() > 0){ 
				results[pageCount++] = v;	//add to results if passes similarity test
			}    
	   	 	if (pageCount >= maxPages){ // limit reached, return results
				break;
			}
	  		ArrayList<String> outLinks = v.getLinks();		 // find all outgoing links from this page
	  
	  		int i = 0;
	    	while(i < outLinks.size()){
	    		if ((history.insert(outLinks.get(i)))){		//add url to frontier if it can be inserted into history tree
					frontier.enqueue(outLinks.get(i));		//depth first search through web
	    		}
	    		i++;
	  		}
        }

	// sort the result and print
	MergeSort.sort(0,pageCount,results);

	System.out.println("\n+++++++ Search Results:");
	System.out.println("# of results: " + pageCount);

	for (int i = 0; i < pageCount; i++) {
	    System.out.println(results[i]);
	}
   }
}



class DoublingQueue{			//the frontier
	private int first, last;
	private String[] url;
	public DoublingQueue(){
		first = last = 0;
		url = new String[2];
	}
	public boolean isEmpty(){
			return (url[first] == null);
	}
	public String dequeue(){
		if (url[first] != null){
			int temp = first++;
			if (first == url.length)
				first = 0;
			return url[temp];
		}
		else {
			return null;
		}
	}
	public void enqueue(String newUrl){
		url[last++] = newUrl;
		if ((last == url.length) && (first != 0))
			last = 0;
		else if ((last == first) || (last == url.length))
			resize();
		else return;
		}
	public void resize(){
		String[] temp = new String[url.length * 2];
		for (int i = 0; i < url.length; i++){
			temp[i] = dequeue();
		}
		last = url.length - 1;
		url = temp;
		first = 0;
	}
}
class HistoryTree{
	private Node root;
	class Node{
		private Node left, right;
		private String URL;
		Node(){
			left = right = null;
			URL = "";
		}
		Node(String url){
			left = right = null;
			URL = url;
		}
	}
	HistoryTree(){
		root = new Node("start");
	}
	public boolean insert(String url){  //returns true if successfuly inserts item into tree, false if item already exists
		 return insert(url, root);
	}
	private boolean insert(String url, Node root){
		if (root == null){
			root = new Node(url);
			return true;
		}
		else if (url.compareTo(root.URL) == 0){
			return false;
		}
		else if (url.compareTo(root.URL) > 0){
			if (root.right == null){
				root.right = new Node(url);
				return true;
			}
			else return insert(url, root.right);
		}
		else {
			if (root.left == null){
				root.left = new Node(url);
				return true;
			}
			else return insert(url, root.left);
		}
	}
	public void printTree(){
		printTree(root);
	}
	public void printTree(Node item){
		if (item == null) return;
		printTree(item.left);
		System.out.println(item.URL);
		printTree(item.right);
	}
}

