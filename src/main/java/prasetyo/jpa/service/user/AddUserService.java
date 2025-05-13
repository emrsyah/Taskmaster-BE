package prasetyo.jpa.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import prasetyo.jpa.entity.User;
import prasetyo.jpa.repository.UserRepository;

@Slf4j
@Service
public class AddUserService {

  @Autowired
  private UserRepository userRepository;

  public void handle(User user) throws Exception {
    userRepository.save(user);
  }
}
