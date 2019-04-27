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
		<script language="javascript"
			src="${pageContext.request.contextPath}/javascript/jquery.qrcode.min.js">
</script>
		<style type="text/css">
/**/ /**
*    分页打印打印相关
*/
@media print {
	.notprint {
		display: none;
	}
	.PageNext {
		page-break-after: always;
	}
	.notprintTr {
		display: none;
	}
}

@media screen {
	.notprint {
		display: inline;
		cursor: hand;
	}
	.notprintTr {
		background-color: #d0d0d0;
	}
}

.text1 {
	width: 120px;
	overflow: hidden;
	text-overflow: ellipsis;
}

.text2 {
	width: 80px;
	overflow: hidden;
	text-overflow: ellipsis;
}
</style>
	</head>
	<body tyle="font-family: sans-serif;">
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng" style="width: 100%;">
			<div align="center">
				<%--					<s:if test='waigouDelivery.status!="待打印" && waigouDelivery.status!="送货中"'>--%>
				<%--						<table width="100%" align=center class="notprint">--%>
				<%--						<tr>--%>
				<%--							<td align=right>--%>
				<%--								<input type=button id="bp"--%>
				<%--									onClick="pp.beforePrint();this.disabled = true;document.getElementById('ap').disabled = false;"--%>
				<%--									value="分页" style="border: 1px solid #000000">--%>
				<%--								<input type=button value='打印' onClick="window.print()"--%>
				<%--									style="border: 1px solid #000000">--%>
				<%--							</td>--%>
				<%--						</tr>--%>
				<%--					</table>--%>
				<%--				</s:if>--%>
				<s:if test="waigouDelivery!=null">
					<table id="tabHeader"
						style="border-collapse: collapse; width: 95%; line-height: 20px;">
						<tr>
							<th colspan="15"
								style="font-weight: bolder; font-size: 30px; height: 50px;">
								送货单
							</th>
						</tr>
						<tr>
							<td align="left">
								<div style="float: left;">
									<img
										src="barcode.action?msg=${waigouDelivery.planNumber}&type=code128"
										width="180px" height="60px" />
								</div>
								<div id="ercode">
								</div>
								<%--									<img alt="" src="" id="myimg">--%>
								<%--									<div id="barcode"--%>
								<%--										style="height: 60px; width: 60px; float: left;">--%>
								<%--									</div>--%>
							</td>
							<td>
								更新时间:${waigouDelivery.addTime}
							</td>
						</tr>
						<tr>
							<td align="left">
								供应商:${waigouDelivery.gysName}
							</td>
							<td>
								客户名称:${waigouDelivery.customer}
							</td>
						</tr>
						<tr>
							<td align="left">
								供应商编号:${waigouDelivery.userCode}
							</td>
							<td>
								地址:${companyInfo.address}
							</td>
						</tr>
						<tr>
							<td align="left">
								供联系人:${waigouDelivery.gysContacts}
							</td>
							<td>
								客联系人:${waigouDelivery.contacts}
							</td>
						</tr>
						<tr>
							<td align="left">
								供联系电话:${waigouDelivery.gysPhone}
							</td>
							<td>
								客联系电话:${waigouDelivery.contactsPhone}
							</td>
						</tr>
						<tr>
							<td align="left">
								出发地址:${waigouDelivery.chufaDizhi}
							</td>
							<td>
								到达地址:${waigouDelivery.daodaDizhi}
							</td>
						</tr>
						<tr>
							<td align="left">
								送货人姓名: ${waigouDelivery.shContacts}
							</td>
							<td>
								送货人联系电话: ${waigouDelivery.shContactsPhone}
							</td>
						</tr>
						<tr>
							<td align="left" colspan="2">
								车牌: ${waigouDelivery.chepai}
							</td>
						</tr>
					</table>
				</s:if>
				<br />
				<table class="table" align=center cellpadding=3 id="tabDetail"
					style="font-size: xx-small;">
					<tr bgcolor="#c0dcf2" height="25px">
						<th align="center">
							序号
						</th>
						<th align="center">
							采购订单号
						</th>
						<th align="center">
							销售单号
						</th>
						<th align="center">
							产品编码
						</th>
						<th align="center">
							件号
						</th>
						<th align="center">
							零件名称
						</th>
						<s:if test="tag=='外委'">
							<%--								<th align="center">--%>
							<%--								委外工序--%>
							<%--							</th>--%>
							<th align="center">
								下工序
							</th>
						</s:if>
						<th align="center">
							规格
						</th>
						<th align="center">
							图号
						</th>
						<th align="center">
							版本
						</th>
						<th align="center">
							供货属性
						</th>
						<th align="center">
							数量
						</th>
						<s:if test='waigouDelivery.status!="待打印" && waigouDelivery.status!="送货中"'>
							<th>
								签收数量	
							</th>
						</s:if>
						<%--							<s:if test='waigouDelivery.status!="待打印"'>--%>
						<%--							<th>--%>
						<%--								合格数量	--%>
						<%--							</th>--%>
						<%--							</s:if>--%>
						<%--							</s:if>--%>
						<th align="center">
							单位
						</th>
						<th align="center">
							箱(包)数
						</th>
						<th align="center">
							备注
						</th>
					</tr>
					<!-- 
						<tr>
							<th align="center">
								No.
							</th>
							<th align="center">
								PO
							</th>
							<th align="center">
								MarkId
							</th>
							<th align="center">
								Desription
							</th>
							<th align="center">
								version
							</th>
							<th align="center">
								AP
							</th>
							<th align="center">
								Qty
							</th>
							<th align="center">
								UOM
							</th>
							<th align="center">
								Ctn
							</th>
							<th align="center">
								Remarks
							</th>
						</tr> -->
					<s:iterator value="list" id="pageWgww2" status="pageStatus2">
					<tr align="center"   style="height: 25px;"
								onclick="noprintTr(this)">
						<td>
							<s:property value="#pageStatus2.index+1" />
						</td>
						<td>
							${pageWgww2.cgOrderNum}
						</td>
						<td style="max-width: 100px;">
							${pageWgww2.neiorderNum}
						</td>
						<td>
							${pageWgww2.strs}
						</td>
						<td>
							${pageWgww2.markId}
						</td>
						<td>
							${pageWgww2.proName}
						</td>
						<s:if test="tag=='外委'">
							<%--								<td>--%>
							<%--									${pageWgww2.processName}--%>
							<%--								</td>--%>
							<td>
								${pageWgww2.nextProcessName}
							</td>
						</s:if>
						<td>
							${pageWgww2.specification}
						</td>
						<td>
							${pageWgww2.tuhao}
						</td>
						<td>
							${pageWgww2.banben}
						</td>
						<td>
							${pageWgww2.kgliao}
						</td>
						<td align="right">
							${pageWgww2.shNumber}
							<input type="hidden" value="${pageWgww2.shNumber}" name="pageWgww2.shNumber" />
						</td>
						<s:if test='waigouDelivery.status!="待打印" && waigouDelivery.status!="送货中"'>
							<th align="right">
								${pageWgww2.qrNumber}
							</th>
						</s:if>
						<%--							<s:if test='waigouDelivery.status!="待打印"'>--%>
						<%--							<th align="right">--%>
						<%--								${pageWgww2.hgNumber}--%>
						<%--							</th>--%>
						<%--							</s:if>--%>
						<%--							</s:if>--%>
						<td>
							${pageWgww2.unit}
						</td>
						<td>
							${pageWgww2.ctn}
						</td>
						<td>
							<span class="remarks">${pageWgww2.remarks}</span>
						</td>
					</s:iterator>
					<tr>
						<th colspan="10">
						</th>
						<th id="sunNum_th">
							<span class="sumNum_span">${sumNum}</span>
							<input type="hidden" value="${sumNum}" class="sumNum"/>
						</th>
						<!--						<s:if test='waigouDelivery.status!="待打印" && waigouDelivery.status!="送货中"'>-->
						<!--							<th align="right">-->
						<!--								${sumbhsprice}-->
						<!--							</th>-->
						<!--							<s:if test='waigouDelivery.status!="待打印"'>-->
						<!--							<th align="right">-->
						<!--								${sumMoney}-->
						<!--							</th>-->
						<!--							</s:if>-->
						<!--							</s:if>-->
						<!--							<th></th>-->
						<!--							<th></th>-->
						<th colspan="10"></th>
					</tr>
					<%--						<tr style="">--%>
					<%--							<td colspan="15">--%>
					<%--							&nbsp;&nbsp;备注(异常请详细说明):--%>
					<%--							<br />--%>
					<%--							<br />--%>
					<%--							<br />--%>
					<%--							<br />--%>
					<%--							</td>--%>
					<%--						</tr>--%>
				</table>


				<table class="table" id="tabFooter" align=center cellpadding="4"
					width="100%" style="font-size: 5px;">
					<tr>
						<td width="100px;">
							送货人:
							<br />
							Shipper / Agent
						</td>
						<td></td>
						<td width="100px;">
							收货人:
							<br />
							Received by
						</td>
						<td></td>
					</tr>
					<tr>
						<td>
							送货日期:
							<br />
							Delivery Date
						</td>
						<td></td>
						<td>
							收货日期:
							<br />
							Date Received
						</td>
						<td></td>
					</tr>
					<tr>
						<td>
							公司印章:
							<br />
							Company Chop
						</td>
						<td></td>
						<td>
							公司印章;
							<br />
							Company Chop
						</td>
						<td>
						</td>
					</tr>
				</table>
				<div id="divPrint"></div>
				<div class="notprint" align="center">
					<input type="button" value="打印" class="input"
						onclick="printDelivery()" />
					<s:if
						test='waigouDelivery.status=="待打印"||waigouDelivery.status=="送货中"'>
						<s:if test='waigouDelivery.status=="送货中"'>
							<input type="button" value="确认送货" class="input"
								onchange="numyanzheng()"
								onclick="javascript:location.href='WaigouwaiweiPlanAction!updateDeliveryToPrint.action?id=${waigouDelivery.id}';" />
						</s:if>
					</s:if>
				</div>
			</div>
		</div>
		<div style="display: none;"></div>
		<img alt="" src="" id="myimg">
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">
<%--$('#barcode').qrcode( {--%>
<%--	render : "table", //table方式 --%>
<%--	width : 60, //宽度 --%>
<%--	height : 60, //高度 --%>
<%--	text : "${waigouDelivery.planNumber}" //任意内容 --%>
<%--}); //任意字符串 --%>
$(function(){
	getQRCode (60,60,'${waigouDelivery.planNumber}','ercode') ;
<%--	printDelivery();--%>
})
function printDelivery() {
	$.ajax( {
		type : "POST",
		url : "WaigouwaiweiPlanAction!updateDeliveryToPrintajax.action",
		data : {
			id : "${waigouDelivery.id}"
		},
		dataType : "json",
		cache : false,//防止数据缓存
		async:false,
		success : function(msg) {
			window.print();
		}
<%--		--%>
	});
}



