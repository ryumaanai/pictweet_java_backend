package in.tech_camp.pictweet.entity;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
public class TweetEntity {
  private Integer id;
  private String text;
  private String image;
  private Timestamp createdAt;
  @ToString.Exclude
  private UserEntity user;
  @ToString.Exclude
  private List<CommentEntity> comments;
}
