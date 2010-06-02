
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
			<td><b># Proteins (input): </b></td><td><bean:write name="enrichment" property="numInputProteins" /></td>
		</tr>
		<tr>
			<td><b># Proteins (
			<a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=<bean:write name="species" property="id"/>">
				<bean:write name="species" property="name" /></a>): 
			</b></td><td><bean:write name="enrichment" property="numSpeciesProteins" /></td>
		</tr>
	</table>

<!-- BIOLOGICAL PROCESS -->
<logic:present name="bioProcessTerms">
	<div align="center">
	<b># Enriched Terms (Biological Process):<bean:write name="bioProcessTerms" property="enrichedTermCount" /></b>
	</div>
	<yrcwww:table name="bioProcessTerms" tableId='go_enrichment_table' tableClass="table_basic" center="true" />
<br>
</logic:present>

<!-- CELLULAR COMPONENT -->
<logic:present name="cellComponentTerms">
	<div align="center">
	<b># Enriched Terms (Cellular Component):<bean:write name="cellComponentTerms" property="enrichedTermCount" /></b>
	</div>
	<yrcwww:table name="cellComponentTerms" tableId='go_enrichment_table' tableClass="table_basic" center="true" />
<br>
</logic:present>

<!-- MOLECULAR FUNCTION -->
<logic:present name="molFunctionTerms" >
	<div align="center">
	<b># Enriched Terms (Molecular Function):<bean:write name="molFunctionTerms" property="enrichedTermCount" /></b>
	</div>
	<yrcwww:table name="molFunctionTerms" tableId='go_enrichment_table' tableClass="table_basic" center="true" />
<br>
</logic:present>

</div>

