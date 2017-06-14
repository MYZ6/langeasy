<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%
	String root = request.getContextPath();
%>
<!doctype html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>vocabulary</title>
	
	<link rel="stylesheet" type="text/css" href="<%=root %>/gwtw/main.css" />
	
	<script type="text/javascript">
	    var root = "<%=root %>";
	    var type = "${param.t}";
	</script>
	<script type="text/javascript" src="<%=root %>/lib/jquery-1.11.3.min.js"></script>
	<script type="text/javascript" src="<%=root %>/gwtw/main.js"></script>
</head>
<body>

	<div id="word-list" class="word-list" style="">
	</div>

	<div id="checked-list" class="word-list" style="">
	</div>

	<div class="command-zone">
		<div>Total: <t class="total"></t></div>
		<input type="button" id="btn-pass" value="Pass" />
		<br>
		<input type="button" id="btn-unknown" value="Unknown" />
		<input type="button" id="btn-refresh" value="Refresh" />
		<div id="translate">
		
		
		</div>
	</div>
</body>
</html>
