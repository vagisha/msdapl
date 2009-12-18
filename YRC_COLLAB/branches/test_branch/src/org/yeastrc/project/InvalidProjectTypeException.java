package org.yeastrc.project;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version 2003-02-19
 */
 public class InvalidProjectTypeException extends RuntimeException {

 	/** Constructs an InvalidProjectTypeException with no detail message. */
 	public InvalidProjectTypeException () {
 		super();
 	}

	/** Constructs an InvalidProjectTypeException with the specified detail message. */
 	public InvalidProjectTypeException (String message) {
 		super(message);
 	}

 }