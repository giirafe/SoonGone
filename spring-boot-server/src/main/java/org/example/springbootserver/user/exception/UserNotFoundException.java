package org.example.springbootserver.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("User not found with userId: " + userId);
    }
}