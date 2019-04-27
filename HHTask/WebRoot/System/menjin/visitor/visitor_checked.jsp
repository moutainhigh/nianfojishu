<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
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
			  padding: 80px 20px 0px 20px;;
			  margin-bottom: 0px;
			}
			.custom-variables-eng{
				--some-color:white;
			  --some-keyword: italic;
			  --some-size: 1.6em;
/* 			  --some-complex-value: 1px 1px 2px whitesmoke, 0 0 1em slategray, 0 0 0.2em slategray; */
			  color: var(--some-color);
			  font-size: var(--some-size);
/* 			  font-style: var(--some-keyword); */
			  text-shadow: var(--some-complex-value);
			  cursor: pointer;
			  padding: 0px 20px 45px 20px;
			}
			body{
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
			.modal-content{
				/* 选项*/
 			    border: 2px solid #ccc; 
				border-radius:25px;
				background-color: #529DD5;
			  	-webkit-border-radius: 30px;
				-moz-border-radius: 30px;
				-webkit-box-shadow: rgb(20,255,255) 0px 0px 15px;
				-moz-box-shadow: rgb(20,255,255) 0px 0px 15px;
				box-shadow: rgb(20,255,255) 0px 0px 15px;
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
	<body >
		<!-- 模态框（Modal） -->
		<div class="modal fade" id="identityModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
		    <div class="modal-dialog">
		        <div class="modal-content">
		            <div class="modal-header">
		                <h4 class="modal-title" id="myModalLabel">提醒消息</h4>
		            </div>
		            <div class="modal-body">
						<div class="row">
							<h3 class="text-center">请将居民身份证放置在下方识读区</h3>
						</div>
						<div class="row" >
							<div class="col-lg-2"></div>
							<div class="col-lg-8">
								<img src="<%=basePath%>/img/idcard.jpg" alt="请刷身份证"  style="width: 100%;" />
							</div>
							<div class="col-lg-2"></div>
						</div>
						<div class="row">
							<div class="alert alert-warning">
							    	<p>注：如刷身份证无反应，请与工作人员联系。</p>
							    	<p>本窗口将在<span id="second">15</span>秒后自动关闭。</p>
							</div>
						</div>
		            </div>
		            <div class="modal-footer">
		                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
		            </div>
		        </div>
		    </div>
		</div>
		
		<!-- 指纹模态框 -->
		<div class="modal fade" id="fingerModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
		    <div class="modal-dialog">
		    	<div class="modal-content">
		    		 <div class="modal-header">
		                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		                <h4 class="modal-title" id="myModalLabel">提醒消息</h4>
		            </div>
		            <div class="modal-body">
						<div class="row">
							<h3 class="text-center">请在屏幕下方进行指纹识别认证</h3>
						</div>
						<div class="row">
							<h3 class="text-center" style="color: red;" id="fingerMessage"></h3>
						</div>
		            </div>
		            <div class="modal-footer">
		                <button class="btn btn-default" data-dismiss="modal">关闭(<span id="fingerTime">15</span>)</button>
		            </div>
		        </div>
		    </div>
		</div>
		
		<!-- 凭证、二维码Modal -->
		<div class="modal fade" id="voucherModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
		    <div class="modal-dialog">
		    	<div class="modal-content">
		    		 <div class="modal-header">
		                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		                <h4 class="modal-title" id="myModalLabel">提醒消息</h4>
		            </div>
		            <div class="modal-body">
						<div class="row">
							<h3 class="text-center">请在屏幕下方扫描纸质二维码</h3>
							<input type="text" name="param" id="voucher" onkeyup="enterVoucher(this)" style="width: 0px;">
						</div>
		            </div>
		            <div class="modal-footer">
		                <button class="btn btn-default" data-dismiss="modal">关闭(<span id="voucherTime">15</span>)</button>
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
					<img src="<%=basePath%>/${companyInfo.logo}" height="70px;" 
						alt="<%=basePath%>/${companyInfo.logo}">
					<br>
					<h1 align="center">
						请选择认证方式
					</h1>
					<h2 align="center">Please select authentication mode</h2>
	            </div>
	            <div class="row" style="height: 40px;">
	            	<s:if test="errorMessage!=null && errorMessage!=''">
	            		<div class="col-lg-1"></div>
	            		<div class="col-lg-10">
		            		<div class="alert alert-danger" role="alert" id="errorMessage">
								<p class="m-0" align="center">${errorMessage}</p>
							</div>
	            		</div>
	            		<div class="col-lg-1"></div>
	            	</s:if>
	            </div>
			</div>
			<div id="body">
	            <div class="row ">
	            	<div class="col-lg-4"><!-- tanchuApply() -->
	                   	<div class="itemClass" onclick="tanchuCheck('identityCard')">
	                  		<p class="custom-variables text-center">身份证认证</p>
	                   		<p class="custom-variables-eng text-center" >Identification ID card</p>
	                    </div>
	                </div>
	                <div class="col-lg-4">
	                   	<div class="itemClass" onclick="showZhiWen();">
	                  		<p class="custom-variables text-center">指纹认证</p>
	                  		<p class="custom-variables-eng text-center" >Fingerprint authentication</p>
	                    </div>
	                </div>
	                <div class="col-lg-4">
	                   	<div class="itemClass" onclick="tanchuCheck('voucher')">
	                  		<p class="custom-variables text-center">凭证、二维码认证</p>
	                  		<p class="custom-variables-eng text-center" >Credential Recognition</p>
	                    </div>
	                </div>
				</div>
			</div>
			<div id="footer">
            	<div class="row">
            		<div class="text-left col-lg-12">
	            		<button  type="button" class="btn btn-default" onclick="location.href='<%=basePath%>/visitorAction!toVisitorIndex.action'"  id="returnGoHis">返回上一页</button>
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
	var pageTimer = {};//定义所有的定时器
	var portStatus = false;
	$(function(){
		setTimeout(function(){
			$("#errorMessage").hide('slow');
		},4000);
		//20秒后跳转到主页
		setTimeout(function(){
			location.href="<%=basePath%>/visitorAction!toVisitorIndex.action";
		},20000);
		
		//model 关闭事件
		$('#identityModal').on('hide.bs.modal', function () {
			clearInterval(pageTimer["identity"]);
		});
		$('#showModal').on('hide.bs.modal', function () {
			clearInterval(pageTimer["message"]);
			clearInterval(pageTimer["check"]);
			clearInterval(pageTimer["identity"]);
		});
		$('#voucherModal').on('hide.bs.modal', function () {
			clearInterval(pageTimer["message"]);
			clearInterval(pageTimer["check"]);
			clearInterval(pageTimer["identity"]);
			clearInterval(pageTimer["voucher"]);
		});
		$('#fingerModal').on('hide.bs.modal', function () {
			clearInterval(pageTimer["message"]);
			clearInterval(pageTimer["check"]);
			clearInterval(pageTimer["identity"]);
			clearInterval(pageTimer["voucher"]);
			clearInterval(pageTimer["finger"]);
		});
		
	});
	var fingerErrIndex = 0;
	
	function tanchuCheck(style){
		if(style=='identityCard'){//身份证认证
			$('#identityModal').modal('show');
			$("#second").text(15);
			pageTimer["identity"] = setInterval(dijian,1000);
			var numberIndex = 0;
			
			function dijian(){
				numberIndex++;
				var numberVal = $("#second").text();
				var number = parseInt(numberVal)-1;
				if(number<=0){
					clearInterval(pageTimer["identity"]);
					$('#identityModal').modal('hide');
					location.href="<%=basePath%>/visitorAction!toVisitorIndex.action";
				}
				$("#second").text(number);
				console.log("IdentitySecond:"+number);
				
				getIdentityCard(numberIndex,"check")
			}
		}else if(style=='finger'){//指纹认证
			$("#fingerModal").modal('show');
			$("#fingerTime").text(15);
			pageTimer["finger"] = setInterval(dijian,1000);
			var numberIndex = 0;
			
			//开启比对
			var dataList = [];
			$.ajax({
				type:"POST",
				url:"http://localhost:8888/finger/executeFingerDataBase",
				dataType:"jsonp",
				success:function(data){
// 					$("#fingerMessage").text("");
// 					clearInterval(pageTimer["finger"]);
// 					getLisStatus("finger");
				},error:function(XMLHttpRequest, textStatus, errorThrown){
					clearInterval(pageTimer["finger"]);
					$("#showMessage").html("开启比对指纹库异常，请联系管理员："+XMLHttpRequest.status+"  "+XMLHttpRequest.readyState+"  "+textStatus);
					$("#showModal").modal('show');
					$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
					jishi(false);
					$('#fingerModal').modal('hide');
				}
			});
			//获取今天所有可以进、出的指纹库
// 			var dataList = null;
// 			$.ajax({
// 				type:"post",
// 				url:"<%=basePath%>/visitorAction!getFingerDataByDateTime.action",
// 				dataType:"json",
// 				success:function(data){
// 				},error:function(XMLHttpRequest, textStatus, errorThrown){
// 					clearInterval(pageTimer["finger"]);
// 					$("#showMessage").html(data.info+"[获取指纹库异常，请联系管理员]");
// 					$("#showModal").modal('show');
// 					$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
// 					jishi(false);
// 					$('#fingerModal').modal('hide');
// 				}
// 			});
					//dataList = data;//将所有指纹信息添加到指纹库
					
			
			
			function dijian(){
				//在这里监听----------------------------------------------------------------------
				//clearInterval(pageTimer["finger"]);//清除指纹弹出框计时
				
				//获得监听指纹比对状态
				$.ajax({
					type:"post",
					url:"http://localhost:8888/finger/getFingerStatus",
					dataType:"jsonp",
					success:function(data){
						console.log(data.info);
						if(data!=null && data.info=="比对成功"){
							clearInterval(pageTimer["finger"]);//清除指纹弹出框计时
							
							$.ajax({
								type:"post",
								url:"<%=basePath%>/visitorAction!verifyInAndOut.action",
								data:{
									"param":data.identition,
									"pageStatus":"finger"
								},
								async : false,
								dataType:"json",
								success:function(data){
									$("#showModal").modal('show');
									if(data!=null&& (data.indexOf("成功")>0 || data.indexOf("正确")>0)){
										//15秒后跳转到主页（或者点击返回到主页）
										$("#showMessage").html("<span style='color:#00FF00'>"+data+"</span>");
										clearInterval(pageTimer["finger"]);
										$('#fingerModal').modal('hide');
										$("#returnBtn").html("<button class='btn btn-default' onclick='location.href=\"<%=basePath%>/visitorAction!toVisitorIndex.action\"'>返回到主页(<span id='second1'>10</span>)</button>");
										jishi(true);
									}else{
										$("#showMessage").html(data);
										clearInterval(pageTimer["finger"]);
										$('#fingerModal').modal('hide');
										$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
										jishi(false);
									}
								},
								error:function(e){
								    alert("系统异常，请联系管理员");
								}
							});
						}else if(data!=null && data.info=="无此用户"){
							fingerErrIndex++;
							$("#fingerMessage").text("指纹比对失败，请重试("+fingerErrIndex+")");
							clearInterval(pageTimer["finger"]);//清除指纹弹出框计时
							if(fingerErrIndex<=5){
								tanchuCheck("finger");
							}else{
								$("#fingerMessage").text("");
								$("#fingerModal").modal('hide');
							}
						}
					},error:function(XMLHttpRequest, textStatus, errorThrown){
						$("#showMessage").html("系统监听指纹库异常，请联系管理员："+XMLHttpRequest.status+"  "+XMLHttpRequest.readyState+"  "+textStatus);
						$("#showModal").modal("show");
						$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
						jishi(false);
					}
				});
				
				var numberVal = $("#fingerTime").text();
				var number = parseInt(numberVal)-1;
				if(number<=0){
					clearInterval(pageTimer["finger"]);
					$('#fingerModal').modal('hide');
					location.href="<%=basePath%>/visitorAction!toVisitorIndex.action";
				}
				$("#fingerTime").text(number);
				console.log("fingerTime:"+number);
				
				
			}
		}else if(style=='voucher'){//二维码认证
			$('#voucherModal').modal('show');
			$("#voucherTime").text(15);
			pageTimer["voucher"] = setInterval(dijian,1000);
			var numberIndex = 0;
			$("#voucher").val("");
			function dijian(){
				numberIndex++;
				var numberVal = $("#voucherTime").text();
				var number = parseInt(numberVal)-1;
				if(number<=0){
					clearInterval(pageTimer["voucher"]);
					$('#voucherModal').modal('hide');
				}
				$("#voucherTime").text(number);
				console.log("voucherTime:"+number);
				$("#voucher").focus();
			}
		}else if(style=='face'){//人脸识别
			
		}
		
		
	}
	
	function jishi(idName){
		pageTimer["message"] = setInterval(dijian,1000);
		function dijian(){
			var numberVal = $("#second1").text();
			var number = parseInt(numberVal)-1;
			if(number<=0){
				clearInterval(pageTimer["message"]);
				$('#showModal').modal('hide');
				$('#identityModal').modal('hide');
				$('#fingerModal').modal('hide');
				if(idName==true){
					location.href="<%=basePath%>/visitorAction!toVisitorIndex.action";
				}
			}
			$("#second1").text(number);
			console.log("second1:"+number);
		}
	}
	
	function getIdentityCard(numberIndex,pattern){
		if(numberIndex%5==0){
			return false;//五秒执行一次获取操作，目的因为跨域访问不能使用同步
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
						var identity= data.identtiy;
						if(identity!=null && identity!=""){
							$.ajax({
								type:"post",
								url:"<%=basePath%>/visitorAction!verifyInAndOut.action",
								data:{
									"param":identity,
									"pageStatus":"idCard"
								},
								async : false,
								dataType:"json",
								success:function(data){
									$("#showModal").modal('show');
									if(data!=null&& (data.indexOf("成功")>0 || data.indexOf("正确")>0)){
										//15秒后跳转到主页（或者点击返回到主页）
										$("#showMessage").html("<span style='color:#00FF00'>"+data+"</span>");
										$("#returnBtn").html("<button class='btn btn-default' onclick='location.href=\"<%=basePath%>/visitorAction!toVisitorIndex.action\"'>返回到主页(<span id='second1'>10</span>)</button>");
										jishi(true);
									}else{
										$("#showMessage").html(data);
										$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
										jishi(false);
									}
								},
								error:function(e){
								    alert("系统异常，请联系管理员");
								}
							});
						    return true;
						}else{
							return false;
						}
					}else if(data.status!=1){
						clearInterval(pageTimer["identity"]);
						$("#showMessage").html(data.info+"[获取身份失败，请重新放置卡片]");
						$("#showModal").modal('show');
						$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
						jishi(false);
						$('#identityModal').modal('hide');
					}
				}else{
					clearInterval(pageTimer["identity"]);
					$("#showMessage").html("本地项目异常，请联系管理员");
					$("#showModal").modal('show');
					$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
					jishi(false);
					$('#identityModal').modal('hide');
				}
			},
			error:function(XMLHttpRequest, textStatus, errorThrown) {
				clearInterval(pageTimer["identity"]);
				$("#showMessage").html("系统获取身份证异常："+XMLHttpRequest.status+"  "+XMLHttpRequest.readyState+"  "+textStatus);
				$("#showModal").modal('show');
				$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
				jishi(false);
				$('#identityModal').modal('hide');
		    }
		});
		return false;
	}
	
	//获取今天的可以进出的指纹信息
	function getFingerDataByDateTime(){
		$.ajax({
			type:"post",
			url:"<%=basePath%>/visitorAction!getFingerDataByDateTime.action",
			dataType:"json",
			success:function(data){
				
			},error:function(){
				
			}
		});
	}
	
	//扫描二维码
	function enterVoucher(obj){
		var voucher = obj.value;
		if(voucher!=null && voucher!="" && voucher.length==21){
			$.ajax({
				type:"post",
				url:"<%=basePath%>/visitorAction!verifyInAndOut.action",
				data:{
					"param":voucher,
					"pageStatus":"voucher"
				},
				async : false,
				dataType:"json",
				success:function(data){
					$("#showModal").modal('show');
					if(data!=null&& (data.indexOf("成功")>0 || data.indexOf("正确")>0)){
						//15秒后跳转到主页（或者点击返回到主页）
						$("#showMessage").html("<span style='color:#00FF00'>"+data+"</span>");
						$("#returnBtn").html("<button class='btn btn-default' onclick='location.href=\"<%=basePath%>/visitorAction!toVisitorIndex.action\"'>返回到主页(<span id='second1'>10</span>)</button>");
						$('#voucherModal').modal('hide');
						jishi(true);
					}else{
						$("#showMessage").html(data);
						$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second1'>10</span>)</button>");
						$('#voucherModal').modal('hide');
						jishi(false);
					}
				},
				error:function(e){
				    alert("系统异常，请联系管理员");
				}
			});
		}
	}
	
	function showZhiWen(){
		$("#fingerMessage").text("");
		fingerErrIndex=0;
		tanchuCheck('finger');
	}
	</script>
</html>
