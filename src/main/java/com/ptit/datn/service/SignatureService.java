package com.ptit.datn.service;

import com.ptit.datn.service.dto.RsaKeyDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SignatureService {

    public static String createSignature(String hash, String privateKeyBase64) throws Exception {
        // Bước 1: Giải mã private key từ base64
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);

        // Bước 2: Chuyển đổi byte[] thành PrivateKey
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        // Bước 1: Khởi tạo Signature với thuật toán SHA256withRSA
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        // Bước 2: Cung cấp dữ liệu cần ký (hash) dưới dạng byte
        byte[] hashBytes = hash.getBytes("UTF-8");
        signature.update(hashBytes);

        // Bước 3: Ký dữ liệu
        byte[] signatureBytes = signature.sign();

        // Bước 4: Chuyển chữ ký sang định dạng Base64
        return Base64.getEncoder().encodeToString(signatureBytes);
    }


    public static boolean verifySignature(String data, String signature, String publicKeyBase64) {
        try {
            //Lấy Public Key từ chuỗi Base64 (lưu trong DB)
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

            //Tạo đối tượng Signature với thuật toán SHA256withRSA
            Signature rsaSignature = Signature.getInstance("SHA256withRSA");

            //Cấu hình Public Key để xác thực
            rsaSignature.initVerify(publicKey);

            //Cập nhật dữ liệu gốc
            rsaSignature.update(data.getBytes());

            //Giải mã và xác thực chữ ký
            byte[] signatureBytes = Base64.getDecoder().decode(signature);
            return rsaSignature.verify(signatureBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static RsaKeyDTO generateRsaKeyPair(){
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyPairGen.initialize(2048); // Kích thước khóa (2048 bit được khuyến nghị)
        KeyPair keyPair = keyPairGen.generateKeyPair();

        // Bước 2: Lấy Public Key và Private Key
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // Bước 3: Chuyển đổi khóa sang định dạng Base64
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        return RsaKeyDTO.builder().publicKey(publicKeyBase64).privateKey(privateKeyBase64).build();
    }

    public static Path createPrivateKeyFile(String privateKeyBase64) throws IOException {
        Path filePath = Paths.get(System.getProperty("java.io.tmpdir"), "private_key.pem");
        Files.write(filePath, privateKeyBase64.getBytes(StandardCharsets.UTF_8));
        return filePath;
    }


    public static String generateHashFromMultipartFile(MultipartFile file) {
        try {
            // Chuyển MultipartFile thành mảng byte
            byte[] fileBytes = file.getBytes();

            // Tạo giá trị hash SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(fileBytes);

            // Chuyển giá trị hash thành chuỗi
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean compareHashes(String newHash, String storedHash) {
        if (newHash == null || storedHash == null) {
            return false;
        }
        return newHash.equals(storedHash);
    }
}

