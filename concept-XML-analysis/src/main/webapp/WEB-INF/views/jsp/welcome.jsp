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
		$("#step2jsp").attr("disabled",true);
		$("#step3jsp").attr("disabled",true);

		$("#inputXsd").fileinput({
			previewFileType : "text",
			allowedFileExtensions : [ "xsd" ],
			previewClass : "bg-warning",
			initialCaption : "The Moon and the Earth"
		});

	});

	$("#step1Nav").click(function() {
		alert("step1Nav .click() called.");
	});

	$("#step2Nav").click(function() {
		alert("step2Nav .click() called.");
	});

	$("#step3Nav").click(function() {
		alert("step3Nav .click() called.");
	});
</script>
</head>

</head>
<body>

	<div id="parent" class="snap12">
		<ul class="nav nav-tabs">
			<li id="step1Nav" class="active"><a><b>STEP-1</b></a></li>
			<li id="step2Nav" role="presentation"><a><b>STEP-2</b></a></li>
			<li id="step3Nav" role="presentation"><a><b>STEP-3</b></a></li>
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
											<br>
										</div>
									</td>
								</tr>
								<tr>
									<td>
										<div>
											<input id="inputXsd" name="inputXsd" type="file"
											class="btn btn-default file-loading form-control" value="Please Select XSD"></input>
										</div>
									</td>
									<td>&nbsp;&nbsp;&nbsp;&nbsp</td>
									<td>
										<div>
											<input type="submit"
												title="Upload selected files"
												class="btn btn-primary" value="Upload"/>
										</div>
									</td>
								</tr>
							</tbody>
						</table>
				</div>
			</div>
		</div>

		<div id="step2jsp">
			<jsp:include page="step1.jsp"></jsp:include>
		</div>

		<div id="step3jsp">
			<jsp:include page="step1.jsp"></jsp:include>
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