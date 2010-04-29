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
  $().ajaxStop($.unblockUI);

//---------------------------------------------------------------------------------------
//PROTEIN DETAILS
//---------------------------------------------------------------------------------------
function showProteinDetails(nrseqId) {
	
	// protName.setHyperlink("viewProtein.do?id="+protein.getNrseqId());
	window.location.href = "viewProtein.do?id="+nrseqId;
}

// ---------------------------------------------------------------------------------------
// PAGE RESULTS
// ---------------------------------------------------------------------------------------
function pageResults(pageNum) {
  	$("input#pageNum").val(pageNum);
  	$("input#download").val("false");
  	$("input#goEnrichment").val("false");
  	//$("input#cluster").val("false");
  	//alert("setting to "+pageNum+" value set to: "+$("input#pageNum").val());
  	$("form[name='proteinSetComparisonForm']").submit();
}

//---------------------------------------------------------------------------------------
//NAVIGATE TO RELEVANT PAGE
//---------------------------------------------------------------------------------------
function goToHeatMapIndex(index) {
	var numPerPage = $("input#numPerPage").val();
	var newPage = Math.ceil(index / numPerPage);
	$("input#rowIndex").val(index);
	pageResults(newPage);
}

// ---------------------------------------------------------------------------------------
// SORT RESULTS
// ---------------------------------------------------------------------------------------
function sortResults(sortBy, sortOrder) {
  	$("input#pageNum").val(1);
  	$("input#download").val("false");
  	$("input#goEnrichment").val("false");
  	$("input#cluster").val("false");
  	$("input#sortBy").val(sortBy);
  	$("input#sortOrder").val(sortOrder);
  	$("form[name='proteinSetComparisonForm']").attr('target', '');
  	$("form[name='proteinSetComparisonForm']").submit();
}

// ---------------------------------------------------------------------------------------
// UPDATE RESULTS
// ---------------------------------------------------------------------------------------
function updateResults() {
  	$("input#pageNum").val(1);
  	$("input#download").val("false");
  	$("input#goEnrichment").val("false");
  	//$("input#cluster").val("false");
  	$("input#numPerPage").val($("input#pager_result_count").val());
  	//alert("setting result count to: "+$("input#numPerPage").val());
  	$("form[name='proteinSetComparisonForm']").attr('target', '');
  	$("form[name='proteinSetComparisonForm']").submit();
}

// ---------------------------------------------------------------------------------------
// DOWNLOAD RESULTS
// ---------------------------------------------------------------------------------------
function downloadResults() {
  	$("input#download").val("true");
  	$("input#goEnrichment").val("false");
  	$("input#cluster").val("false");
  	$("form[name='proteinSetComparisonForm']").attr('target', '_blank');
  	$("form[name='proteinSetComparisonForm']").submit();
}

// ---------------------------------------------------------------------------------------
// GENE ONTOLOGY ENRICHMENT
// ---------------------------------------------------------------------------------------
function doGoEnrichmentAnalysis() {
	$("input#download").val("false");
	$("input#cluster").val("false");
	$("input#goEnrichment").val("true");
	if(!validateGoEnrichmentForm())
    	return false;
	$("form[name='proteinSetComparisonForm']").submit();
}

function validateGoEnrichmentForm() {
	// fieldValue is a Form Plugin method that can be invoked to find the 
    // current value of a field 
    value = $('form input[@name=goEnrichmentPVal]').fieldValue();
    valid = validateFloat(value, "P-Value", 0.0, 1.0);
    if(!valid)	return false;
    
    return true;
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
// ---------------------------------------------------------------------------------------
// TOGGLE AND, OR, NOT FILTERS
// ---------------------------------------------------------------------------------------
function toggleAndSelect(dsIndex, red, green, blue) {
	
	var id = "AND_"+dsIndex+"_select";
	var value = $("input#"+id).val();
	if(value == "true") {
		$("input#"+id).val("false");	
		$("td#AND_"+dsIndex+"_td").css("background-color", "#FFFFFF");
	}
	else {
		var color = "rgb("+red+","+green+","+blue+")";
		$("input#"+id).val("true");	
		$("td#AND_"+dsIndex+"_td").css("background-color", color);
	}
}
function toggleOrSelect(dsIndex, red, green, blue) {
	var id = "OR_"+dsIndex+"_select";
	var value = $("input#"+id).val();
	if(value == "true") {
		$("input#"+id).val("false");	
		$("td#OR_"+dsIndex+"_td").css("background-color", "#FFFFFF");
	}
	else {
		var color = "rgb("+red+","+green+","+blue+")";
		$("input#"+id).val("true");	
		$("td#OR_"+dsIndex+"_td").css("background-color", color);
	}
}
function toggleNotSelect(dsIndex, red, green, blue) {
	var id = "NOT_"+dsIndex+"_select";
	var value = $("input#"+id).val();
	if(value == "true") {
		$("input#"+id).val("false");	
		$("td#NOT_"+dsIndex+"_td").css("background-color", "#FFFFFF");
	}
	else {
		var color = "rgb("+red+","+green+","+blue+")";
		$("input#"+id).val("true");	
		$("td#NOT_"+dsIndex+"_td").css("background-color", color);
	}
}
function toggleXorSelect(dsIndex, red, green, blue) {
	var id = "XOR_"+dsIndex+"_select";
	var value = $("input#"+id).val();
	if(value == "true") {
		$("input#"+id).val("false");	
		$("td#XOR_"+dsIndex+"_td").css("background-color", "#FFFFFF");
	}
	else {
		var color = "rgb("+red+","+green+","+blue+")";
		$("input#"+id).val("true");	
		$("td#XOR_"+dsIndex+"_td").css("background-color", color);
	}
}

// ---------------------------------------------------------------------------------------
// TOGGLE FULL PROTEIN NAMES
// ---------------------------------------------------------------------------------------
function toggleFullNames() {
	if($("#full_names").text() == "[Full Names]") {
		$("#full_names").text("[Short Names]");
		$(".full_name").show();
		$(".short_name").hide();
	}
	else if($("#full_names").text() == "[Short Names]") {
		$("#full_names").text("[Full Names]");
		$(".full_name").hide();
		$(".short_name").show();
	}
}

// ---------------------------------------------------------------------------------------
// TOGGLE FULL PROTEIN DESCRIPTIONS
// ---------------------------------------------------------------------------------------
function toggleFullDescriptions() {
	if($("#full_descriptions").text() == "[Full Descriptions]") {
		$("#full_descriptions").text("[Short Descriptions]");
		$(".full_description").show();
		$(".short_description").hide();
	}
	else if($("#full_descriptions").text() == "[Short Descriptions]") {
		$("#full_descriptions").text("[Full Descriptions]");
		$(".full_description").hide();
		$(".short_description").show();
	}
}

function showAllDescriptionsForProtein(nrseqId) {
	$("#short_desc_"+nrseqId).hide();
	$("#full_desc_"+nrseqId).show();
}
function hideAllDescriptionsForProtein(nrseqId) {
	$("#short_desc_"+nrseqId).show();
	$("#full_desc_"+nrseqId).hide();
}