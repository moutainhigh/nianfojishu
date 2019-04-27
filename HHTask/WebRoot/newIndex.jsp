<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="/WEB-INF/fenye.tld" prefix="fenye"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<script type="text/javascript">
if (/Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent)) {
	window.location.href = "ModuleFunctionAction!findMfByUser.action?pageStatus=qx";
}
</script>
		<meta charset="UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=Edge">
		<title>PEBS生产力生态平衡系统</title>
		<link rel="shortcut icon"
			href="<%=basePath%>/upload/file/sysImages/favicon.ico" />
		<link type="text/css" rel="styleSheet" href="css/setting.css" />
		<link type="text/css" rel="styleSheet" href="css/index3.css" />
		<link type="text/css" rel="styleSheet" href="css/fileTree.css" />
		<link type="text/css" rel="styleSheet"
			href="css/jquery.mCustomScrollbar.css" />


		<link rel="stylesheet"
			href="<%=basePath%>/javascript/typeahead.js/examples.css" />

		<script type="text/javascript" src="js/jQuery-1.7.1.js">
</script>
		<script type="text/javascript" src="js/jquery.tree.js">
</script>
		<script type="text/javascript" src="js/time.js">
</script>
		<script type="text/javascript" src="js/leftnav.js">
</script>
		<script type="text/javascript" src="js/jqPaginator.js">
</script>
		<!-- ztree -->
		<script type="text/javascript"
			src="${pageContext.request.contextPath}/javascript/zTree/js/jquery.ztree.core-3.5.js">
</script>
		<!-- typeahead -->
		<script language="javascript"
			src="${pageContext.request.contextPath}/javascript/typeahead.js/typeahead.bundle.js">
</script>

		<!-- 表单验证CSS -->
		<script
			src="<%=basePath%>javascript/calendar/js/formValidator/js/jquery.validationEngine.js"
			type="text/javascript">
</script>
		<script
			src="<%=basePath%>javascript/calendar/js/formValidator/js/jquery.validationEngine-en.js"
			type="text/javascript">
</script>
		<link rel="stylesheet"
			href="<%=basePath%>javascript/calendar/js/formValidator/css/validationEngine.jquery.css"
			type="text/css" media="screen" charset="utf-8" />


		<!-- Jquery and Jquery UI -->
		<script type="text/javascript"
			src="<%=basePath%>javascript/calendar/js/jquery-ui-1.8.6.custom.min.js">
</script>
		<script type="text/javascript"
			src="<%=basePath%>javascript/calendar/js/jquery-ui-timepicker-addon.js">
</script>
		<link rel="stylesheet"
			href="<%=basePath%>javascript/calendar/css/redmond/jquery-ui-1.8.1.custom.css">

		<!-- FullCalender -->
		<%--<link--%>
			<%--href='<%=basePath%>javascript/fullcalendar-3.7.0/fullcalendar.min.css'--%>
			<%--rel='stylesheet' />--%>
		<%--<link--%>
			<%--href='<%=basePath%>javascript/fullcalendar-3.7.0/fullcalendar.print.min.css'--%>
			<%--rel='stylesheet' media='print' />--%>
		<%--<script--%>
			<%--src='<%=basePath%>javascript/fullcalendar-3.7.0/lib/moment.min.js'>--%>
<%--</script>--%>
		<%--<script--%>
			<%--src='<%=basePath%>javascript/fullcalendar-3.7.0/fullcalendar.js'>--%>
<%--</script>--%>
		<%--<script src='<%=basePath%>javascript/fullcalendar-3.7.0/locale-all.js'>--%>
<%--</script>--%>

		<link
				href='<%=basePath%>javascript/fullcalendar-2.9.1/fullcalendar.css'
				rel='stylesheet' />
		<link
				href='<%=basePath%>javascript/fullcalendar-2.9.1/fullcalendar.print.css'
				rel='stylesheet' media='print' />
		<script
				src='<%=basePath%>javascript/fullcalendar-2.9.1/lib/moment.min.js'>
		</script>
		<script
				src='<%=basePath%>javascript/fullcalendar-2.9.1/fullcalendar.js'>
		</script>
		<script src='<%=basePath%>javascript/fullcalendar-2.9.1/lang-all.js'>
		</script>

		<!-- bootstrap -->
		<%--		<link rel="stylesheet" type="text/css"--%>
		<%--			href="${pageContext.request.contextPath}/javascript/bootstrap-3.3.7-dist/css/bootstrap.min.css">--%>
		<%--		<script type="text/javascript"--%>
		<%--			src="${pageContext.request.contextPath}/javascript/bootstrap-3.3.7-dist/js/bootstrap.js">--%>
		<%--</script>--%>

		<script src="<%=basePath%>javascript/DatePicker/WdatePicker.js">
</script>
		<style>
body {
	padding: 0;
	font-family: "Lucida Grande", Helvetica, Arial, Verdana, sans-serif;
	font-size: 14px;
}

#calendar {
	width: 1000px;
	float: left;
}

#calendarDetail {
	font-size: 20px;
}

#messageTitle {
	color: red;
}

.sysheader {
	padding: 10px 5px 10px 0px;
	font-size: 14px;
	font-weight: normal;
	margin: 0;
}

.sysdesc {
	padding: 10px 5px 10px 0px;
	margin: 0;
}

.rowElem {
	height: 35px;
	clear: left;
}

.rowElem label {
	clear: left;
	float: left;
	padding: 2px 6px 2px 0px;
	text-align: left;
	width: 60px;
}

.rowElem input {
	display: block;
	float: left;
	width: 230px;
	padding: 5px;
}

.rowElem select {
	display: block;
	width: 230px;
	padding: 5px;
}

.rowElem span {
	float: left;
	width: 85px;
}

.clear {
	clear: both;
}

#headercontent a {
	text-decoration: none;
}

.dialogmask {
	display: none;
	z-index: 9999;
	text-align: center;
	padding-top: 50px;
}

