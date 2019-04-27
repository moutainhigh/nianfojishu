<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="/WEB-INF/fenye.tld" prefix="fenye"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<%@include file="/util/sonHead.jsp"%>
		<link rel="stylesheet"
			href="${pageContext.request.contextPath}/javascript/layer/theme/default/layer.css">
		<script type="text/javascript"
			src="${pageContext.request.contextPath}/javascript/layer/layer.js">
</script>
	</head>
	<body >
		<h3></h3>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng">

			<div align="center">
				<h4>
					文件受控升版
				</h4>

				<form
					action="systemFileAction_shengBan.action"
					enctype="multipart/form-data" method="post"
					onsubmit="return validate()" id="ztree">
					<table class="table">
						<tr>
							<th align="right">
								文件类型
								<input name="tags" value="tags" type="hidden"/>
							</th>
							<td>
								<select name="systemFile.fileType" id = "fileType">
									<option value="${systemFile.fileType}">
										${systemFile.fileType}
									</option>
								</select>
							</td>
							<th align="right">
								文件等级
							</th>
							<td>
								<select name="systemFile.fileLevel" id="fileLevel">
									<option value="${systemFile.fileLevel}">
										${systemFile.fileLevel}
									</option>
								</select>
							</td>
						</tr>
						<tr>
							<th align="right">
								文件名称
							</th>
							<td colspan="1">
								<input id="fileName" name="systemFile.fileName" value="${systemFile.fileName }"
									style="width:200px;height: 30px;" readonly="readonly"/>
							</td>
							
						</tr>
						<tr>
							<th align="right">
								文件编号
							</th>
							<td>
								<input name="systemFile.fileNo" type="fileNo" id="fileNo" readonly="readonly"
									value="${systemFile.fileNo}" style="width:200px;height: 30px;"/>
							</td>
							<th align="right">
								文件版本号
							</th>
							<td>
								<input type="text" name="systemFile.banben" id="banben" placeholder="${systemFile.banben}"
									style="width:200px;height: 30px;"/>
							</td>
						</tr>
						<tr>
							<th align="right">
								所属部门
							</th>
							<td colspan="1">
								<SELECT id="dept" class="cxselet" name="systemFile.department">
									<option value="${systemFile.department}">
										${systemFile.department}
									</option>
								</SELECT>
							</td>
							<th align="right">
								产品编码
							</th>
							<td>
								<input type="text" name="systemFile.cpCode"
									value="${systemFile.cpCode}" style="width:200px;height: 30px;">
							</td>
						</tr>
						<tr>

							<th align="right">
								上传文件
							</th>
							<td colspan="3">
								<s:if test="tags=='hetong'">
									<input type='file' name='attachment' multiple="multiple">
								</s:if>
								<s:else>
									<input type="file" name="sys" id="sys" />
								</s:else>
								<div id="showFiles">
									<!-- js获取值时使用 -->
									<input type="hidden" id="fileInput" value="${systemFile.fileUrl}">
									<input type="hidden" id="fileNameInput" value="${systemFile.otherName }" />
								</div>
							</td>
						</tr>
						<tr>
							<th align="right">文件更改单</th>
							<td colspan="3">
								<input type="file" name="loggingFile" >
								<div >
									历史更改单：
									<br>
									<a href="#">${systemFile.loggingFile }</a>
								</div>
							</td>
						</tr>
						<tr>
							<th align="right">
								文件描述
							</th>
							<td colspan="3">
								<textarea rows="8" cols="80" name="systemFile.description">${systemFile.description}</textarea>
							</td>
						</tr>
						<tr>
							<th align="right">
								文件管控审批人员
							</th>
							<td colspan="3">
								<div id="freeDeptDiv">
									<font color="red">文件管控审批人员:</font>
									<input type="hidden" value="" name="uidsAndLevels" id="uidsAndLevels">
									<hr />
									<div>
										<div >
											<div>
												<div style="float: left; " align="center">
													<input type="text" id="searchDeptInput"style="width: 120px;"
															  placeholder="搜索部门" onkeyup="searchDept()">
													<br>
													<select id="userGroup" name="" style="width: 120px;" size="15" onchange="changeDept()">
														<option value="">
															请选择部门
														</option>
														
													</select>
												</div>
												<div style="float: left; ">
													<input type="text" id="searchperson"style="width: 120px;"
														 onkeyup="changeDept()" placeholder="搜索审批人">
													<br>
													<select id="person" name="" style="width: 120px;" size="15">
														<option>
															选择审批人
														</option>
													</select>
												</div>
												
												<div id="allLevel" style="float: left; width: 50%;" align="left">
												<div>
													<input type="button" value="添加审核等级" class="input"
														style="width: 100px;" onclick="addLevel()" />
													<input type="button" value="删除审核等级" class="input"
														style="width: 100px;" onclick="delLevel()" />
												</div>
												<div id="levelDiv1">
													<div style="float: left; padding-top: 10px;">
														<input type="button" value="------->"
															onclick="getPerson(1,this)" />
														1级
														<input type="hidden" name="" value="1" />
														<br />
														<input type="button" value="<-------"
															onclick="backPerson(1,this)" />
													</div>
													<div style="float: left;">
														<select id="level1" style="width: 100px;" size="3"></select>
														<span id="addStatus1" style="color: red;"> </span>
													</div>
													<div style="clear: both;">
													</div>
													<br />
												</div>
											</div>
											</div>
										</div>
									</div>
										
