<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>GlassFish Server Open Source Edition 4.0 - Error report</title>
<style type="text/css">
<!--
H1 {
	font-family: Tahoma, Arial, sans-serif;
	color: white;
	background-color: #525D76;
	font-size: 22px;
}

H2 {
	font-family: Tahoma, Arial, sans-serif;
	color: white;
	background-color: #525D76;
	font-size: 16px;
}

H3 {
	font-family: Tahoma, Arial, sans-serif;
	color: white;
	background-color: #525D76;
	font-size: 14px;
}

BODY {
	font-family: Tahoma, Arial, sans-serif;
	color: black;
	background-color: white;
}

B {
	font-family: Tahoma, Arial, sans-serif;
	color: white;
	background-color: #525D76;
}

P {
	font-family: Tahoma, Arial, sans-serif;
	background: white;
	color: black;
	font-size: 12px;
}

A {
	color: black;
}

HR {
	color: #525D76;
}
-->
</style>
</head>
<body>
<%
		String errorMessage = (String) request.getAttribute("errorMsg");
		if (errorMessage == null) {
			errorMessage = "";
		}
	%>
	<h1>HTTP Status 404 - Not Found</h1>
	<hr />
	<p>
		<b>type</b> Status report
	</p>
	<p>
		<b>message</b><div id="error"><%=errorMessage %></div>
	</p>
	<p>
		<b>description</b>The requested resource is not available.
	</p>
	<hr />
</body>
</html>