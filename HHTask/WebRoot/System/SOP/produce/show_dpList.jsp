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
<STYLE type="text/css">
td:hover .qs_ul {
	display: block;
}

.qs_ul {
	display: none;
	border: 1px solid #999;
	list-style: none;
	margin: 0;
	padding: 0;
	position: absolute;
	width: auto;
	background: #CCC;
	color: green;
}
.contentDiv {
	position: absolute;
	z-index: 255;
	width: 100%;
	display: none;
}
</STYLE>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng" style="width: 100%;">
		<div id="bodyDiv" align="center" class="transDiv"
				onclick="chageDiv('none')">
			</div>
			<div id="contentDiv"
				style="position: absolute; z-index: 255; width: 100%;display: none;"
				align="center">
				<div id="closeDiv"
					style="position: relative; top: 165px; left: 0px; right: 200px; z-index: 255; background: url(<%=basePath%>/images/bq_bg2.gif); width: 900px;">
					<table style="width: 100%">
						<tr>
							<td>
								<span id="title">不合格品确认操作</span>
							</td>
							<td align="right">
								<img alt="" src="<%=basePath%>/images/closeImage.png" width="30"
									id="closeTcDiv" height="32" onclick="chageDiv('none')">
							</td>
						</tr>
					</table>
					<div id="operatingDiv"
						style="background-color: #ffffff; width: 100%; ">
						<iframe id="xiugaiIframe" src="" marginwidth="0" marginheight="0"
							hspace="0" vspace="0" frameborder="0" scrolling="yes"
							style="width: 98%; height: 1000px; margin: 0px; padding: 0px;"></iframe>

					</div>
				</div>
			</div>
			
			<div align="center">
				<form action="WaigouwaiweiPlanAction!findAllDefectiveProduct.action"
					method="post">
					<table class="table">
						<tr>
							<th align="right">
								件号
							</th>
							<td>
								<input type="text" name="dp.markId" value="${dp.markId}" />
							</td>
							<th align="right">
								版本
							</th>
							<td>
								<input type="text" name="dp.banben" value="${dp.banben}" />
							</td>
						</tr>
						<tr>
							<th align="right">
								检验人
							</th>
							<td>
								<input type="text" name="dp.jyuserName" value="${dp.jyuserName}" />
							</td>
							<th align="right">
								检验人工号
							</th>
							<td>
								<input type="text" name="dp.jyuserCode" value="${dp.jyuserCode}" />
							</td>
						</tr>
						<tr>
							<th align="right">
								类型
							</th>
							<td>
								<SELECT name="dp.type">
									<option value="${dp.type}">
										${dp.type}
									</option>
									<option value=""></option>
									<option value="外购来料不良">
										外购来料不良
									</option>
									<option value="外购在库不良">
										外购在库不良
									</option>
								</SELECT>
							</td>
							<th align="right">
								状态
							</th>
							<td>
								<SELECT name="dp.status">
									<option value="${dp.status}">
										${dp.status}
									</option>
									<option value=""></option>
									<option value="待确认">
										待确认
									</option>
									<option value="待领">
										待领
									</option>
									<option value="已领">
										已领
									</option>
								</SELECT>
							</td>
						</tr>
						<tr>
							<th align="right">
								检验批次
							</th>
							<td>
								<input type="text" name="dp.examineLot" value="${dp.examineLot}" />
							</td>
							<th align="right">
								检验时间
							</th>
							<td>
								<input type="text" name="dp.checkTime" value="${dp.checkTime}"
									class="Wdate"
									onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})" />
							</td>
						</tr>
						<tr>
							<th align="right">
								生成日期从
							</th>
							<td>
								<input type="text" name="firsttime" value="${firsttime}"
									class="Wdate"
									onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})" />
							</td>
							<th align="right">
								止
							</th>
							<td>
								<input type="text" name="endtime" value="${endtime}"
									class="Wdate"
									onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})" />
							</td>
						</tr>
						<tr>
							<th align="right">
								来料类型
							</th>
							<td>
								<s:if test="tag == 'wg'">
									<SELECT name="dp.wlType">
										<option value="外购">外购</option>
									</SELECT>
								</s:if>
								<s:elseif test="tag == 'ww'">
									<SELECT name="dp.wlType">
										<option value="外委">外委</option>
									</SELECT>
								</s:elseif>
								<s:else>
									<SELECT name="dp.wlType">
										<option value="${dp.wlType}">${dp.wlType}</option>
										<option value=""></option>
										<option value="外委">外委</option>
										<option value="外购">外购</option>
									</SELECT>
								</s:else>
							</td>
						</tr>
					</table>
					<input type="hidden" value="${pageStatus}" name="pageStatus" />
					<input type="submit" value="查询" class="input" />
				</form>
				<table class="table">
					<tr align="center" bgcolor="#c0dcf2" height="50px">
						<th>
							序号
						</th>
						<th>
							采购订单号
						</th>
						<th>
							送货单号
						</th>
						<th>
							件号
						</th>
						<th>
							供料属性
						</th>
						<th>
							版本
						</th>
						<th>
							零件名称
						</th>
						<th>
							物料类别
						</th>
						<th>
							检验批次
						</th>
						<th>
							检验员名称
						</th>
						<th>
							检验时间
						</th>
						<th>
							来料数量
						</th>
						<th>
							检验数量
						</th>
