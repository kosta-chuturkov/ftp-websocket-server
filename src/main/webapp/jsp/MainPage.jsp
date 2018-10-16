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
	final String profilePictureAddress = (String) session.getAttribute("profilePictureAddress");
	final Object portObj = session.getAttribute("port");

	    if (user == null || hash == null || host== null || portObj == null) {
	      user = "";
	     
	    }
	    port =((Integer)portObj).intValue();
%>
<link rel="stylesheet" href="<c:url value="/resources/css/jquery-ui.css" />">
<script type="text/javascript" src="<c:url value="/resources/js/jquery.min.js" />"></script>
      <script type="text/javascript" src="<c:url value="/resources/js/select2.full.js" />"></script>
      <script type="text/javascript" src="<c:url value="/resources/js/bootstrap.min.js" />"></script>
      <script type="text/javascript" src="<c:url value="/resources/js/prettify.min.js"/>"></script>
      <script type="text/javascript" src="<c:url value="/resources/js/anchor.min.js"/>"></script>
      <link href="<c:url value="/resources/css/bootstrap.css" />" type="text/css" rel="stylesheet">
      <link href="<c:url value="/resources/css/select2.min.css" />" type="text/css" rel="stylesheet">

      <link href="<c:url value="/resources/css/font-awesome.css" />" type="text/css" rel="stylesheet">
      <link href="<c:url value="/resources/css/s2-docs.css" />" type="text/css" rel="stylesheet">
  <script type="text/javascript">
  $(".js-example-basic-multiple").select2();
  </script>
  <style>
<style type="text/css">
.deleteBtn {
	background-color: #4CAF50;
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
.deleteBtn:hover {
	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #378de5), color-stop(1, #79bbff) );
	background:-moz-linear-gradient( center top, #378de5 5%, #79bbff 100% );
	filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#378de5', endColorstr='#79bbff');
	background-color:#378de5;
}.deleteBtn:active {
	position:relative;
	top:1px;
}
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
	height:23px;
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
}


html,body,div,span,applet,object,iframe,h1,h2,h3,h4,h5,h6,p,blockquote,pre,abbr,acronym,address,big,cite,code,del,dfn,em,font,img,ins,kbd,q,s,samp,small,strike,strong,sub,sup,tt,var,b,u,i,center,dl,dt,dd,ol,ul,li,fieldset,form,label,legend,table,caption,tbody,tfoot,thead,tr,th,td
	{
	border: 0;
	outline: 0;
	font-size: 100%;
	vertical-align: sub;
	background: transparent;
	margin: 0;
	padding: 0;
}

body {
	line-height: 1.5;
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
	overflow: hidden; /* will contain if #first is longer than #second */
}

#toplinks {
	height: 51px;
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
	padding-right: 15px;
	overflow: hidden; /* will contain if #first is longer than #second */
}

a,:focus {
	outline: 0;
}

p,:focus {
	outline: 0;
}
.js-data-example-ajax {
		position: absolute;
		width:400px;
}

