
<%@page import="java.util.List"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<script src="/yrc/js/jquery.ui-1.6rc2/jquery-1.2.6.js"></script>

<script type="text/javascript" src="/yrc/js/jquery.ui-1.6rc2/ui/ui.core.js">
</script><script type="text/javascript" src="/yrc/js/jquery-impromptu.1.6.js"></script>
<script type="text/javascript" src="/yrc/js/jquery.ui-1.6rc2/ui/ui.tabs.js"></script>
<script type="text/javascript" src="/yrc/js/jquery.ui-1.6rc2/ui/ui.dialog.js"></script>
<script type="text/javascript" src="/yrc/js/jquery.ui-1.6rc2/ui/ui.draggable.js"></script>
<script type="text/javascript" src="/yrc/js/jquery.ui-1.6rc2/ui/ui.resizable.js"></script>

<script type="text/javascript" src="/yrc/js/jquery.history.js"></script>

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
	int clusterCount = ((List<Integer>)request.getAttribute("clusterIds")).size();
%>

<script>

// ---------------------------------------------------------------------------------------
// AJAX DEFAULTS
// ---------------------------------------------------------------------------------------
  $.ajaxSetup({
  	type: 'POST',
  	timeout: 15000,
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
  $().ajaxStart($.blockUI).ajaxStop($.unblockUI);



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
		else if (hash == 'protclusters')
			$tabs.tabs('select', 1);
		else if (hash == 'protdetails')
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

	// set up the tabs and select the first tab
    $("#results > ul").tabs().tabs('select', 0);
  
 	$(".stripe_table th").addClass("ms_A");
 	$(".stripe_table tbody > tr:odd").addClass("ms_A");
 	
  	setupProteinListTable();
  	
   	$('table.sortable').each(function() {
    	var $table = $(this);
    	makeSortable($table);
  	});
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
				$("#protsequence_"+pinferProteinId).load("proteinSequence.do",   					// url
								                        {'pinferProteinId': pinferProteinId}); 	// data
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
function showProteinDetails(proteinId) {
	// first hide all divs 
	$(".protdetail_prot").hide();
	
	// load content in the appropriate div
	$("#protein_"+proteinId).load("proteinDetails.do",   									// url
								  {'pinferId': <%=pinferId%>, 'pinferProtId': proteinId}, 	// data
								  function(responseText, status, xhr) {						// callback
								  		// stripe the table
										$("#protdetailstbl_"+proteinId+" th.main").addClass("ms_A");
										$("#protdetailstbl_"+proteinId+" tbody tr.main").addClass("ms_A");
										$(this).show();
										var $tabs = $("#results").tabs();
										//$("#protdetailslink").click(); // so that history works
										$tabs.tabs('select', 2);
										$(".allpsms").each(function(){
											var table = $(this);
											makeSortable(table);
										});
										
								  });	
}
  
// ---------------------------------------------------------------------------------------
// SHOW PROTEIN CLUSTER
// ---------------------------------------------------------------------------------------    
function showProteinCluster(proteinClusterIdx) {

	$("#clusterlist")[0].selectedIndex = proteinClusterIdx - 1;
	selectProteinCluster();
	
	var $tabs = $("#results").tabs();
	$("#protclusterslink").click();
	//$tabs.tabs('select', 1);
	return false;
}
  
  
function selectProteinCluster() {

	var clusterId = $("#clusterlist")[0].selectedIndex + 1;
	
	// hide all other first
	for(var i = 1; i <= <%=clusterCount%>; i++) {
		$("#protcluster_"+i).hide();
	}
	// get data from the server and put it in the appropriate div
	$("#protcluster_"+clusterId).load("proteinCluster.do",   								// url
								  	  {'pinferId': <%=pinferId%>, 'clusterId': clusterId}, 	// data
								      function(responseText, status, request) {				// callback
 								  		
 								  		$("#assoctable_"+clusterId).css('border', '1px dashed gray').css('border-spacing', '2px');
 										$("#assoctable_"+clusterId+"  td").css('border', '1px #CCCCCC dashed').css('padding', '4px');
								  	$("#assoctable_"+clusterId+"  th").css('border', '1px #CCCCCC dashed').css('padding', '4px').addClass("ms_A");
								  	
								  	$("#prot_grp_table_"+clusterId).css('border', '1px dashed gray').css('border-spacing', '2px');
								  	$("#prot_grp_table_"+clusterId+"  td").css('border', '1px #CCCCCC dashed').css('padding', '4px');
								  	$("#prot_grp_table_"+clusterId+"  th").css('border', '1px #CCCCCC dashed').css('padding', '4px').addClass("ms_A");
								  	
								  	$("#pept_grp_table_"+clusterId).css('border', '1px dashed gray').css('border-spacing', '2px');
								  	$("#pept_grp_table_"+clusterId+"  td").css('border', '1px #CCCCCC dashed').css('padding', '4px');
								  	$("#pept_grp_table_"+clusterId+"  th").css('border', '1px #CCCCCC dashed').css('padding', '4px').addClass("ms_A");
 										
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
								  });	
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
 	$("#protGrp_"+proteinGroupId).css("background-color","#FFFF00");
 	
 	
 	// now select the PEPTIDE group cells we want AND the PROTEIN-PEPTIDE association cells
		lastSelectedPeptGrpIds = [];
		var j = 0;
		// peptide groups NOT unique to protein
		for(var i = 0; i < peptGrpIds.length; i++) {
			$(".peptGrp_"+peptGrpIds[i]).each(function() {$(this).css("background-color","#FFFF00");});
			$("#peptEvFor_"+proteinGroupId+"_"+peptGrpIds[i]).css("background-color","#FFFF00");
			lastSelectedPeptGrpIds[j] = peptGrpIds[i];
			j++;
		}
		// peptide groups UNIQUE to protein
		for(var i = 0; i < uniqPeptGrpIds.length; i++) {
			$(".peptGrp_"+uniqPeptGrpIds[i]).each(function() {$(this).css("background-color","#00FFFF");});
			$("#peptEvFor_"+proteinGroupId+"_"+uniqPeptGrpIds[i]).css("background-color","#00FFFF");
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
									$("#psmtbl_"+runSearchId+" th").addClass("ms_A");
									$("#psmtbl_"+runSearchId+" tr:even").addClass("ms_A");
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
  	$("#protlisttable tbody tr.protgrp_row").addClass("ms_A");
  	$("#protlisttable > thead > tr > th").addClass("ms_A");
  	
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
  				$("#peptforprot_"+id).load("getProteinPeptides.do", 	//url
  									{'pinferId': <%=pinferId%>, 		// data
  									 'proteinGroupId': id
  									 },
  									 function(responseText, status, xhr) {						// callback
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
  				$("#peptforprot_"+protId+"_"+grpId).load("getProteinPeptides.do", 	//url
  									{'pinferId': <%=pinferId%>, 					// data
  									 'proteinGroupId': grpId,
  									 'proteinId': protId
  									 },
  									 function(responseText, status, xhr) {			// callback
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
	$('th', $table).each(function(){$(this).removeClass('ms_selected_header');});
	$(this).addClass('ms_selected_header');
	
	var $table = $("#protlisttable");
	$('th', $table).each(function(column) {
  		
  		if ($(this).is('.sortable')) {
  		
  			var $header = $(this);
  			var sortBy = $(this).attr('id');
  			
      		$(this).addClass('clickable').hover(
      			function() {$(this).addClass('ms_hover');} , 
      			function() {$(this).removeClass('ms_hover');}).click(function() {
				
					alert("sorting by "+sortBy);
					// sorting direction
					var sortOrder = 1;
	        		if ($(this).is('.sorted-desc')) {
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
		success:  updateResults
	};
    // bind 'filterForm' and provide a callback function 
    $('#filterForm').ajaxForm(options); 
});

function updateResults(responseText, statusText) {
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
	$("#proteinListTable").load("sortProteinInferenceResult.do",   			// url
							{'inferId': 		pinferId, 
							 'sortBy': 			sortBy,
							 'sortOrder': 		sortOrderStr}, 	            // data
							function(responseText, status, xhr) {			// callback
										setupProteinListTable();
										// highlight the sorted column
										$("#"+sortBy).addClass("ms_selected_header");
										if(sortOrder == 1)
											$("#"+sortBy).addClass("sorted-asc");
										else
											$("#"+sortBy).addClass("sorted-desc");
								   });	
	
	return false;
}

// ---------------------------------------------------------------------------------------
// PAGE RESULTS
// ---------------------------------------------------------------------------------------
function pageResults(pageNum) {
  
  	// get data from the server and put it in the appropriate div
  	$("#proteinListTable").load("pageProteinInferenceResult.do",   			// url
  							{'inferId': 		<%=pinferId%>, 
  							 'pageNum': 		pageNum}, 	            	// data
  							function(responseText, status, xhr) {			// callback
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
      			function() {$(this).addClass('ms_hover');} , 
      			function() {$(this).removeClass('ms_hover');}).click(function() {

				// the header for the column used for sorting is highlighted
				$('th', $table).each(function(){$(this).removeClass('ms_selected_header');});
				$header.addClass('ms_selected_header');
				
				// remove row striping
				if($table.is('.stripe_table')) {
					$("tbody > tr:odd", $table).removeClass("ms_A");
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
        				
        				var $sortHead = $table.find('th').filter(':nth-child(' + (column + 1) + ')');

	          	if (newDirection == 1) {$sortHead.addClass('sorted-asc'); $sortHead.removeClass('sorted-desc');} 
	          	else {$sortHead.addClass('sorted-desc'); $sortHead.removeClass('sorted-asc');}
        
        				// add row striping back
        				if($table.is('.stripe_table')) {
					$("tbody > tr:odd", $table).addClass("ms_A");
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

<yrcwww:contentbox title="IDPicker* Results" centered="true" width="1000" scheme="ms">


<div style="font-size: 8pt;margin-top: 3px;" align="center">
 	<i>*Proteomic Parsimony through Bipartite Graph Analysis Improves Accuracy and Transparency.</i>
 	<br>
	Tabb <i>et. al.</i> <i>J Proteome Res.</i> 2007 Sep;6(9):3549-57
</div>
  
  <div id="results" class="flora">
      <ul>
          <li><a href="#protlist" rel="history" id="protlistlink"><span>Protein List</span></a></li>
          <li><a href="#protclusters" rel="history" id="protclusterslink"><span>Protein Clusters</span></a></li>
          <li><a href="#protdetails" rel="history" id="protdetailslink"><span>Protein Details</span></a></li>
          <li><a href="#input" rel="history" id="inputlink"><span>Summary</span></a></li>
      </ul>
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
          	<b>Select Protein Cluster: </b>
          	<select id="clusterlist" onchange="selectProteinCluster()">
          		<logic:iterate name="clusterIds" id="id">
          			<option value="<bean:write name="id" />"><bean:write name="id" /></option>
          		</logic:iterate>
          	</select>
          	
          	<!-- create a placeholder div for each protein cluster -->
          	<logic:iterate name="clusterIds" id="id">
          		<div id="protcluster_<bean:write name="id" />" style="display: none;"></div>
          	</logic:iterate>
      </font></div>
      
      
      
      
      <!-- PROTEIN DETAILS -->
      <div id="protdetails">
      		<!-- create a placeholder div for each protein -->
      		<logic:iterate name="proteinGroups" id="proteinGroup">
      			<logic:iterate name="proteinGroup" property="proteins" id="protein">
      				<div id="protein_<bean:write name="protein" property="protein.id" />" style="display: none;" class="protdetail_prot"></div>
      			</logic:iterate>
      		</logic:iterate>
      </div>
      
      <!-- INPUT SUMMARY -->
      <div id="input">
      	<%@ include file="inputSummary.jsp" %>
	  </div>

 	
</yrcwww:contentbox>
</CENTER>

<%@ include file="/includes/footer.jsp" %>