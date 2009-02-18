
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox scheme="pinfer" title="Protein Inference Job" width="700">
<center>
<logic:present name="pinferJob">
<table align="center" width="80%">
<yrcwww:colorrow scheme="pinfer">
<td>Job ID: </td>
<td><bean:write name="pinferJob" property="id"/></td>
</yrcwww:colorrow>
<yrcwww:colorrow scheme="pinfer">
<td>Submitted By: </td>
<td>
	<bean:write name="pinferJob" property="researcher.firstName"/>
	<bean:write name="pinferJob" property="researcher.lastName"/>
</td>
</yrcwww:colorrow>
<yrcwww:colorrow scheme="pinfer">
<td>Submitted On: </td>
<td><bean:write name="pinferJob" property="submitDate"/></td>
</yrcwww:colorrow>
<yrcwww:colorrow scheme="pinfer">
<td>Status: </td>
<td><bean:write name="pinferJob" property="statusDescription"/></td>
</yrcwww:colorrow>
<yrcwww:colorrow scheme="pinfer">
<td>Comments: </td>
<td><bean:write name="pinferJob" property="comments"/></td>
</yrcwww:colorrow>

<bean:define name="program" id="program" type="edu.uwpr.protinfer.ProteinInferenceProgram"/>
<logic:present name="params">
<yrcwww:colorrow scheme="pinfer">
<td colspan="2" align="center"><b>Parameters</b></td>
</yrcwww:colorrow>
<logic:iterate name="params" id="param" type="edu.uwpr.protinfer.database.dto.idpicker.IdPickerParam">
<yrcwww:colorrow scheme="pinfer" repeat="true">
	<td><%=program.getDisplayNameForParam(param.getName()) %></td>
	<td><bean:write name="param" property="value"/></td>
</yrcwww:colorrow>
</logic:iterate>
</logic:present>

<yrcwww:colorrow scheme="pinfer"><td colspan="2" align="center"><b>Input</b></td></yrcwww:colorrow>
<logic:iterate name="inputList" id="input">
<yrcwww:colorrow scheme="pinfer" repeat="true">
<td colspan="2"><bean:write name="input" property="fileName" /></td>
</yrcwww:colorrow>
</logic:iterate>

<logic:present name="pinferJob" property="log">
<yrcwww:colorrow scheme="pinfer">
<td colspan="2" align="center"><b>Log</b></td>
</yrcwww:colorrow>
<yrcwww:colorrow scheme="pinfer" repeat="true">
	<td colspan="2" align="left"><pre style="font-size:8pt;"><bean:write name="pinferJob" property="log"/></pre></td>
</yrcwww:colorrow>


</logic:present>

</table>
</logic:present>
</center>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>