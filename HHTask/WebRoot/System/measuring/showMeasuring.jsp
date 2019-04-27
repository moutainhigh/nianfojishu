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
	<meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
		<link>
		<%@include file="/util/sonHead.jsp"%>
		<link rel="stylesheet" type="text/css"
			href="<%=basePath%>/css/button.css" />
		<style type="text/css">
input:disabled {
	border: 1px solid #DDD;
	background-color: #F5F5F5;
	color: #ACA899;
}
</style>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="bodyDiv" align="center" class="transDiv"
			onclick="chageDiv('none')">
		</div>
		<div id="contentDiv"
			style="position: absolute; z-index: 255; width: 900px; display: none;"
			align="center">
			<div id="closeDiv"
				style="position: relative; top: 165px; left: 0px; right: 200px; z-index: 255; background: url(<%=basePath%>/images/bq_bg2.gif); width: 900px;">
				<table style="width: 100%">
					<tr>
						<td>
							<span id="title">您正在对不合格品缺陷类型进行操作</span>
						</td>
						<td align="right">
							<img alt="" src="<%=basePath%>/images/closeImage.png" width="30"
								id="closeTcDiv" height="32" onclick="chageDiv('none')">
						</td>
					</tr>
				</table>
				<div id="operatingDiv"
					style="background-color: #ffffff; width: 100%;">
					<iframe id="xiugaiIframe" src="" marginwidth="0" marginheight="0"
						hspace="0" vspace="0" frameborder="0" scrolling="yes"
						style="width: 98%; height: 500px; margin: 0px; padding: 0px;"></iframe>

				</div>
			</div>
		</div>
		<div id="gongneng">
			<div align="center">
				<font style="font-size: 22px;font-weight: bolder;">
					<s:if test="tag=='lj'">
						量具管理
					</s:if>
					<s:elseif test="tag=='jj'">
						检具管理
					</s:elseif>	
					<s:elseif test="tag=='gj'">
						工具管理
					</s:elseif>
				</font>
					<s:if test="(flag==null||flag!='showhas' ) && (tag==null || tag!='pingmu')">
						<form action="MeasuringAction_saveMeasuring.action" method="post">
							<table class="table">
								<tr>
									<td align="right">
										名称
									</td>
									<td>
										<input type="text" name="measuring.matetag" />
									</td>
									<td align="right">
										本厂编号
										<td>
											<input type="text" name="measuring.measuring_no" />
										</td>
									</td>
									<td align="right">
										位置
									</td>
									<td>
										<input type="text" name="measuring.place" />
									</td>
								</tr>
								<tr>
									<td align="right">
										报检状态
									</td>
									<td>
										<select name="measuring.calibrationstate">
											<s:if test='text=="preson"'>
												<option value=""></option>
												<option value="待校检">
													待校检
												</option>
											</s:if>
											<s:else>
												<option value="${measuring.calibrationstate}">
													${measuring.calibrationstate}
												</option>
												<option value=""></option>
												<option value="正常">
													正常
												</option>
												<option value="待校检">
													待校检
												</option>
												<option value="校检中">
													校检中
												</option>
												<option value="报废">
													报废
												</option>
											</s:else>
										</select>
									</td>
									<td align="right">
										校准类型
									</td>
									<td>
										<select name="measuring.jztype">
											<option value="${measuring.jztype}">
												${measuring.jztype}
											</option>
											<option value="内校">
												内校
											</option>
											<option value="外校">
												外校
											</option>
										</select>
									</td>
									<td align="right">
										分类
									</td>
									<td>
										<input type="text" value="${measuring.parClass}"
											name="measuring.parClass" />
									</td>

								</tr>
								<tr>
									<td align="right">
										负责人
									</td>
									<td>
										<input type="text" value="${measuring.personliable}"
											name="measuring.personliable" />
									</td>
									<td align="right">
										仓库
									</td>
									<td>
										<input type="text" value="${measuring.storehouse}" name="measuring.storehouse">
									</td>
									<td align="right">
										
									</td>
									<td>
										
									</td>
								</tr>
								<tr>
									<td colspan="10" align="center">
										<input type="hidden" value="${tag}" name="tag" />
										<input type="hidden" value="${text}" name="text" />
										<input type="submit" style="width: 100px; height: 40px;"
											value="查询" class="button0 blue" />
										<input type="button" onclick="tanchu()"
											style="width: 100px; height: 40px;" value="添加"
											class="button0 blue" />
										<input type="button" onclick="exportExcel(this.form);todisabledone(this)" data="downData"
											style="width: 100px; height: 40px;" value="导出"
											class="button0 blue" />
										<input type="button" onclick="window.open('MeasuringAction_findALlCheckrecord.action?tag=${tag}')"
											style="width: 100px; height: 40px;" value="检验明细"
											class="button0 blue" />
									</td>
								</tr>
							</table>
						</form>
					</s:if>
				<!-- ------------------------------------------------------------- -->
				<table width="100%" border="0" class="table"
					style="border-collapse: collapse;">
					<tr bgcolor="#c0dcf2" height="50px">
						<td align="center">
							序号
						</td>
						<td align="center">
							本体编号
						</td>
						<td align="center">
							本厂编号
						</td>
						<td align="center">
							名称
						</td>
						<td align="center">
							规格
						</td>
						<td align="center">
							测试精度
						</td>
						<td align="center">
							分类
						</td>
						<td align="center">
							仓库
						</td>
						<%--
					--%>
