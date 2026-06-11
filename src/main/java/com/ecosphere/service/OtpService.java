package com.ecosphere.service;

import com.ecosphere.dto.VerifyOtpRequest;
import com.ecosphere.entity.OtpVerification;
import com.ecosphere.entity.Role;
import com.ecosphere.entity.User;
import com.ecosphere.repository.UserRepository;
import com.ecosphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ecosphere.repository.OtpRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

//    public OtpService(OtpRepository otpRepository,
//                      EmailService emailService) {
//        this.otpRepository = otpRepository;
//        this.emailService = emailService;
//    }

    public String generateOtp() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    public void sendOtp(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException(
                    "Email already registered"
            );
        }
        otpRepository.deleteByEmail(email);

        // 1. generate otp
        String otp = generateOtp();

        // 2. save in DB
        OtpVerification record = new OtpVerification();
        record.setEmail(email);
        record.setOtp(otp);
        record.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        record.setUsed(false);

        otpRepository.save(record);


        // 3. send email
        emailService.sendOtp(email, otp);

    }
    public void verifyOtpAndRegister(VerifyOtpRequest request) {
        OtpVerification record = otpRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (record.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (record.isUsed()) {
            throw new RuntimeException("OTP already used");
        }

        // 4. validate OTP
        if (!record.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        // 5. mark OTP used
        record.setUsed(true);
        otpRepository.save(record);

        // 6. create user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );
        user.setVerified(true);
        user.setRole(Role.CITIZEN);

        userRepository.save(user);
    }
    public void verifyOtp(VerifyOtpRequest request) {

        OtpVerification record = otpRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (record.isUsed()) {
            throw new RuntimeException("OTP already used");
        }

        if (record.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (!record.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        record.setUsed(true);
        otpRepository.save(record);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVerified(true);
        userRepository.save(user);
//        if (!user.isVerified()) {
//            throw new RuntimeException("Please verify OTP first");
//        }
    }
}