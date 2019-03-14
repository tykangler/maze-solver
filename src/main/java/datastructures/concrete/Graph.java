package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IEdge;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import datastructures.interfaces.ISet;
import misc.exceptions.NoPathExistsException;

/**
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a graph.
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends IEdge<V> & Comparable<E>> {

    private IDictionary<V, ISet<E>> graph;
    private IDictionary<V, Vertex> vertexMappings;
    private int numEdges;
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //
    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated than usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've constrained Graph
    //   so that E *must* always be an instance of IEdge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the IEdge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.

    /**
     * Constructs a new graph based on the given vertices and edges.
     *
     * @throws IllegalArgumentException if any of the edges have a negative weight
     * @throws IllegalArgumentException if one of the edges connects to a vertex not
     *                                  present in the 'vertices' list
     * @throws IllegalArgumentException if vertices or edges are null or contain null
     */
    public Graph(IList<V> vertices, IList<E> edges) {
        graph = new ChainedHashDictionary<V, ISet<E>>();
        vertexMappings = new ChainedHashDictionary<V, Vertex>();
        for (V vertex : vertices) {
            if (vertex == null) {
                throw new IllegalArgumentException();
            }
            graph.put(vertex, new ChainedHashSet<E>());
            vertexMappings.put(vertex, new Vertex(vertex));
        }
        for (E edge : edges) {
            if (edge == null || edge.getWeight() < 0 || !graph.containsKey(edge.getVertex1()) || 
            !graph.containsKey(edge.getVertex2())) {
                    throw new IllegalArgumentException();
            }
            graph.get(edge.getVertex1()).add(edge);
            graph.get(edge.getVertex2()).add(edge);
        }
        numEdges += edges.size();
    }

    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     *
     * @throws IllegalArgumentException if any of the edges have a negative weight
     * @throws IllegalArgumentException if one of the edges connects to a vertex not
     *                                  present in the 'vertices' list
     * @throws IllegalArgumentException if vertices or edges are null or contain null
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        this(setToList(vertices), setToList(edges));
    }

    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        if (set == null) {
            throw new IllegalArgumentException();
        }
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
        return graph.size();
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        return numEdges;
    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        IDisjointSet<V> components = new ArrayDisjointSet<V>();
        IPriorityQueue<E> sortedEdges = new ArrayHeap<E>();
        ISet<E> mstEdges = new ChainedHashSet<E>();
        for (KVPair<V, ISet<E>> vertices : graph) {
            components.makeSet(vertices.getKey());
            for (E edge : vertices.getValue()) {
                sortedEdges.insert(edge);
            }
        }
        while (!sortedEdges.isEmpty()) {
            E edge = sortedEdges.removeMin();
            if (components.findSet(edge.getVertex1()) != components.findSet(edge.getVertex2())) {
                mstEdges.add(edge);
                components.union(edge.getVertex1(), edge.getVertex2());
            }
        }
        return mstEdges;
    }

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     * @throws IllegalArgumentException if start or end is null
     */
    public IList<E> findShortestPathBetween(V start, V end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException();
        }
        IPriorityQueue<Vertex> minPQ = new ArrayHeap<Vertex>();
        vertexMappings.get(start).dist = 0.0;
        minPQ.insert(vertexMappings.get(start));
        return dijkstra(minPQ, end);
    }

    private IList<E> dijkstra(IPriorityQueue<Vertex> minPQ, V end) {
        while (!minPQ.isEmpty()) {
            Vertex currVertex = minPQ.removeMin();
            ISet<E> edges = graph.get(currVertex.data);
            for (E edge : edges) {
                double newDist = currVertex.dist + edge.getWeight();
                Vertex otherVertex = vertexMappings.get(edge.getOtherVertex(currVertex.data));
                if (newDist < otherVertex.dist) {
                    otherVertex.dist = newDist;
                    minPQ.insert(otherVertex);
                    otherVertex.predecessors.add(edge);
                }
            }
        }
        return vertexMappings.get(end).predecessors;
    }



    private class Vertex implements Comparable<Vertex> {
        V data;
        IList<E> predecessors;
        double dist;
        
        Vertex(V data) {
            this(data, Double.POSITIVE_INFINITY);
        }

        Vertex(V data, double dist) {
            this.data = data;
            predecessors = new DoubleLinkedList<E>();
            this.dist = dist;
        }

        public int compareTo(Vertex other) {
           return Double.compare(this.dist, other.dist);
        }
    }
}
