package com.example.homepage.mapper;

import com.example.homepage.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM users")
    List<User> findAll();
    
    @Select("SELECT * FROM users WHERE id = #{id} LIMIT 1")
    User findById(Long id);
    
    @Select("SELECT * FROM users WHERE username = #{username} LIMIT 1")
    User findByUsername(String username);
    
    @Select("SELECT * FROM users WHERE email = #{email} LIMIT 1")
    User findByEmail(String email);
    
    @Insert("INSERT INTO users (username, password, email, created_at) VALUES (#{username}, #{password}, #{email}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Update("UPDATE users SET username = #{username}, password = #{password}, email = #{email} WHERE id = #{id}")
    int update(User user);
    
    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(Long id);
}
