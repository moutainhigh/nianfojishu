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
<script type="text/javascript">
var index = 1;

function addLine() {
	var newLine = '<tr align="center"><th><select  name="t.scope['
			+ index
			+ '].type" style="width:209px;"  > '
			+ '<option>手动填写</option> <option>是否合格</option> <option>有无</option> <option>OKorNo</option></select> </th> '
			+ ' <th><input name="t.scope['
			+ index
			+ '].content"/></th>'
			+ '<th><input name="t.scope['
			+ index
			+ '].zltz"  /></th>'
			+ '<th><input name="t.scope['
			+ index
			+ '].jcff"  />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'
			+ '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'
			+ '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th></tr>';
	$($('#mytable tr')[$('#mytable tr').length - 2]).insertBefore(newLine);
	$('#lastTr').before(newLine);
	index++;
}

function delLine() {
	var n = $('#mytable tr').length;
	if (index == 1) {
		alert("只剩最后一项了,再删真没了");
		return;
	}
	$($('#mytable tr')[$('#mytable tr').length - 3]).remove();
	index--;
}

function check() {
	var content = document.getElementById("content");
	var zltz = document.getElementById("zltz");
	var jcff = document.getElementById("jcff");
	var name = document.getElementById("name");
	var cmodel = document.getElementById("cmodel");
	var partNumber = document.getElementById("partNumber");
	var ctype1 = document.getElementById("ctype1");
	var gongxuNum = document.getElementById("gongxuNum");
	var zhonglei = document.getElementById("zhonglei");
	var filename = $("#abc").val();
	if (name != null && name.value == "") {
		alert("请填写名称");
		return false;
	} else if (zhonglei != null && zhonglei == "") {
		alert("请选择模板种类");
		return false;
	} else if (content != null && content.value == "") {
		alert("请填写检查条目");
		return false;
	} else if (jcff != null && jcff.value == "") {
		alert("请填写检查方法");
		return false;
	}else if(filename == ""){
		alert("请上传图纸");
		return false;
	}
	document.getElementById("submit").disabled = "disabled";
	return true;

}
</script>
</head>
<body>
	<%@include file="/util/sonTop.jsp"%>
	<div id="gongneng">

		<div align="center">
			<form id="myForm" action="OsTemplate_add.action" method="post"
				enctype="multipart/form-data" onsubmit="return check();">
				<input type="hidden" name="t.dept" value="${companyInfo.shortName}" />
				<table id="mytable1" class="table" style="width: 90%">
					<tr>
						<th colspan="4"><s:if test="status == 'fl'">
								<font size="6">添加辅料检验模版</font>
							</s:if> <s:else>
								<font size="6">添加通用检验模版</font>
							</s:else></th>
					</tr>
					<s:if test="status == 'fl'">
						<tr>
							<th>件号</th>
							<td><input id="partNumber" name="t.partNumber" /></td>
							<th>名称</th>
							<td><input id="name" name="t.name"></td>
						</tr>
					</s:if>
					<tr>
						<th>型别</th>
						<td><input id="cmodel" name="t.cmodel" /></td>
						<th>产品类型</th>
						<td><input id="ctype" name="t.ctype"></td>
					</tr>
					<tr>
						<th>模板类型</th>
						<td><s:if test="status == 'fl'">
								<input type="text" value="辅料" name="t.zhonglei" id="zhonglei"
									readonly="readonly" />
							</s:if> <s:else>
								<select id="zhonglei" name="t.zhonglei">
									<option></option>
									<option value="外购件检验">外购件检验</option>
									<option value="外委">外委检验</option>
									<option value="总成检验">总成检验</option>
									<option value="巡检">巡检</option>
								</select>
							</s:else></td>
						<th>版本</th>
						<td><input id="banbenNumber" name="t.banbenNumber"></td>
					</tr>
					<tr>
						<th>检验类型</th>
						<td align="left"><select name="t.xjtype" id="xjtype"
							style="width: 209px;" onchange="changvalue(this)">
								<option value="按时间">按时间</option>
								<option value="按时间">按次数</option>
						</select></td>
						<th>检验频次</th>
						<td id="xjcheckpc_id"><input name="t.xjcheckpc"
							id="xjcheckpc" /></td>
					</tr>
					<tr>
						<th>上传图纸</th>
						<td><input id="abc" type="file" name="attachment" /></td>
						<th>生产类型</th>
						<td><select id="ctype1" name="t.ctype1">
								<option></option>
								<option value="外购件">外购件</option>
								<option value="原材料">原材料</option>
								<option value="自制件">自制件</option>
								<option value="自制件">总成件</option>
						</select></td>
					</tr>
					<tr>
						<th>检验标准</th>
						<td>
							<select name="t.xjbzId" id="xjbzId">
								<option></option>
							</select>
						</td>
						<td colspan="3"></td>
					</tr>
				</table>

				<table class="table" id="mytable">
					<tr>
						<th colspan="4" align="center" style="font-size: 28">检验明细</th>
					</tr>
					<tr>
						<th>结果类型</th>
						<th>检查条目</th>
						<th>质量特征</th>
						<th>检查方法</th>
					</tr>
					<tr>
						<th><select name="t.scope[0].type" style="width: 209px;"
							id="type">
								<option>手动填写</option>
								<option>是否合格</option>
								<option>有无</option>
								<option>OKorNo</option>
						</select></th>
						<th><input name="t.scope[0].zltz" id="zltz" /></th>
						<th><input name="t.scope[0].content" id="content" /></th>
						<th><input name="t.scope[0].jcff" id="jcff" /> <input
							type="button" onclick="addLine();" value="追加"> <input
							type="button" onclick="delLine();" value="删除"></th>
					</tr>

					<tr id="lastTr">
						<td align="center" colspan="8"><input type="hidden" value="是"
							name="t.ispublic" /> <input type="submit" value="提交" id="sub"
							class="input"></td>
					</tr>
					<tr>
						<td align="center" colspan="8">常用符号: Φ ± ° ≤ ≥ ℃ < > № ⊥ ◎ ○
							&nbsp;&nbsp;&nbsp;&nbsp;</td>
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
$(function(){
	$.ajax( {
					type : "POST",
					url : "markIdAction!findAllJyPc.action?status=xj",
					dataType : "json",
					success : function(data) {
						$("#xjbzId").empty();
						if(data!=null){
							$(data).each(function(){
								$("#xjbzId").append("<option value="+this.id+">"+this.leixing+"</option>")
							})
						}
					}
				})
})

</script>
</body>
</html>
