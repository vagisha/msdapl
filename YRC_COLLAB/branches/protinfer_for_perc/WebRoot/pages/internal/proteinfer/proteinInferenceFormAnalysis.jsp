<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<bean:define name="proteinInferenceFormAnalysis" property="inputSummary.inputGroupId" id="analysisIdInt" 
    			scope="request"/>
<bean:define name="proteinInferenceFormAnalysis" property="inputSummary.programName" id="analysisProgram" scope="request"/>

<script type="text/javascript">

var currentAnalysisIds = [<%=analysisIdInt%>];
var analysisProgram = '<%=analysisProgram%>';

$(document).ready(function(){
	$("#addAnalysesButton").click(function() {
		requestAnalysisList();
		return false;
	});
});

function requestAnalysisList() {
	
	var haveAnalyses = "";
	for (var i = 0; i < currentAnalysisIds.length; i+= 1) {
		if (i > 0)
			haveAnalyses += ",";
		haveAnalyses += currentAnalysisIds[i];
	}
	//alert(haveAnalyses);
	var winHeight = 500
	var winWidth = 700;
	var doc = "/yrc/listInputGroups.do?excludeInputGroups="+haveAnalyses+"&inputGenerator="+analysisProgram;
	//alert(doc);
	window.open(doc, "ADD_PROTINFER_INPUT", "width=" + winWidth + ",height=" + winHeight + ",status=no,resizable=yes,scrollbars=yes");
}

