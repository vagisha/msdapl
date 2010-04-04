<%@page import="org.yeastrc.ms.domain.search.Program"%>
<%@page import="org.yeastrc.ms.domain.general.MsInstrument"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<!-- SEARCHES FOR THE EXPERIMENT -->
<logic:notEmpty name="experiment" property="searches">
	<logic:iterate name="experiment" property="searches" id="search">
		<div style="background-color: #FFFFE0; margin:5 5 5 5; padding:5; border: 1px dashed gray;" >
		<table width="90%">
			<tr>
				<td width="33%"><b>Program: </b>&nbsp;
				<b><bean:write name="search" property="searchProgram"/>
				&nbsp;
				<bean:write name="search" property="searchProgramVersion"/></b></td>
				
				
				<!-- !!!!!! SEQUEST !!!!!! -->
				<logic:equal name="search" property="searchProgram" value="<%=Program.SEQUEST.toString() %>">
				<td width="33%">
					<b>
					<html:link action="viewSequestResults.do" 
								paramId="ID" 
								paramName="search" paramProperty="id">[View Results]</html:link>
					<!-- <html:link action="percolatorPepXmlDownloadForm.do" 
								paramId="ID" 
								paramName="search" paramProperty="id">[PepXML]</html:link> -->
					</b>
				</td>
				</logic:equal>
				
				<!-- !!!!!! MASCOT !!!!!! -->
				<logic:equal name="search" property="searchProgram" value="<%=Program.MASCOT.toString() %>">
				<td width="33%">
				<b>
					<html:link action="viewMascotResults.do" 
								paramId="ID" 
								paramName="search" paramProperty="id">[View Results]</html:link>
				</b>
				</td>
				</logic:equal>
				
				<!-- !!!!!! XTANDEM !!!!!! -->
				<logic:equal name="search" property="searchProgram" value="<%=Program.XTANDEM.toString() %>">
				<td width="33%">
				<b>
					<html:link action="viewXtandemResults.do" 
								paramId="ID" 
								paramName="search" paramProperty="id">[View Results]</html:link>
				</b>
				</td>
				</logic:equal>
				
				
				<td width="33%"><b>Search Date: </b>&nbsp;
				<bean:write name="search" property="searchDate"/></td>
				
			</tr>
			<tr>
				<td><b>Search Database: </b></td>
				<td><bean:write name="search" property="searchDatabase"/></td>
			</tr>
			<tr>
				<td><b>Enzyme: </b></td>
				<td><bean:write name="search" property="enzymes"/></td>
			</tr>
			<tr>
				<td valign="top"><b>Residue Modifications: </b></td>
				<td width="33%" valign="top"><b>Static: </b>
				<bean:write name="search" property="staticResidueModifications"/></td>
				<td width="33%" valign="top"><b>Dynamic: </b>
				<bean:write name="search" property="dynamicResidueModifications"/></td>
			</tr>
			<tr>
				<td valign="top"><b>Terminal Modifications: </b></td>
				<td width="33%" valign="top"><b>Static: </b>
				<bean:write name="search" property="staticTerminalModifications"/></td>
				<td width="33%" valign="top"><b>Dynamic: </b>
				<bean:write name="search" property="dynamicTerminalModifications"/></td>
			</tr>
		</table>
		</div>	
	</logic:iterate>
</logic:notEmpty>