<!--

/**//** 
**    ================================================================================================== 
**    类名：CLASS_PRINT 
**    功能：打印分页 
**    示例： 
    --------------------------------------------------------------------------------------------------- 

        var pp = new CLASS_PRINT();

        window.onload = function(){
            pp.header = document.getElementById("tabHeader");
            pp.content= document.getElementById("tabDetail");
            pp.footer = document.getElementById("tabFooter");

            pp.hideCols("5,7");    
            pp.hideRows("3,15");
            pp.pageSize = 10;    
        }

        <BODY onbeforeprint="pp.beforePrint()" onafterprint="pp.afterPrint()">


*/
function CLASS_PRINT()
{
    this.header        = null;
    this.content    = null;
    this.footer        = null;
    this.board        = null;
    this.pageSize    = 10;

    var me            = this;

    //哈希表类
    function Hashtable()
    {
        this._hash        = new Object();
        this.add        = function(key,value){
                            if(typeof(key)!="undefined"){
                                if(this.contains(key)==false){
                                    this._hash[key]=typeof(value)=="undefined"?null:value;
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        }
        this.remove        = function(key){delete this._hash[key];}
        this.count        = function(){var i=0;for(var k in this._hash){i++;} return i;}
        this.items        = function(key){return this._hash[key];}
        this.contains    = function(key){return typeof(this._hash[key])!="undefined";}
        this.clear        = function(){for(var k in this._hash){delete this._hash[k];}}

    }

    //字符串转换为哈希表
    this.str2hashtable = function(key,cs){
        
            var _key    = key.split(/,/g);
            var _hash    = new Hashtable();
            var _cs        = true;
            if(typeof(cs)=="undefined"||cs==null){
                _cs = true;
            } else {
                _cs = cs;
            }

            for(var i in _key){
                if(_cs){
                    _hash.add(_key[i]);
                } else {
                    _hash.add((_key[i]+"").toLowerCase());
                }

            }
            return _hash;
        }

    this._hideCols    = this.str2hashtable("");
    this._hideRows    = this.str2hashtable("");

    this.hideCols = function(cols){
        me._hideCols = me.str2hashtable(cols)
    }

    this.isHideCols = function(val){    
        return    me._hideCols.contains(val);
    }

    this.hideRows = function(rows){
        me._hideRows = me.str2hashtable(rows)
    }

    this.isHideRows = function(val){    
        return    me._hideRows.contains(val);
    }

    this.afterPrint = function()
    {
        var table = me.content;        
        
        if(typeof(me.board)=="undefined"||me.board==null){        
            me.board = document.getElementById("divPrint");
            if(typeof(me.board)=="undefined"||me.board==null){
                me.board = document.createElement("div");
                document.body.appendChild(me.board);
            }
        }

        if(typeof(table)!="undefined"){
            for(var i =0;i<table.rows.length;i++){
                var tr = table.rows[i];
                for(var j=0;j<tr.cells.length;j++){
                    if(me.isHideCols(j)){
                        tr.cells[j].style.display = "";
                    }
                }
            }
        }

        me.content.style.display    = '';
        me.header.style.display        = '';
        me.footer.style.display        = '';
        me.board.innerHTML            = '';

    }

    this.beforePrint = function(){

        var table = me.content;   

        if(typeof(me.board)=="undefined"||me.board==null){        
            me.board = document.getElementById("divPrint");
            if(typeof(me.board)=="undefined"||me.board==null){
                me.board = document.createElement("div");
                document.body.appendChild(me.board);
            }
        }


        if(typeof(table)!="undefined"&&this.hideCols.length>0){        
            
            for(var i =0;i<table.rows.length;i++){
                var tr = table.rows[i];
                for(var j=0;j<tr.cells.length;j++){
                    if(me.isHideCols(j)){                    
                        tr.cells[j].style.display = "none";
                    }
                }
            }
        }
    
        
        ///开始分页    
        var pageSize = this.pageSize;
        
        var head    = me.header;
        var foot    = me.footer;
        
        var page    = new Array();
        var rows    = "";    
        var rowIndex= 1;

        var cp        = 0;
        var tp        = 0;
        
        
        for(i=1;i<table.rows.length;i++){                
            if(this.isHideRows(i)==false){
                if((((rowIndex-1)%pageSize)==0&&rowIndex>1)||i==table.rows.length){                                
                    page[page.length] = getTable(head,table,rows,foot);
                                                    
                    rows    = getOuterHTML(table.rows[i]) + "\n" ; 
                    rowIndex= 2;
                                                                            
                } else {
                    rows    += getOuterHTML(table.rows[i]) + "\n"; 
                    rowIndex++;
                }
            }
        }
        
        if(rows.length>0){
            page[page.length] = getTable(head,table,rows,foot);
        }

        tp = page.length;

        for(var i=0;i<page.length;i++){
        	var arrays =		 page[i].split('<!-- hj -->');
       		var pagemoney = 0;
       		for(var j=1;j<arrays.length;j+=2){
       			pagemoney+=parseFloat(arrays[j]);
       		}
       		pagemoney = pagemoney.toFixed(3);
       		 var daxie = daxiezhuanhuan(pagemoney);
            page[i] = page[i].replace(/\<\!--ct-->/g,'第'+(i+1)+'页 共' + tp+'页').replace(/\<\!--cp--\>/g,i+1).replace(/\<\!--tp--\>/g,tp);
            page[i] = page[i].replace(/\<!-- byhj -->/g,'<th align="right">本页合计:</th> <th colspan="6" align="left" >'+pagemoney
            	+'</th><th align="left"  colspan="7" id="daxie">金额大写:'+daxie+'</th>').replace(/\<!-- byhj -->/g,i+1).replace(/\<!-- byhj -->/g,tp);
        }
        
                    
        head.style.display        = 'none';
        foot.style.display        = 'none';
        table.style.display        = 'none';
        if(page.length>1){
            me.board.innerHTML = page.join("\n<div class='pageNext'></div>");
        }else{
            me.board.innerHTML = page.join("");
        }
    }

function getOuterHTML (node) {

    if(typeof(node)!="undefined"&&typeof(node.outerHTML)!="undefined"){
        return node.outerHTML;
    }

    var emptyElements = {
      HR: true, BR: true, IMG: true, INPUT: true
    };
    var specialElements = {
      TEXTAREA: true
    };

    var html = '';
    switch (node.nodeType){
        case Node.ELEMENT_NODE:
            html += '<';
            html += node.nodeName;
            if (!specialElements[node.nodeName]) {
                for (var a = 0; a < node.attributes.length; a++)
                    html += ' ' + node.attributes[a].nodeName.toUpperCase() + '="' + node.attributes[a].nodeValue + '"';
                html += '>'; 
                if (!emptyElements[node.nodeName]){
                    html += node.innerHTML;
                    html += '<\/' + node.nodeName + '>';
                }
            }
            else 
                switch (node.nodeName){
                    case 'TEXTAREA':
                        var content = '';
                        for (var a = 0; a < node.attributes.length; a++)
                            if (node.attributes[a].nodeName.toLowerCase() != 'value')
                                html += ' ' + node.attributes[a].nodeName.toUpperCase() + '="' + node.attributes[a].nodeValue + '"';
                            else 
                                content = node.attributes[a].nodeValue;
                            html += '>'; 
                            html += content;
                            html += '<\/' + node.nodeName + '>';
                        break; 
                }
            break;
        case Node.TEXT_NODE:
            html += node.nodeValue;
            break;
        case Node.COMMENT_NODE:
            html += '<!' + '--' + node.nodeValue + '--' + '>';
            break;
    }
    return html;
}

    function getTable(header,table,content,footer){
        var htm = "";

        if(typeof(header)!="undefined"){
            htm += getOuterHTML(header);
        }

        if(typeof(table)!="undefined"){        
            htm += "\n<" + table.tagName;
            
            for(var i =0;i<table.attributes.length;i++){
                if(table.attributes[i].specified){
                    if(table.attributes[i].name=="style")
                        htm += " style='" + table.style.cssText + "'";
                    else
                        htm += " " + table.attributes[i].nodeName + "='" + table.attributes[i].nodeValue + "'";
                }        
            }    
            
            if(table.rows.length>0){
                htm += ">\n" + getOuterHTML(table.rows[0]) + content + "</" + table.tagName + ">";
            } else {
                htm += ">\n" + content + "</" + table.tagName + ">\n";
            }        
        }

        if(typeof(footer)!="undefined"){
            htm += getOuterHTML(footer);
        }
        
        return htm;
    }

    if(!window.attachEvent){
        window.attachEvent = function(){window.addEventListener(arguments[0].substr(2),arguments[1],arguments[2]);}
    }
}


var pp = new CLASS_PRINT();
window.onload = function()
{

    pp.header = document.getElementById("tabHeader");
    pp.content= document.getElementById("tabDetail");
    pp.footer = document.getElementById("tabFooter");
	

    //pp.hideCols("5,7");    
    //pp.hideRows("1,2");
     //控制打印行数
    
    	 pp.pageSize = 10; 
    pp.beforePrint();
}

document.onkeydown=function(){
with(window.event){
	//禁用 Ctrl+p 打印
  //Ctrl + p keyCode == 80
	if (event.ctrlKey && event.keyCode == 80){
		event.keyCode=0;event.returnValue=false;
	} 
  
}
}
//禁用右键
<%--document.oncontextmenu = function(){--%>
<%--    event.returnValue = false;--%>
<%--}--%>
function noprintTr(obj1){
	var tdclass =	$(obj1).attr("class");
	var input =	$(obj1).find("input")[0];
	var shNumber = input.value;
	var sumNum  = $(".sumNum")[0].value;
	if(tdclass!='notprintTr'){
		var value = sumNum*1 -shNumber*1;
		$(".sumNum_span").text(value);
		$(".sumNum").val(value);
		$(obj1).addClass("notprintTr");
	}else{
		var value = sumNum*1 + shNumber*1;
		$(".sumNum_span").text(value);
		$(".sumNum").val(value);
		$(obj1).removeClass();
	}
}


//-->
</script>
	</body>
</html>
