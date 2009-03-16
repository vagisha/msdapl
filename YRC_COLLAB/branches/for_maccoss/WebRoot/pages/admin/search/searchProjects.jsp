<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/adminHeader.jsp" %>


<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox title="Search Projects Form" centered="true" width="700" scheme="adminSearch">

<P>To search the projects in the YRC database, enter your search terms below.  Only results containing all of the terms
you entered will be returned.  The researcher names on the project are also searched.

<html:form action="searchProjects" method="POST">

 <CENTER>
 
 <html:text property="searchString" size="50"/>
 
 <P>Limit your search to:<BR>
 
  <P><U>Project Type:</U><BR>
  <html:checkbox property="types" value="C"/>Collaboration
  <html:checkbox property="types" value="T"/>Training
  <html:checkbox property="types" value="D"/>Dissemination
  <NOBR><html:checkbox property="types" value="Tech"/>Technology Development</NOBR>
 
  <P><U>Groups:</U><BR>
  <NOBR><html:checkbox property="groups" value="MacCoss"/>MacCoss</NOBR>
  <NOBR><html:checkbox property="groups" value="Informatics"/>Informatics</NOBR>
 
 
 <P ALIGN="center"><html:submit value="Search Projects"/>

</html:form>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>