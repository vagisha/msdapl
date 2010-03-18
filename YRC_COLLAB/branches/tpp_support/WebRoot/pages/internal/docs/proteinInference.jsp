<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<head>
 <title>MSDaPl Docs: Protein Inference</title>
 <link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='/css/global.css' />">
</head>

<yrcwww:contentbox centered="true" width="90" widthRel="true" title="Protein inference">

	<!-- PROTEIN INFERENCE PROCESS -->
	<div style="border: 1px dotted; margin:10x; padding:10px;">
	This document is for the protein inference program implemented in MSDaPl.
	It is available for use with <a href="http://noble.gs.washington.edu/proj/percolator/" target="_blank">Percolator</a> 
	results generated with the MacCoss Lab's pipeline.
	The parsimonious protein inference in this program is based on the IDPicker algorithm published in:<br>
 	<i>Proteomic Parsimony through Bipartite Graph Analysis Improves Accuracy and Transparency.</i>
 	&nbsp; &nbsp;
	<br>Tabb <i>et. al.</i> <i>J Proteome Res.</i> 2007 Sep;6(9):3549-57
	
	<br/>
	<br/>
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">
	Parsimonious Protein Inference<br/></div>
	<li><b>Step 1:</b></li>
	A bipartitie graph is created with edges between peptides and their matching proteins.<br/>
	<div align="center" style="margin: 5px;">
	<img src="<yrcwww:link path='images/docs/protinfer1.png'/>" border="1"/>
	</div> <br/> <br/>
	
	<li><b>Step 2:</b></li>
	Peptides that match the same set of proteins are merged into a single node in the graph.
	For example, peptides 3, 7, and 9 match protein A and no other protein.<br/>
	<div align="center" style="margin: 5px;">
	<img src="<yrcwww:link path='images/docs/protinfer2.png'/>" border="1"/>
	</div> <br/> <br/>
	
	<li><b>Step 3:</b></li>
	Proteins that match the same set of peptide are merged into a single node in the graph. These proteins comprise
	an <span style="color:red;"><b>indistinguishable protein group</b></span>. <br/>
	<div align="center" style="margin: 5px;">
	<img src="<yrcwww:link path='images/docs/protinfer3.png'/>" border="1"/>
	</div> <br/> <br/>
	
	<li><b>Step 4:</b></li>
	The graph is then resolved into its connected components, or proteins that share peptides. 
	Each connected component is referred to as a <b>protein cluster</b>.<br/>
	<div align="center" style="margin: 5px;">
	<img src="<yrcwww:link path='images/docs/protinfer4.png'/>" border="1"/>
	</div> <br/> <br/>
	
	<li><b>Step 5:</b></li>
	The smallest set of proteins sufficient to explain the peptides in each cluster are marked as parsimonious.<br/>
	<div align="center" style="margin: 5px;">
	<img src="<yrcwww:link path='images/docs/protinfer5.png'/>" border="1"/>
	</div> <br/> <br/>
	
	<br/>
	<br/>
	
	<!-- OPTIONS -->
	<a name="PI_OPTIONS"></a>
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">
	Program Options<br/></div>
	Protein inference implemented in MSDaPl takes <a href="http://noble.gs.washington.edu/proj/percolator/" target="_blank">Percolator</a>
	results as input.<br><br/>
	Results can be filtered on <i>q-value</i> and <i>Posterior Error Probability (PEP)</i> calculated by Percolator.
	<br/><br/>
	Proteins (indistinguishable protein groups) can be filtered on the number of peptides and number of unique peptides identified.
	<br/>
	The number of peptides can be calculated as one of the following:<br/>
	<table>
	<tr>
	<td valign="top">
		<li>unique peptide sequences</li>
		<li>unique modified peptide sequence</li>
		<li>Unique combination of peptide sequence + charge</li>
		<li>Unique ions (sequence + charge + modifications)</li>
	</td>
	<td valign="top">
		<img src="<yrcwww:link path='images/docs/protinfer_opts1.png'/>" border="1" align="middle"/>
	</td>
	</tr>
	</table>
	<br/><br/>
	If the "Remove Ambiguous Spectra" option is checked <br/><br/>
	<img src="<yrcwww:link path='images/docs/protinfer_opts2.png'/>" border="1"/>
	<br/>
	<br/>
	any spectra that have 2 or more Percolator results that pass the q-value threshold are removed from the analysis.
	<br/>
	
	
	<br/>
	<br/>
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">
	Results View<br/></div>
	Coming Soon...
	
	<br/>
	<br/>
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">
	Normalized Spectrum Abundance Factor (NSAF)<br/></div>
	Coming Soon...
	
</yrcwww:contentbox>