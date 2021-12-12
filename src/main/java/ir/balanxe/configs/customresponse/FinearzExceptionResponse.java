package ir.balanxe.configs.customresponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FinearzExceptionResponse implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        if (httpServletResponse.getHeader("occurred_exception").equals("true"))
            httpServletResponse.sendError(
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "داریم پلتفرم‌مون رو خفن‌تر میکنیم، لطفا چند لحظه صبر کن"
            );
        else
            httpServletResponse.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "ابتدا عمل ورود را انجام دهید"
            );
    }
}
