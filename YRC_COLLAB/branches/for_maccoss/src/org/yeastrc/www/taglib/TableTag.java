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

import org.apache.log4j.Logger;
import org.apache.struts.util.RequestUtils;
import org.yeastrc.ms.domain.search.SORT_ORDER;
import org.yeastrc.www.misc.TableCell;
import org.yeastrc.www.misc.TableHeader;
import org.yeastrc.www.misc.TableRow;
import org.yeastrc.www.misc.Tabular;

/**
 * 
 */
public class TableTag extends TagSupport {

    private String name;  // name of the bean that contains the table data
    private String tableId;
    private String tableClass; 
    private boolean center = false;
    
    private static final Logger log = Logger.getLogger(TableTag.class.getName());
    
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
                tblAttrib = tblAttrib + "id=\""+tableId+"\" ";
            if(tableClass != null)
                tblAttrib = tblAttrib + "class=\""+tableClass+"\" ";
            if(center) 
                tblAttrib = tblAttrib + "align=\"center\" ";
            writer.print("<table "+tblAttrib+">\n");
            
            
            // print header
            writer.print("<thead>\n");
            writer.print("<tr>\n");
            for(TableHeader header: tabular.tableHeaders()) {
                writer.print("<th");
                if(header.getHeaderId() != null) {
                    writer.print(" id=\""+header.getHeaderId()+"\" ");
                }
                String styleClass = "";
                if(header.getStyleClass() != null)
                    styleClass = header.getStyleClass()+" ";
                
                if(header.isSortable()) {
                    
                    styleClass += "sortable "+header.getSortClass().getCssClass()+" ";
                    if(header.isSorted()) {
                        String headerClass = header.getSortOrder() == SORT_ORDER.ASC ? "sorted-asc" : "sorted-desc";
                        styleClass += headerClass;
                    }
                    
                }
                if(styleClass.length() > 0) {
                    writer.write(" class=\""+styleClass+"\" ");
                }
                if(header.getColspan() > 0) {
                    writer.print(" colspan=\""+header.getColspan()+"\" ");
                }
                if(header.getWidth() > 0) {
                    writer.print(" width=\""+header.getWidth()+"%\" ");
                }
                writer.print(">"+header.getHeaderName()+"</th>\n");
            }
            writer.print("</tr>\n");
            writer.print("</thead>\n");
            
            // print data
            writer.print("<tbody>\n");
            for(int i = 0; i < tabular.rowCount(); i++) {
                TableRow row = tabular.getRow(i);
                
                writer.print("<tr ");
                
                String styleClass = "";
                
                if(row.isRowHighighted()) {
                    styleClass += "tr_highlight ";
                }
                if(row.getStyleClass() != null) {
                    styleClass += row.getStyleClass()+" ";
                }
                if(styleClass.length() > 0) {
                    writer.print(" class=\""+styleClass+"\" ");
                }
                writer.print(">\n");
                
                for(TableCell cell: row.getCells()) {
                    writer.print("<td ");
                    
                    if(cell.getRowSpan() > 0) {
                        writer.print("rowspan=\""+cell.getRowSpan()+"\" ");
                    }
                    if(cell.getClassName() != null && cell.getClassName().length() > 0) {
                        writer.print(" class=\""+cell.getClassName()+"\"");
                    }
                    if(cell.getId() != null && cell.getId().length() > 0) {
                        writer.print(" id=\""+cell.getId()+"\"");
                    }
                    if(cell.getName() != null && cell.getName().length() > 0) {
                        writer.print(" name=\""+cell.getName()+"\"");
                    }
                    writer.print(">");
                    
                    if(cell.getHyperlink() != null) {
                        String url = "";
                        if(!cell.isAbsoluteHyperlink()) {
                            url += contextPath;
                        }
                        url += cell.getHyperlink();
                        if(!cell.openLinkInNewWindow())
                            writer.print("<a href=\""+url+"\" >"); 
                        else {
//                            String windowName = context.getContextPath()+"_WINDOW";
//                            int winHeight = 600;
//                            int winWidth = 800;
//                            String js = "javascript:window.open('"+url+"', '"+windowName+"'"+
//                                        ", 'status=no,resizable=yes,scrollbars=yes, width="+winWidth+", height="+winHeight+"'"+
//                                        "); return false;";
//                            writer.print("<a href=\"\" onClick=\""+js+"\">");
                            if(cell.getTargetName() == null) {
                                writer.print("<a href=\""+url+"\" target='_blank' >");
                            }
                            else {
                                writer.print("<a href=\""+url+"\" target='"+cell.getTargetName()+"' >");
                            }
                            //window.open(doc, "SPECTRUM_WINDOW", "width=" + winWidth + ",height=" + winHeight + ",status=no,resizable=yes,scrollbars=yes");
                        }
                    }
                    if(cell.getData() != null && cell.getData().trim().length() > 0) {
                        writer.write(cell.getData());
                    }
                    else {
                        writer.write("&nbsp;");
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
            log.error("Exception in TableTag", e);
            throw new JspException("Error: Exception while writing to client: " + e.getMessage());
        }
    }

    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    public void release() {
        super.release();
    }
}
