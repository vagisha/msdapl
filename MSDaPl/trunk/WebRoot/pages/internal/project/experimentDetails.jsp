<%@page import="org.yeastrc.ms.domain.search.Program"%>
<%@page import="org.yeastrc.ms.domain.general.MsInstrument"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<A name='Expt<bean:write name="experiment" property="id"/>'></A> 
			<div style="border:1px dotted gray;margin:5 5 5 5; padding:0 0 5 0;">
			<div style="background-color:#ED9A2E;width:100%; margin:0; padding:3 0 3 0; color:white;" >
			
			
				<logic:equal name="experiment" property="hasFullInformation" value="false">
				<span style="margin-left:10;" 
				      class="foldable fold-close" id="expt_fold_<bean:write name="experiment" property="id"/>" 
				      onclick="showExperimentDetails(<bean:write name="experiment" property="id"/>)">
				&nbsp;&nbsp;&nbsp;&nbsp;</span>
				</logic:equal>
				
				<logic:equal name="experiment" property="hasFullInformation" value="true">
				<span style="margin-left:10;" 
				      class="foldable fold-open" id="expt_fold_<bean:write name="experiment" property="id"/>" 
				      onclick="showExperimentDetails(<bean:write name="experiment" property="id"/>)">
				&nbsp;&nbsp;&nbsp;&nbsp;</span>
				</logic:equal>
				
				<span style="padding-left:10;"><b>Experiment ID: <bean:write name="experiment" property="id"/></b></span>
			</div>
			
			
			<div style="padding:0; margin:0;"> 
			<div style="margin:0; padding:5;">
			<table cellspacing="0" cellpadding="0">		
				<tr>	
					<td><b>Date Uploaded: </b></td>
					<td style="padding-left:10">
						<bean:write name="experiment" property="uploadDate"/> 
						<logic:equal name="writeAccess" value="true">
							&nbsp; &nbsp;
							<span class="clickable underline" style="color:red; font-weight:bold;" onClick="confirmDeleteExperiment('<bean:write name="experiment" property="id"/>')">[Delete Experiment]</span>
						</logic:equal>
					</td>
				</tr>
				<tr>
					<td><b>Location: </b></td>
					<td style="padding-left:10"><bean:write name="experiment" property="serverDirectory"/></td>
				</tr>
				<tr>
					<td><b>Instrument: </b>
						<logic:equal name="writeAccess" value="true">
							<span class="editableInstrument clickable" 
							      id="instrumentfor_<bean:write name='experiment' property='id'/>" 
							      title="<bean:write name='experiment' property='instrumentId'/>_<bean:write name='experiment' property='id'/>"
							      style="font-size:8pt; color:red;">[Change]</span>
						</logic:equal>
					</td>
					<td style="padding-left:10">
						<span
							id="instrumentfor_<bean:write name='experiment' property='id'/>_select"
						>
							<bean:write name="experiment" property="instrumentName"/>
						</span>
					</td>
				</tr>
				<tr>
					<td valign="top"><b>Comments </b>
						<logic:equal name="writeAccess" value="true">
						<span class="editableComment clickable" data-editable_id="experiment_<bean:write name='experiment' property='id'/>" style="font-size:8pt; color:red;">[Edit]</span>
						</logic:equal>
						<b>: </b></td>
					<td style="padding-left:10">
						<div id="experiment_<bean:write name='experiment' property='id'/>_text"><bean:write name="experiment" property="comments"/></div>
						<div id="experiment_<bean:write name='experiment' property='id'/>_edit" align="center"
						     style="display:none;">
						     <textarea rows="5" cols="60" class="edit_text"></textarea>
						     <br>
						     <button class="saveExptComments" data-editable_id="<bean:write name='experiment' property='id'/>">Save</button>
						     <button class="cancelExptComments" data-editable_id="<bean:write name='experiment' property='id'/>">Cancel</button>
						</div>
					</td>
				</tr>
				<logic:equal name="experiment" property="uploadSuccess" value="false">
					<tr>
						<td style="color:red; font-weight:bold;">Upload Failed</td>
						<td><html:link action="viewUploadJob.do" 
									   paramId="id" 
									   paramName="experiment" paramProperty="uploadJobId">View Log</html:link></td>
					</tr>
				</logic:equal>
			</table>
			</div>
			
			
			<logic:equal name="experiment" property="hasFullInformation" value="false">
				<div id="expt_fold_<bean:write name="experiment" property="id"/>_target"></div>
			</logic:equal>
			
			
			<logic:equal name="experiment" property="hasFullInformation" value="true">
			<div id="expt_fold_<bean:write name="experiment" property="id"/>_target"> <!-- begin collapsible div -->
			
				<%@ include file="experimentFullDetails.jsp" %>
			
			</div> <!-- end of collapsible div -->
			</logic:equal>
		</div> 
		</div> <!-- End of one experiment -->