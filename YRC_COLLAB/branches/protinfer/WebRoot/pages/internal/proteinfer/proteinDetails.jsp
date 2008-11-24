<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<div  align="center" style="padding:5px;font-size: 10pt;width: 90%; color: black;" >
<b><bean:write name="inferredProtein" property="accession"/></b>
<span style="text-decoration: underline; cursor: pointer; font-size: 8pt; color: red;"
      onclick="toggleProteinSequence(<bean:write name="inferredProtein" property="proteinId" />, <bean:write name="pinferId" />)"
      id="protseqbutton_<bean:write name="inferredProtein" property="proteinId" />">[View Sequence]</span>
<br>
<span style="color: #888888; font-size: 9pt"><bean:write name="inferredProtein" property="description" /></span>
</div>

<table align="center" cellpadding="2" style="border: 1px solid gray; border-spacing: 2px">
<tr class="ms_A">
	<td>Coverage(%)</td>
	<td># Peptides</td>
	<td># Uniq.Peptides</td>
	<td># Spectra </td>
	<td>Other Proteins in Group</td>
</tr>
<tr>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="inferredProtein" property="percentCoverage" /></td>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="inferredProtein" property="peptideEvidenceCount" /></td>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="inferredProtein" property="uniquePeptideEvidenceCount" /></td>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="inferredProtein" property="spectralEvidenceCount" /></td>
	<td style="border: 1px #CCCCCC dotted;" align="center">
		<logic:empty name="groupProteins">--</logic:empty>
		<logic:iterate name="groupProteins" id="prot">
			<span onclick="showProteinDetails(<bean:write name="prot" property="nrseqProteinId" />)" 
							style="text-decoration: underline; cursor: pointer">
			<bean:write name="prot" property="accession" />
			</span><br>
		</logic:iterate>
	</td>
</tr>
</table>
<br><br>

<!--  placeholder for protein sequence -->
<table  align="center" width="90%" id="protseqtbl_<bean:write name="inferredProtein" property="proteinId"/>" style="display: none;"">
	<tr><td style="background-color: #D4FECA;" id="protsequence_<bean:write name="inferredProtein" property="proteinId"/>" ></td></tr>
</table>

<br><br>

<table width="95%" id="protdetailstbl_<bean:write name="inferredProtein" property="proteinId"/>" class="stripe_table">
	<thead>
    <tr class="main">
    <th align="left"><b><font size="2pt">Peptide</font></b></th>
    <th width="10%" align="left"><b><font size="2pt"># Spectra</font></b></th>
    <th width="10%" align="left"><b><font size="2pt">Best FDR</font></b></th>
    <th width="10%" align="left"><b><font size="2pt">Unique</b></th>
    </tr>
    </thead>
   	<tbody>
       <logic:iterate name="inferredProtein" property="peptides" id="pept">
            <tr class="main">
            <td><bean:write name="pept" property="modifiedPeptideSeq" /></td>
            <td><bean:write name="pept" property="spectrumMatchCount" /></td>
            <td><bean:write name="pept" property="bestFdr" /></td>
            <td><bean:write name="pept" property="uniqueToProtein" /></td>
            </tr>
            <tr>
            	<tr><td colspan="5">
        		<table align="center" width="70%"
        			style="border: 1px dashed gray; border-spacing: 4px; margin-top: 6px; margin-bottom: 6px;" >
        			<tr>
			        <td style="text-decoration: underline;">Scan Number</td>
			        <td style="text-decoration: underline;">Charge</td>
			        <td style="text-decoration: underline;">XCorr</td>
			        <td style="text-decoration: underline;">DeltaCN</td>
			        <td style="text-decoration: underline;">FDR</td>
			        <td style="text-decoration: underline;">Spectrum</td>
			        </tr>
			        <logic:iterate name="pept" property="spectrumMatchList" id="psm">
			        	<tr>
			        		<td><bean:write name="psm" property="scanNumber" /></td>
			        		<td><bean:write name="psm" property="charge" /></td>
			        		<td><bean:write name="psm" property="xcorrRounded" /></td>
			        		<td><bean:write name="psm" property="deltaCnRounded" /></td>
			        		<td><bean:write name="psm" property="fdrRounded" /></td>
			        		<td><span style="text-decoration: underline; cursor: pointer;" 
			  					onclick="viewSpectrum(<bean:write name="psm" property="scanId" />, <bean:write name="psm" property="hitId" />)" >
								View
								</span>
							</td>
			        	</tr>
			        </logic:iterate>
			     </table>
			        
            	</tr>
        </logic:iterate>
        </tbody>
        </table>
    