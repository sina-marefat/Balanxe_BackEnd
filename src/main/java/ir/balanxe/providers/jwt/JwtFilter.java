package ir.balanxe.providers.jwt;

import ir.balanxe.modules.user.model.User;
import ir.balanxe.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final String ACCESS_TOKEN_GRANT_TYPE = "access_token";
    private boolean isServerOn = true;
    private boolean isTradeEngineOn = true;
    private boolean isDashboardOn = true;
    private boolean isConvertOn = true;

    @Autowired
    public JwtFilter(
            JwtUtil jwtUtil,
            UserService userService
    ) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (!checkServerStatus(httpServletRequest)) {
            httpServletResponse.setHeader("occurred_exception", String.valueOf(true));
            throw new IOException("Service Unavailable");
        } else
            httpServletResponse.setHeader("occurred_exception", String.valueOf(false));


        String authorizationHeader = httpServletRequest.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            UUID userId = jwtUtil.extractUserId(token);

            if (userId != null && jwtUtil.extractGrantType(token).equals(ACCESS_TOKEN_GRANT_TYPE) && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userService.findById(userId);

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private boolean checkServerStatus(HttpServletRequest httpServletRequest) {

        return isServerOn;
    }

    public boolean isServerOn() {
        return isServerOn;
    }


}
