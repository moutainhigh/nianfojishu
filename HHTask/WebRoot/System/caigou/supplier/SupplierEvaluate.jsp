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
    <title></title>
    <%@include file="/util/sonHead.jsp" %>
</head>
<body>
<%@include file="/util/sonTop.jsp" %>
<div id="gongneng" style="width: 100%;">
    <div align="center">

        <div align="center">
            <div class="row clearfix">
                <h1 align="center">
                    ${supplierEvaluateMonth} 供应商考核评分表
                </h1>

                <table class="table">
                    <form action="SupplierEvaluateAction_find.action"
                          method="post" id="findform">
                        <tr>
                            <td>
                                <label for="">
                                    月度：
                                </label>
                                <select id="supplierEvaluateMonth" name="supplierEvaluateMonth"
                                        style="width: 100px;" ${supplierEvaluateMonth}>
                                    <option value="">
                                    </option>
                                </select>
                            </td>
                            <td>
                                <label for="">
                                    供应商类别：
                                </label>
                                <select id="category" name="category"
                                        style="width: 100px;" value="${category}">
                                    <option value="">
                                    </option>
                                </select>
                            </td>
                            <td>
                                <label for="">
                                    供应商名称：
                                </label>
                                <input name="supplierEvaluate.supplierName" id="supplierEvaluatename"
                                       value="${supplierEvaluate.supplierName}">
                            </td>

                        </tr>
                        <tr>
                            <td colspan="4" align="center">
                                <input type="hidden" name="errorMessage" value="all"/>
                                <input type="submit" value="查询"
                                       style="width: 80px; height: 50px;"/>
                                <input type="button" value="导出" onclick="exportExcel();todisabledone(this)"
                                       style="width: 80px; height: 50px;"/>
                            </td>
                        </tr>
                    </form>
                    <%--<form action="SupplierEvaluateAction_generateMonthReport.action"--%>
                    <%--method="post" id="generate">--%>
                    <%--<tr>--%>
                    <%--<td colspan="4" align="center">--%>
                    <%--<label for="">--%>
                    <%--手动按月生成评分表：--%>
                    <%--</label>--%>
                    <%--<INPUT id="start"--%>
                    <%--class="validate[required,funcCall[validate2time]]"--%>
                    <%--name="supplierEvaluateMonth"--%>
                    <%--onclick="WdatePicker({dateFmt:'yyyy-MM',skin:'whyGreen'})"--%>
                    <%--style="width: 80px;"/>--%>
                    <%--&lt;%&ndash;<input type="text" value=""&ndash;%&gt;--%>
                    <%--&lt;%&ndash;style="width: 80px; height: 50px;"/>&ndash;%&gt;--%>
                    <%--<input id="generateSubmit" type="submit" value="生成" onclick="PreventRepeat()"/>--%>
                    <%--</td>--%>
                    <%--</tr>--%>
                    <%--</form>--%>
                </table>
                <%--评定级别:&nbsp;&nbsp;&nbsp; A>=${levelScore[0]} &nbsp;&nbsp;&nbsp;B>=${levelScore[1]} &nbsp;&nbsp;&nbsp;C>=${levelScore[2]}--%>
                评定级别:&nbsp;&nbsp;&nbsp;
                <s:iterator value="supplierEvaluateLevelList" id="lev" status="Status0">
                            <span id="name${Status0.index}"
                                   <%--name="supplierEvaluateLevelList[${Status0.index}].levelName"--%>
                                  value="${lev.levelName}">${lev.levelName}</span>
                                >=
                            <span id="scl${Status0.index}"
                                   <%--name="supplierEvaluateLevelList[${Status0.index}].levelScore"--%>
                                   value="${lev.levelScore}">${lev.levelScore}</span>
                    &nbsp;&nbsp;&nbsp;
                </s:iterator>
                <%------------列表显示------------%>
                <form action="SupplierEvaluateAction_updateScore.action?supplierEvaluateMonth=${supplierEvaluateMonth}&category=${category}&cpage=${cpage}&total=${total}" method="post" onsubmit="return check()" id="myform">
                    <div class="col-md-12 column">
                        <table class="table">
                            <thead>
                            <tr bgcolor="#c0dcf2">
                                <th rowspan="2">
                                    序号
                                </th>
                                <th rowspan="2">
                                    供应商
                                </th>
                                <th rowspan="2">
                                    供应商类别
                                </th>
                                <th colspan="4">
                                    品质(40分)
                                </th>
                                <th colspan="3">
                                    交期(30分)
                                </th>
                                <th colspan="2">
                                    成本(20分)
                                </th>
                                <th colspan="2">
                                    服务(10分)
                                </th>
                                <th rowspan="2">
                                    合计得分
                                </th>
                                <th rowspan="2">
                                    评定级别
                                </th>
                                <%--<th rowspan="2">--%>
                                <%--责任人--%>
                                <%--</th>--%>
                                <%--<th rowspan="2">--%>
                                <%--备注--%>
                                <%--</th>--%>
                                <th rowspan="2">
                                    操作
                                </th>
                            </tr>
                            <tr>
                                <th>
                                    交货批次
                                </th>
                                <th>
                                    合格批次
                                </th>
                                <th>
                                    合格率
                                </th>
                                <th>
                                    品质得分
                                </th>
                                <th>
                                    准时交货批次
                                </th>
                                <th>
                                    准时交货率
                                </th>
                                <th>
                                    交期得分
                                </th>
                                <th>
                                    成本得分
                                </th>
                                <th>
                                    原因
                                </th>
                                <th>
                                    服务得分
                                </th>
                                <th>
                                    原因
                                </th>
                            </tr>
                            </thead>
                            <tbody>
                            <s:iterator value="supplierEvaluateList" id="sE" status="Status0">
                            <s:if test="#Status0.index%2==1">
                            <tr align="center" bgcolor="#e6f3fb"
                                onmouseover="chageBgcolor(this)"
                                onmouseout="outBgcolor(this,'#e6f3fb')">
                                </s:if>
                                <s:else>
                            <tr align="center" onmouseover="chageBgcolor(this)"
                                onmouseout="outBgcolor(this,'')" id="tr${sE.id}">
                                </s:else>
                                <td>
                                    <s:if test="#Status0.index%2==1">
                                    <font>
                                        </s:if>
                                        <s:else>
                                        <font color="#c0dcf2">
                                            </s:else>
                                            <s:property value="#Status0.index+1"/>
                                        </font>
                                </td>
                                <td>
                                        <%--${Status0.index+1}--%>
                                        ${sE.supplierName}
                                </td>
                                <td>
                                        ${sE.supplierCatagory}
                                </td>
                                <td>
                                        ${sE.qualityBatch}
                                </td>
                                <td>
                                        ${sE.qualityQualifiedBatch}
                                </td>
                                <td class="qualityPercent">
                                        ${sE.qualityPercent}
                                </td>
                                <td class="qualityScore">
                                        ${sE.qualityScore}
                                </td>
                                <td>
                                        ${sE.deliveryDateBatch}
                                </td>
                                <td class="deliveryDatePercent">
                                        ${sE.deliveryDatePercent}
                                </td>
                                <td class="deliveryDateScore">
                                        ${sE.deliveryDateScore}
                                </td>
                                <td class="costScore">
                                    <s:if test="#sE.skipSupplier=='true'">
                                        ${sE.costScore}
                                    </s:if>
                                    <s:else>
                                        <input type="hidden" style="width: 30px"
                                               name="supplierEvaluateList[${Status0.index}].id" value="${sE.id}">
                                        <input type="text" style="width: 30px"
                                               name="supplierEvaluateList[${Status0.index}].costScore"
                                               value="${sE.costScore}" class="costS" id="costS${sE.id}">
                                    </s:else>

                                </td>
                                <td><s:if test="#sE.skipSupplier=='true'">
                                    ${sE.costInfo}
                                </s:if>
                                    <s:else>
                                        <input type="text" name="supplierEvaluateList[${Status0.index}].costInfo"
                                               value="${sE.costInfo}" class="costinfo" id="costinfo${sE.id}"/>
                                    </s:else>

                                </td>
                                <td class="serviceScore">
                                    <s:if test="#sE.skipSupplier=='true'">
                                        ${sE.serviceScore}
                                    </s:if>
                                    <s:else>
                                        <input type="text" style="width: 30px"
                                               name="supplierEvaluateList[${Status0.index}].serviceScore"
                                               value="${sE.serviceScore}" class="serviceS" id="serviceS${sE.id}"/>
                                    </s:else>

                                </td>
                                <td><s:if test="#sE.skipSupplier=='true'">
                                    ${sE.serviceinfo}
                                </s:if>
                                    <s:else>
                                        <input type="text" name="supplierEvaluateList[${Status0.index}].serviceinfo"
                                               value="${sE.serviceinfo}" class="serviceinfo"  id="serviceinfo${sE.id}"/>
                                    </s:else>
                                </td>
                                <td class="totalScore">
                                        ${sE.evaluateScore}
                                </td>
                                <td class="level">
                                        ${sE.evaluateLevel}
                                </td>
                                <td>
                                    <s:if test="#sE.skipSupplier=='true'">
                                        <%--<a href="SupplierEvaluateAction_skipSupplier.action?supplierEvaluate.id=${sE.id}">考评</a>--%>
                                    </s:if>
                                    <s:else>
                                        <a href="SupplierEvaluateAction_skipSupplier.action?supplierEvaluate.id=${sE.id}&supplierEvaluateMonth=${supplierEvaluateMonth}&category=${category}&cpage=${cpage}&total=${total}">不考评</a>
                                        <input type="button" value="打分" style="width: 40px;height: 20px"  class="input" onclick="updateScore(this)" id="${sE.id}"/>
                                    </s:else>

                                </td>
                                </s:iterator>
                            </tr>
                            <tr>
                                <td align="center" colspan="20">
                                    <input type="submit" value="打分" style="width: 50px; height: 31px;" onclick="" ;>
                                </td>
                            </tr>

                            <tr>
                                <s:if test="errorMessage==null">
                                    <td colspan="20" align="right">
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
                </form>
            </div>
        </div>

    </div>
