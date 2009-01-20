<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>



<logic:notPresent name="proteinInferenceForm">
	<logic:forward  name="percolatorProteinInference" />
</logic:notPresent>
 
<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>



<yrcwww:contentbox title="Protein Inference*" centered="true" width="750" scheme="ms">

 <CENTER>
   <html:form action="doPercolatorProtInfer" method="post" styleId="form1">
   <html:hidden name="proteinInferenceForm" property="projectId" />
  
  <bean:define name="proteinInferenceForm" property="inputSummary" id="inputSummary"></bean:define>
    
  <TABLE CELLPADDING="4px" CELLSPACING="0px" width="90%">
  	<yrcwww:colorrow scheme="ms">
  		<td WIDTH="20%" VALIGN="top"><b>Search Program: </b></td>
  		<td WIDTH="20%" VALIGN="top">
  		<bean:write name="inputSummary" property="searchProgram" />&nbsp;
  		<logic:present name="inputSummary" property="searchProgramVersion">
  		version <bean:write name="inputSummary" property="searchProgramVersion" />
  		</logic:present>
  		</td>
  	</yrcwww:colorrow>
  	<yrcwww:colorrow scheme="ms">
  		<td WIDTH="20%" VALIGN="top"><b>Search Database: </b></td>
  		<td WIDTH="25%" VALIGN="top"><bean:write name="inputSummary" property="searchDatabase" /></td>
  	</yrcwww:colorrow>
  	
  	<logic:present name="inputSummary" property="analysisProgram">
  		<yrcwww:colorrow scheme="ms">
  		<td WIDTH="20%" VALIGN="top"><b>Analysis Program: </b></td>
  		<td WIDTH="20%" VALIGN="top">
  		<bean:write name="inputSummary" property="analysisProgram" />&nbsp;
  		<logic:present name="inputSummary" property="analysisProgramVersion">
  			version <bean:write name="inputSummary" property="analysisProgramVersion" />
  		</logic:present>
  		</td>
  		</yrcwww:colorrow>
  	</logic:present>
  	
   <yrcwww:colorrow scheme="ms">
    <td VALIGN="top" colspan="2">
    <B>Select Input Files:</B>
    <html:hidden name="proteinInferenceForm" property="inputSummary.searchId" />
    <html:hidden name="proteinInferenceForm" property="inputSummary.searchAnalysisId" />
    </td>
   </yrcwww:colorrow>

	<logic:iterate name="proteinInferenceForm" property="inputSummary.inputFiles" id="inputFile">
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
   
   
   <bean:define name="proteinInferenceForm" property="programParams" id="programParams"></bean:define>
   
    <yrcwww:colorrow scheme="ms">
    <td WIDTH="20%" VALIGN="top" colspan="2">
  		<B><bean:write name="programParams" property="programDisplayName" /> Parameters:</B>
  		<html:hidden name="proteinInferenceForm" property="programParams.programName" />
  	</td>
  	</yrcwww:colorrow>
  	
  	<logic:iterate name="proteinInferenceForm" property="programParams.paramList" id="param">
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
    			<html:radio name="param" property="value" value="<%=option%>" indexed="true"><%=option%></html:radio>
    		<%} %>
    	</logic:equal>
    </td>
    
   	</yrcwww:colorrow>
   </logic:iterate>
   
   <yrcwww:colorrow scheme="ms">
   <td colspan="2" align="center">
   	<NOBR>
 		<html:submit value="Run IDPicker" styleClass="button" />
 		<input type="button" class="button" onclick="javascript:onCancel();" value="Cancel"/>
 	</NOBR>
 	<div style="font-size: 8pt;margin-top: 3px;">
 	<i>*Proteomic Parsimony through Bipartite Graph Analysis Improves Accuracy and Transparency.</i>
 	<br>
	Tabb <i>et. al.</i> <i>J Proteome Res.</i> 2007 Sep;6(9):3549-57
 	</div>
   </td>
   </yrcwww:colorrow>
  </TABLE>

 
</html:form>
</CENTER>
</yrcwww:contentbox>

<script type="text/javascript">
	function onCancel(projectID) {
		document.location = "/yrc/viewAllSearches.do";
	}
</script>


<%@ include file="/includes/footer.jsp" %>