<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html;"%>
<!DOCTYPE HTML>
<html lang="en">
<head>
	<title>Server File Upload Page</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<%
		String user = (String) session.getAttribute("email");
		String hash = (String) session.getAttribute("PREFID");
		if (user == null || hash == null) {
			user = "";
		}
		String maxFileSize = (String) session.getAttribute("maxStorage");
	%>
	<style type="text/css">
		label {
			margin-right: 20px;
		}

		input[type=radio].css-checkbox {
			display: none;
		}

		input[type=radio].css-checkbox + label.css-label {
			padding-left: 29px;
			height: 25px;
			display: inline-block;
			line-height: 25px;
			background-repeat: no-repeat;
			background-position: 0 0;
			font-size: 15px;
			vertical-align: middle;
			cursor: pointer;
		}

		input[type=radio].css-checkbox:checked + label.css-label {
			background-position: 0 -25px;
		}

		label.css-label {
			background-image: url(/resources/images/csscheckbox.png);
			-webkit-touch-callout: none;
			-webkit-user-select: none;
			-khtml-user-select: none;
			-moz-user-select: none;
			-ms-user-select: none;
			user-select: none;
		}

		.right {
			float: right;
		}

		#livesearch {
			position: absolute;
		}

		.ui-widget {
			position: absolute;
		}

		.js-data-example-ajax {
        		position: absolute;
        		width:400px;
        }
	</style>
	<!-- Bootstrap styles -->

	<link rel="stylesheet"
		  href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">
	<!-- Generic page styles -->
	<link rel="stylesheet" href="<c:url value="/resources/css/style.css" />">
	<!-- blueimp Gallery styles -->
	<link rel="stylesheet"
		  href="<c:url value="/resources/css/blueimp-gallery.min.css" />">
	<!-- CSS to style the file input field as button and adjust the Bootstrap progress bars -->
	<link rel="stylesheet"
		  href="<c:url value="/resources/css/jquery.fileupload.css" />">
	<link rel="stylesheet"
		  href="<c:url value="/resources/css/jquery.fileupload-ui.css" />">
	<!-- CSS adjustments for browsers with JavaScript disabled -->
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
	<noscript>
		<link rel="stylesheet"
			  href="<c:url value="/resources/css/jquery.fileupload-noscript.css" />">
	</noscript>
	<noscript>
		<link rel="stylesheet"
			  href="<c:url value="/resources/css/jquery.fileupload-ui-noscript.css" />">
	</noscript>

</head>
<body>
<div class="navbar navbar-default navbar-fixed-top">
	<div class="container">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-fixed-top .navbar-collapse">
				<span class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
		</div>
		<div class="navbar-collapse collapse">
			<ul class="nav navbar-nav">
				<li><a href="<c:url value="/api/main/" />"><strong><%=user%>
				</strong></a></li>
				<li><a href="javascript:logout();" /><b>Logout</b></a></li>
			</ul>
		</div>
	</div>
