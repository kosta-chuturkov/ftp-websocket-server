package ftp.core.common.model.dto;

public class UserNickNameDto {

	public UserNickNameDto(String nickName) {
		super();
		this.nickName = nickName;
	}
	
	public UserNickNameDto() {
	}

	private String nickName;

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	
}
