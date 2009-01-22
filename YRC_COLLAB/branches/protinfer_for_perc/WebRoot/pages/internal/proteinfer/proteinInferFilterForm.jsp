<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

  <html:form action="/updateProteinInferenceResult" method="post" styleId="filterForm" >
  
  <html:hidden name="proteinInferFilterForm" property="pinferId" />
  
  <TABLE CELLPADDING="5px" CELLSPACING="5px" align="center" style="border: 1px solid gray;">
  
  <!-- Filtering options -->
  
  <tr>
  
  <td><table>
  <tr>
  <td>Min. Peptides: </td>
  <td><html:text name="proteinInferFilterForm" property="minPeptides" size="3"></html:text></td>
  </tr>
  <tr>
  <td>Min. Unique Peptides: </td>
  <td><html:text name="proteinInferFilterForm" property="minUniquePeptides" size="3"></html:text></td>
  </tr>
  </table></td>
  
  <td><table>
  <tr>
  <td>Min. Coverage:</td>
  <td><html:text name="proteinInferFilterForm" property="minCoverage" size="3"></html:text>(%)</td>
  </tr>
  <tr>
  <td>Min. Spectrum Matches: </td>
  <td><html:text name="proteinInferFilterForm" property="minSpectrumMatches" size="3"></html:text></td>
  </tr>
  </table></td>
  
  <td><table>
  <tr>
  	<td colspan="2">Group Indistinguishable Proteins: </td>
  	<td colspan="2">
  		<html:radio name="proteinInferFilterForm" property="joinGroupProteins" value="true">Yes</html:radio>
  		<html:radio name="proteinInferFilterForm" property="joinGroupProteins" value="false">No</html:radio>
  	</td>
  </tr>
  <tr>
  	<td colspan="2">Show Proteins: </td>
  	<td colspan="2">
  		<html:radio name="proteinInferFilterForm" property="showAllProteins" value="true">All</html:radio>
  		<html:radio name="proteinInferFilterForm" property="showAllProteins" value="false">Parsimonious</html:radio>
  	</td>
  </tr>
  </tr>
  </table></td>
  </tr>
  <tr>
    	<td colspan="3" align="center"><html:submit styleClass="button" >Update</html:submit></td>
  </tr>
 </TABLE>
 
</html:form>
