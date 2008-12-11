
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<div align="center">
	<table>
		<tr>
		<td>
		<%@ include file="proteinInferFilterForm.jsp" %>
		</td>
		</tr>
		
		<tr>
		<td valign="top">
		<table CELLPADDING="5px" CELLSPACING="2px" align="center" style="border: 1px solid gray;">
			<tr>
				<td style="border: 1px dotted #AAAAAA;"># Unfiltered Proteins: <b><bean:write name="unfilteredProteinCount" /></b></td>
				<td style="border: 1px dotted #AAAAAA;">
					# Filtered Protein Groups (# proteins): 
					<b><bean:write name="filteredProteinGrpCount" /></b>(<bean:write name="filteredProteinCount" />)
				</td>
				<td style="border: 1px dotted #AAAAAA;"># Parsimonious Protein Groups (# proteins): 
					<b><bean:write name="parsimProteinGrpCount" /></b>(<bean:write name="parsimProteinCount" />)
				</td>
			</tr>
		</table>
		</td>
		
		
		
		</tr>
	</table>
</div>



<!-- Protein Annotation Dialog -->
<div id="prot_annot_dialog" class="flora" title="Annotate Protein">
	<input type="hidden" id="prot_id" value="" />
	Protein: <b><span id="prot_name"></span></b><br>
	<input type="radio" name="annotate" value="Accept" id="prot_accept" checked="checked"/>
	Accept	
	<input type="radio" name="annotate" value="Reject" id="prot_reject"/>
	Reject
	<input type="radio" name="annotate" value="Not Sure" id="prot_notsure" />
	Not Sure
	<br>
	<textarea name="comments" rows="4" cols="45" id="prot_comments"></textarea>
</div>

			

