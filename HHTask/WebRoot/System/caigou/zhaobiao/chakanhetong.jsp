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
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng"
			style="width: 100%; border: solid 1px #0170b8; margin-top: 10px;">
			<div id="xitong"
				style="width: 100%; font-weight: bold; height: 50px;" align="left">
				<div
					style="float: left; width: 50%; padding-left: 30px; padding-top: 5px;"
					align="left">

				</div>
				<div style="float: left; width: 45%; padding-top: 5px;"
					align="right">
					<a href="zhaobiaoAction!listZhmoban.action" style="color: #ffffff">刷新</a>
				</div>
			</div>

			<div align="center" id="d1">
				<table class="table">
					<tr bgcolor="#c0dcf2">
						<th>
							序号
						</th>
						<th>
							投标公司
						</th>
						<th>
							到货时间
						</th>
						<th>
							联系方式
						</th>
						<th>
							负责人
						</th>

						<th>
							备注
						</th>
						<th>
							合同状态
						</th>
						<th>
							合同管理
						</th>
					</tr>
					<s:iterator value="list" id="zhtoubiao" status="pageIndex">
						<tr align="center" bgcolor="#e6f3fb"
							onmouseover="chageBgcolor(this)"
							onmouseout="outBgcolor(this,'#e6f3fb')">
							<td>
								${pageIndex.index+1}
							</td>
							<td>
								${zhtoubiao[1]}
							</td>
							<td>
								${zhtoubiao[4]}
							</td>
							<td>
								${zhtoubiao[8]}
							</td>
							<td>
								${zhtoubiao[9]}
							</td>



							<td>
								${zhtoubiao[4]}
							</td>
							<td>
								${zhtoubiao[11]}
							</td>
							<td>
								<a
									href="zhaobiaoAction!shengchenhetong.action?zhtoubiao.tid=${zhtoubiao[0]}">查看合同详细</a>
								<s:if test="#pagezhtoubiao.tmoney!=''">
<%--									<a href="DownAction.action?fileName="--%>
<%--										+${pagezhtoubiao.tmoney}+"&directory=/upload/zhaobiao/file/">查看附件</a>--%>
									<a href="FileViewAction.action?FilePath=/upload/zhaobiao/file/${pagezhtoubiao.tmoney}">查看附件</a>
								</s:if>



								<a
									href="zhaobiaoAction!chongxinpinbiao.action?zhtoubiao.tid=${zhtoubiao[0]}"
									onclick="return confirm('确认重新评标?此操作将会舍弃已中标的供应商,重新评标,并且要重新提交至上级审核');">重新评标</a>

							</td>

						</tr>
					</s:iterator>
					<tr>
						<s:if test="errorMessage==null">
							<th colspan="11" align="right">
								第
								<font color="red"><s:property value="cpage" /> </font> /
								<s:property value="total" />
								页
								<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
									styleClass="page" theme="number" />
						</s:if>
						<s:else>
							<th colspan="11" align="center" style="color: red">
						</s:else>
						<th>
							&ldquo;
						</th>
					</tr>

				</table>
			</div>
			<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		</center>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->

	</body>
	<SCRIPT type="text/javascript">
	    // $(function (){
	    	// if(list.length==0){
		    //	 $.post("zhaobiaoAction!listAll.action",{},function(msg){
		    		 //alert(msg);
		    		 
		    //		$("#d1").html(msg);
		    		
		   //   	})
	      //	}
	      //	window.location.href="<%=basePath%>zhaobiaoAction!listAll.action";
	     //})
	     function showdetail(id){
	    	//alert("1111");
			//var style="dialogWidth:45;dialogHeight:35;status:no;help:no"; 
			//window.showModalDialog("<%=basePath%>add.jsp","",style);
			window.open ("zhaobiaoAction!listByIdZhUser.action?zhUser.id="+id, "newwindow", "height=600, width=600, top=400,left=300,toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, status=no");  
	    }
	</SCRIPT>

</html>
