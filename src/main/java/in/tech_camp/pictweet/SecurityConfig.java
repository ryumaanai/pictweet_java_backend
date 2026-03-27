package in.tech_camp.pictweet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        //ここに記述されたGETリクエストは許可されます（ログイン不要です)
                        .requestMatchers(HttpMethod.GET, "/css/**", "/images/**", "/", "/users/sign_up", "/users/login", "/tweets/{id:[0-9]+}","/users/{id:[0-9]+}","/tweets/search","/error").permitAll()
                        //ここに記述されたPOSTリクエストは許可されます(ログイン不要です)
                        .requestMatchers(HttpMethod.POST, "/user").permitAll()
                        .anyRequest().authenticated())
                        //上記以外のリクエストは認証されたユーザーのみ許可されます(要ログイン)

                .formLogin(login -> login
                        .loginProcessingUrl("/login")
                        //ログインフォームでログインボタンを押した際のパスを設定しています
                        .loginPage("/users/login")
                        //ログインフォームのパスを設定しています
                        .defaultSuccessUrl("/", true)
                        //ログイン成功後のリダイレクト先です
                        .failureUrl("/login?error")
                        //ログイン失敗後のリダイレクト先です
                        .usernameParameter("email")
                        //ログイン時にusernameとして扱うパラメーターを指定します
                        .permitAll())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        //ログアウトボタンを押した際のパスを設定しています
                        .logoutSuccessUrl("/"));
                        //ログアウト成功時のリダイレクト先です
                        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