.typeahead {
	font-size: 12px;
}
</style>
	</head>
	<body>
		<!-- 导航栏-->
		<!-- 功能列表-->
		<div class="menu custom-menu-trigger-selector" id="menu"
			style="position: absolute; z-index: 999; display: none; position: fixed;">
			<div style="position: fixed; top: 0; left: 0;">

				<ul>
					<li>
						<a href="ModuleFunctionAction!findMfByUser.action"><img
								src="img/pebs_logo.png" height="36" width="122" class="logoleft">
						</a>
					</li>
					<li>
						<a href="ModuleFunctionAction!findMfByUser.action"> <s:if
								test='#session.Users.sex =="男"'>
								<img alt="${Users.name}"
									src="upload/user/${Users.password.picture}" height="88"
									width="89"
									style="border: solid 1px #000000; border-radius: 50%;"
									onerror="this.src='images/man.jpg'">
							</s:if> <s:else>
								<img alt="${Users.name}"
									src="upload/user/${Users.password.picture}" height="88"
									width="89"
									style="border: solid 1px #000000; border-radius: 50%;"
									onerror="this.src='images/woman.jpg'">
							</s:else> </a>
					</li>
				</ul>
				<ul id="list">
					<s:iterator id="mf" value="allModuleList" status="pageId">
						<li data="${mf.id}" style="background-image: url('');">
							<a href="javascript:;">${mf.functionName}</a>
						</li>
					</s:iterator>
				</ul>
			</div>
		</div>
		<div id="marginDiv"
			style="background-color: #33363f; position: absolute; z-index: 999; left: 0px; padding: 0px; margin: 0px; overflow: hidden; position: fixed; width: 5px; height: 100%">
		</div>
		<div id="showOrCloseMenuDiv"
			style="position: absolute; z-index: 999; left: 5px; padding: 0px; margin: 0px; overflow: hidden; position: fixed; background-color: #252733; height: 100%">
			<div
				style="background-color: #33363f; color: #ffffff; margin-bottom: 10px;"
				id="showOrCloseMenu" align="center">
				<SPAN id="jiantou">》</SPAN>
				<br />
				P
				<br />
				E
				<br />
				B
				<br />
				S
				<br />
			</div>
			<div style="background-color: #252733; margin-top: 40px;"
				align="center">
				<a onclick="showWork('5');"
					href="ModuleFunctionAction!findMfByIdForJump.action?id=5&pageStatus=3"
					title="项目管理" target="workMain"><img src="img/project.png"
						height="20" width="20">
					<div class="moname">
						项目
					</div> </a>
				<a onclick="showWork('8');"
					href="ModuleFunctionAction!findMfByIdForJump.action?id=8&pageStatus=3"
					title="采购管理" target="workMain"><img src="img/caigou.png"
						height="20" width="20">
					<div class="moname">
						采购
					</div> </a>
				<a onclick="showWork('6');"
					href="ModuleFunctionAction!findMfByIdForJump.action?id=6&pageStatus=3"
					title="生产管理" target="workMain"><img src="img/product.png"
						height="20" width="20">
					<div class="moname">
						生产
					</div> </a>
				<a onclick="showWork('605');"
					href="ModuleFunctionAction!findMfByIdForJump.action?id=605&pageStatus=3"
					title="物流系统" target="workMain"><img src="img/wl.png"
						height="20" width="20">
					<div class="moname">
						物流
					</div> </a>
				<a onclick="showWork('19');"
					href="ModuleFunctionAction!findMfByIdForJump.action?id=19&pageStatus=3"
					title="财务管理" target="workMain"><img src="img/finance.png"
						height="20" width="20">
					<div class="moname">
						财务
					</div> </a>
				<a onclick="showWork('20');"
					href="ModuleFunctionAction!findMfByIdForJump.action?id=20&pageStatus=3"
					title="人事管理" target="workMain"><img src="img/resource.png"
						height="20" width="20">
					<div class="moname">
						人事
					</div> </a>
				<a onclick="showWork('22');"
					href="ModuleFunctionAction!findMfByIdForJump.action?id=22&pageStatus=3"
					title="信息管理" target="workMain"><img src="img/message.png"
						height="20" width="20">
					<div class="moname">
						信息
					</div> </a>
				<a onclick="showWork('722');"
					href="ModuleFunctionAction!findMfByIdForJump.action?id=722&pageStatus=3"
					title="仪表盘" target="workMain"><img src="img/ybp.png"
						height="20" width="20"
					<div class="moname">
						仪表
					</div> </a>
			</div>
		</div>
		<!-- 功能明细 (先生成需要的明细div)-->
		<s:iterator id="mf" value="allModuleList" status="pageId">
			<div class="menucontainer">
				<!-- 关闭右边按钮 -->
				<div class="togglebtn">
				</div>
				<div class="content-1 left">
					<ul id="ztreeView" class="files">
					</ul>
				</div>
			</div>
		</s:iterator>
		<!-- 功能列表over-->

		<div class="centercontainer" style="margin-left: 25px;">
			<div class="header">
				<div class="headrinfo">
					<ul>
						<li>
							<h1>
								<a href="ModuleFunctionAction!findMfByUser.action"> 欢迎您,
									${sessionScope.Users.name}! </a>
							</h1>
						</li>
						<li>
							<a href="ModuleFunctionAction!findMfByUser.action"><img
									src="img/icon_03.png" height="13" width="13"> </a>
						</li>

						<li>
							<a onclick="showWork();" href="userCenter/updatePassword.jsp"
								target="workMain"><img src="img/icon_06.png" height="13"
									width="13"> </a>
						</li>


						<li>
							<a onclick="showWork();"
								href="AlertMessagesAction!findAlertMessages.action?alertMessages.readStatus=no"
								target="workMain"> <img src="img/icon_08.png" height="12"
									width="12"><span class="messageCount">(0)</span> </a>
						</li>
						<%--						<li>--%>
						<%--							<a href="#" id="nav-add-favorite"><img src="img/icon_10.png"--%>
						<%--									height="13" width="14"> </a>--%>
						<%--						</li>--%>
						<!-- 邮件显示开始 -->
						<li>
							<a
								href="${pageContext.request.contextPath}/UsersAction!perfectUsers.action"
								target="_blank"><img src="img/icon_12.png" title="已读邮件"
									height="13" width="14"><span id="allMail"></span> </a>
						</li>
						<li>
							<a
								href="${pageContext.request.contextPath}/UsersAction!perfectUsers.action"
								target="_blank"><img src="img/icon_11.jpg" title="未读邮件"
									height="13" width="14"><span id="unReadMail"></span> </a>
						</li>
						<!-- 邮件显示结束 -->
						<li>
							<a onclick="showWork();" href="FavoriteAction_show.action"
								target="workMain" id="nav-add-favorite"><img
									src="img/icon_10.png" height="13" width="14"> </a>
						</li>
						<li>
							<a href="logOff.jsp">[退出]</a>
						</li>
					</ul>
					<div class="search" id="remote">
						<form class="searchFun"
							action="ModuleFunctionAction!searchModuleFunction.action"
							method="post" style="margin: 0px; padding-top: 10px;"
							target="workMain">
							<input type="text" name="moduleFunction.functionName"
								style="width: 180px; height: 15px"
								class="search_text form-control typeahead" accesskey="s"
								tabindex="9" autocomplete="off" x-webkit-speech=""
								x-webkit-grammar="builtin:search" />
							<%--							<input type="submit" class="search_bt search" value=""--%>
							<%--								style="width: 16px; height: 16px; background-repeat: no-repeat; background-image: url(img/search_03.png); border: 0;"--%>
							<%--								onclick="showWork();" />--%>
						</form>
					</div>

				</div>
			</div>
			<div class="notice">
				<div class="header_bt">
					<div class="noticeleft">
						<strong><img src="img/notice_07.png" height="22"
								width="22"> </strong>
					</div>
					<div class="marquee">
						<marquee direction="left" scrollamount="5"
							onmouseout="this.start()" onmouseover="this.stop()">
							<font color="#737373" id="show"></font>
						</marquee>
					</div>
					<div class="fr time">
						北京时间：
						<span id="hour">00</span>时
						<span id="minute">00</span>分
						<span id="second">00</span>秒
						<a onclick="showWork();"
							href="DingdanAction!listLoginUsers.action" target="workMain">
							当前 <a class="zxrs" style="color: #FF0000;"href="javascript:void(0)">${count}</a>人在线</a>
					</div>
				</div>
			</div>
			<div class="centernav">
				<ul class="leftindexnav" style="margin-left: 20px;">
					<li>
						<a href="ModuleFunctionAction!findMfByUser.action"><img
								src="img/navicon_06.png" height="20" width="21"> </a>
					</li>
					<li>
						<a href="ModuleFunctionAction!findMfByUser.action?pageStatus=qx"
							title="清新首页"><img src="img/navicon_09.png" height="19"
								width="19"> </a>
					</li>
					<li>
						<a href="ModuleFunctionAction!findMfByUser.action?pageStatus=xk"
							title="炫酷首页"><img src="img/navicon_03.png" height="20"
								width="18"> </a>
					</li>
					<li>
						<a onclick="showWork();" href="FavoriteAction_show.action"
							target="workMain" id="nav-add-favorite" title="我的收藏"><img
								src="img/icon_10.png" height="19" width="18"> </a>
					</li>
				</ul>
				<div class="cenjinav">
					<p></p>
				</div>
			</div>
			<div class="tablecontent">
				<div class="content">
					<div class="person" id="person">
						<div class="homepage">
							<div class="infoleft">
								<s:if test='#session.Users.sex =="男"'>
									<img alt="${Users.name}"
										src="upload/user/${Users.password.picture}" width="120px;"
										style="border: solid 1px #000000" height="130px;"
										onerror="this.src='images/man.jpg'">
								</s:if>
								<s:else>
									<img alt="${Users.name}"
										src="upload/user/${Users.password.picture}" width="120px;"
										style="border: solid 1px #000000" height="130px;"
										onerror="this.src='images/woman.jpg'">
								</s:else>
							</div>
							<div class="inforight">
								<h1>
									${Users.name} 提醒消息:
									<a onclick="showWork();"
										href="AlertMessagesAction!findAlertMessages.action?alertMessages.readStatus=no"
										target="workMain"><span class="messageCount">[0]</span> </a>项
								</h1>
								<ul>
									<li>
										<a onclick="showWork();" target="workMain"
											href="oaAppDetailAction!preSaveOADetail.action?oadetail.appayTag=A&tag=">采购申请</a>
									</li>
									<li>
										<a onclick="showWork();" target="workMain"
											href="AskForLeaveAction!preAskForLeave.action?askForLeave.appayTag=A">请假申请</a>
									</li>
									<li>
										<a onclick="showWork();" target="workMain"
											href="System/renshi/hr_overtime_add.jsp">加班申请</a>
									</li>
									<li>
										<%--<a onclick="showWork();" target="workMain"--%>
											<%--href="userCenter/log_workLog.jsp">工作记录</a>--%>
                                        <a onclick="showWork();" target="workMain"
											href="CalendarAction!findCalendarbyPerson.action">工作记录</a>
									</li>
									<li>
										<a onclick="showWork();" target="workMain"
											href="annualLeave!gerennianxiumingxi.action">个人年休</a>
									</li>
									<li>
										<a onclick="showWork();" target="workMain"
											href="annualLeave!listhuanxiumingxi.action">个人换休</a>
									</li>
									<li>
										<a onclick="showWork();" target="workMain"
											href="WageAction!findPersonWage.action">个人工资</a>
									</li>
									<li>
										<a onclick="showWork();" target="workMain"
											href="IntegralGiftAction_giftindex.action">积分乐园</a>
									</li>
									<li>
										<a onclick="showWork();" target="workMain"
											href="CorePayableAction!findSupplierCorePayablePerson.action">财务中心</a>
									</li>
									<li>
										<a onclick="showWork();" target="workMain"
											href="CircuitRunAction_findCircuitRun.action?circuitRun.addUserId=0">待审批</a>
									</li>
									<li>
										<a onclick="showWork();" target="workMain"
											href="meetingAction!findMeetingByCon.action">会议安排</a>
									</li>
									<li>
										<a  target="workMain2"
											href="inDoorScreenAction!gotoIndoorScreenBySrc.action?id=1">E-Work</a>
									</li>
								</ul>
							</div>
						</div>
						<div class="cal">
							<div id='calendar'></div>
							<DIV style="DISPLAY: none;" id="reservebox">
								<FORM action="CalendarAction!addCalendar.action"
									id="reserveformID" style="padding: 0px; margin: 0px;"
									  method="post" enctype="multipart/form-data">
									<DIV class="rowElem" align="left">
										<LABEL for="title">
											标题:
										</LABEL>
										<INPUT id="title" name="calendar.title"
											class="validate[required]" />
										<font color="red">*</font>
									</DIV>
									<DIV class="rowElem" align="left" style="height: 70px;">
										<LABEL for="content">
											内容:
										</LABEL>
										<textarea rows="4" cols="40" id="content"
											style="margin: 0px; padding: 0px;"
											name="calendar.thingContent"></textarea>
									</DIV>
									<div class="rowElem" align="left">
										<LABEL for="thingType">
											所属人:
										</LABEL>
										<input id="allusersid" name="usersid" type="hidden"style="width: 200px; float: left;"/>
										<input id="allusers" type="text"style="width: 200px; float: left;" readonly="readonly"/>
										<LABEL for="thingType">
											 选择
										</LABEL>
										<select id="dept" style="width: 80px; float: left;">
											<option></option>
										</select>
										<select id="users"  style="width: 80px; float: left;"
												name="calendar.userId" onchange="return addman(this.options[this.selectedIndex].value)" >
										</select>
										<%--<input type="button" value="添加" style="width: 40px; float: left;" onclick="addman()" />--%>
										<input type="button" value="清空" style="width: 40px; float: left;" onclick="delman()"/>

									</div>
									<div class="rowElem" align="left" id="deptdiv"></div>
									<DIV class="rowElem" align="left">
										<LABEL for="thingType">
											事件类型:
										</LABEL>
										<select id="thingType" style="width: 100px;"
											name="calendar.thingType">
											<option></option>
											<option value="工作计划">
												工作计划
											</option>
											<option value="工作日程">
												工作日程
											</option>
											<option value="法定假">
												法定假
											</option>
											<option value="年休假">
												年休假
											</option>
											<option value="换休">
												换休
											</option>
											<option value="公假">
												公假
											</option>
											<option value="病假">
												病假
											</option>
											<option value="婚假">
												婚假
											</option>
											<option value="丧假">
												丧假
											</option>
											<option value="事假">
												事假
											</option>
											<option value="双休日">
												双休日
											</option>
										</select>
									</DIV>
									<DIV class="rowElem" align="left">
										<LABEL for="start">
											开始时间:
										</LABEL>
										<INPUT id="start"
											class="validate[required,funcCall[validate2time]]"
											name="calendar.start"
											onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',skin:'whyGreen'})"
											style="width: 150px;" />
										<font color="red">*</font>
										<INPUT id="start2" name="calendar.start"
											style="width: 150px; display: none;" />

									</DIV>
									<DIV class="rowElem" align="left" />
										<LABEL for="end">
											结束时间:
										</LABEL>
										<INPUT id="end" name="calendar.endDate"
											onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',skin:'whyGreen'})"
											style="width: 150px;" />
										<INPUT id="end2" name="calendar.endDate"
											style="width: 150px; display: none;" />
									</DIV>
									<DIV class="rowElem" align="left">
										<LABEL for="allDay">
											是否全天:
										</LABEL>
										<input id="allDay" type="radio" value="true"
											name="calendar.allDay" checked="checked"
											style="float: left; width: 20px;" />
										<span style="float: left; width: 40px;">是</span>
										<input id="allDay2" type="radio" value="false"
											name="calendar.allDay" style="float: left; width: 40px;" />
										<span style="float: left; width: 40px;">否</span>
									</DIV>
									<DIV class="rowElem" align="left">
										<LABEL for="thingStatus">
											事件性质:
										</LABEL>
										<input id="thingStatus" type="radio" value="private"
											name="calendar.thingStatus" checked="checked"
											style="float: left; width: 20px;" />
										<span style="float: left; width: 40px;">私人</span>
										<input type="radio" value="public" name="calendar.thingStatus"
											style="float: left; width: 40px;" />
										<span style="float: left; width: 40px;">公开</span>
									</DIV>
									<DIV class="rowElem" align="left">
										<LABEL for="isRepeat">
											是否重复:
										</LABEL>
										<input id="isRepeat" name="calendar.isRepeat" type="checkbox"
											value="yes" style="width: 20px" />
									</DIV>
									<DIV id="thingTypeDiv" class="rowElem" align="left"
										style="display: none;">
										<LABEL for="repeatCycle">
											重复周期:
										</LABEL>
										<select id="thingType" style="width: 60px;"
											name="calendar.repeatCycle">
											<option value="day">
												每天
											</option>
											<option value="week">
												每周
											</option>
											<option value="month">
												每月
											</option>
											<option value="year">
												每年
											</option>
										</select>
									</DIV>
									<DIV id="repeatFrequencyDiv" class="rowElem" align="left"
										style="display: none;">
										<LABEL for="repeatFrequency">
											重复频率:
										</LABEL>
										<select id="repeatFrequency" name="calendar.repeatFrequency"
											style="width: 50px;">
											<option value="1">
												1
											</option>
										</select>
									</DIV>
								<DIV>
									<label for="">
										附件
									</label>
									<input type="file" name="fileUpload" multiple="multiple"
										   id="files" />
									</FORM>
								</DIV>

							</DIV>
							<div id='calendarDetail'>
								<div>
									<div id="messageimg">
									</div>
									<div>
										<div>
											<span id="messageSendUserName"></span>
											<br />
											<br />
										</div>
										<div>
											<span id="messageTitle"></span> <span id="tishi" style="font-size:12px"></span>      
										</div>
										<div>
											<span id='messageContent'></span>
										</div>
										
										<div id='messageAddTime'></div>
										<div id='messageTimeLong'></div>
										<div id='startTime'></div>
										<div id='endTime'></div>
										<div id='AttachmentDIV'></div>
										<div id='messageSubmit'></div>
										<div id='messageDelete'></div>

									</div>

								</div>
							</div>
						</div>

					</div>
					<div id="showMf" style="display: none;">
						<div class="headercon">
							<ul>
								<li>
									<a href="javascript:;"><strong></strong> </a>
								</li>
							</ul>
							<dl>
								<dt>
									<a href="#" id="showAllPm" target="showQuan">全屏</a>
								</dt>
								<dd>
									<a href="javascript:;"
										onclick="document.getElementById('workMainIframe').contentWindow.history.back();">返回</a>
								</dd>
								<dd>
									<a href="#" id="shuaxin" target="workMain">刷新</a>
								</dd>
							</dl>
						</div>
						<div style="padding-top: 15px;">
							<!--主体 iframe -->
							<iframe name="workMain" target="workMain" id="workMainIframe"
								data="<%=basePath%>" src="" marginwidth="0" marginheight="0"
								hspace="0" vspace="0" frameborder="0" scrolling="auto"
								style="width: 100%; height: 1000px; margin: 0px; padding: 0px;"></iframe>
						</div>
					</div>
				</div>
				<div id="fix_sidebar">
					<div id="back_top">
						<a href="#"></a>
					</div>
				</div>
			</div>
		</div>
		<script type="text/javascript"
			src="js/jquery.mCustomScrollbar.concat.min.js">
