package com.backend.board_service.repository;

import com.backend.board_service.entity.Post;
import com.backend.board_service.entity.QPost;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JPAPostRepository implements PostRepository {

    private final EntityManager em;

    public JPAPostRepository(EntityManager em) {
        this.em = em;
    }

    // 1. 게시글 작성
    @Override
    public Post savePost(Post post) {
        em.persist(post);
        return post;
    }

    // 2. 게시글 목록 조회
    @Override
    public List<Post> findAll() {
        String jpql = "select p from Post p";
        TypedQuery<Post> query = em.createQuery(jpql, Post.class);
        return query.getResultList();
    }

    // 3. 게시글 상세 조회 (회원 ID로 조회 -> 회원 user.email에서 받아옴)
    @Override
    public Optional<Post> findByPostId(Long id) {
        Post post = em.find(Post.class, id);
        return Optional.ofNullable(post);
    }

    @Override
    public List<Post> findByUserId(Long userId) {
        String jpql = "select p from Post p where p.user.id = :userId";
        TypedQuery<Post> query = em.createQuery(jpql, Post.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    // 4. 게시글 수정
    @Override
    public void updatePost(Long id, Post post) {
        em.merge(post);
    }

    // 5. 게시글 삭제
    @Override
    public void deletePost(Long id) {
        findByPostId(id).ifPresent(em::remove);
    }

    // 6. 좋아요 업데이트
    @Override
    public void updatePostLike(Long id, Integer likes) {
        String jpql = "update Post p set p.likes = :likes where p.id = :id";
        em.createQuery(jpql)
                .setParameter("likes", likes)
                .setParameter("id", id)
                .executeUpdate();
    }

    // 7. 특정 키워드 게시글 검색 (QueryDSL 사용)
    @Override
    public List<Post> searchPosts(String keyword) {
        QPost post = QPost.post;
        return new JPAQueryFactory(em)
                .selectFrom(post)
                .where(post.title.containsIgnoreCase(keyword))
                .fetch();
    }


}
