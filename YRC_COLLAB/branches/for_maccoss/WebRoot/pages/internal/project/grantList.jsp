<!-- Show the grant information entered using the new system -->
<logic:notEmpty name="grants">
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
			<tr>
				<td style="font-size:8pt;padding: 3px;padding-left:0px;"><bean:write name="grant" property="title" /></td>
				<td style="font-size:8pt;padding: 3px;">
				<html:link action="viewResearcher.do" paramId="id" paramName="grant" paramProperty="grantPI.ID"><bean:write name="grant" property="grantPI.lastName" /></html:link>
				</td>
				<td style="font-size:8pt;padding: 3px;"><bean:write name="grant" property="fundingSource.sourceType.displayName" /></td>
				<td style="font-size:8pt;padding: 3px;"><bean:write name="grant" property="fundingSource.sourceName.displayName" /></td>
				<td style="font-size:8pt;padding: 3px;"><bean:write name="grant" property="grantNumber" /></td>
			</tr>
		</logic:iterate>
		</table>
   	
   	</TD>
</yrcwww:colorrow>
</logic:notEmpty>


<!-- If no grants were entered using the new system we will show the grant information entered using the old system -->
	<logic:empty name="grants">
	<yrcwww:colorrow>
   		<TD valign="top" width="25%">Funding Source(s):</TD>
   		<TD valign="top" width="75%"><bean:write name="project" property="fundingTypes"/></TD>
  	</yrcwww:colorrow>

  	<yrcwww:colorrow>
   		<TD valign="top" width="25%">Federal Funding:</TD>
   		<TD valign="top" width="75%"><bean:write name="project" property="federalFundingTypes"/></TD>
  	</yrcwww:colorrow>

  	<logic:notEmpty name="project" property="foundationName">
	  	<yrcwww:colorrow>
	   		<TD valign="top" width="25%">Foundation Name:</TD>
	   		<TD valign="top" width="75%"><bean:write name="project" property="foundationName"/></TD>
	  	</yrcwww:colorrow>
  	</logic:notEmpty>
  
  	<yrcwww:colorrow>
   		<TD valign="top" width="25%">Grant number:</TD>
   		<TD valign="top" width="75%"><bean:write name="project" property="grantNumber"/></TD>
  	</yrcwww:colorrow>
  	<yrcwww:colorrow>
   		<TD valign="top" width="25%">Annual Budget:</TD>
   		<TD valign="top" width="75%"><bean:write name="project" property="grantAmount"/></TD>
  	</yrcwww:colorrow>

   	<yrcwww:member group="any">
    	<yrcwww:colorrow>
     		<TD WIDTH="25%" VALIGN="top">BTA:</TD>
    		 <TD WIDTH="75%" VALIGN="top"><bean:write name="project" property="BTA"/>%</TD>
   		 </yrcwww:colorrow>
   	</yrcwww:member>
   	</logic:empty>