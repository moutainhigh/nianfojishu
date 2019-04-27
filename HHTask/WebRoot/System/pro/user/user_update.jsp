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
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng"
			style="width: 100%; border: solid 1px #0170b8; margin-top: 10px;">
			<div id="xitong"
				style="width: 100%; font-weight: bold; height: 50px; "
				align="left">
				<div
					style="float: left; width: 50%; padding-left: 30px; padding-top: 5px;"
					align="left">
					
				</div>
				<div style="float: left; width: 45%; padding-top: 5px;"
					align="right">
				</div>
			</div>
			
			<div align="center">
				<form action="proUserAction!updateProUser.action" method="post"
					style="">
					<input id="id" name="proUser.id" type="hidden" value="${proUser.id}"/>
					<input id="proId" name="proUser.proId" type="hidden" value="${proUser.proId}"/>
					<br>
					<table border="0" width="100%" class="table">
						<tr>
							<td align="right">
								部门:
							</td>
							<td>
								<select id="dept" type="text" name="proUser.dept" value=""></select>
							</td>
							<td align="right">
								人员:
							</td>
							<td>
								<select id="userId" type="text" name="proUser.userId" value=""></select>
							</td>
							<td align="right">
								用户组:
							</td>
							<td>
								<select  id="userGroup" name="proUser.userGroup">
									<option value="项目成员">项目成员</option>
									<option value="项目相关">项目相关</option>
									<option value="项目负责人">项目负责人</option>
								</select>
							</td>	
						</tr>
						<tr>
							<td align="center" colspan="6">
								<input type="submit" value="修改"
									style="width: 100px; height: 50px;" />
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
	</body>
</html>
<script type="text/javascript">
$(function(){
	$.ajax({
		type: "get",
		dataType: "text",
        url: "DeptNumberAction!finAllDeptNumberForSetlect.action",
		async: false,
		success: function(data){
			var dept=data.split("|");
			for(var i=0;i<dept.length-1;i++){
				//var overtimeDeptItem=new Option(dept[i],dept[i]);
				$("<option value='"+dept[i]+"'>"+dept[i]+"</option>").appendTo("#dept");
    			//$("#overtimeDept").append(overtimeDeptItem); 
			}
		}
	});
	
	//级联查询出部门所对应的所有人员
	
	$.ajax( {
		url : "UsersAction!findUsersByDept.action",
		type : 'post',
		dataType : 'json',
		cache : false,//防止数据缓存
		async : false,	
		data : {
			deptName : '${proUser.user.dept}'
		},
		success : function(useradsfa) {
			$("#userId").empty();//清空
			$("<option value=''>请选择</option>").appendTo("#userId");
			$(useradsfa).each(function() {
				$("<option value='"
					+ this.id
					+ "'>"
					+ this.name+this.code
					+ "</option>").appendTo("#userId")});
		},
		error : function() {
			alert("服务器异常!");
		}
	});
	
	//级联查询出部门所对应的所有人员
	$("#dept").bind("change",function() {
		if ($("#dept").val() != "") {
			$.ajax( {
				url : "UsersAction!findUsersByDept.action",
				type : 'post',
				dataType : 'json',
				cache : false,//防止数据缓存
				data : {
					deptName : $("#dept").val()
				},
				success : function(useradsfa) {
					$("#userId").empty();//清空
					$("<option value=''>请选择</option>").appendTo("#userId");
					$(useradsfa).each(function() {
						$("<option value='"
							+ this.id
							+ "'>"
							+ this.name+this.code
							+ "</option>").appendTo("#userId")});
				},
				error : function() {
					alert("服务器异常!");
				}
			});
		}
	});
	
	$("#dept").find("option[value='${proUser.user.dept}']").attr("selected",true);
	$("#userId").find("option[value='${proUser.userId}']").attr("selected",true);
	$("#userGroup").find("option[value='${proUser.userGroup}']").attr("selected",true);
});
</script>
