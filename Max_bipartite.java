
package max_bipartite;

/*

   




  *************************
     	Hawazin salem Aljehani  1805587
	Jana Aloraifi           1805259
	Rehab Nuhayd            1808142
	
    ***************************
References :
https://github.com/SleekPanther/bipartite-matching/blob/master/BipartiteMatching.java
 


** added some comments and make the graph as directed graph


*/
import java.util.*;
public class Max_bipartite {


	class Edge{
		private static final int defaultEdgeCapacity = 1;	//This Flow network is for bipartite matching so the default capacity is always 1
		private int fromVertex;		//an edge is composed of 2 vertices
		private int toVertex;
		private int capacity;		//edges also have a capacity & a flow
		private int flow;

		//Overloaded constructor to create a generic edge with a default capacity
		public Edge(int fromVertex, int toVertex){
			this(fromVertex, toVertex, defaultEdgeCapacity);
		}

		public Edge(int fromVertex, int toVertex, int capacity){
			this.fromVertex = fromVertex;
			this.toVertex = toVertex;
			this.capacity = capacity;
		}
		
		//Given an end-node, Returns the other end-node (completes the edge)
		public int getOtherEndNode(int vertex){
			if(vertex==fromVertex){
				return toVertex;
			}
			return fromVertex;
		}
		// get Capacity
		public int getCapacity(){
			return capacity;
		}
		// get Flow
		public int getFlow(){
			return flow;
		}
		 
		public int residualCapacityTo(int vertex){
			if(vertex==fromVertex){
				return flow;
			}
			return (capacity-flow);
		}
		
		public void increaseFlowTo(int vertex, int changeInFlow){
			if(vertex==fromVertex){
				flow = flow-changeInFlow;
			}
			else{
				flow = flow+changeInFlow;
			}
		}
		
		//Prints edge using Array indexes, not human readable ID's like "S" or "T"
		@Override
		public String toString(){
			return "(" + fromVertex+" --> "+toVertex + ")";
		}
	}



	private ArrayList<ArrayList<Edge>> graph;		//Graph is represented as an ArrayList of Edges
	private ArrayList<String> getStringVertexIdFromArrayIndex;	//convert between array indexes (starting from 0) & human readable vertex names
	private int vertexCount;		//How many vertices are in the graph

	//These fields are updated by fordFulkersonMaxFlow and when finding augmentation paths
	private Edge[] edgeTo;
	private boolean[] isVertexMarked;		//array of all vertices, updated each time an augmentation path is found
	private int flow;

	//Constructor initializes graph edge list with number of vertexes, string equivalents for array indexes & adds empty ArrayLists to the graph for how many vertices ther are
	public  Max_bipartite(int vertexCount, ArrayList<String> getStringVertexIdFromArrayIndex){
		this.vertexCount = vertexCount;
		this.getStringVertexIdFromArrayIndex = getStringVertexIdFromArrayIndex;

		graph = new ArrayList<>(vertexCount);		//Populate graph with empty ArrayLists for each vertex
		for(int i=0; i<vertexCount; ++i){
			graph.add(new ArrayList<>());
		}
	}

	public void addEdge(int fromVertex, int toVertex){
		Edge newEdge = new Edge(fromVertex, toVertex);	//create new edge between 2 vertices
		graph.get(fromVertex).add(newEdge);		//make the graph as directed graph 
		
	}

	//Adds edges from the source to all vertices in the left half
	public void connectSourceToLeftHalf(int source, int[] leftHalfVertices){
		for(int vertexIndex : leftHalfVertices){
			// System.out.println("addEdge(source, vertexIndex) = ("+source+", "+vertexIndex+")");
			this.addEdge(source, vertexIndex);
		}
	}

	//Adds edges from all vertices in right half to sink
	public void connectSinkToRightHalf(int sink, int[] rightHalfVertices){
		for(int vertexIndex : rightHalfVertices){
			// System.out.println("addEdge(vertexIndex, sink) = ("+vertexIndex+", "+sink+")");
			this.addEdge(vertexIndex, sink);
		}
	}
	
