package org.myapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.common.util.Base64Utility;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.rs.security.oauth2.client.OAuthClientUtils;
import org.apache.cxf.rs.security.oauth2.client.OAuthClientUtils.Consumer;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.grants.refresh.RefreshTokenGrant;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;

public class MyAppClient {

	private WebClient accessTokenService = WebClient
			.create("http://localhost:8080/cxfOAuth/services/oauth/token");;
	private String authorizationServiceURI = "http://localhost:8080/cxfOAuth/services/oauth/token";
	private Consumer consumer = new Consumer("ass", "lakbjs");

	// inject properties, register the client application...

	public URI getAuthorizationServiceURI(Person request, URI redirectUri,
	/* state */String reservationRequestKey) {
		String scope = OAuthConstants.AUTHORIZATION_CODE_GRANT;
		return OAuthClientUtils.getAuthorizationURI(authorizationServiceURI,
				consumer.getKey(), redirectUri.toString(),
				reservationRequestKey, scope);
	}

	public ClientAccessToken getAccessToken(AuthorizationCodeGrant codeGrant) {
		try {
			return OAuthClientUtils.getAccessToken(accessTokenService,
					consumer, codeGrant);
		} catch (OAuthServiceException ex) {
			return null;
		}
	}

	public String createAuthorizationHeader(ClientAccessToken token) {
		return OAuthClientUtils.createAuthorizationHeader(token);
	}

	public static void main(String[] args) throws Exception {

		// the pseudo-code for getting the access token

		String encode = Base64Utility.encode("user:pass".getBytes());
		String codeGrantToken = callAuthorizationService(encode);
		AuthorizationCodeGrant codeGrant = new AuthorizationCodeGrant(
				codeGrantToken,
				new URI(
						"http://localhost:8080/services/reservations/reserve/complete"));
		callwsAuthenticated(codeGrant, encode);
		callwsWithoutAuthenticated(encode);
	}

	private static void callwsAuthenticated(AuthorizationCodeGrant codeGrant,
			String encode) throws IOException {
		System.out.println("Start test with authentication");
		WebClient oauthServer = WebClient
				.create("http://localhost:8080/cxfOAuth/services/oauth/token?client_id=user");
		oauthServer.header(HttpHeaders.AUTHORIZATION, "Basic " + encode);

		ClientAccessToken accessToken = OAuthClientUtils.getAccessToken(
				oauthServer, codeGrant);

		WebClient endUserResourceClient = WebClient
				.create("http://localhost:8080/cxfOAuth/services/myapp/");

		endUserResourceClient.header(HttpHeaders.AUTHORIZATION,
				OAuthClientUtils.createAuthorizationHeader(accessToken));
		try {
			Response response = endUserResourceClient.get();
			System.out.println("Response code: " + response.getStatus());
			System.out.println("Data: "
					+ IOUtils.toString((InputStream) response.getEntity()));
		} catch (NotAuthorizedException ex) {
			String refreshToken = accessToken.getRefreshToken();
			if (refreshToken != null) {
				// retry once

				// refresh the token
				accessToken = OAuthClientUtils.getAccessToken(oauthServer,
						new RefreshTokenGrant(refreshToken));

				// reset Authorization header
				endUserResourceClient
						.replaceHeader(HttpHeaders.AUTHORIZATION,
								OAuthClientUtils
										.createAuthorizationHeader(accessToken));

				// try to access the end user resource again
				Response response = endUserResourceClient.get();
				System.out.println("Response code: " + response.getStatus());
				System.out.println("Data: "
						+ IOUtils.toString((InputStream) response.getEntity()));
			} else {
				throw ex;
			}
		}
		System.out.println("End test with authentication");
	}

	private static void callwsWithoutAuthenticated(String encode)
			throws IOException {
		System.out.println("Start test without authentication");

		WebClient endUserResourceClient = WebClient
				.create("http://localhost:8080/cxfOAuth/services/myapp/");

		endUserResourceClient.header(HttpHeaders.AUTHORIZATION, "Basic "
				+ encode);

		Response response = endUserResourceClient.get();
		System.out.println("Response code: " + response.getStatus());
		System.out.println("Data: "
				+ IOUtils.toString((InputStream) response.getEntity()));
		System.out.println("End test without authentication");
	}

	private static String callAuthorizationService(String encode)
			throws IOException {
		JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
		bean.setAddress("https://localhost:8443/cxfOAuth/services/oauth/authorize?client_id=user&scope=updateCalendar-7&response_type=code");

		SpringBusFactory bf = new SpringBusFactory();
		URL busFile = new URL(
				"file://\\java\\i2S-devenv\\workspaces\\workspaceTests\\cxfOAuth\\src\\main\\resources\\wsClient.xml");
		Bus springBus = bf.createBus(busFile.toString());
		bean.setBus(springBus);

		// Map<String, Object> properties = new HashMap<String, Object>();
		// properties.put("ws-security.signature.username", "alice");
		// properties.put("ws-security.signature.properties",
		// CRYPTO_RESOURCE_PROPERTIES);
		// bean.setProperties(properties);

		WebClient wc = bean.createWebClient();
		wc.type(MediaType.APPLICATION_FORM_URLENCODED).accept(
				MediaType.APPLICATION_JSON);
		wc.header(HttpHeaders.AUTHORIZATION, "Basic " + encode);
		// wc.header(HttpHeaders.WWW_AUTHENTICATE, "user#pass");

		Response response = wc.get();
		String code = response.getLocation().getQuery().split("=")[1];
		System.out.println(code);
		System.out.println(response.getStatus());
		return code;
	}
}
