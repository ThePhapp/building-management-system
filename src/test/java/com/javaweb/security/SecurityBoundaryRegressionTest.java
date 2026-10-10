package com.javaweb.security;

import com.javaweb.config.WebSecurityConfig;
import com.javaweb.security.support.SecurityProbeController;
import com.javaweb.service.IUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SecurityProbeController.class)
@Import(WebSecurityConfig.class)
public class SecurityBoundaryRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @Test
    public void anonymousUserApiWriteIsDeniedEvenWithValidCsrfToken() throws Exception {
        mockMvc.perform(post("/api/user").with(csrf()))
                .andExpect(anonymousRequestIsDenied());
    }

    @Test
    public void anonymousBuildingApiWriteIsDeniedEvenWithValidCsrfToken() throws Exception {
        mockMvc.perform(post("/api/building").with(csrf()))
                .andExpect(anonymousRequestIsDenied());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void adminCanAccessUserAdministration() throws Exception {
        mockMvc.perform(get("/admin/user/list"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    public void staffCannotAccessUserAdministration() throws Exception {
        mockMvc.perform(get("/admin/user/list"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void managerCannotAccessUserAdministration() throws Exception {
        mockMvc.perform(get("/admin/user/list"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void authenticatedUserApiWriteWithoutCsrfIsDenied() throws Exception {
        mockMvc.perform(post("/api/user"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.path").value("/api/user"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void authenticatedBuildingApiWriteWithoutCsrfIsDenied() throws Exception {
        mockMvc.perform(post("/api/building"))
                .andExpect(status().isForbidden());
    }

    private ResultMatcher anonymousRequestIsDenied() {
        return result -> {
            int responseStatus = result.getResponse().getStatus();
            assertThat(responseStatus)
                    .as("anonymous API writes must receive 401, 403, or a login redirect")
                    .isIn(401, 403, 302);

            if (responseStatus == 302) {
                assertThat(result.getResponse().getRedirectedUrl())
                        .as("a redirect must send the anonymous caller to the login page")
                        .endsWith("/login");
            }
        };
    }
}
