package com.zdt.zyellowpage.jsonEntity;

public class LoginRequest {

	private String method;

	private LoginUser data;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public LoginUser getData() {
		return data;
	}

	public void setData(LoginUser data) {
		this.data = data;
	}

	public LoginRequest(String method, String userName, String password) {
		this.method = method;
		this.data = new LoginUser(userName, password);
	}

	class LoginUser {
		private String password;

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		private String username;

		@Override
		public String toString() {

			return username + ":" + password;
		};

		public LoginUser(String username, String password) {
			this.username = username;
			this.password = password;
		}
	}
}
