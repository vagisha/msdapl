<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<%@ include file="/includes/bareHeader.jsp" %>


<SCRIPT LANGUAGE="JavaScript">

/**
 *	JavaScript code written by Michael Riffle <mriffle@u.washington.edu>
 *	June 22, 2002
 */

// Sort two objects, based on their value property
function checkboxSort(a, b) {
   var lastAChar, lastBChar;

   lastAChar = a.value.charAt(a.value.length - 1);
   lastBChar = b.value.charAt(b.value.length - 1);

   if(parseInt(a.value) == parseInt(b.value)) {
      if(lastAChar >= lastBChar) { return 1; }
      return -1;
   }

   return parseInt(a.value) - parseInt(b.value);
}

function changeAXIS() {
   var LIST = document.axisForm.AXIS;
   var i;
   var output;

   // Need to set up the array, because LIST isn't a true array and doesn't support sorting
   var myArray = new Array(LIST.length);
   for(i=0; i<LIST.length; i++) {
      myArray[i] = LIST[i];
   }

   // Sort this array based on the value property of each checkbox object
   myArray.sort(checkboxSort);

   // Generate the output string we're sending back to the main form
   output = "";
   for(i=0; i<myArray.length; i++) {
      if(myArray[i].checked) { 
         if(output.length != 0) { output += " "; }
         output += myArray[i].value;
      }
   }

   // Send the output string to the main form, and close this window
   opener.document.forms[0].axisII.value = output;
   alert("AXIS II information updated.  This change will be only be saved if you save this project.");
   window.close();

}

</SCRIPT>

<yrcwww:contentbox title="SELECT AXIS II" centered="true" width="810" scheme="project">

<html:form action="AXIS" method="POST">

<TABLE BORDER="0" CELLPADDING="NO" CELLSPACING="5">
<TR>
<TD><html:multibox property="AXIS" value="30"/>
<FONT STYLE="font-size:10pt;">30 : Aging</FONT></TD>
<TD><html:multibox property="AXIS" value="59"/>
<FONT STYLE="font-size:10pt;">59 : Genome</FONT></TD>
<TD><html:multibox property="AXIS" value="74H"/>

<FONT STYLE="font-size:10pt;">74H : Metabolism/Biochemistry/Physiology/Structure -> Protein/Amino Acids</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="31"/>
<FONT STYLE="font-size:10pt;">31 : AIDS, SAIDS</FONT></TD>
<TD><html:multibox property="AXIS" value="60"/>
<FONT STYLE="font-size:10pt;">60 : Growth and Development</FONT></TD>
<TD><html:multibox property="AXIS" value="75A"/>
<FONT STYLE="font-size:10pt;">75A : Minority Health -> Asian/Pacific Islands</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="33"/>
<FONT STYLE="font-size:10pt;">33 : Alternative Medicine</FONT></TD>

<TD><html:multibox property="AXIS" value="62"/>
<FONT STYLE="font-size:10pt;">62 : Health Care Applications</FONT></TD>
<TD><html:multibox property="AXIS" value="75B"/>
<FONT STYLE="font-size:10pt;">75B : Minority Health -> Afro-American</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="32"/>
<FONT STYLE="font-size:10pt;">32 : Anesthesiology</FONT></TD>
<TD><html:multibox property="AXIS" value="63A"/>
<FONT STYLE="font-size:10pt;">63A : Imaging -> CT</FONT></TD>
<TD><html:multibox property="AXIS" value="75C"/>
<FONT STYLE="font-size:10pt;">75C : Minority Health -> Hispanic</FONT></TD>

</TR>
<TR>
<TD><html:multibox property="AXIS" value="34"/>
<FONT STYLE="font-size:10pt;">34 : Anthropology/Ethnography</FONT></TD>
<TD><html:multibox property="AXIS" value="63B"/>
<FONT STYLE="font-size:10pt;">63B : Imaging -> Laser</FONT></TD>
<TD><html:multibox property="AXIS" value="75D"/>
<FONT STYLE="font-size:10pt;">75D : Minority Health -> Native American</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="35"/>
<FONT STYLE="font-size:10pt;">35 : Arthritis</FONT></TD>
<TD><html:multibox property="AXIS" value="63C"/>

<FONT STYLE="font-size:10pt;">63C : Imaging -> MRI, MRS</FONT></TD>
<TD><html:multibox property="AXIS" value="75E"/>
<FONT STYLE="font-size:10pt;">75E : Minority Health -> Other</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="36"/>
<FONT STYLE="font-size:10pt;">36 : Behavior/Psychology/Social Science</FONT></TD>
<TD><html:multibox property="AXIS" value="63E"/>
<FONT STYLE="font-size:10pt;">63E : Imaging -> PET</FONT></TD>
<TD><html:multibox property="AXIS" value="77"/>
<FONT STYLE="font-size:10pt;">77 : Model Development</FONT></TD>
</TR>

