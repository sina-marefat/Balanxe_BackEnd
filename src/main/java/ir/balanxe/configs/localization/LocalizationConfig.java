package ir.balanxe.configs.localization;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Configuration
public class LocalizationConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        registry.addInterceptor(localeChangeInterceptor);
    }

    @Bean
    public LocaleResolver localeResolver() {
        return new SmartLocaleResolver();
    }


    public class SmartLocaleResolver extends CookieLocaleResolver {

        @Override
        public Locale resolveLocale(HttpServletRequest request) {
            String acceptLanguage = request.getHeader("Accept-Language");
            String lang = request.getParameter("lang");
            if (acceptLanguage == null || acceptLanguage.trim().isEmpty()) {
                return super.determineDefaultLocale(request);
            }
            return request.getLocale();
        }
    }
}
