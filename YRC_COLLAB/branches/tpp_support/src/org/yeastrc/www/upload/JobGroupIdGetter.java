/**
 * JobGroupIdGetter.java
 * @author Vagisha Sharma
 * Sep 22, 2010
 */
package org.yeastrc.www.upload;

import org.yeastrc.project.Projects;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;

/**
 * 
 */
public class JobGroupIdGetter {

	private JobGroupIdGetter() {}
	
	public static int get(User user) {
		
		int jobGroupId;
		
		boolean maccoss = Groups.getInstance().isMember(user.getResearcher().getID(), Projects.MACCOSS);
        boolean yates = Groups.getInstance().isMember(user.getResearcher().getID(), Projects.YATES);
        boolean goodlett = Groups.getInstance().isMember(user.getResearcher().getID(), Projects.GOODLETT);
        boolean bruce = Groups.getInstance().isMember(user.getResearcher().getID(), Projects.BRUCE);
        boolean villen = Groups.getInstance().isMember(user.getResearcher().getID(), Projects.VILLEN);
        // NOTE: The groupID values come from org.yeastrc.jqs.queue.MSJobUtils of the JOB_QUEUE project
        if (yates)
            jobGroupId = 0;
        else if (maccoss)
            jobGroupId = 1;
        else if( goodlett )
            jobGroupId = 2;
        else if( bruce )
            jobGroupId = 3;
        else if( villen )
        	jobGroupId = 4;
        else
        	jobGroupId = 1000; // unknown group; needs to be a positive number
        
        return jobGroupId;
	}
}