<!-- 									<input type="button" value="增加" onclick="addFreeDept()" -->
<!-- 										style="width: 60px; height: 30px"> -->
<!-- 									<ul id="freeDeptUl0"> -->
<!-- 										<li id="freeDeptli0"> -->
<!-- 											<SELECT id="zrdept0" name="approvalId" -->
<!-- 												onchange="changefreeDept(0)"></SELECT> -->
<!-- 											<SELECT id="zrpeople0" name="ids"></SELECT> -->
<!-- 											<input type="button" value="删除" onclick="deleteFreeDept(0)" -->
<!-- 												style="width: 60px; height: 30px"> -->
<!-- 										</li> -->
<!-- 									</ul> -->
								</div>
							</td>
						</tr>
						<tr>
							<th align="right">
								推送人员
								<br />
								<br />
								<input id="test2" type="button" value="选择推送人员">
							</th>
							<td colspan="3">
								<input id="fid" name="systemFile.personToLookId"
									value="${systemFile.personToLookId}" readonly="readonly"
									type="hidden" />
								<textarea id="tishiPerson" name="systemFile.personToLook"
									rows="5" cols="80" readonly="readonly">${systemFile.personToLook}</textarea>
							</td>
						</tr>
						<tr>
							<td colspan="4" align="center">
								<input id="submitBtn" type="submit" value="升版" class="input">
							</td>
						</tr>
					</table>
				</form>
			</div>
			<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		</center>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">
$(function() {
	$("#fileType").tinyselect();
	$("#fileLevel").tinyselect();
	$("#dept").tinyselect();
	var oldloggingFile =  $("#oldloggingFile").val();
	if(oldloggingFile!=null && oldloggingFile!=""){
		$("#showOldLoggingFile").append();
	}else{
		$("#showOldLoggingFile").hide();
	}
});

$(function() {
	$.ajax( {
		type : "post",
		url : "GzstoreAction_getdept.action",
		dataType : "json",
		success : function(data) {
			//填充部门信息
			$(data).each(function() {
				var html = "";
				if (this.dept == "${Users.dept}") {
					html = "<option selected='selected' value='"
							+ this.id + "'>" + this.dept + "</option>";
				} else {
					html = "<option value='" + this.id + "'>"
							+ this.dept + "</option>";
				}
				$(html).appendTo("#userGroup");
			});
			changeDept();
		}
	});
	
	
	
});

$(function(){
	//显示查看
	var fileInput = $("#fileInput").val();
	if(fileInput!=null && fileInput!=""){
		var str = "<p>历史文件：</p>";
		if("合同类"==$("#fileType").val()){
			var suffix = fileInput.split(",");
			var fileName = $("#fileNameInput").val().split(",");
			for(var i=0;i<suffix.length;i++){
				var subSuffix = suffix[i].substring(suffix[i].indexOf("."));
				str+="<p id='pfile"+i+"'>文件"+(i+1)+"：<a href='${pageContext.request.contextPath}/FileViewAction.action?"+
						"FilePath=/upload/file/sysFile/"+suffix[i]+"'>"+fileName[i]+"</a>&nbsp;&nbsp;&nbsp;"+
						"<input type='hidden' value='"+suffix[i]+"' name='oldFiles'>"+
						"<input type='hidden' value='"+fileName[i]+"' name='oldFileNames'>"+
						"<a href='#' onclick='deleteFile("+i+")' >删除</a></p>";
			}
		}else{
			var suffix = fileInput.substring(fileInput.indexOf("."));
			//alert(suffix);
			str+="<a href='${pageContext.request.contextPath}/FileViewAction.action?FilePath=/upload/file/sysFile/"+fileInput+"'>"+fileInput+"</a><br>";
<%--			if(suffix.indexOf("jpg")<0 && suffix.indexOf("pdf")<0 && suffix.indexOf("jpg")<0){--%>
<%--				str+="&nbsp;<a href='${pageContext.request.contextPath}/upload/file/sysFile/"+fileInput+"'>下载</a>";--%>
<%--			}--%>
		}
		$("#showFiles").append(str);
	}
	
});

