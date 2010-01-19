<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<logic:notPresent name="job" scope="request">
  <logic:forward name="viewUploadJob" />
</logic:notPresent>


<yrcwww:contentbox title="View Upload Job" centered="true" width="80" widthRel="true" scheme="upload">
	<center>
	
	<a href="<yrcwww:link path='listUploadJobs.do?status=pending'/>"><b>View Pending Jobs</b></a> ||
	<a href="<yrcwww:link path='listUploadJobs.do?status=complete'/>"><b>View Completed Jobs</b></a>
	<br><br><br>
	
	<table border="0" width="85%" align="center" class="table_basic">
	
		<thead>
		<tr>
			<th width="100%" colspan="2" align="center"><span style="margin-bottom:20px;font-size:10pt;font-weight:bold;">Job Data</span></th>
		</tr>
		</thead>
		
		<tbody>
		<tr>
			<td width="20%" align="left" valign="top">Submitted By:</td>
			<td width="80%" align="left" valign="top">
				<a href="<yrcwww:link path='viewResearcher.do?'/>id=<bean:write name="job" property="submitter" />">
					<bean:write name="job" property="researcher.firstName" /> <bean:write name="job" property="researcher.lastName" /></a>
			</td>
		</tr>	

		<tr >
			<td width="20%" align="left" valign="top">Submitted On:</td>
			<td width="80%" align="left" valign="top"><bean:write name="job" property="submitDate" /></td>
		</tr>
	
		<tr >
			<td width="20%" align="left" valign="top">Last Change:</td>
			<td width="80%" align="left" valign="top"><bean:write name="job" property="lastUpdate" /></td>
		</tr>

		<tr >
			<td width="20%" align="left" valign="top">Status:</td>
			<td width="80%" align="left" valign="top">
				<logic:equal name="job" property="status" value="4">
					<bean:write name="job" property="statusDescription" /> <a href="<yrcwww:link path='viewYatesRun.do?'/>id=<bean:write name="job" property="runID" />"><span style="color:red;">[View Run]</span></a>
				</logic:equal>
				<logic:notEqual name="job" property="status" value="4"><!-- not completed -->
					<bean:write name="job" property="statusDescription" />
					
					<logic:notEqual name="job" property="status" value="1"><!-- not running -->
						[<a style="color:red;" href="<yrcwww:link path='deleteJob.do?'/>id=<bean:write name="job" property="id" scope="request"/>">Delete</a>]
						
					<logic:notEqual name="job" property="status" value="0"><!-- not waiting to run -->
					
					
						[<a style="color:red;" href="<yrcwww:link path='resetJob.do?'/>id=<bean:write name="job" property="id" scope="request"/>">Retry</a>]
						
					
					</logic:notEqual>
					</logic:notEqual>
					
				</logic:notEqual>
			</td>
		</tr>

		<logic:notEmpty name="job" property="log">
			<tr >
				<td width="100%" colspan="2">
					<div style="width:100%;height:auto;overflow:auto;">
						Log Text:<br><br>
						<pre style="font-size:8pt;"><bean:write name="job" property="log" /></pre>
					</div>
				</td>
			</tr>
		</logic:notEmpty>
		</tbody>
	</table>
	<br><br>
	<table border="0" width="85%" style="margin-top:10px;" align="center" class="table_basic">

		<thead>
		<tr >
			<th width="100%" colspan="2" align="center"><span style="margin-bottom:20px;font-size:10pt;font-weight:bold;">Experiment Details</span></th>
		</tr>
		</thead>
		
		<tbody>
		<tr >
			<td width="20%" align="left" valign="top">Project:</td>
			<td width="80%" align="left" valign="top">
				<a href="<yrcwww:link path='viewProject.do?'/>ID=<bean:write name="job" property="projectID" />">
					<bean:write name="job" property="project.title" /></a>
			</td>
		</tr>

		<tr >
			<td width="20%" align="left" valign="top">Directory:</td>
			<td width="80%" align="left" valign="top">
				<bean:write name="job" property="serverDirectory" />
			</td>
		</tr>

		<tr >
			<td width="20%" align="left" valign="top">Run Date:</td>
			<td width="80%" align="left" valign="top">
				<bean:write name="job" property="runDate" />
			</td>
		</tr>

		<tr >
			<td width="20%" align="left" valign="top">Species:</td>
			<td width="80%" align="left" valign="top">
				<logic:equal name="species" property="id" value="0">
					<bean:write name="species" property="name"/>
				</logic:equal>
				
				<logic:notEqual name="species" property="id" value="0">
					<a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=<bean:write name="species" property="id"/>">
    				<i><bean:write name="species" property="name" /></i></a>
				</logic:notEqual>
			</td>
			
		</tr>

		<tr >
			<td width="20%" align="left" valign="top">Comments:</td>
			<td width="80%" align="left" valign="top">
				<bean:write name="job" property="comments" />
			</td>
		</tr>
		</tbody>
	
	</table>
	</center>
	<br><br>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>