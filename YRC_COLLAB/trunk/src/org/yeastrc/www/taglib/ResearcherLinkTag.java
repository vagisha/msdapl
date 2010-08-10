/*
 * ProteinLinkTag.java
 * Created on Aug 6, 2010
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.util.RequestUtils;
import org.apache.struts.util.ResponseUtils;
import org.yeastrc.project.Researcher;


/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Feb 2, 2006
 */

public class ResearcherLinkTag extends TagSupport {
	
    /**
     * Name of the bean that contains the data we will be rendering.
     */
    protected String name = null;

    public String getName() {
        return (this.name);
    }

    public void setName(String name) {
        this.name = name;
    }


    /**
     * Name of the property to be accessed on the specified bean.
     */
    protected String property = null;

    public String getProperty() {
        return (this.property);
    }

    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * The scope to be searched to retrieve the specified bean.
     */
    protected String scope = null;

    public String getScope() {
        return (this.scope);
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
    
    
    /**
     * Process the start tag.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {

        // Look up the requested property value
        Researcher researcher = (Researcher)(RequestUtils.lookup(pageContext, name, property, scope));
        if (researcher == null)
            return (SKIP_BODY);  // Nothing to output
        
        String output = "";
        
        try {
 
        	output = "<a href=\"/yrc/viewResearcher.do?id=" + researcher.getID() + "\">";
        	output += researcher.getListing() + " (" + researcher.getOrganization() + ")</a>";

        } catch (Exception e) {
        	output = "ERROR";
        }
        

        // Print this property value to our output writer, suitably filtered
        ResponseUtils.write(pageContext, output);

        // Continue processing this page
        return (SKIP_BODY);

    }
    
    
    
    
    /**
     * Release all allocated resources.
     */
    public void release() {
        super.release();
        name = null;
        property = null;
        scope = null;
    }
    
	
}