<!--						<th>-->
<!--							检验合格数量-->
<!--						</th>-->
						<th>
							检验不合格数量
						</th>
						<th>
							确认接收数量
						</th>
						<th>
							确认退回数量
						</th>
						<th>
							调拨数量
						</th>
						<th>
							确认人
						</th>
						<th>
							确认时间
						</th>
						<th>
							类型
						</th>
						<th>
							状态
						</th>
						<th>
							附件
						</th>
						<th>
							不良描述
						</th>
						<th>
							处理结果
						</th>
						<th>
							审批状态
						</th>
						<td>
							操作
						</td>
					</tr>
					<s:iterator value="list" id="pageList" status="statussdf">
						<s:if test="#statussdf.index%2==1">
							<tr align="center" bgcolor="#e6f3fb"
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'')">
						</s:else>
						<td>
							<s:property value="#statussdf.index+1" />
						</td>
						<td>
							${pageList.cgOrderNum}
						</td>
						<td>
							${pageList.shOrderNum}
						</td>
						<td>
							${pageList.markId}
						</td>
						<td>
							${pageList.kgliao}
						</td>
						<td>
							${pageList.banben}
						</td>
						<td>
							${pageList.proName}
						</td>
						<td>
							${pageList.wgType}
						</td>
						<td>
							${pageList.examineLot}
						</td>
						<td>
							${pageList.jyuserName}
						</td>
						<td>
							${pageList.checkTime}
						</td>
						<td>
							${pageList.llNumber}
						</td>
						<td>
							${pageList.jyNumber}
						</td>
<!--						<td>-->
<!--							${pageList.jyhgNumber}-->
<!--						</td>-->
						<td>
							${pageList.jybhgNumber}
						</td>
						<td>
							${pageList.zjhgNumber}
						</td>
						<td>
							${pageList.zjbhgNumber}
						</td>
						<td>
							${pageList.dbNumber}
						</td>
						<td>
							${pageList.zjUsers}
						</td>
						<td>
							${pageList.zjTime}
						</td>
						<td>
							${pageList.type}
						</td>
						<td>
							${pageList.status}
						</td>
						<td>
							<s:if test="#pageList.fujian!=null">
								<a href="javascript:;" onclick="xiazai('${pageList.fujian}')">下载附件</a><a href="javascript:;" onclick="xiazai('${pageList.fujian}')">下载附件</a>
							</s:if>
							<s:else>
								无附件
							</s:else>
						</td>
						<td style="max-width: 50px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
							<font size="1">${pageList.ramk}</font>
									<ul class="qs_ul">
										<li>
											${pageList.ramk}
										</li>
							</ul>
						</td>
						<td>
							${pageList.result}
						</td>
						<td>
							<a href="CircuitRunAction_findAduitPage.action?id=${pageList.epId}">${pageList.epStatus}</a>
						</td>
						<td>
							<s:if test='#pageList.status == "待确认" && pageStatus == "dqr"  '>
								<a
									href="javascript:;" onclick="tanchu(${pageList.id})">处理</a>
							</s:if>
							<s:else>
								<a
									href="javascript:;" onclick="tanchu(${pageList.id})">明细</a>
							</s:else>
						</td>
						</tr>
					</s:iterator>
					<tr>
						<td colspan="28" align="right">
							第
							<font color="red"><s:property value="cpage" /> </font> /
							<s:property value="total" />
							页
							<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
								styleClass="page" theme="number" />

						</td>
					</tr>
				</table>
			</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
<SCRIPT type="text/javascript">
	function tanchu(num){
		document.getElementById("xiugaiIframe").src = "WaigouwaiweiPlanAction!tobhgth.action?pageStatus=${pageStatus}&id="+ num;
		chageDiv('block')
	}
	//下载
function xiazai(fileName) {
	//对中文进行加密  
	var fileName1 = encodeURI(encodeURI(fileName));
<%--	location.href = "DownAction.action?directory=/upload/file/WaigouDailySheet/&fileName="--%>
<%--			+ fileName1;--%>
	location.href = "FileViewAction.action?FilePath=/upload/file/WaigouDailySheet/"
			+ fileName1;

}

</SCRIPT>
	</body>
</html>
