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
	ExecutorService executor = null;

	@Override
	public void run(String... args) throws Exception {
		ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNamePrefix("video-pool-").build();

		executor = new ThreadPoolExecutor(10, 15, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024),
				namedThreadFactory, new MyRejectPolicy());

		new Thread(this::start, "startVideoThread").start();
	}

	private void start() {
		do {
			List<Video> notDoneVideos = videoService.query100NotDones();
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
				// 修改ing状态,不能放在线程中操作，不然可能循环调用多次后还没执行到
				notDoneVideo.setStatus(VideoStatus.ING);
				videoService.update(notDoneVideo);

				executor.execute(new VideoDetailHandle(notDoneVideo));
			}

		} while (true);
	}
}
/**
 * 超过线程池处理量后还原video执行状态VideoStatus.NOT_DO
 * @author zj
 *
 */
class MyRejectPolicy implements RejectedExecutionHandler{	
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
    	if (r instanceof VideoDetailHandle) {
    		VideoDetailHandle handle = (VideoDetailHandle) r;
    		Video video = handle.getVideo();
    		VideoService videoService = handle.getVideoService();
        	
    		// 修改nodo状态
    		video.setStatus(VideoStatus.NOT_DO);
			videoService.update(video);
        }
    }
}