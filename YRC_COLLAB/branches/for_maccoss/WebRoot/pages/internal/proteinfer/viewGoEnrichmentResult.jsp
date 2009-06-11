
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


 
<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>


<script src="<yrcwww:link path='/js/dragtable.js'/>"></script>

<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.core.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.tabs.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.dialog.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.draggable.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.resizable.js'/>"></script>

<script type="text/javascript" src="<yrcwww:link path='/js/jquery.history.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.cookie.js'/>"></script>

<script src="<yrcwww:link path='/js/tooltip.js'/>"></script>

<script src="<yrcwww:link path='/js/jquery.form.js'/>"></script>

<script src="<yrcwww:link path='/js/jquery.blockUI.js'/>"></script>


<link rel="stylesheet" href="<yrcwww:link path='/css/proteinfer.css'/>" type="text/css" >

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="proteinInferFilterForm">
	<logic:forward  name="viewProteinInferenceResult" />
</logic:notPresent>

<script>

$(document).ready(function() {
   $(".sortable_table").each(function() {
   		var $table = $(this);
   		makeSortableTable($table);
   });
});

</script>


<yrcwww:contentbox title="Summary" centered="true" width="700">
	<table align="center">
		<tr><td>Protein Inference ID: </td><td><bean:write name="pinferId" /></td></tr>
		<tr><td>Species: </td><td><bean:write name="species" property="name" /></td></tr>
		<tr><td># Proteins (input): </td><td><bean:write name="enrichment" property="numInputProteins" /></td></tr>
		<tr><td># Proteins (for given species): </td><td><bean:write name="enrichment" property="numSpeciesProteins" /></td></tr>
		<tr><td>P-value cutoff: </td><td><bean:write name="enrichment" property="pValCutoff" /></td></tr>
	</table>
</yrcwww:contentbox>
<br>

<yrcwww:contentbox title="Biological Process" centered="true" width="700">
	<yrcwww:table name="bioProcessTerms" tableId='bioProc_terms' tableClass="table_basic sortable_table stripe_table" center="true" />
</yrcwww:contentbox>
<br>
<yrcwww:contentbox title="Cellular Component" centered="true" width="700">
	<yrcwww:table name="cellComponentTerms" tableId='cellComp_terms' tableClass="table_basic sortable_table stripe_table" center="true" />
</yrcwww:contentbox>
<br>
<yrcwww:contentbox title="Molecular Function" centered="true" width="700">
	<yrcwww:table name="molFunctionTerms" tableId='molFunc_terms' tableClass="table_basic sortable_table stripe_table" center="true" />
</yrcwww:contentbox>


<%@ include file="/includes/footer.jsp" %>