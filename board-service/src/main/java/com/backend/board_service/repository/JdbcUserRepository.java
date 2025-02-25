package com.backend.board_service.repository;

import com.backend.board_service.entity.Address;
import com.backend.board_service.entity.Gender;
import com.backend.board_service.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcUserRepository implements UserRepository {
    private JdbcTemplate jdbcTemplate;

    public JdbcUserRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 0. 주소 저장
    public Address saveAddress(Address address) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.withTableName("addresses").usingGeneratedKeyColumns("address_id");

        // ADDRESS 객체에서 값 추출하고 매핑
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("city", address.getCity());
        parameters.put("street", address.getStreet());
        parameters.put("zipcode", address.getZipcode());

        // 테이블 삽입 후 addressId 가져와서 객체 반환
        Number key = insert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        Long addressId = key.longValue();

        if (addressId == null) {
            throw new RuntimeException("주소 저장 실패");     // 예외처리
        }

        return new Address(addressId, address.getCity(), address.getStreet(), address.getZipcode());
    }

    // 1. 유저 저장
    @Override
    public User saveUser(User user) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.withTableName("users").usingGeneratedKeyColumns("id");

        // 주소 먼저 저장 후 address_id 가져오기
        Address savedAddress = saveAddress(user.getAddress());

        // 데이터 매핑
        Map<String, Object> params = new HashMap<>();
        params.put("email", user.getEmail());
        params.put("pw", user.getPw());
        params.put("age", user.getAge());
        params.put("gender", user.getGender().name());
        params.put("address_id", savedAddress.getAddress_id());

        // 테이블 삽입 후 userID 가져와서 객체 반환
        Number key = insert.executeAndReturnKey(new MapSqlParameterSource(params));
        if (key == null) {
            throw new RuntimeException("유저 저장 실패");
        }

        return new User(key.longValue(), user.getEmail(), user.getPw(), user.getAge(), user.getGender(), LocalDateTime.now(), savedAddress);
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.query(sql, userRowMapper(), id).stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT u.*, a.address_id, a.city, a.street, a.zipcode " +
                "FROM users u " +
                "JOIN addresses a ON u.address_id = a.address_id " +
                "WHERE u.email = ?";

        return jdbcTemplate.query(sql, userRowMapper(), email).stream().findFirst();
    }

    @Override
    public void updateUser(Long id, User user) {
        String sql = "UPDATE users SET pw = ?, age = ?, gender = ?, address_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getPw(), user.getAge(), user.getGender().name(), user.getAddress().getAddress_id(), id);
    }

    @Override
    public void deleteUser(Long id) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("pw"),
                rs.getInt("age"),
                Gender.valueOf(rs.getString("gender")),  // Enum 변환
                rs.getTimestamp("createdAt").toLocalDateTime(),
                new Address(
                        rs.getLong("address_id"),
                        rs.getString("city"),
                        rs.getString("street"),
                        rs.getString("zipcode")
                )
        );
    }

}
