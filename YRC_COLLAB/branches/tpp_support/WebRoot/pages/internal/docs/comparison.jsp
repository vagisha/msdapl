<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<head>
 <title>MSDaPl Docs: Protein Inference Comparison</title>
 <link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='/css/global.css' />">
</head>

<yrcwww:contentbox centered="true" width="70" widthRel="true" title="Protein inference comparison">
	
	<div style="border: 1px dotted; margin:10x; padding:10px;">
	MSDaPl supports comparing results from two or more protein inference runs.  These can be results from the protein inference
	program implemented in MSDaPl and / or ProteinProphet results. 
	<br/>
	<br/>
	
	The default behavior is to include proteins that were inferred as parsimonious in one or more runs being compared.<br/>
	<b>NOTE: </b> For ProteinProphet parsimonious = NOT subsumed.  
	You can change this behavior by selecting one of the other two available options:<br/><br/>
	<div align="center"><img src="<yrcwww:link path='images/docs/comparison1.png'/>" border="1"/></div>
	<br/><br/>
	
	When comparing protein runs you can choose to <b><span style="color:red">group indistinguishable proteins</span></b>. This is the default option.
	If this option is NOT selected information about shared peptides among proteins is ignored when displaying the results. 
	With this option checked, proteins with identical set of peptides (indistinguishable proteins) are displayed together.
	The figure below explains the process of building a list of indistinguishable protein groups from 2 runs being compared.
	<br/>
	<div align="center" style="margin:10px;"><img src="<yrcwww:link path='images/docs/comparison2.png'/>" border="1"/></div>
	<br/><br/>
	
	</div>
	
</yrcwww:contentbox>