function changeDept(){
	var deptId = $("#userGroup").val();
	var searchperson = $("#searchperson").val();
	if (deptId > 0) {
		$.ajax( {
			type : "post",
			url : "GzstoreAction_getusers.action",
			dataType : "json",
			data : {
				id : deptId,
				"variable":searchperson
			},
			async : false, 
			success : function(data) {
				//填充部门信息
				$("#person").empty();
				$("#person").append("<option>选择审批人</option>");
				$(data).each(function() {
					if(this!=null&& this.name!=null && this.name!=""){
						var html = "<option value='" + this.id + "'>"
							+ this.name + "</option>";
						$(html).appendTo("#person");
					}
				});
	
			}
		});
	}
}

//右移
function getPerson(id, obj) {
	var personVal = $("#person option:selected");
	if (personVal.val() > 0) {
		var userId = personVal.val();
		var userName = personVal.text();
		var a = true;
		var levels = $("#level"+id+" option").each(function(){
			var uid = $(this).val();
			if(parseInt(uid)==parseInt(userId)){
				alert("选择审批人重复");
				a = false;
				return false;
			}
		});
		if(a){
			$("#level"+id).append("<option value='"+userId+"'>"+userName+"</option>");
		}
	} else if (personVal.val() == null) {
		alert("请选择人员!");
	} else {
		alert(personVal.val());
	}
}

//左移
function backPerson(id, obj) {
	var levelVal = $("#level" + id);
	if (levelVal.val() > 0) {
		var checkIndex = $("#level" + id).get(0).selectedIndex;//获取Select选择的索引值 
		var so = $("#level" + id + " option:selected");
		$("#person").append(so);
		//选中第一个option
		$("#level" + id).get(0).selectedIndex = 0;
	} else if (levelVal.val() == null) {
		alert("请选择人员!");
	}
}


//添加审核等级
var count = 1;
function addLevel() {
	count++;
	$("<div id='levelDiv"
			+ count
			+ "'>"
			+ "<div style='float: left; padding-top: 10px;'>"
			+ "<input type='button' value='------->' onclick='getPerson("
			+ count
			+ ",this)' /> "
			+ count
			+ "级"
			+ " <input type='hidden' name='' value='"
			+ count
			+ "' /><br />"
			+ "<input type='button' value='<-------' onclick='backPerson("
			+ count
			+ ",this)' />"
			+ "</div>"
			+ "<div style='float: left;'><select id='level"
			+ count
			+ "' style='width: 100px;' size='3'></select><span id='addStatus"
			+ count
			+ "' style='color: red;'></span></div><div style='clear: both;'></div><br /></div>")
	.appendTo("#allLevel");
}

//删除审核等级
function delLevel() {
	if (count == 1) {
		alert("就剩一个了,不能再删了!");
		return false;
	} else {
		//先删除该审核等级里面的人员
		var selectSize = $("#level" + count).get(0).options.length;
		if (selectSize == 0) {
			$("#levelDiv" + count).remove();
			count--;
		} else {
			alert("请先删除审核等级为" + count + "级的人员!");
		}
	}

}

function deleteFile(delId){
	$("#pfile"+delId).remove();
}

