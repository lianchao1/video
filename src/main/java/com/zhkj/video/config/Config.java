package com.zhkj.video.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("config")
public class Config {
	
    @Value("${video.file.old.path}")
    private String oldPath;
    @Value("${video.file.new.path}")
    private String newPath;
    @Value("${video.file.new.video-name}")
    private String videoName;
    @Value("${video.file.new.image-name}")
    private String imageName;
    

    @Value("${video.command.image}")
    private String imageCommand;
    @Value("${video.command.video}")
    private String videoCommand;
    
	public String getOldPath() {
		return oldPath;
	}
	public void setOldPath(String oldPath) {
		this.oldPath = oldPath;
	}
	public String getNewPath() {
		return newPath;
	}
	public void setNewPath(String newPath) {
		this.newPath = newPath;
	}
	public String getVideoName() {
		return videoName;
	}
	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getImageCommand() {
		return imageCommand;
	}
	public void setImageCommand(String imageCommand) {
		this.imageCommand = imageCommand;
	}
	public String getVideoCommand() {
		return videoCommand;
	}
	public void setVideoCommand(String videoCommand) {
		this.videoCommand = videoCommand;
	}
}
