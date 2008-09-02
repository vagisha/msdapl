<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<logic:notPresent name="highlights" scope="request">
  <logic:redirect href="/yrc/listHighlights.do" />
</logic:notPresent>

<%@ include file="/includes/adminHeader.jsp" %>


<%@ include file="/includes/errors.jsp" %>
<logic:present name="saved" scope="request">
 <P align="center"><B>Highlight saved.</B>
</logic:present>

<yrcwww:contentbox title="Highlights" centered="true" width="750" scheme="progress">

<logic:notEmpty name="highlights" scope="request">
<table width="100%" cellpadding="0" cellspacing="0" border="0">
 <yrcwww:colorrow scheme="progress">
  <td>&nbsp;</td>
  <td>&nbsp;</td>
  <td valign="top"><b><u>Project<u></b></td>
  <td valign="top"><b><u>Year<u></b></td>
  <td valign="top" width="65%"><b><u>Title<u></b></td>
 </yrcwww:colorrow>
 
<logic:iterate id="highlight" name="highlights" scope="request">
 <yrcwww:colorrow scheme="progress">
  <TD valign="top"><html:link href="/yrc/editHighlight.do" paramId="id" paramName="highlight" paramProperty="id">EDIT</html:link></TD>
  <TD valign="top"><html:link href="/yrc/deleteHighlight.do" paramId="id" paramName="highlight" paramProperty="id">DELETE</html:link></TD>
  <TD valign="top"><html:link href="/yrc/viewProject.do" paramId="ID" paramName="highlight" paramProperty="projectID"><bean:write name="highlight" property="projectID"/></html:link></TD>
  <TD valign="top"><bean:write name="highlight" property="year"/></TD>
  <TD valign="top" width="65%"><bean:write name="highlight" property="title"/></TD>
 </yrcwww:colorrow>
</logic:iterate>
</table>
</logic:notEmpty>

<logic:empty name="highlights" scope="request">
<B>Found no highlights in the database...</B>
</logic:empty>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>