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
	
	<link rel="stylesheet" type="text/css" href="<%=root %>/index.css" />
	
	<script type="text/javascript">
	    var root = "<%=root %>";
	    var type = "${param.t}";
	</script>
	<script type="text/javascript" src="<%=root %>/lib/jquery-1.11.3.min.js"></script>
    <script type="text/javascript" src="<%=root %>/lib/audiojs/audio.min.js"></script>
	<script type="text/javascript" src="<%=root %>/index.js"></script>
</head>
<body>
<!--     <div id="shortcuts"> -->
<!--       <div> -->
<!--         <h1>Keyboard shortcuts:</h1> -->
<!--         <p><em>&rarr;</em> Next track</p> -->
<!--         <p><em>&larr;</em> Previous track</p> -->
<!--         <p><em>Space</em> Play/pause</p> -->
<!--       </div> -->
<!--     </div> -->

	<div id="word-list" style="">
	</div>

	<div id="word-content" style="">
	    <div id="wrapper">
	      	<h1 id="audio-text">wait what — notorious xx <em>(2009)</em></h1>
	      	<audio preload id="audio-player"></audio>
	      
			<input id="passBtn" type="button" value="Pass" />
	    </div>
	    
		<div id="word-title"></div>
		<div id="pron"></div>
		<div id="meaning"></div>
		
		<h3>Audio Examples</h3>
		<div id="audio-example"></div>
	</div>

	<div id="translate-panel">
	</div>
</body>
</html>
