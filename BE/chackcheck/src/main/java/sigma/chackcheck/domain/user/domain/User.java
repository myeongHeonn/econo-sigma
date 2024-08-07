package sigma.chackcheck.domain.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loginId", nullable = false, unique = true)
    private String loginId;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    // 기수
    @Column(nullable = false)
    private Integer grade;
    // 대출 유무
    @Builder.Default
    private Boolean isBorrow = false;
    // 예약 유무
    @Builder.Default
    private Boolean isReserve = false;
    // 대출 도서 개수
    @Builder.Default
    private Integer borrowCount = 0;
    // 예약 도서 개수
    @Builder.Default
    private Integer reserveCount = 0;
    @OneToOne
    private Penalty penalty;

    // 권한
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.ROLE_USER;

    @Override // 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    // 사용자의 id를 반환(고유한 값)
    @Override
    public String getUsername() { return loginId; }

    // 사용자의 패스워드 반환
    @Override
    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        return true; // true -> 만료되지 않았음
    }

    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        return true; // true -> 잠금되지 않았음
    }

    // 패스워드 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // true -> 만료되지 않았음
    }

    // 계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        return true; // true -> 사용 가능
    }
}
