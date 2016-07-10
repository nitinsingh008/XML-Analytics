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

<script type="text/javascript">
marqueeInit({
	uniqueid: 'mycrawler',
	style: 
	{
		'padding'	 : '5px',
		'width'		 : '100%',		
		/* 'background' : 'rgba(255,255,255,0.0)', */
		'font-size'	 : '35px',
		'font-style' : 'italic',
		'color'      :'#090995'
		
			
		
	},
	inc: 5, //speed - pixel increment for each iteration of this marquee's movement
	mouse: 'cursor driven', //mouseover behavior ('pause' 'cursor driven' or false)
	moveatleast: 2,
	neutral: 150,
	persist: true,
	savedirection: true
});
</script>

</head>

<body class="main_body">
	<img src="<c:url value='/resources/images/logo.jpg'/>"
		id="full-screen-background-image" />
	<!-- <div id="parent" class="snap12"> -->

		<!-- http://www.dynamicdrive.com/dynamicindex2/crawler/ use this link for reference for crawler marquee -->
		<%-- 		<div class="marquee" id="mycrawler">
			| <img src="<c:url value='/resources/images/xap_logo.png'/>" id="xap_crawler" /> 
			<img src="<c:url value='/resources/images/text1.png'/>" id="text1_crawler" />
<!-- 			XAP - XML Automated Parser | Features  -->	
			: <img src="<c:url value='/resources/images/maven.png'/>" id="maven_crawler" /> 
<!-- 			Generation of maven project -->
		<img src="<c:url value='/resources/images/text2.png'/>" id="text2_crawler" />
			: <img src="<c:url value='/resources/images/java1.png'/>" id="java1_crawler" /> 
<!-- 			Generation of JAXB, POJO classes  -->
					<img src="<c:url value='/resources/images/text3.png'/>" id="text3_crawler" />		
			: <img src="<c:url value='/resources/images/sqlSmall.png'/>" id="sql_crawler" /> 
		<!-- 	Generation of SQL scripts -->
		<img src="<c:url value='/resources/images/text4.png'/>" id="text4_crawler" />		
			: <img src="<c:url value='/resources/images/dbTable.png'/>" id="dbTable_crawler" /> 
		<!-- 	Generation of Database Model => Tables, Sequence, Primary Key, Foreign Key etc -->
		<img src="<c:url value='/resources/images/text5.png'/>" id="text5_crawler" />				  			
			: <img src="<c:url value='/resources/images/xml.png'/>" id="xml_crawler" />  
		<!-- 	Generation of XML Parsers -->
		<img src="<c:url value='/resources/images/text6.png'/>" id="text6_crawler" />		
			: <img src="<c:url value='/resources/images/loader.png'/>" id="loader_crawler" /> 
			<!-- Generation of Loader framework  -->
		<img src="<c:url value='/resources/images/text7.png'/>" id="text7_crawler" />			
			| Developed by  <img src="<c:url value='/resources/images/ConceptCrewSmall.png'/>" id="conceptCrew_crawler" />
			| Contributors : <img src="<c:url value='/resources/images/GauravAgarwal.JPG'/>" id="photo_crawler" /> Gaurav Agarwal
			, Nitin Singh
			, Parag Garg
		</div> --%>
	<div class="container"> 
		<div class="row">
		<br><br>
		</div>
		<div class="header row">
			<div class="col-xs-12">
				<img src="<c:url value='/resources/images/xap_logo.png'/>"
					id="xaplogo" title="XAP" />
				<h6 align="right">
					<a href="#" onclick="clickBack()"> <img
						src="<c:url value='/resources/images/home_icon.png'/>"
						id="home_icon" /> &nbsp;HOME
					</a> | <a href="#"> <img
						src="<c:url value='/resources/images/about_us.png'/>"
						id="aboutus_icon" /> &nbsp;ABOUT US
					</a> | <a href="#"> <img
						src="<c:url value='/resources/images/help.png'/>" id="help_icon" />
						&nbsp;HELP
					</a> | <a href="#"> <img
						src="<c:url value='/resources/images/contact.png'/>"
						id="contact_icon" /> &nbsp;CONTACT US&nbsp;&nbsp;
					</a>
				</h6>
			</div>
		</div>

		<%-- 		<div class="header snap12" align = "right">
			<img src="<c:url value='/resources/images/xap_logo.png'/>"
				id="xaplogo" title="XAP"/> <img
				src="<c:url value='/resources/images/concept_crew.png'/>"
				id="conceptlogo" />

		</div>	 --%>

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
			<div class="alert alert-success alert-dismissible" id="success">
				<button type="button" class="close" data-dismiss="alert"
					id="successCloseButton">
					<span>&times;</span>
				</button>
				<strong>Message!</strong>
				<div id="successMessage"></div>
			</div>

		</div>
		<div class="row">
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

	<!-- 	<div class = "row" id="consoleJsp">
			<h3>Output Console</h3>
			
		</div> -->
		<footer>
			<div class="row footer">
				<div class="col-xs-2"></div>
				<div class="col-xs-1" align="right">
					<img src="<c:url value='/resources/images/concept_crew.png'/>"
					id="concpt_crew_logo" />
				</div>
				<div class="col-xs-7" align="left">
					<h6 align="center">The Concept Crew &copy; markit.com 2016</h6>
					<h6 align="center">Gaurav Agarwal | Nitin Singh | Parag Garg</h6>
				</div>
			</div>
		</footer>
		
	</div>

</body>
</html>