<table cellpadding="4" cellspacing="2" align="center" width="99%"  id="protlisttable">

	<logic:notEmpty name="proteinGroups">
		<thead>
		<tr>
		<th class="sort-int" width="1%"><b><font size="2pt">Protein Group</font></b></th>
		
		<!-- Make Protein and Description columsn sortable only if indistinguishable proteins are NOT grouped together -->
		<logic:equal name="proteinInferFilterForm" property="joinGroupProteins" value="true">
			<th width="1%"><b><font size="2pt">&nbsp;</font></b></th>
			<th><b><font size="2pt">Protein</font></b></th>
			<th><b><font size="2pt">Description</font></b></th>
		</logic:equal>
		
		<logic:equal name="proteinInferFilterForm" property="joinGroupProteins" value="false">
			<th class="sort-alpha-annot" width="1%"><b><font size="2pt">&nbsp;</font></b></th>
			<th class="sort-alpha"><b><font size="2pt">Protein</font></b></th>
			<th class="sort-alpha"><b><font size="2pt">Description</font></b></th>
		</logic:equal>
		
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
	
	
		<!--  WE ARE NOT DISPLAYING THE PROTEIN GROUP MEMBERS TOGETHER -->
		<logic:equal name="proteinInferFilterForm" property="joinGroupProteins" value="false">
			<bean:define value="1" id="rowspan" />
			
		<logic:iterate name="proteinGroup" property="proteins" id="protein">
		
		<tr class="protgrp_row sorting_row">
			<td rowspan="<bean:write name="rowspan" />" valign="middle">
				<bean:write name="proteinGroup" property="groupId" />
			</td>
		
			<td>
			<span id="<bean:write name="protein" property="protein.id" />"
				title="<bean:write name="protein" property="accession" />" 
				class="editprotannot"
				style="text-decoration: underline; cursor: pointer" >
				
				<!-- Are there user entered comments for this protein -->
				<logic:present name="protein" property="protein.userAnnotation">
					<span id="annot_comment_<bean:write name="protein" property="protein.id" />" style="display: none;">
						<bean:write name="protein" property="protein.userAnnotation" />
					</span>
				</logic:present>
				<logic:notPresent name="protein" property="protein.userAnnotation">
					<span id="annot_comment_<bean:write name="protein" property="protein.id" />" style="display: none;"></span>
				</logic:notPresent>
				
				<!-- User entered validation -->
				<logic:present name="protein" property="protein.userValidation">
					<span 
					class="prot_annot_<bean:write name="protein" property="protein.userValidation.statusChar" />"
					id="annot_validation_style_<bean:write name="protein" property="protein.id" />"></span>
					<span 
					class="sort_key"
					style="display: none;"
					id="annot_validation_text_<bean:write name="protein" property="protein.id" />"><bean:write name="protein" property="protein.userValidation.statusChar" /></span>
				</logic:present>
				
				<logic:notPresent name="protein" property="protein.userValidation">
					<span 
					class="prot_annot_U"
					id="annot_validation_style_<bean:write name="protein" property="protein.id" />"></span>
					<span 
					class="sort_key"
					style="display: none;"
					id="annot_validation_text_<bean:write name="protein" property="protein.id" />">U</span>
				</logic:notPresent>
					
			</span>
			</td>
			
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
		
			<td rowspan="<bean:write name="rowspan" />" valign="middle">
				<bean:write name="proteinGroup" property="matchingPeptideCount"/>
			</td>
			<td rowspan="<bean:write name="rowspan" />" valign="middle">
				<bean:write name="proteinGroup" property="uniqMatchingPeptideCount"/>
			</td>
				<td rowspan="<bean:write name="rowspan" />" valign="middle">
			<bean:write name="proteinGroup" property="spectrumCount"/>
			</td>
				<td rowspan="<bean:write name="rowspan" />" valign="middle">
			<span id="protgrpslink" style="cursor:pointer;text-decoration:underline" 
				  onclick="showProteinCluster(<bean:write name="protein" property="protein.clusterId"/>)">
				<bean:write name="protein" property="protein.clusterId"/>
			</span>
			</td>
		</tr>
		
		<!-- Display the View Peptides link for each protein in the group -->
		<tr class="pept_row linked_row">
		<td valign="top" colspan="2"><nobr>
			<span class="showpeptForProt" 
				  style="text-decoration: underline; cursor: pointer;font-size: 7pt; color: #3D902A;" 
				  id="<bean:write name="protein" property="protein.id" />"
				  title="<bean:write name="proteinGroup" property="groupId" />"
				  >Show Peptides</span></nobr></td>
		<td colspan="7">
			<!--  peptides table will go here: proteinPeptides.jsp -->
			<div id="peptforprot_<bean:write name="protein" property="protein.id" />_<bean:write name="proteinGroup" property="groupId" />"></div>
		</td>
		</tr>
			
		</logic:iterate>
		</logic:equal>
		
		
		
		
		
		<!-- WE ARE DISPLAYING PROTEIN GROUP MEMBERSTOGETHER -->
		<logic:equal name="proteinInferFilterForm" property="joinGroupProteins" value="true">
			<bean:define name="proteinGroup" property="proteinCount" id="rowspan" />
		
		<%boolean first = true;%>
		<logic:iterate name="proteinGroup" property="proteins" id="protein">
		
		<%if(first) { %>
		<tr class="protgrp_row sorting_row">
		<td rowspan="<bean:write name="rowspan" />" valign="middle">
			<bean:write name="proteinGroup" property="groupId" />
		</td>
		<%} else {%>
			<tr class="protgrp_row linked_row">
		<%} %>
		
		<td>
		<span id="<bean:write name="protein" property="protein.id" />"
				title="<bean:write name="protein" property="accession" />" 
				class="editprotannot"
				style="text-decoration: underline; cursor: pointer" >
				
			<!-- Are there user entered comments for this protein -->
			<logic:present name="protein" property="protein.userAnnotation">
				<span id="annot_comment_<bean:write name="protein" property="protein.id" />" style="display: none;">
					<bean:write name="protein" property="protein.userAnnotation" />
				</span>
			</logic:present>
			<logic:notPresent name="protein" property="protein.userAnnotation">
				<span id="annot_comment_<bean:write name="protein" property="protein.id" />" style="display: none;"></span>
			</logic:notPresent>
			
			<!-- User entered validation -->
			<logic:present name="protein" property="protein.userValidation">
				<span 
				class="prot_annot_<bean:write name="protein" property="protein.userValidation.statusChar" />"
				id="annot_validation_style_<bean:write name="protein" property="protein.id" />"></span>
				<span 
				class="sort_key"
				style="display: none;"
				id="annot_validation_text_<bean:write name="protein" property="protein.id" />"><bean:write name="protein" property="protein.userValidation.statusChar" /></span>
			</logic:present>
			
			<logic:notPresent name="protein" property="protein.userValidation">
				<span 
				class="prot_annot_U"
				id="annot_validation_style_<bean:write name="protein" property="protein.id" />"></span>
				<span 
				class="sort_key"
				style="display: none;"
				id="annot_validation_text_<bean:write name="protein" property="protein.id" />">U</span>
			</logic:notPresent>
					
		</span>
		</td>
		
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
		<td rowspan="<bean:write name="rowspan" />" valign="middle">
			<bean:write name="proteinGroup" property="matchingPeptideCount"/>
		</td>
		<td rowspan="<bean:write name="rowspan" />" valign="middle">
			<bean:write name="proteinGroup" property="uniqMatchingPeptideCount"/>
		</td>
		<td rowspan="<bean:write name="rowspan" />" valign="middle">
			<bean:write name="proteinGroup" property="spectrumCount"/>
		</td>
		<td rowspan="<bean:write name="rowspan" />" valign="middle">
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
				<span class="showpeptForProtGrp" 
					  style="text-decoration: underline; cursor: pointer;font-size: 7pt; color: #3D902A;" 
					  id="<bean:write name="proteinGroup" property="groupId" />"
					  >Show Peptides</span></nobr></td>
			<td colspan="7">
				<!--  peptides table will go here: proteinPeptides.jsp -->
				<div id="peptforprot_<bean:write name="proteinGroup" property="groupId" />"></div>
			</td>
		</tr>
		</logic:equal>
	</logic:iterate>
	</tbody>
</table>