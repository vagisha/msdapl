<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:empty name="project">
  <logic:forward name="viewProject" />
</logic:empty>
 
<jsp:useBean id="project" class="org.yeastrc.project.Project" scope="request"/>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox title="Project Details" centered="true" width="750">

<SCRIPT LANGUAGE="JavaScript">
 function confirmDelete(ID) {
    if(confirm("Are you sure you want to delete this project?")) {
       if(confirm("Are you ABSOLUTELY sure you want to delete this project?")) {
          document.location.href="<yrcwww:link path='deleteProject.do?ID='/>" + ID;
          return 1;
       }
    }
 }
</SCRIPT>

 <CENTER>
 <TABLE CELLPADDING="no" CELLSPACING="0" width="90%">
  
  <yrcwww:colorrow>
   <TD valign="top" width="25%">ID:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="ID"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Title:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="title"/></TD>
  </yrcwww:colorrow>

  <!-- List the Researchers here: -->

	<bean:define id="pi" name="project" property="PI" scope="request"/>

	<yrcwww:colorrow>
		<TD valign="top" width="25%">PI:</TD>
		<TD valign="top" width="75%">
		<html:link action="viewResearcher.do" paramId="id" paramName="pi" paramProperty="ID">
		    <bean:write name="pi" property="firstName"/> <bean:write name="pi" property="lastName"/>, <bean:write name="pi" property="degree"/></html:link>
		</TD>
	</yrcwww:colorrow>
	
	<logic:iterate name="project" property="researchers" id="researcher">
		<yrcwww:colorrow>
			<TD valign="top" width="25%">Researcher :</TD>
			<TD valign="top" width="75%">
			<html:link action="viewResearcher.do" paramId="id" paramName="researcher" paramProperty="ID">
				<bean:write name="researcher" property="firstName"/> <bean:write name="researcher" property="lastName"/>, <bean:write name="researcher" property="degree"/></html:link>
			</TD>
		</yrcwww:colorrow>
	</logic:iterate>
	
	
	<!-- ========================================================================================= -->
	<!-- List Grants here -->
	<%@ include file="grantList.jsp" %>
	<!-- ========================================================================================= -->
	
  <yrcwww:colorrow>
   <TD valign="top" width="25%">Abstract:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="abstractAsHTML" filter="false"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Progress/Results:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="progressAsHTML" filter="false"/></TD>
  </yrcwww:colorrow>

<logic:notEmpty name="project" property="comments">
  <yrcwww:colorrow>
   <TD valign="top" width="25%">Comments:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="commentsAsHTML" filter="false"/></TD>
  </yrcwww:colorrow>
</logic:notEmpty>

<logic:notEmpty name="project" property="publications">
  <yrcwww:colorrow>
   <TD valign="top" width="25%">Publications:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="publicationsAsHTML" filter="false"/></TD>
  </yrcwww:colorrow>
</logic:notEmpty>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Submit Date:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="submitDate"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Last Updated:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="lastChange"/></TD>
  </yrcwww:colorrow>

 </TABLE>
 
<script>

	function goMacCoss() { document.location.href = "<yrcwww:link path='uploadMacCossFormAction.do?'/>projectID=<bean:write name="project" property="ID" scope="request" />"; }
	function goEdit() { document.location.href = "<yrcwww:link path='editProject.do?ID='/><bean:write name="project" property="ID" scope="request" />"; }
</script>

 
  <div>
  	<input type="button" class="plain_button" value="EDIT PROJECT" onClick="goEdit()">

	<logic:equal name="showMacCossUpload" value="true" scope="request">
	  	<input type="button" class="plain_button" value="UPLOAD MS/MS DATA (MacCoss)" onClick="goMacCoss()">
	</logic:equal>

	<yrcwww:member group="administrators">
	  	<input type="button" class="error_button" value="DELETE PROJECT" onClick="confirmDelete('<bean:write name="project" property="ID"/>')">
	</yrcwww:member>
 </div>

 </CENTER>
</yrcwww:contentbox>

<!-- List the YATES Data here: include file="listYatesData.jsp" -->


<%@ include file="/includes/footer.jsp" %>