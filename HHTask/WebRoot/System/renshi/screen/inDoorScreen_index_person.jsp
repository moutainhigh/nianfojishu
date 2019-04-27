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
		<link rel="stylesheet" type="text/css"
			href="${pageContext.request.contextPath}/javascript/bootstrap-3.3.7-dist/css/bootstrap.min.css">
		<script src="${pageContext.request.contextPath}/javascript/jquery/jquery-3.2.1.js"> </script> 
		<script type="text/javascript"
 			src="${pageContext.request.contextPath}/javascript/bootstrap-3.3.7-dist/js/bootstrap.js"> </script> 
 		<script type="text/javascript" src="<%=basePath%>/javascript/jquery.qrcode.min.js"></script>
		<style type="text/css">
			*{
				font-family: 楷体;
			}
			html,body,.container{
				background-color: #97BCE4;
 				height: 99%; 
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
 			#p2{
				font-weight: 900;
				font-size: 0.8em;
				font-family: Arial Black;
				color: #27408B;
			}
 			#p3{
				font-weight: 900;
				font-size: 1em;
				font-family: Arial Black;
				font-weight: 300;
				color: #27408B;
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
				font-size: 3.625em;
				font-weight: 900;
			}
			h3{
				font-weight: bold;
				margin-top: 10px;
			}
			
 			.table,.table tbody{
				margin-bottom: none;;
				padding-bottom: none;		
				height: 84%;
 			}
 			/*人员状态start*/ 
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
			/*人员状态over*/
			
			#visitorUl{
				font-size: 20px;
				margin: 10 auto;
				height: 100%;
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
				height: 100%;
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
				border: 1px solid rgba(255, 255, 255, 0.9);
			}
			.userImg{
				width:62px;
				height:62px;
				border-radius: 50%;
				margin: 0 auto;
				
			}
			.userStatus{
				height: 10px;
				margin-bottom: 10px;
/* 				float: left; */
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
/* 				font-size: 1.1em; */
				margin: 0 auto;
				height: 420px;
				padding: 10px 10px;
				padding-left: 10px;
				overflow: hidden;
			}
			#codebookUl li{
   				list-style:none;
   				padding: 4px;
   				
			}
			
			#title{
				height: 12%;
			}
			.oneTr{
				height: 50%;
			}
			.twoTr{
				height: 50%;
			}
			.userList{
				height:100%; 
			}
			.carousel-indicators{
				position: absolute;
				top: -57px;
				left: 40px;
			}
			.table,.table tr td{
   				border: 3px solid #5472B4;
				cellpadding:2px;
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
			#showTime{
				font-size: 17px;
				padding: 0;
				margin: auto 0 0 auto;
				position: absolute;
				top:60px;
 				right: 16px; 
/* 				left:0; */
				width: 400px;
				height: 30px;
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
				width: 80%;
				margin: 10 auto ;
				
			}
			.progress{
				margin: 0;
				padding: 0;
			}
			.meetingLi{
				border:  1px solid white; 
				margin-bottom: 10px;
				list-style-type: none;
			}
/* 			.number{ */
/* 				color: white; */
/* 			} */
		</style>
	</head>
	<body>
		<div class="container">
			<div class="row" id="title">
				<div class="col-xs-3" style="position:relative;">
<%--					<iframe style="position:absolute;top:38px;" width="300" scrolling="no" height="60" frameborder="0" allowtransparency="true" --%>
<%--							src="http://i.tianqi.com/index.php?c=code&id=12&icon=1&num=7&dy=2"></iframe>--%>
				</div>
				<div class="col-xs-6" style="height: 20%;">
<%-- 					<h1 class="text-center"><span id="p1">易</span><span id="p4">-</span><span id="p3">Work</span>&nbsp;<span id="p5">&</span>&nbsp;<span id="p1">易</span><span id="p4">-</span><span id="p3">Life</span></h1> --%>
					<h1 class="text-center"  style="height: 20%;"><img src="${pageContext.request.contextPath }/img/e-work.png" class="imgTitle"></h1>
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
							<div class="col-xs-3"></div>
							<div class="col-xs-2">
								<div class="guardClass"></div> 在岗
							</div>
							<div class="col-xs-2">
								<div class="leaveClass"></div> 离岗
							</div>
							<div class="col-xs-2">
								<div class="dutyClass"></div> 值日
							</div>
							<div class="col-xs-3">
								<div class="timeOutClassShow"></div> 未完成任务
							</div>
						</div>
						<p class="text-right" style="font-size: 15px; padding-right: 10px;">按考勤先后从左至右依次排序</p>
						<div class="row">
							<div id="myCarousel" class="carousel slide" data-ride="carousel" data-interval="5000" style="position: relative;">
								<!-- 轮播（Carousel）指标 -->
								<ol class="carousel-indicators">
									<s:iterator value="usersList" status="ps">
										<s:if test="#ps.index%20==0">
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
										<s:if test="#ps.index%20==0">
											<!-- 这里展示一个轮播的视图 -->
											<s:if test="#ps.index==0">
												<div class="item active">
											</s:if>
											<s:else>
												<div class="item">
											</s:else>
										</s:if>
										
										<div class="col-xs-3 text-center">
											<div>
												<!-- 显示待办事项 -->
