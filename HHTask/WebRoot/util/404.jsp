<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<base href="<%=basePath%>">
		<title>错误提示页面,${companyInfo.shortName}作业网</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link rel="shortcut icon" href="favicon.ico" />
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<link rel="stylesheet" type="text/css" href="css/css.css">
		<style data-for="result">
body {
	color: #333;
	background: #fff;
	padding: 0;
	margin: 0;
	position: relative;
	min-width: 700px;
	font-family: arial;
	font-size: 12px
}

p,form,ol,ul,li,dl,dt,dd,h3 {
	margin: 0;
	padding: 0;
	list-style: none
}

input {
	padding-top: 0;
	padding-bottom: 0;
	-moz-box-sizing: border-box;
	-webkit-box-sizing: border-box;
	box-sizing: border-box
}

img {
	border: none;
}

.logo {
	width: 117px;
	height: 38px;
	cursor: pointer
}

#wrapper {
	_zoom: 1
}

#head {
	padding-left: 35px;
	margin-bottom: 20px;
	width: 900px
}

.fm {
	clear: both;
	position: relative;
	z-index: 297
}

.btn,#more {
	font-size: 14px
}

.s_btn {
	width: 95px;
	height: 32px;
	padding-top: 2px\9;
	font-size: 14px;
	padding: 0;
	background-color: #ddd;
	background-position: 0 -48px;
	border: 0;
	cursor: pointer
}

.s_btn_h {
	background-position: -240px -48px
}

.s_btn_wr {
	width: 97px;
	height: 34px;
	display: inline-block;
	background-position: -120px -48px; *
	position: relative;
	z-index: 0;
	vertical-align: top
}

#foot {
	
}

#foot span {
	color: #666
}

.s_ipt_wr {
	height: 32px
}

.s_form:after,.s_tab:after {
	content: ".";
	display: block;
	height: 0;
	clear: both;
	visibility: hidden
}

.s_form {
	zoom: 1;
	height: 55px;
	padding: 0 0 0 10px
}

#result_logo {
	float: left;
	margin: 7px 0 0
}

#result_logo img {
	width: 101px
}

#head {
	padding: 0;
	margin: 0;
	width: 100%;
	position: absolute;
	z-index: 301;
	min-width: 1000px;
	background: #fff;
	border-bottom: 1px solid #ebebeb;
	position: fixed;
	_position: absolute;
	-webkit-transform: translateZ(0)
}

#head .head_wrapper {
	_width: 1000px
}

#head.s_down {
	box-shadow: 0 0 5px #888
}

.fm {
	clear: none;
	float: left;
	margin: 11px 0 0 10px
}

#s_tab {
	background: #f8f8f8;
	line-height: 36px;
	height: 38px;
	padding: 55px 0 0 121px;
	float: none;
	zoom: 1
}

#s_tab a,#s_tab b {
	width: 54px;
	display: inline-block;
	text-decoration: none;
	text-align: center;
	color: #666;
	font-size: 14px
}

#s_tab b {
	border-bottom: 2px solid #38f;
	font-weight: bold;
	color: #323232
}

#s_tab a:hover {
	color: #323232
}

#content_left {
	width: 540px;
	padding-left: 121px;
	padding-top: 5px
}

.to_tieba,.to_zhidao_bottom {
	margin: 10px 0 0 121px
}

#help {
	background: #f5f6f5;
	zoom: 1;
	padding: 0 0 0 50px;
	float: right
}

#help a {
	color: #777;
	padding: 0 15px;
	text-decoration: none
}

#help a:hover {
	color: #333
}

#foot {
	position: fixed;
	bottom: 0;
	width: 100%;
	background: #f5f6f5;
	border-top: 1px solid #ebebeb;
	text-align: left;
	height: 42px;
	line-height: 42px;
	margin-top: 40px; *
	margin-top: 0;
	_position: absolute;
	_bottom: auto;
	_top: expression(eval(document.documentElement.scrollTop +
		document.documentElement.clientHeight-this.offsetHeight- ( parseInt(this.currentStyle.marginTop
		, 10) || 0 ) -( parseInt(this.currentStyle.marginBottom, 10) || 0 ) )
		);
}

.content_none {
	padding: 45px 0 25px 121px
}

.s_ipt_wr.bg,.s_btn_wr.bg,#su.bg {
	background-image: none
}

.s_ipt_wr.bg {
	background: 0
}

.s_btn_wr {
	width: auto;
	height: auto;
	border-bottom: 1px solid transparent; *
	border-bottom: 0
}

.s_btn {
	width: 100px;
	height: 34px;
	color: white;
	letter-spacing: 1px;
	background: #3385ff;
	border-bottom: 1px solid #2d78f4;
	outline: medium; *
	border-bottom: 0;
	-webkit-appearance: none;
	-webkit-border-radius: 0
}

.s_btn:hover {
	background: #317ef3;
	border-bottom: 1px solid #2868c8; *
	border-bottom: 0;
	box-shadow: 1px 1px 1px #ccc
}

.s_btn:active {
	background: #3075dc;
	box-shadow: inset 1px 1px 3px #2964bb;
	-webkit-box-shadow: inset 1px 1px 3px #2964bb;
	-moz-box-shadow: inset 1px 1px 3px #2964bb;
	-o-box-shadow: inset 1px 1px 3px #2964bb
}

