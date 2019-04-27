<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link rel="stylesheet" type="text/css"
			href="<%=basePath%>/javascript/bootstrap-3.3.7-dist/css/bootstrap.min.css">
		<script src="<%=basePath%>/javascript/jquery/jquery-3.2.1.js"> </script> 
		<script type="text/javascript" src="<%=basePath%>/javascript/keyboard-CN/vk_loader.js"></script>
		<script type="text/javascript"
 			src="<%=basePath%>/javascript/bootstrap-3.3.7-dist/js/bootstrap.js"> </script> 
		<style type="text/css">
			i{
				color: red;
			}
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
				overflow: hidden;
			}
			#header{
				width: 100%;
				height: 25%;
			}
			#body{
				width: 100%;
				height: 65%;
			}
			#footer{
				width: 100%;
				height: 10%;
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
			body{
				background-color: #97BCE4;
				padding-right: 0px!important;
			}
			.itemClass{
				/* 选项*/
 			    border: 2px solid #ccc; 
				border-radius:25px;
				background-color: #529DD5;
			  	-webkit-border-radius: 30px;
				-moz-border-radius: 30px;
				-webkit-box-shadow: rgb(20,255,255) 0px 0px 15px;
				-moz-box-shadow: rgb(20,255,255) 0px 0px 15px;
				box-shadow: rgb(20,255,255) 0px 0px 15px;
				padding: 25px;
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
			h1{
				color: white;
			}
			.keyboardClass{
				margin-left: -15px;
			}
			.form-control{ 
				background-color: #97BCE4;
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
		
		<!-- 模态框结束 -->
		<div class="container">
            <div id="header">
				<div class="row" style="height: 40px;">
	            </div>
	            <div class="row clearfix">
	            
	            	<img src="<%=basePath%>/${companyInfo.logo}" height="70px;" alt="<%=basePath%>/${companyInfo.logo}">
					<br>
					<br>
					<h1 align="center">
						请完善您的信息
					</h1>
	            </div>
			</div>
            <div id="body">
            	<div class="row">
            		<div class="col-lg-2"></div>
	            	<div class="col-lg-8">
	            		<div class="row ">
		            		<div class="itemClass">
		            			<form action="<%=basePath%>/visitorAction!addVisitorApply.action" 
		            					autocomplete="off" class="form-horizontal" method="post" role="form" id="form" >
<!-- 		            				<input type="hidden" name="pageStatus" value="select" /> -->
<!-- 		            				<input type="text" name="visitorList[0].id" value="48" /> -->
<!-- 		            				<input type="text" name="visitorList[1].id" value="43" /> -->
<!-- 		            				<input type="text" name="visitorList[2].id" value="46" /> -->
<!-- 		            				<input type="submit" value="提交" /> -->
		            				<div class="form-group">
		            					<label for="visitorName" class="col-lg-2 form-label"> 姓名: <i>*</i></label>
		            					<div class="col-lg-10">
		            						<b>${visitor.visitorName}</b>
			            					<input class="form-control" readonly="readonly" id="visitorName" type="hidden"
			            								name="visitor.visitorName" value="${visitor.visitorName }">
		            					</div>
		            				</div>
		            				<div class="form-group">
		            					<label for="visitorName" class="col-lg-2 form-label"> 身份证号: <i>*</i></label>
		            					<div class="col-lg-10">
		            						<b>${visitor.identityCard}</b>
			            					<input class="form-control" readonly="readonly" id="idCard" type="hidden"
			            													name="visitor.identityCard" value="${visitor.identityCard}">
			            					<input type="hidden" name="visitor.fingerprint" value="${visitor.fingerprint}" id="fingerprint">
			            					<input type="hidden" name="visitor.fingerId" value="${visitor.fingerId}" id="fingerId">
						            		<input type="hidden" name="visitor.picture" value="${visitor.picture}" >
						            		<input type="hidden" name="visitor.visitorIdentityPic" value="${visitor.visitorIdentityPic}" >
		            					</div>
		            				</div>
		            				<div class="form-group">
		            					<label for="visitorName" class="col-lg-2 form-label "> 来访单位:</label>
		            					<div class="col-lg-10">
			            					<input name="visitor.visitorComp" class="form-control search_shuru1" placeholder="请输入来访单位名称" id="visitorComp" 
			            						onfocus="openKeyBoard('visitorComp','softkeyvisitorComp');" 
			            						onBlur="VirtualKeyboard.toggle('visitorComp','softkeyvisitorComp');">
			            					<div id="softkeyvisitorComp" class="keyboardClass"></div>
		            					</div>
		            				</div>
		            				<div class="form-group">
		            					<label for="visitorPhone" class="col-lg-2 form-label"> 来访人手机号: <i>*</i></label>
		            					<div class="col-lg-10">
			            					<input name="visitor.visitorPhone" class="form-control search_shuru1"
			            					 placeholder="请输入手机号" id="visitorPhone" 
												onkeydown="settingNumbersVal(this)"			            					 	
			            					 	onchange="mustNumbers(this);"
			            						onfocus="openKeyBoard('visitorPhone','softkeyVisitorPhone');" 
			            						onBlur="VirtualKeyboard.toggle('visitorPhone','softkeyVisitorPhone');">
			            					<div id="softkeyVisitorPhone" class="keyboardClass"></div>
		            					</div>
		            				</div>
		            				<div class="form-group">
		            					<label for="dateTime" class="col-lg-2 form-label"> 来访日期: <i>*</i></label>
		            					<div class="col-lg-10">
			            					<input name="visitor.dateTime" class="form-control search_shuru1" id="dateTime" size="10" value="${visitor.dateTime }"
			            						onfocus="openKeyBoard('dateTime','softkeydateTime');" 
			            						onBlur="VirtualKeyboard.toggle('dateTime','softkeydateTime');">
			            					<div id="softkeydateTime" class="keyboardClass"></div>
		            					</div>
		            				</div>
		            				<div class="form-group">
		            					<label for="visitorPhone" class="col-lg-2 form-label">被访人手机号:<i>*</i></label>
		            					<div class="col-lg-10">
			            					<input name="visitor.intervieweePhone" class="form-control search_shuru1" placeholder="请输入被访人手机号" 
			            							id="intervieweePhone" 
			            							onkeydown="settingNumbersVal(this)"			            					 	
			            					 		onkeyup="return mustNumbers(this);"
			            							onfocus="openKeyBoard('intervieweePhone','softkeyintervieweePhone');" 
			            							onBlur="VirtualKeyboard.toggle('intervieweePhone','softkeyintervieweePhone');getInterviewee(this);">
			            					<div id="softkeyintervieweePhone" class="keyboardClass"></div>
		            					</div>
		            				</div>
		            				<div class="form-group">
		            					<label for="dept" class="col-lg-2 form-label"> 被访人: <i>*</i></label>
		            					<div class="col-lg-10">
		            						<input type="text" class="form-control" readonly="readonly" name="visitor.interviewee" id="interviewee"
		            							placeholder="输入被访人手机号自动加载被访人">
		            						<input type="hidden" readonly="readonly" name="visitor.intervieweeId" id="intervieweeId">
		            					</div>
		            				</div>
		            				
		            				<div class="form-group">
		            					<label for="visitorCause" class="col-lg-2 form-label search_shuru1"> 来访缘由:<i>*</i></label>
		            					<div class="col-lg-10">
		            						<textarea name="visitor.visitorCause" class="form-control" id="visitorCause" rows="3"
		            							onfocus="openKeyBoard('visitorCause','softkeyvisitorCause');" 
			            						onBlur="VirtualKeyboard.toggle('visitorCause','softkeyvisitorCause');"></textarea>
			            					<div id="softkeyvisitorCause" class="keyboardClass"></div>
		            					</div>
		            				</div>
		            			</form>
		            		</div>
		            		
	            		</div>
		            </div>
	            </div>
	            <div class="row">
	            	<div class="col-lg-12 text-center">
						<br>
						<button type="button" class="btn btn-default" onclick="check()" id="submitBtn"
							style="width: 30%; height: 90px;font-size: 25px;">下一步</button>
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
	<script type="text/javascript">
		var myVal;
		$(function(){
// 			$.ajax({
// 				type:"post",
// 				url:"<%=basePath%>/visitorAction!findAllDeptByIsVisitor.action",
// 				dataType:"json",
//                 async : false,
// 				success:function(data){
// 					if(data!=null){
// 						$("#dept option").remove();
// 						$("#dept").append("<option value='-1'>请选择部门</option>");
// 						for(var i=0;i<data.length;i++){
// 							$("#dept").append("<option value='"+data[i]+"'>"+data[i]+"</option>");
// 						}
// 					}
// 				},error:function(){
// 					alert("系统异常");
// 				}
// 			});
			$('#showModal').on('hide.bs.modal', function () {
				clearInterval(myVal);
			});
			
		});
		
		function changeDept(obj){
			var deptName = obj.value;
			if(deptName!=null && deptName!=""){
				$.ajax({
					type:"post",
					url:"<%=basePath%>/visitorAction!findUsersByDept.action",
					data:{
						"deptName":deptName
					},
	                async : false,
					dataType:"json",
					success:function(data){
						$("#intervieweeId option").remove();
						$("#intervieweeId").append("<option>请选择被访人</option>");
						if(data!=null){
							for(var i=0;i<data.length;i++){
								$("#intervieweeId").append("<option value='"+data[i].id+"'>"+data[i].name+"</option>");
							}
						}
					}
				});
			}
		}
		
		function changeInterviewee(obj){
			var usersId = obj.value;
			
			var usersName = obj.options[obj.selectedIndex].text;
			$("#interviewee").val(usersName);
		}
		function check(){
			
			var visitorName = $("#visitorName").val();
			if(visitorName==null || visitorName==""){
				$("#visitorName").attr("placeholder","姓名不能为空");
				$("#visitorName").css('borderColor','red');
				flag = false;
				return false;
			}else{
				$("#visitorName").css('borderColor','');
			}
			var phone = $("#visitorPhone").val();
			var myreg=/^[1][3,4,5,7,8][0-9]{9}$/;
			var flag = true;
			if(phone==null || phone==""){
				$("#visitorPhone").attr("placeholder","手机号不能为空");
				$("#visitorPhone").css('borderColor','red');
				flag = false;
				return false;
			}else if(!myreg.test(phone)){
				$("#visitorPhone").attr("placeholder","手机号格式不正确");
				$("#visitorPhone").css('borderColor','red');
				flag = false;
				return false;
			}else{
				$("#visitorPhone").css('borderColor','');
			}
			
			var interviewee = $("#interviewee").val();
			if(interviewee==null || interviewee==""){
				$("#intervieweePhone").append("请输入被访人手机号，或者手机号不正确");
				$("#intervieweePhone").css('borderColor','red');
				flag = false;
				return false;
			}else{
// 				var intervieweeId = $("#intervieweeId").val();
// 				if(intervieweeId==null || intervieweeId=="" || intervieweeId<=0 || isNaN(intervieweeId)){
// 					$("#intervieweeId").append("<option selected='selected'>请选择被访人</option>");
// 					$("#intervieweeId").css('borderColor','red');
// 					flag = false;
// 				}else{
					$("#intervieweePhone").css('borderColor','');
// 				}
			}
			
			var dateTime = $("#dateTime").val();
			if(dateTime==null || dateTime==""){
				$("#dateTime").attr("placeholder","日期不能为空");
				$("#dateTime").css('borderColor','red');
				flag = false;
				return false;
			}else{
				if(RQcheck(dateTime)){
					$("#dateTime").css('borderColor','');
				}else{
					$("#dateTime").attr("placeholder","日期格式不正确");
					$("#dateTime").css('borderColor','red');
					flag = false;
					return false;
				}
			}
			var visitorCause = $("#visitorCause").val();
			if(visitorCause==null || visitorCause==""){
				$("#visitorCause").attr("placeholder","来访缘由不能为空");
				$("#visitorCause").css('borderColor','red');
				flag = false;
				return false;
			}else{
				$("#visitorCause").css('borderColor','');
			}
			if(flag){
				$("#submitBtn").val("请稍后，正在进行提交中...");
				$("#submitBtn").attr('disabled', true);
				$("#showMessage").html("请稍后，正在进行提交中...");
				$("#showModal").modal("show");
				$("#form").submit();
// 				var form = new FormData(document.getElementById("form"));
// 				$.ajax({
// 					type:"post",
// 					url:"<%=basePath%>/visitorAction!addVisitorApply.action",
// 					data:form,
// 					processData:false,
// 					contentType:false,
// 					async : false,
// 					dataType:"json",
// 					success:function(data){
// 						if(data!=null&& data.success==true){
// 							//添加指纹到指纹库
// 							$("#visitorId").val(data.data.id);
// 							var fingerprint = $("#fingerprint").val();
// 							$.ajax({
// 								type:"post",
<%-- 								url:"http://localhost:8888/finger/saveFingerDataBase", --%>
// 								data:{
// 									"fingerId":data.data.fingerId,
// 									"fingerprint":fingerprint
// 								},
// 								dataType:"jsonp",
// 								success:function(xiafaData){
// 									$("#submitBtn").val("提交");
// 									$("#submitBtn").attr('disabled', false);
// 									console.log(xiafaData);
// 									$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second'>10</span>)</button>");
// 									jishi(true);
// 								},error:function(XMLHttpRequest, textStatus, errorThrown){
// 									$("#submitBtn").val("提交");
// 									$("#submitBtn").attr('disabled', false);
// 									$("#showMessage").html("系统添加指纹库异常，请联系管理员："+XMLHttpRequest.status+"  "+XMLHttpRequest.readyState+"  "+textStatus);
// 									$("#showModal").modal("show");
// 									$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second'>10</span>)</button>");
// 									jishi(false);
// 								}
// 							});
// 						}else{
// 							$("#submitBtn").val("提交");
// 							$("#submitBtn").attr('disabled', false);
// 							$("#showMessage").html(data.message);
// 							$("#showModal").modal('show');
// 							$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second'>10</span>)</button>");
// 							jishi(false);
// 						}
// 					},
// 					error:function(e){
// 						$("#submitBtn").val("提交");
// 						$("#submitBtn").attr('disabled', false);
// 						$("#showMessage").html('系统提交异常，请联系管理员');
// 						$("#showModal").modal('show');
// 						$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second'>10</span>)</button>");
// 						jishi(false);
// 					}
// 				});
			}
		}
		
		function RQcheck(date) {
           //(-|\/)分隔符
            var result = date.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
            if (result == null)
                return false;
            var d = new Date(result[1], result[3] - 1, result[4]);
            return (d.getFullYear() == result[1] && (d.getMonth() + 1) == result[3] && d.getDate() == result[4]);

        }
		
		var returnStyle="";
		function jishi(flag){
			myVal = setInterval(dijian,1000);
			function dijian(){
				
				if(flag){
					//查看监听
					$.ajax({
						type:"post",
						url:"http://localhost:8888/finger/getFingerStatus",
						dataType:"jsonp",
						success:function(data){
							if(data!=null && (data.info=="下发成功" || data.info.indexOf("已存在")>0) ){
								//30秒后跳转到主页（或者点击返回到主页）
								flag =false;
								returnStyle = "index";
								$("#showModal").modal("show");
								$("#showMessage").html("申请成功，请耐心等待，我们将以短信的方式提醒您，感谢您的使用。");
								$("#returnBtn").html("<button class='btn btn-default' onclick='location.href=\"<%=basePath%>/visitorAction!toVisitorIndex.action\"'>返回到主页(<span id='second'>10</span>)</button>");

								$("#submitBtn").val("已提交");
								$("#submitBtn").attr('disabled', false);
// 							}else if(data!=null && data.info!="" && data.info.indexOf("已存在")>0){
// 								var visitorId = $("#visitorId").val();
// 								$.ajax({
// 									type:"post",
// 									url:"<%=basePath%>/visitorAction!fillFingerByVisitorId.action",
// 									data:{
// 										"id":visitorId
// 									},
// 									dataType:"json",
// 									success:function(fillData){
// 										if(fillData!=null){
// 											clearInterval(myVal);
// 											$("#showModal").modal("show");
// 											$("#showMessage").html("申请成功，请耐心等待，我们将以短信的方式提醒您，感谢您的使用。");
// 											$("#returnBtn").html("<button class='btn btn-default' onclick='location.href=\"<%=basePath%>/visitorAction!toVisitorIndex.action\"'>返回到主页(<span id='second'>10</span>)</button>");
// 										}else{
// 											clearInterval(myVal);
// 											$("#showModal").modal("show");
// 											$("#showMessage").html("指纹库填充异常，指纹库已存在，请联系管理员。");
// 											$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second'>10</span>)</button>");
// 										}
// 									},error:function(){
// 										clearInterval(myVal);
// 										$("#showMessage").html("填充指纹库异常，请联系管理员："+XMLHttpRequest.status+"  "+XMLHttpRequest.readyState+"  "+textStatus);
// 										$("#showModal").modal("show");
// 										$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second'>10</span>)</button>");
// 									}
// 								});
							}else if(data!=null && data.info!=""){
								clearInterval(myVal);
								$("#showMessage").html(data.info);
								$("#showModal").modal("show");
								$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second'>10</span>)</button>");
								$("#submitBtn").val("提交");
								$("#submitBtn").attr('disabled', false);
							}
						},error:function(){
							clearInterval(myVal);
							$("#showMessage").html("系统监听指纹库异常，请联系管理员："+XMLHttpRequest.status+"  "+XMLHttpRequest.readyState+"  "+textStatus);
							$("#showModal").modal("show");
							$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second'>10</span>)</button>");
							$("#submitBtn").val("提交");
							$("#submitBtn").attr('disabled', false);
						}
					});
				}
				
				var numberVal = $("#second").text();
				var number = parseInt(numberVal)-1;
				if(number<=0){
					clearInterval(myVal);
					$('#showModal').modal('hide')
					if(flag || returnStyle=="index"){
						previousPage();//location.href="<%=basePath%>/visitorAction!toVisitorIndex.action";
					}
				}
				$("#second").text(number);
				console.log(number);
			}
		}
		
		function previousPage(){
			if(returnStyle!=null && returnStyle!="" && returnStyle=="index"){
				location.href="<%=basePath%>/visitorAction!toVisitorIndex.action";
			}else{
				location.href="<%=basePath%>/visitorAction!toVisitorIndex.action";
			}
		}
		
		//根据手机号加载员工
		function getInterviewee(obj){
			var phone = obj.value;
			if(phone!=null && phone!="" && phone.length>7){
				$.ajax({
					type:"POST",
					url:"<%=basePath%>/visitorAction!getIntervieweeByPhone.action",
					data:{
						param:phone
					},
					dataType:"json",
					success:function(data){
						if(data!=null){
							$("#interviewee").val(data.name);
							$("#intervieweeId").val(data.id);
						}else{
							$("#showModal").modal("show");
							$("#showMessage").html("<p class='text-center'>对不起，没有查询到被访人信息</p><p class='text-center'>请重新输入被访人信息</p>");
							$("#returnBtn").html("<button class='btn btn-default' data-dismiss='modal'>关闭(<span id='second'>10</span>)</button>");
							$("#submitBtn").val("提交");
							$("#interviewee").val("");
							$("#intervieweeId").val("");
							jishi(false);
						}
					},error:function(){
						alert("加载被访人异常，请联系管理员");
					}
				});
			}
		}
		
		//打开键盘
		function openKeyBoard(idName,showName){
			VirtualKeyboard.toggle(idName, showName);
			$("#kb_langselector,#kb_mappingselector,#copyrights").css("display", "none");
		}
		
		var oldNumber="";
		function settingNumbersVal(obj){
			oldNumber = obj.value;
		}
		function mustNumbers(obj){
			var number=obj.value;
			console.log(number);
			if(isNaN(number)){
				obj.value=oldNumber;
				return false;
			}else{
				return true;
			}
		}
	</script>
</html>