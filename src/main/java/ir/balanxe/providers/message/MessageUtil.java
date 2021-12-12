package ir.balanxe.providers.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

@Service
public class MessageUtil {
    public static  ResourceBundleMessageSource messageSource;

    @Autowired
    public MessageUtil(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public static String getDesiredMessage(String code){
        return messageSource.getMessage(code,null, LocaleContextHolder.getLocale());
    }
}
