<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<logic:notPresent name="unshipped" scope="request">
  <logic:redirect href="/yrc/searchPendingDisseminations.do" />
</logic:notPresent>

<%@ include file="/includes/adminHeader.jsp" %>

<yrcwww:contentbox title="Unshipped Disseminations" centered="true" width="750" scheme="adminSearch">

<logic:notEmpty name="unshipped" scope="request">
<table width="100%" cellpadding="0" cellspacing="0" border="0">
 <yrcwww:colorrow scheme="adminSearch">
  <td><b><html:link href="/yrc/sortProjectSearch.do?sortby=id">ID</html:link></b></td>
  <td><b><html:link href="/yrc/sortProjectSearch.do?sortby=pi">PI</html:link></b></td>
  <td><b><u>Groups</u></td>
  <td><b><html:link href="/yrc/sortProjectSearch.do?sortby=submit"><font style="font-size:8pt;">Submit Date</font></html:link></b></td>
  <td><b><html:link href="/yrc/sortProjectSearch.do?sortby=change"><font style="font-size:8pt;">Changed</font></html:link></b></td>
 </yrcwww:colorrow>
 
<logic:iterate id="project" name="unshipped" scope="request">
 <yrcwww:colorrow scheme="adminSearch">
  <TD valign="top" width="5%">
   <NOBR>
    <html:link href="/yrc/viewProject.do" paramId="ID" paramName="project" paramProperty="ID">
     <bean:write name="project" property="ID"/></html:link>
   </NOBR>
  </TD>
  <TD valign="top" width="20%">
   <logic:present name="project" property="PI"><bean:write name="project" property="PI.lastName"/></logic:present>
  </TD>
  <TD valign="top" width="40%"><bean:write name="project" property="groupsString"/></TD>
  <TD valign="top" width="20%"><bean:write name="project" property="submitDate"/></TD>
  <TD valign="top" width="20%"><bean:write name="project" property="lastChange"/></TD>
 </yrcwww:colorrow>
</logic:iterate>
</table>
</logic:notEmpty>

<logic:empty name="unshipped" scope="request">
<B>No unshipped disseminations found...</B>
</logic:empty>
 


</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>