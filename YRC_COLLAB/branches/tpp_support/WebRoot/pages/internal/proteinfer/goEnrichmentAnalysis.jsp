
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>



<div style="background-color:#ED9A2E;width:100%; margin:40 0 0 0; padding:3 0 3 0; color:white;" align="left">
<span style="margin-left:10;" 
	  class="foldable fold-open" id="goenrich_fold" onclick="toggleGoEnrichmentDetails();">&nbsp;&nbsp;&nbsp;&nbsp; </span>
<b>GO Enrichment</b>
</div>
	  
<div align="center" style="border:1px dotted gray;" id="goenrich_fold_target">

	<table align="center">
		<tr>
			<td><b># Proteins (input): </b></td><td><bean:write name="goEnrichment" property="numInputProteins" /></td>
		</tr>
		<tr>
			<td><b># Proteins (
			<a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=<bean:write name="species" property="id"/>">
				<bean:write name="species" property="name" /></a>): 
			</b></td><td><bean:write name="goEnrichment" property="numProteinsInSet" /></td>
		</tr>
	</table>

<logic:present name="goEnrichment">
	<div align="center">
		<span style="font-weight:bold; color:red;"># Enriched Terms (<bean:write name="goEnrichment" property="title"/>):<bean:write name="goEnrichment" property="enrichedTermCount" /></span>
		<table class="table_basic" align="center">
			<thead>
				<tr>
				<th class="sort-alpha">GO ID</th>
				<th class="sort-alpha">Name</th>
				<th class="sort-float">P-Value</th>
				<th class="sort-int">#Annotated (in set)</th>
				<th class="sort-int">Total (in set)</th>
				<th class="sort-int">#Annotated (All)</th>
				<th class="sort-int">Total (All)</th>
				</tr>
			</thead>
			<tbody>
				<logic:iterate name="goEnrichment" property="enrichedTerms" id="term">
					<tr>
						<td><bean:write name="term" property="goNode.accession"/>
						&nbsp;&nbsp;
						<span style="font-size:8pt;">
						<a target="go_window"
	    				href="http://www.godatabase.org/cgi-bin/amigo/go.cgi?action=query&view=query&search_constraint=terms&query=<bean:write name="term" property="goNode.accession"/>">
        				AmiGO</a>
        				&nbsp;
        				<a target="go_window" href="http://www.yeastrc.org/pdr/viewGONode.do?acc=<bean:write name="term" property="goNode.accession"/>">PDR</a>
        				</span>
						</td>
						
						<td><bean:write name="term" property="goNode.name"/></td>
						<td><bean:write name="term" property="pvalueString"/></td>
						<td><bean:write name="term" property="numAnnotatedProteins"/></td>
						<td><bean:write name="goEnrichment" property="numProteinsInSet"/></td>
						<td><bean:write name="term" property="totalAnnotatedProteins"/></td>
						<td><bean:write name="goEnrichment" property="numProteinsInUniverse"/></td>
					</tr>
				</logic:iterate>
			</tbody>
		</table>
	</div>
</logic:present>
</div>

