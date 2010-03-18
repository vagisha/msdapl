<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<head>
 <title>MSDaPl Docs: Protein Inference Comparison</title>
 <link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='/css/global.css' />">
</head>

<yrcwww:contentbox centered="true" width="90" widthRel="true" title="Protein inference comparison">
	
	<div style="border: 1px dotted; margin:10x; padding:10px;">
	MSDaPl supports comparing results from two or more protein inference runs.  These can be results from the protein inference
	program implemented in MSDaPl and/or ProteinProphet results. 
	<br/>
	<br/>
	
	<!-- OPTIONS -->
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">
	Options<br/></div>
	
	<li>The default behavior is to include proteins that were inferred as parsimonious in one or more runs being compared.<br/>
	<b>NOTE: </b> For ProteinProphet parsimonious = NOT subsumed.  
	You can change this behavior by selecting one of the other two available options:</li>
	<div align="center" style="margin:10px;"><img src="<yrcwww:link path='images/docs/comparison1.png'/>" border="1"/></div>
	
	
	<li>Proteins can be filtered on the accession strings in the fasta file(s) used for peptide search.
	Support for filtering on common names will be added soon.</li>
	
	<li>Filtering criteria can either be applied to individual proteins or to protein groups
	(only when "Group Indistinguishable Proteins" is checked).</li>
	
	<div align="center" style="margin:10px;"><img src="<yrcwww:link path='images/docs/comparison3.png'/>" border="1"/></div>
	
	With "Keep Protein Groups" checked a protein group is filtered out of the final list only if ALL members
	of the group fail to pass the filtering criteria.
	<br/><br/>
	
	<!-- COMPARISON WITH INDISTINGUISHABLE PROTEIN GROUPS -->
	<br/>
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">
	Comparison with indistinguishable protein groups<br/></div>
	When comparing protein runs you can choose to <b><span style="color:red">group indistinguishable proteins</span></b>. This is the default option.
	If this option is NOT selected information about shared peptides among proteins is ignored when displaying the results. 
	With this option checked, proteins with identical set of peptides (indistinguishable proteins) are displayed together.
	The figure below explains the process of building a list of indistinguishable protein groups from 2 runs being compared.
	<br/>
	<div align="center" style="margin:10px;"><img src="<yrcwww:link path='images/docs/comparison2.png'/>" border="1"/></div>
	<br/><br/>
	
	The results from the comparison in the figure above will be displayed as: 
	<br/>
	<div align="center" style="margin:10px;"><img src="<yrcwww:link path='images/docs/comparison4.png'/>" border="1"/></div>
	<br/><br/>
	
	
	<!-- SPECTRUM COUNTS -->
	<br/>
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">
	Spectrum Counts<br/></div>
	Two numbers are displayed in the spectrum counts columns for a protein in each dataset.
	The first is the number of filtered (after any cutoffs applied during the protein inference process) spectra 
	for a protein.  The second number, in parentheses, is the normalized spectrum count. 
	Normalization is done using the total (filtered) spectrum counts for the datasets being compared. 
	
	</div>
</yrcwww:contentbox>