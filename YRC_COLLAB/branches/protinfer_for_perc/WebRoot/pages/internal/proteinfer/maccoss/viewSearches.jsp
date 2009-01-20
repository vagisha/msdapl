<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox title="Available Searches" centered="true" width="1000" scheme="ms">

<logic:present name="searches" >
<table>
	<logic:iterate id="search" name="searches">
		<tr>
		<td>
			Search ID: <bean:write name="search" property="key" />
			<ul>
			<logic:iterate name="search" property="value" id="analysis">
				<li>
					Analysis ID: <bean:write name="analysis" property="key" />
					<a href="/yrc//newPercolatorProtInfer.do?searchId=<bean:write name="search" property="key" />&analysisId=<bean:write name="analysis" property="key" />">
					Run Protein Inference</a>
					
					<ul>
					<logic:iterate name="analysis" property="value" id="pinferRunId">
						<li>Protein Inference ID: 
							<a href="/yrc/viewProteinInferenceResult.do?inferId=<bean:write name="pinferRunId" />">
								<bean:write name="pinferRunId" />
							</a>
						</li>
					</logic:iterate>
					</ul>
				</li>
			</logic:iterate>
			</ul>
		
		</td>
		</tr>
	</logic:iterate>
	
</table>
</logic:present>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>