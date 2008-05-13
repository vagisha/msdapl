<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:empty name="screen">
  <logic:forward name="viewY2HScreen" />
</logic:empty>

<%@ include file="/includes/header.jsp" %>

<yrcwww:contentbox title="View Yeast Two-Hybrid Screen" centered="true" width="600" scheme="y2h">

 <CENTER>
  <B>Screen Information:</B><BR><BR>
 <TABLE CELLPADDING="no" CELLSPACING="0"> 

  <yrcwww:colorrow scheme="y2h">
    <TD valign="top" width="25%">Bait Protein:</TD>
    <TD valign="top" width="75%">
     <html:link href="/yrc/viewProtein.do" paramId="id" paramName="screen" paramProperty="bait.protein.id">
     <bean:write name="screen" property="bait.protein.listing"/></html:link>
    </TD>
  </yrcwww:colorrow>

 <logic:notEmpty name="screen" property="bait.mutations">
  <yrcwww:colorrow scheme="y2h">
    <TD valign="top" width="25%">Bait Mutations:</TD>
    <TD valign="top" width="75%">
     <logic:iterate id="mutation" name="screen" property="bait.mutations">
      <bean:write name="mutation" property="origAminoAcid"/><bean:write name="mutation" property="position"/><bean:write name="mutation" property="newAminoAcid"/>&nbsp;      
     </logic:iterate>
    </TD>
  </yrcwww:colorrow>
 </logic:notEmpty>
 
  <yrcwww:colorrow scheme="y2h">
    <TD valign="top" width="25%">Fragment Info:</TD>
    <TD valign="top" width="75%">
    
     <logic:equal name="screen" property="bait.startResidue" value="0">
        Full length protein
     </logic:equal>
     <logic:notEqual name="screen" property="bait.startResidue" value="0">
        <bean:write name="screen" property="bait.startResidue"/> to 
        <bean:write name="screen" property="bait.endResidue"/>
     </logic:notEqual>
    </TD>
  </yrcwww:colorrow>


  <yrcwww:colorrow scheme="y2h">
   <TD valign="top" width="25%">Screen Date:</TD>
   <TD valign="top" width="75%"><bean:write name="screen" property="screenDate"/></TD>
  </yrcwww:colorrow>
  <yrcwww:colorrow scheme="y2h">
   <TD valign="top" width="25%">Project:</TD>
   <TD valign="top" width="75%">
     <html:link href="/yrc/viewProject.do" paramId="ID" paramName="screen" paramProperty="projectID">
     <bean:write name="screen" property="project.title"/></html:link></TD>
  </yrcwww:colorrow>
  <yrcwww:colorrow scheme="y2h">

   <TD valign="top" width="25%">Comments:
   
   		<yrcwww:member group="TwoHybrid">
   			<logic:empty name="screen" property="comments">
   				<br><font style="font-size:8pt;">[<a href="javascript:showEditBox()">Add Comments</a>]<font>
   			</logic:empty>
   			<logic:notEmpty name="screen" property="comments">
   				<br><font style="font-size:8pt;">[<a href="javascript:showEditBox()">Edit Comments</a>]<font>
			</logic:notEmpty>   
		</yrcwww:member>
		
   </TD>
   
   
   <TD valign="top" width="75%">
   		
   		<div id="comments_text">
	   		<logic:empty name="screen" property="comments">
	   			None entered.
	   		</logic:empty>
	   		<logic:notEmpty name="screen" property="comments">
	   			<bean:write name="screen" property="comments"/>
			</logic:notEmpty>
		</div>
		<div id="comments_edit_box" style="display:none;">
			<html:form action="saveY2HComments" method="post">
				<input type="hidden" name="screenID" value="<bean:write name="screen" property="ID" />">
				<textarea name="comments" rows="5" cols="30"><bean:write name="screen" property="comments" /></textarea><br>
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
 
 <P><TABLE CELLPADDING="no" CELLSPACING="0" WIDTH="50%">

	<yrcwww:colorrow scheme="y2h">
		<TD colspan="2" align="center"><B><U>Screen Results:<BR><BR></U></B></TD>
	</yrcwww:colorrow>
	
	<yrcwww:colorrow scheme="y2h">
		<TD width="50%"><B><U>Prey ORF</U></B></TD>
		<TD width="50%"><B><U>Num Hits</U></B></TD>
	</yrcwww:colorrow>

	<logic:iterate id="result" name="screen" property="results">
		<yrcwww:colorrow scheme="y2h">
			<TD width="50%"><bean:write name="result" property="preyORF.listing"/></TD>
			<TD width="50%"><bean:write name="result" property="numHits"/></TD>
		</yrcwww:colorrow>
	</logic:iterate>
 </TABLE>

 </CENTER>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>