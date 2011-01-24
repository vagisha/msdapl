<%@page import="org.yeastrc.ms.domain.search.Program"%>
<%@page import="org.yeastrc.ms.domain.general.MsInstrument"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<bean:define name="instrumentList" id="instrumentList" type="java.util.List"></bean:define>
<script>

// ---------------------------------------------------------------------------------------
// SAVE COMMENTS FOR AN EXPERIMENT / PROTEIN INFERENCE RUN
// --------------------------------------------------------------------------------------- 
$(document).ready(function() {
  	makeEditable();
	$("a.thumb").each(function() {
		makeZoomableThumbnail(this);
	});
});

function makeZoomableThumbnail(element) {
	var title=$(element).attr('title');
	$(element).colorbox({transition:"none", photo: true, title:title});
}

function makeEditable() {
	makeCommentEditable();
	makeInstrumentEditable();
}

function makeCommentEditable() {
	$(".editableComment").each(function() {
		setupEditableComment(this);
	});
	$(".saveExptComments").each(function() {
		setupSaveExptComments(this);
	});
	$(".cancelExptComments").each(function() {
		setupCancelExptComments(this);
	});
	$(".savePiRunComments").each(function() {
		setupSavePiRunComments(this);
	});
	$(".cancelPiRunComments").each(function() {
		setupCancelPiRunComments(this);
	});
	$(".saveAnalysisComments").each(function() {
		setupSaveAnalysisComments(this);
	});
	$(".cancelAnalysisComments").each(function() {
		setupCancelAnalysisComments(this);
	});
}
function setupEditableComment(editable) {
	// alert("making comment editable "+editable);
	$(editable).click(function() {
		var id = $(this).attr('id');
		var currentComments = $.trim($("#"+id+"_text").text());
		$("#"+id+"_text").hide();
		$("#"+id+"_edit .edit_text").val(currentComments);
		$("#"+id+"_edit").show();
	});
}
function setupSaveExptComments(editable) {
	
	$(editable).click(function() {
		var id = $(this).attr('id');
		saveExptComments(id);
	});
}
function setupCancelExptComments(editable) {
	
	$(editable).click(function() {
		var id = $(this).attr('id');
		$("#experiment_"+id+"_text").show();
		$("#experiment_"+id+"_edit .edit_text").text("");
		$("#experiment_"+id+"_edit").hide();
	});
}
function setupSavePiRunComments(editable) {
	
	$(editable).click(function() {
		var id = $(this).attr('id');
		savePiRunComments(id);
	});
}
function setupCancelPiRunComments(editable) {
	$(editable).click(function() {
		var id = $(this).attr('id');
		$("#piRun_"+id+"_text").show();
		$("#piRun_"+id+"_edit .edit_text").text("");
		$("#piRun_"+id+"_edit").hide();
	});
}

function setupSaveAnalysisComments(editable) {
	
	$(editable).click(function() {
		var id = $(this).attr('id');
		saveAnalysisComments(id);
	});
}
function setupCancelAnalysisComments(editable) {
	$(editable).click(function() {
		var id = $(this).attr('id');
		$("#analysis_"+id+"_text").show();
		$("#analysis_"+id+"_edit .edit_text").text("");
		$("#analysis_"+id+"_edit").hide();
	});
}

function saveExptComments(exptId) {
	saveComments("<yrcwww:link path='saveExperimentComments.do'/>", 'experiment', exptId);
}

function savePiRunComments(piRunId) {
	saveComments("<yrcwww:link path='saveProtInferComments.do'/>", 'piRun', piRunId);
}

function saveAnalysisComments(analysisId) {
	saveComments("<yrcwww:link path='saveAnalysisComments.do'/>", 'analysis', analysisId);
	$("#analysis_"+analysisId).text("[Edit]");
}

