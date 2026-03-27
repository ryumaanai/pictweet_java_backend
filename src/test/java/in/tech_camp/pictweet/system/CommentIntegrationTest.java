package in.tech_camp.pictweet.system;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import in.tech_camp.pictweet.PictweetApplication;
import in.tech_camp.pictweet.entity.CommentEntity;
import in.tech_camp.pictweet.entity.TweetEntity;
import in.tech_camp.pictweet.entity.UserEntity;
import in.tech_camp.pictweet.factory.UserFormFactory;
import in.tech_camp.pictweet.form.UserForm;
import in.tech_camp.pictweet.repository.CommentRepository;
import in.tech_camp.pictweet.repository.TweetRepository;
import in.tech_camp.pictweet.service.UserService;
import static in.tech_camp.pictweet.support.LoginSupport.login;
import jakarta.servlet.http.HttpSession;

@ActiveProfiles("test")
@SpringBootTest(classes = PictweetApplication.class)
@AutoConfigureMockMvc
public class CommentIntegrationTest {
  private UserForm userForm;
  private UserEntity userEntity;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserService userService;

  @Autowired
  private TweetRepository tweetRepository;

  @Autowired
  private CommentRepository commentRepository;

  @BeforeEach
  public void setup() {
      userForm = UserFormFactory.createUser();
      userEntity = new UserEntity();
      userEntity.setEmail(userForm.getEmail());
      userEntity.setNickname(userForm.getNickname());
      userEntity.setPassword(userForm.getPassword());

      userService.createUserWithEncryptedPassword(userEntity);
  }

  @Test
  public void ログインしたユーザーはツイート詳細ページでコメント投稿できる() throws Exception {
     HttpSession session = login(mockMvc, userForm);

    String tweetText = "テストツイート詳細";
    mockMvc.perform(post("/tweets")
            .param("text", tweetText)
            .param("image", "test.png")
            .with(csrf())
            .session((MockHttpSession) session))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/"));

    List<TweetEntity> tweets = tweetRepository.findAll();
    Integer tweetId = tweets.get(0).getId();

     mockMvc.perform(get("/tweets/{tweetId}", tweetId)
             .session((MockHttpSession) session))
             .andExpect(status().isOk())
             .andExpect(content().string(containsString(tweetText)));

    String commentText = "これはテストコメントです";
    mockMvc.perform(post("/tweets/{tweetId}/comment", tweetId)
            .param("text", commentText)
            .with(csrf())
            .session((MockHttpSession) session))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/tweets/" + tweetId));

     List<CommentEntity>  comments = commentRepository.findByTweetId(tweets.get(0).getId());
     Integer commentCount = comments.size();
     assertEquals(1, commentCount);

    mockMvc.perform(get("/tweets/{tweetId}", tweetId)
            .session((MockHttpSession) session))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString(commentText)));
  }
}