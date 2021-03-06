package eu.fbk.hlt.nlp.cluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import eu.fbk.hlt.nlp.criteria.Abbreviation;
import eu.fbk.hlt.nlp.criteria.Acronym;
import eu.fbk.hlt.nlp.criteria.it.Article;
import eu.fbk.hlt.nlp.criteria.Entailment;
import eu.fbk.hlt.nlp.criteria.ModifierSwap;
import eu.fbk.hlt.nlp.criteria.Translation;
import eu.fbk.hlt.nlp.criteria.it.PrepositionalVariant;
import eu.fbk.hlt.nlp.criteria.it.SingularPlural;
import eu.fbk.hlt.nlp.criteria.it.Synonymy;

/**
 * 
 * This class represents a graph. It uses an adjacency list to store both its
 * vertices and edges. The disconnected graphs (clusters) in the adjacency list
 * are printed by the breadth first traversal algorithm.
 * 
 * @author rzanoli
 *
 */
public class Graph {

	private int v; // no of vertices in a graph
	private Vector<int[]>[] adj; // array of linked list for adjacency list representation
	// private Vector<Integer>[] adj;
	//private Keyphrases keyphrases;

	/**
	 * The constructor
	 * 
	 * @param v
	 *            the number of vertices in the graph
	 */
	@SuppressWarnings("unchecked")
	public Graph(int v) {

		//this.keyphrases = keyphrases;
		
		this.v = v;
		adj = new Vector[v];
		for (int i = 0; i < v; i++) {
			adj[i] = new Vector<int[]>();
			// adj[i] = new Vector<Integer>();
		}

	}

	/**
	 * The constructor
	 * 
	 * @param fileName
	 *            the file containing the graph to load
	 */
	public Graph(File fileName) throws Exception {

		loadAdjacencyList(fileName);

	}

	/**
	 * The constructor
	 * 
	 * Given a graph and a number of vertices that is >= to the number of vertices
	 * in such a graph it return a new graph containing the given graph and whose
	 * size is >= to the graph in input. It is used to create a new graph starting
	 * by a given graph and that contains more vertices than the initial graph.
	 * 
	 * @param fileName
	 *            the file containing the graph to load
	 */
	public Graph(int v, Graph graph) {

		this.v = v;
		adj = new Vector[v];
		for (int i = 0; i < v; i++) {
			adj[i] = new Vector<int[]>();
			// adj[i] = new Vector<Integer>();
		}

		for (int i = 0; i < graph.v; i++) {
			adj[i] = graph.adj[i];
		}

	}

	/**
	 * Add an edge into a graph form vertex source to vertex target.
	 *
	 * @param i
	 *            source vertex
	 * @param j
	 *            target vertex
	 * @param edge
	 *            the edge label
	 */
	public void add(int i, int j, int edge) {

		int[] vertexAndEdge = new int[2];
		vertexAndEdge[0] = j;
		vertexAndEdge[1] = edge;
		// adj[i].add(j);
		adj[i].add(vertexAndEdge);

	}

	/**
	 * Print BFS traversal from a given source vertex s to print the disconnected
	 * graph (clusters) with the given vertex source. The produced disconnected
	 * graph is in a format like:
	 * 
	 * 1 1 2 3 1 2 0 2 3 1
	 * 
	 * where rows containing numbers in a number <= 2 are vertices while rows
	 * containing 3 numbers are edges between vertices.
	 * 
	 * As regards vertices, '1' after a vertex index (e.g., 1 1) means that it is
	 * the root of the graph. vertices that are not followed by '1' are other
	 * vertices of the graph.
	 * 
	 * Regarding edges the first two numbers (e.g., 1 2) are the vertices index
	 * connected by the edge and the additional number is the edge label.
	 * 
	 * @param s
	 *            the source vertex
	 * @param visited
	 *            the vertices that are part of a graph; this list will be used to
	 *            remove from the output graph those graphs whose root vertex is
	 *            part of another graph.
	 * 
	 * @return
	 */
	public String BFSUtil(int s, boolean visited[]) {

		// mark all the vertices of the current graph as not visited
		Set<Integer> visitedInCurrentGraph = new HashSet<Integer>();

		// the vertices of the disconnected graph
		StringBuffer vertices = new StringBuffer();
		// and its edges
		StringBuffer edges = new StringBuffer();
		// if the current node is the root of the disconnected graph
		boolean root = true;

		// create a queue for BFS
		LinkedList<Integer> queue = new LinkedList<Integer>();
		// mark the current vertex as visited and enqueue it
		queue.add(s);
		// visited[s] = true;
		visitedInCurrentGraph.add(s);
		while (queue.size() != 0) {
			// dequeue a vertex from queue and print it
			s = queue.pop();
			if (root == true) {
				vertices.append(s + " " + 1);
				root = false;
			} else
				vertices.append(s);
			vertices.append("\n");

			// create an iterator, to get all the adjacent vertices of dequeued
			// vertex s
			// if the adjacent vertex is not visited then mark it visited and
			// enqueue it
			// Iterator<Integer> i = adj[s].iterator();
			Iterator<int[]> i = adj[s].iterator();
			while (i.hasNext()) {
				// int n = i.next();
				int[] elementAndCriteria = i.next();
				// int criteria = 0;
				int n = elementAndCriteria[0];
				int criteria = elementAndCriteria[1];
				if (!visitedInCurrentGraph.contains(n)) {
					visitedInCurrentGraph.add(n);
					queue.add(n);
					visited[n] = true;
				}
				edges.append(s);
				edges.append(" ");
				edges.append(n);
				edges.append(" ");
				edges.append(criteria);
				edges.append("\n");
				
			} // end of inner while loop
				// System.out.println();
		} // end of outer while loop

		/*
		if (vertices.toString().split("\n").length > 100) {
			String[] verticesArray = vertices.toString().split("\n");
			for (int i = 0; i < verticesArray.length; i++) {
				if (verticesArray[i].indexOf(" ") != -1)
					System.out.println("vertice:" + verticesArray[i].split(" ")[0] + "\t" + keyphrases.get(Integer.parseInt(verticesArray[i].split(" ")[0])).getText() + "\t1");
				else
					System.out.println("vertice:" + verticesArray[i] + ":" + keyphrases.get(Integer.parseInt(verticesArray[i])).getText());
			}
			System.out.println("--------------------------------------");
			System.out.println(edges);
			System.out.println("======================================");
			System.exit(0);
		}*/
			
 		// return the vertices and the edges among them
		return vertices.toString() + edges.toString();

	}

