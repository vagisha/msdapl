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
   opener.document.forms[0].axisI.value = output;
   alert("AXIS I information updated.  This change will be only be saved if you save this project.");
   window.close();

}

</SCRIPT>

<yrcwww:contentbox title="SELECT AXIS I" centered="true" width="810" scheme="project">

<html:form action="AXIS" method="POST">

<TABLE BORDER="0" CELLPADDING="NO" CELLSPACING="5">
<TR>
<TD><html:multibox property="AXIS" value="1A"/>
<FONT STYLE="font-size:10pt;">1A : Animals, Whole -> Vertebrates, Mammals</FONT></TD>
<TD><html:multibox property="AXIS" value="7C"/>
<FONT STYLE="font-size:10pt;">7C : Microorganisms -> Parasites</FONT></TD>
<TD><html:multibox property="AXIS" value="17"/>

<FONT STYLE="font-size:10pt;">17 : Hematologic System</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="1B"/>
<FONT STYLE="font-size:10pt;">1B : Animals, Whole -> Vertebrates, Non-Mammal</FONT></TD>
<TD><html:multibox property="AXIS" value="7D"/>
<FONT STYLE="font-size:10pt;">7D : Microorganisms -> Other</FONT></TD>
<TD><html:multibox property="AXIS" value="18"/>
<FONT STYLE="font-size:10pt;">18 : Integumentary/Skin System</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="1C"/>
<FONT STYLE="font-size:10pt;">1C : Animals, Whole -> Invertebrates</FONT></TD>
<TD><html:multibox property="AXIS" value="8"/>
<FONT STYLE="font-size:10pt;">8 : Plants/Fungi</FONT></TD>
<TD><html:multibox property="AXIS" value="19"/>
<FONT STYLE="font-size:10pt;">19 : Lymphatic and Recticulo-Edothelial System</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="1D"/>
<FONT STYLE="font-size:10pt;">1D : Animals, Cell/Membrane/Tissue/Organ -> Vertebrates, Mammal</FONT></TD>
<TD><html:multibox property="AXIS" value="9"/>
<FONT STYLE="font-size:10pt;">9 : Technology/Technique Development</FONT></TD>
<TD><html:multibox property="AXIS" value="20"/>
<FONT STYLE="font-size:10pt;">20 : Muscular System</FONT></TD>

</TR>
<TR>
<TD><html:multibox property="AXIS" value="1E"/>
<FONT STYLE="font-size:10pt;">1E : Animals, Cell/Membrane/Tissue/Organ -> Vertebrates, Non-Mammal</FONT></TD>
<TD><html:multibox property="AXIS" value="11"/>
<FONT STYLE="font-size:10pt;">11 : Facility Construction/Improvement</FONT></TD>
<TD><html:multibox property="AXIS" value="21"/>
<FONT STYLE="font-size:10pt;">21 : Nervous System</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="1F"/>
<FONT STYLE="font-size:10pt;">1F : Animals, Cell/Membrane/Tissue/Organ -> Invertebrates</FONT></TD>
<TD><html:multibox property="AXIS" value="12A"/>
<FONT STYLE="font-size:10pt;">12A : Clinical Trials -> Multi-center</FONT></TD>
<TD><html:multibox property="AXIS" value="22"/>
<FONT STYLE="font-size:10pt;">22 : Oral/Dental</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="2"/>
<FONT STYLE="font-size:10pt;">2 : Biological/Chemical Compounds</FONT></TD>
<TD><html:multibox property="AXIS" value="12B"/>
<FONT STYLE="font-size:10pt;">12B : Clinical Trials -> Single Center</FONT></TD>
<TD><html:multibox property="AXIS" value="23"/>
<FONT STYLE="font-size:10pt;">23 : Reproductive System</FONT></TD>
</TR>

