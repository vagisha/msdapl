/**
 * NodeGenerator.java
 * @author Vagisha Sharma
 * Oct 8, 2008
 * @version 1.0
 */
package edu.uwpr.protinfer.graph;

/**
 * 
 */
public interface NodeCombiner <T extends Node>{

    public abstract T combineNodes(T... nodes) throws InvalidNodeException;
}
