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
		<div id="bodyDiv" align="center" class="transDiv"
			onclick="chageDiv('none')">
		</div>
		<div id="contentDiv"
			style="position: absolute; z-index: 255; width: 900px; display: none;"
			align="center">
			<div id="closeDiv"
				style="background: url(<%=basePath%>/images/bq_bg2.gif); width: 100%;">
				<table style="width: 100%">
					<tr>
						<td>
							<span id="title">关闭并反馈</span>
						</td>
						<td align="right">
							<img alt="" src="<%=basePath%>/images/closeImage.png" width="30"
								height="32" onclick="chageDiv('none')">
						</td>
					</tr>
				</table>
				<div id="operatingDiv"
					style="background-color: #ffffff; width: 100%;">
					<form action="procardTemplateGyAction_closeSb.action" method="post">
						<input name="id" id="sbapplyId" type="hidden">
						<input name="cpage" id="cpage" type="hidden" value="${cpage}">
						是否执行:
						<input type="radio" value="已执行" name="type"> 已执行
						<input type="radio" value="不执行" name="type"> 不执行
						<input type="radio" value="不涉及" name="type" checked="checked"> 不涉及<br/>
						<input type="submit" value="确定"><br/><br/>
					</form>
				</div>
				<div id="operatingDiv2"
					style="background-color: #ffffff; width: 100%;">
					<form action="procardTemplateGyAction_quxiaoSb.action" method="post">
						<input name="bbAply.id" id="sbapplyId2" type="hidden">
						<input name="cpage" id="cpage" type="hidden" value="${cpage}">
						取消原因:<textarea name="bbAply.qxRemark" rows="4" cols="40"></textarea>
						<br/><input type="submit" value="确定"><br/><br/>
					</form>
				</div>
			</div>
		</div>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng" >
			</div>
			
			<div align="center">
				<h3>
					设变版本升级申请管理<br/>
				</h3>
				<form action="procardTemplateGyAction_findselfSbApplyList.action
"
					method="post">
					<input type="hidden" name="pageStatus" value="${pageStatus}">
					<table class="table" align="center">
						<tr>
							<td align="center">
								设变单号:
								<input type="text" name="bbAply.sbNumber" value="<s:property value="bbAply.sbNumber"/>" />
							</td>
							<td align="center">
								外部设变单号:
								<input type="text" name="bbAply.outbsNumber" value="<s:property value="bbAply.outbsNumber"/>" />
							</td>
							<td align="center">
								总成名称:
								<input type="text" name="bbAply.proName" value="<s:property value="bbAply.proName"/>" />
							</td>
						</tr>
						<tr>
							<td align="center">
								总成件号:
								<input type="text" name="bbAply.markId" value="<s:property value="bbAply.markId"/>" />
							</td>
							<td align="center">
								业务件号:
								<input type="text" name="bbAply.ywMarkId" value="<s:property value="bbAply.ywMarkId"/>" />
							</td>
							<td align="center">
								版本号:
								<input type="text" name="bbAply.banbenNumber" value="<s:property value="bbAply.banbenNumber"/>" />
							</td>
						</tr>
						<tr>
							<td align="center">
								设变零件:
								<input type="text" name="markId" value="<s:property value="markId"/>" />
							</td>
							<td align="center">
								设变来源
								<SELECT name="bbAply.sbSource">
									<s:if test="bbAply.sbSource!=null&&bbAply.sbSource!=''">
									<option><s:property value="bbAply.sbSource"/></option></s:if>
									<option></option>
									<option>内部设变</option>
									<option>外部设变</option>
								</SELECT>
							</td>
							<td align="center">
								发起人:
								<input type="text" name="bbAply.applicantName" value="<s:property value="bbAply.applicantName"/>" />
							</td>
							
						</tr>
						<tr><td align="center">
								进度:
								<SELECT name="bbAply.processStatus">
								<option>${bbAply.processStatus}</option>
								<option></option>
								<option>设变发起</option>
								<option>分发项目组</option>
								<option>项目主管初评</option>
								<option>选择设变零件</option>
								<option>变更设计</option>
								<option>工程师评审</option>
								<option>成本审核</option>
								<option>各部门评审</option>
								<option>资料更新</option>
								<option>关联并通知生产</option>
								<option>生产后续</option>
								<option>上传佐证</option>
								<option>关闭</option>
								<option>取消</option>
								</SELECT>
							</td>
						<td align="center">
								发起时间：
								
								<input class="Wdate" type="text" name="startDate"
									value="${startDate}" size="15"
									onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})" />
							</td>
							<td align="center">		
									To:
									<input class="Wdate" type="text" name="endDate"
									value="${endDate}" size="15"
									onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})" />
							</td>
						</tr>
						<tr>
							<td align="center">
								生产组别:
								<input type="text" name="bbAply.aboutPlace" value="<s:property value="bbAply.aboutPlace"/>" />
							</td>
							<td align="center" colspan="2">
								<input type="submit" style="width: 100px; height: 40px;"
									value="查询" />
								<input type="button" style="width: 100px; height: 40px;"
									value="发起设变" onclick="toadd()" />
