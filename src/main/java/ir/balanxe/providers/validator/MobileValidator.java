package ir.balanxe.providers.validator;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class MobileValidator implements Predicate<String> {

    @Override
    public boolean test(String mobile) {
        return mobile.startsWith("09") && mobile.length() == 11;
    }
}
