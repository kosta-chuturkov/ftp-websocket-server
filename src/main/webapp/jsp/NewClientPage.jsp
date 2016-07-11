<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html;" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
    <title>Login</title>
    <link href="<c:url value="/resources/css/login-box.css" />"
          rel="stylesheet" ty pe="text/css"/>
    <script src="<c:url value="/resources/js/jquery-2.1.1.js" />"></script>
</head>
<body>
<%
    String errorMessage = (String) request.getAttribute("errorMsg");
    if (errorMessage == null) {
        errorMessage = "";
    }
%>
<script>
    var serverAddress = window.location.origin;
    var loginUrl = serverAddress + "<c:url value="/api/login" />";

    function validateInput() {
        var username = document.getElementById("email").value;
        var pass = document.getElementById("pswd").value;
        if (username.length < 3 || username.length > 32) {
            document.getElementById("error").innerHTML = "Email must be atleas 3 symbols and not more than 32!";
            return false;
        }
        if (pass.length < 6 || pass.length > 64) {
            document.getElementById("error").innerHTML = "Password must be atleas 6 symbols and not more than 64!";
            return false;
        }
        return true;
    }
    function getCSRF() {
        var name = "CSRF-TOKEN=";
           var ca = document.cookie.split(';');
            for(var i=0; i<ca.length; i++) {
                var c = ca[i];
                while (c.charAt(0)==' ') c = c.substring(1);
                if (c.indexOf(name) != -1) return c.substring(name.length,c.length);
            }
            return "";
    }
    function enterPressedHandler(e, input) {
        var code = (e.keyCode ? e.keyCode : e.which);
        if (code == 13) {
            submitForm();
        }
    }
    function submitForm() {
        if (validateInput()) {
        $.ajax({
            url: loginUrl,
            headers: {
                'X-CSRF-TOKEN':getCSRF(),
            },
            method: 'POST',
            data: $("#loginForm").serialize(),
            success: function(data){
              console.log('succes: '+data);
            },
            error:function(thrownError){
             var r = jQuery.parseJSON(thrownError.responseText);
             $('#error').html(r.message);
            }
          });
        }
    }
</script>
<div id="login-box">
    <H2>Login</H2>
    Login to Cloud Server. <br/> <br/>
    <form id="loginForm" method="post" action="<c:url value="/api/login" />">
        <div id="login-box-name" style="margin-top: 20px;">Email:</div>
        <div id="login-box-field" style="margin-top: 20px;">
            <input id="email" name="email" class="form-login"
                   title="Email" value="" size="30" maxlength="2048"/>
        </div>
        <input type="hidden"
        	name="${_csrf.parameterName}"
        	value="${_csrf.token}"/>
        <div id="login-box-name">Password:</div>
        <div id="login-box-field">
            <input id="pswd" name="pswd" type="password" class="form-login"
                   title="Password" value="" size="30" maxlength="2048"
                   onKeyPress="enterPressedHandler(event, this)"/>
        </div>
        <br/> <span class="login-box-options">Dont have an account ?<a
            href="<c:url value="/api/register" />" style="margin-left: 30px;">Register</a></span> <br/>
        <br/>
        <a href="javascript:submitForm();"><img
                src="<c:url value="/resources/images/login-btn.png" />" width="103"
                height="42" style="margin-left: 90px;"/></a>
        <div id="error">
        </div>
    </form>
</div>
</body>
</html>