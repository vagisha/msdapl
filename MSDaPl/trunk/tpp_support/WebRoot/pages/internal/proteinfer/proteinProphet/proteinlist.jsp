
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>



<div align="center">
	<table>
		<tr>
		<td valign="top">
		<table CELLPADDING="5px" CELLSPACING="2px" align="center" style="border: 1px solid gray;">
			<tr>
			<td style="border: 1px dotted #AAAAAA;">&nbsp;</td>
			<td style="border: 1px dotted #AAAAAA;">All</td>
			<td style="border: 1px dotted #AAAAAA;">Exclude Subsumed</td></tr>
			<tr>
				<td style="border: 1px dotted #AAAAAA;">
					Protein Prophet Groups
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="filteredProphetGrpCount" /></b>
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="parsimProphetGrpCount" /></b>
				</td>
			</tr>
			<tr>
				<td style="border: 1px dotted #AAAAAA;">
					Indistinguishable Protein Groups
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="filteredProteinGrpCount" /></b>
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="parsimProteinGrpCount" /></b>
				</td>
			</tr>
			<tr>
				<td style="border: 1px dotted #AAAAAA;">
					Proteins
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="filteredProteinCount" /></b>
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="parsimProteinCount" /></b>
				</td>
			</tr>
		</table>
		</td>
		
		
		
		</tr>
	</table>
</div>


<bean:define name="proteinProphetFilterForm" property="joinProphetGroupProteins" id="groupProteins"></bean:define>
			
<div id="proteinListTable">
	<%@ include file="proteinListTable.jsp" %>
</div>