package org.myapp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.oauth2.common.OAuthContext;
import org.apache.cxf.rs.security.oauth2.utils.OAuthContextUtils;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class MyApp {

	@Context
	private OAuthContext context;

	@Context
	private MessageContext mc;

	@GET
	@Path("/")
	public Person[] retrievePersons() {
		LogFactory.getLog(getClass().getName()).info(
				OAuthContextUtils.resolveUserName(mc));
		return new Person[] { new Person("asss"), new Person("jkslbnsk") };

	}

}
