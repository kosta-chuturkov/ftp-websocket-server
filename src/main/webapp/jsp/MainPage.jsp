<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html;"%>
<!DOCTYPE html>
<html lang="bg">
<head>
<title>Cloud Server</title>
<meta name="description" content="Cloud Server" />
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<%
        int port = 0;
		String user = (String) session.getAttribute("email");
	final String hash = (String) session.getAttribute("PREFID");
	final String host = (String) session.getAttribute("host");
	final String storage = (String) session.getAttribute("storage");
	final String maxStorage = (String) session.getAttribute("maxStorage");
	final Object portObj = session.getAttribute("port");

	    if (user == null || hash == null || host== null || portObj == null) {
	      user = "";
	     
	    }
	    port =((Integer)portObj).intValue();
%>
<script src="<c:url value="/resources/js/jquery-2.1.1.js" />"></script>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
<script src="http://code.jquery.com/jquery-1.10.2.js"></script>
<script src="http://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
<script>
  $(function() {
    $( "#menu" ).menu();
  });
  </script>
  <style>
<style type="text/css">
.deleteBtn {
	-moz-box-shadow:inset 0px 1px 0px 0px #f5978e;
	-webkit-box-shadow:inset 0px 1px 0px 0px #f5978e;
	box-shadow:inset 0px 1px 0px 0px #f5978e;
	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #FA5858), color-stop(1, #FA5858) );
	background:-moz-linear-gradient( center top, #FA5858 5%, #c62d1f 100% );
	filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#f24537', endColorstr='#c62d1f');
	background-color:#f24537;
	-webkit-border-top-left-radius:5px;
	-moz-border-radius-topleft:5px;
	border-top-left-radius:5px;
	-webkit-border-top-right-radius:5px;
	-moz-border-radius-topright:5px;
	border-top-right-radius:5px;
	-webkit-border-bottom-right-radius:5px;
	-moz-border-radius-bottomright:5px;
	border-bottom-right-radius:5px;
	-webkit-border-bottom-left-radius:5px;
	-moz-border-radius-bottomleft:5px;
	border-bottom-left-radius:5px;
	text-indent:0;
	border:1px solid #d02718;
	display:inline-block;
	color:#ffffff;
	font-family:Arial;
	font-size:15px;
	font-weight:bold;
	font-style:normal;
	height:21px;
	line-height:21px;
	width:87px;
	text-decoration:none;
	text-align:center;
	text-shadow:1px 1px 0px #810e05;
}
.downloadBtn:hover {
	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #c62d1f), color-stop(1, #f24537) );
	background:-moz-linear-gradient( center top, #c62d1f 5%, #f24537 100% );
	filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#c62d1f', endColorstr='#f24537');
	background-color:#c62d1f;
}.downloadBtn:active {
	position:relative;
	top:1px;
}</style>
<style type="text/css">
.downloadBtn {
	-moz-box-shadow:inset 0px 1px 0px 0px #bbdaf7;
	-webkit-box-shadow:inset 0px 1px 0px 0px #bbdaf7;
	box-shadow:inset 0px 1px 0px 0px #bbdaf7;
	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #47A3FF), color-stop(1, #47A3FF) );
	background:-moz-linear-gradient( center top, #47A3FF 5%, #378de5 100% );
	filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#79bbff', endColorstr='#378de5');
	background-color:#79bbff;
	-webkit-border-top-left-radius:5px;
	-moz-border-radius-topleft:5px;
	border-top-left-radius:5px;
	-webkit-border-top-right-radius:5px;
	-moz-border-radius-topright:5px;
	border-top-right-radius:5px;
	-webkit-border-bottom-right-radius:5px;
	-moz-border-radius-bottomright:5px;
	border-bottom-right-radius:5px;
	-webkit-border-bottom-left-radius:5px;
	-moz-border-radius-bottomleft:5px;
	border-bottom-left-radius:5px;
	text-indent:0;
	border:1px solid #84bbf3;
	display:inline-block;
	color:#ffffff;
	font-family:Arial;
	font-size:15px;
	font-weight:bold;
	font-style:normal;
	height:21px;
	line-height:21px;
	width:87px;
	text-decoration:none;
	text-align:center;
	text-shadow:1px 1px 0px #528ecc;
}
.downloadBtn:hover {
	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #378de5), color-stop(1, #79bbff) );
	background:-moz-linear-gradient( center top, #378de5 5%, #79bbff 100% );
	filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#378de5', endColorstr='#79bbff');
	background-color:#378de5;
}.downloadBtn:active {
	position:relative;
	top:1px;
}</style>
<style>
html,body,div,span,applet,object,iframe,h1,h2,h3,h4,h5,h6,p,blockquote,pre,a,abbr,acronym,address,big,cite,code,del,dfn,em,font,img,ins,kbd,q,s,samp,small,strike,strong,sub,sup,tt,var,b,u,i,center,dl,dt,dd,ol,ul,li,fieldset,form,label,legend,table,caption,tbody,tfoot,thead,tr,th,td
	{
	border: 0;
	outline: 0;
	font-size: 100%;
	vertical-align: baseline;
	background: transparent;
	margin: 0;
	padding: 0;
}

body {
	line-height: 1;
	background-color: #D8D8D8;
	text-align: center;
	font-family: Tahoma, sans-serif;
	font-size: 13px;
	color: #FFFFFF;
}

ol,ul {
	list-style: none;
}

blockquote,q {
	quotes: none;
}

blockquote:before,blockquote:after,q:before,q:after {
	content: none;
}

ins {
	text-decoration: none;
}

del {
	text-decoration: line-through;
}

table {
	border-collapse: collapse;
	border-spacing: 0;
}

.clear {
	display: block;
	clear: both;
	width: 1px;
	height: .001%;
	font-size: 0;
	line-height: 0;
}

.left {
	float: left;
}

.right {
	float: right;
}

#toplinks {
	height: 34px;
	background: #FFFFFF;
	text-align: left;
	position: relative;
	border-bottom: 1px solid #9d9b9c;
}

#toplinks a {
	color: #000000;
	text-decoration: none;
	line-height: 34px;
	font-size: 12px;
	font-weight: 700;
}

#toplinks a:hover {
	text-decoration: underline;
}

#toplinks .left strong {
	color: #000;
}

