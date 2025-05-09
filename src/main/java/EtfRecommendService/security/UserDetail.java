package EtfRecommendService.security;

import EtfRecommendService.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@RequiredArgsConstructor
public class UserDetail implements UserDetails {
    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword(){
        return this.user.getPassword().getPassword();
    }

    @Override
    public String getUsername(){
        return this.user.getNickName();
    }
}
