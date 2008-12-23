<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox title="Login Form" centered="true" width="400">

<html:form action="login" method="POST">
  <CENTER>
   <P>Please log in below.

	 <TABLE BORDER="0">
	  <TR>
	   <TD>Username:</TD>
	   <TD><html:text property="username" size="20" maxlength="30"/></TD>
	  </TR>

	  <TR>
	   <TD>Password:</TD>
	   <TD><html:password property="password" size="20" maxlength="30"/></TD>
	  </TR>

	 </TABLE>
   </CENTER> 
 <P ALIGN="center"><INPUT TYPE="submit" VALUE="Click to Login">

</html:form>

<P align="center">Forgot your password? <html:link href="/yrc/pages/login/forgotPassword.jsp">Click here.</html:link>

<P align="center">Not registered? <html:link href="/yrc/viewRegister.do">Click here.</html:link>

<P><B>Note:</B> If you have previously collaborated with the YRC, you are already in our database and do not need to register.  Please proceed
to our <html:link href="/yrc/pages/login/forgotPassword.jsp">forgot password page</html:link>, and enter your email address.  A username and password will
be sent to you.

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>