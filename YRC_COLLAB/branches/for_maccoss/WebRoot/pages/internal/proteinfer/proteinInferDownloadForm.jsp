<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


  <html:form action="/downloadProtInferResults" method="post" styleId="downloadForm" target="_blank" >
  <table>
  <tr><td>
  <html:hidden name="proteinInferFilterForm" property="pinferId" />
  <html:text name="proteinInferFilterForm" property="minPeptides" />
  <html:text name="proteinInferFilterForm" property="minUniquePeptides" />
  <html:text name="proteinInferFilterForm" property="minCoverage" />
  <html:text name="proteinInferFilterForm" property="minSpectrumMatches" />
  <html:text name="proteinInferFilterForm" property="showAllProteins" />
  <html:text name="proteinInferFilterForm" property="validationStatus" /> 	
  <html:text name="proteinInferFilterForm" property="accessionLike" />
  <html:text name="proteinInferFilterForm" property="descriptionLike" />
  </td></tr></table>
  
</html:form>
