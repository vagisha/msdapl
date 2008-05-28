<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/adminHeader.jsp" %>


<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox title="Edit Highlight" centered="true" width="600" scheme="progress">

 <CENTER> 
  <html:form action="saveHighlight" method="post">
	<html:hidden property="id"/>
  <TABLE CELLPADDING="no" CELLSPACING="0" border="0">
  
   <TR>
    <TD VALIGN="top"><B>Title:</B></TD>
    <TD VALIGN="top"><html:text property="title" size="40" maxlength="255"/></TD>
   </TR>

   <TR>
    <TD VALIGN="top"><B>Project ID:</B><br>
     <font style="font-size:8pt;">(0 if none)</font></TD>
    <TD VALIGN="top"><html:text property="projectID" size="3" maxlength="5"/></TD>
   </TR>

   <TR>
    <TD VALIGN="top"><B>Year:</B></TD>
    <TD VALIGN="top"><html:text property="year" size="5" maxlength="4"/></TD>
   </TR>

   <TR>
    <TD VALIGN="top"><B>Body:</B></TD>
    <TD VALIGN="top"><html:textarea property="body" rows="5" cols="40"/></TD>
   </TR>

  </TABLE>

  <P><html:submit value="SAVE"/>
  
  </html:form>
  
 </CENTER>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>