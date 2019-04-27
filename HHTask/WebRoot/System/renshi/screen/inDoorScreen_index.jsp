<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
	<head>
 		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/javascript/calendar/css/redmond/jquery-ui-1.8.1.custom.css">
		<link rel="stylesheet" type="text/css"
			href="${pageContext.request.contextPath}/javascript/bootstrap-3.3.7-dist/css/bootstrap.min.css">
		<script src="${pageContext.request.contextPath}/javascript/jquery/jquery-3.2.1.js"> </script> 
		<script type="text/javascript" src="<%=basePath%>/javascript/echarts/echarts.min.js"></script>
 		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/fullcalendar-3.7.0/lib/jquery-ui.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/bootstrap-3.3.7-dist/js/bootstrap.js"> </script> 
 		<script type="text/javascript" src="<%=basePath%>/javascript/jquery.qrcode.min.js"></script>
		<style type="text/css">
			*{
				font-family: 楷体;
			}
			html,body,.container{
				background-color: #97BCE4;
/* 				height: 97%; */
			}
			#showCalendar{
				background-color: #97BCE4;
			}
 			.container{ 
 				width: 97%; 
 			}
 			#p{
				font-weight: 900;
				font-size: 1em;
				font-family: Arial Black;
				color: #27408B;
			}
			#p1{
				font-size: 0.8em;
				font-family: 黑体;
				color: #27408B;
				font-style: italic;
				font-weight: 900;
			}
 			#p2{
				font-weight: 900;
				font-size: 0.8em;
				font-family: 黑体;
				color: #27408B;
			}
 			#p3{
				font-size: 1em;
				font-family: Arial Black;
				color: #27408B;
				font-weight: 900;
			}
 			#p4{
				font-weight: 900;
				font-size: 0.7em;
				font-family: Arial Black;
				font-weight: bolder;
				color: #27408B;
			}
			#p5{
				font-weight: 900;
				font-size: 0.9em;
				font-family: Arial Black;
				font-weight: bolder;
				color: #27408B;
			}
			h1{
				margin-top:15px;
				font-size: 4em;
				font-weight: 900;
			}
			h3{
				font-weight: bold;
				margin-top: 10px;
			}
			
 			.table,tbody{
				margin-bottom: none;;
				padding-bottom: none; 				
 			} 
			.leaveClass{
				width: 15px;
				height: 15px;
				display: inline-block;
				background-color: red;
				border: solid 1px #000000;
 				border-radius: 50%;
			}
			.guardClass{	
				width: 15px;
				height: 15px;
				display: inline-block;
				border: solid 1px #000000;
 				background-color: green; 
 				border-radius: 50%; 
			}
			.dutyClass{
				width: 15px;
				height: 15px;
				display: inline-block;
				border: solid 1px #000000;
				background-color: blue; 
 				border-radius: 50%; 
			}
			.timeOutClassShow{
				width: 15px;
				height: 15px;
				display: inline-block;
				border: solid 1px #000000;
				background-color: yellow; 
 				border-radius: 50%; 
 				font-size: 20px;
			}
			/*超时数组*/
			.timeOutClass{
				width: 18px;
				height: 18px;
				display: inline-block;
				border: solid 1px #000000;
				background-color: yellow; 
 				border-radius: 50%; 
 				float: right;
			}
			#visitorUl{
				font-size: 20px;
				margin: 10 auto;
				height: 180px;
				width:100%;
				overflow: hidden;
