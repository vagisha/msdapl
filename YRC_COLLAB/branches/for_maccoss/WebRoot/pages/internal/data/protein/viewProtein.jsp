<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:empty name="protein">
  <logic:forward name="viewProtein" />
</logic:empty>

<%@ include file="/includes/header.jsp" %>

<yrcwww:contentbox title="View Protein Information" centered="true" width="750" scheme="search">

 <CENTER>
 <TABLE CELLPADDING="no" CELLSPACING="0" WIDTH="80%"> 

  <yrcwww:colorrow scheme="search">
   <TD valign="top" width="100%" colspan="2" align="center"><font style="font-size:12pt;"><b>General Information:</b></font><br><br></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="search">
   <TD valign="top" width="25%">Name(s) found:</TD>
   <TD valign="top" width="75%">
    
    <logic:empty name="protein" property="bestReferences">
     NONE FOUND
    </logic:empty>
    <logic:notEmpty name="protein" property="bestReferences">
    
     <logic:iterate name="protein" property="bestReferences" id="reference">
      
      <logic:notEmpty name="reference" property="URL">
		<a target="reference_window" href="<bean:write name="reference" property="URL"/>">
		  <logic:notEmpty name="reference" property="proteinName">
		   <bean:write name="reference" property="proteinName"/> /
		  </logic:notEmpty>
		  <bean:write name="reference" property="accessionString"/></a>

		  <font style="font-size:8pt;">[<bean:write name="reference" property="databaseName"/>]</font>
      </logic:notEmpty>

      <logic:empty name="reference" property="URL">
		  <logic:notEmpty name="reference" property="proteinName">
			   <bean:write name="reference" property="proteinName"/> /
		  </logic:notEmpty>
		  
		  <bean:write name="reference" property="accessionString"/>
		  <font style="font-size:8pt;">[<bean:write name="reference" property="databaseName"/>]</font>
      </logic:empty>


      <br>
     </logic:iterate>
    </logic:notEmpty>
   </TD>
  </yrcwww:colorrow>
  
  <yrcwww:colorrow scheme="search">
   <TD valign="top" width="25%">Description(s) found:</TD>
   <TD valign="top" width="75%">
    
    <logic:empty name="protein" property="bestReferences">
     NONE FOUND
    </logic:empty>
    <logic:notEmpty name="protein" property="bestReferences">
    
     <logic:iterate name="protein" property="bestReferences" id="reference">
       <li><bean:write name="reference" property="description"/>
      <font style="font-size:8pt;">[<bean:write name="reference" property="databaseName"/>]</font>
      <br>
     </logic:iterate>
    </logic:notEmpty>
   </TD>
  </yrcwww:colorrow>
  
  <yrcwww:colorrow scheme="search">
   <TD valign="top" width="25%">Organism:</TD>
   <TD valign="top" width="75%">
    <a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=<bean:write name="protein" property="species.id"/>">
    <i><bean:write name="protein" property="species.name" /></i></a>
   </TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="search">
   <TD valign="top" width="25%">Length:</TD>
   <TD valign="top" width="75%"><bean:write name="protein" property="peptide.length" filter="false"/> amino acids</TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="search">
   <TD valign="top" width="100%" colspan="2" align="center"><br><font style="font-size:12pt;"><b>Gene Ontology:</b></font><br><br></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="search">
   <TD valign="top" width="25%">Cellular Component:</TD>
   <TD valign="top" width="75%">
    
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
    
   </TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="search">
   <TD valign="top" width="25%">Biological Process:</TD>
   <TD valign="top" width="75%">
    
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
    
   </TD>
  </yrcwww:colorrow>


  <yrcwww:colorrow scheme="search">
   <TD valign="top" width="25%">Molecular Function:</TD>
   <TD valign="top" width="75%">
    
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
    
   </TD>
  </yrcwww:colorrow>

 </TABLE>

<br><br>

 <TABLE CELLPADDING="no" CELLSPACING="0"> 

  <yrcwww:colorrow scheme="search">
   <TD valign="top" width="100%" colspan="2" align="center"><font style="font-size:12pt;"><b>Sequence:</b></font><br><br></TD>
  </yrcwww:colorrow>
  
  <yrcwww:colorrow scheme="search">
   <TD valign="top" width="15%">Sequence:<BR>
    <font style="font-size:8pt;">
     [<a target="blast_window"
         href="http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?CMD=Web&LAYOUT=TwoWindows&AUTO_FORMAT=Semiauto&ALIGNMENTS=50&ALIGNMENT_VIEW=Pairwise&CDD_SEARCH=on&CLIENT=web&COMPOSITION_BASED_STATISTICS=on&DATABASE=nr&DESCRIPTIONS=100&ENTREZ_QUERY=(none)&EXPECT=1000&FILTER=L&FORMAT_OBJECT=Alignment&FORMAT_TYPE=HTML&I_THRESH=0.005&MATRIX_NAME=BLOSUM62&NCBI_GI=on&PAGE=Proteins&PROGRAM=blastp&SERVICE=plain&SET_DEFAULTS.x=41&SET_DEFAULTS.y=5&SHOW_OVERVIEW=on&END_OF_HTTPGET=Yes&SHOW_LINKOUT=yes&QUERY=<bean:write name="protein" property="peptide.sequenceString"/>">NCBI BLAST</a>]

     <BR>
     [<a target="prot_param_window"
         href="http://us.expasy.org/cgi-bin/protparam?sequence=<bean:write name="protein" property="peptide.sequenceString"/>">ProtParam</a>]
    </font>
   </TD>
   <TD valign="top" width="85%"><pre><bean:write name="protein" property="peptide.sequenceStringFormatted" filter="false"/></pre></TD>
  </yrcwww:colorrow>
 </TABLE>

 </CENTER>
</yrcwww:contentbox>

<!-- List the Y2H Data here: -->
<%@ include file="../../project/listY2HData.jsp" %>

<!-- List the YATES Data here: -->
<%@ include file="../../project/listYatesData.jsp" %>

<!-- List the LOCALIZATION Data here: -->
<%@ include file="../../project/listLocalizationData.jsp" %>

<%@ include file="/includes/footer.jsp" %>