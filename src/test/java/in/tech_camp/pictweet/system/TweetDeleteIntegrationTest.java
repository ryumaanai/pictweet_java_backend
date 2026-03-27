package in.tech_camp.pictweet.system;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import in.tech_camp.pictweet.PictweetApplication;
import in.tech_camp.pictweet.entity.TweetEntity;
import in.tech_camp.pictweet.entity.UserEntity;
import in.tech_camp.pictweet.factory.TweetFormFactory;
import in.tech_camp.pictweet.factory.UserFormFactory;
import in.tech_camp.pictweet.form.TweetForm;
import in.tech_camp.pictweet.form.UserForm;
import in.tech_camp.pictweet.repository.TweetRepository;
import in.tech_camp.pictweet.service.UserService;
import static in.tech_camp.pictweet.support.LoginSupport.login;

@ActiveProfiles("test")
@SpringBootTest(classes = PictweetApplication.class)
@AutoConfigureMockMvc
public class TweetDeleteIntegrationTest {
  private UserForm userForm1;
  private UserEntity userEntity1;

  private UserForm userForm2;
  private UserEntity userEntity2;

  private TweetForm tweetForm1;
  private TweetEntity tweetEntity1;

  private TweetForm tweetForm2;
  private TweetEntity tweetEntity2;

  @Autowired
  private UserService userService;

  @Autowired
  private TweetRepository tweetRepository;

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    userForm1 = UserFormFactory.createUser();
    userEntity1 = new UserEntity();
    userEntity1.setEmail(userForm1.getEmail());
    userEntity1.setNickname(userForm1.getNickname());
    userEntity1.setPassword(userForm1.getPassword());
    userService.createUserWithEncryptedPassword(userEntity1);

    userForm2 = UserFormFactory.createUser();
    userEntity2 = new UserEntity();
    userEntity2.setEmail(userForm2.getEmail());
    userEntity2.setNickname(userForm2.getNickname());
    userEntity2.setPassword(userForm2.getPassword());
    userService.createUserWithEncryptedPassword(userEntity2);

    tweetForm1 = TweetFormFactory.createTweet();
    tweetEntity1 = new TweetEntity();
    tweetEntity1.setUser(userEntity1);
    tweetEntity1.setImage(tweetForm1.getImage());
    tweetEntity1.setText(tweetForm1.getText());
    tweetRepository.insert(tweetEntity1);

    tweetForm2 = TweetFormFactory.createTweet();
    tweetEntity2 = new TweetEntity();
    tweetEntity2.setUser(userEntity2);
    tweetEntity2.setImage(tweetForm2.getImage());
    tweetEntity2.setText(tweetForm2.getText());
    tweetRepository.insert(tweetEntity2);
  }

    @Nested
  class ツイート削除ができるとき {
    @Test
    public void ログインしたユーザーは自らが投稿したツイートの削除ができる() throws Exception {
      MockHttpSession session = login(mockMvc, userForm1);

      assertNotNull(session);

      MvcResult pageResult = mockMvc.perform(get("/").session(session))
          .andReturn();
      String topPageContent = pageResult.getResponse().getContentAsString();
      Document topPageDocument = Jsoup.parse(topPageContent);
      Element deleteMenuElement = topPageDocument.selectFirst("form[action='/tweets/" + tweetEntity1.getId() + "/delete']");
      assertNotNull(deleteMenuElement);
      Element deleteButtonElement = deleteMenuElement.selectFirst("input[type='submit']");
      assertNotNull(deleteButtonElement);
      assertEquals("削除", deleteButtonElement.val());

      List<TweetEntity> tweetsListBeforeDeletion = tweetRepository.findAll();
      Integer initialCount = tweetsListBeforeDeletion.size();

      mockMvc.perform(post("/tweets/{tweetId}/delete",tweetEntity1.getId()).session(session)
          .with(csrf()))
          .andExpect(status().isFound())
          .andExpect(redirectedUrl("/"));

      List<TweetEntity> tweetsListAfterDeletion = tweetRepository.findAll();
      Integer afterCount = tweetsListAfterDeletion.size();
      assertEquals(initialCount - 1, afterCount);

      MvcResult pageResultAfterDelete = mockMvc.perform(get("/"))
          .andReturn();
      String pageContentAfterDelete = pageResultAfterDelete.getResponse().getContentAsString();
      Document documentAfterDelete = Jsoup.parse(pageContentAfterDelete);
      Element divElement = documentAfterDelete.selectFirst(".content_post[style='background-image: url(" + tweetForm1.getImage() + ");']");
      assertNull(divElement);

      mockMvc.perform(get("/"))
          .andExpect(content().string(not(containsString(tweetEntity1.getText()))));
    }
  }

  @Nested
  class ツイート削除ができないとき {
    @Test
    public void ログインしたユーザーは自分以外が投稿したツイートの削除ができない() throws Exception {
      MockHttpSession session = login(mockMvc, userForm1);
      assertNotNull(session);

      MvcResult pageResult = mockMvc.perform(get("/").session(session))
          .andReturn();
      String pageContent = pageResult.getResponse().getContentAsString();
      Document document = Jsoup.parse(pageContent);
      Element deleteMenuElement = document.selectFirst("form[action='/tweets/" + tweetEntity2.getId() + "/delete']");
      assertNull(deleteMenuElement);
    }

    @Test
    public void ログインしていないとツイートの削除ができない() throws Exception {
      MvcResult pageResult = mockMvc.perform(get("/"))
      .andReturn();
      String pageContent = pageResult.getResponse().getContentAsString();
      Document document = Jsoup.parse(pageContent);

      Element tweet1DeleteMenuElement = document.selectFirst("form[action='/tweets/" + tweetEntity1.getId() + "/delete']");
      assertNull(tweet1DeleteMenuElement);
      Element tweet2DeleteMenuElement = document.selectFirst("form[action='/tweets/" + tweetEntity2.getId() + "/delete']");
      assertNull(tweet2DeleteMenuElement);
    }
  }

}
