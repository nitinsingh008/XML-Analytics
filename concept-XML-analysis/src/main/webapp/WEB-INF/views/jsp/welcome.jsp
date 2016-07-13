<jsp:include page="library.jsp"></jsp:include>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>XMLAnalysis</title>
<link href="<c:url value="/resources/core/css/welcome.css" />"
	rel="stylesheet">


<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
<script src="<c:url value="/resources/core/js/crawler.js" />"></script>

<script src="<c:url value="/resources/core/js/welcome.js" />"></script>

</head>

<body class="main_body">
	<img src="<c:url value='/resources/images/logo.jpg'/>"
		id="full-screen-background-image" />
	<div class="container"> 
		<div class="row">
		<br><br>
		</div>
		<div class="row header" >
			<div class="col-xs-6">
				<img src="<c:url value='/resources/images/xap_logo.png'/>"
					id="xaplogo" title="XAP" />			
			</div>
			<div class="col-xs-6" align="right">
				<a href="#" onclick="clickBack()"> 
					<img src="<c:url value='/resources/images/home_icon.png'/>" id="home_icon" /> 
						&nbsp;HOME
				
				</a> | <a href="#" onclick="clickAboutUs()"> 
					<img src="<c:url value='/resources/images/about_us.png'/>" id="aboutus_icon" /> 
						&nbsp;ABOUT US
				
				</a> | <a href="#" onclick="clickHelp()"> 
					<img src="<c:url value='/resources/images/help.png'/>" id="help_icon" />
						&nbsp;HELP
				
				</a> | <a href="#" onclick="clickContact()"> 
					<img src="<c:url value='/resources/images/contact.png'/>" id="contact_icon" /> 
						&nbsp;CONTACT US&nbsp;&nbsp;
				</a>
			</div>
		</div>
		<div class="row">
			<!-- file not chosen alert div -->
			<br><br>
			<div class="alert alert-warning alert-dismissible" id="alert">
				<button type="button" class="close" data-dismiss="alert"
					id="alertCloseButton">
					<span>&times;</span>
				</button>
				<strong>Warning!</strong>
				<div id="alertMessage">Please Select XSD</div>
			</div>
			<br>
			<div class="alert alert-success alert-dismissible" id="success">
				<button type="button" class="close" data-dismiss="alert"
					id="successCloseButton">
					<span>&times;</span>
				</button>
				<strong>Message!</strong>
				<div id="successMessage"></div>
			</div>

		</div>
		<div id="topblank" class="row topblankspace" >
		<br><br>
		</div>
		<div id="step1jsp" class="row upload_form" align="center">
				
					<!-- <form action="/uploadXSD" method="post"
						enctype="multipart/form-data" id="uploadXSD"> -->
						<div class="container" id="divborder">
							<div class="row">
								<div class="btn-group" data-toggle="buttons" onchange="changeIT(this)">
									<label class="btn btn-primary">
										<input name="inputType" value="XML" type="radio" class="active">XML
									</label>
									<label class="btn btn-primary">
										<input name="inputType" value="DELIMITED" type="radio" >DELIMITED
									</label>
						        </div>
							</div>
							<div id="xmlMetaDeta" class="row">
								<div class="container">
									<div class="row">
									<h3>
										<span class="selectxsd">Please Upload XSD</span>
									</h3>
									</div>
									<div class="row">
										<input id="inputXsd" name="inputXsd" type="file"
											class="file-loading form-control" value="Please Select XSD"></input>
										<button type="button" id="uploadButtonXsd"
											title="Upload selected files" class="upload" value="Upload">
											<span>Upload</span>
										</button>
									</div>
								</div>
							</div>
							<div id="delimtedMetaDeta" class="row">
								<div class="container">
									<div class="row">
									<h3>
										<span class="selectxsd">Please Upload Data File</span>
									</h3>
									<br>
									</div>
									<div class="row">
										<div class="col-xs-2"></div>
										<div class="col-xs-2">
										<b>	File Demilimter By : </b>
										</div>
										<div class="col-xs-2">
											<select class="dropdown btn-default dropdown-toggle" id="csvDelimiter">
												<option></option>
												<option value="\t">TAB</option>
												<option value=",">,(comma)</option>
												<option value="|">|(pipe)</option>
											</select> 
										</div>
										<div class="col-xs-2">
											<b>File Have Header data : </b>
										</div>
										<div class="col-xs-2">
											<select class="dropdown btn-default dropdown-toggle" id="haveHeader">
												<option value="Y">Yes</option>
												<option value="N">No</option>
											</select> 
										</div>
										<div class="col-xs-2"></div>
									</div>
									<div>
										<input id="inputDelimited" name="inputXsd" type="file"
											class="file-loading form-control" value="Please Select XSD"></input>
										<button type="button" id="uploadButtonDelimited"
											title="Upload selected files" class="upload" value="Upload">
											<span>Upload</span>
										</button>
									</div>
								</div>
							</div>
						</div>
				<!-- 	</form> -->
		</div>
		
		<div class="row" id="step2jsp"></div>
		<div class="row" id="contactUs"></div>
		<div class="row" id="Help"></div>
		<div class="row" id="AboutUs"></div>
	<!-- 	<div class = "row" id="consoleJsp">
			<h3>Output Console</h3>
			
		</div> -->
		<footer>
			<div class="row footer">
				<div class="col-xs-4"></div>
				<div class="col-xs-1" align="right">
					<img src="<c:url value='/resources/images/concept_crew.png'/>"
					id="concpt_crew_logo" />
				</div>
				<div class="col-xs-7" align="left">
					<h6 style="vertical-align: middle;">The Concept Crew &copy; markit.com 2016</h6>
					<h6 style="vertical-align: middle;">Gaurav Agarwal | Nitin Singh | Parag Garg</h6>
				</div>
			</div>
		</footer>
		
	</div>

</body>
</html>