</script>
		<script type="text/javascript" src="js/jquery.scrollTo.min.js">
</script>
		<script type="text/javascript">
// 回到顶部
$("#back_top").css("display", "none");
$(window).scroll(function() {
	var s_top = $(document).scrollTop() - $(document).height();
	if ($(document).scrollTop() > 0) {
		$("#back_top").css("display", "block");
	} else {
		$("#back_top").css("display", "none");
	}
});

$("#back_top").on('click', 'a', function() {
	$.scrollTo(0, 800);
});

$(function() {
	//公告
	$.ajax( {
		url : "NoticeAction!show.action",
		type : 'post',
		dataType : 'json',
		cache : false,//防止数据缓存
		success : function(useradsfa) {
			$("#show").empty();//清空
		var message = "";
		$(useradsfa).each(function(i, n) {
			message += (i + 1) + "、" + n.content + "&nbsp;&nbsp;&nbsp;&nbsp;";
		});
		$("#show").html(message);
	}
	});

	//系统消息提醒
	$.ajax( {
		type : "POST",
		url : "AlertMessagesAction!findAMCountByUid.action",
		dateType : "json",
		success : function(msg) {
			if (msg > 0) {
				$("#pmImg").show();
				$(".messageCount").text("[" + msg + "]");
				$(".messageCount").css( {
					color : "red"
				});
				$(".messageCount").bind("mouseover", function() {
					$("#messageCount").css("background-color", "#696969");
				})
				$(".messageCount").bind("mouseout", function() {
					$("#messageCount").css("background-color", "#ffffff");
				})
			}
		}
	});

	//获取邮箱已读未读
	$.ajax( {
		type : "POST",
		url : "JavaMailAction!getMailInfo.action",
		dataType : "json",
		success : function(data) {
			if (data != null) {
				$("#allMail").text(data[0]);
				$("#unReadMail").text(data[1]);
				$("#allMail").parent().attr("href", data[2]);
				$("#unReadMail").parent().attr("href", data[2]);
			} else {
<%--				$("#allMail").parent().parent().remove();--%>
<%--				$("#unReadMail").parent().parent().remove();--%>
			}
		},
		error : function() {
<%--			$("#allMail").parent().parent().remove();--%>
<%--			$("#unReadMail").parent().parent().remove();--%>
		}
	});
	

});

