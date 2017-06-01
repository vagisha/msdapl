<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
	<logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="instrumentList">
	<logic:forward name="manageInstruments" />
</logic:notPresent>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/errors.jsp" %>

<link REL="stylesheet" TYPE="text/css" HREF="css/tablesorter.css">

<script>
    function addInstrument() { document.location.href = "addInstrument.do";}
</script>

<yrcwww:contentbox title="MS Instruments" width="800">

	<div style="text-align:center; margin: auto;">
		<html:link action="viewAllInstrumentCalendar.do">View Instrument Calendar</html:link>
	</div>

	<logic:empty name="instrumentList">
		No instruments found in the database. Click <html:link action="addInstrument.do"><b>here</b></html:link> to add an instrument.
	</logic:empty>

	<logic:notEmpty name="instrumentList">
		<table width="98%" class="tablesorter">
			<thead>
			<tr>
				<th>ID</th>
				<th>Name</th>
				<th>Description</th>
				<th>Active</th>
				<th></th>
				<th></th>
			</tr>
			</thead>
			<tbody>
			<logic:iterate id="instrument" name="instrumentList">
				<tr>
					<td><bean:write name="instrument" property="ID"/></td>
					<td class="left_align">
						<span style='margin:2px; background-color:<bean:write name="instrument" property="hexColor"/>'>&nbsp;&nbsp;&nbsp;</span>
						<bean:write name="instrument" property="nameOnly"/>
					</td>
					<td class="left_align"><bean:write name="instrument" property="description"/></td>
					<td class="left_align">
						<logic:equal name="instrument" property="active" value="true"><span style="color:green; font-style:bold;">YES</span></logic:equal>
						<logic:equal name="instrument" property="active" value="false"><span style="color:red;">NO</span></logic:equal>
					</td>
					<td><font color="green">
						<html:link action="/editInstrument.do" paramId="instrumentId" paramName="instrument" paramProperty="ID">Edit</html:link>
					</td>
					<td>
						<html:link action="/viewTimeScheduledForInstrument.do" paramId="instrumentId" paramName="instrument" paramProperty="ID">List Usage </html:link>
					</td>
				</tr>
			</logic:iterate>
			</tbody>
		</table>
	</logic:notEmpty>

	<div align="center">
		<input type="button" class="plain_button" value="Add Instrument" onClick="addInstrument()">
	</div>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>