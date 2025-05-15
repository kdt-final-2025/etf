package EtfRecommendService.security;

import EtfRecommendService.loginUtils.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        try {
            String token = extractToken(request);
            if (token != null && jwtProvider.isValidToken(token, true)) {
                Authentication auth = getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            chain.doFilter(request, response);
        }
        catch (ExpiredJwtException e){
            // 토큰 만료 예외 발생 시
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            String json = "{\"error\": \"EXPIRED_TOKEN\", \"message\": \"Access token expired\"}";
            response.getWriter().write(json);
            response.getWriter().flush();
        }
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Authentication getAuthentication(String token) {
        String username = jwtProvider.getSubject(token);
        List<String> roles = jwtProvider.getRolesFromAccess(token);
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(roles.get(0).split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails userDetails = new UserDetail(username, null, authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }
}

