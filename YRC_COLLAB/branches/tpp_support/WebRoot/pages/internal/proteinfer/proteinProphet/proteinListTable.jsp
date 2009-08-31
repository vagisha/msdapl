
<%@page import="org.yeastrc.ms.domain.protinfer.SORT_BY"%>
<%@page import="org.yeastrc.ms.domain.protinfer.SORT_ORDER"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<div id="resultPager"  style="margin-top: 10px; margin-left: 10px;">

	<%int currPage = (Integer)(request.getAttribute("currentPage")); %>
	
	<logic:notEqual name="onFirst" value="true">
		<span style="text-decoration: underline; cursor: pointer;" onclick="pageResults(1)">
			First</span> &nbsp;
		<span style="cursor: pointer;" onclick="pageResults(<%=currPage - 1%>)">
			&lt;&lt;
		</span>
	</logic:notEqual>
	
	<logic:iterate name="pages" id="pg">
		<logic:notEqual name="pg" value="<%=String.valueOf(currPage) %>">
			<span style="text-decoration: underline; cursor: pointer;" onclick="pageResults(<bean:write name="pg"/>)">
				<bean:write name="pg"/>
			</span> &nbsp;
		</logic:notEqual>
		<logic:equal name="pg" value="<%=String.valueOf(currPage) %>">
			<bean:write name="pg"/> &nbsp;
		</logic:equal>
	</logic:iterate>
	
	<logic:notEqual name="onLast" value="true">
		<span style="cursor: pointer;" onclick="pageResults(<%=currPage + 1%>)">
			&gt;&gt;
		</span>  &nbsp;
		<span style="text-decoration: underline; cursor: pointer;" onclick="pageResults(<bean:write name="pageCount" />)">
			Last</span>
	</logic:notEqual>
	&nbsp; &nbsp; Page <bean:write name="currentPage" /> of <bean:write name="pageCount" />
</div>

<%
 SORT_BY sortBy = (SORT_BY)request.getAttribute("sortBy");
 SORT_ORDER sortOrder = (SORT_ORDER)request.getAttribute("sortOrder");
 String sortedClass = "";
 if(sortOrder == SORT_ORDER.ASC)	sortedClass = "sorted-asc";
 else sortedClass = "sorted-desc";
 %>

