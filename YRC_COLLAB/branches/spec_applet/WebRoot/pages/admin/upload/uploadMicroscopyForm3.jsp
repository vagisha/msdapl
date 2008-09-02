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
function imageSRPopUp(wl, ID, width, height) {
 var doc = "/yrc/viewMicroscopySRUploadImage.do?id=" + ID + "&idCode=" + wl + "&format=png&X=" + width + "&Y=" + height;
 var PIC_WINDOW;

 var winHeight = parseInt(height) + 25;
 var winWidth = parseInt(width) + 25;

 window.open(doc, wl + "_WINDOW_" + ID, "width=" + winWidth + ",height=" + winHeight + ",status=no,resizable=yes");
}

</SCRIPT>

<yrcwww:contentbox title="Upload Microscopy Experiment: Step 3 / 4" centered="true" width="800" scheme="upload">

<P align="left">Use this page to associate <b>selected regions</b> with the full field images shown below.  Please take care to associate selected
regions with all of the full field images in the same order for each image.

<P align="left">This step is optional.  When done, click "NEXT STEP" at the bottom of the page.

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
					<td><b>Emission Filter:</b></td>
					<td><b>
						<logic:notEmpty name="image" property="ffImage.EMFilter">
							<bean:write name="image" property="ffImage.EMFilter" />
						</logic:notEmpty>
						<logic:empty name="image" property="ffImage.EMFilter">
							Not Found.
						</logic:empty>
					</b></td>
				</tr>

			  <tr>
			   <td colspan="2"><br><br></td>
			  </tr>

			  <tr>
			   <td colspan="2" align="center"><font style="font-size:8pt;"><b>Selected Regions (half size):<br>Click on image for full size.</b></font></td>
			  </tr>
			  
			  <!-- Iterate over and show the selected regions so far uploaded for this full field image -->
			  <logic:notEmpty name="image" property="srImages">
				  <tr><td colspan="2">
				   <table border="0"><tr>
					 <logic:iterate id="srImage" name="image" property="srImages">
					  <td align="center">
					    <A HREF="javascript:imageSRPopUp('<bean:write name="image" property="idCode"/>', '<bean:write name="srImage" property="id"/>', '<bean:write name="srImage" property="image.width"/>', '<bean:write name="srImage" property="image.height"/>')">
						<img border="0" src="/yrc/viewMicroscopySRUploadImage.do?scale=0.5&idCode=<bean:write name="image" property="idCode"/>&id=<bean:write name="srImage" property="id" />"></A>
						 <br>
						<a href="/yrc/deleteMicroscopySRUploadImage.do?idCode=<bean:write name="image" property="idCode"/>&id=<bean:write name="srImage" property="id" />"><font style="color:#FF0000;font-size:8pt;"><b>[REMOVE]</b></font></a>
					  </td>
					 </logic:iterate>
				   </tr></table>
			  	  </td></tr>
			  </logic:notEmpty>



			  <!-- Show the form for uploading a new selected region for this full field image -->
			   <tr>
			    <td  align="center" valign="top" colspan="2">
				 <br><br>
				 		<font style="font-size:8pt;"><b>Upload Selected Region:</b><br>
				 		<html:form action="uploadSelectedRegion" method="POST" enctype="multipart/form-data">
						<input type="hidden" name="idCode" value="<bean:write name="image" property="idCode" />">
						<html:file property="srImage" size="20"/>
						 <html:submit value="Upload Image" />
				 </html:form>
				</td>
			   </tr>
			  
			  
			 </table>
			</td>

 		</tr>
 		
 		<tr>
 		 <td colspan="2"><hr width="75%"></td>
 		</tr>
 		
 	</logic:iterate>

 
  </table> 

	<p align="center"><b>NOTE:</b> Please be sure you have <b>SAVED YOUR IMAGES</b> above before clicking NEXT STEP.

	<p align="center"><input type="button" value="<-- PREVIOUS STEP" onClick="document.location.href='/yrc/pages/admin/upload/uploadMicroscopyForm2.jsp'">
					  <input type="button" value="NEXT STEP -->" onClick="document.location.href='/yrc/pages/admin/upload/uploadMicroscopyForm4.jsp'"></p>
	

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>