</style>
<style>
.updateBtn {
	-moz-box-shadow:inset 0px 1px 0px 0px #339933;
	-webkit-box-shadow:inset 0px 1px 0px 0px #339933;
	box-shadow:inset 0px 1px 0px 0px #339933;
	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #339933), color-stop(1, #339933) );
	background:-moz-linear-gradient( center top, #339933 5%, #339933 100% );
	filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#339933', endColorstr='#339933');
	background-color:#339933;
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
	height:27px;
	line-height:21px;
	width:87px;
	text-decoration:none;
	text-align:center;
	text-shadow:1px 1px 0px #528ecc;
}
.updateBtn:active {
	position:relative;
	top:1px;
}
</style>
<style>
.delbtn {
	-moz-box-shadow:inset 0px 1px 0px 0px #e05252;
	-webkit-box-shadow:inset 0px 1px 0px 0px #e05252;
	box-shadow:inset 0px 1px 0px 0px #e05252;
	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #e05252), color-stop(1, #e05252) );
	background:-moz-linear-gradient( center top, #e05252 5%, #e05252 100% );
	filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#e05252', endColorstr='#e05252');
	background-color:#e05252;
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
	height:25px;
	line-height:21px;
	width:87px;
	text-decoration:none;
	text-align:center;
	text-shadow:1px 1px 0px #528ecc;
}
.delbtn:active {
	position:relative;
	top:1px;
}
</style>
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
					<strong>Cloud Server Main Page</strong> &nbsp;|&nbsp; <a class="uploadButton"
						href="<c:url value="/api/upload/" />"><strong>Upload Files</strong></a>&nbsp;|&nbsp;
						<strong><%=user%></strong>&nbsp;|&nbsp;
						&nbsp;|&nbsp;<label id="storageInfo"><%=storage%> left from <%=maxStorage%>.</label>
				</div>
				<div class="right">
				<img id="profilePicImageElement" src="<%=profilePictureAddress%>" style="width:50px;height:50px;" onclick="openUploadDialog(this)">
				<img src="<c:url value="/resources/images/logout.png" />" onclick="logout()" style="width:50px;height:50px;float:right;">
				</div>
				<form id="imageUploadForm" method="POST" enctype="multipart/form-data" action="<c:url value="/profilePicUpdate" />"><input id="fileInputEl" type="file" name="files[]" style="visibility: hidden;" /></form>
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
                        	<table id="filesISharedTable" border="1" cellpadding="2" width="1400">
                        	  <tr>
                        			<td><b>Name</b></td>
                        			<td><b>Date</b></td>
                        			<td><b>Size</b></td>
                        			<td><b>Download link</b></td>
                        			<td><b>Delete</b></td>
                        			<td><b>Shared to Users</b></td>
                        			<td><b>Update Users</b></td>
                        	  </tr>
                        	</table>
                        </div>
						<div id="SharedTab" class="tab">
							<table id="filesSharedWithMeTable" border="1" cellpadding="2" width="1200">
								<tr>
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
	var maxResults = 30;
	var downloadHashes = {};
    var currentPrivateID = 0;
    var currentSharedID = 0;
    var currentSharedToUsersID = 0;
    var privateRowCounter = 0;
    var filesISharedCounter = 0;
    var filesSharedWithMeCounter = 0;
    var serverAddress = location.protocol+'//' + '<%=host%>' + ":" + '<%=port%>';
    var deleteUrl = serverAddress + "<c:url value="/files/delete/" />";
    var updateUsersUrl = serverAddress + "<c:url value="/files/updateUsers/" />";
    var downloadUrl = serverAddress + "<c:url value="/files/" />";
    var profilePicUpdateUrl = serverAddress + "<c:url value="/profilePic/" />";
    var ws = null;
    var sharedFilesWithMeMethod = 'sharedFilesWithMe';
    var filesISharedMethod = 'filesIShared';
    var deletedFile = 'deletedFile';
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
		 applySelect2();
         console.log('Received: ' + event.data);
     };
     ws.onclose = function (event) {
        setConnected(false);
        console.log('Info: connection closed.');
        console.log(event);
        //redirect to main
        window.location.replace(serverAddress)
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
         updateUrl('/sockjs/files');
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
    		callRemoteMethod(filesISharedMethod, params);
        }

    function onSharedTabClick() {
		var params = {
			firstResult: currentSharedID,
			maxResults: maxResults
		};
		callRemoteMethod(sharedFilesWithMeMethod, params);
    }

    function Clear(element) {
    		element.value = "";
    }

	function addSharedFileRow(entry) {
		var downloadLinkURL = downloadUrl + entry.downloadHash;
		var tableName2 = "filesSharedWithMeTable";
		var table2 = document.getElementById(tableName2);
		var rowCount2 = table2.rows.length;
		var size2 = parseInt(entry.size);
		var row1 = table2.insertRow(rowCount2);
		row1.insertCell(0).innerHTML = entry.name;
		row1.insertCell(1).innerHTML = entry.timestamp;
		row1.insertCell(2).innerHTML = size2.fileSize(1);
		row1.insertCell(3).innerHTML = '<a class="downloadBtn" href="' + downloadLinkURL + '" download="' + entry.name + '">Download</a>';
		row1.insertCell(4).innerHTML = entry.sharingUserName;
		filesSharedWithMeCounter++;
		return row1;
    }
     function getSelect(elements, currentItteration){
        var markup = '<select id="sharedSelect'+currentItteration+'" class="js-data-example-ajax" multiple="multiple" tabindex="-1" aria-hidden="true">'
        for(var i=0;i<elements.length;i++){
            markup+='<option selected="selected" value="'+elements[i]+'">'+elements[i]+'</option>';
        }
        markup +="</select>";
        return markup;
     }
    function addSharedWithUsersRow(entry, currentItteration) {
            var usersSharingFile = entry.sharedToUsers;
    		var downloadLinkURL = downloadUrl + entry.downloadHash;
    		var tableName2 = "filesISharedTable";
    		var table2 = document.getElementById(tableName2);
    		var rowCount2 = table2.rows.length;
    		var size2 = parseInt(entry.size);
    		var row1 = table2.insertRow(rowCount2);
    		var deleteLinkURL1 = deleteUrl + entry.deleteHash;
    		var updateUrl = updateUsersUrl + entry.deleteHash;
    		var selectName = 'sharedSelect'+currentItteration;
    		row1.insertCell(0).innerHTML = entry.name;
    		row1.insertCell(1).innerHTML = entry.timestamp;
    		row1.insertCell(2).innerHTML = size2.fileSize(1);
    		row1.insertCell(3).innerHTML = '<a class="downloadBtn" href="' + downloadLinkURL + '" download="' + entry.name + '">Download</a>';
    		row1.insertCell(4).innerHTML = '<input type="button" class="delbtn" value = "Delete" onClick="deleteFileAndRemoveRow(\'' + deleteLinkURL1 + '\',this,\'' + tableName2 + '\')">';
    		row1.insertCell(5).innerHTML = getSelect(usersSharingFile, currentItteration);
    		row1.insertCell(6).innerHTML = '<input type="button" class="updateBtn" value = "Update" onClick="updateUsers(\'' + selectName +'\',\''+updateUrl+ '\')">';
    		filesISharedCounter++;
        }




	function addPrivateFileRow(entry) {
		var tableName = "uploadedFilesTable";
		var table1 = document.getElementById(tableName);
		var rowCount1 = table1.rows.length;
		var row1 = table1.insertRow(rowCount1);
		var size = parseInt(entry.size);
		var downloadLinkURL = downloadUrl + entry.downloadHash;
		var deleteLinkURL1 = deleteUrl + entry.deleteHash;
		row1.insertCell(0).innerHTML = entry.name;
		row1.insertCell(1).innerHTML = entry.timestamp;
		row1.insertCell(2).innerHTML = size.fileSize(1);
		row1.insertCell(3).innerHTML = '<a class="downloadBtn" href="' + downloadLinkURL + '" download="' + entry.name + '">Download</a>';
		row1.insertCell(4).innerHTML = '<input type="button" class="delbtn" value = "Delete" onClick="deleteFileAndRemoveRow(\'' + deleteLinkURL1 + '\',this,\'' + tableName + '\')">';
		row1.insertCell(5).innerHTML = entry.fileType;
		privateRowCounter++;
	}

    function deleteRow(obj,name) {
        var index = obj.parentNode.parentNode.rowIndex;
        var table = document.getElementById(name);
        table.deleteRow(index);
    }
    function removeDeletedFileFromTable(array,obj,name) {
        var index = obj.rowIndex;
        var table = document.getElementById(name);
        table.deleteRow(index);
    }


	function dispatchRequest(responseMethod, arrayLen, array, currentItteration) {
		if (responseMethod === getPrivateFilesMethod) {
			currentPrivateID += arrayLen;
			addPrivateFileRow(array);
		} else if (responseMethod === sharedFilesWithMeMethod) {
		    if(downloadHashes[array.downloadHash]){
               return;
            }else {
			    currentSharedID += arrayLen;
			    downloadHashes[array.downloadHash] = addSharedFileRow(array);
			}
		}else if (responseMethod === filesISharedMethod) {
        	currentSharedToUsersID += arrayLen;
        	addSharedWithUsersRow(array, currentItteration);
        }else if(responseMethod === deletedFile){
          var deletedFileId = array.deletedFileUid;
          var deletedFileRow = downloadHashes[deletedFileId];
          if(deletedFileRow){
            downloadHashes[array.deletedFileUid] = undefined;
            removeDeletedFileFromTable(array,deletedFileRow, "filesSharedWithMeTable");
            filesSharedWithMeCounter--;
          }
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
				dispatchRequest(responseMethod, arrayLen, entry, i);
			}
		} else {
			dispatchRequest(responseMethod, 1, array, i);
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

    function updateUsers(name,path) {
    var options = $('#'+name).select2("val");
    var values = [];
    if(options){
        for(var i = 0; i < options.length; i++){
        var usr = {};
        usr.name = options[i];
          values.push(usr);
        }
    }else {
      var usr1 = {};
      usr1.name = "-1";
      values.push(usr1);
     }
     var result = JSON.stringify(values);
            $.ajax({
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN':getCSRF()
                },
                type: 'POST',
                url: ''+path,
                data: result,
                dataType: 'json',
                error: function(data) {
                if(data.status==200){
                   return;
                }
                  console.log(data.responseText);
                  alert(data.responseText);
                }
            });

        }

        function logout(){
          var req = new XMLHttpRequest();
          req.open("GET","<c:url value="/api/logout"/>", true);
          req.withCredentials = true;
          req.send();
          window.location.replace("<c:url value="/api/login"/>");
        }

        function openUploadDialog(obj){
            $('input[type=file]').click();
        }

        function submitForm() {
            uploadProfPic();
         }


     function formatRepo (repo) {
      if (repo.loading) return repo.text;
      var markup = "<div class='select2-result-repository clearfix'>" +
        "<div class='select2-result-repository__avatar'><img src='" + repo.owner.avatar_url + "' /></div>" +
        "<div class='select2-result-repository__meta'>" +
          "<div class='select2-result-repository__title'>" + repo.full_name + "</div></div></div>";
      return markup;
    }

        function formatRepoSelection (repo) {
          return repo.full_name || repo.text;
        }

        function uploadProfPic(){
        var formData = new FormData();
        var fileInputElement = $('input[type=file]')[0].files[0];
        formData.append("files[]", fileInputElement);

        var request = new XMLHttpRequest();
         request.onreadystatechange = function()
            {
                if (request.readyState == 4 && request.status == 200)
                {
                    var newImageUrl = JSON.parse(request.responseText);
                    var imageUrl = newImageUrl.imageUrl;
                    $("#profilePicImageElement").attr("src", imageUrl);
                }
            };
        request.open("POST", profilePicUpdateUrl);
        request.setRequestHeader("X-CSRF-TOKEN", getCSRF());
        request.send(formData);

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
     function applySelect2(){
    	$(".js-data-example-ajax").select2({
        ajax: {
        type: 'POST',
        url: "<c:url value="/api/users" />",
        headers: {
        'X-CSRF-TOKEN':getCSRF(),
        },
        dataType: 'json',
        delay: 250,
        data: function (params) {
          return {
            q: params.term, // search term
            page: params.page
          };
        },
        processResults: function (data, params) {
          // parse the results into the format expected by Select2
          // since we are using custom formatting functions we do not need to
          // alter the remote JSON data, except to indicate that infinite
          // scrolling can be used

           params.page = params.page || 1;

           return {
              results: data.items,
                 pagination: {
                    more: (params.page * 30) < data.total_count
                 }
          };
        },
        cache: true
      },
      escapeMarkup: function (markup) { return markup; }, // let our custom formatter work
      minimumInputLength: 1,
      templateResult: formatRepo, // omitted for brevity, see the source of this page
      templateSelection: formatRepoSelection // omitted for brevity, see the source of this page
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

        $('input[type=file]').change(function(ev) {
          submitForm();
        });

    };
</script>
</body>
</html>