<!-- SEARCH ANALYSES FOR THE EXPERIMENT -->
<logic:notEmpty name="experiment" property="analyses">
	<div style="background-color: #F0FFF0; margin:5 5 5 5; padding:5; border: 1px dashed gray;" >
	
	<!-- !!!!!! PERCOLATOR !!!!!! -->
	<logic:equal name="experiment" property="analysisProgramName" value="<%=Program.PERCOLATOR.displayName() %>">
		<table width="100%">
			<tbody>
			<logic:iterate name="experiment" property="analyses" id="analysis">
			<tr>
				<td width="25%" valign="middle"><b>Program: Percolator <bean:write name="analysis" property="analysisProgramVersionShort"/></b></td>
				<td width="25%" valign="middle">
					<b><html:link action="viewPercolatorResults.do" paramId="ID" paramName="analysis" paramProperty="id">[View Results]</html:link></b>
				</td>
				<td width="25%">
					<b><a href="<yrcwww:link path='viewQCPlots.do?'/>analysisId=<bean:write name='analysis' property='id' />&experimentId=<bean:write name='experiment' property='id'/>"> 
					[QC Plots]</a></b>
				</td>
				<td width="25%">
					<b><a href="<yrcwww:link path='newPercolatorProteinInference.do?'/>searchAnalysisId=<bean:write name='analysis' property='id' />&projectId=<bean:write name='experiment' property='projectId'/>"> 
					[Infer Proteins]</a></b>
					<a href="" onclick="openInformationPopup('<yrcwww:link path='pages/internal/docs/proteinInference.jsp'/>'); return false;">
   					<img src="<yrcwww:link path='images/info_16.png'/>" align="bottom" border="0"/></a>
				</td>
			</tr>
			</logic:iterate>
			</tbody>
		</table>
	</logic:equal>
	
	<!-- !!!!!! PEPTIDE PROPHET !!!!!! -->
	<logic:equal name="experiment" property="analysisProgramName" value="<%=Program.PEPTIDE_PROPHET.displayName() %>">
	<!-- NAME PF THE ANALYSIS PROGRAM -->
	<div><b><bean:write name="experiment" property="analysisProgramName"/> Results </b></div> 
	<table width="100%">
		<thead>
		<tr align="left">
			<th valign="top">ID</th>
			<th valign="top">Version</th>
			<th valign="top">File</th>
			<th valign="top"></th>
		</thead>
		<tbody>
		<logic:iterate name="experiment" property="analyses" id="analysis">
			<tr>
			<td><bean:write name="analysis" property="id"/></td>
			<td><bean:write name="analysis" property="analysisProgramVersionShort"/></td>
			<td valign="top">
				<b><bean:write name="analysis" property="analysisName" /></b>
			</td>
			<td valign="top">
				<b>
					<html:link action="viewPeptideProphetResults.do" paramId="ID" paramName="analysis" paramProperty="id">[View Results]</html:link>
				</b>
			</td>
		</tr>
		</logic:iterate>
		</table>
		</logic:equal>
	</div>
</logic:notEmpty>

<!-- PROTEIN INFERENCE RESULTS FOR THE EXPERIMENT -->
<logic:equal name="experiment" property="hasProtInferResults" value="true" >
<logic:present name="experiment" property="dtaSelect">
	<div style="background-color: #FFFFF0; margin:5 5 5 5; padding:5; border: 1px dashed gray;" > 
	
		<table width="90%">
		<tr>
			<td width="33%"><b>Program: </b>&nbsp;
			<b>DTASelect</b>
			&nbsp;</td>
			<td width="33%">
				<b><html:link action="viewYatesRun.do" paramId="id" paramName="experiment" paramProperty="dtaSelect.id">[View Results]</html:link></b>
			</td>
			<td width="33%">&nbsp;
			</td>
		</tr>
		</table>
	</div>
</logic:present>

<logic:notEmpty name="experiment" property="proteinProphetRuns">
	<div style="background-color: #FFFFF0; margin:5 5 5 5; padding:5; border: 1px dashed gray;" > 
		<div><b>ProteinProphet Results</b></div> 
		<table width="100%">
		<thead>
		<tr align="left">
			<th></th>
			<th valign="top">ID</th>
			<th valign="top">Version</th>
			<th valign="top">File</th>
			<th valign="top" align="center">#Prophet<br>Groups</th>
			<th valign="top" align="center">#Indist.<br>Groups</th>
			<th valign="top" align="center">#Proteins</th>
			<th valign="top" align="center">#Pept.<br/>Seq.</th>
			<th valign="top" align="center">#Ions</th>
			<th valign="top">Comments</th>
			<th valign="top"></th>
			<th valign="top">Compare</th></tr>
		</thead>
		<tbody>
		<logic:iterate name="experiment" property="proteinProphetRuns" id="prpRun" type="org.yeastrc.experiment.ExperimentProteinProphetRun">
			<tr>
			<logic:equal name="prpRun" property="isBookmarked" value="true">
				<td valign="top"><img alt="B" class="clickable has_bookmark"
						src="<yrcwww:link path="images/bookmark.png"/>"
						id="expt_piRun_<bean:write name='prpRun' property='proteinProphetRun.id'/>"
						onclick="editBookmark(this, <bean:write name='prpRun' property='proteinProphetRun.id'/>)"/>
				</td>
			</logic:equal>
			<logic:equal name="prpRun" property="isBookmarked" value="false">
				<td valign="top"><img alt="B" class="clickable no_bookmark"
						src="<yrcwww:link path="images/no_bookmark.png"/>"
						id="expt_piRun_<bean:write name='prpRun' property='proteinProphetRun.id'/>"
						onclick="javascript:editBookmark(this, <bean:write name='prpRun' property='proteinProphetRun.id'/>)"/>
				</td>
			</logic:equal>
			<td valign="top"><b><bean:write name="prpRun" property="proteinProphetRun.id"/></b></td>
			<td valign="top"><bean:write name="prpRun" property="programVersionShort"/></td>
			<td valign="top"><b><bean:write name="prpRun" property="proteinProphetRun.filename"/></b></td>
			<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="prpRun" property="numParsimoniousProteinProphetGroups"/></td>
			<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="prpRun" property="numParsimoniousProteinGroups"/></td>
			<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="prpRun" property="numParsimoniousProteins"/></td>
			<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="prpRun" property="uniqPeptideSequenceCount"/></td>
			<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="prpRun" property="uniqIonCount"/></td>
			<td valign="top">
				<span id="piRun_<bean:write name='prpRun' property='proteinProphetRun.id'/>_text"><bean:write name="prpRun" property="proteinProphetRun.comments"/></span>
				<logic:equal name="writeAccess" value="true">
				<span class="editableComment clickable" id="piRun_<bean:write name='prpRun' property='proteinProphetRun.id'/>" style="font-size:8pt; color:red;">[Edit]</span>
				</logic:equal>
			</td>
			<td valign="top">
			<a href="<yrcwww:link path='viewProteinProphetResult.do?'/>pinferId=<bean:write name='prpRun' property='proteinProphetRun.id'/>">
				<b><font color="green">View</font></b></a>
			</td>
			<td valign="top" align="center" >
 		 				<input type="checkbox" class="compare_cb" value="<bean:write name='prpRun' property='proteinProphetRun.id'/>"></input>
			</td>
			</tr>
			<tr>
				<td colspan="11" valign="top">
				<div id="piRun_<bean:write name='prpRun' property='proteinProphetRun.id'/>_edit" align="center"
			     style="display:none;">
			     <textarea rows="5" cols="60" class="edit_text"></textarea>
			     <br>
			     <button class="savePiRunComments" id="<bean:write name='prpRun' property='proteinProphetRun.id'/>">Save</button>
			     <button class="cancelPiRunComments" id="<bean:write name='prpRun' property='proteinProphetRun.id'/>">Cancel</button>
				</div>
				</td>
			</tr>
		</logic:iterate>
		<tr><td colspan="11" style="font-size:8pt;" >
			<ul>
			<li>Subsumed proteins are excluded in calculating group and protein counts</li>
			<li>#Indist. Groups = number of indistinguishable protein groups</li>
			<li>#Ions = number of unique combinations of sequence + modifications + charge</li>
			</ul>
		</td></tr>
		</tbody>
		</table>
	</div>
