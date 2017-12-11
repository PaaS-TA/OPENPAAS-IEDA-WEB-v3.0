/*
Sha512Crypt.java

Created: 18 December 2007

Java Port By: James Ratcliff, falazar@arlut.utexas.edu

This class implements the new generation, scalable, SHA512-based
Unix 'crypt' algorithm developed by a group of engineers from Red
Hat, Sun, IBM, and HP for common use in the Unix and Linux
/etc/shadow files.

The Linux glibc library (starting at version 2.7) includes support
for validating passwords hashed using this algorithm.

The algorithm itself was released into the Public Domain by Ulrich
Drepper <drepper@redhat.com>.  A discussion of the rationale and
development of this algorithm is at

http://people.redhat.com/drepper/sha-crypt.html

and the specification and a sample C language implementation is at

http://people.redhat.com/drepper/SHA-crypt.txt

This Java Port is

  Copyright (c) 2008-2013 The University of Texas at Austin.

  All rights reserved.

  Redistribution and use in source and binary form are permitted
  provided that distributions retain this entire copyright notice
  and comment. Neither the name of the University nor the names of
  its contributors may be used to endorse or promote products
  derived from this software without specific prior written
  permission. THIS SOFTWARE IS PROVIDED "AS IS" AND WITHOUT ANY
  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE
  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
  PARTICULAR PURPOSE.

*/

package org.openpaas.ieda.common.api;

import java.security.MessageDigest;

/*------------------------------------------------------------------------------
                                                                        class
                                                                  Sha512Crypt

------------------------------------------------------------------------------*/

/**
 * <p>
 * This class defines a method,
 * {@link Sha512Crypt#Sha512_crypt(java.lang.String, java.lang.String, int)
 * Sha512_crypt()}, which takes a password and a salt string and generates a
 * Sha512 encrypted password entry.
 * </p>
 *
 * <p>
 * This class implements the new generation, scalable, SHA512-based Unix 'crypt'
 * algorithm developed by a group of engineers from Red Hat, Sun, IBM, and HP
 * for common use in the Unix and Linux /etc/shadow files.
 * </p>
 *
 * <p>
 * The Linux glibc library (starting at version 2.7) includes support for
 * validating passwords hashed using this algorithm.
 * </p>
 *
 * <p>
 * The algorithm itself was released into the Public Domain by Ulrich Drepper
 * &lt;drepper@redhat.com&gt;. A discussion of the rationale and development of
 * this algorithm is at
 * </p>
 *
 * <p>
 * http://people.redhat.com/drepper/sha-crypt.html
 * </p>
 *
 * <p>
 * and the specification and a sample C language implementation is at
 * </p>
 *
 * <p>
 * http://people.redhat.com/drepper/SHA-crypt.txt
 * </p>
 */

