<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="z" uri="http://www.dihedron.org/zephyr"%>
<html>
<head>
	<title>Zephyr Demo</title>
	<link href="css/main.css" type="text/css" rel="stylesheet">
</head>
<body>
	<h2>Zephyr Test Cases</h2>


	<fieldset>
		<legend><b>Plain Form Submission (GET)</b></legend>
		<p>By pressing the below button you are submitting a plain form to an action, using the HTTP GET method.</p> 
		<p>The form contains a single parameter called "message", which is passed to the server-side action through an <code>@In</code>code> parameter; the receiving action will reverse the string 
		and send it back through an <code>@Out</code> parameter called "echo", which will be stored as a request attribute and be made available to the page via the Zephyr tag library.</p>
		<form action="TestAction!onSimpleFormSubmission">
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
		<p>The form contains a single parameter called "message", which is passed to the server-side action through an <code>@In</code>code> parameter; the receiving action will reverse the string 
		and send it back through an <code>@Out</code> parameter called "echo", which will be stored as a request attribute and be made available to the page via the Zephyr tag library.</p>
		<form action="TestAction!onSimpleFormSubmission" method="post">
			<label for="message">Message</label><input type="text" name="message" value="" />
			<input type="submit" title="Submit" name="Submit" value="Submit" />
			<z:useBean var="echoPOST" name="echoPOST" type="java.lang.String"></z:useBean>
			<c:if test="${echoPOST !=null && echoPOST.trim().length() > 0}">
				<br><br>
				The reverse of what you said is: <b><c:out value="${echoPOST}"/></b>:
				
			</c:if>			
		</form>
	</fieldset>
	
	<br>

	<fieldset>
		<legend><b>Complex Form Submission (POST)</b></legend>
		<p>By pressing the below button you are submitting a complex form to an action, using an HTTP POST call.</p> 
		<p>The form contains many parameters of different types; each of them is mapped to the an input (<code>@In</code>) parameter to the server side method.</p> 
		<p>The JSON representation of the form will be returned as an <code>@Out</code> parameter and be made available on the page.</p>
		<form action="TestAction!onComplexFormSubmission" method="post">
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
						<input name="street" type="text" size="100" value="${street}" placeholder="please enter the street where you live (min 5)..."/>
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
				
			</table>
			<%--
			<fieldset>
				<legend>Personal Info</legend>
				<label for="name">Name: </label><input name="name" type="text" value="${name}" placeholder="please enter your name (min 3, max 20)..."/><br>
				<label for="surname">Surname: </label><input name="surname" type="text" value="${surname}" placeholder="please enter your family name (min 3, max 20)..."/><br>
				<label for="phone">Phone no.: </label><input name="phone" type="text" value="${phone}" placeholder="please enter your phone number (06-555-12345)..."/><br>
				<label for="email">Email: </label><input name="email" type="text" value="${email}" placeholder="please enter your email address (a.b@c.com)..."/><br>
				<label for="street">Street: </label><input name="street" type="text" value="${street}" placeholder="please enter the street where you live (min 5)..."/><br>
				<label for="number">Number: </label><input name="number" type="text" value="${number}" placeholder="please enter your street number..."/><br>
				<label for="zip">ZIP code: </label><input name="zip" type="text" value="${zip}" placeholder="please enter your ZIP code..."/><br>
				<label for="town">Town: </label><input name="town" type="text" value="${town}" placeholder="please enter the town you live in..."/><br>
			</fieldset>
			--%>
			<input type="submit" title="Submit" name="Submit" value="Submit" />	
			
			<z:useBean var="user" name="user" type="java.lang.String"></z:useBean>
			<c:if test="${user !=null && user.trim().length() > 0}">
				<br><br>
				The server answered:
				<pre><c:out value="${user}"/></pre>
			</c:if>			
				
		</form>
	</fieldset>
		<%--
		
		
<aui:form method="post" action="${formUrl}">
	<aui:fieldset label="Personal Info">
		<aui:input label="Name (min 3, max 20):" name="user:name" type="text" value="${user.name}" placeholder="please enter your name..."/>
		<aui:input label="Surname:" name="user:surname" type="text" value="${user.surname}" placeholder="please enter your family name..."/>
		<aui:input label="Phone (06-555-12345):" name="user:phone" type="text" value="${user.phone}" placeholder="please enter your phone number..."/>
		<aui:input label="Email:" name="user:email" type="text" value="${user.email}" placegolder="please enter your email address..."/>
		<aui:input label="Street:" name="user:address.street" type="text" value="${user.address.street}" placeholder="please enter the street where you live..."/>
		<aui:input label="Street no.:" name="user:address.number" type="text" value="${user.address.number}" placeholder="please enter your street number..."/>
		<aui:input label="ZIP Code:" name="user:address.zip" type="text" value="${user.address.zip}" placeholder="please enter your ZIP code..."/>
		<aui:input label="Town:" name="user:address.town" type="text" value="${user.address.town}" placeholder="please enter the town you live in..."/>
	</aui:fieldset>
	<%--
	<aui:fieldset label="Loves:">
		<aui:input label="Animals" name="loves" type="checkbox" value="animals"/>
		<aui:input label="Flowers" name="loves" type="checkbox" value="flowers"/>
		<aui:input label="Food" name="loves" type="checkbox" value="food"/>
		<aui:input label="Music" name="loves" type="checkbox" value="music"/>
		<aui:input label="Movies" name="loves" type="checkbox" value="movies"/>
	</aui:fieldset>
	<aui:fieldset label="Redirect?">
			<aui:input inlineLabel="right" name="redirect" type="radio" value="notatall" label="No, just go on..." checked="true"/>
			<aui:input inlineLabel="right" name="redirect" type="radio" value="homepage" label="Go to the homepage"/>
			<aui:input inlineLabel="right" name="redirect" type="radio" value="absolute" label="Go to www.google.com" />			
			<aui:input inlineLabel="right" name="redirect" type="radio" value="internal" label="Go to a demo JSP"/>
	</aui:fieldset>
	
	<br>
	<aui:button type="submit" value="Submit!"/> 
</aui:form> 		
		
	</fieldset>
--%>
</body>
</html>
