package com.task.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.DefaultHttpParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;
import com.task.entity.rtx.RtxConnect;

/**
 * 微信企业号开发
 * 
 * @author ZHEN.L
 * 
 */
// @Path("wx")
public class StationResource {

	private String token = "spm"; // 企业号 -> 应用中心 -> 新建应用 -> 消息型应用

	/********** 企业号应用消息对接 **************/
	public static String agentId = ""; // 1000003 企业号 -> 应用中心 -> 点开应用中 -> 应用ID
	// 企业号id
	public static String corpId = ""; // wwf25a9529aedd36ee 企业号 -> 设置 -> 企业号信息
	// 应用id
	public static String secret = ""; // 21GbQQEraZAPCs9SREJ3RV9kGUTmxeNCbkXZRrzhQVo
	// 获取访问权限码URL
	private final static String ACCESS_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=";
	// 创建会话请求URL
	private final static String CREATE_SESSION_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token";
	/***
	 * 华为GDP对接
	 */
	// 获取GDP用户名
	public static String gdpName = "";
	// 获取GDP密码
	public static String gdpPassword = "";

	// @Context
	HttpServletRequest request;
	// @Context
	HttpServletResponse response;

	WXBizMsgCrypt wxcpt = null;

	// public StationResource() {
	// try {
	// wxcpt = new WXBizMsgCrypt(token, secret, corpId);
	// } catch (AesException e) {
	// e.printStackTrace();
	// }
	// }

	public static void getRtxConnect(RtxConnect rtxConnect) {
		if (rtxConnect != null) {
			agentId = rtxConnect.getAgentId();
			corpId = rtxConnect.getCorpId();
			secret = rtxConnect.getSecret();
			gdpName = rtxConnect.getJdpUserName();
			gdpPassword = rtxConnect.getJdpPassWord();
		}
	}

