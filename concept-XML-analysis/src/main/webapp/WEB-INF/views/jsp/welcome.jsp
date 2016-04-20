<jsp:include page="library.jsp"></jsp:include>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>XMLAnalysis</title>
<link href="<c:url value="/resources/core/css/welcome.css" />"
	rel="stylesheet">

<script src="<c:url value="/resources/core/js/welcome.js" />"></script>
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>


</head>

<body>
	<img src="<c:url value='/resources/images/logo.jpg'/>"
		id="full-screen-background-image" />
	<div id="parent" class="snap12">

		<div class="header">
			<img src="<c:url value='/resources/images/xap_logo.png'/>"
				id="xaplogo" /> <%-- <img
				src="<c:url value='/resources/images/concept_crew.png'/>"
				id="conceptlogo" /> --%>

		</div>

		<div id="step1jsp" class="container-fluid">
			<div align="center">
				<div class="upload_form">
					<form action="/uploadXSD" method="post"
						enctype="multipart/form-data" id="uploadXSD">
						<div class="row col-xs-12" id="divborder">


							<div>
								<h3>
									<span class="selectxsd">Please Upload XSD </span>
								</h3>
							</div>


							<div>
								<!-- file not chosen alert div -->
								<div class="alert alert-warning alert-dismissible" id="alert">
									<button type="button" class="close" data-dismiss="alert"
										id="alertCloseButton">
										<span>&times;</span>
									</button>
									<strong>Warning!</strong>
									<div id="alertMessage">Please Select XSD</div>
								</div>
								<br>
							</div>



							<div>
								<input id="inputXsd" name="inputXsd" type="file"
									class="file-loading form-control" 
									value="Please Select XSD"></input>



								<button type="button" id="uploadButton"
									title="Upload selected files" class="upload" value="Upload">
									<span>Upload</span>
								</button>

								<!-- <input type="button" id="uploadButton"
												title="Upload selected files" class="upload"
												value="Upload" />-->

							</div>


						</div>
					</form>
				</div>
			</div>
		</div>

		<div id="step2jsp"></div>

		<div id="step3jsp"></div>


		<div class="footer">
			<!--  <img src="<c:url value='/resources/images/J3jt5K1461055454.png'/>" />-->
			<h6 align="center">The Concept Crew &copy; markit.com 2016</h6>
			<h6 align="center">Gaurav Agarwal | Nitin Singh | Parag Garg</h6>
		</div>

	</div>

</body>
</html>