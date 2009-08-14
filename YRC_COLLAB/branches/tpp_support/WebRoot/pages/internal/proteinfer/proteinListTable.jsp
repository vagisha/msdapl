
<%@page import="org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria.SORT_BY"%>
<%@page import="org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria.SORT_ORDER"%>
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
			 if(sortBy == SORT_BY.GROUP_ID) colSortedClass = sortedClass;
		%>
		<th class="sortable def_sort_asc <%=colSortedClass %>" width="5%" id="<%=SORT_BY.GROUP_ID.name()%>">
			<b><font size="2pt">Protein Group</font></b>
		</th>
		
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
		<th width="55%"><b><font size="2pt">Description</font></b></th>
		
		
		<% colSortedClass = "";
		 if(sortBy == SORT_BY.COVERAGE) colSortedClass = sortedClass;
		%>
		<th class="sortable def_sort_desc <%=colSortedClass %>" width="5%" id="<%=SORT_BY.COVERAGE.name()%>">
			<b><font size="2pt">Coverage (%)</font></b>
		</th>
		
		<% colSortedClass = "";
		 if(sortBy == SORT_BY.NSAF) colSortedClass = sortedClass;
		%>
		<th class="sortable def_sort_desc <%=colSortedClass %>" width="5%" id="<%=SORT_BY.NSAF.name()%>">
			<b><font size="2pt">NSAF**</font></b>
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
		
		<% colSortedClass = "";
			 if(sortBy == SORT_BY.CLUSTER_ID) colSortedClass = sortedClass;
		%>
		<th class="sortable def_sort_asc <%=colSortedClass %>" width="5%" id="<%=SORT_BY.CLUSTER_ID.name()%>">
			<b><font size="2pt">Protein Cluster</font></b></th>
			
		</tr>
		</thead>
	</logic:notEmpty>
	<tbody>
	
	
	<logic:iterate name="proteinGroups" id="proteinGroup">
	
	
		<!--  WE ARE NOT DISPLAYING THE PROTEIN GROUP MEMBERS TOGETHER -->
		<logic:equal name="groupProteins" value="false">
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
				<span 
					id="annot_comment_<bean:write name="protein" property="protein.id" />" 
					style="display: none;"><bean:write name="protein" property="protein.userAnnotation" /></span>
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
			
			<!-- Protein accesion -->
			<td class="left_align"> 
			<logic:equal name="protein" property="protein.isParsimonious" value="true"><b></logic:equal>
			<logic:equal name="protein" property="protein.isParsimonious" value="false"><font color="#888888"></logic:equal>
			<span onclick="showProteinDetails(<bean:write name="protein" property="protein.id" />)" 
					style="text-decoration: underline; cursor: pointer">
			<bean:write name="protein" property="shortAccession" />
			</span>
			<logic:equal name="protein" property="protein.isParsimonious" value="false"></font></logic:equal>
			<logic:equal name="protein" property="protein.isParsimonious" value="true"></b></logic:equal>
			
			</td>
			
			<!-- Protein common name -->
			<td class="left_align"> 
			<logic:equal name="protein" property="protein.isParsimonious" value="true"><b></logic:equal>
			<logic:equal name="protein" property="protein.isParsimonious" value="false"><font color="#888888"></logic:equal>
			<span onclick="showProteinDetails(<bean:write name="protein" property="protein.id" />)" 
					style="text-decoration: underline; cursor: pointer">
				<bean:write name="protein" property="commonName" />
			</span>
			<logic:equal name="protein" property="protein.isParsimonious" value="false"></font></logic:equal>
			<logic:equal name="protein" property="protein.isParsimonious" value="true"></b></logic:equal>
			</td>
			
			<!-- Protein description -->
			<td style="font-size: 8pt;" class="left_align"><bean:write name="protein" property="shortDescription"/></td>
			<td><bean:write name="protein" property="protein.coverage"/></td>
			<td><bean:write name="protein" property="protein.nsafFormatted"/></td>
		
		
		
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
		<tr class="pept_row linked_row pinfer_filler">
		<td valign="top" colspan="2" class="pinfer_filler"><nobr>
			<span class="showpeptForProt" 
				  style="text-decoration: underline; cursor: pointer;font-size: 7pt; color: #000000;" 
				  id="<bean:write name="protein" property="protein.id" />"
				  title="<bean:write name="proteinGroup" property="groupId" />"
				  >Show Peptides</span></nobr></td>
		<td colspan="9" class="pinfer_filler">
			<!--  peptides table will go here: proteinPeptides.jsp -->
			<div id="peptforprot_<bean:write name="protein" property="protein.id" />_<bean:write name="proteinGroup" property="groupId" />"></div>
		</td>
		</tr>
			
		</logic:iterate>
		</logic:equal>
		
		
		
		
		
		<!-- WE ARE DISPLAYING PROTEIN GROUP MEMBERS TOGETHER -->
		<logic:equal name="groupProteins" value="true">
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
			<logic:equal name="protein" property="protein.isParsimonious" value="true"><b></logic:equal>
			<logic:equal name="protein" property="protein.isParsimonious" value="false"><font color="#888888"></logic:equal>
			<span onclick="showProteinDetails(<bean:write name="protein" property="protein.id" />)" 
					style="text-decoration: underline; cursor: pointer">
			<bean:write name="protein" property="shortAccession" />
			</span>
			<logic:equal name="protein" property="protein.isParsimonious" value="false"></font></logic:equal>
			<logic:equal name="protein" property="protein.isParsimonious" value="true"></b></logic:equal>
			
		</td>
		
		<!-- Protein common name -->
			<td class="left_align"> 
			<logic:equal name="protein" property="protein.isParsimonious" value="true"><b></logic:equal>
			<logic:equal name="protein" property="protein.isParsimonious" value="false"><font color="#888888"></logic:equal>
			<span onclick="showProteinDetails(<bean:write name="protein" property="protein.id" />)" 
					style="text-decoration: underline; cursor: pointer">
				<bean:write name="protein" property="commonName" />
			</span>
			<logic:equal name="protein" property="protein.isParsimonious" value="false"></font></logic:equal>
			<logic:equal name="protein" property="protein.isParsimonious" value="true"></b></logic:equal>
			</td>
			
		<!-- Protein Description -->
		<td style="font-size: 8pt;" class="left_align"><bean:write name="protein" property="shortDescription"/></td>
		
		
		<td><bean:write name="protein" property="protein.coverage"/></td>
		<td><bean:write name="protein" property="protein.nsafFormatted"/></td>
		
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
			<td valign="top" colspan="2" class="pinfer_filler"><nobr>
				<span class="showpeptForProtGrp" 
					  style="text-decoration: underline; cursor: pointer;font-size: 7pt; color: #000000;" 
					  id="<bean:write name="proteinGroup" property="groupId" />"
					  >Show Peptides</span></nobr></td>
			<td colspan="9" class="pinfer_filler">
				<!--  peptides table will go here: proteinPeptides.jsp -->
				<div id="peptforprot_<bean:write name="proteinGroup" property="groupId" />"></div>
			</td>
		</tr>
		</logic:equal>
	</logic:iterate>
	</tbody>
</table>
