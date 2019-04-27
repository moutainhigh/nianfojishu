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
		<title>发起设变</title>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng" style="width: 100%;">
			<div align="center">
				<form action="procardTemplateGyAction_addSbApply.action"
					method="post" enctype="multipart/form-data" onsubmit="disBrn();">
					<input name="bbAply.workItemId" type="hidden"
						value="${ecaFeedBackBean.workItemId}" />
					<input name="bbAply.ecType" value="${ecaFeedBackBean.ecType}"
						type="hidden" />
					<table class="table" id="mytable">
						<tr>
							<th>
								外部设变单号
							</th>
							<td>
								<input id="outbsNumber" name="bbAply.outbsNumber"
									value="${ecaFeedBackBean.ecnNumber}" onblur="changesource()" />
							</td>
							<th>
								设变来源
							</th>
							<td>
								<SELECT id="sbSource" name="bbAply.sbSource">
									<s:if test="ecaFeedBackBean.ecnNumber!=null">
										<option value="外部设变">
											外部设变
										</option>
									</s:if>
									<s:else>
										<option value="内部设变">
											内部设变
										</option>
										<option value="外部设变">
											外部设变
										</option>
									</s:else>
								</SELECT>
							</td>
							<th>
								设变类型
							</th>
							<td>
								<SELECT name="bbAply.sbType">
									<option>
										其他
									</option>
									<option>
										ECA变更单
									</option>
									<option>
										ECN变更单
									</option>
									<option>
										一次性设计问题单
									</option>
									<option>
										合同更改单
									</option>
									<option>
										临时技改单
									</option>
									<option>
										DocECN变更单
									</option>
									<option>
										计划ECO生效提醒
									</option>
									<option>
										计划ECO生失效时间维护单
									</option>
									<option>
										设计开发更改通知单
									</option>
									<option>
										加工问题反馈单
									</option>
									<option>
										软件升级
									</option>
								</SELECT>
							</td>
						</tr>
						<tr>
							<th>
								件号
							</th>
							<td>
								<input id="markId_0" name="bbAply.ptbbaList[0].markId"
									onblur="checkProcardTemplate('markId_0','0')"
									value="${ecaFeedBackBean.partDocNumber}" />
							</td>
							<th>
								业务件号
							</th>
							<td>
								<input id="ywMarkId_0" name="bbAply.ptbbaList[0].ywMarkId"
									value="${ecaFeedBackBean.partDocNumber}"
									onblur="checkProcardTemplate('ywMarkId_0','0')" />
							</td>
							<th>
								BOM类型
							</th>
							<td>
								<SELECT id="productStyle_0" name="bbAply.ptbbaList[0].productStyle"></SELECT>
								<input type="button" value="增加" id="" onclick="addLin()"/>
								<input type="button" value="删除" id="" onclick="delLin()"/>
							</td>
						</tr>
						<tr id="lastTr">
							<th>
								生产组别
							</th>
							<td>
								<input name="bbAply.aboutPlace">
							</td>
							<th>
								项目主管
							</th>
							<td colspan="5">
								<s:iterator value="list" id="ap">
									<input value="${ap.id}" type="radio" name="id"
										id="apid${ap.userName}" />
									<label for="apid${ap.userName}">
										${ap.userName}
									</label>
								</s:iterator>
							</td>
						</tr>
						<tr>
							<th>
								附件
							</th>
							<td>
								<input type="button" id="fileButton_1"
									onclick="uploadFile(this,1)" value="上传附件">
								<div id="fileDiv_1" style="display: none;">
								</div>
							</td>
							<th>
								设变原因
							</th>
							<td>
							<SELECT name="bbAply.sbreason">
									<option value="">
									</option>
									<option value="工艺错误">
										工艺错误
									</option>
									<option value="客户/研发要求">
										客户/研发要求
									</option>
									<option value="工艺改良">
										工艺改良
									</option>
									<option value="其他">
										其他
									</option>
								</SELECT>
							</td>
							</td>
							<th>
								备注
							</th>
							<td colspan="3">
								<textarea name="bbAply.remark" rows="4" cols="40">${ecaFeedBackBean.ecnName}</textarea>
							</td>
						</tr>
						<tr>
							<td colspan="6" align="center">
								<input id="subBtn" type="submit" value="提交"
									style="width: 80px; height: 60px;" />
							</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">