#lg {
	display: none
}

#head .headBlock {
	margin: -5px 0 6px 121px
}

#content_left .leftBlock {
	margin-bottom: 14px;
	padding-bottom: 5px;
	border-bottom: 1px solid #f3f3f3
}

.s_ipt_wr {
	border: 1px solid #b6b6b6;
	border-color: #7b7b7b #b6b6b6 #b6b6b6 #7b7b7b;
	background: #fff;
	display: inline-block;
	vertical-align: top;
	width: 539px;
	margin-right: 0;
	border-right-width: 0;
	border-color: #b8b8b8 transparent #ccc #b8b8b8;
	overflow: hidden
}

.s_ipt_wr.ip_short {
	width: 439px;
}

.s_ipt_wr:hover,.s_ipt_wr.ipthover {
	border-color: #999 transparent #b3b3b3 #999
}

.s_ipt_wr.iptfocus {
	border-color: #4791ff transparent #4791ff #4791ff
}

.s_ipt_tip {
	color: #aaa;
	position: absolute;
	z-index: -10;
	font: 16px/ 22px arial;
	height: 32px;
	line-height: 32px;
	padding-left: 7px;
	overflow: hidden;
	width: 526px
}

.s_ipt {
	width: 526px;
	height: 22px;
	font: 16px/ 18px arial;
	line-height: 22px\9;
	margin: 6px 0 0 7px;
	padding: 0;
	background: transparent;
	border: 0;
	outline: 0;
	-webkit-appearance: none
}

#kw {
	position: relative;
	display: inline-block;
}

input::-ms-clear {
	display: none
}

/*Error page css*/
.norsSuggest {
	display: inline-block;
	color: #333;
	font-family: arial;
	font-size: 13px;
	position: relative;
}

.norsTitle {
	font-size: 22px;
	font-family: Microsoft Yahei;
	font-weight: normal;
	color: #333;
	margin: 35px 0 25px 0;
}

.norsTitle2 {
	font-family: arial;
	font-size: 13px;
	color: #666;
}

.norsSuggest ol {
	margin-left: 47px;
}

.norsSuggest li {
	margin: 13px 0;
}

#content_right {
	border-left: 1px solid #e1e1e1;
	width: 384px;
	margin-top: 25px;
	float: right;
	padding-left: 17px;
}

#wrapper_wrapper {
	width: 1212px;
}

.cr-content {
	width: 351px;
	font-size: 13px;
	line-height: 1.54em;
	color: #333;
	margin-top: 6px;
	margin-bottom: 28px;
	word-wrap: break-word;
	word-break: normal;
}

@media screen and (max-width: 1217px) {
	#wrapper_wrapper {
		width: 1002px;
	}
	#wrapper_wrapper #content_right {
		width: 271px;
	}
	#wrapper_wrapper .cr-content {
		width: 259px;
	}
}

.opr-toplist-title {
	position: relative;
	font-size: 14px;
	line-height: 1.29em;
	font-weight: 700;
	margin-bottom: 10px;
}

.opr-toplist-table {
	width: 100%;
	border-collapse: collapse;
	border-spacing: 0;
	font-size: 13px;
}

.opr-toplist-table th,td {
	line-height: 1.54;
	border-bottom: 1px solid #f3f3f3;
	text-align: left;
}

.opr-toplist-table thead th {
	padding-top: 4px;
	padding-bottom: 4px;
	font-weight: 400;
	color: #666;
	white-space: nowrap;
	background-color: #fafafa;
}

.opr-toplist-table .opr-toplist-right {
	text-align: right;
	white-space: nowrap;
}

.opr-toplist-table td {
	width: 100%;
	font-size: 13px;
	padding-top: 6.5px;
	padding-bottom: 6.5px;
	vertical-align: top;
}

.opr-toplist-table a:hover {
	text-decoration: underline;
}

.opr-index-item {
	display: inline-block;
	padding: 1px 0;
	color: #fff;
	width: 14px;
	line-height: 100%;
	font-size: 12px;
	text-align: center;
	background-color: #8eb9f5;
	margin-right: 5px;
}

.opr-index-hot1 {
	background-color: #f54545;
}

.opr-index-hot2 {
	background-color: #ff8547;
}

.opr-index-hot3 {
	background-color: #ffac38;
}

.opr-item-text {
	text-decoration: none;
}

.opr-toplist-info {
	color: #666;
	text-align: right;
	margin-top: 5px;
}

.opr-toplist-info>a {
	color: #666;
}
</style>
	</head>
	<body bgcolor="#ffffff">
		<center>
			<%@include file="sonTop.jsp"%>
			<div id="content_left" align="left">
				<div class="nors">
					<div class="norsSuggest">
						<h3 class="norsTitle">
							很抱歉，您要访问的页面不存在！
						</h3>
						<p class="norsTitle2">
							温馨提示：
						</p>
						<ol style="list-style: none;">
							<li>
								请检查您访问的网址是否正确
							</li>
							<li>
								如果您不能确认访问的网址，请点击<a href="index.jsp">返回首页</a>。
							</li>
							<li>
								回到顶部重新发起搜索
							</li>
							<li>
								如有任何意见或建议，请及时联系系统管理员。
							</li>
						</ol>
					</div>
				</div>
			</div>
			<%@include file="foot.jsp"%>
		</center>
	</body>
</html>
