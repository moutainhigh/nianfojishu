<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
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
		<script type="text/javascript">
// 查看工资明细
function wageDetails() {
	if (XMLHttpReq.readyState == 4) { // 判断对象状态
		if (XMLHttpReq.status == 200) { // 信息已经成功返回，开始处理信息
			var ulMessage = XMLHttpReq.responseText;
			var operatingDiv = document.getElementById("operatingDiv");
			var exportWageform = document.getElementById("exportWageform");
			var uploadChageWageDiv = document
					.getElementById("uploadChageWageDiv");
			operatingDiv.innerHTML = ulMessage;
			exportWageform.style.display = "none";
			uploadChageWageDiv.style.display = "none";
			operatingDiv.style.display = "block";
			chageDiv('block');
		} else { //页面不正常
			window.alert("页面异常,请重试!");
		}
	}
}

//更改标题
function chageTitle(userName, id) {
	var titleDiv = document.getElementById("title");
	titleDiv.innerHTML = "您正在查看<font color='red'> " + userName
			+ "</font> 的工资明细 ";
	sendRequest("WageAction!showWageDetails.action?id=" + id, wageDetails)

}

//添加变动工资
function addChageWage(userName, wageId) {
	var showAllChageWageDiv = document.getElementById("showAllChageWage");
	var addChageWageDiv = document.getElementById("addChageWage");
	var userNameSpan = document.getElementById("userName");
	var wageIdInput = document.getElementById("wageId");
	wageIdInput.value = wageId;
	userNameSpan.innerHTML = userName;
	showAllChageWageDiv.style.display = "none";
	addChageWageDiv.style.display = "block";

}

$(function() {
	var wageList = "${wageList}";
	if (wageList == "") {
		$("#showAllChageWage").hide();
		$("#addChageWage").show("slow");
	}
});

//表单检查
function checkForm() {
	var jiabanfei = document.getElementById("jiabanfei");//加班费
	var wucanfei = document.getElementById("wucanfei");//午餐费
	var jiangjin = document.getElementById("jiangjin");//奖金
	var fangzufei = document.getElementById("fangzufei");//房租费
	var shuidianfei = document.getElementById("shuidianfei");//水电费
	var bingshikangdeng = document.getElementById("bingshikangdeng");//病事旷
	var bufagongzi = document.getElementById("bufagongzi");//补发(补扣)工资
	var other = document.getElementById("other");//其他

	if (jiabanfei.value == "") {
		alert("加班费不能为空!");
		jiabanfei.focus();
		return false;
	} else if (wucanfei.value == "") {
		alert("午餐费不能为空!");
		wucanfei.focus();
		return false;
	} else if (jiangjin.value == "") {
		alert("奖金不能为空!");
		jiangjin.focus();
		return false;
	} else if (fangzufei.value == "") {
		alert("房租费不能为空!");
		fangzufei.focus();
		return false;
	} else if (shuidianfei.value == "") {
		alert("水电费不能为空!");
		shuidianfei.focus();
		return false;
	} else if (bingshikangdeng.value == "") {
		alert("病事旷等不能为空!");
		bingshikangdeng.focus();
		return false;
	} else if (bufagongzi.value == "") {
		alert("补发工资不能为空!");
		bufagongzi.focus();
		return false;
	} else if (other.value == "") {
		alert("其他不能为空!");
		other.focus();
		return false;
	} else {
		return true;
	}

}

//上传变动工资
function uploadWage(status) {
	var uploadChageWageDiv = document.getElementById("uploadChageWageDiv");
	var operatingDiv = document.getElementById("operatingDiv");
	var exportWageform = document.getElementById("exportWageform");
	var downStatus = document.getElementById("downStatus");
	operatingDiv.style.display = "none";
	if (status == "upload") {
		uploadChageWageDiv.style.display = "block";
		exportWageform.style.display = "none";
		chageDiv('block', '您将上传变动工资信息');
	} else if (status == "down") {
		uploadChageWageDiv.style.display = "none";
		exportWageform.style.display = "block";
		downStatus.value = "jixiao";
		chageDiv('block', '您将下载变动工资信息');
	} else if (status == "check") {
		uploadChageWageDiv.style.display = "none";
		exportWageform.style.display = "block";
		downStatus.value = "check";
		chageDiv('block', '您将下载自查工资信息');
	}
}