<TR>
<TD><html:multibox property="AXIS" value="38"/>
<FONT STYLE="font-size:10pt;">38 : Bioethics</FONT></TD>
<TD><html:multibox property="AXIS" value="63F"/>
<FONT STYLE="font-size:10pt;">63F : Imaging -> Spec</FONT></TD>
<TD><html:multibox property="AXIS" value="76A"/>
<FONT STYLE="font-size:10pt;">76A : Neoplasms/Oncology/Cancer -> Malignant</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="39"/>
<FONT STYLE="font-size:10pt;">39 : Biotechnology</FONT></TD>
<TD><html:multibox property="AXIS" value="63G"/>
<FONT STYLE="font-size:10pt;">63G : Imaging -> Radiography</FONT></TD>

<TD><html:multibox property="AXIS" value="76B"/>
<FONT STYLE="font-size:10pt;">76B : Neoplasms/Oncology/Cancer -> Benign</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="41"/>
<FONT STYLE="font-size:10pt;">41 : Cognition/Learning</FONT></TD>
<TD><html:multibox property="AXIS" value="63I"/>
<FONT STYLE="font-size:10pt;">63I : Imaging -> Microscopy</FONT></TD>
<TD><html:multibox property="AXIS" value="78"/>
<FONT STYLE="font-size:10pt;">78 : Nutritian</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="40"/>

<FONT STYLE="font-size:10pt;">40 : Communication/Speech</FONT></TD>
<TD><html:multibox property="AXIS" value="63J"/>
<FONT STYLE="font-size:10pt;">63J : Imaging -> Near Infrared</FONT></TD>
<TD><html:multibox property="AXIS" value="67"/>
<FONT STYLE="font-size:10pt;">67 : Nursing Care Research</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="42"/>
<FONT STYLE="font-size:10pt;">42 : Computer Science</FONT></TD>
<TD><html:multibox property="AXIS" value="63K"/>
<FONT STYLE="font-size:10pt;">63K : Imaging -> Synchotron</FONT></TD>
<TD><html:multibox property="AXIS" value="79"/>

<FONT STYLE="font-size:10pt;">79 : Pain</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="44"/>
<FONT STYLE="font-size:10pt;">44 : Congenital Defects of Malformations</FONT></TD>
<TD><html:multibox property="AXIS" value="64"/>
<FONT STYLE="font-size:10pt;">64 : Immunology/Allergy/Inflammation</FONT></TD>
<TD><html:multibox property="AXIS" value="94"/>
<FONT STYLE="font-size:10pt;">94 : Prevention</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="45"/>
<FONT STYLE="font-size:10pt;">45 : Deafness/Hearing</FONT></TD>

<TD><html:multibox property="AXIS" value="65"/>
<FONT STYLE="font-size:10pt;">65 : Infant Mortality/Low Birth Weight</FONT></TD>
<TD><html:multibox property="AXIS" value="80"/>
<FONT STYLE="font-size:10pt;">80 : Radiology/Radiation Nuclear Medicine</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="46"/>
<FONT STYLE="font-size:10pt;">46 : Degenerative Disorders</FONT></TD>
<TD><html:multibox property="AXIS" value="66"/>
<FONT STYLE="font-size:10pt;">66 : Infectious Diseases</FONT></TD>
<TD><html:multibox property="AXIS" value="81"/>
<FONT STYLE="font-size:10pt;">81 : Rare Disease</FONT></TD>

</TR>
<TR>
<TD><html:multibox property="AXIS" value="48"/>
<FONT STYLE="font-size:10pt;">48 : Device, Protheses, Intra/Extracoporeal</FONT></TD>
<TD><html:multibox property="AXIS" value="68"/>
<FONT STYLE="font-size:10pt;">68 : Information Science</FONT></TD>
<TD><html:multibox property="AXIS" value="82"/>
<FONT STYLE="font-size:10pt;">82 : Rehabilitation</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="49"/>
<FONT STYLE="font-size:10pt;">49 : Diabetes</FONT></TD>
<TD><html:multibox property="AXIS" value="70"/>

<FONT STYLE="font-size:10pt;">70 : Instrument Development</FONT></TD>
<TD><html:multibox property="AXIS" value="83"/>
<FONT STYLE="font-size:10pt;">83 : Sexually Transmitted Diseases</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="50A"/>
<FONT STYLE="font-size:10pt;">50A : Drug/Therapeutic Agent Studies -> Toxic</FONT></TD>
<TD><html:multibox property="AXIS" value="69"/>
<FONT STYLE="font-size:10pt;">69 : International Health</FONT></TD>
<TD><html:multibox property="AXIS" value="85"/>
<FONT STYLE="font-size:10pt;">85 : Sleep Research</FONT></TD>
</TR>

