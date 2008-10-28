package edu.uwpr.protinfer.infer.graph;

import junit.framework.TestCase;
import edu.uwpr.protinfer.infer.graph.GraphTest.TestVertex;

public class BipartiteGraphTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testAddVertex() {
        BipartiteGraph graph =  new BipartiteGraph();
        Vertex v1 = new TestVertex("vertex1");
        Vertex v2 = new TestVertex("vertex2");
        
        Vertex added = graph.addVertex(v1);
        assertTrue(v1 == added);
        
        added = graph.addVertex(v2);
        assertTrue(v2 == added);
        
        graph.addVertex(added);
        assertEquals(2, graph.getVertices().size());
        
        graph.addVertex(v1);
        graph.addVertex(v2);
        assertEquals(2, graph.getVertices().size());
        assertEquals(2, graph.getLeftVertices().size());
        assertEquals(0, graph.getRightVertices().size());
        
        Vertex v3 = new TestVertex("vertex3");
        Vertex v3_a = graph.addVertex(v3);
        Vertex v3_b = graph.addVertex(new TestVertex("vertex3"));
        assertTrue(v3_a == v3_b);
    }

    public final void testAddLeftVertex() {
        BipartiteGraph graph =  new BipartiteGraph();
        Vertex v1 = new TestVertex("vertex1");
        Vertex v2 = new TestVertex("vertex2");
        
        assertEquals(0, graph.getLeftVertices().size());
        graph.addLeftVertex(v1);
        assertEquals(1, graph.getLeftVertices().size());
        graph.addLeftVertex(v1);
        assertEquals(1, graph.getLeftVertices().size());
        graph.addLeftVertex(v2);
        assertEquals(2, graph.getLeftVertices().size());
        
        assertEquals(0, graph.getRightVertices().size());
        // should NOT be able to add the same vertex to the set of right vertices.
        graph.addRightVertex(v1);
        assertEquals(0, graph.getRightVertices().size());
        assertEquals(2, graph.getLeftVertices().size());
        assertEquals(2, graph.getVertices().size());
    }

    public final void testAddRightVertex() {
        BipartiteGraph graph =  new BipartiteGraph();
        Vertex v1 = new TestVertex("vertex1");
        Vertex v2 = new TestVertex("vertex2");
        
        assertEquals(0, graph.getRightVertices().size());
        graph.addRightVertex(v1);
        assertEquals(1, graph.getRightVertices().size());
        graph.addRightVertex(v1);
        assertEquals(1, graph.getRightVertices().size());
        graph.addRightVertex(v2);
        assertEquals(2, graph.getRightVertices().size());
        
        assertEquals(0, graph.getLeftVertices().size());
        // should NOT be able to add the same vertex to the set of left vertices.
        graph.addLeftVertex(v1);
        assertEquals(0, graph.getLeftVertices().size());
        assertEquals(2, graph.getRightVertices().size());
        assertEquals(2, graph.getVertices().size());
    }

    public final void testAddEdge() {
        BipartiteGraph graph =  new BipartiteGraph();
        Vertex v1 = new TestVertex("vertex1");
        Vertex v2 = new TestVertex("vertex2");
        
        // add an edge v1 <--> v2
        graph.addEdge(v1, v2);
        assertEquals(2, graph.getVertices().size());
        assertEquals(1, graph.getEdgeCount());
        assertEquals(1, graph.getAdjacentVertices(v1).size());
        assertEquals(1, graph.getAdjacentVertices(v2).size());
        assertEquals(1, graph.getLeftVertices().size());
        assertEquals(1, graph.getRightVertices().size());
        
        // add another edge v1 <--> v3
        Vertex v3 = new TestVertex("vertex3");
        graph.addEdge(v1, v3);
        assertEquals(3, graph.getVertices().size());
        assertEquals(2, graph.getEdgeCount());
        assertEquals(2, graph.getAdjacentVertices(v1).size());
        assertEquals(1, graph.getAdjacentVertices(v2).size());
        assertEquals(1, graph.getAdjacentVertices(v3).size());
        assertEquals(1, graph.getLeftVertices().size());
        assertEquals(2, graph.getRightVertices().size());
        
        // try to add v1 <--> v2 edge again. It should not get added a second time
        graph.addEdge(v1, v2);
        assertEquals(3, graph.getVertices().size());
        assertEquals(2, graph.getEdgeCount());
        assertEquals(2, graph.getAdjacentVertices(v1).size());
        assertEquals(1, graph.getAdjacentVertices(v2).size());
        assertEquals(1, graph.getLeftVertices().size());
        assertEquals(2, graph.getRightVertices().size());
        
        // add an edge with vertices reversed (SHOULD NOT ADD THE EDGE)
        graph.addEdge(v2, v1);
        assertEquals(3, graph.getVertices().size());
        assertEquals(2, graph.getEdgeCount());
        assertEquals(2, graph.getAdjacentVertices(v1).size());
        assertEquals(1, graph.getAdjacentVertices(v2).size());
        assertEquals(1, graph.getLeftVertices().size());
        assertEquals(2, graph.getRightVertices().size());
        
        // add an edge with a new Vertex object having the same label as v1. Edge should not get added
        graph.addEdge(v1, new TestVertex("vertex2"));
        assertEquals(3, graph.getVertices().size());
        assertEquals(2, graph.getEdgeCount());
        assertEquals(2, graph.getAdjacentVertices(v1).size());
        assertEquals(1, graph.getAdjacentVertices(v2).size());
        assertEquals(1, graph.getLeftVertices().size());
        assertEquals(2, graph.getRightVertices().size());
    }

    public final void testGetAdjacentVertices() {
        fail("Not yet implemented"); // TODO
    }

    public final void testContainsEdge() {
        fail("Not yet implemented"); // TODO
    }

    public final void testGetVertices() {
        fail("Not yet implemented"); // TODO
    }

    public final void testGetLeftVertices() {
        fail("Not yet implemented"); // TODO
    }

    public final void testGetRightVertices() {
        fail("Not yet implemented"); // TODO
    }

    public final void testCombineVertices() {
        fail("Not yet implemented"); // TODO
    }

    public final void testCombineLeftVertices() {
        fail("Not yet implemented"); // TODO
    }

    public final void testCombineRightVertices() {
        fail("Not yet implemented"); // TODO
    }

    public final void testRemoveVertex() {
        fail("Not yet implemented"); // TODO
    }

    public final void testRemoveLeftVertex() {
        fail("Not yet implemented"); // TODO
    }

    public final void testRemoveRightVertex() {
        fail("Not yet implemented"); // TODO
    }

}
