
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script src="<c:url value="/resources/core/js/welcome.js"/>"></script>
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>


<div id="step11" class="span12" align="center">
	<div class="row">
		<c:choose>
			<c:when test="${canUploadFiles eq 'true'}">
				<input id="inputXML" name="inputXML" type="file"
									class="file-loading form-control" 
									value="Please Select XSD" multiple="multiple"></input>
				<button type="button" id="uploadXMLButton"
									title="Upload selected files" class="upload" value="Upload" onclick="uploadXMLButton()">
									<span>Upload</span>
								</button>	
			</c:when>
			<c:otherwise>
				<h3>All Done But You have not choose Option to Upload Files</h3>
			</c:otherwise>
		</c:choose>						
	</div>
	<div class="row">
		<button type="button" id="BackHome" class="upload"
						value="Back" onclick="clickBackToHome()">
						<span>Home</span>
					</button>
	</div>
 </div>