</logic:notEmpty>

<logic:notEmpty name="experiment" property="protInferRuns">
	<div style="background-color: #F0F8FF; margin:5 5 5 5; padding:5; border: 1px dashed gray;" >
		<div><b>Protein Inference Results</b></div> 
		<table width="100%">
		<thead>
		<tr align="left">
			<th></th>
			<th valign="top">ID</th>
			<th valign="top">Version</th>
			<th valign="top">Date</th>
			<th valign="top" align="center">#Indist.<br>Groups</th>
			<th valign="top" align="center">#Proteins</th>
			<th valign="top" align="center">#Pept.<br/>Seq.</th>
			<th valign="top" align="center">#Ions</th>
			<th valign="top">Comments</th>
			<th valign="top">&nbsp;</th>
			<th valign="top">Compare</th></tr>
		</thead>
		<tbody>
		<logic:iterate name="experiment" property="protInferRuns" id="piRun" type="org.yeastrc.experiment.ExperimentProteinferRun">
			<tr>
			<logic:equal name="piRun" property="isBookmarked" value="true">
				<td valign="top"><img alt="B" class="clickable has_bookmark"
						src="<yrcwww:link path="images/bookmark.png"/>"
						id="expt_piRun_<bean:write name='piRun' property='job.pinferId'/>"
						onclick="editBookmark(this, <bean:write name='piRun' property='job.pinferId'/>)"/>
				</td>
			</logic:equal>
			<logic:equal name="piRun" property="isBookmarked" value="false">
				<td valign="top"><img alt="B" class="clickable no_bookmark"
						src="<yrcwww:link path="images/no_bookmark.png"/>"
						id="expt_piRun_<bean:write name='piRun' property='job.pinferId'/>"
						onclick="javascript:editBookmark(this, <bean:write name='piRun' property='job.pinferId'/>)"/>
				</td>
			</logic:equal>
			
			<td valign="top"><b><bean:write name="piRun" property="job.pinferId"/></b></td>
			<td valign="top" align="center"><b><bean:write name="piRun" property="job.version"/></b></td>
			<td valign="top"><bean:write name="piRun" property="job.submitDate"/></td>
			
			<logic:equal name="piRun" property="job.complete" value="true">
			<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="piRun" property="numParsimoniousProteinGroups"/></td>
			<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="piRun" property="numParsimoniousProteins"/></td>
			<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="piRun" property="uniqPeptideSequenceCount"/></td>
			<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="piRun" property="uniqIonCount"/></td>
			</logic:equal>
			
			<logic:equal name="piRun" property="job.complete" value="false">
			<td valign="top">&nbsp;</td>
			<td valign="top">&nbsp;</td>
			</logic:equal>
			
			
			
			<td valign="top">
				<span id="piRun_<bean:write name='piRun' property='job.pinferId'/>_text"><bean:write name="piRun" property="job.comments"/></span>
				<logic:equal name="writeAccess" value="true">
				<span class="editableComment clickable" id="piRun_<bean:write name='piRun' property='job.pinferId'/>" style="font-size:8pt; color:red;">[Edit]</span>
				</logic:equal>
			</td>
			<td valign="top">
			
			<!-- Job COMPLETE -->
			<logic:equal name="piRun" property="job.complete" value="true">
				<nobr>
				<a href="<yrcwww:link path='viewProteinInferenceResult.do?'/>pinferId=<bean:write name='piRun' property='job.pinferId'/>">
				<b><font color="green">View</font></b></a>
				&nbsp;
				<span class="clickable" style="text-decoration: underline; color:red;" 
				      onclick="javascript:deleteProtInferRun(<bean:write name='piRun' property='job.pinferId'/>);">Delete</span>
				</nobr>
			</logic:equal>
			<!-- Job FAILED -->
			<logic:equal name="piRun" property="job.failed" value="true">
				<a href="<yrcwww:link path='viewProteinInferenceJob.do?'/>pinferId=<bean:write name='piRun' property='job.pinferId'/>&projectId=<bean:write name='experiment' property='projectId'/>">
				<b><font color="red"><bean:write name="piRun" property="job.statusDescription"/></font></b>
				</a>
			</logic:equal>
			<!-- Job RUNNING -->
			<logic:equal name="piRun" property="job.running" value="true">
				<a href="<yrcwww:link path='viewProteinInferenceJob.do?'/>pinferId=<bean:write name='piRun' property='job.pinferId'/>&projectId=<bean:write name='experiment' property='projectId'/>">
				<b><font color="#000000"><bean:write name="piRun" property="job.statusDescription"/></font></b>
				</a>
			</logic:equal>
			
 		 	</td>
 		 			
 		 	<logic:equal name="piRun" property="job.complete" value="true">
 		 	<td valign="top" align="center" >
 		 		<input type="checkbox" class="compare_cb" value="<bean:write name='piRun' property='job.pinferId'/>"></input>
			</td>
			</logic:equal>
 		 			
			</tr>
			<tr>
				<td colspan="10" valign="top">
				<div id="piRun_<bean:write name='piRun' property='job.pinferId'/>_edit" align="center"
			     style="display:none;">
			     <textarea rows="5" cols="60" class="edit_text"></textarea>
			     <br>
			     <button class="savePiRunComments" id="<bean:write name='piRun' property='job.pinferId'/>">Save</button>
			     <button class="cancelPiRunComments" id="<bean:write name='piRun' property='job.pinferId'/>">Cancel</button>
				</div>
				</td>
			</tr>
		</logic:iterate>
		
		<tr><td colspan="10" style="font-size:8pt;" >
			<ul>
			<li>Only parsimonious proteins are included in calculating indistinguishable group and protein counts</li>
			<li>#Ions = number of unique combinations of sequence + modifications + charge</li>
			</ul>
		</td></tr>
		
		</tbody>
		</table>
	</div>
