<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<html>

<head>
 <yrcwww:title />
 <link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='/css/global.css' />">
</head>

<body>

<%@ include file="/includes/errors.jsp" %>

<script src="<yrcwww:link path='js/jquery.ui-1.6rc2/jquery-1.2.6.js'/>"></script>
<script>

var fontsize = 2;
$(document).ready(function() {
	setFont(fontsize);
});

function setFont(size) {
	$("td.rowname").each(function() {
		//alert("changing font size");
		$(this).css("fontSize",size); 
	});
}

function increaseFont() {
	//alert("Increasing font");
	if(fontsize < 10) 
		fontsize++;
	setFont(fontsize);
}

function decreaseFont() {
	//alert("decreasing font");
	if(fontsize > 2) 
		fontsize--;
	setFont(fontsize);
}

function updatePage(rowIndex) {
	window.opener.goToHeatMapIndex(rowIndex);
}

</script>


<!-- RESULTS TABLE -->
<div style="margin:10 5 10 5;"> 

<logic:present name="heatmap">

	<table width="70%" cellspacing="0" cellpadding="10" align="center">
	<tr><td align="right" style="color: #3D4960;">
	<b>Font: &nbsp;<span class="clickable" 
				style="background: white; border: 1px solid #CBCBCB; padding:3 3 3 3;" onclick="increaseFont();">+</span> 
				&nbsp;
				<span class="clickable" 
				style="background:white; border: 1px solid #CBCBCB; padding:3 4 3 4;" onclick="decreaseFont()">-</span></b>
	</td></tr>
	</table>
	
	<center>
	<div style="border: 1px solid; width:70%; padding: 3 3 3 3; color: #3D4960;">
	<span>Click on the heatmap to navigate to the relevant page in the comparison view.</span><br/><br/>
	<table width="100%" cellspacing="0" cellpadding="0" align="center" >
	<tr>
		<th width="10%" class="header">Protein</th>
		<logic:iterate name="heatmap" property="datasetLabels" id="datasetLabel">
			<th width="45%" class="header"><bean:write name="datasetLabel"/></th>
		</logic:iterate>
	</tr>
	
	<logic:iterate name="heatmap" property="rows" id="row">
	<tr>
		<td class="rowname"><bean:write name="row" property="rowName" /></td>
		<logic:iterate name="row" property="cells" id="cell">
			<td style="font-size:0pt; background-color:<bean:write name='cell' property='hexColor' />;" 
				class="clickable"
			    onclick="updatePage(<bean:write name='row' property='indexInList' />)">
			    &nbsp;
			</td>
		</logic:iterate>
	</tr>
	</logic:iterate>
	
	</table>
	</div>
	</center>
</logic:present>
</div>

</body>
</html>




