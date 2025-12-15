package com.example.ui;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGIT = "0123456789";

    private static final String ALL = UPPER + LOWER + DIGIT;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {}

    /**
     * Hash a raw password using BCrypt with strength 12.
     */
    public static String hash(String raw) {
        return BCrypt.hashpw(raw, BCrypt.gensalt(12));
    }

    /**
     * Verify a raw password against a stored BCrypt hash.
     */
    public static boolean verify(String raw, String hash) {
        return hash != null && BCrypt.checkpw(raw, hash);
    }

    /**
     * Generate a strong password that satisfies the policy:
     * - at least 8 characters
     * - at least one uppercase, one lowercase, one digit.
     *
     * @param length desired length (minimum 8 will be enforced)
     * @return generated password as plain text (you should hash it before storing)
     */
    public static String generateStrongPassword(int length) {
        int len = Math.max(length, 8);

        List<Character> chars = new ArrayList<>();

        // Ensure at least one of each category
        chars.add(randomChar(UPPER));
        chars.add(randomChar(LOWER));
        chars.add(randomChar(DIGIT));

        // Fill the rest with random characters from all pools
        for (int i = chars.size(); i < len; i++) {
            chars.add(randomChar(ALL));
        }

        // Shuffle to avoid predictable positions
        Collections.shuffle(chars, RANDOM);

        StringBuilder sb = new StringBuilder(len);
        for (char c : chars) {
            sb.append(c);
        }
        return sb.toString();
    }

    private static char randomChar(String pool) {
        int idx = RANDOM.nextInt(pool.length());
        return pool.charAt(idx);
    }
}
