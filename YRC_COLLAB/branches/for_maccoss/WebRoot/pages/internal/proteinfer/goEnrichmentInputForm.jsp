<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


  <html:form action="/goEnrichment" method="post" styleId="goEnrichmentForm" >
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
  <html:hidden name="proteinInferFilterForm" property="validationStatusString" /> 	
  <html:hidden name="proteinInferFilterForm" property="accessionLike" />
  <html:hidden name="proteinInferFilterForm" property="descriptionLike" />
  <div align="center">
   	<a href="" onclick="javascript:doGoEnrichmentAnalysis();return false;" >GO Enrichment</a> &nbsp; 
  </div>
  </html:form>
  
