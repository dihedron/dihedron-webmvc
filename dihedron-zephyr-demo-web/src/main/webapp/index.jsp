<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="z" uri="http://www.dihedron.org/zephyr"%>
<html>
<body>
	<h2>Zephyr Test Cases</h2>


	<fieldset>
		<legend>Plain Form Submission</legend>
		<p>By pressing the below button you are submitting a plain form to an action; the one and only form parameter is passed to the action as an @In
			argument (as the "message" parameter) to the business method and is echoed back as an @Out parameter (in the "echo" request attribute).</p>
		<form action="TestAction!onSimpleFormSubmission">
			<label for="message">Message</label><input type="text" name="message" value="" />
			<input type="submit" title="Invia" name="Invia" value="Invia" />
			<z:useBean var="echo" name="echo" type="java.lang.String"></z:useBean>
			<c:if test="${echo !=null && echo.trim().length() > 0}">
				<br>Echoed message was: '<b><%=echo%></b>'
			</c:if>			
		</form>


	</fieldset>


</body>
</html>
