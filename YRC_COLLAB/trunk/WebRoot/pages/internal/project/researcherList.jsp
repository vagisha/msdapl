	<logic:notEmpty name="project" property="PI">
		<bean:define id="pi" name="project" property="PI" scope="request"/>

		<yrcwww:colorrow>
			<TD valign="top" width="25%">PI:</TD>
			<TD valign="top" width="75%">
				<yrcwww:researcherLink name="pi" />
			</TD>
		</yrcwww:colorrow>
	</logic:notEmpty>

	<!-- list the non-PI researchers here -->
	<logic:notEmpty name="project" property="researchersWithoutPI">
		<logic:iterate name="project" property="researchersWithoutPI" id="researcher">
			<yrcwww:colorrow>
				<TD valign="top" width="25%">Researcher:</TD>
				<TD valign="top" width="75%">
					<yrcwww:researcherLink name="researcher" />
				</TD>
			</yrcwww:colorrow>
		</logic:iterate>
	</logic:notEmpty>

	<logic:notEmpty name="project" property="PI">

		<yrcwww:colorrow>
			<TD valign="top" width="25%">Organization:</TD>
			<TD valign="top" width="75%"><bean:write name="pi" property="organization"/>
		</yrcwww:colorrow>

	</logic:notEmpty>