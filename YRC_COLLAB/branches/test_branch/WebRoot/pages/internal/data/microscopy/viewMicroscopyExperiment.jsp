<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:empty name="experiment">
  <logic:forward name="viewMicroscopyExperiment" />
</logic:empty>

<%@ include file="/includes/header.jsp" %>


<SCRIPT LANGUAGE="JavaScript">

function imagePopUp(type, ID, width, height) {
 var doc = "/yrc/viewMicroscopyImage.do?id=" + ID + "&type=" + type + "&format=png&X=" + width + "&Y=" + height;
 var PIC_WINDOW;

 var winHeight = parseInt(height) + 25;
 var winWidth = parseInt(width) + 25;

 if(type == "FF") {
    winHeight = winHeight + 25;
 }

 window.open(doc, type + "_WINDOW_" + ID, "width=" + winWidth + ",height=" + winHeight + ",status=no,resizable=yes");
}

</SCRIPT>

<yrcwww:contentbox title="View Localization Experiment" centered="true" width="800" scheme="localization">

 <CENTER>

 <TABLE CELLPADDING="no" CELLSPACING="0" width="500">
 
 <!--
  <yrcwww:colorrow scheme="localization">
    <TD valign="top" width="30%">Localized ORF:</TD>
    <TD valign="top" width="70%">
     <html:link href="/yrc/viewProtein.do" paramId="id" paramName="experiment" paramProperty="bait1.id">
     <bean:write name="experiment" property="bait1.listing"/></html:link>
     
     <logic:notEmpty name="experiment" property="tag1">
      (<bean:write name="experiment" property="tag1"/>)
     </logic:notEmpty>
     
    </TD>
  </yrcwww:colorrow>

<logic:notEmpty name="experiment" property="bait2">
  <yrcwww:colorrow scheme="localization">
    <TD valign="top" width="30%">Co-localized ORF:</TD>
    <TD valign="top" width="70%">
     <html:link href="/yrc/viewProtein.do" paramId="id" paramName="experiment" paramProperty="bait2.id">
     <bean:write name="experiment" property="bait2.listing"/></html:link>

     <logic:notEmpty name="experiment" property="tag2">
      (<bean:write name="experiment" property="tag2"/>)
     </logic:notEmpty>

    </TD>
  </yrcwww:colorrow>
</logic:notEmpty>
-->

  <yrcwww:colorrow scheme="localization">
   <TD valign="top" width="30%">Project:</TD>
   <TD valign="top" width="70%">
     <html:link href="/yrc/viewProject.do" paramId="ID" paramName="project" paramProperty="ID">
     <bean:write name="project" property="title"/></html:link></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="localization">
   <TD valign="top" width="30%">Date:</TD>
   <TD valign="top" width="70%"><bean:write name="experiment" property="experimentDate"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="localization">


   <TD valign="top" width="30%">Comments:
   
   		<yrcwww:member group="Microscopy">
   			<logic:empty name="experiment" property="comments">
   				<br><font style="font-size:8pt;">[<a href="javascript:showEditBox()">Add Comments</a>]<font>
   			</logic:empty>
   			<logic:notEmpty name="experiment" property="comments">
   				<br><font style="font-size:8pt;">[<a href="javascript:showEditBox()">Edit Comments</a>]<font>
			</logic:notEmpty>   
		</yrcwww:member>
		
   </TD>
   
   
   <TD valign="top" width="70%">
   		
   		<div id="comments_text">
	   		<logic:empty name="experiment" property="comments">
	   			None entered.
	   		</logic:empty>
	   		<logic:notEmpty name="experiment" property="comments">
	   			<bean:write name="experiment" property="comments"/>
			</logic:notEmpty>
		</div>
		<div id="comments_edit_box" style="display:none;">
			<html:form action="saveMicroscopyComments" method="post">
				<input type="hidden" name="id" value="<bean:write name="experiment" property="id" />">
				<textarea name="comments" rows="5" cols="30"><bean:write name="experiment" property="comments" /></textarea><br>
				<input type="button" value="Cancel Edit" onClick="javascript:hideEditBox()">
				<input type="submit" value="Save Comments">
			</html:form>
		</div>
   		
   		<script language="JavaScript">
   			var editbox=document.all? document.all["comments_edit_box"] : document.getElementById? document.getElementById("comments_edit_box") : ""
   			var commentstext=document.all? document.all["comments_text"] : document.getElementById? document.getElementById("comments_text") : ""

   			function showEditBox() {
   				commentstext.style.display = "none";
   				editbox.style.display = "inline";
   			}
   			function hideEditBox() {
   			   	editbox.style.display = "none";
   				commentstext.style.display = "inline";
   			}
   		</script>
   		
   </TD>


  </yrcwww:colorrow>

 </TABLE>


