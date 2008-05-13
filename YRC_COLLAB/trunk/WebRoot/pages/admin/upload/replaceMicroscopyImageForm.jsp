<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<logic:notPresent name="idCode" scope="request">
  <logic:redirect href="/yrc/pages/admin/upload/uploadMicroscopyForm2.jsp" />
</logic:notPresent>

<%@ include file="/includes/adminHeader.jsp" %>

<%@ include file="/includes/errors.jsp" %>


<yrcwww:contentbox title="Upload Microscopy Experiment: Replace Full Field Image" centered="true" width="700" scheme="upload">

<P align="left">Shown below is the image you're replacing.  To replace the image, use the form below the image.

<hr width="50%">

<p align="center">
<img border="0" src="/yrc/viewMicroscopyUploadImage.do?idCode=<bean:write name="idCode"/>" width="512" height="512"></p>


<html:form action="replaceMicroscopyImage" method="POST" enctype="multipart/form-data">
	<input type="hidden" name="idCode" value="<bean:write name="idCode" />">

 <CENTER>


	<b>Select new image file:</b>
		<html:file property="tiffImage" size="40" />
  
  
 
 <P><input type="button" onClick="javascript:history.back()" value="Cancel"> <html:submit value="Replace This Image"/>
 </CENTER>

</html:form>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>