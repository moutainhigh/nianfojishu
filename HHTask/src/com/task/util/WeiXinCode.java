package com.task.util;

public class WeiXinCode {
//	字符型银行编码  银行名称  
//	private String "ICBC_DEBIT" //工商银行(借记卡) 
//	private String "ICBC_CREDIT" //工商银行(信用卡) 
//	private String "ABC_DEBIT" //农业银行(借记卡) 
//	private String "ABC_CREDIT" //农业银行(信用卡) 
//	private String "PSBC_DEBIT" //邮政储蓄银行(借记卡) 
//	private String "PSBC_CREDIT" //邮政储蓄银行(信用卡) 
//	private String "CCB_DEBIT" //建设银行(借记卡) 
//	private String "CCB_CREDIT" //建设银行(信用卡) 
//	private String "CMB_DEBIT" //招商银行(借记卡) 
//	private String "CMB_CREDIT" //招商银行(信用卡) 
//	private String "BOC_DEBIT" //中国银行(借记卡) 
//	private String "BOC_CREDIT" //中国银行(信用卡) 
//	private String "COMM_DEBIT" //交通银行(借记卡) 
//	private String "SPDB_DEBIT" //浦发银行(借记卡) 
//	private String "SPDB_CREDIT" //浦发银行(信用卡) 
//	private String "GDB_DEBIT" //广发银行(借记卡) 
//	private String "GDB_CREDIT" //广发银行(信用卡) 
//	private String "CMBC_DEBIT" //民生银行(借记卡) 
//	private String "CMBC_CREDIT" //民生银行(信用卡) 
//	private String "PAB_DEBIT" //平安银行(借记卡) 
//	private String "PAB_CREDIT" // 平安银行(信用卡) 
//	private String "CEB_DEBIT" // 光大银行(借记卡) 
//	private String "CEB_CREDIT" // 光大银行(信用卡) 
//	private String "CIB_DEBIT" // 兴业银行(借记卡) 
//	private String "CIB_CREDIT" // 兴业银行(信用卡) 
//	private String "CITIC_DEBIT" // 中信银行(借记卡) 
//	private String "CITIC_CREDIT" // 中信银行(信用卡) 
//	private String "BOSH_DEBIT" // 上海银行(借记卡) 
//	private String "BOSH_CREDIT" // 上海银行(信用卡) 
//	private String "CRB_DEBIT" // 华润银行(借记卡) 
//	private String "HZB_DEBIT" // 杭州银行(借记卡) 
//	private String "HZB_CREDIT" // 杭州银行(信用卡) 
//	private String "BSB_DEBIT" // 包商银行(借记卡) 
//	private String "BSB_CREDIT" // 包商银行(信用卡) 
//	private String "CQB_DEBIT" // 重庆银行(借记卡) 
//	private String "SDEB_DEBIT" // 顺德农商行(借记卡) 
//	private String "SZRCB_DEBIT" // 深圳农商银行(借记卡) 
//	private String "HRBB_DEBIT" // 哈尔滨银行(借记卡) 
//	private String "BOCD_DEBIT" // 成都银行(借记卡) 
//	private String "GDNYB_DEBIT" // 南粤银行(借记卡) 
//	private String "GDNYB_CREDIT" // 南粤银行(信用卡) 
//	private String "GZCB_DEBIT" // 广州银行(借记卡) 
//	private String "GZCB_CREDIT" // 广州银行(信用卡) 
//	private String "JSB_DEBIT" // 江苏银行(借记卡) 
//	private String "JSB_CREDIT" // 江苏银行(信用卡) 
//	private String "NBCB_DEBIT 宁波银行(借记卡) 
//	private String "NBCB_CREDIT 宁波银行(信用卡) 
//	private String "NJCB_DEBIT 南京银行(借记卡) 
//	private String "JZB_DEBIT 晋中银行(借记卡) 
//	private String "KRCB_DEBIT 昆山农商(借记卡) 
//	private String "LJB_DEBIT 龙江银行(借记卡) 
//	private String "LNNX_DEBIT 辽宁农信(借记卡) 
//	private String "LZB_DEBIT 兰州银行(借记卡) 
//	private String "WRCB_DEBIT 无锡农商(借记卡) 
//	private String "ZYB_DEBIT 中原银行(借记卡) 
//	private String "ZJRCUB_DEBIT 浙江农信(借记卡) 
//	private String "WZB_DEBIT 温州银行(借记卡) 
//	private String "XAB_DEBIT 西安银行(借记卡) 
//	private String "JXNXB_DEBIT 江西农信(借记卡) 
//	private String "NCB_DEBIT 宁波通商银行(借记卡) 
//	private String "NYCCB_DEBIT 南阳村镇银行(借记卡) 
//	private String "NMGNX_DEBIT 内蒙古农信(借记卡) 
//	private String "SXXH_DEBIT 陕西信合(借记卡) 
//	private String "SRCB_CREDIT 上海农商银行(信用卡) 
//	private String "SJB_DEBIT 盛京银行(借记卡) 
//	private String "SDRCU_DEBIT 山东农信(借记卡) 
//	private String "SRCB_DEBIT 上海农商银行(借记卡) 
//	private String "SCNX_DEBIT 四川农信(借记卡) 
//	private String "QLB_DEBIT 齐鲁银行(借记卡) 
//	private String "QDCCB_DEBIT 青岛银行(借记卡) 
//	private String "PZHCCB_DEBIT 攀枝花银行(借记卡) 
//	private String "ZJTLCB_DEBIT 浙江泰隆银行(借记卡) 
//	private String "TJBHB_DEBIT 天津滨海农商行(借记卡) 
//	private String "WEB_DEBIT 微众银行(借记卡) 
//	private String "YNRCCB_DEBIT 云南农信(借记卡) 
//	private String "WFB_DEBIT 潍坊银行(借记卡) 
//	private String "WHRC_DEBIT 武汉农商行(借记卡) 
//	private String "ORDOSB_DEBIT 鄂尔多斯银行(借记卡) 
//	private String "XJRCCB_DEBIT 新疆农信银行(借记卡) 
//	private String "ORDOSB_CREDIT 鄂尔多斯银行(信用卡) 
//	private String "CSRCB_DEBIT 常熟农商银行(借记卡) 
//	private String "JSNX_DEBIT 江苏农商行(借记卡) 
//	private String "GRCB_CREDIT 广州农商银行(信用卡) 
//	private String "GLB_DEBIT 桂林银行(借记卡) 
//	private String "GDRCU_DEBIT 广东农信银行(借记卡) 
//	private String "GDHX_DEBIT 广东华兴银行(借记卡) 
//	private String "FJNX_DEBIT 福建农信银行(借记卡) 
//	private String "DYCCB_DEBIT 德阳银行(借记卡) 
//	private String "DRCB_DEBIT 东莞农商行(借记卡) 
//	private String "CZCB_DEBIT 稠州银行(借记卡) 
//	private String "CZB_DEBIT 浙商银行(借记卡) 
//	private String "CZB_CREDIT 浙商银行(信用卡) 
//	private String "GRCB_DEBIT 广州农商银行(借记卡) 
//	private String "CSCB_DEBIT 长沙银行(借记卡) 
//	private String "CQRCB_DEBIT 重庆农商银行(借记卡) 
//	private String "CBHB_DEBIT 渤海银行(借记卡) 
//	private String "BOIMCB_DEBIT 内蒙古银行(借记卡) 
//	private String "BOD_DEBIT 东莞银行(借记卡) 
//	private String "BOD_CREDIT 东莞银行(信用卡) 
//	private String "BOB_DEBIT 北京银行(借记卡) 
//	private String "BNC_DEBIT 江西银行(借记卡) 
//	private String "BJRCB_DEBIT 北京农商行(借记卡) 
//	private String "AE_CREDIT AE(信用卡) 
//	private String "GYCB_CREDIT 贵阳银行(信用卡) 
//	private String "JSHB_DEBIT 晋商银行(借记卡) 
//	private String "JRCB_DEBIT 江阴农商行(借记卡) 
//	private String "JNRCB_DEBIT 江南农商(借记卡) 
//	private String "JLNX_DEBIT 吉林农信(借记卡) 
//	private String "JLB_DEBIT 吉林银行(借记卡) 
//	private String "JJCCB_DEBIT 九江银行(借记卡) 
//	private String "HXB_DEBIT 华夏银行(借记卡) 
//	private String "HXB_CREDIT 华夏银行(信用卡) 
//	private String "HUNNX_DEBIT 湖南农信(借记卡) 
//	private String "HSB_DEBIT 徽商银行(借记卡) 
//	private String "HSBC_DEBIT 恒生银行(借记卡) 
//	private String "HRXJB_DEBIT 华融湘江银行(借记卡) 
//	private String "HNNX_DEBIT 河南农信(借记卡) 
//	private String "HKBEA_DEBIT 东亚银行(借记卡) 
//	private String "HEBNX_DEBIT 河北农信(借记卡) 
//	private String "HBNX_DEBIT 湖北农信(借记卡) 
//	private String "HBNX_CREDIT 湖北农信(信用卡) 
//	private String "GYCB_DEBIT 贵阳银行(借记卡) 
//	private String "GSNX_DEBIT 甘肃农信(借记卡) 
//	private String "JCB_CREDIT JCB(信用卡) 
//	private String "MASTERCARD_CREDIT MASTERCARD(信用卡) 
//	private String "VISA_CREDIT VISA(信用卡) 

}
