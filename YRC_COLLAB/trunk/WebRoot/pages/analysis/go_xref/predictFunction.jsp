<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox title="Function Predicter prepreprepre-beta" centered="true" width="700" scheme="upload">

<html:form action="predictGOFunction" method="GET">

   <table>

    <tr>
     <td>Biological Process:</td>
     <td><input type="text" name="process" size="10" maxlength="255"></td>
    </tr>
    <tr>
     <td>Cellular Component:</td>
     <td><input type="text" name="component" size="10" maxlength="255"></td>
    </tr>

    <tr>
     <td colspan="2">Predicted GO distribution for 3rd term:</td>
    </tr>
    <tr>

     <td colspan="2" valign="top"><textarea name="functionList" cols="40" rows="20"></textarea></td>
    </tr>
   </table>

 
 <P><html:submit value="Submit"/>
 </CENTER>

</html:form>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>