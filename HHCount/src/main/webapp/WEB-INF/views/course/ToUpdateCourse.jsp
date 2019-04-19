<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="zh">
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"> 
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>课程修改</title>
	<link type="image/png" href="http://tm-image.tianyancha.com/tm/b928c552e7d861b16141d5d40e950749.jpg" rel="shortcut icon">
	<script  src="<%=request.getContextPath()%>/static/js/jquery-3.3.1.min.js"></script>	
	<link rel="stylesheet" href="<%=request.getContextPath() %>/course/css/normalize.css" />
	<link rel="stylesheet" href="<%=request.getContextPath() %>/course/css/demo.css">
	<link rel="stylesheet" href="<%=request.getContextPath() %>/course/css/sheets-of-paper-a4.css">	
</head>
<script type="text/javascript">
	function submitFo(form){
		var coursename= document.getElementById("coursename");
		if(coursename.value.trim()==""){
			alert("课程名不能为空");
			return false;
		}else{			
			return true;
		}
	}
	
</script>
<body class="document">
	<div class="page" >	
	<div align="right"><a style="text-decoration: underline;" href="<%=request.getContextPath()%>/Admin/toAdminIndex">返回</a></div>
	<h2 align="center">课程修改</h2>
	<form action="<%=request.getContextPath()%>/Course/Update" method="post" onSubmit="return submitFo(this);">	
		<input type="hidden" name="writer" value="${updateCourse.writer}">
		<input type="hidden" name="writerid" value="${updateCourse.writerid}">
		<input type="hidden" name="id" value="${updateCourse.id}">
		<input type="hidden" name="state" value="${updateCourse.state}">
		<input type="hidden" name="createtime" value="<fmt:formatDate
			value="${updateCourse.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>">
		课程名：
		<input type="text" value="${updateCourse.coursename}" name="coursename" id="coursename" style=" width:650px;BORDER-TOP-STYLE: none; BORDER-RIGHT-STYLE: none; BORDER-LEFT-STYLE: none; BORDER-BOTTOM-STYLE: solid">		
		<br><br>
		每日阅读量：
		<input type="text" value="${updateCourse.courseRead}" name="courseRead" onkeyup="value=value.replace(/[^\d]/g,'')" style= "width:650px;BORDER-TOP-STYLE: none; BORDER-RIGHT-STYLE: none; BORDER-LEFT-STYLE: none; BORDER-BOTTOM-STYLE: solid">		
		<br><br>	
		课程内容：<br>
		<br>
		<textarea rows="30" cols="80" name="coursetext">
		${updateCourse.coursetext}
		</textarea>
		<p align="right">
		<input type="submit" value="内容提交">
		</p>
		</form>
	</div>	
</body>
</html>