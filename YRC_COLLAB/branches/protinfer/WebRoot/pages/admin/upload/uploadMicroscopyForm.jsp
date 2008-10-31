<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<logic:notPresent name="MicroscopyProjects" scope="session">
  <logic:redirect href="/yrc/uploadMicroscopyFormAction.do" />
</logic:notPresent>

<%@ include file="/includes/adminHeader.jsp" %>

<%@ include file="/includes/errors.jsp" %>
<logic:present name="saved" scope="request">
 <center>
 <hr width="50%">
  <B><font color="red">Experiment was successfully saved.</font></B>
 <hr width="50%">
 </center>
</logic:present>

<yrcwww:contentbox title="Upload Microscopy Experiment: Step 1 / 4" centered="true" width="800" scheme="upload">

<html:form action="uploadMicroscopy" method="POST" enctype="multipart/form-data">

 <CENTER>
  <table border="0">
   <tr>
    <td colspan="2">Select the project to which this data belongs:</td>
   </tr>
   <tr>
    <td colspan="2">
     <html:select property="projectID">
		<html:option value="0">PLEASE SELECT A PROJECT</html:option>
      <html:optionsCollection name="MicroscopyProjects" value="ID" label="label"/>
     </html:select>
    </td>
   </tr>
   
   <tr>
    <td colspan="2"><hr width="75%"></td>
   </tr>
   
   <tr>
    <td colspan="2">
   
     <table width="100%" border="0">
      <tr>
      
       <td valign="top">Tagged Protein 1:<br>
        <font style="font-size:8pt;">(e.g. dsn1)</font></td>
       <td valign="top"><html:text property="orf1" size="10" maxlength="25" /></td>
       <td valign="top">Tag or Dye Used:</td>
       <td valign="top">
        <html:select property="tag1">
        
         <html:option value="0">Yellows:</html:option>
         <html:option value="YFP">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;YFP</html:option>
         <html:option value="Venus">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Venus</html:option>
         <html:option value="Citrine">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Citrine</html:option>
         <html:option value="YPet">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ypet</html:option>

         
         <html:option value="00">Cyans:</html:option>
         <html:option value="CFP">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CFP</html:option>
         <html:option value="Cerulean">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Cerulean</html:option>
         <html:option value="CyPet">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Cypet</html:option>

         <html:option value="000">Reds:</html:option>
         <html:option value="dsRed">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;dsRed</html:option>
         <html:option value="T1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;T1</html:option>
         <html:option value="mCherry">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;mCherry</html:option>
         <html:option value="mStrawberry">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;mStrawberry</html:option>
         <html:option value="dTomato">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;dTomato</html:option>
         <html:option value="tdTomato">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;tdTomato</html:option>


         <html:option value="GFP">GFP</html:option>
         <html:option value="GFP">DAPI</html:option>


        </html:select>
       </td>
      
      </tr>
     </table>
   
    </td>
   </tr>

   <tr>
    <td colspan="2">
   
     <table width="100%" border="0">
      <tr>
      
       <td valign="top">Tagged Protein 2:</td>
       <td valign="top"><html:text property="orf2" size="10" maxlength="25" /></td>
       <td valign="top">Tag or Dye Used:</td>
       <td valign="top">
        <html:select property="tag2">
        
         <html:option value="0">Yellows:</html:option>
         <html:option value="YFP">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;YFP</html:option>
         <html:option value="Venus">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Venus</html:option>
         <html:option value="Citrine">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Citrine</html:option>
         <html:option value="YPet">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ypet</html:option>

         
         <html:option value="0">Cyans:</html:option>
         <html:option value="CFP">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CFP</html:option>
         <html:option value="Cerulean">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Cerulean</html:option>
         <html:option value="CyPet">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Cypet</html:option>

         <html:option value="0">Reds:</html:option>
         <html:option value="dsRed">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;dsRed</html:option>
         <html:option value="T1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;T1</html:option>
         <html:option value="mCherry">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;mCherry</html:option>
         <html:option value="mStrawberry">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;mStrawberry</html:option>
         <html:option value="dTomato">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;dTomato</html:option>
         <html:option value="tdTomato">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;tdTomato</html:option>


         <html:option value="GFP">GFP</html:option>
         <html:option value="GFP">DAPI</html:option>


        </html:select>
       </td>
      
      </tr>
     </table>
   
    </td>
   </tr>

   <tr>
    <td colspan="2">
   
     <table width="100%" border="0">
      <tr>
      
       <td valign="top">Tagged Protein 3:</td>
       <td valign="top"><html:text property="orf3" size="10" maxlength="25" /></td>
       <td valign="top">Tag or Dye Used:</td>
       <td valign="top">
        <html:select property="tag3">
        
         <html:option value="0">Yellows:</html:option>
         <html:option value="YFP">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;YFP</html:option>
         <html:option value="Venus">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Venus</html:option>
         <html:option value="Citrine">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Citrine</html:option>
         <html:option value="YPet">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ypet</html:option>

         
         <html:option value="0">Cyans:</html:option>
         <html:option value="CFP">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CFP</html:option>
         <html:option value="Cerulean">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Cerulean</html:option>
         <html:option value="CyPet">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Cypet</html:option>

         <html:option value="0">Reds:</html:option>
         <html:option value="dsRed">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;dsRed</html:option>
         <html:option value="T1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;T1</html:option>
         <html:option value="mCherry">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;mCherry</html:option>
         <html:option value="mStrawberry">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;mStrawberry</html:option>
         <html:option value="dTomato">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;dTomato</html:option>
         <html:option value="tdTomato">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;tdTomato</html:option>


         <html:option value="GFP">GFP</html:option>
         <html:option value="GFP">DAPI</html:option>


        </html:select>
       </td>
      
      </tr>
     </table>
   
    </td>
   </tr>

   <tr>
    <td colspan="2"><hr width="75%"></td>
   </tr>

   <tr>
    <td>R3D log file:</td>
    <td><html:file property="r3dLog" size="30" onblur="parseR3DName()" />
    
    	<SCRIPT>
    	
    		// make sure we never show the user error messages
    		self.onerror = function() { return true; }
    	
    		function parseR3DName() {
    		
    			var r3dText = document.forms[0].r3dLog.value;
    			
    			// do nothing if it's empty
    			if (r3dText == null || r3dText == "") { return; }
    			
    			// example of a line we're parsing
    			// C:\Documents and Settings\Administrator\Desktop\Bir1Ndc10\PWY8s010303pw01.r3d.log
    			// need to parse out the 6 digit number buried in this filename
    			var regmatcher = new RegExp("\\d{6,}", "g");
    			var matches = regmatcher.exec( r3dText );
    			
    			if (matches.length < 1) { return; }
    			
    			var dateString = matches[ matches.length - 1];
    			var date = dateString;
    			
    			var month = 0;
    			var day = 0;
    			var year = 0;
    			
    			if (dateString.length == 6) {
	    			month = parseInt(date.substring( 0, 2 ));
	    			day = parseInt(date.substring( 2, 4 ));
	    			year = parseInt(date.substring( 4, 6 ));

    			} else if (dateString.length > 6) {
    				
    				// try the 6 numbers after the first 1 or 2 numbers
					if (dateString.length == 7) {
						date = dateString.substring( 1, 7 );
					} else {
	    				date = dateString.substring( 2, 8 );
	    			}
    				
	    			month = parseInt(date.substring( 0, 2 ));
	    			day = parseInt(date.substring( 2, 4 ));
	    			year = parseInt(date.substring( 4, 6 ));
					
    				
					if (!validDate(year, month, day) && date.length > 8) {
					
						// try the last 6 numbers
						date = dateString.substring( dateString.length - 6, dateString.length );
						
	    				month = parseInt(date.substring( 0, 2 ));
	    				day = parseInt(date.substring( 2, 4 ));
	    				year = parseInt(date.substring( 4, 6 ));				
					}
					
					if (!validDate(year, month, day)) {
					
						// try the first 6 numbers
						date = dateString.substring(0, 6);

	    				month = parseInt(date.substring( 0, 2 ));
	    				day = parseInt(date.substring( 2, 4 ));
	    				year = parseInt(date.substring( 4, 6 ));
					}
    				
    			} 
    			
    			if (!validDate(year, month, day)) {
    				return;
    					
    			} else {
    					
    				// we have parsed a valid date out of the file name, set the form variables
    				document.forms[0].month.selectedIndex = month;
    				document.forms[0].day.selectedIndex = day;
    					
    				if (year >= 0) {
    					document.forms[0].year.selectedIndex = year + 3;
    				} else {
    					document.forms[0].year.selectedIndex = year - 97;
    				}
    				
    			}

    		}
    		
    		// crude date validator
    		function validDate(year, month, day) {
    			
    			// check the year
    			if (year < 0) { return false; }
    			if (year > 10 && year < 98) { return false; }
    			
    			// check the month
    			if (month < 1) { return false; }
    			if (month > 12) { return false; }
    			
    			// check the day
    			if (day < 1) { return false; }
    			if (day > 31) { return false; }
    			
    			// check february!
    			if (month == 2 && day > 29) { return false; }
    			
    			return true;
    		}
    	
    	 
    	</SCRIPT>
    
    
    </td>
   </tr>
   
   <tr>
    <td>R3D file:</td>
    <td><html:file property="r3dFile" size="30"/></td>
   </tr>

   <tr>
    <td colspan="2"><hr width="75%"></td>
   </tr>
   
   <tr>
    <td><br>Experiment date:</td>
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
    <td>Please select cell status:</td>
    <td><html:select property="cellStatus">
     <html:option value="live">Live</html:option>
     <html:option value="fixed, formaldehyde">Fixed, formaldehyde</html:option>
     <html:option value="fixed, MeOH/acetone">Fixed, MEOH/acetone</html:option>
     <html:option value="Fixed, other">Fixed, other</html:option>
    </html:select></td>
   </tr>
   
   <tr>
    <td>Please select cell treatment:</td>
    <td><html:select property="cellTreatment">
     <html:option value="none">None</html:option>
     <html:option value="nocodazole">Nocodazole</html:option>
     <html:option value="benomyl">Benomyl</html:option>
     <html:option value="alpha-factor">Alpha-factor</html:option>
     <html:option value="hydroxyurea">Hydroxyurea</html:option>
     <html:option value="other">Other</html:option>
    </html:select></td>
   </tr>
   
   <tr>
    <td>Please select the growth medium:</td>
    <td><html:select property="cellGrowthMedium">
     <html:option value="YPD 3X Ade">YPD 3X Ade</html:option>
     <html:option value="YPD">YPD</html:option>
     <html:option value="SD">SD</html:option>
     <html:option value="YPGal">YPGal</html:option>
     <html:option value="SGal">SGal</html:option>
     <html:option value="Rich Broth Media (e.g. YPD)">Other Rich Broth Media (e.g. other YPD)</html:option>
     <html:option value="Defined Media (e.g. SD)">Other Defined Media (e.g. other SD)</html:option>
    </html:select></td>
   </tr>
   
   <tr>
    <td>Please select the growth temp.:</td>
    <td><html:select property="cellGrowthTemp">
     <html:option value="21">21</html:option>
     <html:option value="25">25</html:option>
     <html:option value="30">30</html:option>
     <html:option value="37">37</html:option>
    </html:select></td>
   </tr>
   
   <tr>
    <td>Please select the dichroic mirror:</td>
    <td><html:select property="opticsDichroicMirror">
     <html:option value="CFP/YFP DICHROIC">CFP/YFP DICHROIC</html:option>
     <html:option value="Multiband Pass">Multiband Pass</html:option>
     <html:option value="GFP DICHROIC">GFP DICHROIC</html:option>
     <html:option value="YFP DICHROIC">YFP DICHROIC</html:option>
     <html:option value="CFP/YFP/RFP DICHROIC">CFP/YFP/RFP DICHROIC</html:option>
    </html:select></td>
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