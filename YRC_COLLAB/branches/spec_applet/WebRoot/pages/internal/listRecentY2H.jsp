<logic:notEmpty name="y2hdata" scope="request">

	<!-- WE HAVE YEAST TWO-HYBRID DATA FOR THIS PROJECT -->
	<p><yrcwww:contentbox title="Recent Yeast Two-Hybrid Data" centered="true" width="650">

	 <CENTER>
	 <TABLE CELLPADDING="no" CELLSPACING="0">

	  <yrcwww:colorrow scheme="register">
	   <TD>&nbsp</TD>
	   <TD valign="top"><B><U>UPLOADED</U></B></TD>
	   <TD valign="top"><B><U>BAIT ORF</U></B></TD>
	   <TD valign="top"><B><U>COMMENTS</U></B></TD>
	  </yrcwww:colorrow>

	 <logic:iterate id="screen" name="y2hdata">

	  <yrcwww:colorrow scheme="register">
	   <TD valign="top" width="20%"><html:link href="/yrc/viewY2HScreen.do" paramId="ID" paramName="screen" paramProperty="ID">View Data</html:link></TD>
	   <TD valign="top" width="25%"><bean:write name="screen" property="uploadDate"/></TD>
	   <TD valign="top" width="20%"><NOBR>
	   		<yrcwww:proteinLink name="screen" property="bait.protein" />
       </NOBR></TD>
	   <TD valign="top" width="35%"><bean:write name="screen" property="comments"/></TD>
	  </yrcwww:colorrow>

     </logic:iterate>
	  
	 </TABLE>
	 </CENTER>
	 
	</yrcwww:contentbox>

</logic:notEmpty>