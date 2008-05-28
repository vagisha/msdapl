<yrcwww:colorrow>
   	<TD valign="top" width="25%">Funding Source(s):</TD>
   	<TD valign="top" width="75%">
   	<table id="fundingSources" style="width:90%;">
		<logic:notEmpty name="grants">
			<tr>
				<td style="font-size:8pt;"><nobr><b>Grant Title</b></nobr></td>
				<td style="font-size:8pt;"><nobr><b>PI</b></nobr></td>
				<td style="font-size:8pt;"><nobr><b>Source Type</b></nobr></td>
				<td style="font-size:8pt;"><nobr><b>Source Name</b></nobr></td>
				<td style="font-size:8pt;"><nobr><b>Grant #</b></nobr></td>
			</tr>
		</logic:notEmpty>
		<logic:iterate name="grants" id="grant">
			<bean:define name="grant" property="grantPI" id="PI"></bean:define>
			<bean:define name="grant" property="fundingSource" id="fundingSource"></bean:define>
			<tr>
				<td style="font-size:8pt;padding: 3px;padding-left:0px;"><bean:write name="grant" property="title" /></td>
				<td style="font-size:8pt;padding: 3px;">
				<html:link href="/yrc/viewResearcher.do" paramId="id" paramName="grant" paramProperty="PIID"><bean:write name="PI" property="lastName" /></html:link>
				</td>
				<td style="font-size:8pt;padding: 3px;"><bean:write name="fundingSource" property="typeDisplayName" /></td>
				<td style="font-size:8pt;padding: 3px;"><bean:write name="fundingSource" property="displayName" /></td>
				<td style="font-size:8pt;padding: 3px;"><bean:write name="grant" property="grantNumber" /></td>
			</tr>
		</logic:iterate>
		</table>
   	
   	</TD>
</yrcwww:colorrow>