function addAnalyses(selectedAnalyses) {
	
	if(selectedAnalyses.length > 0) {
		var selected = selectedAnalyses.split(",");
		for(i = 0; i < selected.length; i++) {
			currentAnalysisIds[currentAnalysisIds.length] = selected[i];
			$("#analysisInputList").append("<br><div id="+selected[i]+"></div")
		}
		var currentInputCount = 0;
		for(i = 0; i < currentAnalysisIds.length; i++) {
			currentInputCount += $("input[id='toggle_analysis_"+currentAnalysisIds[i]+"_file']").length;
		}
		alert("current input count: "+currentInputCount);
		
		$.ajax({
  			type: "GET",
  			url: "getInputList.do",
  			dataType: "html",
  			data: "inputIds="+selectedAnalyses+"&inputType=A&index="+currentInputCount,
  			beforeSend: function(xhr) {
  							$.blockUI(); 
  						},
  			success: function(html) {
  			
  				$("#analysisInputList").append(html);
  				
  				// enable the file selection toggle and the link to fold the file list
  				for(i = 0; i < selected.length; i++) {
  					var id = "analysis_"+selected[0];
  					$("#foldable_"+id).click(function() {
						fold($(this));
					});
					
  					$("#toggle_"+id).click(function() {
						toggleSelection($(this));
					});
  				}
  			}
		});
	}
}
</script>    			





  <html:form action="doProteinInference" method="post" styleId="form1">
  
  <html:hidden name="proteinInferenceFormAnalysis" property="projectId" />
  <html:hidden name="proteinInferenceFormAnalysis" property="inputSummary.inputGroupId" />
  <html:hidden name="proteinInferenceFormAnalysis" property="inputTypeChar" />
  
  <TABLE CELLPADDING="4px" CELLSPACING="0px" width="90%">
   
    <yrcwww:colorrow scheme="ms">
    <td WIDTH="20%" VALIGN="top">
  		<B>Parameters:</B>
  		<html:hidden name="proteinInferenceFormAnalysis" property="programParams.programName" />
  	</td>
  	<td></td>
  	</yrcwww:colorrow>
  	
  	<logic:iterate name="proteinInferenceFormAnalysis" property="programParams.paramList" id="param">
    <yrcwww:colorrow scheme="ms" repeat="true">
    
    <td WIDTH="20%" VALIGN="top">
    	<span class="tooltip" title="<bean:write name="param" property="tooltip" />" style="cursor: pointer;">
    		<bean:write name="param" property="displayName" />
    	</span>
    	<logic:present name="param" property="notes">
    		<br>
    		<span style="color: red; font-size: 8pt;"><bean:write name="param" property="notes" /></span>
    	</logic:present>
    </td>
    
    <td WIDTH="20%" VALIGN="top">
    	<html:hidden name="param" property="name" indexed="true" />
    	<logic:equal name="param" property="type" value="text">
    		<html:text name="param" property="value" indexed="true" />
    	</logic:equal>
    	<logic:equal name="param" property="type" value="checkbox">
    		<html:checkbox name="param" property="value" value="true" indexed="true" />
    	</logic:equal>
    	<logic:equal name="param" property="type" value="radio">
    		<bean:define name="param" type="org.yeastrc.www.proteinfer.ProgramParameters.Param" id="progParam"/>
    		<!-- cannot use nested logic:iterate with indexed properties -->
    		<%for(String option: progParam.getOptions()) { %>
    			<html:radio name="param" property="value" value="<%=option%>" indexed="true"><%=option%></html:radio><br>
    		<%} %>
    	</logic:equal>
    </td>
    
   	</yrcwww:colorrow>
   </logic:iterate>
   
   
   <yrcwww:colorrow scheme="ms">
    <td VALIGN="top" colspan="2">
    <B>Select Input Files:</B>
    
    </td>
   </yrcwww:colorrow>
   
   <yrcwww:colorrow scheme="ms" repeat="true">
   
    <td VALIGN="top" colspan="2">
    	<div style="border: solid 1px #3D902A; padding-bottom: 5px;">
    	
    	<div id="analysisInputList">
    	
    	<div id="analysis_<bean:write name="proteinInferenceFormAnalysis" property="inputSummary.inputGroupId"/>">
    	<div style="background-color: #3D902A; color: white; font-weight: bold;" 
    		 class="foldable fold-open"
    		 id="foldable_analysis_<bean:write name="proteinInferenceFormAnalysis" property="inputSummary.inputGroupId"/>">
    		Analysis ID: <bean:write name="proteinInferenceFormAnalysis" property="inputSummary.inputGroupId"/>
    	</div>
    	
    	
    	<div id="foldable_analysis_<bean:write name="proteinInferenceFormAnalysis" property="inputSummary.inputGroupId"/>_div">
    	
    	<div style="color: black;">
    		Analysis Program: 
    		<bean:write name="proteinInferenceFormAnalysis" property="inputSummary.programName" />&nbsp;
  			<bean:write name="proteinInferenceFormAnalysis" property="inputSummary.programVersion" />
  			<br>
  			Search Database:
  			<bean:write name="proteinInferenceFormAnalysis" property="inputSummary.searchDatabase" /> 
    	</div>
    	<br>
    	
    	<% 
    		String myAnalysisId = String.valueOf(analysisIdInt);
    		String toggleIdDiv = "toggle_analysis_"+myAnalysisId;
    		String checkboxId = toggleIdDiv+"_file";
    	%>
    
		<table width="100%">
 		<logic:iterate name="proteinInferenceFormAnalysis" property="inputSummary.inputFiles" id="inputFile" >
		<yrcwww:colorrow scheme="ms" repeat="true">
			<td WIDTH="20%" VALIGN="top"> 
				<html:checkbox name="inputFile" property="isSelected" value="true" indexed="true" 
				styleId="<%=checkboxId%>" />
			</td>
			<td>
				<html:hidden name="inputFile" property="inputId" indexed="true" />
				<html:hidden name="inputFile" property="runName" indexed="true" />
				<bean:write  name="inputFile" property="runName" />
			</td>
		</yrcwww:colorrow>
		</logic:iterate>
 		</table>
		<div class="clickable toggle_selection" style="font-size: 7pt; color: #3D902A;" 
		     id="<%=toggleIdDiv%>">Deselect All</div>
		</div>
		</div>
		</div>
		<center><button class="button" id="addAnalysesButton" >Add</button></center>
	</div>
    </td>
   </yrcwww:colorrow>
   
   <yrcwww:colorrow scheme="ms" repeat="true">
    	<td colspan="2" align="center">Comments<br>
    	<html:textarea name="proteinInferenceFormAnalysis" property="comments" rows="3" cols="50"/>
    	</td>
    </yrcwww:colorrow>
    
   <yrcwww:colorrow scheme="ms" repeat="true">
   <td colspan="2" align="center">
   	<NOBR>
 		<html:submit value="Run Protein Inference" styleClass="button" />
 		<input type="button" class="button" onclick="javascript:onCancel(<bean:write name="projectId" />);" value="Cancel"/>
 	</NOBR>
   </td>
   </yrcwww:colorrow>
  </TABLE>

 
</html:form>
