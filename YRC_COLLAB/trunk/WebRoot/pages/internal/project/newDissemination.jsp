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

<script>

	var lastResearcherIndex = -1;
	var myarr = [];
	
	function removeResearcher(rowIdx) {
		//alert("removing researcher at index "+rowIdx);
		$("#researcherRow_"+rowIdx).hide();
		document.forms[0].elements["researcher[" + rowIdx + "].ID"].value = 0;
	}
	 
	function confirmRemoveResearcher(rowIdx) {
		if(confirm("Are you sure you want to remove this researcher from the project?")) {
			removeResearcher(rowIdx);
		}
	}
	
	function addResearcher() {

		lastResearcherIndex++;

		var newRow = "<tr id=\"researcherRow_" + lastResearcherIndex + "\">";
			newRow += "<TD WIDTH='25%' VALIGN='top'>Researcher:</TD>";
			newRow += "<td WIDTH='25%' VALIGN='top'>";
			newRow += "<input class='researcher' type='hidden' name=\"researcher[" + lastResearcherIndex + "].ID\" value='0' />";
			newRow += "<input size='40' type='text' name='researcher[" + lastResearcherIndex + "]' id='researcher" + lastResearcherIndex + "' />";
			newRow += " <a href=\"javascript:confirmRemoveResearcher('" + lastResearcherIndex + "')\" style='color:red;font-size:8pt;'>[Remove]</a>";
			newRow += "</td>";
			newRow += "</tr>";

				
		if(lastResearcherIndex == 0) {
			$("#piRow").after(newRow);
		}
		else {
			$("#researcherRow_"+(lastResearcherIndex - 1)).after(newRow);
		}
		
		var tidx = lastResearcherIndex;
	    var options = {
	      serviceUrl: '/yrc/service/researcherAutocomplete.do',
	      onSelect: function(value, data){ document.forms[0].elements["researcher[" + tidx + "].ID"].value = data; },
	      width: 500,
	      delimiter: /(,|;)\s*/,
	      deferRequestBy: 0, //miliseconds
	      noCache: false //set to true, to disable caching
	    };
				
    	myarr[ lastResearcherIndex ] = $('#researcher' + lastResearcherIndex).autocomplete( options );
	}
	
	
</script>

<yrcwww:contentbox title="Request Strains and/or Plasmids" centered="true" width="750" scheme="search">

<P>To request that the YRC ship you yeast strains or plasmids, please fill out the form below.
<b>Please limit your request to no more than 4 strains or plasmids per request.</b>

<P><B>NOTE:</B>  If an individual you are listing as a researcher is not currently in the database, you must add them to the database first.
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
  
	<tr> 
   		<td colspan="2" align="left"><div style="margin-bottom:10px;padding:10px;border-style:solid;border-width:1px;border-color:red;color:red;">*To add researchers, begin typing in the last name and select researcher
   		from resulting pull-down menu.<br />*To add more researchers, click the &quot;+Add Researcher&quot; link.<br />*If researcher is not found, you must add them first
   		<a href="/yrc/newResearcher.do">using this form</a>.</div></td> 
   	</tr> 

	<tr id="piRow">
		<td style="width:25%;vertical-align:top;">
			PI:
		</td>

		<td style="width:75%;vertical-align:top;">
			<logic:notEmpty name="editDisseminationForm" property="PI" scope="request">
				<html:hidden name="editDisseminationForm" property="piid"/>
				<input size="40" type="text" name="pi" id="pi" value="<bean:write name="editDisseminationForm" property="PI.lastName" />, <bean:write name="editDisseminationForm" property="PI.firstName" /> (<bean:write name="editDisseminationForm" property="PI.organization" />)" />
			</logic:notEmpty>
			
			<logic:empty name="editDisseminationForm" property="PI" scope="request">
				<input size="40" type="text" name="pi" id="pi" />
				<html:hidden name="editDisseminationForm" property="piid"/>
			</logic:empty>
		</td>
	
	</tr>

   <logic:iterate id="researcher" property="researcherList" name="editDisseminationForm" indexId="cnt">
	   <tr id="researcherRow_<%=cnt%>" >
	   	<TD WIDTH="25%" VALIGN="top">Researcher:</TD>
	   	<td WIDTH="25%" VALIGN="top">
			<html:hidden name="researcher" property="ID" indexed="true" />
			<input class="researcher" size="40" type="text" name="researcher[<%=(cnt)%>]" id="researcher<%=(cnt)%>" value="<bean:write name="researcher" property="lastName" />, <bean:write name="researcher" property="firstName" /> (<bean:write name="researcher" property="organization" />)" />
	   		<a href="javascript:confirmRemoveResearcher('<%=(cnt)%>')" style="color:red; font-size:8pt;">[Remove]</a>
	   	</td>
	   </tr>
   </logic:iterate>

	<tr> 
   		<td colspan="2" align="center"><a href="javascript:addResearcher()">+Add Researcher</a></td> 
   	</tr> 

	<tr><td colspan="2"><hr width="85%"></td></tr>

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

<script type="text/javascript"> 
  //<![CDATA[
 
  var a1; 

  jQuery(function() {
 
     var piOptions = {
      serviceUrl: '/yrc/service/researcherAutocomplete.do',
      onSelect: function(value, data){ document.forms[0].elements["piid"].value = data; },
      width: 500,
      delimiter: /(,|;)\s*/,
      deferRequestBy: 0, //miliseconds
      noCache: false //set to true, to disable caching
    };
 
	// set up the PI auto complete
    a1 = $('#pi').autocomplete(piOptions);

	// set up the auto complete for each researcher
	$('.researcher').each(function(index) {
		//piOptions[ 'onSelect' ] =  function(value, data){ document.forms[0].elements["researcher[" + index + "].ID"].value = data; },

	    var options = {
	      serviceUrl: '/yrc/service/researcherAutocomplete.do',
	      onSelect: function(value, data){ document.forms[0].elements["researcher[" + index + "].ID"].value = data; },
	      width: 500,
	      delimiter: /(,|;)\s*/,
	      deferRequestBy: 0, //miliseconds
	      noCache: false //set to true, to disable caching
	    };

    	myarr[ index ] = $('#researcher' + index).autocomplete( options );
    	lastResearcherIndex = index;
    });
 
  });
  
//]]>
</script> 

<%@ include file="/includes/footer.jsp" %>