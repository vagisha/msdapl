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
 
<jsp:useBean id="project" class="org.yeastrc.project.Collaboration" scope="request"/>

<%@ include file="/includes/header.jsp" %>

<yrcwww:contentbox title="View Collaboration Details" centered="true" width="750">

<SCRIPT LANGUAGE="JavaScript">
 function confirmDelete(ID) {
    if(confirm("Are you sure you want to delete this project?")) {
       if(confirm("Are you ABSOLUTELY sure you want to delete this project?")) {
          document.location.href="/yrc/deleteProject.do?ID=" + ID;
          return 1;
       }
    }
 }
</SCRIPT>

 <CENTER>
 <TABLE CELLPADDING="no" CELLSPACING="0">
  
  <yrcwww:colorrow>
   <TD valign="top" width="25%">ID:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="ID"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Title:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="title"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">YRC Groups:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="groupsString"/></TD>
  </yrcwww:colorrow>

  <!-- List the Researchers here: -->
  <%@ include file="researcherList.jsp" %>

	<!-- ========================================================================================= -->
	<!-- List Grants here -->
	<%@ include file="grantList.jsp" %>
	<!-- ========================================================================================= -->
  
   <yrcwww:member group="any">
    <yrcwww:colorrow>
     <TD WIDTH="25%" VALIGN="top">BTA:</TD>
     <TD WIDTH="75%" VALIGN="top"><bean:write name="project" property="BTA"/>%</TD>
    </yrcwww:colorrow>
   </yrcwww:member>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Abstract:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="abstractAsHTML" filter="false"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Public Abstract:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="publicAbstractAsHTML" filter="false"/></TD>
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

	function goYates() { document.location.href = "/yrc/uploadYatesFormAction.do?projectID=<bean:write name="project" property="ID" scope="request" />"; }
	function goMacCoss() { document.location.href = "/yrc/uploadMacCossFormAction.do?projectID=<bean:write name="project" property="ID" scope="request" />"; }
	function goMicroscopy() { document.location.href = "/yrc/uploadMicroscopyFormAction.do?projectID=<bean:write name="project" property="ID" scope="request" />"; }
	function goEdit() { document.location.href = "/yrc/editProject.do?ID=<bean:write name="project" property="ID" scope="request" />"; }
</script>

 
  <div>
  	<input type="button" class="project_button" value="EDIT PROJECT" onClick="goEdit()">

	<logic:equal name="showYatesUpload" value="true" scope="request">
	  	<input type="button" class="project_button" value="UPLOAD MS/MS DATA (Yates)" onClick="goYates()">
	</logic:equal>

	<logic:equal name="showMacCossUpload" value="true" scope="request">
	  	<input type="button" class="project_button" value="UPLOAD MS/MS DATA (MacCoss)" onClick="goMacCoss()">
	</logic:equal>

	<logic:equal name="showMicroUpload" value="true" scope="request">
	  	<input type="button" class="project_button" value="UPLOAD MICROSCOPY DATA" onClick="goMicroscopy()">
	</logic:equal>

	<yrcwww:member group="administrators">
	  	<input type="button" class="error_button" value="DELETE PROJECT" onClick="confirmDelete('<bean:write name="project" property="ID"/>')">
	</yrcwww:member>
 </div>

 </CENTER>
</yrcwww:contentbox>

<!-- List the Y2H Data here: -->
<%@ include file="listY2HData.jsp" %>

<!-- List the YATES Data here: -->
<%@ include file="listYatesData.jsp" %>

<!-- List the LOCALIZATION Data here: -->
<%@ include file="listLocalizationData.jsp" %>

<%@ include file="/includes/footer.jsp" %>