<!-- 						<td align="center"> -->
<!-- 							库存 -->
<!-- 						</td> -->
						<td align="center">
							位置
						</td>
						<td align="center">
							当前量
						</td>
						<td align="center">
							校准周期(天)
						</td>
						<td align="center">
							校准时间
						</td>
						<td align="center">
							下次校准时间
						</td>
						<td align="center">
							校准状态
						</td>
						<td align="center">
							校准类型
						</td>
						<td align="center">
							责任人
						</td>
						<s:if test="tag==null || tag!='pingmu'">
							<td align="center">
								预览附件
							</td>
							<td align="center">
								操作
							</td>
							<td></td>
						</s:if>
					</tr>

					<!-- ------待校检状态--------------- -->
					<s:iterator id="pageList" value="dMeasuringList"
						status="pageStatus">
						<s:if test="#pageStatus.first">
							<tr bgcolor="red">
								<th colspan="20" align="center">
									<font color="#ffffff">待校检信息 </font>
								</th>
							</tr>
						</s:if>
						<s:if test="#pageStatus.index%2==1">
							<tr align="center" bgcolor="#e6f3fb"
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this,'red')"
								onmouseout="outBgcolor(this,'')">
						</s:else>
						<td>
							<s:if test="#pageStatus.index%2==1">
								<font>
							</s:if>
							<s:else>
								<font color="red">
							</s:else>
							<s:property value="#pageStatus.index+1" />
							</font>
						</td>
						<td>
							${pageList.number}
						</td>
						<td>
							${pageList.measuring_no}
						</td>
						<td>
							${pageList.matetag }
						</td>
						<td>
							${pageList.format}
						</td>
						<td>
							${pageList.csjd}
						</td>
						<td>
							${pageList.parClass}
						</td>
						<td>
							${pageList.storehouse }
						</td>
							<td>${pageList.place }</td><!-- 位置 -->
						<td>
							<fmt:formatNumber value="${pageList.curAmount}" pattern="#" />
						</td>
						<td>
							${pageList.period }
						</td>
						<td>
							${pageList.calibrationTime}
						</td>
						<td>
							${pageList.nextcalibrationTime}
						</td>
						<td>
							${pageList.calibrationstate}
						</td>
						<td>
							${pageList.jztype}
						</td>
						<td>
							${pageList.personliable}
						</td>
						<s:if test="tag==null || tag!='pingmu'">
							<td>
								<s:if test='#pageList.fileName!=null && #pageList.fileName!="" '>
									<%--							<a href="<%=basePath %>/upload/file/Checkrecord/${pageList.fileName}" class="button blue">校验报告</a>--%>
									<a
										href="FileViewAction.action?FilePath=/upload/file/Checkrecord/${pageList.fileName}"
										class="button0 blue">校验报告</a>
								</s:if>
								<s:else>
									<input type="button" class="button0" value="校验报告"
										disabled="disabled" />
								</s:else>
							</td>
							<td>
								<a
										href="javascritp:;" onclick="showQRcode('${pageList.id}','${pageList.measuring_no}')"
										class="button0 blue">二维码</a>
								<a
									href="MeasuringAction_findMeasuringById.action?MeasuringId=${pageList.id}&text=1"
									class="button0 blue">校检</a>
								<a
									href="MeasuringAction_findMeasuringById.action?MeasuringId=${pageList.id }&text=2"
									class="button0 blue">修改周期</a>
								<a
									href="MeasuringAction_delmeasuring.action?measuring.id=${pageList.id}&tag=${tag}"
									class="button0 blue" onclick="return confirm('确定要删除吗？')">删除</a>
							</td>
						</s:if>
						</tr>
					</s:iterator>
					<!-- ------------------------- -->
					<!-- ------校检中状态--------------- -->
					<s:iterator id="pageList" value="zMeasuringList"
						status="pageStatus">
						<s:if test="#pageStatus.first">
							<tr bgcolor="yellow">
								<th colspan="20" align="center">
									<font color="#000">校检中信息 </font>
								</th>
							</tr>
						</s:if>
						<s:if test="#pageStatus.index%2==1">
							<tr align="center" bgcolor="#e6f3fb"
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this,'red')"
								onmouseout="outBgcolor(this,'')">
						</s:else>
						<td>
							<s:if test="#pageStatus.index%2==1">
								<font>
							</s:if>
							<s:else>
								<font color="red">
							</s:else>
							<s:property value="#pageStatus.index+1" />
							</font>
						</td>
						<td>
							${pageList.number}
						</td>
						<td>
							${pageList.measuring_no}
						</td>
						<td>
							${pageList.matetag }
						</td>
						<td>
							${pageList.format}
						</td>
						<td>
							${pageList.csjd}
						</td>
						<td>
							${pageList.parClass}
						</td>
						<td>
							${pageList.storehouse }
						</td>
						<td>${pageList.place }</td><!-- 位置 -->
						<td>
							<fmt:formatNumber value="${pageList.curAmount}" pattern="#" />
						</td>
						<td>
							${pageList.period }
						</td>
						<td>
							${pageList.calibrationTime}
						</td>
						<td>
							${pageList.nextcalibrationTime}
						</td>
						<td>
							${pageList.calibrationstate}
						</td>
						<td>
							${pageList.jztype}
						</td>
						<td>
							${pageList.personliable}
						</td>
						<s:if test="tag==null || tag!='pingmu'">
							<td>
								<s:if test='#pageList.fileName!=null && #pageList.fileName!="" '>
									<%--							<a href="<%=basePath %>/upload/file/Checkrecord/${pageList.fileName}" class="button blue">校验报告</a>--%>
									<a
										href="FileViewAction.action?FilePath=/upload/file/Checkrecord/${pageList.fileName}"
										class="button0 blue">校验报告</a>
								</s:if>
								<s:else>
									<input type="button" class="button0" value="校验报告"
										disabled="disabled" />
								</s:else>
							</td>
							<td>
								<s:if
									test="#pageList.calibrationstate=='正常' &&#pageList.curAmount>=1E-7">
									<a
										href="MeasuringAction_findMeasuringById.action?MeasuringId=${pageList.id}&cpage=${cpage}"
										class="button0 blue">校检</a>
								</s:if>
								<s:else>
									<input type="button" class="button0" value="校验"
										disabled="disabled" />
								</s:else>
								<s:if test="#pageList.calibrationstate=='校检中'">
									<a
										href="MeasuringAction_findMeasuringById.action?MeasuringId=${pageList.id}&text=1&cpage=${cpage}"
										class="button0 blue">确认校检</a>
								</s:if>
								<s:else>
									<input type="button" class="button0" value="确认校检"
										disabled="disabled" />
								</s:else>
								<s:if
									test="#pageList.calibrationTime!=null &&#pageList.calibrationstate=='正常'">
									<a
										href="MeasuringAction_findMeasuringdetail.action?MeasuringId=${pageList.id}&cpage=${cpage}"
										class="button0 blue">明细</a>
								</s:if>
								<s:else>
									<input type="button" class="button0" value="明细"
										disabled="disabled" />
								</s:else>
								<s:if test="#pageList.calibrationstate=='报废'">
									<a
										href="MeasuringAction_findMeasuringdetail.action?MeasuringId=${pageList.id}&t=1&cpage=${cpage}"
										class="button0 blue">明细</a>
								</s:if>
								<a
										href="javascritp:;" onclick="showQRcode('${pageList.id}','${pageList.measuring_no}')"
										class="button0 blue">二维码</a>
								<a
									href="MeasuringAction_findMeasuringById.action?MeasuringId=${pageList.id}&t=3&cpage=${cpage}"
									class="button0 blue">修改</a>
								<a
									href="MeasuringAction_delmeasuring.action?measuring.id=${pageList.id}&tag=${tag}"
									class="button0 blue" onclick="return confirm('确定要删除吗？')">删除</a>
							</td>
							</tr>
						</s:if>
						</tr>
					</s:iterator>

					<!-- ----------量具信息----------------- -->
					<s:iterator value="maps" id="pageList" status="pageStatus">
						<s:if test="#pageStatus.first">
							<tr bgcolor="green">
								<th colspan="22" align="center">
									<font color="#ffffff">已校验</font>
								</th>
							</tr>
						</s:if>
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
						<td>
							${pageList.number}
						</td>
						<td>
							${pageList.measuring_no}
						</td>
						<td>
							${pageList.matetag }
						</td>
						<td>
							${pageList.format}
						</td>
						<td>
							${pageList.csjd}
						</td>
						<td>
							${pageList.parClass}
						</td>
						<td>
							${pageList.storehouse }
						</td>
						<td>${pageList.place }</td><!-- 位置 -->
						<td>
							<fmt:formatNumber value="${pageList.curAmount}" pattern="#" />
						</td>
						<td>
							${pageList.period }
						</td>
						<td>
							${pageList.calibrationTime}
						</td>
						<td>
							${pageList.nextcalibrationTime}
						</td>
						<td>
							${pageList.calibrationstate}
						</td>
						<td>
							${pageList.jztype}
						</td>
						<td>
							${pageList.personliable}
						</td>
						<s:if test="tag==null || tag!='pingmu'">
							<td>
								<s:if test='#pageList.fileName!=null && #pageList.fileName!="" '>
									<%--							<a href="<%=basePath %>/upload/file/Checkrecord/${pageList.fileName}" class="button blue">校验报告</a>--%>
									<a
										href="FileViewAction.action?FilePath=/upload/file/Checkrecord/${pageList.fileName}"
										class="button0 blue">校验报告</a>
								</s:if>
								<s:else>
									<input type="button" class="button0" value="校验报告"
										disabled="disabled" />
								</s:else>
							</td>
							<td>
								<s:if
									test="#pageList.calibrationstate=='正常' &&#pageList.curAmount>=1E-7">
									<a
										href="MeasuringAction_findMeasuringById.action?MeasuringId=${pageList.id}&cpage=${cpage}"
										class="button0 blue">校检</a>
								</s:if>
								<s:else>
									<input type="button" class="button0" value="校验"
										disabled="disabled" />
								</s:else>
								<s:if test="#pageList.calibrationstate=='校检中'">
									<a
										href="MeasuringAction_findMeasuringById.action?MeasuringId=${pageList.id}&text=1&cpage=${cpage}"
										class="button0 blue">确认校检</a>
								</s:if>
								<s:else>
									<input type="button" class="button0" value="确认校检"
										disabled="disabled" />
								</s:else>
								<s:if
									test="#pageList.calibrationTime!=null &&#pageList.calibrationstate=='正常'">
									<a
										href="MeasuringAction_findMeasuringdetail.action?MeasuringId=${pageList.id}&cpage=${cpage}"
										class="button0 blue">明细</a>
								</s:if>
								<s:else>
									<input type="button" class="button0" value="明细"
										disabled="disabled" />
								</s:else>
								<s:if test="#pageList.calibrationstate=='报废'">
									<a
										href="MeasuringAction_findMeasuringdetail.action?MeasuringId=${pageList.id}&t=1&cpage=${cpage}"
										class="button0 blue">明细</a>
								</s:if>
								<a
										href="javascritp:;" onclick="showQRcode('${pageList.id}','${pageList.measuring_no}')"
										class="button0 blue">二维码</a>
								<a
									href="MeasuringAction_findMeasuringById.action?MeasuringId=${pageList.id}&t=3&cpage=${cpage}"
									class="button0 blue">修改</a>
								<a
									href="MeasuringAction_delmeasuring.action?measuring.id=${pageList.id}&tag=${tag}"
									class="button0 blue" onclick="return confirm('确定要删除吗？')">删除</a>
							</td>
							</tr>
						</s:if>
					</s:iterator>
					</tr>
					<s:if test="measuring!=null&&measuring.id!=null">
					<tr align="center" bgcolor="#e6f3fb"
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						<td>
							1
						</td>
						<td>
							${measuring.number}
						</td>
						<td>
							${measuring.measuring_no}
						</td>
						<td>
							${measuring.matetag }
						</td>
						<td>
							${measuring.format}
						</td>
						<td>
							${measuring.csjd}
						</td>
						<td>
							${measuring.parClass}
						</td>
						<td>
							${measuring.storehouse }
						</td>
						<td>${measuring.place }</td><!-- 位置 -->
						<td>
							<fmt:formatNumber value="${measuring.curAmount}" pattern="#" />
						</td>
						<td>
							${measuring.period }
						</td>
						<td>
							${measuring.calibrationTime}
						</td>
						<td>
							${measuring.nextcalibrationTime}
						</td>
						<td>
							${measuring.calibrationstate}
						</td>
						<td>
							${measuring.jztype}
						</td>
						<td>
							${measuring.personliable}
						</td>
						<s:if test="tag==null || tag!='pingmu'">
							<td>
								<s:if test='#pageList.fileName!=null && #pageList.fileName!="" '>
									<%--							<a href="<%=basePath %>/upload/file/Checkrecord/${pageList.fileName}" class="button blue">校验报告</a>--%>
									<a
										href="FileViewAction.action?FilePath=/upload/file/Checkrecord/${measuring.fileName}"
										class="button0 blue">校验报告</a>
								</s:if>
								<s:else>
									<input type="button" class="button0" value="校验报告"
										disabled="disabled" />
								</s:else>
							</td>
						</s:if>
							<td>
								<s:if
									test="#pageList.calibrationstate=='正常' &&#pageList.curAmount>=1E-7">
									<a
										href="MeasuringAction_findMeasuringById.action?MeasuringId=${measuring.id}&cpage=${cpage}"
										class="button0 blue">校检</a>
								</s:if>
								<s:else>
									<input type="button" class="button0" value="校验"
										disabled="disabled" />
								</s:else>
								<s:if test="#pageList.calibrationstate=='校检中'">
									<a
										href="MeasuringAction_findMeasuringById.action?MeasuringId=${measuring.id}&text=1&cpage=${cpage}"
										class="button0 blue">确认校检</a>
								</s:if>
								<s:else>
									<input type="button" class="button0" value="确认校检"
										disabled="disabled" />
								</s:else>
								<s:if
									test="#pageList.calibrationTime!=null &&#pageList.calibrationstate=='正常'">
									<a
										href="MeasuringAction_findMeasuringdetail.action?MeasuringId=${measuring.id}&cpage=${cpage}"
										class="button0 blue">明细</a>
								</s:if>
								<s:else>
									<input type="button" class="button0" value="明细"
										disabled="disabled" />
								</s:else>
								<s:if test="#pageList.calibrationstate=='报废'">
									<a
										href="MeasuringAction_findMeasuringdetail.action?MeasuringId=${measuring.id}&t=1&cpage=${cpage}"
										class="button0 blue">明细</a>
								</s:if>
								<a
										href="javascritp:;" onclick="showQRcode('${measuring.id}','${measuring.measuring_no}')"
										class="button0 blue">二维码</a>
								<a
									href="MeasuringAction_findMeasuringById.action?MeasuringId=${measuring.id}&t=3&cpage=${cpage}"
									class="button0 blue">修改</a>
								<a
									href="MeasuringAction_delmeasuring.action?measuring.id=${measuring.id}&tag=${tag}"
									class="button0 blue" onclick="return confirm('确定要删除吗？')">删除</a>
							</td>
							</tr>
						</s:if>
					<tr>
						<s:if test="errorMessage==null">
							<td colspan="20" align="right">
								第
								<font color="red"><s:property value="cpage" /> </font> /
								<s:property value="total" />
								页
								<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
									styleClass="page" theme="number" />
						</s:if>
						<s:else>
							<td colspan="20" align="center" style="color: red">
								${errorMessage}
						</s:else>
						</td>
					</tr>
				</table>
			</div>
			<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		</center>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript"
			src="<%=request.getContextPath()%>/javascript/jquery-1.8.3.js">
</script>
		<script type="text/javascript">
//	$(function(){
//		var x1 = document.getElementById("price").innerText;  
//		 x1 = parseInt(x1*10)/10;
//		alert("x1="+x1);  
//	})
function add() {
	window.location.href = "System/gzbj/addgzbj.jsp";
}
function tanchu() {
	document.getElementById("xiugaiIframe").src = "<%=basePath%>System/measuring/measuring_add.jsp?tag=${tag}";
	chageDiv('block');
}

function showQRcode(id,no){
	document.getElementById("xiugaiIframe").src = "<%=basePath%>System/measuring/measuring_QRcode.jsp?id="+id+"&no="+no+"&tag=${tag}";
	chageDiv('block');
}

function exportExcel(obj) {//
	obj.action = "MeasuringAction_exportExcel.action";
	obj.submit();
	obj.action = "MeasuringAction_saveMeasuring.action";
}
</script>
	</body>
</html>