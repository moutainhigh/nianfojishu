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
	</head>
	<body>
		<center>
			<%--<div id="printDiv">
				<table class="table" style="margin-top: 4px; margin-left: 10px;">
					<tr>
						<th colspan="11">
							<h2>
								${companyInfo.name}
							</h2>
						</th>
					</tr>
					<tr>
						<th colspan="2">
							${wage.mouth}工资单
						</th>
						<th></th>
						<th>
							姓名:
						</th>
						<th>
							${wage.userName}
						</th>
						<th></th>
						<th>
							工号:
						</th>
						<th>
							${wage.code}
						</th>
						<th></th>
						<th>
							部门:
						</th>
						<th>
							${wage.dept}
						</th>
					</tr>
					<tr>
						<td colspan="11" style="height: 40px;">
						</td>
					</tr>
					<tr>
						<th>
							岗位工资
						</th>
						<th>
							保密津贴
						</th>
						<th>
							补贴
						</th>
						<th>
							绩效考核工资
						</th>
						<th>
							应发工资
						</th>
						<th>
							技能工资
						</th>
						<th>
							特殊补贴
						</th>
						<th>
							奖金
						</th>
						<th>
							加班费
						</th>
						<th>
							其他
						</th>
						<th>
							病事旷等
						</th>
					</tr>
					<tr>
						<th>
							${wage.gangweigongzi}
						</th>
						<th>
							${wage.baomijintie}
						</th>
						<th>
							${wage.dianhuabutie}
						</th>
						<th>
							${wage.jixiaokaohegongzi}
						</th>
						<th>
							${wage.yingfagongzi}
						</th>
						<th>
							${wage.jinenggongzi}
						</th>
						<th>
							${wage.gonglinggongzi}
						</th>
						<th>
							${wage.jiangjin}
						</th>
						<th>
							${wage.jiabanfei}
						</th>
						<th>
							${wage.other}
						</th>
						<th>
							${wage.bingshikangdeng}
						</th>
					</tr>
					<tr>
						<th>
							养老保险
						</th>
						<th>
							医疗保险
						</th>
						<th>
							失业保险
						</th>
						<th>
							公积金
						</th>
						<th>
							午餐费
						</th>
						<th>
							补差
						</th>
						<th>
							房租费
						</th>
						<th>
							补发(补扣)工资
						</th>
						<th>
							应交税金
						</th>
						<th colspan="2">
							实发工资
						</th>
					</tr>
					<tr>
						<th>
							${wage.tongchoujin}
						</th>
						<th>
							${wage.yiliaobaoxian}
						</th>
						<th>
							${wage.shiyebaoxian}
						</th>
						<th>
							${wage.gongjijin}
						</th>
						<th>
							${wage.wucanfei}
						</th>
						<th>
							${wage.buchagongzi}
						</th>
						<th>
							${wage.fangzufei}
						</th>
						<th>
							${wage.bfgongzi}
						</th>
						<th>
							${wage.yingjiaoshuijin}
						</th>
						<th colspan="2">
							${wage.shifagongzi}
						</th>
					</tr>
				</table>
			</div>
			<input class="input" value="打印(A4)" onclick="pagePrint('printDiv')"
				type="button">

			<div id="printDiv2">
				<table class="table" style="margin-top: 4px; margin-left: 10px;">
					<tr>
						<th colspan="4">
							<h2>
								${companyInfo.name}
							</h2>
						</th>
					</tr>
					<tr>
						<th colspan="4">
							${wage.mouth}工资单
						</th>
					</tr>
					<tr>
						<th>
							姓名:
						</th>
						<th>
							${wage.userName}
						</th>
						<th>
							工号:
						</th>
						<th>
							${wage.code}
						</th>
					</tr>
					<tr>
						<th colspan="2">
							部门:
						</th>
						<th colspan="2">
							${wage.dept}
						</th>
					</tr>
					<tr>
						<td colspan="4" style="height: 20px;">
						</td>
					</tr>
					<tr>
						<th>
							岗位工资
						</th>
						<th>
							保密津贴
						</th>
						<th>
							补贴
						</th>
						<th>
							绩效考核工资
						</th>
					</tr>
					<tr>
						<th>
							${wage.gangweigongzi}
						</th>
						<th>
							${wage.baomijintie}
						</th>
						<th>
							${wage.dianhuabutie}
						</th>
						<th>
							${wage.jixiaokaohegongzi}
						</th>
					</tr>
					<tr>
						<th>
							应发工资
						</th>
						<th>
							技能工资
						</th>
						<th>
							特殊补贴
						</th>
						<th>
							奖金
						</th>
					</tr>
					<tr>
						<th>
							${wage.yingfagongzi}
						</th>
						<th>
							${wage.jinenggongzi}
						</th>
						<th>
							${wage.gonglinggongzi}
						</th>
						<th>
							${wage.jiangjin}
						</th>
					</tr>
					<tr>
						<th>
							加班费
						</th>
						<th>
							其他
						</th>
						<th>
							病事旷等
						</th>
						<th>
							养老保险
						</th>
					</tr>
					<tr>
						<th>
							${wage.jiabanfei}
						</th>
						<th>
							${wage.other}
						</th>
						<th>
							${wage.bingshikangdeng}
						</th>
						<th>
							${wage.tongchoujin}
						</th>
					</tr>
					<tr>
						<th>
							医疗保险
						</th>
						<th>
							失业保险
						</th>
						<th>
							公积金
						</th>
						<th>
							午餐费
						</th>
					</tr>
					<tr>
						<th>
							${wage.yiliaobaoxian}
						</th>
						<th>
							${wage.shiyebaoxian}
						</th>
						<th>
							${wage.gongjijin}
						</th>
						<th>
							${wage.wucanfei}
						</th>
					</tr>
					<tr>
						<th>
							补差
						</th>
						<th>
							房租费
						</th>
						<th>
							补发(补扣)工资
						</th>
						<th>
							应交税金
						</th>
					</tr>
					<tr>
						<th>
							${wage.buchagongzi}
						</th>
						<th>
							${wage.fangzufei}
						</th>
						<th>
							${wage.bfgongzi}
						</th>
						<th>
							${wage.yingjiaoshuijin}
						</th>
					</tr>
					<tr>
						<th colspan="2">
							实发工资
						</th>
						<th colspan="2">
							${wage.shifagongzi}
						</th>
					</tr>
				</table>
			</div>
			<input class="input" value="打印(2排)" onclick="pagePrint('printDiv2')"
				type="button">
			--%>
			<div id="printDiv3">
				<table class="table" style="margin-top: 4px; margin-left: 4px;">
					<tr>
						<th colspan="2">
							<h2>
								${companyInfo.name}
							</h2>
						</th>
					</tr>
					<tr>
						<th colspan="2">
							${wage.mouth}工资单
						</th>
					</tr>
					<tr>
						<th colspan="2">
							${wage.dept}-${wage.code}-${wage.userName}
						</th>
					</tr>
					<tr>
						<th>
							岗位工资
						</th>
						<th>
							${wage.gangweigongzi}
						</th>
					</tr>
					<tr>
						<th>
							保密津贴
						</th>
						<th>
							${wage.baomijintie}
						</th>
					</tr>
					<tr>
						<th>
							补贴
						</th>
						<th>
							${wage.dianhuabutie}
						</th>
					</tr>
					<tr>
						<th>
							绩效考核工资
						</th>
						<th>
							${wage.jixiaokaohegongzi}
						</th>
					</tr>
					<tr>
						<th>
							应发工资
						</th>
						<th>
							${wage.yingfagongzi}
						</th>
					</tr>
					<tr>
						<th>
							技能工资
						</th>
						<th>
							${wage.jinenggongzi}
						</th>
					</tr>
					<tr>
						<th>
							特殊补贴
						</th>
						<th>
							${wage.gonglinggongzi}
						</th>
					</tr>
					<tr>
						<th>
							奖金
						</th>
						<th>
							${wage.jiangjin}
						</th>
					</tr>
					<tr>
						<th>
							加班费
						</th>
						<th>
							${wage.jiabanfei}
						</th>
					</tr>
					<tr>
						<th>
							其他
						</th>
						<th>
							${wage.other}
						</th>
					</tr>
					<tr>
						<th>
							病事旷等
						</th>
						<th>
							${wage.bingshikangdeng}
						</th>
					</tr>
					<tr>
						<th>
							养老保险
						</th>
						<th>
							${wage.tongchoujin}
						</th>
					</tr>
					<tr>
						<th>
							医疗保险
						</th>
						<th>
							${wage.yiliaobaoxian}
						</th>
					</tr>
					<tr>
						<th>
							失业保险
						</th>
						<th>
							${wage.shiyebaoxian}
						</th>
					</tr>
					<tr>
						<th>
							公积金
						</th>
						<th>
							${wage.gongjijin}
						</th>
					</tr>
					<tr>
						<th>
							午餐费
						</th>
						<th>
							${wage.wucanfei}
						</th>
					</tr>
					<tr>
						<th>
							补差
						</th>
						<th>
							${wage.buchagongzi}
						</th>
					</tr>
					<tr>
						<th>
							房租费
						</th>
						<th>
							${wage.fangzufei}
						</th>
					</tr>
					<tr>
						<th>
							补发(补扣)工资
						</th>
						<th>
							${wage.bfgongzi}
						</th>
					</tr>
					<tr>
						<th>
							应交税金
						</th>
						<th>
							${wage.yingjiaoshuijin}
						</th>
					</tr>
					<tr>
						<th>
							实发工资
						</th>
						<th>
							${wage.shifagongzi}
						</th>
					</tr>
					<tr>
						<th colspan="2">
							<h2>
								注：私密信息外泄后果自负
							</h2>
						</th>
					</tr>
				</table>
			</div>
			<s:if test="wage.printDate==null">
				<input class="input" value="打印(1排)" onclick="wageprint()"
					type="button">
			</s:if>
			<s:else>
			您已打印过该月份的工资单,打印时间:${wage.printDate}
			</s:else>

		</center>
		<script type="text/javascript">
function wageprint() {
	$.ajax( {
		type : 'post',
		url : 'WageAction!updatePrint.action?id=${wage.id}',
		dataType : 'json',
		success : function(data) {
			pagePrint('printDiv3');
		}
	});
}
</script>
	</body>
</html>
