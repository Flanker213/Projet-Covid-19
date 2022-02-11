package com.statistiquescovid.utilisateur.web.dto;

public class PasswordDTO {

    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
    private String userId;
    private String userTokenResetPassword;

    public PasswordDTO() {
		super();
	}

	public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserTokenResetPassword() {
		return userTokenResetPassword;
	}

	public void setUserTokenResetPassword(String userTokenResetPassword) {
		this.userTokenResetPassword = userTokenResetPassword;
	}

	@Override
	public String toString() {
		return "PasswordDTO [oldPassword=" + oldPassword + ", newPassword=" + newPassword + ", confirmPassword="
				+ confirmPassword + ", userId=" + userId + ", userTokenResetPassword=" + userTokenResetPassword + "]";
	}

}
