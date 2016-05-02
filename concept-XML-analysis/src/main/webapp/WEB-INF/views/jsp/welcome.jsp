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
<script src="<c:url value="/resources/core/js/crawler.js" />"></script>

<script type="text/javascript">
marqueeInit({
	uniqueid: 'mycrawler',
	style: 
	{
		'padding'	 : '5px',
		'width'		 : '100%',		
		'background' : 'rgba(255,255,255,0.0)',
		'font-size'  : '30px',
		'font-style' : 'italic',
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
	<div id="parent" class="snap12">

		<!-- http://www.dynamicdrive.com/dynamicindex2/crawler/ use this link for reference for crawler marquee -->
		<div class="marquee" id="mycrawler">
			| <img src="<c:url value='/resources/images/xap_logo.png'/>" id="xap_crawler" /> XAP - XML Automated Parser
			| Features 
			: <img src="<c:url value='/resources/images/maven.png'/>" id="maven_crawler" /> Generation of new maven project
			: <img src="<c:url value='/resources/images/java1.png'/>" id="java1_crawler" /> Generation of JAXB, POJO classes 		
			: <img src="<c:url value='/resources/images/sqlSmall.png'/>" id="sql_crawler" /> Generation of SQL scripts
			: <img src="<c:url value='/resources/images/dbTable.png'/>" id="dbTable_crawler" /> Generation of Database Model => Tables, Sequence, Primary Key, Foreign Key etc  			
			: <img src="<c:url value='/resources/images/xml.png'/>" id="xml_crawler" />  Generation of XML Parsers
			: <img src="<c:url value='/resources/images/loader.png'/>" id="loader_crawler" /> Generation of Loader framework 
<%-- 			| Developed by  <img src="<c:url value='/resources/images/ConceptCrewSmall.png'/>" id="conceptCrew_crawler" />
			| Contributors : <img src="<c:url value='/resources/images/GauravAgarwal.JPG'/>" id="photo_crawler" /> Gaurav Agarwal
			, Nitin Singh
			, Parag Garg --%>
		</div>

		<div class="header">

			<img src="<c:url value='/resources/images/xap_logo.png'/>"
				id="xaplogo" title="XAP" />
			<h6 align="right"><a href="#">
			<img src="<c:url value='/resources/images/home_icon.png'/>"
				id="home_icon"/>			
          &nbsp;HOME
        </a> | <a href="#">
			<img src="<c:url value='/resources/images/about_us.png'/>"
				id="aboutus_icon"/>			
          &nbsp;ABOUT US
        </a> | <a href="#">
			<img src="<c:url value='/resources/images/help.png'/>"
				id="help_icon"/>			
          &nbsp;HELP
        </a> | <a href="#">
			<img src="<c:url value='/resources/images/contact.png'/>"
				id="contact_icon"/>			
          &nbsp;CONTACT US&nbsp;&nbsp;
        </a></h6>
			
		</div>

		<%-- 		<div class="header snap12" align = "right">
			<img src="<c:url value='/resources/images/xap_logo.png'/>"
				id="xaplogo" title="XAP"/> <img
				src="<c:url value='/resources/images/concept_crew.png'/>"
				id="conceptlogo" />

		</div>	 --%>	


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
			<h6 align="center">The Concept Crew &copy; markit.com 2016</h6>
			<h6 align="center">Gaurav Agarwal | Nitin Singh | Parag Garg</h6>
			 <img src="<c:url value='/resources/images/concept_crew.png'/>" id="concpt_crew_logo" /> 
		</div>
	</div>

</body>
</html>