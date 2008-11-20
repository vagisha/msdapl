
<%@page import="java.util.List"%>
<%@page import="edu.uwpr.protinfer.database.dto.ProteinferProtein"%><%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<script src="/yrc/js/jquery.ui-1.6rc2/jquery-1.2.6.js"></script>
<script type="text/javascript" src="/yrc/js/jquery.ui-1.6rc2/ui/ui.core.js" ></script>
<script type="text/javascript" src="/yrc/js/jquery.ui-1.6rc2/ui/ui.tabs.js"></script>
<script type="text/javascript" src="/yrc/js/jquery.history.js"></script>
<link rel="stylesheet" href="/yrc/css/proteinfer.css" type="text/css" >
<%
	int clusterCount = (Integer)request.getAttribute("clusterCount");
	int pinferId = (Integer)request.getAttribute("pinferId");
	List<ProteinferProtein> inferredProteins = (List<ProteinferProtein>)request.getAttribute("inferredProteins");
%>
<script>


  // set up the tabs and select the first tab
  $(document).ready(function(){
    $("#results > ul").tabs().tabs('select', 0);
  });


// FOR HISTORY
function callback(hash)
{
    // do stuff that loads page content based on hash variable
    if(hash) {
    	$("#load").text(hash + ".html");
		var $tabs = $("#results").tabs();
		var tabidx;
		if(hash == 'protlist')
			tabidx = 0;
		else if (hash == 'protclusters')
			tabidx = 1;
		else if (hash == 'protdetails')
			tabidx = 2;
		else if (hash == 'input')
			tabidx = 3;  
  		$tabs.tabs('select', tabidx);
	} else {
		var $tabs = $("#results").tabs();
		$tabs.tabs('select', 0);
	}
}
// FOR HISTORY
$(document).ready(function() {
    $.history.init(callback);
    $("a[@rel='history']").click(function(){
    	var hash = this.href;
		hash = hash.replace(/^.*#/, '');
        $.history.load(hash);
        return false;
    });
});


  // stripe the proteins table
  $(document).ready(function() {
  	$(".stripe_table th").addClass("ms_A");
  	$(".stripe_table tr:even").addClass("ms_A");
  });
  
  // ajax defaults
  $.ajaxSetup({
  	type: 'POST',
  	timeout: 5000,
  	dataType: 'html',
  	error: function(xhr) {
  				var statusCode = xhr.status;
		  		// status code returned if user is not logged in
		  		// reloading this page will redirect to the login page
		  		if(statusCode == 303)
 					window.location.reload();
 				
 				// otherwise just display an alert
 				else {
 					alert("Request Failed: "+statusCode+"\n"+xhr.statusText);
 				}
  			}
  });
  
  // View the protein sequence
  function toggleProteinSequence (nrseqid, peptides) {
  
  		//alert("protein id: "+nrseqid+" peptides: "+peptides);
  		var button = $("#protseqbutton_"+nrseqid);
  		
  		if(button.text() == "View Protein Sequence") {
  			if($("#protsequence_"+nrseqid).html().length == 0) {
  			
  				//alert("Sending request for: "+nrseqid+"; peptides: "+peptides);
  				// load data in the appropriate div
  				$("#protsequence_"+nrseqid).load("proteinSequence.do",   						// url
  								                 {'nrseqid': nrseqid, 'peptides': peptides}); 	// data
 			}
 			button.text("Hide Protein Sequence");
 			$("#protsequence_"+nrseqid).show();
 		}
 		else {
 			button.text("View Protein Sequence");
 			$("#protsequence_"+nrseqid).hide();
 		}
  }
  
  function viewSpectrum (scanId, hitId) {
  		alert("View spectrum for "+scanId+"; hit: "+hitId);
  		var winHeight = 500
		var winWidth = 970;
		var doc = "/yrc/viewSpectrum.do?scanID="+scanId+"&runSearchResultID="+hitId;
		//alert(doc);
		window.open(doc, "SPECTRUM_WINDOW", "width=" + winWidth + ",height=" + winHeight + ",status=no,resizable=yes,scrollbars=yes");
  }
  
  
  function showProteinDetails(proteinId) {
  	// first hide all divs 
  	$(".protdetail_prot").hide();
  	
  	// load content in the appropriate div
  	$("#protein_"+proteinId).load("proteinDetails.do",   									// url
  								  {'pinferId': <%=pinferId%>, 'nrseqProtId': proteinId}, 	// data
  								  function(responseText, status, xhr) {						// callback
  								  		// stripe the table
  										$("#protdetailstbl_"+proteinId+" th").addClass("ms_A");
  										$("#protdetailstbl_"+proteinId+" tr:even").addClass("ms_A");
  										$(this).show();
  										var $tabs = $("#results").tabs();
  										$("#protdetailslink").click(); // so that history works
  										//$tabs.tabs('select', 2);
  								  });	
  }
  
  
  function showProteinCluster(proteinClusterIdx) {
  
  	$("#clusterlist")[0].selectedIndex = proteinClusterIdx - 1;
  	selectProteinCluster();
  	
  	var $tabs = $("#results").tabs();
  	$("#protclusterslink").click();
  	//$tabs.tabs('select', 1);
  	return false;
  }
  
  
  function selectProteinCluster() {
  
  	var clusterId = $("#clusterlist")[0].selectedIndex + 1;
  	
  	// hide all other first
  	for(var i = 1; i <= <%=clusterCount%>; i++) {
  		$("#protcluster_"+i).hide();
  	}
  	// get data from the server and put it in the appropriate div
  	$("#protcluster_"+clusterId).load("proteinCluster.do",   								// url
  								  	  {'pinferId': <%=pinferId%>, 'clusterId': clusterId}, 	// data
  								      function(responseText, status, request) {				// callback
	  								  		
	  								  		$("#assoctable_"+clusterId).css('border', '1px dashed gray').css('border-spacing', '2px');
	  										$("#assoctable_"+clusterId+"  td").css('border', '1px #CCCCCC dashed').css('padding', '4px');
										  	$("#assoctable_"+clusterId+"  th").css('border', '1px #CCCCCC dashed').css('padding', '4px').addClass("ms_A");
										  	
										  	$("#prot_grp_table_"+clusterId).css('border', '1px dashed gray').css('border-spacing', '2px');
										  	$("#prot_grp_table_"+clusterId+"  td").css('border', '1px #CCCCCC dashed').css('padding', '4px');
										  	$("#prot_grp_table_"+clusterId+"  th").css('border', '1px #CCCCCC dashed').css('padding', '4px').addClass("ms_A");
										  	
										  	$("#pept_grp_table_"+clusterId).css('border', '1px dashed gray').css('border-spacing', '2px');
										  	$("#pept_grp_table_"+clusterId+"  td").css('border', '1px #CCCCCC dashed').css('padding', '4px');
										  	$("#pept_grp_table_"+clusterId+"  th").css('border', '1px #CCCCCC dashed').css('padding', '4px').addClass("ms_A");
	  										
	  										$(this).show();
  								  });	
  }
  
  
  
  var lastSelectedProteinGroupId = -1;
  var lastSelectedPeptGrpIds = new Array(0);
  
  function highlightProteinAndPeptides() {
  	var proteinGroupId = arguments[0];
  	var peptGrpIds = arguments[1].split(",");
  	var uniqPeptGrpIds = arguments[2].split(",");
  	//alert(proteinGroupId+" AND "+peptGrpIds+" AND "+uniqPeptGrpIds);
  	
  	if(proteinGroupId == lastSelectedProteinGroupId) {
  		removeProteinAndPeptideHighlights();
  	}
  	else {
  		
	  	// deselect any last selected cell
	  	removeProteinAndPeptideHighlights();
	  	
	  	// select the PROTEIN group cells the user wants to select
	  	$("#protGrp_"+proteinGroupId).css("background-color","#FFFF00");
	  	
	  	
	  	// now select the PEPTIDE group cells we want AND the PROTEIN-PEPTIDE association cells
  		lastSelectedPeptGrpIds = [];
  		var j = 0;
  		// peptide groups NOT unique to protein
  		for(var i = 0; i < peptGrpIds.length; i++) {
  			$("#peptGrp_"+peptGrpIds[i]).css("background-color","#FFFF00");
  			$("#peptEvFor_"+proteinGroupId+"_"+peptGrpIds[i]).css("background-color","#FFFF00");
  			lastSelectedPeptGrpIds[j] = peptGrpIds[i];
  			j++;
  		}
  		// peptide groups UNIQUE to protein
  		for(var i = 0; i < uniqPeptGrpIds.length; i++) {
  			$("#peptGrp_"+uniqPeptGrpIds[i]).css("background-color","#00FFFF");
  			$("#peptEvFor_"+proteinGroupId+"_"+uniqPeptGrpIds[i]).css("background-color","#00FFFF");
  			lastSelectedPeptGrpIds[j] = uniqPeptGrpIds[i];
  			j++;
  		}
  		lastSelectedProteinGroupId = proteinGroupId;
  	}
  }
  	
  	function removeProteinAndPeptideHighlights() {
  		
  		if(lastSelectedPeptGrpIds != -1) {
	  		// deselect any last selected protein group cells.
	  		$("#protGrp_"+lastSelectedProteinGroupId).css("background-color","");
	  		
	  		// deselect any last selected peptide group cells AND protein-peptide association cells
	  		if(lastSelectedPeptGrpIds.length > 0) {
		  		for(var i = 0; i < lastSelectedPeptGrpIds.length; i++) {
		  			$("#peptGrp_"+lastSelectedPeptGrpIds[i]).css("background-color","");
		  			$("#peptEvFor_"+lastSelectedProteinGroupId+"_"+lastSelectedPeptGrpIds[i]).css("background-color","");
		  		}
		  	}
		  	lastSelectedProteinGroupId = -1;
		  	lastSelectedPeptGrpIds = [];
	  	}
  	}
  	

  function toggleProteinList() {
  	var $mydiv = $(".proteins");
  	if($mydiv.is(':visible'))
  		$mydiv.hide();
  	else
  		$mydiv.show();
  }
  

  $(document).ready(function() {
  
   	$('table.sortable').each(function() {

    	var $table = $(this);
    	$('th', $table).each(function(column) {
    		
    		if ($(this).is('.sort-alpha') || $(this).is('.sort-int') 
    			|| $(this).is('.sort-int-special') || $(this).is('.sort-float') ) {
    		
    			var $header = $(this);
        		$(this).addClass('clickable').hover(
        			function() {$(this).addClass('ms_hover');} , 
        			function() {$(this).removeClass('ms_hover');}).click(function() {

						// remove row striping
						$(".stripe_table tr:even").removeClass("ms_A");
						
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
						
						if ($header.is('.sort-int-special')) {
          					$.each(rows, function(index, row) {
  								var key = parseInt($(row).children('td').eq(column).text().replace(/\(\d*\)/, ''));
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
            				if (a.sortKey < b.sortKey) return -1;
  							if (a.sortKey > b.sortKey) return 1;
  							return 0;
          				});

          				$.each(rows, function(index, row) {
            				$table.children('tbody').append(row);
            				row.sortKey = null;
          				});
          				
          				// add row striping back
          				$(".stripe_table tr:even").addClass("ms_A");
        			});
			}
      	});
  	});
  });
</script>





<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

 
<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<CENTER>

<yrcwww:contentbox title="IDPicker Results" centered="true" width="850" scheme="ms">

	<table align="center" cellpadding="4">
	<tr><td>
	<table cellpadding="2" align="center" style="font-family: Trebuchet MS,Trebuchet,Verdana,Helvetica,Arial,sans-serif;font-size:12px; border: 1px solid gray; border-spacing: 2px">
  	<tr class="ms_A"><td colspan="2" align="center"><b>Parameters</b></td></tr>
  	<tr>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;">Max. FDR</td>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;"><bean:write name="params" property="maxAbsoluteFdr" /></td>
   </tr>
   <tr>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;">Decoy Ratio</td>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;"><bean:write name="params" property="decoyRatio" /></td>
   </tr>
   <tr>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;">Decoy Prefix</td>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;"><bean:write name="params" property="decoyPrefix" /></td>
   </tr>
   <tr>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;">Min. Distinct Peptides</td>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;"><bean:write name="params" property="minDistinctPeptides" /></td>
   </tr>
   <tr>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;">Parsimony Analysis</td>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;"><bean:write name="params" property="doParsimonyAnalysis" /></td>
   </tr>
  </table>
  </td>
  <td>
  
  	<table cellpadding="2" align="center" style="font-family: Trebuchet MS,Trebuchet,Verdana,Helvetica,Arial,sans-serif;font-size:12px; border: 1px solid gray; border-spacing: 3px">
  	<tr class="ms_A"><td colspan="3" align="center"><b>Summary</b></td></tr>
  	<tr>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;">&nbsp</td>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;"><b>Total</b></td>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;"><b>Filtered</b></td>
   </tr>
   <tr>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;"><b>Proteins</b></td>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;"><bean:write name="searchSummary" property="allProteins" /></td>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;"><bean:write name="searchSummary" property="filteredProteinsParsimony" /></td>
   </tr>
   <tr>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;"><b>Peptides</b></td>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;">-</td>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;">-</td>
   </tr>
   <tr>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;"><b>Spectrum<br>Matches</b></td>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;"><bean:write name="searchSummary" property="totalTargetHits" /></td>
    <td VALIGN="top" style="border: 1px #CCCCCC dotted;"><bean:write name="searchSummary" property="filteredTargetHits" /></td>
   </tr>

  </table>
  </td>
  </tr>
  </table>
  
  <div id="results" class="flora">
      <ul>
          <li><a href="#protlist" rel="history" id="protlistlink"><span>Protein List</span></a></li>
          <li><a href="#protclusters" rel="history" id="protclusterslink"><span>Protein Clusters</span></a></li>
          <li><a href="#protdetails" rel="history" id="protdetailslink"><span>Protein Details</span></a></li>
          <li><a href="#input" rel="history" id="inputlink"><span>Input Summary</span></a></li>
      </ul>
      
      <!-- PROTEIN LIST -->
	<div id="protlist">
      	<table cellpadding="4" cellspacing="2" align="center" width="95%" class="sortable stripe_table">
			<logic:notEmpty name="inferredProteins">
				<tr>
				<th class="sort-alpha"><b><font size="2pt">Protein</font></b></th>
				<th class="sort-float"><b><font size="2pt">Coverage(%)</font></b></th>
				<th class="sort-int-special"><b><font size="2pt"># Peptides<br>(Unique)</font></b></th>
				<th class="sort-int"><b><font size="2pt"># Spectra</font></b></th>
				<th class="sort-int"><b><font size="2pt">Protein Cluster</font></b></th>
				<th class="sort-int"><b><font size="2pt">Protein Group</font></b></th>
				
				</tr>
			</logic:notEmpty>
			<logic:iterate name="inferredProteins" id="protein">
				<tr>
				<td>
					<logic:equal name="protein" property="isParsimonious" value="true"><b></logic:equal>
					<logic:equal name="protein" property="isParsimonious" value="false"><font color="#888888"></logic:equal>
					<span onclick="showProteinDetails(<bean:write name="protein" property="nrseqProteinId" />)" 
							style="text-decoration: underline; cursor: pointer">
					<bean:write name="protein" property="accession" />
					</span>
					<logic:equal name="protein" property="isParsimonious" value="false"></font></logic:equal>
					<logic:equal name="protein" property="isParsimonious" value="true"></b></logic:equal>
				</td>
				<td><bean:write name="protein" property="coverage"/></td>
				<td><bean:write name="protein" property="peptideCount"/>(<bean:write name="protein" property="uniquePeptideCount"/>)</td>
				<td><bean:write name="protein" property="spectralCount"/></td>
				<td><span id="protgrpslink" style="cursor:pointer;text-decoration:underline" 
						  onclick="showProteinCluster(<bean:write name="protein" property="clusterId"/>)">
						<bean:write name="protein" property="clusterId"/>
					</span></td>
				<td><bean:write name="protein" property="groupId"/></td>
				</tr>
			</logic:iterate>
		</table>
      </div>
      
      <!-- PROTEIN CLUSTER -->
      <div id="protclusters"><font color="black">
          	<b>Select Protein Cluster: </b>
          	<select id="clusterlist" onchange="selectProteinCluster()">
          		<%for(int i = 1; i <= clusterCount; i++) { %>
          			<option value="<%=i%>"><%=i%></option>
          		<%} %>
          	</select>
          	
          	<!-- create a placeholder div for each protein cluster -->
          	<%for(int i = 1; i <= clusterCount; i++) { %>
          		<div id="protcluster_<%=i %>" style="display: none;"></div>
          	<%} %>
          	
      </font></div>
      
      
      <!-- PROTEIN DETAILS -->
      <div id="protdetails">
      		<!-- create a placeholder div for each protein -->
          	<%for(ProteinferProtein nrseqProt: inferredProteins) { %>
          		<div id="protein_<%=nrseqProt.getNrseqProteinId() %>" style="display: none;" class="protdetail_prot"></div>
          	<%} %>
      </div>
      
      <!-- INPUT SUMMARY -->
      <div id="input">
		<table cellpadding="4" cellspacing="2" align="center" width="90%" class="sortable stripe_table">
		<logic:notEmpty name="searchSummary" property="runSearchList">
		<tr>
		<th class="sort-alpha" align="left"><b><font size="2pt">File Name</font></b></th>
		<th class="sort-int" align="left"><b><font size="2pt">Decoy Hits</font></b></th>
		<th class="sort-int" align="left"><b><font size="2pt">Target Hits</font></b></th>
		<th class="sort-int" align="left"><b><font size="2pt">Filtered Target Hits</font></b></th>
		</tr>
		</logic:notEmpty>
		
	  	<logic:iterate name="searchSummary" property="runSearchList" id="runSearch">
	  		<logic:equal name="runSearch" property="isSelected" value="true">
	  			<tr>
	  				<td><bean:write name="runSearch" property="runName" /></td>
	  				<td><bean:write name="runSearch" property="totalDecoyHits" /></td>
	  				<td><bean:write name="runSearch" property="totalTargetHits" /></td>
	  				<td><bean:write name="runSearch" property="filteredTargetHits" /></td>
	  			</tr>
	  		</logic:equal>
	  	</logic:iterate>
	  	<tr>
	  		<td><b>TOTAL</b></td>
	  		<td><b><bean:write name="searchSummary" property="totalDecoyHits" /></b></td>
	  		<td><b><bean:write name="searchSummary" property="totalTargetHits" /></b></td>
	  		<td><b><bean:write name="searchSummary" property="filteredTargetHits" /></b></td>
	  	</tr>
 		</table>
	</div>

</yrcwww:contentbox>
</CENTER>

<%@ include file="/includes/footer.jsp" %>