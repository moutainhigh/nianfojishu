<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<style>
.noStyle{
	 background: none;
	 color: gray;
}
</style>
<div align="center">
	
	<div>
		<h4>
			填报项目进度
		</h4>
		<form action="projectPoolAction_saveOrSubmitSchedule.action" id="frm" method="post" enctype="multipart/form-data">
			<input type="hidden" name="projectManageyf.id" value="${projectManageyf.id}"/>
			<input type="hidden" value="${message }" id="message">
			<input type="hidden" name="tag" id="tag"/>
			<table class="table">
				<tr>
					<th class="col-xs-3 text-right noStyle">
						项目编号:
					</th>
					<td class="col-xs-3 text-left">
						<span>${projectManageyf.proNum}</span>
					</td>
				</tr>
				<tr>
					<th class="col-xs-3 text-right noStyle">
						项目名称:
					</th>
					<td  class="col-xs-3 text-left">
						${projectManageyf.proName}
					</td>
					
				</tr>
				<tr>
					<th class="col-xs-3 text-right noStyle">
						创建时间:
					</th>
					<td class="col-xs-3 text-left">
						${projectManageyf.addTime}
					</td>
				</tr>
				<tr>
					<th class="col-xs-3 text-right noStyle">
						负责人:
					</th>
					<td class="col-xs-3 text-left">
						${projectManageyf.principals}
					</td>
				</tr>
				<tr>
					<th class="col-xs-3 text-right noStyle">
						指派时间:
					</th>
					<td class="col-xs-3 text-left">
						${projectManageyf.zpTime}
					</td>
				</tr>
				<tr>
					<th class="col-xs-3 text-right noStyle">
						预完成时间:
					</th>
					<td class="col-xs-3 text-left">
						${projectManageyf.reTime}
					</td>
				</tr>
				<tr>
					<th class="col-xs-3 text-right noStyle">
						项目备注:
					</th>
					<td class="col-xs-3 text-left">
						${projectManageyf.remark}
					</td>
				</tr>
				<tr>
					<th class="col-xs-3 text-right noStyle">
						项目评分:
					</th>
					<td class="col-xs-3 text-left">
							${projectManageyf.selfStore}
					</td>
				</tr>
				<tr>
					<th class="col-xs-3 text-right noStyle">
						评分备注:
					</th>
					<td class="col-xs-3 text-left">
						${projectManageyf.gradeRemark}
<%--								<textarea rows="3" cols="30" name="projectManageyf.gradeRemark" ></textarea>	--%>
					</td>
				</tr>
				<tr>
					<th class="noStyle text-center" colspan="2">文件</th>
				</tr>
				<tr>
					<td class="noStyle" align="center" colspan="2" >
						<div id="showFiles">
<!-- 							js获取值时使用 -->
							<input type="hidden" id="fileNames" value="${projectManageyf.yfProjectFile}">
							<input type="hidden" id="aliasFiles" value="${projectManageyf.aliasFile }" />
						</div>
						<input type="file" name="attachment" multiple="multiple" /> 
					</td>
				</tr>
				<tr>
					<th class="noStyle text-center" colspan="2">
						内容
					</th>
				</tr>
				<tr>
					<td class="noStyle text-center" colspan="2" align="center">
						<textarea style="width: 80%; height: 80px;" name="projectManageyf.schedule">${projectManageyf.schedule}</textarea>
					</td>
				</tr>
				<s:if test="status!='ok'">
					<tr>
						<td colspan="4" align="center">
							<input type="button" value="提交" style="width: 100px;height: 40px"
								onclick="submitSchedule()"/>
							<input type="button" value="保存" style="width: 100px;height: 40px"
								onclick="saveSchedule()"/>
<!-- 							<input type="reset" style="width: 100px;height: 30px"> -->
						</td>
					</tr>
				</s:if>
				
			</table>
		</form>
	</div>
<script type="text/javascript">
function deleteFile(delId){
	$("#pfile"+delId).remove();
}
$(function(){
	var fileInput = $("#fileNames").val();
	if(fileInput!=null && fileInput.length>0){
		var suffix = fileInput.split(",");
		var fileName = $("#aliasFiles").val().split(",");
		var str = "";
		for(var i=0;i<suffix.length;i++){
			var subSuffix = suffix[i].substring(suffix[i].indexOf("."));
			str+="<p id='pfile"+i+"'>文件"+(i+1)+"：<a href='${pageContext.request.contextPath}/FileViewAction.action?"+
					"FilePath=/upload/file/project/"+suffix[i]+"'>"+fileName[i]+"</a>&nbsp;&nbsp;&nbsp;"+
					"<input type='hidden' value='"+suffix[i]+"' name='oldFiles'>"+
					"<input type='hidden' value='"+fileName[i]+"' name='oldFileNames'>"+
					"<a href='#' onclick='deleteFile("+i+")' >删除</a></p>";
		}
		$("#showFiles").append(str);
	}
	
	var message = $("#message").val();
	if(""!=message){
		alert(message);
		$("#message").val("");
	}
});
/*function submitResult(result){
	
	if(confirm("确定提交为"+result+"吗?")){
		if("同意"==result){
			$("#result").val("YES");
		}else if("不同意"==result){
			$("#result").val("NO");	
		}
		$("#frm").submit();
	}
}*/

//保存进度，不提交审批
function saveSchedule(){
	/**
	 $.ajax({
	    url: '${pageContext.request.contextPath}/projectPoolAction_saveOrSubmitSchedule.action',
	    type: 'POST',
	    cache: false,
	    data: new FormData($('#frm')[0]),
	    processData: false,
	    contentType: false
	}).done(function(res) {
		alert(res);
	}).fail(function(res) {alert(res);});
	 */
	 document.getElementById("tag").value="save";
	 document.getElementById("frm")
	$("#tag").val("save");
	$("#frm").submit();
}

function submitSchedule(){
	if(confirm("确定要提交项目吗？")){
		$("#tag").val("submit");
		$("#frm").submit();
	}
}
			
		</script>
	</body>
</html>