/* 				padding-left: 75px; */
				padding: 0;
			}
			#visitorUl li{
   				list-style:decimal inside;
   				padding-bottom: 5px;
			}
			#resUl{
				list-style: none;
				margin: 0 auto;
				width:100%;
				height: 180px;
				overflow: hidden;
				padding: 0 auto ;
			}
			.resItem{
				margin: 5px 5px;
				display: inline-block;
			}
			img{
				width:50px;
				height:50px;
				border-radius: 50%;
  				z-index:1000;
				opacity: 1;
			}
			.imgTitle{
				width:inherit;
				height:60px;;
				border-radius:inherit;
  				z-index:1000;
				opacity: 1;
				text-align: center;
				border: none;
			}
			.userImg{
				width:62px;
				height:62px;
				border-radius: 50%;
				margin: 0 auto;
				
			}
			#myChart{
				width: 60px;
				height:60px;
				margin: 0 auto;
			}
			.userStatus{
				height: 10px;
				margin-bottom: 10px;
			}
			.visitorImg{
				margin-right: 20px;
			}
			.resQRCode,.leaveRQCode,.codeBookRQCode,.dutyRQCode{
/* 				height: 60px; */
				width: 100%;
				text-align: center;
			}
			.visitorIcon{
				width: 25px;
				height: 25px;
				display: inline-block;
				border: solid 1px #000000;
 				border-radius: 50%;
 				background-color: #529DD5;
 				margin-right: 10px;
			}
			#showMessage{
				height: 30px;
				line-height: 30px;
				font-size: 20px;
			}
			/*菜谱*/
			#codebookUl{
				list-style-type: none;
				font-size: 15px;
				margin: 0 auto;
				height: 420px;
				overflow: hidden;
			}
			#codebookUl li{
   				list-style:none;
   				padding-bottom: 5px;
			}
			#codebookUl img{
				width:50px;
				height:50px;
				
			}
			
			#title{
				height: 20%;
			}
			.oneTr{
				height: 40%;
			}
			.twoTr{
				height: 40%;
			}
			.userList{
				height:65%; 
			}
			#QRCodeParent{
				padding: 0;
				margin: 0;
				border: none;
			}
			#QRCode,#QRCode tr{
				border-top:none;
				border-bottom:none;
				border:none;
				padding: 0;
				margin: 0;
				background-color: #97BCE4;
				border-collapse: collapse;
				table-layout: fixed;
			}
			#QRCode td:first-child{
				border-left: none;
			}
			#QRCode td:last-child{
				border-right: none;
			}
			#QRCode td{
				border-top:none;
				border-bottom:none;
			}
			.carousel-indicators{
				position: absolute;
				top: -57px;
				left: 40px;
				z-index: -10;
			}
			.table,.table tr td{
   				border: 3px solid #5472B4;
/*  				border-radius:25px; */
				cellpadding:2px;
				
/* 			  	-webkit-border-radius: 5px; */
/* 				-moz-border-radius: 5px; */
/* 				-webkit-box-shadow: #20479E 5px 5px 5px; */
/* 				-moz-box-shadow: #20479E 5px 5px 5px; */
/* 				box-shadow: #20479E 5px 5px 5px; */
			}
			
			#showTime{
				font-size: 17px;
				padding: 0;
				margin: auto 0 0 auto;
				position: absolute;
				top:90px;
 				right: 16px; 
/* 				left:0; */
				width: 400px;
				height: 30px;
			}
			.showBacklog{
				float: right;
				width: 20px;
				height: 20px;
				margin-right:20px;
				border: solid 1px #000000;
 				border-radius: 50%;
 				background-color: #529DD5;
 				color: white;
			}
			.workTimeSchedule{
 				position: absolute; 
 				left: 118px;
 				top:55px; 
				width: 50px;
			}
			.selfTarget{
				overflow:hidden;
  				position: absolute; 
  				left: 122px; 
  				top:22px; 
				width: 45px;
				cursor:pointer;
				background-color:#999999;
				color: #F5D500;
				background-color:rgba(0,0,0,0.2);
			}
			.selfTargetImg{
				width: 15px;
				height: 15px;
			}
			.sign1{
				float: left;
				width: 4px;
				height: 4px;
				margin-top:12px;
				margin-right:2px;
				background-color: gray;
			}
			.sign2{
				float: left;
				width: 4px;
				height: 8px;
				margin-top:8px;
				margin-right:2px;
				background-color: gray;
			}
			.sign3{
				float: left;
				width: 4px;
				height: 12px;
				margin-top:4px;
				margin-right:2px;
				background-color: gray;
			}
			.sign4{
				float: left;
				width: 4px;
				height: 16px;
				margin-right:3px;
				background-color: gray;
			}
			.signColor1{
				background-color: #d9534f;
			}
			.signColor2{
				background-color: #f0ad4e;
			}
			.signColor3{
				background-color: #66CCCC;
			}
			.signColor4{
 				background-color: #5cb85c; 
			}
