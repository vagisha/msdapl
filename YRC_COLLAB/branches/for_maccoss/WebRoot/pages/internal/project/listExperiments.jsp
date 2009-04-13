
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<script src="<yrcwww:link path='js/jquery.ui-1.6rc2/jquery-1.2.6.js'/>"></script>
<script src="<yrcwww:link path='/js/jquery.blockUI.js'/>"></script>
<script>

// ---------------------------------------------------------------------------------------
// AJAX DEFAULTS
// ---------------------------------------------------------------------------------------
  $.ajaxSetup({
  	type: 'POST',
  	//timeout: 5000,
  	dataType: 'html',
  	error: function(xhr) {
  			
  				var statusCode = xhr.status;
		  		// status code returned if user is not logged in
		  		// reloading this page will redirect to the login page
		  		if(statusCode == 303)
 					window.location.reload();
 				
 				// otherwise just display an alert
 				else {
 					alert("Request Failed: "+statusCode+"\n"+xhr.statusText);
 				}
  			}
  });
  
  $.blockUI.defaults.message = '<b>Loading...</b>'; 
  $.blockUI.defaults.css.padding = 20;
  $.blockUI.defaults.fadeIn = 0;
  $.blockUI.defaults.fadeOut = 0;
  //$().ajaxStart($.blockUI).ajaxStop($.unblockUI);
  $().ajaxStop($.unblockUI);


// ---------------------------------------------------------------------------------------
// SHOW/HIDE FILES FOR AN EXPERIMENT
// --------------------------------------------------------------------------------------- 
// View all the files for an experiment
function toggleFilesForExperiment (experimentId) {

	//alert("experimentID: "+experimentId);
	var button = $("#listfileslink_"+experimentId);
	
	if(button.text() == "[List Files]") {
		//alert("View");
		if($("#listfileslink_"+experimentId+"_target").html().length == 0) {
			//alert("Getting...");
			// load data in the appropriate div
			$.blockUI(); 
			$("#listfileslink_"+experimentId+"_target").load("<yrcwww:link path='getFilesForExperiment.do' />",  	// url
							                        {'experimentId': experimentId}, 		// data
							                        function(responseText, status, xhr) {	// callback
								  						$.unblockUI();
								  						// make table sortable
														var $table = $("#search_files_"+experimentId);
														$table.attr('width', "100%");
														$('tbody > tr:odd', $table).addClass("tr_odd");
   														$('tbody > tr:even', $table).addClass("tr_even");
														//makeSortable(table);
								  					});
		}
		button.text("[Hide Files]");
		$("#listfileslink_"+experimentId+"_target").show();
	}
	else {
		button.text("[List Files]");
		$("#listfileslink_"+experimentId+"_target").hide();
	}
}

</script>


