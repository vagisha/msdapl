<logic:notEmpty name="locdata" scope="request">

	<!-- WE HAVE YEAST TWO-HYBRID DATA FOR THIS PROJECT -->
	<p><yrcwww:contentbox title="Localization Data" centered="true" width="750" scheme="localization">

	 <CENTER>
	 <TABLE CELLPADDING="no" CELLSPACING="0">

	  <yrcwww:colorrow scheme="localization">
	   <TD>&nbsp</TD>
	   <TD valign="top"><B><U>EXP. DATE</U></B></TD>
	   <TD valign="top"><B><U>PROT. 1</U></B></TD>
	   <TD valign="top"><B><U>PROT. 2</U></B></TD>	   
	   <TD valign="top"><B><U>COMMENTS</U></B></TD>
	  </yrcwww:colorrow>

	 <logic:iterate id="experiment" name="locdata">

	  <yrcwww:colorrow scheme="localization">
	   <TD valign="top" width="12%"><html:link href="/yrc/viewMicroscopyExperiment.do" paramId="id" paramName="experiment" paramProperty="id">View Data</html:link></TD>
	   <TD valign="top" width="13%"><bean:write name="experiment" property="experimentDate"/></TD>

	   <TD valign="top" width="17%"><NOBR>
	        <yrcwww:proteinLink name="experiment" property="bait1" />
       </NOBR></TD>

	   <TD valign="top" width="17%">
	   		<logic:empty name="experiment" property="bait2">
	   			N/A
	   		</logic:empty>
	   		
	   		<logic:notEmpty name="experiment" property="bait2">
			   <nobr><yrcwww:proteinLink name="experiment" property="bait2" /></nobr>
       		</logic:notEmpty>
       </TD>

	   <TD valign="top" width="41%"><bean:write name="experiment" property="comments"/></TD>


	 </yrcwww:colorrow>

     </logic:iterate>
	  
	 </TABLE>
	 </CENTER>
	 
	</yrcwww:contentbox>

</logic:notEmpty>