function changefreeDept(i) {
	var deptId = $("#zrdept" + i).val();
	if (deptId > 0) {
		$.ajax( {
			type : "post",
			url : "AskForLeaveAction!getDeptUsers.action",
			dataType : "json",
			data : {
				id : deptId
			},
			success : function(data) {
				//填充部门信息
			var selectbox = $("#freeDeptUl" + i + " .tinyselect");
			if (selectbox.length > 1) {
				var len = selectbox.length - 1;
				for ( var n = len; n >= 1; n--) {
					$(selectbox[n]).remove();
				}
			}
			$("#zrpeople" + i).empty();
			$(data).each(
					function() {
						var html = "<option value='" + this.userId + "'>"
								+ this.userName + "</option>";
						$(html).appendTo("#zrpeople" + i);
					});
			$("#zrpeople" + i).tinyselect();

		}
		});
	}
}
var deptIndex = 0;
function setDept(i) {
	$.ajax( {
		type : "post",
		url : "GzstoreAction_getdept.action",
		dataType : "json",
		success : function(data) {
			//填充部门信息
			$(data).each(
					function() {
						var html = "";
						if (this.dept == "${Users.dept}") {
							html = "<option selected='selected' value='"
									+ this.id + "'>" + this.dept + "</option>";
						} else {
							html = "<option value='" + this.id + "'>"
									+ this.dept + "</option>";
						}
						$(html).appendTo("#zrdept" + i);
					});
			changefreeDept(i);
			$("#zrdept" + i).tinyselect();
		}
	});
}
function changefreeDept(i) {
	var deptId = $("#zrdept" + i).val();
	if (deptId > 0) {
		$.ajax( {
			type : "post",
			url : "GzstoreAction_getusers.action",
			dataType : "json",
			data : {
				id : deptId
			},
			success : function(data) {
				//填充部门信息
			var selectbox = $("#freeDeptUl" + i + " .tinyselect");
			if (selectbox.length > 1) {
				var len = selectbox.length - 1;
				for ( var n = len; n >= 1; n--) {
					$(selectbox[n]).remove();
				}
			}
			$("#zrpeople" + i).empty();
			$(data).each(
					function() {
						var html = "<option value='" + this.id + "'>"
								+ this.name + "</option>";
						$(html).appendTo("#zrpeople" + i);
					});
			$("#zrpeople" + i).tinyselect();

		}
		});
	}
}
function addFreeDept() {
	deptIndex++;
	var html = "<ul id='freeDeptUl" + deptIndex + "'>" + "<li id='freeDeptli"
			+ deptIndex + "'>" + "<SELECT id='zrdept" + deptIndex
			+ "' name='approvalId' onchange='changefreeDept(" + deptIndex
			+ ")'></SELECT>" + "<SELECT id='zrpeople" + deptIndex
			+ "' name='ids'></SELECT>"
			+ "<input type='button' value='删除' onclick='deleteFreeDept("
			+ deptIndex + ")' style='width: 60px;height: 30px'>" + "</li></ul>"
	$(html).appendTo("#freeDeptDiv");
	setDept(deptIndex);
}
function deleteFreeDept(index) {
	$("#freeDeptUl" + index).remove();
}<%--	 function showDiv(){--%>
<%--		 document.getElementById("tree").style.display="";--%>
<%--	 }--%>
$('#test2').on('click', function(){
layer.open({
  type: 2,
  title: '选择推送人员',
  area: ['450px', '800px'],
  fixed: false, //不固定
  maxmin: true,
  content: '<%=basePath%>/System/systemfile/checkPerson.jsp'
});
});
$('#test1').on('click', function(){
layer.open({
  type: 2,
  title: '文件管控审批流程',
  area: ['900px', '700px'],
  fixed: false, //不固定
  maxmin: true,
  content: '<%=basePath%>/CircuitCustomize_findAuditNodeByCcId.action?id=632'
});
});
function validate(){
	var arr = [];
	var szIndex = 0;
	for(var i=1;i<=count;i++){
		var levels = document.getElementById("level"+i).options;
		if(levels==null|| levels.length==0){
			alert("审核等级："+i+"级为空！");
			return false;
		}else{
			 for (var j = 0; j < levels.length; j++){
			 		arr.push(i+":"+ levels[j].value);
        	 }
		}
		document.getElementById("uidsAndLevels").value = arr;
	}

	if (!validateText("fileType", "文件类型")) {
		return false;
	}
	if (!validateText("fileLevel", "文件等级")) {
		return false;
	}
	if (!validateText("fileName", "文件名称")) {
		return false;
	}
	if(!validateText("banben","版本号")){
		return false;
	}
	if (!validateText("fileNo", "文件编号")) {
		return false;
	}
	if(!validateText("sys","文件")){
		return false;
	}
	
}
function validateText(id, textname) {
	var textValue = $.trim($("#" + id).val());
	if (textValue == null || textValue == "") {
		$("#" + id).focus();
		alert(textname + "不能为空");
		return false;
	}
	return true;
}
function hetong(){
	var fileType = $("#fileType").val();
	if('合同类'==fileType){
		$("#jine").show();
		$("#money").show();
		$("#fileButton_1").show();
		//$("#sys").hide();
	}else{
		$("#jine").hide();
		$("#money").hide();
		$("#fileButton_1").hide();
		//$("#sys").show();
	}
}

function searchDept(){
	var searchDeptInput = $("#searchDeptInput").val();
	$.ajax( {
		type : "post",
		url : "GzstoreAction_getdept.action",
		dataType : "json",
		data:{
			"variable":searchDeptInput
		},
		success : function(data) {
			$("#userGroup").empty();
			$("#userGroup").append("<option>搜索部门</option>");
			//填充部门信息
			$(data).each(function() {
				var html = "";
				if (this.dept == "${Users.dept}") {
					html = "<option selected='selected' value='"
							+ this.id + "'>" + this.dept + "</option>";
				} else {
					html = "<option value='" + this.id + "'>"
							+ this.dept + "</option>";
				}
				$(html).appendTo("#userGroup");
			});
			changeDept();
		}
	});
}
	</script>
	</body>
</html>