</div>
<div class="container">
	<div class="clearfix">
		<div class="pull-left">
			<h1>Server File Upload page.</h1>
		</div>
	</div>
	<ul class="nav nav-tabs">
		<li class="active">Upload your Files here.</li>
	</ul>
	<br>
	<blockquote>
		<p>Select and upload your files.</p>
	</blockquote>
	<br>

	<!-- The file upload form used as target for the file upload widget -->
	<form id="fileupload" action="<c:url value="/api/upload" />" onsubmit="changeNickNamesInputValue()" method="POST"
		  enctype="multipart/form-data">
		<!-- The fileupload-buttonbar contains buttons to add/delete files and start/cancel the upload -->
		<div style="color: #222; padding: 10px;">
			<table>
				<tr>
					<td><input type="radio" onclick="javascript:clearOtherFields()" name="modifier"
							   value="2" id="radio2"
							   class="css-checkbox" checked="checked"/><label for="radio2"
																			  class="css-label radGroup2">Private.</label>
					</td>
					<td><input type="radio" onclick="javascript:clearOtherFields()" name="modifier"
							   value="3" id="radio3"
							   class="css-checkbox"/><label for="radio3"
															class="css-label radGroup2">Share
						with:</label></td>
					<td><select id="selectedUsers" class="js-data-example-ajax" multiple="multiple" tabindex="-1" aria-hidden="true"></select>
					<input type="hidden" name="nickName" value=""></td>
				</tr>
				<input type="hidden"
                        	name="${_csrf.parameterName}"
                        	value="${_csrf.token}"/>
			</table>
		</div>
		<div class="row fileupload-buttonbar">
			<div class="col-lg-7">
				<!-- The fileinput-button span is used to style the file input field as button -->
					<span class="btn btn-success fileinput-button"> <i
							class="glyphicon glyphicon-plus"></i> <span>Add files...</span> <input
							type="file" name="files[]" multiple>
					</span>
				<button onclick="changeNickNamesInputValue()" type="submit" class="btn btn-primary start">
					<i class="glyphicon glyphicon-upload"></i> <span>Start
							upload</span>
				</button>
				<button type="reset" class="btn btn-warning cancel">
					<i class="glyphicon glyphicon-ban-circle"></i> <span>Cancel
							upload</span>
				</button>
				<button type="button" class="btn btn-danger delete">
					<i class="glyphicon glyphicon-trash"></i> <span>Delete</span>
				</button>
				<input type="checkbox" class="toggle">
				<!-- The global file processing state -->
				<span class="fileupload-process"></span>
			</div>
			<!-- The global progress state -->
			<div class="col-lg-5 fileupload-progress fade">
				<!-- The global progress bar -->
				<div class="progress progress-striped active" role="progressbar"
					 aria-valuemin="0" aria-valuemax="100">
					<div class="progress-bar progress-bar-success" style="width: 0%;"></div>
				</div>
				<!-- The extended global progress state -->
				<div class="progress-extended">&nbsp;</div>
			</div>
		</div>
		<!-- The table listing the files available for upload/download -->
		<table role="presentation" class="table table-striped">
			<tbody class="files"></tbody>
		</table>
	</form>
	<br>
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title">Notes</h3>
		</div>
		<div class="panel-body">
			<ul>
				<li>The maximum file size for uploads is <strong><%=maxFileSize%>.</strong>.
				</li>
			</ul>
		</div>
	</div>
</div>
<!-- The blueimp Gallery widget -->
<div id="blueimp-gallery"
	 class="blueimp-gallery blueimp-gallery-controls" data-filter=":even">
	<div class="slides"></div>
	<h3 class="title"></h3>
	<a class="prev">‹</a> <a class="next">›</a> <a class="close">×</a> <a
		class="play-pause"></a>
	<ol class="indicator"></ol>
