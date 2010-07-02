/**
 * 
 */
package org.yeastrc.www.taglib;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;
import org.apache.struts.util.RequestUtils;
import org.yeastrc.www.go.GOTree;
import org.yeastrc.www.go.GOTreeNode;


/**
 * GOTreeNodeTag.java
 * @author Vagisha Sharma
 * Jun 25, 2010
 * 
 */
public class GOTreeNodeTag extends TagSupport {

	private String treeName;  // name of the bean that contains the GO Tree
	
	private static final Logger log = Logger.getLogger(GOTreeNodeTag.class.getName());
    
    public String getTreeName() {
		return treeName;
	}

	public void setTreeName(String treeName) {
		this.treeName = treeName;
	}

	public int doStartTag() throws JspException{
        
        if(this.treeName == null)   return SKIP_BODY;
        
        GOTree goTree = (GOTree)RequestUtils.lookup(pageContext, treeName, null);
        if(goTree == null)     return SKIP_BODY;
        
        ServletContext context = pageContext.getServletContext();
        String contextPath = context.getContextPath();
        contextPath = contextPath + "/";
        
        try {
            // Get our writer for the JSP page using this tag
            JspWriter writer = pageContext.getOut();

            
            for(GOTreeNode root: goTree.getRoots()) {
            	writer.print("<ul>");
            	printNode(root, writer);
            	writer.print("</ul>");	
            }
            
            // They are authenticated
            return SKIP_BODY;

        }
        catch (Exception e) {
            log.error("Exception in GOTreeNodeTag", e);
            throw new JspException("Error: Exception while writing to client: " + e.getMessage());
        }
    }

	private void printNode(GOTreeNode node, JspWriter writer) throws IOException {
		boolean hasChildren  = false;
		if(!node.hasChildren() && !node.isLeaf()) {
			hasChildren = true;
		}
		String nodeId = node.getGoNode().getAccession();
		nodeId = nodeId.replaceAll("GO:", "");
		if(!hasChildren)
			writer.print("<li id='"+nodeId+"'>");
		else
			writer.print("<li class='jstree-closed' id='"+nodeId+"'>");
		
		if(node.isMarked()) {
			writer.print("<span class='slim-node'>");
		}
		writer.write("<a target='go_window' href='http://www.godatabase.org/cgi-bin/amigo/go.cgi?action=query&view=query&search_constraint=terms&query="+node.getGoNode().getAccession()+"'>");
		writer.write(node.getGoNode().getAccession()+": "+node.getGoNode().getName());
		writer.write("</a>");
		
		writer.print("<span ");
		if(node.isLeaf())
			writer.print("class=\'green\'>");
		else
			writer.print("class=\'red\'>");
		writer.print(node.getAnnotationLabel());
		
		writer.print("</span>");
		writer.print("</span>");
		//writer.write("\n");
		
		if(!node.isLeaf()) {
			writer.print("<ul>");
			for(GOTreeNode child: node.getChildren()) {
	    		printNode(child, writer);
	    	}
			writer.print("</ul>");
		}
		writer.write("</li>");
	}

	public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    public void release() {
        super.release();
    }
}
