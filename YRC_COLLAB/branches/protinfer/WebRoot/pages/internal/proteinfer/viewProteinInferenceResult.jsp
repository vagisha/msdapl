
<%@page import="edu.uwpr.protinfer.infer.InferredProtein"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="edu.uwpr.protinfer.infer.Peptide"%>
<%@page import="java.util.HashMap"%>
<%@page import="edu.uwpr.protinfer.infer.PeptideEvidence"%>

<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<script src="http://code.jquery.com/jquery-latest.js"></script>
<script type="text/javascript" src="http://dev.jquery.com/view/tags/ui/latest/ui/ui.core.js"></script>
<script type="text/javascript" src="http://dev.jquery.com/view/tags/ui/latest/ui/ui.tabs.js"></script>
<script type="text/javascript" src="http://dev.jquery.com/view/tags/ui/latest/ui/effects.blind.js"></script>
<link rel="stylesheet" href="/yrc/js/jquery.ui-1.6rc2/themes/flora/flora.tabs.css" type="text/css" >

<%
	List<InferredProtein> inferredProteins = (List<InferredProtein>)request.getSession().getAttribute("inferredProteins");
    Collections.sort(inferredProteins, new Comparator<InferredProtein>() {
    	public int compare(InferredProtein p1, InferredProtein p2) {
       		return Integer.valueOf(p1.getProteinGroupId()).compareTo(p2.getProteinGroupId());
       	}
 	});
    int maxGrp = inferredProteins.get(inferredProteins.size() - 1).getProteinGroupId();
%>


