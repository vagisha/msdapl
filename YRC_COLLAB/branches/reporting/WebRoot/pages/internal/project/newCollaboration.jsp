<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="editCollaborationForm" scope="request">
 <logic:forward name="newCollaboration"/>
</logic:notPresent>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>
<logic:present name="saved" scope="request">
 <P align="center"><B>Your collaboration was successfully requested.</B>
</logic:present>

<script type="text/javascript" src="/yrc/js/grants.js" ></script>

<yrcwww:contentbox title="Create a New Collaboration" centered="true" width="750" scheme="search">

<P>To request a new collaboration with the YRC, please fill out the form below.
Please review our <html:link href="/yrc/pages/internal/project/collaborationPolicies.jsp">collaboration policies</html:link> first.
The appropriate members of the YRC will automatically be notified of your request, and you should followup with you shortly.

<P><B>NOTE:</B>  If an individual you are listing as the PI, researcher B, C or D is not currently in the database, you must add them to the database first.
Go <html:link href="/yrc/newResearcher.do">here</html:link> to add a new researcher to our database.

 <CENTER>

  <P><html:form action="saveNewCollaboration" method="post">
  <TABLE CELLPADDING="no" CELLSPACING="0">

   <yrcwww:member group="any">
   <TR>
    <TD WIDTH="100%" VALIGN="top" COLSPAN="2"><html:checkbox property="isTech"/> Check here if this <b>is a Technology Development project</b>.<BR><BR></TD>
   </TR>
   </yrcwww:member>

   <TR>
    <TD WIDTH="25%" VALIGN="top">PI:</TD>
    <TD WIDTH="75%" VALIGN="top">
    
    	<html:select property="PI">
    		<html:option value="0">None</html:option>
			<html:options collection="researchers" property="ID" labelProperty="listing"/>
    	</html:select>
    
    </TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Researcher B:</TD>
    <TD WIDTH="75%" VALIGN="top">
    
    	<html:select property="researcherB">
    		<html:option value="0">None</html:option>
			<html:options collection="researchers" property="ID" labelProperty="listing"/>
    	</html:select>
    
    </TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Researcher C:</TD>
    <TD WIDTH="75%" VALIGN="top">
    
    	<html:select property="researcherC">
    		<html:option value="0">None</html:option>
			<html:options collection="researchers" property="ID" labelProperty="listing"/>
    	</html:select>
    
    </TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Researcher D:</TD>
    <TD WIDTH="75%" VALIGN="top">
    
    	<html:select property="researcherD">
    		<html:option value="0">None</html:option>
			<html:options collection="researchers" property="ID" labelProperty="listing"/>
    	</html:select>
    
    </TD>
   </TR>

	<tr><td colspan="2"><hr width="85%"></td></tr>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Collaborating with:</TD>
    <TD WIDTH="75%" VALIGN="top">	
     <NOBR><html:multibox property="groups" value="Noble"/>Computational Biology</NOBR>
     <NOBR><html:multibox property="groups" value="Informatics"/>Informatics</NOBR>    
     <NOBR><html:multibox property="groups" value="MacCoss"/>Mass Spectrometry (MacCoss)</NOBR>
     <NOBR><html:multibox property="groups" value="Yates"/>Mass Spectrometry (Yates)</NOBR>
     <NOBR><html:multibox property="groups" value="Microscopy"/>Microscopy</NOBR>
     <NOBR><html:multibox property="groups" value="PSP"/>Protein Structure Prediction</NOBR>
     <NOBR><html:multibox property="groups" value="TwoHybrid"/>Yeast Two-Hybrid</NOBR>
    </TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Project Title: <font style="font-size:8pt;color:red;">(Appears in NIH CRISP database)</font></TD>
    <TD WIDTH="75%" VALIGN="top"><html:text property="title" size="60" maxlength="80"/></TD>
   </TR>
   
   <TR>
    <TD WIDTH="25%" VALIGN="top">Abstract:</TD>
    <TD WIDTH="75%" VALIGN="top"><html:textarea property="abstract" rows="7" cols="50"/></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Public Abstract:<br><font style="font-size:8pt;color:red;">To appear in NIH CRISP database.</font></TD>
    <TD WIDTH="75%" VALIGN="top"><html:textarea property="publicAbstract" rows="7" cols="50"/></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Progress:</TD>
    <TD WIDTH="75%" VALIGN="top"><html:textarea property="progress" rows="7" cols="50"/></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Publications:<br><font style="font-size:8pt;color:red;">ONLY publications resulting<br>from this collaboration</font></TD>
    <TD WIDTH="75%" VALIGN="top"><html:textarea property="publications" rows="5" cols="50"/></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Comments:</TD>
    <TD WIDTH="75%" VALIGN="top"><html:textarea property="comments" rows="5" cols="50"/></TD>
   </TR>

	<tr><td colspan="2"><hr width="85%"></td></tr>

	<tr>
		<td colspan="2" align="left"><p><b>The following funding information is used by us when filing our annual report
										with NCRR and NIH, and is used to derive summary statistics only.</b><br><br>

	<!-- ===================================================================================== -->
	<!--  List grants here -->
	<%@ include file="grantListForm.jsp" %>
	<!-- ===================================================================================== -->
	
	<tr><td colspan="2"><hr width="85%"></td></tr>

   <TR>
    <TD COLSPAN="2">
     <html:multibox property="sendEmail" value="false"/> Check here if you do <B>NOT</B> want an email sent to the groups in the
     YRC with whom you are requesting a collaboration.  This should only be done if you are absolutely sure they are already
     aware of this collaboration.
    </TD>
   </TR>

  </TABLE>

 <P><NOBR>
 <html:submit value="Request/Save Collaboration"/>
 </NOBR>
 
  </html:form>
  
 </CENTER>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>