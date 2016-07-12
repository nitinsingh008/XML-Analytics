	$(document).ready(function() {
		// hide remaining divs
		$("#delimtedMetaDeta").hide();
		$("#alert").hide();
		$("#success").hide();
		
		$("#uploadButtonXsd").click(function() {
			var fileName = $("#inputXsd").val();
			if (fileName == null || fileName == '') {
				$("#alert").show();
				return;
			}else if(!fileName.endsWith(".xsd")){
				 $('#alertMessage').html('invalid file format');
				 $("#alert").show();
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
				$('#alertMessage').html('either inputfile or delimiter not provided');
				$("#alert").show();
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

		$("#alertCloseButton").click(function() {
			$("#alert").hide();
		});
		
		$("#successCloseButton").click(function() {
			$("#success").hide();
		});
		
		
		
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
			$("#consoleOutPut").click();
			var add = setInterval("readLog()",10);
				$.ajax({
					    url: 'Generate',
					    data: $('#captureParseSettings').serialize(),
					  
					    type: 'POST',
						
					    success: function(data){
					   		clearInterval(add);
					   		$("#successMessage").html("Framework generation Done, Please upload input file");
					   		$("#success").show();
					   		$("#uploadXMLButton").removeAttr("disabled");
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
		   		$("#consoleOutput").html(data);
		    }
		  });
		
	}
	


	function uploadXMLButton(){
		
			
			var fileName = $("#inputXML").val();
			alert(fileName);
			if (fileName == null || fileName == '') {
				$("#alertMessage").html('Select files to upload');
				 $("#alert").show();
				return;
			}
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
				   		$("#successMessage").html(data);
				   		$("#success").show();
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