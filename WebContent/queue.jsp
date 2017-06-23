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
	
	<link rel="stylesheet" type="text/css" href="<%=root %>/queue.css" />
	
	<script type="text/javascript">
	    var root = "<%=root %>";
	    var type = "${param.t}";
	</script>
	<script type="text/javascript" src="<%=root %>/lib/jquery-1.11.3.min.js"></script>
    <script type="text/javascript" src="<%=root %>/lib/audiojs/audio.min.js"></script>
	<script type="text/javascript" src="<%=root %>/queue.js"></script>
</head>
<body>
	<div id="word-content">
	    <div id="wrapper" style="margin: 0 auto;">
	      	<h1 id="audio-text">wait what — notorious xx <em>(2009)</em></h1>
	      	<audio preload id="audio-player"></audio>
	      
			<input id="passBtn" type="button" value="Pass" />
			<span class="repeat-status" style="border: 1px solid green; margin-left: 80px; padding: 8px; cursor: pointer;">Repeat</span>
	    </div>
	    
		<div id="audio-example" style="height: 520px; overflow: auto; margin-top: 10px;"></div>
	</div>

	<div id="translate-panel">
	</div>
</body>
</html>
