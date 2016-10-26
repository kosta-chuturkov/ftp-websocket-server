	var timeout;
	var type = 2;
    var currentPrivateID = 0;
    var currentPublicID = 0;
    var currentSharedID = 0;
    var privateRowCounter = 0;
    var sharedRowCounter = 0;
    var publicRowCounter = 0;
    var interval = 5000;
    var serverUrl = location.protocol+'//' + '<%=host%>' + ":" + '<%=port%>' + "/update/";
    
    function createEventListeners(){
    	document.getElementById("privateTabLabel").addEventListener("click", onPrivateTabClick);
        document.getElementById("publicTabLabel").addEventListener("click", onPublicTabClick);
        document.getElementById("sharedTabLabel").addEventListener("click", onSharedTabClick);
    }
    
    function onPrivateTabClick() {
        stopAjaxPole();
        startAjax1Pole();
    }

    function onPublicTabClick() {
        stopAjaxPole();
        startAjaxPole();
    }
    
    function onSharedTabClick() {
    	stopAjaxPole();
        startAjax2Pole();
    }

    function addRow(entry) {
        if (type == 1) {
            var table = document.getElementById("fileInfoTable");
            var rowCount = table.rows.length;
            var row = table.insertRow(rowCount);
            var downloadLinkURL = location.protocol+'//' + '<%=host%>' + ":" + '<%=port%>' + "/files/" + entry.downloadHash;
            row.insertCell(0).innerHTML = privateRowCounter;
            row.insertCell(1).innerHTML = entry.name;
            row.insertCell(2).innerHTML = entry.timestamp;
            row.insertCell(3).innerHTML = '<a href="' + downloadLinkURL + '">download file</a>';
            privateRowCounter++;
        } else if (type == 2) {
            var tableName = "uploadedFilesTable";
            var table1 = document.getElementById(tableName);
            var rowCount1 = table1.rows.length;
            var row1 = table1.insertRow(rowCount1);
            var downloadLinkURL1 = location.protocol+'//' + '<%=host%>' + ":" + '<%=port%>' + "/files/" + entry.downloadHash;
            var deleteLinkURL1 = location.protocol+'//' + '<%=host%>' + ":" + '<%=port%>' + "/files/delete/" + entry.deleteHash;
            row1.insertCell(0).innerHTML = publicRowCounter;
            row1.insertCell(1).innerHTML = entry.name;
            row1.insertCell(2).innerHTML = entry.timestamp;
            row1.insertCell(3).innerHTML = '<a href="' + downloadLinkURL1 + '">download file</a>';
            row1.insertCell(4).innerHTML = '<input type="button" value = "delete" onClick="deleteFileAndRemoveRow(\'' + deleteLinkURL1 + '\',this,\'' + tableName + '\')">';
            var modifier = parseInt(entry.modifier);
            var fieldValue;
            if(modifier == 1){
            	fieldValue = "public";
            } else if(modifier == 2){
            	fieldValue = "private";
            } else if(modifier == 3){
            	fieldValue = "shared";
            } else {
            	fieldValue = "unknown";
            }
            row1.insertCell(5).innerHTML = fieldValue;
            publicRowCounter++;
        }else if (type == 3) {
            var tableName2 = "sharedFilesTable";
            var table2 = document.getElementById(tableName2);
            var rowCount2 = table2.rows.length;
            var row1 = table2.insertRow(rowCount2);
            var downloadLinkURL2 = location.protocol+'//' + '<%=host%>' + ":" + '<%=port%>' + "/files/" + entry.downloadHash;
            row1.insertCell(0).innerHTML = sharedRowCounter;
            row1.insertCell(1).innerHTML = entry.name;
            row1.insertCell(2).innerHTML = entry.timestamp;
            row1.insertCell(3).innerHTML = '<a href="' + downloadLinkURL2 + '">download file</a>';
            row1.insertCell(4).innerHTML = entry.sharingUserName;
            sharedRowCounter++;
        }
    }

    function deleteRow(obj,name) {
        var index = obj.parentNode.parentNode.rowIndex;
        var table = document.getElementById(name);
        table.deleteRow(index);

    }

    function keyPressedHandler(e, input) {
        var code = (e.keyCode ? e.keyCode : e.which);
        if (code == 13) {
            search();
        }
    }

    function getDataFromJSON(data) {
        var array;
        if (data.data == null) {
            return;
        }
        array = data.data;
        for (var i = 0; i < array.length; i++) {
            var entry = array[i];
            var entryId = parseInt(entry.id);
            if (type == 1) {
                if (currentPublicID < entryId) {
                    currentPublicID = entryId;
                }
            } else  if (type == 2) {
            	 if (currentPrivateID < entryId ) {
                     currentPrivateID = entryId;
                 }
            } else  if (type == 3) {
            	if (currentSharedID < entryId) {
                	currentSharedID = entryId;
                }
            }
            addRow(entry);
        }
    }
   

    function deleteFileAndRemoveRow(path,obj,name) {
    	
        $.ajax({
            type: 'GET',
            url: ''+path,
            data: $(this).serialize(),
            dataType: 'json',
            success: function(data) {
            	if(data.deleted != null){
            		deleteRow(obj,name);
            	}
            },
            complete: function(data) {              
            }
        });
    }
    
    function ajaxForSharedFiles() {
        $.ajax({
            type: 'POST',
            url: serverUrl,
            data: "id="+currentSharedID+"&type="+type+"&email="+ '<%=user%>' +"&PREFID="+ '<%=hash%>',
            dataType: 'json',
            success: function(data) {
                getDataFromJSON(data);
                timeout = setTimeout(ajaxForSharedFiles, interval);
            },
            complete: function(data) {              	
            },
            error: function (xhr, ajaxOptions, thrownError) {
            	stopAjaxPole();
            	alert("Server offline, please reload the page!");
            }
        });
    }
    
    function ajaxForPublicFiles() {
        $.ajax({
            type: 'POST',
            url: serverUrl,
            data: "id="+currentPrivateID+"&type="+type+"&email="+ '<%=user%>' +"&PREFID="+ '<%=hash%>',
            dataType: 'json',
            success: function(data) {
                getDataFromJSON(data);
                timeout = setTimeout(ajaxForPublicFiles, interval);
            },
            complete: function(data) {               	
            },
            error: function (xhr, ajaxOptions, thrownError) {
            	stopAjaxPole();
            	alert("Server offline, please reload the page!");
            }
        });
    }

    function ajax1ForUserFiles() {
        $.ajax({
            type: 'POST',
            url: serverUrl,
            data: "id="+currentPublicID+"&type="+type+"&email="+ '<%=user%>' +"&PREFID="+ '<%=hash%>',
            dataType: 'json',
            success: function(data) {
                getDataFromJSON(data);
                timeout = setTimeout(ajax1ForUserFiles, interval);
            },
            complete: function(data) {             	
            },
            error: function (xhr, ajaxOptions, thrownError) {
            	stopAjaxPole();
            	alert("Server offline, please reload the page!");
            }
        });
    }

    function startAjaxPole() {
    	type = 2;
        ajaxForPublicFiles();
    }

    function startAjax1Pole() {
    	type = 1;
        ajax1ForUserFiles();
    }
    
    function startAjax2Pole() {
    	type = 3;
        ajaxForSharedFiles();
    }
    
    function stopAjaxPole() {
    	clearTimeout(timeout);
    }
