<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:contentbox title="Experiments" centered="true" width="850">

	<logic:empty name="experiments">
		<div align="center" style="margin:20">
		There are no experiments for this project. To upload an experiment for this project click <a href="" onClick="javascript:goMacCoss(); return false;">here</a>
		</div>
	</logic:empty>

	<logic:notEmpty name="experiments">
		
		
		<logic:iterate name="experiments" id="experiment" scope="request">
		
		
			<div style="border:1px dotted gray;margin:5 5 5 5; padding:0 0 5 0;">
			<div style="background-color:#ED9A2E;width:100%; margin:0; padding:3 0 3 0; color:white;">
				<span style="padding-left:10;"><b>Experiment ID: <bean:write name="experiment" property="id"/></b></span>
			</div>
			
			
			<div style="padding:0; margin:0;"> 
			<div style="margin:0; padding:5;">
			<table cellspacing="0" cellpadding="0">		
				<tr>	
					<td><b>Date Uploaded: </b></td><td style="padding-left:10"><bean:write name="experiment" property="uploadDate"/></td>
				</tr>
				<tr>
					<td><b>Location: </b></td>
					<td style="padding-left:10"><bean:write name="experiment" property="serverDirectory"/></td>
				</tr>
				<tr>
					<td><b>Comments: </b></td>
					<td style="padding-left:10"><bean:write name="experiment" property="comments"/></td>
				</tr>
			</table>
			</div>
			
			<!-- SEARCHES FOR THE EXPERIMENT -->
			<logic:notEmpty name="experiment" property="searches">
				<logic:iterate name="experiment" property="searches" id="search">
					<div style="background-color: #FFFFE0; margin:5 5 5 5; padding:5; border: 1px dashed gray;" >
					<table width="90%">
						<tr>
							<td width="25%"><b>Search ID:</b></td> 
							<td width="25%"><bean:write name="search" property="id"/></td>
							<td width="25%"><b>Program: </b></td>
							<td width="25%"><b><bean:write name="search" property="searchProgram"/>
							&nbsp;
							<bean:write name="search" property="searchProgramVersion"/></b></td>
						</tr>
						<tr>
							<td width="25%"><b>Search Date: </b></td>
							<td width="25%"><bean:write name="search" property="searchDate"/></td>
							<td width="25%"><b>Upload Date: </b></td>
							<td width="25%"><bean:write name="search" property="uploadDate"/></td>
						</tr>
						<tr>
							<td width="25%"><b>Search Database: </b></td>
							<td width="25%"><bean:write name="search" property="searchDatabase"/></td>
							<td width="25%"><b>Enzyme: </b></td>
							<td width="25%"><bean:write name="search" property="enzymes"/></td>
						</tr>
						<tr>
							<td width="25%"><b>Static Modifications: </b></td>
							<td width="25%"><bean:write name="search" property="staticModifications"/></td>
							<td width="25%"><b>Dynamic Modifications: </b></td>
							<td width="25%"><bean:write name="search" property="dynamicModifications"/></td></tr>
					</table>
					</div>	
				</logic:iterate>
			</logic:notEmpty>
			
			<!-- SEARCH ANALYSES FOR THE EXPERIMENT -->
			<logic:notEmpty name="experiment" property="analyses">
				<logic:iterate name="experiment" property="analyses" id="analysis">
				<div style="background-color: #F0FFF0; margin:5 5 5 5; padding:5; border: 1px dashed gray;" >
					<table width="90%">
					<tr>
						<td width="25%"><b>Analysis ID:</b>
						<td width="25%"><bean:write name="analysis" property="id"/></td>
						<td width="25%"><b>Program: </b></td>
						<td width="25%"><b><bean:write name="analysis" property="analysisProgram"/>
						&nbsp;
						<bean:write name="analysis" property="analysisProgramVersion"/></b></td>
					</tr>
					<tr>
						<td width="25%"><b>Upload Date: </b></td>
						<td width="25%"><bean:write name="analysis" property="uploadDate"/></td>
					</tr>
					</table>
				</div>
				</logic:iterate>
			</logic:notEmpty>
			
			<!-- Files for this experiment -->
			<!-- <div style="background-color: #FFFAF0; margin:5 5 5 5; padding:5; border: 1px dashed gray;" > -->
			<bean:define name="experiment" property="id" id="experimentId" />
			<!--  <yrcwww:table name="experiment" tableId='<%="search_files_"+experimentId %>' tableClass="search_files" center="true" /> -->
			<!--  </div> -->
			</div>
		</div> <!-- End of one experiment -->
		</logic:iterate>
	</logic:notEmpty>

</yrcwww:contentbox>