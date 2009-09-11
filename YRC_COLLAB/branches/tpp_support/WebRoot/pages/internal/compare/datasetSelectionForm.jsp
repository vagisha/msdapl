
<%@page import="org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<script src="<yrcwww:link path='js/jquery.ui-1.6rc2/jquery-1.2.6.js'/>"></script>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>


<logic:notPresent name="datasetSelectionForm" scope="request">
	<logic:forward  name="selectComparisonDatasets" />
</logic:notPresent>


<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<CENTER>

<yrcwww:contentbox centered="true" title="Available Datasets" width="700">


<!-- Proceed only if we have available datasets -->

<logic:empty name="datasetSelectionForm" property="proteinferRunList">

	<div><b>There are no available protein datasets at this time.</b></div>

</logic:empty>

<html:form action="setComparisonFilters" method="POST">
<div><b>Please select 2 or more datasets from the list below</b></div>
<br>

<logic:notEmpty name="datasetSelectionForm" property="proteinferRunList">

	<div align="center">Available Datasets</div>
	<table width="90%" class="table_basic sortable" align="center">
	<thead>
		<tr align="left">
		<th></th>
		<th class="sort-int">Prot. Infer ID</th>
		<th class="sort-alpha">Program</th>
		<th class="sort-int">ProjectID</th>
		<th>Date</th>
		<th class="sort-alpha">Comments</th>
		</tr>
	</thead>
	<tbody>
	
		<bean:define id="hasDtaRuns" value="false" />
		
		<logic:iterate name="datasetSelectionForm" property="proteinferRunList" id="proteinferRun">
		<yrcwww:colorrow>
			<td>
				<html:checkbox name="proteinferRun" property="selected" indexed="true"/>
			</td>
			<td>
				<html:link action="viewProteinInferenceResult.do" paramId="pinferId" paramName="proteinferRun" paramProperty="runId">
					<logic:equal name="proteinferRun" property="programName" 
							value="<%=ProteinInferenceProgram.DTA_SELECT.name()%>">
							<%hasDtaRuns = "true"; %>
							<font color="red" style="bold">*</logic:equal>
					<bean:write name="proteinferRun" property="runId" />
					<logic:equal name="proteinferRun" property="programName" value=""></font</logic:equal>
				</html:link>
				<html:hidden name="proteinferRun" property="runId" indexed="true"/>
			</td>
			<td class="left_align">
				<bean:write name="proteinferRun" property="programDisplayName" />
				<html:hidden name="proteinferRun" property="programName" indexed="true"/>
			</td>
			
			<td>
				<logic:iterate name="proteinferRun" property="projectIdList" id="projectId">
					<html:link action="viewProject.do" paramId="ID" paramName="projectId">
						<bean:write name="projectId" />
					</html:link>
					&nbsp;
				</logic:iterate>
				<html:hidden name="proteinferRun" property="projectIdString" indexed="true"/>
			</td>
			
			<td>
				<bean:write name="proteinferRun" property="runDate" />
				<logic:notEmpty name="proteinferRun" property="runDate">
					<html:hidden name="proteinferRun" property="runDate" indexed="true"/>
				</logic:notEmpty>
			</td>
			
			<td class="left_align">
				<bean:write name="proteinferRun" property="comments" />
				<html:hidden name="proteinferRun" property="comments" indexed="true"/>
			</td>
		</yrcwww:colorrow>
	</logic:iterate>
	</tbody>
	</table>
</logic:notEmpty>	

<logic:equal name="hasDtaRuns" value="true">
<div style="color:red; font-weight: bold;" align="center">
* WARNING:  Comparison with DTASelect results is not yet fully supported.
</div>
</logic:equal>
<br>

<div align="center">
	<br>
	<html:checkbox name="datasetSelectionForm" property="groupProteins">Group Indistinguishable Proteins</html:checkbox><br>
	<html:submit value="Submit" styleClass="plain_button"/>
</div>

<br>

	
</html:form>

</yrcwww:contentbox>
</CENTER>


<%@ include file="/includes/footer.jsp" %>