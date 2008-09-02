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
 
<jsp:useBean id="project" class="org.yeastrc.project.Dissemination" scope="request"/>

<%@ include file="/includes/header.jsp" %>

<yrcwww:contentbox title="View Plasmid Dissemination Details" centered="true" width="600">

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

<logic:notEmpty name="project" property="title">
  <yrcwww:colorrow>
   <TD valign="top" width="25%">Title:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="title"/></TD>
  </yrcwww:colorrow>
</logic:notEmpty>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">YRC Groups:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="groupsString"/></TD>
  </yrcwww:colorrow>

  <!-- List the Researchers here: -->
  <%@ include file="researcherList.jsp" %>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Ship-to Name:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="name"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Ship-to Phone:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="phone"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Ship-to Email:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="email"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Ship-to Address:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="addressAsHTML" filter="false"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Ship Description:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="description"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">FEDEX #:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="FEDEX"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Commercial Use?:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="commercial"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Has Shipped?:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="shipped"/></TD>
  </yrcwww:colorrow>

<logic:notEmpty name="project" property="comments">
  <yrcwww:colorrow>
   <TD valign="top" width="25%">Comments:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="commentsAsHTML" filter="false"/></TD>
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

 <P>
 <yrcwww:member group="any">
	 <html:link href="/yrc/editProject.do" paramId="ID" paramName="project" paramProperty="ID"><B>[EDIT PROJECT]</B></html:link>
 </yrcwww:member>
 
 <yrcwww:member group="administrators">
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <a href="javascript:confirmDelete('<bean:write name="project" property="ID"/>')"><B>[DELETE PROJECT]</B></a>
 </yrcwww:member>

  </CENTER>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>