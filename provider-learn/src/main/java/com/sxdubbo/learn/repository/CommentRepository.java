package com.sxdubbo.learn.repository;

import com.sxdubbo.learn.domain.CommentPO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * created by  luwei
 * 2018-01-22 19:23.
 **/
public interface CommentRepository extends JpaRepository<CommentPO, String> {

    public CommentPO findByVideoId(Integer videoId);

    public List<CommentPO> findByUserId(Integer userId);
}
