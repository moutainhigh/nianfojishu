<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta http-equiv="X-UA-Compatible" content="ie=edge">
		<meta HTTP-EQUIV="pragma" CONTENT="no-cache"> 
		<meta HTTP-EQUIV="Cache-Control" CONTENT="no-store, must-revalidate"> 
		<meta HTTP-EQUIV="expires" CONTENT="0">
		<meta http-equiv="Access-Control-Allow-Origin" content="*">
		<link rel="stylesheet" type="text/css"
			href="<%=basePath%>/javascript/bootstrap-3.3.7-dist/css/bootstrap.min.css">
		<script src="<%=basePath%>/javascript/jquery/jquery-3.2.1.js"> </script> 
 		<script type="text/javascript" src="<%=basePath%>/javascript/webcam/jquery.webcam.js"></script>
		<script type="text/javascript"
 			src="<%=basePath%>/javascript/bootstrap-3.3.7-dist/js/bootstrap.js"> </script> 
		<style type="text/css">
			#contentDiv{
				overflow: visible;
			}
			.classIcon{
				width: 110px;
				height: 40px;
				line-height: 40px;
				background-color: #9D9D9D;
				border-radius:55%;
				margin-top: 65px;
			}
			.successClassIcon{
				background-color: #FFAF60;
			}
			.classIcon i{
				font-size: 20px;
				line-height: 40px;
				color: white;
			}
			.classTitleIcon{
				height: 40px;
				line-height: 40px;
				background-color: #9D9D9D;
				border-radius:55%;
			}
			.container, body, html{
				width: 98%;
				height: 100%;
				margin: auto;
				padding: auto;
			}
			#header{
				width: 100%;
				height: 30%;
			}
			#body{
				width: 100%;
				height: 60%;
			}
			#footer{
				width: 100%;
				height: 10%;
			}
			#submitBtn{
				background-color: #529DD5;
			}
			.custom-variables {
			  --some-color:white;
			  --some-keyword: italic;
			  --some-size: 3.25em;
/* 			  --some-complex-value: 1px 1px 2px whitesmoke, 0 0 1em slategray, 0 0 0.2em slategray; */
			  color: var(--some-color);
			  font-size: var(--some-size);
/* 			  font-style: var(--some-keyword); */
			  text-shadow: var(--some-complex-value);
			  cursor: pointer;
			  padding: 80px 20px;
			}
			body,.modal-content{
				background-color: #97BCE4;
				padding-right: 0px!important;
			}
			.itemClass{
				/* 选项*/
 			    border: 2px solid #ccc; 
				border-radius:25px;
				background-color: #529DD5;
				width: 100%;
			  	height: 360px;
			  	-webkit-border-radius: 30px;
				-moz-border-radius: 30px;
				-webkit-box-shadow: rgb(20,255,255) 0px 0px 15px;
				-moz-box-shadow: rgb(20,255,255) 0px 0px 15px;
				box-shadow: rgb(20,255,255) 0px 0px 15px;
			}
			h1{
				color: white;
				font-size: 4.25em;
			}
			h2,h3,h4,h5,h6{
				color: white;
			}
			*{
				font-family: 黑体;
			}
			ul{
				list-style: decimal;
				color: gray;
				margin-left: 45px;
			}
			.btn{
				background-color: #529DD5;
			  	-webkit-border-radius: 10px;
				-moz-border-radius: 10px;
				-webkit-box-shadow: rgb(20,255,255) 0px 0px 15px;
				-moz-box-shadow: rgb(20,255,255) 0px 0px 15px;
				box-shadow: rgb(20,255,255) 0px 0px 15px;
			}
			#returnGoHis{
				background-color: #FDDA38;
			}
		</style>
	</head>
	<body>
		<!-- 身份证模态框（Modal） -->
		<div class="modal fade" id="identityModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
		    <div class="modal-dialog">
		        <div class="modal-content">
		            <div class="modal-header">
		                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		                <h4 class="modal-title " id="myModalLabel">提醒消息</h4>
		            </div>
		            <div class="modal-body">
						<div class="row">
							<h3 class="text-center">请将居民身份证放置在下方识读区</h3>
						</div>
						<div class="row" >
							<div class="col-lg-2"></div>
							<div class="col-lg-8">
								<br>
								<img src="<%=basePath%>/img/idcard.jpg" alt="请刷身份证"  style="width: 100%;">
							</div>
							<div class="col-lg-2"></div>
						</div>
						<div class="row">
							<div class="alert alert-warning">
						    	<p>注：如刷身份证无反应，请点击[<a href="#" onclick="javascript:location.reload();">刷新</a>]或与工作人员联系。</p>
						    	<p>如没有访客需求，请不要按系统指示继续操作，我们将保存您的个人信息</p>
						    	<p>本窗口将在<span id="second">30</span>秒后自动关闭。</p>
							</div>
						</div>
		            </div>
		            <div class="modal-footer">
		                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
		            </div>
		        </div>
		    </div>
		</div>
		
		
		
		<!-- 拍照模态框（Modal） -->
		<div class="modal fade" id="picutreModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
		    <div class="modal-dialog" style="width: 680px;">
		        <div class="modal-content">
		            <div class="modal-header">
		                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		                <h4 class="modal-title" id="myModalLabel">提醒消息</h4>
		            </div>
		            <div class="modal-body">
						<div class="row ">
							<h3 class="text-center">请注视上方摄像头<span id="timePicture" style="color:red;">5</span>秒后将自动拍照</h3>
						</div>
						<div class="row " >
		            		<div class="row col-lg-12" align="center">
		            			<br>
			            		<video id="video" ></video><br>
			            		<canvas width="136" height="168" id="preViewPicCanvas"></canvas>
		            		</div>
		            		<div class="row col-lg-12 text-center">
		            			<br><br>
								<button id="capture" type="button" class="btn btn-default">拍照</button>
								<button type="button" class="btn btn-default" onclick="openFingerModal()">下一步</button>
		            		</div>
						</div>
						<div class="row">
							<div class="alert alert-warning">
							    	<p>注：请点击[拍照]按钮进行拍照，拍好照片后点击下一步进行指纹录入。</p>
							    	<p>如果没有拍好照片，请点击第二步的图片即可再次调用本窗口</p>
							    	<p>如没有访客需求，请不要按系统指示继续操作，我们将保存您的个人信息</p>
							</div>
						</div>
		            </div>
		            <div class="modal-footer">
		                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
		            </div>
		        </div>
		    </div>
		</div>
		
		
		<!-- 指纹模态框（Modal） -->
		<div class="modal fade" id="fingerModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
		    <div class="modal-dialog" style="width: 680px;">
		        <div class="modal-content">
		            <div class="modal-header">
		                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		                <h4 class="modal-title" id="myModalLabel">提醒消息</h4>
		            </div>
		            <div class="modal-body">
						<div class="row ">
							<h3 class="text-center">请在屏幕下方指纹识别处录入指纹</h3>
						</div>
						<div class="row " >
		            		<div class="row col-lg-12" align="center">
		            			<br>
		            			<img src="<%=basePath%>/img/finger.png" alt="请录入指纹"  style="height: 168px;">
		            		</div>
		            		<div class="row col-lg-12 text-center">
		            			<br>
								<p  id="fingerInfo" onclick="addFinger()" style="color: red;font-size: 20px;">点击这里开始指纹录入</p>
		            		</div>
						</div>
						<div class="row">
							<div class="alert alert-warning">
							    	<p>注：根据提示用同一个手指录入三次，</p>
							    	<p>如果已经存在或者异常，请换个手指重试</p>
							    	<p>如没有访客需求，请不要按系统指示继续操作，我们将保存您的个人信息</p>
							</div>
						</div>
		            </div>
		            <div class="modal-footer">
		                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
		            </div>
		        </div>
		    </div>
		</div>
		
		<!-- 模态框（Modal） -->
		<div class="modal fade" id="showModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
		    <div class="modal-dialog">
		        <div class="modal-content">
		            <div class="modal-header">
		                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		                <h4 class="modal-title" id="myModalLabel">提醒消息</h4>
		            </div>
		            <div class="modal-body">
		            	<h3 style="color: red" id="showMessage"></h3>
		            </div>
		            <div class="modal-footer" id="returnBtn">
		                <button class="btn btn-default" data-dismiss="modal">关闭</button>
		            </div>
		        </div>
		    </div>
		</div>
		
		
		<div class="container">
			<div id="header">
				<div class="row" style="height: 40px;">
	            </div>
	            <div class="row clearfix">
	            
	            	<img src="<%=basePath%>/${companyInfo.logo}" height="70px;" alt="<%=basePath%>/${companyInfo.logo}">
					<br>
					<br>
					<br>
					<h1 align="center">
						身份信息采集
					</h1>
					<h2 align="center">Identity information collection</h2>
	            </div>
			</div>
			<div id="body">
				<div class="row ">
					<div class="col-lg-4" align="center">
						<div class="itemClass" onclick="tanchuApply()">
							<div class="classIcon oneClassIcon" >
								<i>1</i>
							</div>
							<p>身份证采集</p>
							<div>
								<canvas width="102" height="126" id="identityCanvas" ></canvas>
		            			<p class="">身份证图片信息</p>
							</div>
						</div>
					</div>
					<div class="col-lg-4 " align="center">
						<div class="itemClass" onclick="photographDialog()">
							<div class="classIcon twoClassIcon" >
								<i>2</i>
							</div>
							<p>头像识别采集</p>
							<div class="text-center" id="showPicture">
			            		<canvas width="102" height="126" id="canvas" ></canvas>
			            		<p class="">拍照图片信息</p>
			            	</div>
						</div>
					</div>
					<div class="col-lg-4 " align="center">
						<div class="itemClass" onclick="addFinger()">
							<div class="classIcon threeClassIcon" >
								<i>3</i>
							</div>
							<p>指纹采集</p>
							<div>
								<img width="102" height="126"  id="fingerPic">
								<p>指纹图片信息</p>
							</div>
						</div>
					</div>
				</div>
	            <div class="row ">
	            	<div class="col-lg-12">
	            		<br>
	            		<br>
	            		<s:if test="pageStatus!=null&&pageStatus=='addFollow'">
	            			<form action="<%=basePath%>/visitorAction!addVisitorApply.action" method="post" id="form">
	            				<input type="hidden" name="visitor.visitorComp" value="${visitor.visitorComp}" >
	            				<input type="hidden" name="visitor.visitorPhone" value="${visitor.visitorPhone }" >
	            				<input type="hidden" name="visitor.dateTime" value="${visitor.dateTime }" id="dateTime">
	            				<input type="hidden" name="visitor.intervieweePhone" value="${visitor.intervieweePhone}" >
	            				<input type="hidden" name="visitor.interviewee" value="${visitor.interviewee }" >
	            				<input type="hidden" name="visitor.visitorCause" value="${visitor.visitorCause }" >
	            				<input type="hidden" name="visitor.intervieweeId" value="${visitor.intervieweeId}" >
	            				<input type="hidden" name="visitor.followId" value="${visitor.followId }" >
	            				<s:iterator value="visitorList" id="vl" status="ps">
									<input type="hidden" value="${vl.id}" name="visitorList[${ps.index}].id" class="VisitorIds" >
								</s:iterator>
	            		</s:if>
	            		<s:else>
							<form action="<%=basePath%>/visitorAction!toVisitorApply.action" method="post" id="form">
	            		</s:else>
		            		<input type="hidden" name="visitor.visitorName" id="visitorName" >
		            		<input type="hidden" name="visitor.identityCard" id="identityCard">
		            		<input type="hidden" name="visitor.visitorIdentityPic" id="visitorIdentityPic">
		            		<input type="hidden" name="visitor.fingerId" id="fingerId">
		            		<input type="hidden" name="visitor.fingerprint" id="fingerPrint">
		            		<input type="hidden" name="visitor.picture" id="submitPicutre" >
			            </form>
	            	</div>
	            </div>
	            <div class="row">
	            	<div class="col-lg-12 text-center">
	            		<input type="button" class="btn btn-default" id="submitBtn" value="下一步" onclick="submitForm()" 
	            			style="width: 30%; height: 90px;font-size: 25px;">
	            	</div>
	            </div>
	        </div>
	        <div id="footer">
            	<div class="row">
            		<div class="text-left col-lg-12">
	            		<button type="button" class="btn btn-default" onclick='javaScript:history.go(-1)'  id="returnGoHis">返回上一页</button>
	            	</div>
            	</div>
            	<br>
				<div class="row">
					<p class="col-lg-12 text-right">上海零参科技有限公司提供技术支持</p>
				</div>
			</div>
		</div>
	</body>	
	<script type="text/javascript" >
	
	//-----------------------------------调用媒体设备-摄像头
	//访问用户媒体设备的兼容方法
    function getUserMedia(constraints, success, error) {
      if (navigator.mediaDevices.getUserMedia) {
        //最新的标准API
        navigator.mediaDevices.getUserMedia(constraints).then(success).catch(error);
      } else if (navigator.webkitGetUserMedia) {
        //webkit核心浏览器
        navigator.webkitGetUserMedia(constraints,success, error)
      } else if (navigator.mozGetUserMedia) {
        //firfox浏览器
        navigator.mozGetUserMedia(constraints, success, error);
      } else if (navigator.getUserMedia) {
        //旧版API
        navigator.getUserMedia(constraints, success, error);
      }
    }

    let video = document.getElementById('video');
    let canvas = document.getElementById('canvas');
    let context = canvas.getContext('2d');
    let preViewPicCanvas = document.getElementById("preViewPicCanvas");//预览的照片
	let preViewPicContext= preViewPicCanvas.getContext('2d');
    function success(stream) {
      //兼容webkit核心浏览器
      let CompatibleURL = window.URL || window.webkitURL;
      //将视频流设置为video元素的源
      console.log(stream);

      //video.src = CompatibleURL.createObjectURL(stream);
      video.srcObject = stream;
      video.play();
    }

    function error(error) {
      console.log(`访问用户媒体设备失败${error.name}, ${error.message}`);
    }

    if (navigator.mediaDevices.getUserMedia || navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia) {
      //调用用户媒体设备, 访问摄像头
      getUserMedia({video : {width: 136, height:168 }}, success, error);
    } else {
      alert('不支持访问用户媒体');
    }

    document.getElementById('capture').addEventListener('click', function () {
    	clearInterval(pageTimer["autoPic"]);
      	context.drawImage(video, 0, 0, 102, 126);
      	preViewPicContext.drawImage(video, 0, 0, 136, 168);
    
    	//开始上传
	    var canvas = document.getElementById("canvas"); //创建canvas元素
		//       var width=canvas.width;
		//       var height=canvas.height;
		//       canvas.getContext("2d").drawImage(image,0,0,width,height); //将图片绘制到canvas中
	    var dataURL=canvas.toDataURL('image/jpeg',1); //转换图片为dataURL
		console.log(dataURL);
	      //先上传图片保存，把文件名称返回到前台，然后提交。.
    	var identityCard = $("#identityCard").val();
	    
		$.ajax({
			type:"POST",
			url:"<%=basePath%>/visitorAction!uploadByPicture.action",
			dataType:"json",
			data:{
				image:dataURL,
				"idCard":identityCard
			},
			async : false,
			success:function(data){
				if(data!=null && data.success==true&& data.message=="success"){
					$("#submitPicutre").val(data.data);
					$(".twoClassIcon").addClass("successClassIcon");
					$("#submitBtn").val("下一步");
					//两秒后自动点击下一步
					setTimeout(function(){
			 			console.log("两秒后执行点击下一步");
			 		},3000);
					openFingerModal();
				}else{
					clearInterval(pageTimer["apply"]);
					$("#showMessage").html("上传照片异常："+"结果："+data.success+"，消息："+data.message);
					$("#showModal").modal('show');
					$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
					jishi(false);
					$('#identityModal').modal('hide');	
				}
			},
			error:function(XMLHttpRequest, textStatus, errorThrown) {
				clearInterval(pageTimer["apply"]);
				$("#showMessage").html("上传照片异常："+XMLHttpRequest.status+"  "+XMLHttpRequest.readyState+"  "+textStatus);
				$("#showModal").modal('show');
				$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
				jishi(false);
				$('#identityModal').modal('hide');
		    }
		});
		
    })
    //-----------------------------------------------------摄像头结束
    
    
    var pageTimer = {};//定义所有的定时器
	var portStatus = false;
	$(function(){
		
		//model 关闭事件
		$('#identityModal').on('hide.bs.modal', function () {
			clearInterval(pageTimer["apply"]);
		});
		$('#showModal').on('hide.bs.modal', function () {
			clearInterval(pageTimer["message"]);
			clearInterval(pageTimer["check"]);
		});
		$('#picutreModal').on('hide.bs.modal', function () {
			clearInterval(pageTimer["message"]);
			clearInterval(pageTimer["autoPic"]);
		});
		$('#picutreModal').on('show.bs.modal', function () {
			$("#timePicture").text(5);
			pageTimer["autoPic"] = setInterval(jishi,1000);
			function jishi(){
				var timePicture = $("#timePicture").text();
				var time = parseInt(timePicture);
				time--;
				$("#timePicture").text(time);
				if(time<=0){
					clearInterval(pageTimer["autoPic"]);
					$("#capture").trigger("click");
				}
			}
		});
		
		if("${pageStatus}"!="addFollow"){
			//5分钟后跳转到主页
			setTimeout(function(){
				location.href="<%=basePath%>/visitorAction!toVisitorIndex.action";
			},300000);
		}
		
		
		tanchuApply();
		
		
		
	});
	
	
    //-----------------------------------------------------读取身份证
    function tanchuApply(){
    	$(".oneClassIcon").removeClass("successClassIcon");
		var numberIndex = 0;//请求次数
		$("#second").text(30);
		$('#identityModal').modal('show');
		pageTimer["apply"] = setInterval(dijian,1000);
		function dijian(){
			numberIndex ++;
			var numberVal = $("#second").text();
			var number = parseInt(numberVal)-1;
			if(number<=0){
// 				clearInterval(pageTimer["apply"]);
// 				$("#showMessage").html("获取身份失败，请关闭窗口后重新放置卡片");
// 				$("#showModal").modal('show');
// 				$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
// 				jishi(false);
				$('#identityModal').modal('hide');
				history.go(-1);
			}
			$("#second").text(number);
			console.log("second:"+number);

			getIdentityCard(numberIndex,"apply");
		}
	}
    
    function getIdentityCard(numberIndex,pattern){
		if(numberIndex%2!=0){
			console.log('获取身份证'+numberIndex);
			return false;//两秒执行一次获取操作，目的因为跨域访问不能使用同步
		}
		//ajax获取身份信息
		$.ajax({
			type:"POST",
			url:"http://localhost:8888/termb/readcontent?number="+numberIndex,
			dataType:"jsonp",
			success:function(data){
				if(data!=null){
					if(data.status==2){
						//寻卡失败，继续寻卡
						$("#identityCard").val("");
						$("#name").val("");
					}else if(data.status=1){
						clearInterval(pageTimer["apply"]);
						
						if(pattern=="apply"){
							clearInterval(pageTimer["apply"]);
							$("#visitorName").val(data.name);
							$("#identityCard").val(data.identtiy);
							getIdentityImg(data.identtiy,data.name);//获得图片后上传到服务器
							//$("#applyForm").submit();
						}
					}else if(data.status!=1){
						clearInterval(pageTimer["apply"]);
						$("#showMessage").html(data.info+"[获取身份失败，请重新放置卡片]");
						$("#showModal").modal('show');
						$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
						jishi(false);
						$('#identityModal').modal('hide');
					}
				}else{
					clearInterval(pageTimer["apply"]);
					$("#showMessage").html("本地项目异常，请联系管理员");
					$("#showModal").modal('show');
					$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
					jishi(false);
					$('#identityModal').modal('hide');
				}
			},
			error:function(XMLHttpRequest, textStatus, errorThrown) {
				clearInterval(pageTimer["apply"]);
				$("#showMessage").html("系统获取身份证异常："+XMLHttpRequest.status+"  "+XMLHttpRequest.readyState+"  "+textStatus);
				$("#showModal").modal('show');
				$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
				jishi(false);
// 				$('#identityModal').modal('hide');
		    }
		});
		return false;
	}
    
    function getIdentityImg(identityCard,name){
    	//获取这个身份证时候已经申请
    	var checkState = true;
    	var dateTime = $("#dateTime").val();
    	$.ajax({
    		type:"post",
    		url:"<%=basePath%>/visitorAction!fingFingerByIdentity.action",
    		data:{
    			"idCard":identityCard,
    			"pageStatus":"idCard",
    			"param":dateTime
    		},
    		async : false,
    		dataType:"json",
    		success:function(data){
    			if(data!=null){
    				clearInterval(pageTimer["apply"]);
					$("#showMessage").html("此身份证已在随访人列表，不能重复申请，谢谢<br><br>"
							+"<a href='#' onclick='selectFollowList()'>查看随访人列表</a>&nbsp;或者 <a href='#' "
							+" onclick='deleteVisitorByIdCard(\""+identityCard+"\")'>删除后重新添加</a>");
					$("#showModal").modal('show');
					$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
					jishi(false);
					$('#identityModal').modal('hide');	
					checkState = false;
    				return false;
    			}
    		}
    	});
    	if(checkState){
    		clearInterval(pageTimer["apply"]);
    		var image = new Image();
    		image.src = "http://localhost:8888/termb/getPicture?callback=123456798"; //D:Apache24/htdocs/xp.jpg
    		image.setAttribute("crossOrigin",'anonymous');
    		image.onload= function(){
    			setTimeout(function(){
    				console.log("两秒后执行获取图片nei");
    			},3000);
    			var identityCanvas = document.getElementById("identityCanvas"); //创建canvas元素
    			try{
    	             width=image.width;//确保canvas的尺寸和图片一样
    	             height=image.height;
    		         identityCanvas.width=width;
    		         identityCanvas.height=height;
    		         identityCanvas.getContext("2d").drawImage(image,0,0,width,height); //将图片绘制到canvas中
    		         var dataURL=identityCanvas.toDataURL('image/jpeg',1); //转换图片为dataURL
    		         console.log(dataURL);
    				$.ajax({
    					type:"POST",
    					url:"<%=basePath%>/visitorAction!uploadByIdentity.action",
    					dataType:"json",
    					data:{
    						image:dataURL,
    						"idCard":identityCard
    					},
    					async : false,
    					success:function(data){
    						if(data!=null && data.success==true&& data.message=="success"){
    							$('#identityModal').modal('hide');
    							var visitorIdentityPic = data.data;
    							$("#visitorIdentityPic").val(visitorIdentityPic);
    							$(".oneClassIcon").addClass("successClassIcon");
    							photographDialog();
    							//location.href="<%=basePath%>/visitorAction!judgeIdentity.action?visitorIdentity.id="+data.data;
    						}else{
    							clearInterval(pageTimer["apply"]);
    							$("#showMessage").html("上传身份证异常："+"结果："+data.success+"，消息："+data.message);
    							$("#showModal").modal('show');
    							$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
    							jishi(false);
    							$('#identityModal').modal('hide');	
    						}
    					},
    					error:function(XMLHttpRequest, textStatus, errorThrown) {
    						clearInterval(pageTimer["apply"]);
    						$("#showMessage").html("上传身份证异常："+XMLHttpRequest.status+"  "+XMLHttpRequest.readyState+"  "+textStatus);
    						$("#showModal").modal('show');
    						$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
    						jishi(false);
    						$('#identityModal').modal('hide');
    				    }
    				});
    			}catch(err){
    				//getIdentityImg(identityCard,name);
    				return false;
    			}
    		}
    	}
    	
    	
    }
    //------------------------------------------------------读取身份证结束
    
    //------------------------------------------------------拍照
    
    //------------------------------------------------------拍照结束
    
    //------------------------------------------------------指纹
    
    //------------------------------------------------------指纹结束
    
    
	
	
	function jishi(idName){
		pageTimer["message"] = setInterval(dijian,1000);
		function dijian(){
			var numberVal = $("#second1").text();
			var number = parseInt(numberVal)-1;
			if(number<=0){
				clearInterval(pageTimer["message"]);
				$('#showModal').modal('hide');
				$('#identityModal').modal('hide');
			}
			$("#second1").text(number);
			console.log("second1:"+number);
		}
	}
	
	/**
	 * 拍照弹出框
	 */
	function photographDialog(){
// 		$(".twoClassIcon").removeClass("successClassIcon");
		var identityCard = $("#identityCard").val();
	    if(identityCard==null || identityCard==""){
	    	$("#showMessage").html("请先采集身份");
			$("#showModal").modal('show');
			$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
			jishi(false);
			$('#identityModal').modal('hide');
			return false;
	    }
	    $('#picutreModal').modal('show');
	}
	
	function convertCanvasToImage(canvas){
        //新Image对象,可以理解为DOM;
        var image = new Image();
        //canvas.toDataURL返回的是一串Base64编码的URL,当然,浏览器自己肯定支持
        //指定格式PNG
        image.src = canvas.toDataURL("image/png");
        return image;
    }
	
	//添加指纹
	function addFinger(){
		var identityCard = $("#identityCard").val();
	    if(identityCard==null || identityCard==""){
	    	$("#showMessage").html("请先采集身份");
			$("#showModal").modal('show');
			$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
			jishi(false);
			$('#identityModal').modal('hide');
			return false;
	    }
	    
		$(".threeClassIcon").removeClass("successClassIcon");
		$("#fingerPic").attr("src","");
		$("#fingerModal").modal('show');
		$.ajax({
			type:"POST",
			url:"http://localhost:8888/finger/addFinger",
			data:{
				"ml":"F5010102010003F5"
			},
			dataType:"jsonp",
			success:function(data){
				$("#fingerInfo").text(data);
				clearInterval(pageTimer["finger"]);
				setTimeout(function(){
					console.log("两秒后开启监听");
				},2000);
				getFingerStatus();
			},
			error:function(XMLHttpRequest, textStatus, errorThrown) {
				clearInterval(pageTimer["apply"]);
				$("#showMessage").html("指纹识别异常："+XMLHttpRequest.status+"  "+XMLHttpRequest.readyState+"  "+textStatus);
				$("#showModal").modal('show');
				$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
				jishi(false);
				$('#identityModal').modal('hide');
		    }
		});
	}
	
	//获取指纹状态
	function getFingerStatus(){
		pageTimer["finger"] = setInterval(getFinger,1000);
		function getFinger(){
			$.ajax({
				type:"POST",
				url:"http://localhost:8888/finger/getFingerStatus",
				dataType:"jsonp",
				success:function(data){
					if(data!=null && data.info!=null){
						if(data.info=="指纹信息已存在，无需重复录入"){
// 							clearInterval(pageTimer["finger"]);
							//去服务器中查找当前用户的特征值，然后保存起来
							var identityCard = $("#identityCard").val();
							$.ajax({
								type:"post",
								url:"<%=basePath%>/visitorAction!fingFingerByIdentity.action",
								data:{
									"idCard":identityCard,
									"pageStatus":"finger"
								},
								dataType:"json",
								success:function(data){
									if(data!=null){
										$("#fingerPrint").val(data.fingerprint);
										$("#fingerId").val(data.fingerId);
										clearInterval(pageTimer["finger"]);
										$(".threeClassIcon").addClass("successClassIcon");
										$("#fingerPic")[0].src="<%=basePath%>/img/finger.png";
									    $("#fingerModal").modal('hide');
									    $("#submitBtn").val("身份采集完成5秒后进入下一步");
									    pageTimer["submitPage"] = setInterval(dijian,1000);
									    var number = 5;
										function dijian(){
											if(number<=0){
												clearInterval(pageTimer["submitPage"]);
											    submitForm();
											}
											$("#submitBtn").val("身份采集完成"+number+"秒后进入下一步");
											console.log("submitPage:"+number);
											number--;
										}
									}else{
										$("#showMessage").html("指纹录入失败，请换根手指重新录入");
										$("#showModal").modal('show');
										$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
										$('#identityModal').modal('hide');
										clearInterval(pageTimer["finger"]);
										jishi(false);
									}
								},
								error:function(XMLHttpRequest, textStatus, errorThrown) {
									$("#showMessage").html("加载已存在指纹异常，请刷新后重试："+XMLHttpRequest.status+"  "+XMLHttpRequest.readyState+"  "+textStatus);
									$("#showModal").modal('show');
									$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
									$('#identityModal').modal('hide');
									clearInterval(pageTimer["finger"]);
									jishi(false);
							    }
							});
						}
						
						$("#fingerInfo").text(data.info);
						if(data.info=="指纹采集成功"){
							clearInterval(pageTimer["finger"]);
							$("#fingerPrint").val(data.identition);
							$(".threeClassIcon").addClass("successClassIcon");
							$("#fingerPic")[0].src="<%=basePath%>/img/finger.png";
						    $("#fingerModal").modal('hide');
						    
						    $("#submitBtn").val("身份采集完成5秒后进入下一步");
						    pageTimer["submitPage"] = setInterval(dijian,1000);
						    var number = 5;
							function dijian(){
								if(number<=0){
									clearInterval(pageTimer["submitPage"]);
								    submitForm();
								}
								$("#submitBtn").val("身份采集完成"+number+"秒后进入下一步");
								console.log("submitPage:"+number);
								number--;
							}
// 							clearInterval(pageTimer["finger"]);
						}else if(data.info=="操作失败,请重新操作或换根手指操作"){
							clearInterval(pageTimer["finger"]);
							addFinger();
						}else{
							$("#fingerPrint").val(null);
							$("#fingerId").val(null);
						}
					}
				},
				error:function(XMLHttpRequest, textStatus, errorThrown) {
					clearInterval(pageTimer["finger"]);
					$("#showMessage").html("指纹识别异常："+XMLHttpRequest.status+"  "+XMLHttpRequest.readyState+"  "+textStatus);
					$("#showModal").modal('show');
					$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
					jishi(false);
					$('#identityModal').modal('hide');
			    }
			});
			
			
		}
	}
	
	function submitForm(){
		var identityCard = $("#identityCard").val();
		if(identityCard==null || identityCard==""){
			$("#submitBtn").val("请采集身份，谢谢");
			return false;
		}
		
		var canvas = document.getElementById("canvas");
		if(isCanvasBlank(canvas)){
			$("#submitBtn").val("请先拍照，谢谢");
// 			$("#showMessage").html("请拍照，谢谢");
// 			$("#showModal").modal('show');
// 			$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
// 			jishi(false);
// 			$('#identityModal').modal('hide');
			return false;
		}
		
		
		var finger = $("#fingerPrint").val();
		if(finger==null || finger==""){
			$("#submitBtn").val("请录入指纹，谢谢");
// 			$("#showMessage").html("请录入指纹，谢谢");
// 			$("#showModal").modal('show');
// 			$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
// 			jishi(false);
// 			$('#identityModal').modal('hide');
			return false;
		}
		$("#form").submit();
	}
	
	//判断canvas是否有值
	function isCanvasBlank(canvas) {
	    var blank = document.createElement('canvas');    
	    blank.width = canvas.width;    
	    blank.height = canvas.height;    
	    return canvas.toDataURL() == blank.toDataURL();
	}
	function openFingerModal(){
		var canvas = document.getElementById("canvas");
		if(isCanvasBlank(canvas)){
			$("#showMessage").html("请先拍照，谢谢");
			$("#showModal").modal('show');
			$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
			jishi(false);
			$('#identityModal').modal('hide');
			return;
		}
		addFinger();
		$('#picutreModal').modal('hide');
	    $("#fingerModal").modal('show');
	}
	
	function selectFollowList(){
		$("#form").attr("action","<%=basePath%>/visitorAction!addVisitorApply.action?pageStatus=select").submit();
	}
	
	function deleteVisitorByIdCard(idCard){
		var dateTime = $("#dateTime").val();
		$.ajax({
			type:"post",
			url:"<%=basePath%>/visitorAction!deleteVisitorByIdCard.action",
			data:{
				"idCard":idCard,
				"param":dateTime
			},
			dataType:"json",
			success:function(data){
				if(data!=null && (data==true || data=="true") ){
					$("#showMessage").html("删除成功");
					$("#showModal").modal('show');
					$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
					jishi(false);
					$('#identityModal').modal('hide');
				}else{
					$("#showMessage").html("删除失败");
					$("#showModal").modal('show');
					$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
					jishi(false);
					$('#identityModal').modal('hide');
				}
			},error:function(XMLHttpRequest, textStatus, errorThrown) {
				$("#showMessage").html("删除失败："+XMLHttpRequest.status+"  "+XMLHttpRequest.readyState+"  "+textStatus);
				$("#showModal").modal('show');
				$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
				jishi(false);
				$('#identityModal').modal('hide');
			}
		});
	}
	</script>
</html>