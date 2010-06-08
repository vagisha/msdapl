<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
response.addHeader("Cache-control", "no-store"); // tell proxy not to cache
response.addHeader("Cache-control", "max-age=0"); // stale right away
%>

<html>

<head>
 <yrcwww:title />
 <link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='/css/global.css' />">
</head>

<body>

<script src="<yrcwww:link path='js/jquery.ui-1.6rc2/jquery-1.2.6.js'/>"></script>
<script>
// ---------------------------------------------------------------------------------------
// SETUP ANY BASIC TABLES
// ---------------------------------------------------------------------------------------
$(document).ready(function() {
   $(".table_basic").each(function() {
   		var $table = $(this);
   		$('tbody > tr:odd', $table).addClass("tr_odd");
   		$('tbody > tr:even', $table).addClass("tr_even");
   });
   
   $(".foldable").each(function() {
   		$(this).click(function() {
   			fold($(this));
   		});
   });
});
  
// ---------------------------------------------------------------------------------------
// MAKE A TABLE STRIPED
// ---------------------------------------------------------------------------------------
function makeStripedTable(table) {
	var $table = $(table);
	$('tbody > tr:odd', $table).addClass("tr_odd");
   	$('tbody > tr:even', $table).addClass("tr_even");
}

// ---------------------------------------------------------------------------------------
// MAKE A TABLE SORTABLE
// ---------------------------------------------------------------------------------------
function makeSortableTable(table) {
  	
	var $table = $(table);
	$('th', $table).each(function(column) {
  		
  		if ($(this).is('.sort-alpha') || $(this).is('.sort-int') 
  			|| $(this).is('.sort-float') ) {
  		
  			var $header = $(this);
      		$(this).click(function() {

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
					$(this).removeClass('sorted-desc');
	    			$(this).removeClass('sorted-asc');
				});
				
     			var $sortHead = $table.find('th').filter(':nth-child(' + (column + 1) + ')');

	          	if (newDirection == 1) {$sortHead.addClass('sorted-asc'); $sortHead.removeClass('sorted-desc');} 
	          	else {$sortHead.addClass('sorted-desc'); $sortHead.removeClass('sorted-asc');}
        
        		
        		// add row striping back
        		if($table.is('.stripe_table')) {
					$('tbody > tr:odd', $table).addClass("tr_odd");
   					$('tbody > tr:even', $table).addClass("tr_even");
        		}
      		});
	}
  });
}

// ---------------------------------------------------------------------------------------
// FOLDABLE
// ---------------------------------------------------------------------------------------
function fold(foldable) {
	//alert("foldable clicked");
	if(!foldClose(foldable))
	foldOpen(foldable);
}
function foldClose(foldable) {
	var target_id = foldable.attr('id')+"_target";
   	//alert("target is: "+target_id);
	
	if(foldable.is('.fold-open')) {
		foldable.removeClass('fold-open');
		foldable.addClass('fold-close');
		$("#"+target_id).hide();
		
		return true;
 	}
 	return false;
}
function foldOpen(foldable) {
	var target_id = foldable.attr('id')+"_target";
   	//alert("target is: "+target_id);
	
	if(foldable.is('.fold-close')) {
		foldable.removeClass('fold-close');
		foldable.addClass('fold-open');
		$("#"+target_id).show();
		return true;
	}
	return false;
}

function openInformationPopup(url) {
	window.open(
		url,
		'MSDaPl_info',
		'height=500,width=700,left=10,top=10,resizable=yes,scrollbars=yes,toolbar=no,menubar=no,location=no,directories=no,status=yes');
}
</script>

<div class="mainContainer"> 

<yrcwww:notauthenticated>
<div id="login_header" align="center">
	<table BORDER="0" WIDTH="100%" CELLPADDING="0" CELLSPACING="0">
	<tr>
	<td width="33%">&nbsp;</td>
	<td width="34%" align="center"><img src="<yrcwww:link path='images/MSDAPL_logo_big.png'/>" /></td>
	<td width="33%" align="right" valign="middle">
	<div align="right" style="padding-right:20;color:white; font-size:8pt">
    	<html:link forward="authenticate">Login</html:link>
	</div>
	</div></td>
	</tr>
	</table>
