package ru.fourbarman.planner.micro.plannerusers.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fourbarman.planner.micro.plannerentity.entity.User;
import ru.fourbarman.planner.micro.plannerusers.repository.UserRepository;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(long id) {
        return userRepository.findById(id).get();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Page<User> findByParams(String username, String email, PageRequest pageRequest) {
        return userRepository.findByParams(username, email, pageRequest);
    }

    public User add(User user) {
        return userRepository.save(user);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public void deleteByUserId(long id) {
        userRepository.deleteById(id);
    }

    public void deleteByUserEmail(String email) {
        userRepository.deleteByEmail(email);
    }
}