function chageErrorMessage() {
	var errorMessage = "${errorMessage}";
	if (errorMessage != "" && errorMessage != "没有找到你要查询的内容,请检查后重试!") {
		var uploadChageWageDiv = document.getElementById("uploadChageWageDiv");
		var operatingDiv = document.getElementById("operatingDiv");
		uploadChageWageDiv.style.display = "none";
		operatingDiv.innerHTML = errorMessage;
		chageDiv('block', '您正在查看上传变动工资后的处理信息:');
	}
}
</script>
	</head>
	<body bgcolor="#ffffff" onload="chageErrorMessage(),createDept('dept')">
		<div id="bodyDiv" align="center" class="transDiv"></div>
		<div id="contentDiv"
			style="position: absolute; z-index: 255; width: 980px; display: none;"
			align="center">
			<div id="closeDiv"
				style="background: url(<%=basePath%>/images/bq_bg2.gif); width: 100%;">
				<table style="width: 100%">
					<tr>
						<td>
							<span id="title"></span>
						</td>
						<td align="right">
							<img alt="" src="images/closeImage.png" width="30" height="32"
								onclick="chageDiv('none')">
						</td>
					</tr>
				</table>
				<div id="operatingDiv"
					style="background-color: #ffffff; width: 100%;">
				</div>
				<div id="uploadChageWageDiv"
					style="background-color: #ffffff; width: 100%;">
					<a
						href="DownAction.action?fileName=chageWage.xls&directory=/upload/public/">变动工资模版下载</a>
					<a
						href="FileViewAction.action?FilePath=/upload/public/chageWage.xls&Refresh=true">/预览</a>
					<form action="WageAction!uploadChageWage.action" method="post"
						enctype="multipart/form-data">
						<input type="hidden" name="pageStatus" value="${pageStatus}">
						请选择上传月份:&nbsp;&nbsp;&nbsp;&nbsp;
						<input class="Wdate" type="text" name="wage.mouth"
							onClick="WdatePicker({dateFmt:'yyyy-MM月',skin:'whyGreen'})" />
						<br />
						请选择变动工资文件(excel文件):&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="file" name="chageWage">
						<input type="submit" value="上传">
					</form>
				</div>
				<div style="display: none; background-color: #ffffff; width: 100%;"
					id="exportWageform">
					<form action="WageAction!exportWageArticle.action" method="post">
						<input type="hidden" id="downStatus" name="pageStatus">
						<input type="hidden" name="articleOrSingle" value="wageArticle">
						<table align="center">
							<tr>
								<th align="right">
									月份:
								</th>
								<td>
									<input class="Wdate" type="text" name="wage.mouth"
										onClick="WdatePicker({dateFmt:'yyyy-MM月',skin:'whyGreen'})" />
								</td>
								<th align="center">
									<input id="wageArticle" value="导出工资条" type="submit" data="downData" onclick="todisabled(this)">
								</th>
							</tr>
						</table>
					</form>
				</div>
			</div>
		</div>
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
					<a
						href="ModuleFunctionAction!findMfByIdForJump.action?id=${moduleFunction.id}"
						style="color: #ffffff">待添加变动工资</a>
					<a href="WageAction!findAllWage.action?pageStatus=findAudit"
						style="color: #ffffff">审核中工资</a>
					<a
						href="DownAction.action?fileName=chageWage.xls&directory=/upload/public/">变动工资模版下载</a>
					<a
						href="FileViewAction.action?FilePath=/upload/public/chageWage.xls&Refresh=true"
						style="color: #ffffff">/预览</a>
				</div>
			</div>
			
			<div align="center" id="showAllChageWage">
				<form action="WageAction!findWageByCondition.action" method="post"
					style="margin: 0px;">
					<input type="hidden" name="pageStatus" value="${pageStatus}">
					<table class="table">
						<tr>
							<th colspan="6">
								工资查询
							</th>
						</tr>
						<tr>
							<td align="right">
								姓名:
							</td>
							<td align="left">
								<input name="wage.userName" />
							</td>
							<td align="right">
								工号:
							</td>
							<td align="left">
								<input name="wage.code" />
							</td>
							<td align="right">
								部门:
							</td>
							<td align="left">
								<select name="wage.dept" id="dept" style="width: 155px">
									<option></option>
								</select>
							</td>
						</tr>
						<tr>
							<td align="right">
								月份:
							</td>
							<td align="left">
								<input class="Wdate" type="text" name="wage.mouth"
									onClick="WdatePicker({dateFmt:'yyyy-MM月',skin:'whyGreen'})" />
							</td>
							<td align="right">
								工资状态:
							</td>
							<td align="left">
								<select name="wage.wageStatus" style="width: 155px">
									<option></option>
									<option value="添加变动">
										添加变动
									</option>
									<option value="自查">
										自查
									</option>
								</select>
							</td>
							<td align="right">
								工资类别:
							</td>
							<td align="left">
								<select name="wage.wageClass" style="width: 155px">
									<option></option>
									<option value="承包奖金">
										承包奖金
									</option>
								</select>
							</td>
						</tr>
						<tr>
							<td colspan="6" align="center">
								<input value="查询" type="submit"
									style="width: 100px; height: 50px">
								<input value="重置" type="reset"
									style="width: 100px; height: 50px">
							</td>
						</tr>
					</table>
				</form>
				
				<table  class="table">
					<tr>
						<th colspan="11">
							员 工 工 资 信 息 (
							<a href="javascript:uploadWage('upload')">上传变动工资</a> |
							<a href="javascript:uploadWage('down')">变动工资下载</a> |
							<a href="javascript:uploadWage('check')">自查工资下载</a> |
							<a href="WageAction!submitWageAudit.action"
								onclick="return window.confirm('确定要提交审核吗?')">提交审核</a>)
						</th>
					</tr>
					<tr bgcolor="#c0dcf2" height="50px">
						<th align="center">
							序号
						</th>
						<th align="center">
							姓名
						</th>
						<th align="center">
							部门
						</th>
						<th align="center">
							岗位工资
						</th>
						<th align="center">
							保密津贴
						</th>
						<th align="center">
							绩效考核工资
						</th>
						<th align="center">
							奖金
						</th>
						<th align="center">
							加班费
						</th>
						<th align="center">
							补贴
						</th>
						<th>
							应发工资
						</th>
						<th align="center">
							发放月份
						</th>
						<th align="center">
							工资状态
						</th>
						<th align="center">
							工资类别
						</th>
						<th align="center">
							操作
						</th>
					</tr>
					<s:iterator value="wageList" id="pageWageList"
						status="pageListStatus">
						<s:if test="#pageListStatus.index%2==1">
							<tr align="center" bgcolor="#e6f3fb"
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'')">
						</s:else>
						<td>
							<s:if test="#pageListStatus.index%2==1">
								<font>
							</s:if>
							<s:else>
								<font color="#c0dcf2">
							</s:else>
							<s:property value="#pageListStatus.index+1" />
							</font>
						</td>
						<td>
							${pageWageList.userName}
						</td>
						<td>
							${pageWageList.dept}
						</td>
						<td>
							${pageWageList.gangweigongzi}
						</td>
						<td>
							${pageWageList.baomijintie}
						</td>
						<td>
							${pageWageList.jixiaokaohegongzi}
						</td>
						<td>
							${pageWageList.jiangjin}
						</td>
						<td>
							${pageWageList.jiabanfei}
						</td>
						<td>
							${pageWageList.dianhuabutie}
						</td>
						<td>
							${pageWageList.yingfagongzi}
						</td>
						<td>
							${pageWageList.mouth}
						</td>
						<td>
							${pageWageList.wageStatus}
						</td>
						<td>
							${pageWageList.wageClass}
						</td>
						<td>
							<s:if test="pageStatus=='chage'">
								<s:if test="#pageWageList.wageStatus=='添加变动'">
									<a href="javascript:;"
										onclick="addChageWage('${pageWageList.userName}','${pageWageList.id}')">添加</a> /
									</s:if>
								<s:elseif
									test="#pageWageList.wageStatus=='打回'||#pageWageList.wageStatus=='自查'">
									<a
										href="WageAction!findWageById.action?id=${pageWageList.id}&pageStatus=${pageStatus}">修改</a> /</s:elseif>

							</s:if>
							<s:elseif test="pageStatus=='findAudit'">
								<a
									href="WageAction!findWageById.action?id=${pageWageList.id}&pageStatus=${pageStatus}">修改</a> /
								</s:elseif>
							<a
								href="javascript:chageTitle('${pageWageList.userName}','${pageWageList.id}')">明细</a>
						</td>
						</tr>
					</s:iterator>
					<tr>
						<s:if test="errorMessage==null">
							<td colspan="14" align="right">
								第
								<font color="red"><s:property value="cpage" /> </font> /
								<s:property value="total" />
								页
								<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
									styleClass="page" theme="number" />
						</s:if>
						<s:else>
							<td colspan="14" align="center" style="color: red">
								${errorMessage}
						</s:else>
						</td>
					</tr>
				</table>
			</div>
			<div>
				${successMessage}
			</div>
			<div id="addChageWage" style="display: none;">
				<form action="WageAction!addChageWage.action" method="post"
					onsubmit="return checkForm()">
					<input type="hidden" name="id" id="wageId" value="${wage.id}">
					<input type="hidden" name="pageStatus" value="${pageStatus}">
					<table class="table">
						<tr>
							<th colspan="6">
								为员工
								<font color="red"><span id="userName"></span> </font> &nbsp;
								<s:if test="wage.userName!=''">
										修改				
									</s:if>
								<s:else>
										添加
									</s:else>
								变动工资
							</th>
						</tr>
						<tr>
							<th align="center">
								加班费:
							</th>
							<td>
								<input id="jiabanfei" name="wage.jiabanfei"
									value="${wage.jiabanfei==null?0:wage.jiabanfei}">
							</td>

							<th align="center">
								午餐费:
							</th>
							<td>
								<input id="wucanfei" name="wage.wucanfei"
									value="${wage.wucanfei==null?0:wage.wucanfei}">
							</td>
							<th align="center">
								奖金:
							</th>
							<td align="left">
								<input id="jiangjin" name="wage.jiangjin"
									value="${wage.jiangjin==null?0:wage.jiangjin}">
							</td>
						</tr>
						<tr>
							<th align="center">
								水电费:
							</th>
							<td>
								<input id="shuidianfei" name="wage.shuidianfei"
									value="${wage.shuidianfei==null?0:wage.shuidianfei}">
							</td>
							<th align="center">
								病事旷等:
							</th>
							<td align="left">
								<input id="bingshikangdeng" name="wage.bingshikangdeng"
									value="${wage.bingshikangdeng==null?0:wage.bingshikangdeng}">
							</td>
							<th align="center">
								补发(补扣)工资:
							</th>
							<td>
								<input id="bufagongzi" name="wage.bfgongzi"
									value="${wage.bfgongzi==null?0:wage.bfgongzi}">
							</td>
						</tr>
						<tr>

							<th align="center">
								其他:
							</th>
							<td>
								<input id="other" name="wage.other"
									value="${wage.other==null?0:wage.other}">
							</td>
							<th align="center">
								&nbsp;
							</th>
							<td>
								&nbsp;
							</td>
							<th align="center">
								&nbsp;
							</th>
							<td>
								&nbsp;
							</td>
						</tr>
						<tr>
							<td align="center" colspan="6">
								<s:if test="wage.userName!=''">
									<input type="submit" value="修改"
										style="width: 80px; height: 50px" />
								</s:if>
								<s:else>
									<input type="submit" value="添加"
										style="width: 80px; height: 50px" />
								</s:else>
								&nbsp;&nbsp;
								<input type="reset" value="重置" style="width: 80px; height: 50px" />
							</td>
						</tr>
					</table>
				</form>
			</div>
			<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		</center>
	</body>
</html>