</div>
<%@include file="/util/foot.jsp" %>

<script type="application/javascript">
    $(function () {
        $.ajax({
            type: "post",
            url: "SupplierEvaluateAction_getMonth.action",
            dataType: "json",
            success: function (data) {
                //填充月度信息
                $(data).each(
                    function () {
                        $("<option value='" + this + "'>" + this + "</option>")
                            .appendTo("#supplierEvaluateMonth");
                    });
                var tinyselect = $(".tinyselect");
                $("#supplierEvaluateMonth").tinyselect();
            }
        });
    });

    $(function () {
        $.ajax({
            type: "post",
            url: "SupplierEvaluateAction_getsupplierCatagory.action",
            dataType: "json",
            success: function (data) {
                //填充供应商类型信息
                $(data).each(
                    function () {
                        $("<option value='" + this + "'>" + this + "</option>")
                            .appendTo("#category");
                    });
                var tinyselect = $(".tinyselect");
                $("#category").tinyselect();
            }
        });
    });

</script>
<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
<script type="text/javascript">
    var levelSize=<s:property value="supplierEvaluateLevelList.size()"/>;
    var levelName="<s:property value="supplierEvaluateLevelList.{levelName}" />";
    levelName=levelName.substring(1,levelName.length-1);
    levelName=levelName.split(", ");//“，空格”
    var levelScore=<s:property value="supplierEvaluateLevelList.{levelScore}"/>;


    function check() {
        var txt = $(".costS");
        for (var i = 0; i < txt.length; i++) {
            if (txt.eq(i).val() > 20 || txt.eq(i).val() < 0) {
                alert("成本得分超出范围");
                var f = txt.eq(i);
                f.focus();
                return false;
            }
        }
        var txt2 = $(".serviceS");
        for (var i = 0; i < txt2.length; i++) {
            if (txt2.eq(i).val() > 10 || txt2.eq(i).val() < 0) {
                alert("服务得分超出范围");
                var f2 = txt2.eq(i);
                f2.focus();
                return false;
            }
        }
        return true;
    }

    function PreventRepeat() {
        $('#generate').submit();
        $('#generateSubmit').attr("disabled", "disabled");
    }

    function toPercent(point) {
        var str = Number(point * 100).toFixed(1);
        str += "%";
        return str;
    }

    var qS = document.getElementsByClassName("qualityPercent");
    for (var index in qS) {
        qS[index].innerText = toPercent(qS[index].innerText);
    }
    var dS = document.getElementsByClassName("deliveryDatePercent");
    for (var index in dS) {
        dS[index].innerText = toPercent(dS[index].innerText);
    }


