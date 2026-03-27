package in.tech_camp.pictweet.entity;

import java.util.List;

import lombok.Data;

@Data
public class UserEntity {
  private Integer id;
  private String nickname;
  private String email;
  private String password;
  private List<TweetEntity> tweets;
  private List<CommentEntity> comments;
}
