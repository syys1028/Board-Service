package com.backend.board_service.repository;

import com.backend.board_service.entity.Address;
import com.backend.board_service.entity.Gender;
import com.backend.board_service.entity.Post;
import com.backend.board_service.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcPostRepository implements PostRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcPostRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 1. 게시글 작성
    @Override
    public Post savePost(Post post) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.withTableName("posts").usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("title", post.getTitle());
        params.put("contents", post.getContents());
        params.put("user_id", post.getUserID());
        params.put("likes", post.getLikes());

        // 테이블 삽입 후 postID 가져와서 객체 반환
        Number key = insert.executeAndReturnKey(new MapSqlParameterSource(params));
        return new Post(key.longValue(), post.getTitle(), post.getContents(), post.getUserID(), LocalDateTime.now(), post.getLikes());
    }

    // 2. 게시글 목록 조회
    @Override
    public List<Post> findAll() {
        String sql = "SELECT * FROM posts";
        return jdbcTemplate.query(sql, postRowMapper());
    }

    // 3. 게시글 상세 조회 (회원 ID로 조회 -> 회원 user.email에서 받아옴)
    @Override
    public Optional<Post> findByPostId(Long id) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        List<Post> posts = jdbcTemplate.query(sql, postRowMapper(), id);
        return posts.stream().findFirst();
    }

    @Override
    public Optional<Post> findByUserId(Long userId) {
        String sql = "SELECT * FROM posts WHERE user_id = ?";
        List<Post> posts = jdbcTemplate.query(sql, postRowMapper(), userId);
        return posts.stream().findFirst();
    }

    // 4. 게시글 수정
    @Override
    public void updatePost(Long id, String title, String contents, Integer likes) {
        String sql = "UPDATE posts SET title = ?, contents = ?, likes = ? WHERE id = ?";
        jdbcTemplate.update(sql, title, contents, likes, id);
    }

    // 5. 게시글 삭제
    @Override
    public void deletePost(Long id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // 6. 좋아요 업데이트
    @Override
    public void updatePostLike(Long id, Integer likes) {
        String sql = "UPDATE posts SET likes = ? WHERE id = ?";
        jdbcTemplate.update(sql, likes, id);
    }

    // Mapper
    private RowMapper<Post> postRowMapper() {
        return (rs, rowNum) -> new Post(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("contents"),
                rs.getLong("user_id"),
                rs.getTimestamp("createdAt") != null ? rs.getTimestamp("createdAt").toLocalDateTime() : null,
                rs.getInt("likes")
        );
    }
}
