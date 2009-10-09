
<%@page import="org.yeastrc.ms.domain.search.Program"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<script src="<yrcwww:link path='/js/jquery.blockUI.js'/>"></script>
<script>

// ---------------------------------------------------------------------------------------
// SAVE COMMENTS FOR AN EXPERIMENT / PROTEIN INFERENCE RUN
// --------------------------------------------------------------------------------------- 
$(document).ready(function() {
  	makeEditable();
});

function makeEditable() {
	$(".editableComment").click(function() {
		var id = $(this).attr('id');
		var currentComments = $.trim($("#"+id+"_text").text());
		$("#"+id+"_text").hide();
		$("#"+id+"_edit .edit_text").val(currentComments);
		$("#"+id+"_edit").show();
	});
	
	$(".saveExptComments").click(function() {
		var id = $(this).attr('id');
		// can also use $(my_element).text().replace(/(^\s*)|(\s*$)/g, '');
		var comments = $.trim($("#experiment_"+id+"_edit .edit_text").val());
		saveExptComments(id, comments);
	});
	
	$(".cancelExptComments").click(function() {
		var id = $(this).attr('id');
		$("#experiment_"+id+"_text").show();
		$("#experiment_"+id+"_edit .edit_text").text("");
		$("#experiment_"+id+"_edit").hide();
	});
	
	$(".savePiRunComments").click(function() {
		var id = $(this).attr('id');
		var comments = $.trim($("#experiment_"+id+"_edit .edit_text").val());
		savePiRunComments(id, comments);
	});
	
	$(".cancelPiRunComments").click(function() {
		var id = $(this).attr('id');
		$("#piRun_"+id+"_text").show();
		$("#piRun_"+id+"_edit .edit_text").text("");
		$("#piRun_"+id+"_edit").hide();
	});
}

function saveExptComments(exptId, comments) {
	saveComments("<yrcwww:link path='saveExperimentComments.do'/>", 'experiment', exptId, comments);
}

function savePiRunComments(piRunId, comments) {
	saveComments("<yrcwww:link path='saveProtInferComments.do'/>", 'piRun', piRunId, comments);
}

function saveComments(url, idName, id, comments) {
	var oldComments = $.trim($("#"+idName+"_"+id+"_text").text());
	var newComments = $.trim($("#"+idName+"_"+id+"_edit .edit_text").val());
	
	var textFieldId = "#"+idName+"_"+id+"_text";
	var textBoxId   = "#"+idName+"_"+id+"_edit";
	var success = false;
	
	$.ajax({
		url:      url,
		dataType: "text",
		data:     {'id': 			id, 
		           'comments': 		newComments},
		beforeSend: function(xhr) {
						$(textFieldId).text("Saving....");
						$(textFieldId).show();
						$(textBoxId).hide();
					},
		success:  function(data) {
			        if(data == 'OK') {
			        	$(textFieldId).text(newComments);
			        }
			        else {
			        	$(textFieldId).text(oldComments);
			        	alert("Error saving comments: "+data);
			        }
			        success = true;
		          },
		complete:  function(xhr, textStatus) {
			if(!success) {
				$(textFieldId).text(oldComments);
				alert("Error saving comments");
			}
		}
		
	});
}

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
														makeSortableTable($table);
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

// ---------------------------------------------------------------------------------------
// DELETE PROTEIN INFERENCE RUN
// --------------------------------------------------------------------------------------- 
function deleteProtInferRun(pinferId) {
	if(confirm("Are you sure you want to delete protein inference ID "+pinferId+"?")) {
          document.location.href="<yrcwww:link path='deleteProteinInferJob.do?pinferId='/>"+pinferId+"&projectId=<bean:write name='project' property='ID'/>";
        return 1;
    }
}

