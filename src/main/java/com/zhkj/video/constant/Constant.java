package com.zhkj.video.constant;

public class Constant {
	public static class VideoStatus{
		public static final String NOT_DO = "1";//还没做
		public static final String ING = "2";//正在做
		public static final String SUCCESS = "3";//成功
		public static final String ERROR = "4";//出错
	}
	
	public static class Suffix{
		public static final String VIDEO = ".m3u8";
		public static final String IMAGE = ".jpg";
	}

	public static class CmdType{
		public static final String VIDEO = "video";
		public static final String IMAGE = "image";
	}
	
	public static class ENV{
		public static final String WINDOW = "window";
		public static final String LINUX = "linux";
	}
	
}
