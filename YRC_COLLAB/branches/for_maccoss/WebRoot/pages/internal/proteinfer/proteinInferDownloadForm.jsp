<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


  <html:form action="/downloadProtInferResults" method="post" styleId="downloadForm" target="_blank" >
  <html:hidden name="proteinInferFilterForm" property="pinferId" />
  <html:hidden name="proteinInferFilterForm" property="minPeptides" />
  <html:hidden name="proteinInferFilterForm" property="maxPeptides" />
  <html:hidden name="proteinInferFilterForm" property="minUniquePeptides" />
  <html:hidden name="proteinInferFilterForm" property="maxUniquePeptides" />
  <html:hidden name="proteinInferFilterForm" property="minCoverage" />
  <html:hidden name="proteinInferFilterForm" property="maxCoverage" />
  <html:hidden name="proteinInferFilterForm" property="minSpectrumMatches" />
  <html:hidden name="proteinInferFilterForm" property="maxSpectrumMatches" />
  <html:hidden name="proteinInferFilterForm" property="showAllProteins" />
  <html:hidden name="proteinInferFilterForm" property="excludeIndistinProteinGroups" />
  <html:hidden name="proteinInferFilterForm" property="validationStatusString" /> 	
  <html:hidden name="proteinInferFilterForm" property="accessionLike" />
  <html:hidden name="proteinInferFilterForm" property="descriptionLike" />
  <html:hidden name="proteinInferFilterForm" property="descriptionNotLike" />
  <div align="center">
   	<a href="" onclick="javascript:downloadResults();return false;" ><b>Download Results</b></a> &nbsp; 
   	<html:checkbox name="proteinInferFilterForm"property="printPeptides" >Include Peptides</html:checkbox>
   	<html:checkbox name="proteinInferFilterForm"property="collapseGroups" >Collapse Protein Groups</html:checkbox>
  </div>
  </html:form>
  
