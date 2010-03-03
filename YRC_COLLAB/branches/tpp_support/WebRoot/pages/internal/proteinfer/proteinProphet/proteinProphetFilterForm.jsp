<%@page import="org.yeastrc.ms.domain.protinfer.ProteinUserValidation"%>
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

  <html:form action="/updateProteinProphetResult" method="post" styleId="filterForm" >
  
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
  <td>Protein Mol. Wt.: </td>
  <td>
  	Min <html:text name="proteinProphetFilterForm" property="minMolecularWt" size="3"></html:text>
  	Max <html:text name="proteinProphetFilterForm" property="maxMolecularWt" size="3"></html:text>
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
  
  <td valign="top"><table>
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
  <td>Protein pI: </td>
  <td>
  	Min <html:text name="proteinProphetFilterForm" property="minPi" size="3"></html:text>
  	Max <html:text name="proteinProphetFilterForm" property="maxPi" size="3"></html:text>
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
  
  <td valign="top"><table>
  <tr>
  	<td colspan="2" align="left">Display<br>ProteinProphet Groups: </td>
  	<td align="left">
  		<html:radio name="proteinProphetFilterForm" property="joinProphetGroupProteins" value="true">Yes</html:radio>
  	</td>
  	<td align="left">
  		<html:radio name="proteinProphetFilterForm" property="joinProphetGroupProteins" value="false">No</html:radio>
  	</td>
  </tr>
  <tr>
  	<td colspan="4" align="left">
  		<html:checkbox name="proteinProphetFilterForm" property="excludeSubsumed" >Exclude Subsumed</html:checkbox>
  	</td>
  </tr>
  <tr>
  	<td colspan="4" align="left">
  		<html:checkbox name="proteinProphetFilterForm" property="excludeIndistinProteinGroups" /> Exclude Indistinguishable Groups
  	</td>
  </tr>
  </table></td>
  </tr>
  
  <tr>
  
  	<td colspan="2">
  		Validation Status: 
  		<html:multibox name="proteinProphetFilterForm" property="validationStatus" value="All"/> All
  		<html:multibox name="proteinProphetFilterForm" property="validationStatus" 
  					   value="<%=String.valueOf(ProteinUserValidation.UNVALIDATED.getStatusChar()) %>"/> Unvalidated 
  		<html:multibox name="proteinProphetFilterForm" property="validationStatus"  
  		               value="<%=String.valueOf(ProteinUserValidation.ACCEPTED.getStatusChar()) %>"/> Accepted
  		<html:multibox name="proteinProphetFilterForm" property="validationStatus"  
  		               value="<%=String.valueOf(ProteinUserValidation.REJECTED.getStatusChar()) %>"/> Rejected
  		<html:multibox name="proteinProphetFilterForm" property="validationStatus"  
  		               value="<%=String.valueOf(ProteinUserValidation.NOT_SURE.getStatusChar()) %>"/> Not Sure
  	</td>
  	
  </tr>
  
  <tr>
  	<td colspan="2">
  		Include Charge: &nbsp;&nbsp;
  		<html:multibox name="proteinProphetFilterForm" property="chargeStates" value="All"/> All &nbsp;
  		<html:multibox name="proteinProphetFilterForm" property="chargeStates" value="1"/> +1 &nbsp;
  		<html:multibox name="proteinProphetFilterForm" property="chargeStates" value="2"/> +2 &nbsp;
  		<html:multibox name="proteinProphetFilterForm" property="chargeStates" value="3"/> +3 &nbsp;
  		<html:multibox name="proteinProphetFilterForm" property="chargeStates" value="4"/> +4 &nbsp;
  		<html:multibox name="proteinProphetFilterForm" property="chargeStates" value=">4"/> &gt; +4   
  	</td>
  </tr>
  
  <tr>
  	<td colspan="3">
  	<table align="left">
  		<tr>
  			<td valign="top">Fasta ID(s): </td>
  			<td valign="top"><html:text name="proteinProphetFilterForm" property="accessionLike" size="40"></html:text><br>
  				<span style="font-size:8pt;">Enter a comma-separated list of complete or partial identifiers</span>
  			</td>
  			<td valign="top">Peptide: </td>
  			<td valign="top">
  				<html:text name="proteinProphetFilterForm" property="peptide" size="40"></html:text><br>
  				<span style="font-size:8pt;">Exact Match:<html:checkbox property="proteinProphetFilterForm" property="exactPeptideMatch"></html:checkbox></span>
  			</td>
  		</tr>
  		
  		<tr>
  			<td valign="top">Description Include: </td>
  			<td valign="top"><html:text name="proteinProphetFilterForm" property="descriptionLike" size="40"></html:text><br>
  				<span style="font-size:8pt;">Enter a comma-separated list of terms</span>
  			</td>
  			<td valign="top">Exclude: </td>
  			<td valign="top">
  				<html:text name="proteinProphetFilterForm" property="descriptionNotLike" size="40"></html:text><br>
  				<span style="font-size:8pt;">Enter a comma-separated list of terms</span>
  			</td>
  		</tr>
  	</table>
  	</td>
  </tr>
  
  <tr>
    	<td colspan="3" align="center">
    		<html:submit styleClass="plain_button" style="margin-top:2px;">Update</html:submit>
    	</td>
    	 
    	 
  </tr>
 </TABLE>
</html:form>
