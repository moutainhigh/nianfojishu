package com.task.ServerImpl.sys;

import com.task.Dao.TotalDao;
import com.task.DaoImpl.TotalDaoImpl;
import com.task.Server.sys.ShortLinkServer;
import com.task.ServerImpl.AlertMessagesServerImpl;
import com.task.entity.system.ShortLink;

public class ShortLinkServerImpl implements ShortLinkServer{

	private TotalDao totalDao;
	
	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	@Override
	public String addShortLink(String longUrl,String remark) {
		
		ShortLink shortLink = new ShortLink();
		shortLink.setRemark(remark);
		shortLink.setLongUrl(longUrl);
		boolean save = totalDao.save(shortLink);
		if(save) {
			String shortUrl = AlertMessagesServerImpl.pebsUrl+"/s/"+shortLink.getId();
			totalDao.update(shortLink);
			return shortUrl;
		}
		//生成短链接特征值
//		String oldVal = (String) totalDao.getObjectByCondition("select val from ShortLink where id in (select max(id) from ShortLink)");
		
		return null;
	}
	
	public static String addStaticShortLink(String longUrl,String remark) {
		TotalDao totalDao = TotalDaoImpl.findTotalDao();
		ShortLink shortLink = new ShortLink();
		shortLink.setRemark(remark);
		shortLink.setLongUrl(longUrl);
		boolean save = totalDao.save(shortLink);
		if(save) {
			String shortUrl = AlertMessagesServerImpl.pebsUrl+"/s/"+shortLink.getId();
			totalDao.update(shortLink);
			return shortUrl;
		}
		//生成短链接特征值
//		String oldVal = (String) totalDao.getObjectByCondition("select val from ShortLink where id in (select max(id) from ShortLink)");
		
		return null;
	}

	@Override
	public ShortLink getshortByLongLink(String shortUrl) {
		
		if(shortUrl!=null&& !shortUrl.equals("")) {
			int id = Integer.parseInt(shortUrl);
			ShortLink shortLink = (ShortLink) totalDao.getObjectByCondition("from ShortLink where id =?", id);
			return shortLink;
		}
		return null;
	}
	
	
}