#toplinks .left {
	padding-left: 28px;
	color: #FFFFFF;
}

#toplinks .right {
	padding-right: 20px;
}

a,:focus {
	outline: 0;
}

p,:focus {
	outline: 0;
}
</style>

<script src="<c:url value="/resources/js/jquery.min.js" />"></script>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/main.css" />" />
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/tabs.css" />" />
<script src="<c:url value="/resources/js/tabs.js" />"></script>
<script src="<c:url value="/resources/js/sockjs-0.3.min.js" />"></script>
</head>

<body>
	<div id="bg">
		<div id="wrapper">
			<!--TOP LINKS-->
			<div id="toplinks">
				<div class="left">
					<strong>Cloud Server Main Page</strong> &nbsp;|&nbsp; <a
						href="<c:url value="/upload" />"><strong>Upload Files</strong></a>&nbsp;|&nbsp;
						<strong><%=user%></strong>&nbsp;|&nbsp;
						<a href="<c:url value="/logout" />">Logout</a>&nbsp;|&nbsp;<label id="storageInfo"><%=storage%> left from <%=maxStorage%>.</label>
				</div>
				
			</div>
			<div id="siteContent">
				<div class="tabs">
					<ul class="tab-links">
						<li id="privateTabLabel" class="active"><a href="#PrivateTab">Private Files</a></li>
						<li id="sharedToUsersLabel"><a href="#SharedWithUsersTab">Shared files to users</a></li>
						<li id="sharedTabLabel"><a href="#SharedTab">Shared files with you</a></li>
					</ul>

					<div class="tab-content">
						<div id="PrivateTab" class="tab active">
							<table id="uploadedFilesTable" border="1" cellpadding="2" width="1200">
							 <tr>
								<td><b>Id</b></td>
								<td><b>Name</b></td>
								<td><b>Date</b></td>
								<td><b>Size</b></td>
								<td><b>Download link</b></td>
								<td><b>Delete</b></td>
								<td><b>Type</b></td>
							 </tr>
						   </table>
						</div>
						<div id="SharedWithUsersTab" class="tab">
                        	<table id="sharedWithUsersTable" border="1" cellpadding="2" width="1200">
                        	  <tr>
                        			<td><b>Id</b></td>
                        			<td><b>Name</b></td>
                        			<td><b>Date</b></td>
                        			<td><b>Size</b></td>
                        			<td><b>Download link</b></td>
                        			<td><b>Delete</b></td>
                        			<td><b>Type</b></td>
                        	  </tr>
                        	</table>
                        </div>
						<div id="SharedTab" class="tab">
							<table id="sharedFilesTable" border="1" cellpadding="2" width="1200">
								<tr>
									<td><b>Id</b></td>
									<td><b>Name</b></td>
									<td><b>Date</b></td>
									<td><b>Size</b></td>
									<td><b>Download link</b></td>
									<td><b>Sharing user</b></td>
								</tr>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<script>

	var timeout;
	var type = 2;
	var maxResults = 30;
    var currentPrivateID = 0;
    var currentSharedID = 0;
    var currentSharedToUsersID = 0;
    var privateRowCounter = 0;
    var sharedRowCounter = 0;
    var serverAddress = location.protocol+'//' + '<%=host%>' + ":" + '<%=port%>';
    var deleteUrl = serverAddress + "<c:url value="/files/delete/" />";
    var downloadUrl = serverAddress + "<c:url value="/files/" />";
    var ws = null;
    var getSharedFilesMethod = 'getSharedFiles';
    var getSharedWithUsersMethod = 'getSharedWithUsersFiles';
    var getPrivateFilesMethod = 'getPrivateFiles';
    var url = null;
    var transports = [];

    function setConnected(connected) {
        console.log('connected:'+connected);
    }

     function connect() {
        if (!url) {
         alert('Select whether to use W3C WebSocket or SockJS');
         return;
        }

     ws = (url.indexOf('sockjs') != -1) ?
         new SockJS(url, undefined, {protocols_whitelist: transports}) : new WebSocket(url);

     ws.onopen = function () {
         setConnected(true);
		 onPrivateTabClick();
         console.log('Info: connection opened.');
     };
     ws.onmessage = function (event) {
		 getDataFromJSON(event.data);
         console.log('Received: ' + event.data);
     };
     ws.onclose = function (event) {
        setConnected(false);
        establishConnection();
        console.log('Info: connection closed.');
        console.log(event);
     };
     }

     function disconnect() {
        if (ws != null) {
        ws.close();
        ws = null;
        }
        setConnected(false);
     }

     function callRemoteMethod(method, parameters) {
        if (ws != null) {
        var request = {};
        request.method = method;
        request.params = parameters;
        ws.send(JSON.stringify(request));
        } else {
        alert('connection not established, please connect.');
        }
     }

     function updateUrl(urlPath) {
        if (urlPath.indexOf('sockjs') != -1) {
            url = urlPath;
        }
     }

     function updateTransport(transport) {
        transports = (transport == 'all') ?  [] : [transport];
     }

     function establishConnection(){
         updateUrl('/sockjs/echo');
         updateTransport('websocket');
         connect();
     }


    function createEventListeners(){
    	document.getElementById("privateTabLabel").addEventListener("click", onPrivateTabClick);
    	document.getElementById("sharedToUsersLabel").addEventListener("click", onSharedWithUsersClick);
        document.getElementById("sharedTabLabel").addEventListener("click", onSharedTabClick);
    }

    function onPrivateTabClick() {
		var params = {
			firstResult: currentPrivateID,
			maxResults: maxResults
		};
		callRemoteMethod(getPrivateFilesMethod, params);
    }

     function onSharedWithUsersClick() {
    		var params = {
    			firstResult: currentSharedToUsersID,
    			maxResults: maxResults
    		};
    		callRemoteMethod(getSharedWithUsersMethod, params);
        }
    
    function onSharedTabClick() {
		var params = {
			firstResult: currentSharedID,
			maxResults: maxResults
		};
		callRemoteMethod(getSharedFilesMethod, params);
    }

	function addSharedFileRow(entry) {
		var downloadLinkURL = downloadUrl + entry.downloadHash;
		var tableName2 = "sharedFilesTable";
		var table2 = document.getElementById(tableName2);
		var rowCount2 = table2.rows.length;
		var size2 = parseInt(entry.size);
		var row1 = table2.insertRow(rowCount2);
		row1.insertCell(0).innerHTML = sharedRowCounter;
		row1.insertCell(1).innerHTML = entry.name;
		row1.insertCell(2).innerHTML = entry.timestamp;
		row1.insertCell(3).innerHTML = size2.fileSize(1);
		row1.insertCell(4).innerHTML = '<a class="downloadBtn" href="' + downloadLinkURL + '" download="' + entry.name + '">download</a>';
		row1.insertCell(5).innerHTML = entry.sharingUserName;
		sharedRowCounter++;

    }

    function addSharedWithUsersRow(entry) {
    		var downloadLinkURL = downloadUrl + entry.downloadHash;
    		var tableName2 = "sharedWithUsersTable";
    		var table2 = document.getElementById(tableName2);
    		var rowCount2 = table2.rows.length;
    		var size2 = parseInt(entry.size);
    		var row1 = table2.insertRow(rowCount2);
    		var deleteLinkURL1 = deleteUrl + entry.deleteHash;
    		row1.insertCell(0).innerHTML = sharedRowCounter;
    		row1.insertCell(1).innerHTML = entry.name;
    		row1.insertCell(2).innerHTML = entry.timestamp;
    		row1.insertCell(3).innerHTML = size2.fileSize(1);
    		row1.insertCell(4).innerHTML = '<a class="downloadBtn" href="' + downloadLinkURL + '" download="' + entry.name + '">download</a>';
    		row1.insertCell(5).innerHTML = '<input type="button" class="deleteBtn" value = "delete" onClick="deleteFileAndRemoveRow(\'' + deleteLinkURL1 + '\',this,\'' + tableName2 + '\')">';
    		row1.insertCell(6).innerHTML = entry.fileType;
    		sharedRowCounter++;
        }


	function addPrivateFileRow(entry) {
		var tableName = "uploadedFilesTable";
		var table1 = document.getElementById(tableName);
		var rowCount1 = table1.rows.length;
		var row1 = table1.insertRow(rowCount1);
		var size = parseInt(entry.size);
		var downloadLinkURL = downloadUrl + entry.downloadHash;
		var deleteLinkURL1 = deleteUrl + entry.deleteHash;
		row1.insertCell(0).innerHTML = privateRowCounter;
		row1.insertCell(1).innerHTML = entry.name;
		row1.insertCell(2).innerHTML = entry.timestamp;
		row1.insertCell(3).innerHTML = size.fileSize(1);
		row1.insertCell(4).innerHTML = '<a class="downloadBtn" href="' + downloadLinkURL + '" download="' + entry.name + '">download</a>';
		row1.insertCell(5).innerHTML = '<input type="button" class="deleteBtn" value = "delete" onClick="deleteFileAndRemoveRow(\'' + deleteLinkURL1 + '\',this,\'' + tableName + '\')">';
		row1.insertCell(6).innerHTML = entry.fileType;
		privateRowCounter++;
	}

    function deleteRow(obj,name) {
        var index = obj.parentNode.parentNode.rowIndex;
        var table = document.getElementById(name);
        table.deleteRow(index);

    }

	function addRowToTables(responseMethod, arrayLen, array) {
		if (responseMethod === getPrivateFilesMethod) {
			currentPrivateID += arrayLen;
			addPrivateFileRow(array);
		} else if (responseMethod === getSharedFilesMethod) {
			currentSharedID += arrayLen;
			addSharedFileRow(array);
		}else if (responseMethod === getSharedWithUsersMethod) {
        	currentSharedToUsersID += arrayLen;
        	addSharedWithUsersRow(array);
        }
	}
	function getDataFromJSON(response) {
		var data = JSON.parse(response);
        var array;
        if (data == null) {
            return;
        }
		array = JSON.parse(data.result);
		var responseMethod = data.responseMethod;
		if (array.constructor === Array) {
			var arrayLen = array.length;
			for (var i = 0; i < array.length; i++) {
				var entry = array[i];
				addRowToTables(responseMethod, arrayLen, entry);
			}
		} else {
			addRowToTables(responseMethod, 1, array);
		}
    }
   

    function deleteFileAndRemoveRow(path,obj,name) {
    	if (confirm("Are you sure you want to delete this file ?") != true) {
            return;
        }
        $.ajax({
            type: 'GET',
            url: ''+path,
            data: $(this).serialize(),
            dataType: 'json',
            success: function(data) {
            	$('#storageInfo').html(data.storedBytes);
            },
            complete: function(data) {
            	deleteRow(obj,name);
            	privateRowCounter--;
            }
        });
    }

    window.onload = function() {
    	Object.defineProperty(Number.prototype,'fileSize',{value:function(a,b,c,d){
    		 return (a=a?[1e3,'k','B']:[1024,'K','iB'],b=Math,c=b.log,
    		 d=c(this)/c(a[0])|0,this/b.pow(a[0],d)).toFixed(2)
    		 +' '+(d?(a[1]+'MGTPEZY')[--d]+a[2]:'Bytes');
    		},writable:false,enumerable:false});
    	createEventListeners();
        establishConnection();
    };
</script>
</body>
</html>