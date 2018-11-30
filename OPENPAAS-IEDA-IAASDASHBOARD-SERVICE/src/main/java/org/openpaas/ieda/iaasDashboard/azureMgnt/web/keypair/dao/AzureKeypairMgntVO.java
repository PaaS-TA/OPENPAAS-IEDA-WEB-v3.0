package org.openpaas.ieda.iaasDashboard.azureMgnt.web.keypair.dao;

import javax.validation.constraints.NotNull;

public class AzureKeypairMgntVO {
	@NotNull
	private Integer recid;
	private Integer accountId; // 계정 아이디
	@NotNull
    private String keypairName; // keypair명
	private String keypairType;
	public Integer getRecid() {
		return recid;
	}
	public void setRecid(Integer recid) {
	}
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	public String getKeypairName() {
		return keypairName;
	}
	public void setKeypairName(String keypairName) {
		this.keypairName = keypairName;
	}
	public String getKeypairType() {
		return keypairType;
	}
	public void setKeypairType(String keypairType) {
		this.keypairType = keypairType;
	}
}
