<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="editTrainingForm" scope="request">
 <logic:forward name="newTraining"/>
</logic:notPresent>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>
<logic:present name="saved" scope="request">
 <P align="center"><B>Your request was successfully submitted.</B>
</logic:present>

<yrcwww:contentbox title="Ask Question/Request Training" centered="true" width="750" scheme="search">

<P>To request training from the YRC, please fill out the form below.  The appropriate members of the YRC will automatically be notified of your request, and you should followup with you shortly.

<P><B>NOTE:</B>  If an individual you are listing as the PI, researcher B, C or D is not currently in the database, you must add them to the database first.
Go <html:link href="/yrc/newResearcher.do">here</html:link> to add a new researcher to our database.

 <CENTER>

  <html:form action="saveNewTraining" method="post">
   <input type="hidden" name="title" value="Question or Training Request for the Yeast Resource Center">
  <TABLE CELLPADDING="no" CELLSPACING="0" border="0">

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

	<tr><td colspan="2"><hr width="75%"></td></tr>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Ask which group:</TD>
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
   <TD valign="top" width="25%">Question or description of requested training:<br><font style="font-size:8pt;color:red;">To appear in NIH CRISP database.</font></TD>
   <TD valign="top" width="75%"><html:textarea property="description" rows="5" cols="50"/></TD>
  </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Days of training requested:</TD>
    <TD WIDTH="75%" VALIGN="top"><html:text property="days" size="3" maxlength="3"/></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Hours of training requested:</TD>
    <TD WIDTH="75%" VALIGN="top"><html:text property="hours" size="3" maxlength="3"/></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Comments:</TD>
    <TD WIDTH="75%" VALIGN="top"><html:textarea property="comments" rows="5" cols="50"/></TD>
   </TR>

   <TR>
    <TD COLSPAN="2">
     <html:multibox property="sendEmail" value="false"/> Check here if you do <B>NOT</B> want an email sent to the relevant groups in the YRC.  This should only be done if you are absolutely sure they are already
     aware of this question.
    </TD>
   </TR>

  </TABLE>

 <P>
 <html:submit value="Ask Question/Request Training"/>

  </html:form>
  
 </CENTER>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>