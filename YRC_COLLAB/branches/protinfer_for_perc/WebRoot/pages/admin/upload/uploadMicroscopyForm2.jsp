<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<logic:notPresent name="ffImages" scope="session">
  <logic:redirect href="/yrc/uploadMicroscopyFormAction.do" />
</logic:notPresent>

<%@ include file="/includes/adminHeader.jsp" %>

<%@ include file="/includes/errors.jsp" %>
<logic:present name="saved" scope="request">
 <center>
 <hr width="50%">
  <B><font color="red">Experiment was successfully saved.</font></B>
 <hr width="50%">
 </center>
</logic:present>

<SCRIPT LANGUAGE="JavaScript">

function imagePopUp(type, ID, width, height) {
 var doc = "/yrc/viewMicroscopyUploadImage.do?fullsize=true&idCode=" + ID + "&type=" + type + "&format=png&X=" + width + "&Y=" + height;
 var PIC_WINDOW;

 var winHeight = parseInt(height) + 25;
 var winWidth = parseInt(width) + 25;

 if(type == "FF") {
    winHeight = winHeight + 25;
 }

 window.open(doc, type + "_WINDOW_" + ID, "width=" + winWidth + ",height=" + winHeight + ",status=no,resizable=yes");
}

</SCRIPT>

<yrcwww:contentbox title="Upload Microscopy Experiment: Step 2 / 4" centered="true" width="800" scheme="upload">

<P align="left">Shown below are the images and data found in the R3D and log files.  You may replace any of these images
with a customized TIFF by clicking the links below.  Also, you may add in comments for each image.

<hr width="50%">

 <CENTER>
  <table border="0" width="95%">

	<tr>
		<td align="center" valign="top"><font style="font-size:8pt;"><b>Images Found (half size)<br>Click on image to view full size.</b></font></td>
		<td align="center" valign="top"><font style="font-size:8pt;"><b>Image Information:</b></font></td>
	</tr>
 
 	<logic:iterate id="image" name="ffImages" scope="session">
 		<tr>
 			<td valign="top" align="center" width="50%">
 				<A HREF="javascript:imagePopUp('FF', '<bean:write name="image" property="idCode"/>', '512', '512')">
	 				<img border="0" src="/yrc/viewMicroscopyUploadImage.do?idCode=<bean:write name="image" property="idCode"/>" width="256" height="256"></A>
	 				
	 			<br>
	 			
	 			<font style="font-size:8pt;"><b>[<a href="/yrc/replaceMicroscopyImageForm.do?idCode=<bean:write name="image" property="idCode"/>"><font style="color:#FF0000;">REPLACE IMAGE</font></a>]</b></font>
	 				
	 			<br><br>
	 		</td>

			<td valign="top" align="center" width="50%">
			 <table width="95%" border="0">


				<logic:notEqual name="image" property="ffImage.EMFilter" value="POL">
					<logic:notEqual name="image" property="ffImage.EMFilter" value="merged">
					
						<logic:equal name="image" property="ffImage.isFRET" value="true">

							<tr>
								<td><b>Donor Protein:</b></td>
								<td><b>

									<logic:notEmpty name="image" property="ffImage.excitedProtein">
										<yrcwww:proteinLink name="image" property="ffImage.excitedProtein" />
									</logic:notEmpty>

									<logic:empty name="image" property="ffImage.excitedProtein">
										Not Found.
									</logic:empty>

								</b></td>
							</tr>
							<tr>
								<td><b>Donor Tag:</b></td>
								<td><b>

									<logic:notEmpty name="image" property="ffImage.excitedTag">
										<bean:write name="image" property="ffImage.excitedTag" />
									</logic:notEmpty>
									<logic:empty name="image" property="ffImage.excitedTag">
										Not Found.
									</logic:empty>

								</b></td>
							</tr>

							<tr>
								<td><b>Acceptor Protein:</b></td>
								<td><b>

									<logic:notEmpty name="image" property="ffImage.emissionProtein">
										<yrcwww:proteinLink name="image" property="ffImage.emissionProtein" />
									</logic:notEmpty>

									<logic:empty name="image" property="ffImage.emissionProtein">
										None.
									</logic:empty>

								</b></td>
							</tr>
							<tr>
								<td><b>Acceptor Tag:</b></td>
								<td><b>

									<logic:notEmpty name="image" property="ffImage.emissionTag">
										<bean:write name="image" property="ffImage.emissionTag" />
									</logic:notEmpty>
									<logic:empty name="image" property="ffImage.emissionTag">
										None.
									</logic:empty>

								</b></td>
							</tr>

						</logic:equal>
					
						<logic:notEqual name="image" property="ffImage.isFRET" value="true">

							<tr>
								<td><b>Visible Protein:</b></td>
								<td><b>

									<logic:notEmpty name="image" property="ffImage.emissionProtein">
										<yrcwww:proteinLink name="image" property="ffImage.emissionProtein" />
									</logic:notEmpty>

									<logic:empty name="image" property="ffImage.emissionProtein">
										None.
									</logic:empty>

								</b></td>
							</tr>
							<tr>
								<td><b>Tag Used:</b></td>
								<td><b>

									<logic:notEmpty name="image" property="ffImage.emissionTag">
										<bean:write name="image" property="ffImage.emissionTag" />
									</logic:notEmpty>
									<logic:empty name="image" property="ffImage.emissionTag">
										None.
									</logic:empty>

								</b></td>
							</tr>

						</logic:notEqual>

					</logic:notEqual>
				</logic:notEqual>
			
			  <tr>
				  <td valign="top">Intensity:</td>
				  <td valign="top">

				   <table>
				    <tr>
				     <td>Min:</td>
				     <td><bean:write name="image" property="ffImage.intensityMin"/></td>
				    </tr>
				    <tr>
				     <td>Max:</td>
				     <td><bean:write name="image" property="ffImage.intensityMax"/></td>
				    </tr>
				    <tr>
				     <td>Mean:</td>
				     <td><bean:write name="image" property="ffImage.intensityAvg"/></td>
				    </tr>
				   </table>

				  </td>
			  </tr>

			  <tr>
				  <td>Exposure Time:</td>
				  <td><bean:write name="image" property="ffImage.exposureTime"/></td>
			  </tr>

			  <tr>
				  <td>EM Filter:</td>
				  <td><bean:write name="image" property="ffImage.EMFilter"/></td>
			  </tr>
			  
				  <tr>
				   <td valign="top" colspan="2">
					<html:form action="saveORFAndTag" method="POST">
						<input type="hidden" name="idCode" value="<bean:write name="image" property="idCode" />">
						
						<table width="100%" border="0">
						 
						 <tr>
						  <td align="center">
						   <font style="font-size:8pt;"><u>Comments:</u>
						    <logic:notEmpty name="image" property="ffImage.comments">
						     
						      <p align="left">Currently: <b><bean:write name="image" property="ffImage.comments" /></b></p><br>
						    </logic:notEmpty>
						   </font><br>
						   
						   <html:textarea property="comments" rows="4" cols="30" />
						   
						  </td>
						 </tr>
						</table>
						
						<p align="center"><html:submit value="Save Comments"/></p>
						<br><br>
					</html:form>
				   </td>
				  </tr>

			 </table>
			</td>

 		</tr>
 	</logic:iterate>
 
  </table> 


	<p align="center"><input type="button" value="GO TO NEXT STEP" onClick="document.location.href='/yrc/uploadMicroscopyStep3.do'"></p>
	

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>