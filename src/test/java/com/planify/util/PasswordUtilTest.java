package com.planify.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {
    @Test
    void hashesAndVerifiesPassword() {
        String hash = PasswordUtil.hash("correct horse battery staple");

        assertTrue(PasswordUtil.isHashed(hash));
        assertNotEquals("correct horse battery staple", hash);
        assertTrue(PasswordUtil.verify("correct horse battery staple", hash));
    }

    @Test
    void rejectsWrongPasswordAndMalformedHash() {
        String hash = PasswordUtil.hash("my test password");

        assertFalse(PasswordUtil.verify("wrong password", hash));
        assertFalse(PasswordUtil.verify("anything", "pbkdf2$not-a-number$bad$bad"));
        assertFalse(PasswordUtil.verify("anything", null));
    }

    @Test
    void acceptsLegacyPlainTextOnlyWhenItMatches() {
        assertTrue(PasswordUtil.verify("legacy-demo", "legacy-demo"));
        assertFalse(PasswordUtil.verify("other", "legacy-demo"));
    }
}
