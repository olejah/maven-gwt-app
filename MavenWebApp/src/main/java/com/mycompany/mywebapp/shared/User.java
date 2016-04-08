package com.mycompany.mywebapp.shared;

import com.google.gwt.view.client.ProvidesKey;

public class User {

	private long id;
	private String name;
	private String surname;
	private String email;
	private String role;

	/**
	 * The key provider that provides the unique ID of a contact.
	 */
	public static final ProvidesKey<User> KEY_PROVIDER = new ProvidesKey<User>() {
		@Override
		public Object getKey(User user) {
			return user == null ? null : user.getId();
		}
	};

	public User() {

	}

	public User(long id) {
		this.id = id;
	}

	public Long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getSurname() {
		return this.surname;
	}

	public String getEmail() {
		return this.email;
	}

	public String getRole() {
		return this.role;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public int hashCode() {
		int hashCode = 17;

		hashCode = (int) (hashCode + this.name.hashCode()
				+ this.id + this.surname.hashCode() + this.email
				.hashCode());
		return hashCode;
	}

	public String toString() {
		return new StringBuilder("User ").append(name).append(" ")
				.append(surname).append(" has id ").append(id)
				.append(" and email ").append(email).append("\n").toString();
	}
}
