package com.keycloak.userservice.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;

import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class WebSecurityConfigTest {
  @Autowired
  private WebSecurityConfig webSecurityConfig;

  /**
   * Method under test: {@link WebSecurityConfig#sessionAuthenticationStrategy()}
   */
  @Test
  void testSessionAuthenticationStrategy() throws SessionAuthenticationException {
    //   Diffblue Cover was unable to write a Spring test,
    //   so wrote a non-Spring test instead.
    //   Reason: R026 Failed to create Spring context.
    //   Attempt to initialize test context failed with
    //   java.lang.IllegalStateException: ApplicationContext failure threshold (1) exceeded: skipping repeated attempt to load context for [WebMergedContextConfiguration@3dbb090c testClass = com.keycloak.userservice.config.DiffblueFakeClass48, locations = [], classes = [com.keycloak.userservice.UserServiceApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [], propertySourceProperties = ["org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@4945145b, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@7c1e8c69, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@1f9c82df, org.springframework.boot.test.web.reactive.server.WebTestClientContextCustomizer@d638750, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@6b90eeb2, org.springframework.boot.test.context.SpringBootTestAnnotation@8339b974], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
    //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:145)
    //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:130)
    //       at java.base/java.util.Optional.map(Optional.java:260)
    //   See https://diff.blue/R026 to resolve this issue.

    SessionAuthenticationStrategy actualSessionAuthenticationStrategyResult = (new WebSecurityConfig())
            .sessionAuthenticationStrategy();
    BearerTokenAuthenticationToken authentication = new BearerTokenAuthenticationToken("ABC123");
    MockHttpServletRequest request = new MockHttpServletRequest();
    actualSessionAuthenticationStrategyResult.onAuthentication(authentication, request, new Response());
    HttpSession session = request.getSession();
    assertTrue(session instanceof MockHttpSession);
    assertFalse(((MockHttpSession) session).isInvalid());
    assertSame(request.getServletContext(), session.getServletContext());
  }

  /**
   * Method under test:
   * {@link WebSecurityConfig#securityFilterChain(HttpSecurity)}
   */
  @Test
  @Disabled("TODO: Complete this test")
  void testSecurityFilterChain() throws Exception {
    // TODO: Complete this test.
    //   Reason: R026 Failed to create Spring context.
    //   Attempt to initialize test context failed with
    //   java.lang.IllegalStateException: ApplicationContext failure threshold (1) exceeded: skipping repeated attempt to load context for [WebMergedContextConfiguration@1e0758cc testClass = com.keycloak.userservice.config.DiffblueFakeClass2, locations = [], classes = [com.keycloak.userservice.UserServiceApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [], propertySourceProperties = ["org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@4945145b, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@7c1e8c69, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@1f9c82df, org.springframework.boot.test.web.reactive.server.WebTestClientContextCustomizer@d638750, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@6b90eeb2, org.springframework.boot.test.context.SpringBootTestAnnotation@8339b974], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
    //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:145)
    //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:130)
    //       at java.base/java.util.Optional.map(Optional.java:260)
    //   See https://diff.blue/R026 to resolve this issue.

    AuthenticationManagerBuilder authenticationBuilder = new AuthenticationManagerBuilder(null);
    webSecurityConfig.securityFilterChain(new HttpSecurity(null, authenticationBuilder, new HashMap<>()));
  }
}
