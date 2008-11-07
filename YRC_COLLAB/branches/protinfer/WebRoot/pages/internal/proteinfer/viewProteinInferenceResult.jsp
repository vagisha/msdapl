<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

 
<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<CENTER>
<yrcwww:contentbox title="Protein Inference Results" centered="true" width="750">

  <table cellpadding="2" align="left">
  	<tr><td colspan="2"><b>Input Files:</b></td></tr>
	<logic:notEmpty name="searchSummary" property="runSearchList">
		<tr>
		<td><b>File Name</b></td>
		<td><b>Total Decoy Hits</b></td>
		<td><b>Total Target Hits</b></td>
		<td><b>Filtered Target Hits</b></td>
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
 </table>
 
 <table cellpadding="2" align="left">
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

<table cellpadding="2" align="left">
	<logic:notEmpty name="inferredProteins">
		<tr><td>Protein</td></tr>
		<tr><td>Protein Group</td></tr>
		<tr><td># Peptides</td></tr>
		<tr><td># Spectra</td></tr>
	</logic:notEmpty>
</table>

</yrcwww:contentbox>
</CENTER>

<%@ include file="/includes/footer.jsp" %>