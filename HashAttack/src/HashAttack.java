import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;

public class HashAttack {
    java.security.MessageDigest digest;
    private static final java.util.Random r = new java.util.Random();

    HashAttack() throws Exception {
        digest = java.security.MessageDigest.getInstance("SHA-1");
    }

    BitSet sha1(byte[] input, int bit_size) {
        digest.reset();
        digest.update(input);
        BitSet hash = BitSet.valueOf(Arrays.copyOfRange(digest.digest(), 0, bit_size/8 + 1));
        if(bit_size <= hash.length()) {
            hash.clear(bit_size, hash.length());
        }
        return hash;
    }

    byte[] randomBytes(int length) {
        byte[] random_bytes = new byte[length];
        r.nextBytes(random_bytes);
        return random_bytes;
    }

    public static String bytesToHexString(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }


    /**
     * Brute force finds a collision with a given hash
     * Expected computation: 2^bit_size
     * @param bit_size: the size of the hash
     */
    long preImageAttack(int bit_size) throws Exception {
        long count = 0;
        Set<BitSet> messages = new HashSet<>();
        byte[] pre_image = randomBytes(bit_size);
        BitSet hash0 = sha1(pre_image, bit_size);
        BitSet hash1;

        do {
            count++;
            do {
                pre_image = randomBytes(bit_size);
            } while(!messages.add(BitSet.valueOf(pre_image)));
            hash1 = sha1(randomBytes(bit_size), bit_size);
        } while(!hash0.equals(hash1));
        //System.out.printf("Collision of given hash %s found after %d iterations\n",bytesToHexString(hash0), count);
        return count;
    }

    /**
     * Brute force finds a collision of any two messages to the same hash
     * Expected computation: 2^(bit_size/2)
     * @param bit_size: the size of the hash
     * @return
     */
    long collisionAttack(int bit_size) {
        // Count how many times the hash function is called
        long count = 0;
        // Sets for storing the guessed pre images and hashes.
        Set<BitSet> hashes = new HashSet<>();
        Set<BitSet> messages = new HashSet<>();
        BitSet hash0; // The current hash
        // Find a collision
        do {
            count++;
            byte[] rand_bytes; // The current random message
            // Get a unique random message
            do {
                rand_bytes = randomBytes(bit_size);
            } while(!messages.add(BitSet.valueOf(rand_bytes)));

            // Find the hash of the message
            hash0 = sha1(rand_bytes, bit_size);

        // Loop until a repeat hash is found
        } while(hashes.add(hash0));
        //System.out.printf("Collision of hash %s found after %d iterations\n", bytesToHexString(hash0), count);
        return count;
    }
}