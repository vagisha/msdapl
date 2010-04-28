<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>


<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>


<yrcwww:contentbox title="QC Results" centered="true" width="95" widthRel="true">
<center>

	<div style="padding:10 7 10 7; margin-bottom:5; border: 1px dashed gray;background-color: #F0F0F0;">
	<html:form action="updateQCPlots">
		<html:hidden property="experimentId"/>
		<html:hidden property="analysisId"/>
		qvalue: <html:text property="qvalue"/>
		<html:submit>Update</html:submit>
	</html:form>
	</div>
	
	<!-- #PSM vs RT plot -->
	<div style="padding:10 7 10 7; margin-bottom:5; border: 1px dashed gray;background-color: #F0F0F0;">
		<logic:present name="psmRTDistributionChart">
		<table>
			<tr>
			<td colspan="2" align="center" style="padding-bottom: 7px;">
				<b>
				Total PSMs: <bean:write name="totalPsmCount"/>
				<br>
				Filtered PSMs: <bean:write name="goodPsmCount"/> &nbsp; (<bean:write name="goodPsmPerc" />%)
				</b>
				<br>
			</td>
			</tr>
			<tr>
			<td valign="top" align=>
				<img src="<bean:write name="psmRTDistributionChart"/>" align="top" alt="#PSM-RT Plot" style="padding-right:20px;"></img>
			</td>
			<td valign="top">
				<table class="table_basic stripe_table" width="100%">
				<thead>
				<tr>
				<th>File</th>
				<th>Total</th>
				<th>Filtered</th>
				<th>% Filtered</th>
				</tr>
				</thead>
				<tbody>
				<logic:iterate name="psmRtFileStats" id="file">
					<tr>
						<td><bean:write name="file" property="fileName"/></td>
						<td><bean:write name="file" property="totalCount"/></td>
						<td><bean:write name="file" property="goodCount"/></td>
						<td><bean:write name="file" property="percentGoodCount"/>%</td>
					</tr>
				</logic:iterate>
				</tbody>
				</table>
			</td>
			</tr>
		</table>
		</logic:present>
	</div>
	
	<!-- #Spectra vs RT plot -->
	<div style="padding:10 7 10 7; margin-bottom:5; border: 1px dashed gray;background-color: #F0F0F0;">
		<logic:present name="spectraRTDistributionChart">
		<table>
			<tr>
			<td colspan="2" align="center" style="padding-bottom: 7px;">
				<b>
				Total MS/MS Spectra: <bean:write name="totalSpectraCount"/>
				<br>
				Filtered Spectra: <bean:write name="goodSpectraCount"/> &nbsp; (<bean:write name="goodSpectraPerc" />%)
				</b>
				<br>
			</td>
			</tr>
			<tr>
			<td valign="top" align=>
				<img src="<bean:write name="spectraRTDistributionChart"/>" align="top" alt="#Spectra-RT Plot" style="padding-right:20px;"></img>
			</td>
			<td valign="top">
				<table class="table_basic stripe_table" width="100%">
				<thead>
				<tr>
				<th>File</th>
				<th>Total</th>
				<th>Filtered</th>
				<th>% Filtered</th>
				</tr>
				</thead>
				<tbody>
				<logic:iterate name="spectraRtFileStats" id="file">
					<tr>
						<td><bean:write name="file" property="fileName"/></td>
						<td><bean:write name="file" property="totalCount"/></td>
						<td><bean:write name="file" property="goodCount"/></td>
						<td><bean:write name="file" property="percentGoodCount"/>%</td>
					</tr>
				</logic:iterate>
				</tbody>
				</table>
			</td>
			</tr>
		</table>
		</logic:present>
	</div>
	
</center>	
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>