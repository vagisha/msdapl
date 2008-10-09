/**
 * NodeGenerator.java
 * @author Vagisha Sharma
 * Oct 8, 2008
 * @version 1.0
 */
package edu.uwpr.protinfer.graph;

import java.util.List;

/**
 * 
 */
public interface NodeCombiner <T extends Node>{

    public abstract T combineNodes(List<T> nodes) throws InvalidNodeException;
}
