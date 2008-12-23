<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<logic:present name="proteinId">
<table align="center" width="90%"
  			style="border: 1px dashed gray; border-spacing: 4px; margin-top: 6px; margin-bottom: 6px;" 
  			class="sortable peptlist"
  			id="peptforprottbl_<bean:write name="proteinId" />_<bean:write name="proteinGroupId" />">
</logic:present>

<logic:notPresent name="proteinId">
<table align="center" width="90%"
  			style="border: 1px dashed gray; border-spacing: 4px; margin-top: 6px; margin-bottom: 6px;" 
  			class="sortable peptlist"
  			id="peptforprottbl_<bean:write name="proteinGroupId" />">
</logic:notPresent>

  	 <thead><tr>
     <th style="text-decoration: underline;font-size: 10pt;" class="sort-alpha" align="left">Peptide</th>
     <th style="text-decoration: underline;font-size: 10pt;" class="sort-int" align="left">Charge</th>
     <th style="text-decoration: underline;font-size: 10pt;" class="sort-int" align="left"># Spectra</th>
     <th style="text-decoration: underline;font-size: 10pt;" align="left">Unique</th>
     <th style="text-decoration: underline;font-size: 10pt;" class="sort-float" align="left">Best FDR</th>
     <th style="text-decoration: underline;font-size: 10pt;" align="left">Spectrum</th>
     </tr></thead>
     <tbody>
     <logic:iterate name="proteinPeptides" id="peptide">
     <bean:define name="peptide" property="ion.bestSpectrumMatch" id="psm" />
     	<tr>
     		<td><bean:write name="peptide" property="ion.sequence" /></td>
     		<td><bean:write name="peptide" property="ion.charge" /></td>
     		<td><bean:write name="peptide" property="ion.spectralCount" /></td>
     		<td>
     			<logic:equal name="peptide" property="isUniqueToProteinGroup" value="true">Yes</logic:equal>
     			<logic:notEqual name="peptide" property="isUniqueToProteinGroup" value="true">No</logic:notEqual>
     		</td>
     		<td><bean:write name="psm" property="fdrRounded" /></td>
     		<td><span style="text-decoration: underline; cursor: pointer;" 
				onclick="viewSpectrum(<bean:write name="peptide" property="scanId" />, <bean:write name="psm" property="msRunSearchResultId" />)" >
		View
		</span>
	</td>
     	</tr>
     </logic:iterate>
     </tbody>
</table>
