package com.javaweb.exception;

import com.javaweb.api.support.ErrorProbeController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiExceptionHandlerTest {
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ErrorProbeController())
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    public void malformedJsonReturnsSafeBadRequest() throws Exception {
        mockMvc.perform(post("/api/error-probe/body")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Nội dung JSON không hợp lệ."))
                .andExpect(jsonPath("$.details").isEmpty());
    }

    @Test
    public void beanValidationReturnsFieldDetails() throws Exception {
        mockMvc.perform(post("/api/error-probe/body")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dữ liệu gửi lên không hợp lệ."))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    public void validationGroupsProtectUserCreationFields() throws Exception {
        mockMvc.perform(post("/api/error-probe/user-create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", org.hamcrest.Matchers.hasItem(
                        org.hamcrest.Matchers.containsString("userName"))))
                .andExpect(jsonPath("$.details", org.hamcrest.Matchers.hasItem(
                        org.hamcrest.Matchers.containsString("password"))));
    }

    @Test
    public void notFoundUses404Schema() throws Exception {
        mockMvc.perform(get("/api/error-probe/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Không tìm thấy bản ghi."));
    }

    @Test
    public void conflictUses409WithoutPersistenceDetails() throws Exception {
        mockMvc.perform(get("/api/error-probe/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Dữ liệu đã tồn tại."));
    }

    @Test
    public void unexpectedErrorDoesNotExposeCause() throws Exception {
        mockMvc.perform(get("/api/error-probe/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Hệ thống không thể xử lý yêu cầu."))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("database-password-secret"))));
    }
}
