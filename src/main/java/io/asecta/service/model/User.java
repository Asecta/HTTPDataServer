package io.asecta.service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.mindrot.jbcrypt.BCrypt;

import io.asecta.rest.authentication.Accessor;
import io.asecta.rest.repository.Identifiable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User implements Identifiable, Accessor {

	private static int BCRYPTLOGROUNDS = 13; // Takes roughly 700ms to hash
	private static boolean ENABLE_HASHING = false;

	@Id @GeneratedValue private int id;
	@Column(unique = true) @NonNull private String email;
	@NonNull private String role = "USER";

	@NonNull private String password;

	private String username;
	public String avatar;

	public User(String email, String password) {
		this.email = email;
		setPassword(password);
	}

	@Override
	public boolean isAnonymous() {
		return false;
	}

	public boolean checkPassword(String password) {
		if (ENABLE_HASHING) {
			return BCrypt.checkpw(password, this.password);
		} else {
			return password.equals(this.password);
		}
	}

	public void setPassword(String newPassword) {
		System.out.println("Setting password to: " + newPassword);
		if (ENABLE_HASHING) {
			this.password = BCrypt.hashpw(newPassword, BCrypt.gensalt(BCRYPTLOGROUNDS));
		} else {
			this.password = newPassword;
		}
	}

}