function checkProcardTemplate(tag,num) {
	if ($("#" + tag).val() == null || $("#" + tag).val() == "") {
		$("#markId_"+num).val("");
		$("#ywMarkId_"+num).val("");
		return;
	}
	var strs = tag.split('_');
	$.ajax( {
		type : "POST",
		url : "procardTemplateGyAction_getProcardTemplateMsg.action",
		dataType : "json",
		data : {
			type : strs[0],
			markId : $("#" + tag).val()
		},
		success : function(data) {
			if (data != null) {
				var i = 0;
				$(data).each(
						function() {
							if (i == 0) {
								$("#markId_"+num).val(this.markId);
								$("#ywMarkId_"+num).val(this.ywMarkId);
							}
							$(
									"<option value='" + this.productStyle
											+ "'>" + this.productStyle
											+ "</option>").appendTo(
									"#productStyle_"+num);
							i++;
						});

			}
		}
	});

}
function disBrn() {
	$("#subBtn").attr("disabled", "disabled");
}

function changesource() {
	var outbsNumber = $("#outbsNumber").val();
	if (outbsNumber != null && outbsNumber != "") {
		$("#sbSource").find("option[value='外部设变']").attr("selected", true);
	} else {
		$("#sbSource").find("option[value='内部设变']").attr("selected", true);
	}
}

$(function() {
	checkProcardTemplate("markId_0","0");
})

var fileDivHTML = "";
var count = 0;
function uploadFile(obj, few) {
	var fileDiv = document.getElementById("fileDiv_" + few);
	if (obj.value == "上传附件") {
		fileDiv.style.display = "block";
		obj.value = "添加文件";
	}
	fileDivHTML = "<div id='file"
			+ count
			+ "'><input type='file' name='attachment'><a href='javascript:delFile("
			+ count + "," + few + ")'>删除</a></div>";
	fileDiv.insertAdjacentHTML("beforeEnd", fileDivHTML);
	count++;
}
function delFile(obj, few) {
	document.getElementById("file" + obj).parentNode.removeChild(document
			.getElementById("file" + obj));
	count--;
	if (count <= 0) {
		count = 0;
		document.getElementById("fileButton_" + few).value = "上传附件";
		document.getElementById("fileDiv_" + few).style.display = "none";
	}
}
var index = 1;
function addLin(){
	var newTr = '<tr><th>件号</th><td><input id="markId_'+index+'" name="bbAply.ptbbaList['+index+'].markId" ' +
	' onblur="checkProcardTemplate(&apos;markId_'+index+'&apos;,'+index+')" value="${ecaFeedBackBean.partDocNumber}" /> </td> ' +
	' <th>业务件号</th><td><input id="ywMarkId_'+index+'" name="bbAply.ptbbaList['+index+'].ywMarkId" value="${ecaFeedBackBean.partDocNumber}" ' +
	' onblur="checkProcardTemplate(&apos;ywMarkId_'+index+'&apos;,'+index+')" /></td><th>BOM类型</th>' +
	'<td><SELECT id="productStyle_'+index+'" name="bbAply.ptbbaList['+index+'].productStyle"></SELECT></td></tr> ';
	$("#lastTr").before(newTr);
	checkProcardTemplate("markId_"+index ,index)
	index++;
}


function delLin(){
	if(index <=1){
		alert('已余最后一项，不可删除!~');
		return false;
	}	
	var n = $('#mytable tr').length;
	$($('#mytable tr')[n - 4]).remove();
	index--;
}

</script>
	</body>
</html>