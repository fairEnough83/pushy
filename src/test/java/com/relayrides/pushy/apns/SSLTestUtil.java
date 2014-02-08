package com.relayrides.pushy.apns;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;

class SSLTestUtil {

	private static final String PROTOCOL = "TLS";
	private static final String DEFAULT_ALGORITHM = "SunX509";

	private static final String SERVER_KEYSTORE_FILE_NAME = "/pushy-test-server.jks";
	private static final String CLIENT_KEYSTORE_FILE_NAME = "/pushy-test-client.jks";
	private static final char[] KEYSTORE_PASSWORD = "pushy-test".toCharArray();

	public static SSLEngine createSSLEngineForMockServer() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {

		final InputStream keyStoreInputStream = SSLTestUtil.class.getResourceAsStream(SERVER_KEYSTORE_FILE_NAME);

		if (keyStoreInputStream == null) {
			throw new RuntimeException("Server keystore file not found.");
		}

		final KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(keyStoreInputStream, KEYSTORE_PASSWORD);

		String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");

		if (algorithm == null) {
			algorithm = DEFAULT_ALGORITHM;
		}

		final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(algorithm);
		keyManagerFactory.init(keyStore, KEYSTORE_PASSWORD);

		final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
		trustManagerFactory.init(keyStore);

		// Initialize the SSLContext to work with our key managers.
		final SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
		sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

		final SSLEngine sslEngine = sslContext.createSSLEngine();
		sslEngine.setUseClientMode(false);
		sslEngine.setWantClientAuth(true);

		return sslEngine;
	}

	public static SSLContext createSSLContextForTestClient() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {
		return SSLTestUtil.createSSLContextForTestClient(CLIENT_KEYSTORE_FILE_NAME);
	}

	public static SSLContext createSSLContextForTestClient(final String keystoreFileName) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {
		final InputStream keyStoreInputStream = SSLTestUtil.class.getResourceAsStream(keystoreFileName);

		if (keyStoreInputStream == null) {
			throw new RuntimeException("Server keystore file not found.");
		}

		final KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(keyStoreInputStream, KEYSTORE_PASSWORD);

		String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");

		if (algorithm == null) {
			algorithm = DEFAULT_ALGORITHM;
		}

		final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(algorithm);
		keyManagerFactory.init(keyStore, KEYSTORE_PASSWORD);

		final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
		trustManagerFactory.init(keyStore);

		// Initialize the SSLContext to work with our key managers.
		final SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
		sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

		return sslContext;
	}
}
