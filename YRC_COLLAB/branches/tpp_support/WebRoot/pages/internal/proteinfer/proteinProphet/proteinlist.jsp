
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>



<div align="center">
	<table>
		<tr>
		<td valign="top">
		<table CELLPADDING="5px" CELLSPACING="2px" align="center" style="border: 1px solid gray;">
			<tr>
				<td style="border: 1px dotted #AAAAAA;">
					# Indistinguishable Protein Groups (# proteins)
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					All: 
					<b><bean:write name="filteredProteinGrpCount" /></b>(<bean:write name="filteredProteinCount" />)
				</td>
				<td style="border: 1px dotted #AAAAAA;">Exclude Subsumed: 
					<b><bean:write name="parsimProteinGrpCount" /></b>(<bean:write name="parsimProteinCount" />)
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