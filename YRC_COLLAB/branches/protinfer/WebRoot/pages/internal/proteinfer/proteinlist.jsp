
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<div id="showallpeptlink" style="text-decoration: underline; cursor: pointer; color: #3D902A; padding-left: 5px;">Show All Peptides</div>
<table cellpadding="4" cellspacing="2" align="center" width="99%"  id="protlisttable">
	<logic:notEmpty name="proteinGroups">
		<thead>
		<tr>
		<th class="sort-int" width="1%"><b><font size="2pt">Protein Group</font></b></th>
		<th class="sort-alpha" width="1%"><b><font size="2pt">&nbsp;</font></b></th>
		<th class="sort-alpha"><b><font size="2pt">Protein</font></b></th>
		<th class="sort-alpha"><b><font size="2pt">Description</font></b></th>
		<th class="sort-float" width="3%"><b><font size="2pt">Coverage (%)</font></b></th>
		<th class="sort-int" width="3%"><b><font size="2pt"># Peptides</font></b></th>
		<th class="sort-int" width="3%"><b><font size="2pt"># Uniq. Peptides</font></b></th>
		<th class="sort-int" width="3%"><b><font size="2pt"># Spectra</font></b></th>
		<th class="sort-int" width="3%"><b><font size="2pt">Protein Cluster</font></b></th>
		</tr>
		</thead>
	</logic:notEmpty>
	<tbody>
	<logic:iterate name="proteinGroups" id="proteinGroup">
		
		<%boolean first = true;%>
		<logic:iterate name="proteinGroup" property="proteins" id="protein">
		
		<%if(first) { %>
		<tr class="protgrp_row sorting_row">
		<td rowspan="<bean:write name="proteinGroup" property="proteinCount" />" valign="middle">
			<bean:write name="proteinGroup" property="groupId" />
		</td>
		<%} else {%>
			<tr class="protgrp_row linked_row">
		<%} %>
		
		<td><span id="<bean:write name="protein" property="nrseqProteinId" />" class="editprotannot"
			style="text-decoration: underline; cursor: pointer">Edit</span>
			<div id="dialog_<bean:write name="protein" property="nrseqProteinId" />" class="flora">jQuery UI Dialog</div></td>
		<td>
			<logic:equal name="protein" property="isParsimonious" value="true"><b></logic:equal>
			<logic:equal name="protein" property="isParsimonious" value="false"><font color="#888888"></logic:equal>
			<span onclick="showProteinDetails(<bean:write name="protein" property="nrseqProteinId" />)" 
					style="text-decoration: underline; cursor: pointer">
			<bean:write name="protein" property="accession" />
			</span>
			<logic:equal name="protein" property="isParsimonious" value="false"></font></logic:equal>
			<logic:equal name="protein" property="isParsimonious" value="true"></b></logic:equal>
			
		</td>
		<td style="font-size: 8pt;"><bean:write name="protein" property="shortDescription"/></td>
		<td><bean:write name="protein" property="coverage"/></td>
		<%if(first) { first = false;%>
		<td rowspan="<bean:write name="proteinGroup" property="proteinCount" />" valign="middle">
			<bean:write name="proteinGroup" property="matchingPeptideCount"/>
		</td>
		<td rowspan="<bean:write name="proteinGroup" property="proteinCount" />" valign="middle">
			<bean:write name="proteinGroup" property="uniqMatchingPeptideCount"/>
		</td>
		<td rowspan="<bean:write name="proteinGroup" property="proteinCount" />" valign="middle">
			<bean:write name="proteinGroup" property="spectrumCount"/>
		</td>
		<td rowspan="<bean:write name="proteinGroup" property="proteinCount" />" valign="middle">
			<span id="protgrpslink" style="cursor:pointer;text-decoration:underline" 
				  onclick="showProteinCluster(<bean:write name="protein" property="clusterId"/>)">
				<bean:write name="protein" property="clusterId"/>
			</span>
		</td>
		<%} %>
		</tr>
		</logic:iterate>
		
		<!-- List the peptides and the best match for each peptide -->
		<tr class="pept_row linked_row">
			<td valign="top" colspan="2"><nobr>
				<span class="showpept" 
					  style="text-decoration: underline; cursor: pointer;font-size: 7pt; color: #3D902A;" 
					  id="<bean:write name="proteinGroup" property="groupId" />"
					  >Show Peptides</span></nobr></td>
			<td colspan="7">
				<table align="center" width="90%"
        			style="border: 1px dashed gray; border-spacing: 4px; margin-top: 6px; margin-bottom: 6px;display: none;" 
        			class="sortable peptlist"
        			id="peptforprot_<bean:write name="proteinGroup" property="groupId" />">
        			<thead><tr>
			        <th style="text-decoration: underline;font-size: 10pt;" class="sort-alpha" align="left">Peptide</th>
			        <th style="text-decoration: underline;font-size: 10pt;" class="sort-int" align="left"># Spectra</th>
			        <th style="text-decoration: underline;font-size: 10pt;" align="left">Unique</th>
			        <th style="text-decoration: underline;font-size: 10pt;" class="sort-float" align="left">Best FDR</th>
			        <th style="text-decoration: underline;font-size: 10pt;" align="left">Spectrum</th>
			        </tr></thead>
			        <tbody>
			        <logic:iterate name="proteinGroup" property="matchingPeptideGroups" id="peptideGroup">
			        <logic:iterate name="peptideGroup" property="peptides" id="peptide">
			        <bean:define name="peptide" property="bestSpectrumMatch" id="psm" />
			        	<tr>
			        		<td><bean:write name="peptide" property="sequence" /></td>
			        		<td><bean:write name="peptide" property="spectralCount" /></td>
			        		<td>
			        			<logic:equal name="peptideGroup" property="uniqueToProteinGroup" value="true">Yes</logic:equal>
			        			<logic:notEqual name="peptideGroup" property="uniqueToProteinGroup" value="true">No</logic:notEqual>
			        		</td>
			        		<td><bean:write name="psm" property="fdrRounded" /></td>
			        		<td><span style="text-decoration: underline; cursor: pointer;" 
			  					onclick="viewSpectrum(<bean:write name="psm" property="scanId" />, <bean:write name="psm" property="msRunSearchResultId" />)" >
								View
								</span>
							</td>
			        	</tr>
			        </logic:iterate>
			        </logic:iterate>
			        </tbody>
			     </table>
			</td>
		</tr>
	</logic:iterate>
	</tbody>
</table>