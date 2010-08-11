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

<yrcwww:contentbox title="Ask Question/Request Training" centered="true" width="750" scheme="search">

<P>To request training from the YRC, please fill out the form below.  The appropriate members of the YRC will automatically be notified of your request, and you should followup with you shortly.

<P><B>NOTE:</B>  If an individual you are listing as a researcher is not currently in the database, you must add them to the database first.
Go <html:link href="/yrc/newResearcher.do">here</html:link> to add a new researcher to our database.

 <CENTER>

  <html:form action="saveNewTraining" method="post">
   <input type="hidden" name="title" value="Question or Training Request for the Yeast Resource Center">
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
			<logic:notEmpty name="editTrainingForm" property="PI" scope="request">
				<html:hidden name="editTrainingForm" property="piid"/>
				<input size="40" type="text" name="pi" id="pi" value="<bean:write name="editTrainingForm" property="PI.lastName" />, <bean:write name="editTrainingForm" property="PI.firstName" /> (<bean:write name="editTrainingForm" property="PI.organization" />)" />
			</logic:notEmpty>
			
			<logic:empty name="editTrainingForm" property="PI" scope="request">
				<input size="40" type="text" name="pi" id="pi" />
				<html:hidden name="editTrainingForm" property="piid"/>
			</logic:empty>
		</td>
	
	</tr>

   <logic:iterate id="researcher" property="researcherList" name="editTrainingForm" indexId="cnt">
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
    <TD WIDTH="25%" VALIGN="top">Ask which group:</TD>
    <TD WIDTH="75%" VALIGN="top">	
     <NOBR><html:multibox property="groups" value="Noble"/>Computational Biology (Noble)</NOBR>
     <NOBR><html:multibox property="groups" value="Dunham"/>Genomic Analysis (Dunham)</NOBR>
     <NOBR><html:multibox property="groups" value="Informatics"/>Informatics (Riffle)</NOBR>
     <NOBR><html:multibox property="groups" value="MacCoss"/>Mass Spectrometry (MacCoss)</NOBR>
     <NOBR><html:multibox property="groups" value="Yates"/>Mass Spectrometry (Yates)</NOBR>
     <NOBR><html:multibox property="groups" value="Microscopy"/>Microscopy (Davis and Muller)</NOBR>
     <NOBR><html:multibox property="groups" value="PSP"/>Protein Structure Prediction (Baker)</NOBR>
     <NOBR><html:multibox property="groups" value="TwoHybrid"/>Yeast Two-Hybrid (Fields)</NOBR> 
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