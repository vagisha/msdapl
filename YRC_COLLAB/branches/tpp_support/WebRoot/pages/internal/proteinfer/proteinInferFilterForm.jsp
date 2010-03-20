
<%@page import="org.yeastrc.ms.domain.protinfer.ProteinUserValidation"%>
<%@page import="org.yeastrc.bio.go.GOUtils"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<script type="text/javascript">
	$(document).ready(function() {
		$("input[name='validationStatus'][value='All']").click(function() {
			$("input[name='validationStatus'][value!='All']").each(function() {
				this.checked = false;
			});
		});
		$("input[name='validationStatus'][value!='All']").click(function() {
			$("input[name='validationStatus'][value='All']").each(function() {
				this.checked = false;
			});
		});
		
		$("input[name='chargeStates'][value='All']").click(function() {
			$("input[name='chargeStates'][value!='All']").each(function() {
				this.checked = false;
			});
		});
		$("input[name='chargeStates'][value!='All']").click(function() {
			$("input[name='chargeStates'][value='All']").each(function() {
				this.checked = false;
			});
		});
	});
</script>

  <html:form action="/proteinInferGateway" method="post" styleId="filterForm" >
  
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
  <tr>
  <td>Protein Mol. Wt.: </td>
  <td>
  	Min <html:text name="proteinInferFilterForm" property="minMolecularWt" size="3"></html:text>
  	Max <html:text name="proteinInferFilterForm" property="maxMolecularWt" size="3"></html:text>
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
  <tr>
  <td>Protein pI: </td>
  <td>
  	Min <html:text name="proteinInferFilterForm" property="minPi" size="3"></html:text>
  	Max <html:text name="proteinInferFilterForm" property="maxPi" size="3"></html:text>
  </td>
  </tr>
  </table></td>
  
  <td valign="top"><table>
  
  <logic:notPresent name="showGoForm">
  <tr>
  	<td colspan="2">Group Indistinguishable Proteins: </td>
  	<td>
  		<html:radio name="proteinInferFilterForm" property="joinGroupProteins" value="true">Yes</html:radio>
  	</td>
  	<td>
  		<html:radio name="proteinInferFilterForm" property="joinGroupProteins" value="false">No</html:radio>
  	</td>
  </tr>
  </logic:notPresent>
  
  <tr>
  	<td colspan="2">Show Proteins: </td>
  	<td>
  		<html:radio name="proteinInferFilterForm" property="showAllProteins" value="true">All</html:radio>
  	</td>
  	<td>
  		<html:radio name="proteinInferFilterForm" property="showAllProteins" value="false">Parsimonious</html:radio>
  	</td>
  </tr>
  </table></td>
  </tr>
  
  <tr>
  	<td colspan="2">
  		Validation Status: 
  		<html:multibox name="proteinInferFilterForm" property="validationStatus" value="All"/> All
  		<html:multibox name="proteinInferFilterForm" property="validationStatus" 
  					   value="<%=String.valueOf(ProteinUserValidation.UNVALIDATED.getStatusChar()) %>"/> Unvalidated 
  		<html:multibox name="proteinInferFilterForm" property="validationStatus"  
  		               value="<%=String.valueOf(ProteinUserValidation.ACCEPTED.getStatusChar()) %>"/> Accepted
  		<html:multibox name="proteinInferFilterForm" property="validationStatus"  
  		               value="<%=String.valueOf(ProteinUserValidation.REJECTED.getStatusChar()) %>"/> Rejected
  		<html:multibox name="proteinInferFilterForm" property="validationStatus"  
  		               value="<%=String.valueOf(ProteinUserValidation.NOT_SURE.getStatusChar()) %>"/> Not Sure
  	</td>
  	<td>
  		Exclude Indistinguishable Groups: <html:checkbox name="proteinInferFilterForm" property="excludeIndistinProteinGroups" value="true"/>
  	</td>
  </tr>
  
  <tr>
  	<td colspan="2">
  		Include Charge: &nbsp;&nbsp;
  		<html:multibox name="proteinInferFilterForm" property="chargeStates" value="All"/> All &nbsp;
  		<html:multibox name="proteinInferFilterForm" property="chargeStates" value="1"/> +1 &nbsp;
  		<html:multibox name="proteinInferFilterForm" property="chargeStates" value="2"/> +2 &nbsp;
  		<html:multibox name="proteinInferFilterForm" property="chargeStates" value="3"/> +3 &nbsp;
  		<html:multibox name="proteinInferFilterForm" property="chargeStates" value="4"/> +4 &nbsp;
  		<html:multibox name="proteinInferFilterForm" property="chargeStates" value=">4"/> &gt; +4   
  	</td>
  </tr>
  
  <tr>
  	<td colspan="3">
  	<table align="left">
  		<tr>
  			<td valign="top">Fasta ID(s): </td>
  			<td valign="top"><html:text name="proteinInferFilterForm" property="accessionLike" size="40"></html:text><br>
  				<span style="font-size:8pt;">Enter a comma-separated list of complete or partial identifiers</span>
  			</td>
  			<td valign="top">Peptide: </td>
  			<td valign="top">
  				<html:text name="proteinInferFilterForm" property="peptide" size="40"></html:text>
  				<span style="font-size:8pt;">Exact Match:<html:checkbox property="proteinInferFilterForm" property="exactPeptideMatch"></html:checkbox></span>
  			</td>
  			
  		</tr>
  		<tr>
  			<td valign="top">Description Include: </td>
  			<td valign="top"><html:text name="proteinInferFilterForm" property="descriptionLike" size="40"></html:text><br>
  				<span style="font-size:8pt;">Enter a comma-separated list of terms</span>
  			</td>
  			<td valign="top">Exclude: </td>
  			<td valign="top">
  				<html:text name="proteinInferFilterForm" property="descriptionNotLike" size="40"></html:text><br>
  				<span style="font-size:8pt;">Enter a comma-separated list of terms</span>
  			</td>
  		</tr>
  	</table>
  	</td>
  </tr>
  
 
 <logic:notPresent name="showGoForm">
  <tr>
    	<td colspan="3" align="center">
    		<html:hidden name="proteinInferFilterForm" property="doDownload" />
    		<html:hidden name="proteinInferFilterForm" property="doGoEnrichment" />
    		<button class="plain_button" style="margin-top:2px;" 
    		        onclick="javascript:updateResults();return false;">Update</button>
    		<!--<html:submit styleClass="plain_button" style="margin-top:2px;">Update</html:submit>-->
    	</td>
    	 
  </tr>
  </logic:notPresent>
  
 </TABLE>
 

<logic:notPresent name="showGoForm">
 <div align="center" style="margin:10 0 10 0;">
   	<a href="" onclick="javascript:downloadResults();return false;" ><b>Download Results</b></a> &nbsp; 
   	<html:checkbox name="proteinInferFilterForm"property="printPeptides" >Include Peptides</html:checkbox>
   	<html:checkbox name="proteinInferFilterForm"property="printDescriptions" >Include Descriptions</html:checkbox>
   	<html:checkbox name="proteinInferFilterForm"property="collapseGroups" >Collapse Protein Groups</html:checkbox>
  </div>
</logic:notPresent>


<logic:equal name="speciesIsYeast" value="true">
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
</logic:equal>
</html:form>