</div>
</yrcwww:notauthenticated>
   
<yrcwww:authenticated>

<div id="top_header">

<%
	String home_class = ""; boolean home_menus = false;
	String account_class = ""; boolean account_menus = false;
	String admin_class = ""; boolean admin_menus = false;
	String docs_class = ""; boolean docs_menus = false;
 %>
 
 <logic:equal name="dir" scope="request" value="internal">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="project">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="protein">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="percolator">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="stats">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="peptideProphet">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="sequest">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="mascot">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="xtandem">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="yates">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="proteinfer">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="proteinProphet">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="compare">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="upload">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="search">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="docs">
 	<%docs_class = "current"; docs_menus = true;%>
 </logic:equal>
 
 <logic:equal name="dir" scope="request" value="account">
 	<%account_class = "current"; account_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="admin">
 	<%admin_class = "current"; admin_menus = true;%>
 </logic:equal>
		
<div id="main_menu">
	<ul>
		<li><a href="<yrcwww:link path='pages/internal/front.jsp'/>" title="Home" class="<%=home_class %>"><span>Home</span></a></li>
		<li><html:link  action="editInformation.do" styleClass="<%=account_class %>"><span>Account</span></html:link></li>
		<li><html:link action="manageGroups.do" styleClass="<%=admin_class %>"><span>Admin</span></html:link></li>
		<li><a href="<yrcwww:link path='pages/internal/docs/documentation.jsp'/>" 
		       title="DOCS" 
		       class="<%=docs_class %>">
		       <span>Docs</span></a></li>
	
	</ul>
	
	<div align="right" style="padding-right:20;color:white; font-size:8pt">
		<jsp:useBean id="user" class="org.yeastrc.www.user.User" scope="session"/>
    	Welcome <yrcwww:user attribute="firstname"/> <yrcwww:user attribute="lastname"/>!&nbsp;&nbsp;
    	<html:link action="logout.do">Logout</html:link>
	</div>
	
</div>

</div>

<div id="sub_header">
<div id="sub_menu">
	<ul>
   <%if(account_menus) { %>
    <li><html:link action="editInformation.do"><span>My Information</span></html:link>
    <li><html:link action="editPassword.do"><span>Password</span></html:link></li>
    <li><html:link action="editUsername.do"><span>Username</span></html:link></li>
   <%} %>
   <%if(home_menus) { %>
        <li><html:link action="newProject.do"><span>New Project</span></html:link></li>
        <yrcwww:member group="any">
   			<li><html:link action="uploadMSDataFormAction.do"><span>Upload Data</span></html:link></li>
   			<li><html:link action="listUploadJobs.do"><span>List Uploads</span></html:link></li>
   			<li><html:link action="availableFasta.do"><span>Available FASTA</span></html:link></li>
   			<li><a href="<yrcwww:link path='pages/admin/search/searchProjects.jsp'/>" title="Search Projects" class="<%=home_class %>"><span>Search Projects</span></a></li>
   		</yrcwww:member>
   <%} %>
   <%if(admin_menus) { %>
      <yrcwww:member group="administrators">
        <li><html:link action="manageGroups.do"><span>Manage Groups</span></html:link></li>
      </yrcwww:member>
      <yrcwww:member group="administrators">
        <li><html:link action="manageInstruments.do"><span>Instruments</span></html:link></li>
      </yrcwww:member>
      <yrcwww:member group="administrators">
        <li><html:link action="manageProteinInferences.do"><span>Manage Protein Inferences</span></html:link></li>
      </yrcwww:member>
   <%} %>
   <%if(docs_menus) { %>
   		<li><a href="<yrcwww:link path='pages/internal/docs/documentation.jsp'/>" title="Help Topics" class="<%=docs_class %>"><span>Help Topics</span></a></li>
   		<li><a href="<yrcwww:link path='pages/internal/docs/updates.jsp'/>" title="Updates" class="<%=docs_class %>"><span>Updates</span></a></li>
   <%} %>
  </ul>
</div>
</div>

<div id="history"><yrcwww:history/></div>
</yrcwww:authenticated>

<br><br>