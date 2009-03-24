<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<!-- Make sure we have our Collections defined, if not, go get them -->
<logic:notPresent name="userProjects" scope="request">
	<logic:forward name="standardHome"/>
</logic:notPresent>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>


<!-- SHOW ALL PROJECTS, FOR WHICH THIS USER IS LISTED AS A RESEARCHER -->
<p><yrcwww:contentbox title="Your Projects" centered="true" width="700">

 <logic:empty name="userProjects">
 	<p>You do not have any projects at this time.  
 	Click <b><html:link action="newProject.do">here</html:link></b> to create a new project.</p>
 </logic:empty>
 
 <logic:notEmpty name="userProjects">
 <TABLE BORDER="0" WIDTH="100%">
  <TR>
   <TD>&nbsp;</TD>
   <TD><U>ID</U></TD>
   <TD><U>Title</U></TD>
   <TD><U>Submit Date</U></TD>
  </TR>


<logic:iterate id="project" name="userProjects" scope="request">
 <TR>
  <TD valign="top">
   <NOBR>
    <html:link action="viewProject.do" paramId="ID" paramName="project" paramProperty="ID">View</html:link>
   </NOBR>
  </TD>
  <TD valign="top"><bean:write name="project" property="ID"/></TD>
  <TD valign="top"><bean:write name="project" property="title"/></TD>
  <TD valign="top"><bean:write name="project" property="submitDate"/></TD>
 </TR>
</logic:iterate>

 </TABLE>
 </logic:notEmpty>
</yrcwww:contentbox>

<br>
<!-- SHOW ANY RECENT SUBMISSIONS TO THIS USER'S GROUP -->
<yrcwww:member group="any">
	<yrcwww:contentbox title="Recent Submissions" centered="true" width="700">
	 	<logic:notEmpty name="newProjects" scope="request">
		 <p>Below are projects submitted by researchers to your group(s) within the last month.
	 
		 <p><table border="0" width="100%">
		  <tr>
		   <td>&nbsp;</td>
		   <td><u>ID</u></td>
		   <td><u>PI</u></td>
		   <td><u>Title</u></td>
		   <td><u>Submit Date</u></td>
		  </tr>
	 
		<logic:iterate id="project" name="newProjects" scope="request">
		 <TR>
		  <TD valign="top">
		   <NOBR>
		    <html:link action="viewProject.do" paramId="ID" paramName="project" paramProperty="ID">View</html:link>
		   </NOBR>
		  </TD>
		  <TD valign="top"><bean:write name="project" property="ID"/></TD>
		  <TD valign="top"><bean:write name="project" property="PI.lastName"/></TD>
		  <TD valign="top"><bean:write name="project" property="title"/></TD>
		  <TD valign="top"><bean:write name="project" property="submitDate"/></TD>
		 </TR>
		</logic:iterate>

		</table>
   		</logic:notEmpty>
   		<logic:empty name="newProjects" scope="request">
   		 <p>There have been no projects submitted to your group in the last month.
   		</logic:empty>
	</yrcwww:contentbox>
</yrcwww:member>





<!-- List the 10 most recently submitted MS -->
<yrcwww:member group="administrators">

	<!-- List the YATES Data here: -->
	<%@ include file="listRecentMS.jsp" %>

</yrcwww:member>


<%@ include file="/includes/footer.jsp" %>