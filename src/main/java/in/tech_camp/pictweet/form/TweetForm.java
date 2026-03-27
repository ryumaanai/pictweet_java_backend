package in.tech_camp.pictweet.form;

import in.tech_camp.pictweet.validation.ValidationPriority1;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TweetForm {
  
  @NotBlank(message = "Text can't be blank", groups = ValidationPriority1.class)
  private String text;

  private String image;
}
