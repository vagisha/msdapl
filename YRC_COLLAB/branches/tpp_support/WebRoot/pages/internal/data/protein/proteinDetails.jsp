<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:empty name="protein">
  <logic:forward name="standardHome" />
</logic:empty>

<%@ include file="/includes/header.jsp" %>

<yrcwww:contentbox title="View Protein Information" centered="true" width="80" widthRel="true">

<center>
<div align="center" style="padding:5px;font-size: 10pt;width: 90%; color: black;">

<div align="center">
<table align="center" 
      style="background-color:#F8F8FF; border: 1px solid #CBCBCB; cellspacing="0" cellpadding="2">
<tr>
	<td valign="top" align="left" width="20%"><b>Accession(s):</b></td>
	<td valign="top" align="left">
		<logic:iterate name="protein" property="proteinListing.fastaReferences" id="reference">
			<bean:write name="reference" property="accession"/>
			<br/>
		</logic:iterate>
	</td>
</tr>
<logic:notEmpty name="protein" property="proteinListing.commonReferences">
<tr>
	<td valign="top" align="left"><b>Common Name(s):</b></td>
	<td valign="top" align="left">
		<logic:iterate name="protein" property="proteinListing.commonReferences" id="reference">
			<bean:define name="reference" property="commonReference.name" id="commonName" type="java.lang.String"/>
			<bean:define name="reference" property="accession" id="accession" type="java.lang.String"/>
			<bean:write name="commonName"/>
			<logic:notEqual name="commonName" value="<%=accession.toString() %>">
			 / <bean:write name="accession" />
			</logic:notEqual>
			<logic:equal name="reference" property="hasExternalLink" value="true">
				<a href="<bean:write name="reference" property="url"/>" style="font-size: 8pt;" target="ExternalLink">
				[<bean:write name="reference" property="databaseName"/>]</a>
			</logic:equal>
			<br/>
		</logic:iterate>
	</td>
</tr>
</logic:notEmpty>

<tr>
	<td valign="top" align="left"><b>Organism:</b></td>
	<td valign="top" align="left">
		<a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=<bean:write name="protein" property="proteinListing.speciesId"/>">
    	<i><bean:write name="protein" property="proteinListing.speciesName" /></i></a>
	</td>
</tr>
<tr>
	<td valign="top" align="left"><b>Molecular Wt.:</b></td>
	<td valign="top" align="left">
    	<bean:write name="protein" property="molecularWeight" />
	</td>
</tr>
<tr>
	<td valign="top" align="left"><b>pI:</b></td>
	<td valign="top" align="left">
    	<bean:write name="protein" property="pi" />
	</td>
</tr>

<logic:present name="proteinAbundance">
<tr>
	<td valign="top" align="left"><b>Abundance:</b>
	<br/>
    <span class="small_font">Ghaemmaghami, <em>et al., </em></span><br>
    <span class="small_font"><em>Nature</em> <strong>425</strong>, 737-741 (2003)</span>
	</td>
	<td valign="top" align="left">
    	<bean:write name="proteinAbundance" /> copies / cell 
	</td>
</tr>
</logic:present>

<tr>
	<td valign="top" align="left"><b>Description(s):</b></td>
	<td valign="top" align="left" style="color: #888888; font-size: 9pt;">
		<logic:iterate name="protein" property="proteinListing.descriptionReferences" id="reference">
			<li>
				<logic:equal name="reference" property="hasExternalLink" value="true">
					<a href="<bean:write name="reference" property="url"/>" style="font-size: 8pt;" target="External Link">
						<b>[<bean:write name="reference" property="databaseName"/>]</b>
					</a>
				</logic:equal>
				<logic:equal name="reference" property="hasExternalLink" value="false">
					<span style="color:#000080;"><b>[<bean:write name="reference" property="databaseName"/>]</b></span>
				</logic:equal>
				 &nbsp; &nbsp; <bean:write name="reference" property="description"/>
				<br/>
				
			</li>
		</logic:iterate>
	</td>
</tr>

<!-- GENE ONTOLOGY -->
<tr>
   <td valign="top" align="left"><b>GO Cellular Component:</b></td>
   <td valign="top" align="left">
    <logic:empty name="components">
     NONE FOUND
    </logic:empty>
    <logic:notEmpty name="components">
     <logic:iterate name="components" id="gonode">
	 <a target="go_window"
	    href="http://www.godatabase.org/cgi-bin/amigo/go.cgi?action=query&view=query&search_constraint=terms&query=<bean:write name="gonode" property="accession"/>">
      <bean:write name="gonode" property="name"/></a><br>
     </logic:iterate>    
    </logic:notEmpty>
    </td>
