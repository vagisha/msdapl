<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<logic:notPresent name="Y2HProjects" scope="session">
  <logic:redirect href="/yrc/uploadY2HFormAction.do" />
</logic:notPresent>

<%@ include file="/includes/adminHeader.jsp" %>

<%@ include file="/includes/errors.jsp" %>
<logic:present name="saved" scope="request">
 <center>
 <hr width="50%">
  <B><font color="red">Screen was successfully saved.</font></B>
 <hr width="50%">
 </center>
</logic:present>

<yrcwww:contentbox title="Upload Yeast Two-Hybrid Data" centered="true" width="700" scheme="upload">

<P align="center">To upload yeast two-hybrid data, please fill out the simple form below.

<html:form action="uploadY2H" method="POST" enctype="multipart/form-data">

 <CENTER>
  <table border="0">
   <tr>
    <td colspan="2">Select the project to which this data belongs:</td>
   </tr>
   <tr>
    <td colspan="2">
     <html:select property="projectID">
		<html:option value="0">PLEASE SELECT A PROJECT</html:option>
		<html:optionsCollection name="Y2HProjects" value="ID" label="label"/>
     </html:select>
    </td>
   </tr>

   <tr>
    <td>Data file:</td>
    <td><html:file property="dataFile" size="30"/></td>
   </tr>
   
   <tr>
    <td><br>Screen date:</td>
    <td><br>
     <html:select property="year">
      <html:option value="0">Year</html:option>
      <html:option value="1998">1998</html:option>
      <html:option value="1999">1999</html:option>
      <html:option value="2000">2000</html:option>
      <html:option value="2001">2001</html:option>
      <html:option value="2002">2002</html:option>
      <html:option value="2003">2003</html:option>
      <html:option value="2004">2004</html:option>
      <html:option value="2005">2005</html:option>
      <html:option value="2006">2006</html:option>
      <html:option value="2007">2007</html:option>
      <html:option value="2008">2008</html:option>
      <html:option value="2009">2009</html:option>
      <html:option value="2010">2010</html:option>
     </html:select>
     
     <b> - </b>
      
     <html:select property="month">
      <html:option value="0">Month</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
     </html:select>
     
     <b> - </b>
     
     <html:select property="day">
      <html:option value="0">Day</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
      <html:option value="13">13</html:option>
      <html:option value="14">14</html:option>
      <html:option value="15">15</html:option>
      <html:option value="16">16</html:option>
      <html:option value="17">17</html:option>
      <html:option value="18">18</html:option>
      <html:option value="19">19</html:option>
      <html:option value="20">20</html:option>
      <html:option value="21">21</html:option>
      <html:option value="22">22</html:option>
      <html:option value="23">23</html:option>
      <html:option value="24">24</html:option>
      <html:option value="25">25</html:option>
      <html:option value="26">26</html:option>
      <html:option value="27">27</html:option>
      <html:option value="28">28</html:option>
      <html:option value="29">29</html:option>
      <html:option value="30">30</html:option>
      <html:option value="31">31</html:option>
     </html:select>
     
    </td>
   </tr>
   
   <tr>
    <td valign="top"><br>Bait Mutations:<br>
     <font style="font-size:8pt;">(space separated list,<br> e.g.: S30D S121D S160A,<br> leave blank if none)</font><br></td>
    <td valign="top"><br><html:text property="mutations" size="20" maxlength="255"/><br></td>
   </tr>

   <tr>
    <td valign="top" colspan="2" align="left">Bait Fragment Information:<br>
      <font style="font-size:8pt;">If the bait protein is a fragment, enter start and end residues below:<br>
        Leave blank if the bait is a full length protein.</font><br>
     </td>
   </tr>

   <tr>
    <td valign="top">Start Residue:</td>
    <td valign="top"><html:text property="startResidue" size="3" maxlength="5"/></td>
   </tr>

   <tr>
    <td valign="top">End Residue:</td>
    <td valign="top"><html:text property="endResidue" size="3" maxlength="5"/></td>
   </tr>

   <tr>
    <td valign="top">Comments:</td>
    <td><html:textarea property="comments" cols="40" rows="5"/></td>
   </tr>

  </table> 
 
 <P><html:submit value="Upload Data"/>
 </CENTER>

</html:form>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>