<TR>
<TD><html:multibox property="AXIS" value="50B"/>
<FONT STYLE="font-size:10pt;">50B : Drug/Therapeutic Agent Studies -> Other</FONT></TD>
<TD><html:multibox property="AXIS" value="71"/>
<FONT STYLE="font-size:10pt;">71 : Maternal & Child Health</FONT></TD>
<TD><html:multibox property="AXIS" value="84"/>
<FONT STYLE="font-size:10pt;">84 : Statistics/Mathematics</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="50C"/>
<FONT STYLE="font-size:10pt;">50C : Drug/Therapeutic Agent Studies -> Orphan Drugs</FONT></TD>
<TD><html:multibox property="AXIS" value="72"/>

<FONT STYLE="font-size:10pt;">72 : Mental disorders/Psychiatry</FONT></TD>
<TD><html:multibox property="AXIS" value="89"/>
<FONT STYLE="font-size:10pt;">89 : Structural Biology</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="51"/>
<FONT STYLE="font-size:10pt;">51 : Education</FONT></TD>
<TD><html:multibox property="AXIS" value="73"/>
<FONT STYLE="font-size:10pt;">73 : Men's Health</FONT></TD>
<TD><html:multibox property="AXIS" value="87"/>
<FONT STYLE="font-size:10pt;">87 : Substance Abuse</FONT></TD>
</TR>

<TR>
<TD><html:multibox property="AXIS" value="52"/>
<FONT STYLE="font-size:10pt;">52 : Engineering/Bioengineering</FONT></TD>
<TD><html:multibox property="AXIS" value="74A"/>
<FONT STYLE="font-size:10pt;">74A : Metabolism/Biochemistry/Physiology/Structure -> Carbohydrate</FONT></TD>
<TD><html:multibox property="AXIS" value="86"/>
<FONT STYLE="font-size:10pt;">86 : Surgery</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="54A"/>
<FONT STYLE="font-size:10pt;">54A : Environmental Studies -> Toxic</FONT></TD>
<TD><html:multibox property="AXIS" value="74B"/>
<FONT STYLE="font-size:10pt;">74B : Metabolism/Biochemistry/Physiology/Structure -> Electrolyte/Mineral</FONT></TD>

<TD><html:multibox property="AXIS" value="95"/>
<FONT STYLE="font-size:10pt;">95 : Transgenics</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="54B"/>
<FONT STYLE="font-size:10pt;">54B : Environmental Studies -> Other</FONT></TD>
<TD><html:multibox property="AXIS" value="74C"/>
<FONT STYLE="font-size:10pt;">74C : Metabolism/Biochemistry/Physiology/Structure -> Enzymes</FONT></TD>
<TD><html:multibox property="AXIS" value="88"/>
<FONT STYLE="font-size:10pt;">88 : Transplantation</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="56"/>

<FONT STYLE="font-size:10pt;">56 : Epidemiology</FONT></TD>
<TD><html:multibox property="AXIS" value="74D"/>
<FONT STYLE="font-size:10pt;">74D : Metabolism/Biochemistry/Physiology/Structure -> Gases</FONT></TD>
<TD><html:multibox property="AXIS" value="90"/>
<FONT STYLE="font-size:10pt;">90 : Trauma/Burns/Injury</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="57"/>
<FONT STYLE="font-size:10pt;">57 : Fitness, Physical</FONT></TD>
<TD><html:multibox property="AXIS" value="74E"/>
<FONT STYLE="font-size:10pt;">74E : Metabolism/Biochemistry/Physiology/Structure -> Hormone</FONT></TD>
<TD><html:multibox property="AXIS" value="91"/>

<FONT STYLE="font-size:10pt;">91 : Vaccine</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="55"/>
<FONT STYLE="font-size:10pt;">55 : Gene Therapy</FONT></TD>
<TD><html:multibox property="AXIS" value="74F"/>
<FONT STYLE="font-size:10pt;">74F : Metabolism/Biochemistry/Physiology/Structure -> Lipid</FONT></TD>
<TD><html:multibox property="AXIS" value="93"/>
<FONT STYLE="font-size:10pt;">93 : Women's Health</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="58"/>
<FONT STYLE="font-size:10pt;">58 : Genetics, Including Metabolic Errors</FONT></TD>

<TD><html:multibox property="AXIS" value="74G"/>
<FONT STYLE="font-size:10pt;">74G : Metabolism/Biochemistry/Physiology/Structure -> Nucleic Acid</FONT></TD>
<TD><html:multibox property="AXIS" value="92"/>
<FONT STYLE="font-size:10pt;">92 : Other (SPECIFY)</FONT></TD>
</TR>
</TABLE>

<P ALIGN="CENTER"><INPUT TYPE="button" onClick="changeAXIS()" value="KEEP CHANGES">
<INPUT TYPE="button" onClick="window.close()" value="CANCEL">

</html:form>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>