package com.mee.manage.util;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.binary.Base64;

public class JWTUtil {

    public static Map<String, String> decodeJWT(String jsonWebToken)  {
        Map<String, String> result = null;

        /*
        Verifier verifier = HMACVerifier.newVerifier("key");
        JWT jwt = JWT.getDecoder().decode(jsonWebToken, verifier);
        System.out.println(JSON.toJSONString(jwt.getAllClaims()));
        */
        /*
        SecretKey key = null;
        // KEY = new SecretKeySpec("key".getBytes("UTF-8"),
        // SignatureAlgorithm.HS256.getJcaName());
        key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        // key = Keys.hmacShaKeyFor("key".getBytes());
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jsonWebToken);

        // JwsHeader header = claimsJws.getHeader();
        Claims body = claimsJws.getBody();

        result = new HashMap<>();
        for (Entry<String, Object> entry : body.entrySet()) {
            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            result.put(entry.getKey(), entry.getValue().toString());
        }

        */


        
        try {
            // Algorithm algorithm = Algorithm.HMAC256("secret");
            // JWT.require(algorithm);

            DecodedJWT jwt = JWT.decode(jsonWebToken);

            Map<String, Claim> claims = jwt.getClaims();

            System.out.println("claims: "+JSON.toJSONString(claims));

            System.out.println(jwt.getAlgorithm());
           
            String sign = jwt.getSignature();
            System.out.println("sign: "+sign);

            String keyId = jwt.getKeyId();
            System.out.println("keyId:"+keyId);

            String payLoad = jwt.getPayload();
            System.out.println("payLoad: "+payLoad);

            String subJect = jwt.getSubject();
            System.out.println("subJect:"+subJect);

            System.out.println("token: "+jwt.getToken());
            

            System.out.println("key: " + jwt.getClaim("key").asString());
            System.out.println("icID: " + jwt.getClaim("icID").asLong());


        } catch (JWTDecodeException exception){
            //Invalid token
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }

        
        return result;
    }


    public static Map<String,String> decode(String jsonWebToken) {
        String[] parts = splitToken(jsonWebToken);
        String headerJson;
        String payloadJson;
        try {
            headerJson = StringUtils.newStringUtf8(Base64.decodeBase64(parts[0]));
            payloadJson = StringUtils.newStringUtf8(Base64.decodeBase64(parts[1]));
        } catch (NullPointerException e) {
            throw new JWTDecodeException("The UTF-8 Charset isn't initialized.", e);
        }

        System.out.println("payloadJson: "+payloadJson);
        System.out.println("headerJson: "+headerJson);
        Map<String,String> result = null;
        if(payloadJson != null) {
            result = new HashMap<>();
            JSONObject jObject = JSON.parseObject(payloadJson);
            for(String key : jObject.keySet()) {
                result.put(key, jObject.getString(key));
            }
        }
        return result;
    }


        /**
     * Splits the given token on the "." chars into a String array with 3 parts.
     *
     * @param token the string to split.
     * @return the array representing the 3 parts of the token.
     * @throws JWTDecodeException if the Token doesn't have 3 parts.
     */
    static String[] splitToken(String token) throws JWTDecodeException {
        String[] parts = token.split("\\.");
        if (parts.length == 2 && token.endsWith(".")) {
            //Tokens with alg='none' have empty String as Signature.
            parts = new String[]{parts[0], parts[1], ""};
        }
        if (parts.length != 3) {
            throw new JWTDecodeException(String.format("The token was expected to have 3 parts, but got %s.", parts.length));
        }
        return parts;
    }



    public static void main(String[] args) {
        String jwToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJqd3RJc3N1ZXIiLCJhdWQiOiJqd3RTYWZlQXBpIiwiZXhwIjoiMTU5MDE2MjI3NCIsImtleSI6ImtleSIsImljSUQiOjEwMjEsInN0YXJ0IjoiMzY0MDAwMDAwMCIsInJvbGUiOiJjbGllbnQifQ.hTh4GAUs4xY7-xu3Vgluwu43kwOCtib8r3nFvbtT_OQ";
        Map<String,String> result = decode(jwToken);
        System.out.println(JSON.toJSONString(result));
    }

}