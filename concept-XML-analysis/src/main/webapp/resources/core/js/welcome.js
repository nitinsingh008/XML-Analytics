	$(document).ready(function() {
		// hide remaining divs
	//	$("#step2jsp").hide();
	//	$("#step3jsp").hide();
		//$("#consoleJsp").hide();
		$("#delimtedMetaDeta").hide();
	//	$("#step2jsp").attr("disabled", true);
	//	$("#step3jsp").attr("disabled", true);
		$("#alert").hide();
		$("#success").hide();

		$("#step1Nav").click(function() {
			alert("step1Nav .click() called.");
		});

		$("#step2Nav").click(function() {
			alert("step2Nav .click() called.");
		});

		$("#step3Nav").click(function() {
			alert("step3Nav .click() called.");
		});
		
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
					    	 //$("#step2jsp").attr("disabled", false);
							 //$("#step1jsp").attr("disabled", true);
					    	 $("#step2jsp").html(data);
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
					    	 //$("#step2jsp").attr("disabled", false);
							 //$("#step1jsp").attr("disabled", true);
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
					   		/*$("#step3jsp").html(data);
					    	$("#step3jsp").show();*/
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
		   		//$("#consoleJsp").show();
		    }
		  });
		
	}
	
	function clickBackToHome(){
		$('#step1jsp').show();
		$('#step3jsp').hide();
	}

	function uploadXMLButton(){
		
			
			var fileName = $("#inputXML").val();
			alert(fileName);
			if (fileName == null || fileName == '') {
				$("#alertMessage").html('Select files to upload');
				 $("#alert").show();
				return;
			}/*else if(!fileName.endsWith(".xml")){
				 $('#alertMessage').html('invalid file format');
				 $("#alert").show();
				 return;
			}*/
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