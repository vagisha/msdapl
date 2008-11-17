
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%><logic:notEmpty name="yatesdata" scope="request">

	<%
		Map<Integer, Integer> yatesRunToMsSearchMap = (Map<Integer, Integer>)request.getAttribute("yatesRunToMsSearchMap");
		Map<Integer, List<Integer>> yatesRunToProteinferRunMap = (Map<Integer, List<Integer>>)request.getAttribute("yatesRunToProteinferRunMap");
	 %>
	<!-- WE HAVE YEAST TWO-HYBRID DATA FOR THIS PROJECT -->
	<p><yrcwww:contentbox title="Mass Spectrometry Data" centered="true" width="750" scheme="ms">

	 <CENTER>
	 <TABLE CELLPADDING="no" CELLSPACING="0" width="80%">

	  <yrcwww:colorrow scheme="ms">
	   <TD>&nbsp</TD>
	   <TD valign="top"><B><U>RUN DATE</U></B></TD>
	   <TD valign="top"><B><U>BAIT<br>PROTEIN</U></B></TD>
	   <TD valign="top"><B><U>BAIT<br>DESC</U></B></TD>
	   <TD valign="top"><B><U>COMMENTS</U></B></TD>
	  </yrcwww:colorrow>

	 <logic:iterate id="run" name="yatesdata">

	  <yrcwww:colorrow scheme="ms">
	   <TD valign="top" width="20%">
	   	<html:link href="/yrc/viewYatesRun.do" paramId="id" paramName="run" paramProperty="id">View Run</html:link>
	   	
	   	<bean:define name="run" property="id" id="runId" />
	   	<%	Integer searchId = yatesRunToMsSearchMap.get(runId);
	   		if(searchId != null && searchId > 0) {%>
	   		<div style="font-size:8pt;padding-top: 5px;">
	   		
	   		<%
	   			List<Integer> proteinferIds = yatesRunToProteinferRunMap.get(searchId); 
	   			for(Integer pinferId: proteinferIds) {%>
	   				<a href="/yrc/viewProteinInferenceResult.do?inferId=<%=pinferId %>">IDPicker Result <%=pinferId %></a>
	   				<br>
	   			<%} %>
	   		<b><a href="/yrc/newProteinInference.do?searchId=<%=searchId %>">[Run IDPicker]</a></b>
	   		</div>
	   	<%} %>
	   	
	  </TD>
	   <TD valign="top" width="20%"><bean:write name="run" property="runDate"/></TD>
	   <TD valign="top" width="20%"><NOBR>
	   
	   <logic:empty name="run" property="baitProtein">
	    None Entered
	   </logic:empty>
	   <logic:notEmpty name="run" property="baitProtein">
	   
	   	<nobr>
	   	 <yrcwww:proteinLink name="run" property="baitProtein" />
		</nobr>
	   
	   </logic:notEmpty>
	   
	   
	   
	   </NOBR></TD>
	   <TD valign="top" width="20%"><bean:write name="run" property="baitDesc"/></TD>

	   <TD valign="top" width="20%">
	   <logic:empty name="run" property="comments">
	    No Comments
	   </logic:empty>
	   <logic:notEmpty name="run" property="comments">
	    <bean:write name="run" property="comments"/>
	   </logic:notEmpty>
	   </TD>

	  </yrcwww:colorrow>

     </logic:iterate>
	  
	 </TABLE>
	 </CENTER>
	 
	</yrcwww:contentbox>

</logic:notEmpty>