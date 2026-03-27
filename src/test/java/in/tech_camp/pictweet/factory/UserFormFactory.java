package in.tech_camp.pictweet.factory;

import com.github.javafaker.Faker;

import in.tech_camp.pictweet.form.UserForm;

public class UserFormFactory {
  private static final Faker faker = new Faker();

  public static UserForm createUser() {
    UserForm userForm = new UserForm();

    userForm.setEmail(faker.internet().emailAddress());
    String generatedUsername = faker.name().username();

    if (generatedUsername.length() > 6) {
        generatedUsername = generatedUsername.substring(0, 6);
    }
    userForm.setNickname(generatedUsername);
    userForm.setPassword(faker.internet().password(6, 12));
    userForm.setPasswordConfirmation(userForm.getPassword());
    return userForm;
  }
}
