<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat"%>
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
		<div>
			<div align="center" style="border: 1px solid #00000;">
				<div id="rootTemplateDiv">
					<div>
						<div align="center">
							<h3>
								<font color="red">${successMessage}</font>
								<font color="red">${errorMessage}</font>
							</h3>
						</div>
						<form action="ProcardAction!findAllProcards1.action" method="post"
							id="form">
							<input type="hidden" name="viewStatus" value="${viewStatus}" />
							<input type="hidden" name="pageStatus" value="${pageStatus}" />
							<table class="table">
								<tr>
									<th colspan="6">
										流水单管理(Single water management)
									</th>
								</tr>
								<tr>
									<th align="right">
										件号:
										<br />
										Part No. :
									</th>
									<td>
										<input name="procard.markId" value="${procard.markId}" />
									</td>
									<th align="right">
										名称:
										<br />
										Name :
									</th>
									<td>
										<input name="procard.proName" value="${procard.proName}" />
									</td>
									<th align="right">
										批次:
										<br />
										Batch :
									</th>
									<td>
										<input name="procard.selfCard" value="${procard.selfCard}" />
									</td>
								</tr>
								<tr>
									<th align="right">
										卡片类型:
										<br />
										Card Type :
									</th>
									<td>
										<select name="procard.procardStyle" style="width: 155px;">
											<option>
												${procard.procardStyle}
											</option>
											<option></option>
											<option>
												总成
											</option>
											<option>
												组合
											</option>
											<option>
												外购
											</option>
											<option>
												自制
											</option>
										</select>
									</td>
									<th align="right">
										产品类型:
										<br />
										Product Type :
									</th>
									<td>
										<select name="procard.productStyle" style="width: 155px;">
											<option>
												${procard.productStyle}
											</option>
											<option></option>
											<option>
												试制
											</option>
											<option>
												批产
											</option>
										</select>
									</td>
									<th align="right">
										状态:
										<br />
										State :
									</th>
									<td>
										<select name="procard.status" style="width: 155px">
											<option>
												${procard.status}
											</option>
											<option></option>
											<option>
												初始
											</option>
											<option>
												已发卡
											</option>
											<option>
												已发料
											</option>
											<option>
												领工序
											</option>
											<option>
												完成
											</option>
											<option>
												入库
											</option>
										</select>
									</td>
								</tr>
								<tr>
									<th align="right">
										计划单号:
										<br />
										Single number plan :
									</th>
									<td>
										<input name="procard.planOrderNum"
											value="${procard.planOrderNum}" />
									</td>
									<th align="right">
										卡号:
										<br />
										Card number :
									</th>
									<td>
										<input name="procard.cardNum" value="${procard.cardNum}" />
									</td>
								</tr>
								<tr>
									<th align="right">
										起始时间:
										<br />
										start time :
									</th>
									<td>
										<input type="text" class="Wdate" name="startDate"
											value="${startDate}" id="startDate"
											onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',skin:'whyGreen'})" />
									</td>
									<th align="right">
										结束时间:
										<br />
										end time :
									</th>
									<td>
										<input type="text" class="Wdate" name="endDate"
											value="${endDate}" id="endDate"
											onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',skin:'whyGreen'})" />
									</td>
								</tr>
								<tr>
									<th colspan="6">
										<input type="submit" value="查询" class="input" />
										<input type="reset" value="清空" class="input" />
										<input type="button" value="导出EXCEL" class="input"
											onclick="getExcel();todisabledone(this)" data="downData" />
									</th>
								</tr>
							</table>
						</form>
						<table class="table">
							<tr bgcolor="#c0dcf2" height="50px">
								<th align="center">
									序号
									<br />
									No.
								</th>
								<th align="center">
									件号
									<br />
									Part No.
								</th>
								<th align="center">
									名称
									<br />
									Name
								</th>
								<th align="center">
									卡片类型
									<br />
									Card Type
								</th>
								<th align="center">
									产品类型
									<br />
									Product Type
								</th>
								<th align="center">
									批次
									<br />
									Batch
								</th>
								<s:if test="pageStatus=='noCard'">
									<th align="center">
										产品开始时间
									</th>
									<th align="center">
										入库时间
									</th>
								</s:if>
								<s:else>
									<th align="center">
										制卡时间
										<br />
										Card time
									</th>
								</s:else>
								<th align="center">
									数量
									<br />
									Quantity
								</th>
								<th align="center">
									状态
									<br />
									State
								</th>
								<th align="center" colspan="2">
									操作
									<br />
									Operation
								</th>
							</tr>
							<s:iterator value="procardList" id="pageProcard"
								status="pageStatus">
								<s:if test="#pageStatus.first">
									<tr>
										<th colspan="11" bgcolor="red" style="color: #ffffff">
											生产中产品
										</th>
									</tr>
								</s:if>
								<s:if test="#pageStatus.index%2==1">
									<tr align="center" bgcolor="#e6f3fb" style="height: 50px;"
										onmouseover="chageBgcolor(this)"
										onmouseout="outBgcolor(this,'#e6f3fb')">
								</s:if>
								<s:else>
									<tr align="center" onmouseover="chageBgcolor(this)"
										style="height: 50px;" onmouseout="outBgcolor(this,'')">
								</s:else>
								<td>
									<s:property value="#pageStatus.index+1" />
								</td>
								<td>
									<a title="查看工序"
										href="ProcardAction!findProcardByRunCard.action?id=${pageProcard.id}&pageStatus=history">${pageProcard.markId}</a>
								</td>
								<td>
									${pageProcard.proName}
								</td>
								<td>
									${pageProcard.procardStyle}
								</td>
								<td>
									${pageProcard.productStyle}
								</td>
								<td>
									${pageProcard.selfCard}
								</td>
								<s:if test="pageStatus=='noCard'">
									<td>
										${pageProcard.jihuoDate}
									</td>
									<td>
										${pageProcard.needFinalDate}
									</td>
								</s:if>
								<s:else>
									<td align="center">
										${pageProcard.procardTime}
									</td>
								</s:else>
								<td>
									${pageProcard.filnalCount}
								</td>
								<td>
									${pageProcard.status}
								</td>
								<td colspan="2">
									<s:if test="list!=null">
										<s:if
											test="#pageProcard.procardStyle=='总成'&&#pageProcard.status!='入库'">
											<a
												href="ProcardAction!findWgWwPlan.action?id=${pageProcard.rootId}"
												target="showPlanView">采购计划</a>/</s:if>
										<a
											href="ProcardAction!findProcardView.action?id=${pageProcard.rootId}&pageStatus=history&viewStatus=${viewStatus}"
											target="showProView">生产进度</a>
										<s:if test="rootId!=null">
											<a
												onclick="if(window.confirm('本操作将还原计划数量,并删除整个bom数据,是否继续？')){window.location.href = 'ProcardAction!deleteprocardtree.action?id=${pageProcard.rootId}&pageStatus=${pageStatus}&viewStatus=${viewStatus}'};"
												target="showProView">删除</a>
										</s:if>
										<s:else>
											<a
												onclick="if(window.confirm('本操作将还原计划数量,并删除整个bom数据,是否继续？')){window.location.href = 'ProcardAction!deleteprocardtree.action?id=${pageProcard.id}&pageStatus=${pageStatus}&viewStatus=${viewStatus}'};"
												target="showProView">删除</a>
										</s:else>
									</s:if>
									<s:else>
										<a
											href="ProcardAction!findProcardForCard.action?id=${pageProcard.rootId}">已发卡号</a>
										<br />
										<a
											href="ProcardAction!findProcardView.action?id=${pageProcard.rootId}&pageStatus=history&viewStatus=${viewStatus}"
											target="showProView">生产进度</a>
										<s:if
											test="#pageProcard.procardStyle=='总成'&&#pageProcard.status!='入库'">
											<s:if test="#pageProcard.rootId!=null">
												<a
													onclick="if(window.confirm('本操作将还原计划数量,并删除整个bom数据,是否继续？')){window.location.href = 'ProcardAction!deleteprocardtree.action?id=${pageProcard.rootId}&pageStatus=${pageStatus}&viewStatus=${viewStatus}'};"
													target="showProView"> 删除</a>
											</s:if>
											<s:else>
												<a
													onclick="if(window.confirm('本操作将还原计划数量,并删除整个bom数据,是否继续？')){window.location.href = 'ProcardAction!deleteprocardtree.action?id=${pageProcard.id}&pageStatus=${pageStatus}&viewStatus=${viewStatus}'};"
													target="showProView"> 删除</a>
											</s:else>
										</s:if>
									</s:else>
									<s:if test="#pageProcard.jihuoDate!=null">
										<a href="ProcardAction!findPeople.action?id=${pageProcard.id}">
											领取人员</a>
									</s:if>
								</td>
								</tr>
							</s:iterator>
							<tr>
								<s:if test="errorMessage==null">
									<td colspan="11" align="right">
										第
										<font color="red"><s:property value="cpage" /> </font> /
										<s:property value="total" />
										页
										<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
											styleClass="page" theme="number" />
								</s:if>
								<s:else>
									<td colspan="11" align="center" style="color: red">
								</s:else>
								</td>
							</tr>
						</table>
					</div>
				</div>
				<br>
			</div>
			<%@include file="/util/foot.jsp"%>
			</center>
			<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
			<SCRIPT type="text/javascript">
		function getExcel(){
		 var startDate=document.getElementById("startDate").value;
		 var endDate=document.getElementById("endDate").value;
		 if(startDate==""||endDate==""){
			 alert("请选择起始和结束时间");
			 return false;
		 }
		 var form=document.getElementById("form");
		 form.action="ProcardAction!geExcel.action?cpage=${cpage}";
		 form.submit();
		 form.action="ProcardAction!findAllProcards.action";
		}
		</SCRIPT>
	</body>
</html>
