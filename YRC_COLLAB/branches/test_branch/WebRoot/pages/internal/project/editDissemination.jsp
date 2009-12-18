<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="editDisseminationForm">
	<logic:forward name="editProject" />
</logic:notPresent>


<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox title="View Plasmid Dissemination Details" centered="true" width="750">

 <CENTER>
  <html:form action="saveDissemination" method="post">
  <html:hidden name="editDisseminationForm" property="ID"/>
  <TABLE CELLPADDING="no" CELLSPACING="0" border="0">
  
   <TR>
    <TD WIDTH="25%" VALIGN="top"><B>Submit Date:</B></TD>
    <TD WIDTH="75%" VALIGN="top"><B><bean:write name="editDisseminationForm" property="submitDate"/></B></TD>
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
    <TD WIDTH="25%" VALIGN="top">Plasmid Type(s):</TD>
    <TD WIDTH="75%" VALIGN="top">	
     <NOBR><html:multibox property="groups" value="Microscopy"/>Microscopy</NOBR>
     <NOBR><html:multibox property="groups" value="TwoHybrid"/>Yeast Two-Hybrid</NOBR>  
    </TD>
   </TR>

  <TR>
   <TD valign="top" width="25%">Description:</TD>
   <TD valign="top" width="75%"><html:text property="description" size="60" maxlength="255"/></TD>
  </TR>

  <TR>
   <TD valign="top" width="25%">Ship-to Name:</TD>
   <TD valign="top" width="75%"><html:text property="name" size="60" maxlength="255"/></TD>
  </TR>

  <TR>
   <TD valign="top" width="25%">Ship-to Phone:</TD>
   <TD valign="top" width="75%"><html:text property="phone" size="20" maxlength="255"/></TD>
  </TR>

  <TR>
   <TD valign="top" width="25%">Ship-to Email:</TD>
   <TD valign="top" width="75%"><html:text property="email" size="60" maxlength="255"/></TD>
  </TR>

  <TR>
   <TD valign="top" width="25%">Ship-to Address:</TD>
   <TD valign="top" width="75%"><html:textarea property="address" rows="4" cols="40"/></TD>
  </TR>

  <TR>
   <TD valign="top" width="25%">FEDEX #:</TD>
   <TD valign="top" width="75%"><html:text property="FEDEX" size="20" maxlength="255"/></TD>
  </TR>

  <TR>
   <TD valign="top" width="25%">Commercial Use?:</TD>
   <TD valign="top" width="75%">
    <html:select property="commercial">
     <html:option value="false">No</html:option>
     <html:option value="true">Yes</html:option>
    </html:select>
   </TD>
  </TR>

  <TR>
   <TD valign="top" width="25%">Has Shipped?:</TD>
   <TD valign="top" width="75%">
    <html:select property="shipped">
     <html:option value="false">No</html:option>
     <html:option value="true">Yes</html:option>
    </html:select>
   </TD>
  </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Comments:</TD>
    <TD WIDTH="75%" VALIGN="top"><html:textarea property="comments" rows="5" cols="50"/></TD>
   </TR>

  </TABLE>

 <P>
  <html:submit value="Save Changes" styleClass="button" />
 <input type="button" class="button" onclick="javascript:onCancel(<bean:write name="editDisseminationForm" property="ID"/>);" value="Cancel"/>

  </html:form>

<script type="text/javascript">
	function onCancel(projectID) {
		document.location = "/yrc/viewProject.do?ID="+projectID;
	}
</script>

  </CENTER>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>