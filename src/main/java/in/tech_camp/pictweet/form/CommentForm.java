package in.tech_camp.pictweet.form;

import in.tech_camp.pictweet.validation.ValidationPriority1;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentForm {
  @NotBlank(message = "Comment can't be blank", groups = ValidationPriority1.class)
  private String text;
}
