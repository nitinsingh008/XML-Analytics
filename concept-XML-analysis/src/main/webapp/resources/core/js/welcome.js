	$(document).ready(function() {
		// hide remaining divs
		$("#delimtedMetaDeta").hide();		
		$("#uploadButtonXsd").click(function() {
			var fileName = $("#inputXsd").val();
			if (fileName == null || fileName == '') {
				alert('Please select any XSD');
				return;
			}else if(!fileName.endsWith(".xsd")){
				/* $('#alertMessage').html('invalid file format');
				 $("#alert").modal('show');*/
				alert('Invalid File Format');
			}
			else{
				 var oMyForm = new FormData();
				  oMyForm.append(fileName, inputXsd.files[0]);
				  $.ajax({
					    url: 'uploadXSD',
					    data:oMyForm,
					    dataType: 'text',
					    processData: false,
					    contentType: false,
					    type: 'POST',
					    success: function(data){
					   		 $("#step2jsp").show();
					    	 $("#step1jsp").hide();
					    	 $("#step2jsp").html(data);
					    	 $("#topblank").hide();
					    }
					  });
			}
		});
		
		$("#uploadButtonDelimited").click(function() {
			
			var fileName = $("#inputDelimited").val();
			var delimiter = $("#csvDelimiter").val();
			if (fileName == null || fileName == '' || delimiter == null || delimiter == '') {
				/*$('#alertMessage').html('either inputfile or delimiter not provided');
				$("#alert").modal('show');*/
				alert('either input file or delimiter not provided');
				return;
			}
			else{
				 var oMyForm = new FormData();
				  oMyForm.append(fileName, inputDelimited.files[0]);
				  oMyForm.append("delimited", $("#csvDelimiter").val());
				  oMyForm.append("haveHeader",  $("#haveHeader").val());
				  $.ajax({
					    url: 'uploadCSV',
					    data: oMyForm,
					    dataType: 'text',
					    processData: false,
					    contentType: false,
					    type: 'POST',
					    success: function(data){
					   		 $("#step2jsp").show();
					    	 $("#step1jsp").hide();
					    	 $("#topblank").hide();
					    	 $("#step2jsp").html(data);
					    }
					  });
			}
		});

		/*$("#alertCloseButton").click(function() {
			$("#alert").hide();
		});
		
		$("#successCloseButton").click(function() {
			$("#success").hide();
		});*/
		
		
		
	});
	
	function clickGenerate(){
		
		if(document.getElementById('doAll').checked || document.getElementById('createScript').checked || document.getElementById('createFramework').checked 
			|| document.getElementById('createTable').checked){
			if(document.getElementById('createTable').checked){
				var tns = $("#tnsEntry").val();
				if(tns== null || tns == ''){
					alert("Please enter TNS entry");
					return;
				}
			}
			$("#consoleOutPutLink").click();
			var add = setInterval("readLog()",10);
				$.ajax({
					    url: 'Generate',
					    data: $('#captureParseSettings').serialize(),
					  
					    type: 'POST',
						
					    success: function(data){
					   		clearInterval(add);
					   		readLog();
					   		$("#uploadXMLButton").removeAttr("disabled");
					   		/*$("#alertMessage").html("Framework generation Done, Please upload input file");
					   		$("#alert").modal('show');*/
					   		alert('Framework generation Done, Please upload input file');
					    },
					    error :function(xhr, status, error) {
					    	clearInterval(add);
					    	}
					  });
			}else{
					alert("Please Select Any Option");
					return;
			}
		
			
    }
	
	function clickBack(){
		$('#step1jsp').show();
		$('#step2jsp').hide();
		$("#topblank").show();
   		$("#Help").hide();
   		$("#contactUs").hide();
   		$("#AboutUs").hide();
	}
	
	function initStep2(){
		$('#databaseSetting').hide();
	}
	
	function readLog(){
		$.ajax({
		    url: 'readLog',
		    type: 'GET',
		    success: function(data){
		    	/*var olddata = $("#consoleOutput").html();
		    	if(olddata.length >= data.length){
		    		data = olddata + "--";
		    	}*/
		   		$("#consoleOutput").html(data);
		   		var elem = document.getElementById('consoleOutput');
		   	    elem.scrollTop = elem.scrollHeight;
		    }
		  });
		
	}

	function uploadXMLButton(){
		
			
			var fileName = $("#inputXML").val();
			if (fileName == null || fileName == '') {
				/*$("#alertMessage").html('Select files to upload');
				$("#alert").modal('show');*/
				alert('Select files to upload');
				return;
			}
			var add1 = setInterval("readLog()",10);
			 var oMyForm = new FormData();
			  oMyForm.append(fileName, inputXML.files[0]);
			  $.ajax({
				    url: 'uploadXMLs',
				    data:oMyForm,
				    dataType: 'text',
				    processData: false,
				    contentType: false,
				    type: 'POST',
				    success: function(data){
				    	clearInterval(add1);
				    	readLog();
				   		/*$("#alertMessage").html(data);
				   		$("#alert").removeattr('style');
				   		$("#alert").modal('show');*/
				    	alert(data);
				    }
				  });
		
		
	}
	
	function changeIT(ele){
		//alert($(event).val());
		var selected = $(ele).find("input[name='inputType']:checked").val();
		if(selected == 'DELIMITED'){
			$("#xmlMetaDeta").hide();
			$("#delimtedMetaDeta").show();
		}else if(selected == 'XML'){
			$("#xmlMetaDeta").show();
			$("#delimtedMetaDeta").hide();
		}
		
	}
	
	function clickAboutUs(){
		$.ajax({
		    url: 'getAboutUs',
		    type: 'GET',
		    success: function(data){
		   		$("#AboutUs").html(data);
		   		$("#AboutUs").show();
		   		$("#step2jsp").hide();
		   		$("#step1jsp").hide();
		   		$("#step2jsp").hide();
		   		$("#topblank").show();
		   		$("#contactUs").hide();
		   		$("#Help").hide();
		    }
		  });
	}
	
	function clickHelp(){
		$.ajax({
		    url: 'getHelp',
		    type: 'GET',
		    success: function(data){
		   		$("#Help").html(data);
		   		$("#Help").show();
		   		$("#step2jsp").hide();
		   		$("#step1jsp").hide();
		   		$("#step2jsp").hide();
		   		$("#topblank").show();
		   		$("#contactUs").hide();
		   		$("#AboutUs").hide();
		    }
		  });
	}
	
	function clickContact(){
		$.ajax({
		    url: 'getContact',
		    type: 'GET',
		    success: function(data){
		   		$("#contactUs").html(data);
		   		$("#contactUs").show();
		   		$("#step2jsp").hide();
		   		$("#step1jsp").hide();
		   		$("#step2jsp").hide();
		   		$("#topblank").show();
		   		$("#Help").hide();
		   		$("#AboutUs").hide();
		    }
		  });
	}