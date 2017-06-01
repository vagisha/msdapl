<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
	<logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/errors.jsp" %>

<link rel="stylesheet" href="css/kendo-ui-core/styles/kendo.common.min.css"/>
<link rel="stylesheet" href="css/kendo-ui-core/styles/kendo.fiori.min.css"/>
<script src="js/kendo-ui-core/jquery.min.js"></script>
<script src="js/kendo-ui-core/kendo.ui.core.min.js"></script>

<script>
    var colorPicker;
    $(document).ready(function() {
        //alert("ready");
        $("#colorPicker").kendoColorPicker({
            value: "#ffffff",
            buttons: false,
            preview: true,
            select: preview
        });
        colorPicker = $("#colorPicker").data("kendoColorPicker");
        colorPicker.value('<bean:write name="addInstrumentForm" property="color"/>')
    });

    function preview(e)
    {
        var color = e.value;
        $("#chosenColor").css("color", color).text(color);
        // $("#chosenColor").text(color);
        $("input#colorForForm").val(color);
    }
</script>
<yrcwww:contentbox title="Add MS Instrument" width="800">

	<html:form action="saveInstrument.do">

		<html:hidden name="addInstrumentForm" property="id"/>

		<table align="center" width="98%" class="table_basic">
			<tr>
				<td class="left_align"><b>Instrument Name:</b>
				</td><td class="left_align"><html:text name="addInstrumentForm" property="name" size="75"></html:text></td>
			</tr>
			<tr>
				<td class="left_align"><b>Description:</b></td>
				<td class="left_align"><html:text name="addInstrumentForm" property="description" size="75"></html:text></td>
			</tr>
			<tr>
				<td class="left_align"><b>Active:</b></td>
				<td class="left_align">
					<html:radio name="addInstrumentForm" property="active" value="true">Active</html:radio>
					<html:radio name="addInstrumentForm" property="active" value="false">Retired</html:radio>
					<!--<html:checkbox name="addInstrumentForm" property="active"></html:checkbox>-->
				</td>
			</tr>
			<tr>
				<td class="left_align"><b>Color:</b></td>
				<td class="left_align">
					<input id="colorPicker"/> <span id="chosenColor" style="padding:5px;"></span>
					<html:hidden styleId="colorForForm" name="addInstrumentForm" property="color"></html:hidden>
				</td>
			</tr>
		</table>
		<div align="center">
			<html:submit styleClass="plain_button">Save</html:submit>
			<script>
                function cancel() { document.location.href = 'manageInstruments.do';}
			</script>
			<input type="button" value="Cancel" class="plain_button" onclick="cancel()"/>
		</div>
	</html:form>


</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>