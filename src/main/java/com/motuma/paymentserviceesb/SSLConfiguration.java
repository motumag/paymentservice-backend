package com.motuma.paymentserviceesb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Configuration
public class SSLConfiguration {
    @Bean
    public SSLContext disableCertificateVerification() {
        try {
            // Create a trust manager that trusts all certificates
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };
            // Get the SSL context
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            // Set the custom SSL context to disable certificate verification
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            // Disable hostname verification (optional)
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            return sc; // Return the SSLContext bean
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
            return null; // Handle exception appropriately, returning null for simplicity
        }
    }
}