function showWork(moduleid) {
	$("#person").hide();
	$("#showMf").show();

	if (moduleid != null && moduleid > 0) {
		$("#workMainIframe").attr(
				"src",
				"ModuleFunctionAction!findMfByIdForJump.action?pageStatus=3&id="
						+ moduleid);
		$.ajax( {
			url : 'ModuleFunctionAction!findMfByIdForJson.action',
			type : 'post',
			dataType : 'json',
			data : {
				id : moduleid,
				pageStatus : '3'
			},
			cache : true,
			success : function(mf) {
				$(".cenjinav p").html(mf[0]);
				$(".headercon strong").html(mf[1].functionName);
				$("#showAllPm").attr(
						"href",
						"ModuleFunctionAction!findMfByIdForJump.action?pageStatus=3&id="
								+ moduleid);
				$("#shuaxin").attr(
						"href",
						"ModuleFunctionAction!findMfByIdForJump.action?pageStatus=3&id="
								+ moduleid);
			},
			error : function() {
				location.href = 'ModuleFunctionAction!findMfByUser.action';
			}
		});
	}
}

var ModuleName =new Bloodhound({
  datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
  queryTokenizer: Bloodhound.tokenizers.whitespace,
  prefetch: '',
  remote: {
	url: 'ModuleFunctionAction!searchModuleFunction2.action',
    prepare: function (query, settings) {
                     settings.url += '?moduleFunction.functionName=' + encodeURI(encodeURI(query));
<%--                     alert(encodeURI(encodeURI(query)));--%>
                      return settings;
                   }
  }
});