<TABLE BORDER="0" CELLPADDING="no" CELLSPACING="0" WIDTH="100%">
	<yrcwww:colorrow scheme="localization">
		<TD VALIGN="top" WIDTH="100%" COLSPAN="3" ALIGN="CENTER"><FONT STYLE="font-size: 12pt;"><BR><B>IMAGES:</B><BR><FONT STYLE="font-size: 10pt;">Click on the image to view full size.</FONT></TD>
	</yrcwww:colorrow>
	<yrcwww:colorrow scheme="localization">
		<TD VALIGN="top" ALIGN="CENTER" WIDTH="30%"><FONT STYLE="font-size: 10pt;"><U>Image Info.</U></FONT></TD>
  		<TD VALIGN="top" ALIGN="CENTER" WIDTH="40%"><FONT STYLE="font-size: 10pt;"><U>Full Field</U></FONT></TD>
  		<TD VALIGN="top" ALIGN="CENTER"WIDTH="30%"><FONT STYLE="font-size: 10pt;"><U>Selected Regions</U></FONT></TD>
	</yrcwww:colorrow>

	<logic:iterate id="ff" name="experiment" property="fullFieldImages">
		<yrcwww:colorrow scheme="localization">
			<TD VALIGN="top" ALIGN="LEFT" WIDTH="30%">



			 <table width="95%" border="0">


				<logic:notEmpty name="ff" property="EMFilter">
				<logic:notEqual name="ff" property="EMFilter" value="POL">
					<logic:notEqual name="ff" property="EMFilter" value="merged">
					
						<logic:equal name="ff" property="isFRET" value="true">

							<tr>
								<td><font style="font-size:8pt;"><b>Donor Protein:</b></font></td>
								<td><font style="font-size:8pt;"><b>

									<logic:notEmpty name="ff" property="excitedProtein">
										<yrcwww:proteinLink name="ff" property="excitedProtein" />
									</logic:notEmpty>

									<logic:empty name="ff" property="excitedProtein">
										Not Found.
									</logic:empty>

								</b></font></td>
							</tr>
							<tr>
								<td><font style="font-size:8pt;"><b>Donor Tag:</b></font></td>
								<td><font style="font-size:8pt;"><b>

									<logic:notEmpty name="ff" property="excitedTag">
										<bean:write name="ff" property="excitedTag" />
									</logic:notEmpty>
									<logic:empty name="ff" property="excitedTag">
										Not Found.
									</logic:empty>

								</b></font></td>
							</tr>

							<tr>
								<td><font style="font-size:8pt;"><b>Acceptor Protein:</b></font></td>
								<td><font style="font-size:8pt;"><b>

									<logic:notEmpty name="ff" property="emissionProtein">
										<yrcwww:proteinLink name="ff" property="emissionProtein" />
									</logic:notEmpty>

									<logic:empty name="ff" property="emissionProtein">
										None.
									</logic:empty>

								</b></font></td>
							</tr>
							<tr>
								<td><font style="font-size:8pt;"><b>Acceptor Tag:</b></font></td>
								<td><font style="font-size:8pt;"><b>

									<logic:notEmpty name="ff" property="emissionTag">
										<bean:write name="ff" property="emissionTag" />
									</logic:notEmpty>
									<logic:empty name="ff" property="emissionTag">
										None.
									</logic:empty>

								</b></font></td>
							</tr>

						</logic:equal>
					
						<logic:notEqual name="ff" property="isFRET" value="true">

							<tr>
								<td><font style="font-size:8pt;"><b>Visible Protein:</b></font></td>
								<td><font style="font-size:8pt;"><b>

									<logic:notEmpty name="ff" property="emissionProtein">
										<yrcwww:proteinLink name="ff" property="emissionProtein" />
									</logic:notEmpty>

									<logic:empty name="ff" property="emissionProtein">
										None.
									</logic:empty>

								</b></font></td>
							</tr>
							<tr>
								<td><font style="font-size:8pt;"><b>Tag Used:</b></font></td>
								<td><font style="font-size:8pt;"><b>

									<logic:notEmpty name="ff" property="emissionTag">
										<bean:write name="ff" property="emissionTag" />
									</logic:notEmpty>
									<logic:empty name="ff" property="emissionTag">
										None.
									</logic:empty>

								</b></font></td>
							</tr>

						</logic:notEqual>

					</logic:notEqual>
				</logic:notEqual>
				</logic:notEmpty>
			
			  <tr>
				  <td><font style="font-size:8pt;">EM Filter:</font></td>
				  <td><font style="font-size:8pt;">
				  
					  <logic:notEmpty name="ff" property="EMFilter">
							<bean:write name="ff" property="EMFilter"/>
					  </logic:notEmpty>
					  <logic:empty name="ff" property="EMFilter">
							MERGED
					  </logic:empty>
				  
				  </font></td>
			  </tr>

				<logic:equal name="ff" property="isFRET" value="true">
				<logic:notEmpty name="ff" property="EXFilter">
				  <tr>
					  <td><font style="font-size:8pt;">EX Filter:</font></td>
					  <td><font style="font-size:8pt;">

						  <logic:notEmpty name="ff" property="EXFilter">
								<bean:write name="ff" property="EXFilter"/>
						  </logic:notEmpty>
						  <logic:empty name="ff" property="EXFilter">
								N/A
						  </logic:empty>

					  </font></td>
				  </tr>
			  	</logic:notEmpty>
			  	</logic:equal>

			  <logic:notEmpty name="ff" property="exposureTime">
				  <tr>
					  <td><font style="font-size:8pt;">Exposure Time:</font></td>
					  <td><font style="font-size:8pt;"><bean:write name="ff" property="exposureTime"/></font></td>
				  </tr>
			  </logic:notEmpty>
			  
			  <logic:notEmpty name="ff" property="intensityMin">
				  <tr>
					  <td valign="top"><font style="font-size:8pt;">Intensity:</font></td>
					  <td valign="top">

					   <table>
						<tr>
						 <td><font style="font-size:8pt;">Min:</font></td>
						 <td><font style="font-size:8pt;"><bean:write name="ff" property="intensityMin"/></font></td>
						</tr>
						<tr>
						 <td><font style="font-size:8pt;">Max:</font></td>
						 <td><font style="font-size:8pt;"><bean:write name="ff" property="intensityMax"/></font></td>
						</tr>
						<tr>
						 <td><font style="font-size:8pt;">Mean:</font></td>
						 <td><font style="font-size:8pt;"><bean:write name="ff" property="intensityAvg"/></font></td>
						</tr>
					   </table>

					  </td>
				  </tr>
				</logic:notEmpty>


				<logic:notEmpty name="ff" property="GONodes">
					<tr><td colspan="2"><hr width="50%"></td></tr>
					<tr>
						<td valign="top"><font style="font-size:8pt;">GO Terms:</font></td>
						<td><font style="font-size:8pt;">

							<logic:notEmpty name="ff" property="GONodes">
								<logic:iterate id="node" name="ff" property="GONodes">
									<yrcwww:goLink name="node" /><br>
								</logic:iterate>
							</logic:notEmpty>
							<logic:empty name="ff" property="GONodes">
								None.
							</logic:empty>

						</font></td>
					</tr>
				</logic:notEmpty>
				
				<logic:notEmpty name="ff" property="comments">
					<tr><td colspan="2"><hr width="50%"></td></tr>
					<tr>
						<td valign="top"><font style="font-size:8pt;">Comments:&nbsp;&nbsp;</font></td>
						<td><font style="font-size:8pt;"><bean:write name="ff" property="comments" /></font></td>
					</tr>
				</logic:notEmpty>



	  </table>




			</TD>


			<TD VALIGN="top" ALIGN="CENTER" WIDTH="40%">

				<A HREF="javascript:imagePopUp('FF', '<bean:write name="ff" property="id"/>', '512', '512')">
				<IMG BORDER="0" SRC="/yrc/viewFullFieldThumbnail.do?id=<bean:write name="ff" property="id"/>"></A>

	
				<!--
				<br>Filter: 
				 <logic:empty name="ff" property="EMFilter">MERGED</logic:empty>
				 <logic:notEmpty name="ff"property="EMFilter">
					<bean:write name="ff" property="EMFilter"/>
				 </logic:notEmpty>
				<logic:equal name="ff" property="EMFilter" value="YFP">
				 <br>Min. Intensity: <bean:write name="ff" property="intensityMin"/>
				 <br>Max. Intensity:<bean:write name="ff" property="intensityMax"/>
				 <br>Exposure: <bean:write name="ff" property="exposureTime"/> 
				</logic:equal>
				<logic:equal name="ff" property="EMFilter" value="CFP">
				 <br>Min. Intensity: <bean:write name="ff" property="intensityMin"/>
				 <br>Max. Intensity:<bean:write name="ff" property="intensityMax"/>
				 <br>Exposure: <bean:write name="ff" property="exposureTime"/> 
				</logic:equal>
				-->
				
				
			</TD>
			<TD VALIGN="top" ALIGN="LEFT" WIDTH="30%">
			
			<logic:iterate id="sr" name="ff" property="selectedRegions">
				<A HREF="javascript:imagePopUp('SR', '<bean:write name="sr" property="id"/>', '<bean:write name="sr" property="image.width"/>', '<bean:write name="sr" property="image.height"/>')">
				 <IMG BORDER="0" SRC="/yrc/viewSelectedRegionThumbnail.do?id=<bean:write name="sr" property="id"/>"></A>

			
			</logic:iterate>
			</TD>
		</yrcwww:colorrow>
	</logic:iterate>