	/**
	 * Print BFS traversal of the graph to print all the disconnected graphs
	 * separated each one by a space line.
	 * 
	 * @param s
	 *            the vertex to start printing the graphs.
	 * 
	 * @return the graph containing all the disconnected graph.
	 */
	public String BFS(int s) {

		StringBuffer graphsString = new StringBuffer();

		// mark all the vertices as not visited; this list will be used
		// to remove from the output graph those graphs whose root vertex
		// is part of another graph.
		boolean visited[] = new boolean[this.v];

		// get all the disconnected graphs in the adjacency list
		Map<Integer, String> disconnectedGraphs = new HashMap<Integer, String>();
		for (int i = 0; i < this.v; i++) {
			if (visited[i] == false) {
				String graph_i = BFSUtil(i, visited);
				//System.out.println(graph_i);
				//System.out.print(i + " ");
				/*
				Iterator<Integer> it = disconnectedGraphs.keySet().iterator();
				while(it.hasNext()) {
					int id = it.next();
					String tmpGraph = disconnectedGraphs.get(id);
					if (tmpGraph.equals(graph_i)) {
						System.err.println(graph_i);
						System.err.println("============================");
					}
				}
				*/
				disconnectedGraphs.put(Integer.valueOf(i), graph_i);
			}
		}

		// among the disconnected graphs, it only considers the ones whose root vertex
		// is not part of another graph.
		Iterator<Integer> it = disconnectedGraphs.keySet().iterator();
		while (it.hasNext()) {
			int vertex = it.next();
			if (visited[vertex] == false) {
				graphsString.append(disconnectedGraphs.get(vertex));
				graphsString.append("\n");
				// System.out.println(graphsToPrint.get(vertex));
			}
		}

		// System.out.print(graphsString.toString());
		//System.out.println("====================");

		return graphsString.toString();

	}

	/**
	 * Print the adjacency list
	 * 
	 */
	public void printAdjacencyList(File fileName) throws Exception {

		BufferedWriter bw = null;
		FileWriter fw = null;

		try {

			fw = new FileWriter(fileName);
			bw = new BufferedWriter(fw);

			bw.write(adj.length + "\n"); // number of vertices

			for (int i = 0; i < adj.length; i++) {
				bw.write(String.valueOf(i));
				// Vector<Integer> list = adj[i];
				Vector<int[]> list = adj[i];
				if (list.size() > 0)
					bw.write("\t");
				for (int j = 0; j < list.size(); j++) {
					// System.out.print(list.get(j) + " ");
					bw.write(list.get(j)[0] + "," + list.get(j)[1]);
					if (j < list.size() - 1)
						bw.write(" ");
				}
				bw.write("\n");
			}

		} finally {

			if (bw != null)
				bw.close();

			if (fw != null)
				fw.close();

		}

	}