<yrcwww:contentbox title="Experiments" centered="true" width="850">

	<logic:empty name="experiments">
		<div align="center" style="margin:20">
		There are no experiments for this project. To upload an experiment for this project click <a href="" onClick="javascript:goMacCoss(); return false;">here</a>
		</div>
	</logic:empty>

	<logic:notEmpty name="experiments">
		
		
		<logic:iterate name="experiments" id="experiment" scope="request">
		
		
			<div style="border:1px dotted gray;margin:5 5 5 5; padding:0 0 5 0;">
			<div style="background-color:#ED9A2E;width:100%; margin:0; padding:3 0 3 0; color:white;" >
				<span style="margin-left:10;" class="foldable fold-open" id="expt_fold_<bean:write name="experiment" property="id"/>" >
				&nbsp;&nbsp;&nbsp;&nbsp;</span>
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
			
			<div id="expt_fold_<bean:write name="experiment" property="id"/>_target"> <!-- begin collapsible div -->
			
			<!-- SEARCHES FOR THE EXPERIMENT -->
			<logic:notEmpty name="experiment" property="searches">
				<logic:iterate name="experiment" property="searches" id="search">
					<div style="background-color: #FFFFE0; margin:5 5 5 5; padding:5; border: 1px dashed gray;" >
					<table width="90%">
						<tr>
							<td width="33%"><b>Program: </b>&nbsp;
							<b><bean:write name="search" property="searchProgram"/>
							&nbsp;
							<bean:write name="search" property="searchProgramVersion"/></b></td>
							<td width="33%">
							<b>
								<html:link action="viewSequestResults.do" 
											paramId="ID" 
											paramName="search" paramProperty="id">[View Results]</html:link>
							</b>
							</td>
							<td width="33%"><b>Search Date: </b>&nbsp;
							<bean:write name="search" property="searchDate"/></td>
							
						</tr>
						<tr>
							<td><b>Search Database: </b></td>
							<td><bean:write name="search" property="searchDatabase"/></td>
							
						</tr>
						<tr>
							<td width="33%"><b>Enzyme: </b>&nbsp;
							<bean:write name="search" property="enzymes"/></td>
							<td width="33%"><b>Static Modifications: </b>
							<bean:write name="search" property="staticModifications"/></td>
							<td width="33%"><b>Dynamic Modifications: </b>
							<bean:write name="search" property="dynamicModifications"/></td>
						</tr>
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
						<td width="33%"><b>Program: </b>&nbsp;
						<b><bean:write name="analysis" property="analysisProgram"/>
						&nbsp;
						<bean:write name="analysis" property="analysisProgramVersion"/></b></td>
						<td width="33%">
							<b><html:link action="viewPercolatorResults.do" paramId="ID" paramName="analysis" paramProperty="id">[View Results]</html:link></b>
						</td>
						<td width="33%">
							<b><a href="<yrcwww:link path='newPercolatorProteinInference.do?'/>searchAnalysisId=<bean:write name='analysis' property='id' />&projectId=<bean:write name='project' property='ID'/>"> 
							[Infer Proteins]</a></b>
						</td>
					</tr>
					</table>
				</div>
				</logic:iterate>
			</logic:notEmpty>
			
			<!-- PROTEIN INFERENCE RESULTS FOR THE EXPERIMENT -->
			<logic:equal name="experiment" property="hasProtInferResults" value="true" >
			<logic:present name="experiment" property="dtaSelect">
				<div style="background-color: #FFFFF0; margin:5 5 5 5; padding:5; border: 1px dashed gray;" > 
				
					<table width="90%">
					<tr>
						<td width="33%"><b>Program: </b>&nbsp;
						<b>DTASelect</b>
						&nbsp;
						<td width="33%">
							<b><html:link action="viewYatesRun.do" paramId="id" paramName="experiment" paramProperty="dtaSelect.id">[View Results]</html:link></b>
						</td>
						<td width="33%">&nbsp;
						</td>
					</tr>
					</table>
				</div>
			</logic:present>
			<logic:notEmpty name="experiment" property="protInferRuns">
				<div style="background-color: #F0F8FF; margin:5 5 5 5; padding:5; border: 1px dashed gray;" >
					<div><b>Protein Inference Results</b></div> 
					<table width="90%">
					<thead>
					<tr align="left"><th>ID</th><th>Date</th><th>Submitted By</th><th>Comments</th><th>Status</th></tr>
					</thead>
					<tbody>
					<logic:iterate name="experiment" property="protInferRuns" id="piJob" type="org.yeastrc.www.proteinfer.ProteinferJob">
						<tr>
						<td><b><bean:write name="piJob" property="pinferId"/></b></td>
						<td><bean:write name="piJob" property="submitDate"/></td>
						<td><bean:write name="piJob" property="researcher.lastName"/></td>
						<td><bean:write name="piJob" property="comments"/></td>
						
						<td>
						
						<!-- Job COMPLETE -->
						<logic:equal name="piJob" property="complete" value="true">
							<a href="<yrcwww:link path='viewProteinInferenceResult.do?'/>pinferId=<bean:write name='piJob' property='pinferId'/>">
							<b><font color="green"><bean:write name="piJob" property="statusDescription"/></font></b></a>
							&nbsp;
							<a href="<yrcwww:link path='newProteinSetComparison.do?'/>piRunId=<bean:write name='piJob' property='pinferId'/>">Compare</a>
						</logic:equal>
						<!-- Job FAILED -->
						<logic:equal name="piJob" property="failed" value="true">
							<a href="<yrcwww:link path='viewProteinInferenceJob.do?'/>pinferId=<bean:write name='piJob' property='pinferId'/>&projectId=<bean:write name='project' property='ID'/>">
							<b><font color="red"><bean:write name="piJob" property="statusDescription"/></font></b>
							</a>
						</logic:equal>
						<!-- Job RUNNING -->
						<logic:equal name="piJob" property="running" value="true">
							<a href="<yrcwww:link path='viewProteinInferenceJob.do?'/>pinferId=<bean:write name='piJob' property='pinferId'/>&projectId=<bean:write name='project' property='ID'/>">
							<b><font color="#000000"><bean:write name="piJob" property="statusDescription"/></font></b>
							</a>
						</logic:equal>
						
	   		 			</td>
						</tr>
					</logic:iterate>
					</tbody>
					</table>
				</div>
			</logic:notEmpty>
			</logic:equal>
			
			
			<!-- FILES FOR THE EXPERIMENT (Placeholder)-->
			<div align="center"
				id="listfileslink_<bean:write name='experiment' property='id'/>"  
				class="clickable" style="font-weight:bold; color:#D74D2D;" 
				onclick="javascript:toggleFilesForExperiment(<bean:write name='experiment' property='id'/>);">[List Files]</div>
			<div style="background-color: #FFFFFF; margin:5 5 5 5; padding:0;" id="listfileslink_<bean:write name='experiment' property='id'/>_target"></div>
			
			</div> 
			
		</div> <!-- end of collapsible div -->
		</div> <!-- End of one experiment -->
		<br>
		</logic:iterate>
	</logic:notEmpty>

</yrcwww:contentbox>