$('#remote .typeahead').typeahead(null, {
  name: 'names',
  display: 'modulename',
  source: ModuleName,
  limit : '10',
});

$('#remote .typeahead').bind('typeahead:selected', function(obj, datum, name) { 
	showWork();
	var eValue=eval('datum.'+"moduleid");
	window.location.href='ModuleFunctionAction!findMfByIdForJump.action?id='+eValue;
});

function addmore(){
	var s="<div class='more tt-suggestion tt-selectable '>更多</div>";
	$('.tt-menu').append(s);
}
addmore();
$('.more').on("click",function(){
	 showWork();
	 $(".searchFun").submit(); 
	
});

<%--=================工作台=================--%>





//设置消息为已读
function updatemsg(id){
	if(id>0){
		$.ajax( {
		type : "POST",
		url : "AlertMessagesAction!updateMessagesStatus1.action",
		data : {
			id :id,
		},
		dataType : "json",
		success : function(data) {
			
		}
	})}
}

var ids=new Array()
var repeat=false
//发送指派消息已读
function updateMsgAssign(id) {

    jQuery.each(function(value,i){
        if(value==id){
			repeat=true;
            ids.shift(i);
            return;
		}
		repeat=false;
    })
    if (!repeat) {
        $.ajax({
            type: "POST",
            url: "CalendarAction!updateMsgAssign.action",
            data: {
                id: id,
            },
            dataType: "json",
            success: function (data) {

            }
        })
    }
}

