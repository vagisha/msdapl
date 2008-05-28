<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="editTrainingForm" scope="request">
 <logic:forward name="newSeminars"/>
</logic:notPresent>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>
<logic:present name="saved" scope="request">
 <P align="center"><B>Your seminars were saved.</B>
</logic:present>

<yrcwww:contentbox title="Enter Seminars" centered="true" width="750" scheme="search">

<P>Use this form to enter in your seminars for the current reporting year.  Please enter all of your seminars into a single project.
You may add more seminars to this project by editing this project later.

 <CENTER>

  <html:form action="saveNewSeminars" method="post">
   <input type="hidden" name="comments" value="SEMINARS">
   <input type="hidden" name="sendEmail" value="false">
   <input type="hidden" name="seminars" value="true">
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

	<tr><td colspan="2"><hr width="75%"></td></tr>

   <TR>
    <TD WIDTH="25%" VALIGN="top">For which group:</TD>
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
   <TD valign="top" width="25%">Seminar locations/dates:</TD>
   <TD valign="top" width="75%"><html:textarea property="description" rows="5" cols="50"/></TD>
  </TR>

  </TABLE>

 <P>
 <html:submit value="Ask Question/Request Training"/>

  </html:form>
  
 </CENTER>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>