	/**
	 * Load the Adjacency List
	 * 
	 */
	private void loadAdjacencyList(File fileName) throws Exception {

		BufferedReader br = null;
		FileReader fr = null;

		try {

			fr = new FileReader(fileName);
			br = new BufferedReader(fr);

			String line;

			int lineCounter = 0;
			while ((line = br.readLine()) != null) {
				if (lineCounter == 0) {
					v = Integer.parseInt(line);
					adj = new Vector[v];
					for (int i = 0; i < v; i++) {
						adj[i] = new Vector<int[]>();
						// adj[i] = new Vector<Integer>();
					}
				} else {
					String[] splitLine = line.split("\t");
					int root = Integer.parseInt(splitLine[0]);

					if (splitLine.length == 2) {
						String[] vertices = splitLine[1].split(" ");
						Vector<int[]> verticesList = adj[root];
						for (int i = 0; i < vertices.length; i++) {
							int[] vertexAndEdge = new int[2];
							vertexAndEdge[0] = Integer.parseInt(vertices[i].split(",")[0]);
							vertexAndEdge[1] = Integer.parseInt(vertices[i].split(",")[1]);
							verticesList.add(vertexAndEdge);
						}
					}

				}
				lineCounter++;
			}

		} finally {

			if (br != null)
				br.close();

			if (fr != null)
				fr.close();

		}

	}

	/**
	 * Get some graph statistics
	 * 
	 * @graph the graph
	 * 
	 * @return the statistics
	 */
	public static String getGraphStatistics(String graph) {

		// System.out.println(graph);;

		StringBuffer result = new StringBuffer();

		String[] splitGraphs = graph.split("\n");
		int nNodes = 0;
		int nTotNodes = 0;
		int nRoots = 0;
		int prepositionalVariant = 0;
		int abbreviation = 0;
		int entailment = 0;
		int acronym = 0;
		int modifierswap = 0;
		int singularplural = 0;
		int synonymy = 0;
		//int equality = 0;
		int article = 0;
		int translation = 0;
		Map<Integer, Integer> nodeDistribution = new TreeMap<Integer, Integer>();
		for (int i = 0; i < splitGraphs.length; i++) {
			String[] splitLine = splitGraphs[i].split(" ");
			if (splitGraphs[i].equals("")) {
				if (nodeDistribution.containsKey(nNodes)) {
					int freq = nodeDistribution.get(nNodes);
					freq++;
					nodeDistribution.put(nNodes, freq);
				} else
					nodeDistribution.put(nNodes, 1);
				nNodes = 0;
			} else if (splitLine.length == 2) { // root vertex
				nNodes++;
				nTotNodes++;
				nRoots++;
			} else if (splitLine.length <= 1) { // vertices
				nNodes++;
				nTotNodes++;
			} else { // edges
				if (Integer.parseInt(splitLine[2]) == Abbreviation.id)
					abbreviation++;
				else if (Integer.parseInt(splitLine[2]) == Acronym.id)
					acronym++;
				else if (Integer.parseInt(splitLine[2]) == Entailment.id)
					entailment++;
				else if (Integer.parseInt(splitLine[2]) == ModifierSwap.id)
					modifierswap++;
				else if (Integer.parseInt(splitLine[2]) == SingularPlural.id)
					singularplural++;
				else if (Integer.parseInt(splitLine[2]) == PrepositionalVariant.id)
					prepositionalVariant++;
				else if (Integer.parseInt(splitLine[2]) == Synonymy.id) 
					synonymy++;
				else if (Integer.parseInt(splitLine[2]) == Article.id)
					article++;
				else if (Integer.parseInt(splitLine[2]) == Translation.id)
					translation++;
			}
		}
		if (nodeDistribution.containsKey(nNodes)) {
			int freq = nodeDistribution.get(nNodes);
			freq++;
			nodeDistribution.put(nNodes, freq);
		} else
			nodeDistribution.put(nNodes, 1);

		result.append("#Graphs (clusters) produced: " + nRoots + "\n");
		result.append("#Vertices: " + nTotNodes + "\n");
		result.append(
				"#Edges: " + (article + prepositionalVariant + abbreviation + acronym + entailment + modifierswap + singularplural + synonymy + translation)
						+ " (article:" + article + " abbreviation:" + abbreviation + " " + "acronym:" + acronym
						+ " entailment:" + entailment + " modifier swap:" + modifierswap + " singular/plural:"
						+ singularplural + " synonym:" + synonymy + " prepositional variant:" + prepositionalVariant 
						+ " translation:" + translation + ")" + "\n");

		result.append("\nDistribution (#Graphs, #Vertices):\n");
		Iterator<Integer> it = nodeDistribution.keySet().iterator();
		while (it.hasNext()) {
			int key = it.next();
			int value = nodeDistribution.get(key);
			result.append("\t" + value + "\t" + key + "\n");
		}

		return result.toString();

	}

	/*
	public static void main(String[] args) {

		Graph g = new Graph(5);
		g.add(0, 1, 0);
		g.add(0, 2, 0);
		g.add(1, 0, 0);
		g.add(1, 2, 0);
		g.add(3, 3, 0);
		g.add(2, 4, 0);

		System.out.println("Following is Breadth First Traversal " + "(starting from vertex 0)");

		// g.printAdjacencyList();
		// System.out.println("========================");
		String graphsString = g.BFS(0);
		System.out.println(graphsString);
	}*/
}
