
<%@page import="org.yeastrc.ms.domain.search.SORT_ORDER"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:empty name="filterForm">
  <logic:forward name="viewProject" />
</logic:empty>


<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<%@ include file="percolatorResultJS.jsp" %>
<yrcwww:contentbox title="Percolator Results" centered="true" width="900">
<center>

	<!-- SUMMARY -->
	<div style="padding:0 7 0 7; margin-bottom:5; border: 1px dashed gray;background-color: #FFFAF0;">
		<table width="80%">
			<tr>
				<td align="center"><b>Project ID:</b>
					<logic:iterate name="projectIds" id="projId">
						<html:link action="viewProject.do" paramId="ID" paramName="projId"><bean:write name="projId" /></html:link>&nbsp;
					</logic:iterate>
				</td>
				<td align="center"><b>Experiment ID:</b>
					<logic:iterate name="experimentIds" id="exptId">
						<bean:write name="exptId" />&nbsp;
					</logic:iterate>
				</td>
				
				<td align="center"><b>Program: </b><bean:write name="program" /></td>
			</tr>
		</table>
	</div>
	
	
	<!-- FILTER FORM -->
	<%@ include file="percolatorFilterForm.jsp" %>



	<!-- PAGE RESULTS -->
	<bean:define name="results" id="pageable" />
	<%@include file="/pages/internal/pager.jsp" %>
	
	
				
	<!-- RESULTS TABLE -->
	<div style="background-color: #FFFAF0; margin:5 0 5 0; padding:5; border: 1px dashed gray;" > 
	<yrcwww:table name="results" tableId='perc_results' tableClass="perc_results" center="true" />
	</div>
	
</center>	
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>