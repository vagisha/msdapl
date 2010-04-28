<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<div style="padding:7 7 0 7; margin-bottom:30;">

		<html:form action="viewPercolatorResults" method="POST">
		
		<logic:present name="filterForm" property="searchAnalysisId">
			<html:hidden name="filterForm" property="searchAnalysisId"/>
		</logic:present>
		<logic:present name="filterForm" property="runSearchAnalysisId">
			<html:hidden name="filterForm" property="runSearchAnalysisId"/>
		</logic:present>
		
		<html:hidden name="filterForm" property="pageNum" styleId="pageNum"/>
		<html:hidden name="filterForm" property="sortByString" styleId="sortBy"/>
		<html:hidden name="filterForm" property="sortOrderString" styleId="sortOrder"/>
		<html:hidden name="filterForm" property="usePEP" />
		
			<table cellspacing="0" cellpadding="2" >
				<tr>
					<td>Min. Scan</td> <td> <html:text name="filterForm" property="minScan" size="5"/> </td>
					<td>Max. Scan</td> <td> <html:text name="filterForm" property="maxScan" size="5" /> </td>
					<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<td>Min. Charge</td><td> <html:text name="filterForm" property="minCharge" size="5" /> </td>
					<td>Max. Charge</td><td> <html:text name="filterForm" property="maxCharge" size="5" /> </td>
				</tr>
				
				<tr>
					<td>Min. RT</td> <td> <html:text name="filterForm" property="minRT" size="5"/> </td>
					<td>Max. RT</td> <td> <html:text name="filterForm" property="maxRT" size="5" /> </td>
					<td></td>
					<td>Min. Obs. Mass</td><td> <html:text name="filterForm" property="minObsMass" size="5" /> </td>
					<td>Max. Obs. Mass</td><td> <html:text name="filterForm" property="maxObsMass" size="5" /> </td>
				</tr>
				
				<tr>
					<td>Min. q-value</td> <td> <html:text name="filterForm" property="minQValue" size="5"/> </td>
					<td>Max. q-value</td> <td> <html:text name="filterForm" property="maxQValue" size="5" /> </td>
					<td></td>
					
					<logic:equal name="filterForm" property="usePEP" value="true">
						<td>Min. PEP</td><td> <html:text name="filterForm" property="minPep" size="5" /> </td>
						<td>Max. PEP</td><td> <html:text name="filterForm" property="maxPep" size="5" /> </td>
					</logic:equal>
					
					<logic:equal name="filterForm" property="usePEP" value="false">
						<td>Min. Discriminant Score</td><td> <html:text name="filterForm" property="minDs" size="5" /> </td>
						<td>Max. Discriminant Score</td><td> <html:text name="filterForm" property="maxDs" size="5" /> </td>
					</logic:equal>
				</tr>
				
				<tr>
					<td valign="top">Peptide</td> 
					<td colspan=4 align="left" valign="top" style="font-size:8pt;"> 
						<html:text name="filterForm" property="peptide" size="25" /><br>
						Exact: <html:checkbox name="filterForm" property="exactPeptideMatch"  />
					</td>
					<td valign="top">Modified peptides</td><td valign="top"> <html:checkbox name="filterForm" property="showModified" /> </td>
					<td valign="top">Unmodified peptides</td><td valign="top"> <html:checkbox name="filterForm" property="showUnmodified" /> </td>
				</tr>
				
				<tr>
					<td valign="top">File(s)</td> 
					<td colspan=6 align="left" valign="top" style="font-size:8pt;"> 
						<html:text name="filterForm" property="fileNameFilter" size="50" /><br>
						Enter comma-separated file names
					</td>
					
					<td valign="top">Unique peptides</td><td valign="top"> <html:checkbox name="filterForm" property="peptidesView" /> </td>
				</tr>
				
				<tr>
					<td colspan="9" align="center">
					<html:submit value="Update" 
									styleClass="plain_button" 
									onclick="javascript:updateResults();return false;"/>
					</td>
				</tr>
				<tr>
					<td colspan="9" align="center">
						<b># Results: </b><bean:write name="numResults" />
						&nbsp; &nbsp; &nbsp;
						<b># Results (filtered):</b><bean:write name="numResultsFiltered" />
					</td>
				</tr>
			</table>
		</html:form>
	</div>