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
    private IDictionary<V, VertexInfo> costInfo;
    private int numEdges;

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
        costInfo = new ChainedHashDictionary<V, VertexInfo>();
        for (V vertex : vertices) {
            if (vertex == null) {
                throw new IllegalArgumentException();
            }
            graph.put(vertex, new ChainedHashSet<E>());
            costInfo.put(vertex, new VertexInfo(vertex));
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
        IPriorityQueue<VertexInfo> mpq = new ArrayHeap<VertexInfo>();
        VertexInfo startInfo = costInfo.get(start);
        startInfo.dist = 0.0;
        mpq.insert(startInfo);
        runDijkstra(mpq, end);
        return constructPath(start, end);
    }
    
    /**
     * Runs Dijkstra's shortest-path algorithm on the graph with the given end node.
     * The start node is already initialized in the priority queue with a dist value of 0.
     * Fills in the predecessor and distance values of each node visited and stops once the
     * end is reached. 
     * 
     * @throws NoPathExistsException if a path doesn't exist
     */
    private void runDijkstra(IPriorityQueue<VertexInfo> mpq, V end) {
        ISet<V> visited = new ChainedHashSet<V>();
        VertexInfo curr = mpq.peekMin();
        while (!mpq.isEmpty() && !curr.data.equals(end)) {
            curr = mpq.removeMin();
            if (!visited.contains(curr.data) && !curr.data.equals(end)) {
                for (E edge : graph.get(curr.data)) {
                    double newDist = curr.dist + edge.getWeight();
                    VertexInfo otherVertex = costInfo.get(edge.getOtherVertex(curr.data)); 
                    if (newDist < otherVertex.dist) {
                        otherVertex.dist = newDist;
                        otherVertex.pre = edge;
                        mpq.insert(new VertexInfo(otherVertex.data, edge, newDist));
                    }
                }
                visited.add(curr.data);
            }
        }
        if (!end.equals(curr.data)) {
            throw new NoPathExistsException();
        }
    }

    /**
     * Constructs a path of edges given start and end nodes that have predecessor fields 
     * filled in.
     * 
     * @return the path in list form
     */
    private IList<E> constructPath(V start, V end) {
        IList<E> path = new DoubleLinkedList<E>();
        VertexInfo currInfo = costInfo.get(end);
        while (!currInfo.data.equals(start)) {
            path.insert(0, currInfo.pre);
            V temp = currInfo.pre.getOtherVertex(currInfo.data);
            currInfo = costInfo.get(temp);
        }
        for (KVPair<V, VertexInfo> cost : costInfo) {
            cost.getValue().reset();
        }
        return path;
    }

    /**
     * Used when finding the shortest path between two vertices in a graph
     * Contains a given vertex's data, predecessor, and distance from source.
     * Resets each time findShortestPathBetween() is called.
     */
    private class VertexInfo implements Comparable<VertexInfo> {
        V data;
        E pre;
        double dist;

        VertexInfo(V data) {
            this(data, null, Double.POSITIVE_INFINITY);
        }

        VertexInfo(V data, E pre, double dist) {
            this.data = data;
            this.pre = pre;
            this.dist = dist;
        }

        public void reset() {
            pre = null;
            dist = Double.POSITIVE_INFINITY;
        }

        public int compareTo(VertexInfo other) {
            return Double.compare(this.dist, other.dist);
        }
    }

}
