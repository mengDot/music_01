package com.mrddy.music.jdbc;

import java.sql.Connection;

public interface IPool {

	PoolConnection getConnection();

	Connection getConnectionNoPool();

}