// ---------------------------------------------------------------------------------------
// COMPARE SELECTED PROTEIN INFERENCE RUNS
// --------------------------------------------------------------------------------------- 
function compareSelectedProtInfer() {
	var pinferIds = "";
	var forDisplay = "\n";
	var i = 0;
	$("input.compare_cb:checked").each(function() {
		if(i > 0) {
			pinferIds += ",";
		}
		pinferIds += $(this).val();
		forDisplay += $(this).val()+"\n";
		i++;
	});
	if(i < 2) {
		alert("Please select at least two protein inference results to compare");
		return false;
	}
	var groupIndistinguishable = $("input#grpProts:checked").val() != null;
	forDisplay +="Group Indistinguishable Proteins: "+groupIndistinguishable;
	
	// var doCompare = confirm("Compare protein inference results: "+forDisplay);
	// if(doCompare) {
		var url = "<yrcwww:link path='setComparisonFilters.do?'/>"+"piRunIds="+pinferIds+"&groupProteins="+groupIndistinguishable;
		window.location.href = url;
	// }
}

function compareSelectedProtInferAndMore() {
	var pinferIds = "";
	var i = 0;
	$("input.compare_cb:checked").each(function() {
		if(i > 0) {
			pinferIds += ",";
		}
		pinferIds += $(this).val();
		i++;
	});
	
	var groupIndistinguishable = $("input#grpProts:checked").val() != null;
	var url = "<yrcwww:link path='selectComparisonDatasets.do?'/>"+"piRunIds="+pinferIds+"&groupProteins="+groupIndistinguishable;
	window.location.href = url;
}

function clearSelectedProtInfer() {
	$("input.compare_cb:checked").each(function() {
		$(this).attr('checked', false);
	});
}
</script>


