
<%@page import="org.yeastrc.bio.go.GOUtils"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<logic:present name="showGoForm" >

<html:form action="/proteinProphetGoEnrichment" method="post" styleId="goEnrichmentForm" >
  
  <html:hidden name="proteinProphetFilterForm" property="pinferId" />
  <TABLE CELLPADDING="5px" CELLSPACING="5px" align="center" style="border: 1px solid gray;">
  
  <!-- Filtering options -->
  <tr>
  
  <td><table>
  <tr>
  <td>Peptides: </td>
  <td>
  	Min <html:text name="proteinProphetFilterForm" property="minPeptides" size="3"></html:text>
  	Max <html:text name="proteinProphetFilterForm" property="maxPeptides" size="3"></html:text>
  </td>
  </tr>
  <tr>
  <td>Unique Peptides: </td>
  <td>
  	Min <html:text name="proteinProphetFilterForm" property="minUniquePeptides" size="3"></html:text>
  	Max <html:text name="proteinProphetFilterForm" property="maxUniquePeptides" size="3"></html:text>
  </td>
  </tr>
  <tr>
  	<td valign="bottom">ProteinProphet<br>Group Probability:</td>
  	<td valign="bottom">
  		Min <html:text name="proteinProphetFilterForm" property="minGroupProbability" size="3"></html:text>
  		Max <html:text name="proteinProphetFilterForm" property="maxGroupProbability" size="3"></html:text>
  	</td>
  </tr>
  </table></td>
  
  <td><table>
  <tr>
  <td>Coverage(%):</td>
  <td>
  	Min <html:text name="proteinProphetFilterForm" property="minCoverage" size="3"></html:text>
  	Max <html:text name="proteinProphetFilterForm" property="maxCoverage" size="3"></html:text>
  </td>
  </tr>
  <tr>
  <td>Spectrum Matches: </td>
  <td>
  	Min <html:text name="proteinProphetFilterForm" property="minSpectrumMatches" size="3"></html:text>
  	Max <html:text name="proteinProphetFilterForm" property="maxSpectrumMatches" size="3"></html:text>
  </td>
  </tr>
  <tr>
  	<td valign="bottom">ProteinProphet<br>Protein Probability:</td>
  	<td valign="bottom">
  		Min <html:text name="proteinProphetFilterForm" property="minProteinProbability" size="3"></html:text>
  		Max <html:text name="proteinProphetFilterForm" property="maxProteinProbability" size="3"></html:text>
  	</td>
  </tr>
  </table></td>
  
  <td><table>
  <tr>
  	<td colspan="2" align="left">ProteinProphet Groups: </td>
  	<td align="left">
  		<html:radio name="proteinProphetFilterForm" property="joinProphetGroupProteins" value="true">Yes</html:radio>
  	</td>
  	<td align="left">
  		<html:radio name="proteinProphetFilterForm" property="joinProphetGroupProteins" value="false">No</html:radio>
  	</td>
  </tr>
  <tr>
  	<td colspan="2" align="left">
  		<html:radio name="proteinProphetFilterForm" property="showAllProteins" value="true">Show All Proteins</html:radio>
  	</td>
  	<td colspan="2" align="left">
  		<html:radio name="proteinProphetFilterForm" property="showAllProteins" value="false">Exclude Subsumed</html:radio>
  	</td>
  </tr>
  </table></td>
  </tr>
 </TABLE>
 
 <html:hidden name="proteinProphetFilterForm"
			property="validationStatusString" />
 <html:hidden name="proteinProphetFilterForm" property="accessionLike" />
 <html:hidden name="proteinProphetFilterForm" property="descriptionLike" />
<br>	
 <div align="center"
			style="padding: 5; border: 1px dashed gray; background-color: #F0F8FF;">
			<b>GO Enrichment:</b>
			<html:select name="proteinProphetFilterForm" property="goAspect">
				<html:option
					value="<%=String.valueOf(GOUtils.BIOLOGICAL_PROCESS) %>">Biological Process</html:option>
				<html:option
					value="<%=String.valueOf(GOUtils.CELLULAR_COMPONENT) %>">Cellular Component</html:option>
				<html:option
					value="<%=String.valueOf(GOUtils.MOLECULAR_FUNCTION) %>">Molecular Function</html:option>
			</html:select>
			&nbsp; &nbsp;
			Species:
			<html:select name="proteinProphetFilterForm" property="speciesId">
				<html:option value="4932">Saccharomyces cerevisiae </html:option>
			</html:select>
			&nbsp; &nbsp; P-Value:
			<html:text name="proteinProphetFilterForm" property="goEnrichmentPVal"></html:text>
			&nbsp; &nbsp;
			<a href=""
				onclick="javascript:doGoEnrichmentAnalysis();return false;">Calculate</a>
</div>
</html:form>
	
</logic:present>

<logic:notPresent name="showGoForm">

	<html:form action="/proteinProphetGoEnrichment" method="post"
		styleId="goEnrichmentForm">
		<html:hidden name="proteinProphetFilterForm" property="pinferId" />
		<html:hidden name="proteinProphetFilterForm" property="minPeptides" />
		<html:hidden name="proteinProphetFilterForm" property="maxPeptides" />
		<html:hidden name="proteinProphetFilterForm"
			property="minUniquePeptides" />
		<html:hidden name="proteinProphetFilterForm"
			property="maxUniquePeptides" />
		<html:hidden name="proteinProphetFilterForm" property="minGroupProbability" />
		<html:hidden name="proteinProphetFilterForm" property="maxGroupProbability" />	
		<html:hidden name="proteinProphetFilterForm" property="minProteinProbability" />
		<html:hidden name="proteinProphetFilterForm" property="maxProteinProbability" />	
		
		<html:hidden name="proteinProphetFilterForm" property="minCoverage" />
		<html:hidden name="proteinProphetFilterForm" property="maxCoverage" />
		<html:hidden name="proteinProphetFilterForm"
			property="minSpectrumMatches" />
		<html:hidden name="proteinProphetFilterForm"
			property="maxSpectrumMatches" />
		<html:hidden name="proteinProphetFilterForm" property="joinProphetGroupProteins" />
		<html:hidden name="proteinProphetFilterForm" property="showAllProteins" />
		
		<html:hidden name="proteinProphetFilterForm"
			property="validationStatusString" />
		<html:hidden name="proteinProphetFilterForm" property="accessionLike" />
		<html:hidden name="proteinProphetFilterForm" property="descriptionLike" />

		<div align="center"
			style="padding: 5; border: 1px dashed gray; background-color: #F0F8FF;">
			<b>GO Enrichment:</b>
			<html:select name="proteinProphetFilterForm" property="goAspect">
				<html:option
					value="<%=String.valueOf(GOUtils.BIOLOGICAL_PROCESS) %>">Biological Process</html:option>
				<html:option
					value="<%=String.valueOf(GOUtils.CELLULAR_COMPONENT) %>">Cellular Component</html:option>
				<html:option
					value="<%=String.valueOf(GOUtils.MOLECULAR_FUNCTION) %>">Molecular Function</html:option>
			</html:select>
			&nbsp; &nbsp; 
			Species:
			<html:select name="proteinProphetFilterForm" property="speciesId">
				<html:option value="4932">Saccharomyces cerevisiae </html:option>
			</html:select>
			&nbsp; &nbsp;
			P-Value: <html:text name="proteinProphetFilterForm" property="goEnrichmentPVal"></html:text>
			&nbsp; &nbsp;
			<a href=""
				onclick="javascript:doGoEnrichmentAnalysis();return false;">Calculate</a>
		</div>
	</html:form>
</logic:notPresent>
  
  
  