</div>
<!-- The template to display files available for upload -->
<script id="template-upload" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
    <tr class="template-upload fade">
        <td>
            <span class="preview"></span>
        </td>
        <td>
            <p class="name">{%=file.name%}</p>
            <strong class="error text-danger"></strong>
        </td>
        <td>
            <p class="size">Processing...</p>
            <div class="progress progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100" aria-valuenow="0"><div class="progress-bar progress-bar-success" style="width:0%;"></div></div>
        </td>
        <td>
            {% if (!i && !o.options.autoUpload) { %}
                <button class="btn btn-primary start" onclick="changeNickNamesInputValue()" disabled>
                    <i class="glyphicon glyphicon-upload"></i>
                    <span>Start</span>
                </button>
            {% } %}
            {% if (!i) { %}
                <button class="btn btn-warning cancel">
                    <i class="glyphicon glyphicon-ban-circle"></i>
                    <span>Cancel</span>
                </button>
            {% } %}
        </td>
    </tr>
{% } %}
</script>
<!-- The template to display files available for download -->
<script id="template-download" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
    <tr class="template-download fade">
        <td>
            <span class="preview">
                {% if (file.thumbnailUrl) { %}
                    <a href="{%=file.url%}" title="{%=file.name%}" download="{%=file.name%}" data-gallery><img src="{%=file.thumbnailUrl%}"></a>
                {% } %}
            </span>
        </td>
        <td>
            <p class="name">
                {% if (file.url) { %}
                    <a href="{%=file.url%}" title="{%=file.name%}" download="{%=file.name%}" {%=file.thumbnailUrl?'data-gallery':''%}>{%=file.name%}</a>
                {% } else { %}
                    <span>{%=file.name%}</span>
                {% } %}
            </p>
            {% if (file.error) { %}
                <div><span class="label label-danger">Error</span> {%=file.error%}</div>
            {% } %}
        </td>
        <td>
            <span class="size">{%=o.formatFileSize(file.size)%}</span>
        </td>
        <td>
            {% if (file.deleteUrl) { %}
                <button class="btn btn-danger delete" data-type="{%=file.deleteType%}" data-url="{%=file.deleteUrl%}"{% if (file.deleteWithCredentials) { %} data-xhr-fields='{"withCredentials":true}'{% } %}>
                    <i class="glyphicon glyphicon-trash"></i>
                    <span>Delete</span>
                </button>
                <input type="checkbox" name="delete" value="1" class="toggle">
            {% } else { %}
                <button class="btn btn-warning cancel">
                    <i class="glyphicon glyphicon-ban-circle"></i>
                    <span>Cancel</span>
                </button>
            {% } %}
        </td>
    </tr>
{% } %}
</script>
<!-- The jQuery UI widget factory, can be omitted if jQuery UI is already included -->
<script src="<c:url value="/resources/js/vendor/jquery.ui.widget.js" />"></script>
<!-- The Templates plugin is included to render the upload/download listings -->
<script src="<c:url value="/resources/js/tmpl.min.js" />"></script>
<!-- The Load Image plugin is included for the preview images and image resizing functionality -->
<script src="<c:url value="/resources/js/load-image.min.js" />"></script>
<!-- The Canvas to Blob plugin is included for image resizing functionality -->
<script src="<c:url value="/resources/js/canvas-to-blob.min.js" />"></script>
<!-- Bootstrap JS is not required, but included for the responsive demo navigation -->
<!-- blueimp Gallery script -->
<script src="<c:url value="/resources/js/jquery.blueimp-gallery.min.js" />"></script>
<!-- The Iframe Transport is required for browsers without support for XHR file uploads -->
<script src="<c:url value="/resources/js/jquery.iframe-transport.js" />"></script>
<!-- The basic File Upload plugin -->
<script src="<c:url value="/resources/js/jquery.fileupload.js" />"></script>
<!-- The File Upload processing plugin -->
<script src="<c:url value="/resources/js/jquery.fileupload-process.js" />"></script>
<!-- The File Upload image preview & resize plugin -->
<script src="<c:url value="/resources/js/jquery.fileupload-image.js" />"></script>
<!-- The File Upload audio preview plugin -->
<script src="<c:url value="/resources/js/jquery.fileupload-audio.js" />"></script>
<!-- The File Upload video preview plugin -->
<script src="<c:url value="/resources/js/jquery.fileupload-video.js" />"></script>
<!-- The File Upload validation plugin -->
<script src="<c:url value="/resources/js/jquery.fileupload-validate.js" />"></script>
<!-- The File Upload user interface plugin -->
<script src="<c:url value="/resources/js/jquery.fileupload-ui.js" />"></script>
<!-- The main application script -->
<script src="<c:url value="/resources/js/main.js" />"></script>
<!-- The XDomainRequest Transport is included for cross-domain file deletion for IE 8 and IE 9 -->
<!--[if (gte IE 8)&(lt IE 10)]>
<script src="<c:url value="/js/cors/jquery.xdr-transport.js" /></script>
<![endif]-->
<script>
	function clearOtherFields(){
		document.getElementById("tags").value= "enter user name here...";
	}
	function Clear() {
		document.getElementById("tags").value = "";
		document.getElementById("radio3").checked = true;
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
        function logout(){
            var req = new XMLHttpRequest();
            req.open("GET","<c:url value="/api/logout"/>", true);
            req.withCredentials = true;
            req.send();
            window.location.replace("<c:url value="/api/login"/>");
        }
        function changeNickNamesInputValue(){
        var options = $('#selectedUsers').select2("val");
        if(!options){
            return;
        }
        var values = [];
           for(var i = 0; i < options.length; i++){
            var usr = {};
            usr.name = options[i];
            values.push(usr);
           }
           $('input[name=nickName]').val(JSON.stringify(values));
        }
        function onSelectCheckSharedBox(){
        $('.js-data-example-ajax').on("select2:selecting", function(e) {
             document.getElementById("radio3").checked = true;

         });
        }
        window.onload = function() {
           applySelect2();
           onSelectCheckSharedBox();
        };

</script>
<script src="<c:url value="/resources/js/jquery-ui.js" />"></script>
</body>
</html>
