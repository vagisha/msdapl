<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="editDisseminationForm" scope="request">
 <logic:forward name="newDissemination"/>
</logic:notPresent>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>
<logic:present name="saved" scope="request">
 <P align="center"><B>Your request was successful.</B>
</logic:present>

<yrcwww:contentbox title="Request Strains and/or Plasmids" centered="true" width="750" scheme="search">

<P>To request that the YRC ship you yeast strains or plasmids, please fill out the form below.
<b>Please limit your request to no more than 4 strains or plasmids per request.</b>

<P><B>NOTE:</B>  If an individual you are listing as the PI, researcher B, C or D is not currently in the database, you must add them to the database first.
Go <html:link href="/yrc/newResearcher.do">here</html:link> to add a new researcher to our database.

<P>Permission from the Roger Tsien lab must be obtained before the release of any plasmids
containing <b>mCherry</b>. The <a href="http://www.tsienlab.ucsd.edu/Samples.htm" target="mta_window">Material Transfer Agreement</a> must be signed
by the principal investigator, sent to the Tsien lab; and a copy
faxed to the YRC/Attn Luther Arms at 206-685-1792</P>

<p>This service is provided to academic and non-profit organizations only.

<hr width="50%">

 <CENTER>

  <p><html:form action="saveNewDissemination" method="post">
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
    <TD WIDTH="25%" VALIGN="top">Strain/Plasmid Type:</TD>
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
   <TD valign="top" width="25%">Ship-to Address:<br>
     <font style="font-size:8pt;color:red;">Please supply <b>full mailing address</b>.</font></TD>
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
    <TD WIDTH="25%" VALIGN="top">Comments:</TD>
    <TD WIDTH="75%" VALIGN="top"><html:textarea property="comments" rows="5" cols="50"/></TD>
   </TR>

  </TABLE>

 <P>
 <html:submit value="Request Plasmids/Strains"/>

  </html:form>
  
 </CENTER>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>