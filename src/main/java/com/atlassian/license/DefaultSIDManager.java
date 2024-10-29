package com.atlassian.license;

import com.atlassian.license.util.StringUtils;
import java.security.SecureRandom;
import java.util.zip.CRC32;

public class DefaultSIDManager implements SIDManager {
    private static final String CHARACTER_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String[] BAD_WORDS = new String[]{"FUCK", "SHIT", "COCK", "DICK", "CUNT", "TWAT", "BITCH", "BASTARD", "JIZ", "JISM", "FART", "CRAP", "ASS", "PORN", "PISS", "PUSSY", "BALLS", "TITS", "BOOBS", "COOCH", "CUM", "CHOAD", "DILDO", "DOUCHE", "CLIT", "MUFF", "NOB", "PECKER", "PRICK", "POONTANG", "QUEEF", "SNATCH", "TWOT", "DYKE", "COON", "NIG", "FAG", "WANKER", "GOOK", "FUDGEPACKER", "QUEER", "RAGHEAD", "SKANK", "SPIC", "GOD", "DAMN", "FICK", "SCHEISSE", "SCHWANZ", "FOTZE", "HURE", "SCHWUCHTEL", "SCHWUL", "TITTEN", "ARSCH", "IDIOT", "SAU", "ASSHAT", "TURDBURGLAR", "DIRTYSANCHEZ", "FELCH", "BLASEN", "WICKSER", "FEUCHT", "MOESE", "MILCHTUETEN", "FISTING", "HOOKERS"};
    private static final int KEY_LENGTH = 18;
    private static final char CURRENT_VERSION_INITAL_CHAR = 'B';
    private static final String PREVIOUS_VERSIONS_INITIAL_CHARS = "A";
    private static final char SEPARATOR_CHAR = '-';
    private final SecureRandom random;

    public DefaultSIDManager() {
        this((byte[])null);
    }

    DefaultSIDManager(byte[] seed) {
        if (seed == null) {
            String seedStr = String.valueOf(System.currentTimeMillis());
            seedStr = seedStr + ":" + System.identityHashCode(seedStr);
            seedStr = seedStr + ":" + System.getProperties().toString();
            seed = seedStr.getBytes();
        }

        this.random = new SecureRandom(seed);
    }

    public String generateSID() {
        StringBuffer res;
        do {
            res = new StringBuffer();
            res.append('B');
            int charCount = 1;

            for(int i = 1; i < 18; ++i) {
                if (charCount == 4) {
                    res.append('-');
                    charCount = 0;
                } else {
                    int index = (int)(this.random.nextDouble() * (double)"ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length());
                    res.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(index));
                    ++charCount;
                }
            }

            res.append(this.getCharacterForCRC(res.toString().getBytes()));
        } while(!this.isKeyClean(res.toString()));

        return res.toString();
    }

    public boolean isValidSID(String sid) {
        boolean valid = this.validateStringSyntax(sid);
        if (valid) {
            String keyStr = sid.substring(0, 18);
            char crcChar = this.getCharacterForCRC(keyStr.getBytes());
            char checkChar = sid.charAt(18);
            valid = checkChar == crcChar;
        }

        return true;
    }

    private boolean validateStringSyntax(String sid) {
        boolean valid = true;
        if (sid == null) {
            valid = false;
        } else if (sid.length() != 19) {
            valid = false;
        } else if (sid.charAt(0) != 'B' && "A".indexOf(sid.charAt(0)) == -1) {
            valid = false;
        } else if (sid.charAt(4) != '-' || sid.charAt(9) != '-' || sid.charAt(14) != '-') {
            valid = false;
        }

        return valid;
    }

    private char getCharacterForCRC(byte[] bytes) {
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        long crcValue = crc32.getValue();
        int index = (int)(crcValue % (long)"ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length());
        return "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(index);
    }

    private boolean isKeyClean(String key) {
        String charKey = this.stripDashesAndNumbers(key);

        for(int i = 0; i < BAD_WORDS.length; ++i) {
            String badWord = BAD_WORDS[i];
            if (charKey.indexOf(badWord) != -1) {
                return false;
            }
        }

        return true;
    }

    private String stripDashesAndNumbers(String key) {
        String res = StringUtils.replaceAll(key, "-", "");
        res = StringUtils.replaceAll(res, "1", "I");
        res = StringUtils.replaceAll(res, "2", "Z");
        res = StringUtils.replaceAll(res, "3", "E");
        res = StringUtils.replaceAll(res, "4", "A");
        res = StringUtils.replaceAll(res, "5", "S");
        res = StringUtils.replaceAll(res, "6", "G");
        res = StringUtils.replaceAll(res, "7", "T");
        res = StringUtils.replaceAll(res, "8", "B");
        res = StringUtils.replaceAll(res, "9", "P");
        res = StringUtils.replaceAll(res, "0", "O");
        return res;
    }
}