	// 获取接口访问权限码
	public String getAccessToken() {
		HttpClient client = new HttpClient();
		GetMethod post = new GetMethod(ACCESS_TOKEN_URL + corpId
				+ "&corpsecret=" + secret);
		// post.releaseConnection();
		// post.setRequestHeader("Content-Type",
		// "application/x-www-form-urlencoded;charset=UTF-8");
		// NameValuePair[] param = { new NameValuePair("corpid", corpId),
		// new NameValuePair("corpsecret", secret) };
		// // 设置策略，防止报cookie错误
		// DefaultHttpParams.getDefaultParams().setParameter(
		// "http.protocol.cookie-policy",
		// CookiePolicy.BROWSER_COMPATIBILITY);
		// 给post设置参数
		// post.setRequestBody(param);
		String result = "";
		try {
			client.executeMethod(post);
			result = new String(post.getResponseBodyAsString().getBytes("gbk"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		// 将数据转换成json
		JSONObject jasonObject;

		jasonObject = JSONObject.fromObject(result);
		result = (String) jasonObject.get("access_token");

		post.releaseConnection();
		// System.out.println(result);
		return result;

	}

	/*
	 * ------------使用示例一：验证回调URL---------------企业开启回调模式时，企业号会向验证url发送一个get请求
	 * 假设点击验证时，企业收到类似请求： GET
	 * /cgi-bin/wxpush?msg_signature=5c45ff5e21c57e6ad56bac8758b79b1d9ac89fd3
	 * ×tamp =1409659589&nonce=263014780&echostr=P9nAzCzyDtyTWESHep1vC5X9xho%
	 * 2FqYX3Zpb4yKa9SKld1DsH3Iyt3tP3zNdtp%2B4RPcs8TgAE7OaBO%2BFZXvnaqQ%3D%3D
	 * HTTP/1.1 Host: qy.weixin.qq.com
	 * 
	 * 接收到该请求时，企业应
	 * 1.解析出Get请求的参数，包括消息体签名(msg_signature)，时间戳(timestamp)，随机数字串(nonce
	 * )以及公众平台推送过来的随机加密字符串(echostr), 这一步注意作URL解码。 2.验证消息体签名的正确性 3.
	 * 解密出echostr原文，将原文当作Get请求的response，返回给公众平台 第2，3步可以用公众平台提供的库函数VerifyURL来实现。
	 */
	/**
	 * 回调URL，微信调用此方法进行验证
	 * 
	 * @return
	 */
	// @GET
	// @Path("station")
	public String verify() {
		String msgSignature = request.getParameter("msg_signature");
		String timeStamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		System.out.println(timeStamp + " " + nonce);
		String echostr = request.getParameter("echostr");
		String sEchoStr = null;
		try {
			sEchoStr = wxcpt.VerifyURL(msgSignature, timeStamp, nonce, echostr);
		} catch (Exception e) {
			e.printStackTrace();// 验证URL失败，错误原因请查看异常
		}
		return sEchoStr;
	}

	/*
	 * ------------对用户回复的消息解密---------------
	 * 用户回复消息或者点击事件响应时，企业会收到回调消息，此消息是经过公众平台加密之后的密文以post形式发送给企业，密文格式请参考官方文档
	 * 假设企业收到公众平台的回调消息如下： POST /cgi-bin/wxpush?
	 * msg_signature=477715d11cdb4164915debcba66cb864d751f3e6
	 * ×tamp=1409659813&nonce=1372623149 HTTP/1.1 Host: qy.weixin.qq.com
	 * Content-Length: 613 <xml>
	 * <ToUserName><![CDATA[wx5823bf96d3bd56c7]]></ToUserName
	 * ><Encrypt><![CDATA[RypEvHKD8QQKFhvQ6QleEB4J58tiPdvo
	 * +rtK1I9qca6aM/wvqnLSV5zEPeusUiX5L5X/0lWfrf0QADHHhGd3QczcdCUpj911L3vg3W/
	 * sYYvuJTs3TUUkSUXxaccAS0qhxchrRYt66wiSpGLYL42aM6A8dTT
	 * +6k4aSknmPj48kzJs8qLjvd4Xgpue06DOdnLxAUHzM6
	 * +kDZ+HMZfJYuR+LtwGc2hgf5gsijff0ekUNXZiqATP7PF5mZxZ3Izoun1s4zG4LUMnvw2r
	 * +KqCKIw
	 * +3IQH03v+BCA9nMELNqbSf6tiWSrXJB3LAVGUcallcrw8V2t9EL4EhzJWrQUax5wLVMNS0
	 * +rUPA3k22Ncx4XXZS9o0MBH27Bo6BpNelZpS
	 * +/uh9KsNlY6bHCmJU9p8g7m3fVKn28H3KDYA5Pl
	 * /T8Z1ptDAVe0lXdQ2YoyyH2uyPIGHBZZIs2pDBS8R07+qN+E7Q==]]></Encrypt>
	 * <AgentID><![CDATA[218]]></AgentID> </xml>
	 * 
	 * 企业收到post请求之后应该
	 * 1.解析出url上的参数，包括消息体签名(msg_signature)，时间戳(timestamp)以及随机数字串(nonce)
	 * 2.验证消息体签名的正确性。
	 * 3.将post请求的数据进行xml解析，并将<Encrypt>标签的内容进行解密，解密出来的明文即是用户回复消息的明文，明文格式请参考官方文档
	 * 第2，3步可以用公众平台提供的库函数DecryptMsg来实现。
	 */
	// @POST
	// @Path("station")
	public String receiveMsg(String reqData) {
		String msgSignature = request.getParameter("msg_signature");
		String timeStamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		// post请求的密文数据
		// String sReqData =
		// "<xml><ToUserName><![CDATA[wx5823bf96d3bd56c7]]></ToUserName><Encrypt><![CDATA[RypEvHKD8QQKFhvQ6QleEB4J58tiPdvo+rtK1I9qca6aM/wvqnLSV5zEPeusUiX5L5X/0lWfrf0QADHHhGd3QczcdCUpj911L3vg3W/sYYvuJTs3TUUkSUXxaccAS0qhxchrRYt66wiSpGLYL42aM6A8dTT+6k4aSknmPj48kzJs8qLjvd4Xgpue06DOdnLxAUHzM6+kDZ+HMZfJYuR+LtwGc2hgf5gsijff0ekUNXZiqATP7PF5mZxZ3Izoun1s4zG4LUMnvw2r+KqCKIw+3IQH03v+BCA9nMELNqbSf6tiWSrXJB3LAVGUcallcrw8V2t9EL4EhzJWrQUax5wLVMNS0+rUPA3k22Ncx4XXZS9o0MBH27Bo6BpNelZpS+/uh9KsNlY6bHCmJU9p8g7m3fVKn28H3KDYA5Pl/T8Z1ptDAVe0lXdQ2YoyyH2uyPIGHBZZIs2pDBS8R07+qN+E7Q==]]></Encrypt><AgentID><![CDATA[218]]></AgentID></xml>";
		try {
			String sMsg = wxcpt.DecryptMsg(msgSignature, timeStamp, nonce,
					reqData);
			// 解析出明文xml标签的内容进行处理
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(sMsg);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);
			Element root = document.getDocumentElement();
			NodeList nodelist1 = root.getElementsByTagName("Content");
			if (nodelist1.item(0) == null)
				return "ok";
			String Content = nodelist1.item(0).getTextContent();
			System.out.println("Content：" + Content);
		} catch (Exception e) {
			e.printStackTrace();// 解密失败，失败原因请查看异常
		}
		return "ok";
	}

	/*
	 * ------------使用示例三：企业回复用户消息的加密---------------
	 * 企业被动回复用户的消息也需要进行加密，并且拼接成密文格式的xml串。 假设企业需要回复用户的明文如下： <xml>
	 * <ToUserName><![CDATA[mycreate]]></ToUserName>
	 * <FromUserName><![CDATA[wx5823bf96d3bd56c7]]></FromUserName>
	 * <CreateTime>1348831860</CreateTime> <MsgType><![CDATA[text]]></MsgType>
	 * <Content><![CDATA[this is a test]]></Content>
	 * <MsgId>1234567890123456</MsgId> <AgentID>128</AgentID> </xml>
	 * 
	 * 为了将此段明文回复给用户，企业应：
	 * 1.自己生成时间时间戳(timestamp),随机数字串(nonce)以便生成消息体签名，也可以直接用从公众平台的post
	 * url上解析出的对应值。 2.将明文加密得到密文。
	 * 3.用密文，步骤1生成的timestamp,nonce和企业在公众平台设定的token生成消息体签名。
	 * 4.将密文，消息体签名，时间戳，随机数字串拼接成xml格式的字符串，发送给企业。
	 * 以上2，3，4步可以用公众平台提供的库函数EncryptMsg来实现。
	 */
	// @GET
	// @Path("send")
	public void sendMsg(String timeStamp, String nonce) {
		String sRespData = "<xml><ToUserName><![CDATA[mycreate]]></ToUserName><FromUserName><![CDATA[wxe49318eb604cf00b]]></FromUserName><CreateTime>1348831860</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[this is a test]]></Content><MsgId>1234567890123456</MsgId><AgentID>1</AgentID></xml>";
		try {
			String sEncryptMsg = wxcpt.EncryptMsg(sRespData, timeStamp, nonce);
			System.out.println("after encrypt sEncrytMsg: " + sEncryptMsg);
			response.getWriter().print(sEncryptMsg);
		} catch (Exception e) {
			e.printStackTrace();// 加密失败
		}
		// return sRespData;
	}

	/**
	 * 此方法可以发送任意类型消息
	 * 
	 * @param msgType
	 *            text|image|voice|video|file|news
	 * @param touser
	 *            成员ID列表（消息接收者，多个接收者用‘|’分隔，最多支持1000个）。特殊情况：指定为@all，
	 *            则向关注该企业应用的全部成员发送
	 * @param toparty
	 *            部门ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数
	 * @param totag
	 *            标签ID列表，多个接收者用‘|’分隔。当touser为@all时忽略本参数
	 * @param content
	 *            msgType=text时 ,文本消息内容
	 * @param mediaId
	 *            msgType=image|voice|video时 ,对应消息信息ID（--------）
	 * @param title
	 *            msgType=news|video时，消息标题
	 * @param description
	 *            msgType=news|video时，消息描述
	 * @param url
	 *            msgType=news时，消息链接
	 * @param picurl
	 *            msgType=news时，图片路径
	 * @param safe
	 *            表示是否是保密消息，0表示否，1表示是，默认0
	 */
	public void sendWeChatMsg(String msgType, String touser, String toparty,
			String totag, String content, String mediaId, String title,
			String description, String url, String picurl, String safe) {

		URL uRl;
		String ACCESS_TOKEN = null;
		if (corpId != null && corpId.length() > 0) {
			ACCESS_TOKEN = getAccessToken();
		}
		if (ACCESS_TOKEN == null) {
			return;
		}
		// 拼接请求串
		String action = CREATE_SESSION_URL + "=" + ACCESS_TOKEN;
		// 封装发送消息请求json
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"touser\":" + "\"" + touser + "\",");
		sb.append("\"toparty\":" + "\"" + toparty + "\",");
		sb.append("\"totag\":" + "\"" + totag + "\",");
		if (msgType.equals("text")) {
			sb.append("\"msgtype\":" + "\"" + msgType + "\",");
			sb.append("\"text\":" + "{");
			sb.append("\"content\":" + "\"" + content + "\"");
			sb.append("}");
		} else if (msgType.equals("image")) {
			sb.append("\"msgtype\":" + "\"" + msgType + "\",");
			sb.append("\"image\":" + "{");
			sb.append("\"media_id\":" + "\"" + mediaId + "\"");
			sb.append("}");
		} else if (msgType.equals("voice")) {
			sb.append("\"msgtype\":" + "\"" + msgType + "\",");
			sb.append("\"voice\":" + "{");
			sb.append("\"media_id\":" + "\"" + mediaId + "\"");
			sb.append("}");
		} else if (msgType.equals("video")) {
			sb.append("\"msgtype\":" + "\"" + msgType + "\",");
			sb.append("\"video\":" + "{");
			sb.append("\"media_id\":" + "\"" + mediaId + "\",");
			sb.append("\"title\":" + "\"" + title + "\",");
			sb.append("\"description\":" + "\"" + description + "\"");
			sb.append("}");
		} else if (msgType.equals("file")) {
			sb.append("\"msgtype\":" + "\"" + msgType + "\",");
			sb.append("\"file\":" + "{");
			sb.append("\"media_id\":" + "\"" + mediaId + "\"");
			sb.append("}");
		} else if (msgType.equals("news")) {
			sb.append("\"msgtype\":" + "\"" + msgType + "\",");
			sb.append("\"news\":" + "{");
			sb.append("\"articles\":" + "[");
			sb.append("{");
			sb.append("\"title\":" + "\"" + title + "\",");
			sb.append("\"description\":" + "\"" + description + "\",");
			sb.append("\"url\":" + "\"" + url + "\",");
			sb.append("\"picurl\":" + "\"" + picurl + "\"");
			sb.append("}");
			sb.append("]");
			sb.append("}");
		}
		sb.append(",\"safe\":" + "\"" + safe + "\",");
		sb.append("\"agentid\":" + "\"" + agentId + "\",");
		sb.append("\"debug\":" + "\"" + "1" + "\"");
		sb.append("}");
		String json = sb.toString();
		try {

			uRl = new URL(action);

			HttpsURLConnection http = (HttpsURLConnection) uRl.openConnection();

			http.setRequestMethod("POST");

			http.setRequestProperty("Content-Type",

			"application/json;charset=UTF-8");

			http.setDoOutput(true);

			http.setDoInput(true);

			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");//
			// 连接超时30秒

			System.setProperty("sun.net.client.defaultReadTimeout", "30000"); //
			// 读取超时30秒

			http.connect();

			OutputStream os = http.getOutputStream();

			os.write(json.getBytes("UTF-8"));// 传入参数

			InputStream is = http.getInputStream();

			int size = is.available();

			byte[] jsonBytes = new byte[size];

			is.read(jsonBytes);

			String result = new String(jsonBytes, "UTF-8");

			System.out.println("请求返回结果:" + result);

			os.flush();

			os.close();

		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	public static String sendWeChatMsgText(String touser, String content) {
		StationResource weChat = new StationResource();
		weChat.sendWeChatMsg("text", touser, "", "", content, "", "", "", "",
				"", "0");
		return "";
	}

	public static void main(String[] args) {
		// StationResource weChat = new StationResource();
		StationResource.sendWeChatMsgText("385|567",
				"http://www.zluav.net/userCenter/updatePassword.jsp");
		// weChat
		// .sendWeChatMsg(
		// "text",
		// "helper",
		// "",
		// "",
		// "测试senMsg",
		// "",
		// "测试的",
		// "真的是测试的",
		// "http://www.baidu.com",
		// "http://file27.mafengwo.net/M00/B2/12/wKgB6lO0ahWAMhL8AAV1yBFJDJw20.jpeg",
		// "0");
	}

}