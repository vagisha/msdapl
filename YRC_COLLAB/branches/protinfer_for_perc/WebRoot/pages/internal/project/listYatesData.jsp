
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="org.yeastrc.www.proteinfer.ProteinferJob"%>
<%@page import="org.yeastrc.jobqueue.JobUtils"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

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
	   <TD>&nbsp;</TD>
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
	   	<yrcwww:member group="any">
	   	<% if(searchId != null && searchId > 0) {%>
	   	<div style="font-size:8pt; font-weight: bold;"><b>
	   	<a href="/yrc/newProteinInference.do?searchId=<%=searchId %>&projectId=<bean:write name="projectId" />">
	   	[Run Protein Inference]
	   	</a></b></div>
	   	<%} %>
	   	</yrcwww:member>
	   	
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
	   			request.setAttribute("piJobs", proteinferJobs);
	   	%>
	   		
	   		 <yrcwww:colorrow repeat="true" scheme="ms">
	 		 <td colspan="5" align="center" style="font-size: 8pt;">
	 		 <b>Protein Inference Results</b>
	 		 <table align="center" width="90%" style="border: 1px solid gray; margin-bottom: 20px;">
	   	     <tr>
	   	     <td style="font-size: 8pt; font-weight: bold;">ID</td>
	   	     <td style="font-size: 8pt; font-weight: bold;">Submitter</td>
	   	     <td style="font-size: 8pt; font-weight: bold;">Date</td>
	   	     <td style="font-size: 8pt; font-weight: bold;">Comments</td>
	   	     <td style="font-size: 8pt; font-weight: bold;">Status</td>
	   	     </tr>
	   		 <logic:iterate name="piJobs" id="pijob" type="org.yeastrc.www.proteinfer.ProteinferJob">
	   		 	<tr>
	   		 		<td style="font-size: 8pt;"><bean:write name="pijob" property="pinferId"/></td>
	   		 		<td style="font-size: 8pt;"><bean:write name="pijob" property="researcher.lastName"/></td>
	   		 		<td style="font-size: 8pt;"><bean:write name="pijob" property="submitDate"/></td>
	   		 		<td style="font-size: 8pt;"><bean:write name="pijob" property="comments"/></td>
	   		 		<td style="font-size: 8pt;">
	   		 			<%
	   		 				String fontcolor = "black";
	   		 				int projectId = (Integer)request.getAttribute("projectId");
	   		 				String url = "/yrc/viewProteinInferenceJob.do?projectId="+projectId
	   		 				             +"&inferId="+pijob.getPinferId();
	   		 				if(pijob.getStatus() == JobUtils.STATUS_COMPLETE) {
	   		 					fontcolor = "green";
	   		 					url = "/yrc/viewProteinInferenceResult.do?inferId="+pijob.getPinferId();
	   		 				} 
	   		 				else if (pijob.getStatus() == JobUtils.STATUS_HARD_ERROR) {
	   		 					fontcolor = "red";
	   		 				}
	   		 			%>
	   		 			<a href="<%=url %>">
	   		 			<b><font color="<%=fontcolor %>"><bean:write name="pijob" property="statusDescription"/></font></b></a>
	   		 		</td>
	   		 	</tr>
	   		 </logic:iterate>
	   		</table>
	   		</td>
	   		</yrcwww:colorrow>
	   	<%}} %>
	   	
	  
	  

     </logic:iterate>
	  
	 </TABLE>
	 </CENTER>
	 
	</yrcwww:contentbox>

</logic:notEmpty>