public final class Sha512Crypt {
    static private final String sha512SaltPrefix = "$6$";
    static private final String sha512RoundsPrefix = "rounds=";
    static private final int SALT_LEN_MAX = 16;
    static private final int ROUNDS_DEFAULT = 5000;
    static private final int ROUNDS_MIN = 1000;
    static private final int ROUNDS_MAX = 999999999;
    static private final String SALTCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    static private final String itoa64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : SHA-512 형식으로 암호화 인스턴스 생성
     * @title : getSHA512
     * @return : MessageDigest
    ***************************************************/
    static private MessageDigest getSHA512() {
        try {
            return MessageDigest.getInstance("SHA-512");
        } catch (java.security.NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * <p>
     * This method actually generates an Sha512 crypted password hash from a
     * plaintext password and a salt.
     * </p>
     *
     * <p>
     * The resulting string will be in the form
     * '$6$&lt;rounds=n&gt;$&lt;salt&gt;$&lt;hashed mess&gt;
     * </p>
     *
     * @param keyStr
     *            Plaintext password
     *
     * @param saltStr
     *            An encoded salt/roundes which will be consulted to determine
     *            the salt and round count, if not null
     *
     * @param roundsCount
     *            If this value is not 0, this many rounds will used to generate
     *            the hash text.
     *
     * @return The Sha512 Unix Crypt hash text for the keyStr
     */

    public static final String Sha512_crypt(String keyStr, String saltStr, int roundsCount) {
        MessageDigest ctx = getSHA512();
        MessageDigest altCtx = getSHA512();

        byte[] altResult;
        byte[] tempResult;
        byte[] pBytes = null;
        byte[] sBytes = null;
        int cnt, cnt2;
        int rounds = ROUNDS_DEFAULT; // Default number of rounds.
        StringBuilder buffer;
        boolean includeRoundCount = false;

        /* -- */

        if (saltStr != null) {
            if (saltStr.startsWith(sha512SaltPrefix)) {
                saltStr = saltStr.substring(sha512SaltPrefix.length());
            }

            if (saltStr.startsWith(sha512RoundsPrefix)) {
                String num = saltStr.substring(sha512RoundsPrefix.length(), saltStr.indexOf('$'));
                int srounds = Integer.valueOf(num).intValue();
                saltStr = saltStr.substring(saltStr.indexOf('$') + 1);
                rounds = Math.max(ROUNDS_MIN, Math.min(srounds, ROUNDS_MAX));
                includeRoundCount = true;
            }

            if (saltStr.length() > SALT_LEN_MAX) {
                saltStr = saltStr.substring(0, SALT_LEN_MAX);
            }

            // gnu libc's crypt(3) implementation allows the salt to end
            // in $ which is then ignored.

            if (saltStr.endsWith("$")) {
                saltStr = saltStr.substring(0, saltStr.length() - 1);
            } else {
                if (saltStr.indexOf("$") != -1) {
                    saltStr = saltStr.substring(0, saltStr.indexOf("$"));
                }
            }
        } else {
            java.util.Random randgen = new java.util.Random();
            StringBuilder saltBuf = new StringBuilder();

            while (saltBuf.length() < 16) {
                int index = (int) (randgen.nextFloat() * SALTCHARS.length());
                saltBuf.append(SALTCHARS.substring(index, index + 1));
            }

            saltStr = saltBuf.toString();
        }

        if (roundsCount != 0) {
            rounds = Math.max(ROUNDS_MIN, Math.min(roundsCount, ROUNDS_MAX));
        }

        byte[] key = keyStr.getBytes();
        byte[] salt = saltStr.getBytes();

        ctx.reset();
        ctx.update(key, 0, key.length);
        ctx.update(salt, 0, salt.length);

        altCtx.reset();
        altCtx.update(key, 0, key.length);
        altCtx.update(salt, 0, salt.length);
        altCtx.update(key, 0, key.length);

        altResult = altCtx.digest();

        for (cnt = key.length; cnt > 64; cnt -= 64) {
            ctx.update(altResult, 0, 64);
        }

        ctx.update(altResult, 0, cnt);

        for (cnt = key.length; cnt > 0; cnt >>= 1) {
            if ((cnt & 1) != 0) {
                ctx.update(altResult, 0, 64);
            } else {
                ctx.update(key, 0, key.length);
            }
        }

        altResult = ctx.digest();

        altCtx.reset();

        for (cnt = 0; cnt < key.length; ++cnt) {
            altCtx.update(key, 0, key.length);
        }

        tempResult = altCtx.digest();

        pBytes = new byte[key.length];

        for (cnt2 = 0, cnt = pBytes.length; cnt >= 64; cnt -= 64) {
            System.arraycopy(tempResult, 0, pBytes, cnt2, 64);
            cnt2 += 64;
        }

        System.arraycopy(tempResult, 0, pBytes, cnt2, cnt);

        altCtx.reset();

        for (cnt = 0; cnt < 16 + (altResult[0] & 0xFF); ++cnt) {
            altCtx.update(salt, 0, salt.length);
        }

        tempResult = altCtx.digest();

        sBytes = new byte[salt.length];

        for (cnt2 = 0, cnt = sBytes.length; cnt >= 64; cnt -= 64) {
            System.arraycopy(tempResult, 0, sBytes, cnt2, 64);
            cnt2 += 64;
        }

        System.arraycopy(tempResult, 0, sBytes, cnt2, cnt);

        /*
         * Repeatedly run the collected hash value through SHA512 to burn CPU
         * cycles.
         */

        for (cnt = 0; cnt < rounds; ++cnt) {
            ctx.reset();

            if ((cnt & 1) != 0) {
                ctx.update(pBytes, 0, key.length);
            } else {
                ctx.update(altResult, 0, 64);
            }

            if (cnt % 3 != 0) {
                ctx.update(sBytes, 0, salt.length);
            }

            if (cnt % 7 != 0) {
                ctx.update(pBytes, 0, key.length);
            }

            if ((cnt & 1) != 0) {
                ctx.update(altResult, 0, 64);
            } else {
                ctx.update(pBytes, 0, key.length);
            }

            altResult = ctx.digest();
        }

        buffer = new StringBuilder(sha512SaltPrefix);

        if (includeRoundCount || rounds != ROUNDS_DEFAULT) {
            buffer.append(sha512RoundsPrefix);
            buffer.append(rounds);
            buffer.append("$");
        }

        buffer.append(saltStr);
        buffer.append("$");

        buffer.append(b64_from_24bit(altResult[0], altResult[21], altResult[42], 4));
        buffer.append(b64_from_24bit(altResult[22], altResult[43], altResult[1], 4));
        buffer.append(b64_from_24bit(altResult[44], altResult[2], altResult[23], 4));
        buffer.append(b64_from_24bit(altResult[3], altResult[24], altResult[45], 4));
        buffer.append(b64_from_24bit(altResult[25], altResult[46], altResult[4], 4));
        buffer.append(b64_from_24bit(altResult[47], altResult[5], altResult[26], 4));
        buffer.append(b64_from_24bit(altResult[6], altResult[27], altResult[48], 4));
        buffer.append(b64_from_24bit(altResult[28], altResult[49], altResult[7], 4));
        buffer.append(b64_from_24bit(altResult[50], altResult[8], altResult[29], 4));
        buffer.append(b64_from_24bit(altResult[9], altResult[30], altResult[51], 4));
        buffer.append(b64_from_24bit(altResult[31], altResult[52], altResult[10], 4));
        buffer.append(b64_from_24bit(altResult[53], altResult[11], altResult[32], 4));
        buffer.append(b64_from_24bit(altResult[12], altResult[33], altResult[54], 4));
        buffer.append(b64_from_24bit(altResult[34], altResult[55], altResult[13], 4));
        buffer.append(b64_from_24bit(altResult[56], altResult[14], altResult[35], 4));
        buffer.append(b64_from_24bit(altResult[15], altResult[36], altResult[57], 4));
        buffer.append(b64_from_24bit(altResult[37], altResult[58], altResult[16], 4));
        buffer.append(b64_from_24bit(altResult[59], altResult[17], altResult[38], 4));
        buffer.append(b64_from_24bit(altResult[18], altResult[39], altResult[60], 4));
        buffer.append(b64_from_24bit(altResult[40], altResult[61], altResult[19], 4));
        buffer.append(b64_from_24bit(altResult[62], altResult[20], altResult[41], 4));
        buffer.append(b64_from_24bit((byte) 0x00, (byte) 0x00, altResult[63], 2));

        /*
         * Clear the buffer for the intermediate result so that people attaching
         * to processes or reading core dumps cannot get any information.
         */

        ctx.reset();

        return buffer.toString();
    }

    private static final String b64_from_24bit(byte B2, byte B1, byte B0, int size) {
        int v = ((((int) B2) & 0xFF) << 16) | ((((int) B1) & 0xFF) << 8) | ((int) B0 & 0xff);

        StringBuilder result = new StringBuilder();

        while (--size >= 0) {
            result.append(itoa64.charAt((int) (v & 0x3f)));
            v >>>= 6;
        }

        return result.toString();
    }

    /**
     * <p>
     * This method tests a plaintext password against a SHA512 Unix Crypt'ed
     * hash and returns true if the password matches the hash.
     * </p>
     *
     * @param plaintextPass
     *            The plaintext password text to test.
     * @param sha512CryptText
     *            The hash text we're testing against. We'll extract the salt
     *            and the round count from this String.
     */

    static public final boolean verifyPassword(String plaintextPass, String sha512CryptText) {
        if (sha512CryptText.startsWith("$6$")) {
            return sha512CryptText.equals(Sha512_crypt(plaintextPass, sha512CryptText, 0));
        } else {
            throw new RuntimeException("Bad sha512CryptText");
        }
    }

    /**
     * <p>
     * Returns true if sha512CryptText is a valid Sha512Crypt hashtext, false if
     * not.
     * </p>
     */

    public static final boolean verifyHashTextFormat(String sha512CryptText) {
        if (!sha512CryptText.startsWith(sha512SaltPrefix)) {
            return false;
        }

        sha512CryptText = sha512CryptText.substring(sha512SaltPrefix.length());

        if (sha512CryptText.startsWith(sha512RoundsPrefix)) {
            String num = sha512CryptText.substring(sha512RoundsPrefix.length(), sha512CryptText.indexOf('$'));

            try {
                int srounds = Integer.valueOf(num).intValue();
            } catch (NumberFormatException ex) {
                return false;
            }

            sha512CryptText = sha512CryptText.substring(sha512CryptText.indexOf('$') + 1);
        }

        if (sha512CryptText.indexOf('$') > (SALT_LEN_MAX + 1)) {
            return false;
        }

        sha512CryptText = sha512CryptText.substring(sha512CryptText.indexOf('$') + 1);

        for (int i = 0; i < sha512CryptText.length(); i++) {
            if (itoa64.indexOf(sha512CryptText.charAt(i)) == -1) {
                return false;
            }
        }

        return true;
    }

    /**
     * <p>
     * Validate our implementation using test data from Ulrich Drepper's C
     * implementation.
     * </p>
     */

    @SuppressWarnings("unused")
    private static void selfTest() {
        String msgs[] = { "$6$saltstring", "Hello world!",
                "$6$saltstring$svn8UoSVapNtMuq1ukKS4tPQd8iKwSMHWjl/O817G3uBnIFNjnQJuesI68u4OTLiBFdcbYEdFCoEOfaS35inz1",
                "$6$xxxxxxxx", "geheim",
                "$6$xxxxxxxx$wuSdyeOvQXjj/nNoWnjjo.6OxUWrQFRIj019kh1cDpun6l6cpr3ywSrBprYRYZXcm4Kv9lboCEFI3GzBkdNAz/",
                "$6$rounds=10000$saltstringsaltstring", "Hello world!",
                "$6$rounds=10000$saltstringsaltst$OW1/O6BYHV6BcXZu8QVeXbDWra3Oeqh0sbHbbMCVNSnCM/UrjmM0Dp8vOuZeHBy/YTBmSK6H9qs/y3RnOaw5v.",
                "$6$rounds=5000$toolongsaltstring", "This is just a test",
                "$6$rounds=5000$toolongsaltstrin$lQ8jolhgVRVhY4b5pZKaysCLi0QBxGoNeKQzQ3glMhwllF7oGDZxUhx1yxdYcz/e1JSbq3y6JMxxl8audkUEm0",
                "$6$rounds=1400$anotherlongsaltstring",
                "a very much longer text to encrypt.  This one even stretches over morethan one line.",
                "$6$rounds=1400$anotherlongsalts$POfYwTEok97VWcjxIiSOjiykti.o/pQs.wPvMxQ6Fm7I6IoYN3CmLs66x9t0oSwbtEW7o7UmJEiDwGqd8p4ur1",
                "$6$rounds=77777$short", "we have a short salt string but not a short password",
                "$6$rounds=77777$short$WuQyW2YR.hBNpjjRhpYD/ifIw05xdfeEyQoMxIXbkvr0gge1a1x3yRULJ5CCaUeOxFmtlcGZelFl5CxtgfiAc0",
                "$6$rounds=123456$asaltof16chars..", "a short string",
                "$6$rounds=123456$asaltof16chars..$BtCwjqMJGx5hrJhZywWvt0RLE8uZ4oPwcelCjmw2kSYu.Ec6ycULevoBK25fs2xXgMNrCzIMVcgEJAstJeonj1",
                "$6$rounds=10$roundstoolow", "the minimum number is still observed",
                "$6$rounds=1000$roundstoolow$kUMsbe306n21p9R.FRkW3IGn.S9NPN0x50YhH1xhLsPuWGsUSklZt58jaTfF4ZEQpyUNGc0dqbpBYYBaHHrsX.", };

        for (int t = 0; t < (msgs.length / 3); t++) {
            String saltPrefix = msgs[t * 3];
            String plainText = msgs[t * 3 + 1];
            String cryptText = msgs[t * 3 + 2];

            String result = Sha512_crypt(plainText, cryptText, 0);

            if (result.equals(cryptText)) {
//                System.out.println("Passed Crypt well");
            } else {
//                System.out.println("Failed Crypt Badly");
            }

            if (verifyPassword(plainText, cryptText)) {
//                System.out.println("Passed verifyPassword well");
            } else {
//                System.out.println("Failed verifyPassword Badly");
            }
        }
    }

    /**
     * Test rig
     */

}
