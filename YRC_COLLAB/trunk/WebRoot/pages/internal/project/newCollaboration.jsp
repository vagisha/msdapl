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
<script> 
// ---------------------------------------------------------------------------------------
// SETUP ANY BASIC TABLES
// ---------------------------------------------------------------------------------------
$(document).ready(function() {
   $(".table_basic").each(function() {
   		var $table = $(this);
   		$('tbody > tr:odd', $table).addClass("tr_odd");
   		$('tbody > tr:even', $table).addClass("tr_even");
   });
   
   $(".foldable").each(function() {
   		$(this).click(function() {
   			fold($(this));
   		});
   });
});
  
// ---------------------------------------------------------------------------------------
// MAKE A TABLE STRIPED
// ---------------------------------------------------------------------------------------
function makeStripedTable(table) {
	var $table = $(table);
	$('tbody > tr:odd', $table).addClass("tr_odd");
   	$('tbody > tr:even', $table).addClass("tr_even");
}
 
// ---------------------------------------------------------------------------------------
// MAKE A TABLE SORTABLE
// ---------------------------------------------------------------------------------------
function makeSortableTable(table) {
  	
	var $table = $(table);
	$('th', $table).each(function(column) {
  		
  		if ($(this).is('.sort-alpha') || $(this).is('.sort-int') 
  			|| $(this).is('.sort-float') ) {
  		
  			var $header = $(this);
      		$(this).click(function() {
 
				// remove row striping
				if($table.is('.stripe_table')) {
					$("tbody > tr:odd", $table).removeClass("tr_odd");
					$("tbody > tr:even", $table).removeClass("tr_even");
				}
				
				// sorting direction
				var newDirection = 1;
        		if ($(this).is('.sorted-asc')) {
          			newDirection = -1;
        		}
        				
        		var rows = $table.find('tbody > tr').get();
        				
        		if ($header.is('.sort-alpha')) {
        			$.each(rows, function(index, row) {
						row.sortKey = $(row).children('td').eq(column).text().toUpperCase();
					});
				}
				
				if ($header.is('.sort-int')) {
        					$.each(rows, function(index, row) {
								var key = parseInt($(row).children('td').eq(column).text());
						row.sortKey = isNaN(key) ? 0 : key;
					});
				}
				
				if ($header.is('.sort-float')) {
        					$.each(rows, function(index, row) {
								var key = parseFloat($(row).children('td').eq(column).text());
						row.sortKey = isNaN(key) ? 0 : key;
					});
				}
 
     			rows.sort(function(a, b) {
       				if (a.sortKey < b.sortKey) return -newDirection;
					if (a.sortKey > b.sortKey) return newDirection;
					return 0;
     			});
 
     			$.each(rows, function(index, row) {
       				$table.children('tbody').append(row);
       				row.sortKey = null;
     			});
     			
     			// the header for the column used for sorting is highlighted
				$('th', $table).each(function(){
					$(this).removeClass('sorted-desc');
	    			$(this).removeClass('sorted-asc');
				});
				
     			var $sortHead = $table.find('th').filter(':nth-child(' + (column + 1) + ')');
 
	          	if (newDirection == 1) {$sortHead.addClass('sorted-asc'); $sortHead.removeClass('sorted-desc');} 
	          	else {$sortHead.addClass('sorted-desc'); $sortHead.removeClass('sorted-asc');}
        
        		
        		// add row striping back
        		if($table.is('.stripe_table')) {
					$('tbody > tr:odd', $table).addClass("tr_odd");
   					$('tbody > tr:even', $table).addClass("tr_even");
        		}
      		});
	}
  });
}
 
// ---------------------------------------------------------------------------------------
// FOLDABLE
// ---------------------------------------------------------------------------------------
function fold(foldable) {
	//alert("foldable clicked");
	if(!foldClose(foldable))
	foldOpen(foldable);
}
function foldClose(foldable) {
	var target_id = foldable.attr('id')+"_target";
   	//alert("target is: "+target_id);
	
	if(foldable.is('.fold-open')) {
		foldable.removeClass('fold-open');
		foldable.addClass('fold-close');
		$("#"+target_id).hide();
		
		return true;
 	}
 	return false;
}
function foldOpen(foldable) {
	var target_id = foldable.attr('id')+"_target";
   	//alert("target is: "+target_id);
	
	if(foldable.is('.fold-close')) {
		foldable.removeClass('fold-close');
		foldable.addClass('fold-open');
		$("#"+target_id).show();
		return true;
	}
	return false;
}
 

</script> 

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

<yrcwww:contentbox title="Create a New Collaboration" centered="true" width="750" scheme="search">

<P>To request a new collaboration with the YRC, please fill out the form below.
Please review our <html:link href="/yrc/pages/internal/project/collaborationPolicies.jsp">collaboration policies</html:link> first.
The appropriate members of the YRC will automatically be notified of your request, and you should followup with you shortly.

<P><B>NOTE:</B>  If an individual you are listing as a researcher is not currently in the database, you must add them to the database first.
Go <html:link href="/yrc/newResearcher.do">here</html:link> to add a new researcher to our database.

 <CENTER>

  <P><html:form action="saveNewCollaboration" method="post">
  <TABLE CELLPADDING="no" CELLSPACING="0">

   <yrcwww:member group="any">
   <TR>
    <TD WIDTH="100%" VALIGN="top" COLSPAN="2"><html:checkbox property="isTech"/> Check here if this <b>is a Technology Development project</b>.<BR><BR></TD>
   </TR>
   </yrcwww:member>


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
 <html:submit value="Request/Save Collaboration" styleClass="button"/>
 </NOBR>
 
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