package in.tech_camp.pictweet.form;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import in.tech_camp.pictweet.factory.TweetFormFactory;
import in.tech_camp.pictweet.validation.ValidationPriority1;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;


@ActiveProfiles("test")
public class TweetFormUnitTest {

    private Validator validator;

    private TweetForm tweetForm;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        tweetForm = TweetFormFactory.createTweet();
    }
    @Nested
    class ツイート投稿ができる場合 {
        @Test
        public void テキストと画像が存在していれば投稿できる () {
          Set<ConstraintViolation<TweetForm>> violations = validator.validate(tweetForm, ValidationPriority1.class);
          assertEquals(0, violations.size());
        }

        @Test
        public void テキストのみで投稿できる() {
            tweetForm.setImage("");

            Set<ConstraintViolation<TweetForm>> violations = validator.validate(tweetForm, ValidationPriority1.class);
            assertEquals(0, violations.size());
        }
    }
    @Nested
    class ツイート投稿ができない場合 {
        @Test
        public void テキストが空では投稿できない() {
            tweetForm.setText("");

            Set<ConstraintViolation<TweetForm>> violations = validator.validate(tweetForm, ValidationPriority1.class);
            assertEquals(1, violations.size());
            assertEquals("Text can't be blank", violations.iterator().next().getMessage());
        }
    }
}