<yrcwww:contentbox title="Experiments" centered="true" width="850">

	<logic:empty name="experiments">
		<div align="center" style="margin:20">
		There are no experiments for this project. To upload an experiment for this project click <a href="" onClick="javascript:goMSUpload(); return false;">here</a>
		</div>
	</logic:empty>

	<logic:notEmpty name="experiments">
		
		
		<logic:iterate name="experiments" id="experiment" scope="request">
		
			<A HREF='#Expt<bean:write name="experiment" property="id"/>'></A> 
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
					<td valign="top"><b>Comments </b>
						<logic:equal name="writeAccess" value="true">
						<span class="editableComment clickable" id="experiment_<bean:write name='experiment' property='id'/>" style="font-size:8pt; color:red;">[Edit]</span>
						</logic:equal>
						<b>: </b></td>
					<td style="padding-left:10">
						<div id="experiment_<bean:write name='experiment' property='id'/>_text"><bean:write name="experiment" property="comments"/></div>
						<div id="experiment_<bean:write name='experiment' property='id'/>_edit" align="center"
						     style="display:none;">
						     <textarea rows="5" cols="60" class="edit_text"></textarea>
						     <br>
						     <button class="saveExptComments" id="<bean:write name='experiment' property='id'/>">Save</button>
						     <button class="cancelExptComments" id="<bean:write name='experiment' property='id'/>">Cancel</button>
						</div>
					</td>
				</tr>
				<logic:equal name="experiment" property="uploadSuccess" value="false">
					<tr>
						<td style="color:red; font-weight:bold;">Upload Failed</td>
						<td><html:link action="viewUploadJob.do" 
									   paramId="id" 
									   paramName="experiment" paramProperty="uploadJobId">View Log</html:link></td>
					</tr>
				</logic:equal>
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
							
							
							<!-- !!!!!! SEQUEST !!!!!! -->
							<logic:equal name="search" property="searchProgram" value="<%=Program.SEQUEST.toString() %>">
							<td width="33%">
								<b>
								<html:link action="viewSequestResults.do" 
											paramId="ID" 
											paramName="search" paramProperty="id">[View Results]</html:link>
								<!-- <html:link action="percolatorPepXmlDownloadForm.do" 
											paramId="ID" 
											paramName="search" paramProperty="id">[PepXML]</html:link> -->
								</b>
							</td>
							</logic:equal>
							
							<!-- !!!!!! MASCOT !!!!!! -->
							<logic:equal name="search" property="searchProgram" value="<%=Program.MASCOT.toString() %>">
							<td width="33%">
							<b>
								<html:link action="viewMascotResults.do" 
											paramId="ID" 
											paramName="search" paramProperty="id">[View Results]</html:link>
							</b>
							</td>
							</logic:equal>
							
							
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
						
						<!-- !!!!!! PERCOLATOR !!!!!! -->
						<logic:equal name="analysis" property="analysisProgram" value="<%=Program.PERCOLATOR.toString() %>">
						<td width="33%">
							<b>
								<html:link action="viewPercolatorResults.do" paramId="ID" paramName="analysis" paramProperty="id">[View Results]</html:link>
								<!-- <html:link action="percolatorPepXmlDownloadForm.do" 
											paramId="ID" 
											paramName="search" paramProperty="id">[PepXML]</html:link> -->
							</b>
						</td>
						<td width="33%">
							<b><a href="<yrcwww:link path='newPercolatorProteinInference.do?'/>searchAnalysisId=<bean:write name='analysis' property='id' />&projectId=<bean:write name='project' property='ID'/>"> 
							[Infer Proteins]</a></b>
						</td>
						</logic:equal>
						
						<!-- !!!!!! PEPTIDE PROPHET !!!!!! -->
						<logic:equal name="analysis" property="analysisProgram" value="<%=Program.PEPTIDE_PROPHET.toString() %>">
						
						<td width="33%">
							<b>
								<html:link action="viewPeptideProphetResults.do" paramId="ID" paramName="analysis" paramProperty="id">[View Results]</html:link>
								<!-- <html:link action="percolatorPepXmlDownloadForm.do" 
											paramId="ID" 
											paramName="search" paramProperty="id">[PepXML]</html:link> -->
							</b>
						</td>
						
						<td width="33%">
							<b><bean:write name="analysis" property="analysisName" /></b>
						</td>
						
						</logic:equal>
						
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
			
			<logic:notEmpty name="experiment" property="proteinProphetRuns">
				<div style="background-color: #FFFFF0; margin:5 5 5 5; padding:5; border: 1px dashed gray;" > 
					<div><b>ProteinProphet Results</b></div> 
					<table width="100%">
					<thead>
					<tr align="left">
						<th valign="top">ID</th>
						<th valign="top">Version</th>
						<th valign="top">File</th>
						<th valign="top" align="center">#Groups<br>(#Proteins)</th>
						<th valign="top" align="center">#Peptides</th>
						<th valign="top">Comments</th>
						<th valign="top"></th>
						<th valign="top">Compare</th></tr>
					</thead>
					<tbody>
					<logic:iterate name="experiment" property="proteinProphetRuns" id="prpRun" type="org.yeastrc.experiment.ExperimentProteinProphetRun">
						<tr>
						<td valign="top"><b><bean:write name="prpRun" property="proteinProphetRun.id"/></b></td>
						<td valign="top"><bean:write name="prpRun" property="proteinProphetRun.programVersion"/></td>
						<td valign="top"><b><NOBR><bean:write name="prpRun" property="proteinProphetRun.filename"/></NOBR></b></td>
						<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><nobr><bean:write name="prpRun" property="numParsimoniousProteinGroups"/>(<bean:write name="prpRun" property="numParsimoniousProteins"/>)</nobr></td>
						<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="prpRun" property="uniqPeptideSequenceCount"/></td>
						<td valign="top">
							<span id="piRun_<bean:write name='prpRun' property='proteinProphetRun.id'/>_text"><bean:write name="prpRun" property="proteinProphetRun.comments"/></span>
							<logic:equal name="writeAccess" value="true">
							<span class="editableComment clickable" id="piRun_<bean:write name='prpRun' property='proteinProphetRun.id'/>" style="font-size:8pt; color:red;">[Edit]</span>
							</logic:equal>
						</td>
						<td valign="top">
						<a href="<yrcwww:link path='viewProteinProphetResult.do?'/>pinferId=<bean:write name='prpRun' property='proteinProphetRun.id'/>">
							<b><font color="green">View</font></b></a>
						</td>
						<td valign="top" align="center" >
	   		 				<input type="checkbox" class="compare_cb" value="<bean:write name='prpRun' property='proteinProphetRun.id'/>"></input>
						</td>
						</tr>
						<tr>
							<td colspan="5" valign="top">
							<div id="piRun_<bean:write name='prpRun' property='proteinProphetRun.id'/>_edit" align="center"
						     style="display:none;">
						     <textarea rows="5" cols="60" class="edit_text"></textarea>
						     <br>
						     <button class="savePiRunComments" id="<bean:write name='prpRun' property='proteinProphetRun.id'/>">Save</button>
						     <button class="cancelPiRunComments" id="<bean:write name='prpRun' property='proteinProphetRun.id'/>">Cancel</button>
							</div>
							</td>
						</tr>
					</logic:iterate>
					
					</tbody>
					</table>
				</div>
			</logic:notEmpty>
			
			<logic:notEmpty name="experiment" property="protInferRuns">
				<div style="background-color: #F0F8FF; margin:5 5 5 5; padding:5; border: 1px dashed gray;" >
					<div><b>Protein Inference Results</b></div> 
					<table width="100%">
					<thead>
					<tr align="left">
						<th valign="top">ID</th>
						<th valign="top">Version</th>
						<th valign="top">Date</th>
						<th valign="top">User</th>
						<th valign="top" align="center">#Groups<br>(#Proteins)</th>
						<th valign="top" align="center">#Peptides</th>
						<th valign="top">Comments</th>
						<th valign="top">&nbsp;</th>
						<th valign="top">Compare</th></tr>
					</thead>
					<tbody>
					<logic:iterate name="experiment" property="protInferRuns" id="piJob" type="org.yeastrc.experiment.ExperimentProteinferRun">
						<tr>
						<td valign="top"><b><bean:write name="piJob" property="job.pinferId"/></b></td>
						<td valign="top" align="center"><b><bean:write name="piJob" property="job.version"/></b></td>
						<td valign="top"><bean:write name="piJob" property="job.submitDate"/></td>
						<td valign="top"><bean:write name="piJob" property="job.researcher.lastName"/></td>
						
						<logic:equal name="piJob" property="job.complete" value="true">
						<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><nobr><bean:write name="piJob" property="numParsimoniousProteinGroups"/>(<bean:write name="piJob" property="numParsimoniousProteins"/>)</nobr></td>
						<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="piJob" property="uniqPeptideSequenceCount"/></td>
						</logic:equal>
						
						<logic:equal name="piJob" property="job.complete" value="false">
						<td valign="top">&nbsp;</td>
						<td valign="top">&nbsp;</td>
						</logic:equal>
						
						
						
						<td valign="top">
							<span id="piRun_<bean:write name='piJob' property='job.pinferId'/>_text"><bean:write name="piJob" property="job.comments"/></span>
							<logic:equal name="writeAccess" value="true">
							<span class="editableComment clickable" id="piRun_<bean:write name='piJob' property='job.pinferId'/>" style="font-size:8pt; color:red;">[Edit]</span>
							</logic:equal>
						</td>
						<td valign="top">
						
						<!-- Job COMPLETE -->
						<logic:equal name="piJob" property="job.complete" value="true">
							<nobr>
							<a href="<yrcwww:link path='viewProteinInferenceResult.do?'/>pinferId=<bean:write name='piJob' property='job.pinferId'/>">
							<b><font color="green">View</font></b></a>
							&nbsp;
							<span class="clickable" style="text-decoration: underline; color:red;" 
							      onclick="javascript:deleteProtInferRun(<bean:write name='piJob' property='job.pinferId'/>);">Delete</span>
							</nobr>
						</logic:equal>
						<!-- Job FAILED -->
						<logic:equal name="piJob" property="job.failed" value="true">
							<a href="<yrcwww:link path='viewProteinInferenceJob.do?'/>pinferId=<bean:write name='piJob' property='job.pinferId'/>&projectId=<bean:write name='project' property='ID'/>">
							<b><font color="red"><bean:write name="piJob" property="job.statusDescription"/></font></b>
							</a>
						</logic:equal>
						<!-- Job RUNNING -->
						<logic:equal name="piJob" property="job.running" value="true">
							<a href="<yrcwww:link path='viewProteinInferenceJob.do?'/>pinferId=<bean:write name='piJob' property='job.pinferId'/>&projectId=<bean:write name='project' property='ID'/>">
							<b><font color="#000000"><bean:write name="piJob" property="job.statusDescription"/></font></b>
							</a>
						</logic:equal>
						
	   		 			</td>
	   		 			
	   		 			<logic:equal name="piJob" property="job.complete" value="true">
	   		 			<td valign="top" align="center" >
	   		 				<input type="checkbox" class="compare_cb" value="<bean:write name='piJob' property='job.pinferId'/>"></input>
	   		 				<!-- 
							<a href="<yrcwww:link path='newProteinSetComparison.do?'/>piRunId=<bean:write name='piJob' property='job.pinferId'/>">Compare</a>
							-->
						</td>
						</logic:equal>
	   		 			
						</tr>
						<tr>
							<td colspan="5" valign="top">
							<div id="piRun_<bean:write name='piJob' property='job.pinferId'/>_edit" align="center"
						     style="display:none;">
						     <textarea rows="5" cols="60" class="edit_text"></textarea>
						     <br>
						     <button class="savePiRunComments" id="<bean:write name='piJob' property='job.pinferId'/>">Save</button>
						     <button class="cancelPiRunComments" id="<bean:write name='piJob' property='job.pinferId'/>">Cancel</button>
							</div>
							</td>
						</tr>
					</logic:iterate>
					
					</tbody>
					</table>
				</div>
			</logic:notEmpty>
			<div style="margin:5 5 5 5; padding:5; border: 1px dashed gray;" >
			<table width="100%">
				<tr>
						<td colspan="9" align="right">
							<input type="checkbox" id="grpProts" value="group" checked="checked" />Group Indistinguishable Proteins
							&nbsp;
							<span class="clickable" style="text-decoration:underline;" onclick="javascript:compareSelectedProtInferAndMore();"><b>[Compare More]</b></span>
							&nbsp;
							<span class="clickable" style="text-decoration:underline;" onclick="javascript:compareSelectedProtInfer();"><b>[Compare]</b></span>
							<br><br>
							<span class="clickable" style="text-decoration:underline;" onclick="javascript:clearSelectedProtInfer();">[Clear Selected]</span>
						</td>
					</tr>
			</table>
			</div>
			</logic:equal>
			
			
			<!-- FILES FOR THE EXPERIMENT (Placeholder)-->
			<div align="center">
			<span
				id="listfileslink_<bean:write name='experiment' property='id'/>"  
				class="clickable" style="font-weight:bold; color:#D74D2D;" 
				onclick="javascript:toggleFilesForExperiment(<bean:write name='experiment' property='id'/>);">[List Files]</span>
			</div>
			<div style="background-color: #FFFFFF; margin:5 5 5 5; padding:0;" id="listfileslink_<bean:write name='experiment' property='id'/>_target"></div>
			
			</div> 
			
		</div> <!-- end of collapsible div -->
		</div> <!-- End of one experiment -->
		<br>
		</logic:iterate>
	</logic:notEmpty>

</yrcwww:contentbox>