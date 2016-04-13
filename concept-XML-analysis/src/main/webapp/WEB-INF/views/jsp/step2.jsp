<jsp:include page="library.jsp"></jsp:include>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<html>
<body>
	<div class="container-fluid">
		<div class="row">
					<div class="headerLabel">
						<div class="container">
							<h1>Select Settings</h1>
						</div>
					</div>
				</div>
		<div class="row col-xs-12">	
		<form:form commandName="xsdParseRequest" id="captureParseSettings" name="captureSettings">
				<table width="100%">
					<tbody>
						<tr>
							<td align="center" width="70%"> 
								<table>
									<tbody>
										<tr>
											<td> <strong> XSD View </strong> </td>
										</tr>
										<tr>
											<td><textarea rows="15" cols="50" readonly="readonly">${parsedInString}</textarea> 
												
											</td>
										</tr>
									</tbody>
								</table>
							</td>
							<td align="left" width="30%">
								<table cellpadding="1px">
									<tbody>
									<tr><td><strong>Select Setting</strong> </td></tr>
										<tr>
											<td>Run All</td>
											<td><form:checkbox path="doAll"></form:checkbox></td>
										</tr>
										<tr>
											<td>Create Database Script</td>
											<td><form:checkbox path="createScript"/></td>
										</tr>
										<tr>
											<td>Choose Database Type</td>
											<td><form:select path="databaseType"
													items="${databaseType}"></form:select></td>
										</tr>
										<tr>
											<td>Create Table in Database</td>
											<td><form:checkbox path="createTable" /></td>
										</tr>
										<tr>
											<td>Database TNS</td>
											<td><form:textarea path="tnsEntry" rows="5" cols="20" /></td>
										</tr>
										<tr>
											<td>UserName</td>
											<td><form:input path="userName" /></td>
										</tr>
										<tr>
											<td>Password</td>
											<td><form:input path="password" /></td>
										</tr>
										<tr>
											<td>Generate Parsing framework</td>
											<td><form:checkbox path="createFramework"/></td>
										</tr>
										<tr> 
											<td>
											<input type="hidden" path="parsedXSDPath" id="parsedXSDPath" name="parsedXSDPath" value="${xsdParseRequest.parsedXSDPath}">
											</td>
										</tr>
									</tbody>
								</table>
							</td>
						</tr>
						<tr>
							<td colspan="2">&nbsp;&nbsp;&nbsp;&nbsp</td>
						</tr>
						<tr>
							<td colspan="2" align="center"><input type="button" id="GenerateButton" class="btn btn-primary" value="Generate" onclick="clickGenerate()"/>
							</td>
						</tr>
					</tbody>
				</table>
		</form:form>
		</div>
	</div>
</body>
</html>
