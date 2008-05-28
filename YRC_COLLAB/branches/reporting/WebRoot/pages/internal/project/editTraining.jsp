<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:empty name="project">
  <logic:forward name="editProject" />
</logic:empty>
 
<jsp:useBean id="project" class="org.yeastrc.project.Training" scope="request"/>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<SCRIPT LANGUAGE="javascript">
	function openAXISWindow(type) {
	 var AXISI_WIN, AXISII_WIN;
	 var doc = "/yrc/AXIS.do?ID=<bean:write name="project" property="ID"/>&type=" + type;

	 if(type == "I") {
		AXISI_WIN = window.open(doc, "AXISI_WIN",
									  "width=850,height=550,status=no,resizable=yes,scrollbars");
	 } else if(type == "II") {
		AXISI_WIN = window.open(doc, "AXISII_WIN",
									  "width=850,height=550,status=no,resizable=yes,scrollbars");
	 }
	}
</SCRIPT>

<yrcwww:contentbox title="View Training Details" centered="true" width="750">

 <CENTER>
  <html:form action="saveTraining" method="post">
  <html:hidden name="project" property="ID"/>
  <!--<html:hidden name="project" property="title"/>-->

  <yrcwww:notmember group="any">
   <html:hidden name="project" property="BTA"/>
  </yrcwww:notmember>

  <TABLE CELLPADDING="no" CELLSPACING="0" border="0">
  
   <TR>
    <TD WIDTH="25%" VALIGN="top"><B>Submit Date:</B></TD>
    <TD WIDTH="75%" VALIGN="top"><B><bean:write name="project" property="submitDate"/></B></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Title:</TD>
    <TD WIDTH="75%" VALIGN="top"><html:text property="title" size="60" maxlength="80"/></TD>
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

   <TR>
    <TD WIDTH="25%" VALIGN="top">Training with:</TD>
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
   <TD valign="top" width="25%">Description:<br><font style="font-size:8pt;color:red;">To appear in NIH CRISP database.</font></TD>
   <TD valign="top" width="75%"><html:textarea property="description" rows="5" cols="50"/></TD>
  </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Days:</TD>
    <TD WIDTH="75%" VALIGN="top"><html:text property="days" size="3" maxlength="3"/></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Hours:</TD>
    <TD WIDTH="75%" VALIGN="top"><html:text property="hours" size="3" maxlength="3"/></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Comments:</TD>
    <TD WIDTH="75%" VALIGN="top"><html:textarea property="comments" rows="5" cols="50"/></TD>
   </TR>

   <yrcwww:member group="any">
    <TR>
     <TD WIDTH="25%" VALIGN="top">BTA:</TD>
     <TD WIDTH="75%" VALIGN="top"><html:text property="BTA" size="5" maxlength="6"/>%</TD>
    </TR>
   </yrcwww:member>

	<!--
	   <TR>
		<TD VALIGN="top" WIDTH="25%"><NOBR>AXIS I:&nbsp;&nbsp;<FONT STYLE="font-size:10pt;">
		 <A HREF="javascript:openAXISWindow('I')">(Modify)</A></FONT></NOBR></TD>
		<TD VALIGN="top" WIDTH="75%"><html:text property="axisI" size="40" maxlength="40"/></TD>
	   </TR>

	   <TR>
		<TD VALIGN="top" WIDTH="25%"><NOBR>AXIS II:&nbsp;&nbsp;<FONT STYLE="font-size:10pt;">
		 <A HREF="javascript:openAXISWindow('II')">(Modify)</A></FONT></NOBR></TD>
		<TD VALIGN="top" WIDTH="75%"><html:text property="axisII" size="40" maxlength="40"/></TD>
	   </TR>
	-->
	
  </TABLE>

 <P>
 <html:image src="/yrc/images/buttons/project-save.gif" value="save" property="action"/>

 <html:link href="/yrc/viewProject.do" paramId="ID" paramName="project" paramProperty="ID">
 <html:img src="/yrc/images/buttons/project-cancel.gif" width="200" height="33" border="0"/></html:link>

  </html:form>

  </CENTER>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>