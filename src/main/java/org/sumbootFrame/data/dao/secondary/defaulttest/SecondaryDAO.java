package org.sumbootFrame.data.dao.secondary.defaulttest;

import org.springframework.stereotype.Component;
import org.sumbootFrame.mvc.interfaces.IDao;

import java.util.List;
@Component
public interface SecondaryDAO extends IDao {

	public List<?> getEcsAreainfo();
}