<!-- 												<div class="showBacklogLeft"> -->
												
<!-- 												</div> -->
<%-- 												<s:if test="#ul.backlogList.size()!=0"> --%>
<!-- 													<div class="showBacklog"> -->
<%-- 														${fn:length(ul.backlogList)} --%>
<!-- 													</div> -->
<%-- 												</s:if> --%>
<%-- 												<s:else> --%>
<!-- 													<div class="showBacklogNull"></div> -->
<%-- 												</s:else>	 --%>
												<s:if test="#ul.sex==null || #ul.sex=='男'.toString()">
													<img src="<%=basePath%>/upload/user/${ul.picture}" alt="${ul.name }" class="userImg"
															onerror="this.src='images/man.jpg'">
												</s:if>
												<s:else>
													<img src="<%=basePath%>/upload/user/${ul.picture}" alt="${ul.name }" class="userImg"
															onerror="this.src='images/man.jpg'">
												</s:else>
											</div>
											<div class="userStatus text-center">
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
												${ul.name }
											</div>
											<!-- 显示代办事项进度条(已完成/总数) -->
											<div class="itemBar text-center">
												<s:if test="#ul.totalNum!=null && #ul.totalNum!=0">
<!-- 												<div class="showBacklog"> -->
<%-- 													${fn:length(ul.backlogList)} --%>
<!-- 												</div> -->
													<div class="progress progress-striped text-center" >
														<div class="progress-bar text-center" role="progressbar"  
													        aria-valuemin="0" aria-valuemax="100" style="width:${ul.successNum/ul.totalNum*100 }%;">
													        <s:if test="#ul.successNum==#ul.totalNum">
													        	已完成 ${ul.totalNum}
													        </s:if>
													        <s:else>
														       ${ul.successNum/ul.totalNum*100 }%
													        </s:else>
													    </div>
													    ${ul.successNum}/${ul.totalNum}
													</div>
												</s:if>
												<s:else>
													<div class="showBacklogNull"></div>
												</s:else>	
											</div>
														<s:if test="#ul.timeOutNum!=null && #ul.timeOutNum!=0">
													<div class="timeOutClass ">
															${ul.timeOutNum }
													</div>
														</s:if>
										</div>
										
										<s:if test="#ps.index%20==19">
											</div>
											<!-- 视图结束 -->
										</s:if>
									</s:iterator>
								</div>
								
								<!-- 轮播（Carousel）导航 -->
<!-- 								<a class="left carousel-control" href="#myCarousel" role="button" data-slide="prev"> -->
<%-- 									<span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span> --%>
<%-- 									<span class="sr-only">Previous</span> --%>
<!-- 								</a> -->
<!-- 								<a class="right carousel-control" href="#myCarousel" role="button" data-slide="next"> -->
<%-- 									<span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span> --%>
<%-- 									<span class="sr-only">Next</span> --%>
<!-- 								</a> -->
							</div>
						</div>
					</td>
					<td rowspan="2" class="col-xs-3" class="cookbookTd">
						<h3 class="text-center">会议预定</h3>
						<ul id="codebookUl">
							<s:iterator value="meetingList" id="ml" status="ps">
								<li class="meetingLi ">
									<div>
										<div class="col-xs-6">
											${ml.title}
										</div>
										<div class="col-xs-6">
											${ml.position }
										</div>
									</div>
									
									<p>${ml.startDate } -- ${ml.endDate }</p>
								</li>
							</s:iterator>
						</ul>
					</td>
				</tr>
				<tr class="twoTr">
					<td class="col-xs-3">
						<h3 class="text-center">快递存取</h3>
						<ul id="resUl"  class="clearfix ">
							<s:iterator value="resList" id="rl" status="ps">
								<s:if test="#ps.index%3==0">
									<li>
								</s:if>
									<div class="resItem text-center">
											<img src="<%=basePath%>/upload/user/${rl.picture}" alt="${rl.addName }"
													onerror="this.src='images/man.jpg'">
										<div>${rl.addCode}</div>
									</div>
								<s:if test="#ps.index%3==2">
									</li>
								</s:if>
							</s:iterator>
						</ul>
					</td>
				</tr>
			</table>
			<div class="row col-xs-12 " style="padding: 0;margin: 0">
				<p class="text-right" style="padding: 0;margin: 0">上海零参科技有限公司提供技术支持</p>
			</div>
		</div>
	</body>	
	<script type="text/javascript" >
	
	$(function(){
		setTimeout(function(){
			console.log("三分钟刷新页面");
			location.reload();
		},1000*60*3);
		
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
	
	function wrongpercent(value, row, index) {
        var res = 100 * row.wrongnums / row.testnums;
        return ["<div class='progress'> <div class='progress-bar' role='progressbar' aria-valuenow='50' aria-valuemin='0' aria-valuemax='100' style='width:"+res.toFixed(2)+"%'>"+res.toFixed(2)+"</div> </div>"];
//        return res.toFixed(2);
    }
	</script>
</html>