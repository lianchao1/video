package com.zhkj.video.services;

import java.util.List;

import com.zhkj.video.model.Video;

public interface VideoService extends OutVideoService{

	static final String QUERY_100_NOT_DOS_SQL = "select * from video where status not in ('2','3','4') or status is null order by create_time,id limit 100";
	static final String QUERY_NOT_DOS_SQL = "select * from video where status not in ('2','3','4') or status is null order by create_time,id";
	static final String QUERY_SUCCESSSS_SQL = "select * from video where status = '3' order by create_time,id";
	static final String UPDATE_SQL = "update video set new_name = ?, during_second = ?, status = ?, update_time = now() where id = ?";
	static final String GET_BY_OLDNAME_SQL = "select * from video where old_name = ?";
	static final String GET_SQL = "select * from video where id = ?";
	
	/**
	 * 获取最多100条未处理的
	 * @return
	 */
	List<Video> query100NotDos();
	/**
	 * 获取未处理的
	 * @return
	 */
	List<Video> queryNotDos();
	/**
	 * 保存
	 * @param video
	 */
	void update(Video video);
	/**
	 * 通过id获取
	 * @param id
	 * @return
	 */
	Video get(Long id);
}
