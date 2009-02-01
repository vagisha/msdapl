
<%@page import="edu.uwpr.protinfer.database.dto.ProteinferInput.InputType"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>


<bean:define name="index" id="index" type="java.lang.Integer"/>

<logic:equal name="inputType" value="<%=InputType.SEARCH.name()%>">

<logic:iterate name="inputList" id="inputSummary">

	<div id="<bean:write name="inputSummary" property="searchId"/>">
    	<div style="background-color: #3D902A; color: white; font-weight: bold;" 
    		 class="foldable fold-open"
    		 id="foldable_search_<bean:write name="inputSummary" property="searchId"/>">
    		 Search ID: <bean:write name="inputSummary" property="searchId"/>
    	</div>
    	
    	
    	<div id="foldable_search_<bean:write name="inputSummary" property="searchId"/>_div">
    	
    	<div style="color: black;">
    		Search Program: 
    		<bean:write name="inputSummary" property="searchProgram" />&nbsp;
  			<bean:write name="inputSummary" property="searchProgramVersion" />
  			<br>
  			Search Database:
  			<bean:write name="inputSummary" property="searchDatabase" /> 
    	</div>
    	<br>
    	
    
		<table width="100%">
 		<logic:iterate name="inputSummary" property="inputFiles" id="inputFile" >
		<yrcwww:colorrow scheme="ms" repeat="true">
			<td WIDTH="20%" VALIGN="top"> 
				<input type="checkbox" checked="checked" 
					   id="toggle_search_<bean:write name="inputSummary" property="searchId"/>_file"
					   value="true"
					   name="inputFile[<%=index %>].isSelected" />
			</td>
			<td>
				<input type="hidden" value="<bean:write name="inputFile" property="inputId"/>" 
				       name="inputFile[<%=index %>].inputId"/>
				<input type="hidden" value="<bean:write name="inputFile" property="runName"/>" 
				       name="inputFile[<%=index %>].runName"/>
				<bean:write  name="inputFile" property="runName" />
			</td>
			<%index++; %>
		</yrcwww:colorrow>
		</logic:iterate>
 		</table>
		<div class="clickable toggle_selection" style="font-size: 7pt; color: #3D902A;" 
		     id="toggle_search_<bean:write name="inputSummary" property="searchId"/>">Deselect All</div>
		</div>
		</div>
		<br>
</logic:iterate>
</logic:equal>




<logic:equal name="inputType" value="<%=InputType.ANALYSIS.name()%>">

<logic:iterate name="inputList" id="inputSummary">

	<div id="<bean:write name="inputSummary" property="searchAnalysisId"/>">
    	<div style="background-color: #3D902A; color: white; font-weight: bold;" 
    		 class="foldable fold-open"
    		 id="analysis_<bean:write name="inputSummary" property="searchAnalysisId"/>">
    		Search Analysis ID: <bean:write name="inputSummary" property="searchAnalysisId"/>
    	</div>
    	
    	
    	<div id="foldable_analysis_<bean:write name="inputSummary" property="searchId"/>">
    	
    	<div style="color: black;">
    		Analysis Program: 
    		<bean:write name="inputSummary" property="analysisProgram" />&nbsp;
  			<bean:write name="inputSummary" property="analysisProgramVersion" />
  			<br>
  			Search Database:
  			<bean:write name="inputSummary" property="searchDatabase" /> 
    	</div>
    	<br>
    	
    
		<table width="100%">
 		<logic:iterate name="inputSummary" property="inputFiles" id="inputFile" >
		<yrcwww:colorrow scheme="ms" repeat="true">
			<td WIDTH="20%" VALIGN="top"> 
				<input type="checkbox" checked="checked" 
					   id="file_analysis_<bean:write name="inputSummary" property="searchAnalysisId"/>"
					   value="true"
					   name="inputFile[<%=index %>].isSelected" />
			</td>
			<td>
				<input type="hidden" value="<bean:write name="inputFile" property="inputId"/>" 
				       name="inputFile[<%=index %>].inputId"/>
				<input type="hidden" value="<bean:write name="inputFile" property="runName"/>" 
				       name="inputFile[<%=index %>].runName"/>
				<bean:write  name="inputFile" property="runName" />
			</td>
		</yrcwww:colorrow>
		</logic:iterate>
 		</table>
		<div class="clickable toggle_selection" style="font-size: 7pt; color: #3D902A;" 
		     id="analysis_<bean:write name="inputSummary" property="searchAnalysisId"/>">Deselect All</div>
		</div>
		</div>
		<br>
</logic:iterate>
</logic:equal>