<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>miRTMC</title>
</head>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()
			+ "://"
			+ request.getServerName()
			+ (request.getServerPort() == 80 ? "" : ":"
					+ request.getServerPort()) + path;
	request.setAttribute("_basepath", basePath);
%>
<script type="text/javascript">
	var _basepath = "${_basepath}";
	window.location.href = "${_basepath}/index";
</script>
</head>
<body>
</body>
</html>

