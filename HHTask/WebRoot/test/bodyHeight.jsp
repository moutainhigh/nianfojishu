<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>请调整浏览器窗口</title>
		<meta http-equiv="content-type" content="text/html; charset=gb2312">
		</meta>
	</head>
	<body>
		<h2 align="center">
			请调整浏览器窗口大小
		</h2>
		<hr />
		<form action="#" method="get" name="form1" id="form1">
			<!--显示浏览器窗口的实际尺寸-->
			浏览器窗口 的 实际高度:
			<input type="text" name="availHeight" size="4" />
			<br />
			浏览器窗口 的 实际宽度:
			<input type="text" name="availWidth" size="4" />
			<br />
		</form>
		<script type="text/javascript">
var winWidth = 0;
var winHeight = 0;
function findDimensions() //函数：获取尺寸 
{
	//获取窗口宽度 
	if (window.innerWidth)
		winWidth = window.innerWidth;
	else if ((document.body) && (document.body.clientWidth))
		winWidth = document.body.clientWidth;
	
	//获取窗口高度 
	if (window.innerHeight) {
		winHeight = window.innerHeight;
	} else if ((document.body) && (document.body.clientHeight))
		winHeight = document.body.clientHeight;
	
	//通过深入Document内部对body进行检测，获取窗口大小 
	if (document.documentElement && document.documentElement.clientHeight
			&& document.documentElement.clientWidth) {
		winHeight = document.documentElement.clientHeight;
		winWidth = document.documentElement.clientWidth;
	}
	//结果输出至两个文本框 
	document.form1.availHeight.value = winHeight;

	document.form1.availWidth.value = winWidth;
}

findDimensions();

//调用函数，获取数值 
window.onresize = findDimensions;

</script>
	</body>
</html>