</TABLE>

 <TABLE CELLPADDING="no" CELLSPACING="0" width="600"> 

  <yrcwww:colorrow scheme="localization">
   <TD align="center" colspan="2"><br><b>CELL ATTRIBUTES:</b></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="localization">
    <TD valign="top" width="50%">Live or Fixed:</TD>
    <TD valign="top" width="50%"><bean:write name="experiment" property="cellStatus"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="localization">
    <TD valign="top" width="50%">Cell Treatment:</TD>
    <TD valign="top" width="50%"><bean:write name="experiment" property="cellTreatment"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="localization">
    <TD valign="top" width="50%">Growth Medium:</TD>
    <TD valign="top" width="50%"><bean:write name="experiment" property="cellGrowthMedium"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="localization">
    <TD valign="top" width="50%">Growth Temp.:</TD>
    <TD valign="top" width="50%"><bean:write name="experiment" property="cellGrowthTemperature"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="localization">
   <TD align="center" colspan="2"><br><b>OPTICS:</b></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="localization">
    <TD valign="top" width="50%">Dichroic Mirror:</TD>
    <TD valign="top" width="50%"><bean:write name="experiment" property="opticsDichroicMirror"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="localization">
    <TD valign="top" width="50%">Objective:</TD>
    <TD valign="top" width="50%"><bean:write name="experiment" property="opticsObjective"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="localization">
   <TD align="center" colspan="2"><br><b>CAMERA:</b></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="localization">
    <TD valign="top" width="50%">Type:</TD>
    <TD valign="top" width="50%"><bean:write name="experiment" property="cameraType"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="localization">
    <TD valign="top" width="50%">Gain:</TD>
    <TD valign="top" width="50%"><bean:write name="experiment" property="cameraGain"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="localization">
   <TD align="center" colspan="2"><br><b>IMAGE PROPERTIES:</b></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="localization">
    <TD valign="top" width="50%">XY Dimensions:</TD>
    <TD valign="top" width="50%"><bean:write name="experiment" property="XYDimensions"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="localization">
    <TD valign="top" width="50%">Pixel Size:</TD>
    <TD valign="top" width="50%"><bean:write name="experiment" property="pixelSize"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="localization">
    <TD valign="top" width="50%">Binning:</TD>
    <TD valign="top" width="50%"><bean:write name="experiment" property="binning"/></TD>
  </yrcwww:colorrow>

 </TABLE>


 
 </CENTER>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>