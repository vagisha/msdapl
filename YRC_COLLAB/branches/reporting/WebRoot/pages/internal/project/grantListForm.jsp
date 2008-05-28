<tr><td colspan="2" align="center"><b>Funding Sources:</b>&nbsp;&nbsp;&nbsp;<a href="javascript:addFundingSource();">Add</a></td></tr>
<tr><td colspan="2" align="center" style="padding:5px;">
	<table id="fundingSources" style="border 1px solid #000000; width:80%;">
	<logic:present name="grants">
	<logic:notEmpty name="grants">
		<tr>
			<td></td>
			<td style="font-size:8pt;"><b>Grant Title</b></td>
			<td style="font-size:8pt;"><b>PI</b></td>
			<td style="font-size:8pt;"><b>Source Type</b></td>
			<td style="font-size:8pt;"><b>Source Name</b></td>
			<td style="font-size:8pt;"><b>Grant #</b></td>
			<td style="font-size:8pt;"><b>Annual Funds</b></td>
			<td></td>
			<td></td>
		</tr>
	</logic:notEmpty>
	<% int rowIdx = 1; int sourceIdx = rowIdx-1;%>
	<logic:iterate name="grants" id="grant">
		<bean:define name="grant" property="grantPI" id="PI"></bean:define>
		<bean:define name="grant" property="fundingSource" id="fundingSource"></bean:define>
		<tr>
			<td><input type="hidden" name="grant[<%=sourceIdx %>]" value="<bean:write name="grant" property="ID" />"/></td>
			<td style="font-size:8pt;"><bean:write name="grant" property="title" /></td>
			<td style="font-size:8pt;">
			<html:link href="/yrc/viewResearcher.do" paramId="id" paramName="grant" paramProperty="PIID"><bean:write name="PI" property="lastName" /></html:link>
			</td>
			<td style="font-size:8pt;"><bean:write name="fundingSource" property="typeDisplayName" /></td>
			<td style="font-size:8pt;"><bean:write name="fundingSource" property="displayName" /></td>
			<td style="font-size:8pt;"><bean:write name="grant" property="grantNumber" /></td>
			<td style="font-size:8pt;"><bean:write name="grant" property="grantAmount" /></td>
			<td><a href="javascript:confirmRemoveGrant('<%=rowIdx%>')" style="color:red; font-size:8pt;">[Remove]</a></td>
			<td><a href="javascript:editGrant(<bean:write name="grant" property="ID" />);" style="font-size:8pt;">Edit</a></td>
		</tr>
		<% rowIdx++; sourceIdx++;%>
	</logic:iterate>
	</logic:present>
	</table>
</td></tr>
