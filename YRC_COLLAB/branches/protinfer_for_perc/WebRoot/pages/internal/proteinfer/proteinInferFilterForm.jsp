<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<script src="/yrc/js/tooltip.js"></script>

  <html:form action="/viewProteinInferenceResult" method="post" styleId="form1" >
  
  <html:hidden name="proteinInferFilterForm" property="pinferId" />
  
  <TABLE CELLPADDING="5px" CELLSPACING="2px" align="center" style="border: 1px solid gray;">
  
  <!-- Filtering options -->
  <tr>
  <td>
  	<span class="tooltip" title="These options determine what uniquely defines a peptide" style="cursor: pointer;">
  	Peptide Definition:
  	</span>
  </td>
  <td colspan="3">
  	<html:checkbox name="proteinInferFilterForm" property="peptideDef_useSequence" disabled="true">Sequence</html:checkbox>
  	<html:checkbox name="proteinInferFilterForm" property="peptideDef_useMods" value="true">Modifications</html:checkbox>
  	<html:checkbox name="proteinInferFilterForm" property="peptideDef_useCharge" value="true">Charge</html:checkbox>
  </td>
  </tr>
  
  <tr>
  <td>Min. Peptides: </td>
  <td><html:text name="proteinInferFilterForm" property="minPeptides" size="3"></html:text></td>
  
  <td>Min. Unique Peptides: </td>
  <td><html:text name="proteinInferFilterForm" property="minUniquePeptides" size="3"></html:text></td>
  </tr>
  
  <tr>
  <td>Min. Coverage:</td>
  <td><html:text name="proteinInferFilterForm" property="minCoverage" size="3"></html:text>(%)</td>
  
  <td>Min. Spectrum Matches: </td>
  <td><html:text name="proteinInferFilterForm" property="minSpectrumMatches" size="3"></html:text></td>
  </tr>
  
  <tr>
  	<td colspan="2">Group Indistinguishable Proteins: </td>
  	<td colspan="2">
  		<html:radio name="proteinInferFilterForm" property="joinGroupProteins" value="true">Yes</html:radio>
  		<html:radio name="proteinInferFilterForm" property="joinGroupProteins" value="false">No</html:radio>
  	</td>
  </tr>
  
  
  <tr>
    	<td colspan="4" align="center"><html:submit styleClass="button" >Update</html:submit></td>
    </tr>
 </TABLE>
 
</html:form>
