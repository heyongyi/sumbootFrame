package org.sumbootFrame.data.dao.primary.common;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.sumbootFrame.mvc.interfaces.IDao;

import java.util.List;
@Component
public interface PrimaryCommDAO extends IDao {

	public String getSysdate(@Param("sysdateSql") String sysdateSql);

	public List<?> executeSql(@Param("sql") String sql);


	public List<?> getTables(String schema);

	public List<?> getColumns(@Param("schema") String schema,
							  @Param("tableName") String tableName);

}
