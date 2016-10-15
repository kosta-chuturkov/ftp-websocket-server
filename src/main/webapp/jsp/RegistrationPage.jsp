<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html;"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<h2 align="center">Server Registration</h2>
<style type = "text/css">
*{  
  margin:0;
  padding:0;
}
h2
{
color:#0B0719;
}
body{
  
  background-color:#f0f0f0;
  font-family:helvetica;
}

a{
  display:block;
  color:#ad5482;  
  text-decoration:none;
  font-weight:bold;
  margin-top:40px;
  text-align:center;
}

#bg{
  position:relative;
  top:20px;
  height:600px;
  width:800px;
  margin-left:auto;
  margin-right:auto; 
}

.module{
  position:relative;
  top:15%;    
  height:70%;
  width:450px;
  margin-left:auto;
  margin-right:auto;
  border-radius:5px;
  background:RGBA(255,255,255,1);
    
  -webkit-box-shadow:  0px 0px 15px 0px rgba(0, 0, 0, .45);        
  box-shadow:  0px 0px 15px 0px rgba(0, 0, 0, .45);
  
}

.module ul{
  list-style-type:none;
  margin:0;
}

.tab{
  float:left;
  height:60px;
  width:25%;
  padding-top:20px;
  box-sizing:border-box;
  background:#eeeeee;  
  text-align:center;
  cursor:pointer;
  transition:background .4s;
}

.tab:first-child{  
  -webkit-border-radius: 5px 0px 0px 0px;
  border-radius: 5px 0px 0px 0px;
}

.tab:last-child{  
  -webkit-border-radius: 0px 5px 0px 0px;
  border-radius: 0px 5px 0px 0px;
}

.tab:hover{  
  background-color:rgba(0,0,0,.1);
}

.activeTab{
  background:#fff;
}

.activeTab .icon{
  opacity:1;
}

.icon{
  height:24px;
  width:24px;
  opacity:.2;
}

.form{
  float:left;
  height:86%;
  width:100%;
  box-sizing:border-box;
  padding:40px;
}

.textbox{
  height:50px;
  width:100%;
  border-radius:3px;
  border:rgba(0,0,0,.3) 2px solid;
  box-sizing:border-box;
  padding:10px;
  margin-bottom:30px;
}

.textbox:focus{
  outline:none;
   border:rgba(24,149,215,1) 2px solid;
   color:rgba(24,149,215,1);
}

.button{
  height:50px;
  width:100%;
  border-radius:3px;
  border:rgba(0,0,0,.3) 0px solid;
  box-sizing:border-box;
  padding:10px;
  margin-bottom:30px;
  background:#47A3FF;
  color:#FFF;
  font-weight:bold;
  font-size: 12pt;
  transition:background .4s;
  cursor:pointer;
}

.button:hover{
  background:#00BFFF;
}
</style>
<script src="<c:url value="/resources/js/jquery-2.1.1.js" />"></script>
</head>
<body>
<%
String errorMessage = (String)request.getAttribute("errorMsg");
if(errorMessage == null){
  errorMessage = "";
}
%>
<script>
function validateInput(){
	var userName = document.getElementById("nickname").value;
	var email = document.getElementById("email").value;
	var pass = document.getElementById("pswd").value;
	var passRepeated = document.getElementById("password_repeated").value;
	if(nickname.length < 3 || nickname.length > 32){
		$('#error').html("Username must be atleas 3 symbols and not more than 32!");
		return false;
	}
	if(email.length < 3 || email.length > 32){
		$('#error').html("Email must be atleas 3 symbols and not more than 32!");
		return false;
	}
	if(pass.length < 6 || pass.length > 64){
		$('#error').html("Password must be atleas 3 symbols and not more than 64!");
		return false;
	}
	if(passRepeated.length < 6 || passRepeated.length > 64){
		$('#error').html("Password repeated must be atleas 3 symbols and not more than 64!");
		return false;
	}
	if (pass != passRepeated) {
		$('#error').html("Passwords do not match!");
		return false;
	}
	return true;
}
	function enterPressedHandler(e, input) {
		var code = (e.keyCode ? e.keyCode : e.which);
		if (code == 13) {
			 submitForm();
		};
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
     function goToLoginPage(){
        window.location = "<c:url value="/api/login/" />"
     }
    var serverAddress = window.location.origin;
    var registerUrl = serverAddress + "<c:url value="/api/register" />";
	function submitForm() {
            if (validateInput()) {
            $.ajax({
                url: registerUrl,
                headers: {
                    'X-CSRF-TOKEN':getCSRF(),
                },
                method: 'POST',
                data: $("#submitForm").serialize(),
                success: function(data){
                     alert('Registration successful. You can now login.');
                     goToLoginPage();

                },
                error:function(thrownError){
                var r = jQuery.parseJSON(thrownError.responseText);
                $('#error').html(r.errors[0].message);
                }
              });
            }
        }

</script>
<div id="bg">
  <div class="module"> 
  <form method="post" action="<c:url value="/api/register"/>" id="submitForm">
    <div class="form">
      <input type="text" id="nickname" name="nickname" placeholder="NickName" class="textbox" title="Entered username is invalid!" required>
      <input type="email" id="email" name="email" placeholder="Email Address" class="textbox" title="Entered email is invalid!" required>
      <input type="password" id="pswd" name="pswd" placeholder="Password" class="textbox" pattern="[a-zA-Z0-9]{5,}" title="Minmimum password length is 5 symbols." required>
      <input type="password" id="password_repeated" name="password_repeated" placeholder="Repeat Password" class="textbox" onKeyPress = "enterPressedHandler(event, this)" title="Minmimum password length is 5 symbols." required>
      <input type="button" id="submitButton" value="Register" class="button" onclick="javascript:submitForm();"/>
       <input type="hidden"
              	name="${_csrf.parameterName}"
              	value="${_csrf.token}"/>
      <div id="error"></div>
    </div>
    </form>
  </div>
</div>
</body>
</html>