package org.sumboot.sumFrame.data.dao.common;

import org.apache.ibatis.annotations.Param;
import org.sumboot.sumFrame.mvc.interfaces.IDao;

import java.util.List;

public interface MyBatisCommDAO extends IDao {

	public String getSysdate(@Param("sysdateSql") String sysdateSql);

	public List<?> executeSql(@Param("sql") String sql);

	public String getSequence(String seqName);
	public String getOraSequence(String seqName);

	public List<?> getTables(String schema);

	public List<?> getColumns(@Param("schema") String schema,
							  @Param("tableName") String tableName);

}
