
<%@page import="org.yeastrc.ms.domain.search.SORT_ORDER"%><script src="<yrcwww:link path='js/jquery-1.3.2.min.js'/>"></script>

<script>
// ---------------------------------------------------------------------------------------
// SETUP THE TABLE
// ---------------------------------------------------------------------------------------
$(document).ready(function() {
   $(".perc_results").each(function() {
   		var $table = $(this);
   		$table.attr('width', "100%");
   		$table.attr('align', 'center');
   		$('th', $table).attr("align", "left");
   		
   		// #ED9A2E #D74D2D
   		$('th', $table).each(function() {
   			if($(this).is('.sorted-asc') || $(this).is('.sorted-desc')) {
   				$(this).addClass('th_selected');
   			}
   			else
   			$(this).addClass('th_normal');
   		});
   		$('th', $table).each(function() {
   		
   				if($(this).is('.sortable')) {
   					$(this).hover(
   						function() {$(this).addClass('th_hover');} , 
      					function() {$(this).removeClass('th_hover');}
      				);
      					
      				$(this).click(function() {
						var sortBy = $(this).attr('id');
						// sorting direction
						var sortOrder = "<%=SORT_ORDER.ASC.name()%>";
						if ($(this).is('.sorted-asc')) {
		          			sortOrder = "<%=SORT_ORDER.DESC.name()%>";
		        		}
		        		else if ($(this).is('.sorted-desc')) {
		          			sortOrder = "<%=SORT_ORDER.ASC.name()%>";
		        		}
	        			sortResults(sortBy, sortOrder);
      			});
      		}
      	});
   		
   		$("tbody > tr:even", $table).addClass('project_A');
   		//$('tbody > tr:odd', $table).css("background-color", "F0FFF0");
   });
});

// ---------------------------------------------------------------------------------------
// PAGE RESULTS
// ---------------------------------------------------------------------------------------
function pageResults(pageNum) {
  	$("input#pageNum").val(pageNum);
  	//alert("setting to "+pageNum+" value set to: "+$("input#pageNum").val());
  	$("form").submit();
}
// ---------------------------------------------------------------------------------------
// SORT RESULTS
// ---------------------------------------------------------------------------------------
function sortResults(sortBy, sortOrder) {
  	// alert(sortBy+" "+sortOrder);
  	$("input#pageNum").val(1); // reset the page number to 1
  	$("input#sortBy").val(sortBy);
  	$("input#sortOrder").val(sortOrder);
  	//alert($("input#pageNum").val()+"   "+$("input#sortBy").val()+"   "+$("input#sortOrder").val());
  	$("form").submit();
}
// ---------------------------------------------------------------------------------------
// UPDATE RESULTS
// ---------------------------------------------------------------------------------------
function updateResults() {
	$("input#pageNum").val(1); // reset the page number to 1
	$("form").submit();
}

</script>

