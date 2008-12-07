<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<div  align="center" style="padding:5px;font-size: 10pt;width: 90%; color: black;" >
<b><bean:write name="protein" property="accession"/></b>
<span style="text-decoration: underline; cursor: pointer; font-size: 8pt; color: red;"
      onclick="toggleProteinSequence(<bean:write name="protein" property="protein.id" />)"
      id="protseqbutton_<bean:write name="protein" property="protein.id" />">[View Sequence]</span>
<br>
<span style="color: #888888; font-size: 9pt"><bean:write name="protein" property="description" /></span>
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
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="protein" property="protein.coverage" /></td>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="protein" property="protein.peptideCount" /></td>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="protein" property="protein.uniquePeptideCount" /></td>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="protein" property="protein.spectralCount" /></td>
	<td style="border: 1px #CCCCCC dotted;" align="center">
		<logic:empty name="groupProteins">--</logic:empty>
		<logic:iterate name="groupProteins" id="prot">
			<span onclick="showProteinDetails(<bean:write name="prot" property="protein.id" />)" 
							style="text-decoration: underline; cursor: pointer">
			<bean:write name="prot" property="accession" />
			</span><br>
		</logic:iterate>
	</td>
</tr>
</table>
<br><br>

<!--  placeholder for protein sequence -->
<table  align="center" width="90%" id="protseqtbl_<bean:write name="protein" property="protein.id"/>" style="display: none;"">
	<tr><td style="background-color: #D4FECA;padding-left: 130px;" id="protsequence_<bean:write name="protein" property="protein.id"/>" ></td></tr>
</table>

<br><br>

<table width="95%" id="protdetailstbl_<bean:write name="protein" property="protein.id"/>" class="stripe_table">
	<thead>
    <tr class="main">
    <th align="left"><b><font size="2pt">Peptide</font></b></th>
    <th width="10%" align="left"><b><font size="2pt">Charge</font></b></th>
    <th width="10%" align="left"><b><font size="2pt"># Spectra</font></b></th>
    <th width="10%" align="left"><b><font size="2pt">Best FDR</font></b></th>
    <th width="10%" align="left"><b><font size="2pt">Unique</b></th>
    </tr>
    </thead>
   	<tbody>
       <logic:iterate name="ionList" id="ion">
            <tr class="main">
            <td><bean:write name="ion" property="ion.sequence" /></td>
            <td><bean:write name="ion" property="ion.charge" /></td>
            <td><bean:write name="ion" property="ion.spectralCount" /></td>
            <td><bean:write name="ion" property="ion.bestFdr" /></td>
            <td><bean:write name="ion" property="isUniqueToProteinGroup" /></td>
            </tr>
            <tr>
            	<tr><td colspan="5">
        		<table align="center" width="70%"
        			style="border: 1px dashed gray; border-spacing: 4px; margin-top: 6px; margin-bottom: 6px;" >
        			<tr>
				        <td style="text-decoration: underline;">Scan Number</td>
				        <td style="text-decoration: underline;">Charge</td>
				        
				        <logic:equal name="searchProgram" value="sequest">
				        	<td style="text-decoration: underline;">XCorr</td>
				        </logic:equal>
				        
				        <logic:equal name="searchProgram" value="prolucid">
				        	<td style="text-decoration: underline;">primaryScore</td>
				        </logic:equal>
				        
				        <td style="text-decoration: underline;">DeltaCN</td>
				        <td style="text-decoration: underline;">FDR</td>
				        <td style="text-decoration: underline;">Spectrum</td>
			        </tr>
			        <logic:iterate name="ion" property="psmList" id="psm">
			        	<logic:equal name="searchProgram" value="sequest">
			        		<bean:define name="psm" type="org.yeastrc.ms.domain.search.sequest.SequestSearchResult" id="sequestPsm"></bean:define>
			        	</logic:equal>
			        	<logic:equal name="searchProgram" value="prolucid">
			        		<bean:define name="psm" type="org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult" id="prolucidPsm"></bean:define>
			        	</logic:equal>	
			        	<tr>
			        		<td>scanNum</td>
			        		<td><bean:write name="psm" property="charge" /></td>
			        		<logic:equal name="searchProgram" value="sequest">
			        			<td><bean:write name="sequestPsm" property="sequestResultData.xCorr" /></td>
			        			<td><bean:write name="sequestPsm" property="sequestResultData.deltaCN" /></td>
			        		</logic:equal>
			        		<logic:equal name="searchProgram" value="prolucid">
			        			<td><bean:write name="prolucidPsm" property="prolucidResultData.primaryScore" /></td>
			        			<td><bean:write name="prolucidPsm" property="prolucidResultData.deltaCN" /></td>
			        		</logic:equal>
			        		
			        		<td>fdr</td>
			        		<td><span style="text-decoration: underline; cursor: pointer;" 
			  					onclick="viewSpectrum(<bean:write name="psm" property="scanId" />, <bean:write name="psm" property="id" />)" >
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
    