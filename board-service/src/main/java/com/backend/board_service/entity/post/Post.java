package com.backend.board_service.entity.post;

import com.backend.board_service.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                   // 게시글 ID

    @Column(nullable = false)
    private String title;              // 제목

    @Column(nullable = false)
    private String contents;           // 내용

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
    @JoinColumn(name = "user_id", nullable = false)
    private User user;                 // 작성자 ID (User 테이블의 ID)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;   // 작성 시간

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Column(nullable = false)
    private Integer likes;          // 좋아요 수

    @Version                        // Optimistic Lock
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;      // 게시판 카테고리

    private String imageUrl;        // 하늘 사진 게시판용
    private String youtubeUrl;      // 음악 공유 게시판용
    private Boolean isAchieved;     // 위시리스트 달성 여부

    public Post updatePost(String title, String contents, Integer likes) {
        return this.toBuilder()
                .title(title)
                .contents(contents)
                .likes(likes)
                .build();
    }

    public void changeTitle(String newTitle) {
        this.title = newTitle;
    }
    public void changeContents(String newContents) {
        this.contents = newContents;
    }
    public void changeLikes(Integer likes) { this.likes = likes; }
}