function addman(){
    if(($("#allusers").val()!="")){
        $("#allusers").val(($("#allusers").val()+','+$("#users  option:selected").text()));
        $("#allusersid").val($("#allusersid").val()+','+$("#users").val());
	}else {
        $("#allusers").val($("#users  option:selected").text());
        $("#allusersid").val($("#users").val());
    }
}

function delman(){

        $("#allusers").val("");
        $("#allusersid").val("");
}
	

var caldata={};
var calCircuitData={};
var calProjectData={};
$.ajax({
		url : 'AlertMessagesAction!findAlertMessages4calendar.action',
		dataType : 'json',
		data : {
		},
		async :false,
		cache : false,//防止数据缓存
		success : function(doc) {
			caldata=doc;
			}
		});
		
//加载项目管理中的信息
$.ajax({
	url : 'projectPoolAction_selfProjectManageyfByDateDiff.action',
	dataType : 'json',
	data : {
// 		"projectManageyf.zpTime": start.format(),
// 		"projectManageyf.reTime": end.format(),
		"pageStatus":"applychoose"
	},
	cache : false,//防止数据缓存
	success:function(jsonData){
		calProjectData=jsonData;
	},error:function(XMLHttpRequest, textStatus, errorThrown) {
		console.log("获取项目管理异常"+XMLHttpRequest.status+"  "+XMLHttpRequest.readyState+"  "+textStatus);
	}
});

$.ajax({
		url : 'CircuitRunAction!findCircuitRun4calendar.action',
		dataType : 'json',
		data : {
		},
		async :false,
		cache : false,//防止数据缓存
		success : function(doc) {
            calCircuitData=doc;
			}
		});



