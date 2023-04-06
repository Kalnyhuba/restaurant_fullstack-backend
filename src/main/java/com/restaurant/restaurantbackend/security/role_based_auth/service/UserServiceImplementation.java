package com.restaurant.restaurantbackend.security.role_based_auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.restaurantbackend.emailhelper.EmailService;
import com.restaurant.restaurantbackend.exception.CustomException;
import com.restaurant.restaurantbackend.security.jwt.accesstoken.AccessTokenProvider;
import com.restaurant.restaurantbackend.security.jwt.refreshtoken.RefreshTokenProvider;
import com.restaurant.restaurantbackend.security.role_based_auth.entity.Role;
import com.restaurant.restaurantbackend.security.role_based_auth.entity.User;
import com.restaurant.restaurantbackend.security.role_based_auth.repository.UserRepository;
import com.restaurant.restaurantbackend.security.role_based_auth.token.entity.Token;
import com.restaurant.restaurantbackend.security.role_based_auth.token.entity.TokenKey;
import com.restaurant.restaurantbackend.security.role_based_auth.token.repository.TokenRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements UserService, UserDetailsService {

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    private final AccessTokenProvider accessTokenProvider;

    private final RefreshTokenProvider refreshTokenProvider;

    private final EmailService emailService;

    private String verifyEmailTemplate;

    private String forgotPasswordTemplate;

    private final EmailValidator emailValidator = EmailValidator.getInstance();

    @Autowired
    public UserServiceImplementation(AuthenticationManager authenticationManager,
                                     PasswordEncoder passwordEncoder,
                                     ObjectMapper objectMapper,
                                     UserRepository userRepository,
                                     TokenRepository tokenRepository,
                                     AccessTokenProvider accessTokenProvider,
                                     RefreshTokenProvider refreshTokenProvider,
                                     EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.accessTokenProvider = accessTokenProvider;
        this.refreshTokenProvider = refreshTokenProvider;
        this.emailService = emailService;
        if (!this.userRepository.existsByRoles(List.of(Role.ADMIN))) {
            User admin = new User("sysadmin", "System", "Administrator", "huba.kalny3@gmail.com",
                    passwordEncoder.encode("testadmin123"), List.of(Role.ADMIN));
            admin.setVerified(true);
            this.userRepository.save(admin);
        }
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource verifyEmailTemplateResource = resourceLoader.getResource("classpath:templates/VerifyEmailTemplate.html");
        Resource forgotPasswordTemplateResource = resourceLoader.getResource("classpath:templates/ForgotPasswordTemplate.html");
        try {
            Reader verifyEmailTemplateReader = new InputStreamReader(verifyEmailTemplateResource.getInputStream(), StandardCharsets.UTF_8);
            Reader forgotPasswordTemplateReader = new InputStreamReader(forgotPasswordTemplateResource.getInputStream(), StandardCharsets.UTF_8);
            verifyEmailTemplate = IOUtils.toString(verifyEmailTemplateReader);
            forgotPasswordTemplate = IOUtils.toString(forgotPasswordTemplateReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new CustomException("No user found", HttpStatus.BAD_REQUEST);
        }
        User user = userOptional.get();
        if (!user.isVerified()) {
            throw new CustomException("Email address not verified", HttpStatus.UNAUTHORIZED);
        }
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), user.getRoles());
    }

    @Override
    @Transactional
    public Map<String, String> signIn(String username, String password, String token) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            List<SimpleGrantedAuthority> roles = authentication.getAuthorities()
                    .stream().map(role -> new SimpleGrantedAuthority(role.toString())).collect(Collectors.toList());
            Date current = new Date();
            Date absoluteValidity = new Date(current.getTime() + refreshTokenProvider.getRefreshTokenAbsoluteValidity());
            long loginIdentifier = current.getTime();
            String accessToken = accessTokenProvider.createToken(username, "access-token", roles);
            String refreshToken = refreshTokenProvider.createToken(token, username, "refresh-token", roles, loginIdentifier, absoluteValidity);
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken, "authorities", objectMapper.writeValueAsString(roles));
        } catch (AuthenticationServiceException e) {
            throw e;
        } catch (AuthenticationException e) {
            throw new CustomException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        } catch (JsonProcessingException e) {
            throw new CustomException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String signUp(User user) {
        try {
            validateUser(user);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(List.of(Role.CLIENT));
            userRepository.save(user);
            String verifyToken = RandomStringUtils.randomAlphanumeric(64, 96);
            String verifyTokenHash = DigestUtils.sha256Hex(verifyToken);
            Long expiresAt = System.currentTimeMillis() + 604800000;
            tokenRepository.save(new Token(new TokenKey(user.getId(), verifyTokenHash), expiresAt, "verify-email"));
            sendRegisterEmail(user, verifyToken);
            return "Successfully registered with user " + user.getUsername();
        } catch (MessagingException e) {
            userRepository.delete(user);
            throw new CustomException("Unable to send confirmation email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateUser(User user) {
        if (userRepository.existsByUsername(user.getUsername()) || userRepository.existsByEmail(user.getEmail())) {
            throw new CustomException("Username or email already exists", HttpStatus.CONFLICT);
        }
        if ("Anonymous".equals(user.getUsername())) {
            throw new CustomException("Username invalid", HttpStatus.BAD_REQUEST);
        }
        if (user.getPassword().length() < 8) {
            throw new CustomException("Password is too short, it must be at least 8 characters long", HttpStatus.BAD_REQUEST);
        }
        if (user.getFirstName().isEmpty() || user.getLastName().isEmpty()) {
            throw new CustomException("Name fields cannot be empty", HttpStatus.BAD_REQUEST);
        }
        if (!emailValidator.isValid(user.getEmail())) {
            throw new CustomException("Invalid email format", HttpStatus.BAD_REQUEST);
        }
    }

    private void sendRegisterEmail(User user, String token) throws MessagingException {
        String subject = "Hitelesítés";
        String line1 = "Sikeres regisztráció [USERNAME] felhasználóként!";
        String line2 = "Az alábbi linken tudja megerősíteni e-mail címét:";
        String line3 = "E-mail cím megerősítése";
        line1 = line1.replace("[USERNAME]", user.getUsername());
        String emailBody = verifyEmailTemplate
                .replace("[LINE_1]", line1)
                .replace("[LINE_2]", line2)
                .replace("[LINE_3]", line3)
                .replace("[TOKEN]", token);
        emailService.sendMessage(user.getEmail(), subject, emailBody, List.of());
    }

    @Override
    public Map<String, String> refresh(String refreshToken) {
        try {
            String username = UserService.getCurrentUsername();
            Collection<? extends GrantedAuthority> roles = getCurrentUserAuthorities();
            Date absoluteValidity = refreshTokenProvider.getAbsoluteExpirationDateFromToken(refreshToken);
            long loginId = refreshTokenProvider.getLoginIdFromToken(refreshToken);
            String newAccessToken = accessTokenProvider.createToken(username, "access-token", roles);
            String newRefreshToken = refreshTokenProvider.createToken(refreshToken, username, "refresh-token", roles,
                    loginId, absoluteValidity);
            return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken, "authorities", objectMapper.writeValueAsString(roles));
        } catch (Exception e) {
            throw new CustomException("Could not refresh token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public Collection<? extends GrantedAuthority> getCurrentUserAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities();
    }

    @Override
    public Integer getCurrentUserId() {
        return userRepository.findByUsername(UserService.getCurrentUsername()).orElseThrow(() -> new CustomException("User not found", HttpStatus.BAD_REQUEST)).getId();
    }

    @Override
    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new CustomException("User not found", HttpStatus.BAD_REQUEST);
        }
        return user.get();
    }

    @Override
    public User getUserByUsernameOrEmail(String word) {
        Optional<User> userOptional = userRepository.findByUsername(word);
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(word);
            if (userOptional.isEmpty()) {
                return null;
            }
        }
        return userOptional.get();
    }

    @Override
    public User getCurrentUser() {
        Optional<User> user = userRepository.findByUsername(UserService.getCurrentUsername());
        if (user.isEmpty()) {
            throw new CustomException("User not found", HttpStatus.BAD_REQUEST);
        }
        return user.get();
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public String signOut(String refreshToken) {
        try {
            refreshTokenProvider.invalidateUserTokenWithLoginId(UserService.getCurrentUsername(), refreshToken);
            SecurityContextHolder.clearContext();
            return "Logout successful";
        } catch (Exception e) {
            throw new CustomException("Logout unsuccessful", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String recoverPassword(String email) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                return "Password reset link sent";
            }
            User user = userOptional.get();
            if (tokenRepository.existsByUserId(user.getId())) {
                return "Password reset link sent";
            }
            String passwordToken = RandomStringUtils.randomAlphanumeric(64, 96);
            String passwordTokenHash = DigestUtils.sha256Hex(passwordToken);
            Long expiresAt = System.currentTimeMillis() + 300000;
            sendForgotPasswordEmail(user, passwordToken);
            tokenRepository.save(new Token(new TokenKey(user.getId(), passwordTokenHash), expiresAt, "password-reset"));
            return "Password reset link set";
        } catch (MessagingException e) {
            return "Password reset link set";
        }
    }

    private void sendForgotPasswordEmail(User user, String token) throws MessagingException {
        String subject = "Jelszó visszaállítás";
        String line1 = "Jelszava visszaállításához kattintson az alábbi linkre:";
        String line2 = "Jelszó visszaállítás";
        String emailBody = forgotPasswordTemplate
                .replace("[LINE_1]", line1)
                .replace("[LINE_2]", line2)
                .replace("[TOKEN]", token);
        emailService.sendMessage(user.getEmail(), subject, emailBody, List.of());
    }

    @Override
    public String resetPassword(String token, String password) {
        String tokenHash = DigestUtils.sha256Hex(token);
        Optional<Token> tokenOptional = tokenRepository.findByToken(tokenHash);
        if (tokenOptional.isEmpty()) {
            throw new CustomException("Password reset unsuccessful", HttpStatus.BAD_REQUEST);
        }
        Token passwordToken = tokenOptional.get();
        Integer userId = passwordToken.getTokenId().getUserId();
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            tokenRepository.deleteByToken(tokenHash);
            throw new CustomException("Password reset unsuccessful", HttpStatus.BAD_REQUEST);
        }
        if (!passwordToken.getPurpose().equals("password-reset")) {
            throw new CustomException("Password reset unsuccessful", HttpStatus.BAD_REQUEST);
        }
        Long expiresAt = passwordToken.getExpiresAt();
        if (System.currentTimeMillis() > expiresAt) {
            tokenRepository.deleteByToken(tokenHash);
            throw new CustomException("Reset token has expired", HttpStatus.BAD_REQUEST);
        }
        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        refreshTokenProvider.invalidateUserTokens(user.getUsername());
        tokenRepository.deleteAllByUserIdAndPurpose(userId, "password-reset");
        return "Password changed successfully";
    }

    @Override
    public String verifyEmail(String token) {
        String tokenHash = DigestUtils.sha256Hex(token);
        Optional<Token> tokenOptional = tokenRepository.findByToken(tokenHash);
        if (tokenOptional.isEmpty()) {
            throw new CustomException("Email verify was not successful", HttpStatus.BAD_REQUEST);
        }
        Token verifyToken = tokenOptional.get();
        Integer userId = verifyToken.getTokenId().getUserId();
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            tokenRepository.deleteByToken(tokenHash);
            throw new CustomException("Email verify was not successful", HttpStatus.BAD_REQUEST);
        }
        if (!verifyToken.getPurpose().equals("verify-email")) {
            throw new CustomException("Email verify was not successful", HttpStatus.BAD_REQUEST);
        }
        Long expiresAt = verifyToken.getExpiresAt();
        if (System.currentTimeMillis() > expiresAt) {
            tokenRepository.deleteByToken(tokenHash);
            userRepository.deleteById(userId);
            throw new CustomException("Verify token has expired", HttpStatus.BAD_REQUEST);
        }
        User user = userOptional.get();
        user.setVerified(true);
        userRepository.save(user);
        tokenRepository.deleteAllByUserIdAndPurpose(userId, "verify-email");
        return "Email successfully verified";
    }

    @Override
    public void removeCookies(HttpServletResponse response) {
        refreshTokenProvider.removeCookies(response);
    }

    @Override
    public ResponseCookie setCookie(String name, String value, long age, boolean httpOnly, String path) {
        return refreshTokenProvider.setCookie(name, value, age, httpOnly, path);
    }

    @Scheduled(fixedDelay = 86400000)
    public void deleteExpiredRefreshTokens() {
        refreshTokenProvider.invalidateAllExpiredTokens();
    }

    @Scheduled(fixedDelay = 86400000)
    public void deleteExpiredPasswordTokens() {
        tokenRepository.deleteAllByExpiresAtBeforeAndPurpose(System.currentTimeMillis(), "password-reset");
    }

    @Scheduled(fixedDelay = 604800000)
    public void deleteExpiredVerifyTokens() {
        long time = System.currentTimeMillis();
        String purpose = "verify-email";
        List<Token> tokens = tokenRepository.findAllByExpiresAtBeforeAndPurpose(time, purpose);
        for (Token token : tokens) {
            Integer userId = token.getTokenId().getUserId();
            userRepository.deleteById(userId);
        }
        tokenRepository.deleteAllByExpiresAtBeforeAndPurpose(time, purpose);
    }
}
