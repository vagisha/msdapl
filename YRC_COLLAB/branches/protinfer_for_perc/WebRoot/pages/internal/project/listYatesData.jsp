
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="org.yeastrc.www.proteinfer.ProteinferJob"%>
<%@page import="org.yeastrc.jobqueue.JobUtils"%>
<logic:notEmpty name="yatesdata" scope="request">

	<%
		Map<Integer, Integer> yatesRunToMsSearchMap = (Map<Integer, Integer>)request.getAttribute("yatesRunToMsSearchMap");
		Map<Integer, List<ProteinferJob>> yatesRunToProteinferRunMap = 
				(Map<Integer, List<ProteinferJob>>)request.getAttribute("yatesRunToProteinferRunMap");
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

	 <bean:define name="run" property="id" id="runId" />
	 <%	Integer searchId = yatesRunToMsSearchMap.get(runId); %>
	   		
	  <yrcwww:colorrow scheme="ms">
	   <TD valign="top" width="20%">
	   	<html:link href="/yrc/viewYatesRun.do" paramId="id" paramName="run" paramProperty="id">View Run</html:link>
	   	<% if(searchId != null && searchId > 0) {%>
	   	<div style="font-size:8pt; font-weight: bold;"><b>
	   	<a href="/yrc/newProteinInference.do?searchId=<%=searchId %>&projectId=<bean:write name="projectId" />">
	   	[Run IDPicker]
	   	</a></b></div>
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
	  
	  <bean:define name="run" property="id" id="runId" />
	   	<%	if(searchId != null && searchId > 0) {
	   		List<ProteinferJob> proteinferJobs = yatesRunToProteinferRunMap.get(searchId);
	   		if(proteinferJobs.size() > 0) {
	   	%>
	   		
	   		 <yrcwww:colorrow repeat="true" scheme="ms">
	 		 <td colspan="5" align="center" style="font-size: 8pt;">
	 		 <b>IDPicker Results</b>
	 		 <table align="center" width="90%" style="border: 1px solid gray;">
	   	     <tr>
	   	     <td style="font-size: 8pt; font-weight: bold;">Run ID</td>
	   	     <td style="font-size: 8pt; font-weight: bold;">Submitter</td>
	   	     <td style="font-size: 8pt; font-weight: bold;">Date</td>
	   	     <td style="font-size: 8pt; font-weight: bold;">Status</td>
	   	     </tr>
	   		<%
	   			for(ProteinferJob piJob: proteinferJobs) {%>
	   				<tr>
	   				<td style="font-size: 8pt;"><%=piJob.getPinferId() %></td>
	   				<td style="font-size: 8pt;"><%=piJob.getResearcher().getLastName()%></td>
	   				<td style="font-size: 8pt;"><%=piJob.getSubmitDate() %></td>
	   				<td style="font-size: 8pt;">
	   					<%int status = piJob.getStatus();
	   					if(status == JobUtils.STATUS_COMPLETE) {%>
	   						<a href="/yrc/viewProteinInferenceResult.do?inferId=<%=piJob.getPinferId() %>"><b><font color="green">
	   					<%} %>
	   					<%=piJob.getStatusDescription() %>
	   					<% if(status == JobUtils.STATUS_COMPLETE) {%>
	   						</font></b></a>
	   					<%} %>
	   				</td>
	   				</tr>
	   			<%} %>
	   		</table>
	   		</td>
	   		</yrcwww:colorrow>
	   	<%}} %>
	   	
	  
	  

     </logic:iterate>
	  
	 </TABLE>
	 </CENTER>
	 
	</yrcwww:contentbox>

</logic:notEmpty>