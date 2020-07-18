package com.mee.manage.vo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import lombok.Data;

@Data
public class OAuthVo {

    public static String SIGN_METHOD_PLAINTEXT = "PLAINTEXT";

    public static String SIGN_METHOD_HMAC_SHA1 = "HMAC-SHA1";


    String signatureMethod;

    String consumerKey;

    String consumerSecret;

    String accessToken;

    String tokenSecret;

    String callbackUrl;

    String verifier;

    String timestamp;

    String nonce;

    String version;

    String realm;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("OAuth ");
        if (this.consumerKey != null) {
            sb.append("oauth_consumer_key=").append(this.consumerKey).append(",");
        }
        if (this.signatureMethod != null) {
            sb.append("oauth_signature_method=").append(this.signatureMethod).append(",");
        }
        if (this.timestamp != null) {
            sb.append("oauth_timestamp=").append(this.timestamp).append(",");
        }
        if (this.nonce != null) {
            sb.append("oauth_nonce=").append(this.nonce).append(",");
        }
        if (this.version != null) {
            sb.append("oauth_version=").append(this.version).append(",");
        }
        if (this.accessToken != null) {
            sb.append("oauth_token=").append(this.accessToken).append(",");
        }
        if (this.verifier != null) {
            sb.append("oauth_verifier=").append(this.verifier).append(",");
        }
        if (this.realm != null) {
            sb.append("realm=").append(this.realm).append(",");
        }
        if (this.callbackUrl != null) {
            try {
                sb.append("oauth_callback=").append(URLEncoder.encode(this.callbackUrl,"UTF-8")).append(",");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (this.consumerSecret != null) {
            try {
                String signStr = this.consumerSecret + "&";
                if(this.tokenSecret != null) {
                    signStr += this.tokenSecret;
                }
                sb.append("oauth_signature=").append(URLEncoder.encode(signStr,"UTF-8")).append(",");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return sb.substring(0, sb.length() - 1);
    }
}