
<%@page import="edu.uwpr.protinfer.ProteinInferenceProgram"%><%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox scheme="ms" title="Protein Inference Job" width="700">
<center>
<logic:present name="pinferJob">
<table align="center" width="80%">
<yrcwww:colorrow scheme="ms">
<td>Job ID: </td>
<td><bean:write name="pinferJob" property="id"/></td>
</yrcwww:colorrow>
<yrcwww:colorrow scheme="ms">
<td>Submitted By: </td>
<td>
	<bean:write name="pinferJob" property="researcher.firstName"/>
	<bean:write name="pinferJob" property="researcher.lastName"/>
</td>
</yrcwww:colorrow>
<yrcwww:colorrow scheme="ms">
<td>Submitted On: </td>
<td><bean:write name="pinferJob" property="submitDate"/></td>
</yrcwww:colorrow>
<yrcwww:colorrow scheme="ms">
<td>Status: </td>
<td><bean:write name="pinferJob" property="statusDescription"/></td>
</yrcwww:colorrow>
<yrcwww:colorrow scheme="ms">
<td>Comments: </td>
<td><bean:write name="pinferJob" property="comments"/></td>
</yrcwww:colorrow>

<bean:define name="program" id="program" type="edu.uwpr.protinfer.ProteinInferenceProgram"/>
<logic:present name="params">
<td colspan="2" align="center"><b>Parameters</b></td>
	<logic:iterate name="params" id="param" type="edu.uwpr.protinfer.database.dto.idpicker.IdPickerParam">
	<yrcwww:colorrow scheme="ms" repeat="true">
		<td><%=program.getDisplayNameForParam(param.getName()) %></td>
		<td><bean:write name="param" property="value"/></td>
	</yrcwww:colorrow>
	</logic:iterate>
</logic:present>


</table>
</logic:present>
</center>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>