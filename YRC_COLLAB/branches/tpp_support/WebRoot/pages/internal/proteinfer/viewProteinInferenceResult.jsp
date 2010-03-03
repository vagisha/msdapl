
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


 
<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>


<script src="<yrcwww:link path='/js/dragtable.js'/>"></script>

<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.core.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.tabs.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.dialog.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.draggable.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.resizable.js'/>"></script>

<script type="text/javascript" src="<yrcwww:link path='/js/jquery.history.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.cookie.js'/>"></script>

<script src="<yrcwww:link path='/js/tooltip.js'/>"></script>

<script src="<yrcwww:link path='/js/jquery.form.js'/>"></script>

<script src="<yrcwww:link path='/js/jquery.blockUI.js'/>"></script>


<link rel="stylesheet" href="<yrcwww:link path='/css/proteinfer.css'/>" type="text/css" >

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="proteinInferFilterForm">
	<logic:forward  name="viewProteinInferenceResult" />
</logic:notPresent>


<%
	int pinferId = (Integer)request.getAttribute("pinferId");
	//int clusterCount = ((List<Integer>)request.getAttribute("clusterIds")).size();
%>

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



// FOR HISTORY
function callback(hash)
{
	var $tabs = $("#results").tabs();
    // do stuff that loads page content based on hash variable
    if(hash) {
    	$("#load").text(hash + ".html");
		var $tabs = $("#results").tabs();
		
		if(hash == 'protlist')
			$tabs.tabs('select', 0);
		else if (hash == 'protdetails')
			$tabs.tabs('select', 1);
		else if (hash == 'protclusters')
			$tabs.tabs('select', 2);
		else if (hash == 'input')
			$tabs.tabs('select', 3);
	} else {
		$tabs.tabs('select', 0);
	}
}
// FOR HISTORY
$(document).ready(function() {
    $.history.init(callback);
    $("a[@rel='history']").click(function(){
    	var hash = this.href;
		hash = hash.replace(/^.*#/, '');
        $.history.load(hash);
        return false;
    });
});

  
// ---------------------------------------------------------------------------------------
// WHAT TO DO WHEN THE DOCUMENT LOADS
// ---------------------------------------------------------------------------------------
$(document).ready(function() {
	
	// reset the form.  When clicking the reload button the form is 
	// not resest, so we reset it manually. 
 	$("#filterForm")[0].reset();
 	
 	
	var selected = 0;
	if(location.hash == "#protdetails")  selected = 1;
	if(location.hash == "#protclusters") selected = 2;
	if(location.hash ==  "#input") 		 selected = 3;
	
	
	// set up the tabs and select the first tab
    $("#results > ul").tabs().tabs('select', selected);
  
 	$(".stripe_table th").addClass("pinfer_A");
 	$(".stripe_table tbody > tr:odd").addClass("pinfer_A");
 	
 	
  	setupProteinListTable();
  	
   	$('table.sortable').each(function() {
    	var $table = $(this);
    	makeSortable($table);
  	});

	// Make the comments for this protein inference run editable
	makeCommentsEditable();
	  	
  	// If the protein details cookie is saved load the protein details
	var cookie = $.cookie("protdetails");
	if(cookie) {
		var cookievals = cookie.split('_');
		var pinferId = cookievals[0];
		var proteinId = cookievals[1];
		// make sure the protein inference ID saved in the cookie is the same as the results we are displaying
		if(pinferId == <%=pinferId%>) {
			var block = selected == 2;
			//alert("protein details "+block);
			showProteinDetails(proteinId, false, block);
		}
	}
	// if a cookie is saved, get the cluster id from the cookie
	var cookie = $.cookie("clusterdetails");
	if(cookie) {
		var cookievals = cookie.split('_');
		var pinferId = cookievals[0];
		var clusterId = cookievals[1];
		// make sure the protein inference ID saved in the cookie is the same as the results we are displaying
		if(pinferId == <%=pinferId%>) {
			var block = selected == 1;
			// alert("cluster details "+block);
			showProteinCluster(clusterId, false, block);
		}
	}	
});
  
// ---------------------------------------------------------------------------------------
// SAVE COMMENTS FOR A PROTEIN INFERENCE RUN
// ---------------------------------------------------------------------------------------
var writeAccess = <bean:write name='writeAccess'/>;
 
function makeCommentsEditable() {

	if(writeAccess == true) {
		$(".editableComment").click(function() {
			var id = $(this).attr('id');
			var currentComments = $.trim($("#"+id+"_text").text());
			$("#"+id+"_text").hide();
			$("#"+id+"_edit .edit_text").val(currentComments);
			$("#"+id+"_edit").show();
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
}

function savePiRunComments(piRunId, comments) {
	saveComments("<yrcwww:link path='saveProtInferComments.do'/>", 'piRun', piRunId, comments);
}

function saveComments(url, idName, id, comments) {
	var oldComments = $.trim($("#"+idName+"_"+id+"_text").text());
	var newComments = $.trim($("#"+idName+"_"+id+"_edit .edit_text").val());
	
	var textFieldId = "#"+idName+"_"+id+"_text";
	var textBoxId   = "#"+idName+"_"+id+"_edit";
	
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
		          },
		complete:  function(xhr, textStatus) {}
		
	});
}

// ---------------------------------------------------------------------------------------
// SUBMIT/SHOW/HIDE PHILIUS RESULTS
// --------------------------------------------------------------------------------------- 
// Submit Philius job OR show / hide results
function philiusAnnotations(pinferProteinId, nrseqProteinId) {

	var button = $("#philiusbutton_"+pinferProteinId);
	
	if(button.text() == "[Get Annotations]") {
		// submit a Philius job, get a job token and display a status message.
		//alert("Submitting Philius job...");
		button.text("[Processing...]");
		
		
		var token = 0;
		//$.post("<yrcwww:link path='submitPhiliusJob.do'/>",
    	//				{'nrseqId': nrseqProteinId},
    	//				function(data) {
    	//					token = data;
    	//		});
		
		alert("token is: "+token);
		
		// show the status text with a link for fetching the results with the returned token.
		var statusText = "Your request has been submitted to the Philius Server.  ";
		statusText += "Processing typically takes about <b>10 minutes</b>.  ";
		statusText += "To get your results click on <b>REFRESH</b>."
		$("#philius_status_"+pinferProteinId).text(statusText);
		$("#philius_status_"+pinferProteinId).show();
		
	}
	else if (button.text() == "[Processing...]") {
		// hide the philius status text
		$("#philius_status_"+pinferProteinId).hide();
		// request the results 
		// if results are still not available
		// show philius status again
		
		// otherwise show the Philius annotations and hide the protein sequence.
		$("#protsequence_"+pinferProteinId).hide();
		$("#philiusannot_"+pinferProteinId).text("Placeholder for Philius results");
		$("#philiusannot_"+pinferProteinId).show();
	}
	else if (button.text() == "[Hide Annotations]") {
		$("#philiusannot_"+pinferProteinId).hide();
		$("#protsequence_"+pinferProteinId).show();
		button.text("[Show Sequence]");
	}
	else if (button.text() == "[Show Annotations}") {
		$("#philiusannot_"+pinferProteinId).show();
		$("#protsequence_"+pinferProteinId).hide();
		button.text("[Hide Annotations]");
	}
}

// ---------------------------------------------------------------------------------------
// SHOW/HIDE HITS FOR AN ION
// --------------------------------------------------------------------------------------- 
// View all the hits for an ion
function toggleHitsForIon (pinferIonId) {

	// alert("ion id: "+pinferIonId);
	var button = $("#showhitsforion_"+pinferIonId);
	
	if(button.text() == "[Show]") {
		//alert("View");
		if($("#hitsforion_"+pinferIonId).html().length == 0) {
			//alert("Getting...");
			// load data in the appropriate div
			$.blockUI(); 
			$("#hitsforion_"+pinferIonId).load("<yrcwww:link path='psmListForIon.do'/>",   					// url
							                        {'pinferId': <%=pinferId%>, 'pinferIonId': pinferIonId}, 		// data
							                        function(responseText, status, xhr) {	// callback
								  						$.unblockUI();
								  						// make table sortable
														var table = $("#allpsms_"+pinferIonId);
														makeSortable(table);
								  					});
		}
		button.text("[Hide]");
		$("#hitsforion_"+pinferIonId).show();
	}
	else {
		button.text("[Show]");
		$("#hitsforion_"+pinferIonId).hide();
	}
}

// ---------------------------------------------------------------------------------------
// SHOW SPECTRUM
// ---------------------------------------------------------------------------------------  
function viewSpectrum (scanId, hitId) {
	//alert("View spectrum for "+scanId+"; hit: "+hitId);
	var winHeight = 500
	var winWidth = 970;
	var doc = "<yrcwww:link path='/viewSpectrum.do'/>?scanID="+scanId+"&runSearchResultID="+hitId;
	//alert(doc);
	window.open(doc, "SPECTRUM_WINDOW", "width=" + winWidth + ",height=" + winHeight + ",status=no,resizable=yes,scrollbars=yes");
}
  
// ---------------------------------------------------------------------------------------
// SHOW PROTEIN DETAILS
// --------------------------------------------------------------------------------------- 
function showProteinDetails(proteinId, display, block) {
	
	if(display == undefined) display = true;
	if(block == undefined)   block = true;
	var showDiv = location.hash != "#protdetails" && display;

	
	// load content in the appropriate div
	if(block)	$.blockUI();
	$("#protein_div").load("<yrcwww:link path='proteinDetails.do'/>",   			    // url
								  {'pinferId': <%=pinferId%>, 'pinferProtId': proteinId}, 	// data
								  function(responseText, status, xhr) {						// callback
								  		
								  		if(block)	$.unblockUI();
								  		
								  		// stripe the table
										$("#protdetailstbl_"+proteinId+" th.main").addClass("pinfer_A");
										$("#protdetailstbl_"+proteinId+" tbody tr.main").addClass("pinfer_A");
										$(this).show();
										// save a cookie
										saveProtDetailCookie(<%=pinferId%>, proteinId);
										if(showDiv) {
											$("#protdetailslink").click(); // so that history works
											//var $tabs = $("#results").tabs();
											//$tabs.tabs('select', 1);
										}
								  });
}

function saveProtDetailCookie(pinferId, proteinId) {
	var COOKIE_NAME = 'protdetails';
	var date = new Date();
    date.setTime(date.getTime() + (2 * 60 * 60 * 1000)); // expire in two hours
    $.cookie(COOKIE_NAME, pinferId+"_"+proteinId, { path: '/', expires: date });
}
// ---------------------------------------------------------------------------------------
// SHOW PROTEIN CLUSTER
// --------------------------------------------------------------------------------------- 
function showProteinCluster(proteinClusterIdx, display, block) {

	if(display == undefined) display = true;
	var showDiv = location.hash != "#protclusters" && display;
	
	//$("#clusterlist")[0].selectedIndex = proteinClusterIdx - 1;
	selectProteinCluster(proteinClusterIdx, block, showDiv);
	
	return false;
}
  
function selectProteinCluster(clusterId, block, showDiv) {

	//var clusterId = $("#clusterlist")[0].selectedIndex + 1;
	
	// get data from the server and put it in the appropriate div
	if(block == undefined)   block = true;
	if(block)	$.blockUI();
	$("#protcluster_div").load("<yrcwww:link path='proteinCluster.do'/>",   								// url
								  	  {'pinferId': <%=pinferId%>, 'clusterId': clusterId}, 	// data
								      function(responseText, status, request) {				// callback
 								  		
 								  		if(block) $.unblockUI();
 								  		
 								  		if($("#assoctable_"+clusterId).length) { // make sure the DOM element exists
 								  			dragtable.makeDraggable($("#assoctable_"+clusterId).get(0));
 								  		}
 								  		
 								  		$("#assoctable_"+clusterId).addClass("table_pinfer_small");
 								  		$("#assoctable_"+clusterId).css('border', '1px dashed gray').css('border-spacing', '2px');
 										$("#assoctable_"+clusterId+"  td").css('border', '1px #CCCCCC dashed').css('padding', '4px');
								  		$("#assoctable_"+clusterId+"  th").css('border', '1px #CCCCCC dashed').css('padding', '4px');
								  	
								  		$("#prot_grp_table_"+clusterId).addClass("table_pinfer_small");
								  		$("#prot_grp_table_"+clusterId).css('border', '1px dashed gray').css('border-spacing', '2px');
								  		$("#prot_grp_table_"+clusterId+"  td").css('border', '1px #CCCCCC dashed').css('padding', '4px');
								  		$("#prot_grp_table_"+clusterId+"  th").css('border', '1px #CCCCCC dashed').css('padding', '4px');
								  	
								  		$("#pept_grp_table_"+clusterId).addClass("table_pinfer_small");
								  		$("#pept_grp_table_"+clusterId).css('border', '1px dashed gray').css('border-spacing', '2px');
								  		$("#pept_grp_table_"+clusterId+"  td").css('border', '1px #CCCCCC dashed').css('padding', '4px');
								  		$("#pept_grp_table_"+clusterId+"  th").css('border', '1px #CCCCCC dashed').css('padding', '4px');
 										
 										$(".protgrplist").click(function(){
 											if($("#prot_grp_table_"+clusterId).is(':visible'))
 												$("#prot_grp_table_"+clusterId).hide();
 											else
 												$("#prot_grp_table_"+clusterId).show();
 										});
 										
 										$(".peptgrplist").click(function(){
 											if($("#pept_grp_table_"+clusterId).is(':visible'))
 												$("#pept_grp_table_"+clusterId).hide();
 											else
 												$("#pept_grp_table_"+clusterId).show();
 										});
 										
 										$(this).show();
 										// save a cookie
										saveClusterDetailCookie(<%=pinferId%>, clusterId);
										
										if(showDiv) {
											$("#protclusterslink").click();
											//var $tabs = $("#results").tabs();
											//$tabs.tabs('select', 2);
										}
								  });	
}

function saveClusterDetailCookie(pinferId, clusterId) {
	var COOKIE_NAME = 'clusterdetails';
    var date = new Date();
    date.setTime(date.getTime() + (2 * 60 * 60 * 1000)); // expire in two hours
    $.cookie(COOKIE_NAME, pinferId+"_"+clusterId, { path: '/', expires: date });
}
  
// ---------------------------------------------------------------------------------------
// METHODS FOR USER INTERACTION ON THE PROTEIN CLUSTER TAB
// ---------------------------------------------------------------------------------------      
  var lastSelectedProteinGroupId = -1;
  var lastSelectedPeptGrpIds = new Array(0);
  
function highlightProteinAndPeptides() {
	var proteinGroupId = arguments[0];
	var peptGrpIds = arguments[1].split(",");
	var uniqPeptGrpIds = arguments[2].split(",");
	//alert(proteinGroupId+" AND "+peptGrpIds+" AND "+uniqPeptGrpIds);
	
	if(proteinGroupId == lastSelectedProteinGroupId) {
		removeProteinAndPeptideHighlights();
	}
	else {
		
 	// deselect any last selected cell
 	removeProteinAndPeptideHighlights();
 	
 	// select the PROTEIN group cells the user wants to select
 	// $("#protGrp_"+proteinGroupId).css("background-color","#EEE8AA");
 	$("#protGrp_"+proteinGroupId).css("background-color","#E0E0E0");
 	
 	
 	// now select the PEPTIDE group cells we want AND the PROTEIN-PEPTIDE association cells
		lastSelectedPeptGrpIds = [];
		var j = 0;
		// peptide groups NOT unique to protein
		for(var i = 0; i < peptGrpIds.length; i++) {
			$(".peptGrp_"+peptGrpIds[i]).each(function() {$(this).css("background-color","#E0E0E0");});
			$("#peptEvFor_"+proteinGroupId+"_"+peptGrpIds[i]).css("background-color","#E0E0E0");
			lastSelectedPeptGrpIds[j] = peptGrpIds[i];
			j++;
		}
		// peptide groups UNIQUE to protein
		for(var i = 0; i < uniqPeptGrpIds.length; i++) {
			$(".peptGrp_"+uniqPeptGrpIds[i]).each(function() {$(this).css("background-color","#ADD8E6");});
			$("#peptEvFor_"+proteinGroupId+"_"+uniqPeptGrpIds[i]).css("background-color","#ADD8E6");
			lastSelectedPeptGrpIds[j] = uniqPeptGrpIds[i];
			j++;
		}
		lastSelectedProteinGroupId = proteinGroupId;
	}
}
  	
function removeProteinAndPeptideHighlights() {
	
	if(lastSelectedPeptGrpIds != -1) {
		// deselect any last selected protein group cells.
		$("#protGrp_"+lastSelectedProteinGroupId).css("background-color","");
		
		// deselect any last selected peptide group cells AND protein-peptide association cells
		if(lastSelectedPeptGrpIds.length > 0) {
	 		for(var i = 0; i < lastSelectedPeptGrpIds.length; i++) {
	 			$(".peptGrp_"+lastSelectedPeptGrpIds[i]).each(function() {
	 				$(this).css("background-color","");
	 				});
	 			$("#peptEvFor_"+lastSelectedProteinGroupId+"_"+lastSelectedPeptGrpIds[i]).css("background-color","");
	 		}	
 		}
 		lastSelectedProteinGroupId = -1;
 		lastSelectedPeptGrpIds = [];
	}
}


// ---------------------------------------------------------------------------------------
// MAKE PROTEIN LIST TABLE SORTABLE
// ---------------------------------------------------------------------------------------
function setupProteinListTable() {
  
  	// stripe table rows
  	$("#protlisttable tbody tr.protgrp_row").addClass("pinfer_A");
  	
  	setupShowPeptidesLinks();
  	
  	makeProteinListSortable();
  	
    setupAnnotationsLinks();
}

// ---------------------------------------------------------------------------------------
// SETUP SHOW/HIDE PEPTIDES LINK
// ---------------------------------------------------------------------------------------
function setupShowPeptidesLinks() {

// this function will be called when clicking on the "Show Peptides" link and proteins in a group are linked.
  	$(".showpeptForProtGrp").click(function() {
  		
  		var id = this.id;
  		
  		if($(this).text() == "Show Peptides") {
  			$(this).text("Hide Peptides");
  			if($("#peptforprot_"+id).html().length == 0) {
  				//alert("Sending request for proteinGroup: "+id);
  				$.blockUI();
  				$("#peptforprot_"+id).load("<yrcwww:link path='getProteinPeptides.do'/>", 	//url
  									{'pinferId': <%=pinferId%>, 		// data
  									 'proteinGroupId': id
  									 },
  									 function(responseText, status, xhr) {						// callback
  									 	$.unblockUI();
  										$(this).show();
  										makeSortable($("#peptforprottbl_"+id));
  								   });
  			}
  			else {
  				$("#peptforprot_"+id).show();
  			}
  		}
  		else {
  			$(this).text("Show Peptides");
  			$("#peptforprot_"+id).hide();
  		}
  	});
  	
  	
  	// this function will be called when clicking on the "Show Peptides" link and proteins in a group are NOT linked.
  	$(".showpeptForProt").click(function() {
  	
  		var grpId =  this.title;
  		var protId = this.id;
  		if($(this).text() == "Show Peptides") {
  			$(this).text("Hide Peptides");
  			if($("#peptforprot_"+protId+"_"+grpId).html().length == 0) {
  				//alert("Sending request");
  				$.blockUI();
  				$("#peptforprot_"+protId+"_"+grpId).load("<yrcwww:link path='getProteinPeptides.do'/>", 	//url
  									{'pinferId': <%=pinferId%>, 					// data
  									 'proteinGroupId': grpId,
  									 'proteinId': protId
  									 },
  									 function(responseText, status, xhr) {			// callback
  									 	$.unblockUI();
  										$(this).show();
  										makeSortable($("#peptforprottbl_"+protId+"_"+grpId));
  								   });
  			}
  			else {
  				$("#peptforprot_"+protId+"_"+grpId).show();
  			}
  		}
  		else {
  			$(this).text("Show Peptides");
  			$("#peptforprot_"+protId+"_"+grpId).hide();
  		}
  	});
}

// ---------------------------------------------------------------------------------------
// SETUP ANNOTATIONS
// ---------------------------------------------------------------------------------------
function setupAnnotationsLinks() {

	if(writeAccess) {
	$("#prot_annot_dialog").dialog({
    	autoOpen: false,
    	modal: true,
    	width: 400,
    	height: 200,
    	overlay: { 
        	opacity: 0.5, 
        	background: "black" 
    	},
    	buttons: {
    		"Save": 		function() {
    			
    			var accept = $("#prot_accept").attr("checked");
    			var reject = $("#prot_reject").attr("checked");
    			var notsure = $("#prot_notsure").attr("checked");
    			var protid = $("#prot_id").val();
    			var comments = $("#prot_comments").val();
    			var validation = "U";
    			if(accept)	validation = 'A';
    			if(reject)	validation = 'R';
    			if(notsure) validation = 'N';
    			
    			// send a request to update the annotation for this protein
    			$.post("<yrcwww:link path='saveProteinAnnotation.do'/>",
    					{'pinferProtId': protid,
    					 'comments': comments,
    					 'validation': validation
    					},
    					function(data) {
    						if(data == "OK") {
    							if(comments != null && comments.length > 0) {
									$("#annot_comment_"+protid).text(comments);
									$("#annot_validation_style_"+protid).addClass('prot_annot_U');
								}

				    			if(accept) {
				    				$("#annot_validation_style_"+protid).removeClass();
				    				$("#annot_validation_style_"+protid).addClass('prot_annot_A');
				    			}
								else if (reject) {
									$("#annot_validation_style_"+protid).removeClass();
									$("#annot_validation_style_"+protid).addClass('prot_annot_R');
								}
								else if (notsure) {
									$("#annot_validation_style_"+protid).removeClass();
									$("#annot_validation_style_"+protid).addClass('prot_annot_N');
								}
					
								$("#annot_validation_text_"+protid).text(validation);
    						}
    						else {
    							alert("Error saving protein annotation.\n"+data);
    						}
    					});
    			
    			$(this).dialog("close");
    			
    		},
    		"Delete": function() {
    			var protid = $("#prot_id").val();
    			$.post("<yrcwww:link path='deleteProteinAnnotation.do'/>",
    					{'pinferProtId': protid},
    					function(data) {
    						if(data == "OK") {
    							$("#annot_comment_"+protid).text();
    							$("#annot_validation_text_"+protid).text('U');
								$("#annot_validation_style_"+protid).removeClass();
								$("#annot_validation_style_"+protid).addClass('prot_annot_U');
    						}
    						else {
    							alert("Error deleting protein annotation.\n"+data);
    						}
    			});
    			$(this).dialog("close");
    		},
    		"Cancel": 	function() {$(this).dialog("close");}
    	}
    });
    }
    else {
    	$("#prot_annot_dialog").dialog({
    	autoOpen: false,
    	modal: true,
    	width: 400,
    	height: 200,
    	overlay: { 
        	opacity: 0.5, 
        	background: "black" 
    	}});
    }
  	
  	$(".editprotannot").click(function(e){
  		
  		var protid = this.id;
  		var protname = this.title;
  		// set the values for the protein that the user is editing
  		$("#prot_id").val(protid);
  		$("#prot_name").text(protname);
  		
  		// reset the dialog
  		var comment = $("#annot_comment_"+protid).text();
  		var validation = $("#annot_validation_text_"+protid).text();
  		
  		if(comment != null)
  			$("#prot_comments").val(comment);
  		else
  			$("#prot_comments").val("");
  			
  		$("#prot_accept").attr("checked", "");
  		$("#prot_reject").attr("checked", "");
  		$("#prot_notsure").attr("checked", "");
  		
  		if(validation == "A" || validation == "U")	
  			$("#prot_accept").attr("checked", "checked");
  		
  		else if(validation == "R")
  			$("#prot_reject").attr("checked", "checked");
  			
  		else if(validation == "N") 
  			$("#prot_notsure").attr("checked", "checked");
  		
  		
  		// show the dialog
  		$("#prot_annot_dialog").dialog('open');
  		
	});

}

// ---------------------------------------------------------------------------------------
// MAKE PROTEIN LIST TABLE SORTABLE
// ---------------------------------------------------------------------------------------
function makeProteinListSortable() {
	
	// the header for the column that is sorted is highlighted
	//$('th', $table).each(function(){$(this).removeClass('ms_selected_header');});
	//$(this).addClass('ms_selected_header');
	
	var $table = $("#protlisttable");
	$('th', $table).each(function(column) {
  		
  		if ($(this).is('.sortable')) {
  		
  			var $header = $(this);
  			var sortBy = $(this).attr('id');
  			
      		$(this).hover(
      			function() {$(this).addClass('pinfer_small_hover');} , 
      			function() {$(this).removeClass('pinfer_small_hover');}).click(function() {
				
					// alert("sorting by "+sortBy);
					// sorting direction
					var sortOrder = 1;
					if ($(this).is('.sorted-asc')) {
	          			sortOrder = -1;
	        		}
	        		else if ($(this).is('.sorted-desc')) {
	          			sortOrder = 1;
	        		}
	        		else if($(this).is('.def_sort_desc')) {
	        			sortOrder = -1;
	        		}
        			sortResults(<%=pinferId%>, sortBy, sortOrder);
      			});
		}
    	});
}

// ---------------------------------------------------------------------------------------
// UPDATE RESULTS
// ---------------------------------------------------------------------------------------
$(document).ready(function() { 

	var options = {
		target:   '#protlist_table',
		beforeSubmit: beforeSubmit,
		success:  updateResults
	};
    // bind 'filterForm' and provide a callback function 
    $('#filterForm').ajaxForm(options); 
});
// validate the form parameters before submit.
function beforeSubmit() {
	
    if(!validateForm())
    	return false;
	$.blockUI();
}
function validateForm() {

	// fieldValue is a Form Plugin method that can be invoked to find the 
    // current value of a field 
    
    var value = $("form#filterForm input[name='minPeptides']").fieldValue();
    var valid = validateInt(value, "Min. Peptides", 1);
    if(!valid)	return false;
    var minPept = parseInt(value);
    $('form#filterForm input[name=minPeptides]').val(minPept);
    
    value = $('form#filterForm input[@name=minUniquePeptides]').fieldValue();
    valid = validateInt(value, "Min. Unique Peptides", 0, minPept);
    if(!valid)	return false;
    $('form#filterForm input[@name=minUniquePeptides]').val(parseInt(value));
    
    value = $('form#filterForm input[@name=minCoverage]').fieldValue();
    valid = validateFloat(value, "Min. Coverage", 0.0, 100.0);
    if(!valid)	return false;
    
    value = $('form#filterForm input[@name=minSpectrumMatches]').fieldValue();
    valid = validateInt(value, "Min. Spectrum Matches", 1);
    if(!valid)	return false;
    $('form#filterForm input[@name=minSpectrumMatches]').val(parseInt(value));
    
    value = $('form#filterForm input[@name=minMolecularWt]').fieldValue();
    valid = validateFloat(value, "Min. Molecular Wt.", 0);
    if(!valid)	return false;
    $('form#filterForm input[@name=minMolecularWt]').val(parseInt(value));
    
    
    return true;
}
function validateInt(value, fieldName, min, max) {
	var intVal = parseInt(value);
	var valid = true;
	if(isNaN(intVal))						valid = false;
	if(valid && intVal < min)				valid = false;
	if(max && (valid && intVal > max))		valid = false;
	
	if(!valid) {
		if(max) alert("Value for "+fieldName+" should be between "+min+" and "+max);
		else	alert("Value for "+fieldName+" should be >= "+min);
	}
	return valid;
}
function validateFloat(value, fieldName, min, max) {
	var floatVal = parseFloat(value);
	var valid = true;
	if(isNaN(floatVal))						valid = false;
	if(valid && floatVal < min)			valid = false;
	if(max && (valid && floatVal > max))	valid = false;
	if(!valid) {
		if(max) alert("Value for "+fieldName+" should be between "+min+" and "+max);
		else	alert("Value for "+fieldName+" should be >= "+min);
	}
	return valid;
}
function updateResults(responseText, statusText) {
	$.unblockUI();
  	refreshProteinList(responseText);
}

function refreshProteinList(responseText) {
	if(responseText != "STALE_ID") {
		setupProteinListTable();
	}
	else {
		alert("Got stale Protein Inference ID. Please refresh the page.");
	}
	//setupProteinListTable();
}
// ---------------------------------------------------------------------------------------
// DOWNLOAD RESULTS
// ---------------------------------------------------------------------------------------
function downloadResults() {

	// validate the current entries in the form
	var validated = validateForm();
	if(!validated)	return false;
	
	// copy the values from the filter form to the download form
	$("#downloadForm  input[name='minPeptides']").val($("#filterForm  input[name='minPeptides']").val());
	$("#downloadForm  input[name='maxPeptides']").val($("#filterForm  input[name='maxPeptides']").val());
	
	$("#downloadForm  input[name='minUniquePeptides']").val($("#filterForm input[name='minUniquePeptides']").val());
	$("#downloadForm  input[name='maxUniquePeptides']").val($("#filterForm input[name='maxUniquePeptides']").val());
	
	$("#downloadForm  input[name='minCoverage']").val($("#filterForm  input[name='minCoverage']").val());
	$("#downloadForm  input[name='maxCoverage']").val($("#filterForm  input[name='maxCoverage']").val());
	
	$("#downloadForm  input[name='minMolecularWt']").val($("#filterForm  input[name='minMolecularWt']").val());
	$("#downloadForm  input[name='maxMolecularWt']").val($("#filterForm  input[name='maxMolecularWt']").val());
	
	$("#downloadForm  input[name='minPi']").val($("#filterForm  input[name='minPi']").val());
	$("#downloadForm  input[name='maxPi']").val($("#filterForm  input[name='maxPi']").val());
	
	$("#downloadForm  input[name='minSpectrumMatches']").val($("#filterForm  input[name='minSpectrumMatches']").val());
	$("#downloadForm  input[name='maxSpectrumMatches']").val($("#filterForm  input[name='maxSpectrumMatches']").val());
	
	$("#downloadForm  input[name='showAllProteins']").val($("#filterForm  input[name='showAllProteins']:checked").val());

	$("#downloadForm  input[name='accessionLike']").val($("#filterForm  input[name='accessionLike']").val());
	$("#downloadForm  input[name='descriptionLike']").val($("#filterForm input[name='descriptionLike']").val());
	$("#downloadForm  input[name='descriptionNotLike']").val($("#filterForm input[name='descriptionNotLike']").val());
	
	var validationStatus = "";
	$("#filterForm  input[name='validationStatus']:checked").each(function() {validationStatus += ","+$(this).val();});
	if(validationStatus.length) {
		validationStatus = validationStatus.substring(1); // remove first comma
	}
	$("#downloadForm  input[name='validationStatusString']").val(validationStatus);
	
	
	$("#downloadForm").submit();
	
}

// ---------------------------------------------------------------------------------------
// GENE ONTOLOGY ENRICHMENT
// ---------------------------------------------------------------------------------------
function doGoEnrichmentAnalysis() {

	// validate the current entries in the form
	var validated = validateForm();
	if(!validated)	return false;
	// validate pvalue cutoff for enrichment calculation
	var value = $('form#goEnrichmentForm input[@name=goEnrichmentPVal]').fieldValue();
    var valid = validateFloat(value, "P-Value", 0.0, 1.0);
    if(!valid)	return false;
	
	// copy the values from the filter form to the GO enrichment form
	$("#goEnrichmentForm  input[name='minPeptides']").val($("#filterForm  input[name='minPeptides']").val());
	$("#goEnrichmentForm  input[name='maxPeptides']").val($("#filterForm  input[name='maxPeptides']").val());
	
	$("#goEnrichmentForm  input[name='minUniquePeptides']").val($("#filterForm input[name='minUniquePeptides']").val());
	$("#goEnrichmentForm  input[name='maxUniquePeptides']").val($("#filterForm input[name='maxUniquePeptides']").val());
	
	$("#goEnrichmentForm  input[name='minCoverage']").val($("#filterForm  input[name='minCoverage']").val());
	$("#goEnrichmentForm  input[name='maxCoverage']").val($("#filterForm  input[name='maxCoverage']").val());
	
	$("#goEnrichmentForm  input[name='minMolecularWt']").val($("#filterForm  input[name='minMolecularWt']").val());
	$("#goEnrichmentForm  input[name='maxMolecularWt']").val($("#filterForm  input[name='maxMolecularWt']").val());
	
	$("#goEnrichmentForm  input[name='minPi']").val($("#filterForm  input[name='minPi']").val());
	$("#goEnrichmentForm  input[name='maxPi']").val($("#filterForm  input[name='maxPi']").val());
	
	$("#goEnrichmentForm  input[name='minSpectrumMatches']").val($("#filterForm  input[name='minSpectrumMatches']").val());
	$("#goEnrichmentForm  input[name='maxSpectrumMatches']").val($("#filterForm  input[name='maxSpectrumMatches']").val());
	
	$("#goEnrichmentForm  input[name='showAllProteins']").val($("#filterForm  input[name='showAllProteins']:checked").val());

	$("#goEnrichmentForm  input[name='accessionLike']").val($("#filterForm  input[name='accessionLike']").val());
	$("#goEnrichmentForm  input[name='descriptionLike']").val($("#filterForm input[name='descriptionLike']").val());
	$("#goEnrichmentForm  input[name='descriptionNotLike']").val($("#filterForm input[name='descriptionNotLike']").val());
	
	var validationStatus = "";
	$("#filterForm  input[name='validationStatus']:checked").each(function() {validationStatus += ","+$(this).val();});
	if(validationStatus.length) {
		validationStatus = validationStatus.substring(1); // remove first comma
	}
	$("#goEnrichmentForm  input[name='validationStatusString']").val(validationStatus);
	
	
	$("#goEnrichmentForm").submit();
	
}

// ---------------------------------------------------------------------------------------
// SORT RESULTS
// ---------------------------------------------------------------------------------------
function sortResults(pinferId, sortBy, sortOrder) {
  
  //alert(sortBy);
  
  //var useMods = $("input[@name='peptideDef_useMods']:checked").val() == null ? false : true;
  //var useCharge = $("input[@name='peptideDef_useCharge']:checked").val() == null ? false : true;
  //var groupProteins = $("input[@name='joinGroupProteins']:checked").val();
  
	var sortOrderStr  = sortOrder == 1 ? 'ASC' : 'DESC';
	// get data from the server and put it in the appropriate div
	$.blockUI();
	$("#proteinListTable").load("<yrcwww:link path='sortProteinInferenceResult.do'/>",   			// url
							{'inferId': 		pinferId, 
							 'sortBy': 			sortBy,
							 'sortOrder': 		sortOrderStr}, 	            // data
							function(responseText, status, xhr) {			// callback
										$.unblockUI();
										refreshProteinList(responseText);
								   });	
	
	return false;
}

// ---------------------------------------------------------------------------------------
// PAGE RESULTS
// ---------------------------------------------------------------------------------------
function pageResults(pageNum) {
  
  	// get data from the server and put it in the appropriate div
  	$.blockUI();
  	$("#proteinListTable").load("<yrcwww:link path='pageProteinInferenceResult.do'/>",   			// url
  							{'inferId': 		<%=pinferId%>, 
  							 'pageNum': 		pageNum}, 	            	// data
  							function(responseText, status, xhr) {			// callback
  										$.unblockUI();
  										refreshProteinList(responseText);
  								   });	
  	
  	return false;
}

// ---------------------------------------------------------------------------------------
// MAKE A TABLE SORTABLE
// ---------------------------------------------------------------------------------------
function makeSortable(table) {
  	
	var $table = table;
	$('th', $table).each(function(column) {
  		
  		if ($(this).is('.sort-alpha') || $(this).is('.sort-int') 
  			|| $(this).is('.sort-int-special') || $(this).is('.sort-float') ) {
  		
  			var $header = $(this);
      		$(this).addClass('clickable').hover(
      			function() {$(this).addClass('pinfer_small_hover');} , 
      			function() {$(this).removeClass('pinfer_small_hover');}).click(function() {

				
				// remove row striping
				if($table.is('.stripe_table')) {
					$("tbody > tr:odd", $table).removeClass("tr_odd");
					$("tbody > tr:even", $table).removeClass("tr_even");
				}
				
				// sorting direction
				var newDirection = 1;
        		if ($(this).is('.sorted-asc')) {
          			newDirection = -1;
        		}
        				
        		var rows = $table.find('tbody > tr').get();
        				
        		if ($header.is('.sort-alpha')) {
        			$.each(rows, function(index, row) {
						row.sortKey = $(row).children('td').eq(column).text().toUpperCase();
					});
				}
				
				if ($header.is('.sort-int')) {
        					$.each(rows, function(index, row) {
								var key = parseInt($(row).children('td').eq(column).text());
						row.sortKey = isNaN(key) ? 0 : key;
					});
				}
				
				if ($header.is('.sort-int-special')) {
        					$.each(rows, function(index, row) {
								var key = parseInt($(row).children('td').eq(column).text().replace(/\(\d*\)/, ''));
						row.sortKey = isNaN(key) ? 0 : key;
					});
				}
				
				if ($header.is('.sort-float')) {
        					$.each(rows, function(index, row) {
								var key = parseFloat($(row).children('td').eq(column).text());
						row.sortKey = isNaN(key) ? 0 : key;
					});
				}

     			rows.sort(function(a, b) {
       				if (a.sortKey < b.sortKey) return -newDirection;
					if (a.sortKey > b.sortKey) return newDirection;
					return 0;
     			});

     			$.each(rows, function(index, row) {
       				$table.children('tbody').append(row);
       				row.sortKey = null;
     			});
     			
     			// the header for the column used for sorting is highlighted
				$('th', $table).each(function(){
					$(this).removeClass('pinfer_selected_header_small');
					$(this).removeClass('sorted-desc');
	    			$(this).removeClass('sorted-asc');
				});
				$header.addClass('pinfer_selected_header_small');
				
     			var $sortHead = $table.find('th').filter(':nth-child(' + (column + 1) + ')');

	          	if (newDirection == 1) {$sortHead.addClass('sorted-asc'); $sortHead.removeClass('sorted-desc');} 
	          	else {$sortHead.addClass('sorted-desc'); $sortHead.removeClass('sorted-asc');}
        
        		
        		// add row striping back
        		if($table.is('.stripe_table')) {
					$("tbody > tr:odd", $table).addClass("tr_odd");
					$("tbody > tr:even", $table).addClass("tr_even");
        		}
      		});
	}
  });
}


function toggleDivVisibility(mydiv) {
  	if($(mydiv).is(':visible'))
  		$(mydiv).hide();
  	else
  		$(mydiv).show();
}



</script>



<CENTER>

<yrcwww:contentbox title="Protein Inference* Results" centered="true" width="90" widthRel="true" scheme="pinfer" >
  
  <div id="results" class="flora">
      <ul>
          <li><a href="#protlist" rel="history" id="protlistlink"><span>Protein List</span></a></li>
          <li><a href="#protdetails" rel="history" id="protdetailslink"><span>Protein Details</span></a></li>
          <li><a href="#protclusters" rel="history" id="protclusterslink"><span>Protein Clusters</span></a></li>
          <li><a href="#input" rel="history" id="inputlink"><span>Summary</span></a></li>
      </ul>
   </div>
      
      
    <!-- Protein Annotation Dialog -->
	<div id="prot_annot_dialog" class="flora" title="Validate Protein">
		<input type="hidden" id="prot_id" value="" />
		Protein: <b><span id="prot_name"></span></b><br>
		<input type="radio" name="annotate" value="Accept" id="prot_accept" checked="checked"/>
		Accept	
		<input type="radio" name="annotate" value="Reject" id="prot_reject"/>
		Reject
		<input type="radio" name="annotate" value="Not Sure" id="prot_notsure" />
		Not Sure
		<br>
		<textarea name="comments" rows="4" cols="45" id="prot_comments"></textarea>
	</div>
      
    <!-- PROTEIN LIST -->
	<div id="protlist">
		<CENTER>
		<table><tr><td>
		
		<!-- SUMMARY -->
		<div style="padding:0 7 0 7; margin-bottom:5; border: 1px dashed gray;background-color: #F0F8FF;">
		<table align="center">
			<tr>
				<td>
					<b>Protein Inference ID:</b>
				</td>
				<td>
					<bean:write name="idpickerRun" property="id"/> &nbsp; (Program Version: <b> <bean:write name="idpickerRun" property="programVersion"/> </b>)
				</td>
			</tr>
			<tr>
				<td>
					<b>Date:</b>
				</td>
				<td>
					<bean:write name="idpickerRun" property="date"/>&nbsp;
				</td>
			</tr>
			<tr>
				<td><b>Comments </b>
					<logic:equal name="writeAccess" value="true">
					<span class="editableComment clickable" id="piRun_<bean:write name='idpickerRun' property='id'/>" style="font-size:8pt; color:red;">[Edit]</span>
					</logic:equal>
				</td>
				<td>
					<span id="piRun_<bean:write name='idpickerRun' property='id'/>_text"><bean:write name="idpickerRun" property="comments"/></span>
				</td>
			</tr>
			<tr>
				<td colspan="2" valign="top">
				<div id="piRun_<bean:write name='idpickerRun' property='id'/>_edit" align="center"
			     style="display:none;">
			     <textarea rows="5" cols="60" class="edit_text"></textarea>
			     <br>
			     <button class="savePiRunComments" id="<bean:write name='idpickerRun' property='id'/>">Save</button>
			     <button class="cancelPiRunComments" id="<bean:write name='idpickerRun' property='id'/>">Cancel</button>
				</div>
				</td>
			</tr>
		</table>
		</div>
	
		<%@ include file="proteinInferFilterForm.jsp" %>
		<%@include file="proteinInferDownloadForm.jsp" %>
		
		<logic:equal name="speciesIsYeast" value="true">
		<%@include file="goEnrichmentInputForm.jsp" %>
		</logic:equal>
		
		</td></tr></table>
		</CENTER>
		
		<div id="protlist_table">
    	<%@ include file="proteinlist.jsp" %>
    	</div>
    </div>
    
    
    
      <!-- PROTEIN CLUSTER -->
      <div id="protclusters"><font color="black">
          <!-- create a placeholder div for protein cluster -->
          <div id="protcluster_div" style="display: none;"></div>
      </font></div>
      
      
      
      
      <!-- PROTEIN DETAILS -->
      <div id="protdetails">
      		<!-- create a placeholder div for protein details -->
      		<div id="protein_div" style="display: none;" class="protdetail_prot"></div>
      </div>
      
      <!-- INPUT SUMMARY -->
      <div id="input">
      	<%@ include file="inputSummary.jsp" %>
	  </div>

<div style="font-size: 8pt;margin-top: 3px;" align="left">
	*This protein inference program is based on the IDPicker algorithm published in:<br>
 	<i>Proteomic Parsimony through Bipartite Graph Analysis Improves Accuracy and Transparency.</i>
 	&nbsp; &nbsp;
	<br>Tabb <i>et. al.</i> <i>J Proteome Res.</i> 2007 Sep;6(9):3549-57
</div>
<br>
<div style="font-size: 8pt;margin-top: 3px;" align="left">
	**Normalized Spectral Abundance Factor (NSAF)<br>
	<i>Quantitative proteomic analysis of distinct mammalian mediator complexes using normalized spectral abundance factors.</i>
    &nbsp; &nbsp;
    <br>Paoletti <i>et. al.</i> <i>Proc. Natl. Acad. Sci. USA </i>2006;103:18928-33
</div>

 	
</yrcwww:contentbox>
</CENTER>

<%@ include file="/includes/footer.jsp" %>