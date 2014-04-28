<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="z" uri="http://www.dihedron.org/zephyr"%>
<html>
<head>
	<title>Zephyr Demo</title>
	<link href="css/main.css" type="text/css" rel="stylesheet">
	<script src="js/jquery-2.1.0.js"></script>
		
	<script>
	$(document).ready(function() {
		$('#modelForm > input[type="submit"]').on('click', function(event) {
			$('#result').html('submitting...');
			var data = $('#modelForm').serialize();
			$.ajax({
				type: "POST",
				url: "TestAction!onModelFormSubmission",
				data: data,
				success: function(data) {
					$('#result').html(JSON.stringify(data));
				},
				dataType: "json"
			});
			/* stop the form from submitting the normal way and refreshing the page */
			event.preventDefault();			
		});
	});


	</script>
</head>
<body>
	<h1>Zephyr Test Cases (ver. <z:version/>)</h1>


	<fieldset>
		<legend><b>Plain Form Submission (GET)</b></legend>
		<p>By pressing the below button you are submitting a plain form to an action, using the HTTP GET method.</p> 
		<p>The form contains a single parameter called "message", which is passed to the server-side action through an <code>@In</code> parameter; the receiving action will reverse the string 
		and send it back through an <code>@Out</code> parameter called "echoGET", which will be stored as a request attribute and be made available to the page via the Zephyr tag library.</p>
		<form action="TestAction!onSimpleFormSubmission" method="get" id="simpleGetForm">
			<label for="message">Message</label><input type="text" name="message" value="" />
			<input type="submit" title="Submit" name="Submit" value="Submit" />
			<z:useBean var="echoGET" name="echoGET" type="java.lang.String"></z:useBean>
			<c:if test="${echoGET !=null && echoGET.trim().length() > 0}">
				<br><br>
				The reverse of what you said is: <b><c:out value="${echoGET}"/></b>
			</c:if>			
		</form>
	</fieldset>
	
	<br>

	<fieldset>
		<legend><b>Plain Form Submission (POST)</b></legend>
		<p>By pressing the below button you are submitting a plain form to an action, using an HTTP POST call.</p> 
		<p>The form contains a single parameter called "message", which is passed to the server-side action through an <code>@In</code> parameter; the receiving action will reverse the string 
		and send it back through an <code>@Out</code> parameter called "echoPOST", which will be stored as a request attribute and be made available to the page via the Zephyr tag library.</p>
		<form action="TestAction!onSimpleFormSubmission" method="post" id="simplePostForm">
			<label for="message">Message</label><input type="text" name="message" value="" />
			<input type="submit" title="Submit" name="Submit" value="Submit" />
			<z:useBean var="echoPOST" name="echoPOST" type="java.lang.String"></z:useBean>
			<c:if test="${echoPOST !=null && echoPOST.trim().length() > 0}">
				<br><br>form
				The reverse of what you said is: <b><c:out value="${echoPOST}"/></b>:
				
			</c:if>			
		</form>
	</fieldset>
	
	<br>

	<fieldset>
		<legend><b>Complex Form Submission with Validation (POST)</b></legend>
		<p>By pressing the below button you are submitting a complex form to an action, using an HTTP POST call.</p> 
		<p>The form contains many parameters of different types; each of them is mapped to the an input (<code>@In</code>) parameter to the server side method.</p>
		<p>The method requires validation of some of its inputs, so some warning messages might appear on the log if any of the constraints is not satisfied.</p>  
		<p>The JSON representation of the form will be returned as an <code>@Out</code> parameter and be made available on the page.</p>
		<form action="TestAction!onComplexFormSubmission" method="post" id="complexForm">
			<table>
				<tr>
					<td>
						<label for="name">Name: </label>
					</td>
					<td>
						<input name="name" type="text" size="100" value="${name}" placeholder="please enter your name (min 3, max 20)..."/>
					</td>
				</tr>
				<tr>
					<td>
						<label for="surname">Surname: </label>
					</td>
					<td>
						<input name="surname" type="text" size="100" value="${surname}" placeholder="please enter your family name (min 3, max 20)..."/>
					</td>
				</tr>
				<tr>
					<td>
						<label for="phone">Phone no.: </label>
					</td>
					<td>
						<input name="phone" type="text" size="100" value="${phone}" placeholder="please enter your phone number (06-555-12345)..."/>
					</td>
				</tr>
				<tr>
					<td>
						<label for="email">Email: </label>
					</td>
					<td>
						<input name="email" type="text" size="100" value="${email}" placeholder="please enter your email address (a.b@c.com)..."/>
					</td>
				</tr>
				<tr>
					<td>
						<label for="street">Street: </label>
					</td>
					<td>
						<input name="street" type="text" size="100" value="${street}" placeholder="please enter the street where you live (min 4)..."/>
					</td>
				</tr>
				<tr>
					<td>
						<label for="number">Number: </label>
					</td>
					<td>
						<input name="number" type="text" size="100" value="${number}" placeholder="please enter your street number..."/>
					</td>
				</tr>
				<tr>
					<td>
						<label for="zip">ZIP code: </label>
					</td>
					<td>
						<input name="zip" type="text" size="100" value="${zip}" placeholder="please enter your ZIP code..."/>
					</td>
				</tr>
				<tr>
					<td>
						<label for="town">Town: </label>
					</td>
					<td>
						<input name="town" type="text" size="100" value="${town}" placeholder="please enter the town you live in..."/>
					</td>
				</tr>
				<tr>
					<td>
						<label for="sex">Sex: </label>
					</td>					
					<td>
						<input type="radio" name="sex" value="male">Male
						
						<input type="radio" name="sex" value="female">Female
					</td>
				</tr>
				<tr>
					<td>
						<label for="music">Music Interests: </label>
					</td>
					<td>
						<input type="checkbox" name="music" value="Rock">Rock
						<input type="checkbox" name="music" value="Pop">Pop
						<input type="checkbox" name="music" value="Jazz">Jazz
						<input type="checkbox" name="music" value="Blues">Blues
						<input type="checkbox" name="music" value="Funk">Funk 					
						<input type="checkbox" name="music" value="Reggae">Reggae
						<input type="checkbox" name="music" value="Blues">Classical
						<input type="checkbox" name="music" value="Funk">Ska 					
						<input type="checkbox" name="music" value="Reggae">New Wave
					</td>
				</tr>
			</table>
			<input type="submit" title="Submit" name="Submit" value="Submit" />	
			
			<z:useBean var="user1" name="json1" type="java.lang.String"></z:useBean>
			<c:if test="${user1 !=null && user1.trim().length() > 0}">
				<br><br>
				The server answered:
				<pre><c:out value="${user1}"/></pre>
			</c:if>			
				
		</form>
	</fieldset>

	<br>
	
	<fieldset>
		<legend><b>Output Model-based Form AJAX Submission with Validation and JSON rendering</b></legend>
		<p>By pressing the below button you are submitting a complex form to an action, using an AJAX HTTP POST call.</p> 
		<p>The form contains many parameters of different types; each of them is mapped to a Model object (<code>@Model</code> annotation), possibly setting values into nested objects.</p>
		<p>The method requires validation of some of its inputs, so some warning messages might appear on the log if any of the constraints is not satisfied.</p> 
		<p>Once the object has been populated through <code>@Model</code> annotation processing, it is stored into the REQUEST scope through an <code>@Out</code> annotation; then conteol is
		passed to the JSON renderer, which does not return a JSP page: it picks the bean from the REQUEST scope and converts it to JSON.</p>
		<p>Thus, this very complex example (yet a very simple one in terms of coding) showcases how parameters can be injected and stored automagically, plus how AJAX calls can be performed 
		and how the server can answer with a JSON object format.</p>   
		<form action="TestAction!onModelFormSubmission" method="post" id="modelForm">
			<table>
				<tr>
					<td>
						<label for="user:name">Name: </label>
					</td>
					<td>
						<input name="user:name" type="text" size="100" value="${name}" placeholder="please enter your name (min 3, max 20)..."/>
					</td>
				</tr>
				<tr>
					<td>
						<label for="user:surname">Surname: </label>
					</td>
					<td>
						<input name="user:surname" type="text" size="100" value="${surname}" placeholder="please enter your family name (min 3, max 20)..."/>
					</td>
				</tr>
				<tr>
					<td>
						<label for="user:contacts.phone">Phone no.: </label>
					</td>
					<td>
						<input name="user:contacts.phone" type="text" size="100" value="${phone}" placeholder="please enter your phone number (06-555-12345)..."/>
					</td>
				</tr>
				<tr>
					<td>
						<label for="user:contacts.email">Email: </label>
					</td>
					<td>
						<input name="user:contacts.email" type="text" size="100" value="${email}" placeholder="please enter your email address (a.b@c.com)..."/>
					</td>
				</tr>
				<tr>
					<td>
						<label for="user:address.street">Street: </label>
					</td>
					<td>
						<input name="user:address.street" type="text" size="100" value="${street}" placeholder="please enter the street where you live (min 4)..."/>
					</td>
				</tr>
				<tr>
					<td>
						<label for="user:address.number">Number: </label>
					</td>
					<td>
						<input name="user:address.number" type="text" size="100" value="${number}" placeholder="please enter your street number..."/>
					</td>
				</tr>
				<tr>
					<td>
						<label for="user:address.zip">ZIP code: </label>
					</td>
					<td>
						<input name="user:address.zip" type="text" size="100" value="${zip}" placeholder="please enter your ZIP code..."/>
					</td>
				</tr>
				<tr>
					<td>
						<label for="user:address.town">Town: </label>
					</td>
					<td>
						<input name="user:address.town" type="text" size="100" value="${town}" placeholder="please enter the town you live in..."/>
					</td>
				</tr>
				<tr>
					<td>
						<label for="user:sex">Sex: </label>
					</td>					
					<td>
						<input type="radio" name="user:sex" value="male">Male
						
						<input type="radio" name="user:sex" value="female">Female
					</td>
				</tr>
				<tr>
					<td>
						<label for="user:music">Music Interests: </label>
					</td>
					<td>
						<input type="checkbox" name="user:music" value="Rock">Rock
						<input type="checkbox" name="user:music" value="Pop">Pop
						<input type="checkbox" name="user:music" value="Jazz">Jazz
						<input type="checkbox" name="user:music" value="Blues">Blues
						<input type="checkbox" name="user:music" value="Funk">Funk 					
						<input type="checkbox" name="user:music" value="Reggae">Reggae
						<input type="checkbox" name="user:music" value="Blues">Classical
						<input type="checkbox" name="user:music" value="Funk">Ska 					
						<input type="checkbox" name="user:music" value="Reggae">New Wave
					</td>
				</tr>
			</table>
			<input type="submit" title="Submit" name="Submit" />	
			
			<br><br>
			Server Response:<br>
			<pre id="result"></pre>				
		</form>
	</fieldset>
	
	<br>
	
	<fieldset>
		<legend><b>File Upload</b></legend>
		<p>The following form allows to upload multiple files; the server will compute the MD5 checksum on those files, and print it out.</p>
		<p>This example showcases file handling: the controller will intercept the MIMe multipart-data upload, will parse the names of the
		files and make them available as temporary files through <code>@In</code> parameters to the actions, which will be able to access 
		them without having to deal with streams, buffer etc.</p>
		<i>
		<p><em>NOTE:</em> this example may or may not work, depending on the application server, due to a bug in the Servlet 3.0 specification:
		the specifications requires application servers to parse incoming multipart/form-data requests only if the target servlet is annotated
		with <code>@MultipartConfig</code>; filters, and the Zephyr Controller with them, is not a servlet, and does not require any servlet 
		to be deployed to work; thus, the HttpServletRequest#getParts() method may return an empty collection if the implementors interpreted
		the specification as forbidding the parts parsing unless the target is an annotated servlet. This is the case with Apache Tomcat, but
		you can force the server to behave in a less strict way by setting the <code>allowCasualMultipartParsing</code> parameter in the context.</p>
		<p>For additional details on the Servlet 3.0 Specification bug see <a href="https://java.net/jira/browse/SERVLET_SPEC-87">here</a>.</p>
		<p>For more details on how to configure Tomcat to enable multipart/form-data parsing in Servlet Filters see 
		<a href="http://tomcat.apache.org/tomcat-7.0-doc/config/context.html">here</a>.</p>
		</i>   
		<form action="TestAction!onFileUpload" method="post" enctype="multipart/form-data" id="uploadForm">
			<table>
				<tr>
					<td>
						<label for="file01">First file:</label>
					</td>
					<td>
						<input name="file01" type="file" size="100" placeholder="please select a file..."/>
					</td>
				</tr>
				<tr>
					<td>
						<label for="file02">Second file:</label>
					</td>
					<td>
						<input name="file02" type="file" size="100" placeholder="please select a file..."/>
					</td>
				</tr>
			</table>
			<input type="submit" title="Submit" name="Submit" />
		</form>
	</fieldset>
	<br>
	<p>Powered by <z:hyperlink/></p>
</body>
</html>
