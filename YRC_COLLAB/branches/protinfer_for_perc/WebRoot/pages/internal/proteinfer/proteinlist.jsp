
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>



<div align="center">
	<table>
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

<bean:define name="proteinInferFilterForm" property="joinGroupProteins" id="groupProteins"></bean:define>
			
<div id="proteinListTable">
	<%@ include file="proteinListTable.jsp" %>
</div>