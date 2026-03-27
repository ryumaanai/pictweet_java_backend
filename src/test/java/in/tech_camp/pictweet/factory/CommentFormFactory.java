package in.tech_camp.pictweet.factory;

import com.github.javafaker.Faker;

import in.tech_camp.pictweet.form.CommentForm;

public class CommentFormFactory {
  private static final Faker faker = new Faker();

  public static CommentForm createComment() {
    CommentForm commentForm = new CommentForm();
    commentForm.setText(faker.lorem().sentence(10));
    return commentForm;
  }
}
