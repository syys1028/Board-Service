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

    public Long saveAddress(Address address) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.withTableName("addresses").usingGeneratedKeyColumns("address_id");

        // ADDRESS 객체에서 값 추출하고 매핑
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("city", address.getCity());
        parameters.put("street", address.getStreet());
        parameters.put("zipcode", address.getZipcode());

        // 테이블 삽입 후 addressId 반환
        Number key = insert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        return key != null ? key.longValue() : null;     // 예외처리
    }
    @Override
    public User saveUser(User user) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.withTableName("users").usingGeneratedKeyColumns("id");

        // 주소 먼저 저장 후 address_id 가져오기
        Long addressId = saveAddress(user.getAddress());
        if (addressId == null) {
            throw new RuntimeException("주소 저장 실패");
        }

        // USER 객체에서 값 추출
        String email = user.getEmail();
        String pw = user.getPw();
        int age = user.getAge();
        Gender gender = user.getGender();
        LocalDateTime createdAt = user.getCreatedAt();

        // 데이터 매핑
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("pw", pw);
        params.put("age", age);
        params.put("gender", gender);
        params.put("createdAt", createdAt);
        params.put("address_id", addressId);

        // 테이블 삽입 후 userID 가져와서 객체 반환
        Number key = insert.executeAndReturnKey(new MapSqlParameterSource(params));
        return new User(key.longValue(), email, pw, age, gender, createdAt, user.getAddress());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public void updateUser(Long id, User user) {

    }

    @Override
    public void deleteUser(Long id) {

    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("pw"),
                rs.getInt("age"),
                Gender.valueOf(rs.getString("gender")),
                LocalDateTime.now(),
                new Address(
                        rs.getLong("address_id"),
                        rs.getString("address_city"),
                        rs.getString("address_street"),
                        rs.getString("address_zipcode")
                )
        );
    }
}
