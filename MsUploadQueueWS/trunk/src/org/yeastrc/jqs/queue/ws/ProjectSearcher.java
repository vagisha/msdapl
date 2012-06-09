/**
 * MsJobSearcher.java
 * @author Vagisha Sharma
 * Sep 10, 2010
 */
package org.yeastrc.jqs.queue.ws;

import java.sql.SQLException;

import org.yeastrc.data.InvalidIDException;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.project.Researcher;
import org.yeastrc.www.user.User;

/**
 * 
 */
public class ProjectSearcher {

	private ProjectSearcher() {}
	
	private static ProjectSearcher instance = null;
	
	public static synchronized ProjectSearcher getInstance() {
		if(instance == null)
			instance = new ProjectSearcher();
		
		return instance;
	}
	
	public Project search(int projectId) {
		
		if (projectId == 0)
			return null;
		
		try {
			return ProjectFactory.getProject(projectId);
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (InvalidIDException e1) {
			e1.printStackTrace();
		}
		return null;
	}
}
