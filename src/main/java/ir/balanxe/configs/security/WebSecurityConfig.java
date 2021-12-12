package ir.balanxe.configs.security;

import ir.balanxe.configs.customresponse.FinearzExceptionResponse;
import ir.balanxe.modules.user.service.UserService;
import ir.balanxe.providers.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserService userService;
    private final JwtFilter jwtFilter;

    @Autowired
    public WebSecurityConfig(
            BCryptPasswordEncoder bCryptPasswordEncoder,
            UserService userService,
            JwtFilter jwtFilter
    ) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
        this.jwtFilter = jwtFilter;
    }

    @Override
    protected void configure(
            HttpSecurity http
    ) throws Exception {
        http.csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers("/auth/**").permitAll()
                .and()
                .authorizeRequests()

                .antMatchers(
                        "/user-verification/register-verification-fields/**",
                        "/user-verification/confirm-verification-fields/**",
                        "/user-verification/save-information/**"
                ).hasAuthority("USER")

//                .antMatchers("/fiat-wallet/**")
//                .hasAnyAuthority(
//                        "VERIFIED_USER",
//                        "SUPPORT",
//                        "ACCOUNTANT",
//                        "MARKETING_SPECIALIST",
//                        "FP&A",
//                        "ADMIN",
//                        "SUPER_ADMIN"
//                )

                .antMatchers("/user/profile").permitAll()
                .antMatchers("/user").permitAll()
                .antMatchers("/general/**").permitAll()
                .anyRequest().authenticated().and().exceptionHandling()
                .authenticationEntryPoint(new FinearzExceptionResponse())
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(
            AuthenticationManagerBuilder auth
    ) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(userService);

        return provider;
    }
}
