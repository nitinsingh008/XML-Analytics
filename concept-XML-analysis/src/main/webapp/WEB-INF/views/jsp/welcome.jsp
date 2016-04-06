<jsp:include page="library.jsp"></jsp:include>
<!DOCTYPE html>
<html lang="en">
<head>
<title>XMLAnalysis</title>
<script type="text/javascript">
	$(document).ready(function() {
		// hide remaining divs
		$("#step2jsp").hide();
		$("#step3jsp").hide();
		$("#step2jsp").attr("disabled", true);
		$("#step3jsp").attr("disabled", true);
		$("#alert").hide();

		$("#step1Nav").click(function() {
			alert("step1Nav .click() called.");
		});

		$("#step2Nav").click(function() {
			alert("step2Nav .click() called.");
		});

		$("#step3Nav").click(function() {
			alert("step3Nav .click() called.");
		});
		
		$("#uploadButton").click(function() {
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
					    	 $("#step2jsp").attr("disabled", false);
							 $("#step1jsp").attr("disabled", true);
					    	 $("#step2jsp").html(data);
					    }
					  });
			}
		});

		$("#alertCloseButton").click(function() {
			$("#alert").hide();
		});

	});
	
	function clickGenerate(){
		
			$.ajax({
					    url: 'Generate',
					    data: {
					    	request : $("#captureParseSettings").serialize()
					    },
					    type: 'POST',
					    dataType:"json",
					    success: function(data){
					   		alert("success");
					    }
					  });
    }
</script>
</head>

<body>

	<div id="parent" class="snap12">
		<ul class="nav nav-tabs">
			<li id="step1Nav" class="active"><a><b>STEP-1</b></a></li>
			<li id="step2Nav"><a><b>STEP-2</b></a></li>
			<li id="step3Nav"><a><b>STEP-3</b></a></li>
		</ul>

		<div id="step1jsp" class="container-fluid">
			<div align="center">
				<div class="row">
					<div class="headerLabel">
						<div class="container">
							<h1>Lets Get Started</h1>
						</div>
					</div>
				</div>
				<form action="/uploadXSD" method="post"
					enctype="multipart/form-data" id="uploadXSD">
					<div class="row col-xs-12">
						<table>
							<tbody>
								<tr>
									<td colspan="3" align="center">
										<h3>
											<span class="label label-primary">Please Upload XSD </span>
										</h3>
									</td>
								</tr>
								<tr>
									<td colspan="3">
										<div>
											<!-- file not chosen alert div -->
											<div class="alert alert-warning alert-dismissible"
												id="alert">
												<button type="button" class="close" data-dismiss="alert"
													 id="alertCloseButton">
													<span>&times;</span>
												</button>
												<strong>Warning!</strong> <div id="alertMessage">Please Select XSD</div>
											</div>
											<br>
										</div>
									</td>
								</tr>
								<tr>
									<td>
										<div>
											<input id="inputXsd" name="inputXsd" type="file"
												class="btn btn-default file-loading form-control"
												value="Please Select XSD"></input>
										</div>
									</td>
									<td>&nbsp;&nbsp;&nbsp;&nbsp</td>
									<td>
										<div>
											<input type="button" id="uploadButton"
												title="Upload selected files" class="btn btn-primary"
												value="Upload" />
										</div>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</form>
			</div>
		</div>

		<div id="step2jsp">
			
		</div>

		<div id="step3jsp">
			
		</div>


		<hr>
		<footer>
			<div class="container-fluid row footerBack" align="center">
				<h6>Concept-Crew &copy; markit.com 2016</h6>
			</div>
		</footer>
	</div>

</body>
</html>