</logic:notEmpty>
<div style="margin:5 5 5 5; padding:5; border: 1px dashed gray;" >
<table width="100%">
	<tr>
			<td colspan="9" align="right">
				<input type="checkbox" id="grpProts" value="group" checked="checked" />Group Indistinguishable Proteins
				&nbsp;
				<span class="clickable" style="text-decoration:underline;" onclick="javascript:compareSelectedProtInferAndMore();"><b>[Compare More]</b></span>
				&nbsp;
				<span class="clickable" style="text-decoration:underline;" onclick="javascript:compareSelectedProtInfer();"><b>[Compare]</b></span>
				<br><br>
				<span class="clickable" style="text-decoration:underline;" onclick="javascript:clearSelectedProtInfer();">[Clear Selected]</span>
			</td>
		</tr>
</table>
</div>
</logic:equal>


<!-- FILES FOR THE EXPERIMENT (Placeholder)-->
<div align="center">
<span
	id="listfileslink_<bean:write name='experiment' property='id'/>"  
	class="clickable" style="font-weight:bold; color:#D74D2D;" 
	onclick="javascript:toggleFilesForExperiment(<bean:write name='experiment' property='id'/>);">[List Files]</span>
</div>
<div style="background-color: #FFFFFF; margin:5 5 5 5; padding:0;" id="listfileslink_<bean:write name='experiment' property='id'/>_target"></div>
