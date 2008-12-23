<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<logic:notPresent name="projectsSearch" scope="session">
  <logic:redirect href="/yrc/pages/admin/search/sortProjectSearch.jsp" />
</logic:notPresent>

<%@ include file="/includes/adminHeader.jsp" %>


<%@ include file="/includes/errors.jsp" %>


<yrcwww:contentbox title="Search Projects Results" centered="true" width="750" scheme="adminSearch">

<logic:notEmpty name="projectsSearch" scope="session">
<table width="100%" cellpadding="0" cellspacing="0" border="0">
 <yrcwww:colorrow scheme="adminSearch">
  <td><b><html:link href="/yrc/sortProjectSearch.do?sortby=id">ID</html:link></b></td>
  <td><b><html:link href="/yrc/sortProjectSearch.do?sortby=pi">PI</html:link></b></td>
  <td><b><html:link href="/yrc/sortProjectSearch.do?sortby=title">Title</html:link></b></td>
  <td><b><html:link href="/yrc/sortProjectSearch.do?sortby=type">Type</html:link></b></td>
  <td><b><html:link href="/yrc/sortProjectSearch.do?sortby=submit"><font style="font-size:8pt;">Submit Date</font></html:link></b></td>
  <td><b><html:link href="/yrc/sortProjectSearch.do?sortby=change"><font style="font-size:8pt;">Changed</font></html:link></b></td>
 </yrcwww:colorrow>
 
<logic:iterate id="project" name="projectsSearch" scope="session">
 <yrcwww:colorrow scheme="adminSearch">
  <TD valign="top" width="5%">
   <NOBR>
    <html:link href="/yrc/viewProject.do" paramId="ID" paramName="project" paramProperty="ID">
     <bean:write name="project" property="ID"/></html:link>
   </NOBR>
  </TD>
  <TD valign="top" width="13%">
   <logic:present name="project" property="PI"><bean:write name="project" property="PI.lastName"/></logic:present>
  </TD>
  <TD valign="top" width="43%"><bean:write name="project" property="title"/></TD>
  <TD valign="top" width="15%"><bean:write name="project" property="longType"/></TD>
  <TD valign="top" width="12%"><bean:write name="project" property="submitDate"/></TD>
  <TD valign="top" width="12%"><bean:write name="project" property="lastChange"/></TD>
 </yrcwww:colorrow>
</logic:iterate>
</table>
</logic:notEmpty>

<logic:empty name="projectsSearch" scope="session">
<B>Found 0 matches to your query...</B>
</logic:empty>
 


</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>