
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

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
		
		<td><span id="<bean:write name="protein" property="protein.nrseqProteinId" />" class="editprotannot"
			style="text-decoration: underline; cursor: pointer">Edit</span>
			<div id="dialog_<bean:write name="protein" property="protein.nrseqProteinId" />" class="flora">*</div></td>
		<td>
			<logic:equal name="protein" property="protein.isParsimonious" value="true"><b></logic:equal>
			<logic:equal name="protein" property="protein.isParsimonious" value="false"><font color="#888888"></logic:equal>
			<span onclick="showProteinDetails(<bean:write name="protein" property="protein.id" />)" 
					style="text-decoration: underline; cursor: pointer">
			<bean:write name="protein" property="accession" />
			</span>
			<logic:equal name="protein" property="protein.isParsimonious" value="false"></font></logic:equal>
			<logic:equal name="protein" property="protein.isParsimonious" value="true"></b></logic:equal>
			
		</td>
		<td style="font-size: 8pt;"><bean:write name="protein" property="shortDescription"/></td>
		<td><bean:write name="protein" property="protein.coverage"/></td>
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
				  onclick="showProteinCluster(<bean:write name="protein" property="protein.clusterId"/>)">
				<bean:write name="protein" property="protein.clusterId"/>
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
				<!--  peptides table will go here: proteinPeptides.jsp -->
				<div id="peptforprot_<bean:write name="proteinGroup" property="groupId" />"></div>
			</td>
		</tr>
	</logic:iterate>
	</tbody>
</table>