package com.kevinarpe.suruga_bank.db;

import java.io.File;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public interface SqliteDslConnectionService {

    DSLConnection
    getConnectionFromFile(File filePath)
    throws Exception;

    DSLConnection
    getConnectionFromInMemory()
    throws Exception;
}
