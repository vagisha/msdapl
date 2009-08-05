
<%@page import="org.yeastrc.bio.go.GOUtils"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<logic:present name="showGoForm" >

<html:form action="/protInferGoEnrichment" method="post" styleId="goEnrichmentForm" >
  
  <html:hidden name="proteinInferFilterForm" property="pinferId" />
  <TABLE CELLPADDING="5px" CELLSPACING="5px" align="center" style="border: 1px solid gray;">
  
  <!-- Filtering options -->
  <tr>
  
  <td><table>
  <tr>
  <td>Peptides: </td>
  <td>
  	Min <html:text name="proteinInferFilterForm" property="minPeptides" size="3"></html:text>
  	Max <html:text name="proteinInferFilterForm" property="maxPeptides" size="3"></html:text>
  </td>
  </tr>
  <tr>
  <td>Unique Peptides: </td>
  <td>
  	Min <html:text name="proteinInferFilterForm" property="minUniquePeptides" size="3"></html:text>
  	Max <html:text name="proteinInferFilterForm" property="maxUniquePeptides" size="3"></html:text>
  </td>
  </tr>
  </table></td>
  
  <td><table>
  <tr>
  <td>Coverage(%):</td>
  <td>
  	Min <html:text name="proteinInferFilterForm" property="minCoverage" size="3"></html:text>
  	Max <html:text name="proteinInferFilterForm" property="maxCoverage" size="3"></html:text>
  </td>
  </tr>
  <tr>
  <td>Spectrum Matches: </td>
  <td>
  	Min <html:text name="proteinInferFilterForm" property="minSpectrumMatches" size="3"></html:text>
  	Max <html:text name="proteinInferFilterForm" property="maxSpectrumMatches" size="3"></html:text>
  </td>
  </tr>
  </table></td>
  
  <td><table>
  <tr>
  	<td>Proteins: </td>
  	<td>
  		<html:radio name="proteinInferFilterForm" property="showAllProteins" value="true">All</html:radio>
  	</td>
  	<td>
  		<html:radio name="proteinInferFilterForm" property="showAllProteins" value="false">Parsimonious</html:radio>
  	</td>
  </tr>
  </table></td>
  </tr>
 </TABLE>
 
 <html:hidden name="proteinInferFilterForm"
			property="validationStatusString" />
 <html:hidden name="proteinInferFilterForm" property="accessionLike" />
 <html:hidden name="proteinInferFilterForm" property="descriptionLike" />
<br>	
 <div align="center"
			style="padding: 5; border: 1px dashed gray; background-color: #F0F8FF;">
			<b>GO Enrichment:</b>
			<html:select name="proteinInferFilterForm" property="goAspect">
				<html:option
					value="<%=String.valueOf(GOUtils.BIOLOGICAL_PROCESS) %>">Biological Process</html:option>
				<html:option
					value="<%=String.valueOf(GOUtils.CELLULAR_COMPONENT) %>">Cellular Component</html:option>
				<html:option
					value="<%=String.valueOf(GOUtils.MOLECULAR_FUNCTION) %>">Molecular Function</html:option>
			</html:select>
			&nbsp; &nbsp;
			Species:
			<html:select name="proteinInferFilterForm" property="speciesId">
				<html:option value="4932">Saccharomyces cerevisiae </html:option>
			</html:select>
			&nbsp; &nbsp; P-Value:
			<html:text name="proteinInferFilterForm" property="goEnrichmentPVal"></html:text>
			&nbsp; &nbsp;
			<a href=""
				onclick="javascript:doGoEnrichmentAnalysis();return false;">Calculate</a>
</div>
</html:form>
	
</logic:present>

<logic:notPresent name="showGoForm">

	<html:form action="/protInferGoEnrichment" method="post"
		styleId="goEnrichmentForm">
		<html:hidden name="proteinInferFilterForm" property="pinferId" />
		<html:hidden name="proteinInferFilterForm" property="minPeptides" />
		<html:hidden name="proteinInferFilterForm" property="maxPeptides" />
		<html:hidden name="proteinInferFilterForm"
			property="minUniquePeptides" />
		<html:hidden name="proteinInferFilterForm"
			property="maxUniquePeptides" />
		<html:hidden name="proteinInferFilterForm" property="minCoverage" />
		<html:hidden name="proteinInferFilterForm" property="maxCoverage" />
		<html:hidden name="proteinInferFilterForm"
			property="minSpectrumMatches" />
		<html:hidden name="proteinInferFilterForm"
			property="maxSpectrumMatches" />
		<html:hidden name="proteinInferFilterForm" property="showAllProteins" />
		<html:hidden name="proteinInferFilterForm"
			property="validationStatusString" />
		<html:hidden name="proteinInferFilterForm" property="accessionLike" />
		<html:hidden name="proteinInferFilterForm" property="descriptionLike" />

		<div align="center"
			style="padding: 5; border: 1px dashed gray; background-color: #F0F8FF;">
			<b>GO Enrichment:</b>
			<html:select name="proteinInferFilterForm" property="goAspect">
				<html:option
					value="<%=String.valueOf(GOUtils.BIOLOGICAL_PROCESS) %>">Biological Process</html:option>
				<html:option
					value="<%=String.valueOf(GOUtils.CELLULAR_COMPONENT) %>">Cellular Component</html:option>
				<html:option
					value="<%=String.valueOf(GOUtils.MOLECULAR_FUNCTION) %>">Molecular Function</html:option>
			</html:select>
			&nbsp; &nbsp; 
			Species:
			<html:select name="proteinInferFilterForm" property="speciesId">
				<html:option value="4932">Saccharomyces cerevisiae </html:option>
			</html:select>
			&nbsp; &nbsp;
			P-Value: <html:text name="proteinInferFilterForm" property="goEnrichmentPVal"></html:text>
			&nbsp; &nbsp;
			<a href=""
				onclick="javascript:doGoEnrichmentAnalysis();return false;">Calculate</a>
		</div>
	</html:form>
</logic:notPresent>
  
  
  
