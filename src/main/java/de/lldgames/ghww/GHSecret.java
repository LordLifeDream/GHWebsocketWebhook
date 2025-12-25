package de.lldgames.ghww;

import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.charset.StandardCharsets;

import static de.lldgames.ghww.Authentication.writeFile;

public class GHSecret {
    //class that handles GitHub secret validation
    private static JSONObject secrets;
    private static final String FILE_LOC = "./secrets.json";

    public static boolean validate(String signature, String repo, String body){
        if(!secrets.has(repo)) return  true; //if no secret, trust it all.
        String secret = secrets.getString(repo);
        try {
            String expectedSig = "sha256=" + hmacSha256(secret, body);
            boolean b = constantTimeEquals(expectedSig, signature);
            if(!b) System.out.println("validation failed due to incorrect sig.");
            //System.out.println("sig check " + b);
            return b;

        }catch (Exception e){
            System.out.println("validation failed due to exception!");
            e.printStackTrace();
            return false;
        }
    }


    public static String hmacSha256(String secret, String payload) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec =
                new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);

        byte[] rawHmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(rawHmac);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    public static void load(){
        File f = new File(FILE_LOC);
        if(!f.exists()) {
            writeFile(f, "{}");
        }
        secrets = new JSONObject(Authentication.readFile(f));
    }
}
