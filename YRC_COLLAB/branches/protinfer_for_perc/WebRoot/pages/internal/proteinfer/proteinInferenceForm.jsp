<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="proteinInferenceFormSearch">
	<logic:forward  name="newProteinInference" />
</logic:notPresent>
 
<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>


<link rel="stylesheet" href="/yrc/css/proteinfer.css" type="text/css" >
<script src="/yrc/js/jquery.ui-1.6rc2/jquery-1.2.6.js"></script>
<script src="/yrc/js/tooltip.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		tooltip();
		
		$("#searchopt").click(function() {
			$("#inputType_search").show();
			$("#inputType_analysis").hide();
		});
		
		$("#analysisopt").click(function() {
			$("#inputType_search").hide();
			$("#inputType_analysis").show();
		});
	});
</script>



<yrcwww:contentbox title="Protein Inference*" centered="true" width="750" scheme="ms">

 <CENTER>
 
 
 <logic:present name="proteinInferenceFormAnalysis">
 <div align="center" style="color:black;">
	<b>Select Input Type: </b> 
		<input type="radio" name="inputSelector" value="Search" checked id="searchopt" >Search
		<input type="radio" name="inputSelector" value="Search" id="analysisopt"> Analysis
 </div>
 <br>
 </logic:present>
 
 
 
 <div id="inputType_search">
  <html:form action="doProteinInference" method="post" styleId="form1">
  
  <html:hidden name="proteinInferenceFormSearch" property="projectId" />
  <html:hidden name="proteinInferenceFormSearch" property="inputSummary.searchId" />
  <html:hidden name="proteinInferenceFormSearch" property="inputTypeChar" />
  
  <TABLE CELLPADDING="4px" CELLSPACING="0px" width="90%">
  	<yrcwww:colorrow scheme="ms">
  		<td WIDTH="20%" VALIGN="top"><b>Search Program: </b></td>
  		<td WIDTH="20%" VALIGN="top">
  			<bean:write name="proteinInferenceFormSearch" property="inputSummary.searchProgram" />&nbsp;
  			<bean:write name="proteinInferenceFormSearch" property="inputSummary.searchProgramVersion" />
  		</td>
  	</yrcwww:colorrow>
  	<yrcwww:colorrow scheme="ms">
  		<td WIDTH="20%" VALIGN="top"><b>Search Database: </b></td>
  		<td WIDTH="25%" VALIGN="top"><bean:write name="proteinInferenceFormSearch" property="inputSummary.searchDatabase" /></td>
  	</yrcwww:colorrow>
   <yrcwww:colorrow scheme="ms">
    <td VALIGN="top" colspan="2">
    <B>Select Input Files:</B>
    
    </td>
   </yrcwww:colorrow>

	<logic:iterate name="proteinInferenceFormSearch" property="inputSummary.inputFiles" id="inputFile">
		<yrcwww:colorrow scheme="ms" repeat="true">
		<td WIDTH="20%" VALIGN="top"> 
			<html:checkbox name="inputFile" property="isSelected" value="true" indexed="true"/>
		</td>
		<td>
			<html:hidden name="inputFile" property="inputId" indexed="true" />
			<html:hidden name="inputFile" property="runName" indexed="true" />
			<bean:write  name="inputFile" property="runName" />
		</td>
		</yrcwww:colorrow>
		</logic:iterate>
   
   
    <yrcwww:colorrow scheme="ms">
    <td WIDTH="20%" VALIGN="top">
  		<B>Parameters:</B>
  		<html:hidden name="proteinInferenceFormSearch" property="programParams.programName" />
  	</td>
  	</yrcwww:colorrow>
  	
  	<logic:iterate name="proteinInferenceFormSearch" property="programParams.paramList" id="param">
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
   <td colspan="2" align="center">
   	<NOBR>
 		<html:submit value="Run Protein Inference" styleClass="button" />
 		<input type="button" class="button" onclick="javascript:onCancel(<bean:write name="projectId" />);" value="Cancel"/>
 	</NOBR>
 	<div style="font-size: 8pt;margin-top: 3px;">
 	*This protein inference program is based on the IDPicker algorithm published in:<br>
 	<i>Proteomic Parsimony through Bipartite Graph Analysis Improves Accuracy and Transparency.</i>
 	<br>
	Tabb <i>et. al.</i> <i>J Proteome Res.</i> 2007 Sep;6(9):3549-57
 	</div>
   </td>
   </yrcwww:colorrow>
  </TABLE>

 
