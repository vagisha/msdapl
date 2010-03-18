<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<head>
 <title>MSDaPl Docs: Data Upload</title>
 <link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='/css/global.css' />">
</head>
<yrcwww:contentbox centered="true" width="90" widthRel="true" title="Uploading Data">
	
	<div style="border: 1px dotted; margin:10x; padding:10px;">
	
	The fasta file used for peptide search has to be uploaded to our protein database BEFORE you upload your search results.
	You can check the fasta files available for your lab by clicking on the "<html:link action="availableFasta.do">Available FASTA</html:link>" link in the menu.
	If you do not see your file in the list please contact the administrator for uploading your fasta file. 
	<br/><br/>
	
	
	MSDaPl supports data from two proteomic pipelines:
	<li><b>MacCoss Lab's pipeline</b></li>
	<li><b>Trans-Proteomic Pipeline (TPP)</b></li>
	<br/><br/>
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">Requirements for data from the MacCoss Lab's pipeline<br/></div>
	<br/>
	There are two options for required directory structure:
	<br/><br/>
	Option 1:
	<pre style="font-size:8pt;">
	Experiment directory
	|
	|---- pipeline/sequest (contains Sequest .sqt files, sequest.params and ms2 or cms2 files)
	|
	|---- pipeline/percolator (contains Percolator's .sqt files)
	|
	|---- pipeline/dtaselect/sequest (contains DTASelect-filter.txt)
	</pre>
	<br/>
	Option 2:
	<pre style="font-size:8pt;">
	Experiment directory
	|
	|---- sequest (contains Sequest .sqt files, sequest.params and ms2 or cms2 files)
	|
	|---- percolator (contains Percolator's .sqt files)
	|
	|---- dtaselect/sequest (contains DTASelect-filter.txt)
	</pre>
	<br/>
	<br/>
	
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">Requirements for data from the TPP<br/></div>
	<br/>
	The following files should be available in the experiment directory:
	<li>mzXML files</li>
	<li>pepXML files with Sequest search results. There should be one corresponding to each mzXML file.</li>
	<li>sequest.params file used for database search</li>
	<li>interact.pep.xml file with PeptideProphet results</li>
	<li>interact.prot.xml file with ProteinProphet results</li>
	
	<br/><br/>
	
	</div>
</yrcwww:contentbox>