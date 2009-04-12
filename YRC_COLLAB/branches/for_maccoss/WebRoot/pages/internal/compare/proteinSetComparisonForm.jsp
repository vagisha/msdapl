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

<yrcwww:contentbox centered="true" title="Available Experiments" width="700">


<logic:empty name="proteinSetComparisonForm" property="proteinferRunList">
<div><b>There are no available protein inference experiments at this time.</b></div>
</logic:empty>


<logic:notEmpty name="proteinSetComparisonForm" property="proteinferRunList">

<div><b>Please select 2 or 3 experiment from the list below</b></div>

<html:form action="doProteinSetComparison" method="POST">

	<table width="90%">
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
				<bean:write name="proteinferRun" property="runId" />
				<html:hidden name="proteinferRun" property="runId" indexed="true"/>
			</td>
			
			<td>
				<bean:write name="proteinferRun" property="projectId" />
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
	
	<tr>
		<td colspan="5" align="center"><html:submit value="Submit" styleClass="plain_button"/></td>
	</tr>
	</tbody>
	</table>
</html:form>

</logic:notEmpty>


</yrcwww:contentbox>
</CENTER>


<%@ include file="/includes/footer.jsp" %>