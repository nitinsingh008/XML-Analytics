
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script src="<c:url value="/resources/core/js/welcome.js"/>"></script>
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>


<div id="step11" class="row col-xs-12" align="center">
	<div class="row">
		<h4>
			<strong>File Upload Section</strong> <br>
		</h4>
		<c:choose>
			<c:when test="${canUploadFiles eq 'true'}">
				
			</c:when>
			<c:otherwise>
				<h3>All Done But You have not choose Option to Upload Files</h3>
			</c:otherwise>
		</c:choose>						
	</div>
	<!-- <div class="row">
		<button type="button" id="BackHome" class="upload"
						value="Back" onclick="clickBackToHome()">
						<span>Home</span>
					</button>
	</div> -->
 </div>
