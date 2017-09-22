package org.sumbootFrame.data.dao.primary.common;

import org.apache.ibatis.annotations.Param;
import org.sumbootFrame.mvc.interfaces.IDao;

import java.util.List;

public interface PrimaryCommDAO extends IDao {

	public String getSysdate(@Param("sysdateSql") String sysdateSql);

	public List<?> executeSql(@Param("sql") String sql);

	public String getSequence(String seqName);
	public String getOraSequence(String seqName);

	public List<?> getTables(String schema);

	public List<?> getColumns(@Param("schema") String schema,
							  @Param("tableName") String tableName);

}
