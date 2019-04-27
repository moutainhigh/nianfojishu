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
		<div id="gongneng" style="width: 100%; margin-top: 10px;">
			<div id="xitong"
				style="width: 100%; font-weight: bold; height: 50px;" align="left">
				<div
					style="float: left; width: 50%; padding-left: 30px; padding-top: 5px;"
					align="left">

				</div>
				<div style="float: left; width: 45%; padding-top: 5px;"
					align="right">
					<a href="" style="color: rgb(79, 77, 77)"><br /> </a>
				</div>
			</div>

			<div align="center">
				<h3>
					为${uses.name}添加指纹信息
					<br />
					<s:if test="successMessage!=null">
						<font color="red">${successMessage}</font>
					</s:if>
				</h3>
				<form action="FingerprintMgAction_add.action" method="post"
					onsubmit="return validate()">
					<table class="table">
						<tr style="width: 100%">
							<th align="center">
								姓名：
							</th>
							<th>
								<input id="id_1" type="hidden" name="fingerprintMg.users" value="${uses}" readonly="readonly"/>
								<input id="name1" type="text" name="fingerprintMg.name" value="${uses.name}" readonly="readonly"/>
							</th>
							<th align="center">
								部门：
							</th>
							<th>
								<input id="dept" type="text" name="fingerprintMg.dept" value="${uses.dept}" readonly="readonly"/>
							</th>
						</tr>
						<tr>
							<th align="center">
								工号：
							</th>
							<th>
								<input id="dept" type="text" name="fingerprintMg.code" value="${uses.code}" readonly="readonly"/>
							</th>
							<th colspan="1" align="center">
								手指类型：
							</th>
							<th>
								<SELECT id="fingerType" style="width: 153px;" name="fingerprintMg.fingerType">
									<option value="">
										请选择手指类型
									</option>
									<option value="左拇指">
										左拇指
									</option>
									<option value="左食指">
										左食指
									</option>
									<option value="左中指">
										左中指
									</option>
									<option value="左无名指">
										左无名指
									</option>
									<option value="左小指">
										左小指
									</option>
									<option value="右拇指">
										右拇指
									</option>
									<option value="右食指">
										右食指
									</option>
									<option value="右中指">
										右中指
									</option>
									<option value="右无名指">
										右无名指
									</option>
									<option value="右小指">
										右小指
									</option>
								</SELECT>
							</th>
						</tr>
						<tr>
							<th>
								添加至考勤机
							</th>
							<td colspan="3">
								<s:iterator value="accessEquipmentWei" id="accessE" status="pageStatus">
									<input type="checkbox" name="aceId" id="ace" value="${accessE.id}"/>${accessE.equipmentName}
									${pageStatus.index}
									<s:if test="(#pageStatus.index+1)%5==0"><br/></s:if>
								</s:iterator>
								
								<select id="botelv" style="width: 80px; height: 21px;">
									<option value="COM1" >COM1</option>
									<option value="COM2">COM2</option>
									<option value="COM3">COM3</option>
									<option value="COM4">COM4</option>
									<option value="COM5">COM5</option>
									<option value="COM6">COM6</option>
									<option value="COM7">COM7</option>
									<option value="COM8">COM8</option>
								</select>
								
							</td>
							<td style="display: none"><input type="text" value="F5010102010003F5" name="ml"></td>
						</tr>
						<tr>
							<td colspan="4" align="center">
								<input type="submit" value="添加(Add)"
									style="width: 80px; height: 30px;" />
							</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">
		
		 window.onload = function () {
	        document.getElementById('botelv').addEventListener('change',function(){
	           
	           var comname=this.value;
	        	$.ajax({
					url : "FingerprintMgAction_zhiwenopne.action",
					type : 'post',
					data : {
						"commName" :comname,
						"baudrate":19200
					},
					dataType : 'json',
					success : function(data) {
						alert(data.message);
					},
					error : function() {
						alert("服务器异常!");
					}
				});
	        },false);
	    }
		
	
	
		
		
	function validate() {
		if (!validateText("fingerType", "指纹类型")) {
			return false;
		}
	}

    
    
function validateText(id, textname) {
	var textValue = $.trim($("#" + id).val());
	if (textValue == null || textValue == "") {
		alert(textname + "不能为空");
		return false;
	}
	return true;
}
</script>
	</body>
</html>