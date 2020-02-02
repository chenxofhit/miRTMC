package com.csu.webapp.type;


/**
 * 
 * 用户的四种查询方式枚举
 * 
 * @author chenx
 * @since 2020-01-26 19:13:59
 * 
 */

public enum SearchBy {

	miRNA_name(1, "miRNA name"), gene_utraname(2, "gene utraname"), gene_symbol(3, "gene symbol"), gene_gs_id(4,
			"gene gs id");

	private int code;
	private String msg;

	SearchBy(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public String getCodeStr() {
		return code + "";
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}


    public SearchBy setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public static String getMsgByCode(int code) {
    	SearchBy[] values = SearchBy.values();
        for (SearchBy ec : values) {
            if (ec.code == code) {
                return ec.msg;
            }
        }
        return "";
    }

	
	
}
