<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="editCollaborationForm">
	<logic:forward name="editProject" />
</logic:notPresent>
 
<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<SCRIPT LANGUAGE="javascript">
	function openAXISWindow(type) {
	 var AXISI_WIN, AXISII_WIN;
	 var doc = "/yrc/AXIS.do?ID=<bean:write name="editCollaborationForm" property="ID"/>&type=" + type;

	 if(type == "I") {
		AXISI_WIN = window.open(doc, "AXISI_WIN",
									  "width=850,height=550,status=no,resizable=yes,scrollbars");
	 } else if(type == "II") {
		AXISI_WIN = window.open(doc, "AXISII_WIN",
									  "width=850,height=550,status=no,resizable=yes,scrollbars");
	 }
	}
</SCRIPT>

<yrcwww:contentbox title="Edit Collaboration Details" centered="true" width="750">

 <CENTER>
  <html:form action="saveCollaboration" method="post" styleId="form1">
  <html:hidden name="editCollaborationForm" property="ID"/>
  <yrcwww:notmember group="any">
   <html:hidden name="project" property="BTA"/>
  </yrcwww:notmember>
  
  <TABLE CELLPADDING="no" CELLSPACING="0">
  
   <TR>
    <TD WIDTH="25%" VALIGN="top"><B>Submit Date:</B></TD>
    <TD WIDTH="75%" VALIGN="top"><B><bean:write name="editCollaborationForm" property="submitDate"/></B></TD>
   </TR>

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
    <TD WIDTH="25%" VALIGN="top">Title: <font style="font-size:8pt;color:red;">(Appears in NIH CRISP database)</font></TD>
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
	
	<!-- ===================================================================================== -->
	<!--  List grants here -->
	<%@ include file="grantListForm.jsp" %>
	<!-- ===================================================================================== -->

   <yrcwww:member group="any">

	<tr><td colspan="2"><hr width="85%"></td></tr>
	
    <TR>
     <TD WIDTH="25%" VALIGN="top">BTA:</TD>
     <TD WIDTH="75%" VALIGN="top"><html:text property="BTA" size="5" maxlength="6"/>%</TD>
    </TR>
   </yrcwww:member>

  </TABLE>

 <P><NOBR>
 <html:submit value="Save Changes" styleClass="button" />
 <input type="button" class="button" onclick="javascript:onCancel(<bean:write name="editCollaborationForm" property="ID"/>);" value="Cancel"/>
 </NOBR>
 
  </html:form>

<script type="text/javascript">
	function onCancel(projectID) {
		document.location = "/yrc/viewProject.do?ID="+projectID;
	}
</script>

 </CENTER>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>