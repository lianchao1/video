package com.zhkj.video.command;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.zhkj.video.config.Config;
import com.zhkj.video.constant.Constant.VideoStatus;
import com.zhkj.video.model.Video;
import com.zhkj.video.services.VideoService;

import cn.hutool.core.thread.ThreadFactoryBuilder;

@Component
@Order(value = 1)
public class VideoCommand implements CommandLineRunner {
	private final static Logger logger = LoggerFactory.getLogger(VideoCommand.class);
	private final static Long EMPTY_DATA_SLEEP_TIME = 10000l;
	@Autowired
	private VideoService videoService;
	@Autowired
	private Config config;
	ExecutorService executor = null;
	public static ExecutorService consumeStreamExecutor = null;

	@Override
	public void run(String... args) throws Exception {
		ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNamePrefix("video-pool-").build();
		executor = new ThreadPoolExecutor(config.getCorePoolSize(), config.getCorePoolSize(), 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1024),
				namedThreadFactory, new MyRejectPolicy());
		
		ThreadFactory consumeStreamNamedThreadFactory = new ThreadFactoryBuilder().setNamePrefix("video-consumeStream-pool-").build();
		consumeStreamExecutor = new ThreadPoolExecutor(config.getCorePoolSize() * 2, config.getCorePoolSize() * 2, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1024),
				consumeStreamNamedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

		new Thread(this::start, "startVideoThread").start();
	}

	private void start() {
		do {
			List<Video> notDoneVideos = videoService.query100NotDos();
			if (notDoneVideos.size() == 0) {
				try {
					Thread.sleep(VideoCommand.EMPTY_DATA_SLEEP_TIME);
				} catch (InterruptedException e) {
					logger.error("sleep interruptedException", e);
				}
				continue;
			}
			List<Future<Video>> futures = new ArrayList<Future<Video>>();
			for (Video notDoneVideo : notDoneVideos) {
				if(notDoneVideo == null || notDoneVideo.getId() == null || StringUtils.isBlank(notDoneVideo.getOldName()))continue;
				// ??????ing??????,??????????????????????????????????????????????????????????????????????????????
				notDoneVideo.setStatus(VideoStatus.ING);
				videoService.update(notDoneVideo);

				executor.execute(new VideoDetailHandle(notDoneVideo));
			}

		} while (true);
	}
}
/**
 * ?????????????????????????????????video????????????VideoStatus.NOT_DO
 * @author zj
 *
 */
class MyRejectPolicy implements RejectedExecutionHandler{
	private final static Logger logger = LoggerFactory.getLogger(MyRejectPolicy.class);
	private final static Long OUT_DATA_SLEEP_TIME = 30 * 60 * 1000l;
	
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
    	if (r instanceof VideoDetailHandle) {
    		VideoDetailHandle handle = (VideoDetailHandle) r;
    		Video video = handle.getVideo();
    		VideoService videoService = handle.getVideoService();
        	
    		// ??????nodo??????
    		video.setStatus(VideoStatus.NOT_DO);
			videoService.update(video);
			
			try {
				Thread.sleep(MyRejectPolicy.OUT_DATA_SLEEP_TIME);
			} catch (InterruptedException e) {
				logger.error("sleep interruptedException", e);
			}
        }
    }
}