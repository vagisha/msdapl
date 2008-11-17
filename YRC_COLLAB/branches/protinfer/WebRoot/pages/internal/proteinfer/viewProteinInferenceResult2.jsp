<%@page import="edu.uwpr.protinfer.infer.InferredProtein"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Map"%>
<%@page import="edu.uwpr.protinfer.infer.PeptideEvidence"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.Set"%>
<%@page import="edu.uwpr.protinfer.idpicker.InferredProteinGroup"%>
<%@page import="edu.uwpr.protinfer.idpicker.InferredPeptideGroup"%>
<%@page import="edu.uwpr.protinfer.SequestSpectrumMatch"%>

<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<script src="/yrc/js/jquery.ui-1.6rc2/jquery-1.2.6.js"></script>
<script type="text/javascript" src="/yrc/js/jquery.ui-1.6rc2/ui/ui.core.js" ></script>
<script type="text/javascript" src="/yrc/js/jquery.ui-1.6rc2/ui/ui.tabs.js"></script>
<link rel="stylesheet" href="/yrc/js/jquery.ui-1.6rc2/themes/flora/flora.tabs.css" type="text/css" >

<%
	List<InferredProtein> inferredProteins = (List<InferredProtein>)request.getSession().getAttribute("inferredProteins");
    Collections.sort(inferredProteins, new Comparator<InferredProtein>() {
    	public int compare(InferredProtein p1, InferredProtein p2) {
       		return Integer.valueOf(p1.getProteinClusterId()).compareTo(p2.getProteinClusterId());
       	}
 	});
    int maxCluster = inferredProteins.get(inferredProteins.size() - 1).getProteinClusterId();
    
    System.out.println("Max Clusters: "+maxCluster);
    
    Map<Integer, InferredProteinGroup<SequestSpectrumMatch>> protGroupList = 
    		(Map<Integer, InferredProteinGroup<SequestSpectrumMatch>>)request.getSession().getAttribute("protGroupList");
    Map<Integer, InferredPeptideGroup<SequestSpectrumMatch>> peptGroupList = 
    	   	(Map<Integer, InferredPeptideGroup<SequestSpectrumMatch>>)request.getSession().getAttribute("peptGroupList");
    Map<Integer, Set<Integer>> proteinClusterIds = 
    		(Map<Integer, Set<Integer>>)request.getSession().getAttribute("proteinClusterIds");
%>


