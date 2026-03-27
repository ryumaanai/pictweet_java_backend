package in.tech_camp.pictweet.support;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import in.tech_camp.pictweet.form.UserForm;

public class LoginSupport {
  public static MockHttpSession login(MockMvc mockMvc, UserForm userForm) throws Exception {
    MvcResult loginResult = mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("email", userForm.getEmail())
            .param("password", userForm.getPassword())
            .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/"))
            .andReturn();
    return (MockHttpSession) loginResult.getRequest().getSession();
  }
}
