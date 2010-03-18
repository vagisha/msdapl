<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<table align="center" width="70%">
<tr>

<td align="center" valign="top">
<!-- Available documents table -->
<table class="table_basic" width="95%" align="center">
<thead>
	<tr><th>Topics</th></tr>
</thead>
<tbody>
	<tr>
	<td>
		<a href="#UPLOAD">Uploading data</a>
	</td>
	</tr>
	<tr>
	<td>
		<a href="#PROTINFER">Protein inference</a>
	</td>
	</tr>
	<tr>
	<td>
		<a href="#COMPARISON">Protein inference comparison</a>
	</td>
	</tr>
</tbody>
</table>
<!--END  Available documents table -->
</td>

<td align="center" valign="top">
<!-- Updates table -->
<table class="table_basic" width="95%" align="center">
<thead>
	<tr>
		<th>MSDaPl Updates</th>
	</tr>
</thead>
<tbody>
	<tr>
	<td class="center_align">
		<b>03/18/10</b> &nbsp; &nbsp; <span class="clickable underline">[Info]</span>
	</td>
	</tr>
</tbody>
</table>
<!-- END Updates table -->


</td>
</tr>

</table>
<br/>
<br/>
<a name="UPLOAD"></a>
<%@ include file="uploadingData.jsp" %>

<br/>
<br/>
<a name="PROTINFER"></a>
<%@ include file="proteinInference.jsp" %>

<br/>
<br/>
<a name="COMPARISON"></a>
<%@ include file="comparison.jsp" %>

<br/>
<br/>

<%@ include file="/includes/footer.jsp" %>