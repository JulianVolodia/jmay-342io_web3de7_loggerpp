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
        // Fixed: Add size limit to prevent Base64 decode bomb attacks
        final int MAX_DECODE_SIZE_MB = 10;
        final int MAX_DECODE_SIZE_BYTES = MAX_DECODE_SIZE_MB * 1024 * 1024;

        // Check input size (base64 encoding inflates by ~33%, so input limit is ~13MB for 10MB output)
        if (string.length() > MAX_DECODE_SIZE_BYTES * 4 / 3) {
            throw new UnsupportedEncodingException(
                String.format("Base64 input too large (%d bytes). Maximum allowed: %d bytes",
                    string.length(), MAX_DECODE_SIZE_BYTES * 4 / 3)
            );
        }

        byte[] decoded = Base64.getDecoder().decode(string.getBytes());

        // Check output size to prevent memory exhaustion
        if (decoded.length > MAX_DECODE_SIZE_BYTES) {
            throw new UnsupportedEncodingException(
                String.format("Base64 decoded output too large (%d bytes). Maximum allowed: %d bytes (%d MB)",
                    decoded.length, MAX_DECODE_SIZE_BYTES, MAX_DECODE_SIZE_MB)
            );
        }

        return new String(decoded);
    }
}
