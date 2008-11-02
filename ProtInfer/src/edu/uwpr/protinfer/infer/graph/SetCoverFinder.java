package edu.uwpr.protinfer.infer.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SetCoverFinder <L extends Vertex<L>, R extends Vertex<R>>{

    // map of R vertex labels and adjacent L vertex labels
    private Map<String, Set<String>> rToLMap;
    
    // For quick lookup of CoverVertices.
    private Map<String, CoverVertex<L,R>> qVerticesMap= new HashMap<String, CoverVertex<L,R>> ();
    
    // Array of adjacency counts. 
    private List<String>[] adjCounts;
    
    private void initSetCoverFinder(BipartiteGraph<L, R> graph) {
        int maxAdjCount = initMaps(graph);
        initAdjacencyCountArray(maxAdjCount);
    }
    
    private int initMaps(BipartiteGraph<L, R> graph) {

        List<L> verticesL = graph.getLeftVertices();
        rToLMap = new HashMap<String, Set<String>>(graph.getRightVertices().size());
        qVerticesMap= new HashMap<String, CoverVertex<L,R>> (verticesL.size());
        
        int maxAdjCount = 0;
        for (L vertex: verticesL) {
            CoverVertex<L, R> qv = new CoverVertex<L,R>(vertex, graph);
            qVerticesMap.put(vertex.getLabel(), qv);
            
            Set<String> adjRLabels = qv.getAdjRLabels();
            for (String rLabel: adjRLabels) {
                Set<String> adjLLabels = rToLMap.get(rLabel);
                if (adjLLabels == null) {
                    adjLLabels = new HashSet<String>();
                    rToLMap.put(rLabel, adjLLabels);
                }
                adjLLabels.add(vertex.getLabel());
            }
            
            maxAdjCount = Math.max(maxAdjCount, adjRLabels.size());
        }
        return maxAdjCount;
    }
    
    private void initAdjacencyCountArray(int maxAdjCount) {
        adjCounts = new ArrayList[maxAdjCount+1];
        for(CoverVertex<L,R> qv: qVerticesMap.values()) {
            List<String> qvWithAdjCount = adjCounts[qv.getAdjacentCount()];
            if (qvWithAdjCount == null) {
                qvWithAdjCount = new ArrayList<String>();
                adjCounts[qv.getAdjacentCount()] = qvWithAdjCount;
            }
            qvWithAdjCount.add(qv.getVertex().getLabel());
        }
    }
    
    public List<L> getGreedySetCover(BipartiteGraph<L, R> graph) {
        
        initSetCoverFinder(graph);
        
        List<L> setCover = new ArrayList<L>();
        
        for (int i = adjCounts.length - 1 ; i >= 0; i--) {
            
            List<String> qvWithAdjCount = adjCounts[i];
            
            // if there are no vertices with this adjacency count go to the next iteration
            if (qvWithAdjCount == null || qvWithAdjCount.size() == 0)
                continue;
            
            String lLabel = qvWithAdjCount.get(0);
            qvWithAdjCount.remove(0);
            
            // add this to the set cover
            CoverVertex<L,R> qv = qVerticesMap.get(lLabel);
            setCover.add(qv.getVertex());
            
            // get all the labels of all vertices adjacent to this vertex
            // and remove them from the graph. 
            for (String rLabel: qv.getAdjRLabels()) {
                Set<String> lLabels = rToLMap.get(rLabel);
                // other L vertices adjacent to this R vertex
                for (String label: lLabels) {
                    CoverVertex<L, R> qvl = qVerticesMap.get(label);
                    if (qvl == qv)  continue;
                    removeAdjacentVertx(qvl, rLabel);
                }
            }
            
            if (qvWithAdjCount.size() > 0) i++;
        }
        
        return setCover;
    }
    
    private void removeAdjacentVertx(CoverVertex<L,R> qv, String adjLabel) {
        int oldAdjCount = qv.getAdjacentCount();
        qv.removeAdjLabel(adjLabel);
        this.adjCounts[oldAdjCount].remove(qv.getVertex().getLabel());
        // if no adjacent vertices are left, remove this vertex
        if (qv.getAdjacentCount() == 0) {
            qVerticesMap.remove(qv.getVertex().getLabel());
            return;
        }
        List<String> adjList = this.adjCounts[qv.getAdjacentCount()];
        if (adjList == null) {
            adjList = new ArrayList<String>();
            adjCounts[qv.getAdjacentCount()] = adjList;
        }
        adjList.add(qv.getVertex().getLabel());
    }
    
    private static final class CoverVertex <L extends Vertex<L>, R extends Vertex<R>>{
        
        private final L vertex;
        private Set<String> adjRLabels;
        
        public CoverVertex(L vertex, BipartiteGraph<L, R> graph) {
            this.vertex = vertex;
            Set<R> adjR = graph.getAdjacentSetL(vertex);
            adjRLabels = new HashSet<String>(adjR.size());
            for (R adj: adjR) 
                adjRLabels.add(adj.getLabel());
        }
        
        public int getAdjacentCount() {
            return adjRLabels.size();
        }
        
        public void removeAdjLabel(String label) {
            adjRLabels.remove(label);
        }
        
        public Set<String> getAdjRLabels() {
            return adjRLabels;
        }
        
        public L getVertex() {
            return vertex;
        }
    }
}
