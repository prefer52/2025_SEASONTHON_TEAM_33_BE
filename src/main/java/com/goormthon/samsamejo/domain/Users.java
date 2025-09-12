package com.goormthon.samsamejo.domain;

import com.goormthon.samsamejo.domain.type.EProvider;
import com.goormthon.samsamejo.domain.type.ERole;
import com.goormthon.samsamejo.domain.type.Education;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private EProvider oauthProvider;

    private String oauthId;

    private String password;

    private String name;

    private String email;

    @Enumerated(value = EnumType.STRING)
    private ERole role;

    @Column(nullable = false)
    @ColumnDefault("0")
    private boolean isRegistered;

    private String contact;

    @Enumerated(value = EnumType.STRING)
    private Education education;

    private String major;

    private String interests;

    private String skills;

    private String resumeOriginName;

    private String resumeSaveName;

    private String portfolioOriginName;

    private String portfolioSaveName;


    @Builder
    private Users(
            EProvider oauthProvider,
            String oauthId,
            String name,
            String email,
            ERole role,
            boolean isRegistered,
            String contact,
            Education education,
            String major,
            String interests,
            String skills,
            String resumeOriginName,
            String resumeSaveName,
            String portfolioOriginName,
            String portfolioSaveName
    ) {
        this.oauthProvider = oauthProvider;
        this.oauthId = oauthId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.isRegistered = isRegistered;
        this.contact = contact;
        this.education = education;
        this.major = major;
        this.interests = interests;
        this.skills = skills;
        this.resumeOriginName = resumeOriginName;
        this.resumeSaveName = resumeSaveName;
        this.portfolioOriginName = portfolioOriginName;
        this.portfolioSaveName = portfolioSaveName;
    }

    public void updateUserInfo(
            String name,
            String email,
            String phoneNumber,
            Education education,
            String major,
            List<String> interestFields,
            List<String> skills
    ) {
        this.name = name;
        this.email = email;
        this.contact = phoneNumber;
        this.education = education;
        this.major = major;
        this.interests = String.join(",", interestFields);
        this.skills = String.join(",", skills);
    }

    public void updateResume(String resumeOriginName, String resumeSaveName) {
        this.resumeOriginName = resumeOriginName;
        this.resumeSaveName = resumeSaveName;
    }

    public void updatePortfolio(String portfolioOriginName, String portfolioSaveName) {
        this.portfolioOriginName = portfolioOriginName;
        this.portfolioSaveName = portfolioSaveName;
    }

    public void register() {
        this.isRegistered = true;
    }
}