<%--								<input type="button" value="导出1" style="width: 100px; height: 40px;" onclick="exprotBbAply(this.form)"/>--%>
								<input type="button" value="导出" style="width: 100px; height: 40px;" onclick="exprotBbAply_new(this.form)"/>
							</td>
						</tr>
					</table>
				</form>
				<table width="100%" class="table">
					<tr bgcolor="#c0dcf2" height="50px">
						<td align="center">
							序号
						</td>
						<td align="center">
							设变单号
						</td>
						<td align="center">
							外部设变单号
						</td>
						<td align="center">
							总成件号
						</td>
						<td align="center">
							业务件号
						</td>
						<td align="center">
							生产组别
						</td>
						<td align="center">
							总成名称
						</td>
						<td align="center">
							版本
						</td>
						<td align="center">
							发起人
						</td>
						<td align="center">
							发起时间
						</td>
						<td align="center">
							进程
						</td>
						<td align="center">
							最后操作
						</td>
						<td align="center">
							备注
						</td>
						<td align="center" colspan="2">操作<br/>(Operation)</td>
					</tr>
					<tr bgcolor="red"><td colspan="14" align="center">待处理</td>
					</tr>
					<s:iterator value="list" id="dclsbApply" status="dclpageStatus">
						<s:if test="#dclpageStatus.index%2==1">
							<tr align="center" bgcolor="#e6f3fb"
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'')">
						</s:else>
						<td>
							<s:if test="#dclpageStatus.index%2==1">
								<font>
							</s:if>
							<s:else>
								<font color="#c0dcf2">
							</s:else>
							<s:property value="#dclpageStatus.index+1" />
							</font>
						</td>
						<td align="left">
							${dclsbApply.sbNumber}
						</td>
						<td align="left">
							${dclsbApply.outbsNumber}
						</td>
						<td align="left">
							${dclsbApply.markId}
						</td>
						<td align="left">
							${dclsbApply.ywMarkId}
						</td>
						<td align="left">
							${dclsbApply.aboutPlace}
						</td>
						<td style="max-width: 60px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
									${dclsbApply.proName}
									<ul class="qs_ul">
										<li>
											${dclsbApply.proName}
										</li>
									</ul>
						</td>
						<td align="left">
							${dclsbApply.banbenNumber}
						</td>
						<td align="left">
							${dclsbApply.applicantName}
						</td>
						<td align="left">
							${dclsbApply.applyTime}
						</td>
						<td align="center">
							${dclsbApply.processStatus}
						<td align="left" style="width: 80px;">
							<s:if test="#dclsbApply.lastop=='打回'">
							<font color="red">
								${dclsbApply.lastop}
							</font>
							</s:if>
							<s:else>
								${dclsbApply.lastop}
							</s:else>
							<br/>
							${dclsbApply.lastUserName}
							<br/>${dclsbApply.lastTime}
							
						</td>
						</td>
						<td align="left">
							${dclsbApply.remark}
						</td>
						<td  colspan="2">
						 <input type="button" value="进度"
								style="width: 60px; height: 30px;"
								onclick="view(${dclsbApply.id},this)" />
						<s:if test="#dclsbApply.processStatus!='资料更新'
						&&#dclsbApply.processStatus!='关联并通知生产'
						&&#dclsbApply.processStatus!='生产后续'
						&&#dclsbApply.processStatus!='关闭'
						&&#dclsbApply.processStatus!='取消'
						&&#dclsbApply.processStatus!='上传佐证'">
						 <input type="button" value="取消"
								style="width: 60px; height: 30px;"
								onclick="toquxiao(${dclsbApply.id})" />
						</s:if>
						<s:if test="#dclsbApply.processStatus=='分发项目组'">
						<s:if test="ryzbList!=null&&ryzbList.size()>0">
							<s:iterator value="ryzbList" id="ryzb">
								<s:if test="#ryzb=='设变关闭组'">
									<s:if test="#dclsbApply.workItemId!=null&&#dclsbApply.workItemId>0&&#dclsbApply.ecType!=null">
										 <input type="button" value="关闭并反馈"
										style="width: 80px; height: 30px;"
										onclick="toclose(${dclsbApply.id},${dclsbApply.workItemId})" />
									</s:if>
									<s:else>
										<input type="button" value="关闭"
										style="width: 60px; height: 30px;"
										onclick="toclose(${dclsbApply.id})" />
									</s:else>
								</s:if>
							</s:iterator>
						</s:if>
						</s:if>
