package com.zhkj.video.services;

import java.util.List;

import com.zhkj.video.model.Video;

public interface OutVideoService {

	/**
	 * 获取已处理的
	 * @return
	 */
	List<Video> querySuccesss();
	/**
	 * 通过oldName获取
	 * @param oldName
	 * @return
	 */
	Video getByOldName(String oldName);
}