/* 			.sign1.sign2{ */
/* 			} */
/* 			.sign1.sign2.sign3{ */
/* 			} */
/*  			.sign1.sign2.sign3.sign4{  */
/*  			}  */
/* 			.sign1 .sign2 .sign3 .sign4{ */
/* 				background-color: green; */
/* 			} */
			.workTimeHourAndOut{
				height: 15px;
				float: left;
				width: 20px;
				margin: 0;
				padding: 0;
			}
			.workTimeOut{
				height: 8px;
				font-weight:8px;
				width:5px;
				font-size: 10px;
				margin: 0;
				padding: 0;
			}
			.workTimeHour{
				float:left;
				height: 8px;
				width:6px;
				font-size: 7px;
			}
			
			/*显示公告*/
			.notice .header_bt .marquee {
				float: left;
			    width: 95%;
			    overflow: hidden;
			    padding-right: 3%;
			    display: inline;
			    color: red;
			    height: 25px;
			    line-height: 25px;
			}
			.notice .noticeleft {
			    float: left;
 			    width: 5%; 
			    text-align: right;
			}
			.notice .noticeleft img{
				width: 25px;
				height: 25px;	
			}
			.showBacklogLeft{
				width: 1px;
				height: 15px;
				float: left;
				margin-left:30px;
				
			}
			.showBacklog{
				float: right;
				width: 20px;
				height: 20px;
				margin-right:20px;
				border: solid 1px #000000;
 				border-radius: 50%;
 				background-color: #529DD5;
 				color: white;
			}
			.showBacklogNull{
				float: right;
				width: 20px;
				height: 20px;
				margin-right:20px;
 				border-radius: 50%;
			}
			.cookbookTd{
				height: 80px;
			}
			.itemBar{
				height: 10px;
				margin-bottom:10px;
				float: left;
				margin: 10 auto ;
				
			}
			.meetingLi{
				border:  1px solid white; 
				margin-bottom: 10px;
				list-style-type: none;
			}
		</style>
	</head>
	<body>
		<div class="container">
			<div class="row" id="title">
				<div class="col-xs-3" style="position:relative;">
					<iframe style="position:absolute;top:60px;" width="300" scrolling="no" height="60" frameborder="0" allowtransparency="true" 
							src="http://i.tianqi.com/index.php?c=code&id=12&icon=1&num=7&dy=2"></iframe>
				</div>
				<div class="col-xs-6" style="height: 20%;">
