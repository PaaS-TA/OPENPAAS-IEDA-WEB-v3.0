package org.openpaas.ieda.iaasDashboard.azureMgnt.web.keypair.dto;

import javax.validation.constraints.NotNull;

public class AzureKeypairMgntDTO {
	@NotNull
	private Integer recid;
	private Integer accountId; // 계정 아이디
	@NotNull
    private String keypairName; // keypair명
	
	public Integer getRecid() {
		return recid;
	}
	public void setRecid(Integer recid) {
		this.recid = recid;
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
}