<script>
  $(document).ready(function(){
    $("#results > ul").tabs().tabs('select', 0);
  });


  function showProteinGroup(proteinGrpIdx) {
  
  	$("#grouplist")[0].selectedIndex = proteinGrpIdx - 1;
  	selectProteinGroup();
  	
  	var $tabs = $("#results").tabs();
  	$tabs.tabs('select', 1);
  	return false;
  }
  
  function selectProteinGroup() {
  
  	var selected = $("#grouplist")[0].selectedIndex;
  	
  	for(var i = 1; i <= <%=maxGrp%>; i++) {
  		$("#protgrp_"+i).hide();
  	}
  	$("#protgrp_"+(selected+1)).show();
  }
  
  var lastSelectedProteinId = -1;
  
  function highlightProtein(proteinId) {
  	var $table = $("#assoctable")[0];
  	$(".peptev_"+proteinId).each(function(){
  		this.style.background = "#FFFF00";
  	});
  	
  	$(".peptev_"+lastSelectedProteinId).each(function(){
  		this.style.background = "";
  	});
  	
  	lastSelectedProteinId = proteinId;
  }
  
  function toggleDiv(divClass) {
  	$("."+divClass).toggle("blind", {direction: "vertical"}, 800);
  }
  
});

  $(document).ready(function() {
  
   	$('table.sortable').each(function() {

    	var $table = $(this);
    	$('th', $table).each(function(column) {
    		
    		if ($(this).is('.sort-alpha') || $(this).is('.sort-int')) {
    		
    			var $header = $(this);
        		$(this).addClass('clickable').hover(
        			function() {$(this).addClass('hover');} , 
        			function() {$(this).removeClass('hover');}).click(function() {

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

          				rows.sort(function(a, b) {
            				if (a.sortKey < b.sortKey) return -1;
  							if (a.sortKey > b.sortKey) return 1;
  							return 0;
          				});

          			$.each(rows, function(index, row) {
            			$table.children('tbody').append(row);
            			row.sortKey = null;
          			});
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
<yrcwww:contentbox title="Protein Inference Results" centered="true" width="750" scheme="ms">

	<table cellpadding="2">
  	<tr>
  		<td colspan="2"><b>IDPicker Parameters:</b></td>
  	</tr>
  	
  	<tr>
    <td WIDTH="25%" VALIGN="top">Max. Absolute FDR:</td>
    <td WIDTH="25%" VALIGN="top"><bean:write name="params" property="maxAbsoluteFdr" /></td>
   </tr>
   
   <tr>
    <td WIDTH="25%" VALIGN="top">Max. Relative FDR:</td>
    <td WIDTH="25%" VALIGN="top"><bean:write name="params" property="maxRelativeFdr" /></td>
   </tr>
   
   <tr>
    <td WIDTH="25%" VALIGN="top">Decoy Ratio:</td>
    <td WIDTH="25%" VALIGN="top"><bean:write name="params" property="decoyRatio" /></td>
   </tr>
   
   <tr>
    <td WIDTH="25%" VALIGN="top">Decoy Prefix:</td>
    <td WIDTH="25%" VALIGN="top"><bean:write name="params" property="decoyPrefix" /></td>
   </tr>

   <tr>
    <td WIDTH="25%" VALIGN="top">Min. Distinct Peptides:</td>
    <td WIDTH="25%" VALIGN="top"><bean:write name="params" property="minDistinctPeptides" /></td>
   </tr>
   
   <tr>
    <td WIDTH="25%" VALIGN="top">Parsimony Analysis:</td>
    <td WIDTH="25%" VALIGN="top"><bean:write name="params" property="doParsimonyAnalysis" /></td>
   </tr>
  	
  </table>
  
  
  <div id="results" class="flora">
      <ul>
          <li><a href="#protlist"><span>Protein List</span></a></li>
          <li><a href="#protgroups"><span>Protein Groups</span></a></li>
          <li><a href="#peptlist"><span>Peptide List</span></a></li>
          <li><a href="#input"><span>Input Summary</span></a></li>
      </ul>
      
      <!-- PROTEIN LIST -->
	<div id="protlist">
      	<table cellpadding="4" cellspacing="2" align="center" width="90%" class="sortable">
			<logic:notEmpty name="inferredProteins">
				<yrcwww:colorrow scheme="ms">
				<th class="sort-alpha"><b><font size="2pt">Protein</font></b></th>
				<th class="sort-int"><b><font size="2pt">Protein Group</font></b></th>
				<th class="sort-int"><b><font size="2pt"># Peptides</font></b></th>
				<th class="sort-int"><b><font size="2pt"># Unique Peptides</font></b></th>
				<th class="sort-int"><b><font size="2pt"># Spectra</font></b></th>
				</yrcwww:colorrow>
			</logic:notEmpty>
			<logic:iterate name="inferredProteins" id="protein">
				<yrcwww:colorrow scheme="ms">
				<td><bean:write name="protein" property="protein.accession" /></td>
				<td><span id="protgrpslink" style="cursor:pointer;text-decoration:underline" 
						  onclick="showProteinGroup(<bean:write name="protein" property="proteinGroupId"/>)">
						<bean:write name="protein" property="proteinGroupId"/>
					</span></td>
				<td><bean:write name="protein" property="peptideEvidenceCount"/></td>
				<td><bean:write name="protein" property="peptideEvidenceCount"/></td>
				<td><bean:write name="protein" property="spectralCount"/></td>
				</yrcwww:colorrow>
			</logic:iterate>
		</table>
      </div>
      
      <!-- PROTEIN GROUPS -->
      <div id="protgroups"><font color="black">
          	Select Protein Group: 
          	<select id="grouplist" onchange="selectProteinGroup()">
          		<%for(int i = 1; i <maxGrp; i++) { %>
          			<option value="<%=i%>"><%=i%></option>
          		<%} %>
          	</select>
         
          	<%
          		int idx = 0;
          		for(int grp = 1; grp <= maxGrp; grp++) { 
          			// get all the proteins in this group
          			List<InferredProtein> grpProteins = new ArrayList<InferredProtein>();
          			InferredProtein prot = inferredProteins.get(idx);
          			while(prot.getProteinGroupId() == grp) {
          				grpProteins.add(prot);
          				idx++;
          				if (idx >= inferredProteins.size())
          					break;
          				prot = inferredProteins.get(idx);
          			}%>
          			
          			<div id="protgrp_<%=grp %>" style="display: none;">
          				
          				<br><div style="background-color: #3D902A; color: #EBFFE6; padding: 2px" onclick="toggleDiv(proteins)">
          					<b>Protein Group: <%=grp %></b>
          				</div><br>
          			
          				<div class="proteins">
	          			<% for(InferredProtein pr: grpProteins) { %>
	          				<%if(pr.getProtein().isAccepted()) { %><b><%} %>
							<span onclick="highlightProtein(<%=pr.getProtein().getId()%>)" 
						      	  style="cursor:pointer;text-decoration:underline" >
								<%=pr.getAccession() %> (<%=pr.getProtein().getId()%>)
							</span><br>
							<%if(pr.getProtein().isAccepted()) { %></b><%} %>  
	          			<%} %>
          				</div>
          				
	          			<br><br>
	          			<div style="background-color: #3D902A; color: #EBFFE6; padding: 2px"><b>Peptides: </b></div><br>
	          			<table cellpadding="4" cellspacing="2" width="90%">
          				<%	List<PeptideEvidence> pevList = grpProteins.get(0).getPeptides();
          					for(PeptideEvidence ev: pevList) {%>
          			 		<tr><td><%=ev.getPeptide().getModifiedSequence() %></td></tr>
          			 	<%} %>
          				</table>
	          				
	          			<%if(grpProteins.size() > 1) { %>
	          				<br><div style="background-color: #3D902A; color: #EBFFE6; padding: 2px" ><b>Protein - Peptide Association</b></div><br>
	          					<%
	          						Map<String, Peptide> peptMap = new HashMap<String, Peptide>();
	          						for(InferredProtein pr: grpProteins) {
	          							List<PeptideEvidence> plist = pr.getPeptides();
	          							for(PeptideEvidence pev: plist) {
	          								peptMap.put(pev.getPeptide().getModifiedSequence(), pev.getPeptide());
	          							}
	          						}
	          					%>
	          			
	          				<table id="assoctable" cellpadding="4" cellspacing="2" width="80%">
	          					<tr>
	          						<td>Peptide</td>
	          						<%for(InferredProtein pr: grpProteins) { %>
	          							<td><%=pr.getProtein().getId() %></td>
	          						<%} %>
	          					</tr>
	          					<%for(Peptide pept: peptMap.values()) { %>
	          						<tr>
	          							<td><%=pept.getModifiedSequence() %></td>
	          							<%for(InferredProtein pr: grpProteins) { %>
	          								<td class="peptev_<%=pr.getProtein().getId()%>">
	          								<%if(pr.getPeptideEvidence(pept) != null) { %>
	          									x
	          								<%} %>
	          								</td>
	          							<%} %>
	          						<tr>
	          					<%} %>
	          				</table>
	          			<%} %>
          			</div>
          	<%} %>
      </font></div>
      
      <!-- PEPTIDE LIST -->
      <div id="peptlist">
			<p>Peptide List</p>
      </div>
      
      <!-- INPUT SUMMARY -->
      <div id="input">
		<table cellpadding="4" cellspacing="2" align="center" width="90%" class="sortable">
		<logic:notEmpty name="searchSummary" property="runSearchList">
		<thead>
			<yrcwww:colorrow scheme="ms">
			<th class="sort-alpha" align="left"><b><font size="2pt">File Name</font></b></th>
			<th class="sort-int" align="left"><b><font size="2pt">Total Decoy Hits</font></b></th>
			<th class="sort-int" align="left"><b><font size="2pt">Total Target Hits</font></b></th>
			<th class="sort-int" align="left"><b><font size="2pt">Filtered Target Hits</font></b></th>
			</yrcwww:colorrow>
		</thead>
		</logic:notEmpty>
		
	  	<logic:iterate name="searchSummary" property="runSearchList" id="runSearch">
	  		<logic:equal name="runSearch" property="isSelected" value="true">
	  			<yrcwww:colorrow scheme="ms">
	  				<td><bean:write name="runSearch" property="runName" /></td>
	  				<td><bean:write name="runSearch" property="totalDecoyHits" /></td>
	  				<td><bean:write name="runSearch" property="totalTargetHits" /></td>
	  				<td><bean:write name="runSearch" property="filteredTargetHits" /></td>
	  			</yrcwww:colorrow>
	  		</logic:equal>
	  	</logic:iterate>
 		</table>
      </div>
      
  </div>
  
</yrcwww:contentbox>
</CENTER>

<%@ include file="/includes/footer.jsp" %>