<table cellpadding="0" cellspacing="0" align="center" width="99%"  id="protlisttable" class="table_pinfer" style="margin-top:10;">

	<logic:notEmpty name="proteinGroups">
		<thead>
		<tr>
		
		<% String colSortedClass = "";
			 if(sortBy == SORT_BY.PROTEIN_PROPHET_GROUP) colSortedClass = sortedClass;
		%>
		<th class="sortable def_sort_asc <%=colSortedClass %>" width="5%" id="<%=SORT_BY.PROTEIN_PROPHET_GROUP.name()%>">
			<b><font size="2pt">Protein Prophet Group</font></b>
		</th>
		
		<% colSortedClass = "";
			 if(sortBy == SORT_BY.PROBABILITY) colSortedClass = sortedClass;
		%>
		
		<logic:equal name="groupProteins" value="true">
			<th class="sortable def_sort_desc <%=colSortedClass %>" width="5%" id="<%=SORT_BY.PROBABILITY.name()%>">
				<b><font size="2pt">Group Prob.</font></b>
			</th>
		</logic:equal>
		<logic:equal name="groupProteins" value="false">
			<th width="5%">
				<b><font size="2pt">Group Prob.</font></b>
			</th>
		</logic:equal>
		
		<th width="5%" >
			<b><font size="2pt">Indist. Protein Group</font></b>
		</th>
		
		<logic:equal name="groupProteins" value="false">
		<th class="sortable def_sort_desc <%=colSortedClass %>" width="5%" id="<%=SORT_BY.PROBABILITY.name()%>">
			<b><font size="2pt">Prob.</font></b>
		</th>
		</logic:equal>
		
		<logic:equal name="groupProteins" value="true">
		<th width="5%" >
			<b><font size="2pt">Prob.</font></b>
		</th>
		</logic:equal>
		
		
		<!-- Make FastaID and protein annotation sortable only if indistinguishable proteins are NOT grouped together -->
		<logic:equal name="groupProteins" value="true">
			<th width="1%"><b><font size="2pt">S</font></b></th>
			<th width="10%"><b><font size="2pt">Fasta ID</font></b></th>
		</logic:equal>
		
		
		<logic:equal name="groupProteins" value="false">
			<% colSortedClass = "";
			 if(sortBy == SORT_BY.VALIDATION_STATUS) colSortedClass = sortedClass;
			%>
			<th width="1%" class="sortable def_sort_asc <%=colSortedClass %>" id="<%=SORT_BY.VALIDATION_STATUS.name()%>"><b><font size="2pt">S</font></b></th>
			
			<% colSortedClass = "";
			 if(sortBy == SORT_BY.ACCESSION) colSortedClass = sortedClass;
			%>
			<th width="10%" class="sortable def_sort_asc <%=colSortedClass %>" id="<%=SORT_BY.ACCESSION.name()%>"><b><font size="2pt">Fasta ID</font></b></th>
		</logic:equal>
		
		
		<th width="10%"><b><font size="2pt">Common<br>Name</font></b></th>
		<th width="40%"><b><font size="2pt">Description</font></b></th>
		
		
		
		<% colSortedClass = "";
		 if(sortBy == SORT_BY.COVERAGE) colSortedClass = sortedClass;
		%>
		<th class="sortable def_sort_desc <%=colSortedClass %>" width="5%" id="<%=SORT_BY.COVERAGE.name()%>">
			<b><font size="2pt">Coverage (%)</font></b>
		</th>
		
			
		<% colSortedClass = "";
			 if(sortBy == SORT_BY.NUM_PEPT) colSortedClass = sortedClass;
		%>
		<th class="sortable def_sort_desc <%=colSortedClass %>" width="5%" id="<%=SORT_BY.NUM_PEPT.name()%>">
			<b><font size="2pt"># Peptides</font></b>
		</th>
		
		<% colSortedClass = "";
			 if(sortBy == SORT_BY.NUM_UNIQ_PEPT) colSortedClass = sortedClass;
		%>
		<th class="sortable def_sort_desc <%=colSortedClass %>" width="5%" id="<%=SORT_BY.NUM_UNIQ_PEPT.name()%>">
			<b><font size="2pt"># Uniq. Peptides</font></b></th>
		
		<% colSortedClass = "";
			 if(sortBy == SORT_BY.NUM_SPECTRA) colSortedClass = sortedClass;
		%>
		<th class="sortable def_sort_desc <%=colSortedClass %>" width="5%" id="<%=SORT_BY.NUM_SPECTRA.name()%>">
			<b><font size="2pt"># Spectra</font></b></th>
		
			
		</tr>
		</thead>
	</logic:notEmpty>
	<tbody>
	
	<!-- Iterate over all protein prophet groups -->
	<logic:iterate name="proteinGroups" id="proteinGroup">
	
		<bean:define name="proteinGroup" property="indistinguishableProteinGroupCount" id="iGrpCount" />
		<bean:define name="proteinGroup" property="proteinCount" id="proteinCount" />
		
		<!-- WE ARE DISPLAYING PROTEIN_PROPHET GROUP MEMBERS TOGETHER -->
		<logic:equal name="groupProteins" value="true">
			<bean:define id="prophet_grp_row_span" value="<%= String.valueOf((Integer)proteinCount + (Integer)iGrpCount)%>" />
		</logic:equal>
		
		<%boolean begin_pp_grp = true;%>
		
		<!-- Iterate over all indistinguishable protein groups in a protein prophet group-->
		<logic:iterate name="proteinGroup" property="indistinguishableProteinGroups" id="iGroup">
		
		<bean:define name="iGroup" property="proteinCount" id="grp_rowspan" />
		<%boolean begin_i_grp = true;%>
		
		<!--  WE ARE NOT DISPLAYING THE PROTEIN GROUP MEMBERS TOGETHER -->
		<logic:equal name="groupProteins" value="false">
			<bean:define name="iGroup" property="proteinCount" id="prophet_grp_row_span" />
			<%begin_pp_grp = true;%>
		</logic:equal>
		
		<!-- Iterate over all  proteins in a indistinguishable group-->
		<logic:iterate name="iGroup" property="proteins" id="protein">
		
		<%if(begin_pp_grp) { begin_pp_grp = false; %>
		<tr class="protgrp_row sorting_row">
		<td rowspan="<bean:write name='prophet_grp_row_span' />" valign="middle" style="border-bottom-color:#CFCFCF;">
			<bean:write name="proteinGroup" property="proteinProphetGroupNumber" />
		</td>
		<td rowspan="<bean:write name='prophet_grp_row_span' />" valign="middle" style="border-bottom-color:#CFCFCF;">
			<bean:write name="proteinGroup" property="groupProbability" />
		</td>
		<%} else {%>
			<tr class="protgrp_row linked_row">
		<%} %>
		
		
		<%if(begin_i_grp) { %>
		<td rowspan="<bean:write name="grp_rowspan" />" valign="middle">
			<bean:write name="iGroup" property="groupId" />
		</td>
		<td rowspan="<bean:write name="grp_rowspan" />" valign="middle">
			<bean:write name="iGroup" property="probability" />
		</td>
		<%} %>
		
		
		<!-- User Validation -->
		<td>
		<span id="<bean:write name="protein" property="protein.id" />"
				title="<bean:write name="protein" property="accession" />" 
				class="editprotannot"
				style="text-decoration: underline; cursor: pointer" >
				
			<!-- Are there user entered comments for this protein -->
			<logic:present name="protein" property="protein.userAnnotation">
				<span id="annot_comment_<bean:write 
				      name="protein" property="protein.id" />" 
				      style="display: none;"><bean:write name="protein" property="protein.userAnnotation" />
				</span>
			</logic:present>
			<logic:notPresent name="protein" property="protein.userAnnotation">
				<span id="annot_comment_<bean:write name="protein" property="protein.id" />" style="display: none;"></span>
			</logic:notPresent>
			
			<!-- User entered validation -->
			<span 
			class="prot_annot_<bean:write name="protein" property="protein.userValidation.statusChar" />"
			id="annot_validation_style_<bean:write name="protein" property="protein.id" />"></span>
			<span 
			class="sort_key"
			style="display: none;"
			id="annot_validation_text_<bean:write name="protein" property="protein.id" />"><bean:write name="protein" property="protein.userValidation.statusChar" /></span>
					
		</span>
		</td>
		
		<!-- Protein accession -->
		<td class="left_align">
			<logic:equal name="protein" property="protein.subsumed" value="false"><b></logic:equal>
			<logic:equal name="protein" property="protein.subsumed" value="true"><font color="#888888"></logic:equal>
			<span onclick="showProteinDetails(<bean:write name="protein" property="protein.id" />)" 
					style="text-decoration: underline; cursor: pointer">
			<bean:write name="protein" property="shortAccession" />
			</span>
			<logic:equal name="protein" property="protein.subsumed" value="true"></font></logic:equal>
			<logic:equal name="protein" property="protein.subsumed" value="false"></b></logic:equal>
			
		</td>
		
		<!-- Protein common name -->
			<td class="left_align"> 
			<logic:equal name="protein" property="protein.subsumed" value="false"><b></logic:equal>
			<logic:equal name="protein" property="protein.subsumed" value="true"><font color="#888888"></logic:equal>
			<span onclick="showProteinDetails(<bean:write name="protein" property="protein.id" />)" 
					style="text-decoration: underline; cursor: pointer">
				<bean:write name="protein" property="commonName" />
			</span>
			<logic:equal name="protein" property="protein.subsumed" value="true"></font></logic:equal>
			<logic:equal name="protein" property="protein.subsumed" value="false"></b></logic:equal>
			</td>
			
		<!-- Protein Description -->
		<td style="font-size: 8pt;" class="left_align"><bean:write name="protein" property="shortDescription"/></td>
		
		
		
		<td><bean:write name="protein" property="protein.coverage"/></td>
		
		<%if(begin_i_grp) { begin_i_grp = false;%>
		<td rowspan="<bean:write name="grp_rowspan" />" valign="middle">
			<bean:write name="iGroup" property="matchingPeptideCount"/>
		</td>
		<td rowspan="<bean:write name="grp_rowspan" />" valign="middle">
			<bean:write name="iGroup" property="uniqMatchingPeptideCount"/>
		</td>
		<td rowspan="<bean:write name="grp_rowspan" />" valign="middle">
			<bean:write name="iGroup" property="spectrumCount"/>
		</td>
		
		<%} %>
		
		</tr>
		</logic:iterate>
		
		<!-- List the peptides and the best match for each peptide -->
		<tr class="pept_row linked_row"> 
			<td valign="top" colspan="2" class="pinfer_filler"><nobr>
				<span class="showpeptForProtGrp" 
					  style="text-decoration: underline; cursor: pointer;font-size: 7pt; color: #000000;" 
					  id="<bean:write name="iGroup" property="groupId" />"
					  >Show Peptides</span></nobr></td>
			
			<logic:equal name="groupProteins" value="true">
				<td colspan="8" class="pinfer_filler">
			</logic:equal>
			<logic:equal name="groupProteins" value="false">
				<td colspan="10" class="pinfer_filler">
			</logic:equal>
				<!--  peptides table will go here: proteinPeptides.jsp -->
				<div id="peptforprot_<bean:write name="iGroup" property="groupId" />"></div>
			</td>
		</tr>
		</logic:iterate>
		
	</logic:iterate>
	</tbody>
</table>
