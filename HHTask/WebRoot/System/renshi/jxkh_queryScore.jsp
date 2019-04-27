<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="com.task.entity.*,java.text.SimpleDateFormat"%>
<%@ page import="com.task.Dao.*"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<html>
	<head>
		<%@include file="/util/sonHead.jsp"%>
		<SCRIPT type="text/javascript">
function sdsView(){
	if (XMLHttpReq.readyState == 4) { // 判断对象状态
		if (XMLHttpReq.status == 200) { // 信息已经成功返回，开始处理信息
			var message = XMLHttpReq.responseText;
			var scoreDetailsDIV = document.getElementById("operatingDiv");
			scoreDetailsDIV.innerHTML = message;
			chageDiv('block',"");
		} else { //页面不正常
			window.alert("页面异常,请重试!");
		}
	}
}

</SCRIPT>
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
							您查询打分明细:
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
			</div>
		</div>
		<div id="operatingDiv"
			style="display: none; width: 600px; position: absolute; top: 200px; left: 360px; z-index: 255; background-color: #FFF">
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
			<s:if test="status != 'person'">
				<div style="float: left; width: 45%; padding-top: 5px;"
					align="right">
					<a
						href="ModuleFunctionAction!findMfByIdForJump.action?id=${moduleFunction.id}"
						style="color: #ffffff">查询所有</a>
				</div>
			</s:if>
			</div>
			
			<div align="center">
				<form action="AssScoreAction!findAssScoreByCondition.action"
					style="padding: 0px; margin: 0px;" method="post">
					<input type="hidden" name="status" value="${status}">
					<input type="hidden" name="assScore.id" value="${assScore.id}">
					<input name="template.assObject" value="员工级" type="hidden" />
					<table class="table">
						<tr>
							<th colspan="7" align="center">
								绩效考核成绩查询
							</th>
						</tr>
						<s:if test="status != 'person'">
						<tr>
							<td align="right">
								人员名称:
							</td>
							<td>
								<input name="assScore.userName" />
							</td>
							<td align="right">
								考核表名称:
							</td>
							<td>
								<input name="assScore.templateName" />
							</td>
						</tr>
						<tr>
							<td align='right'>
								考核部门:
							</td>
							<s:if test="%{#pageStatus=='dept'}">
								<%
									Users user = (Users) request.getSession().getAttribute(
												TotalDao.users);
										if (user.getDept().indexOf("and") > 0) {
											String[] dept = user.getDept().split("and");
											out
													.print("<td><select  name='assScore.dept' style='width:150px'>");
											for (int i = 0; i < dept.length; i++) {
												out.print("<option>" + dept[i] + "</option>");
											}
											out.print("</select></td>");
										} else {
											out
													.print("<td><input readonly='readonly' name='assScore.dept' value='"
															+ user.getDept() + "'/></td>");
										}
								%>
							</s:if>
							<s:elseif test="%{#pageStatus=='all'}">
								<td>
									<select id="dept" name="assScore.dept" style="width: 155px"
										onmouseover="createDept('dept')">
										<option></option>
									</select>
								</td>
							</s:elseif>
							<td align="right">
								考核月份:
							</td>
							<td>
								<input class="Wdate" type="text" name="assScore.asstMouth"
									onClick="WdatePicker({dateFmt:'yyyy-MM月',skin:'whyGreen'})" />
							</td>
						</tr>
						</s:if>
						<s:else>
							<tr>
								<td align="right">
								考核月份:
							</td>
							<td>
								<input type="hidden"  value="${assScore.userId}" name="assScore.userId"/>
								<input class="Wdate" type="text" name="assScore.asstMouth"
									onClick="WdatePicker({dateFmt:'yyyy-MM月',skin:'whyGreen'})" />
							</td>
							</tr>
						</s:else>
						<tr>
							<td colspan="6" align="center">
								<input type="submit" value="查询"
									style="width: 80px; height: 60px">
								<input type="reset" value="重置" style="width: 80px; height: 60px">
							</td>
						</tr>
					</table>
				</form>

				<table  class="table">
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
							卡号
						</th>
						<th align="center">
							考核月份
						</th>
						<th align="center">
							考核成绩
						</th>
						<th align="center">
							打分人
						</th>
						<th align="center">
							操作
						</th>
					</tr>
					<s:iterator id="pageAssScore" value="assScorewList"
						status="iteratorStatus">
						<s:if test="#iteratorStatus.index%2==1">
							<tr align="center" bgcolor="#e6f3fb"
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'')">
						</s:else>
						<td>
							<s:property value="#iteratorStatus.index+1" />
						</td>
						<td align="center">
							${pageAssScore.userName}
						</td>
						<td align="center">
							${pageAssScore.dept}
						</td>
						<td align="center">
							${pageAssScore.cardId}
						</td>
						<td align="center">
							${pageAssScore.asstMouth}
						</td>
						<td align="center">
							${pageAssScore.percentageScore}分
						</td>
						<td align="center">
							${pageAssScore.assPeople}
						</td>
						<td align="center">
							<a
								href="javascript:sendRequest('AssScoreAction!scoreDetail.action?id=${pageAssScore.id}',sdsView)">打分明细</a>/
							<a
								href="System/renshi/jxkh_showPersonalScore.jsp?cardId=${pageAssScore.cardId}&userCode=${pageAssScore.code}"
								target="_black">分析</a>
							<s:if test="%{#pageStatus=='dept'}">
									/
								<a onclick="return window.confirm('确定要删除该成绩?')"
									href="AssScoreAction!delScore.action?id=${pageAssScore.id}&status=${pageStatus}">删除</a>
							</s:if>
						</td>
					</s:iterator>
					<tr>
						<s:if test="errorMessage==null">
							<td colspan="8" align="right">
								第
								<font color="red"><s:property value="cpage" /> </font> /
								<s:property value="total" />
								页
								<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
									styleClass="page" theme="number" />
							</td>
						</s:if>
						<s:else>
							<td colspan="8" align="center" style="color: red">
								${errorMessage}
							</td>
						</s:else>
					</tr>
				</table>

			</div>
			<font color="red">${successMessage}</font>
			<%
				session.removeAttribute("successMessage");
				session.removeAttribute("errorMessage");
			%>
			<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		</center>
	</body>
</html>