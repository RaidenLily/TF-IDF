package com.company.entiy;

import com.company.bean.WbMessage;
import lombok.Data;
import org.ansj.domain.Result;

@Data
public class ResultAndWb {
	private Result result;
	private WbMessage wbMessage;
	private boolean end;

	public ResultAndWb(Result result, WbMessage wbMessage,boolean end){
		this.result=result;
		this.wbMessage=wbMessage;
		this.end=end;
	}

	public ResultAndWb(boolean end){
		this.end=end;
	}
}
