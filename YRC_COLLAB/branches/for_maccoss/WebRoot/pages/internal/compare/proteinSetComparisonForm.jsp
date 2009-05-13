<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<script src="<yrcwww:link path='js/jquery.ui-1.6rc2/jquery-1.2.6.js'/>"></script>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>


<logic:notPresent name="proteinSetComparisonForm" scope="request">
	<logic:forward  name="newProteinSetComparison" />
</logic:notPresent>


<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<CENTER>

<yrcwww:contentbox centered="true" title="Available Datasets" width="700">


<!-- Proceed only if we have available datasets -->

<logic:empty name="proteinSetComparisonForm" property="proteinferRunList">

<logic:empty name="proteinSetComparisonForm" property="dtaRunList">

	<div><b>There are no available protein datasets at this time.</b></div>

</logic:empty>
</logic:empty>

<html:form action="doProteinSetComparison" method="POST">
<div><b>Please select 2 or more datasets from the list below</b></div>
<br>

<logic:notEmpty name="proteinSetComparisonForm" property="proteinferRunList">

	<div align="center">Available Datasets</div>
	<table width="90%" class="table_basic" align="center">
	<thead>
		<tr align="left">
		<th></th>
		<th>Prot. Infer ID</th>
		<th>ProjectID</th>
		<th>Date</th>
		<th>Comments</th>
		</tr>
	</thead>
	<tbody>
		<logic:iterate name="proteinSetComparisonForm" property="proteinferRunList" id="proteinferRun">
		<yrcwww:colorrow>
			<td>
				<html:checkbox name="proteinferRun" property="selected" indexed="true"/>
			</td>
			<td>
				<html:link action="viewProteinInferenceResult.do" paramId="pinferId" paramName="proteinferRun" paramProperty="runId">
					<bean:write name="proteinferRun" property="runId" />
				</html:link>
				<html:hidden name="proteinferRun" property="runId" indexed="true"/>
			</td>
			
			<td>
				<html:link action="viewProject.do" paramId="ID" paramName="proteinferRun" paramProperty="projectId">
					<bean:write name="proteinferRun" property="projectId" />
				</html:link>
				<html:hidden name="proteinferRun" property="projectId" indexed="true"/>
			</td>
			
			<td>
				<bean:write name="proteinferRun" property="runDate" />
				<html:hidden name="proteinferRun" property="runDate" indexed="true"/>
			</td>
			
			<td>
				<bean:write name="proteinferRun" property="comments" />
				<html:hidden name="proteinferRun" property="comments" indexed="true"/>
			</td>
		</yrcwww:colorrow>
	</logic:iterate>
	</tbody>
	</table>
</logic:notEmpty>	
	
<br>


<logic:notEmpty name="proteinSetComparisonForm" property="dtaRunList">

	<div align="center">Available DTASelect Datasets</div>
	<table width="90%" class="table_basic" align="center">
	<thead>
		<tr align="left">
		<th></th>
		<th>DTASelect ID</th>
		<th>ProjectID</th>
		<th>Date</th>
		<th>Comments</th>
		</tr>
	</thead>
	<tbody>
		<logic:iterate name="proteinSetComparisonForm" property="dtaRunList" id="dtaRun">
		<yrcwww:colorrow>
			<td>
				<html:checkbox name="dtaRun" property="selected" indexed="true"/>
			</td>
			<td>
				<html:link action="viewYatesRun.do" paramId="id" paramName="dtaRun" paramProperty="runId">
					<bean:write name="dtaRun" property="runId" />
				</html:link>
				<html:hidden name="dtaRun" property="runId" indexed="true"/>
			</td>
			
			<td>
				<html:link action="viewProject.do" paramId="ID" paramName="dtaRun" paramProperty="projectId">
					<bean:write name="dtaRun" property="projectId" />
				</html:link>
				<html:hidden name="dtaRun" property="projectId" indexed="true"/>
			</td>
			
			<td>
				<bean:write name="dtaRun" property="runDate" />
				<html:hidden name="dtaRun" property="runDate" indexed="true"/>
			</td>
			
			<td>
				<bean:write name="dtaRun" property="comments" />
				<html:hidden name="dtaRun" property="comments" indexed="true"/>
			</td>
		</yrcwww:colorrow>
	</logic:iterate>
	</tbody>
	</table>
</logic:notEmpty>

	
	<div align="center">
		<html:submit value="Submit" styleClass="plain_button"/>
	</div>
</html:form>




</yrcwww:contentbox>
</CENTER>


<%@ include file="/includes/footer.jsp" %>