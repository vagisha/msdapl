
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>



<div align="center">
	<table>
		<tr>
		<td valign="top">
		<table CELLPADDING="5px" CELLSPACING="2px" align="center" style="border: 1px solid gray;">
			<tr>
				<td style="border: 1px dotted #AAAAAA;">
					# Protein Groups (# proteins): 
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


<bean:define name="proteinInferFilterForm" property="joinGroupProteins" id="groupProteins"></bean:define>
			
<div id="proteinListTable">
	<%@ include file="proteinListTable.jsp" %>
</div>