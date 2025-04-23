package will.dev.BTBTEST.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import will.dev.BTBTEST.entity.Jwt;
import will.dev.BTBTEST.services.UserService;

import java.io.IOException;

@Service
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    HandlerExceptionResolver handlerExceptionResolver;//Pour gérer les exceptions au niveau du filtre
    private final UserService userService;
    private final JwtService jwtService;

    public JwtFilter(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    //Traitement pour autoriser un user a éffectuer des actions dans l'apk grace au token
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = null;
        String username = null;
        Boolean isTokenExpred = true;
        Jwt tokenDansBd = null;//--------------------------------------------------------------------------Branch déconnexion

        try {
            //Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NDI2OTYyMDAsImV4cCI6MTc0MjY5ODAwMCwic3ViIjoiZm90c29uZG9uZ21vOEBnbWFpbC5jb20iLCJub20iOiJGb3RzbyBOZG9uZ21vIiwiZW1haWwiOiJmb3Rzb25kb25nbW84QGdtYWlsLmNvbSJ9.LfcWY1gDsACTUwld1wLmw6LICu6K54WnCku7-89VVn4
            String authorisation = request.getHeader("Authorization");
            //System.out.println("Authorization Header: " + authorisation);

            if (authorisation != null && authorisation.startsWith("Bearer ")) {
                token = authorisation.substring(7);
                tokenDansBd = this.jwtService.tokenByValue(token);//-------------------------------------------Branch déconnexion
                isTokenExpred = jwtService.isTokenExpred(token);
                username = jwtService.extractUsername(token);
            }

            if (!isTokenExpred
                    && tokenDansBd.getUser().getEmail().equals(username)//---------------------------------------Branch déconnexion
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(username);
                if (userDetails != null) {
                    System.out.println("Utilisateur authentifié : " + userDetails.getUsername());
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    System.out.println("⚠️ Aucun utilisateur trouvé pour ce token !");
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request,response, null, exception);
        }
    }
}
