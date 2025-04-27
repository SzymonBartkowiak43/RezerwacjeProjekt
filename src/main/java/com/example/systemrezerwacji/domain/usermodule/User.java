package com.example.systemrezerwacji.domain.usermodule;

import com.example.systemrezerwacji.domain.salonmodule.Salon;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Table(name = "users")
@Builder
public class User  implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String name;
    private String password;

    @Setter
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<UserRole> roles;

    @OneToMany(mappedBy = "user")
    List<Salon> salons;

    private User(UserBuilder userBuilder){
        this.email = userBuilder.email;
        this.name = userBuilder.name;
        this.password = userBuilder.password;
        roles = userBuilder.roles;
    }
    public User() {

    }
    User addUserRole(UserRole userRole) {
        roles.add(userRole);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    static class UserBuilder {
        private String email;
        private String name;
        private String password;
        private Set<UserRole> roles = new HashSet<>();

        UserBuilder addEmail(String email) {
            this.email = email;
            return this;
        }

        UserBuilder addName(String name) {
            this.name = name;
            return this;
        }

        UserBuilder addPassword(String password) {
            this.password = password;
            return this;
        }

        UserBuilder addUserRole(UserRole userRole) {
            this.roles.add(userRole);
            return this;
        }

        User build() {
            return new User(this);
        }

    }

}
