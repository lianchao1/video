package com.zhkj.video.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.zhkj.video.model.Video;

@Service("videoService")
public class VideoServiceImpl implements VideoService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
	@Override
	public List<Video> query100NotDos() {
		return jdbcTemplate.query(VideoService.QUERY_100_NOT_DOS_SQL,new Object[0], new VideoRowMapper());
	}
    
	@Override
	public List<Video> queryNotDos() {
		return jdbcTemplate.query(VideoService.QUERY_NOT_DOS_SQL,new Object[0], new VideoRowMapper());
	}

	@Override
	public List<Video> querySuccesss() {
		return jdbcTemplate.query(VideoService.QUERY_SUCCESSSS_SQL,new Object[0], new VideoRowMapper());
	}

	@Override
	public void update(Video video) {
		if(video == null || video.getId() == null) throw new RuntimeException("video对象错误，更新失败！");
		
		jdbcTemplate.update(VideoService.UPDATE_SQL, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, video.getNewName());
                preparedStatement.setLong(2, video.getDuringSecond());
                preparedStatement.setString(3, video.getStatus());
                preparedStatement.setLong(4, video.getId());
            }
        });
	}

	@Override
	public Video getByOldName(String oldName) {
		if(StringUtils.isBlank(oldName)) return null;
		List<Video> videos = jdbcTemplate.query(VideoService.GET_BY_OLDNAME_SQL,new Object[]{oldName}, new VideoRowMapper());
		Video video = null;
        if(!videos.isEmpty()){
        	video = videos.get(0);
        }
        return video;
	}

	@Override
	public Video get(Long id) {
		if(id == null) return null;
		List<Video> videos = jdbcTemplate.query(VideoService.GET_SQL,new Object[]{id}, new VideoRowMapper());
		Video video = null;
        if(!videos.isEmpty()){
        	video = videos.get(0);
        }
        return video;
	}

}

class VideoRowMapper implements RowMapper<Video> {
    @Override
    public Video mapRow(ResultSet resultSet,int i) throws SQLException{
    	Video video = new Video();
    	video.setId(resultSet.getLong("id"));
        video.setOldName(resultSet.getString("old_name"));
        video.setNewName(resultSet.getString("new_name"));
        video.setStatus(resultSet.getString("status"));
        video.setCreateTime(resultSet.getDate("create_time"));
        video.setUpdateTime(resultSet.getDate("update_time"));
        video.setDuringSecond(resultSet.getLong("during_second"));
        return video;
    }
}
