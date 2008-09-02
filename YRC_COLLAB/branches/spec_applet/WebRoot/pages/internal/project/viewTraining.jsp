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
 
<jsp:useBean id="project" class="org.yeastrc.project.Training" scope="request"/>

<%@ include file="/includes/header.jsp" %>

<yrcwww:contentbox title="View Training Details" centered="true" width="600">

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
   <TD valign="top" width="75%"><bean:write name="project" property="groupsString"/>
  </yrcwww:colorrow>

  <!-- List the Researchers here: -->
  <%@ include file="researcherList.jsp" %>

   <yrcwww:member group="any">
    <yrcwww:colorrow>
     <TD WIDTH="25%" VALIGN="top">BTA:</TD>
     <TD WIDTH="75%" VALIGN="top"><bean:write name="project" property="BTA"/>%</TD>
    </yrcwww:colorrow>
   </yrcwww:member>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Description:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="description"/>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Days:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="days"/>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Hours:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="hours"/>
  </yrcwww:colorrow>

<logic:notEmpty name="project" property="comments">
  <yrcwww:colorrow>
   <TD valign="top" width="25%">Comments:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="commentsAsHTML" filter="false"/>
  </yrcwww:colorrow>
</logic:notEmpty>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Submit Date:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="submitDate"/>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Last Updated:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="lastChange"/>
  </yrcwww:colorrow>

 </TABLE>

 <P>
 <html:link href="/yrc/editProject.do" paramId="ID" paramName="project" paramProperty="ID"><B>[EDIT PROJECT]</B></html:link>
 <yrcwww:member group="administrators">
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <a href="javascript:confirmDelete('<bean:write name="project" property="ID"/>')"><B>[DELETE PROJECT]</B></a>
 </yrcwww:member>


 </CENTER>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>