//计算得分
    $(".costScore").on('input propertychange',scorechange)
    $(".serviceScore").on('input propertychange',scorechange)




    function scorechange() {
        var s1 = document.getElementsByClassName("qualityScore");
        var s2 = document.getElementsByClassName("deliveryDateScore");
        var s3 = document.getElementsByClassName("costS");
        var s4 = document.getElementsByClassName("serviceS");
        var s5 = document.getElementsByClassName("totalScore");
        var level = document.getElementsByClassName("level");
        for (var index in s5) {
            var socre=s5[index].innerText;
            if(s3[index].value!=""&&s4[index].value!=""){
                socre = s1[index].innerText*1+s2[index].innerText*1+s3[index].value*1+s4[index].value*1;
                s5[index].innerText=socre;
                for(var i=0;i<levelSize;i++){
                    if(socre>=levelScore[i]){
                        level[index].innerText=levelName[i];
                        break;
                    }
                    if(i==parseInt(levelSize)-1){
                        level[index].innerText="未设定评分";
                    }
                }
                // if(socre>=alevel){level[index].innerText="A"}else if(socre>=blevel){level[index].innerText="B"}else if(socre>=clevel){level[index].innerText="C"}
                // else {
                //     level[index].innerText="未设定评分";
                // }
            }
        }
    };


    function  updateScore(obj) {
        if(!check()){
            return false;
        }
        var id =(obj.id);
        var costS=document.getElementById("costS"+id).value;
        var serviceS=document.getElementById("serviceS"+id).value;
        var costinfo=document.getElementById("costinfo"+id).value;
        var serviceinfo=document.getElementById("serviceinfo"+id).value;
        $.ajax({
            url: 'SupplierEvaluateAction_updateScore2.action?supplierEvaluateMonth=${supplierEvaluateMonth}&category=${category}&cpage=${cpage}&total=${total}',
            type: 'post',
            data:{
                "supplierEvaluate.id":id,
                "supplierEvaluate.costScore":costS,
                "supplierEvaluate.costInfo":costinfo,
                "supplierEvaluate.serviceScore":serviceS,
                "supplierEvaluate.serviceinfo":serviceinfo,
            },
            dataType: 'json',
            success: function (data) {
                alert(data);
                window.location.reload();
            },
            error: function (jqXHR, textStatus, errorThrown) {
            }
        });

    }

    function exportExcel(objForm) {
        document.forms.findform.action = "SupplierEvaluateAction_exportEXCEL.action";
        $("#supplierEvaluateMonth").val("<s:property value="supplierEvaluateMonth" escape='false'/>");
        document.forms.findform.submit();
        document.forms.findform.action = "SupplierEvaluateAction_find.action";

    }
</script>
</body>
</html>