<TR>
<TD><html:multibox property="AXIS" value="3"/>
<FONT STYLE="font-size:10pt;">3 : Biomaterials</FONT></TD>
<TD><html:multibox property="AXIS" value="13"/>
<FONT STYLE="font-size:10pt;">13 : Cardiovascular System</FONT></TD>
<TD><html:multibox property="AXIS" value="24"/>
<FONT STYLE="font-size:10pt;">24 : Respiratory System</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="4"/>
<FONT STYLE="font-size:10pt;">4 : Human, Cells Only</FONT></TD>
<TD><html:multibox property="AXIS" value="14"/>
<FONT STYLE="font-size:10pt;">14 : Connective Tissue</FONT></TD>
<TD><html:multibox property="AXIS" value="25A"/>
<FONT STYLE="font-size:10pt;">25A : Sensory System -> Ear</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="5A"/>
<FONT STYLE="font-size:10pt;">5A : Human, Adult -> Female</FONT></TD>
<TD><html:multibox property="AXIS" value="15"/>
<FONT STYLE="font-size:10pt;">15 : Endocrine System</FONT></TD>
<TD><html:multibox property="AXIS" value="25B"/>
<FONT STYLE="font-size:10pt;">25B : Sensory System -> Eye</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="5B"/>
<FONT STYLE="font-size:10pt;">5B : Human, Adult -> Male</FONT></TD>
<TD><html:multibox property="AXIS" value="16A"/>
<FONT STYLE="font-size:10pt;">16A : Gastrointestinal System -> Esophagus</FONT></TD>
<TD><html:multibox property="AXIS" value="25C"/>
<FONT STYLE="font-size:10pt;">25C : Sensory System -> Taste/Smell</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="5A"/>
<FONT STYLE="font-size:10pt;">5A : Human, Infant/Child -> Female</FONT></TD>
<TD><html:multibox property="AXIS" value="16B"/>
<FONT STYLE="font-size:10pt;">16B : Gastrointestinal System -> Gallbladder</FONT></TD>
<TD><html:multibox property="AXIS" value="25D"/>

<FONT STYLE="font-size:10pt;">25D : Sensory System -> Touch</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="5B"/>
<FONT STYLE="font-size:10pt;">5B : Human, Infant/Child -> Male</FONT></TD>
<TD><html:multibox property="AXIS" value="16C"/>
<FONT STYLE="font-size:10pt;">16C : Gastrointestinal System -> Intestine</FONT></TD>
<TD><html:multibox property="AXIS" value="26"/>
<FONT STYLE="font-size:10pt;">26 : Skeletal System</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="6"/>
<FONT STYLE="font-size:10pt;">6 : Human, Membrane/Tissue/Isolated Organ</FONT></TD>
<TD><html:multibox property="AXIS" value="16D"/>
<FONT STYLE="font-size:10pt;">16D : Gastrointestinal System -> Liver</FONT></TD>
<TD><html:multibox property="AXIS" value="27"/>
<FONT STYLE="font-size:10pt;">27 : Urinary System/Kindney/Renal</FONT></TD>
</TR>
<TR>
<TD><html:multibox property="AXIS" value="7A"/>
<FONT STYLE="font-size:10pt;">7A : Microorganisms -> Bacteria</FONT></TD>
<TD><html:multibox property="AXIS" value="16E"/>
<FONT STYLE="font-size:10pt;">16E : Gastrointestinal System -> Pancreas</FONT></TD>
<TD><html:multibox property="AXIS" value="28"/>
<FONT STYLE="font-size:10pt;">28 : Other (SPECIFY)</FONT></TD>

</TR>
<TR>
<TD><html:multibox property="AXIS" value="7B"/>
<FONT STYLE="font-size:10pt;">7B : Microorganisms -> Viruses</FONT></TD>
<TD><html:multibox property="AXIS" value="16F"/>
<FONT STYLE="font-size:10pt;">16F : Gastrointestinal System -> Stomach</FONT></TD>
<TD>&nbsp;</TD>
</TR>
</TABLE>
<P ALIGN="CENTER"><INPUT TYPE="button" onClick="changeAXIS()" value="KEEP CHANGES">
<INPUT TYPE="button" onClick="window.close()" value="CANCEL">

</html:form>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>