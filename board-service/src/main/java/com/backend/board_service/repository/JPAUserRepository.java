package com.backend.board_service.repository;

import com.backend.board_service.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class JPAUserRepository implements UserRepository {

    private final EntityManager em;

    public JPAUserRepository(EntityManager em) {
        this.em = em;
    }

    // 1. 회원 가입
    @Override
    public User saveUser(User user) {
        em.persist(user);
        return user;
    }

    // 2-1. 회원 정보 조회 (아이디)
    @Override
    public Optional<User> findById(Long id) {
        User user = em.find(User.class, id);
        return Optional.ofNullable(user);
    }

    // 2-2. 회원 정보 조회 (이메일)
    @Override
    public Optional<User> findByEmail(String email) {
        String jpql = "SELECT u FROM User u JOIN FETCH u.address WHERE u.email = :email";
        TypedQuery<User> query = em.createQuery(jpql, User.class);
        query.setParameter("email", email);
        List<User> result = query.getResultList();
        return result.stream().findFirst();
    }

    // 3. 회원 정보 수정
    @Override
    public void updateUser(Long id, User user) {
        em.merge(user);
    }

    // 4. 회원 삭제
    @Override
    public void deleteUser(Long id) {
        findById(id).ifPresent(em::remove);
    }

    // 5. 아이디 존재 확인
    @Override
    public boolean existsById(Long id) {
        return em.find(User.class, id) != null;
    }

}
