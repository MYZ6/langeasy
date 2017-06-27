<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%
	String root = request.getContextPath();
%>
<!doctype html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Gone with the Wind</title>
	
	<link rel="stylesheet" type="text/css" href="<%=root %>/gwtw/sentence.css" />
	
	<script type="text/javascript">
	    var root = "<%=root %>";
	    var type = "${param.t}";
	</script>
	<script type="text/javascript" src="<%=root %>/lib/jquery-1.11.3.min.js"></script>
    <script type="text/javascript" src="<%=root %>/lib/audiojs/audio.min.js"></script>
	<script type="text/javascript" src="<%=root %>/gwtw/read.js"></script>
</head>
<body>
	<div id="word-content" style="">
	</div>

	<div id="translate-panel">
	</div>
</body>
</html>
