/**
 * TableTag.java
 * @author Vagisha Sharma
 * Apr 4, 2009
 * @version 1.0
 */
package org.yeastrc.www.taglib;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.util.RequestUtils;

/**
 * 
 */
public class TableTag extends TagSupport {

    private String name;  // name of the bean that contains the table data
    private String tableId;
    private String tableClass; 
    private boolean center = false;
    
    public String getName() {
        return (this.name);
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setTableId(String id) {
        this.tableId = id;
    }
    public String getTableId() {
        return tableId;
    }
    
    public void setTableClass(String tblClass) {
        this.tableClass = tblClass;
    }
    public String getTableClass() {
        return tableClass;
    }
    
    public void setCenter(String center) {
        this.center = Boolean.valueOf(center);
    }
    public String getCenterClass() {
        return Boolean.toString(center);
    }
    
    public int doStartTag() throws JspException{
        
        if(this.name == null)   return SKIP_BODY;
        
        Tabular tabular = (Tabular)RequestUtils.lookup(pageContext, name, null);
        if(tabular == null)     return SKIP_BODY;
        tabular.tabulate();
        
        ServletContext context = pageContext.getServletContext();
        String contextPath = context.getContextPath();
        contextPath = contextPath + "/";
        
        try {
            // Get our writer for the JSP page using this tag
            JspWriter writer = pageContext.getOut();

            // start table
            String tblAttrib = "";
            if(tableId != null)
                tblAttrib = tblAttrib + "id="+tableId+" ";
            if(tableClass != null)
                tblAttrib = tblAttrib + "class="+tableClass+" ";
            if(center) 
                tblAttrib = tblAttrib + "center=true ";
            writer.print("<table "+tblAttrib+">\n");
            
            
            // print header
            writer.print("<thead>\n");
            writer.print("<tr>\n");
            for(String header: tabular.columnNames()) {
                writer.print("<th>"+header+"</th>\n");
            }
            writer.print("</tr>\n");
            writer.print("</thead>\n");
            
            // print data
            writer.print("<tbody>\n");
            for(int i = 0; i < tabular.rowCount(); i++) {
                TableRow row = tabular.getRow(i);
                writer.print("<tr>\n");
                for(TableCell cell: row.getCells()) {
                    writer.print("<td>");
                    if(cell.getHyperlink() != null) {
                        writer.print("<a href=\""+contextPath+cell.getHyperlink()+"\">");
                    }
                    if(cell.getData() != null) {
                        writer.write(cell.getData());
                    }
                    if(cell.getHyperlink() != null) {
                        writer.print("</a>");
                    }
                    writer.print("</td\n>");
                }
                writer.print("</tr>\n");
            }
            writer.print("</tbody>\n");
            
            // end table
            writer.print("</table>");
            
            
            // They are authenticated
            return SKIP_BODY;

        }
        catch (Exception e) {
            throw new JspException("Error: Exception while writing to client" + e.getMessage());
        }
    }

    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    public void release() {
        super.release();
    }
}
