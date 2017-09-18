package org.sumbootFrame.data.dao.common;

import org.sumbootFrame.mvc.interfaces.IDao;

import java.util.List;

public interface MyBatisDAO extends IDao {

	public List<?> getCartAttribute();
}
