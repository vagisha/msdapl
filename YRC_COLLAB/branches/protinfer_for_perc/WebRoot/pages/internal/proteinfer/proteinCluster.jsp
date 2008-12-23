
<%@page import="org.yeastrc.www.proteinfer.idpicker.WIdPickerCluster"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<!-- PROTEINS TABLE -->
<br>
<div style="background-color: #3D902A; color: #EBFFE6; padding: 2px; cursor: pointer" class="protgrplist">
 <b>Proteins in  Cluster <bean:write name="clusterId" /></b>
</div>
<br>

<table cellpadding="2" cellspacing="2" align="center" width="90%"  id="prot_grp_table_<bean:write name="clusterId" />">
 <tr>
 <th><b><font size="2pt">Protein<br>Group ID</font></b></th>
 <th><b><font size="2pt">Accession(s)</font></b></th>
 <th><b><font size="2pt"># Peptides<br>(Unique)</font></b></th>
 <th><b><font size="2pt"># Spectra</font></b></th>
 </tr>
 
 <logic:iterate name="cluster" property="proteinGroups" id="protGrp">
  <tr id="protGrp_<bean:write name="protGrp" property="groupId" />">
     <td valign="middle">
     <span onclick="highlightProteinAndPeptides('<bean:write name="protGrp" property="groupId" />', '<bean:write name="protGrp" property="nonUniqMatchingPeptideGroupIdsString" />', '<bean:write name="protGrp" property="uniqMatchingPeptideGroupIdsString" />')"
     style="cursor:pointer;text-decoration:underline"><bean:write name="protGrp" property="groupId" />
     </span>
     </td>
     <td>
        <logic:iterate name="protGrp" property="proteins" id="prot" >
            <logic:equal name="prot" property="protein.isParsimonious" value="true"><b></logic:equal>
            <div onclick="showProteinDetails(<bean:write name="prot" property="protein.id" />)"
                 style="text-decoration: underline; cursor: pointer">
                 <logic:equal name="prot" property="protein.isParsimonious" value="false"><font color="#888888"></logic:equal>
                 <bean:write name="prot" property="accession" />
                 <logic:equal name="prot" property="protein.isParsimonious" value="false"></font></logic:equal>
            </div>
            <logic:equal name="prot" property="protein.isParsimonious" value="true"></b></logic:equal>
        </logic:iterate>
     </td>
     <td><bean:write name="protGrp" property="matchingPeptideCount" />(<bean:write name="protGrp" property="uniqMatchingPeptideCount" />)</td>
     <td><bean:write name="protGrp" property="spectrumCount" /></td>
 </tr>
 </logic:iterate>

        
</table>
<br>

<!-- PEPTIDES TABLE -->
<div style="background-color: #3D902A; color: #EBFFE6; padding: 2px; cursor: pointer;" class="peptgrplist" ><b>Peptides in Cluster <bean:write name="clusterId" /></b></div><br>
<table cellpadding="4" cellspacing="2" align="center" width="90%" id="pept_grp_table_<bean:write name="clusterId" />">
        
        <tr>
        <th><b><font size="2pt">Peptide<br>Group ID</font></b></th>
        <th><b><font size="2pt">Sequence(s)</font></b></th>
        <th><b><font size="2pt"># Spectra</font></b></th>
        <th><b><font size="2pt">Best FDR</font></b></th>
        </tr>
        
        <logic:iterate name="cluster" property="peptideGroups" id="peptGrp">
        	<logic:iterate name="peptGrp" property="peptides" id="pept">
        	<tr class="peptGrp_<bean:write name="pept" property="groupId" />">
        		<td><bean:write name="pept" property="groupId" /></td>
        		<td><bean:write name="pept" property="sequence" /></td>
        		<td><bean:write name="pept" property="spectralCount" /></td>
        		<td><bean:write name="pept" property="bestFdr" /></td>
        	</tr>
       		</logic:iterate>
        </logic:iterate>

</table>

<!-- PROTEINS-PEPTIDE ASSOCIATION TABLE -->
<bean:size name="cluster" property="proteinGroups" id="protGroupsSize"/>
<logic:greaterEqual name="protGroupsSize" value="2">
<br><div style="background-color: #3D902A; color: #EBFFE6; padding: 2px" ><b>Protein - Peptide Association</b></div><br>
<table id="assoctable_<bean:write name="clusterId" />"
       cellpadding="4" cellspacing="2" align="center">
       
    <tr>
      <th><b><font size="2pt">Group ID <br>(Peptide / Protein)</font></b></th>
      <logic:iterate name="cluster" property="proteinGroups" id="protGrp" >
            <th><b><font size="2pt"><bean:write name="protGrp" property="groupId" /></font></b></th>
       </logic:iterate>
    </tr>

	<%
	    WIdPickerCluster cluster = (WIdPickerCluster)request.getAttribute("cluster");
	%>
	<logic:iterate name="cluster" property="peptideGroups" id="peptGrp" type="edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptideGroup">
    <tr>
       	<th><b><font size="2pt"><bean:write name="peptGrp" property="groupId" /></font></b></th>
		<logic:iterate name="cluster" property="proteinGroups" id="protGrp" type="org.yeastrc.www.proteinfer.idpicker.WIdPickerProteinGroup">
	    	<td id="peptEvFor_<bean:write name="protGrp" property="groupId" />_<bean:write name="peptGrp" property="groupId" />">
	         	<%if(cluster.proteinAndPeptideGroupsMatch(protGrp.getGroupId(), peptGrp.getGroupId())) { %>
	          	 x
	          	<%} else {%>&nbsp;<%} %>
	        </td>
		</logic:iterate>
    </tr>
    </logic:iterate>
</table>
</logic:greaterEqual>