</html:form>
</div>





<logic:present name="proteinInferenceFormAnalysis">
 <div id="inputType_analysis" style="display: none;">
  <html:form action="doProteinInference" method="post" styleId="form1">
  
  <html:hidden name="proteinInferenceFormAnalysis" property="projectId" />
  <html:hidden name="proteinInferenceFormAnalysis" property="inputSummary.searchId" />
  <html:hidden name="proteinInferenceFormAnalysis" property="inputSummary.searchAnalysisId" />
  <html:hidden name="proteinInferenceFormAnalysis" property="inputTypeChar" />
  
  <TABLE CELLPADDING="4px" CELLSPACING="0px" width="90%">
  	<yrcwww:colorrow scheme="ms" repeat="true">
  		<td WIDTH="20%" VALIGN="top"><b>Analysis Program: </b></td>
  		<td WIDTH="20%" VALIGN="top">
  			<bean:write name="proteinInferenceFormAnalysis" property="inputSummary.analysisProgram" />&nbsp;
  			<bean:write name="proteinInferenceFormAnalysis" property="inputSummary.analysisProgramVersion" />
  		</td>
  	</yrcwww:colorrow>
  	<yrcwww:colorrow scheme="ms">
  		<td WIDTH="20%" VALIGN="top"><b>Search Database: </b></td>
  		<td WIDTH="25%" VALIGN="top"><bean:write name="proteinInferenceFormAnalysis" property="inputSummary.searchDatabase" /></td>
  	</yrcwww:colorrow>
   <yrcwww:colorrow scheme="ms">
    <td VALIGN="top" colspan="2">
    <B>Select Input Files:</B>
    
    </td>
   </yrcwww:colorrow>

	<logic:iterate name="proteinInferenceFormAnalysis" property="inputSummary.inputFiles" id="inputFile">
		<yrcwww:colorrow scheme="ms" repeat="true">
		<td WIDTH="20%" VALIGN="top"> 
			<html:checkbox name="inputFile" property="isSelected" value="true" indexed="true"/>
		</td>
		<td>
			<html:hidden name="inputFile" property="inputId" indexed="true" />
			<html:hidden name="inputFile" property="runName" indexed="true" />
			<bean:write  name="inputFile" property="runName" />
		</td>
		</yrcwww:colorrow>
		</logic:iterate>
   
   
    <yrcwww:colorrow scheme="ms">
    <td WIDTH="20%" VALIGN="top">
  		<B>Parameters:</B>
  		<html:hidden name="proteinInferenceFormAnalysis" property="programParams.programName" />
  	</td>
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
   <td colspan="2" align="center">
   	<NOBR>
 		<html:submit value="Run Protein Inference" styleClass="button" />
 		<input type="button" class="button" onclick="javascript:onCancel(<bean:write name="projectId" />);" value="Cancel"/>
 	</NOBR>
 	<div style="font-size: 8pt;margin-top: 3px;">
 	*This protein inference program is based on the IDPicker algorithm published in:<br>
 	<i>Proteomic Parsimony through Bipartite Graph Analysis Improves Accuracy and Transparency.</i>
 	<br>
	Tabb <i>et. al.</i> <i>J Proteome Res.</i> 2007 Sep;6(9):3549-57
 	</div>
   </td>
   </yrcwww:colorrow>
  </TABLE>

 
</html:form>
</div>
</logic:present>




</CENTER>
</yrcwww:contentbox>

<script type="text/javascript">
	function onCancel(projectID) {
		document.location = "/yrc/viewProject.do?ID="+projectID;
	}
</script>


<%@ include file="/includes/footer.jsp" %>