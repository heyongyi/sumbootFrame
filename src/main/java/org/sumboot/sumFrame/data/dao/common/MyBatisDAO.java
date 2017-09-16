package org.sumboot.sumFrame.data.dao.common;

import org.apache.ibatis.annotations.Param;
import org.sumboot.sumFrame.mvc.interfaces.IDao;

import java.util.List;

public interface MyBatisDAO extends IDao {

	public List<?> getCartAttribute();
}
