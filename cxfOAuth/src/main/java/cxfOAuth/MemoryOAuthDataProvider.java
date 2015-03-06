/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package cxfOAuth;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.rs.security.oauth2.common.AccessTokenRegistration;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeDataProvider;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeRegistration;
import org.apache.cxf.rs.security.oauth2.grants.code.ServerAuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.tokens.bearer.BearerAccessToken;

public class MemoryOAuthDataProvider implements AuthorizationCodeDataProvider {

	private Client client = null;

	private ServerAuthorizationCodeGrant grant;

	private ServerAccessToken accessToken = null;

	public static final String CALLBACK = "http://www.example.com/callback";
	public static final String APPLICATION_NAME = "Test Oauth 1.0 application";
	public static final String CLIENT_SECRET = "pass";
	public static final String[] SIGN_METHOD = { "HMAC-SHA1", "PLAINTEXT" };

	public Client getClient(String clientId) throws OAuthServiceException {
		client = new Client(clientId, CLIENT_SECRET, true, APPLICATION_NAME,
				CALLBACK);
		client.getRedirectUris().add(
				"http://localhost:8080/services/reservations/reserve/complete");
		return client;
	}

	public ServerAccessToken createAccessToken(
			AccessTokenRegistration accessToken1) throws OAuthServiceException {
		throw new RuntimeException("createAccessToken");
	}

	public ServerAccessToken getAccessToken(String accessToken)
			throws OAuthServiceException {
		if (this.accessToken.getTokenKey().equals(accessToken)) {
			return this.accessToken;
		}
		throw new RuntimeException("Not found");
	}

	public ServerAccessToken getPreauthorizedToken(Client client,
			List<String> requestedScopes, UserSubject subject, String grantType)
			throws OAuthServiceException {
		accessToken = new BearerAccessToken(client, 244L);
		accessToken.setSubject(subject);
		accessToken.setGrantType(grantType);
		return accessToken;
	}

	public ServerAccessToken refreshAccessToken(Client client,
			String refreshToken, List<String> requestedScopes)
			throws OAuthServiceException {
		// TODO Auto-generated method stub
		throw new RuntimeException("refreshAccessToken");
	}

	public void removeAccessToken(ServerAccessToken accessToken)
			throws OAuthServiceException {
		// TODO Auto-generated method stub
		throw new RuntimeException("removeAccessToken");
	}

	public void revokeToken(Client client, String token, String tokenTypeHint)
			throws OAuthServiceException {
		// TODO Auto-generated method stub
		throw new RuntimeException("revokeToken");
	}

	public List<OAuthPermission> convertScopeToPermissions(Client client,
			List<String> requestedScope) {
		// TODO Auto-generated method stub
		OAuthPermission a = new OAuthPermission();
		a.getHttpVerbs().add("get");
		a.setPermission("a");
		a.getUris()
				.add("http://localhost:8080/services/reservations/reserve/complete1");
		ArrayList<OAuthPermission> arrayList = new ArrayList<OAuthPermission>();
		arrayList.add(a);
		return arrayList;
	}

	public ServerAuthorizationCodeGrant createCodeGrant(
			AuthorizationCodeRegistration reg) throws OAuthServiceException {
		grant = new ServerAuthorizationCodeGrant(reg.getClient(), 134L);
		grant.setRedirectUri("http://localhost:8080/services/reservations/reserve/complete");
		grant.setSubject(reg.getSubject());
		return grant;
	}

	public ServerAuthorizationCodeGrant removeCodeGrant(String code)
			throws OAuthServiceException {
		ServerAuthorizationCodeGrant theGrant = null;
		if (grant != null && grant.getCode().equals(code)) {
			theGrant = grant;
			grant = null;
			return theGrant;
		}
		return null;
	}

}