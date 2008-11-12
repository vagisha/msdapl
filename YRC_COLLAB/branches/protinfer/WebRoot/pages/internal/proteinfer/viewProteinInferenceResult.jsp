<%@page import="edu.uwpr.protinfer.infer.InferredProtein"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="edu.uwpr.protinfer.infer.Peptide"%>
<%@page import="java.util.HashMap"%>
<%@page import="edu.uwpr.protinfer.infer.PeptideEvidence"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.Set"%>

<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<script src="http://code.jquery.com/jquery-latest.js"></script>
<script type="text/javascript" src="http://dev.jquery.com/view/tags/ui/latest/ui/ui.core.js"></script>
<script type="text/javascript" src="http://dev.jquery.com/view/tags/ui/latest/ui/ui.tabs.js"></script>
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
  
  $(document).ready(function() {
  	$(".ajaxbutton").click(function() {
  		var html = $.ajax({
  			url: "ajaxAction.do",
  			async: false
 		}).responseText;
 		alert(html);
 		$(".ajaxresult").html(html);
  	});
  });
  
  function showProteinCluster(proteinClusterIdx) {
  
  	$("#clusterlist")[0].selectedIndex = proteinClusterIdx - 1;
  	selectProteinCluster();
  	
  	var $tabs = $("#results").tabs();
  	$tabs.tabs('select', 1);
  	return false;
  }
  
  function selectProteinCluster() {
  
  	var selected = $("#clusterlist")[0].selectedIndex;
  	
  	for(var i = 1; i <= <%=maxCluster%>; i++) {
  		$("#protcluster_"+i).hide();
  	}
  	$("#protcluster_"+(selected+1)).show();
  }
  
  var lastSelectedProteinGroupId = -1;
  
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
					<bean:write name="protein" property="protein.accession" />
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
         
          	<%
          		int idx = 0;
          		for(int clust = 1; clust <= maxCluster; clust++) { 
          		
          			// get all the proteins and peptides in this cluster
          			Map<Integer, List<InferredProtein>> clusterProteins = new HashMap<Integer, List<InferredProtein>>();
          			Map<Integer, Map<String, PeptideEvidence>> clusterPeptides = new HashMap<Integer, Map<String, PeptideEvidence>>();
          			InferredProtein prot = inferredProteins.get(idx);
          			while(prot.getProteinClusterId() == clust) {
          				
          				// protein
          				List<InferredProtein> groupProteins = clusterProteins.get(prot.getProteinGroupId());
          				if(groupProteins == null) {
          					groupProteins = new ArrayList<InferredProtein>();
          					clusterProteins.put(prot.getProteinGroupId(), groupProteins);
          				}
          				groupProteins.add(prot);
          				
          				// its peptides
          				List<PeptideEvidence> plist = prot.getPeptides();
						for(PeptideEvidence pev: plist) {
          					Map<String, PeptideEvidence> peptMap = clusterPeptides.get(pev.getPeptide().getPeptideGroupId());
          					if(peptMap == null) {
          						peptMap = new HashMap<String, PeptideEvidence>();
          						clusterPeptides.put(pev.getPeptide().getPeptideGroupId(), peptMap);
          					}
          					peptMap.put(pev.getPeptide().getModifiedSequence(), pev);
          				}
          				
          				idx++;
          				if (idx >= inferredProteins.size())
          					break;
          				prot = inferredProteins.get(idx);
          			}
        				
          		%>
          			<div id="protcluster_<%=clust %>" style="display: none;">
          			
          				<!--  PROTEINS TABLE -->
          				<br><div style="background-color: #3D902A; color: #EBFFE6; padding: 2px; cursor: pointer" onclick="toggleProteinList()">
          					<b>Protein Cluster: <%=clust %></b>
          				</div><br>
          				<div class="proteins">
          				<table cellpadding="2" cellspacing="2" align="center" width="90%" class="grp_table">
          					<tr>
          					<th><b><font size="2pt">Protein<br>Group ID</font></b></th>
          					<th><b><font size="2pt">Accession(s)</font></b></th>
          					<th><b><font size="2pt"># Peptides</font></b></th>
          					<th><b><font size="2pt"># Unique Peptides</font></b></th>
          					<th><b><font size="2pt"># Spectra</font></b></th>
          					</tr>
	          				<% for(Integer grpId: clusterProteins.keySet()) { 
	          					InferredProtein p = clusterProteins.get(grpId).get(0);
	          					Set<Integer> peptideGrpIds = new HashSet<Integer>(p.getPeptideEvidenceCount());
	          					List<PeptideEvidence> list = p.getPeptides();
	          					for(PeptideEvidence pev: list)
	          						peptideGrpIds.add(pev.getPeptide().getPeptideGroupId());
	          					String argsToHighlightFunction = ""+grpId;
	          					for(Integer pepGrpId: peptideGrpIds)	argsToHighlightFunction+= ","+pepGrpId;
	          				%>
	          					<tr>
	          					<td valign="middle" >
	          						<span onclick="highlightProteinAndPeptides(<%=argsToHighlightFunction %>)" 
						      	 	style="cursor:pointer;text-decoration:underline"><%=grpId %></span>
						      	 </td>
						      	 
						      	<td>
	          					<% for(InferredProtein pr: clusterProteins.get(grpId)) { %>
									<%if(pr.getProtein().isAccepted()) { %><b><%} %>
									<%=pr.getAccession() %>
									<%if(pr.getProtein().isAccepted()) { %></b><%} %> 
	          					<%} %>
	          					</td>
	          					<td><%=clusterProteins.get(grpId).get(0).getPeptideEvidenceCount() %></td>
								<td><%=clusterProteins.get(grpId).get(0).getUniquePeptideEvidenceCount() %></td>
								<td><%=clusterProteins.get(grpId).get(0).getSpectralEvidenceCount() %></td>
								</tr>
							<%} %>
	          			</table>
          				</div>
	          			<br>
	          			
	          			<!--  PEPTIDES TABLE -->
          				<div style="background-color: #3D902A; color: #EBFFE6; padding: 2px"><b>Peptides: </b></div><br>
          				<table cellpadding="4" cellspacing="2" align="center" width="90%" class="grp_table" >
          					<tr>
          						<th><b><font size="2pt">Peptide<br>Group ID</font></b></th>
          						<th><b><font size="2pt">Sequence(s)</font></b></th>
          						<th><b><font size="2pt"># Spectra</font></b></th>
          						<th><b><font size="2pt">Best FDR</font></b></th>
          					</tr>
         					<% for(Integer grpId: clusterPeptides.keySet()) {
         						boolean first = true; 
         					%>
       			 				<% for(PeptideEvidence pep: clusterPeptides.get(grpId).values()) {%>
       			 				<tr>
       			 					<%if(first) {first = false; %>
       			 					<td rowspan="<%=clusterPeptides.get(grpId).size()%>"><%=grpId %></td>
       			 				 	<%}%>
       			 					<td><%=pep.getPeptide().getModifiedSequence() %></td>
       			 					<td><%=pep.getSpectrumMatchCount() %></td>
       			 					<td>1.0</td>
       			 					</tr>
       			 				<%} %>
         			 		<%} %>
          				</table>
          				
	          			
	          			<!-- PROTEIN - PEPTIDE ASSOCIATION -- ONLY IF THERE WAS MORE THAN ONE PROTEIN GROUP IN THE CLUSTER -->	
	          			<%if(clusterProteins.size() > 1) { %>
	          				<br><div style="background-color: #3D902A; color: #EBFFE6; padding: 2px" ><b>Protein - Peptide Association</b></div><br>
	          			
	          				<table id="assoctable" cellpadding="4" cellspacing="2" align="center" class="grp_table"  >
	          					<tr>
	          						<th><b><font size="2pt">Group ID <br>(Peptide / Protein)</font></b></th>
	          						<%	List<Integer> prGrpIds = new ArrayList<Integer>(clusterProteins.size());
	          							for(Integer prGrpId: clusterProteins.keySet()) { 
	          								prGrpIds.add(prGrpId); // so that we iterate over the protein group ids 
	          													   // in the same order later on.
	          						%>
	          							<th><b><font size="2pt"><%=prGrpId %></font></b></th>
	          						<%} %>
	          					</tr>
	          					<%for(Integer pepGrpId: clusterPeptides.keySet()) { 
	          						// get a representative of this group. Any protein that matches this peptide
	          						// will match all others in this group.
	          						PeptideEvidence pev = clusterPeptides.get(pepGrpId).values().iterator().next();
	          					%>
	          						<tr>
	          							<th><b><font size="2pt"><%=pepGrpId %></font></b></th>
	          							<%for(Integer prGrpId: prGrpIds) { 
	          							 	// get a representative of this protein group. If a peptide matches
	          							 	// this protein it means it matches all others in this group
	          							 	InferredProtein pr = clusterProteins.get(prGrpId).get(0);
	          							 %>
	          								<td class="peptev_<%=pr.getProteinGroupId()%>">
	          								<%if(pr.getPeptideEvidence(pev.getPeptide()) != null) { %>
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
      
      <!-- PEPTIDE LIST -->
      <div id="protdetails">
      		<button class="ajaxbutton">Click Me!</button>
			<p>Peptide List</p>
			<div class="ajaxresult"></div>
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