	//Finds max flow / min cut of a graph
	public void fordFulkersonMaxFlow(int source, int sink){
		edgeTo = new Edge[vertexCount];
		while(existsAugmentingPath(source, sink)){
			int flowIncrease = 1;	//default value is 1 since it's a bipartite matching problem with capacities = 1
			//Print the 1st vertex of the pair
			System.out.print("Matched Vertices  "+(getStringVertexIdFromArrayIndex.get( edgeTo[sink].getOtherEndNode(sink) ) ));		
			//Loop over The path from source to sink. (Update max flow & print the other matched vertex)
			for(int i=sink; i!=source; i=edgeTo[i].getOtherEndNode(i)){
				//Loop stops when i reaches the source, so print out the vertex in the path that comes right before the source
				if(edgeTo[i].getOtherEndNode(i)==source){
					System.out.println(getStringVertexIdFromArrayIndex.get(i));		//use human readable vertex ID's
				}
				flowIncrease = Math.min(flowIncrease, edgeTo[i].residualCapacityTo(i));
			}
			
			//Update Residual Capacities
			for(int i=sink; i!=source; i=edgeTo[i].getOtherEndNode(i)){ 
				edgeTo[i].increaseFlowTo(i, flowIncrease);
			}
			flow+=flowIncrease;
		}
		System.out.println("\nThe Maximum number of applicants that can be assigned to hospitals is  = "+flow);
	}
	
	//Calls dfs to find an augmentation path & check if it reached the sink
	public boolean existsAugmentingPath(int source, int sink){
		isVertexMarked = new boolean[vertexCount];		//recreate array of visited nodes each time searching for a path
		isVertexMarked[source] = true;		//visit the source

		// System.out.print("Augmenting Path : S ");
		depthFirstSearch(source, sink);		//attempts to find path from source to sink & updates isVertexMarked
		// System.out.print("T  ");

		return isVertexMarked[sink];	//if it reached the sink, then a path was found
	}
	
	public void depthFirstSearch(int v, int sink){
		if(v==sink){	//No point in finding a path if the starting vertex is already at the sink
			return;
		}
		
		for(Edge edge : graph.get(v)){		//loop over all edges in the graph
			int otherEndNode = edge.getOtherEndNode(v);
			if(!isVertexMarked[otherEndNode] && edge.residualCapacityTo(otherEndNode)>0 ){	//if otherEndNode is unvisited AND if the residual capacity exists at the otherEndNode
				// System.out.print( getStringVertexIdFromArrayIndex.get(otherEndNode) +" ");
				edgeTo[otherEndNode] = edge;		//update next link in edge chain
				isVertexMarked[otherEndNode] = true;		//visit the node
				depthFirstSearch(otherEndNode, sink);		//recursively continue exploring
			}
		}
	}

        
        


	public static void main(String[] args){
		int vertexCount2 = 12;
		int vertexCountIncludingSourceAndSink2 = vertexCount2 +2;
		//convert between array indexes (starting from 0) & human readable vetex names
		ArrayList<String> getStringVertexIdFromArrayIndex2 = new ArrayList<String>(
                 Arrays.asList("Ahmed", "Mahmoud", "Eman", "Fatimah", "Kamel", "Nojood", "King Abdelaziz University <--","King Fahad <--", "East Jeddah <--", "King Fahd Armed Forces <--", "King Faisal Specialist <--","Ministry of National guard <--"));
		getStringVertexIdFromArrayIndex2.add("M");	//Add source & sink as last 2 items in the list
		getStringVertexIdFromArrayIndex2.add("N");

		int source2 = vertexCount2;	//add a source & sink (these are array indexes)
		int sink2 = vertexCount2+1;

		//these must be consecutive indexes. rightHalfVertices starts with the next integer after the last item in leftHaldVertices
		int[] leftHalfVertices2 ={0, 1, 2, 3, 4, 5};	//source is connected to these vertices
		int[] rightHalfVertices2 = {6, 7, 8, 9, 10,11};	//sink is connected to these vertices

		 Max_bipartite graph2BipartiteMatcher = new  Max_bipartite(vertexCountIncludingSourceAndSink2, getStringVertexIdFromArrayIndex2);
		graph2BipartiteMatcher.addEdge(0, 6);
                graph2BipartiteMatcher.addEdge(0, 7);
                graph2BipartiteMatcher.addEdge(1, 11);
                graph2BipartiteMatcher.addEdge(2, 6);
                graph2BipartiteMatcher.addEdge(2, 9);
		graph2BipartiteMatcher.addEdge(3, 8);
		graph2BipartiteMatcher.addEdge(4, 9);
		graph2BipartiteMatcher.addEdge(4, 10);
		graph2BipartiteMatcher.addEdge(5, 11);
		

		graph2BipartiteMatcher.connectSourceToLeftHalf(source2, leftHalfVertices2);
		graph2BipartiteMatcher.connectSinkToRightHalf(sink2, rightHalfVertices2);

		System.out.println("--------Bipartite Matching on Graph-------- ");
                System.out.println("");
		graph2BipartiteMatcher.fordFulkersonMaxFlow(source2 , sink2);

	}
}
