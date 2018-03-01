package org.sumbootFrame.data.dao.primary;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.sumbootFrame.mvc.interfaces.IDao;

import java.util.HashMap;
import java.util.List;
@Component
public interface PrimaryCommDAO extends IDao {

	public String getSysdate(@Param("sysdateSql") String sysdateSql);

	public List<?> executeSql(@Param("sql") String sql);


	public List<?> getTables(String schema);

	public List<?> getColumns(@Param("schema") String schema,
							  @Param("tableName") String tableName);


	public void insertuser(HashMap<String,Object> tUserShareInfo);

}