<script>
  $(document).ready(function(){
    $("#results > ul").tabs().tabs('select', 0);
  });

  $(document).ready(function() {
  	$(".stripe_table th").addClass("ms_A");
  	$(".stripe_table tr:even").addClass("ms_A");
  });
  
  $(document).ready(function() {
  	$(".grp_table").css('border', '1px dashed gray').css('border-spacing', '2px');
  	$(".grp_table  td").css('border', '1px #CCCCCC dashed').css('padding', '4px');
  	$(".grp_table  th").css('border', '1px #CCCCCC dashed').css('padding', '4px').addClass("ms_A");
  });
  
  
  function toggleProteinSequence (nrseqid, peptides) {
  		var button = $("#protseqbutton_"+nrseqid);
  		
  		if(button.text() == "View Protein Sequence") {
  			if($("#protsequence_"+nrseqid).html().length == 0) {
  				alert("Sending request for: "+nrseqid+"; peptides: "+peptides);
  				var html = $.ajax({
  					url: "proteinSequence.do?nrseqid="+nrseqid+"&peptides="+peptides,
  					async: false
 				}).responseText;
 				//alert("Got response: "+html);
 				html = "<pre>"+html+"</pre>";
 				$("#protsequence_"+nrseqid).html(html);
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
  
  function showProteinCluster(proteinClusterIdx) {
  
  	$("#clusterlist")[0].selectedIndex = proteinClusterIdx - 1;
  	selectProteinCluster();
  	
  	var $tabs = $("#results").tabs();
  	$tabs.tabs('select', 1);
  	return false;
  }
  
  function showProteinDetails(proteinId) {
  	// first hide all divs 
  	$(".protdetail_prot").hide();
  	// show the relevant one
  	$("#protein_"+proteinId).show();
  	var $tabs = $("#results").tabs();
  	$tabs.tabs('select', 2);
  }
  
  function selectProteinCluster() {
  
  	var selected = $("#clusterlist")[0].selectedIndex;
  	
  	for(var i = 1; i <= <%=maxCluster%>; i++) {
  		$("#protcluster_"+i).hide();
  	}
  	$("#protcluster_"+(selected+1)).show();
  }
  
  var lastSelectedProteinGroupId = -1;
  var lastSelectedPeptGrpIds = new Array(0);
  
  function highlightProteinAndPeptides() {
  	var proteinGroupId = arguments[0];
  	if(proteinGroupId == lastSelectedProteinGroupId)
  		return;
  		
  	// deselect any last selected cell
  	$(".peptev_"+lastSelectedProteinGroupId).each(function(){
  		this.style.background = "";
  	});
  	
  	// select the one the user wants to select
  	$(".peptev_"+proteinGroupId).each(function(){
  		this.style.background = "#FFFF00";
  	});
  	
  	// deselect any last selected PROTEIN group cells
  	if(lastSelectedProteinGroupId != -1) {
  		$(".protGrp_"+lastSelectedProteinGroupId).each(function() {
  			this.style.background = "";
  		});
  	}
  	
  	// now select the PROTEIN group cells we want
  	$(".protGrp_"+proteinGroupId).each(function() {
  		this.style.background = "#FFFF00";
  	});
  	
  	// deselect any last selected PEPTIDE group cells
  	if(lastSelectedPeptGrpIds.length > 0) {
  		for(var i = 0; i < lastSelectedPeptGrpIds.length; i++) {
  			$(".peptGrp_"+lastSelectedPeptGrpIds[i]).each(function() {
  			this.style.background = "";
  		});
  		}
  	}
  	
  	// now select the PEPTIDE group cells we want
  	lastSelectedPeptGrpIds = new Array(arguments.length);
  	var j = 0;
  	for(var i = 1; i < arguments.length; i++) {
  		$(".peptGrp_"+arguments[i]).each(function() {
  			this.style.background = "#FFFF00";
  		});
  		lastSelectedPeptGrpIds[j] = arguments[i];
  		j++;
  	}
  	
  	lastSelectedProteinGroupId = proteinGroupId;
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
    		
    		if ($(this).is('.sort-alpha') || $(this).is('.sort-int') || $(this).is('.sort-int-special')) {
    		
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
          <li><a href="#protlist"><span>Protein List</span></a></li>
          <li><a href="#protclusters"><span>Protein Clusters</span></a></li>
          <li><a href="#protdetails"><span>Protein Details</span></a></li>
          <li><a href="#input"><span>Input Summary</span></a></li>
      </ul>
      
      <!-- PROTEIN LIST -->
	<div id="protlist">
      	<table cellpadding="4" cellspacing="2" align="center" width="95%" class="sortable stripe_table">
			<logic:notEmpty name="inferredProteins">
				<tr>
				<th class="sort-alpha"><b><font size="2pt">Protein</font></b></th>
				<th class="sort-int"><b><font size="2pt">Protein Cluster</font></b></th>
				<th class="sort-int"><b><font size="2pt">Protein Group</font></b></th>
				<th class="sort-int-special"><b><font size="2pt"># Peptides<br>(Unique)</font></b></th>
				<th class="sort-int"><b><font size="2pt"># Spectra</font></b></th>
				</tr>
			</logic:notEmpty>
			<logic:iterate name="inferredProteins" id="protein">
				<tr>
				<td>
					<logic:equal name="protein" property="isAccepted" value="true"><b></logic:equal>
					<span onclick="showProteinDetails(<bean:write name="protein" property="proteinId" />)" 
							style="text-decoration: underline; cursor: pointer">
						<bean:write name="protein" property="protein.accession" />
					</span>
					<logic:equal name="protein" property="isAccepted" value="true"></b></logic:equal>
				</td>
				<td><span id="protgrpslink" style="cursor:pointer;text-decoration:underline" 
						  onclick="showProteinCluster(<bean:write name="protein" property="proteinClusterId"/>)">
						<bean:write name="protein" property="proteinClusterId"/>
					</span></td>
				<td><bean:write name="protein" property="proteinGroupId"/></td>
				<td><bean:write name="protein" property="peptideEvidenceCount"/>(<bean:write name="protein" property="uniquePeptideEvidenceCount"/>)</td>
				<td><bean:write name="protein" property="spectralEvidenceCount"/></td>
				</tr>
			</logic:iterate>
		</table>
      </div>
      
      <!-- PROTEIN CLUSTER -->
      <div id="protclusters"><font color="black">
      
          	<b>Select Protein Cluster: </b>
          	<select id="clusterlist" onchange="selectProteinCluster()">
          		<%for(int i = 1; i <= maxCluster; i++) { %>
          			<option value="<%=i%>"><%=i%></option>
          		<%} %>
          	</select>
          	
          	<% for(Integer clusterId: proteinClusterIds.keySet()) { 
          			
          			Set<Integer> clusterProteinGroupIds = proteinClusterIds.get(clusterId);
          			Set<Integer> clusterPeptideGroupIds = new HashSet<Integer>();
          			
          			for(Integer protGrpId: clusterProteinGroupIds) {
          				InferredProteinGroup protGrp = protGroupList.get(protGrpId);
          				List<Integer> peptGrpIds = protGrp.getMatchingPeptideGroupIds();
          				
          				for(Integer peptGrpId: peptGrpIds) {
          					clusterPeptideGroupIds.add(peptGrpId);
          				}
          			}
          	%>
          			<div id="protcluster_<%=clusterId %>" style="display: none;">
          			
          				<!--  PROTEINS TABLE -->
          				<br><div style="background-color: #3D902A; color: #EBFFE6; padding: 2px; cursor: pointer" onclick="toggleProteinList()">
          					<b>Protein Cluster: <%=clusterId %></b>
          				</div><br>
          				<div class="proteins">
          				<table cellpadding="2" cellspacing="2" align="center" width="90%" class="grp_table" id="prot_grp_table">
          					<tr>
          					<th><b><font size="2pt">Protein<br>Group ID</font></b></th>
          					<th><b><font size="2pt">Accession(s)</font></b></th>
          					<th><b><font size="2pt"># Peptides<br>(Unique)</font></b></th>
          					<th><b><font size="2pt"># Spectra</font></b></th>
          					</tr>
	          				<% for(Integer protGrpId: clusterProteinGroupIds) { 
	          					InferredProteinGroup prGrp = protGroupList.get(protGrpId);
	          					String argsToHighlightFunction = ""+protGrpId;
	          					int numUniquePeptidesForProteinGrp = 0;
	          					List<Integer> peptGrpIds = prGrp.getMatchingPeptideGroupIds();
	          					for(Integer pepGrpId: peptGrpIds) {
	          					
	          						argsToHighlightFunction+= ","+pepGrpId;
	          						
	          						InferredPeptideGroup peptGrp = peptGroupList.get(pepGrpId);
	          						if(peptGrp.isUniqueToProtein()) {
          								numUniquePeptidesForProteinGrp += peptGrp.getPeptideEvidenceListList().size();
          							}
	          					}
	          				%>
	          					<tr>
	          					<td valign="middle" class="protGrp_<%=protGrpId%>">
	          						<span onclick="highlightProteinAndPeptides(<%=argsToHighlightFunction %>)" 
						      	 	style="cursor:pointer;text-decoration:underline"><%=protGrpId %></span>
						      	 </td>
						      	 
						      	<td class="protGrp_<%=protGrpId%>">
	          					<% 	List<InferredProtein> grpPrList = prGrp.getInferredProteinList();
	          						for(InferredProtein pr: grpPrList) { %>
									<%if(pr.getProtein().isAccepted()) { %><b><%} %>
									<div onclick="showProteinDetails(<%=pr.getProteinId() %>)" 
									style="text-decoration: underline; cursor: pointer">
									<%=pr.getAccession() %>
									</div>
									<%if(pr.getProtein().isAccepted()) { %></b><%} %> 
	          					<%} %>
	          					</td>
	          					<td class="protGrp_<%=protGrpId%>"><%=grpPrList.get(0).getPeptideEvidenceCount() %>(<%=numUniquePeptidesForProteinGrp %>)</td>
								<td class="protGrp_<%=protGrpId%>"><%=grpPrList.get(0).getSpectralEvidenceCount() %></td>
								</tr>
							<%} %>
	          			</table>
          				</div>
	          			<br>
	          			
	          			<!--  PEPTIDES TABLE -->
          				<div style="background-color: #3D902A; color: #EBFFE6; padding: 2px"><b>Peptides: </b></div><br>
          				<table cellpadding="4" cellspacing="2" align="center" width="90%" class="grp_table" id="pept_grp_table">
          					<tr>
          						<th><b><font size="2pt">Peptide<br>Group ID</font></b></th>
          						<th><b><font size="2pt">Sequence(s)</font></b></th>
          						<th><b><font size="2pt"># Spectra</font></b></th>
          						<th><b><font size="2pt">Best FDR</font></b></th>
          					</tr>
         					<% for(Integer grpId: clusterPeptideGroupIds) {
         						boolean first = true; 
         						InferredPeptideGroup peptGrp = peptGroupList.get(grpId);
         					%>
       			 				<% 
       			 					List<PeptideEvidence> pepEvList = peptGrp.getPeptideEvidenceListList();
       			 					for(PeptideEvidence pep: pepEvList) {%>
       			 				<tr>
       			 					<%if(first) {first = false; %>
       			 					<td rowspan="<%=pepEvList.size()%>" class="peptGrp_<%=grpId%>"><%=grpId %></td>
       			 				 	<%}%>
       			 					<td class="peptGrp_<%=grpId%>"><%=pep.getPeptide().getModifiedSequence() %></td>
       			 					<td class="peptGrp_<%=grpId%>"><%=pep.getSpectrumMatchCount() %></td>
       			 					<td class="peptGrp_<%=grpId%>"><%=pep.getBestFdr() %></td>
       			 					</tr>
       			 				<%} %>
         			 		<%} %>
          				</table>
          				
	          			
	          			<!-- PROTEIN - PEPTIDE ASSOCIATION -- ONLY IF THERE WAS MORE THAN ONE PROTEIN GROUP IN THE CLUSTER -->	
	          			<%if(clusterProteinGroupIds.size() > 1) { %>
	          				<br><div style="background-color: #3D902A; color: #EBFFE6; padding: 2px" ><b>Protein - Peptide Association</b></div><br>
	          			
	          				<table id="assoctable" cellpadding="4" cellspacing="2" align="center" class="grp_table"  >
	          					<tr>
	          						<th><b><font size="2pt">Group ID <br>(Peptide / Protein)</font></b></th>
	          						<%	for(Integer prGrpId: clusterProteinGroupIds) { %>
	          							<th><b><font size="2pt"><%=prGrpId %></font></b></th>
	          						<%} %>
	          					</tr>
	          					<%for(Integer pepGrpId: clusterPeptideGroupIds) { %>
	          						<tr>
	          							<th><b><font size="2pt"><%=pepGrpId %></font></b></th>
	          							<%for(Integer prGrpId: clusterProteinGroupIds) { 
	          							 	InferredProteinGroup prGrp = protGroupList.get(prGrpId);
	          							 	List<Integer> protPeptGrpIds = prGrp.getMatchingPeptideGroupIds();
	          							 %>
	          								<td class="peptev_<%=prGrp.getGroupId()%>">
	          								<%if(protPeptGrpIds.contains(pepGrpId)) { %>
	          									x
	          								<%} else {%>&nbsp;<%} %>
	          								</td>
	          							<%} %>
	          						<tr>
	          					<%} %>
	          				</table>
	          			<%} %>
	          			
	          			<br><br>
          			</div>
          	<%} %>
      </font></div>
      
      <!-- PROTEIN DETAILS -->
      <div id="protdetails" >
      		
			
			<%for(InferredProtein prot: inferredProteins) { %>
			<div id="protein_<%=prot.getProtein().getId() %>" style="display: none;" class="protdetail_prot">
				
				<br><b><font size="2px" color="black">Protein: <%=prot.getAccession() %></font></b><br><br>
				
				<!-- Placeholder for where the protein sequence will be displayed via an ajax call -->
				<%
					StringBuilder peptides = new StringBuilder();
					List<PeptideEvidence> pevList = prot.getPeptides();
					for(PeptideEvidence pev: pevList) {
						peptides.append(","+pev.getPeptideSeq());
					}
					if(peptides.length() > 0)
						peptides.deleteCharAt(0);
				 %>
				<button onclick="toggleProteinSequence(<%=prot.getProteinId() %>, '<%=peptides.toString() %>')"
						id="protseqbutton_<%=prot.getProteinId() %>">View Protein Sequence</button>
				<br>
				<table  align="center" width="90%">
					<tr><td style="background-color: #D4FECA;" id="protsequence_<%=prot.getProteinId() %>"></td></tr>
				</table>
					
				<br><br>
				<table width="95%" >
				<tr>
					<th width="10%"><b><font size="2pt">Group ID</font></b></th>
					<th><b><font size="2pt">Sequence</font></b></th>
					<th width="10%"><b><font size="2pt"># Spectra</font></b></th>
					<th width="10%"><b><font size="2pt">Best FDR</font></b></th>
					<th width="10%"><b><font size="2pt">Unique</b></th>
				</tr>
				<%	//List<PeptideEvidence> pevList = prot.getPeptides();
					for(PeptideEvidence pev: pevList) { %>
					<tr>
					<td><%=pev.getPeptide().getPeptideGroupId() %></td>
					<td><b><%=pev.getPeptide().getModifiedSequence() %></b></td>
					<td><%=pev.getSpectrumMatchCount() %> </td>
					<td><%=pev.getBestFdr() %></td>
					<td><%=peptGroupList.get(pev.getPeptide().getPeptideGroupId()).isUniqueToProtein() %></td>
					</tr>
					
					<!-- nested table for spectrum matches -->
					<tr><td colspan="5">
					<table align="center" width="70%" 
						   style="border: 1px dashed gray; border-spacing: 4px; margin-top: 6px; margin-bottom: 6px;" >
						<tr>
							<td style="text-decoration: underline;">Scan Number</td>
							<td style="text-decoration: underline;">Assumed Charge</td>
							<td style="text-decoration: underline;">XCorr</td>
							<td style="text-decoration: underline;">DeltaCN</td>
							<td style="text-decoration: underline;">FDR</td>
							<td style="text-decoration: underline;">Spectrum</td>
						</tr>
						<%
							List<SequestSpectrumMatch> psmList = pev.getSpectrumMatchList();
							for (SequestSpectrumMatch psm: psmList) { 
						%>
							<tr>
							<td><%=psm.getScanNumber() %></td>
							<td><%=psm.getCharge() %></td>
							<td><%=psm.getXcorrRounded() %></td>
							<td><%=psm.getDeltaCnRounded() %></td>
							<td><%=psm.getFdrRounded() %></td>
							<td><span style="text-decoration: underline; cursor: pointer;" onclick="viewSpectrum(<%=psm.getScanId() %>, <%=psm.getHitId() %>)">View</span></td>
							</tr>
						<%} %>
					</table>
					</td></tr>
						
				<%} // end for%>
				
				</table>
			</div>
			<%} // end for%>
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