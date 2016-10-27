/*************************************************************************
 *  Compilation:  javac Page.java
 *
 *  This models a "web page".  It is designed as part of the OnlineQuery
 *  program.
 *
 *************************************************************************/

import java.util.ArrayList;

public class Page /*implements Comparable*/ {
    private String theURL;          // the URL of the page
    private double theSimilarity;   // the similarity wrt the given query
    private boolean theResponse;    // whether or not the page can be viewed
    private String theContent;      // the HTML code of the entire page
    private ArrayList<String> theLinks; // the list of all outlinks

    public Page() {   // when we only want the content
	theURL = "";
	theSimilarity = 0;
	theResponse = true;
	theContent = "";
	theLinks = null;
    }	

    public Page(String url) {
	theURL = url;
	theSimilarity = 0;       // no query provided yet
	theResponse = true;   
	theContent = "";
	theLinks = null;

	In in = new In(theURL);  // try to open the page

	if (!in.exists()) {      // if no response, skip the rest
	    theResponse = false;
	    return;
	}
	
	theContent = in.readAll(); // get the content as one big string

	if (theContent == null) {  // an empty page
	    theResponse = false;
	    return;
	}

	findLinks();            // find all outlinks
    }

    public boolean response()      { return theResponse;      }
    public String  getURL()        { return theURL;           }
    public String  getContent()    { return theContent;       }
    public double  getSimilarity() { return theSimilarity;    }
    public ArrayList<String> getLinks() { return theLinks;    }

    public void    setContent(String content) {
	theContent = content;
	findLinks();    // find all outlinks
    }

    public String toString() {    // print the similarity, the url, and a snippet of the content 

		String content = "";
		if (theContent != null) {
	    	if ((theContent.indexOf("<title>") != -1)  && (theContent.indexOf("</title>") != -1))
	    		content = theContent.substring(theContent.indexOf("<title>") + 7, theContent.indexOf("</title>"));
	    	else 
	    		content = "No Title";
		}
		return "- (" + theSimilarity + ") " + theURL + "\n" + content;
    }

    public void findSimilarity(Page query) {
    	    	//sort words
		
		//Use BST to sort words in two strings
    	String q = query.getContent();
		MyBinaryTree thisTree = new MyBinaryTree();  //initialize a tree for page 1
		thisTree.insertMany(theContent);					  //enter page 1 data into tree 1
		MyQueue thisSortedTree = new MyQueue();		  //initialize a queue for page 1
		thisTree.toQueue(thisSortedTree);				  //enter tree 1 into queue 1
		
		MyBinaryTree qTree = new MyBinaryTree();  //initialize a tree for page 2
		qTree.insertMany(query.getContent());				  //enter page 2 data into tree 2
		MyQueue qSortedTree = new MyQueue();		  //initialize a queue for page 2
		qTree.toQueue(qSortedTree);				  //enter tree 2 into queue 2

		theSimilarity = (double)dotProduct(thisSortedTree, qSortedTree) / (qTree.magnitude() * thisTree.magnitude());
    }
    public static int dotProduct(MyQueue a, MyQueue b){			//algorithm based on MergeSort to calculate dot product
		int value = 0;
		if ((a.isEmpty()) || (b.isEmpty())){
			return 0;
		}
		a.dequeue(); b.dequeue();
		while(true){ 
			if (a.word.compareTo(b.word) == 0){
				value += (a.frequency * b.frequency);	//weight similarity by frequency of word
				if (a.isEmpty() || b.isEmpty()) break;
				a.dequeue(); b.dequeue();
			}
			else if ((a.word.compareTo(b.word)) < 0 && (!a.isEmpty()))
				 a.dequeue(); 
			else if (!b.isEmpty()) b.dequeue();
			else break;
		} 
		return value;
	}

