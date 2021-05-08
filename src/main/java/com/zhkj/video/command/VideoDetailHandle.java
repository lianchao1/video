package com.zhkj.video.command;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.iron.base.utils.ApplicationContextUtil;
import com.zhkj.video.config.Config;
import com.zhkj.video.constant.Constant.Suffix;
import com.zhkj.video.constant.Constant.VideoStatus;
import com.zhkj.video.model.Video;
import com.zhkj.video.services.VideoService;

public class VideoDetailHandle implements Runnable {
	private final static Logger logger = LoggerFactory.getLogger(VideoDetailHandle.class);

	private Video video;
	private VideoService videoService;
	private String oldPath;
	private String newPath;
	private String videoName;
	private String imageName;

    private String imageCommand;
    private String videoCommand;

	public VideoDetailHandle(Video video) {
		this.video = video;
		this.videoService = (VideoService) ApplicationContextUtil.getBean("videoService");

		Config config = (Config) ApplicationContextUtil.getBean("config");
		this.oldPath = config.getOldPath();
		this.newPath = config.getNewPath();
		this.videoName = config.getVideoName();
		this.imageName = config.getImageName();
		this.imageCommand = config.getImageCommand();
		this.videoCommand = config.getVideoCommand();
	}

	@Override
	public void run() {
		logger.info("开始执行文件: id:{} oldPath:{} oldName:{} ", this.video.getId(), this.oldPath, this.video.getOldName());

		try {
			/*
			 * 执行视频处理
			 */
			
			//创建新目录
			String needCreatePath = this.newPath + File.separator + this.video.getOldName();
			File file = new File(needCreatePath);
	        boolean fileFlag = file.mkdirs();//false代表目录已经存在
			if(fileFlag) {
				logger.info("创建新目录: id:{} newPath:{}", this.video.getId(), needCreatePath);
			}else {
				logger.info("创建新目录已经存在: id:{} newPath:{}", this.video.getId(), needCreatePath);
			}
			
			//拼凑命令
			String newVideoName = this.video.getOldName() + File.separator + this.videoName + Suffix.VIDEO;
			this.video.setNewName(newVideoName);
			String newImageName = this.video.getOldName() + File.separator + this.imageName + Suffix.IMAGE;
			String videoCommand = String.format(this.videoCommand, this.oldPath + File.separator + this.video.getOldName(), this.newPath + File.separator + newVideoName);
		    String imageCommand = String.format(this.imageCommand, this.oldPath + File.separator + this.video.getOldName(), this.newPath + File.separator + newImageName);
			
		    logger.info("执行视频命令: id:{} videoCommand:{}", this.video.getId(), videoCommand);
			logger.info("执行图片命令: id:{} imageCommand:{}", this.video.getId(), imageCommand);
		    
			
			logger.info("成功执行文件: id:{} newPath:{} newName:{} videoName:{} imageName:{}", this.video.getId(),
					this.newPath, this.video.getNewName(), this.videoName + Suffix.VIDEO, this.imageName + Suffix.IMAGE);
			// 修改done状态
			this.video.setStatus(VideoStatus.DONE);
			videoService.update(video);
		} catch (Exception e) {
			this.video.setNewName(null);//清空newName
			logger.error("异常执行文件: id:{} oldPath:{} oldName:{} ", this.video.getId(), this.oldPath,
					this.video.getOldName(), e);
			// 修改error状态
			this.video.setStatus(VideoStatus.ERROR);
			videoService.update(video);
		}
	}

	public Video getVideo() {
		return video;
	}

	public VideoService getVideoService() {
		return videoService;
	}
    
}
