package com.umcsuser.current.models;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "ID")
@ToString(exclude = "passwordHash")

public class User {
    private String ID;
    private String login;
    private String passwordHash;
    private Role role;

    public User copy(){
        return User.builder()
                .ID(ID)
                .login(login)
                .passwordHash(passwordHash)
                .role(role)
                .build();
    }

}
