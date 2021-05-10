package com.zhkj.video.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iron.base.utils.ApplicationContextUtil;
import com.zhkj.video.config.Config;
import com.zhkj.video.constant.Constant.CmdType;
import com.zhkj.video.constant.Constant.ENV;
import com.zhkj.video.constant.Constant.Suffix;
import com.zhkj.video.constant.Constant.VideoStatus;
import com.zhkj.video.model.Video;
import com.zhkj.video.services.VideoService;

import cn.hutool.core.date.DateUtil;

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

	private String env;

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
		this.env = config.getEnv();
	}

	@Override
	public void run() {
		logger.info("开始执行文件: id:{} oldPath:{} oldName:{} ", this.video.getId(), this.oldPath, this.video.getOldName());
		Long startDate = DateUtil.currentSeconds();
		try {
			/*
			 * 执行视频处理
			 */

			// 创建新目录
			String needCreatePath = this.newPath + File.separator + this.video.getOldName();
			File file = new File(needCreatePath);
			boolean fileFlag = file.mkdirs();// false代表目录已经存在
			if (fileFlag) {
				logger.info("创建新目录: id:{} newPath:{}", this.video.getId(), needCreatePath);
			} else {
				logger.info("创建新目录已经存在: id:{} newPath:{}", this.video.getId(), needCreatePath);
			}

			// 拼凑命令
			String newVideoName = this.video.getOldName() + File.separator + this.videoName + Suffix.VIDEO;
			this.video.setNewName(newVideoName);
			String newVideoPathName = this.newPath + File.separator + newVideoName;
			String newImagePathName = this.newPath + File.separator + this.video.getOldName() + File.separator
					+ this.imageName + Suffix.IMAGE;
			String videoCommand = String.format(this.videoCommand + " %s %s",
					this.oldPath + File.separator + this.video.getOldName(), newVideoPathName);
			String imageCommand = String.format(this.imageCommand + " %s %s",
					this.oldPath + File.separator + this.video.getOldName(), newImagePathName);

			logger.info("执行视频命令: id:{} videoCommand:{}", this.video.getId(), videoCommand);
			logger.info("执行图片命令: id:{} imageCommand:{}", this.video.getId(), imageCommand);

			boolean flag = this.doProcess(CmdType.VIDEO, newVideoPathName, videoCommand);
			if (flag) {
				flag = this.doProcess(CmdType.IMAGE, newImagePathName, imageCommand);
				if (!flag) {
					throw new RuntimeException("执行图片命令失败！");
				}
			} else {
				throw new RuntimeException("执行视频命令失败！");
			}
			
			//耗时秒
			this.video.setDuringSecond(DateUtil.currentSeconds() - startDate);
			
			logger.info("成功执行文件: id:{} 耗时秒:{}ms newPath:{} newName:{} videoName:{} imageName:{}", this.video.getId(),
					this.video.getDuringSecond(), this.newPath, this.video.getNewName(), this.videoName + Suffix.VIDEO,
					this.imageName + Suffix.IMAGE);
			// 修改done状态
			this.video.setStatus(VideoStatus.SUCCESS);
			videoService.update(video);
		} catch (Exception e) {
			this.video.setNewName(null);// 清空newName
			logger.error("异常执行文件: id:{} oldPath:{} oldName:{} ", this.video.getId(), this.oldPath,
					this.video.getOldName(), e);
			// 修改error状态
			this.video.setStatus(VideoStatus.ERROR);
			videoService.update(video);
		}
	}

	private boolean doProcess(String type, String newFilePathName,String cmd) throws IOException, InterruptedException {
		Process pro = null;
		if(ENV.WINDOW.equals(this.env)) {
			pro = Runtime.getRuntime().exec(new String[]{"cmd", "/c", cmd});
		}else{
			pro = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
		}
		
		//错误流需要被消费掉
		final BufferedReader es = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
		new Thread(new ConsumeStreamRunnable(es)).start();
		//输入流需要被消费掉
		final BufferedReader is = new BufferedReader(new InputStreamReader(pro.getInputStream()));
		new Thread(new ConsumeStreamRunnable(is)).start();
		
		int code = pro.waitFor();
//		if(code != 0) {//图片生成会报错，但成功生成图片的
//			return false;
//		}
		
        Thread.sleep(5000l);
        File f = new File(newFilePathName);
        return f.exists();
	}
	
	public Video getVideo() {
		return video;
	}

	public VideoService getVideoService() {
		return videoService;
	}

}

class ConsumeStreamRunnable implements Runnable{
	private final static Logger logger = LoggerFactory.getLogger(ConsumeStreamRunnable.class);
	
	BufferedReader br = null;
	public ConsumeStreamRunnable(BufferedReader br) {
		this.br = br;
	}

	@Override
	public void run() {
		String line = null;
		boolean isDebugEnabled = logger.isDebugEnabled();
		StringBuilder sb = new StringBuilder();
		try {
			while ((line = br.readLine()) != null) {
				if(isDebugEnabled) {
					if (sb.length() == 0) {
						sb.append(line);
                    } else {
                    	sb.append("\n").append(line);
                    };
				}
			}
			if(isDebugEnabled) {
				logger.debug(sb.toString());
			}
		} catch (IOException e) {
			logger.error("", e);
		}
	}
}
	


