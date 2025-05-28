package signer;

import jakarta.validation.constraints.NotNull;

import java.nio.charset.StandardCharsets;
import java.security.*;

public final class Signer {

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String SIGNING_ALGORITHM = "SHA256withRSA";

    public static byte[] generateSignature(
        @NotNull String data,
        @NotNull PrivateKey privateKey
    ) throws NoSuchAlgorithmException,
        InvalidKeyException,
        SignatureException
    {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] hashedData = digest.digest(data.getBytes(StandardCharsets.UTF_8));

        Signature signature = Signature.getInstance(SIGNING_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(hashedData);
        return signature.sign();
    }

    public static boolean verifySignature(
        @NotNull String data,
        byte[] signedHash,
        @NotNull PublicKey publicKey
    ) throws NoSuchAlgorithmException,
        InvalidKeyException,
        SignatureException
    {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] hashedData = digest.digest(data.getBytes(StandardCharsets.UTF_8));

        Signature signature = Signature.getInstance(SIGNING_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(hashedData);
        return signature.verify(signedHash);
    }

}
