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

<script type="text/javascript" src="/yrc/js/grants.js" ></script>

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


<yrcwww:contentbox title="Edit Collaboration Details" centered="true" width="750">

 <CENTER>
  <html:form action="saveCollaboration" method="post" styleId="form1">
  <html:hidden name="editCollaborationForm" property="ID"/>
  <yrcwww:notmember group="any">
   <html:hidden name="editCollaborationForm" property="BTA"/>
  </yrcwww:notmember>
  
  <TABLE CELLPADDING="no" CELLSPACING="0">
  
   <TR>
    <TD WIDTH="25%" VALIGN="top"><B>Submit Date:</B></TD>
    <TD WIDTH="75%" VALIGN="top"><B><bean:write name="editCollaborationForm" property="submitDate"/></B></TD>
   </TR>

	<tr><td colspan="2"><hr width="85%"></td></tr>

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
			<logic:notEmpty name="editCollaborationForm" property="PI" scope="request">
				<html:hidden name="editCollaborationForm" property="piid"/>
				<input size="40" type="text" name="pi" id="pi" value="<bean:write name="editCollaborationForm" property="PI.lastName" />, <bean:write name="editCollaborationForm" property="PI.firstName" /> (<bean:write name="editCollaborationForm" property="PI.organization" />)" />
			</logic:notEmpty>
			
			<logic:empty name="editCollaborationForm" property="PI" scope="request">
				<input size="40" type="text" name="pi" id="pi" />
				<html:hidden name="editCollaborationForm" property="piid"/>
			</logic:empty>
		</td>
	
	</tr>

   <logic:iterate id="researcher" property="researcherList" name="editCollaborationForm" indexId="cnt">
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
    <TD WIDTH="25%" VALIGN="top">Collaborating with:</TD>
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
    <TD WIDTH="25%" VALIGN="top">Title: <font style="font-size:8pt;color:red;">(Appears in NIH public database)</font></TD>
    <TD WIDTH="75%" VALIGN="top"><html:text property="title" size="60" maxlength="80"/></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Abstract:</TD>
    <TD WIDTH="75%" VALIGN="top"><html:textarea property="abstract" rows="7" cols="50"/></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Public Abstract:<br><font style="font-size:8pt;color:red;">To appear in NIH public database.</font></TD>
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