function saveComments(url, idName, id) {
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

function makeInstrumentEditable() {
	$(".editableInstrument").each(function() {
		setupEditableInstrument(this);
	});
}
function setupEditableInstrument(editable) {

	$(editable).click(function() {
		var id = $(this).attr('id');
		
		var instrumentId = $(this).attr('title').split("_")[0];
		var experimentId = $(this).attr('title').split("_")[1];

		var target = ("#"+id+"_select"); // target element after which we will add the list box and 'Save' , 'Cancel' links
		
		var editInstrumentElementId = "\'"+id+"_edit\'";
		
		var onclickSaveStr = "onClick=\""+"saveSelectedInstrument(\'"+id+"_select\', "+experimentId+", "+editInstrumentElementId+");\"";
		var onclickCancelStr = "onClick=\""+"cancelEditSelectedInstrument(\'"+id+"_select\', "+editInstrumentElementId+");\"";
		
		var toAppend = '<span id='+editInstrumentElementId+'>';
		
		toAppend += '<select id="instrumentSelect"> ';
		toAppend += '<option value="0">-Select-</option>';
		<% for (Object instrument: instrumentList) {%>
			toAppend += '<option value=\"'+<%= ((MsInstrument)instrument).getId()%>;
			toAppend += '\">'+"<%= ((MsInstrument)instrument).getName()%>"+'</option>';
		<%}%>
		toAppend += '</select> ';
		toAppend += '&nbsp;&nbsp;<span class="clickable underline" '+onclickSaveStr+'><font color="red"><b>Save</b></font></span>';
		toAppend += '&nbsp;&nbsp;<span class="clickable underline" '+onclickCancelStr+'><font color="red"><b>Cancel</b></font></span>';
		toAppend +='</span>';
		$(target).after(toAppend);
		$(target).hide();
	});
}

function saveSelectedInstrument(elementId, experimentId, editInstrumentElementId) {
	var selectedInstrumentId = $("#instrumentSelect option[selected]").val();
	
	if(selectedInstrumentId == 0) {
		alert("Please select a instrument"); return;
	}
	
	var selectedInstrumentName = $("#instrumentSelect option[selected]").text();
	
	var oldInstrumentName = $("#"+elementId).text();
	
	$.ajax({
		url:      "<yrcwww:link path='saveExperimentInstrument.do'/>",
		dataType: "text",
		data:     {'id': 			experimentId, 
		           'instrumentId':  selectedInstrumentId},
		beforeSend: function(xhr) {
						$("#"+elementId).text("Saving....");
						$("#"+elementId).show();
						$("#"+editInstrumentElementId).remove();
					},
		success:  function(data) {
			        if(data == 'OK') {
			        	$("#"+elementId).text(selectedInstrumentName);
			        }
			        else {
			        	$(textFieldId).text(oldInstrumentName);
			        	alert("Error saving instrument: "+data);
			        }
			        success = true;
		          },
		complete:  function(xhr, textStatus) {
			if(!success) {
				$(textFieldId).text(oldInstrumentName);
			    alert("Error saving instrument: "+data);
			}
		}
		
	});
}

function cancelEditSelectedInstrument(elementId, editInstrumentElementId) {
	$("#"+elementId).show();
	$("#"+editInstrumentElementId).remove();
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
// RE-RUN PROTEIN INFERENCE
// --------------------------------------------------------------------------------------- 
function rerunProtInferRun(pinferId) {
	if(confirm("Are you sure you want to re-run protein inference ID "+pinferId+"?")) {
          document.location.href="<yrcwww:link path='rerunProteinInferJob.do?pinferId='/>"+pinferId+"&projectId=<bean:write name='project' property='ID'/>";
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
		// var url = "<yrcwww:link path='setComparisonFilters.do?'/>"+"piRunIds="+pinferIds+"&groupProteins="+groupIndistinguishable;
		var url = "<yrcwww:link path='doProteinSetComparison.do?'/>"+"piRunIds="+pinferIds+"&groupProteins="+groupIndistinguishable;
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
	var url = "<yrcwww:link path='selectComparisonDatasetProjects.do?'/>"+
	"piRunIds="+pinferIds+
	"&groupProteins="+groupIndistinguishable+
	'&projectId=<bean:write name='project' property='ID'/>';
	window.location.href = url;
}

// Used for ProteinProphet inferences
// TODO update to use the same method as MSDaPl's protein inferences.
function clearSelectedProtInfer() {
	$("input.compare_cb:checked").each(function() {
		$(this).attr('checked', false);
	});
}

// Used for MSDaPl's protein inferences
function clearSelectedProtInfer(analysisId) {
	$("input.compare_cb#"+analysisId).each(function() {
		$(this).attr('checked', false);
	});
}


function selectAllProtInfer(analysisId) {
	$("input.compare_cb#"+analysisId).each(function() {
		$(this).attr('checked', true);
	});
}

function confirmDeleteExperiment(experimentId) {
    if(confirm("Are you sure you want to delete ExperimentID "+experimentId+"?")) {
          document.location.href="<yrcwww:link path='deleteExperiment.do?experimentId='/>" + experimentId;
          return 1;
    }
 }
 
 
 function goToExperiment(exptId) {
 
	showExperimentDetails(exptId);
	$("#expt_fold_"+exptId).removeClass('fold-close');
	$("#expt_fold_"+exptId).addClass('fold-open');
	$("#expt_fold_"+exptId+"_target").show();
 	window.location.href="#Expt"+exptId;
 }
 
 function showExperimentDetails(exptId) {
 
 	// check if the experiment is already loaded
 	// if not, load the experiment first
 	if($("#expt_fold_"+exptId+"_target").html().length == 0) {
 		//alert("load experiment");
 		// load data in the appropriate div
		$.blockUI(); 
		$("#expt_fold_"+exptId+"_target").load("<yrcwww:link path='getDetailsForExperiment.do' />",  	// url
							                        {'experimentId': exptId, 'projectId': <bean:write name='project' property='ID'/>}, 		// data
							                        function(responseText, status, xhr) {	// callback
								  						$.unblockUI();
								  						// set up editable comments and instrument
								  						var title = "expt_"+exptId;
								  						$(".editableComment[title='"+title+"']").each(function() {
								  							setupEditableComment($(this));
								  						});
								  						$(".savePiRunComments[title='"+title+"']").each(function() {
								  							setupSavePiRunComments($(this));
								  						});
								  						$(".cancelPiRunComments[title='"+title+"']").each(function() {
								  							setupCancelPiRunComments($(this));
								  						});
								  						$(".saveAnalysisComments[title='"+title+"']").each(function() {
								  							setupSaveAnalysisComments($(this));
								  						});
								  						$(".cancelAnalysisComments[title='"+title+"']").each(function() {
								  							setupCancelAnalysisComments($(this));
								  						});
								  						
								  						$("#expt_fold_"+exptId+"_target a.thumb").each(function() {
															makeZoomableThumbnail(this);
														});
														
								  					});
 		
 	}
 	
 }
 
 
 // ---------------------------------------------------------------------------------------
// SAVE / REMOVE BOOKMARK for a PROTEIN INFERENCE RUN
// --------------------------------------------------------------------------------------- 
function editBookmark(imgElement, piRunId) {
	
	var action = $(imgElement).attr('id');
	if($(imgElement).is(".no_bookmark")) {
		saveBookmark(imgElement, piRunId);
	}
	else if($(imgElement).is(".has_bookmark")) {
		removeBookmark(imgElement, piRunId)
	}
}

function saveBookmark(imgElement, piRunId) {

	//alert("Saving bookmark for "+piRunId);
	$.ajax({
		url:      "<yrcwww:link path='bookmarkProteinInfer.do'/>",
		data:     {'pinferId': 			piRunId, 
		           'projectId': 	    <bean:write name='project' property='ID'/>,
		           'save':  piRunId},
		success:  function(data) {
			        if(data == 'OK') {
			        	$(imgElement).attr('src', "<yrcwww:link path='images/bookmark.png'/>");
			        	$(imgElement).removeClass("no_bookmark");
			        	$(imgElement).addClass("has_bookmark");
			        }
			        else {
			        	alert("Error saving bookmark: "+data);
			        }
		          }
	});
}

function removeBookmark(imgElement, piRunId) {

	//alert("Removing bookmark for "+piRunId);
	$.ajax({
		url:      "<yrcwww:link path='bookmarkProteinInfer.do'/>",
		data:     {'pinferId': 			piRunId,
				   'projectId': 	    <bean:write name='project' property='ID'/>},
		success:  function(data) {
			        if(data == 'OK') {
			        	// If this action was triggered from the bookmarked list, remove the bookmark icon 
						// from the protein inference listed with the experiment as well
			        	$("#expt_piRun_"+piRunId).attr('src', "<yrcwww:link path='images/no_bookmark.png'/>")
						$("#expt_piRun_"+piRunId).removeClass("has_bookmark");
			        	$("#expt_piRun_"+piRunId).addClass("no_bookmark");
						
						// If this is currently in the bookmark list, hide it
						$("#bookmark_"+piRunId).hide();
						
			        }
			        else {
			        	alert("Error saving bookmark: "+data);
			        }
		          }
	});
}

</script>


<yrcwww:contentbox title="Experiments" centered="true" width="95" widthRel="true">

	<logic:empty name="experiments">
		<div align="center" style="margin:20">
		There are no experiments for this project. To upload an experiment for this project click <a href="" onClick="javascript:goMSUpload(); return false;">here</a>
		</div>
	</logic:empty>

	<!--  Experiment summaries -->
	<logic:notEmpty name="experiments">
		
		<bean:size name="experiments" id="exptCount"/>
		<logic:greaterThan name="exptCount" value="5">
		
		<div style="border:1px dotted gray;margin:5 5 5 5; padding:0 0 5 0;">
		<div style="background-color:#ED9A2E;width:100%; margin:0; padding:3 0 3 0; color:white;" >
		<span style="margin-left:10;" 
			  class="foldable fold-close" id="exptlist_fold">
				&nbsp;&nbsp;&nbsp;&nbsp;
		</span>
		<span style="padding-left:10;"><b>All Available Experiments</b></span>
		</div>
		<div id="exptlist_fold_target" style="display:none; padding:10 0 5 0;"> 
		<table class="table_basic stripe_table sortable" align="center">
			<thead>
				<tr>
				<th>ID</th>
				<th>Date</th>
				<th>Location</th>
				</tr>
			</thead>
			<tbody>
				<logic:iterate name="experiments" id="summary">
				<tr>
				<td><span class="clickable underline" onclick="goToExperiment(<bean:write name="summary" property="id"/>)">
					<bean:write name="summary" property="id"/>
					</span>
				</td>
				<td><bean:write name="summary" property="uploadDate"/> </td>
				<td><bean:write name="summary" property="serverDirectory"/></td>
				</tr>
				</logic:iterate>
			</tbody>
		</table>
		</div>
		</div>
		</logic:greaterThan>
		<br/>
		
	</logic:notEmpty>
	
	<!-- PROTEIN INFERENCE FAVORITES -->
	<logic:present name="hasBookmarks">
		<div id="pinfer_bookmarks">
		<%@ include file="proteinInferBookmarks.jsp" %>
		</div>
	</logic:present>
	<logic:notPresent name="hasBookmarks">
		<div id="pinfer_bookmarks" style="display:none;">
		<%@ include file="proteinInferBookmarks.jsp" %>
		</div>
	</logic:notPresent>
	
	
	
	<!-- EXPERIMENT DETAILS -->
	<logic:notEmpty name="experiments">
		<logic:iterate name="experiments" id="experiment" scope="request">
			<%@ include file="experimentDetails.jsp" %>
		<br>
		</logic:iterate>
	</logic:notEmpty>

</yrcwww:contentbox>