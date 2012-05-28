package org.oddjob.spring;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class MyQuery implements Runnable {

	private DataSource dataSource;
	
	private int meaningOfLife;
	
	@Override
	public void run() {

		meaningOfLife = new JdbcTemplate(dataSource).query(
				"values 42", 
				new ResultSetExtractor<Integer>() {
			@Override
			public Integer extractData(ResultSet rs)
					throws SQLException, DataAccessException {
				rs.next();
				return rs.getInt(1);
			}
		});
	}

	public int getMeaningOfLife() {
		return meaningOfLife;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