$(document).ready(function() {
    // caldata.push.apply(caldata,calCircuitData);
	var initialLocaleCode = 'zh_cn';
		$('#calendar').fullCalendar({
			header : {
					left : 'prevYear,nextYear, prev,next, today',
					center : 'title',
					right : 'month,agendaWeek,agendaDay,listMonth'
				},
			buttonText : {
							prevYear : '去年',
							nextYear : '明年',
							today : '今天',
							month : '月',
							listMonth :'列表',
							agendaWeek : '周',
							agendaDay : '日'
						},
<%--			defaultDate: '2017-11-12',--%>
            lang : initialLocaleCode,//2.9.1
			// buttonIcons: true, // show the prev/next text   2.9.1annotation;
			weekNumbers: true,
			navLinks: true, // can click day/week names to navigate views
			editable: true,
			eventLimit: true, // allow "more" link when too many events
			businessHours: true, // display business hours
			
			//内容高度
			contentHeight : 800,
			//每周开始的日期：0为周日
			firstDay : 0,
			//第几周显示的文字
			weekNumberTitle : '周',
			//数据
<%--			events: caldata,--%>
			events : function(start, end,timezone, callback) {
										$.ajax( {
													url : 'CalendarAction!findCalendar.action',
													dataType : 'json',
													data : {
														start: start.format(),
			                							end: end.format()
<%--														start : $.fullCalendar--%>
<%--																.formatDate(--%>
<%--																		start,--%>
<%--																		"yyyy-MM-dd HH:mm:ss"),--%>
<%--														end : $.fullCalendar--%>
<%--																.formatDate(--%>
<%--																		end,--%>
<%--																		"yyyy-MM-dd HH:mm:ss")--%>
													},
													cache : false,//防止数据缓存
													success : function(doc) {
														var events = [];
														var Allevents = [];
														$(doc).each(
																		function() {
																			events.push( {
																						fkId : $(
																								this)
																								.attr(
																										'id'),
																						id : $(
																								this)
																								.attr(
																										'id'),
																						title : $(this).attr('addUserName')+'  提醒\n'+($(this).attr('userName')==null?'自己':$(this).attr('userName'))+'\n'+$(
																								this)
																								.attr(
																									'title'
																									),
																						allDay : $(
																								this)
																								.attr(
																										'allDay'),
																						start : $(
																								this)
																								.attr(
																										'start'),
                                                                                        addTime : $(
                                                                                            this)
                                                                                            .attr(
                                                                                                       'start'),
																						end : $(
																								this)
																								.attr(
																										'endDate'),
																						url : $(
																								this)
																								.attr(
																										'url'),
																						className : $(
																								this)
																								.attr(
																										'className'),
																						editable : $(
																								this)
																								.attr(
																										'editable'),
																						source : $(
																								this)
																								.attr(
																										'source'),
																						color : $(
																								this)
																								.attr(
																										'color'),
																						backgroundColor : $(
																								this)
																								.attr(
																										'backgroundColor'),
																						borderColor : $(
																								this)
																								.attr(
																										'borderColor'),
																						textColor : $(
																								this)
																								.attr(
																										'textColor'),
																						thingType : $(
																								this)
																								.attr(
																										'thingType'),
																						thingStatus : $(
																								this)
																								.attr(
																										'thingStatus'),
																						userId : $(
																								this)
																								.attr(
																										'userId'),
																						userName : $(
																								this)
																								.attr(
																										'userName'),
																						code : $(
																								this)
																								.attr(
																										'code'),
																						dept : $(
																								this)
																								.attr(
																										'dept'),
																						addUserId : $(
																								this)
																								.attr(
																										'addUserId'),
																						addUserName : $(
																								this)
																								.attr(
																										'addUserName'),
																						addUserCode : $(
																								this)
																								.attr(
																										'addUserCode'),
																						addUserDept : $(
																								this)
																								.attr(
																										'addUserDept'),
																						addDateTime : $(
																								this)
																								.attr(
																										'addDateTime'),
																						//thingContent : $(
																						content : $(
																								this)
																								.attr(
																										'thingContent'),
																						sumDay : $(
																								this)
																								.attr(
																										'sumDay'),
																						isRepeat : $(
																								this)
																								.attr(
																										'isRepeat'),
																						repeatCycle : $(
																								this)
																								.attr(
																										'repeatCycle'),
																						repeatFrequency : $(
																								this)
																								.attr(
																										'repeatFrequency'),
                                                                                		calendarState : $(
																							this)
																							.attr(
																								'calendarState'),
																						epId : $(
																									this)
																									.attr(
																										'epId'),
																						hread : $(
																							this)
																							.attr(
																								'hread'),
                                                                             			   attachmentPath : $(
																									this)
																									.attr(
																										'attachmentPath')
																									});
																		});
														Allevents=caldata.concat(events)
														if(calProjectData!=null && calProjectData.length>0){
															Allevents=calProjectData.concat(Allevents);
														}
														callback(Allevents);
														
													},
													error : function() {
														alert('there was an error while fetching events!');
													},
													color : 'red', // a non-ajax option
													textColor : 'black' // a non-ajax option
												});
									},
									
									
			//
			eventClick: function(calEvent, jsEvent, view) {

                    //提醒消息 点击已阅
                    if(calEvent.color=='#257e4a'){
                        updatemsg(calEvent.id);
                    }
					//处理
					if(calEvent.color=="#61E1E3"){//项目管理在日历中显示
						$("#messageSendUserName").text("");
						$("#messageTitle").text("项目管理");
						$("#deleteUrl").remove();
						$("#disposeUrl").remove();
	                	$("#submitUrl").remove();
						$("#messagePersonimg").remove();
	               		 $("#attachUrl").remove();
						var dispose ="<a id='disposeUrl' href='"+calEvent.functionUrl+"'>  前往处理         </a>";
						$("#messageContent").load(calEvent.functionUrl);
					}else{
						if(calEvent.functionId!=0){
							var dispose="<a id='disposeUrl' href='AlertMessagesAction!findAlertMessagesForUrl.action?id="+calEvent.id+"'>  前往处理         </a>"
						}else if(calEvent.functionUrl.indexOf("CircuitRunAction_findAduitPage.action")>=0){
							
							var dispose ="<a id='disposeUrl' href='"+calEvent.functionUrl+"&msgId="+calEvent.id+"'>  前往处理         </a>"
						}else{
							
							var dispose ="<a id='disposeUrl' href='"+calEvent.functionUrl+"' onclick='updatemsg("+calEvent.id+")'>  前往处理         </a>"
						}
						
						//删除
						if(calEvent.fkId!=null){
							var deletediv="<a id='deleteUrl' href='CalendarAction!delCalendar.action?id="+ calEvent.id+"&isRepeat=delOne'>删除</a>";
							dispose="";
						}else{
							var deletediv="<a id='deleteUrl' href='AlertMessagesAction!delAlertMessages.action?id="+ calEvent.id+"&calendar=true'>删除</a>";
						}


	                	var submitdiv;
						//提交
						if(calEvent.calendarState=="未完成"||calEvent.calendarState=="打回"){
						    if(calEvent.userId==${Users.id}){
	                            submitdiv="<a id='submitUrl' href='CalendarAction!submitCalendarState.action?id="+ calEvent.id+"'>提交</a>";
	                        }
						}
	                	// CircuitRunAction_findAduitPage.action?id=24093
						if(calEvent.epId!=null){
	                        submitdiv="<a id='submitUrl' href='CircuitRunAction_findAduitPage.action?id="+ calEvent.epId+"'>审批中</a>";
						}
	                	if(calEvent.calendarState=="完成"){
	                    	submitdiv="<a id='submitUrl' '>已完成</a>";
	                	}
	                	var attachment;
	                	//附件
						if(calEvent.attachmentPath!=""){
	                        attachment="<a id ='attachUrl' target='showPri' href='<%=basePath%>FileViewAction.action?FilePath=/upload/file/Calendar/"+calEvent.attachmentPath+"'>查看附件 </a>";
						}else {
	                        attachment="";
	                    }
	                	
						$("#deleteUrl").remove();
						$("#disposeUrl").remove();
	                	$("#submitUrl").remove();
						$("#messagePersonimg").remove();
	               		 $("#attachUrl").remove();

						
						
						var personimg =calEvent.sendUserImg;
						if(personimg!=null && personimg.length>0){
							var imgdiv="<img alt='提醒' src='"+calEvent.sendUserImg+"' height='100px;' width='100px;' align='middle'  id='messagePersonimg' "+"onerror="+"this.src='images/man.jpg'"+""+" >";
						}
						else{
							var imgdiv="<img alt='12' src='images/man.jpg' height='100px;' width='100px;' align='middle'  id='messagePersonimg' >";
						}

						// $("#messageimg").append(imgdiv);messageTimeLong
						$("#messageDelete").append(deletediv);
						$("#messageDelete").append(dispose);
						$("#messageSubmit").append(submitdiv);
	            		$("#AttachmentDIV").append(attachment);
						$("#messageContent").text('\n内容:'+calEvent.content);	
						//$("#messageTitle").text('\n标题:'+calEvent.title);
						var strs= new Array(); //定义一数组 
						strs=calEvent.title.split("\n"); //字符分割 
						var title1 ='\n标题:'+strs[2];
						var title2='('+strs[0]+' '+strs[1]+')'; //分割后的字符输出 
						$("#messageTitle").text(title1);
						$("#tishi").text(title2);
						$("#startTime").text('\n计划时间:'+calEvent.start.format("YYYY-MM-DD")+'至'+calEvent._end.format("YYYY-MM-DD"));
						$("#messageAddTime").text('\n添加时间:'+calEvent.addDateTime.split(" ")[0]);
						if(calEvent.sendUserName!=null && calEvent.addTime!=null){
							$("#messageSendUserName").text(calEvent.sendUserName+'   '+calEvent.addTime+'   提醒您');	
						}
						// 如果是Calendar 是指派 且时长为0
						if(calEvent.userId!=null && calEvent.hread!="true" && calEvent.userId==${Users.id}){
	                        updateMsgAssign(calEvent.id);
	                        ids.push(calEvent.id);
						}
					}
					
					




					

    		},
    		
    		
    		 dayClick: function(date, jsEvent, view) {
<%--    				 alert('Clicked on: ' + date.format());--%>
				 					//判断显示明细的对话框是否在打开
						if ($("#reserveinfo").dialog("isOpen")) {
							$("#reserveinfo").dialog("destroy");//销毁明细对话框
						}
						//判断添加的对话框是否在打开
						if ($("#reservebox").dialog("isOpen")) {
							var titleVal = $("#title").val();
							if (titleVal != "") {
								if (window
										.confirm("您的活动尚未保存,确定要舍弃吗?") == false) {
									return false;
								}
							}
							//重置表单
							$("#reserveformID")[0].reset();
							$("#thingTypeDiv").hide();
							$("#repeatFrequencyDiv").hide();
							$("#reservebox").dialog("destroy");//销毁添加的对话框
						};
						//选中的时间
						var selectdate = date.format();
						var x = event.clientX;//x坐标
						var y = event.clientY;//y坐标
						$("#start").val(selectdate+' 00:00:01');//为开始时间赋值
						$("#end").val(selectdate+' 23:59:59');//为开始时间赋值

						//jquery弹出对话框插件(dialog)
						$("#reservebox").dialog(
										{
											title : selectdate,
											modal : false,//模态对话框，若为是，则不可操作背景层。
											autoOpen : false,//这个属性为true的时候dialog被调用的时候自动打开dialog窗口。当属性为false的时候，一开始隐藏窗口，知道.dialog("open")的时候才弹出dialog窗口。默认为：true。
											closeOnEscape : true, //为true的时候，点击键盘ESC键关闭dialog，默认为true;(ie不支持)
											hide : 'slide',//关闭效果
											show : 'show',//打开效果
											draggable : true,//是否可拖动
											resizable : true,//是否可改变大小
											//height : 270,//高度
											//minHeight : 270,//最小高度
											width : 355,//宽度
											minWidth : 355,//最小宽度
											position : [
													x - 200,
													y - 500 ],//使窗口跟随鼠标移动

											//关闭之前的方法
											beforeClose : function(event, ui) {
												var titleVal = $("#title").val();
												if (titleVal != "") {
													if (window.confirm("您的活动尚未保存,确定要舍弃吗?") == false) {
														return false;
													}
												};
												//重置表单
												$("#reserveformID")[0].reset();
												//清空验证提示信息
												$.validationEngine.closePrompt("#title");
												$.validationEngine.closePrompt("#start");
												$.validationEngine.closePrompt("#end");
												//隐藏date2
												$("#start2").hide();
												$("#end2").hide();
											},
											buttons : {
												"关闭" : function() {
													$("#reservebox").dialog("close");
												},
												"添加" : function() {
													if ($("#reserveformID").validationEngine({
															returnIsValid : true
														})) {
														$("#reserveformID").submit();
														}
												}
											}
										});

						//是否重复处理
						$("#isRepeat").bind("click",function() {
									if ($("#isRepeat").attr('checked')) {
										$("#repeatFrequency").empty();//清空下拉框
										//添加option(生成30个重复周期)
										for ( var i = 1; i <= 30; i++) {
											$(
													"<option value='"
															+ i
															+ "'>"
															+ i
															+ "</option>")
													.appendTo(
															"#repeatFrequency");
										}
										$("#thingTypeDiv").show('slow');
										$("#repeatFrequencyDiv").show('slow');
									} else {
										$("#thingTypeDiv").hide('slow');
										$("#repeatFrequencyDiv").hide('slow');
									}
								});
						
						//查询所有的部门
						$.ajax( {
								url : 'DeptNumberAction!findAllDept.action',
								dataType : 'json',
								cache : false,//防止数据缓存
								success : function(allDdept) {
									$("#dept").empty();
									$("<option></option>").appendTo("#dept");
									$(allDdept).each(
										function() {$("<option value='"
															+ this.dept
															+ "'>"
															+ this.dept
															+ "</option>")
													.appendTo(
															"#dept");
										});
								}

							});
						//级联查询出部门所对应的所有人员
						$("#dept").bind("change",
							function() {
								if ($("#dept").val() != "") {
									$.ajax( {
											url : "UsersAction!findUsersByDept.action",
											type : 'post',
											dataType : 'json',
											contentType : "application/x-www-form-urlencoded; charset=utf-8",
											data : {
												deptName : $("#dept").val()
											},
											success : function(useradsfa) {
												$("#users").empty();//清空
												$("<option></option>").appendTo("#users");
												$(useradsfa).each(
													function() {
														$(
																"<option value='"
																		+ this.id
																		+ "'>"
																		+ this.name
																		+ "</option>")
																.appendTo("#users")
													});
											},
											error : function() {
												alert("服务器异常!");
											}
										});
								}
							});
						
						//打开添加对话框
						$("#reservebox").dialog("open");
		    },
    		
    		eventAfterRender:function( event, element, view ) {
		    	
		    	
<%--		    	if(event.color=="#257e4a"){--%>
<%--		    		$("<span>提醒消息</span>").prependTo(element);		--%>
<%--		    	}--%>
<%--		    	else if(event.color=="#CC6699"){--%>
<%--		    		$("<span>审核消息</span>").prependTo(element);--%>
<%--		    	}else{--%>
<%--		    		$("<span>个人日程</span>").prependTo(element);--%>
<%--		    	}--%>
		    	
		    	 //$('#calendar').fullCalendar('render');
		    	
    		}
    		
		});
		//$('#calendar').fullCalendar( 'rerenderEvents' );									
			
		if(caldata.length>0){
			$(".fc-icon-left-single-arrow").css("color","red");
		}


		
		
	});

<%--$(function(){--%>
<%--	if("${Users.name}"=="左龙"){--%>
<%--		alert("你个菜鸡，还玩劫!哈沙尅~");--%>
<%--		window.location.href = "/test/qixi/index.html";--%>
<%--	}--%>
<%--})--%>

	
	
<%--=================工作台 over=================--%>
</script>
	</body>
</html>