<%-- 					<h1 class="text-center"><span id="p1">易</span><span id="p4">-</span><span id="p3">Work</span>&nbsp;<span id="p5">&</span>&nbsp;<span id="p1">易</span><span id="p4">-</span><span id="p3">Life</span></h1> --%>
					<h1 class="text-center"  style="height: 20%;"><img src="${pageContext.request.contextPath }/img/e-work-fin.png" class="imgTitle"></h1>
					<div class="notice">
						<div class="header_bt">
							<div class="noticeleft">
								<strong><img src="img/notice_07.png" > </strong>
							</div>
							<div class="marquee">
								<marquee direction="left" scrollamount="2"
									onmouseout="this.start()" onmouseover="this.stop()">
									<font id="showAnnouncement"></font>
								</marquee>
							</div>
						</div>
					</div>
				</div>
				<div class="col-xs-3" style="position:relative;">
					<p class="text-right" id="showTime"></p>
				</div>
			</div>
			<table class="table">
				<tr class="oneTr">
					<td class="col-xs-3">
						<h3 class="text-center">来访人员</>
						<ul class="clearfix text-center" id="visitorUl">
							<s:iterator value="visitorList" id="vl" status="ps">
								<li>
									<img class="visitorImg" src="<%=basePath%>/upload/file/menjin/visitor/${vl.visitorIdentityPic}">${vl.visitorName}
								</li>
							</s:iterator>
						</ul>
					</td>
					<td rowspan="2" colspan="2" class="col-xs-6" id="userList">
						<div class="row " id="showMessage">
							<div class="col-xs-4"></div>
							<div class="col-xs-2">
								<img src="${pageContext.request.contextPath }/img/target.png" class="selfTargetImg"/> PKR
							</div>
							<div class="col-xs-2">
								<div class="guardClass"></div> 在岗
							</div>
							<div class="col-xs-2">
								<div class="leaveClass"></div> 离岗
							</div>
							<div class="col-xs-2">
								<div class="dutyClass"></div> 值日
							</div>
						</div>
						<p class="text-right" style="font-size: 15px; padding-right: 10px;">按考勤时长从左至右依次排序</p>
						<div class="row">
							<div id="myCarousel" class="carousel slide" data-ride="carousel" data-interval="5000" style="position: relative;">
								<!-- 轮播（Carousel）指标 -->
								<ol class="carousel-indicators">
									<s:iterator value="usersList" status="ps">
										<s:if test="#ps.index%16==0">
											<s:if test="#ps.index==0">
												<li data-target="#myCarousel" data-slide-to="${ps.index/16}" class="active"></li>
											</s:if>
											<s:else>
												<li data-target="#myCarousel" data-slide-to="${ps.index/16}"></li>
											</s:else>
										</s:if>
									</s:iterator>
								</ol>
								<!-- 轮播（Carousel）项目 -->
								<div class="carousel-inner">
									<s:iterator value="usersList" id="ul" status="ps">
										<s:if test="#ps.index%16==0">
											<!-- 这里展示一个轮播的视图 -->
											<s:if test="#ps.index==0">
												<div class="item active">
											</s:if>
											<s:else>
												<div class="item">
											</s:else>
										</s:if>
										
										<div class="col-xs-3 text-center">
											<div style="clear: inherit;" >
												<!-- 头像 -->
													<s:if test="#ul.sex==null || #ul.sex=='男'.toString()">
														<img src="<%=basePath%>/upload/user/${ul.picture}" alt="${ul.name }" class="userImg"
																onerror="this.src='images/man.jpg'" style="z-index: 2">
													</s:if>
													<s:else>
														<img src="<%=basePath%>/upload/user/${ul.picture}" alt="${ul.name }" class="userImg"
																onerror="this.src='images/woman.jpg'" style="z-index: 2">
													</s:else>
													<div class="selfTarget" 
														onclick="javascript:searchCalendar('${ul.id}','${ul.name}','${ul.code}');">
														<img src="${pageContext.request.contextPath }/img/target.png" style="width: 15px;height: 15px;">${ul.totalNum - ul.successNum + ul.timeOutNum}<s:if test="#ul.timeOutNum>0"><span style="font-size:10px;width:5px;height:5px;margin-left: 1px;color:red">-${ul.timeOutNum}</span></s:if>
																											</div>
													<div class="workTimeSchedule" style="cursor: pointer;" 
														onclick="javascript:searchLineChart('${ul.code}');"><!-- 工作时间进度的显示 -->
														<s:if test="#ul.successPer>=100">
															<div class="signColor4 sign1"></div>
															<div class="signColor4 sign2 "></div>
															<div class="signColor4 sign3"></div>
															<div class="signColor4 sign4"></div>
														</s:if>
														<s:elseif test="#ul.successPer!=null && #ul.successPer>=75">
															<div class="signColor2 sign1"></div>
															<div class="signColor2 sign2 "></div>
															<div class="signColor2 sign3"></div>
															<div class="sign4"></div>
														</s:elseif>
														<s:elseif test="#ul.successPer>=50">
															<div class="signColor3 sign1"></div>
															<div class="signColor3 sign2 "></div>
															<div class="sign3"></div>
															<div class="sign4"></div>
														</s:elseif>
														<s:elseif test="#ul.successPer>=25">
															<div class="sign1 signColor3"></div>
															<div class="sign2"></div>
															<div class="sign3"></div>
															<div class="sign4"></div>
														</s:elseif>
														<s:else>
															<div class="sign1"></div>
															<div class="sign2"></div>
															<div class="sign3"></div>
															<div class="sign4"></div>
														</s:else>
														<div class="workTimeHourAndOut">
															<div class="workTimeOut"><s:if test="#ul.successPer>100">+</s:if></div>
															<div class="workTimeHour">${ul.successHours}h</div>
														</div>
													</div>										<!-- 工作时长 -->
													
													<div>${ul.code }</div>
													<div class="userStatus">
														<!-- 上班状态 -->
														<s:if test="#ul.userStatus=='在岗'">
															<div class="guardClass"></div>
														</s:if>
														<s:elseif test="#ul.userStatus=='离开'">
															<div class="leaveClass"></div>
														</s:elseif>
														
														<!-- 值日状态 -->
														<s:if test="#ul.dutyStatus==null || #ul.dutyStatus=='否'.toString()">
		<!-- 													<div class="guardClass"></div> -->
														</s:if>
														<s:elseif test=" #ul.dutyStatus=='是'.toString()">
															<div class="dutyClass"></div>
														</s:elseif>
													</div>
												</div>
										</div>
										
										<s:if test="#ps.index%16==15">
											</div>
											<!-- 视图结束 -->
										</s:if>
									</s:iterator>
								</div>
								
							</div>
						</div>
					</td>
					<td rowspan="2" class="col-xs-3" class="cookbookTd">
						<h3 class="text-center">菜谱</h3>
						<ul id="codebookUl">
							<li >${dateTime} <img src="<%=basePath%>/upload/file/cookbook/14.png">尖椒炒茄子</li>
							<li >${dateTime} <img src="<%=basePath%>/upload/file/cookbook/4.png">土豆炖牛肉</li>
							<li >${dateTime} <img src="<%=basePath%>/upload/file/cookbook/11.png">爆炒西兰花</li>
							<li >${dateTime} <img src="<%=basePath%>/upload/file/cookbook/6.jpg">土豆烧排骨</li>
							<li >${dateTime} <img src="<%=basePath%>/upload/file/cookbook/12.png">排骨焖鹌鹑蛋</li>
							<li >${dateTime} <img src="<%=basePath%>/upload/file/cookbook/1.png">土豆丝</li>
							<li >${dateTime} <img src="<%=basePath%>/upload/file/cookbook/13.png">番茄鸡蛋汤</li>
						</ul>
					</td>
				</tr>
				<tr class="twoTr">
					<td class="col-xs-3">
						<h3 class="text-center">快递待取</h3>
						<ul id="resUl"  class="clearfix ">
							<s:iterator value="resList" id="rl" status="ps">
								<s:if test="#ps.index%3==0">
									<li>
								</s:if>
									<div class="resItem text-center">
											<img src="<%=basePath%>/upload/user/${rl.picture}" alt="${rl.addName }"
													onerror="this.src='images/man.jpg'">
										<div>${rl.addCode}<br/>${rl.copenTime}h</div>
									</div>
								<s:if test="#ps.index%3==2">
									</li>
								</s:if>
							</s:iterator>
						</ul>
					</td>
				</tr>
				<tr>
					<td colspan="15" id="QRCodeParent"> 
					
						<table class="table" id="QRCode">
 							<tr> 
								<td>
									<h3 class="text-center">快递预存</h3>
									<div class="resQRCode">
										
									</div>
								</td>
								<td>
									<h3 class="text-center">请假</h3>
									<div class="leaveRQCode">
										
									</div>
								</td>
								<td>	
									<table width=100% height=100% style="font-size: 10px;">
									    <tr>
										    <td width=30% style="border: none;">
											    <table width=100%>
											    <tr>
											        <td style="text-align: center;">昨日</td>
											    </tr>
											        <s:iterator id="userzr" value="usersList2" status="">
											            <tr>  
											                <td style="text-align: center;">
											                    <s:if test="#userzr.sex==null || #userzr.sex=='男'.toString()">
																	<img style="width:21px;height:21px;" src="<%=basePath%>/upload/user/${userzr.picture}" alt="${userzr.name }" class="userImg"
																			onerror="this.src='images/man.jpg'">
																</s:if>
																<s:else>
																	<img style="width:21px;height:21px;" src="<%=basePath%>/upload/user/${userzr.picture}" alt="${userzr.name }" class="userImg"
																			onerror="this.src='images/woman.jpg'">
																</s:else>
													           <br/> <span>${userzr.name}</span>
											                </td>  
											            </tr>
											        </s:iterator>
											        <tr>
												        <s:if test="scoreList==null||scoreList.size()==0">
												    	   <td style="border: none;text-align: center;">今日待评</td>
												    	</s:if>
												    	<s:iterator id="score" value="scoreList">
												            <td style="border: none;text-align: center;">
				                                                ${score.titleName}:&nbsp;${score.fraction}分							            
												            </td>
													    </s:iterator>
												    </tr>
											    </table>   
										    </td>
									        	
									        
									        <th>
									            <h3 class="text-center">值日</h3>
												<div class="dutyRQCode">
												</div>
									        </th>
									        
									        <td width=30% style="border: none;">
									            <table width=100%>
										            <tr>
												        <td style="text-align: center;">今日</td>
												    </tr>
											        <s:iterator id="userjr" value="usersList3" status="">
											            <tr>  
											                <td style="text-align: center;">
											                    <s:if test="#userjr.sex==null || #userjr.sex=='男'.toString()">
																	<img style="width:21px;height:21px;" src="<%=basePath%>/upload/user/${userjr.picture}" alt="${userjr.name }" class="userImg"
																			onerror="this.src='images/man.jpg'">
																</s:if>
																<s:else>
																	<img style="width:21px;height:21px;" src="<%=basePath%>/upload/user/${userjr.picture}" alt="${userjr.name }" class="userImg"
																			onerror="this.src='images/woman.jpg'">
																</s:else>
													            <br/><span>${userjr.name}</span>
											                </td>  
											            </tr>
											        </s:iterator>
											        <tr>
												        <td style="text-align: center;">明日待评</td>
												    </tr>
											    </table> 
									        </td>
									        
									    </tr>
									</table> 
										 
										
										
									  
								</td>
								<td>
									<h3 class="text-center">餐饮预定</h3>
									<div class="codeBookRQCode">										
									</div>
 								</td> 
 							</tr> 						
 						</table>						
 					</td>
				</tr>
			</table>
			<div class="row col-xs-12 " style="padding: 0;margin: 0">
				<p class="text-right" style="padding: 0;margin: 0">上海零参科技有限公司提供技术支持</p>
			</div>
		</div>
		<div>
			<!-- <div id="bdtts_div_id">
			<audio id="tts_autio_id" autoplay="autoplay">
			<source id="tts_source_id"
				src=""
				type="audio/mpeg">
			<embed id="tts_embed_id" height="0" width="0" src="">
				</audio>
		</div>
		</div> -->
		<div id="showCalendar" ></div>
	</body>	
	<script type="text/javascript" >
	    
	$(function(){    	
		//加载预存二维码
		var resRQCode = "http://116.228.66.246:8099/s/2";
		$(".resQRCode").qrcode({
            width: 70,
            height: 70,
            render: "canvas", //设置渲染方式 table canvas
            text: utf16to8(resRQCode),
            background: "#ffffff", //背景颜色 
            foreground: "#000000" //前景颜色 
        });
		
		
		//加载请假二维码
		var leaveRQCode = "http://116.228.66.246:8099/s/1";
		$(".leaveRQCode").qrcode({
            width: 70,
            height: 70,
            render: "canvas", //设置渲染方式 table canvas
            text: utf16to8(leaveRQCode),
            background: "#ffffff", //背景颜色 
            foreground: "#000000" //前景颜色 
        });
		
		//菜谱预定二维码
		var codeBookRQCode = "该功能还未实现，还不能点餐的哦";
		$(".codeBookRQCode").qrcode({
            width: 70,
            height: 70,
            render: "canvas", //设置渲染方式 table canvas
            text: utf16to8(codeBookRQCode),
            background: "#ffffff", //背景颜色 
            foreground: "#000000" //前景颜色 
        });
		
		//值日二维码
// 		var dutyRQCode = "值日的功能还没添加到这里呢${dutyRandomToScreenIndex}";
// 		$(".dutyRQCode").qrcode({
//             width: 70,
//             height: 70,
//             render: "canvas", //设置渲染方式 table canvas
//             text: utf16to8(dutyRQCode),
//             background: "#ffffff", //背景颜色 
//             foreground: "#000000" //前景颜色 
//         });
		
		
   		  $(".dutyRQCode").qrcode({
   	            width: 70,
   	            height: 70,
   	            render: "canvas", //设置渲染方式 table canvas
   	            text: utf16to8("${dutyRandomToScreenIndex}"),
   	            background: "#ffffff", //背景颜色 
   	            foreground: "#000000" //前景颜色 
   	      });
    	   $.ajax({
				url     : "http://116.228.66.246:8099/ClearInfoAction_ajaxRandomNum.action", //访问路径
				type    : "post",                      //提交方式
				data    : "homeTitle.randomNum=${dutyRandomByScreen}",//传递到后台的参数
				dataType: "json",                      //后台返回结果的类型
				success : function(data){//成功时执行的代码	    			       
				}
   		   });
		
		/*来访人滚动列表*/
		var visitorSumLiHeight =0;
		$("#visitorUl").find("li").each(function(){
			visitorSumLiHeight += $(this).height();
		});
		var visitorUlHeight = $("#visitorUl").height();
        var bool=false;
		if(visitorSumLiHeight>=visitorUlHeight){
			bool = true
		}
		var marginTop =0;
        setInterval(function(){
            if(!bool) return;//判断运行和停止
            $("#visitorUl li:first").animate({marginTop:marginTop--},10,function(){
                if(!($(this).is(":animated"))) {    //判断是否有一个动画节点
                    if (visitorSumLiHeight + marginTop <= visitorUlHeight-20) {  //判断移出位置是否超过高度
                        $(this).css("margin", "0");
                        $(this).appendTo($("#visitorUl")); ////把第一个节点移到ul后面
                        marginTop = 0;      //重新设置移动数值
                    }
                }
        	});
        },80);
        $("#visitorUl").mouseover(function(){   //li鼠标移入，停止移动
        	if(visitorSumLiHeight>=visitorUlHeight){
    			bool = true
    		}
        });
        $("#visitorUl").mouseout(function(){
            bool=false;
        });
        
        
        //快递存入滚动
        /*来访人滚动列表*/
		var resSumLiHeight =0;
		$("#resUl").find("li").each(function(){
			resSumLiHeight += $(this).height();
		});
		var resUlHeight = $("#resUl").height();
        var resBool=false;
		if(resSumLiHeight>=resUlHeight+10){
			resBool = true
		}
		var resMarginTop =0;
        setInterval(function(){
            if(!resBool) return;//判断运行和停止
            $("#resUl li:first").animate({resMarginTop:resMarginTop--},10,function(){
                if(!($(this).is(":animated"))) {    //判断是否有一个动画节点
                    if (resSumLiHeight + resMarginTop <= resUlHeight-20) {  //判断移出位置是否超过高度
                        $(this).css("margin", "0");
                        $(this).appendTo($("#resUl")); ////把第一个节点移到ul后面
                        resMarginTop = 0;      //重新设置移动数值
                    }
                }
        	});
        },100);
        $("#resUl").mouseover(function(){   //li鼠标移入，停止移动
        	if(resSumLiHeight>=resUlHeight+10){
	            resBool=true;
    		}
        });
        $("#resUl").mouseout(function(){
            resBool=false;
        });
        
        
        //当前时间
        function getDate(){
        	var date = new Date();
            var seperator1 = "-";
            var year = date.getFullYear();
            var month = date.getMonth() + 1;
            var strDate = date.getDate();
            if (month >= 1 && month <= 9) {
                month = "0" + month;
            }
            if (strDate >= 0 && strDate <= 9) {
                strDate = "0" + strDate;
            }
    		var week; 
       		
    		if(date.getDay()==0) week="星期日"
    		if(date.getDay()==1) week="星期一"
    		if(date.getDay()==2) week="星期二" 
    		if(date.getDay()==3) week="星期三"
    		if(date.getDay()==4) week="星期四"
    		if(date.getDay()==5) week="星期五"
    		if(date.getDay()==6) week="星期六"
    		
    		var hour = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
    		var minute = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
    		var second = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
    		
            //当前日期 年月日
            var taday = year + seperator1 + month + seperator1 + strDate+" "+week+" "+hour+":"+minute+":"+second;
            
            
            
            
//         	var date = new Date();
//         	var date1 = date.toLocaleString();
        	$("#showTime").text(taday);
        }
		setInterval(getDate,1000);
		
		
		//公告
		$.ajax( {
			url : "NoticeAction!show.action",
			type : 'post',
			dataType : 'json',
			cache : false,//防止数据缓存
			success : function(useradsfa) {
				$("#showAnnouncement").empty();//清空
			var message = "";
			$(useradsfa).each(function(i, n) {
				message += (i + 1) + "、" + n.content + "&nbsp;&nbsp;&nbsp;&nbsp;";
			});
			$("#showAnnouncement").html(message);
		}
		});
	});
	
	
	function utf16to8(str) {
        var out, i, len, c;
        out = "";
        len = str.length;
        for (i = 0; i < len; i++) {
            c = str.charCodeAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                out += str.charAt(i);
            } else if (c > 0x07FF) {
                out += String.fromCharCode(0xE0 | ((c >> 12) & 0x0F));
                out += String.fromCharCode(0x80 | ((c >> 6) & 0x3F));
                out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F));
            } else {
                out += String.fromCharCode(0xC0 | ((c >> 6) & 0x1F));
                out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F));
            }
        }
        return out;
	}
	
	function searchCalendar(userId,userName,userCode){
// 		location.href="${pageContext.request.contextPath}/inDoorScreenAction!findCalendarbyPerson.action?calendar.userId="+userId+"&calendar.userName="+userName;
		$("#showCalendar").load("${pageContext.request.contextPath}/inDoorScreenAction!findCalendarbyPerson.action?calendar.userId="+userId+"&calendar.userName="+userName+"&calendar.calendarState=未完成&pageStatus=e-work");//&pageSize=2
		$("#showCalendar").dialog({
// 			closed : true,
			hide: 'slide',
			width:'80%',
	        height:'630',
			closeText: "X",
			draggable:true,
			close: function( event, ui ) {
				window.location.reload()
			}
	    });	
	}
	
	function searchLineChart(userCode){
		var date = new Date();
        var seperator1 = "-";
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        var endTime = year + seperator1 + month + seperator1 + strDate;
        
        var date2 = new Date(endTime);
        date2.setDate(date.getDate()-7);
        var month2 = date2.getMonth()+1;
        var strDate2 = date2.getDate();
        if(month2>=0 && month2<=9){
        	month2 = "0"+month2;
        }
        if(strDate2>=0 & strDate2<=9){
        	strDate2 = "0"+strDate2;
        }
        var startTime = date2.getFullYear() + seperator1 + month2 + seperator1 + strDate2;
        
// 		var startTime = fun_date(-7);//$("#startTime").val();
// 		var endTime = $("#endTime").val();
		$("#showCalendar").load("${pageContext.request.contextPath}/faceAction!toSearchLineChart.action?userCode="+
				userCode+"&startTime="+startTime+"&endTime="+endTime+"&pageStatus=e-work");
		$("#showCalendar").dialog({
// 			closed : true,
			hide: 'slide',
			width:'80%',
	        height:'630',
			closeText: "X",
			draggable:true,
			close: function( event, ui ) {
				window.location.reload()
			}
	    });	
	}
	
	</script>
</html>
