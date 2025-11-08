package com.nccgroup.loggerplusplus.reflection.transformer;

import com.coreyd97.BurpExtenderUtilities.Preferences;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64DecodeTransformer extends ParameterValueTransformer {

    public Base64DecodeTransformer(Preferences preferences){
        super(preferences, "Base64 Decode");
    }

    @Override
    public String transform(String string) throws UnsupportedEncodingException {
        // Security note: Generous limit for pentesting scenarios
        // Pentesters often encounter large legitimate payloads (embedded images, files, etc.)
        final int MAX_DECODE_SIZE_MB = 100;  // 100MB limit to prevent extreme OOM attacks
        final int MAX_DECODE_SIZE_BYTES = MAX_DECODE_SIZE_MB * 1024 * 1024;

        // Check input size (base64 encoding inflates by ~33%, so input limit is ~133MB for 100MB output)
        if (string.length() > MAX_DECODE_SIZE_BYTES * 4 / 3) {
            // Log warning but don't fail - let user decide if they want to process it
            System.err.println(String.format("Warning: Base64 input very large (%d bytes, %.2f MB). Maximum recommended: %d bytes (%d MB)",
                    string.length(), string.length() / (1024.0 * 1024.0), MAX_DECODE_SIZE_BYTES * 4 / 3, MAX_DECODE_SIZE_MB));
        }

        byte[] decoded = Base64.getDecoder().decode(string.getBytes());

        // Check output size - warn if large but still allow for testing purposes
        if (decoded.length > MAX_DECODE_SIZE_BYTES) {
            System.err.println(String.format("Warning: Base64 decoded output very large (%d bytes, %.2f MB). This may cause memory issues.",
                    decoded.length, decoded.length / (1024.0 * 1024.0)));
        }

        return new String(decoded);
    }
}
