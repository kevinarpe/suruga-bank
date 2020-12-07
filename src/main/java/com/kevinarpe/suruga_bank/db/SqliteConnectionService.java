package com.kevinarpe.suruga_bank.db;

import java.io.File;
import java.sql.Connection;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public interface SqliteConnectionService {

    Connection getConnectionFromFile(File filePath)
    throws Exception;

    Connection getConnectionFromInMemory()
    throws Exception;
}