</tr>
<tr>
   <td valign="top" align="left"><b>GO Biological Process:</b></td>
   <td valign="top" align="left">
    
    <logic:empty name="processes">
     NONE FOUND
    </logic:empty>
    <logic:notEmpty name="processes">
     <logic:iterate name="processes" id="gonode">
	 <a target="go_window"
	    href="http://www.godatabase.org/cgi-bin/amigo/go.cgi?action=query&view=query&search_constraint=terms&query=<bean:write name="gonode" property="accession"/>">
      <bean:write name="gonode" property="name"/></a><br>
     </logic:iterate>    
    </logic:notEmpty>
    
   </td>
</tr>

<tr>
   <td valign="top" align="left"><b>GO Molecular Function:</b></td>
   <td valign="top" align="left">
    <logic:empty name="functions">
     NONE FOUND
    </logic:empty>
    <logic:notEmpty name="functions">
     <logic:iterate name="functions" id="gonode">
	 <a target="go_window"
	    href="http://www.godatabase.org/cgi-bin/amigo/go.cgi?action=query&view=query&search_constraint=terms&query=<bean:write name="gonode" property="accession"/>">
      <bean:write name="gonode" property="name"/></a><br>
     </logic:iterate>    
    </logic:notEmpty>
    
   </td>
</tr>

<tr>
<td colspan="2" style="font-size: 8pt; color: red;" align="center">
<a  style="color:red;"   href="<yrcwww:link path='viewProtein.do?id'/>=<bean:write name='protein' property='protein.id'/>">[List experiments with this protein]</a>
</td>
</tr>
</table>

</div>

<br>

<!-- PROTEIN SEQUENCE -->
<div align="center">
<table  align="center" width="60%" id="protseqtbl_<bean:write name='protein' property='protein.id'/>" style="border:1px solid gray;">
	<tr>
	<td valign="top">
	<font style="font-size:9pt;">
     [<a target="blast_window"
         href="http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?CMD=Web&LAYOUT=TwoWindows&AUTO_FORMAT=Semiauto&ALIGNMENTS=50&ALIGNMENT_VIEW=Pairwise&CDD_SEARCH=on&CLIENT=web&COMPOSITION_BASED_STATISTICS=on&DATABASE=nr&DESCRIPTIONS=100&ENTREZ_QUERY=(none)&EXPECT=1000&FILTER=L&FORMAT_OBJECT=Alignment&FORMAT_TYPE=HTML&I_THRESH=0.005&MATRIX_NAME=BLOSUM62&NCBI_GI=on&PAGE=Proteins&PROGRAM=blastp&SERVICE=plain&SET_DEFAULTS.x=41&SET_DEFAULTS.y=5&SHOW_OVERVIEW=on&END_OF_HTTPGET=Yes&SHOW_LINKOUT=yes&QUERY=<bean:write name="protein" property="sequence"/>">NCBI BLAST</a>]

	<BR><br>
	<b>YRC Philius</b><br>
	<span><a href="http://www.ncbi.nlm.nih.gov/sites/entrez?cmd=Search&db=pubmed&term=18989393">Reynolds <I>et al.</I></a></span>
	<br/>
	<span style="text-decoration: underline; cursor: pointer;"
      onclick="philiusAnnotations(<bean:write name="protein" property="protein.id" />,<bean:write name="protein" property="id" />)"
      id="philiusbutton_<bean:write name="protein" property="protein.id"/>">[Get Predictions]</span>
    </font>
	</td>
	<td align="left" valign="top">
	<div id="protsequence_<bean:write name="protein" property="protein.id"/>">
	<!-- Protein sequwnce -->
	<pre><bean:write name="protein"  property="htmlSequence" filter="false"/></pre>
	</div>
	<!-- Place holder for Philius Annotations -->
	<div id="philiusannot_<bean:write name="protein" property="protein.id"/>"></div>
	</td>
	</tr>
	<tr>
		<td colspan="2" align="center">
		<div id="philius_status_<bean:write name="protein" property="protein.id"/>" 
			 style="display:none;font-size:10pt;color:red;"></div>
		</td>
	</tr>
</table>
</div>
<br><br>
</div>
</center>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>