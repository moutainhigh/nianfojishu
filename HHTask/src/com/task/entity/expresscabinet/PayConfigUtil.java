package com.task.entity.expresscabinet;
import java.io.Serializable;


public class PayConfigUtil implements Serializable {
	private static final long serialVersionUID = 1L;
    // 账号信息  
   public static String appid = "wxa62d21c7055ef60d";  // appid 填写自己的 
   public static String APP_SECRET = "YOkgs7JR0VEloxkwV2G_GIX0BH2l9URqmF-AXRGwmVhW_5SGCw6bpev3V_syyuzJ"; // appsecret 填写自己的
   public static String MCH_ID = "1309955901"; // 商业号 填写自己微信的 
   public static String API_KEY ="HHTaskpebs123456HHTaskpebs123456"; // Api key 填写自己微信的 
   public static String CREATE_IP ="";  //ip地址
   public static String NOTIFY_URL ="http://www.weixin.qq.com/wxpay/pay.php"; //回调url，测试使用
   //public static String NOTIFY_URL ="http://pebs-ek.imwork.net/"; //回调url(确保可直接在浏览器访问,并且没有访问限制(如登陆))
   public static String UFDODER_URL ="https://api.mch.weixin.qq.com/pay/unifiedorder"; //统一下单url，微信提供的
   public static String CXDODER_URL ="https://api.mch.weixin.qq.com/pay/orderquery";   //查询订单url，微信提供的

}