<%--							<input type="button" value="删除(delete)"--%>
<%--								style="width: 60px; height: 30px;"--%>
<%--								onclick="todelete(${dclsbApply.id })" />--%>
						</td>

					</s:iterator>
					</tr>
					<tr bgcolor="red"><td colspan="14" align="center">所有</td>
					</tr>
					<s:iterator value="bbAplyList" id="sbApply" status="pageStatus">
						<s:if test="#pageStatus.index%2==1">
							<tr align="center" bgcolor="#e6f3fb"
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'')">
						</s:else>
						<td>
							<s:if test="#pageStatus.index%2==1">
								<font>
							</s:if>
							<s:else>
								<font color="#c0dcf2">
							</s:else>
							<s:property value="#pageStatus.index+1" />
							</font>
						</td>
						<td align="left">
							${sbApply.sbNumber}
						</td>
						<td align="left">
							${sbApply.outbsNumber}
						</td>
						<td align="left">
							${sbApply.markId}
						</td>
						<td align="left">
							${sbApply.ywMarkId}
						</td>
						<td align="left">
							${sbApply.aboutPlace}
						</td>
						<td style="max-width: 60px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
									${sbApply.proName}
									<ul class="qs_ul">
										<li>
											${sbApply.proName}
										</li>
									</ul>
						</td>
						<td align="left">
							${sbApply.banbenNumber}
						</td>
						<td align="left">
							${sbApply.applicantName}
						</td>
						<td align="left">
							${sbApply.applyTime}
						</td>
						<td align="center">
							${sbApply.processStatus}
						<td align="left" style="width: 80px;">
							<s:if test="#sbApply.lastop=='打回'">
							<font color="red">
								${sbApply.lastop}
							</font>
							</s:if>
							<s:else>
								${sbApply.lastop}
							</s:else>
							<br/>
							${sbApply.lastUserName}
							<br/>${sbApply.lastTime}
							
						</td>
						</td>
						<td align="left">
							${sbApply.remark}
						</td>
						<td  colspan="2">
						 <input type="button" value="进度"
								style="width: 60px; height: 30px;"
								onclick="view(${sbApply.id},this)" />
						<s:if test="#sbApply.processStatus!='资料更新'
						&&#sbApply.processStatus!='关联并通知生产'
						&&#sbApply.processStatus!='生产后续'
						&&#sbApply.processStatus!='关闭'
						&&#sbApply.processStatus!='取消'
						&&#sbApply.processStatus!='上传佐证'">
						 <input type="button" value="取消"
								style="width: 60px; height: 30px;"
								onclick="toquxiao(${sbApply.id})" />
						</s:if>
						<s:if test="#sbApply.processStatus=='分发项目组'">
						<s:if test="ryzbList!=null&&ryzbList.size()>0">
							<s:iterator value="ryzbList" id="ryzb">
								<s:if test="#ryzb=='设变关闭组'">
									<s:if test="#sbApply.workItemId!=null&&#sbApply.workItemId>0&&#sbApply.ecType!=null">
										 <input type="button" value="关闭并反馈"
										style="width: 80px; height: 30px;"
										onclick="toclose(${sbApply.id},${sbApply.workItemId})" />
									</s:if>
									<s:else>
										<input type="button" value="关闭"
										style="width: 60px; height: 30px;"
										onclick="toclose(${sbApply.id})" />
									</s:else>
								</s:if>
							</s:iterator>
						</s:if>
						</s:if>
<%--							<input type="button" value="删除(delete)"--%>
<%--								style="width: 60px; height: 30px;"--%>
<%--								onclick="todelete(${sbApply.id })" />--%>
						</td>

					</s:iterator>
					</tr>
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
						<s:if test="successMessage!=null">
						<tr>
							<td colspan="14" align="center" style="color: red">
								${successMessage}
								
						</td>
					</tr>
                          </s:if>
				</table>
			</div>
			<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
<script type="text/javascript">
function view(id,obj) {
	todisabled(obj);
	window.location.href = "procardTemplateGyAction_showSbApplyJd2.action?bbAply.id=" + id;
}
function toadd(){
	window.location.href = "procardTemplateGyAction_toAddSb.action" ;
}
function todelete(id){
	if(confirm("是否确定删除?")){
	window.location.href = "procardTemplateGyAction_deleteSb.action?id="+id+"&cpage=${cpage}" ;
	}
}
function toquxiao(id){
<%--	if(confirm("是否确定取消?")){--%>
<%--	window.location.href = "procardTemplateGyAction_quxiaoSb.action?id="+id+"&cpage=${cpage}" ;--%>
<%--	}--%>
			$("#sbapplyId2").val(id);
			$("#operatingDiv").hide();
			$("#operatingDiv2").show();
			chageDiv("block");
}
function toclose(id,outid){
	if(confirm("是否确定关闭?")){
		if(outid!=null){
			$("#sbapplyId").val(id);
			$("#operatingDiv").show();
			$("#operatingDiv2").hide();
			chageDiv("block");
		}else{
			window.location.href = "procardTemplateGyAction_closeSb.action?id="+id+"&cpage=${cpage}" ;
		}
	}
}
function exprotBbAply(obj){
	$(obj).attr("action","procardTemplateGyAction_exprotBbAply.action");
	$(obj).submit();
	$(obj).attr("action","procardTemplateGyAction_findSbApplyList.action")
}
function exprotBbAply_new(obj){
	$(obj).attr("action","procardTemplateGyAction_exprotBbAply_new.action");
	$(obj).submit();
	$(obj).attr("action","procardTemplateGyAction_findSbApplyList.action")
}
</script>
	</body>
</html>
