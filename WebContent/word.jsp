<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%
	String root = request.getContextPath();
%>
<!doctype html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>word</title>
	
	<script type="text/javascript">
	    var root = "<%=root %>";
	    var wordId = "${param.id}";
	</script>
	<script type="text/javascript" src="<%=root %>/lib/jquery-1.11.3.min.js"></script>
	<script type="text/javascript" src="<%=root %>/word.js"></script>
</head>
<body>

	<div id="word-content">
		<div id="word-title"></div>
		<div id="pron"></div>
		<input type="button" id="btn-listen" value="listen" />
		<div id="meaning"></div>
		
		<br>
		<div id="audio-example"></div>
	</div>

</body>
</html>
