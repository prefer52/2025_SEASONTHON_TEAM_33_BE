package com.goormthon.samsamejo.service;

import com.goormthon.samsamejo.domain.RefreshToken;
import com.goormthon.samsamejo.domain.Users;
import com.goormthon.samsamejo.domain.type.EProvider;
import com.goormthon.samsamejo.domain.type.ERole;
import com.goormthon.samsamejo.domain.type.Education;
import com.goormthon.samsamejo.dto.response.security.JwtDto;
import com.goormthon.samsamejo.exception.RestException;
import com.goormthon.samsamejo.repository.RefreshTokenRepository;
import com.goormthon.samsamejo.repository.UsersRepository;
import com.goormthon.samsamejo.security.info.UserClaims;
import com.goormthon.samsamejo.util.FileUtil;
import com.goormthon.samsamejo.util.JwtUtil;
import com.goormthon.samsamejo.util.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.goormthon.samsamejo.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsersRepository usersRepository;
    private final JwtUtil jwtUtil;
    private final S3Util s3Util;

    @Value("${cloud.aws.s3.save-path.resume}")
    private String resumeSavePath;
    @Value("${cloud.aws.s3.save-path.portfolio}")
    private String portfolioSavePath;

    public void createUserTokens(String refreshToken, Long userId) {
        refreshTokenRepository.save(RefreshToken.builder().userId(userId).refreshToken(refreshToken).build());
    }

    public void deleteUserTokens(Long userId) {
        refreshTokenRepository.deleteById(userId);
    }

    @Transactional
    public Users saveOAuthUser(String oauthId, EProvider oauthProvider) {
        return usersRepository.save(Users.builder()
                .oauthId(oauthId)
                .role(ERole.USER)
                .oauthProvider(oauthProvider)
                .build()
        );
    }

    public JwtDto reissue(String refreshToken) {
        UserClaims userClaims = jwtUtil.getUserClaimsFromToken(refreshToken);

        Users user = usersRepository.findById(userClaims.id()).orElseThrow(() -> new RestException(TOKEN_INVALID));
        JwtDto jwtDto = jwtUtil.generateTokens(user.getId(), user.getRole());

        refreshTokenRepository.save(RefreshToken.builder().userId(user.getId()).refreshToken(jwtDto.refreshToken()).build());
        return jwtDto;
    }

    @Transactional
    public void createUserInfo(
            Long userId,
            String name,
            String email,
            String phoneNumber,
            Education education,
            String major,
            List<String> interestFields,
            List<String> skills,
            MultipartFile resume,
            MultipartFile portfolio
    ) throws IOException {
        Users user = usersRepository.findById(userId).orElseThrow(() -> new RestException(NOT_FOUND_USER));
        user.updateUserInfo(name, email, phoneNumber, education, major, interestFields, skills);
        if (Optional.ofNullable(resume).isPresent() && !resume.isEmpty()) {
            user.updateResume(resume.getOriginalFilename(), FileUtil.getRandomFileName(resume.getOriginalFilename()));
            s3Util.uploadFile(resume.getBytes(), resumeSavePath, user.getResumeSaveName(), resume.getContentType());
        }

        if (Optional.ofNullable(portfolio).isPresent() && !portfolio.isEmpty()) {
            user.updateResume(portfolio.getOriginalFilename(), FileUtil.getRandomFileName(portfolio.getOriginalFilename()));
            s3Util.uploadFile(portfolio.getBytes(), portfolioSavePath, user.getPortfolioSaveName(), portfolio.getContentType());
        }

        user.register();
    }

    @Transactional
    public void updateUserInfo(
            Long userId,
            String name,
            String email,
            String phoneNumber,
            Education education,
            String major,
            List<String> interestFields,
            List<String> skills) {
        Users user = usersRepository.findById(userId).orElseThrow(() -> new RestException(NOT_FOUND_USER));
        user.updateUserInfo(name, email, phoneNumber, education, major, interestFields, skills);
    }

    @Transactional
    public void updateResume(Long userId, MultipartFile resume) throws IOException {
        Users user = usersRepository.findById(userId).orElseThrow(() -> new RestException(NOT_FOUND_USER));
        if (Optional.ofNullable(resume).isPresent() && !resume.isEmpty()) {
            user.updateResume(resume.getOriginalFilename(), FileUtil.getRandomFileName(resume.getOriginalFilename()));
            s3Util.uploadFile(resume.getBytes(), resumeSavePath, user.getResumeSaveName(), resume.getContentType());
        }
    }

    @Transactional
    public void updatePortfolio(Long userId, MultipartFile portfolio) throws IOException {
        Users user = usersRepository.findById(userId).orElseThrow(() -> new RestException(NOT_FOUND_USER));
        if (Optional.ofNullable(portfolio).isPresent() && !portfolio.isEmpty()) {
            user.updateResume(portfolio.getOriginalFilename(), FileUtil.getRandomFileName(portfolio.getOriginalFilename()));
            s3Util.uploadFile(portfolio.getBytes(), portfolioSavePath, user.getPortfolioSaveName(), portfolio.getContentType());
        }
    }
}
