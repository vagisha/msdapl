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
			
			<!-- bookmark link is editable -->
			<logic:equal name="writeAccess" value="true">
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
			</logic:equal>
			
			
			<!-- bookmark link is NOT editable -->
			<logic:equal name="writeAccess" value="false">
				<logic:equal name="prpRun" property="isBookmarked" value="true">
				<td valign="top"><img alt="B" src="<yrcwww:link path="images/bookmark.png"/>"/>
				</td>
				</logic:equal>
				<logic:equal name="prpRun" property="isBookmarked" value="false">
				<td valign="top"><img alt="B" src="<yrcwww:link path="images/no_bookmark.png"/>"/>
				</td>
				</logic:equal>
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
				<span class="editableComment clickable" title="expt_<bean:write name='experiment' property='id'/>" 
				id="piRun_<bean:write name='prpRun' property='proteinProphetRun.id'/>" style="font-size:8pt; color:red;">[Edit]</span>
				</logic:equal>
			</td>
			<td valign="top">
			<a href="<yrcwww:link path='viewProteinProphetResult.do?'/>pinferId=<bean:write name='prpRun' property='proteinProphetRun.id'/>">
				<b><font color="green">View</font></b></a>
			</td>
			<td valign="top" align="center" >
 		 		<input type="checkbox" class="compare_cb" name="<bean:write name='experiment' property='id'/>" value="<bean:write name='prpRun' property='proteinProphetRun.id'/>"></input>
			</td>
			</tr>
			<tr>
				<td colspan="11" valign="top">
				<div id="piRun_<bean:write name='prpRun' property='proteinProphetRun.id'/>_edit" align="center"
			     style="display:none;">
			     <textarea rows="5" cols="60" class="edit_text"></textarea>
			     <br>
			     <button class="savePiRunComments" title="expt_<bean:write name='experiment' property='id'/>"
			     		id="<bean:write name='prpRun' property='proteinProphetRun.id'/>">Save</button>
			     <button class="cancelPiRunComments" title="expt_<bean:write name='experiment' property='id'/>"
			     		id="<bean:write name='prpRun' property='proteinProphetRun.id'/>">Cancel</button>
				</div>
				</td>
			</tr>
		</logic:iterate>
		<tr>
		<td colspan="11" style="font-size:8pt;" >
			<ul>
			<li>Subsumed proteins are excluded in calculating group and protein counts</li>
			<li>#Indist. Groups = number of indistinguishable protein groups</li>
			<li>#Ions = number of unique combinations of sequence + modifications + charge</li>
			</ul>
		</td>
		<td colspan="1" style="text-align:center" >
			<span class="clickable small_font" style="text-decoration:underline;" onclick="javascript:selectAllProteinProphet(<bean:write name='experiment' property='id'/>);">[Select All]</span>
			<br/>
			<span class="clickable small_font" style="text-decoration:underline;" onclick="javascript:clearSelectedProteinProphet(<bean:write name='experiment' property='id'/>);">[Clear Selected]</span>
		</td>
		</tr>
		</tbody>
		</table>
	</div>