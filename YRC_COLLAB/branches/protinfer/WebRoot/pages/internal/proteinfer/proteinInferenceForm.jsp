<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="proteinInferenceForm">
	<logic:forward name="editProject" />
</logic:notPresent>
 
<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox title="IDPicker" centered="true" width="750" scheme="ms">

 <CENTER>
  <html:form action="doProteinInference" method="post" styleId="form1">
  <bean:define name="proteinInferenceForm" property="idPickerParams" id="idPickerParam"></bean:define>
  <bean:define name="proteinInferenceForm" property="searchSummary" id="searchSummary"></bean:define>
    
  <TABLE CELLPADDING="no" CELLSPACING="0" width="75%">
   <tr>
    <td WIDTH="25%" VALIGN="top"><B>Select Input Files:</B></td>
    <td WIDTH="75%" VALIGN="top">
    	<html:hidden name="proteinInferenceForm" property="searchSummary.msSearchId" />
    </td>
   </tr>

   <logic:iterate name="proteinInferenceForm" property="searchSummary.runSearchList" id="runSearch">
   		<tr>
   			<td WIDTH="25%" VALIGN="top"> 
   				<html:checkbox name="runSearch" property="isSelected" value="true" indexed="true"/>
   			</td>
   			<td>
   				<html:hidden name="runSearch" property="runSearchId" indexed="true" />
   				<html:hidden name="runSearch" property="runName" indexed="true" />
   				<bean:write  name="runSearch" property="runName" />
   			</td>
   		</tr>
   </logic:iterate>
  </TABLE>

  <br>
  
  <TABLE CELLPADDING="no" CELLSPACING="0" width="75%">
  	<tr><td WIDTH="25%" VALIGN="top" colspan="2"><B>IDPicker Parameters:</B></td></tr>
   <tr>
    <td WIDTH="25%" VALIGN="top">Max. Absolute FDR:</td>
    <td WIDTH="25%" VALIGN="top"><html:text name="proteinInferenceForm" property="idPickerParams.maxAbsoluteFdr" /></td>
   </tr>
   
   <tr>
    <td WIDTH="25%" VALIGN="top">Max. Relative FDR:</td>
    <td WIDTH="25%" VALIGN="top"><html:text name="proteinInferenceForm" property="idPickerParams.maxRelativeFdr" /></td>
   </tr>
   
   <tr>
    <td WIDTH="25%" VALIGN="top">Decoy Ratio:</td>
    <td WIDTH="25%" VALIGN="top"><html:text name="proteinInferenceForm" property="idPickerParams.decoyRatio" /></td>
   </tr>
   
   <tr>
    <td WIDTH="25%" VALIGN="top">Decoy Prefix:</td>
    <td WIDTH="25%" VALIGN="top"><html:text name="proteinInferenceForm" property="idPickerParams.decoyPrefix" /></td>
   </tr>

   <tr>
    <td WIDTH="25%" VALIGN="top">Min. Distinct Peptides:</td>
    <td WIDTH="25%" VALIGN="top"><html:text name="proteinInferenceForm" property="idPickerParams.minDistinctPeptides" /></td>
   </tr>
   
   <tr>
    <td WIDTH="25%" VALIGN="top">Parsimony Analysis:</td>
    <td WIDTH="25%" VALIGN="top"><html:checkbox name="proteinInferenceForm" property="idPickerParams.doParsimonyAnalysis" value="true" /></td>
   </tr>
   
  </TABLE>
  <br>
  
 <NOBR>
 <html:submit value="Run IDPicker" styleClass="button" />
 <input type="button" class="button" onclick="javascript:onCancel(<bean:write name="searchSummary" property="msSearchId"/>);" value="Cancel"/>
 </NOBR>
 
 
</html:form>
</CENTER>
</yrcwww:contentbox>

<script type="text/javascript">
	function onCancel(projectID) {
		document.location = "/yrc/viewProject.do?ID="+projectID;
	}
</script>


<%@ include file="/includes/footer.jsp" %>