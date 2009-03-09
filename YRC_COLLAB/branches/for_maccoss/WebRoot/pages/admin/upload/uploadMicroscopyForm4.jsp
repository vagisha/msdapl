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

<yrcwww:contentbox title="Upload Microscopy Experiment: Step 4 / 4" centered="true" width="800" scheme="upload">

<P align="left">Use this page to associate <b>Gene Ontology (GO)</b> terms with the images from the experiment.  For each of the YFP and CFP (if applicable) channels, please
supply GO terms that you feel are represented by the results for that image.

<P align="left">This step is optional.  When done, click "SAVE EXPERIMENTAL DATA" at the bottom of the page.  None of the data you have uploaded for
this microscopy experiment will be saved to the database until you click this button.

<P align="center">Common GO terms:</P>
<P align="center">
	<table border="0">
		<tr>
			<td>Nucleus:</td><td>GO:0005634</td>
			<td width="25">&nbsp;</td>
			<td>Nucleolus:</td><td>GO:0005730</td>
		</tr>
		<tr>
			<td>Mitochondrion:</td><td>GO:0005739</td>
			<td width="25">&nbsp;</td>
			<td>Plasma membrane:</td><td>GO:0005886</td>
		</tr>
		<tr>
			<td>Kinetochore:</td><td>GO:0000776</td>
			<td width="25">&nbsp;</td>
			<td>Spindle pole body:</td><td>GO:0005816</td>
		</tr>
	</table>

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
				  <td colspan="2">&nbsp;<br><br></td>
			  </tr>

			  <tr>
			   <td colspan="2" align="center"><font style="font-size:8pt;"><b>Associated GO terms:</b></font></td>
			  </tr>

			  <logic:empty name="image" property="goTerms">
			  	<tr>
			  		<td colspan="2" align="center"><font style="color:#FF0000;font-size:8pt;"><b>None Assigned</b></font></td>
			  	</tr>
			  </logic:empty>
			  <logic:notEmpty name="image" property="goTerms">

			  	<logic:iterate id="goNode" name="image" property="goTerms">
			  		<tr>
			  			<td valign="top"><font style="color:#FF0000;font-size:8pt;"><b><yrcwww:goLink name="goNode" /></b></font></td>
			  			<td valign="top"><a href="/yrc/deleteMicroscopyGOAnnotation.do?idCode=<bean:write name="image" property="idCode" />&goAcc=<bean:write name="goNode" property="accession" />"><font style="color:#FF0000;font-size:8pt;"><b>[Delete]</b></font></a></td>
			  		</tr>
			  	</logic:iterate>
			  
			  
			  </logic:notEmpty>


			  <!-- Show the form for uploading a new GO Annotations for this full field image -->
			   <tr>
			    <td  align="center" valign="top" colspan="2">
				 <br><br>
					
				<font style="font-size:8pt;"><b>Assign GO term:</b><br>
				 <a href="http://www.godatabase.org/cgi-bin/amigo/go.cgi?search_constraint=terms&action=replace_tree" target="GO_WINDOW">Find GO Term (new window)</a><BR><BR>
				<html:form action="uploadMicroscopyGOAnnotation" method="POST" enctype="multipart/form-data">
					<input type="hidden" name="idCode" value="<bean:write name="image" property="idCode" />">
					GO term (eg: GO:0000037): <html:text property="goAcc" size="10" maxlength="20" />
					<html:submit value="Save" />
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


	<p align="center"><input type="button" value="<-- PREVIOUS STEP" onClick="document.location.href='/yrc/pages/admin/upload/uploadMicroscopyForm3.jsp'">
					  <input type="button" value="SAVE EXPERIMENTAL DATA" onClick="document.location.href='/yrc/saveMicroscopyExperiment.do'"></p>
	

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>