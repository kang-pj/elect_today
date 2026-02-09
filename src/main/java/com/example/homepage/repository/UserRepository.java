package com.example.homepage.repository;

import com.example.homepage.entity.User;
import com.example.homepage.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    
    private final UserMapper userMapper;
    
    public List<User> findAll() {
        return userMapper.findAll();
    }
    
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userMapper.findById(id));
    }
    
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userMapper.findByUsername(username));
    }
    
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userMapper.findByEmail(email));
    }
    
    public User save(User user) {
        if (user.getId() == null) {
            userMapper.insert(user);
        } else {
            userMapper.update(user);
        }
        return user;
    }
    
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }
}
