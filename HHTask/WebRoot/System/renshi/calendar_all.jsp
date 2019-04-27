<%@ page language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/WEB-INF/fenye.tld" prefix="fenye" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <base href="<%=basePath%>">

    <title>所有日程查看</title>

    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">


    <%@include file="/util/sonHead.jsp" %>


    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/css/css.css"/>
</head>
<body>
<h1 align="center">所有日程查看</h1>
<div align="center">
    <div>
        <form action="CalendarAction!findallCalendar.action" method="post">
            <table class="table">
                <tr>
                    <td>
                        名称
                    </td>
                    <td>
                        <input value="${calendar.title}" name="calendar.title">
                    </td>
                    <td>
                        内容
                    <td>
                    <td>
                        <input value="${calendar.thingContent}" name="calendar.thingContent">
                    </td>
                    <td>
                        开始时间
                    </td>
                    <td>
                        <input value="${calendar.start}" name="calendar.start">
                    </td>
                    <td>
                        所属人
                    </td>
                    <td>
                        <input value="${calendar.userName}" name="calendar.userName">
                    </td>
                    <td>
                        完成状态
                    </td>
                    <td>
                        <select name="dimissionLog.dept" id="dept">
                            <option value="完成">完成</option>
                            <option value="未完成">未完成</option>
                            <option value="打回">打回</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th colspan="11">
                        <input type="submit" value="查询" class="input"/>
                    </th>
                </tr>
            </table>
        </form>
    </div>
    <table class="table">
        <tbody>
        <tr>
            <th>序号
            </th>
            <th>名称
            </th>
            <th>内容
            </th>
            <th>开始时间
            </th>
            <th>结束时间
            </th>
            <th>所属人
            </th>
            <th>查看时间
            </th>
            <th>完成时间
            </th>
            <th>
                完成状态
            </th>
            <th>
                操作
            </th>
        </tr>

        <s:iterator value="calendarList" id="cal" status="Status0">
            <tr>
                <td>
                        ${Status0.index+1}
                </td>
                <td>
                        ${cal.title}
                </td>
                <td>
                        ${cal.thingContent}
                </td>
                <td>
                        ${cal.start}
                </td>
                <td>
                        ${cal.endDate}
                </td>
                <td>
                        ${cal.userName}
                </td>
                <td>
                        ${cal.viewTime}
                </td>

                <td>
                        ${cal.compTime}
                </td>
                <td>
                        ${cal.calendarState}
                </td>

                <td>
<%--                    <a href="">测试</a>--%>
                </td>
            </tr>
        </s:iterator>


        <tr>
            <s:if test="errorMessage==null">
                <td colspan="22" align="right">
                    第
                    <font color="red"><s:property value="cpage"/> </font> /
                    <s:property value="total"/>
                    页
                    <fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
                                 styleClass="page" theme="number"/>
                </td>
            </s:if>
        </tr>
        </tbody>
    </table>
</div>
</div>
<script type="text/javascript">

</script>
</body>
</html>