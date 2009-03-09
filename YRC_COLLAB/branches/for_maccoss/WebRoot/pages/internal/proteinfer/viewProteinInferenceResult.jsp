
<%@page import="java.util.List"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<script src="/yrc/js/jquery.ui-1.6rc2/jquery-1.2.6.js"></script>

<script type="text/javascript" src="/yrc/js/jquery.ui-1.6rc2/ui/ui.core.js"></script>
<script type="text/javascript" src="/yrc/js/jquery.ui-1.6rc2/ui/ui.tabs.js"></script>
<script type="text/javascript" src="/yrc/js/jquery.ui-1.6rc2/ui/ui.dialog.js"></script>
<script type="text/javascript" src="/yrc/js/jquery.ui-1.6rc2/ui/ui.draggable.js"></script>
<script type="text/javascript" src="/yrc/js/jquery.ui-1.6rc2/ui/ui.resizable.js"></script>

<script type="text/javascript" src="/yrc/js/jquery.history.js"></script>
<script type="text/javascript" src="/yrc/js/jquery.cookie.js"></script>

<script src="/yrc/js/tooltip.js"></script>

<script src="/yrc/js/jquery.form.js"></script>

<script src="/yrc/js/jquery.blockUI.js"></script>

<link rel="stylesheet" href="/yrc/css/proteinfer.css" type="text/css" >

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
// SHOW/HIDE PROTEIN SEQUENCE
// --------------------------------------------------------------------------------------- 
// View the protein sequence
function toggleProteinSequence (pinferProteinId) {

	//alert("protein id: "+pinferProteinId+" pinferId: "+pinferId);
	var button = $("#protseqbutton_"+pinferProteinId);
	
	if(button.text() == "[View Sequence]") {
		//alert("View");
		if($("#protsequence_"+pinferProteinId).html().length == 0) {
			//alert("Getting...");
			// load data in the appropriate div
			$.blockUI(); 
			$("#protsequence_"+pinferProteinId).load("proteinSequence.do",   				// url
							                        {'pinferProteinId': pinferProteinId}, 	// data
							                        function(responseText, status, xhr) {	// callback
								  						$.unblockUI();
								  					});
		}
		button.text("[Hide Sequence]");
		$("#protseqtbl_"+pinferProteinId).show();
	}
	else {
		button.text("[View Sequence]");
		$("#protseqtbl_"+pinferProteinId).hide();
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
			$("#hitsforion_"+pinferIonId).load("psmListForIon.do",   					// url
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
	var doc = "/yrc/viewSpectrum.do?scanID="+scanId+"&runSearchResultID="+hitId;
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
	$("#protein_div").load("proteinDetails.do",   									// url
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
	$("#protcluster_div").load("proteinCluster.do",   								// url
								  	  {'pinferId': <%=pinferId%>, 'clusterId': clusterId}, 	// data
								      function(responseText, status, request) {				// callback
 								  		
 								  		if(block) $.unblockUI();
 								  		
 								  		$("#assoctable_"+clusterId).css('border', '1px dashed gray').css('border-spacing', '2px');
 										$("#assoctable_"+clusterId+"  td").css('border', '1px #CCCCCC dashed').css('padding', '4px');
								  		$("#assoctable_"+clusterId+"  th").css('border', '1px #CCCCCC dashed').css('padding', '4px').addClass("pinfer_A_small");
								  	
								  		$("#prot_grp_table_"+clusterId).css('border', '1px dashed gray').css('border-spacing', '2px');
								  		$("#prot_grp_table_"+clusterId+"  td").css('border', '1px #CCCCCC dashed').css('padding', '4px');
								  		$("#prot_grp_table_"+clusterId+"  th").css('border', '1px #CCCCCC dashed').css('padding', '4px').addClass("pinfer_A_small");
								  	
								  		$("#pept_grp_table_"+clusterId).css('border', '1px dashed gray').css('border-spacing', '2px');
								  		$("#pept_grp_table_"+clusterId+"  td").css('border', '1px #CCCCCC dashed').css('padding', '4px');
								  		$("#pept_grp_table_"+clusterId+"  th").css('border', '1px #CCCCCC dashed').css('padding', '4px').addClass("pinfer_A_small");
 										
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
 	$("#protGrp_"+proteinGroupId).css("background-color","#EEE8AA");
 	
 	
 	// now select the PEPTIDE group cells we want AND the PROTEIN-PEPTIDE association cells
		lastSelectedPeptGrpIds = [];
		var j = 0;
		// peptide groups NOT unique to protein
		for(var i = 0; i < peptGrpIds.length; i++) {
			$(".peptGrp_"+peptGrpIds[i]).each(function() {$(this).css("background-color","#EEE8AA");});
			$("#peptEvFor_"+proteinGroupId+"_"+peptGrpIds[i]).css("background-color","#EEE8AA");
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
// SHOW SPECTRUM MATCHES
// ---------------------------------------------------------------------------------------    
function showSpectrumMatches(runSearchId, runName) {
	$(".input_psm").hide();
	$("#psm_"+runSearchId).show();
	
	if($("#psm_"+runSearchId).html().length == 0) {
		$("#psm_"+runSearchId).html("<b>Loading Peptide Spectrum Matches for: "+runName+"...</b>");
		$("#psm_"+runSearchId).load("psmMatches.do", //url
								{'pinferId': <%=pinferId%>, 'runSearchId': runSearchId},
								function(responseText, status, xhr) {						// callback
							  		// stripe the table
									$("#psmtbl_"+runSearchId+" th").addClass("pinfer_A");
									$("#psmtbl_"+runSearchId+" tr:even").addClass("pinfer_A");
									makeSortable($("#psmtbl_"+runSearchId));
									$(this).show();
							  });
	}
} 
  

// ---------------------------------------------------------------------------------------
// MAKE PROTEIN LIST TABLE SORTABLE
// ---------------------------------------------------------------------------------------
function setupProteinListTable() {
  
  	// stripe table rows
  	$("#protlisttable tbody tr.protgrp_row").addClass("pinfer_A");
  	$("#protlisttable > thead > tr > th").addClass("pinfer_A");
  	
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
  				$("#peptforprot_"+id).load("getProteinPeptides.do", 	//url
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
  				$("#peptforprot_"+protId+"_"+grpId).load("getProteinPeptides.do", 	//url
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
    			$.post("saveProteinAnnotation.do",
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
    			$.post("deleteProteinAnnotation.do",
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
  			
      		$(this).addClass('clickable').hover(
      			function() {$(this).addClass('pinfer_hover');} , 
      			function() {$(this).removeClass('pinfer_hover');}).click(function() {
				
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
	// fieldValue is a Form Plugin method that can be invoked to find the 
    // current value of a field 
    
    var value = $('input[@name=minPeptides]').fieldValue();
    var valid = validateInt(value, "Min. Peptides", 1);
    if(!valid)	return false;
    var minPept = parseInt(value);
    $('input[@name=minPeptides]').val(minPept);
    
    value = $('input[@name=minUniquePeptides]').fieldValue();
    valid = validateInt(value, "Min. Unique Peptides", 0, minPept);
    if(!valid)	return false;
    $('input[@name=minUniquePeptides]').val(parseInt(value));
    
    value = $('input[@name=minCoverage]').fieldValue();
    valid = validateFloat(value, "Min. Coverage", 0.0, 100.0);
    if(!valid)	return false;
    
    value = $('input[@name=minSpectrumMatches]').fieldValue();
    valid = validateInt(value, "Min. Spectrum Matches", 1);
    if(!valid)	return false;
    $('input[@name=minSpectrumMatches]').val(parseInt(value));
    
	$.blockUI();
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
  	setupProteinListTable();
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
	$("#proteinListTable").load("sortProteinInferenceResult.do",   			// url
							{'inferId': 		pinferId, 
							 'sortBy': 			sortBy,
							 'sortOrder': 		sortOrderStr}, 	            // data
							function(responseText, status, xhr) {			// callback
										$.unblockUI();
										setupProteinListTable();
								   });	
	
	return false;
}

// ---------------------------------------------------------------------------------------
// PAGE RESULTS
// ---------------------------------------------------------------------------------------
function pageResults(pageNum) {
  
  	// get data from the server and put it in the appropriate div
  	$.blockUI();
  	$("#proteinListTable").load("pageProteinInferenceResult.do",   			// url
  							{'inferId': 		<%=pinferId%>, 
  							 'pageNum': 		pageNum}, 	            	// data
  							function(responseText, status, xhr) {			// callback
  										$.unblockUI();
  										setupProteinListTable();
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
      			function() {$(this).addClass('pinfer_hover');} , 
      			function() {$(this).removeClass('pinfer_hover');}).click(function() {

				
				// remove row striping
				if($table.is('.stripe_table')) {
					$("tbody > tr:odd", $table).removeClass("pinfer_A");
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
					$("tbody > tr:odd", $table).addClass("pinfer_A");
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



 
<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<CENTER>

<yrcwww:contentbox title="Protein Inference* Results" centered="true" width="950" scheme="pinfer">
  
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
		<%@ include file="proteinInferFilterForm.jsp" %>
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