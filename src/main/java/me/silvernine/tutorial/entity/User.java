package me.silvernine.tutorial.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "`user`")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

   @Id
   @Column(name = "user_id")
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long userId;

   @Column(name = "username", length = 50, unique = true)
   private String username;

   @Column(name = "password", length = 100)
   @JsonIgnore
   private String password;

   @Column(name = "nickname", length = 50)
   private String nickname;

   @Column(name = "activated")
   @JsonIgnore
   private boolean activated;

   @ManyToMany
   @JoinTable(
      name = "user_authority",
      joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
      inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
   @JsonIgnore
   private Set<Authority> authorities;
}