    public void findLinks() {
	theLinks = new ArrayList<String> ();
	int i = 0;
    while (i < theContent.length() - 7){
    	if ((theContent.indexOf("http://", i) == -1) || (theContent.indexOf("\">", theContent.indexOf("http://", i)) == -1))
    		break;
    	else {
    		theLinks.add(theContent.substring(theContent.indexOf("http://", i), theContent.indexOf("\">", theContent.indexOf("http://", i))));
    		i = theContent.indexOf("\">", i) + 1;
    	}
	}
	}
    public int compareTo(Page other) {
	// ***
    	if (theSimilarity > other.theSimilarity)
    		return -1;
    	else if (theSimilarity == other.theSimilarity)
    		return 0;
    	else return 1;
    }
}

//CLASS USED TO CREATE SORTED QUEUE, USED BY FIND SIMILARITY TO CALCULATE DOT PRODUCT
class MyQueue{  
	public String word;
	public int frequency;
	private Link current, newbie;
	private static class Link{
		public String s; public int count; public Link next;
		Link(){
			this.s = ""; this.count = 0;
		}
		Link(String s, int count){
			this.s = s; this.count = count;
		}
	}
	public MyQueue(){
		current = newbie = null;
	}
	public void enqueue(String s, int count){
		if (newbie == null){
			newbie = new Link(s, count);
			current = newbie;
		}
		else {
			Link interim = newbie;
			newbie = new Link(s, count);
			interim.next = newbie;
		}
	}
	public void dequeue(){
		word = current.s;
		frequency = current.count;
		Link temp = current;
		current = current.next;
	}
	public boolean isEmpty(){
		return (current == null);
	}

}

//CLASS USED TO CREATE BINARYTREE OF PAGE CONTENT. USED BY FIND SIMILARITY TO CALCULATE MAGNITUDE OF VECTOR
class MyBinaryTree{
	private Node root;
	private int n = 0;
	private static class Node{
		Node left; Node right; String s; int count; 
		Node(String string){
			left = null;
			right = null;
			s = string;
			count = 1;
		}
	}
	public void MyBinaryTree(){
		root = null;
	}
	public void insert(String s){						//adds a word to the tree
		insert(root, s);
		n++;
	}
	private void insert(Node item, String s){
		if (root == null) root = new Node(s);
		else if (s.compareTo(item.s) == 0)
			item.count++;
		else if (s.compareTo(item.s) < 0){
			if (item.left == null) item.left = new Node(s);
			else insert(item.left, s);
		}
		else {
			if (item.right == null) item.right = new Node(s);
			else insert(item.right, s);
		}
	}
	
	public boolean lookup(String s){					//returns if word is in tree
		return (lookup(root, s));
	}
	private boolean lookup(Node item, String s){		
		if (item == null)
			return false;
		else if (item.s.compareTo(s) == 0)
			return (true);
		else if (item.s.compareTo(s) == -1)
			return (lookup(item.left, s));
		else return (lookup(item.right, s));
	}
	public void insertMany(String s){		//this method separates a large string into multiple words and inserts them
	int i = 0, j = 0;
	while (j < s.length()){
		while((j < s.length()) && (((int) s.charAt(j) < (int) 'A') || ((int) s.charAt(j) > (int) 'z'))){
			j++;
		}
		i = j;
		while((j < s.length()) && ((int) s.charAt(j) >= (int) 'A') && ((int) s.charAt(j) <= (int) 'z')){
			j++;
		}
		if (i < s.length()) insert(s.substring(i, j));
	}
	}
	public void toQueue(MyQueue list){
		toQueue(list, root);
	}
	private void toQueue(MyQueue list, Node item){
		if (item == null) return;
		toQueue(list, item.left);
		list.enqueue(item.s, item.count);
		toQueue(list,item.right);
	}
	public void printTree(){
		printTree(root);
	}
	public void printTree(Node item){
		if (item == null) return;
		printTree(item.left);
		System.out.println(item.s);
		printTree(item.right);
	}
	public double magnitude(){
		return Math.sqrt(magnitude(root));
	}
	private double magnitude(Node item){
		if (item == null) return 0;
		else return (item.count * item.count) + magnitude(item.left) + magnitude(item.right);
	}
}

