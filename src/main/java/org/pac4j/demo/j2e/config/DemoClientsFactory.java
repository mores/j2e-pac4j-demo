package org.pac4j.demo.j2e.config;

import org.pac4j.cas.client.CasClient;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.ClientsFactory;
import org.pac4j.http.client.direct.ParameterClient;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.http.client.indirect.IndirectBasicAuthClient;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.http.profile.creator.AuthenticatorProfileCreator;
import org.pac4j.http.profile.creator.test.SimpleTestUsernameProfileCreator;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oauth.client.StravaClient;
import org.pac4j.oauth.client.TwitterClient;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.client.SAML2ClientConfiguration;
import java.io.File;

public class DemoClientsFactory implements ClientsFactory {

    @Override
    public Clients build(final Object env) {
        final OidcClient oidcClient = new OidcClient();
        oidcClient.setClientID("343992089165-sp0l1km383i8cbm2j5nn20kbk5dk8hor.apps.googleusercontent.com");
        oidcClient.setSecret("uR3D8ej1kIRPbqAFaxIE3HWh");
        oidcClient.setDiscoveryURI("https://accounts.google.com/.well-known/openid-configuration");
        oidcClient.addCustomParam("prompt", "consent");

        final SAML2ClientConfiguration cfg = new SAML2ClientConfiguration("resource:samlKeystore.jks",
                                                "pac4j-demo-passwd",
                                                "pac4j-demo-passwd",
                                                "resource:testshib-providers.xml");
        cfg.setMaximumAuthenticationLifetime(3600);
        cfg.setServiceProviderEntityId("urn:mace:saml:pac4j.org");
        cfg.setServiceProviderMetadataPath(new File("target", "sp-metadata.xml").getAbsolutePath());
        final SAML2Client saml2Client = new SAML2Client(cfg);

        final FacebookClient facebookClient = new FacebookClient("145278422258960", "be21409ba8f39b5dae2a7de525484da8");
        final TwitterClient twitterClient = new TwitterClient("CoxUiYwQOSFDReZYdjigBA",
                "2kAzunH5Btc4gRSaMr7D7MkyoJ5u1VzbOOzE8rBofs");
        // HTTP
        final FormClient formClient = new FormClient("http://localhost:8080/theForm.jsp",
                new SimpleTestUsernamePasswordAuthenticator(), new SimpleTestUsernameProfileCreator());
        final IndirectBasicAuthClient basicAuthClient = new IndirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator(),
                new SimpleTestUsernameProfileCreator());

        // CAS
        final CasClient casClient = new CasClient();
        // casClient.setGateway(true);
        casClient.setCasLoginUrl("http://localhost:8888/cas/login");
        casClient.setCasPrefixUrl("http://localhost:8888/cas/p3");

        // Strava
        final StravaClient stravaClient = new StravaClient();
        stravaClient.setApprovalPrompt("auto");
        // client_id
        stravaClient.setKey("3945");
        // client_secret
        stravaClient.setSecret("f03df80582396cddfbe0b895a726bac27c8cf739");
        stravaClient.setScope("view_private");

        // REST authent with JWT for a token passed in the url as the token parameter
        ParameterClient parameterClient = new ParameterClient("token", new JwtAuthenticator(Constants.JWT_SALT), new AuthenticatorProfileCreator());
        parameterClient.setSupportGetRequest(true);
        parameterClient.setSupportPostRequest(false);

        return new Clients("http://localhost:8080/callback", oidcClient, saml2Client, facebookClient,
                twitterClient, formClient, basicAuthClient, casClient, stravaClient, parameterClient);
    }
}