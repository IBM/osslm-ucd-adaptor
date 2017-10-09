package com.accantosystems.stratoss.driver.ucd.config;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

public class SelfSignedCertificateClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

	private static final Logger logger = LoggerFactory.getLogger( SelfSignedCertificateClientHttpRequestFactory.class );

	@Override
    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
        if (connection instanceof HttpsURLConnection) {
        	// Create a HostnameVerifier that allows all host names
            ((HttpsURLConnection) connection).setHostnameVerifier( (hostname, session) -> true );
            
            // Create a socket factory that allows all certificates
            try {
				X509TrustManager tm = new X509TrustManager() {
				    @Override public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException { /* Empty */ }
				    @Override public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException { /* Empty */ }

				    @Override
				    public X509Certificate[] getAcceptedIssuers() {
				        return new X509Certificate[0];
				    }
				};
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(null, new TrustManager[] { tm }, null);
				SSLContext.setDefault(ctx);
				
				((HttpsURLConnection) connection).setSSLSocketFactory(ctx.getSocketFactory());
			} catch (KeyManagementException | NoSuchAlgorithmException e) {
				logger.error("Exception caught while configuring self-signing HTTP request factory", e);
			}
        }
        super.prepareConnection(connection, httpMethod);
    }

}